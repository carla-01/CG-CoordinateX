package tabelacores;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Painel da direita: consulta um índice e mostra a cor dele.
public class PainelInspetor extends JPanel {

    private TabelaCores tabela;

    private JTextField campoIndice;
    private JButton botaoVer;
    private JLabel mensagemErro;

    private JPanel amostra;
    private JLabel corHexGrande;

    private JLabel valorIndiceHex;
    private JLabel valorIndiceDecimal;
    private JLabel valorBinario;
    private JLabel valorR;
    private JLabel valorG;
    private JLabel valorB;
    private JLabel valorTabela;

    private JButton botaoColorida;
    private JButton botaoCinza;
    private JButton botaoFogo;

    public PainelInspetor(TabelaCores tabela) {
        this.tabela = tabela;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Estilo.CARTAO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        montarCampo();
        add(Box.createVerticalStrut(14));
        montarAmostra();
        add(Box.createVerticalStrut(16));
        montarInformacoes();
        add(Box.createVerticalStrut(18));
        montarBotoes();
    }

    private void montarCampo() {
        add(criarRotulo("Índice do pixel (decimal ou 0x...)", Estilo.TEXTO_FRACO));
        add(Box.createVerticalStrut(6));

        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setBackground(Estilo.CARTAO);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoIndice = new JTextField("0x180", 8);
        campoIndice.setFont(Estilo.VALOR);

        botaoVer = new JButton("Ver cor");
        botaoVer.setFocusPainted(false);

        linha.add(campoIndice);
        linha.add(Box.createHorizontalStrut(8));
        linha.add(botaoVer);
        add(linha);

        mensagemErro = criarRotulo(" ", Estilo.ERRO);
        add(mensagemErro);
    }

    private void montarAmostra() {
        amostra = new JPanel();
        amostra.setPreferredSize(new Dimension(280, 110));
        amostra.setMaximumSize(new Dimension(280, 110));
        amostra.setBorder(BorderFactory.createLineBorder(Estilo.TEXTO, 2));
        amostra.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(amostra);

        add(Box.createVerticalStrut(8));
        corHexGrande = new JLabel("#000000");
        corHexGrande.setFont(Estilo.HEX_GRANDE);
        corHexGrande.setForeground(Estilo.TEXTO);
        corHexGrande.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(corHexGrande);
    }

    private void montarInformacoes() {
        JPanel grade = new JPanel(new GridLayout(0, 2, 12, 8));
        grade.setBackground(Estilo.CARTAO);
        grade.setAlignmentX(Component.LEFT_ALIGNMENT);

        valorIndiceHex = criarValor();
        valorIndiceDecimal = criarValor();
        valorBinario = criarValor();
        valorR = criarValor();
        valorG = criarValor();
        valorB = criarValor();
        valorTabela = criarValor();

        grade.add(criarRotulo("Índice (hex)", Estilo.TEXTO_FRACO));
        grade.add(valorIndiceHex);
        grade.add(criarRotulo("Índice (decimal)", Estilo.TEXTO_FRACO));
        grade.add(valorIndiceDecimal);
        grade.add(criarRotulo("12 bits", Estilo.TEXTO_FRACO));
        grade.add(valorBinario);
        grade.add(criarRotulo("R", Estilo.TEXTO_FRACO));
        grade.add(valorR);
        grade.add(criarRotulo("G", Estilo.TEXTO_FRACO));
        grade.add(valorG);
        grade.add(criarRotulo("B", Estilo.TEXTO_FRACO));
        grade.add(valorB);
        grade.add(criarRotulo("Tabela", Estilo.TEXTO_FRACO));
        grade.add(valorTabela);

        add(grade);
    }

    private void montarBotoes() {
        add(criarRotulo("Trocar tabela (o frame buffer não muda)", Estilo.TEXTO_FRACO));
        add(Box.createVerticalStrut(6));

        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setBackground(Estilo.CARTAO);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoColorida = criarBotao("Colorida");
        botaoCinza = criarBotao("Cinza");
        botaoFogo = criarBotao("Fogo");

        linha.add(botaoColorida);
        linha.add(Box.createHorizontalStrut(6));
        linha.add(botaoCinza);
        linha.add(Box.createHorizontalStrut(6));
        linha.add(botaoFogo);
        add(linha);
    }

    // Atualiza tudo pra mostrar a cor do índice
    public void mostrarIndice(int indice) {
        int cor = tabela.getCor(indice);
        int r = tabela.getVermelho(indice);
        int g = tabela.getVerde(indice);
        int b = tabela.getAzul(indice);

        amostra.setBackground(new Color(cor));
        corHexGrande.setText(Hexa.cor(cor));

        valorIndiceHex.setText(Hexa.indice(indice));
        valorIndiceDecimal.setText("" + indice);
        valorBinario.setText(Hexa.binario(indice));
        valorR.setText("0x" + Hexa.canal(r) + "  (" + r + ")");
        valorG.setText("0x" + Hexa.canal(g) + "  (" + g + ")");
        valorB.setText("0x" + Hexa.canal(b) + "  (" + b + ")");
        valorTabela.setText(tabela.getNome());

        campoIndice.setText(Hexa.indice(indice));
        mensagemErro.setText(" ");
    }

    public void mostrarErro(String mensagem) {
        mensagemErro.setText(mensagem);
    }

    public String getTextoDigitado() {
        return campoIndice.getText();
    }

    public JTextField getCampoIndice() {
        return campoIndice;
    }

    public JButton getBotaoVer() {
        return botaoVer;
    }

    public JButton getBotaoColorida() {
        return botaoColorida;
    }

    public JButton getBotaoCinza() {
        return botaoCinza;
    }

    public JButton getBotaoFogo() {
        return botaoFogo;
    }

    private JLabel criarRotulo(String texto, Color cor) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(Estilo.ROTULO);
        rotulo.setForeground(cor);
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return rotulo;
    }

    private JLabel criarValor() {
        JLabel valor = new JLabel("-");
        valor.setFont(Estilo.VALOR);
        valor.setForeground(Estilo.TEXTO);
        return valor;
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setBackground(Estilo.DESTAQUE);
        botao.setForeground(Color.WHITE);
        return botao;
    }
}
