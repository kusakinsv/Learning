package ru.one.tests.i1.roundprice;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundPrice {
    public static void main(String[] args) {
        double firstPrice = 34.39717;
        double minPriceIncrement = 0.05;

        long time = System.currentTimeMillis();
        System.out.println(time);
        System.out.println(incrementAndRoundPrice(firstPrice, minPriceIncrement));
        long time2 = System.currentTimeMillis();//-time;
        System.out.println(time2);
    }

    private static double incrementAndRoundPrice(double price, double increment){
        double doublepricex = Math.round(price*100);
        double finalprice = increment*(Math.round((doublepricex/100)/increment));
        double scale = Math.pow(10, 2);
        return Math.round(finalprice * scale)/scale;
    }

//    private static double getFinalPrice(double price, double increment){
//        long pricex = Math.round(price*100);
//        double doublepricex = pricex;
//        doublepricex = doublepricex/100;
//        double finalprice = increment*(Math.round(doublepricex/increment));
//        double scale = Math.pow(10, 2);
//        double fprice = Math.round(finalprice * scale)/scale;
//        return fprice;
//    }
}
