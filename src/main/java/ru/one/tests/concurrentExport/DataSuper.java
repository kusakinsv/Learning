package ru.one.tests.concurrentExport;

import com.github.javafaker.Faker;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataSuper {
    private String hello = "HELLO";
    private int superOne = 1111;

    DataSuper(){
        Faker faker = new Faker();
        this.hello = faker.beer().name();
        this.superOne = faker.number().numberBetween(100, 500);
    }
}
