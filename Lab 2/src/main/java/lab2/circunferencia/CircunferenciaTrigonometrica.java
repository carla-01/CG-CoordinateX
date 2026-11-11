package lab2.circunferencia;

import core.dispositivo.Raster;

// Método trigonométrico (algoritmo do slide, aula G4):
//   1. θ = 0; i = passo; θend = π/4 (45°)
//   2. se θ > θend, pare
//   3. x = r · cos(θ)    y = r · sen(θ)
//   4. Círculo_ponto(round x, round y, valor)  (simetria de 8 pontos)
//   5. θ = θ + i
//   6. volta ao passo 2
// Passo i: o slide diz "i = define o passo", então ele é escolhido pelo
// usuário (em graus). Passo 0 = automático: π/4 dividido em n partes
// iguais, com n = teto(r · π/4), isto é, i <= 1/r (~1 pixel ao longo da
// curva, arco = r · i), sem buracos.
// Com um passo grande a circunferência fica com buracos (é a desvantagem do
// método). Se o passo não divide 45° exato, o ponto de 45° é desenhado no
// fim, para não abrir buraco na diagonal.
public class CircunferenciaTrigonometrica extends AlgoritmoCircunferencia {

    private double passoGraus = 0;

    public String getNome() {
        return "Trigonométrico";
    }

    // 0 = automático (~1 pixel)
    public double getPassoGraus() {
        return passoGraus;
    }

    public void setPassoGraus(double passoGraus) {
        if (passoGraus < 0 || passoGraus > 45) {
            throw new IllegalArgumentException("O passo θ deve estar entre 0 e 45 graus (0 = automático).");
        }
        this.passoGraus = passoGraus;
    }

    public void desenhar(Raster raster, int xc, int yc, int r, int cor) {
        if (r == 0) {
            raster.setPixel(xc, yc, cor);
            return;
        }

        double tetaFinal = Math.PI / 4;
        double passo;
        if (passoGraus == 0) {
            passo = tetaFinal / Math.ceil(r * tetaFinal);
        } else {
            passo = Math.toRadians(passoGraus);
        }
        double teta = 0;
        double ultimo = 0;

        // a pequena folga evita que o erro de arredondamento do double
        // pule o último passo (θ = θend)
        while (teta <= tetaFinal + 1e-9) {
            ponto(raster, xc, yc, r, teta, cor);
            ultimo = teta;
            teta = teta + passo;
        }
        if (ultimo < tetaFinal - 1e-9) {
            ponto(raster, xc, yc, r, tetaFinal, cor);
        }
    }

    private void ponto(Raster raster, int xc, int yc, int r, double teta, int cor) {
        double x = r * Math.cos(teta);
        double y = r * Math.sin(teta);
        pontosCirculo(raster, xc, yc, (int) Math.round(x), (int) Math.round(y), cor);
    }
}
