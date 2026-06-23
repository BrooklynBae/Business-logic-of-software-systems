package com.blps_lab1.demo.bpm;

public final class CamundaProcessConstants {
    public static final String PROCESS_DEFINITION_KEY = "airbnb-fast-booking";

    public static final String TOPIC_SEARCH_ACCOMMODATIONS = "search-accommodations";
    public static final String TOPIC_CHECK_AVAILABILITY = "check-availability";
    public static final String TOPIC_CREATE_RESERVATION_DRAFT = "create-reservation-draft";
    public static final String TOPIC_SEND_RESERVATION_MESSAGE = "send-reservation-message";
    public static final String TOPIC_CALL_EIS_ADAPTER = "call-eis-adapter";
    public static final String TOPIC_FINALIZE_BOOKING = "finalize-booking";
    public static final String TOPIC_CANCEL_EXPIRED_DRAFT = "cancel-expired-draft";

    private CamundaProcessConstants() {
    }
}
