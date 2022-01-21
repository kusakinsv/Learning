package ru.one.tests.concurrentExport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.OutputStreamWriter;
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
public class CsvExportService<Data> {
    private Data object;
    private String separator = ";";

    public ByteArrayInputStream exportToCsvFileJson(List<Data> data, Map<String, String> metadata) throws InvalidObjectException {
        if (data.size() == 0) throw new InvalidObjectException("The exported List of objects is empty");
        object = data.get(0);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStreamWriter sw = new OutputStreamWriter(byteArrayOutputStream);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            List<String> requiredColumns = new ArrayList<>(metadata.keySet());
            for (String requiredColumn : requiredColumns) {
                if (!requiredColumn.startsWith("$.")) requiredColumn = "$." + requiredColumn;
            }

            for (int i = 0, z = 0; i < data.size(); i++, z++) {
                StringBuffer s = new StringBuffer("");
                String objectConvertedToJson = mapper.writeValueAsString(data.get(i));
                DocumentContext jsonParsedContext = JsonPath.parse(objectConvertedToJson);
                for (int y = 0; y < requiredColumns.size(); y++) {
                    var value = jsonParsedContext.read(requiredColumns.get(y));
                    if (value instanceof Double || value instanceof Float) {
                        s.append(String.format("%f",(double) value)).append(separator);
                    } else if (value instanceof String) {
                        s.append((String) value).append(separator);
                    } else if (value instanceof Long) {
                        s.append((long) value).append(separator);
                    } else if (value instanceof Integer) {
                        s.append((int) value).append(separator);
                    } else if (value instanceof Boolean) {
                        s.append((Boolean) value).append(separator);
                    } else if (mapper.readValue(value.toString(), LocalDateTime.class) instanceof LocalDateTime) {
                        s.append(mapper.readValue(value.toString(), LocalDateTime.class).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).append(separator);
                    } else if (mapper.readValue(value.toString(), LocalDate.class) instanceof LocalDate) {
                        s.append(mapper.readValue(value.toString(), LocalDateTime.class).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).append(separator);
                    } else s.append(value.toString()).append(separator);
                }
                if (i < data.size()) s.append("\n");
                sw.write(s.toString());
                sw.flush();
            }
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    public ByteArrayInputStream exportToCsvFile(List<Data> data) throws InvalidObjectException {
        if (separator.equals("")) separator = ";";
        if (data.size() == 0) throw new InvalidObjectException("The exported List of objects is empty");
        var fieldsNames = Arrays.stream(data.get(0).getClass().getDeclaredFields()).collect(Collectors.toList());
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStreamWriter sw = new OutputStreamWriter(byteArrayOutputStream);
            List<Field> fieldList = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                StringBuffer s = new StringBuffer("");
                for (int y = 0; y < fieldsNames.size(); y++) {
                    var fieldNameForSearch = fieldsNames.get(y);
                    Class<?> classWhereFindField = data.get(i).getClass();
                    while(classWhereFindField.getSuperclass()!=null){
                        fieldList.addAll(Arrays.asList(classWhereFindField.getDeclaredFields()));
                        classWhereFindField = classWhereFindField.getSuperclass();
                    }
                    Field field = null;
                    field = fieldList.get(fieldList.indexOf(fieldNameForSearch));
                    field.setAccessible(true);
                    var value = field.get(data.get(i));
                    if (value instanceof Double || value instanceof Float) {
                        s.append(String.format("%f", (double) value)).append(separator);
                    } else if (value instanceof String) {
                        s.append((String) value).append(separator);
                    } else if (value instanceof Long) {
                        s.append((long) value).append(separator);
                    } else if (value instanceof Integer) {
                        s.append((int) value).append(separator);
                    } else if (value instanceof Boolean) {
                        s.append((Boolean) value).append(separator);
                    } else if (value instanceof LocalDateTime) {
                        s.append(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).append(separator);
                    } else if (value instanceof LocalDate) {
                        s.append(((LocalDate) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).append(separator);
                    } else s.append(value.toString()).append(separator);
                    fieldList.clear();
                }
                if (i < data.size()) s.append("\n");
                sw.write(s.toString());
                sw.flush();
            }
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException | IllegalAccessException e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    public ByteArrayInputStream exportToCsvFile(List<Data> data, Map<String, String> metadata) throws InvalidObjectException {
        if (data.size() == 0) throw new InvalidObjectException("The exported List of objects is empty");
        object = data.get(0);

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStreamWriter sw = new OutputStreamWriter(byteArrayOutputStream);
            List<Field> fieldList = new ArrayList<>();
            List<Field> fieldsNames = new ArrayList<>();
            for (String s : metadata.keySet()) {
                fieldsNames.add(getField(s));
            }

            for (int i = 0, z = 0; i < data.size(); i++, z++) {
                StringBuffer s = new StringBuffer("");
                for (int y = 0; y < fieldsNames.size(); y++) {
                    var fieldNameForSearch = fieldsNames.get(y);
                    Class<?> classWhereFindField = data.get(i).getClass();
                    while(classWhereFindField.getSuperclass()!=null){
                        fieldList.addAll(Arrays.asList(classWhereFindField.getDeclaredFields()));
                        classWhereFindField = classWhereFindField.getSuperclass();
                    }
                    var field = fieldList.get(fieldList.indexOf(fieldNameForSearch));
                    field.setAccessible(true);
                    var value = field.get(data.get(i));
                    if (value instanceof Double || value instanceof Float) {
                        s.append(String.format("%f",(double) value)).append(separator);
                    } else if (value instanceof String) {
                        s.append((String) value).append(separator);
                    } else if (value instanceof Long) {
                        s.append((long) value).append(separator);
                    } else if (value instanceof Integer) {
                        s.append((int) value).append(separator);
                    } else if (value instanceof Boolean) {
                        s.append((Boolean) value).append(separator);
                    } else if (value instanceof LocalDateTime) {
                        s.append(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).append(separator);
                    } else if (value instanceof LocalDate) {
                        s.append(((LocalDate) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).append(separator);
                    } else s.append(value.toString()).append(separator);
                }
                if (i < data.size()) s.append("\n");
                sw.write(s.toString());
                sw.flush();
            }
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (NoSuchFieldException | IOException | IllegalAccessException e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    private Field getField(String columnName) throws NoSuchFieldException {
        List<Field> fieldList = new ArrayList<>();
        Class<?> classWhereFindField = object.getClass();
        while(classWhereFindField.getSuperclass()!=null){
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
}

