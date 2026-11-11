package lab3.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Polígono = lista de vértices em ordem (o último liga ao primeiro).
// Imutável: transformar() devolve um polígono novo.
public final class Poligono {

    private final List<Vertice> vertices;

    public Poligono(List<Vertice> vertices) {
        if (vertices == null || vertices.isEmpty()) {
            throw new IllegalArgumentException("O polígono precisa de pelo menos um vértice.");
        }
        this.vertices = Collections.unmodifiableList(new ArrayList<Vertice>(vertices));
    }

    // xy[i] = { x, y }
    public static Poligono deVertices(double[][] xy) {
        List<Vertice> lista = new ArrayList<Vertice>();
        for (int i = 0; i < xy.length; i++) {
            lista.add(new Vertice(xy[i][0], xy[i][1]));
        }
        return new Poligono(lista);
    }

    // Polígono regular inscrito numa circunferência de centro (0, 0).
    // O primeiro vértice fica em cima (90°) e os demais seguem no sentido
    // anti-horário: vértice k em ângulo 90° + k·360°/n.
    public static Poligono regular(int lados, double raio) {
        if (lados < 3) {
            throw new IllegalArgumentException("Um polígono precisa de pelo menos 3 lados.");
        }
        if (raio <= 0) {
            throw new IllegalArgumentException("O raio deve ser maior que zero.");
        }
        List<Vertice> lista = new ArrayList<Vertice>();
        for (int k = 0; k < lados; k++) {
            double angulo = Math.toRadians(90.0 + 360.0 * k / lados);
            lista.add(new Vertice(raio * Math.cos(angulo), raio * Math.sin(angulo)));
        }
        return new Poligono(lista);
    }

    // Pentágono irregular, convexo e sem simetria (para a reflexão e a
    // rotação "aparecerem"). A média dos vértices já é (0, 0).
    public static Poligono irregularPadrao() {
        return deVertices(new double[][] { { -8, -7 }, { 6, -8 }, { 9, 2 }, { 1, 9 }, { -8, 4 } });
    }

    // Retângulo de lados paralelos aos eixos (contorno da janela do usuário
    // ou da viewport). Vértices no sentido anti-horário, a partir de (xmin, ymin).
    public static Poligono retangulo(double xmin, double xmax, double ymin, double ymax) {
        if (xmax <= xmin || ymax <= ymin) {
            throw new IllegalArgumentException("Precisa de xmin < xmax e ymin < ymax.");
        }
        return deVertices(new double[][] { { xmin, ymin }, { xmax, ymin }, { xmax, ymax }, { xmin, ymax } });
    }

    public int getQuantidade() {
        return vertices.size();
    }

    public Vertice get(int i) {
        return vertices.get(i);
    }

    public List<Vertice> getVertices() {
        return vertices;
    }

    // Média dos vértices
    public Vertice centroide() {
        double sx = 0;
        double sy = 0;
        for (int i = 0; i < vertices.size(); i++) {
            sx += vertices.get(i).getX();
            sy += vertices.get(i).getY();
        }
        return new Vertice(sx / vertices.size(), sy / vertices.size());
    }

    // Translada o polígono para que a média dos vértices fique em (0, 0)
    public Poligono centralizadoNaOrigem() {
        Vertice c = centroide();
        List<Vertice> lista = new ArrayList<Vertice>();
        for (int i = 0; i < vertices.size(); i++) {
            lista.add(new Vertice(vertices.get(i).getX() - c.getX(), vertices.get(i).getY() - c.getY()));
        }
        return new Poligono(lista);
    }

    // P' = M · P para cada vértice
    public Poligono transformar(Matriz3 matriz) {
        List<Vertice> lista = new ArrayList<Vertice>();
        for (int i = 0; i < vertices.size(); i++) {
            double[] p = matriz.aplicar(vertices.get(i).getX(), vertices.get(i).getY());
            lista.add(new Vertice(p[0], p[1]));
        }
        return new Poligono(lista);
    }
}
