package lab3.desenho;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import core.dispositivo.Raster;
import lab3.modelo.Poligono;
import lab3.transformacao.Transformacoes;

class DesenhoPoligonoTest {

    private static Set<String> desenhar(Poligono p) {
        final Set<String> pixels = new HashSet<String>();
        new DesenhoPoligono().desenhar(new Raster() {
            public void setPixel(int x, int y, int cor) {
                pixels.add(x + "," + y);
            }
        }, p, 0xFFFFFF);
        return pixels;
    }

    @Test
    void arredondamentoParaLongeDoZero() {
        assertEquals(3, DesenhoPoligono.arredondar(2.5));
        assertEquals(-3, DesenhoPoligono.arredondar(-2.5));
        assertEquals(2, DesenhoPoligono.arredondar(2.4));
        assertEquals(-2, DesenhoPoligono.arredondar(-2.4));
        assertEquals(0, DesenhoPoligono.arredondar(0.49));
    }

    @Test
    void quadradoTemOContornoCompleto() {
        Poligono quadrado = Poligono.deVertices(new double[][] { { -3, -3 }, { 3, -3 }, { 3, 3 }, { -3, 3 } });
        Set<String> pixels = desenhar(quadrado);
        assertEquals(24, pixels.size()); // 4 lados de 7 pixels, cantos compartilhados
        assertTrue(pixels.contains("-3,-3"));
        assertTrue(pixels.contains("3,3"));
        assertTrue(pixels.contains("0,-3"));
        assertTrue(pixels.contains("3,0"));
    }

    @Test
    void osVerticesRefletidosSaoDesenhadosNoLugarCerto() {
        Poligono p = Poligono.irregularPadrao();
        Set<String> espelhado = desenhar(p.transformar(Transformacoes.reflexaoEixoY()));
        for (int i = 0; i < p.getQuantidade(); i++) {
            int x = DesenhoPoligono.arredondar(p.get(i).getX());
            int y = DesenhoPoligono.arredondar(p.get(i).getY());
            assertTrue(espelhado.contains((-x) + "," + y));
        }
    }
}
