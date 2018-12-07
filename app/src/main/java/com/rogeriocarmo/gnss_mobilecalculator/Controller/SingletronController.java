package com.rogeriocarmo.gnss_mobilecalculator.Controller;

import android.content.Context;
import android.util.Log;

import com.rogeriocarmo.gnss_mobilecalculator.R;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.rogeriocarmo.gnss_mobilecalculator.Model.CoordenadaGPS;
import com.rogeriocarmo.gnss_mobilecalculator.Model.CoordenadaGeodesica;
import com.rogeriocarmo.gnss_mobilecalculator.Model.Ecef2LlaConverter;
import com.rogeriocarmo.gnss_mobilecalculator.Model.EpocaGPS;
import com.rogeriocarmo.gnss_mobilecalculator.Model.EpocaObs;
import com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSConstants;
import com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSDate;
import com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSMeasurement;
import com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSNavMsg;
import com.rogeriocarmo.gnss_mobilecalculator.Model.GpsTime;
import com.rogeriocarmo.gnss_mobilecalculator.Model.ResultEpch;

import static com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSConstants.C_TO_N0_THRESHOLD_DB_HZ;
import static com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSConstants.GM;
import static com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSConstants.LIGHTSPEED;
import static com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSConstants.MAX_ITERACOES;
import static com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSConstants.TOW_DECODED_MEASUREMENT_STATE_BIT;
import static com.rogeriocarmo.gnss_mobilecalculator.Model.GNSSConstants.We;

public class SingletronController {
    private static SingletronController INSTANCE = null;

    public static ArrayList<GNSSNavMsg> listaEfemeridesOriginal = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoesOriginal = new ArrayList<>();

    public static ArrayList<GNSSNavMsg> listaEfemeridesAtual = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoesAtual = new ArrayList<>();
    public static ArrayList<CoordenadaGPS> listaCoordAtual = new ArrayList<>();
    public static ArrayList<Integer> listaPRNsAtual = new ArrayList<>();
    public static ArrayList<EpocaGPS> listaEpocas = new ArrayList<>();

    public static ArrayList<ResultEpch> listaResultados = new ArrayList<>();

    public static EpocaGPS epocaAtual;
    public static TextWritter writter; //FIXME REVER

    private static int qntSatEpchAtual;

    private SingletronController() {
        listaEfemeridesOriginal = new ArrayList<>();
        listaMedicoesOriginal = new ArrayList<>();
        listaEfemeridesAtual = new ArrayList<>();
        listaMedicoesAtual = new ArrayList<>();
        listaCoordAtual = new ArrayList<>();
        listaPRNsAtual = new ArrayList<>();
        listaEpocas = new ArrayList<>();
        listaResultados = new ArrayList<>();
    }

    public static SingletronController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SingletronController();
        }
        return(INSTANCE);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }

    public void processamento_completo(Context mContext){
        try {
            readLogger_RawAssets(mContext);
            calcPseudorange();
            readRINEX_RawAssets(mContext);
            processar_todas_epocas();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String readLogger_RawAssets(Context context) throws  IOException{
        int qntMedicoesDescartadas = 0;
//        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.logdia05hora15))); // FIXME DEIXAR DINAMICO


        // TODO PPTE
        //TODO PPTE
//        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.log15901))); // FIXME DEIXAR DINAMICO

        //TODO EP01
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.log31do10bbbbbbbb))); // FIXME DEIXAR DINAMICO
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
                String[] linhaRaw = mLine.split(",");

                if (!linhaRaw[28].equalsIgnoreCase(String.valueOf(GNSSConstants.CONSTELLATION_GPS))){
//                    Log.e("Constellation", "Non-GPS Measurement: Type " + linhaRaw[28]);
                    qntMedicoesDescartadas++;
                    continue;
                }

                GNSSMeasurement novaMedicao = new GNSSMeasurement();

                novaMedicao.setElapsedRealtimeMillis(Integer.parseInt(linhaRaw[1]));
                novaMedicao.setTimeNanos(Long.parseLong(linhaRaw[2]));

                try{
                    novaMedicao.setLeapSecond(Integer.parseInt(linhaRaw[3]));
                }catch (NumberFormatException ex){
//                    Log.e("Err","LeapSecond: " + ex.getMessage());
                }

                try{
                    novaMedicao.setTimeUncertaintyNanos(Double.parseDouble(linhaRaw[4]));
                }catch (NumberFormatException ex){
//                    Log.e("Err","TimeUncertaintyNanos: " + ex.getMessage());
                }

                novaMedicao.setFullBiasNanos(Long.parseLong(linhaRaw[5]));

                if (novaMedicao.getFullBiasNanos() > 0){
                    Log.e("Raw","FullBiasNanos: Should de negative");
                    novaMedicao.setFullBiasNanos(novaMedicao.getFullBiasNanos() * -1);
                }

                try{
                    novaMedicao.setBiasNanos(Double.parseDouble(linhaRaw[6]));
                }catch (Exception e){
                    novaMedicao.setBiasNanos(0);
                }

                try{
                    novaMedicao.setBiasUncertaintyNanos(Double.parseDouble(linhaRaw[7]));
                }catch (Exception e){
                    novaMedicao.setBiasUncertaintyNanos(0);
                }

                try{
                    novaMedicao.setDriftNanosPerSecond(Double.parseDouble(linhaRaw[8]));
                }catch (NumberFormatException ex){
//                    Log.e("Err","DriftNanosPerSecond: " + ex.getMessage());
                }

                try{
                    novaMedicao.setDriftUncertaintyNanosPerSecond(Double.parseDouble(linhaRaw[9]));
                }catch (NumberFormatException ex){
//                    Log.e("Err","DriftUncertaintyNanosPerSecond: " + ex.getMessage());
                }

                try{
                    novaMedicao.setHardwareClockDiscontinuityCount(Integer.parseInt(linhaRaw[10]));
                }catch (NumberFormatException ex){
//                    Log.e("Err","HardwareClockDiscontinuityCount: " + ex.getMessage());
                }

                novaMedicao.setSvid(Integer.parseInt(linhaRaw[11]));
                novaMedicao.setTimeOffsetNanos(Double.parseDouble(linhaRaw[12]));


                novaMedicao.setState(Integer.parseInt(linhaRaw[13]));
                novaMedicao.setReceivedSvTimeNanos(Long.parseLong(linhaRaw[14]));
                novaMedicao.setReceivedSvTimeUncertaintyNanos(Double.parseDouble(linhaRaw[15]));

                if (novaMedicao.getReceivedSvTimeUncertaintyNanos() > 500){
                    qntMedicoesDescartadas++;
//                    Log.e("Raw","TimeUncertainty");
                    continue;
                }

                novaMedicao.setCn0DbHz(Double.parseDouble(linhaRaw[16]));

                if (!(novaMedicao.getCn0DbHz() >= C_TO_N0_THRESHOLD_DB_HZ)
                        || (novaMedicao.getState() & (1L << TOW_DECODED_MEASUREMENT_STATE_BIT)) == 0) {
                    qntMedicoesDescartadas++;
                    //                    Log.e("Carrier/State","Erro");
                    continue;
                }

                novaMedicao.setPseudorangeRateMetersPerSecond(Double.parseDouble(linhaRaw[17]));
                novaMedicao.setPseudorangeRateUncertaintyMetersPerSecond(Double.parseDouble(linhaRaw[18]));

                if (novaMedicao.getPseudoRangeUncertaintyMeters() > 10){ // FIXME
                    qntMedicoesDescartadas++;
//                    Log.e("Raw","PseudoRangeUncertainty");
                    continue;
                }

                novaMedicao.setAccumulatedDeltaRangeState(Integer.parseInt(linhaRaw[19]));
                novaMedicao.setAccumulatedDeltaRangeMeters(Double.parseDouble(linhaRaw[20]));
                novaMedicao.setAccumulatedDeltaRangeUncertaintyMeters(Double.parseDouble(linhaRaw[21]));

                try{
                    novaMedicao.setCarrierFrequencyHz(Double.parseDouble(linhaRaw[22]));
                    novaMedicao.setCarrierCycles(Integer.parseInt(linhaRaw[23]));
                    novaMedicao.setCarrierPhase(Integer.parseInt(linhaRaw[24]));
                    novaMedicao.setCarrierPhaseUncertainty(Double.parseDouble(linhaRaw[25]));
                } catch (NumberFormatException err){
//                    Log.e("err","CarrierPhase errors...");
                }

                novaMedicao.setMultipathIndicator(Integer.parseInt(linhaRaw[26]));

                if (novaMedicao.getMultipathIndicator() == 1){
                    qntMedicoesDescartadas++;
//                    Log.e("Raw","MultipathIndicator");
                    continue;
                }

                try{
                    novaMedicao.setSnrInDb(Double.parseDouble(linhaRaw[27]));
                } catch (NumberFormatException err){
//                    Log.e("err","SNR: " + err.getMessage());
                }

                novaMedicao.setConstellationType(Integer.parseInt(linhaRaw[28]));
//                novaMedicao.setAgcDb(Double.parseDouble(linhaRaw[29]));
//                novaMedicao.setCarrierFrequencyHz(Double.parseDouble(linhaRaw[30]));

                // FIXME %compute full cycle time of measurement, in milliseonds
                Long allRxMillis = Math.round((novaMedicao.getTimeNanos() - novaMedicao.getFullBiasNanos()) * 1e-6);
                // FIXME %%llRxMillis is now accurate to one millisecond (because it's an integer)

                novaMedicao.setAllRxMillis(allRxMillis);

                listaMedicoesOriginal.add(novaMedicao);
            }
        }

//        Log.i("QntDescartadas","Quantidade de medidas descartadas: " + qntMedicoesDescartadas);
//        Log.i("QntPreservadas","Quantidade de medidas preservadas: " + listaMedicoesOriginal.size());

        reader.close();
        return sb.toString();
    }

    public String readRINEX_RawAssets(Context context) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.brdc31do10)));

        StringBuilder sb = new StringBuilder();

        //PULANDO O CABEÇALHO DE 8 LINHAS
        String mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();

        String sub = "";
        int numEfemerides = contEfemerides(context) - 2; // FIXME FAZER UM WHILE

        for (int i = 0; i < numEfemerides; i++){
            GNSSNavMsg efemeride = new GNSSNavMsg();
            mLine = reader.readLine();

//first line - epoch of satellite clock (toc)
//==================================================================================================
            sub = mLine.substring(0, 2).replaceAll("\\s", "");
            efemeride.setPRN(Integer.valueOf(sub));  // FIXME

            try { // FIXME REVER
                int year = Integer.valueOf(mLine.substring(3, 6).replaceAll("\\s", ""));
                int month = Integer.valueOf(mLine.substring(6, 8).replaceAll("\\s", ""));
                int day = Integer.valueOf(mLine.substring(9, 11).replaceAll("\\s", ""));
                int hour = Integer.valueOf(mLine.substring(12, 14).replaceAll("\\s", ""));
                int minute = Integer.valueOf(mLine.substring(15, 17).replaceAll("\\s", ""));
                double seconds = Double.valueOf(mLine.substring(18, 22).replaceAll("\\s", ""));

                GNSSDate data = new GNSSDate(year, month, day, hour, minute, seconds);
                efemeride.setGNSSDate(data);

            }catch (Exception err){
                efemeride.setToc(0);
                Log.e("TOC-ERR","Erro: " + err.getMessage());
            }

            double af0 = Double.valueOf(mLine.substring(22,41).replace('D','e')
                    .replaceAll("\\s",""));

            double af1 = Double.valueOf(mLine.substring(41,60).replace('D','e')
                    .replaceAll("\\s",""));

            double af2 = Double.valueOf(mLine.substring(60,79).replace('D','e')
                    .replaceAll("\\s",""));

            efemeride.setAf0(af0);
            efemeride.setAf1(af1);
            efemeride.setAf2(af2);
//second line - broadcast orbit
//==================================================================================================
            mLine = reader.readLine();

            sub = mLine.substring(3, 22).replace('D', 'e');
            double iode = Double.parseDouble(sub.trim());
            efemeride.setIODE(iode);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCrs(Double.parseDouble(sub.trim()));

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setDelta_n(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setM0(Double.parseDouble(sub.trim()));
//third line - broadcast orbit (2)
//==================================================================================================

            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double Cuc = Double.parseDouble(sub.trim());
            efemeride.setCuc(Cuc);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setE(Double.parseDouble(sub.trim()));

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setCus(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setAsqrt(Double.parseDouble(sub.trim()));
//fourth line
//==================================================================================================
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double toe = Double.parseDouble(sub.trim());
            efemeride.setToe(toe);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCic(Double.parseDouble(sub.trim()));

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setOmega_0(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setCis(Double.parseDouble(sub.trim()));
//fifth line
//==================================================================================================
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setI0(Double.parseDouble(sub.trim()));

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCrc(Double.parseDouble(sub.trim()));

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setW(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setOmega_v(Double.parseDouble(sub.trim()));
//sixth line
//==================================================================================================
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setIDOT(Double.parseDouble(sub.trim()));

            sub = mLine.substring(22, 41).replace('D', 'e');
            double L2Code = Double.parseDouble(sub.trim());
            efemeride.setCodeL2(L2Code);

            sub = mLine.substring(41, 60).replace('D', 'e');
            double week = Double.parseDouble(sub.trim());
            efemeride.setGPS_Week((int) week);

            sub = mLine.substring(60, 79).replace('D', 'e');
            double L2Flag = Double.parseDouble(sub.trim());
            efemeride.setL2PdataFlag((int) L2Flag);
//seventh line
//==================================================================================================

            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double svAccur = Double.parseDouble(sub.trim());
            efemeride.setAccuracy((int) svAccur);

            sub = mLine.substring(22, 41).replace('D', 'e');
            double svHealth = Double.parseDouble(sub.trim());
            efemeride.setHealth((int) svHealth);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setTGD(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            double iodc = Double.parseDouble(sub.trim());
            efemeride.setIODC((int) iodc);
//eigth line
//==================================================================================================
            mLine = reader.readLine();

            int len = mLine.length();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setTtx(Double.parseDouble(sub.trim()));

            if (len > 22) {
                sub = mLine.substring(22, 41).replace('D', 'e');
                efemeride.setFit_interval(Double.parseDouble(sub.trim()));

            } else {
                efemeride.setFit_interval(0);
            }

            listaEfemeridesOriginal.add(efemeride);
        }

        reader.close();
        return sb.toString();
    }

    /**
     * Converte uma data (UTC) em segundos da semana GPS correspondente.
     * <p>Implementação conforme (MONICO, 2008)</p>
     * @param DAY_WEEK Dia da semana, 0 = Domingo...
     * @param HOR_DAY Horário do dia (UTC)
     * @param MIN Minutos do dia (UTC)
     * @param SEG Segundos do dia (UTC)
     * @return Segundos da semana GPS correspondente
     */
    public double calc_Tr(int DAY_WEEK, int HOR_DAY, int MIN, double SEG) {
        return (  (DAY_WEEK * 24 + HOR_DAY) * 3600 + MIN * 60 + SEG );
    }

    /**
     * Converte uma data (UTC) em segundos da semana GPS correspondente.
     * <p>Implementação conforme (MONICO, 2008)</p>
     * @param dataGNSS Data no formato UTC.
     * @return Segundos da semana GPS correspondente
     * @see GNSSDate
     */
    public double calc_Tr(GNSSDate dataGNSS) {
        return (  (dataGNSS.getDay_week() * 24 + dataGNSS.getHour()) * 3600 + dataGNSS.getMin() * 60 + dataGNSS.getSec() );
    }

    /**
     * Lê um arquivo <b>RINEX de Navegação</b> e retorna a quantidade de efemérides no arquivo.
     * @param context Contexto
     * @return O número de efemérides brutas no arquivo RINEX de observação
     * @throws IOException
     */
    private int contEfemerides(Context context) throws IOException{
        int numLines = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.brdc31do10)));

        StringBuilder sb = new StringBuilder();
        /*PULANDO O CABEÇALHO DE 8 LINHAS*/
        String mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();

        while (mLine != null && !mLine.equals("")) {
            numLines++;
            mLine = reader.readLine();
        }
        reader.close();

        return numLines / 8;
    }

    public void calcPseudorange(){
        ArrayList<Long> listaAllRxNanos = new ArrayList<>();

        int INDEX_BIAS = 0;

        for (int i = 0; i < listaMedicoesOriginal.size(); i++){
            Long allRxNanosAtual = (listaMedicoesOriginal.get(i).getTimeNanos() -
                    listaMedicoesOriginal.get(i).getFullBiasNanos());
            listaMedicoesOriginal.get(i).setAllRxNanos(allRxNanosAtual); // FIXME allRxNanos!
            if (!listaAllRxNanos.contains(allRxNanosAtual)) { // Inicio de uma nova época
                listaAllRxNanos.add(allRxNanosAtual);

                INDEX_BIAS = i;
            }

            /*Cálculo da pseudodistância*/
            Long weekNumber =  Math.round(Math.floor(-listaMedicoesOriginal.get(i).getFullBiasNanos() * 1e-9 / GNSSConstants.WEEKSEC));
            Long weekNumberNanos = Math.round(weekNumber) * Math.round(GNSSConstants.WEEKSEC*1e9);

            Long tRxNanos = listaMedicoesOriginal.get(i).getTimeNanos() -
                    listaMedicoesOriginal.get(INDEX_BIAS).getFullBiasNanos() - weekNumberNanos;

            if (tRxNanos < 0){
                Log.e("tRx","tRxNanos should be positive!");
            }

            Double tRxSeconds  = ( (double)(tRxNanos) - listaMedicoesOriginal.get(i).getTimeOffsetNanos() -
                    listaMedicoesOriginal.get(i).getBiasNanos()) * 1e-9;
            Double tTxSeconds  = (double)(listaMedicoesOriginal.get(i).getReceivedSvTimeNanos())*1e-9;

            Double prSeconds = tRxSeconds - tTxSeconds;

            // Checking for week Rollover
            boolean iRollover = (prSeconds > GNSSConstants.WEEKSEC/2);

            if (iRollover) {
                double prS = prSeconds;
                double delS = Math.round(prS/GNSSConstants.WEEKSEC)*GNSSConstants.WEEKSEC;
                prS = prS - delS;

                int maxBiasSeconds = 10;

                if (prS > maxBiasSeconds) {
                    Log.e("Week Rollover", "Failed to correct week rollover\n");
                }
                else{
                    prSeconds = prS;
                    tRxSeconds = tRxSeconds - delS;
                    Log.w("Week Rollover", "Corrected week rollover\n");
                }
            }

            double pseudorange = prSeconds * LIGHTSPEED;
            double pseudorangeUncertaintyMeters = (double)(listaMedicoesOriginal.get(i).getReceivedSvTimeUncertaintyNanos())
                    *1e-9 * LIGHTSPEED;
            /*Cálculo da pseudodistância*/

            listaMedicoesOriginal.get(i).setPseudorangeMeters(pseudorange);
            listaMedicoesOriginal.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);
            listaMedicoesOriginal.get(i).setGpsWeek(weekNumber.intValue());
            listaMedicoesOriginal.get(i).settTxSeconds(tTxSeconds);
            listaMedicoesOriginal.get(i).settRxSeconds(tRxSeconds);
        }

        for (int i = 0; i < listaAllRxNanos.size(); i++) {
            Long AllRxNanosAtual = listaAllRxNanos.get(i);
            EpocaGPS novaEpoca = new EpocaGPS(AllRxNanosAtual);

            for (int j = 0; j < listaMedicoesOriginal.size(); j++) {
                // A medição pertence à época
                if (listaMedicoesOriginal.get(j).getAllRxNanos().equals(AllRxNanosAtual)){ // TODOO CONDIÇÃO DE MESMA EPOCA
                    if (novaEpoca.getNumMedicoes() == 0){ // Primeira medição da época
                        Long mArrivalTimeSinceGpsEpochNs = listaMedicoesOriginal.get(j).getAllRxNanos();

                        GpsTime gpsTime = new GpsTime(mArrivalTimeSinceGpsEpochNs);

                        long gpsWeekEpochNs = GpsTime.getGpsWeekEpochNano(gpsTime);
                        double mArrivalTimeSinceGPSWeekNs = mArrivalTimeSinceGpsEpochNs - gpsWeekEpochNs;
                        int mGpsWeekNumber = gpsTime.getGpsWeekSecond().first;

                        int year = gpsTime.getGpsDateTime().getYear() % 2000;
                        int month = gpsTime.getGpsDateTime().getMonthOfYear();
                        int day_month = gpsTime.getGpsDateTime().getDayOfMonth();
                        int day_week = gpsTime.getGpsDateTime().getDayOfWeek();
                        int hour = gpsTime.getGpsDateTime().getHourOfDay();
                        int minute = gpsTime.getGpsDateTime().getMinuteOfHour();
                        double seconds = gpsTime.getGpsDateTime().getSecondOfMinute();
                        double milliseconds = gpsTime.getGpsDateTime().getMillisOfSecond() * 1e-3;
                        double secFinal = seconds + milliseconds;

                        GNSSDate dataAtual = new GNSSDate(year, month, day_month, hour, minute, secFinal);
                        dataAtual.setDay_week(day_week);
                        novaEpoca.setData(dataAtual);
                        novaEpoca.setId(listaEpocas.size() + 1);
                    }

                    if (novaEpoca.addSatelitePRN(listaMedicoesOriginal.get(j).getSvid())){
                        novaEpoca.addMedicao(listaMedicoesOriginal.get(j));
                    }
                }
            }

            if (novaEpoca.getNumSatelites() >= 5){
                listaEpocas.add(novaEpoca);
            }
        }
    }

    /**
     * Calcula as <b>coordenadas X,Y,Z (WGS-84)</b> para cada satélite.
     * <p>Calcula o <b>erro do relógio</b> para cada satélite em segundos.
     */
    private void calcCoordenadas(){
        GNSSDate dataObservacao = epocaAtual.getDateUTC();

        for (int i = 0; i < qntSatEpchAtual; i++ ){// FIXME
            //------------------------------------------
            //Dados de entrada
            //------------------------------------------
            //Tempo de recepcao do sinal ->  Hora da observacao
//            double tr = (3*24+0)*3600 + 0*60 + 0.00; // FIXME CORRIGIR O TEMPO
//            double tr = calc_Tr(GNSSConstants.DAY_QUA,)
            double a0 = listaEfemeridesAtual.get(i).getAf0();
            double a1 = listaEfemeridesAtual.get(i).getAf1();
            double a2 = listaEfemeridesAtual.get(i).getAf2();

            double Crs = listaEfemeridesAtual.get(i).getCrs();
            double delta_n = listaEfemeridesAtual.get(i).getDelta_n();
            double m0 = listaEfemeridesAtual.get(i).getM0();

            double Cuc = listaEfemeridesAtual.get(i).getCuc();
            double e = listaEfemeridesAtual.get(i).getE();
            double Cus = listaEfemeridesAtual.get(i).getCus();
            double a = listaEfemeridesAtual.get(i).getAsqrt() * listaEfemeridesAtual.get(i).getAsqrt();

            double toe = listaEfemeridesAtual.get(i).getToe();
            double Cic = listaEfemeridesAtual.get(i).getCic();
            double omega_0 = listaEfemeridesAtual.get(i).get0mega_0();
            double Cis = listaEfemeridesAtual.get(i).getCis();

            double io = listaEfemeridesAtual.get(i).getI0();
            double Crc = listaEfemeridesAtual.get(i).getCrc();
            double w = listaEfemeridesAtual.get(i).getW();
            double omega_v = listaEfemeridesAtual.get(i).getOmega_v();
            double idot = listaEfemeridesAtual.get(i).getIDOT();

            /*Tempo de transmisao do sinal*/
            double dtr = 0d; // ERRO DO RELÓGIO DO RECEPTOR
            double tr = calc_Tr(dataObservacao);
            double tgps = tr - (listaMedicoesAtual.get(i).getPseudorangeMeters() / LIGHTSPEED);

            double dts = a0 + a1 * (tgps - toe) + a2 * (Math.pow(tgps - toe,2.0)); // ERRO DO SATÉLITE fixme É O TOC
            double tpropag = listaMedicoesAtual.get(i).getPseudorangeMeters() / LIGHTSPEED - dtr + dts;

            tgps = tr - dtr- tpropag + dts; // melhoria no tempo de transmissao
            double delta_tk = tgps - toe;

            /*
              Considerando possível mudança de semana
              Autor: Bruno Vani
             */
            if (delta_tk > 302400)
                delta_tk = delta_tk - 604800;
            else if (delta_tk < -302400)
                delta_tk = delta_tk + 604800;

            /*(4.9)*/
            double no = Math.sqrt(GM / (a*a*a)); // terceira lei de kepler

            /*(4.10)*/
            double n = no + delta_n; // movimento medio corrigido
            double mk = m0 + n * delta_tk; // anomalia media

            /*
              iteracao - anomalia excentrica
             */
            /*(4.11)*/
            double ek = mk;
            for (int k = 0; k < 7; k++){
                ek = mk + e * Math.sin(ek);
            }

            // Anomalia verdadeira
            /*(4.12)*/
            double sen_vk = ( (Math.sqrt(1 - (e * e)) ) * Math.sin(ek) )  / ( 1 - (e * Math.cos(ek)) );
            double cos_vk = (Math.cos(ek) - e) / (1 - e * Math.cos(ek) );

            /*
              Teste do quadrante
              autor: Bruno Vani
             */
            double vk = 0d;
            if (((sen_vk >= 0) && (cos_vk >= 0)) || (sen_vk < 0) && (cos_vk >= 0)) { // I ou III quadrante
                vk = Math.atan(sen_vk / cos_vk);
            } else if (((sen_vk >= 0) && (cos_vk < 0)) || ((sen_vk < 0 ) && (cos_vk) < 0)) { //  II ou IV quadrante
                vk = Math.atan(sen_vk / cos_vk) + 3.1415926535898; // FIXME Math.pi();
            } else{
                Log.e("VK","Erro no ajuste do quadrante!");
            }

            //coordenadas planas do satelite
            /*(4.13)*/
            double fik = vk + w; // argumento da latitude
            double delta_uk = Cuc * Math.cos(2 * fik) + Cus * Math.sin(2 * fik); // correcao do argumento da latitude
            // latitude
            double uk = fik + delta_uk; //argumento da latitude corrigido
            /*(4.14)*/
            double delta_rk = Crc * Math.cos(2 * fik) + Crs * Math.sin(2 * fik); //correcao do raio
            double rk = a * (1 - e * Math.cos(ek)) + delta_rk; //raio corrigido

            double delta_ik = Cic * Math.cos(2 * fik) + Cis * Math.sin(2 * fik); //correcao da inclinacao
            double ik = io + idot * delta_tk + delta_ik; //inclinacao corrigida
            /*(4.15)*/
            // Coordenadas do satélite no plano
            double xk = rk * Math.cos(uk); //posicao x no plano orbital
            double yk = rk * Math.sin(uk); //posicao y no plano orbital

            // Coordenadas do satélite em 3D (WGS 84)
            double Omegak = omega_0 + omega_v * delta_tk - We * tgps;

            // Coordenadas originais do satelites
            double X = ((xk * Math.cos(Omegak)) - (yk * Math.sin(Omegak) * Math.cos(ik)));
            double Y = ((xk * Math.sin(Omegak)) + (yk * Math.cos(Omegak) * Math.cos(ik)));
            double Z = (yk * Math.sin(ik));

            // Coordenadas do satelites corrigidas do erro de rotacao da Terra
            double alpha = We * tpropag;
            double Xc = X + alpha * Y;
            double Yc = -alpha * X + Y;
            double Zc = Z;

            int PRN = listaEfemeridesAtual.get(i).getPRN();
            CoordenadaGPS novaCoord = new CoordenadaGPS(PRN,Xc,Yc,Zc,dts);
            listaCoordAtual.add(novaCoord);
        }
    }

    /**
     * Ajusta as medições GNSS (pseudodistancias) e as efemérides transmitidas (dados de navegação) para pertencer a mesma época.
     * <p> Elimina as medições e efemérides de outra época e mantem apenas as da época em análise.</p>
     * <p>
     *     Tudo dentro de uma mesmo UTC é considerado a mesma época.
     * </p>
     *@return A data para a época considerada no ajustamento.
     */
    private EpocaGPS escolherEpoca(int INDEX_ANALISE){ //FIXME ARRUMAR DATA!!!
        /**
         * DEFINIÇÃO MANUAL DA DATA DO RINEX:
         */
        int YEAR = 18; // FIXME RINEX
        int MONTH = 10; // FIXME RINEX
        int DAY_MONTH = 31; // FIXME RINEX
        //int DAY_WEEK = GNSSConstants.DAY_SEX; // FIXME RINEX
        int HOUR_DAY = 20; // FIXME RINEX
        int MIN_HOUR = 0; // FIXME RINEX
        double SEC = 0.0; // FIXME RINEX

        GNSSDate dataRINEX = new GNSSDate(YEAR,MONTH,DAY_MONTH,HOUR_DAY,MIN_HOUR,SEC);

        EpocaGPS epocaEmAnalise = listaEpocas.get(INDEX_ANALISE);

        qntSatEpchAtual = epocaEmAnalise.getNumSatelites();

        listaMedicoesAtual.addAll(epocaEmAnalise.getListaMedicoes());

        int cont = 0;
        int j = 0;

        try{
            do{
                if ( (listaEfemeridesOriginal.get(j).getData().compareTo(dataRINEX) == 0) && // Mesma época
                        epocaEmAnalise.containsSatellite(listaEfemeridesOriginal.get(j).getPRN()) )
                { // Satélite da Mesma Época
//                    Log.i("TimeNanosUtilizado: ","TimeNanos" + String.valueOf(listaMedicoesOriginal.get(j).getTimeNanos().toString()));
                    listaEfemeridesAtual.add(listaEfemeridesOriginal.get(j));
                    cont++;
                }
                j++;
            } while (cont < qntSatEpchAtual);
        }catch (IndexOutOfBoundsException e){
            Log.e("Index","Erro nas efemérides: " + e.getMessage());
            e.printStackTrace();
        }

        Collections.sort(listaMedicoesAtual);
        Collections.sort(listaEfemeridesAtual);

        return epocaEmAnalise;
    }

    public void processar_epoca(int INDEX_ANALISE){
        listaEfemeridesAtual = new ArrayList<>();
        listaMedicoesAtual = new ArrayList<>();
        listaCoordAtual = new ArrayList<>();
        listaPRNsAtual = new ArrayList<>();
        epocaAtual = escolherEpoca(INDEX_ANALISE);
        calcCoordenadas();
        listaResultados.add(calcularMMQ()); // para a época atual
    }

    public void processar_todas_epocas(){
//        Log.i("RESULTADO_HEADER","# Epoca (GPS time); N_epoca; X(m); Y(m); Z(m); Dtr(s); SigmaX(m); SigmaY(m); SigmaZ(m); SigmaDtr(s); Qtde_Sat; Dtr(m);");
        for (int i = 0; i < listaEpocas.size(); i++) {
            processar_epoca(i);
        }
    }

    public void processar_n_epocas(int lastEpch){
//        Log.i("RESULTADO_HEADER","# Epoca (GPS time); N_epoca; X(m); Y(m); Z(m); Dtr(s); SigmaX(m); SigmaY(m); SigmaZ(m); SigmaDtr(s); Qtde_Sat; Dtr(m);");
        for (int i = 0; i < lastEpch; i++) {
            processar_epoca(i);
        }
    }

    /**
     * Aplica o ajustamento pelo método dos mínimos quadrados (MMQ). <p>
     * Utiliza a abordagem encontrada em (MONICO, 2008) p. 292-300.
     */
    private ResultEpch calcularMMQ(){
        double[] L0 = new double[qntSatEpchAtual];
        double[] L = new double[qntSatEpchAtual]; // Delta_L
        double[][] A = new double[qntSatEpchAtual][4];

        double[] Lb = new double[qntSatEpchAtual];
        for (int i = 0; i < qntSatEpchAtual; i++) {
            Lb[i] = listaMedicoesAtual.get(i).getPseudorangeMeters();
        }

//      USANDO O VETOR NULO:
//      double Xe = 0d;
//      double Ye = 0d;
//      double Ze = 0d;

//      USANDO O MARCO EP2:
        double Xe = 3687623.9881914;
        double Ye = -4620693.11583979;
        double Ze = -2387150.62016113;

        double[] X0 = new double[]{Xe, Ye, Ze,0d};
        double[] X = new double[4];
        double[] Xa = new double[4];

        double S0post;
        Double[] precision = new Double[4];
        Double[] discrepanciesXYZ = new Double[3];
        int numIteracao = 1;

        // Solucao iterativa pelo metodo parametrico (MONICO, 2008)
        for (int k = 0; k < MAX_ITERACOES; k++){
            // Vetor L0
            for (int i = 0; i < qntSatEpchAtual; i++){
                double dx = listaCoordAtual.get(i).getX() - X0[0];
                double dy = listaCoordAtual.get(i).getY() - X0[1];
                double dz = listaCoordAtual.get(i).getZ() - X0[2];

                double ro = Math.sqrt( (dx*dx) + (dy*dy) + (dz*dz) );
                L0[i] = ro;
            }

            //Vetor delta_L => L = Lb-L0;
            for (int i = 0; i < Lb.length; i++){
                L[i] = Lb[i] - ( L0[i] + LIGHTSPEED * (X0[3] - listaCoordAtual.get(i).getDts()) ) ;
            }

            //MATRIZ A
            for (int i = 0; i < qntSatEpchAtual; i++){
                double dx = listaCoordAtual.get(i).getX() - X0[0];
                double dy = listaCoordAtual.get(i).getY() - X0[1];
                double dz = listaCoordAtual.get(i).getZ() - X0[2];

                double distGeo = Math.sqrt(( dx*dx) + (dy*dy) + (dz*dz) );

                A[i][0] = -( dx / distGeo);
                A[i][1] = -( dy / distGeo);
                A[i][2] = -( dz / distGeo);
                A[i][3] = 1.0d;
            }

            RealMatrix rA =  MatrixUtils.createRealMatrix(A);
            RealMatrix rP = MatrixUtils.createRealIdentityMatrix(qntSatEpchAtual);
            RealVector rL  =  MatrixUtils.createRealVector(L);
            //  N = A'PA;
            RealMatrix rN = rA.transpose().multiply(rP).multiply(rA);
            //U = A'PL;
            RealVector rU = rA.transpose().multiply(rP).operate(rL);
            //X = -inv(N)*U;

            RealMatrix rInvN;
            RealVector rX;

            try {
                rInvN = MatrixUtils.inverse(rN);
                rX = rInvN.operate(rU);
            } catch (org.apache.commons.math3.linear.SingularMatrixException e){
                Log.e("Inv","Matrix is singular!");
                return null;
            }

            X = rX.toArray();
//            double[] X2 = X;
            double[] X2 = new double[X.length];
            System.arraycopy(X,0,X2,0,X.length);
            X[3] = X[3] / LIGHTSPEED;

            //Xa = X0+X;
            for (int j = 0; j < X0.length; j++){
                Xa[j] = X0[j] + X[j];
            }

            // Vetor dos resíduos V = AX+L
            RealVector rX2 = MatrixUtils.createRealVector(X2);
            RealVector rV = rA.operate(rX2).add(rL);

            // Setup VtPV
            RealMatrix rVt = MatrixUtils.createRowRealMatrix(rV.toArray()); // Inverte V
            RealMatrix result = rVt.multiply(rP);
            RealVector VtPV = result.operate(rV);

            // Fator de variância a posteriori:
            S0post = VtPV.getEntry(0) / (double) (qntSatEpchAtual - 4);

            // MVC das coordenadas ajustadas
            RealMatrix MVCXa = rInvN.scalarMultiply(S0post);

            if (!MatrixUtils.isSymmetric(MVCXa,0.005)){
                Log.e("MVCXa","Should be symmetric!");
                Log.e("MVCXa",MVCXa.toString().replace("},","},\n")
                        .replace("BlockRealMatrix",""));
            }

            // Desvio padrão das coordenadas:
            precision[0] = Math.sqrt(MVCXa.getEntry(0,0)); // Coordenada Xa
            precision[1] = Math.sqrt(MVCXa.getEntry(1,1)); // Coordenada Ya
            precision[2] = Math.sqrt(MVCXa.getEntry(2,2)); // Coordenada Za
            precision[3] = Math.sqrt(MVCXa.getEntry(3,3)); // Coordenada dtr

            // Discrepâncias em relação as coordenadas originais
            discrepanciesXYZ[0] = Xe - Xa[0];
            discrepanciesXYZ[1] = Ye - Xa[1];
            discrepanciesXYZ[2] = Ze - Xa[2];

            //Verificação da Tolerancia
            if ( X[0] < 0.0004 && X[1] < 0.0004 && X[2] < 0.0004) { // Módulo do erro das coordenadas em metros
                break;
            }else{ // Próxima iteração
                //X0 = Xa;
                System.arraycopy(Xa,0,X0,0,X0.length);
            }
            numIteracao++;
        } // Fim do laço do ajustamento

        Ecef2LlaConverter.GeodeticLlaValues valores = Ecef2LlaConverter.convertECEFToLLACloseForm(
                X0[0], X0[1], X0[2]);

        Double latiDegrees =  Math.toDegrees(valores.latitudeRadians);
        Double longDegrees =  Math.toDegrees(valores.longitudeRadians);
        Double altMeters   = valores.altitudeMeters;

        ResultEpch resultado = new ResultEpch(epocaAtual.getId(), epocaAtual.getDateUTC(), Xa[0], Xa[1], Xa[2], Xa[3], latiDegrees, longDegrees, altMeters,
                numIteracao, precision[0], precision[1], precision[2], precision[3], epocaAtual.getNumSatelites(),
                discrepanciesXYZ[0], discrepanciesXYZ[1], discrepanciesXYZ[2]);

//        Log.i("RESULTADO", resultado.toString());

        return resultado;
    }

    public String[] getListaEpocas(){
        ArrayList<String> lista = new ArrayList<>();
        for (int i = 0; i < listaEpocas.size(); i++){
            lista.add(listaEpocas.get(i).toString());
            lista.add("\n----------------------------------------------------------------\n");

        }
        return (Arrays.copyOf(lista.toArray(), lista.size(), String[].class));
    }

    public String[] getListaResultados(){
        ArrayList<String> lista = new ArrayList<>();
        lista.add("# Epoca (GPS time); N_epoca; X(m); Y(m); Z(m); Dtr(s); SigmaX(m); SigmaY(m); SigmaZ(m); SigmaDtr(s); Qtde_Sat; Dtr(m);\n");
        for (int i = 0; i < listaResultados.size(); i++){
            lista.add(listaResultados.get(i).toString() + "\n");
        }
        return (Arrays.copyOf(lista.toArray(), lista.size(), String[].class));
    }

    public static int getNumResultados() {
        return  listaResultados.size();
    }

    public boolean gravar_epocas(Context context){
        writter = new TextWritter(context); // FIXME
        return writter.gravar_txtSD(getListaEpocas(),"ListEpchs.txt");
    }

    public boolean gravar_resultados(Context context){
        writter = new TextWritter(context); // FIXME
        return writter.gravar_txtSD(getListaResultados(),"ListResults.txt");
    }

    public void send_txt(){
        writter.send();
    }

    public ArrayList<EpocaObs> getObservacoes() {
        ArrayList<EpocaObs> listaObservacoes = new ArrayList<>();

        for (int i = 0; i < listaEpocas.size(); i++) {

            GNSSDate utc = listaEpocas.get(i).getDateUTC();
            ArrayList<Double> obs = listaEpocas.get(i).getPseudorangesObs();
            ArrayList<Integer> prns = listaEpocas.get(i).getListaPRNs();

            EpocaObs newObserv = new EpocaObs(utc, prns, obs);
            listaObservacoes.add(newObserv);
        }

        Log.i("OBSERVACOES", Arrays.deepToString(listaObservacoes.toArray()));
        return listaObservacoes;
    }

    public CoordenadaGPS getCentroide(){
        double sumX = 0d;
        double sumY = 0d;
        double sumZ = 0d;

        int qntResult = listaResultados.size();

        for (int i = 0; i < qntResult; i++) {
            sumX += listaResultados.get(i).getXmeters();
            sumY += listaResultados.get(i).getYmeters();
            sumZ += listaResultados.get(i).getZmeters();
        }

        double meanX = sumX / qntResult;
        double meanY = sumY / qntResult;
        double meanZ = sumZ / qntResult;

        return new CoordenadaGPS(-1, meanX, meanY, meanZ, -1);
    }

    public ArrayList<CoordenadaGeodesica> getResultadosGeodeticos() {
        ArrayList<CoordenadaGeodesica> listaResulGeod= new ArrayList<>();

        for (int i = 0; i < listaResultados.size(); i++) {

            CoordenadaGeodesica resultadoEpoca = new CoordenadaGeodesica(i + 1,
                    listaResultados.get(i).getLatiDegrees(),
                    listaResultados.get(i).getLongDegrees(),
                    listaResultados.get(i).getAltMeters()
            );

            listaResulGeod.add(resultadoEpoca);
        }

        CoordenadaGPS centroideXYZ = getCentroide();

        Ecef2LlaConverter.GeodeticLlaValues valores = Ecef2LlaConverter.convertECEFToLLACloseForm(
                centroideXYZ.getX(), centroideXYZ.getY(), centroideXYZ.getZ());

        Double latiDegrees =  Math.toDegrees(valores.latitudeRadians);
        Double longDegrees =  Math.toDegrees(valores.longitudeRadians);
        Double altMeters   =  valores.altitudeMeters;

        CoordenadaGeodesica resultCentroide = new CoordenadaGeodesica(listaResulGeod.size() + 1,
                latiDegrees,
                longDegrees,
                altMeters
        );

        listaResulGeod.add(resultCentroide);

//        Log.i("RESULTADO_GEO",Arrays.deepToString(listaResultados.toArray()));
        return listaResulGeod;
    }

}
