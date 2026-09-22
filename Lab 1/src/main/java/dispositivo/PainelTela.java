package dispositivo;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

// Painel que desenha o frame buffer na tela.
public class PainelTela extends JPanel {

    private Dispositivo dispositivo;

    public PainelTela(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
        setBackground(Color.BLACK);
    }

    // O Java chama esse método sempre que precisa desenhar o painel
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(dispositivo.getFrameBuffer(), 0, 0, null);
    }
}