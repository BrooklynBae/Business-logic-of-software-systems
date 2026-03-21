package services;

import data.repository.UserRepository;
import data.tables.PaymentType;
import data.tables.User;
import dto.PaymentRequest;
import dto.PaymentResponseDto;
import dto.ReservationDto;
import org.springframework.stereotype.Service;
import services.utils.ReservationDraftStorage;

import java.util.Random;

@Service
public class PaymentService {

    private final ReservationDraftStorage draftStorage;
    private final ReservationService reservationService;
    private final UserRepository userRepository;
    Random random = new Random();

    public PaymentService(ReservationDraftStorage draftStorage, ReservationService reservationService, UserService userService, UserRepository userRepository) {
        this.draftStorage = draftStorage;
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    public ReservationDto updatePaymentData(Long id, PaymentRequest request) {
        ReservationDto reservationDto = draftStorage.getDraft(id);

        reservationDto.setPaymentMethod(request.getPaymentMethod());
        reservationDto.setPaymentType(request.getPaymentType());

        return reservationDto;
    }

    public ReservationDto processPayment(Long id, PaymentRequest paymentRequest) {

        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();

        ReservationDto reservationRequest = draftStorage.getDraft(id);
        Long idOwner = reservationRequest.getOwner().getId();
        User user = userRepository.getReferenceById(idOwner);

        if (user.getPhoto().isBlank()) {
            paymentResponseDto.setReservationId(id);// id draft
            paymentResponseDto.setAvailable(false);
            paymentResponseDto.setSuccess(false);
            paymentResponseDto.setMessage("Please, add your photo");
            return paymentResponseDto;
        } //no email?

        if (paymentRequest.getPaymentType().equals(PaymentType.NOW)) {
            if (random.nextBoolean()) {
                paymentResponseDto.setReservationId(id);// id reservation
                paymentResponseDto.setAvailable(true);
                paymentResponseDto.setSuccess(true);
                paymentResponseDto.setMessage("ORA ORA ORA ORA ORA");

                draftStorage.removeDraft(id);
            } else {
                paymentResponseDto.setReservationId(id); //id draft
                paymentResponseDto.setAvailable(true);
                paymentResponseDto.setSuccess(false);
                paymentResponseDto.setMessage("Payment failed, you're too poor");
            }
            reservationService.confirmReservation(id);
        }
        //setId = id Reservatikon
        //draftStorage.getDraft(id).setPaymentCompleted(true);
        return paymentResponseDto;
    }
}
