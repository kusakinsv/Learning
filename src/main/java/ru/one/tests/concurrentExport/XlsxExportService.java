package ru.one.tests.concurrentExport;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InvalidObjectException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class XlsxExportService<Data> {
    private Data object;

    private String EVENT_SHEET_TITLE = "Export";

    public XlsxExportService() throws FileNotFoundException {
    }

    @SneakyThrows
    public ByteArrayInputStream exportToExcelFile(List<Data> data, Map<String, String> metadata) {
        if (data.size()==0) throw new InvalidObjectException("The exported List of objects is empty");
        object = data.get(0);
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(EVENT_SHEET_TITLE);
            Row row = sheet.createRow(0);
            Font font = workbook.createFont();
            font.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);

            List<Field> fieldsNames = new ArrayList<>();
            for (String s : metadata.keySet()) {
                fieldsNames.add(getField(s));
            }

            for (int i = 0; i < fieldsNames.size(); i++) {
                row.createCell(i);
                row.getCell(i).setCellValue(firstUpperCase(metadata.get(fieldsNames.get(i).getName())));
                row.getCell(i).setCellStyle(headerCellStyle);;
            }

            for (int i = 0, z = 0; i < data.size(); i++, z++) {
                Row dataRow = sheet.createRow(i + 1);
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
            for (int i = 0; i < fieldsNames.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    @SneakyThrows
    public ByteArrayInputStream exportToExcelFile(List<Data> data) {
        if (data.size()==0) throw new InvalidObjectException("The exported List of objects is empty");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(EVENT_SHEET_TITLE);

            Row row = sheet.createRow(0);
            Font font = workbook.createFont();
            font.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);

            var fieldsNames = Arrays.stream(data.get(0).getClass().getDeclaredFields()).collect(Collectors.toList());
            int fieldsNumber = data.get(0).getClass().getDeclaredFields().length;
            for (int i = 0; i < fieldsNumber; i++) {
                row.createCell(i);
                row.getCell(i).setCellValue(firstUpperCase(fieldsNames.get(i).getName()));
                row.getCell(i).setCellStyle(headerCellStyle);;
            }
            //todo outOfMemory
            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                for (int y = 0; y < fieldsNumber; y++) {
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
            for (int i = 0; i < fieldsNumber; i++) {
                sheet.autoSizeColumn(i);
            }
            System.out.println("Записываю... " + System.currentTimeMillis());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    private Field getField(String columnName) throws NoSuchFieldException {
        var fieldStream = Arrays.stream(this.object.getClass().getDeclaredFields());
        var optionalField = fieldStream.filter(p -> Objects.equals(p.getName(), columnName)).findFirst();
        if (optionalField.isEmpty()) {
            throw new NoSuchFieldException(String.format("Metadata field \"%s\" not found in the exported object", columnName));
        }
        var field = optionalField.get();
        field.setAccessible(true);
        return field;
    }

    public String firstUpperCase(String word){
        if(word == null) return null;
        else if(word.isEmpty()) return word;
        else return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}

