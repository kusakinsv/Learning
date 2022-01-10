package ru.one.tests.concurrentExport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DataOne {
    private long id;
    private String name;
    private double salary;

    @Override
    public String toString() {
        return String.format(
                "id=%s, name=%s, salary=%s", this.id, this.name, this.salary);
    }
}
