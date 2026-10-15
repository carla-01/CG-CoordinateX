package frontend.labs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
// sem grade, e os pixels são acesos na cor 00FF00.
//
// Dois sistemas de coordenadas (caixa "Plano cartesiano"):
//   ligada    - plano cartesiano: origem (0, 0) no centro da tela, y para
//               cima, com os eixos x e y e os 8 octantes desenhados por cima
//               (não acendem pixels). Aceita números negativos.
//   desligada - dispositivo: origem no canto superior esquerdo, y para baixo.
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
    private JCheckBox caixaPlano;

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
    // (no sistema de coordenadas em uso)
    private Pixel primeiroClique;

    public TelaLab2(Navegacao navegacao) {
        super(navegacao, "Lab 2 - Retas e Circunferências",
                "Retas: DDA e Ponto Médio (Bresenham, demais octantes por simetria)\n"
                        + "Circunferências: polinomial, trigonométrico e ponto médio\n"
                        + "Plano cartesiano: origem no centro, y para cima (desligado: dispositivo).",
                "Escolha a aba e clique na grade, ou digite os pontos e clique em Desenhar.");

        painel = new PainelRaster();
        painel.setMostrarGrade(false);
        painel.setPlanoCartesiano(true);
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

        caixaPlano = new JCheckBox("Plano cartesiano", true);
        caixaPlano.setOpaque(false);
        caixaPlano.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trocarSistema();
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
        barra.add(caixaPlano);
        barra.add(telaCheia);
        return barra;
    }

    private JPanel criarAbaPixel() {
        return Componentes.painelAba(new JComponent[] {
                linhaTexto("Clique numa célula da grade para ligar ou desligar um pixel (setPixel)."),
                linhaTexto("Passe o mouse para ver a coordenada (x, y) na barra de status.") });
    }

    private JPanel criarAbaReta() {
        comboReta = new JComboBox<String>();
        for (int i = 0; i < algoritmosReta.length; i++) {
            comboReta.addItem(algoritmosReta[i].getNome());
        }
        comboReta.setSelectedIndex(1);

        // valores iniciais no plano cartesiano
        campoX1 = Componentes.campo("0", 3);
        campoY1 = Componentes.campo("0", 3);
        campoX2 = Componentes.campo("12", 3);
        campoY2 = Componentes.campo("7", 3);

        JButton desenhar = Componentes.botao("Desenhar");
        desenhar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desenharRetaPelosCampos();
            }
        });

        JButton testarOctantes = Componentes.botao("Testar os 8 octantes");
        testarOctantes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testarOctantes();
            }
        });

        JPanel algoritmo = Componentes.linhaEntrada();
        algoritmo.add(Componentes.rotulo("Algoritmo"));
        algoritmo.add(comboReta);
        algoritmo.add(testarOctantes);

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

        campoXc = Componentes.campo("0", 3);
        campoYc = Componentes.campo("0", 3);
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

    // ===== Sistema de coordenadas =====

    // null = coordenadas do dispositivo
    private PlanoCartesiano plano() {
        return painel.getPlano();
    }

    // Pixel do dispositivo -> ponto no sistema em uso
    private Pixel noSistema(Pixel dispositivo) {
        PlanoCartesiano plano = plano();
        if (plano == null) {
            return dispositivo;
        }
        return plano.paraCartesiano(dispositivo);
    }

    // Ponto no sistema em uso -> pixel do dispositivo
    private Pixel noDispositivo(int x, int y) {
        PlanoCartesiano plano = plano();
        if (plano == null) {
            return new Pixel(x, y);
        }
        return plano.paraDispositivo(x, y);
    }

    // Liga/desliga o plano cartesiano e converte os pontos já digitados
    private void trocarSistema() {
        PlanoCartesiano antes = plano();
        painel.setPlanoCartesiano(caixaPlano.isSelected());
        PlanoCartesiano depois = plano();
        cancelarPrimeiroClique();

        JTextField[][] pontos = { { campoX1, campoY1 }, { campoX2, campoY2 }, { campoXc, campoYc } };
        for (int i = 0; i < pontos.length; i++) {
            try {
                Pixel p = new Pixel(Componentes.lerInteiro(pontos[i][0]), Componentes.lerInteiro(pontos[i][1]));
                if (antes != null) {
                    p = antes.paraDispositivo(p.getX(), p.getY());
                }
                if (depois != null) {
                    p = depois.paraCartesiano(p);
                }
                pontos[i][0].setText("" + p.getX());
                pontos[i][1].setText("" + p.getY());
            } catch (NumberFormatException erro) {
                // campo inválido: deixa como está
            }
        }

        String texto;
        if (depois == null) {
            texto = "Coordenadas do dispositivo: origem no canto superior esquerdo, y para baixo";
        } else {
            texto = "Plano cartesiano: origem no centro, y para cima";
        }
        log("--- " + texto);
        status.setMensagem(texto);
    }

    // ===== Mouse =====

    private void mostrarMouse(Pixel pixel) {
        if (pixel == null) {
            status.setCoordenadas("");
            return;
        }
        Pixel ponto = noSistema(pixel);
        if (plano() == null) {
            status.setCoordenadas("(" + pixel.getX() + ", " + pixel.getY() + ")");
        } else {
            status.setCoordenadas("x = " + ponto.getX() + "  y = " + ponto.getY() + "   (dispositivo " + pixel + ")");
        }

        if (ferramenta == PIXEL) {
            String estado;
            if (frameBuffer.estaAceso(pixel.getX(), pixel.getY())) {
                estado = "aceso";
            } else {
                estado = "apagado";
            }
            status.setMensagem("Pixel " + ponto + " " + estado + ". Clique para ligar/desligar.");
        } else if (ferramenta == RETA) {
            if (primeiroClique == null) {
                status.setMensagem("Clique para escolher P1.");
            } else {
                int dx = ponto.getX() - primeiroClique.getX();
                int dy = ponto.getY() - primeiroClique.getY();
                String declive;
                if (dx == 0) {
                    declive = "infinito";
                } else {
                    declive = String.format(Locale.US, "%.3f", (double) dy / dx);
                }
                status.setMensagem("P1 = " + primeiroClique + "   P2 = " + ponto + "   dx = " + dx + "  dy = " + dy
                        + "  m = " + declive + "  octante " + RetaPontoMedio.descobrirOctante(dx, dy)
                        + "   (clique para desenhar)");
            }
        } else {
            if (primeiroClique == null) {
                status.setMensagem("Clique para escolher o centro.");
            } else {
                status.setMensagem("Centro = " + primeiroClique + "   raio = " + raioAte(ponto)
                        + "   (clique para desenhar)");
            }
        }
    }

    private void tratarClique(Pixel pixel) {
        if (ferramenta == PIXEL) {
            alternarPixel(pixel);
            return;
        }

        Pixel ponto = noSistema(pixel);
        if (primeiroClique == null) {
            primeiroClique = ponto;
            painel.setMarcador(pixel);
            if (ferramenta == RETA) {
                campoX1.setText("" + ponto.getX());
                campoY1.setText("" + ponto.getY());
            } else {
                campoXc.setText("" + ponto.getX());
                campoYc.setText("" + ponto.getY());
            }
            mostrarMouse(pixel);
            return;
        }

        Pixel inicio = primeiroClique;
        cancelarPrimeiroClique();
        if (ferramenta == RETA) {
            campoX2.setText("" + ponto.getX());
            campoY2.setText("" + ponto.getY());
            desenharReta(inicio.getX(), inicio.getY(), ponto.getX(), ponto.getY());
        } else {
            int raio = raioAte(ponto);
            campoRaio.setText("" + raio);
            desenharCircunferencia(inicio.getX(), inicio.getY(), raio);
        }
    }

    private void cancelarPrimeiroClique() {
        primeiroClique = null;
        painel.setMarcador(null);
    }

    // Distância do centro até o ponto, arredondada
    private int raioAte(Pixel ponto) {
        int dx = ponto.getX() - primeiroClique.getX();
        int dy = ponto.getY() - primeiroClique.getY();
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
        log("setPixel" + noSistema(pixel) + " " + acao);
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

    // Desenha, a partir da origem, uma reta dentro de cada octante, com x e y
    // positivos e negativos, e confere no log se cada pixel caiu no octante
    // certo. Liga o plano cartesiano, onde os octantes são os da matemática.
    private void testarOctantes() {
        if (caixaPlano.isSelected() == false) {
            caixaPlano.setSelected(true);
            trocarSistema();
        }

        int longo = Math.min(frameBuffer.getNdh(), frameBuffer.getNdv()) / 2 - 3;
        int curto = longo * 2 / 5;
        if (curto < 1) {
            mostrarErro("Tela pequena", "Aumente ndh e ndv para testar os octantes.");
            return;
        }
        int[][] fins = { { longo, curto }, { curto, longo }, { -curto, longo }, { -longo, curto },
                { -longo, -curto }, { -curto, -longo }, { curto, -longo }, { longo, -curto } };

        AlgoritmoReta algoritmo = algoritmosReta[comboReta.getSelectedIndex()];
        log("--- Teste dos 8 octantes (" + algoritmo.getNome() + "): retas saindo de (0, 0)");
        int erros = 0;
        for (int i = 0; i < fins.length; i++) {
            int esperado = i + 1;
            int x2 = fins[i][0];
            int y2 = fins[i][1];
            List<Pixel> pixels = new ArrayList<Pixel>();
            algoritmo.desenhar(registrar(pixels), 0, 0, x2, y2, COR_PIXEL);

            int calculado = RetaPontoMedio.descobrirOctante(x2, y2);
            int foraDoOctante = 0;
            for (int k = 0; k < pixels.size(); k++) {
                Pixel p = pixels.get(k);
                if (RetaPontoMedio.estaNoOctante(esperado, p.getX(), p.getY()) == false) {
                    foraDoOctante++;
                }
            }
            Pixel ultimo = pixels.get(pixels.size() - 1);
            boolean chegou = ultimo.getX() == x2 && ultimo.getY() == y2;

            String resultado;
            if (calculado == esperado && foraDoOctante == 0 && chegou) {
                resultado = "OK";
            } else {
                resultado = "ERRO";
                erros++;
            }
            log("octante " + esperado + ": (0, 0) -> (" + x2 + ", " + y2 + ")  calculado " + calculado + ", "
                    + pixels.size() + " pixels, " + foraDoOctante + " fora do octante, termina em " + ultimo
                    + "  " + resultado);
        }
        painel.atualizar();

        if (erros == 0) {
            status.setMensagem("Teste dos 8 octantes: todas as retas no octante certo.");
        } else {
            status.setMensagem("Teste dos 8 octantes: " + erros + " reta(s) com erro, veja o log.");
        }
    }

    // Raster que acende o pixel no frame buffer e anota a ordem.
    // Os pontos chegam no sistema em uso (plano cartesiano ou dispositivo).
    private Raster registrar(final List<Pixel> pixels) {
        PlanoCartesiano plano = plano();
        final Raster destino;
        if (plano == null) {
            destino = frameBuffer;
        } else {
            destino = plano.criarRaster(frameBuffer);
        }
        return new Raster() {
            public void setPixel(int x, int y, int cor) {
                pixels.add(new Pixel(x, y));
                destino.setPixel(x, y, cor);
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
            Pixel d = noDispositivo(p.getX(), p.getY());
            if (frameBuffer.contem(d.getX(), d.getY()) == false) {
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
