package com.rogeriocarmo.gnss_mobilecalculator.Controller;

import com.rogeriocarmo.gnss_mobilecalculator.Model.EpocaGPS;

import java.util.ArrayList;

public class AnaliseEpoca {

    private EpocaGPS epoca;

    private Double minCn0DbHz;
    private int indexMinCn0DbHz;
    private Double maxCn0DbHz;
    private int indexMaxCn0DbHz;
    private Double meanCn0DbHz;
    //TODO FAZER DESVIO PADRÃO

    public AnaliseEpoca(EpocaGPS epocaAnalise) {
        this.epoca = epocaAnalise;
        calcMinMaxMean();
    }

    private void calcMinMaxMean() {
        ArrayList<Double> listaCn0DbHz = epoca.getListCn0DbHz();

        Double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;
        Double mean = 0d;
        int sizeList = listaCn0DbHz.size();

        for (int i = 0; i < sizeList; i++) {
            if (listaCn0DbHz.get(i) > max){
                max = listaCn0DbHz.get(i);
                indexMaxCn0DbHz = i;
            }

            if (listaCn0DbHz.get(i) < min){
                min = listaCn0DbHz.get(i);
                indexMinCn0DbHz = i;
            }

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

}
