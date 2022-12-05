package ru.one.testapps.stringserchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentNumberSercher {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>() {{
            add("Плата за выдачу наличных к чеку № '6242822' от '12/12/2020' согласно договору РКО № 2101-Д от '13/04/2005'");
            add("По счету № 21-11101161071 от 11.10.2021 за перевозку грузов и транспортно-экспедиционные услуги В т.ч. НДС (20%) 139-33");
            add("Оплата по счету № 106/3-6 от 22.10.2012 по Дог. № 507 от 12.10.2012 Раб.по конс. тех. сопр.сер.пр-ва РГКБ насосов ДЦН22С-ДТ2 за 1кв.2021г. Акт № 1112/2021 В т.ч. НДС (20%) 1223-80");
            add("По сч.№771от31.10.2021 г. за никель первичный марки Н1 по Спец.№01от11.01.2020г. к дог.№ 111-П03/16 от 12.02.2016г.Курс Долл.США на 02.02.2016=73,9746руб. . В т.ч. НДС (20%) 50547-79");
            add("Счет № 1111 от 11.10.2021; Оказание услуг по сбору, вывозу и утилизации мусора;к Договору № 181-2101/2001 от 11.02.2021 Без налога (НДС)");
        }};
        for (String s : list) {
            s = findDocument(s);
            System.out.println(s);
        }

    }

    public static String findDocument(String findString) {
        String stringlowCase = findString.toLowerCase();
        List<String> substring = new ArrayList<>();//Arrays.asList(string.split("\\s*(\\s|,|!|\\.)\\s*"));
        List<String> schet2 = Arrays.asList("дог.", "договор", "договору", "договора");
        List<String> schet = Arrays.asList("cчет", "счету", "счета", "фактуре", "фактура", "фактуры", "сч.ф", "сч.", "с-ф", "счет-договор", "счет-договору", "счет-договора", "дог.", "договор", "договору", "договора");
        List<String> founded = new ArrayList<>();
        for (int i = 0; i < schet.size(); i++) {
            if (stringlowCase.contains(schet.get(i))) {
                founded.add(schet.get(i));
            }
        }
        if (founded.size() == 0) {
            for (int i = 0; i < schet2.size(); i++) {
                if (stringlowCase.contains(schet2.get(i))) {
                    founded.add(schet2.get(i));
                }
            }
        }
        if (founded.size() == 0) return findString;
        String[] splitString = stringlowCase.split(founded.get(0));
        stringlowCase = splitString[1];
        substring = Arrays.asList(stringlowCase.split("\\s*(\\s|,|!|\\.)\\s*"));


        String preape = "";
        int counter = 1;
        for (int i = 0; i < substring.size(); i++) {
            if (substring.get(i).contains("№")) preape = substring.get(i);
            if (preape.equals("№")) {
                preape = substring.get(i + 1);
                break;
            }
            if (preape.contains("№")) preape = preape.substring(1);
        }
        return preape;
    }
}
