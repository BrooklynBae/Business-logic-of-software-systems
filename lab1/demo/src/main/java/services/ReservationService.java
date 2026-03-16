package services;

import data.repository.PlaceRepository;
import data.repository.ReservationRepository;
import data.repository.UserRepository;
import data.tables.PaymentType;
import data.tables.Reservation;
import dto.PaymentRequest;
import dto.PaymentResponseDto;
import dto.ReservationRequest;
import dto.ReservationDto;
import org.springframework.stereotype.Service;
import services.utils.ReservationDraftStorage;

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

    public ReservationDto findReservation(long id) {
        return toReservationDto(reservationRepository.getReferenceById(id));
    }

    public Long createDraft(ReservationRequest request) {
        return draftStorage.saveDraft(request);
    }

    //id draft res
    //move to payment service
    public PaymentResponseDto confirmPayment(Long id, PaymentRequest paymentRequest) {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        if (paymentRequest.getPaymentType().equals(PaymentType.NOW)) {
            //random
            //if success ->
                paymentResponseDto.setDraftResId(id);//maybe useless
                paymentResponseDto.setAvailable(true);
                paymentResponseDto.setSuccess(true);
                paymentResponseDto.setMessage("ORA ORA ORA ORA ORA");
        }
        draftStorage.getDraft(id).setPaymentCompleted(true);
        return paymentResponseDto;
    }

    //id draft res
    public ReservationDto confirmReservation(Long id) {
        //will be called when payment is confirmed
        ReservationRequest request = draftStorage.getDraft(id);

        if (request == null) {
            throw new RuntimeException("Draft not found or expired");
        }
        Reservation reservation = new Reservation();
        reservation.setUser(userRepository.getReferenceById(request.getIdOwner()));
        reservation.setPlace(placeRepository.getReferenceById(request.getIdPlace()));
        reservation.setArrival(request.getArrival());
        reservation.setDeparture(request.getDeparture());
        reservation.setGuestsAmount(request.getGuestsAmount());
        reservation.setPetsAmount(request.getPetsAmount());
        reservation.setPrice();//logic for price
        reservation.setPlaceType(placeRepository.getReferenceById(request.getIdPlace()).getPlaceType());
        reservation.setPaymentType(request);//как то блять связать Reservation request from draft storage and payment response

    }
}
