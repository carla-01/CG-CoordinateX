package dispositivo;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import modelo.Pixel;

// O dispositivo gráfico: o monitor do computador.
// Guarda a resolução (ndh x ndv) e o frame buffer (memória de imagem).
public class Dispositivo {

    // Verde puro no sistema RGB
    public static final int VERDE = 0x00FF00;

    private GraphicsDevice monitor;
    private int ndh;
    private int ndv;
    private BufferedImage frameBuffer;

    public Dispositivo() {
        GraphicsEnvironment ambiente = GraphicsEnvironment.getLocalGraphicsEnvironment();
        monitor = ambiente.getDefaultScreenDevice();

        // Resolução real da tela
        ndh = monitor.getDisplayMode().getWidth();
        ndv = monitor.getDisplayMode().getHeight();

        // TYPE_INT_RGB já começa todo preto: background limpo
        frameBuffer = new BufferedImage(ndh, ndv, BufferedImage.TYPE_INT_RGB);
    }

    // Acende UM único pixel no frame buffer.
    // A origem (0, 0) fica no canto superior esquerdo,
    // igual à tela do Java, então não precisa inverter nada.
    public void drawPixel(Pixel p, int cor) {
        frameBuffer.setRGB(p.getX(), p.getY(), cor);
    }

    // O clique já vem com a origem no canto superior esquerdo.
    public Pixel pixelDoClique(int xTela, int yTela) {
        return new Pixel(xTela, yTela);
    }

    public int getNdh() {
        return ndh;
    }

    public int getNdv() {
        return ndv;
    }

    public GraphicsDevice getMonitor() {
        return monitor;
    }

    public BufferedImage getFrameBuffer() {
        return frameBuffer;
    }
}