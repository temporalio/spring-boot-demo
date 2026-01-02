package com.temporal.demos.temporalspringbootdemo.webui.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import com.temporal.demos.temporalspringbootdemo.webui.model.QueryInfo;
import com.temporal.demos.temporalspringbootdemo.webui.model.SignalInfo;
import com.temporal.demos.temporalspringbootdemo.webui.model.StartInfo;
import com.temporal.demos.temporalspringbootdemo.webui.util.DialectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class TemporalWebDialectController {
    private static final Logger log = LoggerFactory.getLogger(TemporalWebDialectController.class);

    private final WorkflowClient workflowClient;
    private final ObjectMapper objectMapper;
    private final int signalDelayMillis;

    public TemporalWebDialectController(WorkflowClient workflowClient,
                                        @Qualifier("webUiObjectMapper") ObjectMapper objectMapper,
                                        @org.springframework.beans.factory.annotation.Value("${temporal.ui.signal.delay.millis:0}") int signalDelayMillis) {
        this.workflowClient = workflowClient;
        this.objectMapper = objectMapper;
        this.signalDelayMillis = signalDelayMillis;
    }

    @GetMapping("/temporalshowhistory/{wfid}/{wfrunid}")
    public String showExecutionHistory(@PathVariable("wfid") String workflowId,
                                       @PathVariable("wfrunid") String workflowRunId,
                                       Model model) {
        model.addAttribute("workflowid",
                workflowId);
        model.addAttribute("workflowrunid",
                workflowRunId);

        model.addAttribute("workflowhistory",
                new DialectUtils(workflowClient).getWorkflowHistory(workflowId, workflowRunId));
        return "temporalwebdialect :: showhistorymodal";
    }

    @GetMapping("/temporalsignalworkflow/{wfid}/{wfrunid}")
    public String showSignalWorkflow(@PathVariable("wfid") String workflowId,
                                       @PathVariable("wfrunid") String workflowRunId,
                                       Model model) {
        SignalInfo signalInfo = new SignalInfo();
        signalInfo.setWorkflowId(workflowId);
        signalInfo.setWorkflowRunId(workflowRunId);
        model.addAttribute("signalInfo", signalInfo);
        return "temporalwebdialect :: showsignalworkflowmodal";
    }

    @GetMapping("/temporalqueryworkflow/{wfid}/{wfrunid}")
    public String showQueryWorkflow(@PathVariable("wfid") String workflowId,
                                     @PathVariable("wfrunid") String workflowRunId,
                                     Model model) {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setWorkflowId(workflowId);
        queryInfo.setWorkflowRunId(workflowRunId);
        model.addAttribute("queryInfo", queryInfo);
        return "temporalwebdialect :: showqueryworkflowmodal";
    }

    @GetMapping("/temporalgetresult/{wfid}/{wfrunid}")
    public String showGetResult(@PathVariable("wfid") String workflowId,
                                    @PathVariable("wfrunid") String workflowRunId,
                                    Model model) {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setWorkflowId(workflowId);
        queryInfo.setWorkflowRunId(workflowRunId);
        model.addAttribute("queryInfo", queryInfo);
        return "temporalwebdialect :: getresultmodal";
    }

    @PostMapping("/startexec")
    public String startExec(@ModelAttribute StartInfo startInfo,
                                       Model model) {
        WorkflowStub workflowStub =
                workflowClient.newUntypedWorkflowStub(startInfo.getWorkflowType(),
                        WorkflowOptions.newBuilder()
                                .setTaskQueue(startInfo.getTaskQueue())
                                .setWorkflowId(startInfo.getWorkflowId())
                                .build());

        if(startInfo.getInputJson() != null && startInfo.getInputJson().length() > 0) {
            try {
                JsonFactory factory = objectMapper.getFactory();
                JsonParser parser = factory.createParser(startInfo.getInputJson());
                JsonNode inputObj = objectMapper.readTree(parser);
                workflowStub.start(inputObj);
            } catch (Exception e) {
                log.error("Failed to start workflow: {}", startInfo.getWorkflowId(), e);
            }
        } else {
            workflowStub.start();
        }

        return "redirect:/";
    }

    @PostMapping("/signalwf")
    public String signalWf(@ModelAttribute SignalInfo signalInfo,
                            Model model) {

        WorkflowStub workflowStub =
                workflowClient.newUntypedWorkflowStub(signalInfo.getWorkflowId(),
                        Optional.of(signalInfo.getWorkflowRunId()),
                        Optional.empty());

        if(signalInfo.getSignalJson() != null && signalInfo.getSignalJson().length() > 0) {
            try {
                JsonFactory factory = objectMapper.getFactory();
                JsonParser parser = factory.createParser(signalInfo.getSignalJson());
                JsonNode signalObj = objectMapper.readTree(parser);
                workflowStub.signal(signalInfo.getSignalName(), signalObj);
            } catch (Exception e) {
                log.error("Failed to signal workflow: {} with signal: {}",
                    signalInfo.getWorkflowId(), signalInfo.getSignalName(), e);
            }
        } else {
            workflowStub.signal(signalInfo.getSignalName());
        }

        if (signalDelayMillis > 0) {
            try {
                Thread.sleep(signalDelayMillis);
            } catch (InterruptedException e) {
                log.warn("Signal delay interrupted", e);
                Thread.currentThread().interrupt();
            }
        }

        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/querywf/{wfid}/{wfrunid}/{queryname}")
    public String queryWf(@PathVariable("wfid") String workflowId,
                           @PathVariable("wfrunid") String workflowRunId,
                           @PathVariable("queryname") String queryName) {

        try {
            WorkflowStub workflowStub =
                    workflowClient.newUntypedWorkflowStub(workflowId,
                            Optional.of(workflowRunId),
                            Optional.empty());

            CloudEvent ce = workflowStub.query(queryName, CloudEvent.class);

            if (ce == null) {
                log.warn("Query {} returned null for workflow: {}", queryName, workflowId);
                return "Error: Query returned no result";
            }

            String res = new String(EventFormatProvider
                    .getInstance()
                    .resolveFormat(JsonFormat.CONTENT_TYPE)
                    .serialize(ce));

            JsonNode resJson = objectMapper.readTree(res);
            return resJson.toPrettyString();
        } catch (Exception e) {
            log.error("Failed to query workflow: {} with query: {}", workflowId, queryName, e);
            return "Error: " + e.getMessage();
        }
    }

    @ResponseBody
    @GetMapping("/wfresult/{wfid}/{wfrunid}")
    public String getWfResult(@PathVariable("wfid") String workflowId,
                          @PathVariable("wfrunid") String workflowRunId) {

        try {
            WorkflowStub workflowStub =
                    workflowClient.newUntypedWorkflowStub(workflowId,
                            Optional.of(workflowRunId),
                            Optional.empty());

            CloudEvent ce = workflowStub.getResult(CloudEvent.class);

            if (ce == null) {
                log.warn("Workflow result is null for workflow: {}", workflowId);
                return "Error: Workflow returned no result";
            }

            String res = new String(EventFormatProvider
                    .getInstance()
                    .resolveFormat(JsonFormat.CONTENT_TYPE)
                    .serialize(ce));

            JsonNode resJson = objectMapper.readTree(res);
            return resJson.toPrettyString();
        } catch (Exception e) {
            log.error("Failed to get workflow result: {}", workflowId, e);
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/")
    public String displayDemo(Model model) {
        model.addAttribute("startInfo", new StartInfo());
        return "index";
    }
}
