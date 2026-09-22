package dispositivo;

import javax.swing.JFrame;
import transformacao.Transformacao;

// Abre a janela em tela cheia, simulando o display.
public class TelaDisplay {

    private Dispositivo dispositivo;
    private Transformacao transformacao;

    public TelaDisplay(Dispositivo dispositivo, Transformacao transformacao) {
        this.dispositivo = dispositivo;
        this.transformacao = transformacao;
    }

    public void mostrar() {
        JFrame janela = new JFrame("Computação Gráfica - LAB 1");
        janela.setUndecorated(true); // sem barra de título
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PainelTela painel = new PainelTela(dispositivo);
        painel.addMouseListener(new OuvinteMouse(dispositivo, transformacao));
        janela.addKeyListener(new OuvinteTeclado());

        janela.add(painel);
        dispositivo.getMonitor().setFullScreenWindow(janela);
    }
}