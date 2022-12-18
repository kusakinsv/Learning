package ru.one.learning.patterns.builder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Car {
    @Setter private String mark;
    @Setter private Transmission transmission;
    @Setter private Engine engine;
    @Setter private int speed;

    public static CarBuilder toBuilder(){
        return new CarBuilder();
    }

    static class CarBuilder{
        private String mark = "Some Auto";
        private Transmission transmission = Transmission.MANUAL;
        private Engine engine = Engine.PETROL;
        private int speed = 80;

       Car build(){
           Car car = new Car();
           car.setMark(mark);
           car.setTransmission(transmission);
           car.setEngine(engine);
           car.setSpeed(speed);
           return car;
       }

        CarBuilder mark(String mark){
            this.mark = mark;
            return this;
        }
        CarBuilder transmission(Transmission transmission){
            this.transmission = transmission;
            return this;
        }
        CarBuilder engine(Engine engine){
            this.engine = engine;
            return this;
        }
        CarBuilder speed(int speed){
            this.speed = speed;
            return this;
        }
    }
}
