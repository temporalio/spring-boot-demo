package com.temporal.demos.temporalspringbootdemo;

import com.temporal.demos.temporalspringbootdemo.model.Customer;
import com.temporal.demos.temporalspringbootdemo.workflows.CustomerWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class WorkflowController {

    @Autowired
    private WorkflowClient workflowClient;

    @PostMapping(value = "/onboard",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Customer onboardNewCustomer(@RequestBody Customer customer) {
        CustomerWorkflow workflow = workflowClient
                .newWorkflowStub(CustomerWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("CustomerOnboarding")
                        .setWorkflowId("Customer-" + customer.getName())
                        .build());
        return workflow.onboard(customer);
    }
}
