package Codigos;

import android.support.annotation.NonNull;

public class GNSSNavMsg implements Comparable<GNSSNavMsg>{
    /**
     * GNSSNavMsg structure based on ReadRinexNav.m file from Google
     * clock variable names af0, af1, af2 follow IS GPS 200
    */
    private GNSSDate data;
    private Integer PRN;          // SV PRN number TODO Verificar se fica como int msm!
    private double Toc;          // Time of clock (seconds)
    private double af0;          // SV clock bias (seconds)
    private double af1;          // SV clock drift (sec/sec)
    private double af2;          // SV clock drift rate (sec/sec2)
    private double IODE;         // Issue of data, ephemeris
    private double Crs;          // Sine harmonic correction to orbit radius (meters)
    private double Delta_n;      // Mean motion difference from computed value (radians/sec)
    private double M0;           // Mean anomaly at reference time (radians)
    private double Cuc;          // Cosine harmonic correction to argument of lat (radians)
    private double e;            // Eccentricity (dimensionless)
    private double Cus;          // Sine harmonic correction to argument of latitude (radians)
    private double Asqrt;        // Square root of semi-major axis (meters^1/2)
    private double Toe;          // Reference time of ephemeris (seconds)
    private double Cic;          // Sine harmonic correction to angle of inclination (radians)
    private double Omega0;        // Longitude of ascending node at weekly epoch (radians)
    private double Cis;          // Sine harmonic correction to angle of inclination (radians)
    private double i0;           // Inclination angle at reference time (radians)
    private double Crc;	         // Cosine harmonic correction to the orbit radius (meters)
    private double w;	     // Argument of perigee (radians)
    private double Omega_v;    // Rate of right ascension (radians/sec)
    private double IDOT;	     // Rate of inclination angle (radians/sec)
    private double codeL2;       // codes on L2 channel
    private double GPS_Week;     // GPS week (to go with Toe), (NOT Mod 1024)
    private double L2PdataFlag;      // L2 P data flag
    private double accuracy;     // SV user range accuracy (meters)
    private double health;       // Satellite health
    private double TGD;          // Group delay (seconds)
    private double IODC;         // Issue of Data, Clock
    private double ttx;	         // Transmission time of message (seconds)
    private double Fit_interval; //fit interval (hours), zero if not known

    public Integer getPRN() {
        return PRN;
    }

    public void setPRN(Integer PRN) {
        this.PRN = PRN;
    }

    public double getToc() {
        return Toc;
    }

    public void setToc(double toc) {
        Toc = toc;
    }

    public double getAf0() {
        return af0;
    }

    public void setAf0(double af0) {
        this.af0 = af0;
    }

    public double getAf1() {
        return af1;
    }

    public void setAf1(double af1) {
        this.af1 = af1;
    }

    public double getAf2() {
        return af2;
    }

    public void setAf2(double af2) {
        this.af2 = af2;
    }

    public double getIODE() {
        return IODE;
    }

    public void setIODE(double IODE) {
        this.IODE = IODE;
    }

    public double getCrs() {
        return Crs;
    }

    public void setCrs(double crs) {
        Crs = crs;
    }

    public double getDelta_n() {
        return Delta_n;
    }

    public void setDelta_n(double delta_n) {
        Delta_n = delta_n;
    }

    public double getM0() {
        return M0;
    }

    public void setM0(double m0) {
        M0 = m0;
    }

    public double getCuc() {
        return Cuc;
    }

    public void setCuc(double cuc) {
        Cuc = cuc;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }

    public double getCus() {
        return Cus;
    }

    public void setCus(double cus) {
        Cus = cus;
    }

    public double getAsqrt() {
        return Asqrt;
    }

    public void setAsqrt(double asqrt) {
        Asqrt = asqrt;
    }

    public double getToe() {
        return Toe;
    }

    public void setToe(double toe) {
        Toe = toe;
    }

    public double getCic() {
        return Cic;
    }

    public void setCic(double cic) {
        Cic = cic;
    }

    public double get0mega_0() {
        return Omega0;
    }

    public void setOmega_0(double omega0) {
        this.Omega0 = omega0;
    }

    public double getCis() {
        return Cis;
    }

    public void setCis(double cis) {
        Cis = cis;
    }

    public double getI0() {
        return i0;
    }

    public void setI0(double i0) {
        this.i0 = i0;
    }

    public double getCrc() {
        return Crc;
    }

    public void setCrc(double crc) {
        Crc = crc;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getOmega_v() {
        return Omega_v;
    }

    public void setOmega_v(double OMEGA_DOT) {
        this.Omega_v = OMEGA_DOT;
    }

    public double getIDOT() {
        return IDOT;
    }

    public void setIDOT(double IDOT) {
        this.IDOT = IDOT;
    }

    public double getCodeL2() {
        return codeL2;
    }

    public void setCodeL2(double codeL2) {
        this.codeL2 = codeL2;
    }

    public double getGPS_Week() {
        return GPS_Week;
    }

    public void setGPS_Week(double GPS_Week) {
        this.GPS_Week = GPS_Week;
    }

    public double getL2PdataFlag() {
        return L2PdataFlag;
    }

    public void setL2PdataFlag(double l2Pdata) {
        L2PdataFlag = l2Pdata;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getTGD() {
        return TGD;
    }

    public void setTGD(double TGD) {
        this.TGD = TGD;
    }

    public double getIODC() {
        return IODC;
    }

    public void setIODC(double IODC) {
        this.IODC = IODC;
    }

    public double getTtx() {
        return ttx;
    }

    public void setTtx(double ttx) {
        this.ttx = ttx;
    }

    public double getFit_interval() {
        return Fit_interval;
    }

    public void setFit_interval(double fit_interval) {
        Fit_interval = fit_interval;
    }

    public void setGNSSDate(GNSSDate data) {
        this.data = data;
    }

    public GNSSDate getData() {
        return data;
    }


    /**
     * Comparable methot to sort all the Navigation Messages by the satelite id (PRN)
     * TODO: Implement Comparator methods
     * @param another Another instance of the GNSSNavMsg class.
     * @return A negative number if this object PRN is lesser than another.
     * <p>0 if the PRNs are equal.
     * <p>A positive number if this object PRN is greater than another.
     */
    @Override
    public int compareTo(@NonNull GNSSNavMsg another) {
        return (this.getPRN() - another.getPRN());
    }
}
