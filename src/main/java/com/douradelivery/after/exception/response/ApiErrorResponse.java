package com.douradelivery.after.exception.response;

import java.time.LocalDateTime;

public class ApiErrorResponse {

    private boolean success = false;
    private String message;
    private LocalDateTime timestamp;

    public ApiErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

