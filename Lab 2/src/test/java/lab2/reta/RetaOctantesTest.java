package lab2.reta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import core.dispositivo.PlanoCartesiano;
import core.dispositivo.Raster;
import core.modelo.Pixel;

// Testa as retas nos 8 octantes, com coordenadas positivas e negativas,
// para os dois algoritmos (DDA e Ponto Médio).
//
// Os octantes são os da matemática (y para cima), como no plano cartesiano:
//   1: dx >= dy >= 0       5: dx <= dy < 0
//   2: dy >  dx >= 0       6: dy <  dx < 0
//   3: dy > -dx > 0        7: dy < -dx <= 0
//   4: -dx >= dy >= 0      8: dx >= -dy > 0
class RetaOctantesTest {

    // P2 - P1 de uma reta bem dentro de cada octante (índice = octante - 1)
    private static final int[][] DELTAS = { { 20, 8 }, { 8, 20 }, { -8, 20 }, { -20, 8 }, { -20, -8 },
            { -8, -20 }, { 8, -20 }, { 20, -8 } };

    // P1 em vários quadrantes: na origem, com x negativo, com y negativo...
    private static final int[][] INICIOS = { { 0, 0 }, { -7, 5 }, { 4, -9 }, { -13, -11 }, { 30, 25 } };

    // Retas sobre os eixos e as diagonais (bordas entre os octantes)
    private static final int[][] BORDAS = { { 10, 0 }, { 7, 7 }, { 0, 10 }, { -7, 7 }, { -10, 0 }, { -7, -7 },
            { 0, -10 }, { 7, -7 } };

    static Stream<Arguments> retasNosOctantes() {
        List<Arguments> casos = new ArrayList<Arguments>();
        for (AlgoritmoReta algoritmo : algoritmos()) {
            for (int[] inicio : INICIOS) {
                for (int i = 0; i < DELTAS.length; i++) {
                    casos.add(Arguments.of(algoritmo.getNome(), algoritmo, i + 1, inicio[0], inicio[1],
                            DELTAS[i][0], DELTAS[i][1]));
                }
            }
        }
        return casos.stream();
    }

    static Stream<Arguments> retasNasBordas() {
        List<Arguments> casos = new ArrayList<Arguments>();
        for (AlgoritmoReta algoritmo : algoritmos()) {
            for (int[] inicio : INICIOS) {
                for (int[] delta : BORDAS) {
                    casos.add(Arguments.of(algoritmo.getNome(), algoritmo, inicio[0], inicio[1], delta[0], delta[1]));
                }
            }
        }
        return casos.stream();
    }

    static Stream<Arguments> algoritmosParaTeste() {
        List<Arguments> casos = new ArrayList<Arguments>();
        for (AlgoritmoReta algoritmo : algoritmos()) {
            casos.add(Arguments.of(algoritmo.getNome(), algoritmo));
        }
        return casos.stream();
    }

    private static AlgoritmoReta[] algoritmos() {
        return new AlgoritmoReta[] { new RetaDDA(), new RetaPontoMedio() };
    }

    @ParameterizedTest(name = "{0}: octante {2}, P1 = ({3}, {4}), P2 - P1 = ({5}, {6})")
    @MethodSource("retasNosOctantes")
    void retaFicaNoOctanteCerto(String nome, AlgoritmoReta algoritmo, int octante, int x1, int y1, int dx,
            int dy) {
        assertEquals(octante, RetaPontoMedio.descobrirOctante(dx, dy), "descobrirOctante");

        List<Pixel> pixels = desenhar(algoritmo, x1, y1, x1 + dx, y1 + dy);
        conferirReta(pixels, x1, y1, dx, dy);

        for (Pixel p : pixels) {
            assertTrue(RetaPontoMedio.estaNoOctante(octante, p.getX() - x1, p.getY() - y1),
                    "pixel " + p + " fora do octante " + octante);
        }
    }

    @ParameterizedTest(name = "{0}: P1 = ({2}, {3}), P2 - P1 = ({4}, {5})")
    @MethodSource("retasNasBordas")
    void retaNosEixosENasDiagonais(String nome, AlgoritmoReta algoritmo, int x1, int y1, int dx, int dy) {
        List<Pixel> pixels = desenhar(algoritmo, x1, y1, x1 + dx, y1 + dy);
        conferirReta(pixels, x1, y1, dx, dy);
    }

    // P1 = P2 (com x negativo): acende só aquele pixel
    @ParameterizedTest(name = "{0}")
    @MethodSource("algoritmosParaTeste")
    void retaDeUmPontoSo(String nome, AlgoritmoReta algoritmo) {
        List<Pixel> pixels = desenhar(algoritmo, -4, 6, -4, 6);
        assertEquals(1, pixels.size());
        assertEquals("(-4, 6)", pixels.get(0).toString());
    }

    // O mesmo desenho visto na tela: no plano cartesiano (y para cima) a
    // reta tem que aparecer no lado certo dos eixos do dispositivo
    @ParameterizedTest(name = "{0}")
    @MethodSource("algoritmosParaTeste")
    void retaNoPlanoApareceNoLadoCertoDaTela(String nome, AlgoritmoReta algoritmo) {
        PlanoCartesiano plano = new PlanoCartesiano(64, 48);
        Pixel origem = plano.getOrigem();

        for (int i = 0; i < DELTAS.length; i++) {
            int octante = i + 1;
            final List<Pixel> naTela = new ArrayList<Pixel>();
            Raster dispositivo = new Raster() {
                public void setPixel(int x, int y, int cor) {
                    naTela.add(new Pixel(x, y));
                }
            };
            algoritmo.desenhar(plano.criarRaster(dispositivo), 0, 0, DELTAS[i][0], DELTAS[i][1], 0);

            boolean direita = octante == 1 || octante == 2 || octante == 7 || octante == 8;
            boolean cima = octante <= 4;
            for (Pixel p : naTela) {
                if (direita) {
                    assertTrue(p.getX() >= origem.getX(), "octante " + octante + ": " + p + " devia estar à direita");
                } else {
                    assertTrue(p.getX() <= origem.getX(), "octante " + octante + ": " + p + " devia estar à esquerda");
                }
                // y do plano para cima = dcy menor no dispositivo
                if (cima) {
                    assertTrue(p.getY() <= origem.getY(), "octante " + octante + ": " + p + " devia estar em cima");
                } else {
                    assertTrue(p.getY() >= origem.getY(), "octante " + octante + ": " + p + " devia estar embaixo");
                }
            }
        }
    }

    // Regras que toda reta rasterizada tem que seguir
    private static void conferirReta(List<Pixel> pixels, int x1, int y1, int dx, int dy) {
        int passos = Math.max(Math.abs(dx), Math.abs(dy));
        assertEquals(passos + 1, pixels.size(), "um pixel por passo do eixo maior");

        Pixel primeiro = pixels.get(0);
        Pixel ultimo = pixels.get(pixels.size() - 1);
        assertEquals("(" + x1 + ", " + y1 + ")", primeiro.toString(), "começa em P1");
        assertEquals("(" + (x1 + dx) + ", " + (y1 + dy) + ")", ultimo.toString(), "termina em P2");

        boolean deitada = Math.abs(dx) >= Math.abs(dy);
        for (int k = 1; k < pixels.size(); k++) {
            int px = pixels.get(k).getX() - pixels.get(k - 1).getX();
            int py = pixels.get(k).getY() - pixels.get(k - 1).getY();
            // O eixo maior anda exatamente 1 no sentido da reta;
            // o menor anda 0 ou 1, nunca para trás
            if (deitada) {
                assertEquals(Integer.signum(dx), px, "passo em x no pixel " + pixels.get(k));
                assertTrue(py == 0 || py == Integer.signum(dy), "passo em y no pixel " + pixels.get(k));
            } else {
                assertEquals(Integer.signum(dy), py, "passo em y no pixel " + pixels.get(k));
                assertTrue(px == 0 || px == Integer.signum(dx), "passo em x no pixel " + pixels.get(k));
            }
        }

        // Cada pixel fica a no máximo meio pixel da reta ideal (no eixo menor)
        for (Pixel p : pixels) {
            double u = p.getX() - x1;
            double v = p.getY() - y1;
            double erro;
            if (deitada) {
                erro = Math.abs(v - u * dy / dx);
            } else {
                erro = Math.abs(u - v * dx / dy);
            }
            assertTrue(erro <= 0.5 + 1e-9, "pixel " + p + " longe da reta ideal: " + erro);
        }
    }

    private static List<Pixel> desenhar(AlgoritmoReta algoritmo, int x1, int y1, int x2, int y2) {
        final List<Pixel> pixels = new ArrayList<Pixel>();
        algoritmo.desenhar(new Raster() {
            public void setPixel(int x, int y, int cor) {
                pixels.add(new Pixel(x, y));
            }
        }, x1, y1, x2, y2, 0);
        return pixels;
    }
}
