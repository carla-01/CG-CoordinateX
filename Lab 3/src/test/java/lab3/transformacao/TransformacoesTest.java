package lab3.transformacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import lab3.modelo.Matriz3;

class TransformacoesTest {

    private static final double TOL = 1e-9;

    private static void confere(Matriz3 m, double x, double y, double esperadoX, double esperadoY) {
        double[] p = m.aplicar(x, y);
        assertEquals(esperadoX, p[0], TOL);
        assertEquals(esperadoY, p[1], TOL);
    }

    @Test
    void translacaoSomaOsDeslocamentos() {
        confere(Transformacoes.translacao(3, -2), 1, 1, 4, -1);
    }

    @Test
    void escalaMultiplicaAsCoordenadas() {
        confere(Transformacoes.escala(2, 3), 4, 5, 8, 15);
    }

    @Test
    void rotacaoDe90GrausLevaXparaY() {
        confere(Transformacoes.rotacao(90), 1, 0, 0, 1);
        confere(Transformacoes.rotacao(90), 0, 1, -1, 0);
    }

    @Test
    void rotacaoPreservaADistanciaAteAOrigem() {
        double[] p = Transformacoes.rotacao(37).aplicar(3, 4);
        assertEquals(5.0, Math.hypot(p[0], p[1]), TOL);
    }

    @Test
    void reflexoesDosSlides() {
        confere(Transformacoes.reflexaoEixoX(), 2, 3, 2, -3);
        confere(Transformacoes.reflexaoEixoY(), 2, 3, -2, 3);
        confere(Transformacoes.reflexaoOrigem(), 2, 3, -2, -3);
        confere(Transformacoes.reflexaoRetaYigualX(), 2, 3, 3, 2);
        confere(Transformacoes.reflexaoRetaYigualMenosX(), 2, 3, -3, -2);
    }

    @Test
    void reflexaoEmRetaGeralCoincideComOsCasosParticulares() {
        assertTrue(Transformacoes.reflexaoEmReta(0).igualA(Transformacoes.reflexaoEixoX(), TOL));
        assertTrue(Transformacoes.reflexaoEmReta(90).igualA(Transformacoes.reflexaoEixoY(), TOL));
        assertTrue(Transformacoes.reflexaoEmReta(45).igualA(Transformacoes.reflexaoRetaYigualX(), TOL));
        assertTrue(Transformacoes.reflexaoEmReta(135).igualA(Transformacoes.reflexaoRetaYigualMenosX(), TOL));
    }

    @Test
    void reflexaoOrigemEIgualARotacaoDe180() {
        assertTrue(Transformacoes.reflexaoOrigem().igualA(Transformacoes.rotacao(180), TOL));
    }

    @Test
    void cisalhamentos() {
        confere(Transformacoes.cisalhamentoX(0.5), 2, 4, 4, 4);
        confere(Transformacoes.cisalhamentoY(0.5), 2, 4, 2, 5);
        assertEquals(1.0, Transformacoes.cisalhamentoX(3).determinante(), TOL);
    }

    @Test
    void escalaComPontoFixoDoSlide24() {
        // linha (2,1)-(4,1), escala 2, ponto fixo (2,1) -> (2,1)-(6,1)
        Matriz3 m = Transformacoes.escalaComPontoFixo(2, 2, 2, 1);
        confere(m, 2, 1, 2, 1);
        confere(m, 4, 1, 6, 1);
    }

    @Test
    void formasFechadasDasCompostas() {
        double sx = 2;
        double sy = 3;
        double px = 5;
        double py = -4;
        Matriz3 escala = new Matriz3(new double[][] { { sx, 0, px * (1 - sx) }, { 0, sy, py * (1 - sy) },
                { 0, 0, 1 } });
        assertTrue(Transformacoes.escalaComPontoFixo(sx, sy, px, py).igualA(escala, TOL));

        double g = 30;
        double c = Math.cos(Math.toRadians(g));
        double s = Math.sin(Math.toRadians(g));
        Matriz3 rotacao = new Matriz3(new double[][] { { c, -s, px * (1 - c) + py * s },
                { s, c, py * (1 - c) - px * s }, { 0, 0, 1 } });
        assertTrue(Transformacoes.rotacaoComPontoFixo(g, px, py).igualA(rotacao, TOL));
    }

    @Test
    void rotacaoEmTornoDeUmPontoFixoNaoMoveOPonto() {
        confere(Transformacoes.rotacaoComPontoFixo(77, 2, 1), 2, 1, 2, 1);
    }

    @Test
    void composicaoAplicaNaOrdemDada() {
        // primeiro escala por 2, depois translada (1,0): (1,1) -> (2,2) -> (3,2)
        Matriz3 m = Transformacoes.compor(Transformacoes.escala(2, 2), Transformacoes.translacao(1, 0));
        confere(m, 1, 1, 3, 2);
        // ordem inversa: translada e depois escala: (1,1) -> (2,1) -> (4,2)
        Matriz3 n = Transformacoes.compor(Transformacoes.translacao(1, 0), Transformacoes.escala(2, 2));
        confere(n, 1, 1, 4, 2);
    }

    private static boolean comutam(Matriz3 a, Matriz3 b) {
        return a.multiplicar(b).igualA(b.multiplicar(a), TOL);
    }

    @Test
    void tabelaDeComutatividadeDoSlide28() {
        assertTrue(comutam(Transformacoes.translacao(2, 3), Transformacoes.translacao(-1, 4)));
        assertTrue(comutam(Transformacoes.escala(2, 3), Transformacoes.escala(4, 0.5)));
        assertTrue(comutam(Transformacoes.rotacao(30), Transformacoes.rotacao(50)));
        assertTrue(comutam(Transformacoes.escala(2, 2), Transformacoes.rotacao(40)));
    }

    @Test
    void casosQueNaoComutam() {
        assertFalse(comutam(Transformacoes.translacao(2, 3), Transformacoes.rotacao(40)));
        assertFalse(comutam(Transformacoes.translacao(2, 3), Transformacoes.escala(2, 2)));
        assertFalse(comutam(Transformacoes.escala(2, 1), Transformacoes.rotacao(45)));
    }

    @Test
    void janelaViewportMapeiaOsCantos() {
        Matriz3 m = Transformacoes.janelaViewport(0, 10, 0, 5, 100, 300, 50, 150);
        confere(m, 0, 0, 100, 50);
        confere(m, 10, 5, 300, 150);
        confere(m, 5, 2.5, 200, 100);
    }
}
