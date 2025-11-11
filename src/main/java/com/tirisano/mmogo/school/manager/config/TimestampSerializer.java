package com.tirisano.mmogo.school.manager.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.cloud.Timestamp;

import java.io.IOException;
import java.time.Instant;

public class TimestampSerializer extends JsonSerializer<Timestamp> {
    @Override
    public void serialize(Timestamp timestamp, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (timestamp == null) {
            gen.writeNull();
        } else {
            // Convert Timestamp to ISO-8601 string
            Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
            gen.writeString(instant.toString());
        }
    }
}