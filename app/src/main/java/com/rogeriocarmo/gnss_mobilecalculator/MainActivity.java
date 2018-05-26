package com.rogeriocarmo.gnss_mobilecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import static Codigos.Reader.ajustarEpocas;
import static Codigos.Reader.calcCoordendas;
import static Codigos.Reader.calcPseudoranges;
import static Codigos.Reader.calcularMMQ;
import static Codigos.Reader.readLogger_RawAssets;
import static Codigos.Reader.readRINEX_RawAssets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            readRINEX_RawAssets(MainActivity.this);
        } catch (IOException e) {
            Log.e("ERR_ef","Erro ao abrir o RINEX");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                    "Erro ao abrir o arquivo de efemérides: " + msg,
                    Toast.LENGTH_LONG).show();
        }

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
        }

        try{
            ajustarEpocas();
        } catch (Exception e){
            Log.e("ERR_ajEpch","Erro ao ajustar a época para processamento");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                    "Erro ao ajustar as épocas: " + msg,
                    Toast.LENGTH_LONG).show();
        }

        try{
            calcCoordendas();
        } catch (Exception e){
            Log.e("ERR_coord","Erro ao calcular as coordenadas dos satélites");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                    "Erro ao calcular as coordenadas do satélite: " + msg,
                    Toast.LENGTH_LONG).show();
        }

        try{
            calcularMMQ();
        } catch (Exception e){
            Log.e("ERR_MMQ","Erro ao processar o ajustamento");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                    "Erro ao calcular o ajustamento: " + msg,
                    Toast.LENGTH_LONG).show();
        }

        Log.i("THE_END","O PROGRAMA FOI FINALIZADO COM SUCESSO! xD");
        Toast.makeText(getApplicationContext(),
                "Programa executado com sucesso!",
                Toast.LENGTH_LONG).show();
    }
}
