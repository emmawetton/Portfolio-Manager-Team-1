package com.portfolio.api.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ErrorResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        int status = 404;
        String message = "Not Found";
        LocalDateTime timestamp = LocalDateTime.now();

        ErrorResponse response = new ErrorResponse(status, message, timestamp);

        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    void testSetters() {
        ErrorResponse response = new ErrorResponse(0, null, null);

        int status = 500;
        String message = "Internal Server Error";
        LocalDateTime timestamp = LocalDateTime.now();

        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(timestamp);

        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime timestamp = LocalDateTime.now();

        ErrorResponse r1 = new ErrorResponse(400, "Bad Request", timestamp);
        ErrorResponse r2 = new ErrorResponse(400, "Bad Request", timestamp);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        ErrorResponse response = new ErrorResponse(
                401,
                "Unauthorized",
                LocalDateTime.now()
        );

        assertNotNull(response.toString());
        assertTrue(response.toString().contains("Unauthorized"));
    }
}

