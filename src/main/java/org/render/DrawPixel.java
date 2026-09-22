package org.render;

import java.awt.Color;
import java.awt.Graphics;

// Acende um unico pixel na tela usando funcao nativa do AWT, sem representa-lo como um bloco de pixels.
public class DrawPixel {

    public static void drawPixel(Graphics g, int x, int y, Color cor) {
        g.setColor(cor);
        g.fillRect(x, y, 1, 1);
    }
}
