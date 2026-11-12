package core.modelo;

// Um ponto do DISPOSITIVO: coordenadas inteiras.
// Origem (0, 0) no canto superior esquerdo, y cresce para BAIXO.
public class Pixel {

    private int x;
    private int y;

    public Pixel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}