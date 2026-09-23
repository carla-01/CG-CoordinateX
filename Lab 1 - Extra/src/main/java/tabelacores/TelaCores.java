package tabelacores;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Monta a janela e liga a paleta com o inspetor.
public class TelaCores {

    private TabelaCores tabela;
    private PainelPaleta paleta;
    private PainelInspetor inspetor;
    private int indiceAtual;

    public TelaCores(TabelaCores tabela, ControladorVideo controlador, int tamanhoPaleta) {
        this.tabela = tabela;
        this.paleta = new PainelPaleta(controlador, tamanhoPaleta);
        this.inspetor = new PainelInspetor(tabela);
        this.indiceAtual = 0x180;
    }

    public void mostrar() {
        JFrame janela = new JFrame("Tabela de Cores - 24 bits / pixel de 12 bits");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setResizable(false);

        JPanel fundo = new JPanel(new BorderLayout(20, 16));
        fundo.setBackground(Estilo.FUNDO);
        fundo.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        fundo.add(montarCabecalho(), BorderLayout.NORTH);
        fundo.add(montarMolduraPaleta(), BorderLayout.CENTER);
        fundo.add(inspetor, BorderLayout.EAST);

        ligarEventos();
        selecionarIndice(indiceAtual);

        janela.setContentPane(fundo);
        janela.pack();
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }

    private JPanel montarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout(0, 4));
        cabecalho.setBackground(Estilo.FUNDO);

        JLabel titulo = new JLabel("Tabela de Cores");
        titulo.setFont(Estilo.TITULO);
        titulo.setForeground(Estilo.TEXTO);

        JLabel subtitulo = new JLabel("Pixel de 12 bits (0x000 a 0xFFF)  →  cor de 24 bits (#RRGGBB)");
        subtitulo.setFont(Estilo.SUBTITULO);
        subtitulo.setForeground(Estilo.TEXTO_FRACO);

        cabecalho.add(titulo, BorderLayout.NORTH);
        cabecalho.add(subtitulo, BorderLayout.SOUTH);
        return cabecalho;
    }

    private JPanel montarMolduraPaleta() {
        JPanel moldura = new JPanel();
        moldura.setBackground(Estilo.CARTAO);
        moldura.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        moldura.add(paleta);
        return moldura;
    }

    private void ligarEventos() {
        OuvinteCampoIndice ouvinteCampo = new OuvinteCampoIndice(this);
        inspetor.getCampoIndice().addActionListener(ouvinteCampo); // Enter
        inspetor.getBotaoVer().addActionListener(ouvinteCampo);

        paleta.addMouseListener(new OuvinteCliquePaleta(this, paleta));

        inspetor.getBotaoColorida().addActionListener(new OuvinteBotaoTabela(this, 1));
        inspetor.getBotaoCinza().addActionListener(new OuvinteBotaoTabela(this, 2));
        inspetor.getBotaoFogo().addActionListener(new OuvinteBotaoTabela(this, 3));
    }

    // Mostra a cor de um índice na paleta e no inspetor
    public void selecionarIndice(int indice) {
        indiceAtual = indice;
        paleta.setIndiceSelecionado(indice);
        inspetor.mostrarIndice(indice);
    }

    // Lê o que foi digitado e seleciona, se for válido
    public void selecionarPeloCampo() {
        int indice = Hexa.lerIndice(inspetor.getTextoDigitado());

        if (indice < 0 || indice >= TabelaCores.TAMANHO) {
            inspetor.mostrarErro("Digite de 0 a 4095 (ou 0x000 a 0xFFF)");
            return;
        }
        selecionarIndice(indice);
    }

    public void trocarTabela(int tipo) {
        if (tipo == 1) {
            tabela.preencherColorida();
        } else if (tipo == 2) {
            tabela.preencherCinza();
        } else {
            tabela.preencherFogo();
        }

        System.out.println("Tabela trocada para: " + tabela.getNome());
        paleta.atualizar();
        inspetor.mostrarIndice(indiceAtual);
    }
}