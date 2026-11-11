package lab3.transformacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SequenciaTransformacoesTest {

    @Test
    void escalaComPontoFixoEscritaComoSequencia() {
        SequenciaTransformacoes s = SequenciaTransformacoes.ler("T(-2,-1) E(2,2) T(2,1)");
        assertEquals(3, s.getPassos().size());
        assertTrue(s.getMatrizTotal().igualA(Transformacoes.escalaComPontoFixo(2, 2, 2, 1), 1e-9));
    }

    @Test
    void aceitaMaiusculasMinusculasEEspacos() {
        SequenciaTransformacoes s = SequenciaTransformacoes.ler("  r( 90 )  refx ;  cisx(0.5)");
        assertEquals(3, s.getPassos().size());
    }

    @Test
    void escalaComUmArgumentoEUniforme() {
        assertTrue(SequenciaTransformacoes.ler("E(3)").getMatrizTotal()
                .igualA(Transformacoes.escala(3, 3), 1e-9));
    }

    @Test
    void entradasInvalidasViramErroClaro() {
        assertThrows(IllegalArgumentException.class, () -> SequenciaTransformacoes.ler(""));
        assertThrows(IllegalArgumentException.class, () -> SequenciaTransformacoes.ler("X(1)"));
        assertThrows(IllegalArgumentException.class, () -> SequenciaTransformacoes.ler("T(1)"));
        assertThrows(IllegalArgumentException.class, () -> SequenciaTransformacoes.ler("T(a,b)"));
        assertThrows(IllegalArgumentException.class, () -> SequenciaTransformacoes.ler("T(1,2) @@"));
    }
}
