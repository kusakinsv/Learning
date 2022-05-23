package ru.one.tests.or;

public class OrTest {
    public static void main(String[] args) {
        boolean a = false;
        int j = 10;

        if ( j == 10|11|12){
            a = true;
        }
        System.out.println(a);
    }
}
