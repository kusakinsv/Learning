package ru.one.tests.concurrentExport.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Gear {
    private int size;
    private String[] color;
}
