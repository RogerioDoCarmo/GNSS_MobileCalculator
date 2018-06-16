package Codigos;

import android.content.Context;
import android.util.Log;

import com.rogeriocarmo.gnss_mobilecalculator.R;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static Codigos.GNSSConstants.C_TO_N0_THRESHOLD_DB_HZ;
import static Codigos.GNSSConstants.TOW_DECODED_MEASUREMENT_STATE_BIT;
import static Codigos.GNSSConstants.WEEKSEC;

public class ProcessamentoPPS {
    // ORIGINAIS
    public static ArrayList<GNSSNavMsg> listaEfemeridesOriginal = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoesOriginal = new ArrayList<>();
    // PARA CADA ÉPOCA
    public static ArrayList<GNSSNavMsg> listaEfemeridesAtual = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoesAtual = new ArrayList<>();

    public static ArrayList<CoordenadaGPS> listaCoordAtual = new ArrayList<>();
    public static ArrayList<Integer> listaPRNs = new ArrayList<>();
    public static ArrayList<EpocaGPS> listaEpocas = new ArrayList<>();
    public static ArrayList<CoordenadaGPS> listaCoordReceptor = new ArrayList<>();

    private static int qntSatProcessar;

    public ProcessamentoPPS(){ //TODO Por enquanto pegar da pasta raw assets msm!
        this.listaEfemeridesOriginal = new ArrayList<>();
        this.listaMedicoesOriginal = new ArrayList<>();
        this.listaEfemeridesAtual = new ArrayList<>();
        this.listaMedicoesAtual = new ArrayList<>();
        this.listaCoordAtual = new ArrayList<>();
        this.listaCoordReceptor = new ArrayList<>();
        this.listaPRNs = new ArrayList<>();
    }

    /**
     * Teste manual de cálculo de coordenada para o satélite G05
     * <p>A data é <b>15/07/2012(UTC)</b>
     * <p>A data da coleta é <b>2012-07-15 11h13m45s </b>
     * <p>A data da efeméride utilizada é <b>2012-07-15 10:00:00</b>
     * @param context A activity em execução atual
     * @return
     * @throws IOException
     */
    public static String testeCoord_G05(Context context) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.teste05)));
//        int numEfemerides = contEfemerides(context);

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();

        //PULANDO O CABEÇALHO DE 13 LINHAS...
        String mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();

        String sub = "";
        int numEfemerides = 1; // FIXME FAZER UM WHILE

        for (int i = 0; i < numEfemerides; i++){
            GNSSNavMsg efemeride = new GNSSNavMsg();
            mLine = reader.readLine();

//first line - epoch of satellite clock (toc)
//====================================================================
            sub = mLine.substring(0, 2).trim();
            efemeride.setPRN(Integer.valueOf(sub));  // FIXME
            Log.i("PRN", sub);

            try { // FIXME REVER

                int year = Integer.valueOf(mLine.substring(3, 6).replaceAll("\\s", ""));
                int month = Integer.valueOf(mLine.substring(6, 8).replaceAll("\\s", ""));
                int day = Integer.valueOf(mLine.substring(9, 11).replaceAll("\\s", ""));
                int hour = Integer.valueOf(mLine.substring(12, 14).replaceAll("\\s", ""));
                int minute = Integer.valueOf(mLine.substring(15, 17).replaceAll("\\s", ""));
                double seconds = Double.valueOf(mLine.substring(18, 22).replaceAll("\\s", ""));

                GNSSDate data = new GNSSDate(year, month, day, hour, minute, seconds);
                efemeride.setGNSSDate(data); // TODO VERIFICAR ERRO

            }catch (Exception err){
                efemeride.setToc(0);
                Log.e("TOC-ERR","Erro: " + err.getMessage());
//                efemeride.setGNSSDate(null);
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
//// second line - broadcast orbit
//====================================================================
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
//            third line - broadcast orbit (2)
//            ====================================================================

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
//--------------------------------------------------------------------------------------------------
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
//--------------------------------------------------------------------------------------------------

            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setI0(Double.parseDouble(sub.trim()));

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCrc(Double.parseDouble(sub.trim()));

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setW(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setOmega_v(Double.parseDouble(sub.trim()));
//            sixth line
//--------------------------------------------------------------------------------------------------

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
//--------------------------------------------------------------------------------------------------

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
            //--------------------------------------------------------------------------------------------------
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

//--------------------------------------------------------------------------------------------------
            listaEfemeridesOriginal.add(efemeride);
        }

        reader.close();

        return sb.toString();
    }

    /**
     * Teste manual de cálculo de coordenada para o satélite G05
     * @see ProcessamentoPPS#testeCoord_G05(Context)
     * Calcula as <b>coordenadas X,Y,Z (WGS-84)</b> para o satélite.
     * <p>Calcula o <b>erro do relógio</b> para o satélite em segundos.
     */
    public static void calcCoordendasTeste_G05(){
        /**
         * Os valroes originais foram extraído da execução no site:
         * <p>http://is-cigala-calibra.fct.unesp.br/pps/indexn.php</p>
         */
        // O valor da pseudodistancia de G05 extraído do RINEX.
        Double pseudorange = 25259606.102;

        double Xoriginal = 12749332.708;
        double Yoriginal = 8942678.944;
        double Zoriginal = -21541599.861;
        double dtsOriginal =  -0.00033476511554568;

        //int L = 10;
        double GM = 3.9860044185E14; // 3.986004418E14;
        double We = 7.2921151467E-5; // 7.2921151467E-5;
        double c = 299792458;

        GNSSDate dataObservacao = new GNSSDate(12,7,15,11,0,0);

        for (int i = 0; i < 1; i++ ){// FIXME
            //------------------------------------------
            //Dados de entrada
            //------------------------------------------
            //Tempo de recepcao do sinal ->  Hora da observacao
//            double tr = (3*24+0)*3600 + 0*60 + 0.00; // FIXME CORRIGIR O TEMPO
//            double tr = calc_Tr(GNSSConstants.DAY_QUA,)
            double a0 = listaEfemeridesOriginal.get(i).getAf0();
            double a1 = listaEfemeridesOriginal.get(i).getAf1();
            double a2 = listaEfemeridesOriginal.get(i).getAf2();

            double Crs = listaEfemeridesOriginal.get(i).getCrs();
            double delta_n = listaEfemeridesOriginal.get(i).getDelta_n();
            double m0 = listaEfemeridesOriginal.get(i).getM0();

            double Cuc = listaEfemeridesOriginal.get(i).getCuc();
            double e = listaEfemeridesOriginal.get(i).getE();
            double Cus = listaEfemeridesOriginal.get(i).getCus();
            double a = listaEfemeridesOriginal.get(i).getAsqrt() * listaEfemeridesOriginal.get(i).getAsqrt();

            double toe = listaEfemeridesOriginal.get(i).getToe();
            double Cic = listaEfemeridesOriginal.get(i).getCic();
            double omega_0 = listaEfemeridesOriginal.get(i).get0mega_0();
            double Cis = listaEfemeridesOriginal.get(i).getCis();

            double io = listaEfemeridesOriginal.get(i).getI0();
            double Crc = listaEfemeridesOriginal.get(i).getCrc();
            double w = listaEfemeridesOriginal.get(i).getW();
            double omega_v = listaEfemeridesOriginal.get(i).getOmega_v();
            double idot = listaEfemeridesOriginal.get(i).getIDOT();

            // ------------------------------------------
            //Tempo de transmisao do sinal
            // ------------------------------------------
            double dtr = 0d; // ERRO DO RELÓGIO
            double tr = calc_Tr(GNSSConstants.DAY_DOM,11,0,0);
            double tgps = tr - (pseudorange / c);

            double dts = a0 + a1 * (tgps - toe) + a2 * (Math.pow(tgps - toe,2.0)); // ERRO DO SATÉLITE
            double tpropag = pseudorange/c - dtr + dts;

            tgps = tr - dtr- tpropag + dts; // melhoria no tempo de transmissao

            //------------------------------------------
            //Coordenadas do satelite
            //------------------------------------------

            double delta_tk = tgps - toe;

            /**
             * Considerando possível mudança de semana
             * Autor: Bruno Vani
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

            /**
             * iteracao - anomalia excentrica
             */
            /*(4.11)*/
            double ek = mk;
            for (int k = 0; k < 7; k++){
                ek = mk + e * Math.sin(ek);
            }

            //Anomalia verdadeira
//            double sen_vk = ((Math.sqrt(1-(e*e))*Math.sin(ek))/(1-(e*Math.cos(ek))));
//            double cos_vk = (Math.cos((ek)-e)/(1-(e*(Math.cos(ek)))));
            // Anomalia verdadeira
            /*(4.12)*/
            double sen_vk = ( (Math.sqrt(1 - (e * e)) ) * Math.sin(ek) )  / ( 1 - (e * Math.cos(ek)) );
            double cos_vk = (Math.cos(ek) - e) / (1 - e * Math.cos(ek) );

            /**
             * Teste do quadrante
             * autor: Bruno Vani
             */
            double vk = 0d;
            if (((sen_vk >= 0) && (cos_vk >= 0)) || (sen_vk < 0) && (cos_vk >= 0)) { // I ou III quadrante
                vk = Math.atan(sen_vk / cos_vk);
            } else if (((sen_vk >= 0) && (cos_vk < 0)) || ((sen_vk < 0 ) && (cos_vk) < 0)) { //  II ou IV quadrante
                vk = Math.atan(sen_vk / cos_vk) + 3.1415926535898; // FIXME Math.pi();
            } else{
                Log.e("VK","Erro no ajuste do quadrante");
            }
//            double vk = Math.atan(sen_vk,cos_vk);
//            double vk = Math.atan2(sen_vk,cos_vk);

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
//            //Coordenadas finais para o processamento
//            double X = Xk;
//            double Y = Yk;
//            double Z = Zk;

            //coord = [coord; efemerides(i,1) X Y Z dts];
            int PRN = 5;
            CoordenadaGPS novaCoord = new CoordenadaGPS(PRN,Xc,Yc,Zc,dts);

            double difX = Xoriginal - Xc;
            double difY = Yoriginal - Yc;
            double difZ = Zoriginal - Zc;
            double difDts = dtsOriginal - dts;

            Log.i("FimTeste","Diferença na coordenada X: " + difX);
            Log.i("FimTeste","Diferença na coordenada Y: " + difY);
            Log.i("FimTeste","Diferença na coordenada Z: " + difZ);
            Log.i("FimTeste","Diferença no dts: " + difDts);
    }
        listaEfemeridesOriginal = null;
        listaEfemeridesOriginal = new ArrayList<>();
    }

    /**
     *
     * @param context A activity em execução atual
     * @return
     * @throws IOException
     */
    public static String readRINEX_RawAssets(Context context) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

//        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.hour1550original)));
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.brdc159)));

//        int numEfemerides = contEfemerides(context);

        // do reading, usually loop until end of file reading
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
//====================================================================
            sub = mLine.substring(0, 2).trim();
            efemeride.setPRN(Integer.valueOf(sub));  // FIXME
//            Log.i("PRN", sub);

            try { // FIXME REVER
                int year = Integer.valueOf(mLine.substring(3, 6).replaceAll("\\s", ""));
                int month = Integer.valueOf(mLine.substring(6, 8).replaceAll("\\s", ""));
                int day = Integer.valueOf(mLine.substring(9, 11).replaceAll("\\s", ""));
                int hour = Integer.valueOf(mLine.substring(12, 14).replaceAll("\\s", ""));
                int minute = Integer.valueOf(mLine.substring(15, 17).replaceAll("\\s", ""));
                double seconds = Double.valueOf(mLine.substring(18, 22).replaceAll("\\s", ""));

//                Log.i("year_RINEX",String.valueOf(year));
//                Log.i("month_RINEX",String.valueOf(month));
//                Log.i("day_RINEX",String.valueOf(day));
//                Log.i("hour_RINEX",String.valueOf(hour));
//                Log.i("minute_RINEX",String.valueOf(minute));
//                Log.i("seconds_RINEX",String.valueOf(seconds));
//                Log.i("seconds_RINEX",String.valueOf(seconds));
//                Log.i("Fim_TOC","=============================");

             GNSSDate data = new GNSSDate(year, month, day, hour, minute, seconds);
             efemeride.setGNSSDate(data);

            }catch (Exception err){
                efemeride.setToc(0);
                Log.e("TOC-ERR","Erro: " + err.getMessage());
//                efemeride.setGNSSDate(null);
            }

//            Log.i("af0","af0: " + mLine.substring(22,41).replace('D','e').replaceAll("\\s",""));
//            Log.i("af1","af1: " + mLine.substring(41,60).replace('D','e').replaceAll("\\s",""));
//            Log.i("af2","af2: " + mLine.substring(60,79).replace('D','e').replaceAll("\\s",""));

            double af0 = Double.valueOf(mLine.substring(22,41).replace('D','e')
                    .replaceAll("\\s",""));

            double af1 = Double.valueOf(mLine.substring(41,60).replace('D','e')
                    .replaceAll("\\s",""));

            double af2 = Double.valueOf(mLine.substring(60,79).replace('D','e')
                    .replaceAll("\\s",""));

            efemeride.setAf0(af0);
            efemeride.setAf1(af1);
            efemeride.setAf2(af2);
//// second line - broadcast orbit
//====================================================================
            mLine = reader.readLine();

            sub = mLine.substring(3, 22).replace('D', 'e');
            double iode = Double.parseDouble(sub.trim());
            efemeride.setIODE(iode);
//            Log.i("IODE",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCrs(Double.parseDouble(sub.trim()));
//            Log.i("Crs",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setDelta_n(Double.parseDouble(sub.trim()));
//            Log.i("Delta_n",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setM0(Double.parseDouble(sub.trim()));
//            Log.i("M0",sub);
//            third line - broadcast orbit (2)
//            ====================================================================

            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double Cuc = Double.parseDouble(sub.trim());
            efemeride.setCuc(Cuc);
//            Log.i("Cuc",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setE(Double.parseDouble(sub.trim()));
//            Log.i("E",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setCus(Double.parseDouble(sub.trim()));
//            Log.i("Cus",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setAsqrt(Double.parseDouble(sub.trim()));
//            Log.i("Asqrt",sub);
//fourth line
//--------------------------------------------------------------------------------------------------
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double toe = Double.parseDouble(sub.trim());
            efemeride.setToe(toe);
//            Log.i("Toe",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCic(Double.parseDouble(sub.trim()));
//            Log.i("Cic",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setOmega_0(Double.parseDouble(sub.trim()));
//            Log.i("Omega0","Valor: " + efemeride.get0mega_0() );

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setCis(Double.parseDouble(sub.trim()));
//            Log.i("Cis",sub);
            //fifth line
//--------------------------------------------------------------------------------------------------

            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setI0(Double.parseDouble(sub.trim()));
//            Log.i("I0",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCrc(Double.parseDouble(sub.trim()));
//            Log.i("Crc",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setW(Double.parseDouble(sub.trim()));
//            Log.i("w",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setOmega_v(Double.parseDouble(sub.trim()));
//            Log.i("Omega_v",sub);
            //sixth line
//--------------------------------------------------------------------------------------------------

            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setIDOT(Double.parseDouble(sub.trim()));
//            Log.i("IDOT",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            double L2Code = Double.parseDouble(sub.trim());
            efemeride.setCodeL2(L2Code);
//            Log.i("CodeL2",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            double week = Double.parseDouble(sub.trim());
            efemeride.setGPS_Week((int) week);
//            Log.i("GPS_WEEK",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            double L2Flag = Double.parseDouble(sub.trim());
            efemeride.setL2PdataFlag((int) L2Flag);
//            Log.i("L2_Flag",sub);
//seventh line
//--------------------------------------------------------------------------------------------------

            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double svAccur = Double.parseDouble(sub.trim());
            efemeride.setAccuracy((int) svAccur);
//            Log.i("Sv_Accur",sub);

            sub = mLine.substring(22, 41).replace('D', 'e');
            double svHealth = Double.parseDouble(sub.trim());
            efemeride.setHealth((int) svHealth);
//            Log.i("Sv_Health",sub);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setTGD(Double.parseDouble(sub.trim()));
//            Log.i("Tgd",sub);

            sub = mLine.substring(60, 79).replace('D', 'e');
            double iodc = Double.parseDouble(sub.trim());
            efemeride.setIODC((int) iodc);
//            Log.i("IODC",sub);
            //eigth line
            //--------------------------------------------------------------------------------------------------
            mLine = reader.readLine();

            int len = mLine.length();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setTtx(Double.parseDouble(sub.trim()));
//            Log.i("Transmission Time (TTX)",sub);

            if (len > 22) {
                sub = mLine.substring(22, 41).replace('D', 'e');
                efemeride.setFit_interval(Double.parseDouble(sub.trim()));

            } else {
                efemeride.setFit_interval(0);
            }

//            Log.i("Fit Interval",sub);

//--------------------------------------------------------------------------------------------------
            listaEfemeridesOriginal.add(efemeride);
//            Log.i("FIM-OBERVAVAO","===========================================");
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
    public static double calc_Tr(int DAY_WEEK, int HOR_DAY, int MIN, double SEG) {
        return (  (DAY_WEEK * 24 + HOR_DAY) * 3600 + MIN * 60 + SEG );
    }

    /**
     * Converte uma data (UTC) em segundos da semana GPS correspondente.
     * <p>Implementação conforme (MONICO, 2008)</p>
     * @param dataGNSS Data no formato UTC.
     * @return Segundos da semana GPS correspondente
     * @see GNSSDate
     */
    public static double calc_Tr(GNSSDate dataGNSS) {
        return (  (dataGNSS.getDay_week() * 24 + dataGNSS.getHour()) * 3600 + dataGNSS.getMin() * 60 + dataGNSS.getSec() );
    }

    /**
     * Lê um arquivo <b>RINEX de Navegação</b> e retorna a quantidade de efemérides no arquivo.
     * @param context Activity em execução
     * @return O número de efemérides brutas no arquivo RINEX de observação
     * @throws IOException
     */
    public static int contEfemerides(Context context) throws IOException{
        int numLines = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.brdc159)));

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

        return numLines / 8; // FIXME REVER PARA RINEX EDITADOS
    }

    /**
     * Lê o arquivo gerado pelo GNSS Logger app para extrair as medições brutas (linhas Raw).
     * @param context A activity em execução
     * @return
     * @throws IOException
     */
    public static String readLogger_RawAssets(Context context) throws  IOException{
        int qntMedicoesDescartadas = 0;
//        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.logdia05hora15))); // FIXME DEIXAR DINAMICO


        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.log15901))); // FIXME DEIXAR DINAMICO


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
//                Log.i("raw",mLine);

                String[] linhaRaw = mLine.split(",");

                if (!linhaRaw[28].equalsIgnoreCase(String.valueOf(GNSSConstants.CONSTELLATION_GPS))){
//                    Log.e("Constellation", "Non-GPS Measurement: Type " + linhaRaw[28]);
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
//                Log.i("State","Resultado verificado: " + Integer.parseInt(linhaRaw[13]));

//                int state = Integer.parseInt(linhaRaw[13]);
//
//                if ( (novaMedicao.getState() & (1L << TOW_DECODED_MEASUREMENT_STATE_BIT) != 0) );
//
//
//                if (verificacaoStatus == 0){
////                    Log.e("StateEr","TOW not decoded!: " + verificacaoStatus);
//                    qntMedicoesDescartadas++;
//                    continue;
//                }else{
////                    Log.i("StateOk","TOW certo: " + verificacaoStatus);
//                }

//                Log.i("State", "Verificação: " + (Integer.parseInt(linhaRaw[13]) & (1L << TOW_DECODED_MEASUREMENT_STATE_BIT)));

                novaMedicao.setReceivedSvTimeNanos(Long.parseLong(linhaRaw[14]));
                novaMedicao.setReceivedSvTimeUncertaintyNanos(Double.parseDouble(linhaRaw[15]));

                if (novaMedicao.getReceivedSvTimeUncertaintyNanos() > 500){
                    qntMedicoesDescartadas++;
//                    Log.e("Raw","TimeUncertainty");
                    continue;
                }

                novaMedicao.setCn0DbHz(Double.parseDouble(linhaRaw[16]));

//                if (novaMedicao.getCn0DbHz() <= C_TO_N0_THRESHOLD_DB_HZ ){
//                    qntMedicoesDescartadas++;
////                    Log.e("Raw","Cn0DbHz");
//                    continue;
//                }else{
////                    Log.i("Cn0DbHzOk","Valor: " + String.valueOf(novaMedicao.getCn0DbHz()));
//                }

                if (novaMedicao.getCn0DbHz() >= C_TO_N0_THRESHOLD_DB_HZ
                        && (novaMedicao.getState() & (1L << TOW_DECODED_MEASUREMENT_STATE_BIT)) != 0){
                }else{
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

        Log.i("QntDescartadas","Quantidade de medidas descartadas: " + qntMedicoesDescartadas);
        Log.i("QntPreservadas","Quantidade de medidas preservadas: " + listaMedicoesOriginal.size());

        reader.close();
        return sb.toString();
    }

    /**
     * Calcula as pseudodistâncias em metros para cada <b>medição GPS</b> capturada.
     * <p>Essas pseudodistãncias preencherão o velor Lb no ajustamento paramétrico.</p>
     * @see ProcessamentoPPS#calcularMMQ()
     */
    public static void calcPseudoranges(){

//        //TODO LALALLAL FLAG 666
//        double AVERAGE_TRAVEL_TIME_SECONDS = 70.0e-3;
//        double SECONDS_PER_NANO = 1.0e-9;
//        double DOUBLE_ROUND_OFF_TOLERANCE = 0.0000000001;
//

//        long mLargestTowNs = Long.MIN_VALUE;

//        long mLargestTowNs = listaMedicoesOriginal.get(399).getReceivedSvTimeNanos();

        //fixme POR CAUSA DA FLAG 666
//        for (int i = 0; i < listaMedicoesOriginal.size(); i++) {

//            Long weekNumber =  Math.round(Math.floor(-listaMedicoesOriginal.get(i).getFullBiasNanos() * 1e-9 / GNSSConstants.WEEKSEC));
//            Long gpsSecsWek = Math.round((listaMedicoesOriginal.get(i).getReceivedSvTimeNanos()) * 1e-9);
//
//            GpsTime gpt = GpsTime.fromWeekTow(weekNumber.intValue(),gpsSecsWek.intValue());
//            gpt.getUtcDateTime();
//
//            listaMedicoesOriginal.get(i).setGpsWeek(weekNumber.intValue());
//
//            int year = gpt.getUtcDateTime().getYear() % 2000;
//            int month = gpt.getUtcDateTime().getMonthOfYear();
//            int day = gpt.getUtcDateTime().getDayOfMonth();
//            int hour = gpt.getUtcDateTime().getHourOfDay();
//            int minute = gpt.getUtcDateTime().getMinuteOfHour();
//            double seconds = gpt.getUtcDateTime().getSecondOfMinute();
//
//            if (listaMedicoesOriginal.get(i).getTimeNanos() == 168216752000000L){
//                long receivedGPSTowNs = listaMedicoesOriginal.get(i).getReceivedSvTimeNanos();
//
//                if (receivedGPSTowNs > mLargestTowNs) {
//                    mLargestTowNs = receivedGPSTowNs;
//                }
//                Log.i("Verificacao","Verifiquei");
//            }


//        }
//
//        double mArrivalTimeSinceGPSWeekNs = 0.0;
//        int mDayOfYear1To366 = 0;
//        int mGpsWeekNumber = 0;
//        long mArrivalTimeSinceGpsEpochNs = 0;
//        long mLargestTowNs = Long.MIN_VALUE;;
//
//        for (int i = 390; i <= 400; i++ ){
//            long receivedGPSTowNs = listaMedicoesOriginal.get(i).getReceivedSvTimeNanos();
//            if (receivedGPSTowNs > mLargestTowNs) {
//                mLargestTowNs = receivedGPSTowNs;
//            }
//        }
//
//        //TODO LALALALL FLAG 666
//
//        // mArrivalTimeSinceGpsEpochNs = gnssClock.getTimeNanos() - gnssClock.getFullBiasNanos();
//        //ISSO É UMA VEZ PARA TODO AS MEDICOES!!!!!!
//        mArrivalTimeSinceGpsEpochNs = listaMedicoesOriginal.get(389).getTimeNanos() -
//                listaMedicoesOriginal.get(389).getFullBiasNanos();


        int FLAG_OPCAO = 5;

        GNSSDate dataAnterior;
        EpocaGPS epocaanterior = null;

        for (int i = 0; i < listaMedicoesOriginal.size(); i++){ // FIXME
//            if (FLAG_OPCAO == 666) { // Implementação do GNSS Loogger
//
//                Long weekNumber =  Math.round(Math.floor(-listaMedicoesOriginal.get(i).getFullBiasNanos() * 1e-9 / GNSSConstants.WEEKSEC));
//                Long gpsSecsWek = Math.round((listaMedicoesOriginal.get(i).getReceivedSvTimeNanos()) * 1e-9);
//
//
//
//
//                // calculate day of year and Gps week number needed for the least square
//                GpsTime gpsTime = new GpsTime(mArrivalTimeSinceGpsEpochNs);
//                // Gps weekly epoch in Nanoseconds: defined as of every Sunday night at 00:00:000
//                long gpsWeekEpochNs = GpsTime.getGpsWeekEpochNano(gpsTime);
//                mArrivalTimeSinceGPSWeekNs = mArrivalTimeSinceGpsEpochNs - gpsWeekEpochNs;
//                mGpsWeekNumber = gpsTime.getGpsWeekSecond().first;
//                // calculate day of the year between 1 and 366
//                Calendar cal = gpsTime.getTimeInCalendar();
//                mDayOfYear1To366 = cal.get(Calendar.DAY_OF_YEAR);
//
//                long receivedGPSTowNs = listaMedicoesOriginal.get(i).getReceivedSvTimeNanos();
//                double deltai = mLargestTowNs - receivedGPSTowNs;
//
//                double pseudorangeMeters =
//                        (AVERAGE_TRAVEL_TIME_SECONDS + deltai * SECONDS_PER_NANO) * GNSSConstants.LIGHTSPEED;
//
//                listaMedicoesOriginal.get(i).setPseudorangeMeters(pseudorangeMeters); //FIXME !!!!!!!!!!!!!!!!!!
//
//                GpsTime gpt = GpsTime.fromWeekTow(weekNumber.intValue(),gpsSecsWek.intValue());
//                gpt.getUtcDateTime();
//
//                listaMedicoesOriginal.get(i).setGpsWeek(weekNumber.intValue());
//
//                int year = gpt.getUtcDateTime().getYear() % 2000;
//                int month = gpt.getUtcDateTime().getMonthOfYear();
//                int day = gpt.getUtcDateTime().getDayOfMonth();
//                int hour = gpt.getUtcDateTime().getHourOfDay();
//                int minute = gpt.getUtcDateTime().getMinuteOfHour();
//                double seconds = gpt.getUtcDateTime().getSecondOfMinute();
//
//                if (hour == 17 && minute == 9 && seconds == 56.0)
//                    Log.i("Achei", "Valor de i: " + i);
//
////                Log.i("gpsWEEK","Svid: " + listaMedicoesOriginal.get(i).getSvid() +
////                            "Semana: " + weekNumber.intValue() +
////                        " Segundos da semana: " + gpsSecsWek.intValue());
//
////                Log.i("gpsUTC", "Svid: " + listaMedicoesOriginal.get(i).getSvid() +
////                        " Hora: " + String.valueOf(hour) +
////                        " Minutos: " + String.valueOf(minute) +
////                        " Segundos: " + String.valueOf(seconds));
//
//                GNSSDate dataAtual = new GNSSDate(year, month, day, hour, minute, seconds);
//                listaMedicoesOriginal.get(i).setData(dataAtual);
////                Log.i("prr", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Pseudorange: " + listaMedicoesOriginal.get(i).getPseudorangeMeters() + " m");
////                Log.i("Uncertainty", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Uncertainty: " + listaMedicoesOriginal.get(i).getPseudoRangeUncertaintyMeters() + " m");
//                //FIXME !!!!!!!!!!
//                if (i == 0){
//                    dataAnterior = listaMedicoesOriginal.get(0).getData();
//                    epocaanterior = new EpocaGPS(dataAtual);
//                }else{
//                    dataAnterior = listaMedicoesOriginal.get(i-1).getData();
//                }
//                if (dataAtual.compareTo(dataAnterior) != 0){ // Início de uma nova época
//                    listaEpocas.add(epocaanterior); // Guarda a época anterior que já acabou e cria uma nova
//                    epocaanterior = new EpocaGPS(dataAtual);
//                    epocaanterior.setId(listaEpocas.size());
//                    epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
//                }else{ // Continua na mesma época
//                    epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
//                }
//
//            }
//
//            if (FLAG_OPCAO == 666) continue;

            if (FLAG_OPCAO == 5) {  // Implementação do matlab
                Long weekNumber =  Math.round(Math.floor(-listaMedicoesOriginal.get(i).getFullBiasNanos() * 1e-9 / GNSSConstants.WEEKSEC));
//                Long gpsSecsWek = Math.round((listaMedicoesOriginal.get(i).getReceivedSvTimeNanos()) * 1e-9);

                Long WEEKNANOS = Math.round(GNSSConstants.WEEKSEC*1e9);
                Long weekNumberNanos = Math.round(weekNumber)*Math.round(GNSSConstants.WEEKSEC*1e9);

                Long tRxNanos = listaMedicoesOriginal.get(i).getTimeNanos() -
                        listaMedicoesOriginal.get(0).getFullBiasNanos() - weekNumberNanos;

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

                    if (prS>maxBiasSeconds) {
                        Log.e("Week Rollover", "Failed to correct week rollover\n");
                    }
                    else{
                        prSeconds = prS;
                        tRxSeconds = tRxSeconds - delS;
                        Log.i("Week Rollover", "Corrected week rollover\n");
                    }
                }

                double pseudorange = prSeconds*GNSSConstants.LIGHTSPEED;
                double pseudorangeUncertaintyMeters = (double)(listaMedicoesOriginal.get(i).getReceivedSvTimeUncertaintyNanos())
                        *1e-9* GNSSConstants.LIGHTSPEED;

                listaMedicoesOriginal.get(i).setPseudorangeMeters(pseudorange);
                listaMedicoesOriginal.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);

                Long gpsSecsWek = Math.round(tRxSeconds);

//                Long tRxNanos = (listaMedicoesOriginal.get(i).getTimeNanos() + (int) listaMedicoesOriginal.get(i).getTimeOffsetNanos()) -
//                        (listaMedicoesOriginal.get(i).getFullBiasNanos() + (int) listaMedicoesOriginal.get(i).getBiasNanos()) -
//                        weekNumber * GNSSConstants.WEEKSEC *1000000000; // fixme
//
//                Double prMs = (tRxNanos - listaMedicoesOriginal.get(i).getReceivedSvTimeNanos()) * 1e-6;
//                Double prMeters = prMs * GNSSConstants.LIGHTSPEED * 1e-3;
//                Double pseudorangeUncertaintyMeters = listaMedicoesOriginal.get(i).getReceivedSvTimeUncertaintyNanos() * 1e-9 * GNSSConstants.LIGHTSPEED;
//
//                listaMedicoesOriginal.get(i).setPseudorangeMeters(prMeters);
//                listaMedicoesOriginal.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);

                GpsTime gpt = GpsTime.fromWeekTow(weekNumber.intValue(),gpsSecsWek.intValue());
                gpt.getUtcDateTime();

                listaMedicoesOriginal.get(i).setGpsWeek(weekNumber.intValue());

                int year = gpt.getUtcDateTime().getYear() % 2000;
                int month = gpt.getUtcDateTime().getMonthOfYear();
                int day = gpt.getUtcDateTime().getDayOfMonth();
                int hour = gpt.getUtcDateTime().getHourOfDay();
                int minute = gpt.getUtcDateTime().getMinuteOfHour();
                double seconds = gpt.getUtcDateTime().getSecondOfMinute();

//                if (hour == 17 && minute == 9 && seconds == 56.0)
//                    Log.i("Achei", "Valor de i: " + i);

                if (hour == 17 && minute == 9 && seconds == 20.0)
                    Log.i("Achei", "Valor de i: " + i);

//                Log.i("year_OBS",String.valueOf(year));
//                Log.i("month_OBS",String.valueOf(month));
//                Log.i("day_OBS",String.valueOf(day));
//                Log.i("hour_OBS",String.valueOf(hour));
//                Log.i("minute_OBS",String.valueOf(minute));
//                Log.i("seconds_OBS",String.valueOf(seconds));

//                Log.i("gpsWEEK","Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                            "Semana: " + weekNumber.intValue() +
//                        " Segundos da semana: " + gpsSecsWek.intValue());

//                Log.i("gpsUTC", "Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                        " Hora: " + String.valueOf(hour) +
//                        " Minutos: " + String.valueOf(minute) +
//                        " Segundos: " + String.valueOf(seconds));

                GNSSDate dataAtual = new GNSSDate(year, month, day, hour, minute, seconds);
                listaMedicoesOriginal.get(i).setData(dataAtual);
//                Log.i("prr", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Pseudorange: " + listaMedicoesOriginal.get(i).getPseudorangeMeters() + " m");
//                Log.i("Uncertainty", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Uncertainty: " + listaMedicoesOriginal.get(i).getPseudoRangeUncertaintyMeters() + " m");
                //FIXME !!!!!!!!!!
                if (i == 0){
                    dataAnterior = listaMedicoesOriginal.get(0).getData();
                    epocaanterior = new EpocaGPS(dataAtual);
                }else{
                    dataAnterior = listaMedicoesOriginal.get(i-1).getData();
                }

//                if (i == 20){
//                    Log.i("Teste","Teste");
//                }

                if (dataAtual.compareTo(dataAnterior) != 0){ // Início de uma nova época
                    listaEpocas.add(epocaanterior); // Guarda a época anterior que já acabou e cria uma nova
                    epocaanterior = new EpocaGPS(dataAtual);
                    epocaanterior.setId(listaEpocas.size());
                    epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
                }else{ // Continua na mesma época
                    epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
                }
            }

            if (FLAG_OPCAO == 5) continue;

            if (FLAG_OPCAO == 3){ // GSA 1

                Double NumberNanoSecondsWeek =  604800e9;

//                int INDEX_BIAS = 390;
                int INDEX_BIAS = 0;

                /**
                 * GPS Week Number within this week
                 * Aply gpsWeek % 1024 to get the week number in [0,1024]
                 */
                Long weekNumber =  Math.round(Math.floor(-listaMedicoesOriginal.get(i).getFullBiasNanos() * 1e-9 / GNSSConstants.WEEKSEC));
                Long gpsSecsWek = Math.round((listaMedicoesOriginal.get(i).getReceivedSvTimeNanos()) * 1e-9);

                Long tTx = listaMedicoesOriginal.get(i).getReceivedSvTimeNanos();
                Double tRxGNSS = listaMedicoesOriginal.get(i).getTimeNanos() + listaMedicoesOriginal.get(i).getTimeOffsetNanos()
                        - (listaMedicoesOriginal.get(INDEX_BIAS).getFullBiasNanos() + listaMedicoesOriginal.get(INDEX_BIAS).getBiasNanos());// FIXME

                Double weekNumberNanos = Math.floor( (- listaMedicoesOriginal.get(i).getFullBiasNanos() / NumberNanoSecondsWeek) )
                        * NumberNanoSecondsWeek;
                Double tRx = tRxGNSS - weekNumberNanos;

                Double pseudorange = (tRx - tTx)/1E9 * GNSSConstants.LIGHTSPEED;
                Double pseudorangeUncertaintyMeters = listaMedicoesOriginal.get(i).getReceivedSvTimeUncertaintyNanos() * 1e-9 * GNSSConstants.LIGHTSPEED;

                listaMedicoesOriginal.get(i).setPseudorangeMeters(pseudorange);
                listaMedicoesOriginal.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);

//                Long tRxNanos = (listaMedicoesOriginal.get(i).getTimeNanos() + (int) listaMedicoesOriginal.get(i).getTimeOffsetNanos()) -
//                        (listaMedicoesOriginal.get(i).getFullBiasNanos() + (int) listaMedicoesOriginal.get(i).getBiasNanos()) -
//                        weekNumber * GNSSConstants.WEEKSEC *1000000000; // fixme
//
//                Double prMs = (tRxNanos - listaMedicoesOriginal.get(i).getReceivedSvTimeNanos()) * 1e-6;
//                Double prMeters = prMs * GNSSConstants.LIGHTSPEED * 1e-3;
//                Double pseudorangeUncertaintyMeters = listaMedicoesOriginal.get(i).getReceivedSvTimeUncertaintyNanos() * 1e-9 * GNSSConstants.LIGHTSPEED;
//
//                listaMedicoesOriginal.get(i).setPseudorangeMeters(prMeters);
//                listaMedicoesOriginal.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);

                GpsTime gpt = GpsTime.fromWeekTow(weekNumber.intValue(),gpsSecsWek.intValue());
                gpt.getUtcDateTime();

                listaMedicoesOriginal.get(i).setGpsWeek(weekNumber.intValue());

                int year = gpt.getUtcDateTime().getYear() % 2000;
                int month = gpt.getUtcDateTime().getMonthOfYear();
                int day = gpt.getUtcDateTime().getDayOfMonth();
                int hour = gpt.getUtcDateTime().getHourOfDay();
                int minute = gpt.getUtcDateTime().getMinuteOfHour();
                double seconds = gpt.getUtcDateTime().getSecondOfMinute();

                if (hour == 17 && minute == 9 && seconds == 56.0)
                    Log.i("Achei", "Valor de i: " + i);

//                Log.i("year_OBS",String.valueOf(year));
//                Log.i("month_OBS",String.valueOf(month));
//                Log.i("day_OBS",String.valueOf(day));
//                Log.i("hour_OBS",String.valueOf(hour));
//                Log.i("minute_OBS",String.valueOf(minute));
//                Log.i("seconds_OBS",String.valueOf(seconds));

//                Log.i("gpsWEEK","Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                            "Semana: " + weekNumber.intValue() +
//                        " Segundos da semana: " + gpsSecsWek.intValue());

//                Log.i("gpsUTC", "Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                        " Hora: " + String.valueOf(hour) +
//                        " Minutos: " + String.valueOf(minute) +
//                        " Segundos: " + String.valueOf(seconds));

                GNSSDate dataAtual = new GNSSDate(year, month, day, hour, minute, seconds);
                listaMedicoesOriginal.get(i).setData(dataAtual);
//                Log.i("prr", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Pseudorange: " + listaMedicoesOriginal.get(i).getPseudorangeMeters() + " m");
//                Log.i("Uncertainty", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Uncertainty: " + listaMedicoesOriginal.get(i).getPseudoRangeUncertaintyMeters() + " m");
                //FIXME !!!!!!!!!!
                if (i == 0){
                    dataAnterior = listaMedicoesOriginal.get(0).getData();
                    epocaanterior = new EpocaGPS(dataAtual);
                }else{
                    dataAnterior = listaMedicoesOriginal.get(i-1).getData();
                }

//                if (i == 20){
//                    Log.i("Teste","Teste");
//                }

                if (dataAtual.compareTo(dataAnterior) != 0){ // Início de uma nova época
                    listaEpocas.add(epocaanterior); // Guarda a época anterior que já acabou e cria uma nova
                    epocaanterior = new EpocaGPS(dataAtual);
                    epocaanterior.setId(listaEpocas.size());
                    epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
                }else{ // Continua na mesma época
                    epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
                }
            }

            if (FLAG_OPCAO == 3) continue;


            if (FLAG_OPCAO == 1) {  // IMPLEMENTAÇÃO SEGUNDO A GSA
                double pseudorangeMeters = 0d;
                double pseudorangeUncertaintyMeters = 0d;

//            if (FLAG_OPCAO == 1){ // Implementação da GSA
//
//            }

                // Generate the measured time in full GNSS time
                Long tRx_GNSS = listaMedicoesOriginal.get(i).getTimeNanos() - (listaMedicoesOriginal.get(0).getFullBiasNanos() + Math.round(listaMedicoesOriginal.get(0).getBiasNanos())); // FIXME VER SE É LONG
                // Change the valid range from full GNSS to TOW
                Long tRx = tRx_GNSS % Math.round(WEEKSEC*1e9);
                // Generate the satellite time
                Long tTx = listaMedicoesOriginal.get(i).getReceivedSvTimeNanos() + Math.round(listaMedicoesOriginal.get(i).getTimeOffsetNanos());
                // Generate the pseudorange
                Long prMilliSeconds = (tRx - tTx);
                pseudorangeMeters = prMilliSeconds * GNSSConstants.LIGHTSPEED * 1e-9;
                pseudorangeUncertaintyMeters = (double) listaMedicoesOriginal.get(i).getReceivedSvTimeUncertaintyNanos() * 1e-9 * GNSSConstants.LIGHTSPEED;

                listaMedicoesOriginal.get(i).setPseudorangeMeters(pseudorangeMeters);
                listaMedicoesOriginal.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);

//                listaMedicoesOriginal.get(i).settTxSeconds(tTx); // FIXME RECÉM ALTERADO
//                listaMedicoesOriginal.get(i).settRxSeconds(tRx); // FIXME RECÉM ALTERADO

//            int year = Integer.valueOf(2017);
//            int month = Integer.valueOf(12);
//            int day = Integer.valueOf(13);


                //GPS Week number and Seconds within the week:
                int weekNumberr =  (int)Math.floor(-(double)(listaMedicoesOriginal.get(i).getFullBiasNanos()*1e-9/GNSSConstants.WEEKSEC));
                /**
                 * Aply gpsWeek % 1024 to get the week number in [0,1024]
                 */
                int gpsWeek = weekNumberr;// % 1024;
                Long gpsSecsWek = Math.round(tRx * 1e-9); // FIXME REVER
                //TESTE FIXME
//            gpsSecsWek = Math.round((listaMedicoes.get(i).getReceivedSvTimeNanos() + prMilliSeconds) * 1e-9);
                gpsSecsWek = Math.round((listaMedicoesOriginal.get(i).getReceivedSvTimeNanos()) * 1e-9);
                Log.i("gpsWeek","Semana: " + gpsWeek + " Segundos da semana: " + gpsSecsWek.intValue());

                GpsTime gpt = GpsTime.fromWeekTow(gpsWeek,gpsSecsWek.intValue());
                gpt.getUtcDateTime();

                listaMedicoesOriginal.get(i).setGpsWeek(gpsWeek);
//            int hour = Integer.valueOf(mLine.substring(12, 14).replaceAll("\\s", ""));
//            int minute = Integer.valueOf(mLine.substring(15, 17).replaceAll("\\s", ""));
//            double seconds = Double.valueOf(mLine.substring(18, 22).replaceAll("\\s", ""));
                int year = gpt.getUtcDateTime().getYear() % 2000;
                int month = gpt.getUtcDateTime().getMonthOfYear();
                int day = gpt.getUtcDateTime().getDayOfMonth();
                int hour = gpt.getUtcDateTime().getHourOfDay();
                int minute = gpt.getUtcDateTime().getMinuteOfHour();
                double seconds = gpt.getUtcDateTime().getSecondOfMinute();

//                Log.i("year_OBS",String.valueOf(year));
//                Log.i("month_OBS",String.valueOf(month));
//                Log.i("day_OBS",String.valueOf(day));
//                Log.i("hour_OBS",String.valueOf(hour));
//                Log.i("minute_OBS",String.valueOf(minute));
//                Log.i("seconds_OBS",String.valueOf(seconds));

                Log.i("hora-minuto", "Svid: " + listaMedicoesOriginal.get(i).getSvid() +
                        " Hora: " + String.valueOf(hour) + " Minuto: " + String.valueOf(minute));

                GNSSDate data = new GNSSDate(year, month, day, hour, minute, seconds);
                listaMedicoesOriginal.get(i).setData(data);

//                Log.i("tTx/tRx","Svid: " +  listaMedicoes.get(i).getSvid() + " tTx: " + tTx + " tRx: " + tRx + " Intervalo: " + prMilliSeconds);
//                Log.i("prr", "Svid: " +  listaMedicoes.get(i).getSvid() + " Pseudorange: " + listaMedicoes.get(i).getPseudorangeMeters() + " m");
//                Log.i("Uncertainty", "Svid: " +  listaMedicoes.get(i).getSvid() + " Uncertainty: " + listaMedicoes.get(i).getPseudoRangeUncertaintyMeters() + " m");

                GNSSDate dataAtual = new GNSSDate(year, month, day, hour, minute, seconds);
                listaMedicoesOriginal.get(i).setData(dataAtual);
//                Log.i("prr", "Svid: " +  listaMedicoes.get(i).getSvid() + " Pseudorange: " + listaMedicoes.get(i).getPseudorangeMeters() + " m");
//                Log.i("Uncertainty", "Svid: " +  listaMedicoes.get(i).getSvid() + " Uncertainty: " + listaMedicoes.get(i).getPseudoRangeUncertaintyMeters() + " m");
                //FIXME !!!!!!!!!!
                if (i == 0){
                    dataAnterior = listaMedicoesOriginal.get(0).getData();
                    epocaanterior = new EpocaGPS(dataAtual);
                }else{
                    dataAnterior = listaMedicoesOriginal.get(i-1).getData();
                }

//                if (i == 20){
//                    Log.i("Teste","Teste");
//                }

                if (dataAtual.compareTo(dataAnterior) != 0){ // Início de uma nova época
                    listaEpocas.add(epocaanterior); // Guarda a época anterior que já acabou e cria uma nova
                    epocaanterior = new EpocaGPS(dataAtual);
                    epocaanterior.setId(i + 1);
                    epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
                }else{ // Continua na mesma época
                    epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
                }

            }else{ // IMPLEMENTAÇÃO SEGUNDO A PLANILHA
                /**
                 * GPS Week Number within this week
                 * Aply gpsWeek % 1024 to get the week number in [0,1024]
                 */
                Long weekNumber =  Math.round(Math.floor(-listaMedicoesOriginal.get(i).getFullBiasNanos() * 1e-9 / GNSSConstants.WEEKSEC));
                Long gpsSecsWek = Math.round((listaMedicoesOriginal.get(i).getReceivedSvTimeNanos()) * 1e-9);

                Long tRxNanos = (listaMedicoesOriginal.get(i).getTimeNanos() + (int) listaMedicoesOriginal.get(i).getTimeOffsetNanos()) -
                                (listaMedicoesOriginal.get(i).getFullBiasNanos() + (int) listaMedicoesOriginal.get(i).getBiasNanos()) -
                                weekNumber * GNSSConstants.WEEKSEC *1000000000; // fixme

                Double prMs = (tRxNanos - listaMedicoesOriginal.get(i).getReceivedSvTimeNanos()) * 1e-6;
                Double prMeters = prMs * GNSSConstants.LIGHTSPEED * 1e-3;
                Double pseudorangeUncertaintyMeters = listaMedicoesOriginal.get(i).getReceivedSvTimeUncertaintyNanos() * 1e-9 * GNSSConstants.LIGHTSPEED;

                listaMedicoesOriginal.get(i).setPseudorangeMeters(prMeters);
                listaMedicoesOriginal.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);

                GpsTime gpt = GpsTime.fromWeekTow(weekNumber.intValue(),gpsSecsWek.intValue());
                gpt.getUtcDateTime();

                listaMedicoesOriginal.get(i).setGpsWeek(weekNumber.intValue());

                int year = gpt.getUtcDateTime().getYear() % 2000;
                int month = gpt.getUtcDateTime().getMonthOfYear();
                int day = gpt.getUtcDateTime().getDayOfMonth();
                int hour = gpt.getUtcDateTime().getHourOfDay();
                int minute = gpt.getUtcDateTime().getMinuteOfHour();
                double seconds = gpt.getUtcDateTime().getSecondOfMinute();

//                Log.i("year_OBS",String.valueOf(year));
//                Log.i("month_OBS",String.valueOf(month));
//                Log.i("day_OBS",String.valueOf(day));
//                Log.i("hour_OBS",String.valueOf(hour));
//                Log.i("minute_OBS",String.valueOf(minute));
//                Log.i("seconds_OBS",String.valueOf(seconds));

//                Log.i("gpsWEEK","Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                            "Semana: " + weekNumber.intValue() +
//                        " Segundos da semana: " + gpsSecsWek.intValue());

//                Log.i("gpsUTC", "Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                        " Hora: " + String.valueOf(hour) +
//                        " Minutos: " + String.valueOf(minute) +
//                        " Segundos: " + String.valueOf(seconds));

                GNSSDate dataAtual = new GNSSDate(year, month, day, hour, minute, seconds);
                listaMedicoesOriginal.get(i).setData(dataAtual);
//                Log.i("prr", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Pseudorange: " + listaMedicoesOriginal.get(i).getPseudorangeMeters() + " m");
//                Log.i("Uncertainty", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Uncertainty: " + listaMedicoesOriginal.get(i).getPseudoRangeUncertaintyMeters() + " m");
                //FIXME !!!!!!!!!!
                if (i == 0){
                    dataAnterior = listaMedicoesOriginal.get(0).getData();
                    epocaanterior = new EpocaGPS(dataAtual);
                }else{
                    dataAnterior = listaMedicoesOriginal.get(i-1).getData();
                }

//                if (i == 20){
//                    Log.i("Teste","Teste");
//                }

                if (dataAtual.compareTo(dataAnterior) != 0){ // Início de uma nova época
                    listaEpocas.add(epocaanterior); // Guarda a época anterior que já acabou e cria uma nova
                    epocaanterior = new EpocaGPS(dataAtual);
                    epocaanterior.setId(listaEpocas.size());
                    epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
                }else{ // Continua na mesma época
                    epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
                }
            }
        }
        int qntEpocas = listaEpocas.size();

        for (int i = 0; i < listaEpocas.size(); i++){
            if (listaEpocas.get(i).getNumSatelites() >= 5 )
                Log.i("EpocaS",listaEpocas.get(i).toString() + "\n--------------------------------");
        }

        Log.i("FimPr","Fim do cálculo das pseudodistâncias");
        Log.i("FimPr","Foram encontradas " + qntEpocas + " épocas!");
    }

    public static void calcPseudorangesMatlab(){

        GNSSDate dataAnterior;
        Double FctSeconsAnterior;
        EpocaGPS epocaanterior = null;

        ArrayList<Double> ListaFctSeconds = new ArrayList<>();
        ArrayList<Integer> ListaSvIds = new ArrayList<>();

        //listaMedicoesOriginal.size();

//        DateTime agora = DateTime.now(DateTimeZone.UTC);
//        GpsTime agoraGPS = GpsTime.fromUtc(agora);

        for (int i = 0; i < listaMedicoesOriginal.size(); i++){
            Double allRxMilliseconds = (double) listaMedicoesOriginal.get(i).getAllRxMilliseconds();
            Double FctSecondsAtual = allRxMilliseconds * 1e-3;

            listaMedicoesOriginal.get(i).setFctSeconds(FctSecondsAtual);

//            if ( ! FctSeconds.contains(FctSecondsAtual) ) { // Armazena apenas Fct Unicos
//                FctSeconds.add(FctSecondsAtual);
//            }
//
//            if ( ! SvIds.contains(listaMedicoesOriginal.get(i).getSvid()) ) { // Armazena apenas SvIds Unicos
//                SvIds.add(listaMedicoesOriginal.get(i).getSvid());
//            }

            //FIXME EEEEEEEE
            Long weekNumber =  Math.round(Math.floor(-listaMedicoesOriginal.get(i).getFullBiasNanos() * 1e-9 / GNSSConstants.WEEKSEC));
//                Long gpsSecsWek = Math.round((listaMedicoesOriginal.get(i).getReceivedSvTimeNanos()) * 1e-9);

//            Long WEEKNANOS = Math.round(GNSSConstants.WEEKSEC*1e9);
            Long weekNumberNanos = Math.round(weekNumber) * Math.round(GNSSConstants.WEEKSEC*1e9);

            Long tRxNanos = listaMedicoesOriginal.get(i).getTimeNanos() -
                    listaMedicoesOriginal.get(0).getFullBiasNanos() - weekNumberNanos;

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
                    Log.i("Week Rollover", "Corrected week rollover\n");
                }
            }

            double pseudorange = prSeconds*GNSSConstants.LIGHTSPEED;
            double pseudorangeUncertaintyMeters = (double)(listaMedicoesOriginal.get(i).getReceivedSvTimeUncertaintyNanos())
                    *1e-9* GNSSConstants.LIGHTSPEED;

            listaMedicoesOriginal.get(i).setPseudorangeMeters(pseudorange);
            listaMedicoesOriginal.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);
            listaMedicoesOriginal.get(i).setGpsWeek(weekNumber.intValue());
            listaMedicoesOriginal.get(i).settTxSeconds(tTxSeconds);
            listaMedicoesOriginal.get(i).settRxSeconds(tRxSeconds);

            if (i == 0){
                FctSeconsAnterior = listaMedicoesOriginal.get(0).getFctSeconds();
                epocaanterior = new EpocaGPS(FctSecondsAtual);
            }else{
                FctSeconsAnterior = listaMedicoesOriginal.get(i-1).getFctSeconds();
            }


            int truncado = truncateSafely(tRxSeconds);

            GpsTime tempo = GpsTime.fromWeekTow(weekNumber.intValue(),truncado);
//            Log.i("Tempo convertido","Truncado: " + truncado + " == "
//                    + tempo.getGpsDateTime().toString());


            //FIXME @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            int year = tempo.getGpsDateTime().getYear() % 2000;
            int month = tempo.getGpsDateTime().getMonthOfYear();
            int day_month = tempo.getGpsDateTime().getDayOfMonth();
            int day_week = tempo.getGpsDateTime().getDayOfWeek();
            int hour = tempo.getGpsDateTime().getHourOfDay();
            int minute = tempo.getGpsDateTime().getMinuteOfHour();
            double seconds = tempo.getGpsDateTime().getSecondOfMinute();

//            Log.i("hora-minuto", "Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                    " Hora: " + String.valueOf(hour) + " Minuto: " + String.valueOf(minute));

//                if (hour == 17 && minute == 9 && seconds == 56.0)
//                    Log.i("Achei", "Valor de i: " + i);

//            if (hour == 17 && minute == 9 && seconds == 38.0)
//                Log.i("Achei", "Valor de i: " + i);

//                Log.i("year_OBS",String.valueOf(year));
//                Log.i("month_OBS",String.valueOf(month));
//                Log.i("day_OBS",String.valueOf(day));
//                Log.i("hour_OBS",String.valueOf(hour));
//                Log.i("minute_OBS",String.valueOf(minute));
//                Log.i("seconds_OBS",String.valueOf(seconds));

//                Log.i("gpsWEEK","Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                            "Semana: " + weekNumber.intValue() +
//                        " Segundos da semana: " + gpsSecsWek.intValue());

//                Log.i("gpsUTC", "Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                        " Hora: " + String.valueOf(hour) +
//                        " Minutos: " + String.valueOf(minute) +
//                        " Segundos: " + String.valueOf(seconds));

            GNSSDate dataAtual = new GNSSDate(year, month, day_month, hour, minute, seconds);
            dataAtual.setDay_week(day_week);
            listaMedicoesOriginal.get(i).setData(dataAtual);
//                Log.i("prr", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Pseudorange: " + listaMedicoesOriginal.get(i).getPseudorangeMeters() + " m");
//                Log.i("Uncertainty", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Uncertainty: " + listaMedicoesOriginal.get(i).getPseudoRangeUncertaintyMeters() + " m");
            //FIXME !!!!!!!!!!
//            if (i == 0){
//                dataAnterior = listaMedicoesOriginal.get(0).getData();
//                epocaanterior = new EpocaGPS(dataAtual);
//            }else{
//                dataAnterior = listaMedicoesOriginal.get(i-1).getData();
//            }

//                if (i == 20){
//                    Log.i("Teste","Teste");
//                }

            if (!FctSeconsAnterior.equals(FctSecondsAtual)){ // Início de uma nova época
                epocaanterior.setGPSweekNumber(weekNumber.intValue());
                epocaanterior.setGPSsecondsWeek(truncado); // FIXME REVER
                epocaanterior.setData(dataAtual); // FIXME
                listaEpocas.add(epocaanterior); // Guarda a época anterior que já acabou e cria uma nova
                epocaanterior = new EpocaGPS(FctSecondsAtual);
                epocaanterior.setId(listaEpocas.size());
                epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
                epocaanterior.addMedicao(listaMedicoesOriginal.get(i));
            }else{ // Continua na mesma época
                epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
                epocaanterior.addMedicao(listaMedicoesOriginal.get(i));
            }

        }

        Log.i("Pr","Fim implementacao Matlab");
        // JUNTAR EM EPOCAS TODO

        int qntEpocas = listaEpocas.size();

        for (int i = 0; i < listaEpocas.size(); i++){
            if (listaEpocas.get(i).getNumSatelites() >= 5 )
                Log.i("EpocaS",listaEpocas.get(i).toString() + "\n--------------------------------");
        }

        Log.i("FimPr","Fim do cálculo das pseudodistâncias segundo o MATLAB");
        Log.i("FimPr","Foram encontradas " + qntEpocas + " épocas!");

    }


    public static void calcPseudorangesMatlab2222222(){

        Double allRxSecondsAnterior;

        GNSSDate dataAnterior;
        Double FctSeconsAnterior;
        EpocaGPS epocaanterior = null;

        ArrayList<Double> ListaFctSeconds = new ArrayList<>();
//        ArrayList<Integer> ListaSvIds = new ArrayList<>();
        ArrayList<Double> listaAllRxSeconds = new ArrayList<>();
        ArrayList<Long> listaAllRxNanos = new ArrayList<>();

        //listaMedicoesOriginal.size();

//        DateTime agora = DateTime.now(DateTimeZone.UTC);
//        GpsTime agoraGPS = GpsTime.fromUtc(agora);

        int INDEX_BIAS = 0;

        for (int i = 0; i < listaMedicoesOriginal.size(); i++){
            Double allRxMilliseconds = (double) listaMedicoesOriginal.get(i).getAllRxMillis();
                        listaMedicoesOriginal.get(i).setAllRxMilliseconds(allRxMilliseconds);

            Double FctSecondsAtual = allRxMilliseconds * 1e-3;
            listaMedicoesOriginal.get(i).setFctSeconds(FctSecondsAtual); // FIXME FctSeconds!
            if (!ListaFctSeconds.contains(FctSecondsAtual)) { // Inicio de uma nova época
                ListaFctSeconds.add(FctSecondsAtual);

                INDEX_BIAS = i; // FIXME REVERRRRRRRRRRRRRRRRRRRRRR!!!!!!!
            }

            Long allRxNanosAtual = (listaMedicoesOriginal.get(i).getTimeNanos() -
                    listaMedicoesOriginal.get(i).getFullBiasNanos());
            listaMedicoesOriginal.get(i).setAllRxNanos(allRxNanosAtual); // FIXME allRxNanos!
            if (!listaAllRxNanos.contains(allRxNanosAtual)) { // Inicio de uma nova época
                listaAllRxNanos.add(allRxNanosAtual);

                INDEX_BIAS = i; // FIXME REVERRRRRRRRRRRRRRRRRRRRRR!!!!!!!
            }

            Double allRxSecondsAtual = (listaMedicoesOriginal.get(i).getTimeNanos() -
                    listaMedicoesOriginal.get(i).getFullBiasNanos()) * 1e-9; // FIXME allRxSeconds!
            listaMedicoesOriginal.get(i).setAllRxSeconds(allRxSecondsAtual);  // Inicio de uma nova época
            if (!listaAllRxSeconds.contains(allRxSecondsAtual)) { // Inicio de uma nova época
                listaAllRxSeconds.add(allRxSecondsAtual);

                INDEX_BIAS = i; // FIXME REVERRRRRRRRRRRRRRRRRRRRRR!!!!!!!
            }

//            int INDEX_BIAS = 3188;

//            if (allRxSecondsatual == 1.2125132757127006E9){
//                Log.i("Achei","allalalaa " + i);
//                INDEX_BIAS = i;
//            }

//            if ( ! FctSeconds.contains(FctSecondsAtual) ) { // Armazena apenas Fct Unicos
//                FctSeconds.add(FctSecondsAtual);
//            }
//

            Long weekNumber =  Math.round(Math.floor(-listaMedicoesOriginal.get(i).getFullBiasNanos() * 1e-9 / GNSSConstants.WEEKSEC));
            Long weekNumberNanos = Math.round(weekNumber) * Math.round(GNSSConstants.WEEKSEC*1e9);

            Long tRxNanos = listaMedicoesOriginal.get(i).getTimeNanos() -
                    listaMedicoesOriginal.get(INDEX_BIAS).getFullBiasNanos() - weekNumberNanos; // FIXME

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
                    Log.i("Week Rollover", "Corrected week rollover\n");
                }
            }

            double pseudorange = prSeconds*GNSSConstants.LIGHTSPEED;
            double pseudorangeUncertaintyMeters = (double)(listaMedicoesOriginal.get(i).getReceivedSvTimeUncertaintyNanos())
                    *1e-9* GNSSConstants.LIGHTSPEED;

            listaMedicoesOriginal.get(i).setPseudorangeMeters(pseudorange);
            listaMedicoesOriginal.get(i).setPseudoRangeUncertaintyMeters(pseudorangeUncertaintyMeters);
            listaMedicoesOriginal.get(i).setGpsWeek(weekNumber.intValue());
            listaMedicoesOriginal.get(i).settTxSeconds(tTxSeconds);
            listaMedicoesOriginal.get(i).settRxSeconds(tRxSeconds);

//            if (i == 0){
////                FctSeconsAnterior = listaMedicoesOriginal.get(0).getFctSeconds();
//                allRxSecondsAnterior = (listaMedicoesOriginal.get(0).getTimeNanos() -
//                                        listaMedicoesOriginal.get(0).getFullBiasNanos()) * 1e-9;
//                epocaanterior = new EpocaGPS(allRxSecondsAnterior);
//            }else{
////                allRxSecondsAnterior = listaMedicoesOriginal.get(i-1).getFctSeconds();
//            }
//

//            int truncado = truncateSafely(tRxSeconds);

//            GpsTime tempo = GpsTime.fromWeekTow(weekNumber.intValue(),truncado);
////            Log.i("Tempo convertido","Truncado: " + truncado + " == "
////                    + tempo.getGpsDateTime().toString());
//
//
//            //FIXME @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//            int year = tempo.getGpsDateTime().getYear() % 2000;
//            int month = tempo.getGpsDateTime().getMonthOfYear();
//            int day_month = tempo.getGpsDateTime().getDayOfMonth();
//            int day_week = tempo.getGpsDateTime().getDayOfWeek();
//            int hour = tempo.getGpsDateTime().getHourOfDay();
//            int minute = tempo.getGpsDateTime().getMinuteOfHour();
//            double seconds = tempo.getGpsDateTime().getSecondOfMinute();

//            Log.i("hora-minuto", "Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                    " Hora: " + String.valueOf(hour) + " Minuto: " + String.valueOf(minute));

//                if (hour == 17 && minute == 9 && seconds == 56.0)
//                    Log.i("Achei", "Valor de i: " + i);

//            if (hour == 17 && minute == 9 && seconds == 38.0)
//                Log.i("Achei", "Valor de i: " + i);

//                Log.i("year_OBS",String.valueOf(year));
//                Log.i("month_OBS",String.valueOf(month));
//                Log.i("day_OBS",String.valueOf(day));
//                Log.i("hour_OBS",String.valueOf(hour));
//                Log.i("minute_OBS",String.valueOf(minute));
//                Log.i("seconds_OBS",String.valueOf(seconds));

//                Log.i("gpsWEEK","Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                            "Semana: " + weekNumber.intValue() +
//                        " Segundos da semana: " + gpsSecsWek.intValue());

//                Log.i("gpsUTC", "Svid: " + listaMedicoesOriginal.get(i).getSvid() +
//                        " Hora: " + String.valueOf(hour) +
//                        " Minutos: " + String.valueOf(minute) +
//                        " Segundos: " + String.valueOf(seconds));

//            GNSSDate dataAtual = new GNSSDate(year, month, day_month, hour, minute, seconds);
//            dataAtual.setDay_week(day_week);
//            listaMedicoesOriginal.get(i).setData(dataAtual);
//                Log.i("prr", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Pseudorange: " + listaMedicoesOriginal.get(i).getPseudorangeMeters() + " m");
//                Log.i("Uncertainty", "Svid: " +  listaMedicoesOriginal.get(i).getSvid() + " Uncertainty: " + listaMedicoesOriginal.get(i).getPseudoRangeUncertaintyMeters() + " m");
            //FIXME !!!!!!!!!!
//            if (i == 0){
//                dataAnterior = listaMedicoesOriginal.get(0).getData();
//                epocaanterior = new EpocaGPS(dataAtual);
//            }else{
//                dataAnterior = listaMedicoesOriginal.get(i-1).getData();
//            }

//                if (i == 20){
//                    Log.i("Teste","Teste");
//                }

//            if (!FctSeconsAnterior.equals(FctSecondsAtual)){ // Início de uma nova época
//                epocaanterior.setGPSweekNumber(weekNumber.intValue());
//                epocaanterior.setGPSsecondsWeek(truncado); // FIXME REVER
//                epocaanterior.setData(dataAtual); // FIXME
//                listaEpocas.add(epocaanterior); // Guarda a época anterior que já acabou e cria uma nova
//                epocaanterior = new EpocaGPS(FctSecondsAtual);
//                epocaanterior.setId(listaEpocas.size());
//                epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
//                epocaanterior.addMedicao(listaMedicoesOriginal.get(i));
//            }else{ // Continua na mesma época
//                epocaanterior.addSatelitePRN(listaMedicoesOriginal.get(i).getSvid());
//                epocaanterior.addMedicao(listaMedicoesOriginal.get(i));
//            }

        }

//        Log.i("Pr","Fim do calculo de pseudoranges igual o MATLAB");

//        ArrayList<Double> listaAllRxSeconds = new ArrayList<>();
//
//        for (int i = 0; i < listaMedicoesOriginal.size(); i++) { // FIXME
//            Double AllRxSecondsAtuall = listaMedicoesOriginal.get(i).getAllRxSeconds();
//
//            if (!listaAllRxSeconds.contains(AllRxSecondsAtuall)) { // Inicio de uma nova época
//                listaAllRxSeconds.add(AllRxSecondsAtuall);
//            }
//        }

//        double AVERAGE_TRAVEL_TIME_SECONDS = 70.0e-3;
//        double SECONDS_PER_NANO = 1.0e-9;
//        double SPEED_OF_LIGHT_MPS = 299792458.0;

        for (int i = 0; i < listaAllRxSeconds.size(); i++) { // FIXME
            Double AllRxSecondsAtual = listaAllRxSeconds.get(i); // TODO CADA i É UMA ÉPOCA
            EpocaGPS novaEpoca = new EpocaGPS(AllRxSecondsAtual); // rever

//            long mLargestTowNs = Long.MIN_VALUE;

            for (int j = 0; j < listaMedicoesOriginal.size(); j++) {
                // A medição pertence à época
                if (listaMedicoesOriginal.get(j).getAllRxSeconds().equals
                        (AllRxSecondsAtual)){ // TODOO CONDIÇÃO DE MESMA EPOCA
//
//                Double timeTag = Math.abs(FctSecondsAtual * 1e3 -
//                        listaMedicoesOriginal.get(i).getAllRxMilliseconds());
//                if (timeTag < 1){
//
                    if (novaEpoca.getNumMedicoes() == 0){ // Primeira medição da época
                        Long mArrivalTimeSinceGpsEpochNs = listaMedicoesOriginal.get(j).getAllRxNanos();

                        GpsTime gpsTime = new GpsTime(mArrivalTimeSinceGpsEpochNs);
                        long gpsWeekEpochNs = GpsTime.getGpsWeekEpochNano(gpsTime);
                        double mArrivalTimeSinceGPSWeekNs = mArrivalTimeSinceGpsEpochNs - gpsWeekEpochNs;
                        int mGpsWeekNumber = gpsTime.getGpsWeekSecond().first;
                        // calculate day of the year between 1 and 366
                        Calendar cal = gpsTime.getTimeInCalendar();
                        int mDayOfYear1To366 = cal.get(Calendar.DAY_OF_YEAR);

                        int year = gpsTime.getGpsDateTime().getYear() % 2000;
                        int month = gpsTime.getGpsDateTime().getMonthOfYear();
                        int day_month = gpsTime.getGpsDateTime().getDayOfMonth();
                        int day_week = gpsTime.getGpsDateTime().getDayOfWeek();
                        int hour = gpsTime.getGpsDateTime().getHourOfDay();
                        int minute = gpsTime.getGpsDateTime().getMinuteOfHour();
                        double seconds = gpsTime.getGpsDateTime().getSecondOfMinute();

                        GpsTime TESTE = GpsTime.fromWeekTow(mGpsWeekNumber,
                                (int)(listaMedicoesOriginal.get(j).getReceivedSvTimeNanos() * 1e-9));

                        DateTime TESTE2 = TESTE.getGpsDateTime();

                        GNSSDate dataAtual = new GNSSDate(year, month, day_month, hour, minute, seconds);
                        dataAtual.setDay_week(day_week);
                        novaEpoca.setData(dataAtual);
                        novaEpoca.setId(listaEpocas.size());
                    }
//                    long receivedGPSTowNs = listaMedicoesOriginal.get(j).getReceivedSvTimeNanos();
//                    if (receivedGPSTowNs > mLargestTowNs) {
//                        mLargestTowNs = receivedGPSTowNs;
//                    }
                    if (novaEpoca.addSatelitePRN(listaMedicoesOriginal.get(j).getSvid())){
                        novaEpoca.addMedicao(listaMedicoesOriginal.get(j));
                    }
                }
            }
//            novaEpoca.setmLargestTowNs(mLargestTowNs);
//
//            // Recalculando as pseudodistancias
//
//            for (int k = 0; k < novaEpoca.getNumMedicoes(); k++){
//                double deltai = novaEpoca.getmLargestTowNs() -
//                        novaEpoca.getListaMedicoes().get(k).getReceivedSvTimeNanos();
//                double pseudorangeMeters =
//                        (AVERAGE_TRAVEL_TIME_SECONDS + deltai * SECONDS_PER_NANO) * SPEED_OF_LIGHT_MPS;
//                novaEpoca.getListaMedicoes().get(k).setPseudorangeMeters(pseudorangeMeters);
//
//            }
            listaEpocas.add(novaEpoca);
//            if (!listaAllRxSeconds.contains(AllRxSecondsAtuall)){ // Inicio de uma nova época
//                listaAllRxSeconds.add(AllRxSecondsAtuall);
//
//
//                for (int j = 0; j < listaMedicoesOriginal.size(); j++){
//                    try {
//                        if (listaMedicoesOriginal.get(j).getAllRxSeconds().equals(AllRxSecondsAtuall)) {
//
//
//                        }
//                    }catch (NullPointerException e) {
//                        Log.e("ErroNULL", "Erro na posicao X: " + j); // FIXME
//                        break; // FIXME
//                    }
//                }
//                listaEpocas.add(novaEpoca);
//                break; // FIXME
//            }
        }

//        int qntEpocas = listaEpocas.size();
//        int qntEpocasMaior5 = 0;
//        for (int i = 0; i < listaEpocas.size(); i++){
//            if (listaEpocas.get(i).getNumSatelites() >= 5 ){
//                qntEpocasMaior5++;
//                Log.i("Epocas",listaEpocas.get(i).toString() + "\n--------------------------------");
//            }
//
//        }
        Log.i("FimPr","Fim do cálculo das pseudodistâncias MATLAB!!!!!!!!!!");
//        Log.i("FimPr","Foram encontradas " + qntEpocas + " épocas!");
//        Log.i("FimPr","Foram encontradas " + qntEpocasMaior5 + " épocas com 5+ sats!");
    }


    /**
     * Ajusta as medições GNSS (pseudodistancias) e as efemérides transmitidas (dados de navegação) para pertencer a mesma época.
     * <p> Elimina as medições e efemérides de outra época e mantem apenas as da época em análise.</p>
     * <p>
     *     Tudo dentro de uma mesmo UTC é considerado a mesma época.
     * </p>
     *@return A data para a época considerada no ajustamento.
     */
    public static EpocaGPS escolherEpoca(int INDEX_ANALISE){
        /**
         * DEFINIÇÃO MANUAL DA DATA DO RINEX:
         */
        int YEAR = 18; // FIXME RINEX
        int MONTH = 6; // FIXME RINEX
        int DAY_MONTH = 8; // FIXME RINEX
        //int DAY_WEEK = GNSSConstants.DAY_SEX; // FIXME RINEX
        int HOUR_DAY = 16; // FIXME RINEX
        int MIN_HOUR = 0; // FIXME RINEX
        double SEC = 0.0; // FIXME RINEX

        GNSSDate dataRINEX = new GNSSDate(YEAR,MONTH,DAY_MONTH,HOUR_DAY,MIN_HOUR,SEC);

        EpocaGPS epocaEmAnalise = listaEpocas.get(INDEX_ANALISE);

//EXCLUSOES TESTE INDEX_ANALISE == 0
//        epocaEmAnalise.excluirSatelitePRN(1); //EXCLUIDO
        epocaEmAnalise.excluirSatelitePRN(7); // MANTIDO
        epocaEmAnalise.excluirSatelitePRN(11); // TIRADO 3º
        epocaEmAnalise.excluirSatelitePRN(18); // TIRADO 2º
        epocaEmAnalise.excluirSatelitePRN(30); // TIRADO 1º

        qntSatProcessar = epocaEmAnalise.getNumSatelites(); // FIXME

        Log.i("epocaAnalise","||||||||||||||||||||||||||||||\n");
        Log.i("epocaAnalise",epocaEmAnalise.toString());
        Log.i("epocaAnalise","||||||||||||||||||||||||||||||\n");

//        ArrayList<GNSSMeasurement> listaMedicoes2 = new ArrayList<>();
//        ArrayList<GNSSNavMsg> listaEfemerides2 = new ArrayList<>();
//        A seleção da época é feita de modo manual:
//        Descarta as observações fora da época
//        int cont = 0;
//        int j = 0;
//        try{
//            do{
//                if ( (listaMedicoesOriginal.get(j).getData().compareTo(epocaEmAnalise.getDateUTC()) == 0) && // Mesma época
//                        epocaEmAnalise.containsSatellite(listaMedicoesOriginal.get(j).getSvid()) )
//                { // Satélite da Mesma Época
////                    Log.i("TimeNanosUtilizado: ","TimeNanos" + String.valueOf(listaMedicoesOriginal.get(j).getTimeNanos().toString()));
//                    listaMedicoesAtual.add(listaMedicoesOriginal.get(j));
//                    cont++;
//                }
//                j++;
//            } while (cont < qntSatProcessar);
//        }catch (IndexOutOfBoundsException e){
//            e.printStackTrace();
//            Log.e("Index","Erro nas medições: " + e.getMessage());
//        }

        for (int i = 0; i < epocaEmAnalise.getListaMedicoes().size(); i++) {
            listaMedicoesAtual.add(epocaEmAnalise.getListaMedicoes().get(i));
        }

//        listaMedicoesOriginal = null;
//        listaMedicoesOriginal = listaMedicoes2; // FIXME VAI PERDER AS ORIGINAIS

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
            } while (cont < qntSatProcessar);
        }catch (IndexOutOfBoundsException e){
            Log.e("Index","Erro nas efemérides: " + e.getMessage());
            e.printStackTrace();
        }

        Collections.sort(listaMedicoesAtual);
        Collections.sort(listaEfemeridesAtual);

        return epocaEmAnalise;
    }

    public static ArrayList<Double> processar_todas_epocas(){
        ArrayList<Double> resultadosMMQ = new ArrayList<>();

        /**
         * DEFINIÇÃO MANUAL DA DATA DO RINEX:
         */
        int YEAR = 18; // FIXME RINEX
        int MONTH = 6; // FIXME RINEX
        int DAY_MONTH = 8; // FIXME RINEX
        //int DAY_WEEK = GNSSConstants.DAY_SEX; // FIXME RINEX
        int HOUR_DAY = 16; // FIXME RINEX
        int MIN_HOUR = 0; // FIXME RINEX
        double SEC = 0.0; // FIXME RINEX

        GNSSDate dataRINEX = new GNSSDate(YEAR,MONTH,DAY_MONTH,HOUR_DAY,MIN_HOUR,SEC);

        for (int i = 0; i < listaEpocas.size(); i++){
            int INDEX_ANALISE = i; //TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//        EpocaGPS epocaEmAnalise = listaEpocas.get(INDEX_ANALISE);

            EpocaGPS epocaEmAnalise = listaEpocas.get(INDEX_ANALISE);

//EXCLUSOES TESTE INDEX_ANALISE == 0
//        epocaEmAnalise.excluirSatelitePRN(1); //EXCLUIDO
//        epocaEmAnalise.excluirSatelitePRN(7); // MANTIDO
//        epocaEmAnalise.excluirSatelitePRN(11); //EXCLUIDO
//        epocaEmAnalise.excluirSatelitePRN(18); // MANTIDO
//            epocaEmAnalise.excluirSatelitePRN(30); // MANTIDO

            qntSatProcessar = epocaEmAnalise.getNumSatelites(); // FIXME

//            Log.i("epocaAnalise","||||||||||||||||||||||||||||||\n");
//            Log.i("epocaAnalise",epocaEmAnalise.toString());
//            Log.i("epocaAnalise","||||||||||||||||||||||||||||||\n");

//        ArrayList<GNSSMeasurement> listaMedicoes2 = new ArrayList<>();
//        ArrayList<GNSSNavMsg> listaEfemerides2 = new ArrayList<>();
//        A seleção da época é feita de modo manual:
//        Descarta as observações fora da época
//        int cont = 0;
//        int j = 0;
//        try{
//            do{
//                if ( (listaMedicoesOriginal.get(j).getData().compareTo(epocaEmAnalise.getDateUTC()) == 0) && // Mesma época
//                        epocaEmAnalise.containsSatellite(listaMedicoesOriginal.get(j).getSvid()) )
//                { // Satélite da Mesma Época
////                    Log.i("TimeNanosUtilizado: ","TimeNanos" + String.valueOf(listaMedicoesOriginal.get(j).getTimeNanos().toString()));
//                    listaMedicoesAtual.add(listaMedicoesOriginal.get(j));
//                    cont++;
//                }
//                j++;
//            } while (cont < qntSatProcessar);
//        }catch (IndexOutOfBoundsException e){
//            e.printStackTrace();
//            Log.e("Index","Erro nas medições: " + e.getMessage());
//        }

            for (int k = 0; k < epocaEmAnalise.getListaMedicoes().size(); k++) {
                listaMedicoesAtual.add(epocaEmAnalise.getListaMedicoes().get(k));
            }

//        listaMedicoesOriginal = null;
//        listaMedicoesOriginal = listaMedicoes2; // FIXME VAI PERDER AS ORIGINAIS

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
                } while (cont < qntSatProcessar);
            }catch (IndexOutOfBoundsException e){
                Log.e("Index","Erro nas efemérides: " + e.getMessage());
                e.printStackTrace();
            }

            Collections.sort(listaMedicoesAtual);
            Collections.sort(listaEfemeridesAtual);

            listaCoordAtual = new ArrayList<>();
            calcCoordenadas(epocaEmAnalise);
            resultadosMMQ.add(calcularMMQ());
        }

        return  resultadosMMQ;
    }

    /**
     * Calcula as <b>coordenadas X,Y,Z (WGS-84)</b> para cada satélite.
     * <p>Calcula o <b>erro do relógio</b> para cada satélite em segundos.
     */
    public static void calcCoordenadas(EpocaGPS epocaEmAnalise){
        double GM = 3.9860044185E14; // 3.986004418E14;
        double We = 7.2921151467E-5; // 7.2921151467E-5;
        double c = 299792458;

//        inserirMedidasManuais();

        GNSSDate dataObservacao = epocaEmAnalise.getDateUTC();

//        EpocaGPS epocaEmAnalise = escolherEpoca();
//        GNSSDate dataObservacao = epocaEmAnalise.getDateUTC();

//        Log.i("Coord","Inicio do calculo das coordenadas dos satélites.");

        for (int i = 0; i < qntSatProcessar; i++ ){// FIXME
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

            // ------------------------------------------
            //Tempo de transmisao do sinal
            // ------------------------------------------
            double dtr = 0d; // ERRO DO RELÓGIO
            double tr = calc_Tr(dataObservacao);
            double tgps = tr - (listaMedicoesAtual.get(i).getPseudorangeMeters() / c);

            double dts = a0 + a1 * (tgps - toe) + a2 * (Math.pow(tgps - toe,2.0)); // ERRO DO SATÉLITE
            double tpropag = listaMedicoesAtual.get(i).getPseudorangeMeters()/c - dtr + dts;

            tgps = tr - dtr- tpropag + dts; // melhoria no tempo de transmissao

            //------------------------------------------
            //Coordenadas do satelite
            //------------------------------------------

            double delta_tk = tgps - toe;

            /**
             * Considerando possível mudança de semana
             * Autor: Bruno Vani
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

            /**
             * iteracao - anomalia excentrica
             */
            /*(4.11)*/
            double ek = mk;
            for (int k = 0; k < 7; k++){
                ek = mk + e * Math.sin(ek);
            }

            //Anomalia verdadeira
//            double sen_vk = ((Math.sqrt(1-(e*e))*Math.sin(ek))/(1-(e*Math.cos(ek))));
//            double cos_vk = (Math.cos((ek)-e)/(1-(e*(Math.cos(ek)))));
            // Anomalia verdadeira
            /*(4.12)*/
            double sen_vk = ( (Math.sqrt(1 - (e * e)) ) * Math.sin(ek) )  / ( 1 - (e * Math.cos(ek)) );
            double cos_vk = (Math.cos(ek) - e) / (1 - e * Math.cos(ek) );

            /**
             * Teste do quadrante
             * autor: Bruno Vani
             */
            double vk = 0d;
            if (((sen_vk >= 0) && (cos_vk >= 0)) || (sen_vk < 0) && (cos_vk >= 0)) { // I ou III quadrante
                vk = Math.atan(sen_vk / cos_vk);
            } else if (((sen_vk >= 0) && (cos_vk < 0)) || ((sen_vk < 0 ) && (cos_vk) < 0)) { //  II ou IV quadrante
                vk = Math.atan(sen_vk / cos_vk) + 3.1415926535898; // FIXME Math.pi();
            } else{
                Log.e("VK","Erro no ajuste do quadrante");
            }
//            double vk = Math.atan(sen_vk,cos_vk);
//            double vk = Math.atan2(sen_vk,cos_vk);

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

//            //Coordenadas finais para o processamento
//            double X = Xk;
//            double Y = Yk;
//            double Z = Zk;

            //coord = [coord; efemerides(i,1) X Y Z dts];
            int PRN = listaEfemeridesAtual.get(i).getPRN();
            CoordenadaGPS novaCoord = new CoordenadaGPS(PRN,Xc,Yc,Zc,dts);
            listaCoordAtual.add(novaCoord);
        }

//        CoordenadaGPS novaCoord11 = new CoordenadaGPS(11,11976995.597,-13228640.256,19156745.287,
//                -0.00073578934199949);
//        listaCoordAtual.add(novaCoord11);
//
//        CoordenadaGPS novaCoord26 = new CoordenadaGPS(26,15957489.517,8420202.096,-19546113.765,
//                -0.00014169401157237);
//        listaCoordAtual.add(novaCoord26);
//
//        Collections.sort(listaCoordAtual); // FIXME
//        Log.i("CoordFIM","Fim do cálculo das coordenadas!");

    }

    private static void inserirMedidasManuais(){
        /**
         * DEFINIÇÃO MANUAL DA ÉPOCA PARA ANÁLISE:
         */
        int YEAR = 18;
        int MONTH = 5;
        int DAY_MONTH = 21;
        int DAY_WEEK = GNSSConstants.DAY_SEG; // FIXME
        int HOUR_DAY = 19; // FIXME
        int MIN_HOUR = 15;
        double SEC = 0.0;

        GNSSDate epocaAnalise = new GNSSDate(YEAR,MONTH,DAY_MONTH,HOUR_DAY,MIN_HOUR,SEC);

        GNSSMeasurement novaMedicao11 = new GNSSMeasurement(11,24856683.359,epocaAnalise);
        GNSSMeasurement novaMedicao26 = new GNSSMeasurement(26,24842838.117,epocaAnalise);

        listaMedicoesOriginal.add(novaMedicao11);
        listaMedicoesOriginal.add(novaMedicao26);



    }

//    static double Xe = 3942590.30657541;
//    static double Ye = -4940172.84476568;
//    static double Ze = -2553313.07836198;

    static double Xe = 0;
    static double Ye = 0;
    static double Ze = 0;

    static double[] Lb;

    /**
     * Exemplo do livro
     */
    private static void setarExemplo(){
        qntSatProcessar = 7;
        listaCoordAtual = null;
        listaCoordAtual = new ArrayList<>();
        Lb = new double[qntSatProcessar];

        /*Exemplo do livro do Galera pg 292-299*/
        Xe = 3687627.3634;
        Ye = -4620821.5137;
        Ze = -2386884.4153;

        Lb[0] = 48100232.525;
        Lb[1] = 47574517.142;
        Lb[2] = 50223295.501;
        Lb[3] = 47847868.400;
        Lb[4] = 51708803.181;
        Lb[5] = 49370715.900;
        Lb[6] = 49337431.587;

        CoordenadaGPS novaCoord1 = new CoordenadaGPS(2,13191926.036,-9634277.149,-20330138.156,
                                -8.93963614364e-5); // Microsecs: -89.3963614364
        listaCoordAtual.add(novaCoord1);

        CoordenadaGPS novaCoord2 = new CoordenadaGPS(7,21244105.748,-15360752.012,-2877135.125,
                0.00057038458904959996); // Microsecs: 570.3845890496
        listaCoordAtual.add(novaCoord2);

        CoordenadaGPS novaCoord3 = new CoordenadaGPS(10,-135122.979,-25794393.804,5954578.737,
                3.88125012807e-5); // Microsecs: 38.8125012807
        listaCoordAtual.add(novaCoord3);

        CoordenadaGPS novaCoord4 = new CoordenadaGPS(13,19720605.766,-17653994.853,-1657890.383,
                -4.76228094177e-5); // Microsecs: -47.6228094177
        listaCoordAtual.add(novaCoord4);

        CoordenadaGPS novaCoord5 = new CoordenadaGPS(19,25910284.743,5823456.939,-2525126.594,
                1.64975089736e-5); // Microsecs: 16.4975089736
        listaCoordAtual.add(novaCoord5);

        CoordenadaGPS novaCoord6 = new CoordenadaGPS(26,-1932297.136,-16733519.796,-20382553.367,
                0.00065557612181870004); // Microsecs: 655.5761218187
        listaCoordAtual.add(novaCoord6);

        CoordenadaGPS novaCoord7 = new CoordenadaGPS(27,22374396.828,-3351761.100,-14280051.988,
                2.691733116672e-5); // Microsecs: 26.91733116672
        listaCoordAtual.add(novaCoord7);

        Log.i("Exemplo","Exemplo configurado...");
    }

    /**
     * Aplica o ajustamento pelo método dos mínimos quadrados (MMQ). <p>
     * Utiliza a abordagem encontrada em (MONICO, 2008) p. 292-300.
     */
    public static double calcularMMQ(){

//        setarExemplo();

        boolean flag = false;

        int contIteracoes = 0;
        double c = 2.99792458e8;
        double[] L0 = new double[qntSatProcessar];
        double[] L = new double[qntSatProcessar]; // Delta_L
        double[][] A = new double[qntSatProcessar][4];

        // FIXME COMENTAR AS LINHAS ABAIXO AO ATIVAR setarExemplo!
        Lb = new double[qntSatProcessar];
        //Carregando o vetor Lb
        for (int i = 0; i < qntSatProcessar; i++) {
            Lb[i] = listaMedicoesAtual.get(i).getPseudorangeMeters();
        }

        Lb[0] = 21648268.359;

        // Arquivos do RINEX da PPTE
//        Lb[0]  = 0;
//        Lb[1]  = 0;
//        Lb[2]  = 0;
//        Lb[3]  = 0;
//        Lb[4]  = 0;
//        Lb[5]  = 0;
//        Lb[6]  = 0;
//        Lb[7]  = 0;
//        Lb[8]  = 0;
//        Lb[9]  = 0;
//        Lb[10] = 0;

        // FIXME COMENTAR AS LINHAS ABAIXO AO ATIVAR setarExemplo!
        //USANDO A PPTE - COORDENADAS APROXIMADAS:
        double Xe = 3687624.3673;
        double Ye = -4620818.6831;
        double Ze = -2386880.3817;

//      USANDO O VETOR NULO:
//      double Xe = 0d;
//      double Ye = 0d;
//      double Ze = 0d;

//        USANDO O APP:
//      double Xe = 3687627.26039751;
//      double Ye = -4620683.42055526;
//      double Ze = -2387155.01580668;

//        USANDO O EP2:
//      double Xe = 3942590.30657541;
//      double Ye = -4940172.84476568;
//      double Ze = -2553313.07836198;

        // COORDENADAS REAIS
        double Xok = 3687624.3674;
        double Yok = -4620818.6827;
        double Zok = -2386880.3805;

//        Log.i("Lb","Vetor Lb criado");

        double[] X0 = new double[]{Xe, Ye, Ze,0d};
        double[][] N = new double[4][4];
        double[] U = new double[4];
        double[] X = new double[4];
        double[] Xa = new double[4];
        double erro = 0d;

        int MAX_ITERACOES = 8;

        // Solucao iterativa pelo metodo parametrico
        for (int k = 0; k < MAX_ITERACOES; k++){
            // Vetor L0
            for (int i = 0; i < qntSatProcessar; i++){ // PARA CADA SATÉLITE
                // dx = coord(i,2)-X0(1);
                double dx = listaCoordAtual.get(i).getX() - X0[0];
                double dy = listaCoordAtual.get(i).getY() - X0[1];
                double dz = listaCoordAtual.get(i).getZ() - X0[2];

                // L0(i,1) = sqrt(dx^2+dy^2+dz^2) + c*(0 - coord(i,5))
//                L0[i] = Math.sqrt( (dx*dx) + (dy*dy) + (dz*dz) ) + c * (0 - listaCoordAtual.get(i).getDts());
                double ro = Math.sqrt( (dx*dx) + (dy*dy) + (dz*dz) );
                L0[i] = ro;
            }

            //Vetor delta_L:
            //L = Lb-L0;
            for (int i = 0; i < Lb.length; i++){
//                L[i] = Lb[i] - L0[i];
//                L[i] = Lb[i] - (L0[i] + c * (0 - listaCoordAtual.get(i).getDts())); //FIXME IGUAL O DO VANI
                L[i] = Lb[i] - ( L0[i] + c * (X0[3] - listaCoordAtual.get(i).getDts()) ) ; //FIXME IGUAL O DO GALERA

            }

            //MATRIZ A
            for (int i = 0; i < qntSatProcessar; i++){
                double dx = listaCoordAtual.get(i).getX() - X0[0];
                double dy = listaCoordAtual.get(i).getY() - X0[1];
                double dz = listaCoordAtual.get(i).getZ() - X0[2];

                double distGeo = Math.sqrt(( dx*dx) + (dy*dy) + (dz*dz) );

                A[i][0] = -( dx / distGeo);
//                A[i][0] = (double)Math.round((-( dx / distGeo)) * 1000d) / 1000d;
                A[i][1] = -( dy / distGeo);
//                A[i][1] = (double)Math.round((-( dy / distGeo)) * 1000d) / 1000d;
                A[i][2] = -( dz / distGeo);
//                A[i][2] = (double)Math.round((-( dz / distGeo)) * 1000d) / 1000d;
                A[i][3] = 1.0d;
            }

//            Log.i("Iteracao","Matriz A recém-calculada: \n" + Arrays.deepToString(A))

            RealMatrix rA =  MatrixUtils.createRealMatrix(A);
//             RealMatrix rA = new Array2DRowRealMatrix(A, false);

            RealMatrix rP = MatrixUtils.createRealIdentityMatrix(qntSatProcessar);

            RealVector rL  =  MatrixUtils.createRealVector(L);
            //  N = A'PA;
            RealMatrix rN = rA.transpose().multiply(rP).multiply(rA);
            //U = A'PL;
            RealVector rU = rA.transpose().multiply(rP).operate(rL);
            //X = -inv(N)*U;

//            RealMatrix rInvN = new LUDecomposition(rN).getSolver().getInverse();
            RealMatrix rInvN = MatrixUtils.inverse(rN);
            RealVector rX = rInvN.operate(rU);
//            RealVector rX = rInvN.scalarMultiply(-1.0d).operate(rU);
//            RealVector rX = rInvN.operate(rU).mapMultiply(-1.0d);

            X = rX.toArray();
            double[] X2 = X;
            X[3] = X[3] / c;

            //Xa = X0+X; // FIXME FAZER UMA FUNÇÃO PARA ISSO
            for (int j = 0; j < X0.length; j++){
                Xa[j] = X0[j] + X[j];
            }

            //TODO VERIFICAR O LUGAR
            // Vetor dos resíduos V = AX+L
            RealVector rX2 = MatrixUtils.createRealVector(X2);
            RealVector rV = rA.operate(rX2).add(rL);
//            RealMatrix rV = MatrixUtils.createColumnRealMatrix(rA.operate(rX).add(rL).toArray());
            // Setup VtPV
            RealMatrix rVt = MatrixUtils.createRowRealMatrix(rV.toArray()); // Inverte V
            RealMatrix result = rVt.multiply(rP);
            RealVector VtPV = result.operate(rV);
            // Fator de variância a posteriori:
            double S0post = VtPV.getEntry(0) / (double) (qntSatProcessar - 4);

//            Log.i("VtPV","VtPV: " + VtPV.getEntry(0));
//            Log.i("Posteriori","SigmaP: " + S0post);

            // MVC das coordenadas ajustadas
            RealMatrix MVCXa = rInvN.scalarMultiply(S0post); // TODO TESTAR NO OCTAVE

            if (!MatrixUtils.isSymmetric(MVCXa,0.005)){
                Log.e("MVCXa","Should be symmetric!");
                Log.e("MVCXa",MVCXa.toString().replace("},","},\n")
                        .replace("BlockRealMatrix",""));
            }

//            Log.i("MVCXa",MVCXa.toString().replace("},","},\n")
//                    .replace("BlockRealMatrix",""));

            Double[] precision = new Double[4];

            // Desvio padrão das coordenadas:
            precision[0] = Math.sqrt(MVCXa.getEntry(0,0)); // Coordenada Xa
            precision[1] = Math.sqrt(MVCXa.getEntry(1,1)); // Coordenada Ya
            precision[2] = Math.sqrt(MVCXa.getEntry(2,2)); // Coordenada Za
            precision[3] = Math.sqrt(MVCXa.getEntry(3,3)); // Coordenada dtr

//            Log.i("Precisao", Arrays.deepToString(precision));

            // Discrepâncias em relação as coordenadas originais
            Double[] discrepanciesXYZ = new Double[3];
            discrepanciesXYZ[0] = Xe - Xa[0];
            discrepanciesXYZ[1] = Ye - Xa[1];
            discrepanciesXYZ[2] = Ze - Xa[2];

//            Log.i("Discrepancias", Arrays.deepToString(discrepanciesXYZ));
//            Log.i("Discrepancias", "--------------------------------");

            //Verificação da Tolerancia
            erro = Math.abs(maxValue(X));
            int numIteracao = contIteracoes + 1;

            if (erro < 0.0004 ) { // Módulo do erro em metros
                Log.i("FimERRO","============================================");
                Log.i("FimERRO","N° de iterações: " + numIteracao);
                Log.i("FimERRO","Coordenada Xr: " + Xa[0]);
                Log.i("FimERRO","Coordenada Yr: " + Xa[1]);
                Log.i("FimERRO","Coordenada Zr: " + Xa[2]);
                Log.i("FimERRO","Erro do relógio do receptor: " + Xa[3]);
                Log.i("FimERRO","Erro das coordenadas: " + erro);
                Log.i("FimERRO","============================================");
                flag = true;
                break;
            }else{ // Próxima iteração
                //X0 = Xa;
                System.arraycopy(Xa,0,X0,0,X0.length);
            }

            contIteracoes++;
        } // Fim do laço do ajustamento

        if (!flag){
            int numIteracao = contIteracoes + 1;
//            Log.i("FimFOOR","============================================");
//            Log.i("FimFOOR","Nº de iterações: " + numIteracao);
//            Log.i("FimFOOR","Coordenada Xr: " + Xa[0]);
//            Log.i("FimFOOR","Coordenada Yr: " + Xa[1]);
//            Log.i("FimFOOR","Coordenada Zr: " + Xa[2]);
//            Log.i("FimFOOR","Erro do relógio do receptor: " + Xa[3]);
//            Log.i("FimFOOR","Erro das coordenadas: " + erro);
//            Log.i("FimFOOR","============================================");
        }

        // Distância Geométrica entre a solucão obtida e a solução final:

        double dx = Xok - X0[0];
        double dy = Yok - X0[1];
        double dz = Zok - X0[2];

        double distGeo3D = Math.sqrt(( dx*dx) + (dy*dy) + (dz*dz) );
        double distGeo2D = Math.sqrt(( dx*dx) + (dy*dy) );

        Log.i("Distancia","Distancia Geometrica em relação às 3 coordenadas (X,Y,Z) originais: " + distGeo3D);
        Log.i("Distancia","Distancia Geometrica em relação às 2 coordenadas (X,Y) originais: " + distGeo2D);
//        Log.i("Distancia","========================================================" +
//                                    "=======================================================");

        return distGeo3D;
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
        list.remove(list.size() -1); // Remove o erro do relógio da verificação
        return Collections.max(list);
    }

    private static double getDesvioPadrao(double [] valores){
        return Math.sqrt(new Variance().evaluate(valores));
    }

    private static double getVariancia(double [] valores){
        return (new Variance().evaluate(valores));
    }

    private static int truncateSafely(double value) {
        // For negative numbers, use Math.ceil.
        // ... For positive numbers, use Math.floor.
        if (value < 0) {
            return (int) Math.ceil(value);
        } else {
            return (int) Math.floor(value);
        }
    }

}
