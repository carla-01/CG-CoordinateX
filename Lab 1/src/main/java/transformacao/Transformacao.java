package transformacao;

import dispositivo.Dispositivo;
import modelo.JanelaMundo;
import modelo.Pixel;
import modelo.Ponto;

// Classe mãe com os 4 procedimentos do exercício.
// Cada classe filha faz as contas para um intervalo de NDC.
//
//   Mundo  --userToNdc-->  NDC  --ndcToDc-->  Dispositivo   (saída)
//   Mundo <--ndcToUser--   NDC <--inpToNdc--  Dispositivo   (entrada)
public abstract class Transformacao {

    protected JanelaMundo janela;
    protected Dispositivo dispositivo;

    public Transformacao(JanelaMundo janela, Dispositivo dispositivo) {
        this.janela = janela;
        this.dispositivo = dispositivo;
    }

    // user_to_ndc: mundo -> NDC
    public abstract Ponto userToNdc(Ponto mundo);

    // ndc_to_user: NDC -> mundo
    public abstract Ponto ndcToUser(Ponto ndc);

    // ndc_to_dc: NDC -> dispositivo
    public abstract Pixel ndcToDc(Ponto ndc);

    // inp_to_ndc: entrada do dispositivo -> NDC
    public abstract Ponto inpToNdc(Pixel entrada);

    // Nome do cenário para mostrar no console
    public abstract String getNome();
}