package rasterizacao;

import java.awt.Color;

// Algoritmo do PONTO MÉDIO para retas (Bresenham) - aula G3, slides 16 a 28.
//
// No slide 26 (1º octante, 0 <= m <= 1):
//     dx = x2 - x1;   dy = y2 - y1;
//     d = 2*dy - dx;          // valor inicial de d
//     incE  = 2*dy;           // incremento se escolher E
//     incNE = 2*(dy - dx);    // incremento se escolher NE
//     while (x < x2) {
//         if (d <= 0) { d += incE;  x++; }          // E
//         else        { d += incNE; x++; y++; }     // NE
//         write_pixel(x, y);
//     }
//
// Para desenhar os lados de QUALQUER polígono a reta pode ir para qualquer
// direção. Por isso usamos o mesmo algoritmo com duas adaptações:
//   1) passoX / passoY = +1 ou -1  -> a reta pode andar para a esquerda/baixo;
//   2) se |dy| > |dx| (reta "em pé") os papéis de x e y são trocados.
// As fórmulas de d, incE e incNE são as mesmas do slide.
public class RetaPontoMedio {

    // Ponte entre o algoritmo e onde o pixel é aceso
    // (o mundo e a viewport convertem a coordenada para a tela de jeitos diferentes).
    public interface Plotador {
        void plotar(int x, int y, Color cor);
    }

    public static void desenhar(int x1, int y1, int x2, int y2, Color cor, Plotador tela) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int passoX = (x2 >= x1) ? 1 : -1;
        int passoY = (y2 >= y1) ? 1 : -1;

        int x = x1;
        int y = y1;
        tela.plotar(x, y, cor); // primeiro pixel

        if (dx >= dy) {
            // |m| <= 1 : anda sempre em x; y anda só quando escolhe NE
            int d = 2 * dy - dx;
            int incE = 2 * dy;
            int incNE = 2 * (dy - dx);

            while (x != x2) {
                if (d <= 0) {
                    d = d + incE;       // escolhe E
                } else {
                    d = d + incNE;      // escolhe NE
                    y = y + passoY;
                }
                x = x + passoX;
                tela.plotar(x, y, cor);
            }
        } else {
            // |m| > 1 : mesmo algoritmo com x e y trocados
            int d = 2 * dx - dy;
            int incE = 2 * dx;
            int incNE = 2 * (dx - dy);

            while (y != y2) {
                if (d <= 0) {
                    d = d + incE;
                } else {
                    d = d + incNE;
                    x = x + passoX;
                }
                y = y + passoY;
                tela.plotar(x, y, cor);
            }
        }
    }
}
