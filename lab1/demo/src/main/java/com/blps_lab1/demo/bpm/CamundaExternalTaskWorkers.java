package com.blps_lab1.demo.bpm;

import com.blps_lab1.demo.dto.ReservationRequest;
import com.blps_lab1.demo.dto.TaskMessage;
import com.blps_lab1.demo.services.api.IPlaceService;
import com.blps_lab1.demo.services.api.IReservationDraftService;
import com.blps_lab1.demo.services.api.IReservationService;
import com.blps_lab1.demo.services.utils.JmsTaskProducer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class CamundaExternalTaskWorkers {
    private final CamundaRestClient camundaRestClient;
    private final IPlaceService placeService;
    private final IReservationService reservationService;
    private final IReservationDraftService reservationDraftService;
    private final JmsTaskProducer jmsTaskProducer;
    private final Repository jackrabbitRepository;

    public CamundaExternalTaskWorkers(CamundaRestClient camundaRestClient,
                                      IPlaceService placeService,
                                      IReservationService reservationService,
                                      IReservationDraftService reservationDraftService,
                                      JmsTaskProducer jmsTaskProducer,
                                      Repository jackrabbitRepository) {
        this.camundaRestClient = camundaRestClient;
        this.placeService = placeService;
        this.reservationService = reservationService;
        this.reservationDraftService = reservationDraftService;
        this.jmsTaskProducer = jmsTaskProducer;
        this.jackrabbitRepository = jackrabbitRepository;
    }

    @Scheduled(fixedDelayString = "${camunda.external-task.poll-delay:5000}")
    public void pollExternalTasks() {
        process(CamundaProcessConstants.TOPIC_SEARCH_ACCOMMODATIONS, this::searchAccommodations);
        process(CamundaProcessConstants.TOPIC_CHECK_AVAILABILITY, this::checkAvailability);
        process(CamundaProcessConstants.TOPIC_CREATE_RESERVATION_DRAFT, this::createReservationDraft);
        process(CamundaProcessConstants.TOPIC_SEND_RESERVATION_MESSAGE, this::sendReservationMessage);
        process(CamundaProcessConstants.TOPIC_CALL_EIS_ADAPTER, this::callEisAdapter);
        process(CamundaProcessConstants.TOPIC_FINALIZE_BOOKING, this::finalizeBooking);
        process(CamundaProcessConstants.TOPIC_CANCEL_EXPIRED_DRAFT, this::cancelExpiredDrafts);
    }

    private void process(String topic, ExternalTaskHandler handler) {
        List<Map<String, Object>> tasks;
        try {
            tasks = camundaRestClient.fetchAndLock(topic);
        } catch (Exception e) {
            return;
        }
        for (Map<String, Object> task : tasks) {
            String taskId = String.valueOf(task.get("id"));
            try {
                Map<String, Object> variables = CamundaVariablesMapper.fromCamundaVariables(task.get("variables"));
                Object processInstanceId = task.get(CamundaProcessConstants.VARIABLE_PROCESS_INSTANCE_ID);
                if (processInstanceId != null) {
                    variables.put(CamundaProcessConstants.VARIABLE_PROCESS_INSTANCE_ID, processInstanceId);
                }
                Map<String, Object> result = handler.handle(variables);
                camundaRestClient.completeExternalTask(taskId, result);
            } catch (Exception e) {
                camundaRestClient.failExternalTask(taskId, e.getMessage(), e.toString());
            }
        }
    }

    private Map<String, Object> searchAccommodations(Map<String, Object> variables) {
        String town = stringValue(variables, "town");
        int count = (town == null || town.isBlank())
                ? placeService.findAllSortedByRating().size()
                : placeService.findByTown(town).size();
        return Map.of("accommodationFound", count > 0, "accommodationCount", count);
    }

    private Map<String, Object> checkAvailability(Map<String, Object> variables) {
        Long placeId = longValue(variables, "idPlace", "placeId", "selectedPlaceId");
        LocalDate arrival = dateValue(variables, "arrival");
        LocalDate departure = dateValue(variables, "departure");
        reservationService.ensureDatesAvailable(placeId, arrival, departure);
        return Map.of("available", true);
    }

    private Map<String, Object> createReservationDraft(Map<String, Object> variables) {
        withSystemAuthentication("PERM_PROCESS_PAYMENT", () -> {
            ReservationRequest request = new ReservationRequest();
            request.setUserId(longValue(variables, "userId"));
            request.setIdPlace(longValue(variables, "idPlace", "placeId", "selectedPlaceId"));
            request.setArrival(dateValue(variables, "arrival"));
            request.setDeparture(dateValue(variables, "departure"));
            request.setGuestsAmount(intValue(variables, "guestsAmount", 1));
            request.setPetsAmount(intValue(variables, "petsAmount", 0));
            request.setCoverLetter(stringValue(variables, "coverLetter"));
            var draft = reservationDraftService.createDraft(request);
            variables.put("draftId", draft.getId());
        });
        return Map.of("draftCreated", true, "draftId", variables.get("draftId"));
    }

    private Map<String, Object> sendReservationMessage(Map<String, Object> variables) {
        Long draftId = longValue(variables, "draftId");
        String processInstanceId = stringValue(variables, CamundaProcessConstants.VARIABLE_PROCESS_INSTANCE_ID);
        jmsTaskProducer.sendToQueue("reservation.confirmation", new TaskMessage(draftId, "CREATE_RESERVATION", processInstanceId));
        return Map.of("asyncMessageSent", true);
    }

    private Map<String, Object> callEisAdapter(Map<String, Object> variables) throws Exception {
        Session session = null;
        try {
            session = jackrabbitRepository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            session.getRootNode();
            return Map.of("eisAvailable", true);
        } finally {
            if (session != null && session.isLive()) {
                session.logout();
            }
        }
    }

    private Map<String, Object> finalizeBooking(Map<String, Object> variables) {
        return Map.of("finalizedBySpringWorker", true);
    }

    private Map<String, Object> cancelExpiredDrafts(Map<String, Object> variables) {
        withSystemAuthentication("PERM_MODERATE_DRAFTS", reservationDraftService::deleteExpiredDrafts);
        return Map.of("expiredDraftCleanupRequested", true);
    }

    private void withSystemAuthentication(String authority, Runnable action) {
        var previous = SecurityContextHolder.getContext().getAuthentication();
        try {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    "SYSTEM_CAMUNDA_WORKER",
                    null,
                    List.of(new SimpleGrantedAuthority(authority))
            ));
            action.run();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(previous);
        }
    }

    private String stringValue(Map<String, Object> variables, String key) {
        Object value = variables.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private Long longValue(Map<String, Object> variables, String... keys) {
        for (String key : keys) {
            Object value = variables.get(key);
            if (value instanceof Number number) {
                return number.longValue();
            }
            if (value != null && !String.valueOf(value).isBlank()) {
                return Long.parseLong(String.valueOf(value));
            }
        }
        return null;
    }

    private Integer intValue(Map<String, Object> variables, String key, int fallback) {
        Object value = variables.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return value == null || String.valueOf(value).isBlank() ? fallback : Integer.parseInt(String.valueOf(value));
    }

    private LocalDate dateValue(Map<String, Object> variables, String key) {
        Object value = variables.get(key);
        return value == null ? null : LocalDate.parse(String.valueOf(value));
    }

    @FunctionalInterface
    private interface ExternalTaskHandler {
        Map<String, Object> handle(Map<String, Object> variables) throws Exception;
    }
}
