package org.example;

import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    private static final FakerEmployeeDataGenerator generator = new FakerEmployeeDataGenerator(new Faker(Locale.of("en")));

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            log.error("Target directory is not specified! Usage: java Main <output_dir_path>");
            System.exit(1);
        }

        File outputDir = new File(args[0]);

        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();

            if (created) {
                log.info("Created directory: {}",outputDir.getAbsolutePath());
            } else {
                log.error("Failed to create directory: {}", outputDir.getAbsolutePath());
                System.exit(1);
            }
        } else if(!outputDir.isDirectory()) {
            log.error("Specified path is not a directory: {}", outputDir.getAbsolutePath());
            System.exit(1);
        }

        log.info("Files will be saved to: {}", outputDir.getAbsolutePath());

        int recordsCount = ThreadLocalRandom.current().nextInt(10, 101);
        List<EmployeeRecord> records = generator.generate(recordsCount);

        long timestamp = System.currentTimeMillis() / 1000;
        File outputFile = new File(outputDir, timestamp + "_data.txt");

        String jsonContent = EmployeeJsonSerializer.toJson(records);

        try {
            Files.writeString(outputFile.toPath(), jsonContent.toString());
            log.info("Records Json: {}",jsonContent);
            log.info("Saved {} records to {}", records.size(), outputFile.getName());
        } catch (IOException e) {
            log.error("Failed to write to file: {}", outputFile.getAbsolutePath(), e);
        }
    }
}

