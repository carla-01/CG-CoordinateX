package tabelacores;

// Exercício: tabela de cores com 24 bits de profundidade,
// para um sistema com profundidade de pixel de 12 bits.
public class MainTabelaCores {

    public static void main(String[] args) {
        int tamanho = 512; // imagem de 512 x 512 (blocos de 8 x 8)

        TabelaCores tabela = new TabelaCores();

        FrameBufferIndexado frameBuffer = new FrameBufferIndexado(tamanho, tamanho);
        frameBuffer.desenharTodosOsIndices();

        ControladorVideo controlador = new ControladorVideo(frameBuffer, tabela);

        int bytesFrameBuffer = frameBuffer.calcularBytes();
        int bytesTabela = TabelaCores.TAMANHO * 24 / 8;
        int bytesSemTabela = tamanho * tamanho * 24 / 8;

        System.out.println("=== Tabela de Cores ===");
        System.out.println("Profundidade do pixel:  12 bits (0x000 a 0xFFF)");
        System.out.println("Posições na tabela:     " + TabelaCores.TAMANHO + " (2^12)");
        System.out.println("Bits por cor na tabela: 24 (#RRGGBB)");
        System.out.println("Cores possíveis:        16777216 (2^24)");
        System.out.println("Cores ao mesmo tempo:   " + TabelaCores.TAMANHO);
        System.out.println();
        System.out.println("=== Memória (" + tamanho + " x " + tamanho + ") ===");
        System.out.println("Frame buffer (12 bits): " + bytesFrameBuffer + " bytes");
        System.out.println("Tabela de cores:        " + bytesTabela + " bytes");
        System.out.println("Total com tabela:       " + (bytesFrameBuffer + bytesTabela) + " bytes");
        System.out.println("Sem tabela (24 bits):   " + bytesSemTabela + " bytes");

        TelaCores tela = new TelaCores(tabela, controlador, tamanho);
        tela.mostrar();
    }
}