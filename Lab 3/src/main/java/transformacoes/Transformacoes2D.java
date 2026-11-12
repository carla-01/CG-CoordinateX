package transformacoes;

// Todas as transformações geométricas 2D da aula G5,
// escritas EXATAMENTE como as matrizes dos slides (coordenadas homogêneas).
public class Transformacoes2D {

    // ===== Transformações básicas (slides 6 a 17) =====

    // Translação (slide 17):   x' = x + tx    y' = y + ty
    //          | 1  0  tx |
    //   T  =   | 0  1  ty |
    //          | 0  0  1  |
    public static Matriz translacao(double tx, double ty) {
        return new Matriz(new double[][] {
                { 1, 0, tx },
                { 0, 1, ty },
                { 0, 0, 1 }
        });
    }

    // Escala (slide 17):   x' = sx . x    y' = sy . y
    //          | sx  0   0 |
    //   S  =   | 0   sy  0 |
    //          | 0   0   1 |
    public static Matriz escala(double sx, double sy) {
        return new Matriz(new double[][] {
                { sx, 0, 0 },
                { 0, sy, 0 },
                { 0, 0, 1 }
        });
    }

    // Rotação em torno da origem (slides 8, 9 e 17):
    //   x' = x.cos(θ) - y.sen(θ)
    //   y' = x.sen(θ) + y.cos(θ)
    //          | cos θ  -sen θ  0 |
    //   R  =   | sen θ   cos θ  0 |
    //          |   0       0    1 |
    // θ é digitado em GRAUS e convertido para radianos.
    public static Matriz rotacao(double grausTheta) {
        double theta = Math.toRadians(grausTheta);
        double cos = Math.cos(theta);
        double sen = Math.sin(theta);
        return new Matriz(new double[][] {
                { cos, -sen, 0 },
                { sen, cos, 0 },
                { 0, 0, 1 }
        });
    }

    // ===== Reflexão / espelhamento (slides 18 e 19) =====

    // Em torno do eixo x:  (x, y) -> (x, -y)
    public static Matriz reflexaoEixoX() {
        return new Matriz(new double[][] {
                { 1, 0, 0 },
                { 0, -1, 0 },
                { 0, 0, 1 }
        });
    }

    // Em torno do eixo y:  (x, y) -> (-x, y)
    public static Matriz reflexaoEixoY() {
        return new Matriz(new double[][] {
                { -1, 0, 0 },
                { 0, 1, 0 },
                { 0, 0, 1 }
        });
    }

    // Em torno da origem:  (x, y) -> (-x, -y)
    public static Matriz reflexaoOrigem() {
        return new Matriz(new double[][] {
                { -1, 0, 0 },
                { 0, -1, 0 },
                { 0, 0, 1 }
        });
    }

    // Em torno da reta y = x:  (x, y) -> (y, x)
    // (é a resposta do "?" do slide 19: troca x com y)
    public static Matriz reflexaoRetaYX() {
        return new Matriz(new double[][] {
                { 0, 1, 0 },
                { 1, 0, 0 },
                { 0, 0, 1 }
        });
    }

    // ===== Cisalhamento (slide 20) =====

    // Cisalhamento em x:  x' = x + a.y    y' = y
    //          | 1  a  0 |
    //          | 0  1  0 |
    //          | 0  0  1 |
    public static Matriz cisalhamentoX(double a) {
        return new Matriz(new double[][] {
                { 1, a, 0 },
                { 0, 1, 0 },
                { 0, 0, 1 }
        });
    }

    // Cisalhamento em y:  x' = x    y' = b.x + y
    //          | 1  0  0 |
    //          | b  1  0 |
    //          | 0  0  1 |
    public static Matriz cisalhamentoY(double b) {
        return new Matriz(new double[][] {
                { 1, 0, 0 },
                { b, 1, 0 },
                { 0, 0, 1 }
        });
    }

    // ===== Transformações compostas (slides 21 a 27) =====

    // Escala com ponto fixo (px, py)  (slides 24 e 25):
    //   1) translada o ponto fixo para a origem   T(-px, -py)
    //   2) escala                                 S(sx, sy)
    //   3) translada de volta                     T(px, py)
    //   C = T(px, py) . S(sx, sy) . T(-px, -py)
    public static Matriz escalaPontoFixo(double sx, double sy, double px, double py) {
        return translacao(px, py).multiplicar(escala(sx, sy)).multiplicar(translacao(-px, -py));
    }

    // Rotação em torno de um ponto fixo (x, y)  (slides 26 e 27):
    //   C = T(x, y) . R(θ) . T(-x, -y)
    public static Matriz rotacaoPontoFixo(double grausTheta, double x, double y) {
        return translacao(x, y).multiplicar(rotacao(grausTheta)).multiplicar(translacao(-x, -y));
    }

    // ===== Transformação Janela -> Porta de Visão (slides 29 a 33) =====
    //   M = T(umin, vmin) . S( (umax-umin)/(xmax-xmin) , (vmax-vmin)/(ymax-ymin) ) . T(-xmin, -ymin)
    public static Matriz janelaParaViewport(double xmin, double xmax, double ymin, double ymax,
                                            double umin, double umax, double vmin, double vmax) {
        double sx = (umax - umin) / (xmax - xmin);
        double sy = (vmax - vmin) / (ymax - ymin);
        return translacao(umin, vmin).multiplicar(escala(sx, sy)).multiplicar(translacao(-xmin, -ymin));
    }
}
