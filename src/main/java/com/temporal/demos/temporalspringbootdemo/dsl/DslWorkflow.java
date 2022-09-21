package com.temporal.demos.temporalspringbootdemo.dsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.temporal.demos.temporalspringbootdemo.model.Customer;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DslWorkflow {
    @WorkflowMethod
    JsonNode onboard(String dslSource, String dslDataInput);
}
