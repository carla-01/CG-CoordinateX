package app;

import frontend.TelaLab3;

import javax.swing.SwingUtilities;

// Computação Gráfica - LAB 3
// Transformações geométricas 2D (aula G5) aplicadas a um polígono na origem,
// desenhado com o algoritmo do ponto médio (aula G3).
public class Main {

    public static void main(String[] args) {
        // Igual ao Lab 1: 1 pixel do Java = 1 pixel real (mesmo com Windows em 125%/150%)
        System.setProperty("sun.java2d.uiScale", "1");

        SwingUtilities.invokeLater(() -> new TelaLab3().setVisible(true));
    }
}
