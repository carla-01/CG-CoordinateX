package lab2.reta;

import core.dispositivo.Raster;
import core.modelo.Pixel;

/*
 * Reta pelo algoritmo do Ponto Médio (Bresenham), como no Foley.
 *
 *   E  = (x + 1, y)        NE = (x + 1, y + 1)
 *
 * Para decidir, olhamos o ponto médio entre E e NE e vemos se a reta passa
 * acima ou abaixo dele. Isso é feito com a variável de decisão d, que só
 * usa inteiros:
 *
 *   d inicial = 2dy - dx
 *   d <= 0 -> E,  d = d + 2dy
 *   d >  0 -> NE, d = d + 2(dy - dx)
 *
 * Para os outros 7 octantes usamos simetria: levamos a reta para o
 * 1º octante, rodamos o inc_line lá e devolvemos cada pixel para o
 * octante de origem antes de acender.
 *
 * Divisão dos octantes (comparando dx e dy):
 *   1: dx >= dy >= 0       5: dx <= dy < 0
 *   2: dy >  dx >= 0       6: dy <  dx < 0
 *   3: dy > -dx > 0        7: dy < -dx <= 0
 *   4: -dx >= dy >= 0      8: dx >= -dy > 0
 */
public class RetaPontoMedio implements AlgoritmoReta {

    public String getNome() {
        return "Ponto Médio (Bresenham)";
    }

    public void desenhar(Raster raster, int x1, int y1, int x2, int y2, int cor) {
        int octante = descobrirOctante(x2 - x1, y2 - y1);

        // Transforma P1 e P2 como se a reta estivesse no 1º octante
        Pixel p1 = paraPrimeiroOctante(octante, x1, y1);
        Pixel p2 = paraPrimeiroOctante(octante, x2, y2);

        incLine(raster, octante, p1.getX(), p1.getY(), p2.getX(), p2.getY(), cor);
    }

    // inc_line do slide. Aqui a reta já está no 1º octante.
    private void incLine(Raster raster, int octante, int x1, int y1, int x2, int y2, int color) {
        int dx, dy, incE, incNE, d, x, y;
        dx = x2 - x1;
        dy = y2 - y1;
        d = 2 * dy - dx;        /* Valor inicial de d */
        incE = 2 * dy;          /* Incremento de E */
        incNE = 2 * (dy - dx);  /* Incremento de NE */
        x = x1;
        y = y1;
        writePixel(raster, octante, x, y, color);
        while (x < x2) {
            if (d <= 0) {
                /* Escolhe E */
                d = d + incE;
                x = x + 1;
            } else {
                /* Escolhe NE */
                d = d + incNE;
                x = x + 1;
                y = y + 1;
            } /* end if */
            writePixel(raster, octante, x, y, color);
        } /* end while */
    } /* end inc_line */

    // write_pixel. O pixel foi calculado no 1º octante, então
    // antes de acender ele volta para o octante verdadeiro da reta.
    private void writePixel(Raster raster, int octante, int x, int y, int color) {
        Pixel p = doPrimeiroOctante(octante, x, y);
        raster.setPixel(p.getX(), p.getY(), color);
    }

    // Primeiro vê se a reta é mais "deitada" ou mais "em pé" e depois
    // usa os sinais de dx e dy para saber o lado
    public static int descobrirOctante(int dx, int dy) {
        if (Math.abs(dx) >= Math.abs(dy)) {
            // |m| <= 1
            if (dx >= 0 && dy >= 0) return 1;
            if (dx < 0 && dy >= 0) return 4;
            if (dx < 0 && dy < 0) return 5;
            return 8;
        }
        // |m| > 1
        if (dx >= 0 && dy >= 0) return 2;
        if (dx < 0 && dy >= 0) return 3;
        if (dx < 0 && dy < 0) return 6;
        return 7;
    }

    // Usado no teste dos octantes: diz se o deslocamento (dx, dy) cai
    // dentro do octante, contando os eixos e as diagonais
    public static boolean estaNoOctante(int octante, int dx, int dy) {
        Pixel p = paraPrimeiroOctante(octante, dx, dy);
        return p.getX() >= p.getY() && p.getY() >= 0;
    }

    // Leva um ponto de qualquer octante para o 1º, combinando três
    // operações: trocar x com y (reflexão em y = x), trocar o sinal de x
    // (reflexão no eixo y) e trocar o sinal de y (reflexão no eixo x)
    public static Pixel paraPrimeiroOctante(int octante, int x, int y) {
        switch (octante) {
            case 1: return new Pixel(x, y);
            case 2: return new Pixel(y, x);
            case 3: return new Pixel(y, -x);
            case 4: return new Pixel(-x, y);
            case 5: return new Pixel(-x, -y);
            case 6: return new Pixel(-y, -x);
            case 7: return new Pixel(-y, x);
            default: return new Pixel(x, -y);
        }
    }

    // Faz o caminho de volta. Quase todos os casos são iguais à ida, porque
    // refletir duas vezes volta ao ponto original. Só o 3 e o 7 mudam: neles
    // a transformação é uma rotação de 90°, e a volta gira para o outro lado.
    public static Pixel doPrimeiroOctante(int octante, int x, int y) {
        switch (octante) {
            case 1: return new Pixel(x, y);
            case 2: return new Pixel(y, x);
            case 3: return new Pixel(-y, x);
            case 4: return new Pixel(-x, y);
            case 5: return new Pixel(-x, -y);
            case 6: return new Pixel(-y, -x);
            case 7: return new Pixel(y, -x);
            default: return new Pixel(x, -y);
        }
    }
}
