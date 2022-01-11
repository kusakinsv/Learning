//package ru.one.tests.concurrentExport;
//
//import lombok.AllArgsConstructor;
//import lombok.SneakyThrows;
//import org.apache.poi.ss.usermodel.Row;
//
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.lang.reflect.Field;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//@AllArgsConstructor
//public class ThreadCsvWriter<Data> implements Runnable {
//    private List<Data> data;
//    private int startRecord;
//    private int countOfRecords;
//
//
//    @SneakyThrows
//    @Override
//    public void run() {
//    String separator = ";";
//        int counter = countPerThread*finalCounterofThreads;
//        OutputStreamWriter writer = new OutputStreamWriter(tmpFile);
//        synchronized (data) {
//            StringBuffer s = new StringBuffer("");
//            for (int i = counter; i < counter+finalCounterofThreads; i++) {
//
//                for (int y = 0; y < fieldsNames.size(); y++) {
//                    String fieldNameForSearch = fieldsNames.get(y).getName();
//                    Field field = null;
//                    field = data.get(i).getClass().getDeclaredField(fieldNameForSearch);
//                    field.setAccessible(true);
//                    var value = field.get(data.get(i));
//                    if (value instanceof Double || value instanceof Float) {
//                        s.append(String.format("%f", (double) value)).append(separator);
//                    } else if (value instanceof String) {
//                        s.append((String) value).append(separator);
//                    } else if (value instanceof Long) {
//                        s.append((long) value).append(separator);
//                    } else if (value instanceof Integer) {
//                        s.append((int) value).append(separator);
//                    } else if (value instanceof Boolean) {
//                        s.append((Boolean) value).append(separator);
//                    } else if (value instanceof LocalDateTime) {
//                        s.append(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).append(separator);
//                    } else if (value instanceof LocalDate) {
//                        s.append(((LocalDate) value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).append(separator);
//                    } else s.append(value.toString()).append(separator);
//                }
//                if (i < data.size()) s.append("\n");
//            }
//            writer.write(s.toString());
//            writer.flush();
//
//
//    }
//
//}
