package core.dispositivo;

import java.awt.image.BufferedImage;

// Frame buffer (memória de imagem): a imagem é guardada como uma
// matriz de pixels com ndh colunas e ndv linhas.
//
// A origem (0, 0) fica no canto superior esquerdo e o y cresce para baixo,
// igual à tela do Java:  0 <= dcx <= ndh - 1  e  0 <= dcy <= ndv - 1
public class FrameBuffer implements Raster {

    private int ndh;
    private int ndv;
    private int corFundo;
    private BufferedImage imagem;

    public FrameBuffer(int ndh, int ndv) {
        this(ndh, ndv, Cores.PRETO);
    }

    public FrameBuffer(int ndh, int ndv, int corFundo) {
        this.ndh = ndh;
        this.ndv = ndv;
        this.corFundo = corFundo;
        imagem = new BufferedImage(ndh, ndv, BufferedImage.TYPE_INT_RGB);
        limpar();
    }

    // Acende UM único pixel. Pixels fora do frame buffer são ignorados,
    // assim uma reta ou circunferência que sai da tela não quebra o programa.
    public void setPixel(int x, int y, int cor) {
        if (contem(x, y)) {
            imagem.setRGB(x, y, cor);
        }
    }

    public int getPixel(int x, int y) {
        return imagem.getRGB(x, y) & 0xFFFFFF;
    }

    public boolean estaAceso(int x, int y) {
        return getPixel(x, y) != corFundo;
    }

    public boolean contem(int x, int y) {
        return x >= 0 && x < ndh && y >= 0 && y < ndv;
    }

    // Pinta todos os pixels com a cor de fundo
    public void limpar() {
        for (int y = 0; y < ndv; y++) {
            for (int x = 0; x < ndh; x++) {
                imagem.setRGB(x, y, corFundo);
            }
        }
    }

    public int getNdh() {
        return ndh;
    }

    public int getNdv() {
        return ndv;
    }

    public int getCorFundo() {
        return corFundo;
    }

    // A imagem que o controlador de vídeo manda para o monitor
    public BufferedImage getImagem() {
        return imagem;
    }
}
