package Codigos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.YearMonth;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

//import android.support.v4.BuildConfig;

import static Codigos.FileHelper.getPrivateStorageDir;
import static Codigos.FileHelper.isExternalStorageWritable;
import static Codigos.FileHelper.writeTextFile2External;
import static Codigos.GNSSConstants.EP_02_APP_X;
import static Codigos.GNSSConstants.EP_02_APP_Y;
import static Codigos.GNSSConstants.EP_02_APP_Z;

public class Rinex2Writer {


    private ArrayList<EpocaObs> listaEpocas;
    private static final String FILE_PREFIX = "GNSS";
    private static final String PSEUDORANGE = "C"; // GPS C/A
    private static final String FREQ_GPS = "L1";
    private static final String RINEX_TYPE = "O"; // Observation File
    private static final String MARKER = "GNSS Mobile Calculator";

    GNSSDate dataEpchAtual;
    private File newFile;
    private final Context mContext;
    private ArrayList<String> txtContent;

    public boolean gravarRINEX(){
        if (isExternalStorageWritable()){
            newFile = startNewLog();
            criarCabecalho();
//            escrever_observacoes(2648);
            escrever_todas_observacoes();
            try {
                writeTextFile2External(newFile,
                                        txtContent.toArray(new String[txtContent.size()]));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
            return false;
    }

    public Rinex2Writer(Context mContext, ArrayList<EpocaObs> epocas) {
        this.mContext = mContext;
        this.txtContent = new ArrayList<>();
        this.listaEpocas = epocas;
    }

    public Rinex2Writer(Context context){
        this.mContext = context;
        this.txtContent = new ArrayList<>();
    }

    private void criarCabecalho() {
        txtContent.add("     2.11           OBSERVATION DATA    M (MIXED)           RINEX VERSION / TYPE");
        txtContent.add("\n");
        txtContent.add("GNSS Calculator     FCT/UNESP               2018Mar15 UTC   PGM / RUN BY / DATE"); //FIXME REVER
        txtContent.add("\n");
        txtContent.add("GNSS Mobile Calculator                                      MARKER NAME");
        txtContent.add("\n");
        txtContent.add("41611M002                                                   MARKER NUMBER");//FIXME REVER!
        txtContent.add("\n");
        txtContent.add("Android App         GNSS Calculator                         OBSERVER / AGENCY");
        txtContent.add("\n");
        txtContent.add("0040109624          motorola            moto x4             REC # / TYPE / VERS");
        txtContent.add("\n");
        txtContent.add("0040109624          moto x4             NONE                ANT # / TYPE");
        txtContent.add("\n");

        //TODO AUTOMATIZAR
        String Xaprx = new DecimalFormat("########.####").format(EP_02_APP_X);
        String Yaprx = new DecimalFormat("########.####").format(EP_02_APP_Y);
        String Zaprx = new DecimalFormat("########.####").format(EP_02_APP_Z);

        StringBuilder lineAprx = new StringBuilder();

        lineAprx.append("  " + Xaprx);
        lineAprx.append(" "  + Yaprx);
        lineAprx.append(" "  + Zaprx);

        lineAprx.append("                  APPROX POSITION XYZ");
        txtContent.add(lineAprx.toString());
        txtContent.add("\n");

        txtContent.add("        0.0000        0.0000        0.0000                  ANTENNA: DELTA H/E/N");
        txtContent.add("\n");
        txtContent.add("     1     1                                                WAVELENGTH FACT L1/2");
        txtContent.add("\n");

        //        txtContent.add("    11    L1    L2    L5    C1    P1    C2    P2    C5    S1# / TYPES OF OBSERV");
        String typeLine1 = String.format("     1    %s                                                # / TYPES OF OBSERV",
                                                    "C1"); // C/A Pseudorange

        txtContent.add(typeLine1);
        txtContent.add("\n");
        txtContent.add("     1.0000                                                 INTERVAL");
        txtContent.add("\n");
        txtContent.add("    18                                                      LEAP SECONDS");// FIXME USAR String.form
        txtContent.add("\n");
        txtContent.add("Trabalho de Conclusao de Curso - FCT/UNESP - Computacao     COMMENT");
        txtContent.add("\n");
        txtContent.add("Autor: Rogerio Ramos Rodrigues do Carmo                     COMMENT");
        txtContent.add("\n");
        txtContent.add("Arquivo gerado a partir de medicoes de aparelho Android     COMMENT");
        txtContent.add("\n");

        // , new DecimalFormat("#.####### ").format(0.0000000)
        @SuppressLint("DefaultLocale") String firstObs = String.format("  %d     %d    %d    %d     %d    0.0000000     GPS       TIME OF FIRST OBS",
                listaEpocas.get(0).getData_UTC().getYear() + 2000,
                listaEpocas.get(0).getData_UTC().getMonth(),
                listaEpocas.get(0).getData_UTC().getDay_Month(),
                listaEpocas.get(0).getData_UTC().getHour(),
                listaEpocas.get(0).getData_UTC().getMin());

        txtContent.add(firstObs);
        txtContent.add("\n");
        txtContent.add("                                                            END OF HEADER");
        txtContent.add("\n");
    }

    public File startNewLog() {
            File baseDirectory = null;
            if ( isExternalStorageWritable() ) {
                try {
                    String fileName = String.format("%s_%s.18o", FILE_PREFIX, "Teste_31-10_PT02");
                    baseDirectory = getPrivateStorageDir(mContext,fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("RINEX", "Cannot read external storage.");
                return null;
            }

            return baseDirectory;
    }

    private void escrever_todas_observacoes(){
        for (int i = 0; i < listaEpocas.size(); i++){
            escrever_observacoes(i);
        }
    }

    @SuppressLint("DefaultLocale")
    private void escrever_observacoes(int INDEX_EPCH){
        StringBuilder listaSatEpch = new StringBuilder();

        for (int i = 0; i <listaEpocas.get(INDEX_EPCH).getLista_PRNs().size(); i++) {
           listaSatEpch.append("G" + String.format("%02d",listaEpocas.get(INDEX_EPCH).getLista_PRNs().get(i)));
        }

        @SuppressWarnings("MalformedFormatString") @SuppressLint("DefaultLocale") String epchHeaderLine = String.format(" %d %s %s %s %s %s  0 %s%s\n",
                listaEpocas.get(INDEX_EPCH).getData_UTC().getYear() % 2000,
                String.format("%2d",listaEpocas.get(INDEX_EPCH).getData_UTC().getMonth()),
                String.format("%2d",listaEpocas.get(INDEX_EPCH).getData_UTC().getDay_Month()),
                String.format("%2d",listaEpocas.get(INDEX_EPCH).getData_UTC().getHour()),
                String.format("%2d",listaEpocas.get(INDEX_EPCH).getData_UTC().getMin()),
//                new DecimalFormat("#.#######").format(listaEpocas.get(INDEX_EPCH).getData_UTC().getSec()),
                String.format("%2d",Math.round(listaEpocas.get(INDEX_EPCH).getData_UTC().getSec())) + ".0000000",
                String.format("%2d",listaEpocas.get(INDEX_EPCH).getLista_PRNs().size()),
                listaSatEpch);

        txtContent.add(epchHeaderLine);

        String newLine;
        for (int i = 0; i < listaEpocas.get(INDEX_EPCH).getLista_PRNs().size(); i++) {
            newLine = String.format("  %s",
                            new DecimalFormat("########.###").format(
                                        listaEpocas.get(INDEX_EPCH).getLista_Pseudoranges().get(i)));
            txtContent.add(newLine);
            txtContent.add("\n");
        }
    }

    public void send(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse(newFile.getAbsolutePath());
        intent.setDataAndType(uri, "text/plain");
        mContext.startActivity(intent);
    }

}
