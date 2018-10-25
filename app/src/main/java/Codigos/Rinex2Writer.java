package Codigos;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class Rinex2Writer {

    private final Object mFileLock = new Object();
    private BufferedWriter mFileWriter;
    private File mFile;

//    private final Context mContext;

    private static final String FILE_PREFIX = "gnss";

    private ArrayList<CoordenadaGPS> listaEpocas;
    private static final String pseudorange = "C"; // GPS C/A
    private static final String Frequency_GPS = "L1";
    private static final String TYPE = "O";

    private static final String MARKER = "GNSS Mobile Calculator";

    StringBuilder builder = new StringBuilder();

    public Rinex2Writer(Context mContext, ArrayList<CoordenadaGPS> epocas) {
//        this.mContext = mContext;
        this.listaEpocas = epocas;
    }

    public Rinex2Writer(Context context){
//        this.mContext = context;
        startNewLog();
        send();
    }

    public Rinex2Writer(){

        startNewLog();
        send();
    }

    private void criarCabecalho() {
        if (mFileWriter == null) {
            return;
        }

        try{
            mFileWriter.write("     2.11           OBSERVATION DATA    M (MIXED)           RINEX VERSION / TYPE"); // TODO SEPARAR EM 2 COLUNAS COM 2 APPENDS
            mFileWriter.newLine();
            mFileWriter.write("teqc  2018Mar15                         20180605 08:43:32UTCPGM / RUN BY / DATE");
            mFileWriter.newLine();
            mFileWriter.write("PPTE                                                        MARKER NAME");
            mFileWriter.newLine();
            mFileWriter.write("41611M002                                                   MARKER NUMBER");
            mFileWriter.newLine();
            mFileWriter.write("RBMC                IBGE/CGED                               OBSERVER / AGENCY");
            mFileWriter.newLine();
            mFileWriter.write("5215K84090          TRIMBLE NETR9       5.33                REC # / TYPE / VERS");
            mFileWriter.newLine();
            mFileWriter.write("5215K84090          TRIMBLE NETR9       5.33                REC # / TYPE / VERS");
            mFileWriter.newLine();

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

            mFileWriter.write(lineAprx.toString());

            mFileWriter.newLine();
            builder.append("        0.0020        0.0000        0.0000                  ANTENNA: DELTA H/E/N");
            mFileWriter.newLine();
            builder.append("     1     1                                                WAVELENGTH FACT L1/2");
            mFileWriter.newLine();
        } catch (IOException e) {
            Log.e("ERROR_WRITING_FILE", e.getMessage());
        }


    }

    public String print() {
        Log.i("RINEX",builder.toString());
        return builder.toString();
    }

    public void startNewLog() {
        synchronized (mFileLock) {
            File baseDirectory;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                baseDirectory = new File(Environment.getExternalStorageDirectory(), FILE_PREFIX);
                baseDirectory.mkdirs();
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                Log.e("RINEX", "Cannot write to external storage.");
                return;
            } else {
                Log.e("RINEX", "Cannot read external storage.");
                return;
            }

//            SimpleDateFormat formatter = new SimpleDateFormat("yyy_MM_dd_HH_mm_ss");
//            Date now = new Date();
            String fileName = String.format("%s_%s.18o", FILE_PREFIX, "TESTE1");
            File currentFile = new File(baseDirectory, fileName);
            String currentFilePath = currentFile.getAbsolutePath();
            BufferedWriter currentFileWriter;
            try {
                currentFileWriter = new BufferedWriter(new FileWriter(currentFile));
                criarCabecalho();
            } catch (IOException e) {
                Log.e("Could not open file: " + currentFilePath, e.getMessage());
                return;
            }

            // initialize the contents of the file
//            try {
//                currentFileWriter.write(COMMENT_START);
//                currentFileWriter.newLine();
//            } catch (IOException e) {
//                Log.e("Count not init file: " + currentFilePath, e.getMessage());
//                return;
//            }


            mFile = currentFile;
            mFileWriter = currentFileWriter;


//            }
        }
    }

    private void escrever_observacoes(){
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Send the current log via email or other options selected from a pop menu shown to the user. A
     * new log is started when calling this function.
     */
    public void send() {
        if (mFile == null) {
            return;
        }

//        Intent emailIntent = new Intent(Intent.ACTION_SEND); // TODO IMPLEMENTAR SEND
//        emailIntent.setType("*/*");
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SensorLog");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
//        // attach the file
//        Uri fileURI =
//                FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", mFile);
//        emailIntent.putExtra(Intent.EXTRA_STREAM, fileURI);
//        getUiComponent().startActivity(Intent.createChooser(emailIntent, "Send log.."));
        if (mFileWriter != null) {
            try {
                mFileWriter.flush();
                mFileWriter.close();
                mFileWriter = null;

                Log.i("RINEX_FINAL", mFileWriter.toString());

            } catch (IOException e) {
                Log.e("Unable to close", e.getMessage());
                return;
            }
        }
    }

}
