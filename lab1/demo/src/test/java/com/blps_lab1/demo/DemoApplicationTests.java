package com.blps_lab1.demo;

import com.blps_lab1.demo.bpm.CamundaProcessConstants;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoApplicationTests {

    @Test
    void camundaProcessAndFormsArePackaged() throws Exception {
        try (InputStream bpmn = resource("bpmn/airbnb-fast-booking-lab4.bpmn")) {
            var document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(bpmn);
            String xml = document.getDocumentElement().getTextContent();
            assertNotNull(xml);
        }

        for (String form : List.of(
                "forms/start-booking.form",
                "forms/search-accommodation.form",
                "forms/select-accommodation.form",
                "forms/confirm-booking.form",
                "forms/admin-moderate-reservation.form",
                "forms/owner-approve-booking.form",
                "forms/payment.form",
                "forms/cancel-booking.form"
        )) {
            assertNotNull(resource(form), form + " must be packaged");
        }

        assertTrue(CamundaProcessConstants.PROCESS_DEFINITION_KEY.equals("airbnb-fast-booking"));
        assertTrue(CamundaProcessConstants.MESSAGE_RESERVATION_ASYNC_PROCESSED.equals("reservation-async-processed"));
    }

    private InputStream resource(String path) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        assertNotNull(stream, path + " must exist");
        return stream;
    }
}
