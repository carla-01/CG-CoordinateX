package lab3.recorte;

// Recorte de linha Cohen-Sutherland (aula G5, slides 37 a 46).
//
// A janela [xmin, xmax] x [ymin, ymax] divide o plano em 9 regiões e cada
// ponto recebe um código de 4 bits:
//
//      1001 | 1000 | 1010        bit 1 (8) = acima     (y > ymax)
//     ------+------+------       bit 2 (4) = abaixo    (y < ymin)
//      0001 | 0000 | 0010        bit 3 (2) = direita   (x > xmax)
//     ------+------+------       bit 4 (1) = esquerda  (x < xmin)
//      0101 | 0100 | 0110
//
// Para cada segmento:
//   - os dois códigos 0000           -> aceitação trivial
//   - AND lógico dos códigos != 0000 -> rejeição trivial (os dois do mesmo lado)
//   - senão: escolhe um ponto de fora e uma borda que a linha cruza (bit 1,
//     2, 3, 4, nessa ordem), calcula a interseção, descarta a parte de fora,
//     dá novo código ao ponto e repete o teste.
//
// Interseção (slide 43):
//   borda vertical   x = a:  y = y1 + (a - x1) · (y2 - y1) / (x2 - x1)
//   borda horizontal y = b:  x = x1 + (b - y1) · (x2 - x1) / (y2 - y1)
public final class CohenSutherland {

    public static final int ACIMA = 8;    // bit 1
    public static final int ABAIXO = 4;   // bit 2
    public static final int DIREITA = 2;  // bit 3
    public static final int ESQUERDA = 1; // bit 4

    private final double xmin;
    private final double xmax;
    private final double ymin;
    private final double ymax;

    public CohenSutherland(double xmin, double xmax, double ymin, double ymax) {
        if (xmax <= xmin || ymax <= ymin) {
            throw new IllegalArgumentException("A janela precisa de xmin < xmax e ymin < ymax.");
        }
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }

    public int codigo(double x, double y) {
        int c = 0;
        if (y > ymax) {
            c |= ACIMA;
        } else if (y < ymin) {
            c |= ABAIXO;
        }
        if (x > xmax) {
            c |= DIREITA;
        } else if (x < xmin) {
            c |= ESQUERDA;
        }
        return c;
    }

    // Código com os 4 bits, ex.: 9 -> "1001"
    public static String texto(int codigo) {
        StringBuilder sb = new StringBuilder();
        for (int bit = 3; bit >= 0; bit--) {
            if ((codigo & (1 << bit)) != 0) {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }
        return sb.toString();
    }

    // Devolve { x1, y1, x2, y2 } do pedaço dentro da janela, ou null se o
    // segmento todo ficou de fora
    public double[] recortar(double x1, double y1, double x2, double y2) {
        int c1 = codigo(x1, y1);
        int c2 = codigo(x2, y2);
        while (true) {
            if ((c1 | c2) == 0) {
                return new double[] { x1, y1, x2, y2 }; // aceitação trivial
            }
            if ((c1 & c2) != 0) {
                return null; // rejeição trivial
            }

            // ponto de fora
            int fora;
            if (c1 != 0) {
                fora = c1;
            } else {
                fora = c2;
            }

            double x;
            double y;
            if ((fora & ACIMA) != 0) {
                x = x1 + (ymax - y1) * (x2 - x1) / (y2 - y1);
                y = ymax;
            } else if ((fora & ABAIXO) != 0) {
                x = x1 + (ymin - y1) * (x2 - x1) / (y2 - y1);
                y = ymin;
            } else if ((fora & DIREITA) != 0) {
                y = y1 + (xmax - x1) * (y2 - y1) / (x2 - x1);
                x = xmax;
            } else {
                y = y1 + (xmin - x1) * (y2 - y1) / (x2 - x1);
                x = xmin;
            }

            // descarta a parte de fora e dá novo código ao novo vértice
            if (fora == c1) {
                x1 = x;
                y1 = y;
                c1 = codigo(x1, y1);
            } else {
                x2 = x;
                y2 = y;
                c2 = codigo(x2, y2);
            }
        }
    }
}
