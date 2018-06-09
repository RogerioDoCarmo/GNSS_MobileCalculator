package Codigos;

import java.util.ArrayList;
import java.util.Arrays;

public class EpocaGPS {
    private int id;
    private GNSSDate UTC;
    private ArrayList<Integer> listaPRNs;
    private int numSatelites;
    private ArrayList<GNSSMeasurement> listaMedicoes;
    private int numMedicoes;
    private ArrayList<CoordenadaGPS> listaCoordSatelites;
    private int numMsgNav;
    private ArrayList<GNSSNavMsg> listaMsgNavegacao;

    public EpocaGPS(GNSSDate UTC, ArrayList<Integer> listaPRNs){
        this.setUTC(UTC);
        this.setListaPRNs(listaPRNs);
        this.setNumSatelites(listaPRNs.size());
    }

    public EpocaGPS(GNSSDate UTC){
        this.setUTC(UTC);
        this.listaPRNs = new ArrayList<>();
        this.listaMedicoes = new ArrayList<>();
        this.listaCoordSatelites = new ArrayList<>();
    }

    public void addSatelitePRN(int PRN){
        getListaPRNs().add(PRN);
        this.setNumSatelites(getListaPRNs().size());
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
        String msg = "ID: " + this.id + "\n" +
                     "UTC: Hora: " + getDateUTC().getHour() + " Minutos: " + getDateUTC().getMin() + " Segundos: " + getDateUTC().getSec() + " \n" +
                     "Nº de satélites: " + getNumSatelites() + " \n" +
                     "Lista de satélites: " + Arrays.toString(getListaPRNs().toArray());
        return msg;
    }

    public GNSSDate getDateUTC() {
        return UTC;
    }

    public void setUTC(GNSSDate UTC) {
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
}
