package com.temporal.demos.temporalspringbootdemo;

import com.temporal.demos.temporalspringbootdemo.converter.CloudEventsPayloadConverter;
import io.temporal.common.converter.DataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConverterConfig {
    @Bean
    public DataConverter appDataConverter() {
        return DefaultDataConverter.newDefaultInstance()
                .withPayloadConverterOverrides(new CloudEventsPayloadConverter());
    }
}
