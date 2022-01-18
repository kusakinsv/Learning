package ru.one.tests.concurrentExport;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
//todo ТЗ: сделать чтобы выгружал в xlsx(например entity) 10 млн. записей максимально быстро (10 полей)
public class ConcurrentXLSXExportService<Data> {
    private Data object;


        @SneakyThrows
        public void exportToExcelFile(List<Data> data) {
            if (data.size() == 0) throw new InvalidObjectException("The exported List of objects is empty");
//            String path = "C:\\Coding\\Java\\Projects\\Learning\\output\\textExport.xlsx";
//            OutputStream os2 = new FileOutputStream(path); //дома
            Path applicationPath = Paths.get("").toAbsolutePath();
            String path = String.valueOf(applicationPath + "\\export-service\\tmp\\textExport.csv");
            OutputStream tmpFile = new FileOutputStream(path); //дома
            OutputStream os2 = new FileOutputStream(String.valueOf(tmpFile)); //дома
            //tmp Для записи
            File newTempfile = File.createTempFile("export-service-tmp", applicationPath.toString());

            Workbook createWorkbook = new XSSFWorkbook();

            Workbook buffer = WorkbookFactory.create(File.createTempFile("export-service-tmp", applicationPath.toString()));

            Sheet createSheet = createWorkbook.createSheet("Тест Выгрузка");
            createWorkbook.write(os2);
            createWorkbook.close();

            os2.close();
            System.out.printf("Количество записей: %s \n", data.size());
            int countPerThread = 10000; //optional
            int countForThisThread = countPerThread;
            int leftover = 0;
            int  counter = data.size();
            int counterofThreads = 1;
            if (counter <= countPerThread) {
                countForThisThread = counter;}
            if (counter > countPerThread) {
                if ((counter%countPerThread) !=0) {
                    leftover = counter % countPerThread;
                    counterofThreads  = (counter - leftover) / countPerThread;
                    if (leftover > 0) counterofThreads  = counterofThreads +1;
                } else {
                    counterofThreads  = counter/countPerThread;}
            }

            File file = File.createTempFile("tmp", ".tmp");

//            if (data.size() > 5000)  counterofThreads = data.size()/5000;
            var fieldsNames = Arrays.stream(data.get(0).getClass().getDeclaredFields()).collect(Collectors.toList());
            List<Thread> threads = new ArrayList<>();

            FileInputStream data1file = new FileInputStream(path);
            Workbook workbook = new XSSFWorkbook(data1file);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i < counterofThreads; i++) {
                int finalI = i;
                int counterOFRecords = i*countPerThread;
                Thread thread = new Thread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                            StringBuffer s = new StringBuffer("");
                            for (int i = counterOFRecords; i < counterOFRecords+countPerThread; i++) {
//                                Row dataRow = sheet.createRow(i+1);// С заголовком
                                Row dataRow = sheet.createRow(i);
                                for (int y = 0; y < fieldsNames.size(); y++) {
                                    dataRow.createCell(y);
                                    String fieldNameForSearch = fieldsNames.get(y).getName();
                                    var field = data.get(i).getClass().getDeclaredField(fieldNameForSearch);
                                    field.setAccessible(true);
                                    var value = field.get(data.get(i));
                                    if (value instanceof Number) {
                                        dataRow.getCell(y).setCellValue(((Number) value).doubleValue());
                                    } else if (value instanceof String) {
                                        dataRow.getCell(y).setCellValue((String) value);
                                    } else if (value instanceof Boolean) {
                                        dataRow.getCell(y).setCellValue((Boolean) value);
                                    } else if (value instanceof LocalDateTime) {
                                        dataRow.getCell(y).setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                                    } else if (value instanceof LocalDate) {
                                        dataRow.getCell(y).setCellValue(((LocalDate) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                                    } else dataRow.getCell(y).setCellValue(value.toString());
                                }
                            }
//                        for (int i = 0; i < fieldsNumber; i++) {
//                            sheet.autoSizeColumn(i);
//                        }
                        if (finalI > 0) { Thread joiningThread = threads.get(finalI -1);
                            System.out.println("Wait: " + (finalI-1) + " " + Thread.currentThread().getName());
                            joiningThread.join();}
                        OutputStream os = new FileOutputStream(path); //дома
                        System.out.println(threads.size());

                        workbook.write(os);
                        os.flush();
                        os.close();
                    }
                }, String.format("Thread-%s", i));
                threads.add(thread);
                thread.start();
                System.out.printf("%s Started \n", thread.getName());


            }

        }
}
//            try (Workbook workbook = new XSSFWorkbook()) {
//                Sheet sheet = workbook.createSheet("Тест Выгрузка");
//                Row row = sheet.createRow(0);
//                Font font = workbook.createFont();
//                font.setBold(true);
//                CellStyle headerCellStyle = workbook.createCellStyle();
//                headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                headerCellStyle.setFont(font);
//
//
//                for (int i = 0; i < fieldsNumber; i++) {
//                    row.createCell(i);
////                    row.getCell(i).setCellValue(firstUpperCase(fieldsNames.get(i).getName()));
//                    row.getCell(i).setCellStyle(headerCellStyle);;
//                }
//                //todo outOfMemory
//                for (int i = 0; i < data.size(); i++) {
//                    Row dataRow = sheet.createRow(i + 1);
//                    for (int y = 0; y < fieldsNumber; y++) {
//                        dataRow.createCell(y);
//                        String fieldNameForSearch = fieldsNames.get(y).getName();
//                        var field = data.get(i).getClass().getDeclaredField(fieldNameForSearch);
//                        field.setAccessible(true);
//                        var value = field.get(data.get(i));
//                        if (value instanceof Number) {
//                            dataRow.getCell(y).setCellValue(((Number) value).doubleValue());
//                        } else if (value instanceof String) {
//                            dataRow.getCell(y).setCellValue((String) value);
//                        } else if (value instanceof Boolean) {
//                            dataRow.getCell(y).setCellValue((Boolean) value);
//                        } else if (value instanceof LocalDateTime) {
//                            dataRow.getCell(y).setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
//                        } else if (value instanceof LocalDate) {
//                            dataRow.getCell(y).setCellValue(((LocalDate) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//                        } else dataRow.getCell(y).setCellValue(value.toString());
//                    }
//                }
//                for (int i = 0; i < fieldsNumber; i++) {
//                    sheet.autoSizeColumn(i);
//                }
//                for (int i = 0; i < data.size(); i++) {
//                    Row dataRow = sheet.createRow(i + 1);
//                    for (int y = 0; y < fieldsNumber; y++) {
//                        dataRow.createCell(y);
//                        String fieldNameForSearch = fieldsNames.get(y).getName();
//                        var field = data.get(i).getClass().getDeclaredField(fieldNameForSearch);
//                        field.setAccessible(true);
//                        var value = field.get(data.get(i));
//                        if (value instanceof Number) {
//                            dataRow.getCell(y).setCellValue(((Number) value).doubleValue());
//                        } else if (value instanceof String) {
//                            dataRow.getCell(y).setCellValue((String) value);
//                        } else if (value instanceof Boolean) {
//                            dataRow.getCell(y).setCellValue((Boolean) value);
//                        } else if (value instanceof LocalDateTime) {
//                            dataRow.getCell(y).setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
//                        } else if (value instanceof LocalDate) {
//                            dataRow.getCell(y).setCellValue(((LocalDate) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//                        } else dataRow.getCell(y).setCellValue(value.toString());
//                    }
//                }
//                for (int i = 0; i < fieldsNumber; i++) {
//                    sheet.autoSizeColumn(i);
//                }
//                System.out.println("Записываю... " + System.currentTimeMillis());
//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                workbook.write(outputStream);
////                return new ByteArrayInputStream(outputStream.toByteArray());
//            }
//        }



//    }




