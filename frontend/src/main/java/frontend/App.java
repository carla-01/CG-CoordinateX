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
                final JanelaPrincipal janela = new JanelaPrincipal();
                janela.adicionarLab(new TelaLab1(janela));
                janela.adicionarLab(new TelaLab1Extra(janela));
                janela.adicionarLab(new TelaLab2(janela));

                // O Lab 3 tem janela própria: abre no lugar desta e, ao
                // fechar, volta para a tela inicial
                janela.adicionarLab("Lab 3 - Transformações Geométricas", new Runnable() {
                    @Override
                    public void run() {
                        janela.setVisible(false);
                        new TelaLab3(new Runnable() {
                            @Override
                            public void run() {
                                janela.voltarAoInicio();
                                janela.setVisible(true);
                            }
                        }).setVisible(true);
                    }
                });
                janela.setVisible(true);
            }
        });
    }
}
