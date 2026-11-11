package lab3.transformacao;

import lab3.modelo.Matriz3;

// Transformação do Gato de Arnold (aula G5, slides 34 a 36).
//
// Aritmética modular: x mod 1 é o único número em [0, 1) que difere de x
// por um inteiro (2,3 mod 1 = 0,3;  -3,7 mod 1 = 0,3). Assim (x, y) mod 1 é
// sempre um ponto do quadrado unitário S = [0, 1) x [0, 1).
//
//   Γ(x, y) = (x + y, x + 2y) mod 1
//
//   M = [1 1; 1 2] = [1 0; 1 1] · [1 1; 0 1]
//     = cisalhamento em y · cisalhamento em x   (o de x atua primeiro)
//
// det M = 1: a área não muda. Por causa do "mod 1" ela NÃO é uma
// transformação afim (não entra na matriz acumulada).
//
// Numa imagem de N x N pixels, o pixel (i, j) é o ponto (i/N, j/N) de S.
// Aplicando Γ e o mod 1, ele vai para ((i + j) mod N, (i + 2j) mod N).
// Como a conta é exata (inteiros), depois de um número finito de passos
// (o período) todos os pixels voltam ao lugar: a figura reaparece.
public final class GatoDeArnold {

    private GatoDeArnold() {
    }

    // x mod 1, sempre em [0, 1)
    public static double mod1(double x) {
        double r = x - Math.floor(x);
        if (r >= 1) {
            r = 0;
        }
        return r;
    }

    // A matriz [1 1; 1 2] em coordenadas homogêneas (sem o mod 1)
    public static Matriz3 matriz() {
        return new Matriz3(new double[][] { { 1, 1, 0 }, { 1, 2, 0 }, { 0, 0, 1 } });
    }

    // Forma fatorada: primeiro o cisalhamento em x, depois o em y
    public static Matriz3 matrizFatorada() {
        return Transformacoes.compor(Transformacoes.cisalhamentoX(1), Transformacoes.cisalhamentoY(1));
    }

    // Γ no quadrado unitário
    public static double[] aplicar(double x, double y) {
        return new double[] { mod1(x + y), mod1(x + 2 * y) };
    }

    // Γ num pixel (i, j) de uma imagem N x N (0 <= i, j < N)
    public static int[] aplicarPixel(int i, int j, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("O lado N deve ser pelo menos 1.");
        }
        return new int[] { mod(i + j, n), mod(i + 2 * j, n) };
    }

    // Menor número de passos que leva TODOS os pixels de volta (M^k = I mod N)
    public static int periodo(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("O lado N deve ser pelo menos 1.");
        }
        if (n == 1) {
            return 1;
        }
        // potência de M mod n, começando em M
        long a = 1, b = 1, c = 1, d = 2;
        int k = 1;
        while (!(a == 1 && b == 0 && c == 0 && d == 1)) {
            long na = (a * 1 + b * 1) % n;
            long nb = (a * 1 + b * 2) % n;
            long nc = (c * 1 + d * 1) % n;
            long nd = (c * 1 + d * 2) % n;
            a = na;
            b = nb;
            c = nc;
            d = nd;
            k++;
        }
        return k;
    }

    // Resto sempre positivo (o % do Java pode dar negativo)
    public static int mod(int v, int n) {
        int r = v % n;
        if (r < 0) {
            r += n;
        }
        return r;
    }
}
