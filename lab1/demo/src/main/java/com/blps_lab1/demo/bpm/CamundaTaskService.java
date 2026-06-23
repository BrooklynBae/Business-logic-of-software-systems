package com.blps_lab1.demo.bpm;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CamundaTaskService {
    private final CamundaRestClient camundaRestClient;

    public CamundaTaskService(CamundaRestClient camundaRestClient) {
        this.camundaRestClient = camundaRestClient;
    }

    public JsonNode findMyTasks(Authentication authentication) {
        List<String> groups = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .toList();
        return camundaRestClient.getUserTasks(authentication.getName(), groups);
    }

    public JsonNode completeTask(String taskId, Map<String, Object> variables) {
        return camundaRestClient.completeUserTask(taskId, variables);
    }
}
