package modelo;

import java.util.Locale;

// Um ponto 2D com coordenadas reais (double), no sistema do MUNDO.
// Nas transformações ele é tratado em coordenadas homogêneas: (x, y, 1).
public class Ponto {

    private double x;
    private double y;

    public Ponto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String toString() {
        return String.format(Locale.US, "(%.2f, %.2f)", x, y);
    }
}
