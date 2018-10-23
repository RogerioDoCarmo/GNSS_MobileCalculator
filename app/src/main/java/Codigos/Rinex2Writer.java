package Codigos;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Rinex2Writer {

    private final Object mFileLock = new Object();
    private BufferedWriter mFileWriter;
    private File mFile;

    private ArrayList<CoordenadaGPS> listaEpocas;
    private static final String pseudorange = "C"; // GPS C/A
    private static final String Frequency_GPS = "L1";
    private static final String TYPE = "O";

    private static final String MARKER = "GNSS Mobile Calculator";

    StringBuilder builder = new StringBuilder();

    public Rinex2Writer(ArrayList<CoordenadaGPS> epocas) {
        this.listaEpocas = epocas;
    }

    public Rinex2Writer(){
        criarCabecalho();
    }

    private void criarCabecalho() {
        builder.append("     2.11           OBSERVATION DATA    M (MIXED)           RINEX VERSION / TYPE"); // TODO SEPARAR EM 2 COLUNAS COM 2 APPENDS
        builder.append("\n");
        builder.append("teqc  2018Mar15                         20180605 08:43:32UTCPGM / RUN BY / DATE");
        builder.append("\n");
        builder.append("PPTE                                                        MARKER NAME");
        builder.append("\n");
        builder.append("41611M002                                                   MARKER NUMBER");
        builder.append("\n");
        builder.append("RBMC                IBGE/CGED                               OBSERVER / AGENCY");
        builder.append("\n");
        builder.append("5215K84090          TRIMBLE NETR9       5.33                REC # / TYPE / VERS");
        builder.append("\n");
        builder.append("5215K84090          TRIMBLE NETR9       5.33                REC # / TYPE / VERS");
        builder.append("\n");

//        String Xaprx = new DecimalFormat("########.#### ").format(listaEpocas.get(0).getX());
        String Xaprx = new DecimalFormat("########.#### ").format(3687624.3674);
//        String Yaprx = new DecimalFormat("########.#### ").format(listaEpocas.get(0).getY());
        String Yaprx = new DecimalFormat("########.#### ").format(-4620818.6827);
//        String Zaprx = new DecimalFormat("########.#### ").format(listaEpocas.get(0).getZ());
        String Zaprx = new DecimalFormat("########.#### ").format(-2386880.3805);

        StringBuilder lineAprx = new StringBuilder();

        lineAprx.append("  " + Xaprx);
        lineAprx.append("  " + Yaprx);
        lineAprx.append("  " + Zaprx);

        lineAprx.append("             APPROX POSITION XYZ");

        builder.append(lineAprx.toString());

        builder.append("\n");
        builder.append("        0.0020        0.0000        0.0000                  ANTENNA: DELTA H/E/N");
        builder.append("\n");
        builder.append("     1     1                                                WAVELENGTH FACT L1/2");
        builder.append("\n");

    }

    public String print() {
        Log.i("RINEX",builder.toString());
        return builder.toString();
    }

}
