package ru.one.tests.concurrentExport;

import com.github.javafaker.Faker;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TestExport {
    public static void main(String[] args) throws IOException, NoSuchFieldException {
//        ApplicationContext context = new AnnotationConfigApplicationContext(ExportServiceConfig.class);
//        OutputStream os = new FileOutputStream("C:\\Coding\\IBS\\Components\\ibs-data-export\\textExport.xlsx"); //дома
//        OutputStream os2 = new FileOutputStream("C:\\Coding\\IBS\\Components\\ibs-data-export\\textExport.csv"); //дома
        Faker faker = new Faker();
        long time = System.currentTimeMillis();
        List<DataOne> one = new ArrayList<>();
        List<DataTwo> two = new ArrayList<>();
        for (int i = 0; i < 20000; i++) {
//            one.add(new DataOne((long) i+1, faker.name().fullName(), Double.parseDouble(faker.commerce().price(30000.00, 200000.00).replace(',', '.'))));
            two.add(new DataTwo(i+1, faker.name().firstName(), faker.name().lastName(), faker.address().fullAddress(), Double.parseDouble(faker.commerce().price(30000.00, 200000.00).replace(',', '.')), OffsetDateTime.now().toLocalDateTime()));
        }
        LinkedHashMap<String, String> metadata = new LinkedHashMap<>(){{
            put("id", "ID");
            put("name", "Имя");
            put("surname", "Фамилия");
            put("salary", "Зарплата");
        }};
        long time1 = System.currentTimeMillis()-time;
        System.out.println("На добавление " + time1);
        ConcurrentXLSXExportService xlsxExportService = new ConcurrentXLSXExportService();
        xlsxExportService.exportToExcelFile(two);
//        os.write(xlsxExportService.exportToExcelFile(one).readAllBytes());;
//        os.write(xlsxExportService.exportToExcelFile(one, metadata).readAllBytes());;
//        os.write(xlsxExportService.exportToExcelFile(two).readAllBytes());
        time = System.currentTimeMillis()-time-time1;
        System.out.println("На запись " + time);
//        System.out.println(System.currentTimeMillis());
//        os.close();
//        os2.close();
    }
}
