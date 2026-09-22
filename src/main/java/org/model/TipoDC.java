package org.model;

public class TipoDC {
    private double dcx;
    private double dcy;

    public TipoDC(double dcx, double dcy) {
        this.dcx = dcx;
        this.dcy = dcy;
    }

    public double getdcx() {
        return dcx;
    }

    public double getdcy() {
        return dcy;
    }

    public void setdcx(double dcx) {
        this.dcx = dcx;
    }

    public void setdcy(double dcy) {
        this.dcy = dcy;
    }

    @Override
    public String toString() {
        return "(" + dcx + ", " + dcy + ")";
    }
}
