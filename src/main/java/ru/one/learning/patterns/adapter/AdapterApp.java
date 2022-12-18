package ru.one.learning.patterns.adapter;

public class AdapterApp {
    public static void main(String[] args) {
        VectorGraphics graphics = new VectorAdapterFromRaster();
        graphics.drawLine();
        graphics.drawsSquare();

        System.out.println();

        VectorGraphics graphics2 = new VectorAdapterFromRaster2();
        graphics2.drawLine();
        graphics2.drawsSquare();
    }
}
