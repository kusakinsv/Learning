package ru.one.learning.patterns.adapter;

public class VectorAdapterFromRaster2 implements VectorGraphics {
    RasterGraphics rasterGraphics = new RasterGraphics();

    public void drawLine() {
        rasterGraphics.drawRasterLine();
    }

    @Override
    public void drawsSquare() {
        rasterGraphics.drawRasterSquare();
    }
}
