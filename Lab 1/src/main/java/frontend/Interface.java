package frontend;

import dispositivo.Dispositivo;
import modelo.JanelaMundo;
import modelo.Pixel;
import modelo.Ponto;
import transformacao.Transformacao;
import transformacao.TransformacaoNdc01;
import transformacao.TransformacaoNdc11;
import tabelacores.ControladorVideo;
import tabelacores.FrameBufferIndexado;
import tabelacores.Hexa;
import tabelacores.TabelaCores;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class Interface extends JFrame {

    private static final Color FACE = new Color(0xC0C0C0);
    private static final Color TITULO_INICIO = new Color(0x000080);
    private static final Color TITULO_FIM = new Color(0x1084D0);
    private static final Color XADREZ_CLARO = Color.WHITE;
    private static final Color XADREZ_ESCURO = new Color(0xC0C0C0);

    private JTextField campoXmin;
    private JTextField campoXmax;
    private JTextField campoYmin;
    private JTextField campoYmax;
    private JTextField campoX;
    private JTextField campoY;

    private JButton botaoCenario1;
    private JButton botaoCenario2;
    private int cenarioSelecionado = 1;

    private JLabel statusLabel;
    private JPanel painelCanvas;
    private Dispositivo dispositivo;
    private final List<String> historicoTransformacoes = new ArrayList<String>();
    private CardLayout layoutCards;
    private JPanel painelCards;

    // Lab 1 Extra: tabela de cores
    private static final int TAMANHO_PALETA = 384; // 64 x 64 blocos de 6 x 6
    private TabelaCores tabelaCores;
    private ControladorVideo controladorVideo;
    private BufferedImage imagemPaleta;
    private int indiceCorAtual;
    private int tabelaSelecionada = 1;
    private JButton botaoTabelaColorida;
    private JButton botaoTabelaCinza;
    private JButton botaoTabelaFogo;
    private JTextField campoIndiceCor;
    private JPanel painelPaletaCores;
    private JPanel amostraCor;
    private JLabel valorCorHex;
    private JLabel valorIndiceHex;
    private JLabel valorIndiceDecimal;
    private JLabel valorIndiceBinario;
    private JLabel valorCanalR;
    private JLabel valorCanalG;
    private JLabel valorCanalB;
    private JLabel valorNomeTabela;
    private JLabel statusTabela;

    private int arrastarX;
    private int arrastarY;
    private Rectangle limitesRestaurados;

    public Interface() {
        setUndecorated(true);
        setSize(820, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FACE);
        raiz.setBorder(new BevelBorder(BevelBorder.RAISED));

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(criarBarraTitulo(), BorderLayout.NORTH);
        raiz.add(topo, BorderLayout.NORTH);

        layoutCards = new CardLayout();
        painelCards = new JPanel(layoutCards);
        painelCards.setBackground(FACE);
        painelCards.add(criarTelaInicial(), "inicial");
        painelCards.add(criarTelaPrincipal(), "principal");
        painelCards.add(criarTelaTabelaCores(), "tabela");
        raiz.add(painelCards, BorderLayout.CENTER);

        setContentPane(raiz);
    }

    private JPanel criarTelaInicial() {
        JPanel telaInicial = new JPanel();
        telaInicial.setLayout(new javax.swing.BoxLayout(telaInicial, javax.swing.BoxLayout.Y_AXIS));
        telaInicial.setBackground(FACE);
        telaInicial.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("CoordinateX");
        titulo.setAlignmentX(CENTER_ALIGNMENT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 40));

        JLabel professor = new JLabel("Professor: Robson Pequeno de Sousa");
        professor.setAlignmentX(CENTER_ALIGNMENT);
        professor.setFont(new Font("SansSerif", Font.PLAIN, 25));

        JSeparator divisoria = new JSeparator();
        divisoria.setAlignmentX(CENTER_ALIGNMENT);
        divisoria.setMaximumSize(new Dimension(430, 2));

        JLabel equipeTitulo = new JLabel("EQUIPE");
        equipeTitulo.setAlignmentX(CENTER_ALIGNMENT);
        equipeTitulo.setFont(new Font("SansSerif", Font.ITALIC, 25));

        JLabel equipe = new JLabel(
                "Adrielly Carla | Maria Eduarda | Mirelle Casimiro | Nidlan Hendell | Rui Fernando");
        equipe.setAlignmentX(CENTER_ALIGNMENT);
        equipe.setFont(new Font("SansSerif", Font.PLAIN, 20));

        JButton iniciar = criarBotaoInicial("Lab 1 - Coordenadas");
        iniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutCards.show(painelCards, "principal");
            }
        });

        JButton tabelaCores = criarBotaoInicial("Lab 1 Extra - Tabela de Cores");
        tabelaCores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutCards.show(painelCards, "tabela");
            }
        });

        JPanel botoesIniciais = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botoesIniciais.setBackground(FACE);
        botoesIniciais.setAlignmentX(CENTER_ALIGNMENT);
        botoesIniciais.add(iniciar);
        botoesIniciais.add(tabelaCores);

        telaInicial.add(javax.swing.Box.createVerticalGlue());
        telaInicial.add(titulo);
        telaInicial.add(javax.swing.Box.createVerticalStrut(8));
        telaInicial.add(professor);
        telaInicial.add(javax.swing.Box.createVerticalStrut(18));
        telaInicial.add(divisoria);
        telaInicial.add(javax.swing.Box.createVerticalStrut(18));
        telaInicial.add(equipeTitulo);
        telaInicial.add(javax.swing.Box.createVerticalStrut(8));
        telaInicial.add(equipe);
        telaInicial.add(javax.swing.Box.createVerticalStrut(28));
        telaInicial.add(botoesIniciais);
        telaInicial.add(javax.swing.Box.createVerticalGlue());

        return telaInicial;
    }

    private JButton criarBotaoInicial(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.PLAIN, 16));
        botao.setFocusPainted(false);
        botao.setBackground(FACE);
        botao.setBorder(BorderFactory.createCompoundBorder(
                new BevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        return botao;
    }

    // ===== Lab 1 Extra: tabela de cores (24 bits) para pixel de 12 bits =====
    // Usa só o núcleo do Lab 1 - Extra (TabelaCores, FrameBufferIndexado,
    // ControladorVideo). A tela é desta Interface, igual ao Lab 1.

    private JPanel criarTelaTabelaCores() {
        tabelaCores = new TabelaCores();

        FrameBufferIndexado frameBuffer = new FrameBufferIndexado(TAMANHO_PALETA, TAMANHO_PALETA);
        frameBuffer.desenharTodosOsIndices();
        controladorVideo = new ControladorVideo(frameBuffer, tabelaCores);

        JPanel tela = new JPanel(new BorderLayout());
        tela.setBackground(FACE);
        tela.add(criarBarraMenuTabela(), BorderLayout.NORTH);
        tela.add(criarCaixaFerramentasTabela(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FACE);
        centro.add(criarPainelEntradaTabela(), BorderLayout.NORTH);
        centro.add(criarAreaTabela(), BorderLayout.CENTER);
        tela.add(centro, BorderLayout.CENTER);
        tela.add(criarBarraStatusTabela(), BorderLayout.SOUTH);

        imagemPaleta = controladorVideo.gerarImagem();
        selecionarIndiceCor(0x180);
        return tela;
    }

    private JMenuBar criarBarraMenuTabela() {
        JMenuBar barra = new JMenuBar();
        barra.setBackground(FACE);

        JMenu arquivo = new JMenu("Arquivo");
        JMenuItem inicio = new JMenuItem("Voltar ao inicio");
        inicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutCards.show(painelCards, "inicial");
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

        JMenu ajuda = new JMenu("Ajuda");
        JMenuItem sobre = new JMenuItem("Sobre");
        sobre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Interface.this,
                        "CG-CoordinateX - Lab 1 Extra\n"
                                + "Pixel de 12 bits -> 4096 posicoes na tabela\n"
                                + "Cada posicao guarda uma cor de 24 bits (#RRGGBB)\n",
                        "Sobre", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        ajuda.add(sobre);

        barra.add(arquivo);
        barra.add(ajuda);
        return barra;
    }

    private JPanel criarCaixaFerramentasTabela() {
        JPanel caixa = new JPanel(new BorderLayout());
        caixa.setBackground(FACE);
        caixa.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        caixa.setPreferredSize(new Dimension(90, 10));

        JPanel grade = new JPanel(new GridLayout(0, 1, 4, 4));
        grade.setBackground(FACE);

        Dimension tamanhoBotao = new Dimension(80, 46);

        botaoTabelaColorida = criarBotaoFerramenta("Colorida", tamanhoBotao);
        botaoTabelaColorida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trocarTabelaCores(1);
            }
        });

        botaoTabelaCinza = criarBotaoFerramenta("Cinza", tamanhoBotao);
        botaoTabelaCinza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trocarTabelaCores(2);
            }
        });

        botaoTabelaFogo = criarBotaoFerramenta("Fogo", tamanhoBotao);
        botaoTabelaFogo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trocarTabelaCores(3);
            }
        });

        grade.add(botaoTabelaColorida);
        grade.add(botaoTabelaCinza);
        grade.add(botaoTabelaFogo);
        caixa.add(grade, BorderLayout.NORTH);

        atualizarBotoesTabela();
        return caixa;
    }

    private void atualizarBotoesTabela() {
        JButton[] botoes = { botaoTabelaColorida, botaoTabelaCinza, botaoTabelaFogo };
        for (int i = 0; i < botoes.length; i++) {
            if (i + 1 == tabelaSelecionada) {
                botoes[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
            } else {
                botoes[i].setBorder(new BevelBorder(BevelBorder.RAISED));
            }
        }
    }

    private JPanel criarPainelEntradaTabela() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        painel.setBackground(FACE);
        painel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        campoIndiceCor = new JTextField("0x180", 6);
        campoIndiceCor.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JButton verCor = new JButton("Ver cor");
        verCor.setFocusPainted(false);
        verCor.setBackground(FACE);
        verCor.setBorder(BorderFactory.createCompoundBorder(
                new BevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));

        ActionListener lerCampo = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selecionarIndicePeloCampo();
            }
        };
        campoIndiceCor.addActionListener(lerCampo); // Enter
        verCor.addActionListener(lerCampo);

        painel.add(new JLabel("indice do pixel (0 a 4095 ou 0x000 a 0xFFF)"));
        painel.add(campoIndiceCor);
        painel.add(verCor);
        return painel;
    }

    private JPanel criarAreaTabela() {
        JPanel area = new JPanel(new BorderLayout(6, 0));
        area.setBackground(FACE);
        area.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        // Paleta: a imagem gerada pelo controlador de video (64 x 64 indices)
        painelPaletaCores = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharPaleta(g);
            }
        };
        painelPaletaCores.setPreferredSize(new Dimension(TAMANHO_PALETA, TAMANHO_PALETA));
        painelPaletaCores.setBackground(Color.WHITE);
        painelPaletaCores.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int bloco = TAMANHO_PALETA / 64;
                int coluna = e.getX() / bloco;
                int linha = e.getY() / bloco;
                if (coluna < 64 && linha < 64) {
                    selecionarIndiceCor(linha * 64 + coluna);
                }
            }
        });

        JPanel moldura = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        moldura.setBackground(FACE);
        moldura.setBorder(new BevelBorder(BevelBorder.LOWERED));
        moldura.add(painelPaletaCores);

        area.add(moldura, BorderLayout.CENTER);
        area.add(criarPainelInfoCor(), BorderLayout.EAST);
        return area;
    }

    private void desenharPaleta(Graphics g) {
        g.drawImage(imagemPaleta, 0, 0, null);

        // Moldura branca e preta no bloco selecionado
        int bloco = TAMANHO_PALETA / 64;
        int x = (indiceCorAtual % 64) * bloco;
        int y = (indiceCorAtual / 64) * bloco;
        g.setColor(Color.WHITE);
        g.drawRect(x - 1, y - 1, bloco + 1, bloco + 1);
        g.setColor(Color.BLACK);
        g.drawRect(x - 2, y - 2, bloco + 3, bloco + 3);
    }

    private JPanel criarPainelInfoCor() {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setBackground(FACE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                new BevelBorder(BevelBorder.LOWERED),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        painel.setPreferredSize(new Dimension(230, 10));

        amostraCor = new JPanel();
        amostraCor.setPreferredSize(new Dimension(10, 70));
        amostraCor.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel grade = new JPanel(new GridLayout(0, 2, 4, 6));
        grade.setBackground(FACE);

        valorCorHex = criarValorInfo();
        valorIndiceHex = criarValorInfo();
        valorIndiceDecimal = criarValorInfo();
        valorIndiceBinario = criarValorInfo();
        valorCanalR = criarValorInfo();
        valorCanalG = criarValorInfo();
        valorCanalB = criarValorInfo();
        valorNomeTabela = criarValorInfo();

        grade.add(new JLabel("Cor (24 bits)"));
        grade.add(valorCorHex);
        grade.add(new JLabel("Indice (hex)"));
        grade.add(valorIndiceHex);
        grade.add(new JLabel("Indice (dec)"));
        grade.add(valorIndiceDecimal);
        grade.add(new JLabel("12 bits"));
        grade.add(valorIndiceBinario);
        grade.add(new JLabel("R"));
        grade.add(valorCanalR);
        grade.add(new JLabel("G"));
        grade.add(valorCanalG);
        grade.add(new JLabel("B"));
        grade.add(valorCanalB);

        // O nome da tabela ocupa a linha inteira (nao cabe na coluna da grade)
        JPanel linhaTabela = new JPanel(new BorderLayout());
        linhaTabela.setBackground(FACE);
        linhaTabela.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        linhaTabela.add(new JLabel("Tabela"), BorderLayout.NORTH);
        linhaTabela.add(valorNomeTabela, BorderLayout.CENTER);

        // Deixa as linhas com a altura normal, sem esticar
        JPanel informacoes = new JPanel(new BorderLayout());
        informacoes.setBackground(FACE);
        informacoes.add(grade, BorderLayout.NORTH);
        informacoes.add(linhaTabela, BorderLayout.CENTER);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(FACE);
        conteudo.add(informacoes, BorderLayout.NORTH);

        painel.add(amostraCor, BorderLayout.NORTH);
        painel.add(conteudo, BorderLayout.CENTER);
        return painel;
    }

    private JLabel criarValorInfo() {
        JLabel valor = new JLabel("-");
        valor.setFont(new Font("Monospaced", Font.BOLD, 12));
        return valor;
    }

    private JPanel criarBarraStatusTabela() {
        statusTabela = new JLabel(" Clique na paleta ou digite um indice.");
        statusTabela.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(FACE);
        barra.add(statusTabela, BorderLayout.CENTER);

        JLabel participantes = new JLabel(
                " Grupo: Adrielly Carla | Maria Eduarda | Mirelle Casimiro | Nidlan Hendell | Rui Fernando ");
        participantes.setFont(new Font("SansSerif", Font.PLAIN, 10));
        participantes.setBorder(new BevelBorder(BevelBorder.LOWERED));
        barra.add(participantes, BorderLayout.SOUTH);
        return barra;
    }

    // Mostra a cor de um indice: consulta a tabela (12 bits -> 24 bits)
    private void selecionarIndiceCor(int indice) {
        indiceCorAtual = indice;

        int cor = tabelaCores.getCor(indice);
        int r = tabelaCores.getVermelho(indice);
        int g = tabelaCores.getVerde(indice);
        int b = tabelaCores.getAzul(indice);

        amostraCor.setBackground(new Color(cor));
        valorCorHex.setText(Hexa.cor(cor));
        valorIndiceHex.setText(Hexa.indice(indice));
        valorIndiceDecimal.setText("" + indice);
        valorIndiceBinario.setText(Hexa.binario(indice));
        valorCanalR.setText("0x" + Hexa.canal(r) + " (" + r + ")");
        valorCanalG.setText("0x" + Hexa.canal(g) + " (" + g + ")");
        valorCanalB.setText("0x" + Hexa.canal(b) + " (" + b + ")");
        valorNomeTabela.setText(tabelaCores.getNome());

        campoIndiceCor.setText(Hexa.indice(indice));
        statusTabela.setText(" Pixel " + Hexa.indice(indice) + " (12 bits)  ->  tabela["
                + indice + "]  ->  " + Hexa.cor(cor) + " (24 bits)");
        painelPaletaCores.repaint();
    }

    private void selecionarIndicePeloCampo() {
        int indice = Hexa.lerIndice(campoIndiceCor.getText());

        if (indice < 0 || indice >= TabelaCores.TAMANHO) {
            JOptionPane.showMessageDialog(this, "Digite de 0 a 4095 (ou 0x000 a 0xFFF).",
                    "Indice invalido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        selecionarIndiceCor(indice);
    }

    // Troca a tabela; o frame buffer (os indices) continua o mesmo
    private void trocarTabelaCores(int tipo) {
        tabelaSelecionada = tipo;
        if (tipo == 1) {
            tabelaCores.preencherColorida();
        } else if (tipo == 2) {
            tabelaCores.preencherCinza();
        } else {
            tabelaCores.preencherFogo();
        }
        atualizarBotoesTabela();

        imagemPaleta = controladorVideo.gerarImagem();
        selecionarIndiceCor(indiceCorAtual);
    }

    private JPanel criarTelaPrincipal() {
        JPanel telaPrincipal = new JPanel(new BorderLayout());
        telaPrincipal.setBackground(FACE);
        telaPrincipal.add(criarBarraMenu(), BorderLayout.NORTH);
        telaPrincipal.add(criarCaixaFerramentas(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FACE);
        centro.add(criarPainelEntrada(), BorderLayout.NORTH);
        centro.add(criarCanvas(), BorderLayout.CENTER);
        telaPrincipal.add(centro, BorderLayout.CENTER);
        telaPrincipal.add(criarBarraStatus(), BorderLayout.SOUTH);

        return telaPrincipal;
    }

    private JPanel criarBarraTitulo() {
        JPanel barra = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gradiente = new GradientPaint(0, 0, TITULO_INICIO, getWidth(), 0, TITULO_FIM);
                g2.setPaint(gradiente);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        barra.setPreferredSize(new Dimension(10, 24));

        JLabel titulo = new JLabel("  LAB1 CG - CoordinateX - Transformacao de Coordenadas");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        barra.add(titulo, BorderLayout.WEST);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 3));
        botoes.setOpaque(false);

        JButton minimizar = criarBotaoJanela("_");
        minimizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setExtendedState(Frame.ICONIFIED);
            }
        });

        JButton restaurar = criarBotaoJanela("□");
        restaurar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alternarMaximizar();
            }
        });

        JButton fechar = criarBotaoJanela("X");
        fechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        botoes.add(minimizar);
        botoes.add(restaurar);
        botoes.add(fechar);
        barra.add(botoes, BorderLayout.EAST);

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
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            setBounds(ge.getMaximumWindowBounds());
        } else {
            setBounds(limitesRestaurados);
            limitesRestaurados = null;
        }
    }

    private JMenuBar criarBarraMenu() {
        JMenuBar barra = new JMenuBar();
        barra.setBackground(FACE);

        JMenu arquivo = new JMenu("Arquivo");
        JMenuItem sair = new JMenuItem("Sair");
        sair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        JMenuItem inicio = new JMenuItem("Voltar ao inicio");
        inicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutCards.show(painelCards, "inicial");
            }
        });
        arquivo.add(inicio);
        arquivo.add(sair);

        JMenu editar = new JMenu("Editar");
        JMenu exibir = new JMenu("Exibir");
        JMenuItem verHistorico = new JMenuItem("Ver Histórico");
        verHistorico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarHistorico();
            }
        });
        exibir.add(verHistorico);
        JMenu opcoes = new JMenu("Opcoes");

        JMenu ajuda = new JMenu("Ajuda");
        JMenuItem sobre = new JMenuItem("Sobre");
        sobre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Interface.this,
                        "CG-CoordinateX - Lab 1\nJanela do Mundo -> NDC -> Dispositivo\n",
                        "Sobre", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        ajuda.add(sobre);

        barra.add(arquivo);
        barra.add(editar);
        barra.add(exibir);
        barra.add(opcoes);
        barra.add(ajuda);
        return barra;
    }

    private JPanel criarCaixaFerramentas() {
        JPanel caixa = new JPanel(new BorderLayout());
        caixa.setBackground(FACE);
        caixa.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        caixa.setPreferredSize(new Dimension(90, 10));

        JPanel grade = new JPanel(new GridLayout(0, 1, 4, 4));
        grade.setBackground(FACE);

        Dimension tamanhoBotao = new Dimension(80, 46);

        botaoCenario1 = criarBotaoFerramenta("<html>NDC<br>[0,1]</html>", tamanhoBotao);
        botaoCenario1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cenarioSelecionado = 1;
                atualizarBotoesCenario();
            }
        });

        botaoCenario2 = criarBotaoFerramenta("<html>NDC<br>[-1,1]</html>", tamanhoBotao);
        botaoCenario2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cenarioSelecionado = 2;
                atualizarBotoesCenario();
            }
        });

        JButton botaoTransformar = criarBotaoFerramenta("<html>Transfor<br>-mar</html>", tamanhoBotao);
        botaoTransformar.setBorder(new BevelBorder(BevelBorder.RAISED));
        botaoTransformar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transformar();
            }
        });

        JButton botaoLimpar = criarBotaoFerramenta("Limpar", tamanhoBotao);
        botaoLimpar.setBorder(new BevelBorder(BevelBorder.RAISED));
        botaoLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispositivo = null;
                painelCanvas.repaint();
                statusLabel.setText(" Para comecar, preencha os parametros e clique em Transformar.");
            }
        });

        grade.add(botaoCenario1);
        grade.add(botaoCenario2);
        grade.add(botaoTransformar);
        grade.add(botaoLimpar);

        caixa.add(grade, BorderLayout.NORTH);

        atualizarBotoesCenario();

        return caixa;
    }

    private JButton criarBotaoFerramenta(String texto, Dimension tamanho) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setPreferredSize(tamanho);
        botao.setMaximumSize(tamanho);
        botao.setMinimumSize(tamanho);
        botao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        botao.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        botao.setFont(new Font("SansSerif", Font.PLAIN, 11));
        return botao;
    }

    private void atualizarBotoesCenario() {
        if (cenarioSelecionado == 1) {
            botaoCenario1.setBorder(new BevelBorder(BevelBorder.LOWERED));
            botaoCenario2.setBorder(new BevelBorder(BevelBorder.RAISED));
        } else {
            botaoCenario1.setBorder(new BevelBorder(BevelBorder.RAISED));
            botaoCenario2.setBorder(new BevelBorder(BevelBorder.LOWERED));
        }
    }

    private JPanel criarPainelEntrada() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        painel.setBackground(FACE);
        painel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        campoXmin = criarCampo("0");
        campoXmax = criarCampo("100");
        campoYmin = criarCampo("0");
        campoYmax = criarCampo("100");
        campoX = criarCampo("50");
        campoY = criarCampo("50");

        painel.add(new JLabel("xmin"));
        painel.add(campoXmin);
        painel.add(new JLabel("xmax"));
        painel.add(campoXmax);
        painel.add(new JLabel("ymin"));
        painel.add(campoYmin);
        painel.add(new JLabel("ymax"));
        painel.add(campoYmax);
        painel.add(new JLabel("ponto x"));
        painel.add(campoX);
        painel.add(new JLabel("ponto y"));
        painel.add(campoY);

        return painel;
    }

    private JTextField criarCampo(String valorInicial) {
        JTextField campo = new JTextField(valorInicial, 4);
        campo.setBorder(new BevelBorder(BevelBorder.LOWERED));
        return campo;
    }

    private JScrollPane criarCanvas() {
        painelCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharCanvas(g);
            }
        };
        painelCanvas.setPreferredSize(new Dimension(400, 300));
        painelCanvas.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(painelCanvas);
        scroll.setBorder(new BevelBorder(BevelBorder.LOWERED));
        return scroll;
    }


    private void desenharCanvas(Graphics g) {
        if (dispositivo == null) {
            int tamanhoQuadro = 8;
            for (int y = 0; y < painelCanvas.getHeight(); y += tamanhoQuadro) {
                for (int x = 0; x < painelCanvas.getWidth(); x += tamanhoQuadro) {
                    boolean par = ((x / tamanhoQuadro) + (y / tamanhoQuadro)) % 2 == 0;
                    if (par) {
                        g.setColor(XADREZ_CLARO);
                    } else {
                        g.setColor(XADREZ_ESCURO);
                    }
                    g.fillRect(x, y, tamanhoQuadro, tamanhoQuadro);
                }
            }
            return;
        }

        g.drawImage(dispositivo.getFrameBuffer(), 0, 0, null);
    }

    private JPanel criarBarraStatus() {
        statusLabel = new JLabel(" Para comecar, preencha os parametros e clique em Transformar.");
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

    private void transformar() {
        try {
            double xmin = Double.parseDouble(campoXmin.getText());
            double xmax = Double.parseDouble(campoXmax.getText());
            double ymin = Double.parseDouble(campoYmin.getText());
            double ymax = Double.parseDouble(campoYmax.getText());

            double x = Double.parseDouble(campoX.getText());
            double y = Double.parseDouble(campoY.getText());

            JanelaMundo janela = new JanelaMundo(xmin, xmax, ymin, ymax);

            if (janela.ehValida() == false) {
                JOptionPane.showMessageDialog(this, "xmax deve ser maior que xmin e ymax maior que ymin.",
                        "Janela invalida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Ponto pontoMundo = new Ponto(x, y);

            if (janela.contem(pontoMundo) == false) {
                JOptionPane.showMessageDialog(this, "O ponto precisa estar dentro da janela do mundo.",
                        "Ponto fora da janela", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dispositivo = new Dispositivo();

            Transformacao transformacao;
            if (cenarioSelecionado == 1) {
                transformacao = new TransformacaoNdc01(janela, dispositivo);
            } else {
                transformacao = new TransformacaoNdc11(janela, dispositivo);
            }

            Ponto ndc = transformacao.userToNdc(pontoMundo);
            Pixel pixel = transformacao.ndcToDc(ndc);

            dispositivo.drawPixel(pixel, Dispositivo.VERDE);

            painelCanvas.setPreferredSize(new Dimension(dispositivo.getNdh(), dispositivo.getNdv()));
            painelCanvas.revalidate();
            painelCanvas.repaint();

            statusLabel.setText(" (" + transformacao.getNome() + ")  Mundo " + pontoMundo
                    + "  ->  NDC " + ndc + "  ->  Dispositivo " + pixel);

                historicoTransformacoes.add("Janela do mundo: " + janela
                    + "\nPonto de entrada: " + pontoMundo
                    + "\nNDC calculado: " + ndc
                    + "\nPixel de dispositivo: " + pixel
                    + "\nTransformacao: " + transformacao.getNome());

        } catch (NumberFormatException erro) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos com numeros validos.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarHistorico() {
        JTextArea textoHistorico = new JTextArea();
        textoHistorico.setEditable(false);
        textoHistorico.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textoHistorico.setBackground(Color.WHITE);
        textoHistorico.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        if (historicoTransformacoes.isEmpty()) {
            textoHistorico.setText("Nenhuma transformacao realizada nesta sessao.");
        } else {
            StringBuilder conteudo = new StringBuilder();
            for (int indice = 0; indice < historicoTransformacoes.size(); indice++) {
                conteudo.append("Transformacao ").append(indice + 1).append("\n");
                conteudo.append(historicoTransformacoes.get(indice));
                conteudo.append("\n\n");
            }
            textoHistorico.setText(conteudo.toString());
            textoHistorico.setCaretPosition(0);
        }

        JScrollPane rolagem = new JScrollPane(textoHistorico);
        rolagem.setPreferredSize(new Dimension(570, 300));
        rolagem.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JOptionPane.showMessageDialog(this, rolagem, "Histórico de Transformações",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Ponto de entrada proprio: so abre essa tela se rodar esta classe.
    // Nao e chamada por app.Main 
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Interface tela = new Interface();
                tela.setVisible(true);
            }
        });
    }
}
