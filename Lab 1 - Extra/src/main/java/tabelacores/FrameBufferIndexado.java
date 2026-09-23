package tabelacores;

// Frame buffer onde cada pixel guarda um ÍNDICE de 12 bits (0x000 a 0xFFF),
// e não a cor em si. A cor de verdade fica na tabela de cores.
public class FrameBufferIndexado {

    private int ndh;
    private int ndv;
    private int[][] indices;

    public FrameBufferIndexado(int ndh, int ndv) {
        this.ndh = ndh;
        this.ndv = ndv;
        indices = new int[ndv][ndh];
    }

    public void setIndice(int x, int y, int indice) {
        indices[y][x] = indice;
    }

    public int getIndice(int x, int y) {
        return indices[y][x];
    }

    public int getNdh() {
        return ndh;
    }

    public int getNdv() {
        return ndv;
    }

    // Divide a imagem em 64 x 64 blocos (64 * 64 = 4096)
    // e coloca um índice diferente em cada bloco.
    public void desenharTodosOsIndices() {
        int larguraBloco = ndh / 64;
        int alturaBloco = ndv / 64;

        for (int y = 0; y < ndv; y++) {
            for (int x = 0; x < ndh; x++) {
                int coluna = x / larguraBloco;
                int linha = y / alturaBloco;
                int indice = linha * 64 + coluna;
                setIndice(x, y, indice);
            }
        }
    }

    // Memória usada: 12 bits por pixel
    public int calcularBytes() {
        return ndh * ndv * 12 / 8;
    }
}