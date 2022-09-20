package com.temporal.demos.temporalspringbootdemo.activities;

import com.temporal.demos.temporalspringbootdemo.model.Customer;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CustomerActivities {
    Customer onboardToCloud(Customer customer);
    Customer onboardToZendesk(Customer customer);
    Customer onboardToSlack(Customer customer);
}
