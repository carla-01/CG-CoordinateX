package frontend;

import javax.swing.JFrame;

// O que uma tela de lab pode pedir para a janela principal.
public interface Navegacao {

    void voltarAoInicio();

    // Usada como "dono" das caixas de diálogo
    JFrame getJanela();
}
