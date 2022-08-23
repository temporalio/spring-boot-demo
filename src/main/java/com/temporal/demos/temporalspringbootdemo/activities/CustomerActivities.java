package com.temporal.demos.temporalspringbootdemo.activities;

import com.temporal.demos.temporalspringbootdemo.model.Customer;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CustomerActivities {
    Customer mileStoneOne(Customer customer);
    Customer mileStoneTwo(Customer customer);
    Customer mileStoneThree(Customer customer);
}
