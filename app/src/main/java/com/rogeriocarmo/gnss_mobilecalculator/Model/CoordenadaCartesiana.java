package com.rogeriocarmo.gnss_mobilecalculator.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class CoordenadaCartesiana implements Comparable<CoordenadaCartesiana>, Parcelable {
    private int NumEpch;
    private double Xmeters;
    private double Ymeters;
    private double Zmeters;

    /**
     * Creates an instance of a GPS coordinate to represent a satellite ou receiver position.
     *
     * @param NumEpch    The satellite ID (NumEpch).
     * @param Xmeters The X coordinate in meters of the object in the WGS 84
     * @param Ymeters The Y coordinate in meters of the object in the WGS 84
     * @param Zmeters The Z coordinate in meters of the object in the WGS 84
     */
    public CoordenadaCartesiana(int NumEpch, double Xmeters, double Ymeters, double Zmeters) {
        this.NumEpch = NumEpch;
        this.Xmeters = Xmeters;
        this.Ymeters = Ymeters;
        this.Zmeters = Zmeters;
    }

    public CoordenadaCartesiana(Parcel in) {
        NumEpch = in.readInt();
        Xmeters = in.readDouble();
        Ymeters = in.readDouble();
        Zmeters = in.readDouble();
    }

    public int getNumEpch() {
        return NumEpch;
    }

    public void setNumEpch(int numEpch) {
        this.NumEpch = numEpch;
    }

    public double getXmeters() {
        return Xmeters;
    }

    public void setXmeters(double xmeters) {
        this.Xmeters = xmeters;
    }

    public double getYmeters() {
        return Ymeters;
    }

    public void setYmeters(double ymeters) {
        this.Ymeters = ymeters;
    }

    public double getZmeters() {
        return Zmeters;
    }

    public void setZmeters(double zmeters) {
        this.Zmeters = zmeters;
    }

    @Override
    public int compareTo(@NonNull CoordenadaCartesiana another) {
        return (this.NumEpch - another.getNumEpch());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(NumEpch);
        dest.writeDouble(Xmeters);
        dest.writeDouble(Ymeters);
        dest.writeDouble(Zmeters);
    }

    public static final Creator<CoordenadaCartesiana> CREATOR = new Creator<CoordenadaCartesiana>() {
        public CoordenadaCartesiana createFromParcel(Parcel in) {
            return new CoordenadaCartesiana(in);
        }

        public CoordenadaCartesiana[] newArray(int size) {
            return new CoordenadaCartesiana[size];
        }
    };

}
