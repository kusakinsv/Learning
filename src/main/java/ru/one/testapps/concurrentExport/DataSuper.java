package ru.one.testapps.concurrentExport;

import com.github.javafaker.Faker;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataSuper {
    private String hello = "HELLO";
    private int number = 1111;

//    DataSuper(){
//        Faker faker = new Faker();
//        this.hello = faker.beer().name();
//        this.number = faker.number().numberBetween(100, 500);
//    }
}
