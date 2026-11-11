package lab3.modelo;

// Vértice de um polígono, com coordenadas reais (as transformações
// trabalham com números reais; só o desenho arredonda para pixels).
public final class Vertice {

    private final double x;
    private final double y;

    public Vertice(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + Matriz3.formatar(x) + ", " + Matriz3.formatar(y) + ")";
    }
}
