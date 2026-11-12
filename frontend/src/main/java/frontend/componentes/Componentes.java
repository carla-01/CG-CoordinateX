package frontend.componentes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

// Fábrica dos componentes usados em todas as telas, para que
// todos os labs fiquem com a mesma cara.
public class Componentes {

    // Conteúdo de uma aba: as linhas ficam uma embaixo da outra, alinhadas à esquerda
    public static JPanel painelAba(JComponent[] linhas) {
        JPanel coluna = new JPanel();
        coluna.setLayout(new BoxLayout(coluna, BoxLayout.Y_AXIS));
        coluna.setBackground(Estilo.FACE);
        for (int i = 0; i < linhas.length; i++) {
            linhas[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            coluna.add(linhas[i]);
        }

        // Segura as linhas no topo da aba, sem esticar
        JPanel aba = new JPanel(new BorderLayout());
        aba.setBackground(Estilo.FACE);
        aba.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        aba.add(coluna, BorderLayout.NORTH);
        return aba;
    }

    // Botão padrão (barra de opções e abas)
    public static JButton botao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setFont(Estilo.BOTAO);
        botao.setBackground(Estilo.FACE);
        botao.setBorder(BorderFactory.createCompoundBorder(
                new BevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        return botao;
    }

    // Deixa "afundado" só o botão escolhido de um grupo (como um rádio)
    public static void marcarSelecionado(JButton[] botoes, int selecionado) {
        for (int i = 0; i < botoes.length; i++) {
            int relevo;
            if (i == selecionado) {
                relevo = BevelBorder.LOWERED;
            } else {
                relevo = BevelBorder.RAISED;
            }
            botoes[i].setBorder(BorderFactory.createCompoundBorder(
                    new BevelBorder(relevo),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        }
    }

    public static JTextField campo(String valorInicial, int colunas) {
        JTextField campo = new JTextField(valorInicial, colunas);
        campo.setBorder(new BevelBorder(BevelBorder.LOWERED));
        return campo;
    }

    public static JCheckBox caixaMarcar(String texto, boolean marcada) {
        JCheckBox caixa = new JCheckBox(texto, marcada);
        caixa.setFont(Estilo.NORMAL);
        caixa.setBackground(Estilo.FACE);
        caixa.setFocusPainted(false);
        return caixa;
    }

    public static JLabel rotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(Estilo.NORMAL);
        return rotulo;
    }

    // Linha de campos (parte de cima da tela)
    public static JPanel linhaEntrada() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        painel.setBackground(Estilo.FACE);
        return painel;
    }

    // Caixa de seleção do zoom. Os textos ficam "1x", "2x", ...
    public static JComboBox<String> comboZoom(int[] valores, int padrao) {
        JComboBox<String> combo = new JComboBox<String>();
        for (int i = 0; i < valores.length; i++) {
            combo.addItem(valores[i] + "x");
            if (valores[i] == padrao) {
                combo.setSelectedIndex(i);
            }
        }
        return combo;
    }

    // Lê o zoom escolhido no combo ("10x" -> 10)
    public static int lerZoom(JComboBox<String> combo) {
        String texto = (String) combo.getSelectedItem();
        return Integer.parseInt(texto.substring(0, texto.length() - 1));
    }

    // Lê um número real. Aceita "2.5" ou "2,5".
    public static double lerNumero(JTextField campo) {
        String texto = campo.getText().trim().replace(',', '.');
        return Double.parseDouble(texto);
    }

    public static int lerInteiro(JTextField campo) {
        return Integer.parseInt(campo.getText().trim());
    }

    // Número com 4 casas decimais, sempre com ponto
    public static String formatar(double valor) {
        return String.format(Locale.US, "%.4f", valor);
    }
}
