package ru.one.learning.patterns.adapter;

public class VectorAdapterFromRustler extends RasterGraphics implements VectorGraphics {

    @Override
    public void drawLine() {
        drawRasterLine();
    }

    @Override
    public void drawsSquare() {
        drawRasterSquare();
    }
}
