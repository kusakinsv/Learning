package ru.one.tests.concurrentExport;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
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
            String path = "C:\\Coding\\Java\\Projects\\Learning\\output\\textExport.xlsx";
            OutputStream os2 = new FileOutputStream(path); //дома
            Workbook createWorkbook = new XSSFWorkbook();
            Sheet createSheet = createWorkbook.createSheet("Тест Выгрузка");
            createWorkbook.write(os2);
            createWorkbook.close();
            os2.close();
            System.out.printf("Количество записей: %s \n", data.size());

            File file = File.createTempFile("tmp", ".tmp");
            if (data.size() == 0) throw new InvalidObjectException("The exported List of objects is empty");
            int counterofThreads = 3;
//            if (data.size() > 5000)  counterofThreads = data.size()/5000;
            var fieldsNames = Arrays.stream(data.get(0).getClass().getDeclaredFields()).collect(Collectors.toList());
            List<Thread> threads = new ArrayList<>();

            FileInputStream data1file = new FileInputStream(path);
            Workbook workbook = new XSSFWorkbook(data1file);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0, j = 0; i < counterofThreads; i++, j += 2) {
                System.out.println("j=" + j);
                int finalJ = j;
                int jPlus2 = j+2;
                int finalI = i;


                Thread t = new Thread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        synchronized (data) {
                            for (int i = finalJ; i < jPlus2; i++) {
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
                        }
//                        for (int i = 0; i < fieldsNumber; i++) {
//                            sheet.autoSizeColumn(i);
//                        }
                        if (finalI !=0)threads.get(finalI-1).join();
                        OutputStream os = new FileOutputStream(path); //дома
                        System.out.println(threads.size());

                        workbook.write(os);
                        os.flush();
                        os.close();
                    }
                }, String.format("Thread-%s", i));
                threads.add(t);
                t.start();
                System.out.printf("%s Started \n", t.getName());


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




