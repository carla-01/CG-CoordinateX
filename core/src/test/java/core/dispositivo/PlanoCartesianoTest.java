package core.dispositivo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import core.modelo.Pixel;

// Confere que x/y positivos e negativos do plano caem no lado certo da tela.
// Dispositivo 64 x 48: a origem do plano fica no pixel (32, 24).
class PlanoCartesianoTest {

    private final PlanoCartesiano plano = new PlanoCartesiano(64, 48);
    private final Pixel origem = plano.paraDispositivo(0, 0);

    @Test
    void origemNoCentroDaTela() {
        assertEquals(32, origem.getX());
        assertEquals(24, origem.getY());
    }

    @Test
    void xPositivoParaDireitaENegativoParaEsquerda() {
        assertTrue(plano.paraDispositivo(5, 0).getX() > origem.getX());
        assertTrue(plano.paraDispositivo(-5, 0).getX() < origem.getX());
    }

    // No dispositivo o y cresce para baixo: y positivo do plano = dcy menor
    @Test
    void yPositivoParaCimaENegativoParaBaixo() {
        assertTrue(plano.paraDispositivo(0, 5).getY() < origem.getY());
        assertTrue(plano.paraDispositivo(0, -5).getY() > origem.getY());
    }

    @Test
    void quatroQuadrantes() {
        assertPixel(37, 19, plano.paraDispositivo(5, 5));   // 1º: direita, cima
        assertPixel(27, 19, plano.paraDispositivo(-5, 5));  // 2º: esquerda, cima
        assertPixel(27, 29, plano.paraDispositivo(-5, -5)); // 3º: esquerda, baixo
        assertPixel(37, 29, plano.paraDispositivo(5, -5));  // 4º: direita, baixo
    }

    @Test
    void idaEVolta() {
        for (int x = -32; x < 32; x++) {
            for (int y = -23; y <= 24; y++) {
                Pixel volta = plano.paraCartesiano(plano.paraDispositivo(x, y));
                assertPixel(x, y, volta);
            }
        }
    }

    // O plano inteiro cabe no dispositivo: x de -32 a 31 e y de -23 a 24
    @Test
    void cantosDoPlanoSaoOsCantosDaTela() {
        assertPixel(0, 0, plano.paraDispositivo(-32, 24));
        assertPixel(63, 47, plano.paraDispositivo(31, -23));
    }

    @Test
    void rasterAcendeNoPixelConvertido() {
        final int[] aceso = new int[2];
        Raster raster = plano.criarRaster(new Raster() {
            public void setPixel(int x, int y, int cor) {
                aceso[0] = x;
                aceso[1] = y;
            }
        });
        raster.setPixel(-10, 7, 0);
        assertEquals(22, aceso[0]);
        assertEquals(17, aceso[1]);
    }

    private static void assertPixel(int x, int y, Pixel p) {
        assertEquals(x, p.getX(), "x de " + p);
        assertEquals(y, p.getY(), "y de " + p);
    }
}
