package ru.one.testapps.concurrentExport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class XlsxExportServiceTestFunc<Data> {
    private Data object;
    private String sheetTitle = "Export";

    public ByteArrayInputStream exportToExcelFile(List<Data> data, Map<String, String> metadata) throws InvalidObjectException {
        if (data.size()==0) throw new InvalidObjectException("The exported List of objects is empty");
        else if (data.size() > 1048574) throw new IllegalArgumentException("XLSX format supports maximum of 1048575 values, use CSV method");
        object = data.get(data.size()-1);
        try (Workbook workbook = new SXSSFWorkbook(createSizeSetterPhantomWorkbook(object, metadata))) {
            Sheet sheet = workbook.getSheetAt(0);
            Font font = workbook.createFont();
            font.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            List<String> requiredColumns = new ArrayList<>(metadata.keySet());

//            Row headerRow = sheet.createRow(0);
//            for (int i = 0; i < requiredColumns.size(); i++) {
//                headerRow.createCell(i);
//                headerRow.getCell(i).setCellValue(firstUpperCase(metadata.get(requiredColumns.get(i))));
//                headerRow.getCell(i).setCellStyle(headerCellStyle);
//            }

            for (String requiredColumn : requiredColumns) {
                requiredColumn = requiredColumn.toLowerCase();
                if (!requiredColumn.startsWith("$.")) requiredColumn = "$." + requiredColumn;
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
            outputStream.flush();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }



    public ByteArrayInputStream exportToExcelFile(List<Data> data) throws InvalidObjectException, NoSuchFieldException, IllegalAccessException {
        if (data.size()==0) throw new InvalidObjectException("The exported List of objects is empty");
        else if (data.size() > 1048574) throw new IllegalArgumentException("XLSX format supports maximum of 1048575 values");
        object = data.get(data.size()-1);
        var fieldsNames = Arrays.stream(object.getClass().getDeclaredFields()).collect(Collectors.toList());

        try (Workbook workbook = new SXSSFWorkbook(createSizeSetterPhantomWorkbook(object))) {
            Sheet sheet = workbook.getSheetAt(0);
            Font font = workbook.createFont();
            font.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);
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

    private String firstUpperCase(String word){
        if(word == null) return null;
        else if(word.isEmpty()) return word;
        else return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private XSSFWorkbook createSizeSetterPhantomWorkbook(Data data, Map<String, String> metadata) throws JsonProcessingException {
        ColumnSizeSetterPhantomXSSFWorkbook columnSizeSetter = new ColumnSizeSetterPhantomXSSFWorkbook(data, metadata, sheetTitle);
        return columnSizeSetter.createColumnSizeSetterBookWithMetadata();
    }

    private XSSFWorkbook createSizeSetterPhantomWorkbook(Data data) throws NoSuchFieldException, IllegalAccessException {
        ColumnSizeSetterPhantomXSSFWorkbook columnSizeSetter = new ColumnSizeSetterPhantomXSSFWorkbook(data, sheetTitle);
        return columnSizeSetter.createColumnSizeSetterBook();
    }

//    private XSSFWorkbook sizeSetterPhantomWorkbook() throws NoSuchFieldException, IllegalAccessException {
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

    class ColumnSizeSetterPhantomXSSFWorkbook<Data> {
        private Data object;
        private Map<String, String> metadata;
        private String sheetTitle;

        public ColumnSizeSetterPhantomXSSFWorkbook(Data object, Map<String, String> metadata, String sheetTitle) {
            this.object = object;
            this.metadata = metadata;
            this.sheetTitle = sheetTitle;
        }

        public ColumnSizeSetterPhantomXSSFWorkbook(Data object, String sheetTitle) {
            this.object = object;
            this.sheetTitle = sheetTitle;
        }

        protected XSSFWorkbook createColumnSizeSetterBook() throws NoSuchFieldException, IllegalAccessException {
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
                sizeSetterRow.getCell(i).setCellValue(firstUpperCase(fieldsNames.get(i).getName()));
                sizeSetterRow.getCell(i).setCellStyle(headerCellStyle);;
            }
            return sizeSetterworkbook;
        }

        protected XSSFWorkbook createColumnSizeSetterBookWithMetadata() throws JsonProcessingException {
            XSSFWorkbook sizeSetterWorkbook = new XSSFWorkbook();
            Sheet sizeSetterSheet = sizeSetterWorkbook.createSheet(sheetTitle);
            Row sizeSetterRow = sizeSetterSheet.createRow(0);
            Font font = sizeSetterWorkbook.createFont();
            font.setBold(true);
            CellStyle headerCellStyle = sizeSetterWorkbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);

            List<String> requiredColumns = new ArrayList<>(metadata.keySet());
            for (String requiredColumn : requiredColumns) {
                if (!requiredColumn.startsWith("$.")) requiredColumn = "$." + requiredColumn;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());


//            for (int i = 0; i < requiredColumns.size(); i++) {
//                sizeSetterRow.createCell(i);
//                sizeSetterRow.getCell(i).setCellValue(firstUpperCase(metadata.get(requiredColumns.get(i))));
//                sizeSetterRow.getCell(i).setCellStyle(headerCellStyle);
//            }

            String objectConvertedToJson = mapper.writeValueAsString(object);
            DocumentContext jsonParsedContext = JsonPath.parse(objectConvertedToJson);
            for (int y = 0; y < requiredColumns.size(); y++) {
                sizeSetterRow.createCell(y);
                var value = jsonParsedContext.read(requiredColumns.get(y));
                if (value instanceof Number) {
                    sizeSetterRow.getCell(y).setCellValue(((Number) value).doubleValue());
                } else if (value instanceof String) {
                    sizeSetterRow.getCell(y).setCellValue((String) value);
                } else if (value instanceof Boolean) {
                    sizeSetterRow.getCell(y).setCellValue((Boolean) value);
                } else if (mapper.readValue(value.toString(), LocalDateTime.class) instanceof LocalDateTime) {
                    sizeSetterRow.getCell(y).setCellValue((mapper.readValue(value.toString(), LocalDateTime.class)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                } else if (mapper.readValue(value.toString(), LocalDate.class) instanceof LocalDate) {
                    sizeSetterRow.getCell(y).setCellValue((mapper.readValue(value.toString(), LocalDate.class)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                } else {
                    sizeSetterRow.getCell(y).setCellValue(value.toString());
                }
                sizeSetterSheet.autoSizeColumn(y, true);
                sizeSetterRow.getCell(y).setCellValue(firstUpperCase(metadata.get(requiredColumns.get(y))));
                sizeSetterRow.getCell(y).setCellStyle(headerCellStyle);;
            }

//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            sizeSetterWorkbook.write(outputStream);
            return sizeSetterWorkbook;
        }

        private String firstUpperCase(String word){
            if(word == null) return null;
            else if(word.isEmpty()) return word;
            else return word.substring(0, 1).toUpperCase() + word.substring(1);
        }
    }
}

