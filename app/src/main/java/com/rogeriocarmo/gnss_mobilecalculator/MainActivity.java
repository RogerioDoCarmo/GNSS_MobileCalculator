package com.rogeriocarmo.gnss_mobilecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import static Codigos.ProcessamentoPPS.calcCoordenadas;
import static Codigos.ProcessamentoPPS.calcCoordendasTeste_G05;
import static Codigos.ProcessamentoPPS.calcPseudoranges;
import static Codigos.ProcessamentoPPS.calcularMMQ;
import static Codigos.ProcessamentoPPS.readLogger_RawAssets;
import static Codigos.ProcessamentoPPS.readRINEX_RawAssets;
import static Codigos.ProcessamentoPPS.testeCoord_G05;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try { // Teste para um único satélite
//            testeCoord_G05(getApplicationContext());
//            calcCoordendasTeste_G05();
//            Log.i("TesteCoord","Fim do teste de coordenada de G05");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            readLogger_RawAssets(MainActivity.this);
        } catch (IOException e) {
            Log.e("ERR_log","Erro ao abrir o arquivo de Log");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                    "Erro ao abrir o arquivo de log: " + msg,
                    Toast.LENGTH_LONG).show();
        }

        try{
            calcPseudoranges();
        } catch (Exception e){
            Log.e("ERR_pr","Erro ao calcular pseudodistâncias");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                    "Erro ao calcular as pseudodistâncias: " + msg,
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        try {
            readRINEX_RawAssets(MainActivity.this);
        } catch (IOException e) {
            Log.e("ERR_ef","Erro ao abrir o RINEX");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                    "Erro ao abrir o arquivo de efemérides: " + msg,
                    Toast.LENGTH_LONG).show();
        }

        try{
            calcCoordenadas();
        } catch (Exception e){
            Log.e("ERR_coord","Erro ao calcular as coordenadas dos satélites");
            e.printStackTrace();
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                    "Erro ao calcular as coordenadas do satélite: " + msg,
                    Toast.LENGTH_LONG).show();
        }

        try{
            calcularMMQ();
        } catch (Exception e){
            Log.e("ERR_MMQ","Erro ao processar o ajustamento");
            e.printStackTrace();
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                    "Erro ao calcular o ajustamento: " + msg,
                    Toast.LENGTH_LONG).show();
        }

        Log.i("THE_END","O PROGRAMA FOI FINALIZADO COM SUCESSO! xD");
//        Toast.makeText(getApplicationContext(),
//                "Programa executado com sucesso!",
//                Toast.LENGTH_LONG).show();
    }
}
