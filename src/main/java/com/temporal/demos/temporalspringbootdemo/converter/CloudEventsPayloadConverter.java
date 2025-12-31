package com.temporal.demos.temporalspringbootdemo.converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.protobuf.ByteString;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.temporal.api.common.v1.Payload;
import io.temporal.common.converter.DataConverterException;
import io.temporal.common.converter.PayloadConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CloudEventsPayloadConverter implements PayloadConverter {

    public CloudEventsPayloadConverter() {
        mapper = new ObjectMapper();
        // preserve the original value of timezone coming from the server in Payload
        // without adjusting to the host timezone
        // may be important if the replay is happening on a host in another timezone
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    private EventFormat CEFormat =
            EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);
    private final ObjectMapper mapper;

    @Override
    public String getEncodingType() {
        return "json/plain";
    }

    @Override
    public Optional<Payload> toData(Object value) throws DataConverterException {
        try {
            if (value == null) {
                throw new DataConverterException("Cannot convert null value to CloudEvent");
            }

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

            if (cloudEvent == null) {
                throw new DataConverterException("CloudEvent conversion resulted in null");
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
            byte[] data = content.getData().toByteArray();
            if (data == null || data.length == 0) {
                throw new DataConverterException("Payload data is null or empty");
            }

            CloudEvent cloudEvent = CEFormat.deserialize(data);
            if (cloudEvent == null) {
                throw new DataConverterException("Deserialized CloudEvent is null");
            }

            // Type safety check before casting
            if (!valueClass.isAssignableFrom(CloudEvent.class)) {
                throw new DataConverterException(
                    "Cannot convert CloudEvent to " + valueClass.getName() +
                    ". Expected CloudEvent or compatible type.");
            }

            @SuppressWarnings("unchecked")
            T result = (T) cloudEvent;
            return result;
        } catch (DataConverterException e) {
            // Re-throw our own exceptions
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Fall back to Jackson deserialization for non-CloudEvent types or malformed data
            try {
                JavaType reference = mapper.getTypeFactory().constructType(valueType);
                byte[] data = content.getData().toByteArray();
                if (data == null || data.length == 0) {
                    throw new DataConverterException("Payload data is null or empty");
                }
                return mapper.readValue(data, reference);
            } catch (IOException ee) {
                throw new DataConverterException(ee);
            }
        }
    }
}
