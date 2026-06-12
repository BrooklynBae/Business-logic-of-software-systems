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
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(rollbackFor = Exception.class)
    public void handleConfirmation(byte[] payloadBytes) {
        try {
            String rawJson = new String(payloadBytes, StandardCharsets.UTF_8);
            System.out.println(">>> [XA JTA TRACE] Начало транзакции подтверждения брони: " + rawJson);

            TaskMessage task = objectMapper.readValue(rawJson, TaskMessage.class);

            if ("CREATE_RESERVATION".equals(task.getTaskType())) {
                PaymentRequest simulatedPayment = new PaymentRequest();
                simulatedPayment.setPaymentType(PaymentType.LATER);
                simulatedPayment.setPaymentMethod(PaymentMethod.CARD);

                reservationService.confirmReservation(task.getDraftId(), simulatedPayment);
                reservationDraftService.removeDraft(task.getDraftId());

                System.out.println(">>> [XA JTA TRACE] Успешное завершение 2PC коммита");
            }
        } catch (Exception e) {
            System.err.println(">>> [XA ROLLBACK] Откат транзакции подтверждения брони!");
            throw new RuntimeException("Forced XA Rollback for confirmation queue", e);
        }
    }
}