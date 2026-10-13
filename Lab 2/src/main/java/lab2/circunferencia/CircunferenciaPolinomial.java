package lab2.circunferencia;

import core.dispositivo.Raster;

// Método polinomial: usa a equação da circunferência
//   x² + y² = r²   =>   y = sqrt(r² - x²)
// x anda de 1 em 1, de 0 até r/√2 (o ponto em que x = y, a 45°).
// Depois disso a inclinação passa de 1 e apareceriam buracos,
// por isso o resto vem da simetria de 8 pontos.
public class CircunferenciaPolinomial extends AlgoritmoCircunferencia {

    public String getNome() {
        return "Polinomial";
    }

    public void desenhar(Raster raster, int xc, int yc, int r, int cor) {
        int xFinal = (int) Math.round(r / Math.sqrt(2));

        for (int x = 0; x <= xFinal; x++) {
            int y = (int) Math.round(Math.sqrt(r * r - x * x));
            pontosCirculo(raster, xc, yc, x, y, cor);
        }
    }
}
