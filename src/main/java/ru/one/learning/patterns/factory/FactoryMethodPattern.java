package ru.one.learning.patterns.factory;

public class FactoryMethodPattern {
    public static void main(String[] args) {
        WatchMaker maker = new DigitalWatchMaker();
        Watch watch = maker.createWatch();
        maker.createWatch().showTime();
        FactoryMethodPattern.getMakerByName("rome").createWatch().showTime();
    }

    public static WatchMaker getMakerByName(String maker){
        if (maker.equals("digital")) return new DigitalWatchMaker();
        else if (maker.equals("rome")) return new RomeWatchMaker();
        throw new RuntimeException("bad name: " + maker);
    }

}

interface Watch {
    void showTime();
}

class RomeWatch implements Watch {
    @Override
    public void showTime() {
        System.out.println("XX-VI");
    }
}

class DigitalWatch implements Watch {
    @Override
    public void showTime() {
        System.out.println("20:06");
    }
}

interface WatchMaker {
    Watch createWatch();
}

class DigitalWatchMaker implements WatchMaker{
    @Override
    public Watch createWatch() {
        return new DigitalWatch();
    }
}

class RomeWatchMaker implements WatchMaker{
    @Override
    public Watch createWatch() {
        return new RomeWatch();
    }
}