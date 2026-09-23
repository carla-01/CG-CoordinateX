package tabelacores;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Clique na grade seleciona a cor daquele bloco.
public class OuvinteCliquePaleta extends MouseAdapter {

    private TelaCores tela;
    private PainelPaleta paleta;

    public OuvinteCliquePaleta(TelaCores tela, PainelPaleta paleta) {
        this.tela = tela;
        this.paleta = paleta;
    }

    public void mousePressed(MouseEvent e) {
        int indice = paleta.getIndiceNaPosicao(e.getX(), e.getY());
        if (indice != -1) {
            tela.selecionarIndice(indice);
        }
    }
}