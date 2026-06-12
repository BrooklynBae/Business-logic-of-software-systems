package com.blps_lab1.demo.listener;

import com.blps_lab1.demo.data.tables.PaymentMethod;
import com.blps_lab1.demo.data.tables.PaymentType;
import com.blps_lab1.demo.dto.TaskMessage;
import com.blps_lab1.demo.dto.PaymentRequest;
import com.blps_lab1.demo.services.api.IReservationDraftService;
import com.blps_lab1.demo.services.api.IReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ReservationConfirmationJmsListener {

    private final IReservationService reservationService;
    private final IReservationDraftService reservationDraftService;
    private final Repository jackrabbitRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReservationConfirmationJmsListener(IReservationService reservationService,
                                              IReservationDraftService reservationDraftService,
                                              Repository jackrabbitRepository) {
        this.reservationService = reservationService;
        this.reservationDraftService = reservationDraftService;
        this.jackrabbitRepository = jackrabbitRepository;
    }

    @JmsListener(destination = "reservation.confirmation", containerFactory = "jmsListenerContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    public void handleConfirmation(byte[] payloadBytes) {
        Session jcrSession = null;
        try {
            String rawJson = new String(payloadBytes, StandardCharsets.UTF_8);
            System.out.println(">>> [XA JTA TRACE] Начало распределенной транзакции (PostgreSQL + Jackrabbit КИС): " + rawJson);

            UsernamePasswordAuthenticationToken systemAuth = new UsernamePasswordAuthenticationToken(
                    "SYSTEM_ASYNC_WORKER",
                    null,
                    List.of(new SimpleGrantedAuthority("PERM_PROCESS_PAYMENT"))
            );
            SecurityContextHolder.getContext().setAuthentication(systemAuth);

            TaskMessage task = objectMapper.readValue(rawJson, TaskMessage.class);

            if ("CREATE_RESERVATION".equals(task.getTaskType())) {
                PaymentRequest simulatedPayment = new PaymentRequest();
                simulatedPayment.setPaymentType(PaymentType.LATER);
                simulatedPayment.setPaymentMethod(PaymentMethod.CARD);

                Long newReservationId = reservationService.confirmReservation(task.getDraftId(), simulatedPayment);
                reservationDraftService.removeDraft(task.getDraftId());

                jcrSession = jackrabbitRepository.login(new SimpleCredentials("admin", "admin".toCharArray()));
                saveContractToJackrabbitEis(jcrSession, newReservationId, task.getDraftId());

                System.out.println(">>> [XA JTA TRACE] Обе системы отработали Phase 1 (Prepare). Координатор Narayana дает команду Commit.");
            }
        } catch (Exception e) {
            System.err.println(">>> [XA ROLLBACK] Критическая ошибка! Откат изменений в СУБД PostgreSQL и КИС Jackrabbit.");
            throw new RuntimeException("Forced XA Rollback due to failure in DB or EIS Repository", e);
        } finally {
            SecurityContextHolder.clearContext();

            if (jcrSession != null && jcrSession.isLive()) {
                jcrSession.logout();
            }
        }
    }

    private void saveContractToJackrabbitEis(Session session, Long reservationId, Long draftId) throws Exception {
        Node rootNode = session.getRootNode();
        Node archiveNode;
        if (rootNode.hasNode("corporate_contracts")) {
            archiveNode = rootNode.getNode("corporate_contracts");
        } else {
            archiveNode = rootNode.addNode("corporate_contracts", "nt:unstructured");
        }

        String nodeName = "contract_" + reservationId;
        Node contractNode = archiveNode.addNode(nodeName, "nt:unstructured");

        contractNode.setProperty("reservationId", reservationId);
        contractNode.setProperty("draftId", draftId);
        contractNode.setProperty("documentType", "OFFICIAL_RENTAL_CONTRACT");
        contractNode.setProperty("status", "PAID_VIA_HOLDING_ENGINE");
        contractNode.setProperty("createdAt", java.util.Calendar.getInstance());
        contractNode.setProperty("legalNotice", "Verified and protected by global JTA 2PC mechanism.");

        session.save();
        System.out.println(">>> [JCA-JACKRABBIT-EIS] Контракт сформирован и буферизирован в КИС.");
    }
}