package ru.one.tests.datetime;

import com.mifmif.common.regex.Node;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TimeStringToOffset {
    public static void main(String[] args) {
        OffsetDateTime localTime = OffsetDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
        String moscowTimeOpened = localTime.minusHours(2).format(dtf);
        System.out.println(localTime);
        System.out.println(moscowTimeOpened);

//        OffsetDateTime mytime = OffsetDateTime.parse(moscowTimeOpened, DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
        LocalDateTime localDateTime = LocalDateTime.parse(moscowTimeOpened, dtf);
        OffsetDateTime offsetDateTime = localDateTime.atOffset(ZoneOffset.ofHours(5)).plusHours(2);
        System.out.println(offsetDateTime);
        HashMap hashMap = new HashMap();
    }
}
