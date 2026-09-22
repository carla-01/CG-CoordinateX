package org.transformacoes;

import org.model.*;

public class Transformador {
    //pessoa 2

    public TipoNDC userToNDC(JanelaMundo janelaMundo, Ponto ponto) {
        TipoNDC ndc = new TipoNDC(0, 0);
        ndc.setnNDCX( (ponto.getX() - janelaMundo.getXmin())/(janelaMundo.getXmax() - janelaMundo.getXmin()) );
        ndc.setnNDCY( (ponto.getY() - janelaMundo.getYmin())/(janelaMundo.getYmax() - janelaMundo.getYmin()) );

        return ndc;
    }

    public TipoDC NDCToDC(ResolucaoTela resolucaoTela, TipoNDC ndc) {
        TipoDC dc = new TipoDC(0, 0);
        dc.setdcx( Math.round( (ndc.getnNDCX() * (resolucaoTela.getLargura() - 1)) ) );
        dc.setdcy( Math.round( (ndc.getnNDCY() * (resolucaoTela.getAltura() - 1)) ) );
        return dc;
    }

    
}
