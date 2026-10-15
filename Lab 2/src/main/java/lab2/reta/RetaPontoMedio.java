package lab2.reta;

import core.dispositivo.Raster;

// Algoritmo do Ponto Médio para retas (Bresenham), versão do Foley.
// Usa só inteiros:
//   d inicial = 2dy - dx
//   escolhe E  (d <= 0):  d += incE  = 2dy
//   escolhe NE (d > 0):   d += incNE = 2(dy - dx)
//
// O algoritmo só vale no 1º octante (0 <= m <= 1, x crescendo).
// Para os demais octantes usamos SIMETRIA:
//   1. descobrimos em qual octante está a reta;
//   2. levamos P1 e P2 para o 1º octante (reflexões em x, em y e em y = x);
//   3. rodamos o algoritmo no 1º octante;
//   4. cada pixel calculado é levado de volta ao octante original.
//
// Octantes (como dx e dy se comparam):
//   1: dx >= dy >= 0       5: dx <= dy < 0
//   2: dy >  dx >= 0       6: dy <  dx < 0
//   3: dy > -dx > 0        7: dy < -dx <= 0
//   4: -dx >= dy >= 0      8: dx >= -dy > 0
// Obs.: no dispositivo o y cresce para baixo, então o 1º octante
// aparece na tela "para a direita e para baixo". No plano cartesiano
// (core.dispositivo.PlanoCartesiano, y para cima) os octantes ficam
// onde a matemática diz: o 1º "para a direita e para cima".
public class RetaPontoMedio implements AlgoritmoReta {

    public String getNome() {
        return "Ponto Médio (Bresenham)";
    }

    public void desenhar(Raster raster, int x1, int y1, int x2, int y2, int cor) {
        int octante = descobrirOctante(x2 - x1, y2 - y1);

        // Leva os dois pontos para o 1º octante
        int[] p1 = paraPrimeiroOctante(octante, x1, y1);
        int[] p2 = paraPrimeiroOctante(octante, x2, y2);

        // inc_line dos slides, no 1º octante
        int dx = p2[0] - p1[0];
        int dy = p2[1] - p1[1];
        int d = 2 * dy - dx;         // valor inicial de d
        int incE = 2 * dy;           // incremento de E
        int incNE = 2 * (dy - dx);   // incremento de NE
        int x = p1[0];
        int y = p1[1];

        acender(raster, octante, x, y, cor);
        while (x < p2[0]) {
            if (d <= 0) {
                // Escolhe E
                d = d + incE;
                x = x + 1;
            } else {
                // Escolhe NE
                d = d + incNE;
                x = x + 1;
                y = y + 1;
            }
            acender(raster, octante, x, y, cor);
        }
    }

    // Volta o pixel do 1º octante para o octante original e acende
    private void acender(Raster raster, int octante, int x, int y, int cor) {
        int[] p = doPrimeiroOctante(octante, x, y);
        raster.setPixel(p[0], p[1], cor);
    }

    public static int descobrirOctante(int dx, int dy) {
        if (Math.abs(dx) >= Math.abs(dy)) {
            // reta "deitada": |m| <= 1
            if (dx >= 0 && dy >= 0) return 1;
            if (dx < 0 && dy >= 0) return 4;
            if (dx < 0 && dy < 0) return 5;
            return 8;
        }
        // reta "em pé": |m| > 1
        if (dx >= 0 && dy >= 0) return 2;
        if (dx < 0 && dy >= 0) return 3;
        if (dx < 0 && dy < 0) return 6;
        return 7;
    }

    // true se o deslocamento (dx, dy) está dentro do octante, contando as
    // bordas (eixos e diagonais): levado ao 1º octante, fica dx >= dy >= 0
    public static boolean estaNoOctante(int octante, int dx, int dy) {
        int[] p = paraPrimeiroOctante(octante, dx, dy);
        return p[0] >= p[1] && p[1] >= 0;
    }

    // Reflexões que levam um ponto do octante dado para o 1º octante
    public static int[] paraPrimeiroOctante(int octante, int x, int y) {
        switch (octante) {
            case 1: return new int[] { x, y };
            case 2: return new int[] { y, x };     // reflete em y = x
            case 3: return new int[] { y, -x };
            case 4: return new int[] { -x, y };    // reflete no eixo y
            case 5: return new int[] { -x, -y };
            case 6: return new int[] { -y, -x };
            case 7: return new int[] { -y, x };
            default: return new int[] { x, -y };   // 8: reflete no eixo x
        }
    }

    // Caminho inverso: do 1º octante de volta para o octante dado
    public static int[] doPrimeiroOctante(int octante, int x, int y) {
        switch (octante) {
            case 1: return new int[] { x, y };
            case 2: return new int[] { y, x };
            case 3: return new int[] { -y, x };
            case 4: return new int[] { -x, y };
            case 5: return new int[] { -x, -y };
            case 6: return new int[] { -y, -x };
            case 7: return new int[] { y, -x };
            default: return new int[] { x, -y };
        }
    }
}
