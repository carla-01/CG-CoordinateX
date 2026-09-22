package org.render;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.model.*;

import java.awt.Color;
import java.awt.Graphics;


public class Display extends JPanel {
    //pessoa 3
    private Ponto ponto;
    private ResolucaoTela resolucaoTela;

    public Display(Ponto ponto, ResolucaoTela resolucaoTela) {
        this.ponto = ponto;
        this.resolucaoTela = resolucaoTela;
        setBackground(Color.BLACK);
    }

    public static void exibir(ResolucaoTela resolucao, Ponto ponto) {
        
        JFrame frame = new JFrame("Coordenada de Dispositivo (Pixel)");
        Display painel = new Display(ponto, resolucao);
        painel.setPreferredSize(new java.awt.Dimension(resolucao.getLargura(), resolucao.getAltura()));

        frame.add(painel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // centraliza na tela
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        setBackground(Color.BLACK);
        super.paintComponent(g); // limpa o painel com a cor de fundo

        // --- Desenha o pixel exato (1x1), como resultado "puro" da transformação ---
        g.setColor(Color.green);
        g.fillRect(ponto.getX(), ponto.getY(), 1, 1);

        // --- Marcador maior ao redor, só para facilitar a visualização na tela ---
        /*
        g.setColor(Color.RED);
        g.drawOval(ponto.getX() - 5, ponto.getY() - 5, 10, 10);
        g.drawString("(" + ponto.getX() + ", " + ponto.getY() + ")", ponto.getX() + 10, ponto.getY());
        */
    }
}
