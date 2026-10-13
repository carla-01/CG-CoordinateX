package lab2.circunferencia;

import core.dispositivo.Raster;

// Método trigonométrico: usa a forma paramétrica polar
//   x = r · cos(θ)    y = r · sin(θ)
// θ anda de 0 até π/4 (45°) com passo dθ = 1/r, que dá um
// deslocamento de ~1 pixel ao longo da curva (arco = r · dθ = 1).
// O resto vem da simetria de 8 pontos.
public class CircunferenciaTrigonometrica extends AlgoritmoCircunferencia {

    public String getNome() {
        return "Trigonométrico";
    }

    public void desenhar(Raster raster, int xc, int yc, int r, int cor) {
        if (r == 0) {
            raster.setPixel(xc, yc, cor);
            return;
        }

        double passo = 1.0 / r;
        for (double teta = 0; teta <= Math.PI / 4 + passo; teta += passo) {
            int x = (int) Math.round(r * Math.cos(teta));
            int y = (int) Math.round(r * Math.sin(teta));
            pontosCirculo(raster, xc, yc, x, y, cor);
        }
    }
}
