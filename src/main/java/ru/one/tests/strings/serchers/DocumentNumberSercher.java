package ru.one.tests.strings.serchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentNumberSercher {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>() {{
            add("Плата за выдачу наличных к чеку № '8442800' от '02/12/2021' согласно договору РКО № 3204 от '03/04/2006'");
            add("По счету № 21-00101161069 от 13.11.2021 за перевозку грузов и транспортно-экспедиционные услуги В т.ч. НДС (20%) 139-33");
            add("Оплата по счету № 706/3 от 22.10.2021 по Дог. № 571 от 27.10.2014 Раб.по конс. тех. сопр.сер.пр-ва РГК насосов ДЦН44С-ДТ за 3кв.2021г. Акт № 109/2021 В т.ч. НДС (20%) 2223-80");
            add("По сч.№678от30.11.2021 г. за никель первичный марки Н1 по Спец.№07от15.09.2021г. к дог.№ 321-03/18 от 16.03.2018г.Курс Долл.США на 02.12.2021=73,9746руб. . В т.ч. НДС (20%) 5547-79");
            add("Счет № 1093 от 14.10.2021; Оказание услуг по сбору, вывозу и утилизации эмульсии;к Договору № 080-2108/2021 от 13.10.2021 Без налога (НДС)");
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
