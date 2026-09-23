package tabelacores;

import java.awt.image.BufferedImage;

// Faz o papel do controlador de vídeo:
// lê o índice de cada pixel, consulta a tabela e manda a cor pra tela.
public class ControladorVideo {

    private FrameBufferIndexado frameBuffer;
    private TabelaCores tabela;

    public ControladorVideo(FrameBufferIndexado frameBuffer, TabelaCores tabela) {
        this.frameBuffer = frameBuffer;
        this.tabela = tabela;
    }

    public BufferedImage gerarImagem() {
        int ndh = frameBuffer.getNdh();
        int ndv = frameBuffer.getNdv();
        BufferedImage imagem = new BufferedImage(ndh, ndv, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < ndv; y++) {
            for (int x = 0; x < ndh; x++) {
                int indice = frameBuffer.getIndice(x, y); // 12 bits
                int cor = tabela.getCor(indice);          // 24 bits
                imagem.setRGB(x, y, cor);
            }
        }
        return imagem;
    }
}