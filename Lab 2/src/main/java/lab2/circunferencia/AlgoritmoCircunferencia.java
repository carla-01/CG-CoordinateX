package lab2.circunferencia;

import core.dispositivo.Raster;

// Um algoritmo de rasterização da circunferência de centro (xc, yc) e raio r.
// As coordenadas são do DISPOSITIVO (inteiras).
//
// Todos calculam só um oitavo da circunferência (de x = 0 até x = y,
// isto é, de 90° a 45°) e usam a simetria de 8 pontos para o resto.
public abstract class AlgoritmoCircunferencia {

    public abstract String getNome();

    public abstract void desenhar(Raster raster, int xc, int yc, int r, int cor);

    // Simetria de 8 pontos (CirclePoints do Foley):
    // (x, y) gera (±x, ±y) e (±y, ±x), deslocados para o centro.
    protected void pontosCirculo(Raster raster, int xc, int yc, int x, int y, int cor) {
        raster.setPixel(xc + x, yc + y, cor);
        raster.setPixel(xc + y, yc + x, cor);
        raster.setPixel(xc + y, yc - x, cor);
        raster.setPixel(xc + x, yc - y, cor);
        raster.setPixel(xc - x, yc - y, cor);
        raster.setPixel(xc - y, yc - x, cor);
        raster.setPixel(xc - y, yc + x, cor);
        raster.setPixel(xc - x, yc + y, cor);
    }
}
