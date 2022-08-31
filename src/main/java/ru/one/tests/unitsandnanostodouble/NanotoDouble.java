package ru.one.tests.unitsandnanostodouble;

public class NanotoDouble {
    public static void main(String[] args) {
        long a = 174;
        int b = 420000000;

        double a1 = a;
        double result = a + ((double)b)/1000000000;
        System.out.println(a1);
        System.out.println(result);


    }

}
