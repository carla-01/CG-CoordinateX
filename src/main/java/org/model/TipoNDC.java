package org.model;

public class TipoNDC {
    //pessoa 2
    private double nNDCX;
    private double nNDCY;

    public TipoNDC(double nNDCX, double nNDCY) {
        this.nNDCX = nNDCX;
        this.nNDCY = nNDCY;
    }

    public double getnNDCX() {
        return nNDCX;
    }

    public double getnNDCY() {
        return nNDCY;
    }

    public void setnNDCX(double nNDCX) {
        this.nNDCX = nNDCX;
    }

    public void setnNDCY(double nNDCY) {
        this.nNDCY = nNDCY;
    }

    @Override
    public String toString() {
        return "(" + nNDCX + ", " + nNDCY + ")";
    }

}
