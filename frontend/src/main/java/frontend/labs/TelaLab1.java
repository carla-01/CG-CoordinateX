package frontend.labs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import core.dispositivo.Cores;
import core.dispositivo.FrameBuffer;
import core.dispositivo.Monitor;
import core.modelo.JanelaMundo;
import core.modelo.Pixel;
import core.modelo.Ponto;
import frontend.Navegacao;
import frontend.TelaLab;
import frontend.componentes.Componentes;
import frontend.componentes.Estilo;
import frontend.componentes.PainelRaster;
import frontend.componentes.TelaCheia;
import lab1.transformacao.Transformacao;
import lab1.transformacao.TransformacaoNdc01;
import lab1.transformacao.TransformacaoNdc11;

// Lab 1: Mundo -> NDC -> Dispositivo e o caminho inverso.
//
// O frame buffer tem a resolução real do monitor (ndh x ndv) e
// só UM pixel verde aceso. Com zoom 1 cada pixel do frame buffer é
// um pixel do monitor; o botão "Tela cheia" mostra no display inteiro.
public class TelaLab1 extends TelaLab {

    private static final int[] ZOOMS = { 1, 2, 4, 8, 16, 32 };
    private static final String MENSAGEM_INICIAL = "Preencha a janela do mundo e o ponto, e clique em Transformar.";

    private JTextField campoXmin;
    private JTextField campoXmax;
    private JTextField campoYmin;
    private JTextField campoYmax;
    private JTextField campoX;
    private JTextField campoY;
    private JComboBox<String> comboZoom;

    private JButton[] botoesCenario;
    private int cenarioSelecionado = 0; // 0: NDC [0,1]   1: NDC [-1,1]

    // Aba "Dispositivo -> Mundo": valores do pixel sob o mouse
    private JLabel valorDispositivo;
    private JLabel valorNdc;
    private JLabel valorMundo;

    private PainelRaster painel;
    private FrameBuffer frameBuffer;
    private Transformacao transformacao;
    private String mensagemResultado = MENSAGEM_INICIAL;
    private int numeroTransformacao = 0;

    public TelaLab1(Navegacao navegacao) {
        super(navegacao, "Lab 1 - Coordenadas",
                "Janela do Mundo -> NDC -> Dispositivo\n"
                        + "Passe o mouse na tela para ver o caminho inverso.\n"
                        + "Clique num pixel para usar a coordenada dele no mundo.",
                MENSAGEM_INICIAL);

        painel = new PainelRaster();
        painel.setOuvinte(new PainelRaster.Ouvinte() {
            public void mouseSobre(Pixel pixel) {
                mostrarCaminhoInverso(pixel);
            }

            public void clique(Pixel pixel) {
                usarPixelClicado(pixel);
            }
        });

        JTabbedPane abas = criarAbas();
        abas.addTab("Mundo → Dispositivo", criarAbaTransformacao());
        abas.addTab("Dispositivo → Mundo", criarAbaInversa());

        montar(criarBarraOpcoes(), painel.criarRolagem(), abas);
    }

    // ===== Montagem da tela =====

    private JPanel criarBarraOpcoes() {
        // Zoom 1 = pixel real do monitor (padrão do Lab 1)
        comboZoom = Componentes.comboZoom(ZOOMS, 1);
        comboZoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                painel.setZoom(Componentes.lerZoom(comboZoom));
            }
        });

        // Desligado por padrão: no zoom 1 os eixos cobririam pixels reais
        final JCheckBox eixos = Componentes.caixaMarcar("Eixos", false);
        eixos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                painel.setMostrarEixos(eixos.isSelected());
            }
        });

        JButton limpar = Componentes.botao("Limpar");
        limpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpar();
            }
        });

        JButton telaCheia = Componentes.botao("Tela cheia");
        telaCheia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (frameBuffer == null) {
                    mostrarErro("Tela cheia", "Clique em Transformar primeiro.");
                    return;
                }
                TelaCheia.mostrar(frameBuffer);
            }
        });

        JPanel barra = Componentes.linhaEntrada();
        barra.add(Componentes.rotulo("zoom"));
        barra.add(comboZoom);
        barra.add(eixos);
        barra.add(limpar);
        barra.add(telaCheia);
        barra.add(Componentes.rotulo("   dispositivo: " + Monitor.getNdh() + " x " + Monitor.getNdv()
                + " (resolução do monitor)"));
        return barra;
    }

    private JPanel criarAbaTransformacao() {
        campoXmin = Componentes.campo("0", 5);
        campoXmax = Componentes.campo("100", 5);
        campoYmin = Componentes.campo("0", 5);
        campoYmax = Componentes.campo("100", 5);
        campoX = Componentes.campo("50", 5);
        campoY = Componentes.campo("50", 5);

        JPanel janela = Componentes.linhaEntrada();
        janela.add(Componentes.rotulo("Janela do mundo:   xmin"));
        janela.add(campoXmin);
        janela.add(Componentes.rotulo("xmax"));
        janela.add(campoXmax);
        janela.add(Componentes.rotulo("ymin"));
        janela.add(campoYmin);
        janela.add(Componentes.rotulo("ymax"));
        janela.add(campoYmax);

        JPanel ponto = Componentes.linhaEntrada();
        ponto.add(Componentes.rotulo("Ponto no mundo:   x"));
        ponto.add(campoX);
        ponto.add(Componentes.rotulo("y"));
        ponto.add(campoY);

        JButton cenario1 = Componentes.botao("NDC [0,1] x [0,1]");
        JButton cenario2 = Componentes.botao("NDC [-1,1] x [-1,1]");
        botoesCenario = new JButton[] { cenario1, cenario2 };
        for (int i = 0; i < botoesCenario.length; i++) {
            final int cenario = i;
            botoesCenario[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cenarioSelecionado = cenario;
                    Componentes.marcarSelecionado(botoesCenario, cenario);
                }
            });
        }
        Componentes.marcarSelecionado(botoesCenario, cenarioSelecionado);

        JButton transformar = Componentes.botao("Transformar");
        transformar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transformar();
            }
        });

        JPanel cenario = Componentes.linhaEntrada();
        cenario.add(Componentes.rotulo("Cenário:"));
        cenario.add(cenario1);
        cenario.add(cenario2);
        cenario.add(Componentes.rotulo("   "));
        cenario.add(transformar);

        return Componentes.painelAba(new JComponent[] { janela, ponto, cenario });
    }

    private JPanel criarAbaInversa() {
        valorDispositivo = criarValor();
        valorNdc = criarValor();
        valorMundo = criarValor();

        JPanel dica = Componentes.linhaEntrada();
        dica.add(Componentes.rotulo("Passe o mouse na tela (inp_to_ndc e ndc_to_user). "
                + "Clique num pixel para transformá-lo de novo."));

        return Componentes.painelAba(new JComponent[] { dica,
                linhaValor("Dispositivo", valorDispositivo),
                linhaValor("NDC", valorNdc),
                linhaValor("Mundo", valorMundo) });
    }

    private JLabel criarValor() {
        JLabel valor = new JLabel("-");
        valor.setFont(Estilo.VALOR);
        return valor;
    }

    private JPanel linhaValor(String rotulo, JLabel valor) {
        JLabel nome = Componentes.rotulo(rotulo);
        nome.setPreferredSize(new java.awt.Dimension(80, 16));
        JPanel linha = Componentes.linhaEntrada();
        linha.add(nome);
        linha.add(valor);
        return linha;
    }

    // ===== Transformações =====

    private void transformar() {
        JanelaMundo janela;
        Ponto pontoMundo;
        try {
            janela = new JanelaMundo(Componentes.lerNumero(campoXmin), Componentes.lerNumero(campoXmax),
                    Componentes.lerNumero(campoYmin), Componentes.lerNumero(campoYmax));
            pontoMundo = new Ponto(Componentes.lerNumero(campoX), Componentes.lerNumero(campoY));
        } catch (NumberFormatException erro) {
            mostrarErro("Erro", "Preencha todos os campos com numeros validos.");
            return;
        }

        if (janela.ehValida() == false) {
            mostrarErro("Janela invalida", "xmax deve ser maior que xmin e ymax maior que ymin.");
            return;
        }
        if (janela.contem(pontoMundo) == false) {
            mostrarErro("Ponto fora da janela", "O ponto precisa estar dentro da janela do mundo.");
            return;
        }

        // Dispositivo: a resolução real do monitor
        int ndh = Monitor.getNdh();
        int ndv = Monitor.getNdv();
        if (cenarioSelecionado == 0) {
            transformacao = new TransformacaoNdc01(janela, ndh, ndv);
        } else {
            transformacao = new TransformacaoNdc11(janela, ndh, ndv);
        }

        Ponto ndc = transformacao.userToNdc(pontoMundo);
        final Pixel pixel = transformacao.ndcToDc(ndc);

        // Frame buffer limpo (preto) com UM pixel verde aceso
        frameBuffer = new FrameBuffer(ndh, ndv);
        frameBuffer.setPixel(pixel.getX(), pixel.getY(), Cores.VERDE);

        painel.setFrameBuffer(frameBuffer);
        painel.setMarcador(pixel);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                painel.mostrarPixel(pixel);
            }
        });

        mensagemResultado = "(" + transformacao.getNome() + ")  Mundo " + pontoMundo
                + "  ->  NDC " + ndc + "  ->  Dispositivo " + pixel;
        status.setMensagem(mensagemResultado);

        numeroTransformacao++;
        log("--- Transformacao " + numeroTransformacao + " (" + transformacao.getNome() + ")");
        log("Janela:      " + janela);
        log("Mundo:       " + pontoMundo);
        log("NDC:         " + ndc);
        log("Dispositivo: " + pixel + "  [" + ndh + " x " + ndv + "]");
    }

    // Caminho inverso: pixel sob o mouse -> NDC -> mundo
    private void mostrarCaminhoInverso(Pixel pixel) {
        if (pixel == null) {
            status.setCoordenadas("");
            status.setMensagem(mensagemResultado);
            return;
        }
        status.setCoordenadas("(" + pixel.getX() + ", " + pixel.getY() + ")");

        Ponto ndc = transformacao.inpToNdc(pixel);
        Ponto mundo = transformacao.ndcToUser(ndc);
        String textoNdc = "(" + Componentes.formatar(ndc.getX()) + ", " + Componentes.formatar(ndc.getY()) + ")";
        String textoMundo = "(" + Componentes.formatar(mundo.getX()) + ", " + Componentes.formatar(mundo.getY()) + ")";

        valorDispositivo.setText(pixel.toString());
        valorNdc.setText(textoNdc);
        valorMundo.setText(textoMundo);
        status.setMensagem("Mouse: Dispositivo " + pixel + "  ->  NDC " + textoNdc + "  ->  Mundo " + textoMundo);
    }

    // Clique: a coordenada do pixel no mundo vira o novo ponto e é transformada.
    // Ida e volta: o mesmo pixel acende.
    private void usarPixelClicado(Pixel pixel) {
        Ponto mundo = transformacao.ndcToUser(transformacao.inpToNdc(pixel));
        JanelaMundo janela = transformacao.getJanela();

        // Limita à janela: nas bordas o double pode passar por 1e-15
        double x = Math.max(janela.getXmin(), Math.min(janela.getXmax(), mundo.getX()));
        double y = Math.max(janela.getYmin(), Math.min(janela.getYmax(), mundo.getY()));
        campoX.setText(String.valueOf(x));
        campoY.setText(String.valueOf(y));

        log("Clique em " + pixel + " -> mundo (" + Componentes.formatar(x) + ", " + Componentes.formatar(y) + ")");
        transformar();
    }

    private void limpar() {
        frameBuffer = null;
        transformacao = null;
        painel.setFrameBuffer(null);
        mensagemResultado = MENSAGEM_INICIAL;
        status.setMensagem(mensagemResultado);
        status.setCoordenadas("");
        valorDispositivo.setText("-");
        valorNdc.setText("-");
        valorMundo.setText("-");
    }
}
