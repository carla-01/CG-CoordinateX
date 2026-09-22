package org;

import org.input.LeitorEntrada;
import org.model.*;
import org.render.Display;

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
        


        int x = largura/2;
        int y = altura/2;

        Ponto ponto = new Ponto(x, y);
        System.out.println(ponto);

       
        

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

        Display.exibir(tela, ponto);

        leitor.fechar();
    }
}