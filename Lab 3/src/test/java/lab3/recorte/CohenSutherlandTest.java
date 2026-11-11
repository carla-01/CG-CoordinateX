package lab3.recorte;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

// Janela do exemplo do slide 46: canto inferior esquerdo L(-3, 1) e
// canto superior direito R(2, 6)
class CohenSutherlandTest {

    private final CohenSutherland janela = new CohenSutherland(-3, 2, 1, 6);

    @Test
    void codigosDas9Regioes() {
        assertEquals("1001", CohenSutherland.texto(janela.codigo(-4, 7))); // acima e à esquerda
        assertEquals("1000", CohenSutherland.texto(janela.codigo(0, 7)));
        assertEquals("1010", CohenSutherland.texto(janela.codigo(3, 7)));
        assertEquals("0001", CohenSutherland.texto(janela.codigo(-4, 3)));
        assertEquals("0000", CohenSutherland.texto(janela.codigo(0, 3)));  // dentro
        assertEquals("0010", CohenSutherland.texto(janela.codigo(3, 3)));
        assertEquals("0101", CohenSutherland.texto(janela.codigo(-4, 0)));
        assertEquals("0100", CohenSutherland.texto(janela.codigo(0, 0)));
        assertEquals("0110", CohenSutherland.texto(janela.codigo(3, 0)));
        assertEquals("0000", CohenSutherland.texto(janela.codigo(-3, 6))); // na borda conta como dentro
    }

    @Test
    void aceitacaoTrivial() {
        assertArrayEquals(new double[] { -2, 2, 1, 5 }, janela.recortar(-2, 2, 1, 5), 1e-9);
    }

    @Test
    void rejeicaoTrivialPeloAnd() {
        assertNull(janela.recortar(-5, 7, 4, 8)); // os dois acima: 1001 AND 1010 = 1000
    }

    @Test
    void recortaUmLadoHorizontal() {
        assertArrayEquals(new double[] { -3, 3, 2, 3 }, janela.recortar(-5, 3, 4, 3), 1e-9);
    }

    @Test
    void diagonalPassandoPelosDoisCantos() {
        // y = x + 4: entra em L(-3, 1) e sai em R(2, 6)
        assertArrayEquals(new double[] { -3, 1, 2, 6 }, janela.recortar(-4, 0, 3, 7), 1e-9);
    }

    @Test
    void codigosSemBitEmComumMasALinhaNaoEntra() {
        // 0001 AND 1000 = 0000, mas em x = -3 a reta já está em y = 6,5 > 6
        assertNull(janela.recortar(-4, 5, -2, 8));
    }

    @Test
    void janelaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new CohenSutherland(2, 2, 0, 1));
    }
}
