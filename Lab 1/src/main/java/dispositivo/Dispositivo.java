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
    // As fórmulas usam y crescendo para CIMA, mas na tela
    // a linha 0 fica no TOPO. Por isso invertemos o y.
    public void drawPixel(Pixel p, int cor) {
        int coluna = p.getX();
        int linha = (ndv - 1) - p.getY();
        frameBuffer.setRGB(coluna, linha, cor);
    }

    // O clique do mouse vem com y crescendo para BAIXO.
    // Aqui invertemos para ficar igual ao das fórmulas.
    public Pixel pixelDoClique(int xTela, int yTela) {
        int dcx = xTela;
        int dcy = (ndv - 1) - yTela;
        return new Pixel(dcx, dcy);
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