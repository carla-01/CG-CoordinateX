package frontend.labs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.dispositivo.Cores;
import core.dispositivo.FrameBuffer;
import core.dispositivo.Raster;
import core.modelo.Pixel;
import frontend.Navegacao;
import frontend.TelaLab;
import frontend.componentes.Componentes;
import frontend.componentes.PainelRaster;
import frontend.componentes.TelaCheia;
import lab2.circunferencia.AlgoritmoCircunferencia;
import lab2.circunferencia.CircunferenciaPolinomial;
import lab2.circunferencia.CircunferenciaPontoMedio;
import lab2.circunferencia.CircunferenciaTrigonometrica;
import lab2.reta.AlgoritmoReta;
import lab2.reta.RetaDDA;
import lab2.reta.RetaPontoMedio;

// Lab 2: rasterização de retas e circunferências.
//
// A tela do usuário é um dispositivo de ndh x ndv pixels (definido pelo
// usuário) mostrado com zoom. Como no display, a tela é limpa: fundo preto,
// sem grade e sem eixos, e os pixels são acesos na cor 00FF00.
// As coordenadas são do dispositivo: origem (0, 0) no canto superior
// esquerdo, y crescendo para baixo.
//
// Cada aba é uma ferramenta:
//   Pixel          - clique liga/desliga um pixel
//   Reta           - clique em P1 e depois em P2 (ou digite e clique em Desenhar)
//   Circunferência - clique no centro e depois num ponto da borda (define o raio)
public class TelaLab2 extends TelaLab {

    private static final int PIXEL = 0;
    private static final int RETA = 1;
    private static final int CIRCUNFERENCIA = 2;

    private static final int[] ZOOMS = { 1, 2, 4, 6, 8, 10, 12, 16, 20, 24, 32 };
    private static final int COR_FUNDO = Cores.PRETO;
    private static final int COR_PIXEL = Cores.VERDE; // 00FF00

    private final AlgoritmoReta[] algoritmosReta = { new RetaDDA(), new RetaPontoMedio() };
    private final AlgoritmoCircunferencia[] algoritmosCircunferencia = { new CircunferenciaPolinomial(),
            new CircunferenciaTrigonometrica(), new CircunferenciaPontoMedio() };

    private int ferramenta = RETA;

    private JTextField campoNdh;
    private JTextField campoNdv;
    private JComboBox<String> comboZoom;

    private JComboBox<String> comboReta;
    private JTextField campoX1;
    private JTextField campoY1;
    private JTextField campoX2;
    private JTextField campoY2;

    private JComboBox<String> comboCircunferencia;
    private JTextField campoXc;
    private JTextField campoYc;
    private JTextField campoRaio;

    private PainelRaster painel;
    private FrameBuffer frameBuffer;

    // P1 da reta ou centro da circunferência, esperando o segundo clique
    private Pixel primeiroClique;

    public TelaLab2(Navegacao navegacao) {
        super(navegacao, "Lab 2 - Retas e Circunferências",
                "Retas: DDA e Ponto Médio (Bresenham, demais octantes por simetria)\n"
                        + "Circunferências: polinomial, trigonométrico e ponto médio\n"
                        + "Coordenadas do dispositivo: origem no canto superior esquerdo.",
                "Escolha a aba e clique na grade, ou digite os pontos e clique em Desenhar.");

        painel = new PainelRaster();
        painel.setMostrarGrade(false);
        painel.setMostrarEixos(false);
        painel.setOuvinte(new PainelRaster.Ouvinte() {
            public void mouseSobre(Pixel pixel) {
                mostrarMouse(pixel);
            }

            public void clique(Pixel pixel) {
                tratarClique(pixel);
            }
        });

        final JTabbedPane abas = criarAbas();
        abas.addTab("Pixel", criarAbaPixel());
        abas.addTab("Reta", criarAbaReta());
        abas.addTab("Circunferência", criarAbaCircunferencia());
        abas.setSelectedIndex(RETA);
        abas.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ferramenta = abas.getSelectedIndex();
                cancelarPrimeiroClique();
            }
        });

        montar(criarBarraOpcoes(), painel.criarRolagem(), abas);

        criarFrameBuffer(64, 48);
        painel.setZoom(Componentes.lerZoom(comboZoom));
    }

    // ===== Montagem da tela =====

    private JPanel criarBarraOpcoes() {
        campoNdh = Componentes.campo("64", 4);
        campoNdv = Componentes.campo("48", 4);
        JButton aplicar = Componentes.botao("Aplicar");
        aplicar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aplicarResolucao();
            }
        });

        comboZoom = Componentes.comboZoom(ZOOMS, 8);
        comboZoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                painel.setZoom(Componentes.lerZoom(comboZoom));
            }
        });

        JButton limpar = Componentes.botao("Limpar");
        limpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameBuffer.limpar();
                cancelarPrimeiroClique();
                painel.atualizar();
                log("--- Tela limpa");
                status.setMensagem("Tela limpa.");
            }
        });

        JButton telaCheia = Componentes.botao("Tela cheia");
        telaCheia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaCheia.mostrar(frameBuffer);
            }
        });

        JPanel barra = Componentes.linhaEntrada();
        barra.add(Componentes.rotulo("ndh"));
        barra.add(campoNdh);
        barra.add(Componentes.rotulo("ndv"));
        barra.add(campoNdv);
        barra.add(aplicar);
        barra.add(Componentes.rotulo("   zoom"));
        barra.add(comboZoom);
        barra.add(limpar);
        barra.add(telaCheia);
        return barra;
    }

    private JPanel criarAbaPixel() {
        return Componentes.painelAba(new JComponent[] {
                linhaTexto("Clique numa célula da grade para ligar ou desligar um pixel (setPixel)."),
                linhaTexto("Passe o mouse para ver a coordenada (x, y) do dispositivo na barra de status.") });
    }

    private JPanel criarAbaReta() {
        comboReta = new JComboBox<String>();
        for (int i = 0; i < algoritmosReta.length; i++) {
            comboReta.addItem(algoritmosReta[i].getNome());
        }
        comboReta.setSelectedIndex(1);

        campoX1 = Componentes.campo("20", 3);
        campoY1 = Componentes.campo("10", 3);
        campoX2 = Componentes.campo("30", 3);
        campoY2 = Componentes.campo("18", 3);

        JButton desenhar = Componentes.botao("Desenhar");
        desenhar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desenharRetaPelosCampos();
            }
        });

        JPanel algoritmo = Componentes.linhaEntrada();
        algoritmo.add(Componentes.rotulo("Algoritmo"));
        algoritmo.add(comboReta);

        JPanel pontos = Componentes.linhaEntrada();
        pontos.add(Componentes.rotulo("P1:  x1"));
        pontos.add(campoX1);
        pontos.add(Componentes.rotulo("y1"));
        pontos.add(campoY1);
        pontos.add(Componentes.rotulo("   P2:  x2"));
        pontos.add(campoX2);
        pontos.add(Componentes.rotulo("y2"));
        pontos.add(campoY2);
        pontos.add(desenhar);

        return Componentes.painelAba(new JComponent[] { algoritmo, pontos,
                linhaTexto("Ou clique em P1 e depois em P2 na grade.") });
    }

    private JPanel criarAbaCircunferencia() {
        comboCircunferencia = new JComboBox<String>();
        for (int i = 0; i < algoritmosCircunferencia.length; i++) {
            comboCircunferencia.addItem(algoritmosCircunferencia[i].getNome());
        }
        comboCircunferencia.setSelectedIndex(2);

        campoXc = Componentes.campo("32", 3);
        campoYc = Componentes.campo("24", 3);
        campoRaio = Componentes.campo("15", 3);

        JButton desenhar = Componentes.botao("Desenhar");
        desenhar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desenharCircunferenciaPelosCampos();
            }
        });

        JPanel algoritmo = Componentes.linhaEntrada();
        algoritmo.add(Componentes.rotulo("Algoritmo"));
        algoritmo.add(comboCircunferencia);

        JPanel parametros = Componentes.linhaEntrada();
        parametros.add(Componentes.rotulo("Centro:  xc"));
        parametros.add(campoXc);
        parametros.add(Componentes.rotulo("yc"));
        parametros.add(campoYc);
        parametros.add(Componentes.rotulo("   raio"));
        parametros.add(campoRaio);
        parametros.add(desenhar);

        return Componentes.painelAba(new JComponent[] { algoritmo, parametros,
                linhaTexto("Ou clique no centro e depois num ponto da borda.") });
    }

    private JPanel linhaTexto(String texto) {
        JPanel linha = Componentes.linhaEntrada();
        linha.add(Componentes.rotulo(texto));
        return linha;
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
    }

    // Dispositivo com o fundo totalmente limpo, como o display
    private void criarFrameBuffer(int ndh, int ndv) {
        frameBuffer = new FrameBuffer(ndh, ndv, COR_FUNDO);
        painel.setFrameBuffer(frameBuffer);
        cancelarPrimeiroClique();

        String texto = "Dispositivo de " + ndh + " x " + ndv + ":  0 <= x <= " + (ndh - 1)
                + "  e  0 <= y <= " + (ndv - 1);
        log("--- " + texto);
        status.setMensagem(texto);
    }

    // ===== Mouse =====

    private void mostrarMouse(Pixel pixel) {
        if (pixel == null) {
            status.setCoordenadas("");
            return;
        }
        status.setCoordenadas("(" + pixel.getX() + ", " + pixel.getY() + ")");

        if (ferramenta == PIXEL) {
            String estado;
            if (frameBuffer.estaAceso(pixel.getX(), pixel.getY())) {
                estado = "aceso";
            } else {
                estado = "apagado";
            }
            status.setMensagem("Pixel " + pixel + " " + estado + ". Clique para ligar/desligar.");
        } else if (ferramenta == RETA) {
            if (primeiroClique == null) {
                status.setMensagem("Clique para escolher P1.");
            } else {
                int dx = pixel.getX() - primeiroClique.getX();
                int dy = pixel.getY() - primeiroClique.getY();
                String declive;
                if (dx == 0) {
                    declive = "infinito";
                } else {
                    declive = String.format(Locale.US, "%.3f", (double) dy / dx);
                }
                status.setMensagem("P1 = " + primeiroClique + "   P2 = " + pixel + "   dx = " + dx + "  dy = " + dy
                        + "  m = " + declive + "  octante " + RetaPontoMedio.descobrirOctante(dx, dy)
                        + "   (clique para desenhar)");
            }
        } else {
            if (primeiroClique == null) {
                status.setMensagem("Clique para escolher o centro.");
            } else {
                status.setMensagem("Centro = " + primeiroClique + "   raio = " + raioAte(pixel)
                        + "   (clique para desenhar)");
            }
        }
    }

    private void tratarClique(Pixel pixel) {
        if (ferramenta == PIXEL) {
            alternarPixel(pixel);
            return;
        }

        if (primeiroClique == null) {
            primeiroClique = pixel;
            painel.setMarcador(pixel);
            if (ferramenta == RETA) {
                campoX1.setText("" + pixel.getX());
                campoY1.setText("" + pixel.getY());
            } else {
                campoXc.setText("" + pixel.getX());
                campoYc.setText("" + pixel.getY());
            }
            mostrarMouse(pixel);
            return;
        }

        Pixel inicio = primeiroClique;
        cancelarPrimeiroClique();
        if (ferramenta == RETA) {
            campoX2.setText("" + pixel.getX());
            campoY2.setText("" + pixel.getY());
            desenharReta(inicio.getX(), inicio.getY(), pixel.getX(), pixel.getY());
        } else {
            int raio = raioAte(pixel);
            campoRaio.setText("" + raio);
            desenharCircunferencia(inicio.getX(), inicio.getY(), raio);
        }
    }

    private void cancelarPrimeiroClique() {
        primeiroClique = null;
        painel.setMarcador(null);
    }

    // Distância do centro até o pixel, arredondada
    private int raioAte(Pixel pixel) {
        int dx = pixel.getX() - primeiroClique.getX();
        int dy = pixel.getY() - primeiroClique.getY();
        return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
    }

    private void alternarPixel(Pixel pixel) {
        String acao;
        if (frameBuffer.estaAceso(pixel.getX(), pixel.getY())) {
            frameBuffer.setPixel(pixel.getX(), pixel.getY(), frameBuffer.getCorFundo());
            acao = "desligado";
        } else {
            frameBuffer.setPixel(pixel.getX(), pixel.getY(), COR_PIXEL);
            acao = "ligado";
        }
        log("setPixel" + pixel + " " + acao);
        painel.atualizar();
        mostrarMouse(pixel);
    }

    // ===== Desenho =====

    private void desenharRetaPelosCampos() {
        try {
            desenharReta(Componentes.lerInteiro(campoX1), Componentes.lerInteiro(campoY1),
                    Componentes.lerInteiro(campoX2), Componentes.lerInteiro(campoY2));
        } catch (NumberFormatException erro) {
            mostrarErro("Erro", "As coordenadas do dispositivo devem ser numeros inteiros.");
        }
    }

    private void desenharCircunferenciaPelosCampos() {
        try {
            int raio = Componentes.lerInteiro(campoRaio);
            if (raio < 0) {
                mostrarErro("Raio invalido", "O raio deve ser maior ou igual a zero.");
                return;
            }
            desenharCircunferencia(Componentes.lerInteiro(campoXc), Componentes.lerInteiro(campoYc), raio);
        } catch (NumberFormatException erro) {
            mostrarErro("Erro", "As coordenadas do dispositivo devem ser numeros inteiros.");
        }
    }

    private void desenharReta(int x1, int y1, int x2, int y2) {
        AlgoritmoReta algoritmo = algoritmosReta[comboReta.getSelectedIndex()];
        List<Pixel> pixels = new ArrayList<Pixel>();

        algoritmo.desenhar(registrar(pixels), x1, y1, x2, y2, COR_PIXEL);

        terminarDesenho("Reta " + algoritmo.getNome() + ": (" + x1 + ", " + y1 + ") -> (" + x2 + ", " + y2
                + "), octante " + RetaPontoMedio.descobrirOctante(x2 - x1, y2 - y1), pixels);
    }

    private void desenharCircunferencia(int xc, int yc, int raio) {
        AlgoritmoCircunferencia algoritmo = algoritmosCircunferencia[comboCircunferencia.getSelectedIndex()];
        List<Pixel> pixels = new ArrayList<Pixel>();

        algoritmo.desenhar(registrar(pixels), xc, yc, raio, COR_PIXEL);

        terminarDesenho("Circunferência " + algoritmo.getNome() + ": centro (" + xc + ", " + yc + "), raio "
                + raio, pixels);
    }

    // Raster que acende o pixel no frame buffer e anota a ordem
    private Raster registrar(final List<Pixel> pixels) {
        return new Raster() {
            public void setPixel(int x, int y, int cor) {
                pixels.add(new Pixel(x, y));
                frameBuffer.setPixel(x, y, cor);
            }
        };
    }

    // Escreve no log cada setPixel, na ordem em que o algoritmo chamou
    private void terminarDesenho(String titulo, List<Pixel> pixels) {
        log("--- " + titulo);

        int fora = 0;
        StringBuilder linha = new StringBuilder();
        for (int i = 0; i < pixels.size(); i++) {
            Pixel p = pixels.get(i);
            linha.append(String.format("%3d: ", i + 1)).append(p);
            if (frameBuffer.contem(p.getX(), p.getY()) == false) {
                linha.append(" fora");
                fora++;
            }
            log(linha.toString());
            linha.setLength(0);
        }
        painel.atualizar();

        String mensagem = titulo + "  |  " + pixels.size() + " chamadas de setPixel";
        if (fora > 0) {
            mensagem += " (" + fora + " fora da tela)";
        }
        status.setMensagem(mensagem);
    }
}
