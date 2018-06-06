package Codigos;

import java.util.ArrayList;
import java.util.Arrays;

public class EpocaGPS {
    private int id;
    private GNSSDate UTC;
    private ArrayList<Integer> listaPRNs;
    private int numSatelites;

    public EpocaGPS(GNSSDate UTC, ArrayList<Integer> listaPRNs){
        this.setUTC(UTC);
        this.setListaPRNs(listaPRNs);
        this.setNumSatelites(listaPRNs.size());
    }

    public EpocaGPS(GNSSDate UTC){
        this.setUTC(UTC);
        this.listaPRNs = new ArrayList<>();
    }

    public void addSatelite(int PRN){
        getListaPRNs().add(PRN);
        this.setNumSatelites(getListaPRNs().size());
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
}
