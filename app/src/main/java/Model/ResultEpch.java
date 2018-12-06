package Model;

import java.text.DecimalFormat;

public class ResultEpch {
    private int numEpch;

    private GNSSDate data;

    private double Xmeters;
    private double Ymeters;
    private double Zmeters;
    private double DtrS;

    private double latiDegrees;
    private double longDegrees;
    private double altMeters;

    private int numIterations;

    private double sigmaXmeters;
    private double sigmaYmeters;
    private double sigmaZmeters;
    private double sigmaDtrSecons;

    private int numSat;

    private double discXmeters;
    private double discYmeters;
    private double discZmeters;

    public ResultEpch(int numEpch, GNSSDate data, double Xmeters, double Ymeters, double Zmeters, double DtrS, double latiDegrees, double longDegrees, double altMeters,
                      int numIterations, double sigmaXmeters, double sigmaYmeters, double sigmaZmeters, double sigmaDtrSecons, int numSat,
                      double discXmeters, double discYmeters, double discZmeters) {
        this.numEpch = numEpch;
        this.data = data;
        this.Xmeters = Xmeters;
        this.Ymeters = Ymeters;
        this.Zmeters = Zmeters;
        this.DtrS = DtrS;
        this.latiDegrees = latiDegrees;
        this.longDegrees = longDegrees;
        this.altMeters = altMeters;
        this.numIterations = numIterations;
        this.sigmaXmeters = sigmaXmeters;
        this.sigmaYmeters = sigmaYmeters;
        this.sigmaZmeters = sigmaZmeters;
        this.sigmaDtrSecons = sigmaDtrSecons;
        this.numSat = numSat;
        this.discXmeters = discXmeters;
        this.discYmeters = discYmeters;
        this.discZmeters = discZmeters;
    }

    public int getNumEpch() {
        return numEpch;
    }

    public GNSSDate getData() {
        return data;
    }

    public double getXmeters() {
        return Xmeters;
    }

    public double getYmeters() {
        return Ymeters;
    }

    public double getZmeters() {
        return Zmeters;
    }

    public double getDtrS() {
        return DtrS;
    }

    public double getLatiDegrees() {
        return latiDegrees;
    }

    public double getLongDegrees() {
        return longDegrees;
    }

    public double getAltMeters() {
        return altMeters;
    }

    public int getNumIterations() {
        return numIterations;
    }

    public double getSigmaXmeters() {
        return sigmaXmeters;
    }

    public double getSigmaYmeters() {
        return sigmaYmeters;
    }

    public double getSigmaZmeters() {
        return sigmaZmeters;
    }

    public double getSigmaDtrSecons() {
        return sigmaDtrSecons;
    }

    public int getNumSat() {
        return numSat;
    }

    public double getDiscXmeters() {
        return discXmeters;
    }

    public double getDiscYmeters() {
        return discYmeters;
    }

    public double getDiscZmeters() {
        return discZmeters;
    }

    public double getErrorClockMeters(){
        return this.DtrS * GNSSConstants.LIGHTSPEED;
    }

    @Override
    public String toString(){
        return (data.toString() + "; " + numEpch + "; " +
                String.format("%s", new DecimalFormat("###.###").format(Xmeters)) + "; " +
                String.format("%s", new DecimalFormat("###.###").format(Ymeters))) + "; " +
                String.format("%s", new DecimalFormat("###.###").format(Zmeters)) + "; " +
                DtrS + "; " +
                String.format("%s", new DecimalFormat("###.###").format(sigmaXmeters)) + "; " +
                String.format("%s", new DecimalFormat("###.###").format(sigmaYmeters)) + "; " +
                String.format("%s", new DecimalFormat("###.###").format(sigmaZmeters)) + "; " +
                sigmaDtrSecons + "; " +
                numSat + "; " +
                String.format("%s", new DecimalFormat("###.###").format(getErrorClockMeters())) + ";";
    }
}
