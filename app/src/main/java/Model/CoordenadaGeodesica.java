package Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class CoordenadaGeodesica implements Comparable<CoordenadaGeodesica>, Parcelable {
    private int NumEpch;
    private double latDegrees;
    private double lonDegrees;
    private double altMeters;

    /**
     * Creates an instance of a GPS coordinate to represent a satellite ou receiver position.
     *
     * @param NumEpch    The satellite ID (NumEpch).
     * @param latDegrees The latDegrees coordinate in degrees of the object in the WGS 84
     * @param lonDegrees The lonDegrees coordinate in degrees of the object in the WGS 84
     * @param altMeters  The altMeters coordinate of the object in the WGS 84
     */
    public CoordenadaGeodesica(int NumEpch, double latDegrees, double lonDegrees, double altMeters) {
        this.NumEpch = NumEpch;
        this.latDegrees = latDegrees;
        this.lonDegrees = lonDegrees;
        this.altMeters = altMeters;
    }

    public CoordenadaGeodesica(Parcel in) {
        NumEpch = in.readInt();
        latDegrees = in.readDouble();
        lonDegrees = in.readDouble();
        altMeters = in.readDouble();
    }

    public int getNumEpch() {
        return NumEpch;
    }

    public void setNumEpch(int numEpch) {
        this.NumEpch = numEpch;
    }

    public double getLatDegrees() {
        return latDegrees;
    }

    public void setLatDegrees(double latDegrees) {
        this.latDegrees = latDegrees;
    }

    public double getLonDegrees() {
        return lonDegrees;
    }

    public void setLonDegrees(double lonDegrees) {
        this.lonDegrees = lonDegrees;
    }

    public double getAltMeters() {
        return altMeters;
    }

    public void setAltMeters(double altMeters) {
        this.altMeters = altMeters;
    }

    @Override
    public int compareTo(@NonNull CoordenadaGeodesica another) {
        return (this.NumEpch - another.getNumEpch());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(NumEpch);
        dest.writeDouble(latDegrees);
        dest.writeDouble(lonDegrees);
        dest.writeDouble(altMeters);
    }

    public static final Parcelable.Creator<CoordenadaGeodesica> CREATOR = new Parcelable.Creator<CoordenadaGeodesica>() {
        public CoordenadaGeodesica createFromParcel(Parcel in) {
            return new CoordenadaGeodesica(in);
        }

        public CoordenadaGeodesica[] newArray(int size) {
            return new CoordenadaGeodesica[size];
        }
    };

}
