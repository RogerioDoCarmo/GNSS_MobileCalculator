package Codigos;

public class GNSSMeasurement {

    /*Campos adicionados manualmente:*/
    private Long tTx;
    private Long tRx;

    /*Pseudo-distâncias calculadas*/ // FIXME VER SE NÃO SERÁ EM OUTRO LUGAR
    private double PseudorangeMeters;
    private double PseudoRangeUncertaintyMeters;

    /*TODO Campos nao-utilizados*/
    private int ElapsedRealtimeMillis;
    private  double TimeOffsetNanos;
    private int AccumulatedDeltaRangeState;
    private double CarrierFrequencyHz;
    private int CarrierCycles;
    private double CarrierPhase;
    private double CarrierPhaseUncertainty;
    private double SnrInDb;
    private double AgcDb;

    private double Cn0DbHz;
    private int ConstellationType;
    private int State;
    private int Svid;
    private int MultipathIndicator;
    private double PseudorangeRateMetersPerSecond;
    private double PseudorangeRateUncertaintyMetersPerSecond;
    private Long ReceivedSvTimeNanos;
    private double ReceivedSvTimeUncertaintyNanos;
    private double AccumulatedDeltaRangeMeters;
    private double AccumulatedDeltaRangeUncertaintyMeters;

    /*TODO TALVEZ SEPARAR OS CAMPOS ABAIXO NA CLASSE GNSSCLOCK*/

    private Long TimeNanos;
    private double TimeUncertaintyNanos;
    private int LeapSecond;
    private Long FullBiasNanos;
    private double BiasUncertaintyNanos;
    private double DriftNanosPerSecond;
    private double DriftUncertaintyNanosPerSecond;
    private double HardwareClockDiscontinuityCount;
    private double BiasNanos; // FIXME VER SE É LONG

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

    public Long getReceivedSvTimeNanos() {
        return ReceivedSvTimeNanos;
    }

    public void setReceivedSvTimeNanos(long receivedSvTimeNanos) {
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

    /*TODO TALVEZ SEPARAR OS CAMPOS ABAIXO NA CLASSE GNSSCLOCK*/

    public Long getTimeNanos() {
        return TimeNanos;
    }

    public void setTimeNanos(Long timeNanos) {
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

    public Long getFullBiasNanos() {
        return FullBiasNanos;
    }

    public void setFullBiasNanos(Long fullBiasNanos) {
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

    /*TODO Campos nao-utilizados*/

    public int getElapsedRealtimeMillis() {
        return ElapsedRealtimeMillis;
    }

    public void setElapsedRealtimeMillis(int elapsedRealtimeMillis) {
        ElapsedRealtimeMillis = elapsedRealtimeMillis;
    }

    public double getTimeOffsetNanos() {
        return TimeOffsetNanos;
    }

    public void setTimeOffsetNanos(double timeOffsetNanos) {
        TimeOffsetNanos = timeOffsetNanos;
    }

    public int getAccumulatedDeltaRangeState() {
        return AccumulatedDeltaRangeState;
    }

    public void setAccumulatedDeltaRangeState(int accumulatedDeltaRangeState) {
        AccumulatedDeltaRangeState = accumulatedDeltaRangeState;
    }

    public double getCarrierFrequencyHz() {
        return CarrierFrequencyHz;
    }

    public void setCarrierFrequencyHz(double carrierFrequencyHz) {
        CarrierFrequencyHz = carrierFrequencyHz;
    }

    public int getCarrierCycles() {
        return CarrierCycles;
    }

    public void setCarrierCycles(int carrierCycles) {
        CarrierCycles = carrierCycles;
    }

    public double getCarrierPhase() {
        return CarrierPhase;
    }

    public void setCarrierPhase(double carrierPhase) {
        CarrierPhase = carrierPhase;
    }

    public double getCarrierPhaseUncertainty() {
        return CarrierPhaseUncertainty;
    }

    public void setCarrierPhaseUncertainty(double carrierPhaseUncertainty) {
        CarrierPhaseUncertainty = carrierPhaseUncertainty;
    }

    public double getSnrInDb() {
        return SnrInDb;
    }

    public void setSnrInDb(double snrInDb) {
        SnrInDb = snrInDb;
    }

    public double getAgcDb() {
        return AgcDb;
    }

    public void setAgcDb(double agcDb) {
        AgcDb = agcDb;
    }

    public double getPseudorangeMeters() {
        return PseudorangeMeters;
    }

    public void setPseudorangeMeters(double pseudorangeMeters) {
        PseudorangeMeters = pseudorangeMeters;
    }

    public double getPseudoRangeUncertaintyMeters() {
        return PseudoRangeUncertaintyMeters;
    }

    public void setPseudoRangeUncertaintyMeters(double pseudoRangeUncertaintyMeters) {
        PseudoRangeUncertaintyMeters = pseudoRangeUncertaintyMeters;
    }

    /**
     * Retorna o instante de transmissão do sinal em segundos da semana GPS.
     * @return Tempo de <b>transmissão</b> em segundos da semana GPS
     */
    public Long gettTx() {
        return tTx;
    }

    /**
     *{@link GNSSMeasurement#gettTx}
     * @param tTx O instante de transmissão do sinal em segundos da semana GPS.
     */
    public void settTx(Long tTx) {
        this.tTx = tTx;
    }

    /**
     * Retorna o instate de recepção do sinal em segundos da semana GPS.
     * @return Tempo de <b>recepção</b> em segundos da semana GPS.
     */
    public Long gettRx() {
        return tRx;
    }

    /**
     * {@link GNSSMeasurement#gettRx}
     * @param tRx O instante de recepção do sinal em segundos da semana GPS.
     */
    public void settRx(Long tRx) {
        this.tRx = tRx;
    }
}
