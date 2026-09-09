package org.example;

import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//{ "id": UUID,
// "name": string,
// "age" : int,
// "salary" : int,
// "PROFESSION": (ARTIST,IT,ACCOUNTANT) }

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static Faker faker = new Faker();

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


        int recordsCount = faker.number().numberBetween(10,101);

        List<EmployeeRecord> records = new ArrayList<>();

        for (int i = 0; i < recordsCount; i++){
            records.add(generateRandomEmployee(faker));
        }
        log.info("Generated records: {}",recordsCount);
        log.info("Records List: {}",records);
        }

        private static EmployeeRecord generateRandomEmployee(Faker faker) {
        return new EmployeeRecord(
                UUID.randomUUID(),
                faker.name().fullName(),
                faker.number().numberBetween(18,65),
                faker.number().numberBetween(1500,7500),
                faker.options().option(Profession.class)
        );
        }
}
