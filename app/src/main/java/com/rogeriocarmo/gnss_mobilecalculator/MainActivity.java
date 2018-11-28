package com.rogeriocarmo.gnss_mobilecalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import Codigos.CoordenadaGPS;
import Codigos.EpocaGPS;
import Codigos.EpocaObs;
import Codigos.Rinex2Writer;

import static Codigos.ProcessamentoPPS.calcPseudorange;
import static Codigos.ProcessamentoPPS.getObservacoes;
import static Codigos.ProcessamentoPPS.getResultadosGeodeticos;
import static Codigos.ProcessamentoPPS.gravar_epocas;
import static Codigos.ProcessamentoPPS.gravar_resultados;
import static Codigos.ProcessamentoPPS.processar_epoca;
import static Codigos.ProcessamentoPPS.processar_todas_epocas;
import static Codigos.ProcessamentoPPS.readLogger_RawAssets;
import static Codigos.ProcessamentoPPS.readRINEX_RawAssets;
import static Codigos.ProcessamentoPPS.send_txt;

public class MainActivity extends AppCompatActivity {

    private Button btnVisualizar;
    private Button btnRINEX;
    private Button btnEpocas;
    private Button btnResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Vinculação dos botões*/
        btnVisualizar = findViewById(R.id.idVisualizar);
        btnRINEX = findViewById(R.id.btnRINEX);
        btnEpocas = findViewById(R.id.btnEpocas);
        btnResultados = findViewById(R.id.btnResultados);

        btnVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Resultado.class);

                ArrayList<CoordenadaGPS> valores = getResultadosGeodeticos();

                intent.putParcelableArrayListExtra("Coord",valores);
                startActivity(intent);
            }
        });

        btnRINEX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<EpocaObs> observacoes = getObservacoes();

                Rinex2Writer RINEX = new Rinex2Writer(getApplicationContext(),observacoes);
                if (RINEX.gravarRINEX()){
                    Toast.makeText(getApplicationContext(), "Arquivo gravado com sucesso!", Toast.LENGTH_LONG).show();
                    RINEX.send();
                }else{
                    Toast.makeText(getApplicationContext(), "Erro ao gravar o arquivo!", Toast.LENGTH_LONG).show();
                }
//                Intent intent = new Intent(getApplicationContext(), RINEX_Activity.class);
//                intent.putParcelableArrayListExtra("Obs",observacoes);
//                startActivity(intent);
            }
        });

        btnEpocas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gravar_epocas(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Arquivo gravado com sucesso!", Toast.LENGTH_LONG).show();
                    send_txt();
                }else{
                    Toast.makeText(getApplicationContext(), "Erro ao gravar o arquivo!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gravar_resultados(getApplication())) {
                    Toast.makeText(getApplicationContext(), "Arquivo gravado com sucesso!", Toast.LENGTH_LONG).show();
                    send_txt();
                }else{
                    Toast.makeText(getApplicationContext(), "Erro ao gravar o arquivo!", Toast.LENGTH_LONG).show();
                }
            }
        });

        try {
            readLogger_RawAssets(MainActivity.this);
        } catch (IOException e) {
            Log.e("ERR_log","Erro ao abrir o arquivo de Log");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao abrir o arquivo de log: " + msg, Toast.LENGTH_LONG).show();
        }

        try{
            calcPseudorange();
        } catch (Exception e){
            Log.e("ERR_pr","Erro ao calcular pseudodistâncias");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao calcular as pseudodistâncias: " + msg, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        try {
            readRINEX_RawAssets(MainActivity.this);
        } catch (IOException e) {
            Log.e("ERR_ef","Erro ao abrir o RINEX");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao abrir o arquivo de efemérides: " + msg, Toast.LENGTH_LONG).show();
        }

        try{
            processar_todas_epocas();
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



















