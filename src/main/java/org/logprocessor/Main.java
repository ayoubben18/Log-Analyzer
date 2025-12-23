package org.logprocessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    void main() {
        AtomicInteger grandTotal = new AtomicInteger(0);
        List<String> files = List.of("data1.csv", "data2.csv", "data3.csv");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (String file : files) {
                executor.submit(() -> {
                    int totalTime = getTotalErrorTime(file);
                    // thread safe
                    grandTotal.addAndGet(totalTime);
                    IO.println(file + " processed. Total error time: " + totalTime);
                });
            }
        }

        IO.println("All logs are processed");
    }

    private int getTotalErrorTime(String fileName) {
        Path path = Path.of(fileName);

        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .map(this::parseLine)
                    .flatMap(Optional::stream)
                    .filter(log -> log.level() == LogLevel.ERROR)
                    .mapToInt(LogEntry::responseTime)
                    .sum();
        } catch (IOException e) {
            IO.println("Error reading " + fileName + ":" + e.getMessage());
            return 0;
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
