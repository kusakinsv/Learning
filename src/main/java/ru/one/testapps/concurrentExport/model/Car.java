package ru.one.testapps.concurrentExport.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Car extends Automobile{
    private String model;
    private int price;
    private List<Gear> gears;
}
