package org.transformacoes;

import org.model.*;

// Converte pontos entre a janela do mundo, o NDC (nos dois cenarios) e a tela.
public class Transformador {

    // Leva um ponto do mundo para o NDC, dentro da faixa escolhida (ndcMin a ndcMax).
    public TipoNDC userToNDC(JanelaMundo janela, Ponto ponto, double ndcMin, double ndcMax) {
        double faixa = ndcMax - ndcMin;

        double xndc = ndcMin + (ponto.getX() - janela.getXmin()) / (janela.getXmax() - janela.getXmin()) * faixa;
        double yndc = ndcMin + (ponto.getY() - janela.getYmin()) / (janela.getYmax() - janela.getYmin()) * faixa;

        return new TipoNDC(xndc, yndc);
    }

    // Faz o caminho inverso: pega o ponto em NDC e devolve a posicao original na janela do mundo.
    public Ponto ndcToUser(JanelaMundo janela, TipoNDC ndc, double ndcMin, double ndcMax) {
        double faixa = ndcMax - ndcMin;

        double x = janela.getXmin() + (ndc.getnNDCX() - ndcMin) / faixa * (janela.getXmax() - janela.getXmin());
        double y = janela.getYmin() + (ndc.getnNDCY() - ndcMin) / faixa * (janela.getYmax() - janela.getYmin());

        return new Ponto(x, y);
    }

    // Transforma o ponto em NDC no pixel da tela, por semelhanca de triangulos: DCX = NDCX*(largura-1), DCY = NDCY*(altura-1).
    public TipoDC ndcToDC(ResolucaoTela tela, TipoNDC ndc, double ndcMin, double ndcMax) {
        double faixa = ndcMax - ndcMin;

        double tx = (ndc.getnNDCX() - ndcMin) / faixa;
        double ty = (ndc.getnNDCY() - ndcMin) / faixa;

        double dcx = Math.round(tx * (tela.getLargura() - 1));
        double dcy = Math.round(ty * (tela.getAltura() - 1));

        return new TipoDC(dcx, dcy);
    }

    // Pega um pixel da tela, como um clique do mouse, e converte de volta para NDC.
    public TipoNDC inpToNDC(ResolucaoTela tela, TipoDC dc, double ndcMin, double ndcMax) {
        double faixa = ndcMax - ndcMin;

        double tx = dc.getdcx() / (tela.getLargura() - 1);
        double ty = dc.getdcy() / (tela.getAltura() - 1);

        double xndc = ndcMin + tx * faixa;
        double yndc = ndcMin + ty * faixa;

        return new TipoNDC(xndc, yndc);
    }
}
