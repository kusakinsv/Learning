package ru.one.learning.patterns;

public class FactoryMethodPattern {
    public static void main(String[] args) {
        Watch watch = new DigitalWatch();
        watch.showTime();
    }

}
interface Watch{
    void showTime();
}

class RomeWatch implements Watch{
    @Override
    public void showTime() {
        System.out.println("XX-VI");
    }
}

class DigitalWatch implements Watch{
    @Override
    public void showTime() {
        System.out.println("20:06");
    }
}