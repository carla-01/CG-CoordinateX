package lab2.circunferencia;

import core.dispositivo.Raster;

// Método trigonométrico (algoritmo do slide):
//   1. θ = 0; i = passo; θend = π/4 (45°)
//   2. se θ > θend, pare
//   3. x = r · cos(θ)    y = r · sen(θ)
//   4. Círculo_ponto(round x, round y, valor)  (simetria de 8 pontos)
//   5. θ = θ + i
//   6. volta ao passo 2
// Passo i: π/4 dividido em n partes iguais, com n = teto(r · π/4).
// Assim i <= 1/r (no máximo ~1 pixel ao longo da curva, arco = r · i)
// e o último θ cai exatamente em θend, sem buraco em 45°.
public class CircunferenciaTrigonometrica extends AlgoritmoCircunferencia {

    public String getNome() {
        return "Trigonométrico";
    }

    public void desenhar(Raster raster, int xc, int yc, int r, int cor) {
        if (r == 0) {
            raster.setPixel(xc, yc, cor);
            return;
        }

        double tetaFinal = Math.PI / 4;
        double passo = tetaFinal / Math.ceil(r * tetaFinal);
        double teta = 0;

        // a pequena folga evita que o erro de arredondamento do double
        // pule o último passo (θ = θend)
        while (teta <= tetaFinal + 1e-9) {
            double x = r * Math.cos(teta);
            double y = r * Math.sin(teta);
            pontosCirculo(raster, xc, yc, (int) Math.round(x), (int) Math.round(y), cor);
            teta = teta + passo;
        }
    }
}
