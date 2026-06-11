package com.blps_lab1.demo.listener;

import com.blps_lab1.demo.data.tables.PaymentMethod;
import com.blps_lab1.demo.data.tables.PaymentType;
import com.blps_lab1.demo.dto.TaskMessage;
import com.blps_lab1.demo.dto.PaymentRequest;
import com.blps_lab1.demo.services.api.IReservationDraftService;
import com.blps_lab1.demo.services.api.IReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class ReservationConfirmationJmsListener {

    private final IReservationService reservationService;
    private final IReservationDraftService reservationDraftService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReservationConfirmationJmsListener(IReservationService reservationService,
                                              IReservationDraftService reservationDraftService) {
        this.reservationService = reservationService;
        this.reservationDraftService = reservationDraftService;
    }

    @JmsListener(destination = "reservation.confirmation", containerFactory = "jmsListenerContainerFactory")
    public void handleConfirmation(byte[] payloadBytes) {
        try {
            String rawJson = new String(payloadBytes, StandardCharsets.UTF_8);
            System.out.println(">>> [JMS CONFIRMATION] Получена задача подтверждения брони: " + rawJson);

            TaskMessage task = objectMapper.readValue(rawJson, TaskMessage.class);

            if ("CREATE_RESERVATION".equals(task.getTaskType())) {
                PaymentRequest simulatedPayment = new PaymentRequest();
                simulatedPayment.setPaymentType(PaymentType.LATER);
                simulatedPayment.setPaymentMethod(PaymentMethod.CARD);

                Long reservationId = reservationService.confirmReservation(task.getDraftId(), simulatedPayment);
                System.out.println(">>> [JMS CONFIRMATION] Создана реальная бронь с ID: " + reservationId);

                reservationDraftService.removeDraft(task.getDraftId());
                System.out.println(">>> [JMS CONFIRMATION] Черновик ID " + task.getDraftId() + " успешно удален");
            }

        } catch (Exception e) {
            System.err.println(">>> КРИТИЧЕСКИЙ СБОЙ В JMS ПОТОКЕ ПОДТВЕРЖДЕНИЯ БРОНИ!");
            e.printStackTrace();
        }
    }
}