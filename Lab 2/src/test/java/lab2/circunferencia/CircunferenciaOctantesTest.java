package lab2.circunferencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import core.dispositivo.PlanoCartesiano;
import core.dispositivo.Raster;
import core.modelo.Pixel;
import lab2.reta.RetaPontoMedio;

// Testa as circunferências com centro em coordenadas positivas e negativas:
// a simetria de 8 pontos tem que preencher os 8 octantes em volta do centro.
class CircunferenciaOctantesTest {

    private static final int[][] CENTROS = { { 0, 0 }, { -7, 4 }, { 5, -6 }, { -12, -9 }, { 20, 15 } };
    private static final int[] RAIOS = { 1, 2, 5, 10, 17 };

    static Stream<Arguments> circunferencias() {
        List<Arguments> casos = new ArrayList<Arguments>();
        for (AlgoritmoCircunferencia algoritmo : algoritmos()) {
            for (int[] centro : CENTROS) {
                for (int r : RAIOS) {
                    casos.add(Arguments.of(algoritmo.getNome(), algoritmo, centro[0], centro[1], r));
                }
            }
        }
        return casos.stream();
    }

    static Stream<Arguments> algoritmosParaTeste() {
        List<Arguments> casos = new ArrayList<Arguments>();
        for (AlgoritmoCircunferencia algoritmo : algoritmos()) {
            casos.add(Arguments.of(algoritmo.getNome(), algoritmo));
        }
        return casos.stream();
    }

    private static AlgoritmoCircunferencia[] algoritmos() {
        return new AlgoritmoCircunferencia[] { new CircunferenciaPolinomial(), new CircunferenciaTrigonometrica(),
                new CircunferenciaPontoMedio() };
    }

    @ParameterizedTest(name = "{0}: centro ({2}, {3}), raio {4}")
    @MethodSource("circunferencias")
    void preencheOs8OctantesEmVoltaDoCentro(String nome, AlgoritmoCircunferencia algoritmo, int xc, int yc,
            int r) {
        Set<String> pixels = new HashSet<String>();
        List<int[]> deslocamentos = new ArrayList<int[]>();
        for (Pixel p : desenhar(algoritmo, xc, yc, r)) {
            if (pixels.add(p.toString())) {
                deslocamentos.add(new int[] { p.getX() - xc, p.getY() - yc });
            }
        }

        // Os 4 pontos sobre os eixos: (xc ± r, yc) e (xc, yc ± r)
        assertTrue(pixels.contains(texto(xc + r, yc)), "falta (xc + r, yc)");
        assertTrue(pixels.contains(texto(xc - r, yc)), "falta (xc - r, yc)");
        assertTrue(pixels.contains(texto(xc, yc + r)), "falta (xc, yc + r)");
        assertTrue(pixels.contains(texto(xc, yc - r)), "falta (xc, yc - r)");

        int[] porOctante = new int[9];
        for (int[] d : deslocamentos) {
            // Cada pixel fica a menos de 1 pixel da circunferência ideal
            double distancia = Math.sqrt(d[0] * d[0] + d[1] * d[1]);
            assertTrue(Math.abs(distancia - r) < 1, "pixel (" + (xc + d[0]) + ", " + (yc + d[1])
                    + ") a " + distancia + " do centro");

            // Simetria de 8 pontos: (±x, ±y) e (±y, ±x) também estão acesos
            int x = d[0];
            int y = d[1];
            int[][] simetricos = { { x, y }, { y, x }, { y, -x }, { x, -y }, { -x, -y }, { -y, -x }, { -y, x },
                    { -x, y } };
            for (int[] s : simetricos) {
                assertTrue(pixels.contains(texto(xc + s[0], yc + s[1])), "falta o simétrico ("
                        + (xc + s[0]) + ", " + (yc + s[1]) + ") de (" + (xc + x) + ", " + (yc + y) + ")");
            }

            for (int octante = 1; octante <= 8; octante++) {
                if (RetaPontoMedio.estaNoOctante(octante, x, y)) {
                    porOctante[octante]++;
                }
            }
        }

        // Todos os octantes recebem o mesmo número de pixels
        for (int octante = 2; octante <= 8; octante++) {
            assertEquals(porOctante[1], porOctante[octante], "pixels no octante " + octante);
        }

        conferirContinuidade(deslocamentos, xc, yc);
    }

    // Centro negativo no plano cartesiano: a circunferência inteira tem que
    // aparecer à esquerda e abaixo da origem na tela
    @ParameterizedTest(name = "{0}")
    @MethodSource("algoritmosParaTeste")
    void centroNegativoApareceNoTerceiroQuadrante(String nome, AlgoritmoCircunferencia algoritmo) {
        PlanoCartesiano plano = new PlanoCartesiano(64, 48);
        Pixel origem = plano.getOrigem();
        final List<Pixel> naTela = new ArrayList<Pixel>();
        algoritmo.desenhar(plano.criarRaster(new Raster() {
            public void setPixel(int x, int y, int cor) {
                naTela.add(new Pixel(x, y));
            }
        }), -12, -9, 5, 0);

        for (Pixel p : naTela) {
            assertTrue(p.getX() < origem.getX(), p + " devia estar à esquerda da origem");
            assertTrue(p.getY() > origem.getY(), p + " devia estar abaixo da origem");
        }
    }

    // Andando em volta do centro, cada pixel encosta no próximo (sem buracos)
    private static void conferirContinuidade(List<int[]> deslocamentos, int xc, int yc) {
        List<int[]> ordenados = new ArrayList<int[]>(deslocamentos);
        Collections.sort(ordenados, new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Double.compare(Math.atan2(a[1], a[0]), Math.atan2(b[1], b[0]));
            }
        });
        for (int i = 0; i < ordenados.size(); i++) {
            int[] a = ordenados.get(i);
            int[] b = ordenados.get((i + 1) % ordenados.size());
            boolean vizinhos = Math.abs(a[0] - b[0]) <= 1 && Math.abs(a[1] - b[1]) <= 1;
            assertTrue(vizinhos, "buraco entre (" + (xc + a[0]) + ", " + (yc + a[1]) + ") e (" + (xc + b[0])
                    + ", " + (yc + b[1]) + ")");
        }
    }

    private static List<Pixel> desenhar(AlgoritmoCircunferencia algoritmo, int xc, int yc, int r) {
        final List<Pixel> pixels = new ArrayList<Pixel>();
        algoritmo.desenhar(new Raster() {
            public void setPixel(int x, int y, int cor) {
                pixels.add(new Pixel(x, y));
            }
        }, xc, yc, r, 0);
        return pixels;
    }

    private static String texto(int x, int y) {
        return new Pixel(x, y).toString();
    }
}
