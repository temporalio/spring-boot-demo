package com.temporal.demos.temporalspringbootdemo.webui.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import com.temporal.demos.temporalspringbootdemo.webui.dialect.TemporalWebDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalWebDialectConfig {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private WorkflowClient workflowClient;

    @Bean
    public TemporalWebDialect temporalWebDialect() {
        return new TemporalWebDialect(context, workflowClient);
    }

    @Bean("webUiObjectMapper")
    public ObjectMapper webUiObjectMapper() {
        return new ObjectMapper();
    }
}
