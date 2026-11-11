package lab3.desenho;

import core.dispositivo.Raster;
import core.modelo.Pixel;
import lab2.reta.AlgoritmoReta;
import lab2.reta.RetaPontoMedio;
import lab3.modelo.Poligono;
import lab3.modelo.Vertice;

// Desenha o contorno de um polígono: cada lado é uma reta rasterizada pelo
// algoritmo do Ponto Médio (aula G3, reaproveitado do Lab 2), ligando um
// vértice ao seguinte e o último ao primeiro.
//
// Os vértices são reais; o arredondamento para pixel acontece só aqui,
// depois de todas as transformações (assim o erro não se acumula).
public final class DesenhoPoligono {

    private static final double EPSILON = 1e-9;

    private final AlgoritmoReta reta;

    public DesenhoPoligono() {
        this(new RetaPontoMedio());
    }

    public DesenhoPoligono(AlgoritmoReta reta) {
        this.reta = reta;
    }

    // Arredonda "para longe do zero" (2,5 -> 3 e -2,5 -> -3), para que uma
    // reflexão em torno de um eixo dê exatamente o pixel espelhado.
    public static int arredondar(double v) {
        if (v >= 0) {
            return (int) Math.floor(v + 0.5 + EPSILON);
        }
        return -(int) Math.floor(-v + 0.5 + EPSILON);
    }

    public static Pixel pixelDoVertice(Vertice v) {
        return new Pixel(arredondar(v.getX()), arredondar(v.getY()));
    }

    // Os pontos chegam ao raster em coordenadas do plano cartesiano
    public void desenhar(Raster raster, Poligono poligono, int cor) {
        int n = poligono.getQuantidade();
        if (n == 1) {
            Pixel p = pixelDoVertice(poligono.get(0));
            raster.setPixel(p.getX(), p.getY(), cor);
            return;
        }
        for (int i = 0; i < n; i++) {
            Pixel a = pixelDoVertice(poligono.get(i));
            Pixel b = pixelDoVertice(poligono.get((i + 1) % n));
            reta.desenhar(raster, a.getX(), a.getY(), b.getX(), b.getY(), cor);
            if (n == 2) {
                break;
            }
        }
    }
}
