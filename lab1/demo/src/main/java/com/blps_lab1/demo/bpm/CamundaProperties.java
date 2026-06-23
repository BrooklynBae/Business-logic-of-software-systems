package com.blps_lab1.demo.bpm;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "camunda")
public class CamundaProperties {
    private String baseUrl = "http://127.0.0.1:8080/engine-rest";
    private String processDefinitionKey = CamundaProcessConstants.PROCESS_DEFINITION_KEY;
    private String workerId = "airbnb-lab4-worker";
    private ExternalTask externalTask = new ExternalTask();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public ExternalTask getExternalTask() {
        return externalTask;
    }

    public void setExternalTask(ExternalTask externalTask) {
        this.externalTask = externalTask;
    }

    public static class ExternalTask {
        private long lockDuration = 30000L;
        private int maxTasks = 1;

        public long getLockDuration() {
            return lockDuration;
        }

        public void setLockDuration(long lockDuration) {
            this.lockDuration = lockDuration;
        }

        public int getMaxTasks() {
            return maxTasks;
        }

        public void setMaxTasks(int maxTasks) {
            this.maxTasks = maxTasks;
        }
    }
}
