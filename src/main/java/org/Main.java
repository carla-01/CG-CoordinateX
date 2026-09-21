package org;

import org.input.LeitorEntrada;
import org.model.JanelaMundo;
import org.model.Ponto;
import org.model.ResolucaoTela;

public class Main {

    public static void main(String[] args) {

        LeitorEntrada leitor = new LeitorEntrada();

        System.out.println("=== JANELA DO MUNDO ===");

        double xmin = leitor.lerDouble("xmin: ");
        double xmax = leitor.lerDouble("xmax: ");
        double ymin = leitor.lerDouble("ymin: ");
        double ymax = leitor.lerDouble("ymax: ");

        JanelaMundo janela = new JanelaMundo(xmin, xmax, ymin, ymax);

        System.out.println();

        System.out.println("=== RESOLUCAO ===");

        int largura = leitor.lerInt("Largura: ");
        int altura = leitor.lerInt("Altura: ");

        ResolucaoTela tela = new ResolucaoTela(largura, altura);

        System.out.println();

        System.out.println("=== PONTO ===");

        double x = leitor.lerDouble("x: ");
        double y = leitor.lerDouble("y: ");

        Ponto ponto = new Ponto(x, y);

        System.out.println();
        System.out.println("=== DADOS LIDOS ===");

        System.out.println("Janela Mundo:");
        System.out.println("[" + janela.getXmin() + ", " + janela.getXmax() + "] x [" + janela.getYmin() + ", " + janela.getYmax() + "]");

        System.out.println();

        System.out.println("Resolucao:");
        System.out.println(tela.getLargura() + " x " + tela.getAltura());

        System.out.println();

        System.out.println("Ponto:");
        System.out.println(ponto);

        leitor.fechar();
    }
}