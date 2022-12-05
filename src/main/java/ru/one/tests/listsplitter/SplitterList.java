package ru.one.tests.listsplitter;

//разделение списка на несколько частей

import java.util.ArrayList;
import java.util.List;

public class SplitterList {
    public static void main(String[] args) {

        List<String> candleList = new ArrayList<>() {{
            add("FFF");
            add("FFF");
            add("FFF");
            add("FFF");
            add("FFF");
            add("FFF");
            add("FFF");
            add("FFF");
            add("FFF");

//           add("FFF");
        }};

        System.out.println(candleList.size());

        int a = 0;
        int b = 0;
        int c = 0;
        int x = 0;
        int size = candleList.size();

        if ((size % 2) == 0) {
            a = (candleList.size() / 2) - 1;
        } else {
            a = ((candleList.size()-1) / 2);
        }
        System.out.println(x + " " + a + " " + b + " " + c);
        List<String> firstList = candleList.subList(0, a);
        List<String> secondList = candleList.subList(a+1, candleList.size() - 1);


    }
}
