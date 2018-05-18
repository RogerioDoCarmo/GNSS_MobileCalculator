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

        for (int i = 1; i < numEfemerides; i++){
            GNSSNavMsg efemeride = new GNSSNavMsg();
            String PRN;
            try{
                PRN = mLine.substring(0,2);
            }catch (Exception er){
                PRN = mLine.substring(1,2);
            }

            Log.i("PRNN", PRN); // FIXME
            //efemeride.setPRN(Integer.valueOf(PRN));
            listaNavMsgs.add(efemeride);

            mLine = reader.readLine();
            mLine = reader.readLine();
            mLine = reader.readLine();
            mLine = reader.readLine();
            mLine = reader.readLine();
            mLine = reader.readLine();
            mLine = reader.readLine();
            mLine = reader.readLine();
        }

        reader.close();
        return sb.toString();
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
