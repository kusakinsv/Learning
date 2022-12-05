package ru.one.tests.scannertest;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String message = "Удалить незавершенные сделки? (y/n)";
        System.out.println(message);
        Scanner scanner = new Scanner(System.in);
        boolean enterNotComplete = true;
        while (true) {
            String x = scanner.nextLine();
            if (x.toLowerCase(Locale.ROOT).equals("y")) {
                enterNotComplete = false;
                System.out.println("удаление");
            } else if (x.toLowerCase(Locale.ROOT).equals("n")) {
                System.out.println("пропуск");
                enterNotComplete = false;
                System.exit(0);
            } else {
                System.out.println(message);
            }
        }
    }
}
