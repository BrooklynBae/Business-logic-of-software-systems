package com.blps_lab1.demo.bpm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class CamundaRestClient {
    private static final TypeReference<List<Map<String, Object>>> TASK_LIST_TYPE = new TypeReference<>() {
    };

    private final CamundaProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public CamundaRestClient(CamundaProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public JsonNode startProcess(Map<String, Object> variables) {
        return postJson("/process-definition/key/" + properties.getProcessDefinitionKey() + "/start",
                Map.of("variables", CamundaVariablesMapper.toCamundaVariables(variables)));
    }

    public JsonNode getProcessInstance(String processInstanceId) {
        return getJson("/process-instance/" + processInstanceId);
    }

    public JsonNode getUserTasks(String assignee, List<String> candidateGroups) {
        StringBuilder path = new StringBuilder("/task?sortBy=created&sortOrder=desc");
        if (assignee != null && !assignee.isBlank()) {
            path.append("&assignee=").append(encode(assignee));
        }
        if (candidateGroups != null && !candidateGroups.isEmpty()) {
            path.append("&candidateGroupIn=").append(encode(String.join(",", candidateGroups)));
        }
        return getJson(path.toString());
    }

    public JsonNode completeUserTask(String taskId, Map<String, Object> variables) {
        return postJson("/task/" + taskId + "/complete",
                Map.of("variables", CamundaVariablesMapper.toCamundaVariables(variables)));
    }

    public JsonNode correlateMessage(String messageName, String processInstanceId, Map<String, Object> variables) {
        return postJson("/message", Map.of(
                "messageName", messageName,
                "processInstanceId", processInstanceId,
                "processVariables", CamundaVariablesMapper.toCamundaVariables(variables)
        ));
    }

    public List<Map<String, Object>> fetchAndLock(String topic) {
        JsonNode response = postJson("/external-task/fetchAndLock", Map.of(
                "workerId", properties.getWorkerId(),
                "maxTasks", properties.getExternalTask().getMaxTasks(),
                "topics", List.of(Map.of(
                        "topicName", topic,
                        "lockDuration", properties.getExternalTask().getLockDuration()
                ))
        ));
        return objectMapper.convertValue(response, TASK_LIST_TYPE);
    }

    public void completeExternalTask(String taskId, Map<String, Object> variables) {
        postJson("/external-task/" + taskId + "/complete", Map.of(
                "workerId", properties.getWorkerId(),
                "variables", CamundaVariablesMapper.toCamundaVariables(variables)
        ));
    }

    public void failExternalTask(String taskId, String message, String details) {
        postJson("/external-task/" + taskId + "/failure", Map.of(
                "workerId", properties.getWorkerId(),
                "errorMessage", message == null ? "External task failed" : message,
                "errorDetails", details == null ? "" : details,
                "retries", 0,
                "retryTimeout", 0
        ));
    }

    private JsonNode getJson(String path) {
        HttpRequest request = HttpRequest.newBuilder(uri(path))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        return send(request);
    }

    private JsonNode postJson(String path, Object body) {
        try {
            HttpRequest request = HttpRequest.newBuilder(uri(path))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();
            return send(request);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot serialize Camunda request", e);
        }
    }

    private JsonNode send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Camunda REST error " + response.statusCode() + ": " + response.body());
            }
            if (response.body() == null || response.body().isBlank()) {
                return objectMapper.createObjectNode();
            }
            return objectMapper.readTree(response.body());
        } catch (IOException e) {
            throw new IllegalStateException("Camunda REST is unavailable at " + properties.getBaseUrl(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Camunda REST request interrupted", e);
        }
    }

    private URI uri(String path) {
        return URI.create(properties.getBaseUrl().replaceAll("/+$", "") + path);
    }

    private String encode(String value) {
        return value.replace(" ", "%20");
    }
}
