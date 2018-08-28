package Codigos;

import android.support.annotation.NonNull;

public class CoordenadaGPS implements Comparable<CoordenadaGPS>{
    private int PRN;
    private double X;
    private double Y;
    private double Z;
    private double dts; // Erro do relógio em segundos em relação ao sistema de tempo GPS.

    /**
     * Creates an instance of a GPS coordinate to represent a satellite ou receiver position.
     * @param PRN The satellite ID (PRN).
     * @param X The X coordinate of the object in the WGS 84
     * @param Y The Y coordinate of the object in the WGS 84
     * @param Z The Z coordinate of the object in the WGS 84
     * @param dts The error of the clock to the GPS time system <b>in seconds</b>.
     */
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

    /**
     *
     * @return The error of the satellite to the GPS time system <b>in seconds</b>.
     */
    public double getDts() {
        return dts;
    }

    /**
     * @param dts The error of the satellite to the GPS time system <b>in seconds</b>.
     */
    public void setDts(double dts) {
        this.dts = dts;
    }

    @Override
    public int compareTo(@NonNull CoordenadaGPS another) {
        return (this.PRN - another.getPRN());
    }
}
