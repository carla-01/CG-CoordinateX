package lab3.transformacao;

import lab3.modelo.Matriz3;

// Todas as matrizes 3 x 3 (coordenadas homogêneas) da aula G5.
// Cada método devolve a matriz M tal que P' = M · P, com P = (x, y, 1)^T.
// O plano é o cartesiano usual: y para cima, ângulos positivos no sentido
// anti-horário.
public final class Transformacoes {

    private Transformacoes() {
    }

    // ---- Transformações básicas (slides 6, 7, 8-11, 17) ----

    // x' = x + tx,  y' = y + ty
    public static Matriz3 translacao(double tx, double ty) {
        return new Matriz3(new double[][] { { 1, 0, tx }, { 0, 1, ty }, { 0, 0, 1 } });
    }

    // x' = sx·x,  y' = sy·y
    public static Matriz3 escala(double sx, double sy) {
        return new Matriz3(new double[][] { { sx, 0, 0 }, { 0, sy, 0 }, { 0, 0, 1 } });
    }

    // Rotação em torno da origem:  x' = x cos θ - y sen θ,  y' = x sen θ + y cos θ
    public static Matriz3 rotacao(double graus) {
        double rad = Math.toRadians(graus);
        double c = limpar(Math.cos(rad));
        double s = limpar(Math.sin(rad));
        return new Matriz3(new double[][] { { c, -s, 0 }, { s, c, 0 }, { 0, 0, 1 } });
    }

    // ---- Reflexões (slides 18 e 19) ----

    // (x, y) -> (x, -y)
    public static Matriz3 reflexaoEixoX() {
        return escala(1, -1);
    }

    // (x, y) -> (-x, y)
    public static Matriz3 reflexaoEixoY() {
        return escala(-1, 1);
    }

    // (x, y) -> (-x, -y)   (o mesmo que girar 180°)
    public static Matriz3 reflexaoOrigem() {
        return escala(-1, -1);
    }

    // (x, y) -> (y, x)   (a "?" do slide 19)
    public static Matriz3 reflexaoRetaYigualX() {
        return new Matriz3(new double[][] { { 0, 1, 0 }, { 1, 0, 0 }, { 0, 0, 1 } });
    }

    // (x, y) -> (-y, -x)
    public static Matriz3 reflexaoRetaYigualMenosX() {
        return new Matriz3(new double[][] { { 0, -1, 0 }, { -1, 0, 0 }, { 0, 0, 1 } });
    }

    // Reflexão numa reta que passa pela origem e faz ângulo θ com o eixo x:
    // R(θ) · Refl_x · R(-θ) = [[cos 2θ, sen 2θ], [sen 2θ, -cos 2θ]]
    public static Matriz3 reflexaoEmReta(double graus) {
        return compor(rotacao(-graus), reflexaoEixoX(), rotacao(graus));
    }

    // ---- Cisalhamento (slide 20) ----

    // x' = x + a·y,  y' = y
    public static Matriz3 cisalhamentoX(double a) {
        return new Matriz3(new double[][] { { 1, a, 0 }, { 0, 1, 0 }, { 0, 0, 1 } });
    }

    // x' = x,  y' = y + b·x
    public static Matriz3 cisalhamentoY(double b) {
        return new Matriz3(new double[][] { { 1, 0, 0 }, { b, 1, 0 }, { 0, 0, 1 } });
    }

    // ---- Transformações compostas (slides 21 a 28) ----

    // Aplica as matrizes NA ORDEM em que são passadas (a primeira é a primeira
    // a atuar). Matematicamente: M = Mn · ... · M2 · M1 (pré-multiplicação).
    public static Matriz3 compor(Matriz3... emOrdemDeAplicacao) {
        Matriz3 total = Matriz3.identidade();
        for (int i = 0; i < emOrdemDeAplicacao.length; i++) {
            total = emOrdemDeAplicacao[i].multiplicar(total);
        }
        return total;
    }

    // Escala com ponto fixo (px, py): T(px,py) · S(sx,sy) · T(-px,-py)
    // 1) leva o ponto fixo para a origem  2) escala  3) traz de volta.
    public static Matriz3 escalaComPontoFixo(double sx, double sy, double px, double py) {
        return compor(translacao(-px, -py), escala(sx, sy), translacao(px, py));
    }

    // Rotação em torno de (px, py): T(px,py) · R(θ) · T(-px,-py)
    public static Matriz3 rotacaoComPontoFixo(double graus, double px, double py) {
        return compor(translacao(-px, -py), rotacao(graus), translacao(px, py));
    }

    // Janela -> porta de visão (slides 29 a 33):
    // M = T(umin,vmin) · S((umax-umin)/(xmax-xmin), (vmax-vmin)/(ymax-ymin)) · T(-xmin,-ymin)
    // 1) leva o canto da janela para a origem  2) ajusta o tamanho  3) leva à posição final.
    public static Matriz3 janelaViewport(double xmin, double xmax, double ymin, double ymax, double umin,
            double umax, double vmin, double vmax) {
        if (xmax == xmin || ymax == ymin) {
            throw new IllegalArgumentException("A janela do mundo não pode ter largura ou altura zero.");
        }
        double sx = (umax - umin) / (xmax - xmin);
        double sy = (vmax - vmin) / (ymax - ymin);
        return compor(translacao(-xmin, -ymin), escala(sx, sy), translacao(umin, vmin));
    }

    // Valores como cos 90° = 6e-17 viram 0 exato
    private static double limpar(double v) {
        if (Math.abs(v) < 1e-12) {
            return 0;
        }
        return v;
    }
}
