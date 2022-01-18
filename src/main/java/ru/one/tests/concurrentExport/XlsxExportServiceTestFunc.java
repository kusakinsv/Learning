package ru.one.tests.concurrentExport;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class XlsxExportServiceTestFunc<Data> {
    private Data object;
    private String sheetTitle = "Export";

    public ByteArrayInputStream exportToExcelFile(List<Data> data, Map<String, String> metadata) throws InvalidObjectException {
        if (data.size()==0) throw new InvalidObjectException("The exported List of objects is empty");
        else if (data.size() > 1048574) throw new IllegalArgumentException("XLSX format supports maximum 1048575 values");
        object = data.get(data.size()-1);
        try (Workbook workbook = new SXSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(sheetTitle);
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
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }


    public ByteArrayInputStream exportToExcelFile(List<Data> data) throws InvalidObjectException, NoSuchFieldException, IllegalAccessException {
        if (data.size()==0) throw new InvalidObjectException("The exported List of objects is empty");
        else if (data.size() > 1048574) throw new IllegalArgumentException("XLSX format supports maximum 1048575 values");
        object = data.get(data.size()-1);
        var fieldsNames = Arrays.stream(object.getClass().getDeclaredFields()).collect(Collectors.toList());

        try (Workbook workbook = new SXSSFWorkbook(sizeSetterPhantomWorkbook())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i < data.size(); i++) {
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
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return new ByteArrayInputStream(new byte[0]);
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

    private XSSFWorkbook sizeSetterPhantomWorkbook() throws NoSuchFieldException, IllegalAccessException {
        var fieldsNames = Arrays.stream(object.getClass().getDeclaredFields()).collect(Collectors.toList());
        XSSFWorkbook sizeSetterworkbook = new XSSFWorkbook();
        Sheet sizeSetterSheet = sizeSetterworkbook.createSheet(sheetTitle);
        Row sizeSetterRow = sizeSetterSheet.createRow(0);
        Font font = sizeSetterworkbook.createFont();
        font.setBold(true);
        CellStyle headerCellStyle = sizeSetterworkbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setFont(font);

        for (int i = 0; i < fieldsNames.size(); i++) {
            sizeSetterRow.createCell(i);
            String fieldNameForSearch = fieldsNames.get(i).getName();
            var field = object.getClass().getDeclaredField(fieldNameForSearch);
            field.setAccessible(true);
            var value = field.get(object);
            if (value instanceof Number) {
                sizeSetterRow.getCell(i).setCellValue(((Number) value).doubleValue());
            } else if (value instanceof String) {
                sizeSetterRow.getCell(i).setCellValue((String) value);
            } else if (value instanceof Boolean) {
                sizeSetterRow.getCell(i).setCellValue((Boolean) value);
            } else if (value instanceof LocalDateTime) {
                sizeSetterRow.getCell(i).setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            } else if (value instanceof LocalDate) {
                sizeSetterRow.getCell(i).setCellValue(((LocalDate) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } else sizeSetterRow.getCell(i).setCellValue(value.toString());
            sizeSetterSheet.autoSizeColumn(i, true);
        }

        for (int i = 0; i < fieldsNames.size(); i++) {
            sizeSetterRow.createCell(i);
            sizeSetterRow.getCell(i).setCellValue(firstUpperCase(fieldsNames.get(i).getName()));
            sizeSetterRow.getCell(i).setCellStyle(headerCellStyle);;
        }
        return sizeSetterworkbook;
    }

}

