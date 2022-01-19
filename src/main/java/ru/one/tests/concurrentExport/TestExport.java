package ru.one.tests.concurrentExport;

import com.github.javafaker.Faker;
import ru.one.tests.concurrentExport.model.Car;
import ru.one.tests.concurrentExport.model.Gear;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TestExport {
    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        Path applicationPath = Paths.get("").toAbsolutePath();
        String outputFilePath =  applicationPath + "\\export-service\\output\\textExport.csv";
//        ApplicationContext context = new AnnotationConfigApplicationContext(ExportServiceConfig.class);
//        OutputStream os = new FileOutputStream("C:\\Coding\\IBS\\Components\\ibs-data-export\\textExport.xlsx"); //дома
        OutputStream os2 = new FileOutputStream(outputFilePath); //дома
        Faker faker = new Faker();
        long time = System.currentTimeMillis();
        //////////////////////////////////////////////
        List<Gear> gears = new ArrayList<>(){{
            add(new Gear(1, new String[] { "RED", "BLUE", "GREEN", "BLACK" }));
            add(new Gear(8, new String[] { "RED", "BLUE", "GREEN", "BLACK" }));
            add(new Gear(3, new String[] { "RED", "BLUE", "GREEN", "BLACK" }));
            add(new Gear(6, new String[] { "RED", "BLUE", "GREEN", "BLACK" }));
        }};
        List<Car> cars = new ArrayList<>(){{
            add(new Car("BMW", 6000000, gears));
            add(new Car("Tesla", 10000000, gears));
            add(new Car("Mazda", 2000000, gears));
            add(new Car("Audi", 45000000, gears));
            add(new Car("Honda", 2800000, gears));
            add(new Car("Toyota", 2000000, gears));
            add(new Car("Hyndai", 1600000, gears));
        }};

        List<DataTwo> two = new ArrayList<>();
//        for (int i = 0; i < 1000000; i++) {
////            one.add(new DataOne((long) i+1, faker.name().fullName(), Double.parseDouble(faker.commerce().price(30000.00, 200000.00).replace(',', '.'))));
//            two.add(new DataTwo(i+1, faker.name().firstName(), faker.name().lastName(), faker.address().fullAddress(), Double.parseDouble(faker.commerce().price(30000.00, 200000.00).replace(',', '.')), OffsetDateTime.now().toLocalDateTime()));
//        }
        LinkedHashMap<String, String> metadata = new LinkedHashMap<>(){{
            put("id", "ID");
            put("name", "Имя");
            put("surname", "Фамилия");
            put("hello", "Зарплата");
        }};
        ///////////////////////
        long time1 = System.currentTimeMillis()-time;
        System.out.println("На добавление " + time1);

        LinkedHashMap<String, String> metadataJsonPath = new LinkedHashMap<>(){{
            put("$.model", "MODEL");
            put("$.price", "PRICE");
            put("$.gears[2].size", "Фамилия");
//            put("hello", "Зарплата");
        }};




//        CsvExportService csvExportService = new CsvExportService();
//        os2.write(csvExportService.exportToCsvFileJson(two, metadata).readAllBytes());
//        XlsxExportServiceTestFunc exportServiceTestFunc = new XlsxExportServiceTestFunc();
        XlsxExportService xlsxExportService = new XlsxExportService();
        os2.write(xlsxExportService.exportToExcelFileJson(cars, metadataJsonPath).readAllBytes());;
        time = System.currentTimeMillis()-time-time1;
        System.out.println("На запись " + time);
//        System.out.println(System.currentTimeMillis());
//        os.close();
//        os2.close();
    }
}
