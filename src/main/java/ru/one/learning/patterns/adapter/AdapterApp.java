package ru.one.learning.patterns.adapter;

public class AdapterApp {
    public static void main(String[] args) {
        VectorGraphics graphics = new VectorAdapterFromRustler();
        graphics.drawLine();
        graphics.drawsSquare();
    }
}
