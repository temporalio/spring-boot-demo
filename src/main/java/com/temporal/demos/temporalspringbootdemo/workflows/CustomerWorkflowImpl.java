package com.temporal.demos.temporalspringbootdemo.workflows;

import com.temporal.demos.temporalspringbootdemo.activities.CustomerActivities;
import com.temporal.demos.temporalspringbootdemo.model.Customer;
import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
    public String onboard(Customer customer) {
        this.customer = customer;

        List<Promise<Customer>> promiseList = new ArrayList<>();
        promiseList.add(Async.function(activities::mileStoneOne, customer)
            .thenApply(c -> { this.customer = c; return c; }));
        promiseList.add(Async.function(activities::mileStoneTwo, customer)
                .thenApply(c -> { this.customer = c; return c; }));
        promiseList.add(Async.function(activities::mileStoneThree, customer)
                .thenApply(c -> { this.customer = c; return c; }));
        Promise.allOf(promiseList).get();

        Workflow.await(() -> managerApproval);

        return "Customer onboarded...";
    }

    @Override
    public String getMilestone() {
        return customer.getMilestone();
    }

    @Override
    public void approve() {
        this.managerApproval = true;
    }
}
