package org.example;

import net.datafaker.Faker;

import java.util.UUID;

public class EmployeeRecordGenerator {
    private final Faker faker;

    public EmployeeRecordGenerator(Faker faker) {
        this.faker = faker;
    }

    public EmployeeRecord random() {
        return new EmployeeRecord(
                UUID.randomUUID(),
                faker.name().fullName(),
                faker.number().numberBetween(18, 65),
                faker.number().numberBetween(1500, 7500),
                faker.options().option(Profession.class)
        );
    }
}
