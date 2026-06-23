package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.bpm.CamundaProcessService;
import com.blps_lab1.demo.bpm.CamundaTaskService;
import com.blps_lab1.demo.dto.BpmCancelRequest;
import com.blps_lab1.demo.dto.BpmStartReservationRequest;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/bpm")
public class BpmReservationController {
    private final CamundaProcessService camundaProcessService;
    private final CamundaTaskService camundaTaskService;

    public BpmReservationController(CamundaProcessService camundaProcessService,
                                    CamundaTaskService camundaTaskService) {
        this.camundaProcessService = camundaProcessService;
        this.camundaTaskService = camundaTaskService;
    }

    @PostMapping("/reservations/start")
    @PreAuthorize("hasRole('USER') or hasAuthority('PERM_PROCESS_PAYMENT')")
    public ResponseEntity<JsonNode> start(@Valid @RequestBody BpmStartReservationRequest request,
                                          Authentication authentication) {
        Map<String, Object> variables = request.toVariables();
        variables.put("startedBy", authentication.getName());
        return ResponseEntity.ok(camundaProcessService.startFastBooking(variables));
    }

    @GetMapping("/reservations/{processInstanceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JsonNode> getProcess(@PathVariable String processInstanceId) {
        return ResponseEntity.ok(camundaProcessService.findProcessInstance(processInstanceId));
    }

    @GetMapping("/tasks/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JsonNode> myTasks(Authentication authentication) {
        return ResponseEntity.ok(camundaTaskService.findMyTasks(authentication));
    }

    @PostMapping("/tasks/{taskId}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JsonNode> completeTask(@PathVariable String taskId,
                                                 @RequestBody(required = false) Map<String, Object> variables) {
        return ResponseEntity.ok(camundaTaskService.completeTask(taskId, variables == null ? Map.of() : variables));
    }

    @PostMapping("/reservations/{processInstanceId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JsonNode> cancel(@PathVariable String processInstanceId,
                                           @RequestBody(required = false) BpmCancelRequest request) {
        String reason = request == null ? null : request.getReason();
        return ResponseEntity.ok(camundaProcessService.cancelProcess(processInstanceId, reason));
    }
}
