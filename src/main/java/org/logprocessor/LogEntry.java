package org.logprocessor;

public record LogEntry (
        String timestamp,
        LogLevel level,
        String message,
        int responseTime
){}
