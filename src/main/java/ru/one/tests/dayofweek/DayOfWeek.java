package ru.one.tests.dayofweek;

import java.time.OffsetDateTime;

public class DayOfWeek {
    public static void main(String[] args) {
       int day = 1;
       switch (OffsetDateTime.now().plusDays(4).getDayOfWeek().getValue()){
//           case 3,4,5,6: day = 2; break;
           case 7: day = 3; break;
           case 1: day = 4; break;
           case 2: day = 5; break;
           default: day = 2;
       }
        System.out.println(day);
    }
}
