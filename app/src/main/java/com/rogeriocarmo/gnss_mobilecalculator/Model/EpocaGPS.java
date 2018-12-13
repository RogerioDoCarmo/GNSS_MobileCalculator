package com.rogeriocarmo.gnss_mobilecalculator.Model;

import java.util.ArrayList;
import java.util.Arrays;

public class EpocaGPS{

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

    public EpocaGPS(Double FctSeconds){
        this.setFctSeconds(FctSeconds);
        this.listaPRNs = new ArrayList<>();
        this.listaCoordSatelites = new ArrayList<>();
        this.listaMedicoes = new ArrayList<>();
        this.listaMsgNavegacao = new ArrayList<>();
        this.numSatelites = 0;
        this.numMedicoes = 0;
    }

    /*Construtores não-utilizados*/
    public EpocaGPS(){
        this.listaPRNs = new ArrayList<>();
        this.listaCoordSatelites = new ArrayList<>();
        this.listaMedicoes = new ArrayList<>();
        this.listaMsgNavegacao = new ArrayList<>();
    }

    public EpocaGPS(Long allRxNanos){
        this.mArrivalTimeSinceGpsEpochNs = allRxNanos;
        this.listaPRNs = new ArrayList<>();
        this.listaCoordSatelites = new ArrayList<>();
        this.listaMedicoes = new ArrayList<>();
        this.listaMsgNavegacao = new ArrayList<>();
        this.numSatelites = 0;
        this.numMedicoes = 0;
    }

    public EpocaGPS(GNSSDate UTC){
        this.setData(UTC);
        this.listaPRNs = new ArrayList<>();
        this.listaMedicoes = new ArrayList<>();
        this.listaCoordSatelites = new ArrayList<>();
    }

    public EpocaGPS(GNSSDate UTC, ArrayList<Integer> listaPRNs){
        this.setData(UTC);
        this.setListaPRNs(listaPRNs);
        this.setNumSatelites(listaPRNs.size());
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

    public boolean excluirSatelitePRN(int PRN){
        Integer prnExcluir = PRN;
        if (listaPRNs.remove(prnExcluir)){
            for (int i = 0; i < listaMedicoes.size(); i++){
                if (listaMedicoes.get(i).getSvid() == prnExcluir){
                    listaMedicoes.remove(i);
                }
            }
            this.numSatelites = listaPRNs.size();
            this.numMedicoes = listaMedicoes.size();
            return true;
        }
        return false;
    }

    public boolean containsSatellite(int PRN){
        return this.listaPRNs.contains(PRN);
    }

    public void addMsgNavegacao(GNSSNavMsg msgNav){
        getListaMsgNavegacao().add(msgNav);
        this.setNumMsgNav(getListaMsgNavegacao().size());
    }

    @Override
    public String toString() {
        if (this.UTC != null){
        String msg = //"ID: " + this.id + "\n" +
                     "Year: " + UTC.getYear() + " Month: " + UTC.getMonth() + " Day: " + UTC.getDay_Month() + " \n" +
                     "Hour: " + getDateUTC().getHour() + " Minutes: " + getDateUTC().getMin() + " Seconds: " + getDateUTC().getSec() + " \n" +
                     "Number of satellites: " + getNumSatelites() + " \n" +
                     "Satellites: " + Arrays.toString(getListaPRNs().toArray());
        return msg;
        }else{
            return "Under construction!!!";
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

    public ArrayList<Double> getPseudorangesObs(){
        ArrayList<Double> lista = new ArrayList<>();

        for (int i = 0; i < listaMedicoes.size(); i++){
            lista.add( listaMedicoes.get(i).getPseudorangeMeters());
        }

        return lista;
    }

}
