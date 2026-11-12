package frontend.labs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import frontend.Navegacao;
import frontend.TelaLab;
import frontend.componentes.Componentes;
import frontend.componentes.Estilo;
import lab1extra.tabelacores.ControladorVideo;
import lab1extra.tabelacores.FrameBufferIndexado;
import lab1extra.tabelacores.Hexa;
import lab1extra.tabelacores.TabelaCores;

// Lab 1 Extra: tabela de cores (24 bits) para pixel de 12 bits.
// Usa só o núcleo do Lab 1 - Extra (TabelaCores, FrameBufferIndexado,
// ControladorVideo); aqui fica apenas a interface.
public class TelaLab1Extra extends TelaLab {

    private static final int TAMANHO_PALETA = 384; // 64 x 64 blocos de 6 x 6
    private static final int BLOCO = TAMANHO_PALETA / 64;

    private TabelaCores tabelaCores;
    private ControladorVideo controladorVideo;
    private BufferedImage imagemPaleta;
    private int indiceCorAtual;

    private JButton[] botoesTabela;
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

    public TelaLab1Extra(Navegacao navegacao) {
        super(navegacao, "Lab 1 Extra - Tabela de Cores",
                "Pixel de 12 bits -> 4096 posicoes na tabela\n"
                        + "Cada posicao guarda uma cor de 24 bits (#RRGGBB)\n"
                        + "Memoria da tabela: " + TabelaCores.calcularBytes() + " bytes",
                "Clique na paleta ou digite um indice.");

        tabelaCores = new TabelaCores();
        FrameBufferIndexado frameBuffer = new FrameBufferIndexado(TAMANHO_PALETA, TAMANHO_PALETA);
        frameBuffer.desenharTodosOsIndices();
        controladorVideo = new ControladorVideo(frameBuffer, tabelaCores);

        JTabbedPane abas = criarAbas();
        abas.addTab("Tabela de cores", criarAbaTabela());
        abas.addTab("Consultar índice", criarAbaIndice());
        abas.addTab("Memória", criarAbaMemoria());

        montar(null, criarAreaTabela(), abas);

        imagemPaleta = controladorVideo.gerarImagem();
        selecionarIndiceCor(0x180);
    }

    private JPanel criarAbaTabela() {
        JButton colorida = Componentes.botao("Colorida (0xRGB)");
        JButton cinza = Componentes.botao("Tons de cinza");
        JButton fogo = Componentes.botao("Fogo");
        botoesTabela = new JButton[] { colorida, cinza, fogo };

        for (int i = 0; i < botoesTabela.length; i++) {
            final int tipo = i;
            botoesTabela[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    trocarTabelaCores(tipo);
                }
            });
        }
        Componentes.marcarSelecionado(botoesTabela, 0);

        JPanel linhaBotoes = Componentes.linhaEntrada();
        linhaBotoes.add(Componentes.rotulo("Preenchimento da tabela:"));
        linhaBotoes.add(colorida);
        linhaBotoes.add(cinza);
        linhaBotoes.add(fogo);

        JPanel dica = Componentes.linhaEntrada();
        dica.add(Componentes.rotulo("Trocar a tabela muda só as cores: o frame buffer (os índices) continua o mesmo."));

        return Componentes.painelAba(new JComponent[] { linhaBotoes, dica });
    }

    private JPanel criarAbaMemoria() {
        int lado = 512;
        int bytesFrameBuffer = lado * lado * 12 / 8;
        int bytesTabela = TabelaCores.calcularBytes();
        int bytesSemTabela = lado * lado * 24 / 8;

        return Componentes.painelAba(new JComponent[] {
                linhaTexto("Imagem de " + lado + " x " + lado + " pixels:"),
                linhaTexto("Frame buffer (12 bits/pixel):  " + bytesFrameBuffer + " bytes"),
                linhaTexto("Tabela de cores (4096 x 24 bits):  " + bytesTabela + " bytes"),
                linhaTexto("Total com tabela:  " + (bytesFrameBuffer + bytesTabela) + " bytes"
                        + "      Sem tabela (24 bits/pixel):  " + bytesSemTabela + " bytes") });
    }

    private JPanel linhaTexto(String texto) {
        JPanel linha = Componentes.linhaEntrada();
        linha.add(Componentes.rotulo(texto));
        return linha;
    }

    private JPanel criarAbaIndice() {
        campoIndiceCor = Componentes.campo("0x180", 6);
        JButton verCor = Componentes.botao("Ver cor");

        ActionListener lerCampo = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selecionarIndicePeloCampo();
            }
        };
        campoIndiceCor.addActionListener(lerCampo); // Enter
        verCor.addActionListener(lerCampo);

        JPanel linha = Componentes.linhaEntrada();
        linha.add(Componentes.rotulo("Índice do pixel (0 a 4095 ou 0x000 a 0xFFF)"));
        linha.add(campoIndiceCor);
        linha.add(verCor);

        JPanel dica = Componentes.linhaEntrada();
        dica.add(Componentes.rotulo("Ou clique em um bloco da paleta."));

        return Componentes.painelAba(new JComponent[] { linha, dica });
    }

    private JPanel criarAreaTabela() {
        JPanel area = new JPanel(new BorderLayout(6, 0));
        area.setBackground(Estilo.FACE);
        area.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        // Paleta: a imagem gerada pelo controlador de vídeo (64 x 64 índices)
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
                int coluna = e.getX() / BLOCO;
                int linha = e.getY() / BLOCO;
                if (coluna < 64 && linha < 64) {
                    selecionarIndiceCor(linha * 64 + coluna);
                }
            }
        });

        JPanel moldura = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        moldura.setBackground(Estilo.FACE);
        moldura.setBorder(new BevelBorder(BevelBorder.LOWERED));
        moldura.add(painelPaletaCores);

        area.add(moldura, BorderLayout.CENTER);
        area.add(criarPainelInfoCor(), BorderLayout.EAST);
        return area;
    }

    private void desenharPaleta(Graphics g) {
        g.drawImage(imagemPaleta, 0, 0, null);

        // Moldura branca e preta no bloco selecionado
        int x = (indiceCorAtual % 64) * BLOCO;
        int y = (indiceCorAtual / 64) * BLOCO;
        g.setColor(Color.WHITE);
        g.drawRect(x - 1, y - 1, BLOCO + 1, BLOCO + 1);
        g.setColor(Color.BLACK);
        g.drawRect(x - 2, y - 2, BLOCO + 3, BLOCO + 3);
    }

    private JPanel criarPainelInfoCor() {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setBackground(Estilo.FACE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                new BevelBorder(BevelBorder.LOWERED),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        painel.setPreferredSize(new Dimension(230, 10));

        amostraCor = new JPanel();
        amostraCor.setPreferredSize(new Dimension(10, 70));
        amostraCor.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel grade = new JPanel(new GridLayout(0, 2, 4, 6));
        grade.setBackground(Estilo.FACE);

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

        // O nome da tabela ocupa a linha inteira (não cabe na coluna da grade)
        JPanel linhaTabela = new JPanel(new BorderLayout());
        linhaTabela.setBackground(Estilo.FACE);
        linhaTabela.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        linhaTabela.add(new JLabel("Tabela"), BorderLayout.NORTH);
        linhaTabela.add(valorNomeTabela, BorderLayout.CENTER);

        // Deixa as linhas com a altura normal, sem esticar
        JPanel informacoes = new JPanel(new BorderLayout());
        informacoes.setBackground(Estilo.FACE);
        informacoes.add(grade, BorderLayout.NORTH);
        informacoes.add(linhaTabela, BorderLayout.CENTER);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(Estilo.FACE);
        conteudo.add(informacoes, BorderLayout.NORTH);

        painel.add(amostraCor, BorderLayout.NORTH);
        painel.add(conteudo, BorderLayout.CENTER);
        return painel;
    }

    private JLabel criarValorInfo() {
        JLabel valor = new JLabel("-");
        valor.setFont(Estilo.VALOR);
        return valor;
    }

    // Mostra a cor de um índice: consulta a tabela (12 bits -> 24 bits)
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
        String consulta = "Pixel " + Hexa.indice(indice) + " (12 bits)  ->  tabela["
                + indice + "]  ->  " + Hexa.cor(cor) + " (24 bits)";
        status.setMensagem(consulta);
        log(consulta);
        painelPaletaCores.repaint();
    }

    private void selecionarIndicePeloCampo() {
        int indice = Hexa.lerIndice(campoIndiceCor.getText());

        if (indice < 0 || indice >= TabelaCores.TAMANHO) {
            mostrarErro("Indice invalido", "Digite de 0 a 4095 (ou 0x000 a 0xFFF).");
            return;
        }
        selecionarIndiceCor(indice);
    }

    // Troca a tabela; o frame buffer (os índices) continua o mesmo
    private void trocarTabelaCores(int tipo) {
        if (tipo == 0) {
            tabelaCores.preencherColorida();
        } else if (tipo == 1) {
            tabelaCores.preencherCinza();
        } else {
            tabelaCores.preencherFogo();
        }
        Componentes.marcarSelecionado(botoesTabela, tipo);
        log("--- Tabela: " + tabelaCores.getNome());

        imagemPaleta = controladorVideo.gerarImagem();
        selecionarIndiceCor(indiceCorAtual);
    }
}
