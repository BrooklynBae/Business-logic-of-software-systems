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

    public PaymentResponseDto processPayment(Long id) {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();

        ReservationDto reservationDto = draftStorage.getDraft(id);

        User user = reservationDto.getUser();

        if (user.getPhoto().isBlank()) {
            paymentResponseDto.setReservationId(id);// id draft
            paymentResponseDto.setAvailable(false);
            paymentResponseDto.setSuccess(false);
            paymentResponseDto.setMessage("Please, add your photo");
            return paymentResponseDto;
        } //no email?

        if (reservationDto.getPaymentType().equals(PaymentType.NOW)) {
            if (random.nextBoolean()) {
                paymentResponseDto.setAvailable(true);
                paymentResponseDto.setSuccess(true);
                paymentResponseDto.setMessage("ORA ORA ORA ORA ORA");
            } else {
                paymentResponseDto.setReservationId(id); //id draft
                paymentResponseDto.setAvailable(true);
                paymentResponseDto.setSuccess(false);
                paymentResponseDto.setMessage("Payment failed, you're too poor");
                return paymentResponseDto;
            }
            paymentResponseDto.setReservationId(reservationService.confirmReservation(id));
            draftStorage.removeDraft(id);
        }
        return paymentResponseDto;
    }
}
