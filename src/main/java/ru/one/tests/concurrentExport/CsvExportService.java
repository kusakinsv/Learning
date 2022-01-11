package ru.one.tests.concurrentExport;

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

    public ByteArrayInputStream exportToCsvFile(List<Data> data) throws InvalidObjectException {
        if (separator.equals("")) separator = ";";
        if (data.size() == 0) throw new InvalidObjectException("The exported List of objects is empty");
        var fieldsNames = Arrays.stream(data.get(0).getClass().getDeclaredFields()).collect(Collectors.toList());
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStreamWriter sw = new OutputStreamWriter(byteArrayOutputStream);

            for (int i = 0; i < data.size(); i++) {
                StringBuffer s = new StringBuffer("");
                for (int y = 0; y < fieldsNames.size(); y++) {
                    String fieldNameForSearch = fieldsNames.get(y).getName();
                    Field field = null;

                    field = data.get(i).getClass().getDeclaredField(fieldNameForSearch);
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


    public ByteArrayInputStream exportToCsvFile(List<Data> data, Map<String, String> metadata) throws InvalidObjectException {
        if (data.size() == 0) throw new InvalidObjectException("The exported List of objects is empty");
        object = data.get(0);
        List<Field> fieldsNames = new ArrayList<>();
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStreamWriter sw = new OutputStreamWriter(byteArrayOutputStream);
            for (String s : metadata.keySet()) {
                fieldsNames.add(getField(s));
            }
            for (int i = 0, z = 0; i < data.size(); i++, z++) {
                StringBuffer s = new StringBuffer("");
                for (int y = 0; y < fieldsNames.size(); y++) {
                    String fieldNameForSearch = fieldsNames.get(y).getName();
                    var field = data.get(i).getClass().getDeclaredField(fieldNameForSearch);
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
        var fieldStream = Arrays.stream(this.object.getClass().getDeclaredFields());
        var optionalField = fieldStream.filter(p -> Objects.equals(p.getName(), columnName)).findFirst();
        if (optionalField.isEmpty()) {
            throw new NoSuchFieldException(String.format("Metadata field \"%s\" not found in the exported object", columnName));
        }
        var field = optionalField.get();
        field.setAccessible(true);
        return field;
    }
}

