package lab1extra.tabelacores;

// Tabela de cores (Look-Up Table, LUT).
// Pixel de 12 bits  ->  2^12 = 4096 posições na tabela (0x000 a 0xFFF).
// Cada posição guarda uma cor de 24 bits: 8 bits R, 8 bits G, 8 bits B
// (em hexadecimal: 0xRRGGBB).
public class TabelaCores {

    public static final int TAMANHO = 4096; // 2^12

    private int[] cores;
    private String nome;

    public TabelaCores() {
        cores = new int[TAMANHO];
        preencherColorida();
    }

    // Monta a cor 0xRRGGBB a partir de R, G e B (0 a 255).
    // 0x10000 = 65536 empurra o R duas casas hex pra esquerda,
    // 0x100 = 256 empurra o G uma casa.
    public int montarCor(int r, int g, int b) {
        return r * 0x10000 + g * 0x100 + b;
    }

    public void setCor(int indice, int r, int g, int b) {
        cores[indice] = montarCor(r, g, b);
    }

    public int getCor(int indice) {
        return cores[indice];
    }

    // Separa a cor 0xRRGGBB em cada canal
    public int getVermelho(int indice) {
        return cores[indice] / 0x10000;
    }

    public int getVerde(int indice) {
        return (cores[indice] / 0x100) % 0x100;
    }

    public int getAzul(int indice) {
        return cores[indice] % 0x100;
    }

    public String getNome() {
        return nome;
    }

    // Memória usada pela tabela: 4096 posições x 24 bits
    public static int calcularBytes() {
        return TAMANHO * 24 / 8;
    }

    // Tabela 1: os 12 bits do índice viram 3 dígitos hex: 0xRGB.
    // Cada dígito (0 a F) vira um canal de 8 bits repetindo o dígito:
    // 0x1 -> 0x11, 0x8 -> 0x88, 0xF -> 0xFF  (por isso o * 0x11 = * 17)
    // Exemplo: índice 0x180 -> cor #118800
    public void preencherColorida() {
        for (int i = 0; i < TAMANHO; i++) {
            int r = i / 0x100;          // 1º dígito hex
            int g = (i / 0x10) % 0x10;  // 2º dígito hex
            int b = i % 0x10;           // 3º dígito hex
            setCor(i, r * 0x11, g * 0x11, b * 0x11);
        }
        nome = "Colorida (0xRGB)";
    }

    // Tabela 2: 4096 tons de cinza, do preto ao branco.
    public void preencherCinza() {
        for (int i = 0; i < TAMANHO; i++) {
            int cinza = i * 255 / (TAMANHO - 1);
            setCor(i, cinza, cinza, cinza);
        }
        nome = "Tons de cinza";
    }

    // Tabela 3: degradê "fogo": preto -> vermelho -> amarelo -> branco.
    public void preencherFogo() {
        for (int i = 0; i < TAMANHO; i++) {
            double t = (double) i / (TAMANHO - 1); // vai de 0 a 1

            double r = t * 3 * 255;
            double g = (t * 3 - 1) * 255;
            double b = (t * 3 - 2) * 255;

            setCor(i, limitar(r), limitar(g), limitar(b));
        }
        nome = "Fogo";
    }

    // Garante que o valor fica entre 0 e 255
    private int limitar(double valor) {
        if (valor < 0) {
            return 0;
        }
        if (valor > 255) {
            return 255;
        }
        return (int) valor;
    }
}