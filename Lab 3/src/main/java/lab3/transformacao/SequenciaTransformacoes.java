package lab3.transformacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lab3.modelo.Matriz3;

// Lê uma sequência de transformações escrita como texto e calcula a matriz
// composta. As transformações são aplicadas NA ORDEM em que aparecem
// (da esquerda para a direita).
//
//   T(tx,ty)          translação            E(sx,sy) ou E(s)    escala
//   R(graus)          rotação               EP(sx,sy,px,py)     escala c/ ponto fixo
//   RP(graus,px,py)   rotação c/ ponto fixo CisX(a)  CisY(b)    cisalhamento
//   RefX  RefY  RefO  RefYX  RefYMX         reflexões (eixo x, eixo y, origem, y=x, y=-x)
//   RefR(graus)       reflexão numa reta pela origem
//   JV(xmin,xmax,ymin,ymax,umin,umax,vmin,vmax)   janela -> porta de visão
//
// Exemplo: "T(-2,-1) E(2,2) T(2,1)" = escala por 2 com ponto fixo (2,1).
// Use PONTO como separador decimal (1.5) e vírgula/espaço entre argumentos.
public final class SequenciaTransformacoes {

    public static final class Passo {
        private final String texto;
        private final Matriz3 matriz;

        Passo(String texto, Matriz3 matriz) {
            this.texto = texto;
            this.matriz = matriz;
        }

        public String getTexto() {
            return texto;
        }

        public Matriz3 getMatriz() {
            return matriz;
        }
    }

    private static final Pattern TOKEN = Pattern.compile("[\\s;]*([A-Za-z]+)\\s*(?:\\(([^)]*)\\))?[\\s;]*");

    private final List<Passo> passos;
    private final Matriz3 total;

    private SequenciaTransformacoes(List<Passo> passos, Matriz3 total) {
        this.passos = Collections.unmodifiableList(passos);
        this.total = total;
    }

    public List<Passo> getPassos() {
        return passos;
    }

    // Matriz da sequência inteira: Mn · ... · M1
    public Matriz3 getMatrizTotal() {
        return total;
    }

    public static SequenciaTransformacoes ler(String texto) {
        String s = texto == null ? "" : texto.trim();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Digite pelo menos uma transformação, ex.: T(2,1) R(30)");
        }
        List<Passo> passos = new ArrayList<Passo>();
        Matriz3 total = Matriz3.identidade();
        Matcher m = TOKEN.matcher(s);
        int pos = 0;
        while (pos < s.length()) {
            m.region(pos, s.length());
            if (!m.lookingAt()) {
                throw new IllegalArgumentException("Não entendi o trecho: " + s.substring(pos));
            }
            pos = m.end();
            String nome = m.group(1);
            String args = m.group(2);
            String original = nome + (args == null ? "" : "(" + args.trim() + ")");
            Matriz3 matriz = criar(nome.toLowerCase(), lerArgumentos(args, original), original);
            passos.add(new Passo(original, matriz));
            total = matriz.multiplicar(total);
        }
        return new SequenciaTransformacoes(passos, total);
    }

    private static double[] lerArgumentos(String args, String original) {
        if (args == null || args.trim().isEmpty()) {
            return new double[0];
        }
        String[] partes = args.trim().split("[\\s,;]+");
        double[] v = new double[partes.length];
        for (int i = 0; i < partes.length; i++) {
            try {
                v[i] = Double.parseDouble(partes[i]);
            } catch (NumberFormatException erro) {
                throw new IllegalArgumentException("Número inválido em " + original + ": " + partes[i]
                        + " (use ponto decimal)");
            }
            if (Double.isNaN(v[i]) || Double.isInfinite(v[i])) {
                throw new IllegalArgumentException("Número inválido em " + original);
            }
        }
        return v;
    }

    private static Matriz3 criar(String nome, double[] a, String original) {
        switch (nome) {
        case "t":
            exigir(a, 2, original);
            return Transformacoes.translacao(a[0], a[1]);
        case "e":
            if (a.length == 1) {
                return Transformacoes.escala(a[0], a[0]);
            }
            exigir(a, 2, original);
            return Transformacoes.escala(a[0], a[1]);
        case "r":
            exigir(a, 1, original);
            return Transformacoes.rotacao(a[0]);
        case "ep":
            exigir(a, 4, original);
            return Transformacoes.escalaComPontoFixo(a[0], a[1], a[2], a[3]);
        case "rp":
            exigir(a, 3, original);
            return Transformacoes.rotacaoComPontoFixo(a[0], a[1], a[2]);
        case "cisx":
            exigir(a, 1, original);
            return Transformacoes.cisalhamentoX(a[0]);
        case "cisy":
            exigir(a, 1, original);
            return Transformacoes.cisalhamentoY(a[0]);
        case "refx":
            exigir(a, 0, original);
            return Transformacoes.reflexaoEixoX();
        case "refy":
            exigir(a, 0, original);
            return Transformacoes.reflexaoEixoY();
        case "refo":
            exigir(a, 0, original);
            return Transformacoes.reflexaoOrigem();
        case "refyx":
            exigir(a, 0, original);
            return Transformacoes.reflexaoRetaYigualX();
        case "refymx":
            exigir(a, 0, original);
            return Transformacoes.reflexaoRetaYigualMenosX();
        case "refr":
            exigir(a, 1, original);
            return Transformacoes.reflexaoEmReta(a[0]);
        case "jv":
            exigir(a, 8, original);
            return Transformacoes.janelaViewport(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]);
        default:
            throw new IllegalArgumentException("Transformação desconhecida: " + original);
        }
    }

    private static void exigir(double[] a, int quantidade, String original) {
        if (a.length != quantidade) {
            throw new IllegalArgumentException(original + " precisa de " + quantidade + " argumento(s).");
        }
    }
}
