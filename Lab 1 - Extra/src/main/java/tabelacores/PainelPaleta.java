package tabelacores;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

// Mostra a grade com as 4096 cores e marca a que está selecionada.
public class PainelPaleta extends JPanel {

    private ControladorVideo controlador;
    private BufferedImage imagem;
    private int tamanhoBloco;
    private int indiceSelecionado;

    public PainelPaleta(ControladorVideo controlador, int tamanho) {
        this.controlador = controlador;
        this.tamanhoBloco = tamanho / 64;
        this.indiceSelecionado = -1;
        setPreferredSize(new Dimension(tamanho, tamanho));
        atualizar();
    }

    // Gera a imagem de novo (usado quando a tabela muda)
    public void atualizar() {
        imagem = controlador.gerarImagem();
        repaint();
    }

    public void setIndiceSelecionado(int indice) {
        indiceSelecionado = indice;
        repaint();
    }

    // Descobre qual índice está na posição clicada. -1 se for fora.
    public int getIndiceNaPosicao(int x, int y) {
        int coluna = x / tamanhoBloco;
        int linha = y / tamanhoBloco;

        if (coluna < 0 || coluna >= 64 || linha < 0 || linha >= 64) {
            return -1;
        }
        return linha * 64 + coluna;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imagem, 0, 0, null);

        // Marca o bloco selecionado com uma moldura branca e preta
        if (indiceSelecionado != -1) {
            int x = (indiceSelecionado % 64) * tamanhoBloco;
            int y = (indiceSelecionado / 64) * tamanhoBloco;

            g.setColor(Color.WHITE);
            g.drawRect(x - 1, y - 1, tamanhoBloco + 1, tamanhoBloco + 1);
            g.setColor(Color.BLACK);
            g.drawRect(x - 2, y - 2, tamanhoBloco + 3, tamanhoBloco + 3);
        }
    }
}