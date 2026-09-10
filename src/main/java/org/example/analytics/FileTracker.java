package org.example.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class FileTracker {

    private static final Logger log = LoggerFactory.getLogger(FileTracker.class);
    private static final String TRACKER_FILE = ".processed_files";

    private final Path trackerFilePath;
    private final Set<String> processedFiles;

    public FileTracker(Path outputDir) throws IOException {
        this.trackerFilePath = outputDir.resolve(TRACKER_FILE);
        this.processedFiles = loadProcessedFiles();
    }

    /**
     * Checks whether a file has already been processed.
     *
     * @param fileName the filename (not full path) to check
     * @return true if the file was already processed
     */
    public boolean isAlreadyProcessed(String fileName) {
        return processedFiles.contains(fileName);
    }

    /**
     * Marks a file as processed by appending its name to the tracker file.
     *
     * @param fileName the filename to mark as processed
     * @throws IOException if writing to the tracker file fails
     */
    public void markAsProcessed(String fileName) throws IOException {
        processedFiles.add(fileName);
        Files.writeString(
                trackerFilePath,
                fileName + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
        log.info("Marked as processed: {}", fileName);
    }

    private Set<String> loadProcessedFiles() throws IOException {
        Set<String> result = new HashSet<>();
        if (Files.exists(trackerFilePath)) {
            Files.readAllLines(trackerFilePath).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .forEach(result::add);
            log.info("Loaded {} previously processed file(s) from tracker", result.size());
        } else {
            log.info("No tracker file found — starting fresh");
        }
        return result;
    }
}
