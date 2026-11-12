package frontend;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.BevelBorder;

import frontend.componentes.BarraTitulo;
import frontend.componentes.Estilo;

// Janela única do programa: barra de título, tela inicial e
// uma "carta" (CardLayout) para cada lab registrado em App.
public class JanelaPrincipal extends JFrame implements Navegacao {

    private static final String TITULO = "CG - CoordinateX";
    private static final String INICIAL = "inicial";

    private BarraTitulo barraTitulo;
    private CardLayout layoutCards;
    private JPanel painelCards;
    private JPanel botoesLabs;

    public JanelaPrincipal() {
        setUndecorated(true);
        setSize(1024, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilo.FACE);
        raiz.setBorder(new BevelBorder(BevelBorder.RAISED));

        barraTitulo = new BarraTitulo(this, TITULO);
        raiz.add(barraTitulo, BorderLayout.NORTH);

        layoutCards = new CardLayout();
        painelCards = new JPanel(layoutCards);
        painelCards.setBackground(Estilo.FACE);
        painelCards.add(criarTelaInicial(), INICIAL);
        raiz.add(painelCards, BorderLayout.CENTER);

        setContentPane(raiz);
    }

    // Registra um lab: cria o botão na tela inicial e a carta da tela
    public void adicionarLab(final TelaLab tela) {
        final String id = "lab" + painelCards.getComponentCount();
        painelCards.add(tela, id);

        JButton botao = criarBotaoInicial(tela.getNome());
        botao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                barraTitulo.setTitulo(TITULO + " - " + tela.getNome());
                layoutCards.show(painelCards, id);
                tela.aoMostrar();
            }
        });
        botoesLabs.add(botao);
    }

    public void voltarAoInicio() {
        barraTitulo.setTitulo(TITULO);
        layoutCards.show(painelCards, INICIAL);
    }

    public JFrame getJanela() {
        return this;
    }

    private JPanel criarTelaInicial() {
        JPanel telaInicial = new JPanel();
        telaInicial.setLayout(new BoxLayout(telaInicial, BoxLayout.Y_AXIS));
        telaInicial.setBackground(Estilo.FACE);
        telaInicial.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titulo = centralizar(new JLabel("CoordinateX"));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 40));

        JLabel professor = centralizar(new JLabel("Professor: Robson Pequeno de Sousa"));
        professor.setFont(new Font("SansSerif", Font.PLAIN, 25));

        JSeparator divisoria = centralizar(new JSeparator());
        divisoria.setMaximumSize(new Dimension(430, 2));

        JLabel equipeTitulo = centralizar(new JLabel("EQUIPE"));
        equipeTitulo.setFont(new Font("SansSerif", Font.ITALIC, 25));

        JLabel equipe = centralizar(new JLabel(Estilo.EQUIPE));
        equipe.setFont(new Font("SansSerif", Font.PLAIN, 20));

        // Um botão por lab, um embaixo do outro
        botoesLabs = new JPanel(new GridLayout(0, 1, 0, 8));
        botoesLabs.setBackground(Estilo.FACE);
        JPanel moldura = centralizar(new JPanel());
        moldura.setBackground(Estilo.FACE);
        moldura.add(botoesLabs);

        telaInicial.add(Box.createVerticalGlue());
        telaInicial.add(titulo);
        telaInicial.add(Box.createVerticalStrut(8));
        telaInicial.add(professor);
        telaInicial.add(Box.createVerticalStrut(18));
        telaInicial.add(divisoria);
        telaInicial.add(Box.createVerticalStrut(18));
        telaInicial.add(equipeTitulo);
        telaInicial.add(Box.createVerticalStrut(8));
        telaInicial.add(equipe);
        telaInicial.add(Box.createVerticalStrut(28));
        telaInicial.add(moldura);
        telaInicial.add(Box.createVerticalGlue());

        return telaInicial;
    }

    private <T extends JComponent> T centralizar(T componente) {
        componente.setAlignmentX(Component.CENTER_ALIGNMENT);
        return componente;
    }

    private JButton criarBotaoInicial(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.PLAIN, 16));
        botao.setFocusPainted(false);
        botao.setBackground(Estilo.FACE);
        botao.setPreferredSize(new Dimension(360, 40));
        botao.setBorder(BorderFactory.createCompoundBorder(
                new BevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        return botao;
    }
}
