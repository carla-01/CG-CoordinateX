package app;

import dispositivo.Dispositivo;
import dispositivo.TelaDisplay;
import modelo.JanelaMundo;
import modelo.Pixel;
import modelo.Ponto;
import transformacao.Transformacao;
import transformacao.TransformacaoNdc01;
import transformacao.TransformacaoNdc11;

// Computação Gráfica - LAB 1
// Transformações: Mundo -> NDC -> Dispositivo
public class Main {

    public static void main(String[] args) {
        // Evita que a escala do sistema (125%, 150%...) transforme
        // 1 pixel do Java em um grupo de pixels reais.
        System.setProperty("sun.java2d.uiScale", "1");

        LeitorConsole leitor = new LeitorConsole();

        // ----- 1. Janela do mundo -----
        System.out.println("=== Janela do mundo ===");
        JanelaMundo janela = lerJanela(leitor);

        while (janela.ehValida() == false) {
            System.out.println("Erro: xmax deve ser maior que xmin e ymax maior que ymin.");
            System.out.println();
            janela = lerJanela(leitor);
        }

        // ----- Ponto no mundo -----
        System.out.println();
        System.out.println("=== Ponto no mundo ===");
        Ponto pontoMundo = lerPonto(leitor);

        while (janela.contem(pontoMundo) == false) {
            System.out.println("Erro: o ponto precisa estar dentro da janela: " + janela);
            pontoMundo = lerPonto(leitor);
        }

        // ----- 2. Cenário do NDC -----
        System.out.println();
        System.out.println("=== Cenário ===");
        int cenario = leitor.lerCenario();

        Dispositivo dispositivo = new Dispositivo();

        Transformacao transformacao;
        if (cenario == 1) {
            transformacao = new TransformacaoNdc01(janela, dispositivo);
        } else {
            transformacao = new TransformacaoNdc11(janela, dispositivo);
        }

        // ----- Transformações -----
        Ponto ndc = transformacao.userToNdc(pontoMundo);
        Pixel pixel = transformacao.ndcToDc(ndc);

        System.out.println();
        System.out.println("=== Resultado (" + transformacao.getNome() + ") ===");
        System.out.println("Resolução:   " + dispositivo.getNdh() + " x " + dispositivo.getNdv());
        System.out.println("Mundo:       " + pontoMundo);
        System.out.println("NDC:         " + ndc);
        System.out.println("Dispositivo: " + pixel);
        System.out.println();
        System.out.println("Clique na tela para o caminho inverso. ESC para sair.");

        // ----- 3 e 4. Acende o pixel verde e mostra no display -----
        dispositivo.drawPixel(pixel, Dispositivo.VERDE);

        TelaDisplay tela = new TelaDisplay(dispositivo, transformacao);
        tela.mostrar();
    }

    public static JanelaMundo lerJanela(LeitorConsole leitor) {
        double xmin = leitor.lerNumero("xmin: ");
        double xmax = leitor.lerNumero("xmax: ");
        double ymin = leitor.lerNumero("ymin: ");
        double ymax = leitor.lerNumero("ymax: ");
        return new JanelaMundo(xmin, xmax, ymin, ymax);
    }

    public static Ponto lerPonto(LeitorConsole leitor) {
        double x = leitor.lerNumero("x: ");
        double y = leitor.lerNumero("y: ");
        return new Ponto(x, y);
    }
}