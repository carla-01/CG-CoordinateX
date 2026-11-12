package frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import frontend.componentes.BarraStatus;
import frontend.componentes.Componentes;
import frontend.componentes.Estilo;

// Base de todas as telas de lab. Todas seguem o mesmo layout:
//
//   +---------------------------------------------+
//   | menu (Arquivo, ..., Ajuda)                  |
//   | barra de opções (zoom, limpar, ...)         |
//   +---------------------------------------------+
//   |                                             |
//   |        área de desenho (tela do usuário)    |
//   |                                             |
//   +------------------------------+--------------+
//   | [Aba 1][Aba 2][Aba 3]        | Log          |
//   | campos da operação escolhida |              |
//   +------------------------------+--------------+
//   | barra de status (mensagem | coordenadas)    |
//   +---------------------------------------------+
//
// Para criar a tela de um lab novo: estenda esta classe, monte a barra
// de opções, a área de desenho e as abas, e chame montar(...) no fim do
// construtor. Cada operação do lab vira uma aba.
public abstract class TelaLab extends JPanel {

    private static final int ALTURA_ABAS = 150;

    protected Navegacao navegacao;
    protected BarraStatus status;

    private String nome;
    private String sobre;
    private JMenuBar barraMenu;
    private JMenu menuAjuda;
    private JTextArea textoLog;

    public TelaLab(Navegacao navegacao, String nome, String sobre, String mensagemInicial) {
        super(new BorderLayout());
        this.navegacao = navegacao;
        this.nome = nome;
        this.sobre = sobre;
        setBackground(Estilo.FACE);

        status = new BarraStatus(mensagemInicial);
        barraMenu = criarBarraMenu();
    }

    // Nome do lab (botão da tela inicial e barra de título)
    public String getNome() {
        return nome;
    }

    // Chamado pela janela principal quando a tela aparece
    public void aoMostrar() {
    }

    protected void montar(JComponent barraOpcoes, JComponent desenho, JTabbedPane abas) {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.FACE);
        topo.add(barraMenu, BorderLayout.NORTH);
        if (barraOpcoes != null) {
            barraOpcoes.setBorder(new BevelBorder(BevelBorder.RAISED));
            topo.add(barraOpcoes, BorderLayout.CENTER);
        }
        add(topo, BorderLayout.NORTH);

        add(desenho, BorderLayout.CENTER);

        JPanel baixo = new JPanel(new BorderLayout(4, 0));
        baixo.setBackground(Estilo.FACE);
        baixo.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        baixo.setPreferredSize(new Dimension(10, ALTURA_ABAS));
        baixo.add(abas, BorderLayout.CENTER);
        baixo.add(criarPainelLog(), BorderLayout.EAST);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Estilo.FACE);
        rodape.add(baixo, BorderLayout.CENTER);
        rodape.add(status, BorderLayout.SOUTH);
        add(rodape, BorderLayout.SOUTH);
    }

    // Abas das operações (parte de baixo)
    protected JTabbedPane criarAbas() {
        JTabbedPane abas = new JTabbedPane();
        abas.setBackground(Estilo.FACE);
        abas.setFont(Estilo.NORMAL);
        return abas;
    }

    // Menu próprio do lab, entre "Arquivo" e "Ajuda"
    protected void adicionarMenu(JMenu menu) {
        barraMenu.remove(menuAjuda);
        barraMenu.add(menu);
        barraMenu.add(menuAjuda);
    }

    // Escreve uma linha no log (canto inferior direito)
    protected void log(String texto) {
        textoLog.append(texto + "\n");
        textoLog.setCaretPosition(textoLog.getDocument().getLength());
    }

    protected void limparLog() {
        textoLog.setText("");
    }

    protected void mostrarErro(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(navegacao.getJanela(), mensagem, titulo, JOptionPane.ERROR_MESSAGE);
    }

    private JPanel criarPainelLog() {
        textoLog = new JTextArea();
        textoLog.setEditable(false);
        textoLog.setFont(Estilo.LISTA);
        textoLog.setBackground(Color.WHITE);
        textoLog.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        JScrollPane rolagem = new JScrollPane(textoLog);
        rolagem.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JButton limpar = Componentes.botao("Limpar log");
        limpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparLog();
            }
        });

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Estilo.FACE);
        cabecalho.add(new JLabel(" Log"), BorderLayout.WEST);
        cabecalho.add(limpar, BorderLayout.EAST);

        JPanel painel = new JPanel(new BorderLayout(0, 2));
        painel.setBackground(Estilo.FACE);
        painel.setPreferredSize(new Dimension(320, 10));
        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(rolagem, BorderLayout.CENTER);
        return painel;
    }

    private JMenuBar criarBarraMenu() {
        JMenuBar barra = new JMenuBar();
        barra.setBackground(Estilo.FACE);

        JMenu arquivo = new JMenu("Arquivo");
        JMenuItem inicio = new JMenuItem("Voltar ao inicio");
        inicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navegacao.voltarAoInicio();
            }
        });
        JMenuItem sair = new JMenuItem("Sair");
        sair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        arquivo.add(inicio);
        arquivo.add(sair);

        menuAjuda = new JMenu("Ajuda");
        JMenuItem itemSobre = new JMenuItem("Sobre");
        itemSobre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(navegacao.getJanela(), "CG-CoordinateX - " + nome + "\n\n" + sobre,
                        "Sobre", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menuAjuda.add(itemSobre);

        barra.add(arquivo);
        barra.add(menuAjuda);
        return barra;
    }
}
