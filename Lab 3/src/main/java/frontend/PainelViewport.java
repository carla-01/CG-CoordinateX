package frontend;

import modelo.Poligono;
import modelo.Ponto;
import rasterizacao.FrameBuffer;
import rasterizacao.RetaPontoMedio;
import transformacoes.Matriz;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

// Canvas da VIEWPORT (porta de visão), 300 x 300 pixels.
// Coordenadas (u, v): u para a direita e v para CIMA, origem no canto inferior esquerdo.
// Os pontos do mundo chegam aqui pela matriz M da janela -> viewport (slide 33).
public class PainelViewport extends JPanel {

    public static final int TAMANHO = 300;

    private static final Color COR_EIXO = new Color(0x808080);
    private static final Color COR_POLIGONO = new Color(0x000080);
    private static final Color COR_BORDA = new Color(0xC00000);

    private FrameBuffer frameBuffer = new FrameBuffer(TAMANHO, TAMANHO);

    // Limites da porta de visão; pixel fora deles não é aceso (recorte simples)
    private int umin, umax, vmin, vmax;

    private RetaPontoMedio.Plotador plotador = new RetaPontoMedio.Plotador() {
        @Override
        public void plotar(int u, int v, Color cor) {
            if (u >= umin && u <= umax && v >= vmin && v <= vmax) {
                frameBuffer.setPixel(u, (TAMANHO - 1) - v, cor); // v para cima
            }
        }
    };

    public PainelViewport() {
        setPreferredSize(new Dimension(TAMANHO, TAMANHO));
        setBackground(Color.WHITE);
    }

    public void limpar() {
        frameBuffer.limpar(Color.WHITE);
        repaint();
    }

    // m = matriz janela -> viewport; janela = {xmin, xmax, ymin, ymax}; viewport = {umin, umax, vmin, vmax}
    public void desenhar(Poligono poligono, Matriz m, double[] janela, double[] viewport) {
        frameBuffer.limpar(Color.WHITE);
        umin = (int) Math.round(viewport[0]);
        umax = (int) Math.round(viewport[1]);
        vmin = (int) Math.round(viewport[2]);
        vmax = (int) Math.round(viewport[3]);

        // Borda da porta de visão
        reta(umin, vmin, umax, vmin, COR_BORDA);
        reta(umax, vmin, umax, vmax, COR_BORDA);
        reta(umax, vmax, umin, vmax, COR_BORDA);
        reta(umin, vmax, umin, vmin, COR_BORDA);

        // Eixos do mundo, se passarem pela janela
        if (janela[2] <= 0 && 0 <= janela[3]) {
            retaDoMundo(new Ponto(janela[0], 0), new Ponto(janela[1], 0), m, COR_EIXO);
        }
        if (janela[0] <= 0 && 0 <= janela[1]) {
            retaDoMundo(new Ponto(0, janela[2]), new Ponto(0, janela[3]), m, COR_EIXO);
        }

        // Polígono: cada vértice passa pela matriz M antes de virar pixel
        List<Ponto> v = poligono.getVertices();
        for (int i = 0; i < v.size(); i++) {
            retaDoMundo(v.get(i), v.get((i + 1) % v.size()), m, COR_POLIGONO);
        }
        repaint();
    }

    private void retaDoMundo(Ponto a, Ponto b, Matriz m, Color cor) {
        Ponto ua = m.aplicar(a);
        Ponto ub = m.aplicar(b);
        reta((int) Math.round(ua.getX()), (int) Math.round(ua.getY()),
                (int) Math.round(ub.getX()), (int) Math.round(ub.getY()), cor);
    }

    private void reta(int u1, int v1, int u2, int v2, Color cor) {
        RetaPontoMedio.desenhar(u1, v1, u2, v2, cor, plotador);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(frameBuffer.getImagem(), 0, 0, null);
    }
}
