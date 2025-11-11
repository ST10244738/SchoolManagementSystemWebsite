package com.tirisano.mmogo.school.manager.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.Timestamp;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

@Slf4j
public class TimestampDeserializer extends JsonDeserializer<Timestamp> {

    @Override
    public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Check if the current token is an object (i.e., {seconds: ..., nanos: ...})
        if (p.getCurrentToken() == JsonToken.START_OBJECT) {
            JsonNode node = p.getCodec().readTree(p);

            if (node.has("seconds")) {
                long seconds = node.get("seconds").asLong();
                int nanos = node.has("nanos") ? node.get("nanos").asInt() : 0;
                log.debug("✅ Parsed timestamp from object: seconds={}, nanos={}", seconds, nanos);
                return Timestamp.ofTimeSecondsAndNanos(seconds, nanos);
            }

            log.error("❌ Invalid timestamp object format: {}", node);
            throw new IOException("Invalid timestamp object format. Expected {seconds: ..., nanos: ...}");
        }

        // Otherwise, parse as string
        String value = p.getText();

        if (value == null || value.isEmpty()) {
            return null;
        }

        log.debug("Deserializing timestamp from value: {}", value);

        // Automatically add seconds if missing (handles datetime-local from HTML)
        // Pattern: YYYY-MM-DDTHH:MM (exactly 16 characters)
        if (value.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
            value = value + ":00";
            log.debug("Added seconds to timestamp: {}", value);
        }

        // Try parsing as ISO instant with timezone (e.g., "2025-10-11T11:50:00Z")
        try {
            Instant instant = Instant.parse(value);
            log.debug("✅ Parsed as ISO instant");
            return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
        } catch (DateTimeParseException e1) {
            // Continue to next format
        }

        // Try parsing as LocalDateTime (e.g., "2025-10-11T11:50:00")
        try {
            LocalDateTime dateTime = LocalDateTime.parse(value);
            Instant instant = dateTime.toInstant(ZoneOffset.UTC);
            log.debug("✅ Parsed as LocalDateTime");
            return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
        } catch (DateTimeParseException e2) {
            // Continue to next format
        }

        // Try parsing as date only (e.g., "2025-10-11")
        try {
            LocalDate date = LocalDate.parse(value);
            Instant instant = date.atStartOfDay().toInstant(ZoneOffset.UTC);
            log.debug("✅ Parsed as date only");
            return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
        } catch (DateTimeParseException e3) {
            // Continue to next format
        }

        // Try parsing as epoch milliseconds
        try {
            long epochMilli = Long.parseLong(value);
            Instant instant = Instant.ofEpochMilli(epochMilli);
            log.debug("✅ Parsed as epoch milliseconds");
            return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
        } catch (NumberFormatException e4) {
            log.error("❌ Failed to parse timestamp from value: {}", value);
            throw new IOException("Unable to parse timestamp: '" + value + "'. " +
                    "Supported formats: 2025-10-11T11:50, 2025-10-11T11:50:00, 2025-10-11T11:50:00Z, 2025-10-11");
        }
    }
}