package lab1.transformacao;

import core.modelo.JanelaMundo;
import core.modelo.Pixel;
import core.modelo.Ponto;

// Cenário II: NDC em [-1,1] x [-1,1] (centrado na origem)
// (começo = -1, tamanho = 2)
public class TransformacaoNdc11 extends Transformacao {

    public TransformacaoNdc11(JanelaMundo janela, int ndh, int ndv) {
        super(janela, ndh, ndv);
    }

    // ndcx = 2 * (x - xmin) / (xmax - xmin) - 1
    public Ponto userToNdc(Ponto mundo) {
        double ndcx = 2 * (mundo.getX() - janela.getXmin()) / janela.getLargura() - 1;
        double ndcy = 2 * (mundo.getY() - janela.getYmin()) / janela.getAltura() - 1;
        return new Ponto(ndcx, ndcy);
    }

    // x = xmin + (ndcx + 1) / 2 * (xmax - xmin)
    public Ponto ndcToUser(Ponto ndc) {
        double x = janela.getXmin() + (ndc.getX() + 1) / 2 * janela.getLargura();
        double y = janela.getYmin() + (ndc.getY() + 1) / 2 * janela.getAltura();
        return new Ponto(x, y);
    }

    // dcx = round((ndcx + 1) / 2 * (ndh - 1))
    public Pixel ndcToDc(Ponto ndc) {
        int dcx = (int) Math.round((ndc.getX() + 1) / 2 * (ndh - 1));
        int dcy = (int) Math.round((ndc.getY() + 1) / 2 * (ndv - 1));
        return new Pixel(dcx, dcy);
    }

    // ndcx = 2 * dcx / (ndh - 1) - 1
    public Ponto inpToNdc(Pixel entrada) {
        double ndcx = 2.0 * entrada.getX() / (ndh - 1) - 1;
        double ndcy = 2.0 * entrada.getY() / (ndv - 1) - 1;
        return new Ponto(ndcx, ndcy);
    }

    public String getNome() {
        return "NDC [-1,1] x [-1,1]";
    }
}
