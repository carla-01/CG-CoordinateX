package lab1.transformacao;

import core.modelo.JanelaMundo;
import core.modelo.Pixel;
import core.modelo.Ponto;

// Classe mãe com os 4 procedimentos do exercício.
// Cada classe filha faz as contas para um intervalo de NDC.
//
//   Mundo  --userToNdc-->  NDC  --ndcToDc-->  Dispositivo   (saída)
//   Mundo <--ndcToUser--   NDC <--inpToNdc--  Dispositivo   (entrada)
//
// ndh e ndv: número de posições endereçáveis na horizontal e na vertical.
public abstract class Transformacao {

    protected JanelaMundo janela;
    protected int ndh;
    protected int ndv;

    public Transformacao(JanelaMundo janela, int ndh, int ndv) {
        this.janela = janela;
        this.ndh = ndh;
        this.ndv = ndv;
    }

    // user_to_ndc: mundo -> NDC
    public abstract Ponto userToNdc(Ponto mundo);

    // ndc_to_user: NDC -> mundo
    public abstract Ponto ndcToUser(Ponto ndc);

    // ndc_to_dc: NDC -> dispositivo
    public abstract Pixel ndcToDc(Ponto ndc);

    // inp_to_ndc: entrada do dispositivo -> NDC
    public abstract Ponto inpToNdc(Pixel entrada);

    // Nome do cenário para mostrar na tela
    public abstract String getNome();

    public JanelaMundo getJanela() {
        return janela;
    }
}
