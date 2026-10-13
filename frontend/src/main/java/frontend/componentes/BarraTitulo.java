package frontend.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

// Barra de título com degradê azul, no lugar da borda do sistema.
// Arrasta a janela e tem os botões minimizar, maximizar e fechar.
public class BarraTitulo extends JPanel {

    private JFrame janela;
    private JLabel titulo;
    private int arrastarX;
    private int arrastarY;
    private Rectangle limitesRestaurados;

    public BarraTitulo(JFrame janela, String texto) {
        super(new BorderLayout());
        this.janela = janela;
        setPreferredSize(new Dimension(10, 24));

        titulo = new JLabel();
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Estilo.TITULO_JANELA);
        setTitulo(texto);
        add(titulo, BorderLayout.WEST);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 3));
        botoes.setOpaque(false);

        JButton minimizar = botaoJanela("_");
        minimizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BarraTitulo.this.janela.setExtendedState(Frame.ICONIFIED);
            }
        });

        JButton restaurar = botaoJanela("□");
        restaurar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alternarMaximizar();
            }
        });

        JButton fechar = botaoJanela("X");
        fechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        botoes.add(minimizar);
        botoes.add(restaurar);
        botoes.add(fechar);
        add(botoes, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                arrastarX = e.getX();
                arrastarY = e.getY();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    alternarMaximizar();
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = BarraTitulo.this.janela.getLocation();
                BarraTitulo.this.janela.setLocation(p.x + e.getX() - arrastarX, p.y + e.getY() - arrastarY);
            }
        });
    }

    public void setTitulo(String texto) {
        titulo.setText("  " + texto);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, Estilo.TITULO_INICIO, getWidth(), 0, Estilo.TITULO_FIM));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private JButton botaoJanela(String texto) {
        JButton botao = new JButton(texto);
        botao.setPreferredSize(new Dimension(18, 16));
        botao.setFont(new Font("SansSerif", Font.BOLD, 10));
        botao.setFocusPainted(false);
        botao.setMargin(new Insets(0, 0, 0, 0));
        botao.setBackground(Estilo.FACE);
        botao.setBorder(new BevelBorder(BevelBorder.RAISED));
        return botao;
    }

    private void alternarMaximizar() {
        if (limitesRestaurados == null) {
            limitesRestaurados = janela.getBounds();
            GraphicsEnvironment ambiente = GraphicsEnvironment.getLocalGraphicsEnvironment();
            janela.setBounds(ambiente.getMaximumWindowBounds());
        } else {
            janela.setBounds(limitesRestaurados);
            limitesRestaurados = null;
        }
        janela.validate();
    }
}
