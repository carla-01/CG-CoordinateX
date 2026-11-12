package lab1.transformacao;

import core.modelo.JanelaMundo;
import core.modelo.Pixel;
import core.modelo.Ponto;

// Cenário I: NDC em [0,1] x [0,1]
// (começo = 0, tamanho = 1)
public class TransformacaoNdc01 extends Transformacao {

    public TransformacaoNdc01(JanelaMundo janela, int ndh, int ndv) {
        super(janela, ndh, ndv);
    }

    // mundo -> NDC
    // ndcx = (x - xmin) / (xmax - xmin)
    public Ponto userToNdc(Ponto mundo) {
        double ndcx = (mundo.getX() - janela.getXmin()) / janela.getLargura();
        double ndcy = (mundo.getY() - janela.getYmin()) / janela.getAltura();
        return new Ponto(ndcx, ndcy);
    }

    // NDC -> mundo
    // x = xmin + ndcx * (xmax - xmin)
    public Ponto ndcToUser(Ponto ndc) {
        double x = janela.getXmin() + ndc.getX() * janela.getLargura();
        double y = janela.getYmin() + ndc.getY() * janela.getAltura();
        return new Ponto(x, y);
    }

    // NDC -> dispositivo
    // dcx = round(ndcx * (ndh - 1))
    public Pixel ndcToDc(Ponto ndc) {
        int dcx = (int) Math.round(ndc.getX() * (ndh - 1));
        int dcy = (int) Math.round(ndc.getY() * (ndv - 1));
        return new Pixel(dcx, dcy);
    }

    // entrada do dispositivo -> NDC
    // ndcx = dcx / (ndh - 1)
    public Ponto inpToNdc(Pixel entrada) {
        double ndcx = entrada.getX() / (double) (ndh - 1);
        double ndcy = entrada.getY() / (double) (ndv - 1);
        return new Ponto(ndcx, ndcy);
    }

    public String getNome() {
        return "NDC [0,1] x [0,1]";
    }
}
