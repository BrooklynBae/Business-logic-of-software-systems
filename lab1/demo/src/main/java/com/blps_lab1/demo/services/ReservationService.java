package com.blps_lab1.demo.services;

import com.blps_lab1.demo.dto.DateRequest;
import com.blps_lab1.demo.dto.ReservationDto;
import com.blps_lab1.demo.dto.ReservationRequest;
import com.blps_lab1.demo.services.utils.ReservationDraftStorage;
import com.blps_lab1.demo.data.repository.PlaceRepository;
import com.blps_lab1.demo.data.repository.ReservationRepository;
import com.blps_lab1.demo.data.repository.UserRepository;
import com.blps_lab1.demo.data.tables.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationDraftStorage draftStorage;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    public ReservationService(ReservationRepository reservationRepository, ReservationDraftStorage draftStorage, UserRepository userRepository, PlaceRepository placeRepository) {
        this.reservationRepository = reservationRepository;
        this.draftStorage = draftStorage;
        this.userRepository = userRepository;
        this.placeRepository = placeRepository;
    }

    private ReservationDto toReservationDto(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getArrival(),
                reservation.getDeparture(),
                reservation.getGuestsAmount(),
                reservation.getPetsAmount(),
                reservation.getUser(),
                reservation.getPlace(),
                reservation.getPrice(),
                reservation.getPaymentType(),
                reservation.getPaymentMethod(),
                reservation.getPlace().getOwner()
        );
    }

    public ReservationDto findReservation(long id) {
        return toReservationDto(reservationRepository.getReferenceById(id));
    }

    public ReservationDto updateDate(Long id, DateRequest dateRequest) {
        ReservationDto request = draftStorage.getDraft(id);

        if (request == null) {
            throw new RuntimeException("Draft not found or expired");
        }

        checkDates(request.getPlace().getId(), dateRequest.getArrival(), dateRequest.getDeparture());

        request.setArrival(dateRequest.getArrival());
        request.setDeparture(dateRequest.getDeparture());

        double price = countPrice(request.getArrival(), request.getDeparture(), request.getGuestsAmount(), request.getPetsAmount(), request.getPlace().getId());
        request.setPrice(price);

        return request;
    }

    public ReservationDto createDraft(ReservationRequest request) {
        if (request.getGuestsAmount() > placeRepository.getReferenceById(request.getIdPlace()).getMaxGuests()) {
            throw new RuntimeException("This place can not accommodate " + request.getGuestsAmount() + "guests. Limit - " + placeRepository.getReferenceById(request.getIdPlace()).getMaxGuests());
        }

        checkDates(request.getIdPlace(), request.getArrival(), request.getDeparture());

        double price = countPrice(request.getArrival(), request.getDeparture(), request.getGuestsAmount(), request.getPetsAmount(), request.getIdPlace());

        ReservationDto reservationDto = new ReservationDto(
                null,
                request.getArrival(),
                request.getDeparture(),
                request.getGuestsAmount(),
                request.getPetsAmount(),
                userRepository.getReferenceById(request.getIdOwner()),
                placeRepository.getReferenceById(request.getIdPlace()),
                price,
                null,
                null,
                placeRepository.getReferenceById(request.getIdPlace()).getOwner()
        );

        Long id = draftStorage.saveDraft(reservationDto);  //check info, DATES, cnt price, max guests
        reservationDto.setId(id);

        return reservationDto;
    }

    private void checkDates(Long idPlace, LocalDate arrival, LocalDate departure) {
        List<Reservation> conflicts = reservationRepository.findByPlaceIdAndArrivalLessThanAndDepartureGreaterThan(
                idPlace,
                departure,
                arrival);

        if (!conflicts.isEmpty()) {
            String conflictPeriods = conflicts.stream()
                    .map(r -> "[" + r.getArrival() + " - " + r.getDeparture() + "]")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            throw new RuntimeException("Selected dates are already reserved: " + conflictPeriods);
        }
    }

    private Double countPrice(LocalDate arrival, LocalDate departure, Integer guestsAmount, Integer petsAmount, Long idPlace) {
        double pricePerNight = placeRepository.getReferenceById(idPlace).getPricePerNight();
        long totalDays = ChronoUnit.DAYS.between(arrival, departure);
        double guestCoeff = 1 + (guestsAmount - 1) * 0.5;
        double petsCoeff = 1 + (petsAmount - 1) * 0.1;
        return pricePerNight * totalDays * guestCoeff * petsCoeff;
    }


    //id res entity
    protected Long confirmReservation(Long id) {
        ReservationDto reservationDto = draftStorage.getDraft(id);

        if (reservationDto == null) {
            throw new RuntimeException("Draft not found or expired");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(reservationDto.getUser());
        reservation.setPlace(reservationDto.getPlace());
        reservation.setArrival(reservationDto.getArrival());
        reservation.setDeparture(reservationDto.getDeparture());
        reservation.setGuestsAmount(reservationDto.getGuestsAmount());
        reservation.setPetsAmount(reservationDto.getPetsAmount());
        reservation.setPrice(reservationDto.getPrice());
        reservation.setPlaceType(placeRepository.getReferenceById(reservationDto.getPlace().getId()).getPlaceType());
        reservation.setPaymentType(reservationDto.getPaymentType());
        reservation.setPaymentMethod(reservationDto.getPaymentMethod());

        reservationRepository.save(reservation);

        return reservation.getId();
    }
}

