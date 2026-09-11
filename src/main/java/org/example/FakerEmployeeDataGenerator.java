package org.example;

import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakerEmployeeDataGenerator implements DataGenerator<EmployeeRecord>{

    private final Faker faker;

    public FakerEmployeeDataGenerator(Faker faker) {
        this.faker = faker;
    }

    @Override
    public List<EmployeeRecord> generate(int count) {
        List<EmployeeRecord> records = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            records.add(new EmployeeRecord(
                    UUID.randomUUID(),
                    faker.name().fullName(),
                    faker.number().numberBetween(18, 65),
                    faker.number().numberBetween(1500, 7500),
                    faker.options().option(Profession.class)
                    //faker.selection().oneOf(Profession.class)
            ));
        }
        return records;
    }
}
