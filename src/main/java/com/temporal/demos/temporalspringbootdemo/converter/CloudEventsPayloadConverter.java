package com.temporal.demos.temporalspringbootdemo.converter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.ByteString;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.jackson.JsonFormat;
import io.temporal.api.common.v1.Payload;
import io.temporal.common.converter.DataConverterException;
import io.temporal.common.converter.PayloadConverter;

import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CloudEventsPayloadConverter implements PayloadConverter {

    private EventFormat CEFormat =
            EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);

    @Override
    public String getEncodingType() {
        return "json/plain";
    }

    @Override
    public Optional<Payload> toData(Object value) throws DataConverterException {
        try {
            CloudEvent cloudEvent;

            if(value instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) value;
                cloudEvent = CloudEventBuilder.v1()
                        .withId(objectNode.get("id").asText())
                        .withSource(URI.create(objectNode.get("source").asText()))
                        .withType(objectNode.get("type").asText())
                        .withData(objectNode.get("data").toPrettyString().getBytes(StandardCharsets.UTF_8))
                        .build();


            } else {
                cloudEvent = (CloudEvent) value;
            }

            byte[] serialized = CEFormat.serialize(cloudEvent);

            return Optional.of(
                    Payload.newBuilder()
                            .putMetadata(
                                    "encoding", ByteString.copyFrom(getEncodingType(), StandardCharsets.UTF_8))
                            .setData(ByteString.copyFrom(serialized))
                            .build());

        } catch (EventSerializationException | ClassCastException e) {
            throw new DataConverterException(e);
        }
    }

    @Override
    public <T> T fromData(Payload content, Class<T> valueClass, Type valueType)
            throws DataConverterException {
        try {
            return (T) CEFormat.deserialize(content.getData().toByteArray());
        } catch (ClassCastException e) {
            throw new DataConverterException(e);
        }
    }
}
