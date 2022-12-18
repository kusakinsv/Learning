package ru.one.learning.patterns.adapter;

public class VectorAdapterFromRaster extends RasterGraphics implements VectorGraphics {

    @Override
    public void drawLine() {
        drawRasterLine();
    }

    @Override
    public void drawsSquare() {
        drawRasterSquare();
    }
}
