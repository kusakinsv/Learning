package ru.one.testapps.concurrentExport.model;

import com.github.javafaker.Faker;

public class Automobile {
    private String hello;
    private int superOne;

    Automobile(){
        Faker faker = new Faker();
        this.hello = faker.beer().name();
        this.superOne = faker.number().numberBetween(100, 500);
    }

    public String getHello() {
        return this.hello;
    }

    public int getSuperOne() {
        return this.superOne;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    public void setSuperOne(int superOne) {
        this.superOne = superOne;
    }
}
