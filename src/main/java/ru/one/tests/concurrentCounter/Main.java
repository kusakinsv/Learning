package ru.one.tests.concurrentCounter;

import com.github.javafaker.Faker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Path applicationPath = Paths.get("").toAbsolutePath();
        OutputStream os = new FileOutputStream(applicationPath + "\\concurrent-counter\\textExport.txt");
        Faker faker = new Faker();
        List<String> recordList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            recordList.add(String.format("%s | %s", i+1, faker.address()));

        }
    }
}
