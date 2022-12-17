package ru.one.learning.patterns.builder;

public class BuilderApp {
    public static void main(String[] args) {
    Car car = Car.toBuilder()
            .speed(100)
            .mark("BMW")
            .engine(Engine.PETROL)
            .transmission(Transmission.AUTO)
            .build();
        System.out.println(car);

    }
}
