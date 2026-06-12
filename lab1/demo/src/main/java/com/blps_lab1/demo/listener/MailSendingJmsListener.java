package com.blps_lab1.demo.listener;

import com.blps_lab1.demo.dto.TaskMessage;
import com.blps_lab1.demo.data.tables.ReservationDraft;
import com.blps_lab1.demo.services.ReservationDraftService;
import com.blps_lab1.demo.services.api.ISystemReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;

@Component
public class MailSendingJmsListener {

    private final ReservationDraftService reservationDraftService;
    private final ISystemReservationService systemReservationService;
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.mail.username}")
    private String fromEmail;

    public MailSendingJmsListener(ReservationDraftService reservationDraftService,
                                  ISystemReservationService systemReservationService,
                                  JavaMailSender mailSender) {
        this.reservationDraftService = reservationDraftService;
        this.systemReservationService = systemReservationService;
        this.mailSender = mailSender;
    }

    @JmsListener(destination = "mail.sending", containerFactory = "jmsListenerContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    public void handleMailSending(byte[] payloadBytes) {
        try {
            String rawJson = new String(payloadBytes, StandardCharsets.UTF_8);
            System.out.println(">>> [XA JTA TRACE] Начало распределенной транзакции для сообщения: " + rawJson);

            TaskMessage task = objectMapper.readValue(rawJson, TaskMessage.class);
            ReservationDraft draft = reservationDraftService.findEntityById(task.getDraftId());
            String ownerEmail = draft.getPlace().getOwner().getLogin();
            String userName = draft.getUser().getName();

            String rawLetter = "";
            if (draft.getCoverLetter() != null) {
                rawLetter = draft.getCoverLetter().replace("[APPROVED_BY_ADMIN] ", "");
            }

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(ownerEmail);
            mailMessage.setSubject("Новая заявка на бронирование от " + userName);
            mailMessage.setText("Здравствуйте! Пользователь " + userName + " хочет забронировать вашу площадку.\n" +
                    "Сопроводительное письмо:\n\n" + rawLetter);

            mailSender.send(mailMessage);

            systemReservationService.markAsEmailSentBySystem(task.getDraftId());
            System.out.println(">>> [XA JTA TRACE] Все XA-ресурсы готовы к коммиту (PostgreSQL + ActiveMQ)");

        } catch (Exception e) {
            System.err.println(">>> [XA ROLLBACK] Сбой операции. Начинается откат всей распределенной транзакции!");
            throw new RuntimeException("Forced XA Rollback due to internal error", e);
        }
    }
}