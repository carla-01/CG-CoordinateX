package frontend.componentes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;

import core.dispositivo.FrameBuffer;
import core.modelo.Pixel;

// A "tela do usuário": mostra um frame buffer como o display do dispositivo.
//
// - zoom 1: cada pixel do frame buffer é UM pixel do monitor (Lab 1, item 4);
// - zoom >= 4: cada pixel vira uma célula da grade, para ver a rasterização;
// - passando o mouse, avisa qual pixel (dcx, dcy) está embaixo dele;
// - clicando, avisa qual pixel foi clicado.
//
// A origem (0, 0) fica no canto superior esquerdo e o y cresce para baixo.
public class PainelRaster extends JPanel {

    // A partir desse zoom a grade e os contornos são desenhados
    public static final int ZOOM_GRADE = 4;

    // Quem usa o painel (a tela de cada lab) recebe os eventos por aqui
    public interface Ouvinte {
        // pixel = null quando o mouse sai da área do frame buffer
        void mouseSobre(Pixel pixel);

        void clique(Pixel pixel);
    }

    private FrameBuffer frameBuffer;
    private int zoom = 1;
    private Pixel sobMouse;
    private Pixel marcador;
    private boolean mostrarEixos;
    private Ouvinte ouvinte;

    private Regua reguaHorizontal;
    private Regua reguaVertical;

    public PainelRaster() {
        setBackground(Estilo.FACE_ESCURA);
        setPreferredSize(new Dimension(400, 300));
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        reguaHorizontal = new Regua(this, true);
        reguaVertical = new Regua(this, false);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mudarPixelSobMouse(pixelEm(e.getX(), e.getY()));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mudarPixelSobMouse(pixelEm(e.getX(), e.getY()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mudarPixelSobMouse(null);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Pixel pixel = pixelEm(e.getX(), e.getY());
                if (pixel != null && ouvinte != null) {
                    ouvinte.clique(pixel);
                }
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    // Coloca o painel numa área com barras de rolagem e réguas
    public JScrollPane criarRolagem() {
        JScrollPane rolagem = new JScrollPane(this);
        rolagem.setBorder(new BevelBorder(BevelBorder.LOWERED));
        rolagem.setColumnHeaderView(reguaHorizontal);
        rolagem.setRowHeaderView(reguaVertical);
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        rolagem.getHorizontalScrollBar().setUnitIncrement(16);
        return rolagem;
    }

    public void setOuvinte(Ouvinte ouvinte) {
        this.ouvinte = ouvinte;
    }

    // null volta para o fundo quadriculado (nada para mostrar)
    public void setFrameBuffer(FrameBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
        marcador = null;
        atualizarTamanho();
    }

    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public int getZoom() {
        return zoom;
    }

    // Muda o zoom mantendo o mesmo pixel no centro da área visível
    public void setZoom(int novoZoom) {
        JViewport viewport = getViewport();
        Rectangle visivel = getVisibleRect();
        double centroX = (visivel.x + visivel.width / 2.0) / zoom;
        double centroY = (visivel.y + visivel.height / 2.0) / zoom;

        zoom = novoZoom;
        atualizarTamanho();

        if (viewport != null) {
            viewport.validate();
            int x = (int) (centroX * zoom - visivel.width / 2.0);
            int y = (int) (centroY * zoom - visivel.height / 2.0);
            viewport.setViewPosition(limitarPosicao(viewport, x, y));
        }
    }

    public void setMostrarEixos(boolean mostrarEixos) {
        this.mostrarEixos = mostrarEixos;
        repaint();
    }

    // Contorno colorido em um pixel (ex.: o P1 escolhido com o mouse)
    public void setMarcador(Pixel marcador) {
        this.marcador = marcador;
        repaint();
    }

    public Pixel getPixelSobMouse() {
        return sobMouse;
    }

    // Rola a tela até o pixel ficar no centro
    public void mostrarPixel(Pixel pixel) {
        JViewport viewport = getViewport();
        if (viewport == null) {
            return;
        }
        Dimension area = viewport.getExtentSize();
        int x = pixel.getX() * zoom + zoom / 2 - area.width / 2;
        int y = pixel.getY() * zoom + zoom / 2 - area.height / 2;
        viewport.setViewPosition(limitarPosicao(viewport, x, y));
    }

    // Chamar depois de desenhar no frame buffer
    public void atualizar() {
        repaint();
        reguaHorizontal.repaint();
        reguaVertical.repaint();
    }

    private void atualizarTamanho() {
        Dimension tamanho;
        if (frameBuffer == null) {
            tamanho = new Dimension(400, 300);
        } else {
            tamanho = new Dimension(frameBuffer.getNdh() * zoom, frameBuffer.getNdv() * zoom);
        }
        setPreferredSize(tamanho);
        reguaHorizontal.setPreferredSize(new Dimension(tamanho.width, Regua.ESPESSURA));
        reguaVertical.setPreferredSize(new Dimension(Regua.LARGURA_VERTICAL, tamanho.height));
        revalidate();
        reguaHorizontal.revalidate();
        reguaVertical.revalidate();
        atualizar();
    }

    // Posição do mouse na tela -> pixel do frame buffer
    private Pixel pixelEm(int xTela, int yTela) {
        if (frameBuffer == null || xTela < 0 || yTela < 0) {
            return null;
        }
        int x = xTela / zoom;
        int y = yTela / zoom;
        if (frameBuffer.contem(x, y) == false) {
            return null;
        }
        return new Pixel(x, y);
    }

    private void mudarPixelSobMouse(Pixel pixel) {
        boolean mudou;
        if (pixel == null || sobMouse == null) {
            mudou = pixel != sobMouse;
        } else {
            mudou = pixel.getX() != sobMouse.getX() || pixel.getY() != sobMouse.getY();
        }
        if (mudou == false) {
            return;
        }
        sobMouse = pixel;
        if (zoom >= ZOOM_GRADE) {
            repaint();
        }
        reguaHorizontal.repaint();
        reguaVertical.repaint();
        if (ouvinte != null) {
            ouvinte.mouseSobre(pixel);
        }
    }

    private JViewport getViewport() {
        if (getParent() instanceof JViewport) {
            return (JViewport) getParent();
        }
        return null;
    }

    private Point limitarPosicao(JViewport viewport, int x, int y) {
        Dimension total = getPreferredSize();
        Dimension area = viewport.getExtentSize();
        x = Math.max(0, Math.min(x, total.width - area.width));
        y = Math.max(0, Math.min(y, total.height - area.height));
        return new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (frameBuffer == null) {
            desenharXadrez(g);
            return;
        }

        int largura = frameBuffer.getNdh() * zoom;
        int altura = frameBuffer.getNdv() * zoom;

        // Ampliação por "vizinho mais próximo": cada pixel vira um quadrado zoom x zoom
        g.drawImage(frameBuffer.getImagem(), 0, 0, largura, altura, null);

        if (zoom >= ZOOM_GRADE) {
            desenharGrade(g, largura, altura);
        }
        if (mostrarEixos) {
            desenharEixos(g, largura, altura);
        }
        if (zoom >= ZOOM_GRADE) {
            contornar(g, marcador, Estilo.MARCADOR);
            contornar(g, sobMouse, Estilo.MOUSE);
        }
    }

    // Eixos de referência passando pelo pixel central (ndh/2, ndv/2).
    // São só desenho por cima: não acendem pixels no frame buffer.
    private void desenharEixos(Graphics g, int largura, int altura) {
        int x = (frameBuffer.getNdh() / 2) * zoom + zoom / 2;
        int y = (frameBuffer.getNdv() / 2) * zoom + zoom / 2;
        g.setColor(Estilo.EIXOS);
        g.drawLine(x, 0, x, altura - 1);
        g.drawLine(0, y, largura - 1, y);
    }

    // Só desenha as linhas que aparecem na área visível (clip)
    private void desenharGrade(Graphics g, int largura, int altura) {
        Rectangle clip = g.getClipBounds();
        int coluna0 = Math.max(0, clip.x / zoom);
        int coluna1 = Math.min(frameBuffer.getNdh(), (clip.x + clip.width) / zoom + 1);
        int linha0 = Math.max(0, clip.y / zoom);
        int linha1 = Math.min(frameBuffer.getNdv(), (clip.y + clip.height) / zoom + 1);

        g.setColor(corGrade());
        for (int c = coluna0; c <= coluna1; c++) {
            g.drawLine(c * zoom, linha0 * zoom, c * zoom, Math.min(altura, linha1 * zoom));
        }
        for (int l = linha0; l <= linha1; l++) {
            g.drawLine(coluna0 * zoom, l * zoom, Math.min(largura, coluna1 * zoom), l * zoom);
        }
    }

    // Grade clara em fundo claro e escura em fundo escuro
    private Color corGrade() {
        Color fundo = new Color(frameBuffer.getCorFundo());
        int brilho = (fundo.getRed() * 299 + fundo.getGreen() * 587 + fundo.getBlue() * 114) / 1000;
        if (brilho > 128) {
            return Estilo.GRADE_FUNDO_CLARO;
        }
        return Estilo.GRADE_FUNDO_ESCURO;
    }

    private void contornar(Graphics g, Pixel pixel, Color cor) {
        if (pixel == null) {
            return;
        }
        g.setColor(cor);
        g.drawRect(pixel.getX() * zoom, pixel.getY() * zoom, zoom, zoom);
    }

    private void desenharXadrez(Graphics g) {
        int tamanhoQuadro = 8;
        for (int y = 0; y < getHeight(); y += tamanhoQuadro) {
            for (int x = 0; x < getWidth(); x += tamanhoQuadro) {
                boolean par = ((x / tamanhoQuadro) + (y / tamanhoQuadro)) % 2 == 0;
                if (par) {
                    g.setColor(Estilo.XADREZ_CLARO);
                } else {
                    g.setColor(Estilo.XADREZ_ESCURO);
                }
                g.fillRect(x, y, tamanhoQuadro, tamanhoQuadro);
            }
        }
    }
}
