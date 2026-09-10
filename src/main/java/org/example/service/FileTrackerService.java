package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FileTrackerService {

    private static final Logger log = LoggerFactory.getLogger(FileTrackerService.class);
    private static final String TRACKER_FILE_NAME = ".processed_files.txt";

    private final Path trackerPath;
    private final Set<String> processedFiles = new HashSet<>();

    public FileTrackerService(Path outputDir) {
        this.trackerPath = outputDir.resolve(TRACKER_FILE_NAME);
        loadProcessedFiles();
    }

    private void loadProcessedFiles() {
        if (Files.exists(trackerPath)) {
            try (Stream<String> lines = Files.lines(trackerPath)) {
                lines.map(String::trim)
                        .filter(s -> !s.isBlank())
                        .forEach(processedFiles::add);
            } catch (IOException e) {
                log.error("Failed to read tracker ledger: {}", trackerPath, e);
            }
        }
    }

    public List<Path> findUnprocessedFiles(Path inputDir) throws IOException {
        try (Stream<Path> stream = Files.list(inputDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .filter(p -> !processedFiles.contains(p.getFileName().toString()))
                    .toList();
        }
    }

    public void markAsProcessed(Path file) {
        String fileName = file.getFileName().toString();
        try {
            Files.writeString(
                    trackerPath,
                    fileName + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            processedFiles.add(fileName);
        } catch (IOException e) {
            log.error("Failed to append file to tracker ledger: {}", fileName, e);
        }
    }
}