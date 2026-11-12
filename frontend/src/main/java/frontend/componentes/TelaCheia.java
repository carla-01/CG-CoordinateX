package frontend.componentes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.dispositivo.FrameBuffer;
import core.dispositivo.Monitor;

// Mostra o frame buffer no display inteiro, sem zoom:
// o pixel (x, y) do frame buffer é o pixel (x, y) do monitor.
// ESC fecha e volta para a interface.
public class TelaCheia {

    public static void mostrar(final FrameBuffer frameBuffer) {
        final GraphicsDevice monitor = Monitor.getDispositivo();
        final JFrame janela = new JFrame("Tela cheia");
        janela.setUndecorated(true);
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(frameBuffer.getImagem(), 0, 0, null);
            }
        };
        painel.setBackground(Color.BLACK);
        janela.add(painel);

        janela.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    monitor.setFullScreenWindow(null);
                    janela.dispose();
                }
            }
        });

        monitor.setFullScreenWindow(janela);
        janela.requestFocus();
    }
}
