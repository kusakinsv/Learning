package ru.one.testapps.concurrentExport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class DataTwo extends DataSuper {
    private int id;
    private String name;
    private String surname;
    private String adress;
    private double salary;
    private LocalDateTime date;

    @Override
    public String toString() {
        return String.format(
                "DataTwo (id=%s, name=%s, surname=%s, adress=%s, salary=%s, date=%s)", this.id, this.name, this.surname, this.adress, this.salary, this.date);
    }
}
