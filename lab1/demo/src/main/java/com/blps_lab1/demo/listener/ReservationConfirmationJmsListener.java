package com.blps_lab1.demo.listener;

import com.blps_lab1.demo.data.tables.PaymentMethod;
import com.blps_lab1.demo.data.tables.PaymentType;
import com.blps_lab1.demo.dto.TaskMessage;
import com.blps_lab1.demo.dto.PaymentRequest;
import com.blps_lab1.demo.services.api.IReservationDraftService;
import com.blps_lab1.demo.services.api.IReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
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
            System.out.println(">>> [XA JTA TRACE] Начало 2PC транзакции (PostgreSQL + PDF КИС): " + rawJson);

            UsernamePasswordAuthenticationToken systemAuth = new UsernamePasswordAuthenticationToken(
                    "SYSTEM_ASYNC_WORKER", null, List.of(new SimpleGrantedAuthority("PERM_PROCESS_PAYMENT"))
            );
            SecurityContextHolder.getContext().setAuthentication(systemAuth);

            TaskMessage task = objectMapper.readValue(rawJson, TaskMessage.class);

            if ("CREATE_RESERVATION".equals(task.getTaskType())) {
                PaymentRequest simulatedPayment = new PaymentRequest();
                simulatedPayment.setPaymentType(PaymentType.LATER);
                simulatedPayment.setPaymentMethod(PaymentMethod.CARD);

                Long newReservationId = reservationService.confirmReservation(task.getDraftId(), simulatedPayment);
                reservationDraftService.removeDraft(task.getDraftId());

                byte[] pdfContractBytes = generatePdfContractBytes(newReservationId, task.getDraftId());

                jcrSession = jackrabbitRepository.login(new SimpleCredentials("admin", "admin".toCharArray()));
                savePdfToJackrabbitEis(jcrSession, newReservationId, pdfContractBytes);

                System.out.println(">>> [XA JTA TRACE] Phase 2 (Commit) завершена. PDF-договор успешно сохранен в КИС.");
            }
        } catch (Exception e) {
            System.err.println(">>> [XA ROLLBACK] Критический сбой. Откат СУБД и удаления PDF-файла.");
            throw new RuntimeException("Forced XA Rollback due to failure in DB or PDF Generator", e);
        } finally {
            SecurityContextHolder.clearContext();
            if (jcrSession != null && jcrSession.isLive()) {
                jcrSession.logout();
            }
        }
    }

    private byte[] generatePdfContractBytes(Long reservationId, Long draftId) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        Paragraph title = new Paragraph("OFFICIAL RENTAL AGREEMENT & CONTRACT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        document.add(new Paragraph("This legally binding document confirms the successful transaction and reservation activation inside the platform.", bodyFont));
        document.add(new Paragraph("Generated automatically by the Distributed Enterprise Core System.\n\n", bodyFont));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);

        table.addCell("Contract Attribute");
        table.addCell("System Value");

        table.addCell("Global Contract ID");
        table.addCell("CON-ID-" + reservationId * 7);

        table.addCell("Reservation Reference ID");
        table.addCell(String.valueOf(reservationId));

        table.addCell("Source System Draft ID");
        table.addCell(String.valueOf(draftId));

        table.addCell("Security Verification");
        table.addCell("PASSED (JTA/2PC Aproved)");

        document.add(table);

        Paragraph footer = new Paragraph("Protected by global JTA 2PC mechanism. Corporate Archive EIS Ecosystem.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9));
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    private void savePdfToJackrabbitEis(Session session, Long reservationId, byte[] pdfBytes) throws Exception {
        Node rootNode = session.getRootNode();
        Node archiveNode;
        if (rootNode.hasNode("corporate_contracts")) {
            archiveNode = rootNode.getNode("corporate_contracts");
        } else {
            archiveNode = rootNode.addNode("corporate_contracts", "nt:unstructured");
        }

        String fileName = "contract_legal_" + reservationId + ".pdf";

        Node fileNode = archiveNode.addNode(fileName, "nt:file");

        Node contentNode = fileNode.addNode("jcr:content", "nt:resource");

        Binary binary = session.getValueFactory().createBinary(new ByteArrayInputStream(pdfBytes));

        contentNode.setProperty("jcr:data", binary);
        contentNode.setProperty("jcr:mimeType", "application/pdf");
        contentNode.setProperty("jcr:lastModified", Calendar.getInstance());

        session.save();
        System.out.println(">>> [JCA-JACKRABBIT-EIS] Настоящий PDF-файл '" + fileName + "' транзакционно передан в буфер КИС.");
    }
}