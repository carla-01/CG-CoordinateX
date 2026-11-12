package frontend;

import javax.swing.SwingUtilities;

import frontend.labs.TelaLab1;
import frontend.labs.TelaLab1Extra;
import frontend.labs.TelaLab2;

public class App {

    public static void main(String[] args) {
        // Evita que a escala do sistema (125%, 150%...) transforme
        System.setProperty("sun.java2d.uiScale", "1");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JanelaPrincipal janela = new JanelaPrincipal();
                janela.adicionarLab(new TelaLab1(janela));
                janela.adicionarLab(new TelaLab1Extra(janela));
                janela.adicionarLab(new TelaLab2(janela));
                janela.setVisible(true);
            }
        });
    }
}
