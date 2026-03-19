package services;

import data.repository.PlaceRepository;
import data.repository.ReservationRepository;
import data.repository.UserRepository;
import data.tables.PaymentType;
import data.tables.Reservation;
import dto.*;
import org.springframework.stereotype.Service;
import services.utils.ReservationDraftStorage;

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
                reservation.getPrice(),
                reservation.getPaymentType(),
                reservation.getPaymentMethod(),
                reservation.getPlace().getOwner()
        );
    }

    private ReservationResponse toReservationResponse(ReservationRequest reservation,Long id, double price) {
        return new ReservationResponse(
                id,
                reservation.getArrival(),
                reservation.getDeparture(),
                reservation.getGuestsAmount(),
                reservation.getPetsAmount(),
                price,
                reservation.getIdPlace()
        );
    }

    public ReservationDto findReservation(long id) {
        return toReservationDto(reservationRepository.getReferenceById(id));
    }

    public ReservationResponse updateDate(Long id, DateRequest dateRequest) {
        ReservationRequest request = draftStorage.getDraft(id);

        if (request == null) {
            throw new RuntimeException("Draft not found or expired");
        }

        List<Reservation> conflicts = reservationRepository.findByPlaceIdAndArrivalLessThanAndDepartureGreaterThan(
                request.getIdPlace(),
                dateRequest.getDeparture(),
                dateRequest.getArrival());

        if (!conflicts.isEmpty()) {
            String conflictPeriods = conflicts.stream()
                    .map(r -> "[" + r.getArrival() + " - " + r.getDeparture() + "]")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            throw new RuntimeException("Selected dates are already reserved: " + conflictPeriods);
        }

        request.setArrival(dateRequest.getArrival());
        request.setDeparture(dateRequest.getDeparture());

        double price = countPrice(request.getArrival(), request.getDeparture(), request.getGuestsAmount(), request.getPetsAmount(), request.getIdPlace());

        return toReservationResponse(request, id, price);
    }

    public Long createDraft(ReservationRequest request) {

        return draftStorage.saveDraft(request);  //check info, DATES, cnt price, max guests
    }

    private Double countPrice(LocalDate arrival, LocalDate departure, Integer guestsAmount, Integer petsAmount, Long idPlace) {
        double pricePerNight = placeRepository.getReferenceById(idPlace).getPricePerNight();
        long totalDays = ChronoUnit.DAYS.between(arrival, departure);
        double guestCoeff = 1 + (guestsAmount - 1) * 0.5;
        double petsCoeff = 1 + (petsAmount - 1) * 0.1;
        return pricePerNight * totalDays * guestCoeff * petsCoeff;
    }


    //id draft res
    public ReservationDto confirmReservation(Long id) {
        ReservationRequest request = draftStorage.getDraft(id);
        if (request == null) {
            throw new RuntimeException("Draft not found or expired");
        }
        Double price = countPrice(request.getArrival(),
                request.getDeparture(),
                request.getGuestsAmount(),
                request.getPetsAmount(),
                request.getIdPlace());

        Reservation reservation = new Reservation();
        reservation.setUser(userRepository.getReferenceById(request.getIdOwner()));
        reservation.setPlace(placeRepository.getReferenceById(request.getIdPlace()));
        reservation.setArrival(request.getArrival());
        reservation.setDeparture(request.getDeparture());
        reservation.setGuestsAmount(request.getGuestsAmount());
        reservation.setPetsAmount(request.getPetsAmount());
        reservation.setPrice(price);
        reservation.setPlaceType(placeRepository.getReferenceById(request.getIdPlace()).getPlaceType());
        reservation.setPaymentType(request);//как то блять связать Reservation request from draft storage and payment response

    }
}

//TODO: make res model?
