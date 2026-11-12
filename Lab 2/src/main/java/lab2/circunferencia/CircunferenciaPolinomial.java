package lab2.circunferencia;

import core.dispositivo.Raster;

// Método polinomial (algoritmo do slide):
//   1. x = 0; i = passo; xend = r/√2
//   2. se x > xend, pare
//   3. y = sqrt(r² - x²)
//   4. Círculo_ponto(round x, round y, valor)  (simetria de 8 pontos)
//   5. x = x + i
//   6. volta ao passo 2
// xend = r/√2 é o ponto em que x = y (45°). Depois disso a inclinação
// passa de 1 e apareceriam buracos, por isso o resto vem da simetria.
public class CircunferenciaPolinomial extends AlgoritmoCircunferencia {

    // i: passo de x (1 pixel)
    private static final double PASSO = 1.0;

    public String getNome() {
        return "Polinomial";
    }

    public void desenhar(Raster raster, int xc, int yc, int r, int cor) {
        if (r < 0) {
            throw new IllegalArgumentException("O raio deve ser maior ou igual a zero.");
        }
        double raioQuadrado = (double) r * r;
        double xFinal = r / Math.sqrt(2);
        double x = 0;

        while (x <= xFinal) {
            double y = Math.sqrt(raioQuadrado - x * x);
            pontosCirculo(raster, xc, yc, (int) Math.round(x), (int) Math.round(y), cor);
            x = x + PASSO;
        }
    }
}
