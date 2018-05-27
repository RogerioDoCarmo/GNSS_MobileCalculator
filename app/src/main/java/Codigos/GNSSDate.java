package Codigos;

import android.support.annotation.NonNull;

public class GNSSDate implements Comparable<GNSSDate>{
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private double sec;

    /**
     * Creates a GNSS date using an UTC timestamp as entry.
     * <p> Used to determine an epoch for processing.
     * @see ProcessamentoPPS#ajustarEpocas()
     * @param year UTC year (0~99)
     * @param month UTC month (1~12)
     * @param day UTC day (1~31)
     * @param hour UTC year (0~24)
     * @param min UTC year (0~59)
     * @param sec UTC year (0.0~59.9)
     */
    public GNSSDate(int year, int month, int day, int hour, int min, double sec){ // fixme REVISAR intervalos dos campos
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.sec = sec;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
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

    /**
     *
     * @param another anote UTC GNSS datestamp to compare.
     * @return 0 if the two dates are equal. So we have the same epoch to process further.
     * @see ProcessamentoPPS#ajustarEpocas()
     */
    @Override
    public int compareTo(@NonNull GNSSDate another) {
        if (this.getDay() == another.getDay()     &&
            this.getHour() == another.getHour()   &&
            this.getMin() == another.getMin()     &&
            this.getMonth() == another.getMonth() &&
            this.getSec() == another.getSec()     &&
            this.getYear() == another.getYear())
                return 0;
        else
            return -1;
    }

}
