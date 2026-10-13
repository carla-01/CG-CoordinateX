package frontend.componentes;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

// Barra de baixo: mensagem, coordenadas do mouse e a equipe.
public class BarraStatus extends JPanel {

    private JLabel mensagem;
    private JLabel coordenadas;

    public BarraStatus(String mensagemInicial) {
        super(new BorderLayout());
        setBackground(Estilo.FACE);

        mensagem = new JLabel(" " + mensagemInicial);
        mensagem.setBorder(new BevelBorder(BevelBorder.LOWERED));

        coordenadas = new JLabel(" ");
        coordenadas.setFont(Estilo.VALOR);
        coordenadas.setBorder(new BevelBorder(BevelBorder.LOWERED));
        coordenadas.setPreferredSize(new Dimension(130, 10));

        JLabel equipe = new JLabel(" Grupo: " + Estilo.EQUIPE + " ");
        equipe.setFont(Estilo.PEQUENA);
        equipe.setBorder(new BevelBorder(BevelBorder.LOWERED));

        add(mensagem, BorderLayout.CENTER);
        add(coordenadas, BorderLayout.EAST);
        add(equipe, BorderLayout.SOUTH);
    }

    public void setMensagem(String texto) {
        mensagem.setText(" " + texto);
    }

    // Ex.: "(12, 30)". Vazio quando o mouse sai da tela.
    public void setCoordenadas(String texto) {
        coordenadas.setText(" " + texto);
    }
}
