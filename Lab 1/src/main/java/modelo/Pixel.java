package modelo;

// Um ponto do DISPOSITIVO: coordenadas inteiras.
// Aqui o y cresce para CIMA, como nas fórmulas do slide.
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