package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.repository.ReservationDraftRepository;
import com.blps_lab1.demo.data.tables.*;
import com.blps_lab1.demo.dto.DateRequest;
import com.blps_lab1.demo.dto.ReservationDto;
import com.blps_lab1.demo.dto.ReservationRequest;
import com.blps_lab1.demo.exception.BadRequestException;
import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.services.api.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@EnableScheduling
public class ReservationDraftService implements IReservationDraftService {
    private final ReservationDraftRepository reservationDraftRepository;
    private final IReservationService reservationService;
    private final IUserService userService;
    private final IPlaceService placeService;
    private final IServiceOptionService serviceOptionService;

    public ReservationDraftService(ReservationDraftRepository reservationDraftRepository, IReservationService reservationService, IUserService userService, IPlaceService placeService, IServiceOptionService serviceOptionService) {
        this.reservationDraftRepository = reservationDraftRepository;
        this.reservationService = reservationService;
        this.userService = userService;
        this.placeService = placeService;
        this.serviceOptionService = serviceOptionService;
    }

    @Override
    public ReservationDto createDraft(ReservationRequest request) {
        Place place = placeService.findEntityById(request.getIdPlace());
        User user = userService.findEntityById(request.getUserId());
        List<ServiceOption> selectedOptions = serviceOptionService.findEntitiesByIds(request.getServiceOptionIds());

        validateGuestsAndPets(place, request.getGuestsAmount(), request.getPetsAmount(), selectedOptions);

        reservationService.ensureDatesAvailable(request.getIdPlace(), request.getArrival(), request.getDeparture());

        double price = countPrice(
                request.getArrival(),
                request.getDeparture(),
                request.getGuestsAmount(),
                request.getPetsAmount(),
                place,
                selectedOptions
        );

        ReservationDraft reservationDraft = new ReservationDraft();
        reservationDraft.setUser(user);
        reservationDraft.setPlace(place);
        reservationDraft.setArrival(request.getArrival());
        reservationDraft.setDeparture(request.getDeparture());
        reservationDraft.setGuestsAmount(request.getGuestsAmount());
        reservationDraft.setPetsAmount(request.getPetsAmount());
        reservationDraft.setPrice(price);
        reservationDraft.setPlaceType(place.getPlaceType());
        reservationDraft.setServiceOptions(serviceOptionService.findEntitiesByIds(request.getServiceOptionIds()));

        reservationDraftRepository.save(reservationDraft);

        return ReservationDto.builder()
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
    }

    @Override
    public ReservationDto updateDate(Long id, DateRequest dateRequest) {
        ReservationDraft reservationDraft = reservationDraftRepository.getReferenceById(id);

        if (reservationDraft == null) {
            throw new NotFoundException("Draft not found or expired");
        }

        reservationService.ensureDatesAvailable(reservationDraft.getPlace().getId(), dateRequest.getArrival(), dateRequest.getDeparture());

        reservationDraft.setArrival(dateRequest.getArrival());
        reservationDraft.setDeparture(dateRequest.getDeparture());

        double price = countPrice(
                reservationDraft.getArrival(),
                reservationDraft.getDeparture(),
                reservationDraft.getGuestsAmount(),
                reservationDraft.getPetsAmount(),
                reservationDraft.getPlace(),
                serviceOptionService.findEntitiesByIds(reservationDraft.getServiceOptionIds())
        );
        reservationDraft.setPrice(price);

        return reservationDraft;
    }

    @Override
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void deleteExpiredDrafts() {
        int minutesToLive = 30;
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(minutesToLive);
        reservationDraftRepository.deleteByCreatedAtBefore(expiryTime);
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
}
