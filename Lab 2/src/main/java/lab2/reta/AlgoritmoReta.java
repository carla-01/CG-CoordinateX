package lab2.reta;

import core.dispositivo.Raster;

// Um algoritmo de rasterização da reta entre P1 = (x1, y1) e P2 = (x2, y2).
// As coordenadas são do DISPOSITIVO (inteiras).
public interface AlgoritmoReta {

    String getNome();

    void desenhar(Raster raster, int x1, int y1, int x2, int y2, int cor);
}
