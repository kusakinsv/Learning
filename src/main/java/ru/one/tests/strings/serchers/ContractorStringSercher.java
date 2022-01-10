package ru.one.tests.strings.serchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContractorStringSercher {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(){{
            add("ОАО Ромашка");
            add("Ромашка ОАО");
            add("ЗАО Ферма");
            add("ООО \"Рога и Копыта\"");
            add("ООО \"Автозап\"");
            add("\"ЦПИ ООО\"");
            add("УПРАВЛЕНИЕ РОСРЕЕСТРА ПО ПЕРМСКОМУ КРАЮ");
            add("ИФНС РОССИИ ПО СВЕРДЛОВСКОМУ РАЙОНУ Г. ПЕРМИ");
            add("ТЕХБУМ АО");
            add("НПП ХАЛТУРА НПП");
            add("ХАЛТУРА НПО АО");
            add("\"Квартет\" ЗАО");
            add("ООО \"ИТЦ Технополис\"");
            add("Открытое акционерное общество \"Придумай сам\"");
            add("Общество с ограниченной ответственностью \"Придумай сам опять\"");
            add("Акционерное общество \"Придумай сам\"");
            add("Запиливай Акционерное общество");
            add("ИП Зуев Александр Васильнвич");
            add("индивидуальный предприниматель Кыско Александр Васильнвич");
            add("ИП Петров А. В.");
            add("Коаорон");
        }};
        for (String s : list) {
            s = findContractor(s);
            System.out.println(s);
        }
    }

    public static String findContractor(String contractorString){
        String result = contractorString.toLowerCase();
        if (contractorString.contains("ИП ")) {
            result = contractorString.split("ИП ")[1];
            result = result.replaceAll("\\.", "\\s");
            result = result.split(" ")[0];
        } else if (contractorString.toLowerCase().contains("индивидуальный предприниматель ")) {
            result = contractorString.toLowerCase().split("индивидуальный предприниматель ")[1];
            result = result.replaceAll("\\.", "\\s");
            result = result.split(" ")[0];
        } else {
//            List<String> toDelsymbols = Arrays.asList("[ЗО][АО]О", " ОАО", " ПАО", " ООО", " ЗАО", " НПП", "АО ", "ОАО ", "ПАО ", "ООО ", "ЗАО ", "НПП ", "Общество с ограниченной ответственностью", "Акционерное общество", "\"", "\\s");
            List<String> toDelsymbols = Arrays.asList("\\s[ЗОП][АО]О\\b|\\b[ЗОП][АО]О\\s", "\\sАО\\b|\\bАО\\s", "\\НП[ОП]\\b|\\bНП[ОП]\\s", "Общество с ограниченной ответственностью", "\\bАкционерное общество\\s|\\sАкционерное общество\\b", "\"", "\\s");
            for (String toDelsymbol : toDelsymbols) {
            contractorString = contractorString.replaceAll(toDelsymbol, "");
        }
        result = contractorString.toLowerCase();}
        result = result.toLowerCase();
        return result;
    }
}
