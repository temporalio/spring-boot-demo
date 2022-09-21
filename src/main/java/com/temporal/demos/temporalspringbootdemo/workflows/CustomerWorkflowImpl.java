package com.temporal.demos.temporalspringbootdemo.workflows;

import com.temporal.demos.temporalspringbootdemo.activities.CustomerActivities;
import com.temporal.demos.temporalspringbootdemo.model.Customer;
import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

import java.time.Duration;

@WorkflowImpl(taskQueues = "CustomerOnboarding")
public class CustomerWorkflowImpl implements CustomerWorkflow {
    private Customer customer;
    boolean managerApproval = false;

    private CustomerActivities activities =
            Workflow.newActivityStub(CustomerActivities.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(3))
                            .build());

    @Override
    public Customer onboard(Customer customer) {
        this.customer = customer;
        // simple impl just run through onboarding steps
        if(customer.getRole().equals("CLOUD")) {
            customer = activities.onboardToCloud(customer);
            customer = activities.onboardToZendesk(customer);
            customer = activities.onboardToSlack(customer);
        } else if(customer.getRole().equals("ZENDESK")) {
            customer = activities.onboardToZendesk(customer);
        } else {
            customer = activities.onboardToSlack(customer);
        }

        customer.setOnboarded("yes");
        return customer;
    }

    @Override
    public void approve() {
        this.managerApproval = true;
    }
}
