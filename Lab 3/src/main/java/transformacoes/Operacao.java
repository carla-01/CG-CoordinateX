package transformacoes;

// Uma transformação pronta para ser aplicada (ou guardada na sequência):
//  - nome:     texto curto, ex.: "Rotação 45° em torno de (0, 0)"
//  - matriz:   a matriz 3x3 final
//  - detalhes: o passo a passo (matrizes intermediárias) para o histórico
public class Operacao {

    private String nome;
    private Matriz matriz;
    private String detalhes;

    public Operacao(String nome, Matriz matriz, String detalhes) {
        this.nome = nome;
        this.matriz = matriz;
        this.detalhes = detalhes;
    }

    public String getNome() {
        return nome;
    }

    public Matriz getMatriz() {
        return matriz;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public String toString() {
        return nome;
    }
}
