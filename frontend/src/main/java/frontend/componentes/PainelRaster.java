package frontend.componentes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;

import core.dispositivo.FrameBuffer;
import core.dispositivo.PlanoCartesiano;
import core.modelo.Pixel;

// A "tela do usuário": mostra um frame buffer como o display do dispositivo.
//
// - zoom 1: cada pixel do frame buffer é UM pixel do monitor (Lab 1, item 4);
// - zoom >= 4: cada pixel vira uma célula da grade, para ver a rasterização;
// - passando o mouse, avisa qual pixel (dcx, dcy) está embaixo dele;
// - clicando, avisa qual pixel foi clicado.
//
// A origem (0, 0) fica no canto superior esquerdo e o y cresce para baixo.
// Com o plano cartesiano ligado, aparecem os eixos x e y passando pelo
// pixel central, as diagonais y = x e y = -x e o número de cada octante,
// e as réguas passam a mostrar as coordenadas do plano.
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
    private boolean planoCartesiano;
    private boolean mostrarGrade = true;
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

    // Eixos x e y com rótulos, diagonais e octantes; réguas no plano
    public void setPlanoCartesiano(boolean planoCartesiano) {
        this.planoCartesiano = planoCartesiano;
        atualizar();
    }

    // null quando o plano cartesiano está desligado
    public PlanoCartesiano getPlano() {
        if (planoCartesiano == false || frameBuffer == null) {
            return null;
        }
        return new PlanoCartesiano(frameBuffer.getNdh(), frameBuffer.getNdv());
    }

    // false: tela limpa como o display, sem a grade mesmo com zoom
    public void setMostrarGrade(boolean mostrarGrade) {
        this.mostrarGrade = mostrarGrade;
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

        if (mostrarGrade && zoom >= ZOOM_GRADE) {
            desenharGrade(g, largura, altura);
        }
        if (planoCartesiano) {
            desenharPlano(g, largura, altura);
        } else if (mostrarEixos) {
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

    // Plano cartesiano: eixos com rótulos, diagonais tracejadas (limites
    // entre os octantes) e o número de cada octante. Também é só desenho.
    private void desenharPlano(Graphics g, int largura, int altura) {
        int x = (frameBuffer.getNdh() / 2) * zoom + zoom / 2;
        int y = (frameBuffer.getNdv() / 2) * zoom + zoom / 2;
        int tamanho = Math.max(largura, altura);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Estilo.OCTANTES);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
                new float[] { 4, 4 }, 0));
        g2.drawLine(x - tamanho, y + tamanho, x + tamanho, y - tamanho); // y = x
        g2.drawLine(x - tamanho, y - tamanho, x + tamanho, y + tamanho); // y = -x

        // Número de cada octante no meio do seu ângulo (22,5° + k·45°),
        // perto da borda da tela para não ficar em cima do desenho
        g2.setColor(Estilo.OCTANTES_TEXTO);
        g2.setFont(Estilo.VALOR);
        FontMetrics medidas = g2.getFontMetrics();
        int margem = 14;
        for (int octante = 1; octante <= 8; octante++) {
            double angulo = Math.toRadians(22.5 + 45 * (octante - 1));
            double cos = Math.cos(angulo);
            double sen = Math.sin(angulo);
            double raio = Math.min((largura / 2.0 - margem) / Math.abs(cos), (altura / 2.0 - margem) / Math.abs(sen));
            String texto = "" + octante;
            int tx = (int) Math.round(x + raio * cos) - medidas.stringWidth(texto) / 2;
            int ty = (int) Math.round(y - raio * sen) + medidas.getAscent() / 2;
            g2.drawString(texto, tx, ty);
        }
        g2.dispose();

        desenharEixos(g, largura, altura);

        // Setas e rótulos nas pontas positivas dos eixos
        g.setColor(Estilo.EIXOS);
        g.setFont(Estilo.VALOR);
        int[] setaXx = { largura - 1, largura - 8, largura - 8 };
        int[] setaXy = { y, y - 4, y + 4 };
        g.fillPolygon(setaXx, setaXy, 3);
        g.drawString("x", largura - 12, y - 6);
        int[] setaYx = { x, x - 4, x + 4 };
        int[] setaYy = { 0, 7, 7 };
        g.fillPolygon(setaYx, setaYy, 3);
        g.drawString("y", x + 6, 12);
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
