package Codigos;

import android.content.Context;
import android.util.Log;

import com.rogeriocarmo.gnss_mobilecalculator.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static Codigos.GNSSConstants.C_TO_N0_THRESHOLD_DB_HZ;
import static Codigos.GNSSConstants.TOW_DECODED_MEASUREMENT_STATE_BIT;
import static Codigos.GNSSConstants.WEEKSEC;

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
        int qntMedicoesDescartadas = 0;
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

                if (!linhaRaw[28].equalsIgnoreCase(String.valueOf(GNSSConstants.CONSTELLATION_GPS))){
                    Log.e("Constellation", "Non-GPS Measurement: Type " + linhaRaw[28]);
                    qntMedicoesDescartadas++;
                    continue; // TODO Por enquanto só são tratadas medições GPS
                }

                GNSSMeasurement novaMedicao = new GNSSMeasurement();

                //TODO USAR UMA TABELA HASH NO LUGAR DE UM ARRAY SIMPLES!

                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[1]));
                novaMedicao.setTimeNanos(Long.parseLong(linhaRaw[2]));

                try{
                    novaMedicao.setLeapSecond(Integer.parseInt(linhaRaw[3]));
                }catch (NumberFormatException ex){
                    Log.e("Err","LeapSecond: " + ex.getMessage());
                }

                try{
                    novaMedicao.setTimeUncertaintyNanos(Double.parseDouble(linhaRaw[4]));
                }catch (NumberFormatException ex){
                    Log.e("Err","TimeUncertaintyNanos: " + ex.getMessage());
                }

                novaMedicao.setFullBiasNanos(Long.parseLong(linhaRaw[5]));

                novaMedicao.setBiasNanos(Double.parseDouble(linhaRaw[6]));
                novaMedicao.setBiasUncertaintyNanos(Double.parseDouble(linhaRaw[7]));

                try{
                    novaMedicao.setDriftNanosPerSecond(Double.parseDouble(linhaRaw[8]));
                }catch (NumberFormatException ex){
                    Log.e("Err","DriftNanosPerSecond: " + ex.getMessage());
                }

                try{
                    novaMedicao.setDriftUncertaintyNanosPerSecond(Double.parseDouble(linhaRaw[9]));
                }catch (NumberFormatException ex){
                    Log.e("Err","DriftUncertaintyNanosPerSecond: " + ex.getMessage());
                }

                try{
                    novaMedicao.setHardwareClockDiscontinuityCount(Integer.parseInt(linhaRaw[10]));
                }catch (NumberFormatException ex){
                    Log.e("Err","HardwareClockDiscontinuityCount: " + ex.getMessage());
                }

                novaMedicao.setSvid(Integer.parseInt(linhaRaw[11]));
                novaMedicao.setTimeOffsetNanos(Double.parseDouble(linhaRaw[12]));

                //FIXME REVER ESSA QUESTÃO DO STATE!
                novaMedicao.setState(Integer.parseInt(linhaRaw[13]));
//                Log.i("State","Resultado verificado: " + Integer.parseInt(linhaRaw[13]));

                int verificacaoStatus = (int)(Integer.parseInt(linhaRaw[13]) & (1L << TOW_DECODED_MEASUREMENT_STATE_BIT));


                if (verificacaoStatus == 0){
                    Log.e("StateEr","TOW not decoded!: " + verificacaoStatus);
                    qntMedicoesDescartadas++;
                    continue;
                }else{
                    Log.i("StateOk","TOW certo: " + verificacaoStatus);
                }

//                Log.i("State", "Verificação: " + (Integer.parseInt(linhaRaw[13]) & (1L << TOW_DECODED_MEASUREMENT_STATE_BIT)));

                novaMedicao.setReceivedSvTimeNanos(Long.parseLong(linhaRaw[14]));
                novaMedicao.setReceivedSvTimeUncertaintyNanos(Double.parseDouble(linhaRaw[15]));

                novaMedicao.setCn0DbHz(Double.parseDouble(linhaRaw[16]));

                if (novaMedicao.getCn0DbHz() <= C_TO_N0_THRESHOLD_DB_HZ ){
                    Log.e("Cn0DbHzEr","Valor: " + String.valueOf(novaMedicao.getCn0DbHz()));
                    qntMedicoesDescartadas++;
                    continue;
                }else{
                    Log.i("Cn0DbHzOk","Valor: " + String.valueOf(novaMedicao.getCn0DbHz()));
                }

                novaMedicao.setPseudorangeRateMetersPerSecond(Double.parseDouble(linhaRaw[17]));
                novaMedicao.setPseudorangeRateUncertaintyMetersPerSecond(Double.parseDouble(linhaRaw[18]));
                novaMedicao.setAccumulatedDeltaRangeState(Integer.parseInt(linhaRaw[19]));
                novaMedicao.setAccumulatedDeltaRangeMeters(Double.parseDouble(linhaRaw[20]));
                novaMedicao.setAccumulatedDeltaRangeUncertaintyMeters(Double.parseDouble(linhaRaw[21]));

                try{
                    novaMedicao.setCarrierFrequencyHz(Double.parseDouble(linhaRaw[22]));
                    novaMedicao.setCarrierCycles(Integer.parseInt(linhaRaw[23]));
                    novaMedicao.setCarrierPhase(Integer.parseInt(linhaRaw[24]));
                    novaMedicao.setCarrierPhaseUncertainty(Double.parseDouble(linhaRaw[25]));
                } catch (NumberFormatException err){
                    Log.e("err","CarrierPhase errors...");
                }

                novaMedicao.setMultipathIndicator(Integer.parseInt(linhaRaw[26]));

                try{
                    novaMedicao.setSnrInDb(Double.parseDouble(linhaRaw[27]));
                } catch (NumberFormatException err){
                    Log.e("err","SNR: " + err.getMessage());
                }

                novaMedicao.setConstellationType(Integer.parseInt(linhaRaw[28]));
//                novaMedicao.setAgcDb(Double.parseDouble(linhaRaw[29]));
//                novaMedicao.setCarrierFrequencyHz(Double.parseDouble(linhaRaw[30]));

                listaMedicoes.add(novaMedicao);
            }
        }

        Log.i("QntDescartadas","Quantidade de medidas descartadas: " + qntMedicoesDescartadas);
        Log.i("QntPreservadas","Quantidade de medidas preservadas: " + listaMedicoes.size());

        reader.close();
        return sb.toString();
    }

    public static void calcPseudoranges_OLD(){
        for (int i = 0; i < listaMedicoes.size(); i++){

            double pseudorangeMeters = 0d;
            double pseudorangeUncertaintyMeters = 0d;

            // GPS Week number:
            Long weekNumber =  Math.round(Math.floor(-(double)listaMedicoes.get(i).getFullBiasNanos() * 1e-9/GNSSConstants.WEEKSEC));
            //TODO VERIFICAR E ATRIBUIT 0 CASO DE ERRO
//            listaMedicoes.get(i).getBiasNanos();
//            listaMedicoes.get(i).getTimeOffsetNanos();

            //compute time of measurement relative to start of week
            Long WEEKNANOS = Math.round (GNSSConstants.WEEKSEC * 1e9);
            Long weekNumberNanos = weekNumber * Math.round(GNSSConstants.WEEKSEC*1e9);

            //Compute tRxNanos using gnssRaw.FullBiasNanos(1), so that
            //tRxNanos includes rx clock drift since the first epoch:
            Long tRxNanos = listaMedicoes.get(i).getTimeNanos() - listaMedicoes.get(i).getFullBiasNanos() - weekNumberNanos;

            //TODO !!AQUI ENTRA A LOGICA DO CAMPO STATE...
            //tRxNanos now since beginning of the week, unless we had a week rollover

            //subtract the fractional offsets TimeOffsetNanos and BiasNanos:
            double tRxSeconds = (double)(Math.round(tRxNanos) - listaMedicoes.get(i).getTimeOffsetNanos() - listaMedicoes.get(i).getBiasNanos()*1e-9);
            double tTxSeconds = (double)listaMedicoes.get(i).getReceivedSvTimeNanos() * 1e-9;

            double prSeconds = tRxSeconds - tTxSeconds;

            // we are ready to compute pseudorange in meters:
            pseudorangeMeters = prSeconds * GNSSConstants.LIGHTSPEED;
            pseudorangeUncertaintyMeters = (double) listaMedicoes.get(i).getReceivedSvTimeUncertaintyNanos() * 1e-9 * GNSSConstants.LIGHTSPEED;

            listaMedicoes.get(i).setPseudorangeMeters(pseudorangeMeters);
            listaMedicoes.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);

            Log.i("prr", "Svid: " +  listaMedicoes.get(i).getSvid() + " Pseudorange: " + listaMedicoes.get(i).getPseudorangeMeters() + " m");
            Log.i("Uncertainty", "Svid: " +  listaMedicoes.get(i).getSvid() + " Uncertainty: " + listaMedicoes.get(i).getPseudoRangeUncertaintyMeters() + " m");
        }
    }

    public static void calcPseudoranges(){
        for (int i = 0; i < listaMedicoes.size(); i++){

            double pseudorangeMeters = 0d;
            double pseudorangeUncertaintyMeters = 0d;

            // Generate the measured time in full GNSS time
            Long tRx_GNSS = listaMedicoes.get(i).getTimeNanos() - (listaMedicoes.get(0).getFullBiasNanos() + Math.round(listaMedicoes.get(0).getBiasNanos())); // FIXME VER SE É LONG
           // Change the valid range from full GNSS to TOW
            Long tRx = tRx_GNSS % Math.round(WEEKSEC*1e9);
            // Generate the satellite time
            Long tTx = listaMedicoes.get(i).getReceivedSvTimeNanos() + Math.round(listaMedicoes.get(i).getTimeOffsetNanos());
            // Generate the pseudorange
            Long prMilliSeconds = (tRx - tTx);
            pseudorangeMeters = prMilliSeconds * GNSSConstants.LIGHTSPEED * 1e-9;
            pseudorangeUncertaintyMeters = (double) listaMedicoes.get(i).getReceivedSvTimeUncertaintyNanos() * 1e-9 * GNSSConstants.LIGHTSPEED;

            listaMedicoes.get(i).setPseudorangeMeters(pseudorangeMeters);
            listaMedicoes.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);

            listaMedicoes.get(i).settTx(tTx);
            listaMedicoes.get(i).settRx(tRx);

            Log.i("tTx/tRx","Svid: " +  listaMedicoes.get(i).getSvid() + " tTx: " + tTx + " tRx: " + tRx + " Intervalo: " + prMilliSeconds);

            Log.i("prr", "Svid: " +  listaMedicoes.get(i).getSvid() + " Pseudorange: " + listaMedicoes.get(i).getPseudorangeMeters() + " m");
            Log.i("Uncertainty", "Svid: " +  listaMedicoes.get(i).getSvid() + " Uncertainty: " + listaMedicoes.get(i).getPseudoRangeUncertaintyMeters() + " m");
        }
    }


}
