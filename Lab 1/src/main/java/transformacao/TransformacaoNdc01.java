package transformacao;

import dispositivo.Dispositivo;
import modelo.JanelaMundo;
import modelo.Pixel;
import modelo.Ponto;

// Cenário I: NDC em [0,1] x [0,1]
// (começo = 0, tamanho = 1)
public class TransformacaoNdc01 extends Transformacao {

    public TransformacaoNdc01(JanelaMundo janela, Dispositivo dispositivo) {
        super(janela, dispositivo);
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
        int ndh = dispositivo.getNdh();
        int ndv = dispositivo.getNdv();

        int dcx = (int) Math.round(ndc.getX() * (ndh - 1));
        int dcy = (int) Math.round(ndc.getY() * (ndv - 1));
        return new Pixel(dcx, dcy);
    }

    // entrada do dispositivo -> NDC
    // ndcx = dcx / (ndh - 1)
    public Ponto inpToNdc(Pixel entrada) {
        double ndh = dispositivo.getNdh();
        double ndv = dispositivo.getNdv();

        double ndcx = entrada.getX() / (ndh - 1);
        double ndcy = entrada.getY() / (ndv - 1);
        return new Ponto(ndcx, ndcy);
    }


    public String getNome() {
        return "NDC [0,1] x [0,1]";
    }
}
