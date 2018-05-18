package Codigos;

public class GNSSMeasurement {

    private double Cn0DbHz;
    private int ConstellationType;
    private int State;
    private int Svid;
    private int MultipathIndicator;
    private double PseudorangeRateMetersPerSecond;
    private double PseudorangeRateUncertaintyMetersPerSecond;
    private int ReceivedSvTimeNanos;
    private double ReceivedSvTimeUncertaintyNanos;
    private double AccumulatedDeltaRangeMeters;
    private double AccumulatedDeltaRangeUncertaintyMeters;

    /*TODO TALVEZ SEPARAR OS CAMPOS ABAIXO NA CLASSE GNSSCLOCK*/

    private int TimeNanos;
    private double TimeUncertaintyNanos;
    private int LeapSecond;
    private int FullBiasNanos;
    private double BiasUncertaintyNanos;
    private double DriftNanosPerSecond;
    private double DriftUncertaintyNanosPerSecond;
    private double HardwareClockDiscontinuityCount;
    private double BiasNanos;

    public GNSSMeasurement(){

    }


    public double getCn0DbHz() {
        return Cn0DbHz;
    }

    public void setCn0DbHz(double cn0DbHz) {
        Cn0DbHz = cn0DbHz;
    }

    public int getConstellationType() {
        return ConstellationType;
    }

    public void setConstellationType(int constellationType) {
        ConstellationType = constellationType;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public int getSvid() {
        return Svid;
    }

    public void setSvid(int svid) {
        Svid = svid;
    }

    public int getMultipathIndicator() {
        return MultipathIndicator;
    }

    public void setMultipathIndicator(int multipathIndicator) {
        MultipathIndicator = multipathIndicator;
    }

    public double getPseudorangeRateMetersPerSecond() {
        return PseudorangeRateMetersPerSecond;
    }

    public void setPseudorangeRateMetersPerSecond(double pseudorangeRateMetersPerSecond) {
        PseudorangeRateMetersPerSecond = pseudorangeRateMetersPerSecond;
    }

    public double getPseudorangeRateUncertaintyMetersPerSecond() {
        return PseudorangeRateUncertaintyMetersPerSecond;
    }

    public void setPseudorangeRateUncertaintyMetersPerSecond(double pseudorangeRateUncertaintyMetersPerSecond) {
        PseudorangeRateUncertaintyMetersPerSecond = pseudorangeRateUncertaintyMetersPerSecond;
    }

    public double getReceivedSvTimeNanos() {
        return ReceivedSvTimeNanos;
    }

    public void setReceivedSvTimeNanos(int receivedSvTimeNanos) {
        ReceivedSvTimeNanos = receivedSvTimeNanos;
    }

    public double getReceivedSvTimeUncertaintyNanos() {
        return ReceivedSvTimeUncertaintyNanos;
    }

    public void setReceivedSvTimeUncertaintyNanos(double receivedSvTimeUncertaintyNanos) {
        ReceivedSvTimeUncertaintyNanos = receivedSvTimeUncertaintyNanos;
    }

    public double getAccumulatedDeltaRangeMeters() {
        return AccumulatedDeltaRangeMeters;
    }

    public void setAccumulatedDeltaRangeMeters(double accumulatedDeltaRangeMeters) {
        AccumulatedDeltaRangeMeters = accumulatedDeltaRangeMeters;
    }

    public double getAccumulatedDeltaRangeUncertaintyMeters() {
        return AccumulatedDeltaRangeUncertaintyMeters;
    }

    public void setAccumulatedDeltaRangeUncertaintyMeters(double accumulatedDeltaRangeUncertaintyMeters) {
        AccumulatedDeltaRangeUncertaintyMeters = accumulatedDeltaRangeUncertaintyMeters;
    }

    public double getTimeNanos() {
        return TimeNanos;
    }

    public void setTimeNanos(int timeNanos) {
        TimeNanos = timeNanos;
    }

    public double getTimeUncertaintyNanos() {
        return TimeUncertaintyNanos;
    }

    public void setTimeUncertaintyNanos(double timeUncertaintyNanos) {
        TimeUncertaintyNanos = timeUncertaintyNanos;
    }

    public int getLeapSecond() {
        return LeapSecond;
    }

    public void setLeapSecond(int leapSecond) {
        LeapSecond = leapSecond;
    }

    public double getFullBiasNanos() {
        return FullBiasNanos;
    }

    public void setFullBiasNanos(int fullBiasNanos) {
        FullBiasNanos = fullBiasNanos;
    }

    public double getBiasUncertaintyNanos() {
        return BiasUncertaintyNanos;
    }

    public void setBiasUncertaintyNanos(double biasUncertaintyNanos) {
        BiasUncertaintyNanos = biasUncertaintyNanos;
    }

    public double getDriftNanosPerSecond() {
        return DriftNanosPerSecond;
    }

    public void setDriftNanosPerSecond(double driftNanosPerSecond) {
        DriftNanosPerSecond = driftNanosPerSecond;
    }

    public double getDriftUncertaintyNanosPerSecond() {
        return DriftUncertaintyNanosPerSecond;
    }

    public void setDriftUncertaintyNanosPerSecond(double driftUncertaintyNanosPerSecond) {
        DriftUncertaintyNanosPerSecond = driftUncertaintyNanosPerSecond;
    }

    public double getHardwareClockDiscontinuityCount() {
        return HardwareClockDiscontinuityCount;
    }

    public void setHardwareClockDiscontinuityCount(double hardwareClockDiscontinuityCount) {
        HardwareClockDiscontinuityCount = hardwareClockDiscontinuityCount;
    }

    public double getBiasNanos() {
        return BiasNanos;
    }

    public void setBiasNanos(double biasNanos) {
        BiasNanos = biasNanos;
    }

    /*TODO TALVEZ SEPARAR OS CAMPOS ABAIXO NA CLASSE GNSSCLOCK*/

}
