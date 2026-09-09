package org.example;

import net.datafaker.Faker;

import java.util.UUID;

public class EmployeeRecord {
    public EmployeeRecord(UUID id, String name, int age, int salary, Profession profession) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.profession = profession;
    }
    public static EmployeeRecord random(Faker faker) {
        return new EmployeeRecord(
                UUID.randomUUID(),
                faker.name().fullName(),
                faker.number().numberBetween(18, 65),
                faker.number().numberBetween(1500, 7500),
                faker.options().option(Profession.class)
        );
    }

    private UUID id;
    private String name;
    private int age;
    private int salary;

    private Profession profession;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }
}
