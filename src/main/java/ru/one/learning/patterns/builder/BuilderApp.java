package ru.one.learning.patterns.builder;

public class BuilderApp {
    public static void main(String[] args) {

    }

    enum Transmission {
        MANUAL, AUTO
    }

    class Car {
        String model;
        Transmission transmission;
        int maxSpeed;

        public Car(String model, Transmission transmission, int maxSpeed) {
            this.model = model;
            this.transmission = transmission;
            this.maxSpeed = maxSpeed;
        }


    }

}
