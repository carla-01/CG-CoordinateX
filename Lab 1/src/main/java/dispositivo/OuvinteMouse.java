package dispositivo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import modelo.Pixel;
import modelo.Ponto;
import transformacao.Transformacao;

// Trata o clique do mouse: é a ENTRADA do dispositivo.
// Faz o caminho inverso: dispositivo -> NDC -> mundo
public class OuvinteMouse extends MouseAdapter {

    private Dispositivo dispositivo;
    private Transformacao transformacao;

    public OuvinteMouse(Dispositivo dispositivo, Transformacao transformacao) {
        this.dispositivo = dispositivo;
        this.transformacao = transformacao;
    }

    public void mouseClicked(MouseEvent e) {
        Pixel entrada = dispositivo.pixelDoClique(e.getX(), e.getY());
        Ponto ndc = transformacao.inpToNdc(entrada);
        Ponto mundo = transformacao.ndcToUser(ndc);

        System.out.println();
        System.out.println("--- Clique ---");
        System.out.println("Dispositivo: " + entrada);
        System.out.println("NDC:         " + ndc);
        System.out.println("Mundo:       " + mundo);
    }
}