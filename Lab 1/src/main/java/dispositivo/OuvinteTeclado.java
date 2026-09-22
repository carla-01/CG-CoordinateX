package dispositivo;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Fecha o programa quando aperta ESC.
public class OuvinteTeclado extends KeyAdapter {

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }
}