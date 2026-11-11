package lab2.circunferencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import core.dispositivo.Raster;

// Passo θ escolhido pelo usuário ("i = define o passo", slide da aula G4)
class CircunferenciaTrigonometricaTest {

    private static int chamadas(CircunferenciaTrigonometrica c, int r, Set<String> pixels) {
        final int[] n = { 0 };
        c.desenhar(new Raster() {
            public void setPixel(int x, int y, int cor) {
                n[0]++;
                pixels.add(x + "," + y);
            }
        }, 0, 0, r, 0);
        return n[0];
    }

    @Test
    void passoDe45GrausSoCalculaOsDoisExtremos() {
        CircunferenciaTrigonometrica c = new CircunferenciaTrigonometrica();
        c.setPassoGraus(45);
        Set<String> pixels = new HashSet<String>();
        // θ = 0° e θ = 45°, cada um com 8 pontos simétricos
        assertEquals(16, chamadas(c, 10, pixels));
        assertTrue(pixels.contains("10,0"));
        assertTrue(pixels.contains("7,7"));
    }

    @Test
    void passoQueNaoDivide45AindaDesenhaADiagonal() {
        CircunferenciaTrigonometrica c = new CircunferenciaTrigonometrica();
        c.setPassoGraus(20); // 0°, 20°, 40° e o 45° do fim
        Set<String> pixels = new HashSet<String>();
        assertEquals(4 * 8, chamadas(c, 10, pixels));
        assertTrue(pixels.contains("7,7"));
    }

    @Test
    void passoMaiorCalculaMenosPontosQueOAutomatico() {
        CircunferenciaTrigonometrica automatico = new CircunferenciaTrigonometrica();
        CircunferenciaTrigonometrica cinco = new CircunferenciaTrigonometrica();
        cinco.setPassoGraus(5);
        int nAuto = chamadas(automatico, 40, new HashSet<String>());
        int nCinco = chamadas(cinco, 40, new HashSet<String>());
        assertEquals(10 * 8, nCinco); // 0°, 5°, ..., 45°
        assertTrue(nCinco < nAuto);
    }

    @Test
    void passoInvalido() {
        CircunferenciaTrigonometrica c = new CircunferenciaTrigonometrica();
        assertThrows(IllegalArgumentException.class, () -> c.setPassoGraus(-1));
        assertThrows(IllegalArgumentException.class, () -> c.setPassoGraus(46));
    }
}
