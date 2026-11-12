package frontend;

import modelo.Poligono;
import modelo.Ponto;
import rasterizacao.FrameBuffer;
import rasterizacao.RetaPontoMedio;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

// Canvas do MUNDO: plano cartesiano de -250 a 250 em x e em y,
// com a ORIGEM (0, 0) no centro. 1 unidade do mundo = 1 pixel.
// Tudo (eixos, grade, polígono) é desenhado com o algoritmo do ponto médio.
public class PainelMundo extends JPanel {

    public static final int TAMANHO = 501;     // 501 x 501 pixels
    public static final int MEIO = TAMANHO / 2; // pixel da origem = 250

    private static final Color COR_GRADE = new Color(0xE6E6E6);
    private static final Color COR_EIXO = new Color(0x808080);
    private static final Color COR_ORIGINAL = new Color(0xB8B8B8);
    private static final Color COR_POLIGONO = new Color(0x000080);
    private static final Color COR_JANELA = new Color(0xC00000);

    private FrameBuffer frameBuffer = new FrameBuffer(TAMANHO, TAMANHO);

    // Plotador do mundo: converte (x, y) do mundo para (coluna, linha) da tela.
    // A tela tem y para BAIXO, então a linha é MEIO - y.
    private RetaPontoMedio.Plotador plotador = new RetaPontoMedio.Plotador() {
        @Override
        public void plotar(int x, int y, Color cor) {
            frameBuffer.setPixel(x + MEIO, MEIO - y, cor);
        }
    };

    public PainelMundo() {
        setPreferredSize(new Dimension(TAMANHO, TAMANHO));
        setBackground(Color.WHITE);
    }

    // Redesenha tudo do zero no frame buffer
    public void desenhar(Poligono atual, Poligono original, boolean mostrarOriginal,
                         boolean mostrarGrade, double[] janela) {
        frameBuffer.limpar(Color.WHITE);

        if (mostrarGrade) {
            for (int k = -200; k <= 200; k += 50) {
                if (k != 0) {
                    reta(k, -MEIO, k, MEIO, COR_GRADE);
                    reta(-MEIO, k, MEIO, k, COR_GRADE);
                }
            }
        }

        // Eixos x e y
        reta(-MEIO, 0, MEIO, 0, COR_EIXO);
        reta(0, -MEIO, 0, MEIO, COR_EIXO);

        if (mostrarOriginal && original != null) {
            desenharPoligono(original, COR_ORIGINAL);
        }

        // Janela do mundo (quando o mapeamento Window -> Viewport está ativo)
        if (janela != null) {
            int xmin = (int) Math.round(janela[0]);
            int xmax = (int) Math.round(janela[1]);
            int ymin = (int) Math.round(janela[2]);
            int ymax = (int) Math.round(janela[3]);
            reta(xmin, ymin, xmax, ymin, COR_JANELA);
            reta(xmax, ymin, xmax, ymax, COR_JANELA);
            reta(xmax, ymax, xmin, ymax, COR_JANELA);
            reta(xmin, ymax, xmin, ymin, COR_JANELA);
        }

        if (atual != null) {
            desenharPoligono(atual, COR_POLIGONO);
        }

        repaint();
    }

    // Liga V1-V2, V2-V3, ..., Vn-V1 com o ponto médio
    private void desenharPoligono(Poligono poligono, Color cor) {
        List<Ponto> v = poligono.getVertices();
        for (int i = 0; i < v.size(); i++) {
            Ponto a = v.get(i);
            Ponto b = v.get((i + 1) % v.size()); // o último liga no primeiro
            reta((int) Math.round(a.getX()), (int) Math.round(a.getY()),
                    (int) Math.round(b.getX()), (int) Math.round(b.getY()), cor);
        }
    }

    private void reta(int x1, int y1, int x2, int y2, Color cor) {
        RetaPontoMedio.desenhar(x1, y1, x2, y2, cor, plotador);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(frameBuffer.getImagem(), 0, 0, null);

        // Só os rótulos são texto do Swing; as linhas são todas pixel a pixel
        g.setColor(COR_EIXO);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString("x", TAMANHO - 10, MEIO - 4);
        g.drawString("y", MEIO + 4, 10);
        g.drawString("0", MEIO + 3, MEIO + 12);
        for (int k = -200; k <= 200; k += 100) {
            if (k != 0) {
                g.drawString("" + k, MEIO + k - 8, MEIO + 12);
                g.drawString("" + k, MEIO + 3, MEIO - k + 4);
            }
        }
    }
}
