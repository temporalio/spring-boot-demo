package com.temporal.demos.temporalspringbootdemo.webui.model;

public class SignalInfo {
    private String workflowId;
    private String workflowRunId;
    private String signalName;
    private String signalJson;

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(String workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public String getSignalJson() {
        return signalJson;
    }

    public void setSignalJson(String signalJson) {
        this.signalJson = signalJson;
    }
}
