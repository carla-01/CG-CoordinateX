package lab3.modelo;

import java.util.Locale;

// Matriz 3 x 3 imutável, usada em coordenadas homogêneas (aula G5).
//
// Convenção dos slides: o ponto é um vetor COLUNA (x, y, 1) e a
// transformação é P' = M · P. Por isso, ao compor transformações,
// a primeira a ser aplicada fica mais à DIREITA do produto.
public final class Matriz3 {

    private static final double ZERO = 1e-9;

    private final double[][] valores;

    public Matriz3(double[][] valores) {
        if (valores == null || valores.length != 3) {
            throw new IllegalArgumentException("A matriz deve ser 3 x 3.");
        }
        this.valores = new double[3][3];
        for (int l = 0; l < 3; l++) {
            if (valores[l] == null || valores[l].length != 3) {
                throw new IllegalArgumentException("A matriz deve ser 3 x 3.");
            }
            for (int c = 0; c < 3; c++) {
                this.valores[l][c] = valores[l][c];
            }
        }
    }

    public static Matriz3 identidade() {
        return new Matriz3(new double[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } });
    }

    public double get(int linha, int coluna) {
        return valores[linha][coluna];
    }

    // this · outra  (a "outra" é aplicada primeiro)
    public Matriz3 multiplicar(Matriz3 outra) {
        double[][] r = new double[3][3];
        for (int l = 0; l < 3; l++) {
            for (int c = 0; c < 3; c++) {
                double soma = 0;
                for (int k = 0; k < 3; k++) {
                    soma += valores[l][k] * outra.valores[k][c];
                }
                r[l][c] = soma;
            }
        }
        return new Matriz3(r);
    }

    // Aplica a matriz ao ponto (x, y, 1) e "homogeneíza" o resultado
    // dividindo por W (slide 15). Nas transformações afins W continua 1.
    public double[] aplicar(double x, double y) {
        double xh = valores[0][0] * x + valores[0][1] * y + valores[0][2];
        double yh = valores[1][0] * x + valores[1][1] * y + valores[1][2];
        double w = valores[2][0] * x + valores[2][1] * y + valores[2][2];
        if (Math.abs(w) < 1e-12) {
            throw new ArithmeticException("W = 0: o ponto foi para o infinito.");
        }
        return new double[] { xh / w, yh / w };
    }

    public double determinante() {
        double[][] m = valores;
        return m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1])
                - m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0])
                + m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0]);
    }

    public boolean igualA(Matriz3 outra, double tolerancia) {
        for (int l = 0; l < 3; l++) {
            for (int c = 0; c < 3; c++) {
                if (Math.abs(valores[l][c] - outra.valores[l][c]) > tolerancia) {
                    return false;
                }
            }
        }
        return true;
    }

    // Número sem ruído de ponto flutuante: 0.0000000001 -> "0", 2.5000 -> "2.5"
    public static String formatar(double v) {
        if (Math.abs(v) < ZERO) {
            return "0";
        }
        String s = String.format(Locale.US, "%.4f", v);
        if (s.indexOf('.') >= 0) {
            int fim = s.length();
            while (s.charAt(fim - 1) == '0') {
                fim--;
            }
            if (s.charAt(fim - 1) == '.') {
                fim--;
            }
            s = s.substring(0, fim);
        }
        if (s.equals("-0")) {
            return "0";
        }
        return s;
    }

    // Três linhas alinhadas por coluna, ex.: "[  0  -1   2 ]"
    public String[] formatarLinhas() {
        String[][] txt = new String[3][3];
        int[] largura = new int[3];
        for (int l = 0; l < 3; l++) {
            for (int c = 0; c < 3; c++) {
                txt[l][c] = formatar(valores[l][c]);
                largura[c] = Math.max(largura[c], txt[l][c].length());
            }
        }
        String[] linhas = new String[3];
        for (int l = 0; l < 3; l++) {
            StringBuilder sb = new StringBuilder("[ ");
            for (int c = 0; c < 3; c++) {
                for (int i = txt[l][c].length(); i < largura[c]; i++) {
                    sb.append(' ');
                }
                sb.append(txt[l][c]).append(' ');
                if (c < 2) {
                    sb.append(' ');
                }
            }
            sb.append(']');
            linhas[l] = sb.toString();
        }
        return linhas;
    }

    @Override
    public String toString() {
        String[] l = formatarLinhas();
        return l[0] + "\n" + l[1] + "\n" + l[2];
    }
}
