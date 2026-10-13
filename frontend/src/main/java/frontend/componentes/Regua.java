package frontend.componentes;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

import core.dispositivo.FrameBuffer;
import core.modelo.Pixel;

// Régua com as coordenadas do dispositivo, em cima (dcx) e à esquerda (dcy)
// do PainelRaster. A posição do mouse aparece destacada em amarelo.
public class Regua extends JComponent {

    public static final int ESPESSURA = 18;
    public static final int LARGURA_VERTICAL = 36;

    // Distâncias possíveis entre dois números da régua
    private static final int[] PASSOS = { 1, 2, 5, 10, 20, 50, 100, 200, 500, 1000 };

    private PainelRaster painel;
    private boolean horizontal;

    public Regua(PainelRaster painel, boolean horizontal) {
        this.painel = painel;
        this.horizontal = horizontal;
        setFont(Estilo.PEQUENA);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Rectangle clip = g.getClipBounds();
        g.setColor(Estilo.FACE);
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        FrameBuffer frameBuffer = painel.getFrameBuffer();
        if (frameBuffer == null) {
            return;
        }

        int zoom = painel.getZoom();
        int total;
        int inicio;
        int fim;
        if (horizontal) {
            total = frameBuffer.getNdh();
            inicio = clip.x / zoom;
            fim = (clip.x + clip.width) / zoom + 1;
        } else {
            total = frameBuffer.getNdv();
            inicio = clip.y / zoom;
            fim = (clip.y + clip.height) / zoom + 1;
        }
        fim = Math.min(fim, total);

        destacarMouse(g, zoom);

        // Escolhe um passo em que os números não fiquem encavalados
        int espacoMinimo;
        if (horizontal) {
            espacoMinimo = 40;
        } else {
            espacoMinimo = 20;
        }
        int passo = PASSOS[PASSOS.length - 1];
        for (int i = 0; i < PASSOS.length; i++) {
            if (PASSOS[i] * zoom >= espacoMinimo) {
                passo = PASSOS[i];
                break;
            }
        }

        FontMetrics medidas = g.getFontMetrics();
        g.setColor(Color.BLACK);
        inicio = inicio - inicio % passo;
        for (int v = inicio; v < fim; v += passo) {
            int posicao = v * zoom;
            String texto = "" + v;
            if (horizontal) {
                g.drawLine(posicao, ESPESSURA - 6, posicao, ESPESSURA);
                g.drawString(texto, posicao + 2, medidas.getAscent());
            } else {
                g.drawLine(LARGURA_VERTICAL - 6, posicao, LARGURA_VERTICAL, posicao);
                g.drawString(texto, 2, posicao + medidas.getAscent());
            }
        }

        g.setColor(Estilo.FACE_ESCURA);
        if (horizontal) {
            g.drawLine(clip.x, ESPESSURA - 1, clip.x + clip.width, ESPESSURA - 1);
        } else {
            g.drawLine(LARGURA_VERTICAL - 1, clip.y, LARGURA_VERTICAL - 1, clip.y + clip.height);
        }
    }

    private void destacarMouse(Graphics g, int zoom) {
        Pixel mouse = painel.getPixelSobMouse();
        if (mouse == null) {
            return;
        }
        g.setColor(Estilo.REGUA_MOUSE);
        int tamanho = Math.max(zoom, 2);
        if (horizontal) {
            g.fillRect(mouse.getX() * zoom, 0, tamanho, ESPESSURA);
        } else {
            g.fillRect(0, mouse.getY() * zoom, LARGURA_VERTICAL, tamanho);
        }
    }
}
