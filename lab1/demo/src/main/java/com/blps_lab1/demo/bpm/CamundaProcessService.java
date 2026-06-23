package com.blps_lab1.demo.bpm;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CamundaProcessService {
    private final CamundaRestClient camundaRestClient;

    public CamundaProcessService(CamundaRestClient camundaRestClient) {
        this.camundaRestClient = camundaRestClient;
    }

    public JsonNode startFastBooking(Map<String, Object> variables) {
        return camundaRestClient.startProcess(variables);
    }

    public JsonNode findProcessInstance(String processInstanceId) {
        return camundaRestClient.getProcessInstance(processInstanceId);
    }

    public JsonNode cancelProcess(String processInstanceId, String reason) {
        return camundaRestClient.correlateMessage("booking-cancelled", processInstanceId, Map.of(
                "cancelReason", reason == null ? "Cancelled by user" : reason
        ));
    }
}
