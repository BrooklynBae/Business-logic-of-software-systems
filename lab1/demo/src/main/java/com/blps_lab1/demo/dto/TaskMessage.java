package com.blps_lab1.demo.dto;

import java.io.Serializable;

public class TaskMessage implements Serializable {
    private Long draftId;
    private String taskType;

    public TaskMessage() {}
    public TaskMessage(Long draftId, String taskType) {
        this.draftId = draftId;
        this.taskType = taskType;
    }

    public Long getDraftId() { return draftId; }
    public void setDraftId(Long draftId) { this.draftId = draftId; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
}