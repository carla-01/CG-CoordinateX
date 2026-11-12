package frontend;

import modelo.Poligono;
import transformacoes.Matriz;
import transformacoes.Operacao;
import transformacoes.Transformacoes2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// LAB 3 - Transformações Geométricas 2D (aula G5)
// O polígono nasce na ORIGEM e é desenhado com o algoritmo do ponto médio (aula G3).
//
// Layout igual ao da tela "2D" do .jar de referência:
//   esquerda: Objeto 2D | abas Trans/Rot/Escala/Cisal/Reflex | Sequência | Mapeamento 2D
//   centro:   Mundo (500 x 500) e Viewport (300 x 300), histórico embaixo
//   direita:  Informações do objeto + Limpar Tudo
// Visual igual ao da Interface do Lab 1 (estilo Windows clássico).
public class TelaLab3 extends JFrame {

    // ---- cores do estilo (as mesmas do Lab 1) ----
    private static final Color FACE = new Color(0xC0C0C0);
    private static final Color TITULO_INICIO = new Color(0x000080);
    private static final Color TITULO_FIM = new Color(0x1084D0);

    // ---- objeto ----
    private Poligono original;   // como foi gerado (na origem)
    private Poligono atual;      // depois das transformações
    private int contadorOperacoes = 0;

    // ---- Objeto 2D ----
    private JComboBox<String> comboTipo;
    private JTextField campoLados;
    private JTextField campoTamanho;

    // ---- abas ----
    private JTabbedPane abas;
    private JTextField campoTx, campoTy;
    private JTextField campoAngulo, campoRotX, campoRotY;
    private JTextField campoSx, campoSy, campoEscX, campoEscY;
    private JTextField campoCisA, campoCisB;
    private JRadioButton radioRefX, radioRefY, radioRefOrigem, radioRefYX, radioRefYMenosX;

    // ---- sequência (transformações compostas) ----
    private List<Operacao> sequencia = new ArrayList<Operacao>();
    private DefaultListModel<String> modeloSequencia = new DefaultListModel<String>();

    // ---- mapeamento janela -> viewport ----
    private JCheckBox chkMapeamento;
    private JTextField campoXmin, campoXmax, campoYmin, campoYmax;
    private JTextField campoUmin, campoUmax, campoVmin, campoVmax;
    private JButton botaoLimites;
    private Matriz matrizViewport;          // null = mapeamento desligado
    private double[] janela, viewport;

    // ---- desenho e informações ----
    private PainelMundo painelMundo;
    private PainelViewport painelViewport;
    private JTextArea textoHistorico;
    private JTextArea textoVertices;
    private JLabel labelCentro;
    private JLabel labelDimensoes;
    private JLabel statusLabel;
    private JCheckBoxMenuItem menuMostrarOriginal;
    private JCheckBoxMenuItem menuMostrarGrade;

    // ---- janela sem borda do sistema (igual ao Lab 1) ----
    private int arrastarX, arrastarY;
    private Rectangle limitesRestaurados;

    // Aberta pela tela inicial do frontend: fechar volta para ela.
    // null = aberta sozinha pelo app.Main: fechar encerra o programa.
    private final Runnable voltarAoInicio;

    public TelaLab3() {
        this(null);
    }

    public TelaLab3(Runnable voltarAoInicio) {
        this.voltarAoInicio = voltarAoInicio;
        setUndecorated(true);
        setSize(1400, 880);
        setLocationRelativeTo(null);
        if (voltarAoInicio == null) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FACE);
        raiz.setBorder(new BevelBorder(BevelBorder.RAISED));

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(criarBarraTitulo(), BorderLayout.NORTH);
        topo.add(criarBarraMenu(), BorderLayout.SOUTH);
        raiz.add(topo, BorderLayout.NORTH);

        JPanel conteudo = new JPanel(new BorderLayout(6, 6));
        conteudo.setBackground(FACE);
        conteudo.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        conteudo.add(criarPainelEsquerdo(), BorderLayout.WEST);
        conteudo.add(criarPainelCentral(), BorderLayout.CENTER);
        conteudo.add(criarPainelDireito(), BorderLayout.EAST);
        raiz.add(conteudo, BorderLayout.CENTER);

        raiz.add(criarBarraStatus(), BorderLayout.SOUTH);
        setContentPane(raiz);

        gerarObjeto(); // já abre com um pentágono na origem
    }

    // =====================================================================
    //  PAINEL ESQUERDO
    // =====================================================================

    private JScrollPane criarPainelEsquerdo() {
        ColunaLateral coluna = new ColunaLateral();
        coluna.setLayout(new BoxLayout(coluna, BoxLayout.Y_AXIS));
        coluna.setBackground(FACE);

        coluna.add(alinharEsquerda(criarPainelObjeto()));
        coluna.add(Box.createVerticalStrut(6));
        coluna.add(alinharEsquerda(criarAbas()));
        coluna.add(Box.createVerticalStrut(6));
        coluna.add(alinharEsquerda(criarPainelSequencia()));
        coluna.add(Box.createVerticalStrut(6));
        coluna.add(alinharEsquerda(criarPainelMapeamento()));

        // Só rola na vertical; a largura acompanha a coluna
        JScrollPane rolagem = new JScrollPane(coluna,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rolagem.setPreferredSize(new Dimension(300, 10));
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        return rolagem;
    }

    // ---------- Objeto 2D ----------
    private JPanel criarPainelObjeto() {
        comboTipo = new JComboBox<String>(new String[] { "Regular", "Irregular (bandeira)" });
        campoLados = criarCampo("5");
        campoTamanho = criarCampo("80");

        // No irregular o número de lados é fixo
        comboTipo.addActionListener(e -> campoLados.setEnabled(comboTipo.getSelectedIndex() == 0));

        JButton gerar = criarBotao("Gerar Objeto");
        gerar.addActionListener(e -> gerarObjeto());

        JPanel painel = formulario(
                new String[] { "Tipo:", "Nº de lados:", "Tamanho (raio):" },
                new JComponent[] { comboTipo, campoLados, campoTamanho },
                gerar);
        painel.setBorder(criarGrupo("Objeto 2D (centro na origem)"));
        return painel;
    }

    // ---------- Abas das transformações ----------
    private JTabbedPane criarAbas() {
        abas = new JTabbedPane();
        abas.setBackground(FACE);
        abas.setFont(new Font("SansSerif", Font.PLAIN, 11));

        // Translação
        campoTx = criarCampo("50");
        campoTy = criarCampo("30");
        JButton bTrans = criarBotao("Aplicar Translação");
        bTrans.addActionListener(e -> aplicarDaAba(0));
        abas.addTab("Trans", formulario(
                new String[] { "tx:", "ty:" },
                new JComponent[] { campoTx, campoTy }, bTrans));

        // Rotação
        campoAngulo = criarCampo("45");
        campoRotX = criarCampo("0");
        campoRotY = criarCampo("0");
        JButton bRot = criarBotao("Aplicar Rotação");
        bRot.addActionListener(e -> aplicarDaAba(1));
        abas.addTab("Rot", formulario(
                new String[] { "Ângulo θ (graus):", "Ponto fixo x:", "Ponto fixo y:" },
                new JComponent[] { campoAngulo, campoRotX, campoRotY }, bRot));

        // Escala
        campoSx = criarCampo("2");
        campoSy = criarCampo("2");
        campoEscX = criarCampo("0");
        campoEscY = criarCampo("0");
        JButton bEsc = criarBotao("Aplicar Escala");
        bEsc.addActionListener(e -> aplicarDaAba(2));
        abas.addTab("Escala", formulario(
                new String[] { "sx:", "sy:", "Ponto fixo x:", "Ponto fixo y:" },
                new JComponent[] { campoSx, campoSy, campoEscX, campoEscY }, bEsc));

        // Cisalhamento
        campoCisA = criarCampo("0.5");
        campoCisB = criarCampo("0");
        JButton bCis = criarBotao("Aplicar Cisalhamento");
        bCis.addActionListener(e -> aplicarDaAba(3));
        abas.addTab("Cisal", formulario(
                new String[] { "a (em x):", "b (em y):" },
                new JComponent[] { campoCisA, campoCisB }, bCis));

        // Reflexão
        radioRefX = new JRadioButton("Eixo x", true);
        radioRefY = new JRadioButton("Eixo y");
        radioRefOrigem = new JRadioButton("Origem");
        radioRefYX = new JRadioButton("Reta y = x");
        radioRefYMenosX = new JRadioButton("Reta y = -x");
        ButtonGroup grupo = new ButtonGroup();
        JPanel opcoes = new JPanel(new GridLayout(0, 2, 4, 2));
        opcoes.setBackground(FACE);
        JRadioButton[] radios = { radioRefX, radioRefY, radioRefOrigem, radioRefYX, radioRefYMenosX };
        for (JRadioButton r : radios) {
            r.setBackground(FACE);
            grupo.add(r);
            opcoes.add(r);
        }
        JButton bRef = criarBotao("Aplicar Reflexão");
        bRef.addActionListener(e -> aplicarDaAba(4));
        JPanel abaRef = new JPanel(new BorderLayout(0, 6));
        abaRef.setBackground(FACE);
        abaRef.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        abaRef.add(opcoes, BorderLayout.CENTER);
        abaRef.add(bRef, BorderLayout.SOUTH);
        abas.addTab("Reflex", abaRef);

        abas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        return abas;
    }

    // ---------- Sequência (transformações compostas) ----------
    private JPanel criarPainelSequencia() {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setBackground(FACE);
        painel.setBorder(criarGrupo("Sequência (transformação composta)"));

        JLabel dica = new JLabel("<html>Configure na aba acima e clique em Adicionar.</html>");
        dica.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JList<String> lista = new JList<String>(modeloSequencia);
        lista.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane rolagem = new JScrollPane(lista);
        rolagem.setPreferredSize(new Dimension(10, 80));
        rolagem.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JButton adicionar = criarBotao("Adicionar à Sequência");
        adicionar.addActionListener(e -> adicionarNaSequencia());
        JButton aplicar = criarBotao("Aplicar Sequência");
        aplicar.addActionListener(e -> aplicarSequencia());
        JButton limpar = criarBotao("Limpar Sequência");
        limpar.addActionListener(e -> {
            sequencia.clear();
            modeloSequencia.clear();
        });

        JPanel botoes = new JPanel(new GridLayout(0, 1, 0, 4));
        botoes.setBackground(FACE);
        botoes.add(adicionar);
        botoes.add(aplicar);
        botoes.add(limpar);

        painel.add(dica, BorderLayout.NORTH);
        painel.add(rolagem, BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    // ---------- Mapeamento 2D (janela -> viewport) ----------
    private JPanel criarPainelMapeamento() {
        chkMapeamento = new JCheckBox("Ativar Window → Viewport");
        chkMapeamento.setBackground(FACE);
        chkMapeamento.addActionListener(e -> alternarMapeamento());

        campoXmin = criarCampo("-250");
        campoXmax = criarCampo("250");
        campoYmin = criarCampo("-250");
        campoYmax = criarCampo("250");
        campoUmin = criarCampo("0");
        campoUmax = criarCampo("299");
        campoVmin = criarCampo("0");
        campoVmax = criarCampo("299");

        JPanel gradeJanela = gradeMinMax("x", campoXmin, campoXmax, "y", campoYmin, campoYmax);
        JPanel gradeViewport = gradeMinMax("u", campoUmin, campoUmax, "v", campoVmin, campoVmax);

        botaoLimites = criarBotao("Aplicar Limites");
        botaoLimites.addActionListener(e -> aplicarLimites());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(FACE);
        painel.setBorder(criarGrupo("Mapeamento 2D"));
        painel.add(alinharEsquerda(chkMapeamento));
        painel.add(alinharEsquerda(new JLabel("Coordenadas da Window (Mundo)")));
        painel.add(gradeJanela);
        painel.add(Box.createVerticalStrut(4));
        painel.add(alinharEsquerda(new JLabel("Coordenadas da Viewport (0 a 299)")));
        painel.add(gradeViewport);
        painel.add(Box.createVerticalStrut(6));
        painel.add(alinharEsquerda(botaoLimites));

        setCamposMapeamento(false);
        return painel;
    }

    // =====================================================================
    //  PAINEL CENTRAL (Mundo, Viewport e Histórico)
    // =====================================================================

    private JPanel criarPainelCentral() {
        painelMundo = new PainelMundo();
        painelViewport = new PainelViewport();

        JPanel desenhos = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        desenhos.setBackground(FACE);
        desenhos.add(comTitulo("Mundo (500 x 500)  -  origem no centro", painelMundo));
        desenhos.add(comTitulo("Viewport (300 x 300)", painelViewport));

        textoHistorico = new JTextArea();
        textoHistorico.setEditable(false);
        textoHistorico.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane rolagem = new JScrollPane(textoHistorico);
        rolagem.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel historico = new JPanel(new BorderLayout());
        historico.setBackground(FACE);
        historico.setBorder(criarGrupo("Histórico de Operações e Matrizes de Transformação"));
        historico.add(rolagem, BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.setBackground(FACE);
        centro.add(desenhos, BorderLayout.NORTH);
        centro.add(historico, BorderLayout.CENTER);
        return centro;
    }

    // Coloca um título em cima do canvas e uma moldura afundada em volta
    private JPanel comTitulo(String titulo, JPanel canvas) {
        JLabel rotulo = new JLabel(titulo, JLabel.CENTER);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 13));

        JPanel moldura = new JPanel(new BorderLayout());
        moldura.setBorder(new BevelBorder(BevelBorder.LOWERED));
        moldura.add(canvas, BorderLayout.CENTER);

        JPanel caixa = new JPanel(new BorderLayout(0, 4));
        caixa.setBackground(FACE);
        caixa.add(rotulo, BorderLayout.NORTH);
        caixa.add(moldura, BorderLayout.CENTER);
        return caixa;
    }

    // =====================================================================
    //  PAINEL DIREITO (informações do objeto)
    // =====================================================================

    private JPanel criarPainelDireito() {
        textoVertices = new JTextArea();
        textoVertices.setEditable(false);
        textoVertices.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane rolagem = new JScrollPane(textoVertices);
        rolagem.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel vertices = new JPanel(new BorderLayout());
        vertices.setBackground(FACE);
        vertices.setBorder(criarGrupo("Vértices"));
        vertices.add(rolagem, BorderLayout.CENTER);

        labelCentro = criarValor();
        labelDimensoes = criarValor();

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FACE);
        centro.setBorder(criarGrupo("Centro Geométrico"));
        centro.add(labelCentro);

        JPanel dimensoes = new JPanel(new BorderLayout());
        dimensoes.setBackground(FACE);
        dimensoes.setBorder(criarGrupo("Dimensões (L x A)"));
        dimensoes.add(labelDimensoes);

        JPanel baixo = new JPanel(new GridLayout(0, 1, 0, 6));
        baixo.setBackground(FACE);
        baixo.add(centro);
        baixo.add(dimensoes);

        JPanel info = new JPanel(new BorderLayout(0, 6));
        info.setBackground(FACE);
        info.setBorder(criarGrupo("Informações do Objeto"));
        info.add(vertices, BorderLayout.CENTER);
        info.add(baixo, BorderLayout.SOUTH);

        JButton limparTudo = criarBotao("Limpar Tudo");
        limparTudo.addActionListener(e -> limparTudo());

        JPanel direito = new JPanel(new BorderLayout(0, 6));
        direito.setBackground(FACE);
        direito.setPreferredSize(new Dimension(220, 10));
        direito.add(info, BorderLayout.CENTER);
        direito.add(limparTudo, BorderLayout.SOUTH);
        return direito;
    }

    // =====================================================================
    //  AÇÕES
    // =====================================================================

    // Cria o polígono NA ORIGEM
    private void gerarObjeto() {
        try {
            double tamanho = lerNumero(campoTamanho);
            if (tamanho <= 0 || tamanho > 240) {
                erro("O tamanho deve estar entre 0 e 240 (o mundo vai de -250 a 250).");
                return;
            }

            String descricao;
            if (comboTipo.getSelectedIndex() == 0) {
                int lados = (int) lerNumero(campoLados);
                if (lados < 3) {
                    erro("Um polígono precisa de pelo menos 3 lados.");
                    return;
                }
                original = Poligono.regular(lados, tamanho);
                descricao = "Polígono regular de " + lados + " lados, raio " + f(tamanho);
            } else {
                original = Poligono.irregular(tamanho);
                descricao = "Polígono irregular (bandeira) de 7 lados, tamanho " + f(tamanho);
            }
            atual = original;

            contadorOperacoes = 0;
            textoHistorico.setText("Objeto gerado: " + descricao + "\n"
                    + "Centro na origem (0, 0).\n\nVértices:\n" + indentar(atual.listarVertices())
                    + linha());
            atualizarTudo();
            statusLabel.setText(" Objeto gerado na origem: " + descricao);
        } catch (NumberFormatException ex) {
            erro("Valores numéricos inválidos para gerar o objeto.");
        }
    }

    // Botão "Aplicar ..." de cada aba
    private void aplicarDaAba(int aba) {
        try {
            Operacao op = montarOperacao(aba);
            atual = atual.transformar(op.getMatriz());   // P' = M . P em todos os vértices
            registrar(op.getNome(), op.getDetalhes());
            atualizarTudo();
            statusLabel.setText(" Aplicado: " + op.getNome());
        } catch (NumberFormatException ex) {
            erro("Valores numéricos inválidos.");
        }
    }

    // Lê os campos da aba e monta a matriz com as funções da classe Transformacoes2D
    private Operacao montarOperacao(int aba) {
        if (aba == 0) { // ----- Translação -----
            double tx = lerNumero(campoTx);
            double ty = lerNumero(campoTy);
            Matriz t = Transformacoes2D.translacao(tx, ty);
            return new Operacao("Translação T(" + f(tx) + ", " + f(ty) + ")", t,
                    "Matriz de Translação T(" + f(tx) + ", " + f(ty) + "):\n" + t);
        }

        if (aba == 1) { // ----- Rotação -----
            double ang = lerNumero(campoAngulo);
            double px = lerNumero(campoRotX);
            double py = lerNumero(campoRotY);
            Matriz r = Transformacoes2D.rotacao(ang);
            String nome = "Rotação de " + f(ang) + "° em torno de (" + f(px) + ", " + f(py) + ")";

            if (px == 0 && py == 0) { // em torno da origem: só R
                return new Operacao(nome, r, "Matriz de Rotação R(" + f(ang) + "°):\n" + r);
            }
            Matriz c = Transformacoes2D.rotacaoPontoFixo(ang, px, py);
            return new Operacao(nome, c, passosPontoFixo(px, py, "Rotação R(" + f(ang) + "°)", r, c, "R"));
        }

        if (aba == 2) { // ----- Escala -----
            double sx = lerNumero(campoSx);
            double sy = lerNumero(campoSy);
            double px = lerNumero(campoEscX);
            double py = lerNumero(campoEscY);
            Matriz s = Transformacoes2D.escala(sx, sy);
            String nome = "Escala S(" + f(sx) + ", " + f(sy) + ") com ponto fixo (" + f(px) + ", " + f(py) + ")";

            if (px == 0 && py == 0) { // ponto fixo na origem: só S
                return new Operacao(nome, s, "Matriz de Escala S(" + f(sx) + ", " + f(sy) + "):\n" + s);
            }
            Matriz c = Transformacoes2D.escalaPontoFixo(sx, sy, px, py);
            return new Operacao(nome, c,
                    passosPontoFixo(px, py, "Escala S(" + f(sx) + ", " + f(sy) + ")", s, c, "S"));
        }

        if (aba == 3) { // ----- Cisalhamento -----
            double a = lerNumero(campoCisA);
            double b = lerNumero(campoCisB);
            Matriz cx = Transformacoes2D.cisalhamentoX(a);
            Matriz cy = Transformacoes2D.cisalhamentoY(b);

            if (b == 0) {
                return new Operacao("Cisalhamento em x (a = " + f(a) + ")", cx,
                        "Matriz de Cisalhamento em x (a = " + f(a) + "):\n" + cx);
            }
            if (a == 0) {
                return new Operacao("Cisalhamento em y (b = " + f(b) + ")", cy,
                        "Matriz de Cisalhamento em y (b = " + f(b) + "):\n" + cy);
            }
            // Os dois: primeiro em x, depois em y  ->  M = Cy . Cx
            Matriz m = cy.multiplicar(cx);
            return new Operacao("Cisalhamento em x (a = " + f(a) + ") e em y (b = " + f(b) + ")", m,
                    "1) Cisalhamento em x (a = " + f(a) + "):\n" + cx
                            + "2) Cisalhamento em y (b = " + f(b) + "):\n" + cy
                            + "Matriz final M = Cy . Cx:\n" + m);
        }

        // ----- Reflexão -----
        Matriz m;
        String nome;
        if (radioRefX.isSelected()) {
            m = Transformacoes2D.reflexaoEixoX();
            nome = "Reflexão em torno do eixo x";
        } else if (radioRefY.isSelected()) {
            m = Transformacoes2D.reflexaoEixoY();
            nome = "Reflexão em torno do eixo y";
        } else if (radioRefOrigem.isSelected()) {
            m = Transformacoes2D.reflexaoOrigem();
            nome = "Reflexão em torno da origem";
        } else if (radioRefYX.isSelected()) {
            m = Transformacoes2D.reflexaoRetaYX();
            nome = "Reflexão em torno da reta y = x";
        } else {
            m = Transformacoes2D.reflexaoRetaYMenosX();
            nome = "Reflexão em torno da reta y = -x";
        }
        return new Operacao(nome, m, "Matriz de " + nome + ":\n" + m);
    }

    // Texto do passo a passo de uma transformação com ponto fixo (slides 24 a 26)
    private String passosPontoFixo(double px, double py, String nomeBase, Matriz base, Matriz c, String letra) {
        return "1) Translada o ponto fixo para a origem T(" + f(-px) + ", " + f(-py) + "):\n"
                + Transformacoes2D.translacao(-px, -py)
                + "2) " + nomeBase + ":\n" + base
                + "3) Translada de volta T(" + f(px) + ", " + f(py) + "):\n"
                + Transformacoes2D.translacao(px, py)
                + "Matriz final C = T(" + f(px) + ", " + f(py) + ") . " + letra
                + " . T(" + f(-px) + ", " + f(-py) + "):\n" + c;
    }

    // ---------- Sequência ----------
    private void adicionarNaSequencia() {
        try {
            Operacao op = montarOperacao(abas.getSelectedIndex());
            sequencia.add(op);
            modeloSequencia.addElement("M" + sequencia.size() + ": " + op.getNome());
            statusLabel.setText(" Sequência: adicionado M" + sequencia.size() + " = " + op.getNome());
        } catch (NumberFormatException ex) {
            erro("Valores numéricos inválidos.");
        }
    }

    // Slide 21: P' = Mn ... M2 . M1 . P  ->  multiplica tudo antes e aplica UMA vez
    private void aplicarSequencia() {
        if (sequencia.isEmpty()) {
            erro("A sequência está vazia. Adicione transformações primeiro.");
            return;
        }

        StringBuilder passos = new StringBuilder();
        Matriz composta = Transformacoes2D.escala(1, 1); // identidade
        String formula = "";

        for (int i = 0; i < sequencia.size(); i++) {
            Operacao op = sequencia.get(i);
            composta = op.getMatriz().multiplicar(composta); // a nova entra à ESQUERDA
            formula = (i == 0) ? "M1" : "M" + (i + 1) + " . " + formula;
            passos.append("M").append(i + 1).append(" = ").append(op.getNome()).append("\n")
                    .append(op.getMatriz());
        }
        passos.append("Matriz composta M = ").append(formula).append(":\n").append(composta);

        atual = atual.transformar(composta);
        registrar("Sequência de " + sequencia.size() + " transformações", passos.toString());

        sequencia.clear();
        modeloSequencia.clear();
        atualizarTudo();
        statusLabel.setText(" Sequência aplicada: M = " + formula);
    }

    // ---------- Mapeamento ----------
    private void alternarMapeamento() {
        boolean ligado = chkMapeamento.isSelected();
        setCamposMapeamento(ligado);
        if (ligado) {
            aplicarLimites();
        } else {
            matrizViewport = null;
            atualizarTudo();
        }
    }

    private void aplicarLimites() {
        try {
            double xmin = lerNumero(campoXmin), xmax = lerNumero(campoXmax);
            double ymin = lerNumero(campoYmin), ymax = lerNumero(campoYmax);
            double umin = lerNumero(campoUmin), umax = lerNumero(campoUmax);
            double vmin = lerNumero(campoVmin), vmax = lerNumero(campoVmax);

            if (xmax <= xmin || ymax <= ymin) {
                erro("Window inválida: o máximo deve ser maior que o mínimo.");
                return;
            }
            if (umax <= umin || vmax <= vmin || umin < 0 || vmin < 0 || umax > 299 || vmax > 299) {
                erro("Viewport inválida: use valores de 0 a 299, com o máximo maior que o mínimo.");
                return;
            }

            janela = new double[] { xmin, xmax, ymin, ymax };
            viewport = new double[] { umin, umax, vmin, vmax };
            matrizViewport = Transformacoes2D.janelaParaViewport(xmin, xmax, ymin, ymax, umin, umax, vmin, vmax);

            double sx = (umax - umin) / (xmax - xmin);
            double sy = (vmax - vmin) / (ymax - ymin);
            registrar("Mapeamento Window -> Viewport (não altera o objeto)",
                    "Window:   x [" + f(xmin) + ", " + f(xmax) + "]  y [" + f(ymin) + ", " + f(ymax) + "]\n"
                            + "Viewport: u [" + f(umin) + ", " + f(umax) + "]  v [" + f(vmin) + ", " + f(vmax)
                            + "]\n\n"
                            + "1) T(-xmin, -ymin):\n" + Transformacoes2D.translacao(-xmin, -ymin)
                            + "2) S((umax-umin)/(xmax-xmin), (vmax-vmin)/(ymax-ymin)):\n"
                            + Transformacoes2D.escala(sx, sy)
                            + "3) T(umin, vmin):\n" + Transformacoes2D.translacao(umin, vmin)
                            + "Matriz M = T(umin, vmin) . S . T(-xmin, -ymin):\n" + matrizViewport,
                    atual.transformar(matrizViewport), "Coordenadas do Objeto na Viewport (u, v):");
            atualizarTudo();
            statusLabel.setText(" Mapeamento Window -> Viewport atualizado.");
        } catch (NumberFormatException ex) {
            erro("Valores numéricos inválidos nos campos de mapeamento.");
        }
    }

    private void setCamposMapeamento(boolean ligado) {
        JTextField[] campos = { campoXmin, campoXmax, campoYmin, campoYmax,
                campoUmin, campoUmax, campoVmin, campoVmax };
        for (JTextField c : campos) {
            c.setEnabled(ligado);
        }
        botaoLimites.setEnabled(ligado);
    }

    // ---------- Limpar ----------
    // Volta o polígono ao original (na origem) e limpa histórico e sequência
    private void limparTudo() {
        sequencia.clear();
        modeloSequencia.clear();
        gerarObjeto();
    }

    // =====================================================================
    //  ATUALIZAÇÃO DA TELA E HISTÓRICO
    // =====================================================================

    private void atualizarTudo() {
        double[] janelaDesenho = (matrizViewport != null) ? janela : null;
        painelMundo.desenhar(atual, original, menuMostrarOriginal.isSelected(),
                menuMostrarGrade.isSelected(), janelaDesenho);

        if (matrizViewport != null) {
            painelViewport.desenhar(atual, matrizViewport, janela, viewport);
        } else {
            painelViewport.limpar();
        }

        textoVertices.setText(atual.listarVertices());
        textoVertices.setCaretPosition(0);
        labelCentro.setText(" " + atual.centro());
        labelDimensoes.setText(String.format(Locale.US, " %.2f x %.2f", atual.largura(), atual.altura()));
    }

    private void registrar(String titulo, String detalhes) {
        registrar(titulo, detalhes, atual, "Coordenadas Resultantes do Objeto:");
    }

    private void registrar(String titulo, String detalhes, Poligono resultado, String tituloVertices) {
        contadorOperacoes++;
        textoHistorico.append("Operação " + contadorOperacoes + ": " + titulo + "\n\n"
                + detalhes + "\n" + tituloVertices + "\n" + indentar(resultado.listarVertices()) + linha());
        textoHistorico.setCaretPosition(textoHistorico.getDocument().getLength());
    }

    private String linha() {
        return "============================================================\n\n";
    }

    private String indentar(String texto) {
        return "    " + texto.replace("\n", "\n    ").trim() + "\n";
    }

    // =====================================================================
    //  AUXILIARES DE INTERFACE (estilo do Lab 1)
    // =====================================================================

    // Aceita vírgula ou ponto decimal (2,5 ou 2.5), igual ao Lab 1
    private double lerNumero(JTextField campo) {
        return Double.parseDouble(campo.getText().trim().replace(',', '.'));
    }

    // Número sem ".0" sobrando: 2.0 -> "2", 0.5 -> "0.5"
    private String f(double valor) {
        if (valor == Math.rint(valor)) {
            return String.valueOf((long) valor);
        }
        return String.format(Locale.US, "%.2f", valor);
    }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private JTextField criarCampo(String valorInicial) {
        JTextField campo = new JTextField(valorInicial, 4);
        campo.setBorder(new BevelBorder(BevelBorder.LOWERED));
        return campo;
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.PLAIN, 12));
        botao.setFocusPainted(false);
        botao.setBackground(FACE);
        botao.setBorder(BorderFactory.createCompoundBorder(
                new BevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return botao;
    }

    private JLabel criarValor() {
        JLabel valor = new JLabel("-");
        valor.setFont(new Font("Monospaced", Font.BOLD, 12));
        return valor;
    }

    // Moldura de grupo com título (o "group box" do Windows clássico)
    private TitledBorder criarGrupo(String titulo) {
        TitledBorder borda = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), titulo);
        borda.setTitleFont(new Font("SansSerif", Font.BOLD, 11));
        borda.setTitleColor(Color.BLACK);
        return borda;
    }

    // Monta "rótulo | campo" em duas colunas e o botão embaixo
    private JPanel formulario(String[] rotulos, JComponent[] campos, JButton botao) {
        JPanel grade = new JPanel(new GridLayout(0, 2, 6, 4));
        grade.setBackground(FACE);
        for (int i = 0; i < rotulos.length; i++) {
            grade.add(new JLabel(rotulos[i]));
            grade.add(campos[i]);
        }

        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(FACE);
        painel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        painel.add(grade, BorderLayout.NORTH);
        painel.add(botao, BorderLayout.SOUTH);
        return painel;
    }

    // Linha "Xmin [  ] Xmax [  ]" / "Ymin [  ] Ymax [  ]"
    private JPanel gradeMinMax(String a, JTextField aMin, JTextField aMax,
                               String b, JTextField bMin, JTextField bMax) {
        JPanel grade = new JPanel(new GridLayout(2, 4, 4, 4));
        grade.setBackground(FACE);
        grade.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        grade.add(new JLabel(a + "min:"));
        grade.add(aMin);
        grade.add(new JLabel(a + "max:"));
        grade.add(aMax);
        grade.add(new JLabel(b + "min:"));
        grade.add(bMin);
        grade.add(new JLabel(b + "max:"));
        grade.add(bMax);
        grade.setAlignmentX(LEFT_ALIGNMENT);
        return grade;
    }

    private JComponent alinharEsquerda(JComponent c) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }

    // ---------- Barra de título (igual ao Lab 1) ----------
    private JPanel criarBarraTitulo() {
        JPanel barra = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, TITULO_INICIO, getWidth(), 0, TITULO_FIM));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        barra.setPreferredSize(new Dimension(10, 24));

        JLabel titulo = new JLabel("  LAB3 CG - CoordinateX - Transformacoes Geometricas 2D");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        barra.add(titulo, BorderLayout.WEST);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 3));
        botoes.setOpaque(false);
        JButton minimizar = criarBotaoJanela("_");
        minimizar.addActionListener(e -> setExtendedState(Frame.ICONIFIED));
        JButton restaurar = criarBotaoJanela("□");
        restaurar.addActionListener(e -> alternarMaximizar());
        JButton fechar = criarBotaoJanela("X");
        fechar.addActionListener(e -> fechar());
        botoes.add(minimizar);
        botoes.add(restaurar);
        botoes.add(fechar);
        barra.add(botoes, BorderLayout.EAST);

        // Arrastar a janela pela barra de título
        barra.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                arrastarX = e.getX();
                arrastarY = e.getY();
            }
        });
        barra.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - arrastarX, p.y + e.getY() - arrastarY);
            }
        });
        return barra;
    }

    private JButton criarBotaoJanela(String texto) {
        JButton botao = new JButton(texto);
        botao.setPreferredSize(new Dimension(18, 16));
        botao.setFont(new Font("SansSerif", Font.BOLD, 10));
        botao.setFocusPainted(false);
        botao.setMargin(new Insets(0, 0, 0, 0));
        botao.setBackground(FACE);
        botao.setBorder(new BevelBorder(BevelBorder.RAISED));
        return botao;
    }

    private void alternarMaximizar() {
        if (limitesRestaurados == null) {
            limitesRestaurados = getBounds();
            setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        } else {
            setBounds(limitesRestaurados);
            limitesRestaurados = null;
        }
        revalidate();
    }

    // Volta para a tela inicial (se veio de lá) ou encerra o programa
    private void fechar() {
        if (voltarAoInicio == null) {
            System.exit(0);
        }
        dispose();
        voltarAoInicio.run();
    }

    // ---------- Menu ----------
    private JMenuBar criarBarraMenu() {
        JMenuBar barra = new JMenuBar();
        barra.setBackground(FACE);

        JMenu arquivo = new JMenu("Arquivo");
        JMenuItem limpar = new JMenuItem("Limpar tudo");
        limpar.addActionListener(e -> limparTudo());
        JMenuItem sair = new JMenuItem("Sair");
        sair.addActionListener(e -> System.exit(0));
        arquivo.add(limpar);
        if (voltarAoInicio != null) {
            JMenuItem inicio = new JMenuItem("Voltar ao inicio");
            inicio.addActionListener(e -> fechar());
            arquivo.add(inicio);
        }
        arquivo.add(sair);

        JMenu exibir = new JMenu("Exibir");
        menuMostrarOriginal = new JCheckBoxMenuItem("Mostrar polígono original (cinza)", true);
        menuMostrarOriginal.addActionListener(e -> atualizarTudo());
        menuMostrarGrade = new JCheckBoxMenuItem("Mostrar grade", true);
        menuMostrarGrade.addActionListener(e -> atualizarTudo());
        exibir.add(menuMostrarOriginal);
        exibir.add(menuMostrarGrade);

        JMenu ajuda = new JMenu("Ajuda");
        JMenuItem formulas = new JMenuItem("Matrizes (aula G5)");
        formulas.addActionListener(e -> mostrarFormulas());
        JMenuItem sobre = new JMenuItem("Sobre");
        sobre.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "CG-CoordinateX - Lab 3\n"
                        + "Transformações geométricas 2D (aula G5) em coordenadas homogêneas.\n"
                        + "Polígono na origem, desenhado com o algoritmo do ponto médio (aula G3).",
                "Sobre", JOptionPane.INFORMATION_MESSAGE));
        ajuda.add(formulas);
        ajuda.add(sobre);

        barra.add(arquivo);
        barra.add(exibir);
        barra.add(ajuda);
        return barra;
    }

    private void mostrarFormulas() {
        JTextArea texto = new JTextArea(
                "P' = M . P,  com P = (x, y, 1)\n\n"
                        + "Translação     T = [1 0 tx; 0 1 ty; 0 0 1]\n"
                        + "Escala         S = [sx 0 0; 0 sy 0; 0 0 1]\n"
                        + "Rotação        R = [cosθ -senθ 0; senθ cosθ 0; 0 0 1]\n"
                        + "Reflexão x       = [1 0 0; 0 -1 0; 0 0 1]\n"
                        + "Reflexão y       = [-1 0 0; 0 1 0; 0 0 1]\n"
                        + "Reflexão origem  = [-1 0 0; 0 -1 0; 0 0 1]\n"
                        + "Reflexão y = x   = [0 1 0; 1 0 0; 0 0 1]\n"
                        + "Reflexão y = -x  = [0 -1 0; -1 0 0; 0 0 1]\n"
                        + "Cisalhamento x   = [1 a 0; 0 1 0; 0 0 1]\n"
                        + "Cisalhamento y   = [1 0 0; b 1 0; 0 0 1]\n\n"
                        + "Ponto fixo (p):   C = T(p) . S ou R . T(-p)\n"
                        + "Composta:         M = Mn ... M2 . M1\n"
                        + "Window->Viewport: M = T(umin,vmin) . S(...) . T(-xmin,-ymin)");
        texto.setEditable(false);
        texto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        texto.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JOptionPane.showMessageDialog(this, texto, "Matrizes da aula G5", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- Barra de status (igual ao Lab 1) ----------
    private JPanel criarBarraStatus() {
        statusLabel = new JLabel(" Pronto.");
        statusLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(FACE);
        barra.add(statusLabel, BorderLayout.CENTER);

        JLabel participantes = new JLabel(
                " Grupo: Adrielly Carla | Maria Eduarda | Mirelle Casimiro | Nidlan Hendell | Rui Fernando ");
        participantes.setFont(new Font("SansSerif", Font.PLAIN, 10));
        participantes.setBorder(new BevelBorder(BevelBorder.LOWERED));
        barra.add(participantes, BorderLayout.SOUTH);
        return barra;
    }

    // Painel da coluna da esquerda: dentro do JScrollPane ele ocupa sempre
    // a largura visível (sem barra horizontal) e só rola na vertical.
    private static class ColunaLateral extends JPanel implements Scrollable {
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle r, int orientacao, int direcao) {
            return 16;
        }

        public int getScrollableBlockIncrement(Rectangle r, int orientacao, int direcao) {
            return 64;
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
