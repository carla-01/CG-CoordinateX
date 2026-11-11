package lab3.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import lab3.transformacao.Transformacoes;

class PoligonoTest {

    @Test
    void regularFicaCentradoNaOrigemEEquidistante() {
        Poligono p = Poligono.regular(6, 10);
        assertEquals(6, p.getQuantidade());
        assertEquals(0.0, p.centroide().getX(), 1e-9);
        assertEquals(0.0, p.centroide().getY(), 1e-9);
        for (int i = 0; i < 6; i++) {
            assertEquals(10.0, Math.hypot(p.get(i).getX(), p.get(i).getY()), 1e-9);
        }
        // primeiro vértice no topo
        assertEquals(0.0, p.get(0).getX(), 1e-9);
        assertEquals(10.0, p.get(0).getY(), 1e-9);
    }

    @Test
    void irregularPadraoJaEstaNaOrigem() {
        Poligono p = Poligono.irregularPadrao();
        assertEquals(0.0, p.centroide().getX(), 1e-9);
        assertEquals(0.0, p.centroide().getY(), 1e-9);
    }

    @Test
    void centralizarLevaAMediaParaAOrigem() {
        Poligono p = Poligono.deVertices(new double[][] { { 10, 10 }, { 14, 10 }, { 12, 16 } })
                .centralizadoNaOrigem();
        assertEquals(0.0, p.centroide().getX(), 1e-9);
        assertEquals(0.0, p.centroide().getY(), 1e-9);
    }

    @Test
    void transformarNaoAlteraOPoligonoOriginal() {
        Poligono p = Poligono.regular(4, 5);
        Poligono q = p.transformar(Transformacoes.translacao(3, 0));
        assertEquals(0.0, p.centroide().getX(), 1e-9);
        assertEquals(3.0, q.centroide().getX(), 1e-9);
    }

    @Test
    void janelaViraViewportPelaMatriz() {
        // a janela do usuário levada pela matriz JV vira exatamente a viewport
        Poligono janela = Poligono.retangulo(-10, 10, -10, 10);
        Poligono q = janela.transformar(Transformacoes.janelaViewport(-10, 10, -10, 10, 5, 35, 2, 27));
        double[][] esperado = { { 5, 2 }, { 35, 2 }, { 35, 27 }, { 5, 27 } };
        for (int i = 0; i < 4; i++) {
            assertEquals(esperado[i][0], q.get(i).getX(), 1e-9);
            assertEquals(esperado[i][1], q.get(i).getY(), 1e-9);
        }
        assertThrows(IllegalArgumentException.class, () -> Poligono.retangulo(5, 5, 0, 1));
    }

    @Test
    void parametrosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> Poligono.regular(2, 5));
        assertThrows(IllegalArgumentException.class, () -> Poligono.regular(5, 0));
    }
}
