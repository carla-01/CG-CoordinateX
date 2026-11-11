package lab3.transformacao;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import lab3.modelo.Matriz3;

class GatoDeArnoldTest {

    @Test
    void mod1ComOsExemplosDoSlide34() {
        assertEquals(0.3, GatoDeArnold.mod1(2.3), 1e-9);
        assertEquals(0.9, GatoDeArnold.mod1(0.9), 1e-9);
        assertEquals(0.0, GatoDeArnold.mod1(4), 1e-9);
        assertEquals(0.3, GatoDeArnold.mod1(-3.7), 1e-9);
    }

    @Test
    void matrizEhOProdutoDosDoisCisalhamentos() {
        Matriz3 esperada = new Matriz3(new double[][] { { 1, 1, 0 }, { 1, 2, 0 }, { 0, 0, 1 } });
        assertTrue(GatoDeArnold.matriz().igualA(esperada, 1e-12));
        assertTrue(GatoDeArnold.matrizFatorada().igualA(esperada, 1e-12));
        assertEquals(1.0, GatoDeArnold.matriz().determinante(), 1e-12); // não muda a área
    }

    @Test
    void resultadoFicaNoQuadradoUnitario() {
        double[] p = GatoDeArnold.aplicar(0.7, 0.6); // (1,3 ; 1,9) mod 1
        assertEquals(0.3, p[0], 1e-9);
        assertEquals(0.9, p[1], 1e-9);
    }

    @Test
    void pixelEquivaleAoPontoDoQuadradoUnitario() {
        int n = 10;
        int[] q = GatoDeArnold.aplicarPixel(7, 6, n);
        double[] p = GatoDeArnold.aplicar(7.0 / n, 6.0 / n);
        assertEquals(p[0] * n, q[0], 1e-9);
        assertEquals(p[1] * n, q[1], 1e-9);
        assertArrayEquals(new int[] { 3, 9 }, q);
    }

    @Test
    void periodosConhecidos() {
        assertEquals(3, GatoDeArnold.periodo(2));
        assertEquals(10, GatoDeArnold.periodo(5));
        assertEquals(30, GatoDeArnold.periodo(10));
        assertEquals(5, GatoDeArnold.periodo(11));
    }

    @Test
    void depoisDoPeriodoTodosOsPixelsVoltam() {
        int n = 40;
        int k = GatoDeArnold.periodo(n);
        boolean algumMudouAntes = false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int x = i;
                int y = j;
                for (int passo = 1; passo <= k; passo++) {
                    int[] p = GatoDeArnold.aplicarPixel(x, y, n);
                    x = p[0];
                    y = p[1];
                    if (passo == k - 1 && (x != i || y != j)) {
                        algumMudouAntes = true;
                    }
                }
                assertEquals(i, x);
                assertEquals(j, y);
            }
        }
        assertTrue(algumMudouAntes, "um passo antes do período a figura ainda não voltou");
        assertFalse(k <= 1);
    }
}
