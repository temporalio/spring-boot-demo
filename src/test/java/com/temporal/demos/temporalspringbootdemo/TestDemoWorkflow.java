package com.temporal.demos.temporalspringbootdemo;

import com.temporal.demos.temporalspringbootdemo.activities.DemoActivitiesImpl;
import com.temporal.demos.temporalspringbootdemo.workflows.DemoWorkflow;
import com.temporal.demos.temporalspringbootdemo.workflows.DemoWorkflowImpl;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.common.converter.DataConverter;
import io.temporal.testing.TestEnvironmentOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Random;

@SpringBootTest
@Import(DataConverterTestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDemoWorkflow {

    @Autowired
    TestWorkflowEnvironment testWorkflowEnvironment;
    @Autowired
    WorkflowClient workflowClient;


    @Autowired
    DataConverter dataConverter; // needed until https://github.com/temporalio/sdk-java/issues/1463 is fixed

    @Test
    public void testDemoWorkflow() {

        // This is needed until https://github.com/temporalio/sdk-java/issues/1463 is fixed if fixed
        // The injected testWorkflowEnvironment does not register custom data converters currently
        // Remove this block when issue is fixed
        TestWorkflowEnvironment testWorkflowEnvironment = TestWorkflowEnvironment.newInstance(TestEnvironmentOptions.newBuilder()
                .setWorkflowClientOptions(WorkflowClientOptions.newBuilder()
                        .setDataConverter(dataConverter)
                        .build())
                .build());
        Worker worker = testWorkflowEnvironment.newWorker("DemoTaskQueue");
        worker.registerWorkflowImplementationTypes(DemoWorkflowImpl.class);
        worker.registerActivitiesImplementations(new DemoActivitiesImpl());
        testWorkflowEnvironment.start();
        // end block

        DemoWorkflow workflow =
                testWorkflowEnvironment.getWorkflowClient()
                        .newWorkflowStub(
                                DemoWorkflow.class,
                                WorkflowOptions.newBuilder().setTaskQueue("DemoTaskQueue").build());
        WorkflowClient.start(workflow::exec, getTestCE());

        workflow.addEvent(getTestCE());

        CloudEvent res = WorkflowStub.fromTyped(workflow).getResult(CloudEvent.class);

        Assert.notNull(res, "result is null");
    }

    private CloudEvent getTestCE() {
        Random ran = new Random();
        return CloudEventBuilder.v1()
                .withId(String.valueOf(ran.nextInt(1000 - 1 + 1) + 1))
                .withType("example.demo.result")
                .withSource(URI.create("http://temporal.io"))
                .withData(
                        "application/json",
                        ("{\n" +
                                "       \"first\": \"john\",\n" +
                                "       \"last\": \"doe\"\n" +
                                "     }")
                                .getBytes(Charset.defaultCharset()))
                .build();
    }
}
