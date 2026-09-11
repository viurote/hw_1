package org.example;

import lombok.Getter;

import java.util.UUID;

@Getter
public class EmployeeRecord {
    public EmployeeRecord(UUID id, String name, int age, int salary, Profession profession) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.profession = profession;
    }

    private final UUID id;
    private final String name;
    private final int age;
    private final int salary;

    private final Profession profession;

    @Override
    public String toString() {
        return "EmployeeRecord{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                ", profession='" + profession + '\'' +
                '}';
    }

//    public static EmployeeRecord random(Faker faker) {
//        return new EmployeeRecord(
//                UUID.randomUUID(),
//                faker.name().fullName(),
//                faker.number().numberBetween(18, 65),
//                faker.number().numberBetween(1500, 7500),
//                faker.options().option(Profession.class)
//        );
//    }
}
