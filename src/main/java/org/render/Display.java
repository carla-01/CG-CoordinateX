package org.render;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.model.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

// A janela do dispositivo grafico: fundo limpo, sem grade, mostrando o pixel ativo em verde.
public class Display extends JPanel {

    private static final Color COR_PIXEL = new Color(0x00FF00); // verde puro

    private final TipoDC pixel;

    public Display(TipoDC pixel) {
        this.pixel = pixel;
        setBackground(Color.BLACK);
    }

    public static void exibir(ResolucaoTela resolucao, TipoDC pixel) {

        JFrame frame = new JFrame("Coordenada de Dispositivo (Pixel)");
        Display painel = new Display(pixel);
        painel.setPreferredSize(new Dimension(resolucao.getLargura(), resolucao.getAltura()));

        frame.add(painel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // centraliza na tela
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // limpa o painel com a cor de fundo

        DrawPixel.drawPixel(g, (int) pixel.getdcx(), (int) pixel.getdcy(), COR_PIXEL);
    }
}
