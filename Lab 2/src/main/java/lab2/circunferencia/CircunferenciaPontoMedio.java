package lab2.circunferencia;

import core.dispositivo.Raster;

// Algoritmo do Ponto Médio para a circunferência (Bresenham / Foley).
// Começa em (0, r) e anda com x de 1 em 1 enquanto y > x (2º octante).
// Função implícita: F(x, y) = x² + y² - r²
//   F(M) < 0: o ponto médio está dentro  -> escolhe E  (x + 1, y)
//   F(M) >= 0: o ponto médio está fora   -> escolhe SE (x + 1, y - 1)
// Variável de decisão só com inteiros:
//   d inicial = 1 - r     (5/4 - r arredondado)
//   E:  d += 2x + 3
//   SE: d += 2(x - y) + 5
public class CircunferenciaPontoMedio extends AlgoritmoCircunferencia {

    public String getNome() {
        return "Ponto Médio (Bresenham)";
    }

    public void desenhar(Raster raster, int xc, int yc, int r, int cor) {
        int x = 0;
        int y = r;
        int d = 1 - r;

        pontosCirculo(raster, xc, yc, x, y, cor);
        while (y > x) {
            if (d < 0) {
                // Escolhe E
                d = d + 2 * x + 3;
            } else {
                // Escolhe SE
                d = d + 2 * (x - y) + 5;
                y = y - 1;
            }
            x = x + 1;
            pontosCirculo(raster, xc, yc, x, y, cor);
        }
    }
}
