package Codigos;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class EpocaGPS {

    private Double FctSeconds;
    private Long mArrivalTimeSinceGpsEpochNs;
    private Long mLargestTowNs;


    private int id;
    private GNSSDate UTC;
    private ArrayList<Integer> listaPRNs;
    private int numSatelites;
    private ArrayList<GNSSMeasurement> listaMedicoes;
    private int numMedicoes;
    private ArrayList<CoordenadaGPS> listaCoordSatelites;
    private int numMsgNav;
    private ArrayList<GNSSNavMsg> listaMsgNavegacao;
    private int GPSweekNumber;
    private int GPSsecondsWeek;

    public EpocaGPS(Long allRxNanos){
        this.mArrivalTimeSinceGpsEpochNs = allRxNanos;
        this.listaPRNs = new ArrayList<>();
        this.listaCoordSatelites = new ArrayList<>();
        this.listaMedicoes = new ArrayList<>();
        this.listaMsgNavegacao = new ArrayList<>();
        this.numSatelites = 0;
        this.numMedicoes = 0;
    }

    public EpocaGPS(Double FctSeconds){
        this.setFctSeconds(FctSeconds);
        this.listaPRNs = new ArrayList<>();
        this.listaCoordSatelites = new ArrayList<>();
        this.listaMedicoes = new ArrayList<>();
        this.listaMsgNavegacao = new ArrayList<>();
        this.numSatelites = 0;
        this.numMedicoes = 0;
    }

    public EpocaGPS(){
        this.listaPRNs = new ArrayList<>();
        this.listaCoordSatelites = new ArrayList<>();
        this.listaMedicoes = new ArrayList<>();
        this.listaMsgNavegacao = new ArrayList<>();
    }

    public EpocaGPS(GNSSDate UTC, ArrayList<Integer> listaPRNs){
        this.setData(UTC);
        this.setListaPRNs(listaPRNs);
        this.setNumSatelites(listaPRNs.size());
    }

    public EpocaGPS(GNSSDate UTC){
        this.setData(UTC);
        this.listaPRNs = new ArrayList<>();
        this.listaMedicoes = new ArrayList<>();
        this.listaCoordSatelites = new ArrayList<>();
    }

    public boolean addSatelitePRN(int PRN){
        if (!listaPRNs.contains(PRN)) { // FIXME REVER
            getListaPRNs().add(PRN);
            this.setNumSatelites(getListaPRNs().size());
            return true;
        }else{
//            Log.e("PRN-Epch","Repetindo satélite!");
            return false;
        }
    }

    public void addMedicao(GNSSMeasurement medicao){
        getListaMedicoes().add(medicao);
        this.setNumMedicoes(getListaMedicoes().size());
    }

    public void addMsgNavegacao(GNSSNavMsg msgNav){
        getListaMsgNavegacao().add(msgNav);
        this.setNumMsgNav(getListaMsgNavegacao().size());
    }

    public boolean containsSatellite(int PRN){
        return this.listaPRNs.contains(PRN);
    }

    @Override
    public String toString() {
        if (this.UTC != null){
        String msg = "ID: " + this.id + "\n" +
                     "UTC: Hora: " + getDateUTC().getHour() + " Minutos: " + getDateUTC().getMin() + " Segundos: " + getDateUTC().getSec() + " \n" +
                     "Nº de satélites: " + getNumSatelites() + " \n" +
                     "Lista de satélites: " + Arrays.toString(getListaPRNs().toArray());
        return msg;
        }else{
//            String msg = "ID: " + this.id + "\n";

            return "EPOCA EM CONSTRUÇÃO!";
        }
    }

    public GNSSDate getDateUTC() {
        return UTC;
    }

    public void setData(GNSSDate UTC) {
        this.UTC = UTC;
    }

    public ArrayList<Integer> getListaPRNs() {
        return listaPRNs;
    }

    public void setListaPRNs(ArrayList<Integer> listaPRNs) {
        this.listaPRNs = listaPRNs;
    }

    public int getNumSatelites() {
        return numSatelites;
    }

    public void setNumSatelites(int numSatelites) {
        this.numSatelites = numSatelites;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<GNSSMeasurement> getListaMedicoes() {
        return listaMedicoes;
    }

    public void setListaMedicoes(ArrayList<GNSSMeasurement> listaMedicoes) {
        this.listaMedicoes = listaMedicoes;
    }

    public int getNumMedicoes() {
        return numMedicoes;
    }

    public void setNumMedicoes(int numMedicoes) {
        this.numMedicoes = numMedicoes;
    }

    public ArrayList<CoordenadaGPS> getListaCoordSatelites() {
        return listaCoordSatelites;
    }

    public void setListaCoordSatelites(ArrayList<CoordenadaGPS> listaCoordSatelites) {
        this.listaCoordSatelites = listaCoordSatelites;
    }

    public ArrayList<GNSSNavMsg> getListaMsgNavegacao() {
        return listaMsgNavegacao;
    }

    public void setListaMsgNavegacao(ArrayList<GNSSNavMsg> listaMsgNavegacao) {
        this.listaMsgNavegacao = listaMsgNavegacao;
    }

    public int getNumMsgNav() {
        return numMsgNav;
    }

    public void setNumMsgNav(int numMsgNav) {
        this.numMsgNav = numMsgNav;
    }

    public Double getFctSeconds() {
        return FctSeconds;
    }

    public void setFctSeconds(Double fctSeconds) {
        FctSeconds = fctSeconds;
    }

    public int getGPSweekNumber() {
        return GPSweekNumber;
    }

    public void setGPSweekNumber(int GPSweekNumber) {
        this.GPSweekNumber = GPSweekNumber;
    }

    public int getGPSsecondsWeek() {
        return GPSsecondsWeek;
    }

    public void setGPSsecondsWeek(int GPSsecondsWeek) {
        this.GPSsecondsWeek = GPSsecondsWeek;
    }

    public Long getmLargestTowNs() {
        return mLargestTowNs;
    }

    public void setmLargestTowNs(Long mLargestTowNs) {
        this.mLargestTowNs = mLargestTowNs;
    }

    public Long getmArrivalTimeSinceGpsEpochNs() {
        return mArrivalTimeSinceGpsEpochNs;
    }

    public void setmArrivalTimeSinceGpsEpochNs(Long mArrivalTimeSinceGpsEpochNs) {
        this.mArrivalTimeSinceGpsEpochNs = mArrivalTimeSinceGpsEpochNs;
    }
}
