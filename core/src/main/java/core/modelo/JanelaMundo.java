package core.modelo;

// Janela do mundo definida pelo usuário:
// xmin <= x <= xmax  e  ymin <= y <= ymax
public class JanelaMundo {

    private double xmin;
    private double xmax;
    private double ymin;
    private double ymax;

    public JanelaMundo(double xmin, double xmax, double ymin, double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }

    public double getXmin() {
        return xmin;
    }

    public double getXmax() {
        return xmax;
    }

    public double getYmin() {
        return ymin;
    }

    public double getYmax() {
        return ymax;
    }

    // Tamanho na horizontal: xmax - xmin
    public double getLargura() {
        return xmax - xmin;
    }

    // Tamanho na vertical: ymax - ymin
    public double getAltura() {
        return ymax - ymin;
    }

    // A janela só é válida se o máximo for maior que o mínimo
    public boolean ehValida() {
        if (xmax > xmin && ymax > ymin) {
            return true;
        }
        return false;
    }

    // Verifica se o ponto está dentro da janela
    public boolean contem(Ponto p) {
        if (p.getX() < xmin || p.getX() > xmax) {
            return false;
        }
        if (p.getY() < ymin || p.getY() > ymax) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "x de " + xmin + " a " + xmax + ", y de " + ymin + " a " + ymax;
    }
}