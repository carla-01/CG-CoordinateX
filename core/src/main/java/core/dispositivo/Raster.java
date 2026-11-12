package core.dispositivo;

// Raster: um arranjo retangular de pontos (pixels).
// É tudo o que um algoritmo de rasterização precisa: acender um pixel.
// Os algoritmos dos labs desenham em um Raster, e não sabem se ele
// é o frame buffer, a tela cheia ou um registro de pixels.
public interface Raster {

    // setPixel / write_pixel dos slides
    void setPixel(int x, int y, int cor);
}
