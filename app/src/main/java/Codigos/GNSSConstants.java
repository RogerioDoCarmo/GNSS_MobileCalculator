package Codigos;

import java.util.concurrent.TimeUnit;

public class GNSSConstants {
    /**
     * Days of the week as integer numbers.
     */
    public static final int DAY_DOM = 0;
    public static final int DAY_SEG = 1;
    public static final int DAY_TER = 2;
    public static final int DAY_QUA = 3;
    public static final int DAY_QUI = 4;
    public static final int DAY_SEX = 5;
    public static final int DAY_SAB = 6;

    public static final int CONSTELLATION_GPS = 1;
    public static final int CONSTELLATION_GLONASS = 3;
    public static final Long WEEKSEC = 604800L; /*Number of seconds in a week*/
    public static final double  LIGHTSPEED = 2.99792458e8;
    /*Constantes do GNSSLogger*/

    public static final byte IONOSPHERIC_PARAMETERS_PAGE_18_SV_ID = 56;

    public static final int WORD_SIZE_BITS = 30;
    public static final int WORD_PADDING_BITS = 2;
    public static final int BYTE_AS_BITS = 8;
    public static final int GPS_CYCLE_WEEKS = 1024;
    public static final int IODE_TO_IODC_MASK = 0xFF;

    public static final int SUBFRAME_1 = (1 << 0);
    public static final int SUBFRAME_2 = (1 << 1);
    public static final int SUBFRAME_3 = (1 << 2);
    public static final int SUBFRAME_4 = (1 << 3);
    public static final int SUBFRAME_5 = (1 << 4);

    public static final long INTEGER_RANGE = 0xFFFFFFFFL;
    // 3657 is the number of days between the unix epoch and GPS epoch as the GPS epoch started on
    // Jan 6, 1980
    public static final long GPS_EPOCH_AS_UNIX_EPOCH_MS = TimeUnit.DAYS.toMillis(3657);
    // A GPS Cycle is 1024 weeks, or 7168 days
    public static final long GPS_CYCLE_MS = TimeUnit.DAYS.toMillis(7168);

    /** Maximum possible number of GPS satellites */
    public static final int MAX_NUMBER_OF_SATELLITES = 32;

    public static final int L1_CA_MESSAGE_LENGTH_BYTES = 40;

    public static final String TAG = "PseudorangePositionVelocityFromRealTimeEvents";
    public static final double SECONDS_PER_NANO = 1.0e-9;
    public static final int TOW_DECODED_MEASUREMENT_STATE_BIT = 3;
    /** Average signal travel time from GPS satellite and earth */
    public static final int VALID_ACCUMULATED_DELTA_RANGE_STATE = 1;
    public static final int MINIMUM_NUMBER_OF_USEFUL_SATELLITES = 4;
    public static final int C_TO_N0_THRESHOLD_DB_HZ = 18;
}
