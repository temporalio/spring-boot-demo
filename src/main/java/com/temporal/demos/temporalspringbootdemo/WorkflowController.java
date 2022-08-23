package com.temporal.demos.temporalspringbootdemo;

import com.temporal.demos.temporalspringbootdemo.model.Customer;
import com.temporal.demos.temporalspringbootdemo.workflows.CustomerWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class WorkflowController {

    @Autowired
    private WorkflowClient workflowClient;

    @GetMapping("/onboard/{name}/{role}")
    String onboard(@PathVariable String name, @PathVariable String role) {
        Customer customer = new Customer(name, role, "Initial");
        CustomerWorkflow workflow = workflowClient.newWorkflowStub(CustomerWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("CustomerOnboarding")
                        .setWorkflowId("Customer-" + name)
                        .build());
        return workflow.onboard(customer);
    }

    @GetMapping("/milestone/{name}")
    String onboard(@PathVariable String name) {
        return workflowClient.newUntypedWorkflowStub("Customer-" + name).query("milestone", String.class);
    }

    @PostMapping(value = "/approve")
    public String approveCustomer(String name) {
        workflowClient.newUntypedWorkflowStub("Customer-" + name).signal("approve");
        return "Thanks for approving: " + name;
    }
}
