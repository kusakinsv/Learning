package ru.one.tests.concurrentExport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class XlsxExportService<T> {
    private T object;

    private String SHEET_TITLE = "Export";

    public XlsxExportService() throws FileNotFoundException {
    }

    public ByteArrayInputStream exportToExcelFileJson(List<T> data, Map<String, String> metadata) throws IOException {
        if (data.size() == 0) throw new InvalidObjectException("The exported List of objects is empty");
        else if (data.size() > 1048574)
            throw new IllegalArgumentException("XLSX format supports maximum 1048575 values, use CSV method");
        return jsonPathMetadata(data, metadata);
    }

    @SneakyThrows
    private ByteArrayInputStream jsonPathMetadata(List<T> data, Map<String, String> metadata) throws IOException {
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SHEET_TITLE);
            Row row = sheet.createRow(0);
            Font font = workbook.createFont();
            font.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);

            List<String> requiredColumns = new ArrayList<>(metadata.keySet());
            for (String requiredColumn : requiredColumns) {
                if (!requiredColumn.startsWith("$.")) requiredColumn = "$." + requiredColumn;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            for (int i = 0; i < requiredColumns.size(); i++) {
                row.createCell(i);
                row.getCell(i).setCellValue(firstUpperCase(metadata.get(requiredColumns.get(i))));
                row.getCell(i).setCellStyle(headerCellStyle);
            }

            for (int i = 0, z = 0; i < data.size(); i++, z++) {
                Row dataRow = sheet.createRow(i + 1);
                String objectConvertedToJson = mapper.writeValueAsString(data.get(i));
                DocumentContext jsonParsedContext = JsonPath.parse(objectConvertedToJson);
                for (int y = 0; y < requiredColumns.size(); y++) {
                    dataRow.createCell(y);
                    var value = jsonParsedContext.read(requiredColumns.get(y));
                    if (value instanceof Number) {
                        dataRow.getCell(y).setCellValue(((Number) value).doubleValue());
                    } else if (value instanceof String) {
                        dataRow.getCell(y).setCellValue((String) value);
                    } else if (value instanceof Boolean) {
                        dataRow.getCell(y).setCellValue((Boolean) value);
                    } else if (mapper.readValue(value.toString(), LocalDateTime.class) instanceof LocalDateTime) {
                        dataRow.getCell(y).setCellValue((mapper.readValue(value.toString(), LocalDateTime.class)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                    } else if (mapper.readValue(value.toString(), LocalDate.class) instanceof LocalDate) {
                        dataRow.getCell(y).setCellValue((mapper.readValue(value.toString(), LocalDate.class)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    } else {
                        dataRow.getCell(y).setCellValue(value.toString());}
                }
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
    }
    }

    @SneakyThrows
    public ByteArrayInputStream exportToExcelFileOld(List<T> data, Map<String, String> metadata) {
        if (data.size() == 0) throw new InvalidObjectException("The exported List of objects is empty");
        else if (data.size() > 1048574)
            throw new IllegalArgumentException("XLSX format supports maximum 1048575 values");
        object = data.get(0);
        try (Workbook workbook = new SXSSFWorkbook()) {
            List<Field> fieldList = new ArrayList<>();
            Sheet sheet = workbook.createSheet(SHEET_TITLE);
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
                row.getCell(i).setCellStyle(headerCellStyle);
                ;
            }

            for (int i = 0, z = 0; i < data.size(); i++, z++) {
                Row dataRow = sheet.createRow(i + 1);
                for (int y = 0; y < fieldsNames.size(); y++) {
                    dataRow.createCell(y);
                    var fieldNameForSearch = fieldsNames.get(y);
                    Class<?> classWhereFindField = data.get(i).getClass();
                    while (classWhereFindField.getSuperclass() != null) {
                        fieldList.addAll(Arrays.asList(classWhereFindField.getDeclaredFields()));
                        classWhereFindField = classWhereFindField.getSuperclass();
                    }
                    var field = fieldList.get(fieldList.indexOf(fieldNameForSearch));
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
                    fieldList.clear();
                }
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    @SneakyThrows
    public ByteArrayInputStream exportToExcelFile(List<T> data) {
        if (data.size() == 0) throw new InvalidObjectException("The exported List of objects is empty");
        else if (data.size() > 1048575)
            throw new IllegalArgumentException("XLSX format supports maximum 1048576 values");
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SHEET_TITLE);

            Row row = sheet.createRow(0);
            Font font = workbook.createFont();
            font.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);

//            var fieldsNames = Arrays.stream(data.get(0).getClass().getDeclaredFields()).collect(Collectors.toList());
            List<Field> fieldsNames = new LinkedList<>();
            Class<?> current = data.get(0).getClass();
            while (current.getSuperclass() != null) {
                fieldsNames.addAll(Arrays.asList(current.getDeclaredFields()));
                current = current.getSuperclass();
            }

            for (int i = 0; i < fieldsNames.size(); i++) {
                row.createCell(i);
                row.getCell(i).setCellValue(firstUpperCase(fieldsNames.get(i).getName()));
                row.getCell(i).setCellStyle(headerCellStyle);
                ;
            }

            List<Field> fieldList = new ArrayList<>();

            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                for (int y = 0; y < fieldsNames.size(); y++) {
                    dataRow.createCell(y);
                    var fieldNameForSearch = fieldsNames.get(y);

                    Class<?> classWhereFindField = data.get(i).getClass();
                    while (classWhereFindField.getSuperclass() != null) {
                        fieldList.addAll(Arrays.asList(classWhereFindField.getDeclaredFields()));
                        classWhereFindField = classWhereFindField.getSuperclass();
                    }

                    var field = fieldList.get(fieldList.indexOf(fieldNameForSearch));

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
                    fieldList.clear();
                }
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    private Field getField(String columnName) throws NoSuchFieldException {
        List<Field> fieldList = new ArrayList<>();
        Class<?> classWhereFindField = object.getClass();
        while (classWhereFindField.getSuperclass() != null) {
            fieldList.addAll(Arrays.asList(classWhereFindField.getDeclaredFields()));
            classWhereFindField = classWhereFindField.getSuperclass();
        }
        var optionalField = fieldList.stream().filter(p -> Objects.equals(p.getName(), columnName)).findFirst();
        if (optionalField.isEmpty()) {
            throw new NoSuchFieldException(String.format("Metadata field \"%s\" not found in the exported object", columnName));
        }
        var field = optionalField.get();
        field.setAccessible(true);
        return field;
    }

    private String firstUpperCase(String word) {
        if (word == null) return null;
        else if (word.isEmpty()) return word;
        else return word.substring(0, 1).toUpperCase() + word.substring(1);
    }




}
