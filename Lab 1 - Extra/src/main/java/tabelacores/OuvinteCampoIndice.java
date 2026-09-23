package tabelacores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Chamado quando aperta Enter no campo ou clica em "Ver cor".
public class OuvinteCampoIndice implements ActionListener {

    private TelaCores tela;

    public OuvinteCampoIndice(TelaCores tela) {
        this.tela = tela;
    }

    public void actionPerformed(ActionEvent e) {
        tela.selecionarPeloCampo();
    }
}