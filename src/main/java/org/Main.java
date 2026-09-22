package org;

import org.input.LeitorEntrada;
import org.model.*;
import org.render.Display;
import org.transformacoes.Transformador;

public class Main {

    public static void main(String[] args) {

        LeitorEntrada leitor = new LeitorEntrada();
        Transformador transformador = new Transformador();

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
        System.out.println("=== CENARIO NDC ===");
        System.out.println("1) [0,1] x [0,1]");
        System.out.println("2) [-1,1] x [-1,1]");

        int opcao = leitor.lerInt("Opcao: ");

        double ndcMin = 0.0;
        double ndcMax = 1.0;

        if (opcao == 2) {
            ndcMin = -1.0;
        }

        System.out.println();
        System.out.println("=== PONTO (JANELA DO MUNDO) ===");

        double x = leitor.lerDouble("x: ");
        double y = leitor.lerDouble("y: ");

        Ponto ponto = new Ponto(x, y);

        leitor.fechar();

        TipoNDC ndc = transformador.userToNDC(janela, ponto, ndcMin, ndcMax);
        TipoDC dc = transformador.ndcToDC(tela, ndc, ndcMin, ndcMax);

        Ponto pontoValidado = transformador.ndcToUser(janela, ndc, ndcMin, ndcMax);
        TipoNDC ndcValidado = transformador.inpToNDC(tela, dc, ndcMin, ndcMax);

        System.out.println();
        System.out.println("=== RESULTADO ===");
        System.out.println("Janela Mundo: [" + xmin + ", " + xmax + "] x [" + ymin + ", " + ymax + "]");
        System.out.println("Resolucao: " + largura + " x " + altura);
        System.out.println("Cenario NDC: [" + ndcMin + ", " + ndcMax + "]");
        System.out.println();
        System.out.println("Ponto (user) ......... " + ponto);
        System.out.println("user_to_ndc -> NDC .... " + ndc);
        System.out.println("ndc_to_dc -> DC ....... " + dc);
        System.out.println();
        System.out.println("Validacao:");
        System.out.println("ndc_to_user -> user ... " + pontoValidado);
        System.out.println("inp_to_ndc -> NDC ..... " + ndcValidado);

        Display.exibir(tela, dc);
    }
}
