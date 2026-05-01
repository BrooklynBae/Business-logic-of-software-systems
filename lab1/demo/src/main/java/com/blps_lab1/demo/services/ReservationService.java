package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.tables.Place;
import com.blps_lab1.demo.data.tables.ServiceOption;
import com.blps_lab1.demo.data.tables.User;
import com.blps_lab1.demo.dto.CreateReservationEntityRequest;
import com.blps_lab1.demo.dto.DateDto;
import com.blps_lab1.demo.dto.DateRequest;
import com.blps_lab1.demo.dto.ReservationDto;
import com.blps_lab1.demo.dto.ReservationRequest;
import com.blps_lab1.demo.services.utils.ReservationDraftStorage;
import com.blps_lab1.demo.data.repository.ReservationRepository;
import com.blps_lab1.demo.data.tables.Reservation;
import com.blps_lab1.demo.exception.BadRequestException;
import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.services.api.IPlaceService;
import com.blps_lab1.demo.services.api.IReservationService;
import com.blps_lab1.demo.services.api.IServiceOptionService;
import com.blps_lab1.demo.services.api.IUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationDraftStorage draftStorage;
    private final IUserService userService;
    private final IPlaceService placeService;
    private final IServiceOptionService serviceOptionService;

    public ReservationService(ReservationRepository reservationRepository, ReservationDraftStorage draftStorage, IUserService userService, IPlaceService placeService, IServiceOptionService serviceOptionService) {
        this.reservationRepository = reservationRepository;
        this.draftStorage = draftStorage;
        this.userService = userService;
        this.placeService = placeService;
        this.serviceOptionService = serviceOptionService;
    }

    private ReservationDto toReservationDto(Reservation reservation) {
        return ReservationDto.builder()
                .id(reservation.getId())
                .arrival(reservation.getArrival())
                .departure(reservation.getDeparture())
                .guestsAmount(reservation.getGuestsAmount())
                .petsAmount(reservation.getPetsAmount())
                .user(reservation.getUser())
                .place(reservation.getPlace())
                .price(reservation.getPrice())
                .paymentType(reservation.getPaymentType())
                .paymentMethod(reservation.getPaymentMethod())
                .owner(reservation.getPlace().getOwner())
                .serviceOptionIds(reservation.getServiceOptions().stream().map(ServiceOption::getId).toList())
                .build();
    }

    private DateDto toDateDto(Reservation reservation) {
        return DateDto.builder()
                .arrival(reservation.getArrival())
                .departure(reservation.getDeparture())
                .build();
    }

    @Override
    public ReservationDto findReservation(long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found with id = " + id));
        return toReservationDto(reservation);
    }

    @Override
    public ReservationDto updateDate(Long id, DateRequest dateRequest) {
        ReservationDto request = draftStorage.getDraft(id);

        if (request == null) {
            throw new NotFoundException("Draft not found or expired");
        }

        ensureDatesAvailable(request.getPlace().getId(), dateRequest.getArrival(), dateRequest.getDeparture());

        request.setArrival(dateRequest.getArrival());
        request.setDeparture(dateRequest.getDeparture());

        double price = countPrice(
                request.getArrival(),
                request.getDeparture(),
                request.getGuestsAmount(),
                request.getPetsAmount(),
                request.getPlace(),
                serviceOptionService.findEntitiesByIds(request.getServiceOptionIds())
        );
        request.setPrice(price);

        return request;
    }

    @Override
    public ReservationDto createDraft(ReservationRequest request) {
        Place place = placeService.findEntityById(request.getIdPlace());
        User user = userService.findEntityById(request.getUserId());
        List<ServiceOption> selectedOptions = serviceOptionService.findEntitiesByIds(request.getServiceOptionIds());

        validateGuestsAndPets(place, request.getGuestsAmount(), request.getPetsAmount(), selectedOptions);

        ensureDatesAvailable(request.getIdPlace(), request.getArrival(), request.getDeparture());

        double price = countPrice(
                request.getArrival(),
                request.getDeparture(),
                request.getGuestsAmount(),
                request.getPetsAmount(),
                place,
                selectedOptions
        );

        ReservationDto reservationDto = ReservationDto.builder()
                .arrival(request.getArrival())
                .departure(request.getDeparture())
                .guestsAmount(request.getGuestsAmount())
                .petsAmount(request.getPetsAmount())
                .user(user)
                .place(place)
                .price(price)
                .paymentType(null)
                .paymentMethod(null)
                .owner(place.getOwner())
                .serviceOptionIds(selectedOptions.stream().map(ServiceOption::getId).toList())
                .build();

        Long id = draftStorage.saveDraft(reservationDto);
        reservationDto.setId(id);

        return reservationDto;
    }

    @Override
    public void ensureDatesAvailable(Long idPlace, LocalDate arrival, LocalDate departure) {
        List<Reservation> conflicts = reservationRepository.findByPlaceIdAndArrivalLessThanAndDepartureGreaterThan(
                idPlace,
                departure,
                arrival);

        if (arrival.isAfter(departure)) {
            throw new RuntimeException("Arrival date later than departure");
        }

        if (!conflicts.isEmpty()) {
            String conflictPeriods = conflicts.stream()
                    .map(r -> "[" + r.getArrival() + " - " + r.getDeparture() + "]")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            throw new BadRequestException("Selected dates are already reserved: " + conflictPeriods);
        }
    }

    private void validateGuestsAndPets(Place place, Integer guestsAmount, Integer petsAmount, List<ServiceOption> selectedOptions) {
        if (guestsAmount > place.getMaxGuests()) {
            throw new BadRequestException("This place can not accommodate " + guestsAmount + " guests. Limit - " + place.getMaxGuests());
        }
        if (petsAmount != null && petsAmount > 0 && Boolean.FALSE.equals(place.getPetsAllowed())) {
            throw new BadRequestException("This owner does not allow pets in this place");
        }
        boolean hasPetRelatedServices = selectedOptions.stream().anyMatch(option -> Boolean.TRUE.equals(option.getPetRelated()));
        if (hasPetRelatedServices && (petsAmount == null || petsAmount <= 0)) {
            throw new BadRequestException("Pet-related services require at least one pet in reservation");
        }
    }

    private Double countPrice(LocalDate arrival, LocalDate departure, Integer guestsAmount, Integer petsAmount, Place place, List<ServiceOption> selectedOptions) {
        long totalDays = ChronoUnit.DAYS.between(arrival, departure);
        if (totalDays <= 0) {
            throw new BadRequestException("Departure date must be after arrival date");
        }
        double guestCoeff = 1 + (guestsAmount - 1) * 0.5;
        double petsCoeff = 1 + (Math.max(0, petsAmount) * 0.1);
        double servicesPerDay = selectedOptions.stream().mapToDouble(ServiceOption::getPricePerDay).sum();
        return (place.getPricePerNight() + servicesPerDay) * totalDays * guestCoeff * petsCoeff;
    }

    @Override
    public Long confirmReservation(Long id) {
        ReservationDto reservationDto = draftStorage.getDraft(id);

        if (reservationDto == null) {
            throw new NotFoundException("Draft not found or expired");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(reservationDto.getUser());
        reservation.setPlace(reservationDto.getPlace());
        reservation.setArrival(reservationDto.getArrival());
        reservation.setDeparture(reservationDto.getDeparture());
        reservation.setGuestsAmount(reservationDto.getGuestsAmount());
        reservation.setPetsAmount(reservationDto.getPetsAmount());
        reservation.setPrice(reservationDto.getPrice());
        reservation.setPlaceType(reservationDto.getPlace().getPlaceType());
        reservation.setPaymentType(reservationDto.getPaymentType());
        reservation.setPaymentMethod(reservationDto.getPaymentMethod());
        reservation.setServiceOptions(serviceOptionService.findEntitiesByIds(reservationDto.getServiceOptionIds()));

        reservationRepository.save(reservation);

        return reservation.getId();
    }

    @Override
    public void deleteReservation(Long id) {
        findReservation(id);
        reservationRepository.deleteById(id);
    }

    @Override
    public ReservationDto createReservationEntity(CreateReservationEntityRequest request) {
        Place place = placeService.findEntityById(request.getPlaceId());
        User user = userService.findEntityById(request.getUserId());
        List<ServiceOption> selectedOptions = serviceOptionService.findEntitiesByIds(request.getServiceOptionIds());

        validateGuestsAndPets(place, request.getGuestsAmount(), request.getPetsAmount(), selectedOptions);
        ensureDatesAvailable(place.getId(), request.getArrival(), request.getDeparture());

        Reservation reservation = new Reservation();
        reservation.setPlace(place);
        reservation.setUser(user);
        reservation.setArrival(request.getArrival());
        reservation.setDeparture(request.getDeparture());
        reservation.setGuestsAmount(request.getGuestsAmount());
        reservation.setPetsAmount(request.getPetsAmount());
        reservation.setPlaceType(place.getPlaceType());
        reservation.setPaymentType(request.getPaymentType());
        reservation.setPaymentMethod(request.getPaymentMethod());
        reservation.setServiceOptions(selectedOptions);

        double price = countPrice(
                request.getArrival(),
                request.getDeparture(),
                request.getGuestsAmount(),
                request.getPetsAmount(),
                place,
                selectedOptions
        );
        reservation.setPrice(price);

        Reservation saved = reservationRepository.save(reservation);
        return toReservationDto(saved);
    }

    @Override
    public List<DateDto> findAllReservedDates(Long placeId) {
        return reservationRepository.findByPlaceId(placeId).stream()
                .map(this::toDateDto)
                .toList();
    }
}

