package Codigos;

import android.content.Context;
import android.util.Log;

import com.rogeriocarmo.gnss_mobilecalculator.R;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Codigos.GNSSConstants.C_TO_N0_THRESHOLD_DB_HZ;
import static Codigos.GNSSConstants.TOW_DECODED_MEASUREMENT_STATE_BIT;
import static Codigos.GNSSConstants.WEEKSEC;

public class ProcessamentoPPS {

    public static ArrayList<GNSSNavMsg> listaEfemerides = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoes = new ArrayList<>();
    public static ArrayList<CoordenadaGPS> listaCoord = new ArrayList<>();
    public static ArrayList<Integer> listaPRNs = new ArrayList<>();
    private static int l;


    public ProcessamentoPPS(){ //TODO Por enquanto pegar da pasta raw assets msm!
        this.listaEfemerides = new ArrayList<>();
        this.listaMedicoes = new ArrayList<>();
        this.listaCoord = new ArrayList<>();
        this.listaPRNs = new ArrayList<>();
    }

    /**
     *
     * @param context A activity em execução atual
     * @return
     * @throws IOException
     */
    public static String readRINEX_RawAssets(Context context) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.hour1410)));
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

            try { // FIXME REVER
//                double Toc = calcTOC_tr_(Integer.valueOf(mLine.substring(9, 11).replaceAll("\\s", "")), // dia
//                        Integer.valueOf(mLine.substring(12, 14).replaceAll("\\s", "")), // hora
//                        Integer.valueOf(mLine.substring(15, 17).replaceAll("\\s", "")), // minuto
//                        Double.valueOf(mLine.substring(18, 22).replace('D', 'e').trim())); // segundo FIXME
                double Toc = calcTOC_tr_(GNSSConstants.DAY_QUA, // dia // fixme ESTA MANUAL ESSA ENTRADA!
                        Integer.valueOf(mLine.substring(12, 14).replaceAll("\\s", "")), // hora
                        Integer.valueOf(mLine.substring(15, 17).replaceAll("\\s", "")), // minuto
                        Double.valueOf(mLine.substring(18, 22).replace('D', 'e').trim())); // segundo FIXME

                Log.i("TOC", "Dia: " + mLine.substring(9, 11).replaceAll("\\s", "") +
                        " Hora: " + mLine.substring(12, 14).replaceAll("\\s", "") +
                        " Minutos: " + mLine.substring(15, 17).replaceAll("\\s", "") +
                        " Segundos: " + mLine.substring(18, 22).replaceAll("\\s", ""));


                int year = Integer.valueOf(mLine.substring(3, 6).replaceAll("\\s", ""));
                int month = Integer.valueOf(mLine.substring(6, 8).replaceAll("\\s", ""));
                int day = Integer.valueOf(mLine.substring(9, 11).replaceAll("\\s", ""));
                int hour = Integer.valueOf(mLine.substring(12, 14).replaceAll("\\s", ""));
                int minute = Integer.valueOf(mLine.substring(15, 17).replaceAll("\\s", ""));
                double seconds = Double.valueOf(mLine.substring(18, 22).replaceAll("\\s", ""));

                Log.i("year_RINEX",String.valueOf(year));
                Log.i("month_RINEX",String.valueOf(month));
                Log.i("day_RINEX",String.valueOf(day));
                Log.i("hour_RINEX",String.valueOf(hour));
                Log.i("minute_RINEX",String.valueOf(minute));
                Log.i("seconds_RINEX",String.valueOf(seconds));

                efemeride.setToc(Toc);
//                efemeride

             GNSSDate data = new GNSSDate(year, month, day, hour, minute, seconds);
             efemeride.setGNSSDate(data); // TODO VERIFICAR ERRO

            }catch (Exception err){
                efemeride.setToc(0);
                Log.e("TOC-ERR","Erro: " + err.getMessage());
//                efemeride.setGNSSDate(null);
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
            listaEfemerides.add(efemeride);
            Log.i("FIM-MENSAGEM","===========================================");
        }

        reader.close();
        return sb.toString();
    }

    /**
     * Converte uma data (UTC) em segundos da semana GPS correspondente.
     * <p>Implementação conforme (MONICO, 2008)</p>
     * @param DAY_WEEK Dia da semana, 0 = Domingo...
     * @param HOR Horário do dia (UTC)
     * @param MIN Minutos do dia (UTC)
     * @param SEG Segundos do dia (UTC)
     * @return Segundos da semana GPS correspondente
     */
    public static double calcTOC_tr_(int DAY_WEEK, int HOR, int MIN, double SEG) { // TODO Adotar a abordagem de Julian Day do arquivo ReadRinexNav.m linha 83!
        return (  (DAY_WEEK * 24 + HOR) * 3600 + MIN * 60 + SEG );
    }

    /**
     * Converte uma data (UTC) em segundos da semana GPS correspondente.
     * <p>Implementação conforme (MONICO, 2008)</p>
     * @param dataGNSS Data no formato UTC.
     * @return Segundos da semana GPS correspondente
     * @see GNSSDate
     */
    public static double calcTOC_tr_(GNSSDate dataGNSS) { // TODO Adotar a abordagem de Julian Day do arquivo ReadRinexNav.m linha 83!
        return (  (dataGNSS.getDay() * 24 + dataGNSS.getHour()) * 3600 + dataGNSS.getMin() * 60 + dataGNSS.getSec() );
    }

    /**
     * Lê um arquivo <b>RINEX de Navegação</b> e retorna a quantidade de efemérides no arquivo.
     * @param context Activity em execução
     * @return O número de efemérides brutas no arquivo RINEX de observação
     * @throws IOException
     */
    public static int contEfemerides(Context context) throws IOException{
        int numLines = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.hour1410)));

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

    /**
     * Lê o arquivo gerado pelo GNSS Logger app para extrair as medições brutas (linhas Raw).
     * @param context A activity em execução
     * @return
     * @throws IOException
     */
    public static String readLogger_RawAssets(Context context) throws  IOException{
        int qntMedicoesDescartadas = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.logtainanep1))); // FIXME DEIXAR DINAMICO

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

    /**
     * Calcula as pseudodistâncias em metros para cada <b>medição GPS</b> capturada.
     * <p>Essas pseudodistãncias preencherão o velor Lb no ajustamento paramétrico.</p>
     * @see ProcessamentoPPS#calcularMMQ()
     */
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

//            int year = Integer.valueOf(2017);
//            int month = Integer.valueOf(12);
//            int day = Integer.valueOf(13);


            //GPS Week number and Seconds within the week:
            int weekNumberr =  (int)Math.floor(-(double)(listaMedicoes.get(i).getFullBiasNanos()*1e-9/GNSSConstants.WEEKSEC));
            /**
             * Aply gpsWeek % 1024 to get the week number in [0,1024]
             */
            int gpsWeek = weekNumberr;// % 1024;
            Long gpsSecsWek = Math.round(tRx * 1e-9); // FIXME REVER
            //TESTE FIXME
            gpsSecsWek = Math.round((listaMedicoes.get(i).getReceivedSvTimeNanos() + prMilliSeconds) * 1e-9);
            Log.i("gpsWeek","Semana: " + gpsWeek + " Segundos da semana: " + gpsSecsWek.intValue());

            GpsTime gpt = GpsTime.fromWeekTow(gpsWeek,gpsSecsWek.intValue());
            gpt.getUtcDateTime();

            listaMedicoes.get(i).setGpsWeek(gpsWeek);
//            int hour = Integer.valueOf(mLine.substring(12, 14).replaceAll("\\s", ""));
//            int minute = Integer.valueOf(mLine.substring(15, 17).replaceAll("\\s", ""));
//            double seconds = Double.valueOf(mLine.substring(18, 22).replaceAll("\\s", ""));
            int year = gpt.getUtcDateTime().getYear() % 2000;
            int month = gpt.getUtcDateTime().getMonthOfYear();
            int day = gpt.getUtcDateTime().getDayOfMonth();
            int hour = gpt.getUtcDateTime().getHourOfDay();
            int minute = gpt.getUtcDateTime().getMinuteOfHour();
            double seconds = gpt.getUtcDateTime().getSecondOfMinute();

            Log.i("year_OBS",String.valueOf(year));
            Log.i("month_OBS",String.valueOf(month));
            Log.i("day_OBS",String.valueOf(day));
            Log.i("hour_OBS",String.valueOf(hour));
            Log.i("minute_OBS",String.valueOf(minute));
            Log.i("seconds_OBS",String.valueOf(seconds));

            Log.i("hora-minuto", "Svid: " + listaMedicoes.get(i).getSvid() +
                    " Hora: " + String.valueOf(hour) + " Minuto: " + String.valueOf(minute));

            GNSSDate data = new GNSSDate(year, month, day, hour, minute, seconds);
            listaMedicoes.get(i).setData(data);

            Log.i("tTx/tRx","Svid: " +  listaMedicoes.get(i).getSvid() + " tTx: " + tTx + " tRx: " + tRx + " Intervalo: " + prMilliSeconds);

            Log.i("prr", "Svid: " +  listaMedicoes.get(i).getSvid() + " Pseudorange: " + listaMedicoes.get(i).getPseudorangeMeters() + " m");
            Log.i("Uncertainty", "Svid: " +  listaMedicoes.get(i).getSvid() + " Uncertainty: " + listaMedicoes.get(i).getPseudoRangeUncertaintyMeters() + " m");
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
    public static GNSSDate ajustarEpocas(){
        /**
         * DEFINIÇÃO MANUAL DA ÉPOCA PARA ANÁLISE:
         */
        int YEAR = 18;
        int MONTH = 12;
        int DAY_MONTH = 21;
        int DAY_WEEK = GNSSConstants.DAY_QUA;
        int HOUR_DAY = 19;//fixme
        int MIN_HOUR = 30;
        double SEC = 0.0;


        //TODO


        //epocaAnalise.compareTo()

        int size = listaMedicoes.size() - 6;

        /**
         * A seleção da época é feita de modo manual:
         * Descarta as observações fora da época
         */
        for (int i = 0; i < size;i++){
            listaMedicoes.remove(0);
        }

        /**
         * O ArrayList de Medições contem apenas medições da época em análise.
         * Preenche uma Lista com os PRNs dos satélites presentes nas medições que serão utilizadas
         */
        for (int i = 0; i < listaMedicoes.size(); i++){
            listaPRNs.add(listaMedicoes.get(i).getSvid());
        }

        Collections.sort(listaPRNs);


        /**
         * Remove do ArrayList de Efemérides aqueles que não estão na epoca
         * E cujo PRN não está no ArrayList de Observações
         *  Obtem uma lista com os PRNs das observações para a época selecionada
         */
        for (int i = 0; i < listaEfemerides.size(); i++){
            GNSSNavMsg efemerideAtual = listaEfemerides.get(i);
            Integer PRNatual = Integer.valueOf(efemerideAtual.getPRN().trim());

            if ( (efemerideAtual.getData().getHour() != HOUR_DAY ||
                  efemerideAtual.getData().getMin()  != MIN_HOUR) )
            {
                listaEfemerides.remove(i);
//                continue;
            }else{
                Log.i("Efe","PRN: "+ efemerideAtual.getPRN() + " Efemeride mantida - Hora:" + efemerideAtual.getData().getHour() + " Minutos: " +efemerideAtual.getData().getMin() );
            }

//            if (!listaPRNs.contains(PRNatual)){
//                listaEfemerides.remove(i);
//            }
        }

        Collections.sort(listaMedicoes);
        Collections.sort(listaEfemerides);

        int tamanho = listaEfemerides.size();
        Integer ultimoPRN = Integer.valueOf(listaEfemerides.get(0).getPRN().trim());

        try{
            /**
             * Nesse ponto temos efemérides repetidas para determinado satélite
             * Selecionando efemérides únicas para cada satélite:
             */
            for (int i = tamanho - 1; i > 0; i--){ // TODO
//                if (ultimoPRN.equals(Integer.valueOf(listaEfemerides.get(i).getPRN())) ){
//                    ultimoPRN = Integer.valueOf(listaEfemerides.get(i).getPRN());
//                    listaEfemerides.remove(i);
//                    continue;
//
//                }
//                ultimoPRN = Integer.valueOf(listaEfemerides.get(i).getPRN());

                if (listaEfemerides.get(i).getPRN().equals(listaEfemerides.get(i - 1).getPRN())){
                    listaEfemerides.remove(i - 1);
                }

            }

            int tam = listaEfemerides.size();
            int i = tam - 1;
            do{
                Integer PRNatual = Integer.valueOf(listaEfemerides.get(i).getPRN());
                if (!listaPRNs.contains(PRNatual))
                    listaEfemerides.remove(i);
                i--;
            }while (listaPRNs.size() != listaEfemerides.size() );

            Log.i("FimEfemerides","Definidas efemérides únicas para cada satélite");

        }catch (IndexOutOfBoundsException err){
            Log.e("Erro_Index","Erro listaEfemerides");
        }


        l = listaEfemerides.size(); // FIXME


        /**
         * PROCESSANDO PARA O UTC 19:30
         */
//        GNSSDate epocaAnalise = new GNSSDate(year,month,day,hour,min,sec);
        GNSSDate epocaAnalise = new GNSSDate(YEAR,MONTH,DAY_MONTH,HOUR_DAY,MIN_HOUR,SEC);
        return epocaAnalise;
    }

    /**
     * Calcula as <b>coordenadas X,Y,Z (WGS-84)</b> para cada satélite.
     * <p>Calcula o <b>erro do relógio</b> para cada satélite em segundos.
     */
    public static void calcCoordendas(){
        //int L = 10;
        double GM = 3.986004418E14;
        double We = 7.2921151467E-5;
        double c = 299792458;

        Log.i("Coord","Inicio do calculo das coordenadas dos satélites.");

        GNSSDate dataObservacao = ajustarEpocas();

        for (int i = 0; i < l; i++ ){// FIXME
            //------------------------------------------
            //Dados de entrada
            //------------------------------------------
            //Tempo de recepcao do sinal ->  Hora da observacao
//            double tr = (3*24+0)*3600 + 0*60 + 0.00; // FIXME CORRIGIR O TEMPO
//            double tr = calcTOC_tr_(GNSSConstants.DAY_QUA,)
            double tr = calcTOC_tr_(dataObservacao);

            double a0 = listaEfemerides.get(i).getAf0();
            double a1 = listaEfemerides.get(i).getAf1();
            double a2 = listaEfemerides.get(i).getAf2();

            double Crs = listaEfemerides.get(i).getCrs();
            double Deln = listaEfemerides.get(i).getDelta_n();
            double Mo = listaEfemerides.get(i).getM0();

            double Cuc = listaEfemerides.get(i).getCuc();
            double e = listaEfemerides.get(i).getE();
            double Cus = listaEfemerides.get(i).getCus();
            double a = listaEfemerides.get(i).getAsqrt() * listaEfemerides.get(i).getAsqrt();

            double toe = listaEfemerides.get(i).getToe();
            double Cic = listaEfemerides.get(i).getCic();
            double Omega0 = listaEfemerides.get(i).getOmega(); // FIXME REVER
            double Cis = listaEfemerides.get(i).getCis();

            double io = listaEfemerides.get(i).getI0();
            double Crc = listaEfemerides.get(i).getCrc();
            double w = listaEfemerides.get(i).getGPS_Week();
            double Omegadot = listaEfemerides.get(i).getOMEGA_DOT();

            double idot = listaEfemerides.get(i).getIDOT();

            // ------------------------------------------
            //Tempo de transmisao do sinal
            // ------------------------------------------
            double dtr = 0; // ERRO DO RELÓGIO
            double tgps = tr - listaMedicoes.get(i).getPseudorangeMeters()/c;

            double dts = a0 + a1*(tgps-toe) + a2*((tgps-toe)*(tgps-toe)); // ERRO DO SATÉLITE

            double tpropag = listaMedicoes.get(i).getPseudorangeMeters()/c - dtr +dts;

            tgps = tr-dtr-tpropag+dts; // melhoria no tempo de transmissao

            //------------------------------------------
            //Coordenadas do satelite
            //------------------------------------------

            double Deltk = tgps - toe;

            double no = Math.sqrt((GM/(a*a*a)));
            double n = no + Deln;

            double Mk = Mo + n*Deltk;

            // EQUAÇÃO DE EULER
            double Ek = Mk;

            Log.i("Ek","Cálculo iterativo da equação de Euler");
            for (int k = 0; k < 5; k++){
                Ek = Mk + e*Math.sin(Ek);
            }

            double cosVk = (Math.cos((Ek)-e)/(1-(e*(Math.cos(Ek)))));
            double sinVk = ((Math.sqrt(1-(e*e))*Math.sin(Ek))/(1-(e*Math.cos(Ek))));
            double Vk = Math.atan2(sinVk,cosVk);

            double Fik = Vk + w;
            double Deluk = (Cuc*Math.cos(2*Fik)) + (Cus*Math.sin(2*Fik));
            double uk = Fik + Deluk;

            double Delrk = (Crc*Math.cos(2*Fik)) + (Crs*Math.sin(2*Fik));
            double rk = (a*(1-(e*Math.cos(Ek)))) + Delrk;

            double Delik = (Cic*Math.cos(2*Fik)) + (Cis*Math.sin(2*Fik));
            double ik = io + (idot*Deltk) + Delik;

            // Coordenadas do satélite no plano
            double xk = rk*Math.cos(uk);
            double yk = rk*Math.sin(uk);

            // Coordenadas do satélite em 3D (WGS 84)
            double Omegak = Omega0 + Omegadot*Deltk - We*tgps;

            // Coordenadas originais do satelites
            double Xk = ((xk*Math.cos(Omegak))-(yk*Math.sin(Omegak)*Math.cos(ik)));
            double Yk = ((xk*Math.sin(Omegak))+(yk*Math.cos(Omegak)*Math.cos(ik)));
            double Zk = (yk*Math.sin(ik));

            // Coordenadas do satelites corrigidas do erro de rotacao da Terra
            double alpha = We*tpropag;
            double X = Xk + alpha*Yk;
            double Y = -alpha*Xk + Yk;
            double Z = Zk;

            //coord = [coord; efemerides(i,1) X Y Z dts];
            int PRN = Integer.valueOf(listaEfemerides.get(i).getPRN());
            CoordenadaGPS novaCoord = new CoordenadaGPS(PRN,X,Y,Z,dts);
            listaCoord.add(novaCoord);

        }

        Log.i("CoordFIM","Fim do cálculo das coordenadas!");

    }

    /**
     * Aplica o ajustamento pelo método dos mínimos quadrados (MMQ). <p>
     * Utiliza a abordagem encontrada em (MONICO, 2008) p. 292-300.
     */
    public static void calcularMMQ(){
//        GpsNavigationMessageStore;
//        Ephemeris.GpsEphemerisProto;
//        Ephemeris.GpsNavMessageProto;
//        double dx = 0.0, dy = 0.0, dyz = 0.0;

        int MAX_ITERACOES = 100;
        double c = 299792458;
        double[] L0 = new double[l];
        double[] Lb = new double[l];
        double[] L = new double[l]; // DeltaL
        double[][] A = new double[l][4];

        //Carregando o vetor Lb
        for (int i = 0; i < l; i++) { //listaMedicoes.size(); i++)
            Lb[i] = listaMedicoes.get(i).getPseudorangeMeters();
        }

        Log.i("Lb","Criação do vetor Lb");
//        Aproximações iniciais
//        double Xe = 3789545.41209;
//        double Ye = -4587255.83661;
//        double Ze = -2290619.16148;

        // Site para conversão:
        double Xe = 3702008.05442714;
        double Ye = 4611836.75133463;
        double Ze = 2382032.61478866;
//        double Xe = 3702008.05442714;
//        double Ye = 3702008.05442714;
//        double Ze = 2382032.61478866;
        //Medição NMEA usada para coordenada inicial
        /*
        NMEA,$GPGGA,175553.00,2207.263271,S,05124.533248,W,1,12,0.8,438.0,M,0.0,M,,*5A,1513187753149
        (ORIGINAL)
        NMEA UTILIZADA NO TESTE DO TAINAN:
        NMEA,$GPGGA,191430.00,2207.358552,S,05124.456111,W,1,00,0.8,438.2,M,-1.9,M,,*72
,1526930070258
        SITE UTILIZADO PARA A CONVERSAO: http://www.apsalin.com/convert-geodetic-to-cartesian.aspx
        */

        double[] X0 = new double[]{Xe, Ye, Ze,0};
        double[][] N = new double[4][4];
        double[] U = new double[4];
        double[] X = new double[4];
        double[] Xa = new double[4];

        // Solucao pelo metodo parametrico
        // Solucao iterativa
        for (int k = 0; k < MAX_ITERACOES; k++){
            // Vetor L0
            for (int i = 0; i < l; i++){ // PARA CADA SATÉLITE
                // dx = coord(i,2)-X0(1);
                double dx = listaCoord.get(i).getX() - X0[0];
                double dy = listaCoord.get(i).getY() - X0[1];
                double dz = listaCoord.get(i).getZ() - X0[2];

                // L0(i,1) = sqrt(dx^2+dy^2+dz^2) + c*(0 - coord(i,5))
                L0[i] = Math.sqrt((dx*dx) + (dy*dy) + (dz*dz)) + c * (0 - listaCoord.get(i).getDts());
            }

            //Vetor DeltaL:
            //L = L0-Lb;
            for (int i = 0; i < Lb.length; i++){
                L[i] = L0[i] - Lb[i];
            }

            //MATRIZ A
            for (int i = 0; i < l; i++){
                double dx = listaCoord.get(i).getX() - X0[0];
                double dy = listaCoord.get(i).getY() - X0[1];
                double dz = listaCoord.get(i).getZ() - X0[0];

                double distGeo = Math.sqrt((dx*dx) + (dy*dy) + (dz*dz));

                A[i][0] = -(listaCoord.get(i).getX() - X0[0])/distGeo;
                A[i][1] = -(listaCoord.get(i).getY() - X0[1])/distGeo;
                A[i][2] = -(listaCoord.get(i).getZ() - X0[2])/distGeo;
                A[i][3] = 1;
            }

            // Método Paramétrico
            Log.i("par","Iteração do método paramétrico");

            RealMatrix rA =  MatrixUtils.createRealMatrix(A);
            RealVector rL  =  MatrixUtils.createRealVector(L);


            //  N = A'*A;
            RealMatrix rN = rA.transpose().multiply(rA);
            //U = A'*L;
            RealVector rU = rA.transpose().operate(rL);

            //X = -inv(N)*U;
            RealMatrix rInvN = new LUDecomposition(rN).getSolver().getInverse();
            RealVector rX = rInvN.scalarMultiply(-1.0).operate(rU);

            X = rX.toArray();

            X[3] = X[3]/c;

            //Xa = X0+X; // FIXME FAZER UMA FUNÇÃO PARA ISSO
            for (int i = 0; i < X0.length; i++){
                Xa[i] = X0[i] + X[i];
            }

            //Verificação da Tolerancia
            /*
             if abs(max(X)) < 1e-6
                Xa
                abs(max(X))
                k
                break;
             else X0 = Xa;
             end
             */
            double erro = Math.abs(maxValue(Xa));
            if ( erro < 1e-6){
                Log.i("Fim",Xa.toString());
                Log.i("Fim","Coordenada Xr: " + Xa[0]);
                Log.i("Fim","Coordenada Yr: " + Xa[1]);
                Log.i("Fim","Coordenada Zr: " + Xa[2]);
                Log.i("Fim","Erro do relógio do receptor: " + Xa[3]);
                Log.i("Fim","Erro: " + erro);
                Log.i("Fim", "N° de iterações: " + k);
                break;
            }else{ // Próxima iteração
                //X0 = Xa; // ou copiar com um for
                for (int i = 0; i < X0.length; i++){
                    X0[i] = Xa[i];
                }
            }

        }

        Log.i("FimFOOR",Xa.toString());
        Log.i("FimFOOR","Coordenada Xr: " + Xa[0]);
        Log.i("FimFOOR","Coordenada Yr: " + Xa[1]);
        Log.i("FimFOOR","Coordenada Zr: " + Xa[2]);
        Log.i("FimFOOR","Erro do relógio do receptor: " + Xa[3]);
//        Log.i("Fim","Erro: " + erro);
//        Log.i("Fim", "N° de iterações: " + k);
        //TODO Verificação dos operadores de precisão:
        // Vetor dos resíduos
        // Fator de variância a posteriori
        // MVC das coordenadas ajustadas
    }

    /**
     * Retorna o maior valor de um array do tipo double.<p>
     * Utiliza uma estrutura do tipo List<Double> para o processamento.
     * @param array Um array primitivo do tipo double.
     * @return O maior valor do Array.
     */
    private static double maxValue(double array[]) {
        List<Double> list = new ArrayList<>();
        for (double anArray : array) {
            list.add(anArray);
        }
        return Collections.max(list);
    }
}
