package org.logprocessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    void main() {
        Path path = Path.of("data.csv");

        try (Stream<String> lines = Files.lines(path)) {
            double average = lines
                    .map(this::parseLine)
                    .flatMap(Optional::stream)
                    .filter(log -> log.level() == LogLevel.ERROR)
                    .mapToInt(LogEntry::responseTime)
                    .average()
                    .orElse(0.0);

            IO.println("Average error response time : " + average);
        } catch (IOException e) {
            IO.println("Could not read file: " + e.getMessage());
        }
    }

    private Optional<LogEntry> parseLine(String line) {
        try {
            String[] parts = line.split(",");

            LogEntry logEntry = new LogEntry(
                    parts[0],
                    LogLevel.valueOf(parts[1]),
                    parts[2],
                    Integer.parseInt(parts[3])
            );

            return Optional.of(logEntry);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
