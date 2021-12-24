package ru.one.sabina;

import java.util.Arrays;
import java.util.List;

public class ContractorStringSercher {

    public String findContractor(String contractorString){
        String result = contractorString;
        if (contractorString.contains("ИП ")) {
            result = contractorString.split("ИП ")[1];
            result = result.replaceAll("\\.", "\\s");
            result = result.split(" ")[0];
        } else if (contractorString.toLowerCase().contains("индивидуальный предприниматель")) {
            result = contractorString.toLowerCase().split("индивидуальный предприниматель")[0];
            result = result.replaceAll("\\.", "\\s");
            result = result.split(" ")[0];
        } else {
            List<String> toDelsymbols = Arrays.asList("\\s?АО|", " ОАО", " ПАО", " ООО", " ЗАО", " НПП", "АО ", "ОАО ", "ПАО ", "ООО ", "ЗАО ", "НПП ", "Общество с ограниченной ответственностью", "Акционерное общество", "\"", "\\s");
            for (String toDelsymbol : toDelsymbols) {
            contractorString = contractorString.replaceAll(toDelsymbol, "");
        }
        result = contractorString.toLowerCase();}
        result = result.toLowerCase();
        return result;
    }
}
