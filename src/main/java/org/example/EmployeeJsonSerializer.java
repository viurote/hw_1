package org.example;

import java.util.List;
import java.util.StringJoiner;

public final class EmployeeJsonSerializer {

    private EmployeeJsonSerializer() {

    }

    public static String toJson(List<EmployeeRecord> records) {
        StringJoiner jsonArray = new StringJoiner(",\n  ", "[\n  ", "\n]");

        for (EmployeeRecord record : records) {
            jsonArray.add(toJsonObject(record));
        }

        return jsonArray.toString();
    }

    private static String toJsonObject(EmployeeRecord r) {
        return """
            {
                "id": "%s",
                "name": "%s",
                "age": %d,
                "salary": %d,
                "PROFESSION": "%s"
              }""".formatted(
                r.getId(),
                r.getName(),
                r.getAge(),
                r.getSalary(),
                r.getProfession()
        );
    }
}