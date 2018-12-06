package com.rogeriocarmo.gnss_mobilecalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import Controller.SingletronController;
import View.SideBar;

import static Controller.ProcessamentoPPS.calcPseudorange;
import static Controller.ProcessamentoPPS.processar_todas_epocas;
import static Controller.ProcessamentoPPS.readLogger_RawAssets;
import static Controller.ProcessamentoPPS.readRINEX_RawAssets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SingletronController controller = SingletronController.getInstance();

        Intent intent = new Intent (getApplicationContext(), SideBar.class);
        startActivity(intent);

        try {
            controller.readLogger_RawAssets(MainActivity.this);
        } catch (IOException e) {
            Log.e("ERR_log","Erro ao abrir o arquivo de Log");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao abrir o arquivo de log: " + msg, Toast.LENGTH_LONG).show();
        }

        try{
            controller.calcPseudorange();
        } catch (Exception e){
            Log.e("ERR_pr","Erro ao calcular pseudodistâncias");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao calcular as pseudodistâncias: " + msg, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        try {
            controller.readRINEX_RawAssets(MainActivity.this);
        } catch (IOException e) {
            Log.e("ERR_ef","Erro ao abrir o RINEX");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao abrir o arquivo de efemérides: " + msg, Toast.LENGTH_LONG).show();
        }

        try{
            controller.processar_todas_epocas();
            /*
            *Epocas Boas
            * ID = 313
            * ID = 298 ==> A MELHOR!!!!
            * ID = 212
            * ID = 227
            * */
//            processar_epoca(298);
        } catch (Exception e){
            Log.e("ERR_coord","Execucao unica");
            e.printStackTrace();
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao calcular as coordenadas do satélite: " + msg,
                Toast.LENGTH_LONG).show();
        }

    }

}



















