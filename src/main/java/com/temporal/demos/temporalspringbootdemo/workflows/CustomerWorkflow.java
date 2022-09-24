package com.temporal.demos.temporalspringbootdemo.workflows;

import com.temporal.demos.temporalspringbootdemo.model.Customer;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CustomerWorkflow {
    @WorkflowMethod
    Customer onboard(Customer customer);

    @SignalMethod
    void approve();
}
