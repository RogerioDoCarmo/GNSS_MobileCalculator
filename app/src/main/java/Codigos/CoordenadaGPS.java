package Codigos;

public class CoordenadaGPS {
    private int PRN;
    private double X;
    private double Y;
    private double Z;
    private double dts; // Erro do satélite

    public CoordenadaGPS (int PRN, double X, double Y, double Z, double dts ){
        this.PRN = PRN;
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.dts = dts;
    }

    public int getPRN() {
        return PRN;
    }

    public void setPRN(int PRN) {
        this.PRN = PRN;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double z) {
        Z = z;
    }

    public double getDts() {
        return dts;
    }

    public void setDts(double dts) {
        this.dts = dts;
    }
}
