package com.temporal.demos.temporalspringbootdemo.activities;

import com.temporal.demos.temporalspringbootdemo.model.Customer;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(taskQueues = "CustomerOnboarding")
public class CustomerActivitiesImpl implements CustomerActivities {
    @Override
    public Customer onboardToCloud(Customer customer) {
        customer.getMilestones().add("Cloud");
        return customer;
    }

    @Override
    public Customer onboardToZendesk(Customer customer) {
        customer.getMilestones().add("Zendesk");
        return customer;
    }

    @Override
    public Customer onboardToSlack(Customer customer) {
        customer.getMilestones().add("Slack");
        return customer;
    }
}
