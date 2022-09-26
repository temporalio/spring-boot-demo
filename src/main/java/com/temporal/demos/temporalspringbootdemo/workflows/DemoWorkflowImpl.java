package com.temporal.demos.temporalspringbootdemo.workflows;

import com.temporal.demos.temporalspringbootdemo.activities.DemoActivities;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@WorkflowImpl(taskQueues = "DemoTaskQueue")
public class DemoWorkflowImpl implements DemoWorkflow {

    private List<CloudEvent> eventList = new ArrayList<>();

    private DemoActivities demoActivities =
            Workflow.newActivityStub(DemoActivities.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(2))
                            .build());

    @Override // WorkflowMethod
    public CloudEvent exec(CloudEvent cloudEvent) {
        eventList.add(cloudEvent);

        demoActivities.before(cloudEvent);

        // wait for second event

        Workflow.await(() -> eventList.size() == 2);

        demoActivities.after(cloudEvent);

        // return demo result CE
        return CloudEventBuilder.v1()
                .withId(String.valueOf(1000))
                .withType("example.demo.result")
                .withSource(URI.create("http://temporal.io"))
                .withData(
                        "application/json",
                        ("{\n" + "\"result\": \"demo completed\"\n" + "}")
                                .getBytes(Charset.defaultCharset()))
                .build();

    }

    @Override // SignalMethod
    public void addEvent(CloudEvent cloudEvent) {
        eventList.add(cloudEvent);
    }

    @Override // QueryMethod
    public CloudEvent getLastEvent() {
        return eventList.get(eventList.size() - 1);
    }
}
