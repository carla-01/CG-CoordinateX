package frontend.labs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.dispositivo.Cores;
import core.dispositivo.FrameBuffer;
import core.dispositivo.PlanoCartesiano;
import core.dispositivo.Raster;
import core.modelo.Pixel;
import frontend.Navegacao;
import frontend.TelaLab;
import frontend.componentes.Componentes;
import frontend.componentes.PainelRaster;
import frontend.componentes.TelaCheia;
import lab2.reta.AlgoritmoReta;
import lab2.reta.RetaPontoMedio;
import lab3.desenho.DesenhoPoligono;
import lab3.modelo.Matriz3;
import lab3.modelo.Poligono;
import lab3.modelo.Vertice;
import lab3.transformacao.SequenciaTransformacoes;
import lab3.transformacao.Transformacoes;

// Lab 3: transformações geométricas 2D (aula G5) aplicadas a um polígono.
// Continuação do Lab 2: a mesma tela do usuário (display preto, sem grade,
// plano cartesiano opcional) e o mesmo desenho de pixels.
//
// As coordenadas do Lab 3 têm SEMPRE a origem (0, 0) no centro da tela e o y
// para cima, porque as transformações (rotação, escala...) são em torno da
// origem. A caixa "Plano cartesiano" só mostra ou esconde os eixos e octantes.
//
// Os lados são desenhados com o algoritmo do Ponto Médio (aula G3, do Lab 2).
// Cada aba é uma ferramenta:
//
//   Pixel        - liga/desliga pixels, ou desenha um polígono, um retângulo ou
//                  um polígono regular puxando com o mouse
//   Polígono     - regular (n lados), irregular pronto ou vértices digitados
//   Translação   - T(tx, ty)
//   Escala       - S(sx, sy), com ponto fixo opcional
//   Rotação      - R(θ), com ponto fixo opcional
//   Reflexão     - eixo x, eixo y, origem, y = x, y = -x ou reta pela origem
//   Cisalhamento - em x ou em y
//   Janela       - janela do usuário (mundo) -> viewport, com os contornos na tela
//   Composta     - sequência escrita como texto, ex.: T(-2,-1) E(2,2) T(2,1)
//
// As transformações se ACUMULAM: o polígono mostrado é sempre M · P, onde P é o
// polígono original e M a matriz acumulada (a mais recente multiplica pela
// esquerda). Na tela fica só o polígono transformado (a forma antiga é apagada).
// O vértice 1 é marcado com um contorno para mostrar o giro e o espelhamento.
public class TelaLab3 extends TelaLab {

    private static final int ABA_PIXEL = 0;

    private static final int MODO_PIXEL = 0;
    private static final int MODO_POLIGONO = 1;
    private static final int MODO_RETANGULO = 2;
    private static final int MODO_REGULAR = 3;

    private static final int ZOOM_VISUALIZACAO = 8;
    private static final float ESCALA_FONTE = 1.25f; // letras 25% maiores nesta tela
    private static final int ALTURA_ABAS = 172;      // TelaLab usa 150
    private static final int LARGURA_LOG = 260;      // TelaLab usa 320 (sobra espaço para as abas)
    private static final int COR_FUNDO = Cores.PRETO;
    private static final int COR_ATUAL = Cores.VERDE; // 00FF00
    private static final int COR_PREVIA = 0x009900; // lado sendo puxado
    private static final int COR_JANELA = Cores.AMARELO;
    private static final int COR_VIEWPORT = Cores.CIANO;

    private static final String TIPO_REGULAR = "Regular (n lados)";
    private static final String TIPO_IRREGULAR = "Irregular (pronto)";
    private static final String TIPO_DIGITADO = "Irregular (digitar vértices)";

    private static final String[] MODOS = { "Pixel (liga/desliga)", "Polígono (clique nos vértices)",
            "Retângulo (puxe de um canto ao outro)", "Polígono regular (puxe do centro à borda)" };
    private static final String[] AJUDAS = { "Clique na tela para ligar ou desligar um pixel (setPixel).",
            "Clique em cada vértice; o lado acompanha o mouse. Feche clicando no vértice 1 ou em Fechar.",
            "Clique num canto, puxe com o mouse e clique no canto oposto.",
            "Clique no centro, puxe até a borda (define raio e vértice 1) e clique de novo." };

    private static final String[] REFLEXOES = { "Eixo x", "Eixo y", "Origem", "Reta y = x", "Reta y = -x",
            "Reta pela origem (ângulo θ)" };

    private static final String[] EXEMPLOS_TEXTO = { "(escolha um exemplo)",
            "Escala por 2 com ponto fixo (2,1)  [slide 24]",
            "Rotação de 90° em torno de (2,1)  [slide 26]",
            "Rotação 45° e depois escala (2,1)  (não comuta)",
            "Escala (2,1) e depois rotação 45°  (não comuta)" };
    private static final String[] EXEMPLOS_SEQUENCIA = { "", "T(-2,-1) E(2,2) T(2,1)", "T(-2,-1) R(90) T(2,1)",
            "R(45) E(2,1)", "E(2,1) R(45)" };

    private final DesenhoPoligono desenho = new DesenhoPoligono();
    private final AlgoritmoReta reta = new RetaPontoMedio();

    private int ferramenta = ABA_PIXEL;

    private JTextField campoNdh;
    private JTextField campoNdv;
    private JCheckBox caixaPlano;

    // aba Pixel
    private JComboBox<String> comboModo;
    private JTextField campoLadosDesenho;
    private JLabel textoAjuda;

    private JComboBox<String> comboTipo;
    private JTextField campoLados;
    private JTextField campoRaio;
    private JTextField campoVertices;

    private JTextField campoTx;
    private JTextField campoTy;

    private JTextField campoSx;
    private JTextField campoSy;
    private JTextField campoEscalaPx;
    private JTextField campoEscalaPy;

    private JTextField campoAngulo;
    private JTextField campoRotacaoPx;
    private JTextField campoRotacaoPy;

    private JComboBox<String> comboReflexao;
    private JTextField campoThetaReflexao;

    private JComboBox<String> comboCisalhamento;
    private JTextField campoFatorCisalhamento;

    // janela do usuário (mundo) e viewport
    private JTextField campoXmin;
    private JTextField campoXmax;
    private JTextField campoYmin;
    private JTextField campoYmax;
    private JTextField campoUmin;
    private JTextField campoUmax;
    private JTextField campoVmin;
    private JTextField campoVmax;
    private JCheckBox caixaJanela;
    private Poligono contornoJanela;
    private Poligono contornoViewport;

    private JTextField campoSequencia;

    private PainelRaster painel;
    private FrameBuffer frameBuffer;

    // pixels ligados à mão (aba Pixel), no plano cartesiano
    private final List<Pixel> pixelsLivres = new ArrayList<Pixel>();

    // desenho com o mouse em andamento: pontos já clicados e posição do mouse
    private final List<Pixel> pontosDesenho = new ArrayList<Pixel>();
    private Pixel previa;

    // null depois de "Limpar", até criar ou desenhar outro polígono
    private Poligono original;
    private Poligono atual;
    private Matriz3 acumulada = Matriz3.identidade();
    private final List<Matriz3> historico = new ArrayList<Matriz3>();

    public TelaLab3(Navegacao navegacao) {
        super(navegacao, "Lab 3 - Transformações Geométricas",
                "Translação, escala, rotação, reflexão, cisalhamento e transformações compostas\n"
                        + "aplicadas a um polígono (coordenadas homogêneas, matrizes 3 x 3)\n"
                        + "Lados desenhados com o algoritmo do Ponto Médio.",
                "Tela preta ao iniciar. Ative o plano cartesiano quando quiser exibir os eixos.");

        painel = new PainelRaster();
        painel.setMostrarGrade(false);
        painel.setPlanoCartesiano(false);
        painel.setOuvinte(new PainelRaster.Ouvinte() {
            public void mouseSobre(Pixel pixel) {
                mostrarMouse(pixel);
            }

            public void clique(Pixel pixel) {
                tratarClique(pixel);
            }
        });

        final JTabbedPane abas = criarAbas();
        abas.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        abas.addTab("Pixel", criarAbaPixel());
        abas.addTab("Polígono", criarAbaPoligono());
        abas.addTab("Translação", criarAbaTranslacao());
        abas.addTab("Escala", criarAbaEscala());
        abas.addTab("Rotação", criarAbaRotacao());
        abas.addTab("Reflexão", criarAbaReflexao());
        abas.addTab("Cisalhamento", criarAbaCisalhamento());
        abas.addTab("Janela", criarAbaJanela());
        abas.addTab("Composta", criarAbaComposta());
        abas.setSelectedIndex(ABA_PIXEL);
        abas.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ferramenta = abas.getSelectedIndex();
                cancelarDesenho();
            }
        });

        JComponent areaDesenho = painel.criarRolagem();
        montar(criarBarraOpcoes(), areaDesenho, abas);
        aumentarTela(abas, areaDesenho);

        criarFrameBuffer(64, 48);
        painel.setZoom(ZOOM_VISUALIZACAO);

        original = Poligono.irregularPadrao().centralizadoNaOrigem();
        atual = original;
        redesenhar();
        logPoligono("Polígono inicial (irregular, centrado na origem)", original);
    }

    // ===== Montagem da tela =====

    // Mesma barra do Lab 2, mais os botões do Lab 3
    private JPanel criarBarraOpcoes() {
        campoNdh = Componentes.campo("64", 4);
        campoNdv = Componentes.campo("48", 4);
        JButton aplicar = Componentes.botao("Aplicar");
        aoClicar(aplicar, this::aplicarResolucao);

        JButton limpar = Componentes.botao("Limpar");
        aoClicar(limpar, this::limpar);

        caixaPlano = new JCheckBox("Plano cartesiano", false);
        caixaPlano.setOpaque(false);
        aoClicar(caixaPlano, this::trocarPlano);

        JButton telaCheia = Componentes.botao("Tela cheia");
        aoClicar(telaCheia, new Runnable() {
            public void run() {
                TelaCheia.mostrar(frameBuffer);
            }
        });

        JButton desfazer = Componentes.botao("Desfazer");
        aoClicar(desfazer, this::desfazer);

        JButton resetar = Componentes.botao("Voltar ao original");
        aoClicar(resetar, this::resetar);

        JPanel barra = Componentes.linhaEntrada();
        barra.add(Componentes.rotulo("ndh"));
        barra.add(campoNdh);
        barra.add(Componentes.rotulo("ndv"));
        barra.add(campoNdv);
        barra.add(aplicar);
        barra.add(limpar);
        barra.add(caixaPlano);
        barra.add(telaCheia);
        barra.add(desfazer);
        barra.add(resetar);
        return barra;
    }

    private JPanel criarAbaPixel() {
        comboModo = new JComboBox<String>();
        for (int i = 0; i < MODOS.length; i++) {
            comboModo.addItem(MODOS[i]);
        }
        comboModo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textoAjuda.setText(AJUDAS[comboModo.getSelectedIndex()]);
                cancelarDesenho();
            }
        });
        campoLadosDesenho = Componentes.campo("5", 3);

        JButton fechar = Componentes.botao("Fechar polígono");
        aoClicar(fechar, this::fecharPoligono);
        JButton cancelar = Componentes.botao("Cancelar");
        aoClicar(cancelar, this::cancelarDesenho);

        textoAjuda = Componentes.rotulo(AJUDAS[MODO_PIXEL]);
        JPanel ajuda = Componentes.linhaEntrada();
        ajuda.add(textoAjuda);

        return Componentes.painelAba(new JComponent[] { linha("Ferramenta", comboModo),
                linha("lados (regular)", campoLadosDesenho, fechar, cancelar), ajuda,
                linhaTexto("O desenho vira o polígono das transformações (fica onde foi desenhado).") });
    }

    private JPanel criarAbaPoligono() {
        comboTipo = new JComboBox<String>();
        comboTipo.addItem(TIPO_REGULAR);
        comboTipo.addItem(TIPO_IRREGULAR);
        comboTipo.addItem(TIPO_DIGITADO);
        comboTipo.setSelectedIndex(1);

        campoLados = Componentes.campo("5", 3);
        campoRaio = Componentes.campo("10", 3);
        campoVertices = Componentes.campo("-8,-7; 6,-8; 9,2; 1,9; -8,4", 28);

        JButton criar = Componentes.botao("Criar polígono");
        aoClicar(criar, this::criarPoligono);

        return Componentes.painelAba(new JComponent[] { linha("Tipo", comboTipo, "lados", campoLados, "raio", campoRaio),
                linha("Vértices (x,y; x,y; ...)", campoVertices, criar),
                linhaTexto("O polígono é centrado na origem (a média dos vértices vai para (0,0)).") });
    }

    private JPanel criarAbaTranslacao() {
        campoTx = Componentes.campo("10", 4);
        campoTy = Componentes.campo("5", 4);
        JButton aplicar = Componentes.botao("Aplicar");
        aoClicar(aplicar, this::aplicarTranslacao);

        return Componentes.painelAba(new JComponent[] { linha("tx", campoTx, "ty", campoTy, aplicar),
                linhaTexto("x' = x + tx    y' = y + ty") });
    }

    private JPanel criarAbaEscala() {
        campoSx = Componentes.campo("2", 4);
        campoSy = Componentes.campo("2", 4);
        campoEscalaPx = Componentes.campo("0", 4);
        campoEscalaPy = Componentes.campo("0", 4);
        JButton aplicar = Componentes.botao("Aplicar");
        aoClicar(aplicar, this::aplicarEscala);

        return Componentes.painelAba(new JComponent[] { linha("sx", campoSx, "sy", campoSy),
                linha("Ponto fixo:  px", campoEscalaPx, "py", campoEscalaPy, aplicar),
                linhaTexto("Ponto fixo (0, 0) = escala básica. Outro ponto = T(p) · S · T(-p).") });
    }

    private JPanel criarAbaRotacao() {
        campoAngulo = Componentes.campo("45", 4);
        campoRotacaoPx = Componentes.campo("0", 4);
        campoRotacaoPy = Componentes.campo("0", 4);
        JButton aplicar = Componentes.botao("Aplicar");
        aoClicar(aplicar, this::aplicarRotacao);

        return Componentes.painelAba(new JComponent[] { linha("Ângulo θ (graus)", campoAngulo),
                linha("Ponto fixo:  px", campoRotacaoPx, "py", campoRotacaoPy, aplicar),
                linhaTexto("Positivo = anti-horário. Ponto fixo (0, 0) = rotação em torno da origem.") });
    }

    private JPanel criarAbaReflexao() {
        comboReflexao = new JComboBox<String>();
        for (int i = 0; i < REFLEXOES.length; i++) {
            comboReflexao.addItem(REFLEXOES[i]);
        }
        campoThetaReflexao = Componentes.campo("30", 4);
        JButton aplicar = Componentes.botao("Aplicar");
        aoClicar(aplicar, this::aplicarReflexao);

        return Componentes.painelAba(new JComponent[] { linha("Refletir em", comboReflexao, "θ (graus)", campoThetaReflexao, aplicar),
                linhaTexto("O ângulo θ só vale para \"Reta pela origem\".") });
    }

    private JPanel criarAbaCisalhamento() {
        comboCisalhamento = new JComboBox<String>();
        comboCisalhamento.addItem("Em x:  x' = x + a·y");
        comboCisalhamento.addItem("Em y:  y' = y + b·x");
        campoFatorCisalhamento = Componentes.campo("0.5", 4);
        JButton aplicar = Componentes.botao("Aplicar");
        aoClicar(aplicar, this::aplicarCisalhamento);

        return Componentes.painelAba(new JComponent[] { linha("Cisalhamento", comboCisalhamento, "fator (a ou b)", campoFatorCisalhamento, aplicar),
                linhaTexto("A área do polígono não muda (determinante = 1).") });
    }

    private JPanel criarAbaJanela() {
        campoXmin = Componentes.campo("-10", 4);
        campoXmax = Componentes.campo("10", 4);
        campoYmin = Componentes.campo("-10", 4);
        campoYmax = Componentes.campo("10", 4);
        campoUmin = Componentes.campo("4", 4);
        campoUmax = Componentes.campo("28", 4);
        campoVmin = Componentes.campo("2", 4);
        campoVmax = Componentes.campo("20", 4);

        JButton aplicar = Componentes.botao("Aplicar");
        aoClicar(aplicar, this::aplicarJanela);

        caixaJanela = new JCheckBox("Mostrar janela (amarelo) e viewport (ciano)", false);
        caixaJanela.setOpaque(false);
        aoClicar(caixaJanela, this::alternarJanela);

        return Componentes.painelAba(new JComponent[] {
                linha("Janela do usuário:  xmin", campoXmin, "xmax", campoXmax, "ymin", campoYmin, "ymax", campoYmax),
                linha("Viewport:  umin", campoUmin, "umax", campoUmax, "vmin", campoVmin, "vmax", campoVmax, aplicar),
                linha(caixaJanela),
                linhaTexto("M = T(umin, vmin) · S((umax-umin)/(xmax-xmin), (vmax-vmin)/(ymax-ymin)) · T(-xmin, -ymin)") });
    }

    private JPanel criarAbaComposta() {
        campoSequencia = Componentes.campo("T(-2,-1) E(2,2) T(2,1)", 30);
        final JComboBox<String> exemplos = new JComboBox<String>();
        for (int i = 0; i < EXEMPLOS_TEXTO.length; i++) {
            exemplos.addItem(EXEMPLOS_TEXTO[i]);
        }
        exemplos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = exemplos.getSelectedIndex();
                if (i > 0) {
                    campoSequencia.setText(EXEMPLOS_SEQUENCIA[i]);
                }
            }
        });

        JButton aplicar = Componentes.botao("Aplicar sequência");
        aoClicar(aplicar, this::aplicarSequencia);
        JButton comutatividade = Componentes.botao("Testar comutatividade");
        aoClicar(comutatividade, this::testarComutatividade);

        // 4 linhas: a altura das abas (TelaLab) não mostra uma 5ª
        return Componentes.painelAba(new JComponent[] { linha("Exemplo", exemplos, comutatividade),
                linha("Sequência", campoSequencia, aplicar),
                linhaTexto("T(tx,ty) E(sx,sy) R(graus) EP(sx,sy,px,py) RP(graus,px,py) CisX(a) CisY(b)   (esq. → dir.)"),
                linhaTexto("RefX RefY RefO RefYX RefYMX RefR(graus) JV(xmin,xmax,ymin,ymax,umin,umax,vmin,vmax)") });
    }

    // Texto vira rótulo; componente entra como está
    private JPanel linha(Object... itens) {
        JPanel linha = Componentes.linhaEntrada();
        for (int i = 0; i < itens.length; i++) {
            if (itens[i] instanceof String) {
                linha.add(Componentes.rotulo((String) itens[i]));
            } else {
                linha.add((Component) itens[i]);
            }
        }
        return linha;
    }

    private JPanel linhaTexto(String texto) {
        JPanel linha = Componentes.linhaEntrada();
        linha.add(Componentes.rotulo(texto));
        return linha;
    }

    // Só nesta tela: letras e botões maiores (o Estilo dos outros labs não muda).
    // A área de desenho fica igual. As abas ganham altura para caber as linhas.
    private void aumentarTela(JTabbedPane abas, JComponent areaDesenho) {
        List<Component> todos = new ArrayList<Component>();
        juntarComponentes(this, areaDesenho, todos);

        // guarda as fontes antes de mudar, para não aumentar duas vezes
        // um componente que herda a fonte do pai
        List<Font> fontes = new ArrayList<Font>();
        for (int i = 0; i < todos.size(); i++) {
            fontes.add(todos.get(i).getFont());
        }
        for (int i = 0; i < todos.size(); i++) {
            Component c = todos.get(i);
            Font f = fontes.get(i);
            if (f != null) {
                c.setFont(f.deriveFont(f.getSize2D() * ESCALA_FONTE));
            }
            if (c instanceof JButton) {
                JButton botao = (JButton) c;
                if (botao.getText() != null && botao.getText().isEmpty() == false) {
                    botao.setBorder(BorderFactory.createCompoundBorder(botao.getBorder(),
                            BorderFactory.createEmptyBorder(1, 6, 1, 6)));
                }
            }
        }

        // parte de baixo (abas + log), montada pela TelaLab
        Container baixo = abas.getParent();
        baixo.setPreferredSize(new Dimension(10, ALTURA_ABAS));
        if (baixo.getLayout() instanceof BorderLayout) {
            Component log = ((BorderLayout) baixo.getLayout()).getLayoutComponent(BorderLayout.EAST);
            if (log != null) {
                log.setPreferredSize(new Dimension(LARGURA_LOG, 10));
            }
        }
    }

    private static void juntarComponentes(Component c, JComponent pular, List<Component> todos) {
        if (c == pular) {
            return;
        }
        todos.add(c);
        if (c instanceof JMenu) {
            Component[] itens = ((JMenu) c).getMenuComponents();
            for (int i = 0; i < itens.length; i++) {
                juntarComponentes(itens[i], pular, todos);
            }
        }
        if (c instanceof Container) {
            Component[] filhos = ((Container) c).getComponents();
            for (int i = 0; i < filhos.length; i++) {
                juntarComponentes(filhos[i], pular, todos);
            }
        }
    }

    private static void aoClicar(javax.swing.AbstractButton botao, final Runnable acao) {
        botao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acao.run();
            }
        });
    }

    // ===== Dispositivo =====

    private void aplicarResolucao() {
        int ndh;
        int ndv;
        try {
            ndh = Componentes.lerInteiro(campoNdh);
            ndv = Componentes.lerInteiro(campoNdv);
        } catch (NumberFormatException erro) {
            mostrarErro("Resolucao invalida", "ndh e ndv devem ser numeros inteiros.");
            return;
        }
        if (ndh < 2 || ndv < 2 || ndh > 4096 || ndv > 4096) {
            mostrarErro("Resolucao invalida", "ndh e ndv devem estar entre 2 e 4096.");
            return;
        }
        criarFrameBuffer(ndh, ndv);
        redesenhar();
    }

    private void criarFrameBuffer(int ndh, int ndv) {
        frameBuffer = new FrameBuffer(ndh, ndv, COR_FUNDO);
        painel.setFrameBuffer(frameBuffer);
        pontosDesenho.clear();
        previa = null;

        String texto = "Dispositivo de " + ndh + " x " + ndv + ":  " + (-(ndh / 2)) + " <= x <= "
                + (ndh - 1 - ndh / 2) + "  e  " + (-(ndv - 1 - ndv / 2)) + " <= y <= " + (ndv / 2)
                + "  (origem no centro)";
        log("--- " + texto);
        status.setMensagem(texto);
    }

    // Apaga tudo, como o Limpar do Lab 2 (inclusive o polígono)
    private void limpar() {
        pixelsLivres.clear();
        pontosDesenho.clear();
        previa = null;
        original = null;
        atual = null;
        acumulada = Matriz3.identidade();
        historico.clear();
        caixaJanela.setSelected(false);
        redesenhar();
        log("--- Tela limpa");
        status.setMensagem("Tela limpa. Desenhe na aba Pixel ou crie um polígono na aba Polígono.");
    }

    // ===== Sistema de coordenadas =====

    // Origem sempre no centro (com ou sem os eixos na tela)
    private PlanoCartesiano plano() {
        return new PlanoCartesiano(frameBuffer.getNdh(), frameBuffer.getNdv());
    }

    // Só mostra ou esconde os eixos e octantes
    private void trocarPlano() {
        painel.setPlanoCartesiano(caixaPlano.isSelected());
        String texto;
        if (caixaPlano.isSelected()) {
            texto = "Plano cartesiano: eixos x e y e octantes na tela";
        } else {
            texto = "Plano cartesiano escondido (a origem continua no centro da tela)";
        }
        log("--- " + texto);
        status.setMensagem(texto);
    }

    // ===== Mouse =====

    private void mostrarMouse(Pixel pixel) {
        if (pixel == null) {
            status.setCoordenadas("");
            if (previa != null) {
                previa = null;
                redesenhar();
            }
            return;
        }
        Pixel ponto = plano().paraCartesiano(pixel);
        status.setCoordenadas("x = " + ponto.getX() + "  y = " + ponto.getY() + "   (dispositivo " + pixel + ")");
        if (ferramenta != ABA_PIXEL) {
            return;
        }

        int modo = comboModo.getSelectedIndex();
        if (modo == MODO_PIXEL) {
            String estado;
            if (frameBuffer.estaAceso(pixel.getX(), pixel.getY())) {
                estado = "aceso";
            } else {
                estado = "apagado";
            }
            status.setMensagem("Pixel " + ponto + " " + estado + ". Clique para ligar/desligar.");
            return;
        }

        if (pontosDesenho.isEmpty()) {
            if (modo == MODO_POLIGONO) {
                status.setMensagem("Clique no vértice 1 do polígono.");
            } else if (modo == MODO_RETANGULO) {
                status.setMensagem("Clique no primeiro canto do retângulo.");
            } else {
                status.setMensagem("Clique no centro do polígono regular.");
            }
            return;
        }

        // puxando: a forma acompanha o mouse
        previa = ponto;
        redesenhar();
        Pixel inicio = pontosDesenho.get(0);
        if (modo == MODO_POLIGONO) {
            status.setMensagem("Vértice " + (pontosDesenho.size() + 1) + " = " + ponto
                    + "   (para fechar, clique no vértice 1 " + inicio + " ou em Fechar polígono)");
        } else if (modo == MODO_RETANGULO) {
            status.setMensagem("Canto 1 = " + inicio + "   canto 2 = " + ponto + "   largura "
                    + Math.abs(ponto.getX() - inicio.getX()) + "  altura " + Math.abs(ponto.getY() - inicio.getY())
                    + "   (clique para terminar)");
        } else {
            status.setMensagem("Centro = " + inicio + "   raio = " + fmt(distancia(inicio, ponto))
                    + "   (clique para terminar)");
        }
    }

    private void tratarClique(Pixel pixel) {
        if (ferramenta != ABA_PIXEL) {
            return;
        }
        Pixel ponto = plano().paraCartesiano(pixel);
        int modo = comboModo.getSelectedIndex();

        if (modo == MODO_PIXEL) {
            alternarPixel(ponto);
        } else if (pontosDesenho.isEmpty()) {
            pontosDesenho.add(ponto);
            redesenhar();
        } else if (modo == MODO_POLIGONO) {
            Pixel ultimo = pontosDesenho.get(pontosDesenho.size() - 1);
            if (pontosDesenho.size() >= 3 && iguais(ponto, pontosDesenho.get(0))) {
                fecharPoligono();
                return;
            }
            if (iguais(ponto, ultimo) == false) {
                pontosDesenho.add(ponto);
                redesenhar();
            }
        } else if (modo == MODO_RETANGULO) {
            terminarRetangulo(pontosDesenho.get(0), ponto);
            return;
        } else {
            terminarRegular(pontosDesenho.get(0), ponto);
            return;
        }
        mostrarMouse(pixel);
    }

    // ===== Desenho com o mouse (aba Pixel) =====

    private void alternarPixel(Pixel ponto) {
        String acao = "ligado";
        for (int i = 0; i < pixelsLivres.size(); i++) {
            if (iguais(pixelsLivres.get(i), ponto)) {
                pixelsLivres.remove(i);
                acao = "desligado";
                break;
            }
        }
        if (acao.equals("ligado")) {
            pixelsLivres.add(ponto);
        }
        log("setPixel" + ponto + " " + acao);
        redesenhar();
    }

    private void fecharPoligono() {
        if (comboModo.getSelectedIndex() != MODO_POLIGONO || pontosDesenho.size() < 3) {
            mostrarErro("Polígono incompleto", "Escolha a ferramenta Polígono e clique em pelo menos 3 vértices.");
            return;
        }
        List<Vertice> lista = new ArrayList<Vertice>();
        for (int i = 0; i < pontosDesenho.size(); i++) {
            lista.add(new Vertice(pontosDesenho.get(i).getX(), pontosDesenho.get(i).getY()));
        }
        usarDesenho(new Poligono(lista), "Polígono desenhado com " + lista.size() + " vértices");
    }

    private void terminarRetangulo(Pixel a, Pixel b) {
        if (a.getX() == b.getX() || a.getY() == b.getY()) {
            status.setMensagem("Puxe até um canto com x e y diferentes do primeiro.");
            return;
        }
        Poligono p = Poligono.retangulo(Math.min(a.getX(), b.getX()), Math.max(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()), Math.max(a.getY(), b.getY()));
        usarDesenho(p, "Retângulo de " + a + " a " + b);
    }

    private void terminarRegular(Pixel centro, Pixel borda) {
        int lados;
        try {
            lados = Componentes.lerInteiro(campoLadosDesenho);
        } catch (NumberFormatException erro) {
            mostrarErro("Lados inválidos", "O número de lados deve ser um inteiro (3 ou mais).");
            return;
        }
        if (lados < 3) {
            mostrarErro("Lados inválidos", "Um polígono precisa de pelo menos 3 lados.");
            return;
        }
        if (distancia(centro, borda) < 1) {
            status.setMensagem("Puxe para longe do centro para definir o raio.");
            return;
        }
        usarDesenho(regularPuxado(centro, borda, lados), "Polígono regular de " + lados + " lados, centro "
                + centro + ", raio " + fmt(distancia(centro, borda)));
    }

    // Regular com centro c e vértice 1 em p: o regular padrão (vértice 1 em
    // 90°) é girado até o ângulo de p e transladado até c
    private Poligono regularPuxado(Pixel c, Pixel p, int lados) {
        double dx = p.getX() - c.getX();
        double dy = p.getY() - c.getY();
        double graus = Math.toDegrees(Math.atan2(dy, dx));
        return Poligono.regular(lados, Math.hypot(dx, dy)).transformar(
                Transformacoes.compor(Transformacoes.rotacao(graus - 90), Transformacoes.translacao(c.getX(), c.getY())));
    }

    // O desenho vira o polígono das transformações (fica onde foi desenhado)
    private void usarDesenho(Poligono p, String nome) {
        pontosDesenho.clear();
        previa = null;
        original = p;
        atual = p;
        acumulada = Matriz3.identidade();
        historico.clear();
        redesenhar();
        logPoligono(nome, p);
        status.setMensagem(nome + ". Agora use as abas para transformar.");
    }

    private void cancelarDesenho() {
        boolean tinha = pontosDesenho.isEmpty() == false || previa != null;
        pontosDesenho.clear();
        previa = null;
        if (tinha && frameBuffer != null) {
            redesenhar();
        }
    }

    private static boolean iguais(Pixel a, Pixel b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }

    private static double distancia(Pixel a, Pixel b) {
        return Math.hypot(b.getX() - a.getX(), b.getY() - a.getY());
    }

    // ===== Polígono =====

    private void criarPoligono() {
        try {
            String tipo = (String) comboTipo.getSelectedItem();
            Poligono novo;
            String nome;
            if (TIPO_REGULAR.equals(tipo)) {
                int lados = (int) lerReal(campoLados, "lados");
                novo = Poligono.regular(lados, lerReal(campoRaio, "raio"));
                nome = "Polígono regular de " + lados + " lados";
            } else if (TIPO_IRREGULAR.equals(tipo)) {
                novo = Poligono.irregularPadrao();
                nome = "Polígono irregular (pronto)";
            } else {
                novo = lerVertices(campoVertices.getText());
                nome = "Polígono irregular digitado";
            }
            original = novo.centralizadoNaOrigem();
            atual = original;
            acumulada = Matriz3.identidade();
            historico.clear();
            redesenhar();
            logPoligono(nome + " (centrado na origem)", original);
            status.setMensagem(nome + ": " + original.getQuantidade() + " vértices, centro em (0, 0).");
        } catch (IllegalArgumentException erro) {
            mostrarErro("Polígono inválido", erro.getMessage());
        }
    }

    // "x,y; x,y; ..."  (ponto decimal)
    private Poligono lerVertices(String texto) {
        String[] partes = texto.trim().split(";");
        List<Vertice> lista = new ArrayList<Vertice>();
        for (int i = 0; i < partes.length; i++) {
            String p = partes[i].trim();
            if (p.isEmpty()) {
                continue;
            }
            String[] xy = p.replace(',', ' ').trim().split("\\s+");
            if (xy.length != 2) {
                throw new IllegalArgumentException("Vértice " + (lista.size() + 1) + " deve ser \"x,y\": " + p);
            }
            try {
                lista.add(new Vertice(Double.parseDouble(xy[0]), Double.parseDouble(xy[1])));
            } catch (NumberFormatException erro) {
                throw new IllegalArgumentException("Vértice " + (lista.size() + 1) + " inválido: " + p
                        + " (use ponto decimal)");
            }
        }
        if (lista.size() < 3) {
            throw new IllegalArgumentException("Digite pelo menos 3 vértices.");
        }
        return new Poligono(lista);
    }

    // ===== Transformações =====

    private boolean temPoligono() {
        if (atual == null) {
            mostrarErro("Sem polígono", "Desenhe um polígono na aba Pixel ou crie um na aba Polígono.");
            return false;
        }
        return true;
    }

    private void aplicarTranslacao() {
        try {
            double tx = lerReal(campoTx, "tx");
            double ty = lerReal(campoTy, "ty");
            aplicar("Translação T(" + fmt(tx) + ", " + fmt(ty) + ")", Transformacoes.translacao(tx, ty));
        } catch (IllegalArgumentException erro) {
            mostrarErro("Valor inválido", erro.getMessage());
        }
    }

    private void aplicarEscala() {
        try {
            double sx = lerReal(campoSx, "sx");
            double sy = lerReal(campoSy, "sy");
            double px = lerReal(campoEscalaPx, "px");
            double py = lerReal(campoEscalaPy, "py");
            String nome = "Escala S(" + fmt(sx) + ", " + fmt(sy) + ")";
            if (px == 0 && py == 0) {
                aplicar(nome, Transformacoes.escala(sx, sy));
            } else {
                aplicar(nome + " com ponto fixo (" + fmt(px) + ", " + fmt(py) + ")",
                        Transformacoes.escalaComPontoFixo(sx, sy, px, py));
            }
        } catch (IllegalArgumentException erro) {
            mostrarErro("Valor inválido", erro.getMessage());
        }
    }

    private void aplicarRotacao() {
        try {
            double graus = lerReal(campoAngulo, "ângulo");
            double px = lerReal(campoRotacaoPx, "px");
            double py = lerReal(campoRotacaoPy, "py");
            String nome = "Rotação R(" + fmt(graus) + "°)";
            if (px == 0 && py == 0) {
                aplicar(nome, Transformacoes.rotacao(graus));
            } else {
                aplicar(nome + " em torno de (" + fmt(px) + ", " + fmt(py) + ")",
                        Transformacoes.rotacaoComPontoFixo(graus, px, py));
            }
        } catch (IllegalArgumentException erro) {
            mostrarErro("Valor inválido", erro.getMessage());
        }
    }

    private void aplicarReflexao() {
        try {
            int i = comboReflexao.getSelectedIndex();
            Matriz3 m;
            String nome = "Reflexão em " + REFLEXOES[i].toLowerCase();
            switch (i) {
            case 0:
                m = Transformacoes.reflexaoEixoX();
                break;
            case 1:
                m = Transformacoes.reflexaoEixoY();
                break;
            case 2:
                m = Transformacoes.reflexaoOrigem();
                break;
            case 3:
                m = Transformacoes.reflexaoRetaYigualX();
                break;
            case 4:
                m = Transformacoes.reflexaoRetaYigualMenosX();
                break;
            default:
                double graus = lerReal(campoThetaReflexao, "θ");
                m = Transformacoes.reflexaoEmReta(graus);
                nome = "Reflexão na reta pela origem com θ = " + fmt(graus) + "°";
                break;
            }
            aplicar(nome, m);
        } catch (IllegalArgumentException erro) {
            mostrarErro("Valor inválido", erro.getMessage());
        }
    }

    private void aplicarCisalhamento() {
        try {
            double fator = lerReal(campoFatorCisalhamento, "fator");
            if (comboCisalhamento.getSelectedIndex() == 0) {
                aplicar("Cisalhamento em x, a = " + fmt(fator), Transformacoes.cisalhamentoX(fator));
            } else {
                aplicar("Cisalhamento em y, b = " + fmt(fator), Transformacoes.cisalhamentoY(fator));
            }
        } catch (IllegalArgumentException erro) {
            mostrarErro("Valor inválido", erro.getMessage());
        }
    }

    // Lê a janela do usuário e a viewport e guarda os dois contornos
    private void lerJanela() {
        double xmin = lerReal(campoXmin, "xmin");
        double xmax = lerReal(campoXmax, "xmax");
        double ymin = lerReal(campoYmin, "ymin");
        double ymax = lerReal(campoYmax, "ymax");
        double umin = lerReal(campoUmin, "umin");
        double umax = lerReal(campoUmax, "umax");
        double vmin = lerReal(campoVmin, "vmin");
        double vmax = lerReal(campoVmax, "vmax");
        try {
            contornoJanela = Poligono.retangulo(xmin, xmax, ymin, ymax);
        } catch (IllegalArgumentException erro) {
            throw new IllegalArgumentException("Janela do usuário: precisa de xmin < xmax e ymin < ymax.");
        }
        try {
            contornoViewport = Poligono.retangulo(umin, umax, vmin, vmax);
        } catch (IllegalArgumentException erro) {
            throw new IllegalArgumentException("Viewport: precisa de umin < umax e vmin < vmax.");
        }
    }

    // Leva o que está na janela do usuário para a viewport
    private void aplicarJanela() {
        try {
            lerJanela();
            Vertice j0 = contornoJanela.get(0);
            Vertice j2 = contornoJanela.get(2);
            Vertice v0 = contornoViewport.get(0);
            Vertice v2 = contornoViewport.get(2);
            caixaJanela.setSelected(true);
            aplicar("Janela [" + fmt(j0.getX()) + ", " + fmt(j2.getX()) + "] x [" + fmt(j0.getY()) + ", "
                    + fmt(j2.getY()) + "]  ->  viewport [" + fmt(v0.getX()) + ", " + fmt(v2.getX()) + "] x ["
                    + fmt(v0.getY()) + ", " + fmt(v2.getY()) + "]",
                    Transformacoes.janelaViewport(j0.getX(), j2.getX(), j0.getY(), j2.getY(), v0.getX(), v2.getX(),
                            v0.getY(), v2.getY()));
        } catch (IllegalArgumentException erro) {
            mostrarErro("Janela inválida", erro.getMessage());
        }
    }

    // Liga/desliga os contornos da janela e da viewport
    private void alternarJanela() {
        if (caixaJanela.isSelected()) {
            try {
                lerJanela();
            } catch (IllegalArgumentException erro) {
                caixaJanela.setSelected(false);
                mostrarErro("Janela inválida", erro.getMessage());
                return;
            }
        }
        redesenhar();
    }

    private void aplicarSequencia() {
        if (temPoligono() == false) {
            return;
        }
        try {
            SequenciaTransformacoes sequencia = SequenciaTransformacoes.ler(campoSequencia.getText());
            log("--- Sequência composta: " + campoSequencia.getText().trim());
            List<SequenciaTransformacoes.Passo> passos = sequencia.getPassos();
            for (int i = 0; i < passos.size(); i++) {
                logMatriz("passo " + (i + 1) + ": " + passos.get(i).getTexto(), passos.get(i).getMatriz());
            }
            aplicar("Composta  M = M" + passos.size() + " · ... · M1", sequencia.getMatrizTotal());
        } catch (IllegalArgumentException erro) {
            mostrarErro("Sequência inválida", erro.getMessage());
        }
    }

    // M_nova · M_acumulada: a transformação mais recente multiplica pela esquerda
    private void aplicar(String descricao, Matriz3 m) {
        if (temPoligono() == false) {
            return;
        }
        historico.add(acumulada);
        acumulada = m.multiplicar(acumulada);
        atual = original.transformar(acumulada);

        log("--- " + descricao);
        logMatriz("M", m);
        logMatriz("M acumulada", acumulada);
        logVertices(atual);
        int[] fora = redesenhar();
        String mensagem = descricao + "  |  " + fmtPixels(fora);
        status.setMensagem(mensagem);
    }

    private void desfazer() {
        if (historico.isEmpty()) {
            status.setMensagem("Nada para desfazer.");
            return;
        }
        acumulada = historico.remove(historico.size() - 1);
        atual = original.transformar(acumulada);
        log("--- Desfeito (voltou uma transformação)");
        logMatriz("M acumulada", acumulada);
        redesenhar();
        status.setMensagem("Última transformação desfeita.");
    }

    private void resetar() {
        if (original == null) {
            status.setMensagem("Nenhum polígono na tela.");
            return;
        }
        acumulada = Matriz3.identidade();
        historico.clear();
        atual = original;
        log("--- Voltou ao polígono original");
        redesenhar();
        status.setMensagem("Polígono original.");
    }

    // Slide 28: confere no log quais pares de transformações comutam
    private void testarComutatividade() {
        log("--- Comutatividade: M1·M2 = M2·M1 ?  (slide 28)");
        int erros = 0;
        erros += caso("Translação · Translação", Transformacoes.translacao(2, 3), Transformacoes.translacao(-1, 4), true);
        erros += caso("Escala · Escala", Transformacoes.escala(2, 3), Transformacoes.escala(4, 0.5), true);
        erros += caso("Rotação · Rotação", Transformacoes.rotacao(30), Transformacoes.rotacao(50), true);
        erros += caso("Escala (sx = sy) · Rotação", Transformacoes.escala(2, 2), Transformacoes.rotacao(40), true);
        log("... e alguns que NÃO comutam:");
        erros += caso("Translação · Rotação", Transformacoes.translacao(2, 3), Transformacoes.rotacao(40), false);
        erros += caso("Translação · Escala", Transformacoes.translacao(2, 3), Transformacoes.escala(2, 2), false);
        erros += caso("Escala (sx != sy) · Rotação", Transformacoes.escala(2, 1), Transformacoes.rotacao(45), false);

        if (erros == 0) {
            status.setMensagem("Comutatividade: todos os casos batem com a tabela do slide 28.");
        } else {
            status.setMensagem("Comutatividade: " + erros + " caso(s) fora do esperado, veja o log.");
        }
    }

    private int caso(String nome, Matriz3 a, Matriz3 b, boolean esperado) {
        boolean comuta = a.multiplicar(b).igualA(b.multiplicar(a), 1e-9);
        String resposta;
        if (comuta) {
            resposta = "comuta";
        } else {
            resposta = "NÃO comuta";
        }
        String resultado;
        if (comuta == esperado) {
            resultado = "OK";
        } else {
            resultado = "ERRO";
        }
        log(nome + ": " + resposta + "  " + resultado);
        if (comuta == esperado) {
            return 0;
        }
        return 1;
    }

    // ===== Desenho =====

    // Limpa o frame buffer e redesenha tudo: janela/viewport, pixels livres,
    // o polígono atual (verde) e o desenho em andamento. A forma antiga
    // (antes da transformação) não é desenhada.
    // Devolve { pixels de setPixel, pixels fora da tela } do polígono atual.
    private int[] redesenhar() {
        frameBuffer.limpar();
        PlanoCartesiano plano = plano();

        // contornos da janela e da viewport ficam por baixo dos polígonos
        if (caixaJanela != null && caixaJanela.isSelected() && contornoJanela != null) {
            desenho.desenhar(new RasterTela(plano), contornoJanela, COR_JANELA);
            desenho.desenhar(new RasterTela(plano), contornoViewport, COR_VIEWPORT);
        }

        RasterTela livres = new RasterTela(plano);
        for (int i = 0; i < pixelsLivres.size(); i++) {
            livres.setPixel(pixelsLivres.get(i).getX(), pixelsLivres.get(i).getY(), COR_ATUAL);
        }

        RasterTela raster = new RasterTela(plano);
        if (atual != null) {
            desenho.desenhar(raster, atual, COR_ATUAL);
        }

        desenharEmAndamento(plano);

        // contorno no vértice 1 (do desenho em andamento ou do polígono atual)
        Pixel marcar = null;
        if (pontosDesenho.isEmpty() == false) {
            marcar = pontosDesenho.get(0);
        } else if (atual != null) {
            marcar = DesenhoPoligono.pixelDoVertice(atual.get(0));
        }
        if (marcar != null) {
            Pixel dispositivo = plano.paraDispositivo(marcar.getX(), marcar.getY());
            if (frameBuffer.contem(dispositivo.getX(), dispositivo.getY())) {
                painel.setMarcador(dispositivo);
            } else {
                painel.setMarcador(null);
            }
        } else {
            painel.setMarcador(null);
        }
        painel.atualizar();
        return new int[] { raster.total, raster.fora };
    }

    // A forma que está sendo puxada com o mouse
    private void desenharEmAndamento(PlanoCartesiano plano) {
        if (pontosDesenho.isEmpty()) {
            return;
        }
        RasterTela r = new RasterTela(plano);
        Pixel inicio = pontosDesenho.get(0);
        int modo = comboModo.getSelectedIndex();

        if (modo == MODO_POLIGONO) {
            for (int i = 1; i < pontosDesenho.size(); i++) {
                Pixel a = pontosDesenho.get(i - 1);
                Pixel b = pontosDesenho.get(i);
                reta.desenhar(r, a.getX(), a.getY(), b.getX(), b.getY(), COR_ATUAL);
            }
            if (previa != null) {
                Pixel ultimo = pontosDesenho.get(pontosDesenho.size() - 1);
                reta.desenhar(r, ultimo.getX(), ultimo.getY(), previa.getX(), previa.getY(), COR_PREVIA);
            }
        } else if (modo == MODO_RETANGULO) {
            if (previa != null && previa.getX() != inicio.getX() && previa.getY() != inicio.getY()) {
                desenho.desenhar(r, Poligono.retangulo(Math.min(inicio.getX(), previa.getX()),
                        Math.max(inicio.getX(), previa.getX()), Math.min(inicio.getY(), previa.getY()),
                        Math.max(inicio.getY(), previa.getY())), COR_PREVIA);
            }
        } else if (modo == MODO_REGULAR) {
            if (previa != null && distancia(inicio, previa) >= 1) {
                try {
                    int lados = Componentes.lerInteiro(campoLadosDesenho);
                    if (lados >= 3) {
                        desenho.desenhar(r, regularPuxado(inicio, previa, lados), COR_PREVIA);
                    }
                } catch (NumberFormatException erro) {
                    // lados inválido: só mostra o centro
                }
            }
        }
        r.setPixel(inicio.getX(), inicio.getY(), COR_ATUAL);
    }

    // Raster que recebe pontos do plano cartesiano e acende o pixel do dispositivo
    private class RasterTela implements Raster {
        private final PlanoCartesiano plano;
        private int total;
        private int fora;

        RasterTela(PlanoCartesiano plano) {
            this.plano = plano;
        }

        public void setPixel(int x, int y, int cor) {
            total++;
            Pixel d = plano.paraDispositivo(x, y);
            if (frameBuffer.contem(d.getX(), d.getY())) {
                frameBuffer.setPixel(d.getX(), d.getY(), cor);
            } else {
                fora++;
            }
        }
    }

    // ===== Log e texto =====

    private void logPoligono(String titulo, Poligono p) {
        log("--- " + titulo);
        logVertices(p);
    }

    private void logVertices(Poligono p) {
        for (int i = 0; i < p.getQuantidade(); i++) {
            Vertice v = p.get(i);
            log("v" + (i + 1) + " = " + v + "  ->  pixel " + DesenhoPoligono.pixelDoVertice(v));
        }
    }

    private void logMatriz(String nome, Matriz3 m) {
        String[] linhas = m.formatarLinhas();
        log(nome + " =");
        for (int i = 0; i < linhas.length; i++) {
            log("      " + linhas[i]);
        }
    }

    private String fmtPixels(int[] contagem) {
        String texto = contagem[0] + " chamadas de setPixel";
        if (contagem[1] > 0) {
            texto += " (" + contagem[1] + " fora da tela)";
        }
        return texto;
    }

    private static String fmt(double v) {
        return Matriz3.formatar(v);
    }

    // Aceita 2,5 ou 2.5
    private double lerReal(JTextField campo, String nome) {
        String texto = campo.getText().trim().replace(',', '.');
        try {
            double v = Double.parseDouble(texto);
            if (Double.isNaN(v) || Double.isInfinite(v)) {
                throw new NumberFormatException();
            }
            return v;
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException("O campo \"" + nome + "\" deve ser um número (ex.: 2 ou -1.5).");
        }
    }
}
