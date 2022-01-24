//package ru.one.tests.concurrentExport;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.jayway.jsonpath.DocumentContext;
//import com.jayway.jsonpath.JsonPath;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.streaming.SXSSFWorkbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//class ColumnSizeSetterPhantomXSSFWorkbook<Data> {
//    private Data object;
//    private Map<String, String> metadata;
//    private String sheetTitle;
//
//    public ColumnSizeSetterPhantomXSSFWorkbook(Data object, Map<String, String> metadata, String sheetTitle) {
//        this.object = object;
//        this.metadata = metadata;
//        this.sheetTitle = sheetTitle;
//    }
//
//    public ColumnSizeSetterPhantomXSSFWorkbook(Data object, String sheetTitle) {
//        this.object = object;
//        this.sheetTitle = sheetTitle;
//    }
//
//    protected XSSFWorkbook createColumnSizeSetterBook() throws NoSuchFieldException, IllegalAccessException {
//        var fieldsNames = Arrays.stream(object.getClass().getDeclaredFields()).collect(Collectors.toList());
//        XSSFWorkbook sizeSetterworkbook = new XSSFWorkbook();
//        Sheet sizeSetterSheet = sizeSetterworkbook.createSheet(sheetTitle);
//        Row sizeSetterRow = sizeSetterSheet.createRow(0);
//        Font font = sizeSetterworkbook.createFont();
//        font.setBold(true);
//        CellStyle headerCellStyle = sizeSetterworkbook.createCellStyle();
//        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        headerCellStyle.setFont(font);
//
//        for (int i = 0; i < fieldsNames.size(); i++) {
//            sizeSetterRow.createCell(i);
//            String fieldNameForSearch = fieldsNames.get(i).getName();
//            var field = object.getClass().getDeclaredField(fieldNameForSearch);
//            field.setAccessible(true);
//            var value = field.get(object);
//            if (value instanceof Number) {
//                sizeSetterRow.getCell(i).setCellValue(((Number) value).doubleValue());
//            } else if (value instanceof String) {
//                sizeSetterRow.getCell(i).setCellValue((String) value);
//            } else if (value instanceof Boolean) {
//                sizeSetterRow.getCell(i).setCellValue((Boolean) value);
//            } else if (value instanceof LocalDateTime) {
//                sizeSetterRow.getCell(i).setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
//            } else if (value instanceof LocalDate) {
//                sizeSetterRow.getCell(i).setCellValue(((LocalDate) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//            } else sizeSetterRow.getCell(i).setCellValue(value.toString());
//            sizeSetterSheet.autoSizeColumn(i, true);
//            sizeSetterRow.getCell(i).setCellValue(firstUpperCase(fieldsNames.get(i).getName()));
//            sizeSetterRow.getCell(i).setCellStyle(headerCellStyle);;
//        }
//        return sizeSetterworkbook;
//    }
//
//    protected XSSFWorkbook createColumnSizeSetterBookWithMetadata() throws JsonProcessingException {
//        XSSFWorkbook sizeSetterWorkbook = new XSSFWorkbook();
//            Sheet sizeSetterSheet = sizeSetterWorkbook.createSheet(sheetTitle);
//            Row sizeSetterRow = sizeSetterSheet.createRow(0);
//            Font font = sizeSetterWorkbook.createFont();
//            font.setBold(true);
//            CellStyle headerCellStyle = sizeSetterWorkbook.createCellStyle();
//            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            headerCellStyle.setFont(font);
//
//            List<String> requiredColumns = new ArrayList<>(metadata.keySet());
//            for (String requiredColumn : requiredColumns) {
//                if (!requiredColumn.startsWith("$.")) requiredColumn = "$." + requiredColumn;
//            }
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.registerModule(new JavaTimeModule());
//
//
////            for (int i = 0; i < requiredColumns.size(); i++) {
////                sizeSetterRow.createCell(i);
////                sizeSetterRow.getCell(i).setCellValue(firstUpperCase(metadata.get(requiredColumns.get(i))));
////                sizeSetterRow.getCell(i).setCellStyle(headerCellStyle);
////            }
//
//                String objectConvertedToJson = mapper.writeValueAsString(object);
//                DocumentContext jsonParsedContext = JsonPath.parse(objectConvertedToJson);
//                for (int y = 0; y < requiredColumns.size(); y++) {
//                    sizeSetterRow.createCell(y);
//                    var value = jsonParsedContext.read(requiredColumns.get(y));
//                    if (value instanceof Number) {
//                        sizeSetterRow.getCell(y).setCellValue(((Number) value).doubleValue());
//                    } else if (value instanceof String) {
//                        sizeSetterRow.getCell(y).setCellValue((String) value);
//                    } else if (value instanceof Boolean) {
//                        sizeSetterRow.getCell(y).setCellValue((Boolean) value);
//                    } else if (mapper.readValue(value.toString(), LocalDateTime.class) instanceof LocalDateTime) {
//                        sizeSetterRow.getCell(y).setCellValue((mapper.readValue(value.toString(), LocalDateTime.class)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
//                    } else if (mapper.readValue(value.toString(), LocalDate.class) instanceof LocalDate) {
//                        sizeSetterRow.getCell(y).setCellValue((mapper.readValue(value.toString(), LocalDate.class)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//                    } else {
//                        sizeSetterRow.getCell(y).setCellValue(value.toString());
//                    }
//                    sizeSetterSheet.autoSizeColumn(y, true);
//                    sizeSetterRow.getCell(y).setCellValue(firstUpperCase(metadata.get(requiredColumns.get(y))));
//                    sizeSetterRow.getCell(y).setCellStyle(headerCellStyle);;
//                }
//
////            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
////            sizeSetterWorkbook.write(outputStream);
//            return sizeSetterWorkbook;
//    }
//
//    private String firstUpperCase(String word){
//        if(word == null) return null;
//        else if(word.isEmpty()) return word;
//        else return word.substring(0, 1).toUpperCase() + word.substring(1);
//    }
//}
