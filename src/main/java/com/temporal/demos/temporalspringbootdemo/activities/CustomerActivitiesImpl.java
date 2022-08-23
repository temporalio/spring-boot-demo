package com.temporal.demos.temporalspringbootdemo.activities;

import com.temporal.demos.temporalspringbootdemo.model.Customer;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(taskQueues = "CustomerOnboarding")
public class CustomerActivitiesImpl implements CustomerActivities {
    @Override
    public Customer mileStoneOne(Customer customer) {
        customer.setMilestone("Adding customer to system...completed");
        sleep(1);
        return customer;
    }

    @Override
    public Customer mileStoneTwo(Customer customer) {
        customer.setMilestone("Creating customer cloud access...");
        sleep(1);
        return customer;
    }

    @Override
    public Customer mileStoneThree(Customer customer) {
        customer.setMilestone("Manager approval needed...");
        sleep(2);
        return customer;
    }

    private void sleep(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
