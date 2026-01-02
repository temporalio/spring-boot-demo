package com.temporal.demos.temporalspringbootdemo.activities;

import io.cloudevents.CloudEvent;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Demo implementation of DemoActivities.
 *
 * <p>These activities are intentionally minimal for demonstration purposes.
 * In a production application, these would contain actual business logic such as:
 * - Database operations
 * - External API calls
 * - Data transformations
 * - File processing
 * - etc.
 *
 * <p>This demo focuses on showcasing Temporal workflow patterns (signal-wait,
 * activity orchestration, CloudEvents integration) rather than activity implementations.
 */
@Component
@ActivityImpl(taskQueues = "DemoTaskQueue")
public class DemoActivitiesImpl implements DemoActivities {
    private static final Logger log = LoggerFactory.getLogger(DemoActivitiesImpl.class);

    /**
     * Activity executed before waiting for the signal.
     *
     * <p>Intentionally minimal implementation for demo purposes.
     * In production, this would contain pre-signal business logic.
     *
     * @param cloudEvent the CloudEvent containing input data
     */
    @Override
    public void before(CloudEvent cloudEvent) {
        log.info("Before activity executed with event ID: {}", cloudEvent.getId());
        // Intentionally empty - placeholder for demo purposes
    }

    /**
     * Activity executed after receiving the signal.
     *
     * <p>Intentionally minimal implementation for demo purposes.
     * In production, this would contain post-signal business logic.
     *
     * @param cloudEvent the CloudEvent containing input data
     */
    @Override
    public void after(CloudEvent cloudEvent) {
        log.info("After activity executed with event ID: {}", cloudEvent.getId());
        // Intentionally empty - placeholder for demo purposes
    }
}
