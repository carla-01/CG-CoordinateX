package rasterizacao;

import java.awt.Color;
import java.awt.image.BufferedImage;

// Memória de vídeo: uma imagem onde só acendemos pixels, um de cada vez.
// Todo o desenho do polígono passa por setPixel().
public class FrameBuffer {

    private BufferedImage imagem;

    public FrameBuffer(int largura, int altura) {
        imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        limpar(Color.WHITE);
    }

    public void limpar(Color fundo) {
        for (int y = 0; y < getAltura(); y++) {
            for (int x = 0; x < getLargura(); x++) {
                imagem.setRGB(x, y, fundo.getRGB());
            }
        }
    }

    // Acende o pixel (coluna, linha). Fora da tela é ignorado.
    public void setPixel(int coluna, int linha, Color cor) {
        if (coluna >= 0 && coluna < getLargura() && linha >= 0 && linha < getAltura()) {
            imagem.setRGB(coluna, linha, cor.getRGB());
        }
    }

    public int getLargura() {
        return imagem.getWidth();
    }

    public int getAltura() {
        return imagem.getHeight();
    }

    public BufferedImage getImagem() {
        return imagem;
    }
}
