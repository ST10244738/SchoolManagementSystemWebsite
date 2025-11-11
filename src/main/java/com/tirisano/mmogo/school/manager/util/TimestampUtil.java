package com.tirisano.mmogo.school.manager.util;

import com.google.cloud.Timestamp;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

@Slf4j
public class TimestampUtil {

    /**
     * Parse ISO string to Timestamp, handling various formats including datetime-local
     * Supports: 2025-10-07T01:33, 2025-10-07T01:33:00, 2025-10-07T01:33:00Z, 2025-10-07
     */
    public static Timestamp fromIsoString(String isoString) {
        if (isoString == null || isoString.isEmpty()) {
            return null;
        }

        log.debug("Parsing timestamp from: {}", isoString);

        // Add seconds if missing (handles datetime-local format: YYYY-MM-DDTHH:MM)
        if (isoString.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
            isoString = isoString + ":00";
            log.debug("Added seconds: {}", isoString);
        }

        // Try parsing as ISO instant with timezone (e.g., 2025-10-07T01:33:00Z)
        try {
            Instant instant = Instant.parse(isoString);
            log.debug("✅ Parsed as ISO instant");
            return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
        } catch (DateTimeParseException e1) {
            // Continue to next format
        }

        // Try parsing as LocalDateTime (e.g., 2025-10-07T01:33:00)
        // Treat the input as local time in South Africa timezone (UTC+2)
        try {
            LocalDateTime dateTime = LocalDateTime.parse(isoString);
            // Use Africa/Johannesburg timezone (SAST - South African Standard Time)
            // This ensures the time entered by the user is preserved correctly
            ZoneId saZone = ZoneId.of("Africa/Johannesburg");
            Instant instant = dateTime.atZone(saZone).toInstant();
            log.debug("✅ Parsed as LocalDateTime in South Africa timezone");
            return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
        } catch (DateTimeParseException e2) {
            // Continue to next format
        }

        // Try parsing as date only (e.g., 2025-10-07)
        try {
            LocalDate date = LocalDate.parse(isoString);
            Instant instant = date.atStartOfDay().toInstant(ZoneOffset.UTC);
            log.debug("✅ Parsed as date only");
            return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
        } catch (DateTimeParseException e3) {
            log.error("❌ Failed to parse timestamp: {}", isoString);
            throw new IllegalArgumentException("Unable to parse timestamp: " + isoString +
                    ". Supported formats: 2025-10-07T01:33, 2025-10-07T01:33:00, 2025-10-07T01:33:00Z, 2025-10-07");
        }
    }

    /**
     * Convert Timestamp to ISO string
     */
    public static String toIsoString(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()).toString();
    }
}