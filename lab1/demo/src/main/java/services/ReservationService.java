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

        double price = countPrice(request.getArrival(), request.getDeparture(), request.getGuestsAmount(), request.getPetsAmount(), request.getIdPlace());
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

