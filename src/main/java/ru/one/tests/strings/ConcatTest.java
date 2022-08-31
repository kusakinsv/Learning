package ru.one.tests.strings;

import java.util.ArrayList;
import java.util.List;

public class ConcatTest {
    public static void main(String[] args) {

        List<String> stringList = new ArrayList<>(){{
            add("ASD");
            add("OOO");
            add("GGG");
        }};
        String result = String.join(", ", stringList);
        System.out.println(result);
    }
}
