package Codigos;

import android.content.Context;
import android.util.Log;

import com.rogeriocarmo.gnss_mobilecalculator.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReaderRINEX {

    public static ArrayList<GNSSNavMsg> listaNavMsgs = new ArrayList<>();

    public ReaderRINEX(){ //TODO Por enquanto pegar da pasta raw assets msm!
        this.listaNavMsgs = new ArrayList<>();
    }

    public static String readRINEX_RawAssets(Context context) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.hour3470)));
        int numEfemerides = contEfemerides(context);

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();

        //PULANDO O CABEÇALHO
        String mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();

        String sub = "";

        for (int i = 1; i < numEfemerides; i++){
            GNSSNavMsg efemeride = new GNSSNavMsg();
            String PRN;
            try{
                PRN = mLine.substring(0,2);
            }catch (Exception er){
                PRN = mLine.substring(1,2);
            }

            Log.i("PRNN", PRN); // FIXME
            efemeride.setPRN(PRN);

            double Toc = calcTOC_tr_(Integer.valueOf(mLine.substring(9,11).replaceAll("\\s","")), // dia
                                     Integer.valueOf(mLine.substring(12,14).replaceAll("\\s","")), // hora
                                     Integer.valueOf(mLine.substring(15,17).replaceAll("\\s","")), // minuto
                                     Double.valueOf(mLine.substring(18,22).replaceAll("\\s",""))); // segundo FIXME

            Log.i("TOCC","Dia: " + mLine.substring(9,11).replaceAll("\\s","") +
                                " Hora: " + mLine.substring(12,14).replaceAll("\\s","") +
                                " Minutos: " + mLine.substring(15,17).replaceAll("\\s","") +
                                " Segundos: " + mLine.substring(18,22).replaceAll("\\s","")  );

            efemeride.setToc(Toc);

            Log.i("af0","af0: " + mLine.substring(22,41).replace('D','e').replaceAll("\\s",""));
            Log.i("af1","af1: " + mLine.substring(41,60).replace('D','e').replaceAll("\\s",""));
            Log.i("af2","af2: " + mLine.substring(60,79).replace('D','e').replaceAll("\\s",""));

            double af0 = Double.valueOf(mLine.substring(22,41).replace('D','e')
                    .replaceAll("\\s",""));

            double af1 = Double.valueOf(mLine.substring(41,60).replace('D','e')
                    .replaceAll("\\s",""));

            double af2 = Double.valueOf(mLine.substring(60,79).replace('D','e')
                    .replaceAll("\\s",""));

            efemeride.setAf0(af0);
            efemeride.setAf1(af1);
            efemeride.setAf2(af2);
//--------------------------------------------------------------------------------------------------
            mLine = reader.readLine();

            sub = mLine.substring(3, 22).replace('D', 'e');
            double iode = Double.parseDouble(sub.trim());
            // TODO check double -> int conversion ?
            efemeride.setIODE(iode);

            Log.i("IODE",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCrs(Double.parseDouble(sub.trim()));

            Log.i("Crs",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setDelta_n(Double.parseDouble(sub.trim()));

            Log.i("Delta_n",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setM0(Double.parseDouble(sub.trim()));

            Log.i("M0",sub);
//--------------------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------------------
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double Cuc = Double.parseDouble(sub.trim());
            efemeride.setCuc(Cuc);

            Log.i("Cuc",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setE(Double.parseDouble(sub.trim()));

            Log.i("E",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setCus(Double.parseDouble(sub.trim()));

            Log.i("Cus",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setAsqrt(Double.parseDouble(sub.trim()));

            Log.i("Asqrt",sub);
//--------------------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------------------
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double toe = Double.parseDouble(sub.trim());
            efemeride.setToe(toe);

            Log.i("Toe",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCic(Double.parseDouble(sub.trim()));

            Log.i("Cic",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setOMEGA(Double.parseDouble(sub.trim()));

            Log.i("OMEGA",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setCis(Double.parseDouble(sub.trim()));

            Log.i("Cis",sub);
//--------------------------------------------------------------------------------------------------


            mLine = reader.readLine();
            mLine = reader.readLine();
            mLine = reader.readLine();
            mLine = reader.readLine();
            mLine = reader.readLine();

            listaNavMsgs.add(efemeride);

        }

        reader.close();
        return sb.toString();
    }

    public static double calcTOC_tr_(int D, int HOR, int MIN, double SEG) { // TODO Adotar a abordagem de Julian Day do arquivo ReadRinexNav.m linha 83!
        return (  (D * 24 + HOR) * 3600 + MIN * 60 + SEG );
    }

    public static int contEfemerides(Context context) throws IOException{
        int numLines = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.hour3470)));

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        /*PULANDO AS LINHAS DO CABEÇALHO*/
        String mLine = reader.readLine();
               mLine = reader.readLine();
               mLine = reader.readLine();
               mLine = reader.readLine();
               mLine = reader.readLine();
               mLine = reader.readLine();
               mLine = reader.readLine();
               mLine = reader.readLine();

        while (mLine != null) {
            numLines++;
            mLine = reader.readLine();
        }
        reader.close();

        return numLines / 8;
    }

}
