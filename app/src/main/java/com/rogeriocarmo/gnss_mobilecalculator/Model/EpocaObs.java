package com.rogeriocarmo.gnss_mobilecalculator.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class EpocaObs  implements Parcelable {
    //        private GNSSDate UTC = null;
    private static final String type = "G"; //FIXME CONSIDERAR OUTRAS CONSTELAÇÕES
    private GNSSDate Data_UTC ;
    private ArrayList<Integer> lista_PRNs;
    private ArrayList<Double>  lista_Pseudoranges;

    public EpocaObs(GNSSDate UTC, ArrayList<Integer> lista_PRNs, ArrayList<Double> lista_Pseudoranges ){
        this.Data_UTC = UTC;
        this.lista_PRNs = lista_PRNs;
        this.lista_Pseudoranges = lista_Pseudoranges;

    }

    public EpocaObs(Parcel in){ //TODO VERIFICAR OPERAÇÃO INSEGURA
        Data_UTC = (GNSSDate) in.readParcelable(GNSSDate.class.getClassLoader());
        lista_PRNs = in.readArrayList(Integer.class.getClassLoader());
        lista_Pseudoranges = in.readArrayList(Double.class.getClassLoader());

    }

    public String getType() {
        return type;
    }

    public GNSSDate getData_UTC() {
        return Data_UTC;
    }

    public ArrayList<Integer> getLista_PRNs() {
        return lista_PRNs;
    }

    public ArrayList<Double> getLista_Pseudoranges() {
        return lista_Pseudoranges;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeParcelable(Data_UTC,flags);
        dest.writeList(lista_PRNs);
        dest.writeList(lista_Pseudoranges);
    }

    public static final Parcelable.Creator<EpocaObs> CREATOR = new Parcelable.Creator<EpocaObs>() {
        public EpocaObs createFromParcel(Parcel in) {
            return new EpocaObs(in);
        }

        public EpocaObs[] newArray(int size) {
            return new EpocaObs[size];
        }
    };

}
