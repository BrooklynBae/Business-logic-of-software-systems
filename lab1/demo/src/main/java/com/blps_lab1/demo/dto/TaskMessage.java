package com.blps_lab1.demo.dto;

import java.io.Serializable;

public class TaskMessage implements Serializable {
    private Long draftId;
    private String taskType;
    private String processInstanceId;

    public TaskMessage() {}

    public TaskMessage(Long draftId, String taskType) {
        this.draftId = draftId;
        this.taskType = taskType;
    }

    public TaskMessage(Long draftId, String taskType, String processInstanceId) {
        this(draftId, taskType);
        this.processInstanceId = processInstanceId;
    }

    public Long getDraftId() { return draftId; }
    public void setDraftId(Long draftId) { this.draftId = draftId; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getProcessInstanceId() { return processInstanceId; }
    public void setProcessInstanceId(String processInstanceId) { this.processInstanceId = processInstanceId; }
}
