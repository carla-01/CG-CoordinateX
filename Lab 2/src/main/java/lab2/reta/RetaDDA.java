package lab2.reta;

import core.dispositivo.Raster;

// DDA - Digital Differential Analyzer (Método do Declive).
// Método incremental (slide "DDA com laço for"):
//   steps = max(|dx|, |dy|)
//   xInc = dx / steps   e   yInc = dy / steps
//   a cada passo: x += xInc, y += yInc, setPixel(round(x), round(y))
// Como o maior delta anda de 1 em 1, funciona em todos os octantes.
public class RetaDDA implements AlgoritmoReta {

    public String getNome() {
        return "DDA (declive)";
    }

    public void desenhar(Raster raster, int x1, int y1, int x2, int y2, int cor) {
        int dx = x2 - x1;
        int dy = y2 - y1;

        int steps;
        if (Math.abs(dx) > Math.abs(dy)) {
            steps = Math.abs(dx);
        } else {
            steps = Math.abs(dy);
        }

        // Reta de um ponto só (P1 = P2)
        if (steps == 0) {
            raster.setPixel(x1, y1, cor);
            return;
        }

        double xIncrement = (double) dx / steps;
        double yIncrement = (double) dy / steps;
        double x = x1;
        double y = y1;

        raster.setPixel(arredondar(x), arredondar(y), cor);
        for (int k = 0; k < steps; k++) {
            x += xIncrement;
            y += yIncrement;
            raster.setPixel(arredondar(x), arredondar(y), cor);
        }
    }

    // round(a) = piso(a + 0.5); funciona também para números negativos
    private int arredondar(double a) {
        return (int) Math.floor(a + 0.5);
    }
}
