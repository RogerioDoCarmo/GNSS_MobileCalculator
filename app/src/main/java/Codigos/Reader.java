package Codigos;

import android.content.Context;
import android.util.Log;

import com.rogeriocarmo.gnss_mobilecalculator.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Reader {

    public static ArrayList<GNSSNavMsg> listaNavMsgs = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoes = new ArrayList<>();


    public Reader(){ //TODO Por enquanto pegar da pasta raw assets msm!
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

        String sub = "";

        for (int i = 1; i < numEfemerides; i++){
            GNSSNavMsg efemeride = new GNSSNavMsg();
            mLine = reader.readLine();
            String PRN;
            try{
                PRN = mLine.substring(0,2);
            }catch (Exception er){
                PRN = mLine.substring(1,2);
            }

            Log.i("PRN", PRN); // FIXME
            efemeride.setPRN(PRN);

            try {
                double Toc = calcTOC_tr_(Integer.valueOf(mLine.substring(9, 11).replaceAll("\\s", "")), // dia
                        Integer.valueOf(mLine.substring(12, 14).replaceAll("\\s", "")), // hora
                        Integer.valueOf(mLine.substring(15, 17).replaceAll("\\s", "")), // minuto
                        Double.valueOf(mLine.substring(18, 22).replace('D', 'e').trim())); // segundo FIXME

                Log.i("TOC", "Dia: " + mLine.substring(9, 11).replaceAll("\\s", "") +
                        " Hora: " + mLine.substring(12, 14).replaceAll("\\s", "") +
                        " Minutos: " + mLine.substring(15, 17).replaceAll("\\s", "") +
                        " Segundos: " + mLine.substring(18, 22).replaceAll("\\s", ""));

                efemeride.setToc(Toc);
            }catch (Exception err){
                efemeride.setToc(0);
                Log.e("TOC-ERR","Erro: " + err.getMessage());
            }

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

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setI0(Double.parseDouble(sub.trim()));

            Log.i("I0",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCrc(Double.parseDouble(sub.trim()));

            Log.i("Crc",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setOmega(Double.parseDouble(sub.trim()));

            Log.i("Omega",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setOMEGA_DOT(Double.parseDouble(sub.trim()));

            Log.i("Omega_Dot",sub);
//--------------------------------------------------------------------------------------------------

            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setIDOT(Double.parseDouble(sub.trim()));

            Log.i("IDOT",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            double L2Code = Double.parseDouble(sub.trim());
            efemeride.setCodeL2(L2Code);

            Log.i("CodeL2",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            double week = Double.parseDouble(sub.trim());
            efemeride.setGPS_Week((int) week);

            Log.i("GPS_WEEK",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            double L2Flag = Double.parseDouble(sub.trim());
            efemeride.setL2PdataFlag((int) L2Flag);

            Log.i("L2_Flag",sub);

//--------------------------------------------------------------------------------------------------

            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double svAccur = Double.parseDouble(sub.trim());
            efemeride.setAccuracy((int) svAccur);

            Log.i("Sv_Accur",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            double svHealth = Double.parseDouble(sub.trim());
            efemeride.setHealth((int) svHealth);

            Log.i("Sv_Health",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setTGD(Double.parseDouble(sub.trim()));

            Log.i("Tgd",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            double iodc = Double.parseDouble(sub.trim());
            efemeride.setIODC((int) iodc);

            Log.i("IODC",sub);
//--------------------------------------------------------------------------------------------------
            mLine = reader.readLine();

            int len = mLine.length();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setTtx(Double.parseDouble(sub.trim()));

            Log.i("Transmission Time (TTX)",sub);

            if (len > 22) {
                sub = mLine.substring(22, 41).replace('D', 'e');
                efemeride.setFit_interval(Double.parseDouble(sub.trim()));

            } else {
                efemeride.setFit_interval(0);
            }

            Log.i("Fit Interval",sub);

//--------------------------------------------------------------------------------------------------
            listaNavMsgs.add(efemeride);
            Log.i("FIM-MENSAGEM","===========================================");
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

    public static String readLogger_RawAssets(Context context) throws  IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.sensorlog))); // FIXME DEIXAR DINAMICO

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();

        //PULANDO O CABEÇALHO
        String mLine = reader.readLine();
        while ((mLine = reader.readLine()).startsWith("#")){
            mLine = reader.readLine();
        }

    //TODO Tratar o caso de ter ou não o campo AgcDb

        while(mLine != null){
            mLine = reader.readLine();

            if (mLine == null || mLine.isEmpty()) continue;

            if (mLine.startsWith("Raw")){
                /*Lendo uma linha raw*/
                Log.i("raw",mLine);

                String[] linhaRaw = mLine.split(",");

                GNSSMeasurement novaMedicao = new GNSSMeasurement();

                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[1]));

//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[2]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[3]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[4]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[5]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[6]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[7]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[8]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[9]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[10]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[11]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[12]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[13]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[14]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[15]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[16]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[17]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[18]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[19]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[20]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[21]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[22]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[23]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[24]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[25]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[26]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[27]));
//                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[28]));




                listaMedicoes.add(novaMedicao);
            }
        }

        reader.close();
        return sb.toString();
    }

}
