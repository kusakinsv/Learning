package ru.one.sabina;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentNumberSercher {

    public String findDocument (String findString){
        String stringlowCase = findString.toLowerCase();
        List<String> substring = new ArrayList<>();//Arrays.asList(string.split("\\s*(\\s|,|!|\\.)\\s*"));
        List<String> schet2 = Arrays.asList("дог.", "договор", "договору", "договора");
        List<String> schet = Arrays.asList("cчет", "счету", "счета", "фактуре", "фактура", "фактуры", "сч.ф", "сч.", "с-ф", "счет-договор", "счет-договору", "счет-договора", "дог.", "договор", "договору", "договора");
        List<String> founded = new ArrayList<>();
        for (int i = 0; i < schet.size(); i++) {
            if(stringlowCase.contains(schet.get(i))) {
                founded.add(schet.get(i));
            }
        }
        if (founded.size()==0){
            for (int i = 0; i < schet2.size(); i++) {
                if(stringlowCase.contains(schet2.get(i))) {
                    founded.add(schet2.get(i));
                }
            }
        }
        if (founded.size()==0) return findString;
        String[] splitString = stringlowCase.split(founded.get(0));
        stringlowCase = splitString[1];
        substring = Arrays.asList(stringlowCase.split("\\s*(\\s|,|!|\\.)\\s*"));


        String preape = "";
        int counter = 1;
        for (int i = 0; i < substring.size(); i++) {
            if (substring.get(i).contains("№")) preape = substring.get(i);
            if (preape.equals("№")) {
                preape = substring.get(i+1);
                break;
            }
            if (preape.contains("№")) preape = preape.substring(1);
        }
        return preape;
    }
}
