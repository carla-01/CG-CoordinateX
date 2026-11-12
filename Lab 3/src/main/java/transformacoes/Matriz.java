package transformacoes;

import modelo.Ponto;

import java.util.Locale;

// Matriz 3 x 3 para coordenadas homogêneas (aula G5, slides 12 a 17).
//
//  O ponto (x, y) vira o vetor coluna  | x |
//                                      | y |
//                                      | 1 |
//  e toda transformação vira uma multiplicação:  P' = M . P
public class Matriz {

    private double[][] m;

    public Matriz(double[][] valores) {
        this.m = valores;
    }

    public double get(int linha, int coluna) {
        return m[linha][coluna];
    }

    // Produto de matrizes: this . outra
    // ATENÇÃO (slide 21): não é comutativo, A.B é diferente de B.A
    public Matriz multiplicar(Matriz outra) {
        double[][] r = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double soma = 0;
                for (int k = 0; k < 3; k++) {
                    soma += this.m[i][k] * outra.m[k][j];
                }
                r[i][j] = soma;
            }
        }
        return new Matriz(r);
    }

    // P' = M . P   com P = (x, y, 1)
    public Ponto aplicar(Ponto p) {
        double x = p.getX();
        double y = p.getY();
        double w = 1;

        double xNovo = m[0][0] * x + m[0][1] * y + m[0][2] * w;
        double yNovo = m[1][0] * x + m[1][1] * y + m[1][2] * w;
        double wNovo = m[2][0] * x + m[2][1] * y + m[2][2] * w;

        // Slide 15: divide por W para voltar ao cartesiano (aqui W sempre = 1)
        return new Ponto(xNovo / wNovo, yNovo / wNovo);
    }

    // Mostra a matriz em 3 linhas:  |   1.00   0.00  30.00 |
    public String toString() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            texto.append("    |");
            for (int j = 0; j < 3; j++) {
                double valor = m[i][j];
                if (Math.abs(valor) < 0.005) {
                    valor = 0; // evita aparecer "-0.00"
                }
                texto.append(String.format(Locale.US, "%9.2f", valor));
            }
            texto.append(" |\n");
        }
        return texto.toString();
    }
}
