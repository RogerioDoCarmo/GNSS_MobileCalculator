package Model;

import android.support.annotation.NonNull;

public class GNSSMeasurement implements Comparable<GNSSMeasurement>{

    private Double allRxSeconds;

    private int ID_rray;
    private Long allRxMillis;
    private Double allRxMilliseconds;
    private Long allRxNanos;

    private Double fctSeconds;

    /*Campos adicionados manualmente:*/
    private Double tTx;
    private Double tRx;
    private GNSSDate data;
    private int gpsWeek;

    /*Pseudo-distâncias calculadas*/
    private double PseudorangeMeters;
    private double PseudoRangeUncertaintyMeters;

    /*Campos nao-utilizados*/
    private int ElapsedRealtimeMillis;
    private  double TimeOffsetNanos;
    private int AccumulatedDeltaRangeState;
    private double CarrierFrequencyHz;
    private int CarrierCycles;
    private double CarrierPhase;
    private double CarrierPhaseUncertainty;
    private double SnrInDb;
    private double AgcDb;
    /*Campos nao-utilizados*/

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

    /*Campos da classe GnssClock*/
    private Long TimeNanos;
    private double TimeUncertaintyNanos;
    private int LeapSecond;
    private Long FullBiasNanos;
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

    /**
     * Obtem o tempo GNSS no momento da transmissão do sinal.
     * <p> Para satélites GPS é equivalente ao <b>TOW</b> (Segundos da semana GPS atual) em <b>nanosegundos</b>.
     * @return
     */
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
     * @return Tempo de <b>transmissão</b> em segundos da semana GPS.
     */
    public Double gettTxSeconds() {
        return tTx;
    }

    /**
     *{@link GNSSMeasurement#gettTxSeconds}
     * @param tTx O instante de transmissão do sinal em segundos da semana GPS.
     */
    public void settTxSeconds(Double tTx) {
        this.tTx = tTx;
    }

    /**
     * Retorna o instate de recepção do sinal em segundos da semana GPS.
     * @return Tempo de <b>recepção</b> em segundos da semana GPS.
     */
    public Double gettRx() {
        return tRx;
    }

    /**
     * {@link GNSSMeasurement#gettRx}
     * @param tRx O instante de recepção do sinal em segundos da semana GPS.
     */
    public void settRxSeconds(Double tRx) {
        this.tRx = tRx;
    }

    public GNSSDate getData() {
        return data;
    }

    public void setData(GNSSDate data) {
        this.data = data;
    }

    /**
     * GPS Week number since 1980
     */
    public int getGpsWeek() {
        return gpsWeek;
    }

    public void setGpsWeek(int gpsWeek) {
        this.gpsWeek = gpsWeek;
    }

    /**
     * Comparable method to sort all the Measurements by the satellite id (PRN)
     * TODO: Implement Comparator methods
     * @param another Another instance of the GNSSMeasurement class.
     * @return A negative number if this object PRN is lesser than another.
     * <p>0 if the PRNs are equal.
     * <p>A positive number if this object PRN is greater than another.
     */
    @Override
    public int compareTo(@NonNull GNSSMeasurement another) {
        return (this.getSvid() - another.getSvid());
    }

    /**
     * Retorna o indice da medicao no vetor de medicoes original
      * @return
     */
    public int getID_rray() {
        return ID_rray;
    }

    public void setID_rray(int ID_rray) {
        this.ID_rray = ID_rray;
    }

    public Double getAllRxMilliseconds() {
        return allRxMilliseconds;
    }

    public void setAllRxMilliseconds(Double allRxMilliseconds) {
        this.allRxMilliseconds = allRxMilliseconds;
    }

    public void setFctSeconds(Double fctSeconds) {
        this.fctSeconds = fctSeconds;
    }

    public Double getFctSeconds() {
        return fctSeconds;
    }

    public Double getAllRxSeconds() {
        return allRxSeconds;
    }

    public void setAllRxSeconds(Double allRxSeconds) {
        this.allRxSeconds = allRxSeconds;
    }

    public Long getAllRxNanos() {
        return allRxNanos;
    }

    public void setAllRxNanos(Long allRxNanos) {
        this.allRxNanos = allRxNanos;
    }

    public Long getAllRxMillis() {
        return allRxMillis;
    }

    public void setAllRxMillis(Long allRxMillis) {
        this.allRxMillis = allRxMillis;
    }

    @Override
    public String toString() {
        return "PRN: " + this.getSvid() + " Pr: " + this.getPseudorangeMeters() +  " \n";
    }
}
