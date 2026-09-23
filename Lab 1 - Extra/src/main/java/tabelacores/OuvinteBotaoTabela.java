package tabelacores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Botões que trocam a tabela: 1 = colorida, 2 = cinza, 3 = fogo
public class OuvinteBotaoTabela implements ActionListener {

    private TelaCores tela;
    private int tipo;

    public OuvinteBotaoTabela(TelaCores tela, int tipo) {
        this.tela = tela;
        this.tipo = tipo;
    }

    public void actionPerformed(ActionEvent e) {
        tela.trocarTabela(tipo);
    }
}