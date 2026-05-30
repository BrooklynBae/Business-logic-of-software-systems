package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.tables.*;
import com.blps_lab1.demo.dto.*;
import com.blps_lab1.demo.services.api.*;
import com.blps_lab1.demo.data.repository.ReservationRepository;
import com.blps_lab1.demo.exception.BadRequestException;
import com.blps_lab1.demo.exception.NotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final IReservationDraftService reservationDraftService;
    private final IUserService userService;
    private final IPlaceService placeService;
    private final IServiceOptionService serviceOptionService;

    public ReservationService(ReservationRepository reservationRepository, @Lazy IReservationDraftService reservationDraftService, IUserService userService, IPlaceService placeService, IServiceOptionService serviceOptionService) {
        this.reservationRepository = reservationRepository;
        this.reservationDraftService = reservationDraftService;
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
                .serviceOptionIds(reservation.getServiceOptions() != null ?
                        reservation.getServiceOptions().stream().map(ServiceOption::getId).toList() :
                        new ArrayList<>())
                .build();
    }

    private DateDto toDateDto(Reservation reservation) {
        return DateDto.builder()
                .arrival(reservation.getArrival())
                .departure(reservation.getDeparture())
                .build();
    }

    @Override
    @PostAuthorize("returnObject.user.name == authentication.name or returnObject.owner.name == authentication.name or hasAuthority('PERM_MODERATE_DRAFTS')")
    public ReservationDto findReservation(long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found with id = " + id));
        return toReservationDto(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("permitAll()")
    public void ensureDatesAvailable(Long idPlace, LocalDate arrival, LocalDate departure) {
        List<Reservation> conflicts = reservationRepository.findConflictsForUpdate(idPlace, arrival, departure);

        if (arrival.isAfter(departure)) {
            throw new BadRequestException("Arrival date later than departure");
        }

        if (!conflicts.isEmpty()) {
            String conflictPeriods = conflicts.stream()
                    .map(r -> "[" + r.getArrival() + " - " + r.getDeparture() + "]")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            throw new BadRequestException("Selected dates are already reserved: " + conflictPeriods);
        }
    }

    private void validateGuestsAndPets(Place place, Integer guestsAmount, Integer petsAmount, Set<ServiceOption> selectedOptions) {
        if (guestsAmount > place.getMaxGuests()) {
            throw new BadRequestException("This place can not accommodate " + guestsAmount + " guests. Limit - " + place.getMaxGuests());
        }

        int actualPetsAmount = (petsAmount != null) ? petsAmount : 0;

        if (actualPetsAmount > 0) {
            if (Boolean.FALSE.equals(place.getPetsAllowed())) {
                throw new BadRequestException("This place does not allow pets.");
            }
        }
    }

    private Double countPrice(LocalDate arrival, LocalDate departure, Integer guestsAmount, Integer petsAmount, Place place, Set<ServiceOption> selectedOptions) {
        long totalDays = ChronoUnit.DAYS.between(arrival, departure);
        if (totalDays <= 0) {
            throw new BadRequestException("Departure date must be after arrival date");
        }
        double guestCoeff = 1 + (guestsAmount - 1) * 0.5;
        double petsCoeff = 1 + (Math.max(0, petsAmount) * 0.1);

        double accommodationTotalPrice = place.getPricePerNight() * totalDays * guestCoeff * petsCoeff;

        double servicesTotalPrice = selectedOptions.stream()
                .mapToDouble(ServiceOption::getPricePerDay)
                .sum() * totalDays;

        return accommodationTotalPrice + servicesTotalPrice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_PROCESS_PAYMENT')")
    public Long confirmReservation(Long id, PaymentRequest request) {
        ReservationDraft reservationDraft = reservationDraftService.findEntityById(id);

        if (reservationDraft == null) {
            throw new NotFoundException("Draft not found or expired");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(reservationDraft.getUser());
        reservation.setPlace(reservationDraft.getPlace());
        reservation.setArrival(reservationDraft.getArrival());
        reservation.setDeparture(reservationDraft.getDeparture());
        reservation.setGuestsAmount(reservationDraft.getGuestsAmount());
        reservation.setPetsAmount(reservationDraft.getPetsAmount());
        reservation.setPrice(reservationDraft.getPrice());
        reservation.setPlaceType(reservationDraft.getPlace().getPlaceType());
        reservation.setPaymentType(request.getPaymentType());
        reservation.setPaymentMethod(request.getPaymentMethod());
        reservation.setCoverLetter(reservationDraft.getCoverLetter());

        if (reservationDraft.getServiceOptions() != null) {
            reservation.setServiceOptions(new HashSet<>(reservationDraft.getServiceOptions()));
        }

        reservationRepository.save(reservation);

        return reservation.getId();
    }

    @Override
    @PreAuthorize("hasAuthority('PERM_MODERATE_DRAFTS') or @appSecurity.isSelfUser(@reservationRepository.findById(#a0).orElse(null)?.getUser()?.getId(), authentication.name)")
    public void deleteReservation(Long id) {
        findReservation(id);
        reservationRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("permitAll()")
    public List<DateDto> findAllReservedDates(Long placeId) {
        return reservationRepository.findByPlaceId(placeId).stream()
                .map(this::toDateDto)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_MODERATE_DRAFTS')")
    public ReservationDto updateCoverLetterByAdmin(Long id, String newLetter) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found with id = " + id));

        reservation.setCoverLetter(newLetter);

        Reservation saved = reservationRepository.save(reservation);
        return toReservationDto(saved);
    }
}

