package ru.one.tests.i1;

public class TreshHoldTestForI1 {
    public static void main(String[] args) {
       double enterInPos = 30.4;
        double tp = 33.4;

        double a = tp-enterInPos;
        System.out.println(a);

        double result = ((a / 3) / 2) + enterInPos;
        System.out.println(result);

    }

}
