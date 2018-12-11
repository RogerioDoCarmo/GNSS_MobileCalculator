package com.rogeriocarmo.gnss_mobilecalculator.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.DecimalFormat;

public class GNSSDate implements Comparable<GNSSDate>, Parcelable {
    private int year;
    private int month;
    private int day;
    private int day_week;
    private int hour;
    private int min;
    private double sec;

    /**
     * Creates a GNSS date using an UTC timestamp as entry.
     * <p> Used to determine an epoch for processing.
     * @param year UTC year (0~99)
     * @param month UTC month (1~12)
     * @param day UTC day (1~31)
     * @param hour UTC year (0~24)
     * @param min UTC year (0~59)
     * @param sec UTC year (0.0~59.9)
     */
    public GNSSDate(int year, int month, int day, int hour, int min, double sec){ // fixme REVISAR intervalos do campo ANO
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.sec = sec;
    }

    public GNSSDate(Parcel in){
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.hour = in.readInt();
        this.min = in.readInt();
        this.sec = in.readDouble();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay_Month() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public double getSec() {
        return sec;
    }

    public int getDay_week() {
        return day_week;
    }

    public void setDay_week(int day_week) {
        this.day_week = day_week;
    }

    @Override
    public String toString(){
        return (year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + String.format("%s", new DecimalFormat("###.###").format(sec)));
    }

    /**
     *
     * @param another another UTC GNSS datestamp to compare.
     * @return 0 if the two dates are equal. So we have the same epoch to process further.
     */
    @Override
    public int compareTo(@NonNull GNSSDate another) {
        if (this.getDay_Month() == another.getDay_Month()     &&
            this.getHour() == another.getHour()   &&
            this.getMin() == another.getMin()     &&
            this.getMonth() == another.getMonth() &&
            this.getSec() == another.getSec()     &&
            this.getYear() == another.getYear())
                return 0;
        else
            return -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(day_week);
        dest.writeInt(hour);
        dest.writeInt(min);
        dest.writeDouble(sec);
    }

    public static final Parcelable.Creator<GNSSDate> CREATOR = new Parcelable.Creator<GNSSDate>() {
        public GNSSDate createFromParcel(Parcel in) {
            return new GNSSDate(in);
        }

        public GNSSDate[] newArray(int size) {
            return new GNSSDate[size];
        }
    };
}
