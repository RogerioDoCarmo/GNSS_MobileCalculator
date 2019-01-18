package com.rogeriocarmo.gnss_mobilecalculator.Controller;

import com.rogeriocarmo.gnss_mobilecalculator.Model.EpocaGPS;
import com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSConstants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class AnaliseEpoca {

    private EpocaGPS epoca;

    private Double minCn0DbHz;
    private int indexMinCn0DbHz;
    private Double maxCn0DbHz;
    private int indexMaxCn0DbHz;
    private Double meanCn0DbHz;

    private ArrayList<Double> listCn0DbHz;
    private ArrayList<Integer> listPRNs;

    //TODO FAZER DESVIO PADRÃO

    public AnaliseEpoca(EpocaGPS epocaAnalise) {
        this.epoca = epocaAnalise;
        calcMinMaxMean();
    }

    private void calcMinMaxMean() {
        ArrayList<Double> listaCn0DbHz = epoca.getListCn0DbHz();
        listCn0DbHz = new ArrayList<>();
        listPRNs    = new ArrayList<>();

        Double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;
        Double mean = 0d;
        int sizeList = listaCn0DbHz.size();

        for (int i = 0; i < sizeList; i++) {

            listCn0DbHz.add(listaCn0DbHz.get(i));

            if (listaCn0DbHz.get(i) > max){
                max = listaCn0DbHz.get(i);
                indexMaxCn0DbHz = i;
            }

            if (listaCn0DbHz.get(i) < min){
                min = listaCn0DbHz.get(i);
                indexMinCn0DbHz = i;
            }

            listPRNs.add(epoca.getListaPRNs().get(i)); //TODO REVISAR EFICIENCIA

            mean += listaCn0DbHz.get(i);
        }

        mean = mean / sizeList;

        this.minCn0DbHz = min;
        this.maxCn0DbHz = max;
        this.meanCn0DbHz = mean;
    }

    public Double getMinCn0DbHz() {
        return minCn0DbHz;
    }

    public Double getMaxCn0DbHz() {
        return maxCn0DbHz;
    }

    public Double getMeanCn0DbHz() {
        return meanCn0DbHz;
    }

    public int getSatMinCn0DbHz() {
        return epoca.getListaPRNs().get(indexMinCn0DbHz);
    }

    public int getSatMaxCn0DbHz() {
        return epoca.getListaPRNs().get(indexMaxCn0DbHz);
    }

    public ArrayList<Double> getListCn0DbHz() {
        return listCn0DbHz;
    }

    public ArrayList<Integer> getListPRNs() {
        return listPRNs;
    }

//    public double getErrorClockMeters(){
//        return epoca.get * GNSSConstants.LIGHTSPEED;
//    }

    @Override
    public String toString(){
        return
                "ID: " + epoca.getId() + "\n" +
                "Year: " + epoca.getDateUTC().getYear() + " Month: " + epoca.getDateUTC().getMonth() + " Day: " + epoca.getDateUTC().getDay_Month() + " \n" +
                "Hour: " + epoca.getDateUTC().getHour() + " Minutes: " + epoca.getDateUTC().getMin() + " Seconds: " + epoca.getDateUTC().getSec() + " \n" +
                "Number of satellites: " + epoca.getNumSatelites() + " \n" +
                "List of Satellites: " + Arrays.toString(epoca.getListaPRNs().toArray()) + "\n" +
                "List of Cn0DbHz per Satellite: " + Arrays.toString(listCn0DbHz.toArray()) + "\n\n" +
                "Minimum Cn0DbHz: " + minCn0DbHz + "\n" +
                "Satellite of Minimum Cn0DbHz: " + epoca.getListaPRNs().get(indexMinCn0DbHz) + "\n" +
                "Maximum Cn0DbHz: " + maxCn0DbHz + "\n" +
                "Satellite of Maximum Cn0DbHz: " + epoca.getListaPRNs().get(indexMaxCn0DbHz) + "\n" +
                "Arithmetic mean of Cn0DbHz: " + new DecimalFormat("###.###").format(meanCn0DbHz)
        ;

    }

    public String getAsStringLine() {
        return
                epoca.getDateUTC().toString() + "; " +
                epoca.getId() + "; " +
                epoca.getNumSatelites() + "; " +
                minCn0DbHz + "; " +
                maxCn0DbHz + "; " +
                new DecimalFormat("###.###").format(meanCn0DbHz) + ";"
        ;
    }

}
