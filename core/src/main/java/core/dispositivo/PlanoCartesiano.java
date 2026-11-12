package core.dispositivo;

import core.modelo.Pixel;

// Plano cartesiano desenhado sobre o dispositivo.
//
// A origem (0, 0) fica no pixel central (ndh/2, ndv/2), o x cresce para a
// direita e o y cresce para CIMA (no dispositivo o y cresce para baixo).
// Assim números positivos e negativos caem nos quadrantes/octantes da
// matemática:
//
//            y
//        \ 3 | 2 /
//        4 \ | / 1
//       -----+----- x
//        5 / | \ 8
//        / 6 | 7 \
//
//   x = dcx - ndh/2        dcx = ndh/2 + x
//   y = ndv/2 - dcy        dcy = ndv/2 - y
public class PlanoCartesiano {

    private int origemX;
    private int origemY;

    public PlanoCartesiano(int ndh, int ndv) {
        origemX = ndh / 2;
        origemY = ndv / 2;
    }

    // Pixel do dispositivo onde fica a origem (0, 0)
    public Pixel getOrigem() {
        return new Pixel(origemX, origemY);
    }

    public Pixel paraDispositivo(int x, int y) {
        return new Pixel(origemX + x, origemY - y);
    }

    public Pixel paraCartesiano(Pixel dispositivo) {
        return new Pixel(dispositivo.getX() - origemX, origemY - dispositivo.getY());
    }

    // Raster em coordenadas cartesianas: o algoritmo chama setPixel(x, y)
    // no plano e o pixel é aceso no lugar certo do dispositivo
    public Raster criarRaster(final Raster dispositivo) {
        return new Raster() {
            public void setPixel(int x, int y, int cor) {
                dispositivo.setPixel(origemX + x, origemY - y, cor);
            }
        };
    }
}
