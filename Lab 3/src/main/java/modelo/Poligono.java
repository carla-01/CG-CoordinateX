package modelo;

import transformacoes.Matriz;

import java.util.ArrayList;
import java.util.List;

// Polígono = lista de vértices ligados em ordem (o último liga no primeiro).
// Os dois polígonos de exemplo são criados JÁ NA ORIGEM do sistema cartesiano.
public class Poligono {

    private List<Ponto> vertices = new ArrayList<Ponto>();

    public Poligono(List<Ponto> vertices) {
        this.vertices = vertices;
    }

    // ----- Polígono REGULAR -----
    // n vértices sobre uma circunferência de raio r com centro em (0, 0).
    // O polígono fica sempre "de pé", com um lado reto embaixo:
    //  - nº de lados ÍMPAR: primeiro vértice para cima (90 graus);
    //  - nº de lados PAR: gira meio passo (180/n graus) a mais. Sem isso
    //    o quadrado (n = 4) teria vértices em 90, 180, 270 e 0 graus,
    //    ou seja, em cima dos eixos, e apareceria como um losango.
    public static Poligono regular(int lados, double raio) {
        double inicio = (lados % 2 == 0) ? 90 + 180.0 / lados : 90;
        List<Ponto> lista = new ArrayList<Ponto>();
        for (int i = 0; i < lados; i++) {
            double angulo = Math.toRadians(inicio + 360.0 * i / lados);
            double x = raio * Math.cos(angulo);
            double y = raio * Math.sin(angulo);
            lista.add(new Ponto(x, y));
        }
        return new Poligono(lista);
    }

    // ----- Polígono IRREGULAR -----
    // Uma "bandeira" (parecida com a figura do slide de reflexão da aula G5).
    // Não é simétrica nem em x nem em y, então toda reflexão fica visível.
    // Os valores abaixo valem para tamanho = 60 e são escalados pelo tamanho.
    public static Poligono irregular(double tamanho) {
        double[][] forma = {
                { -40, -60 },
                { -40, 60 },
                { 50, 60 },
                { 20, 30 },
                { 50, 0 },
                { -20, 0 },
                { -20, -60 }
        };
        double k = tamanho / 60.0;
        List<Ponto> lista = new ArrayList<Ponto>();
        for (int i = 0; i < forma.length; i++) {
            lista.add(new Ponto(forma[i][0] * k, forma[i][1] * k));
        }
        return new Poligono(lista);
    }

    // Aplica a matriz em TODOS os vértices: P' = M . P
    public Poligono transformar(Matriz m) {
        List<Ponto> novos = new ArrayList<Ponto>();
        for (Ponto p : vertices) {
            novos.add(m.aplicar(p));
        }
        return new Poligono(novos);
    }

    public List<Ponto> getVertices() {
        return vertices;
    }

    // Centro geométrico = média dos vértices
    public Ponto centro() {
        double somaX = 0;
        double somaY = 0;
        for (Ponto p : vertices) {
            somaX += p.getX();
            somaY += p.getY();
        }
        return new Ponto(somaX / vertices.size(), somaY / vertices.size());
    }

    // Largura e altura do retângulo que envolve o polígono
    public double largura() {
        double min = vertices.get(0).getX();
        double max = min;
        for (Ponto p : vertices) {
            min = Math.min(min, p.getX());
            max = Math.max(max, p.getX());
        }
        return max - min;
    }

    public double altura() {
        double min = vertices.get(0).getY();
        double max = min;
        for (Ponto p : vertices) {
            min = Math.min(min, p.getY());
            max = Math.max(max, p.getY());
        }
        return max - min;
    }

    // "V1: (x, y)" um por linha
    public String listarVertices() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < vertices.size(); i++) {
            texto.append("V").append(i + 1).append(": ").append(vertices.get(i)).append("\n");
        }
        return texto.toString();
    }
}
