package com.rogeriocarmo.gnss_mobilecalculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.math3.analysis.function.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    private Button intervalo;
    private int start_epch;
    private int final_epch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Vinculação dos botões*/
        btnVisualizar = findViewById(R.id.idVisualizar);
        btnRINEX = findViewById(R.id.btnRINEX);
        btnEpocas = findViewById(R.id.btnEpocas);
        btnResultados = findViewById(R.id.btnResultados);
        intervalo = findViewById(R.id.intervalo);

        btnVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO USANDO INFLATERRRRRRR

                Intent intent = new Intent(getApplicationContext(), Resultado.class);

                ArrayList<CoordenadaGPS> valores = getResultadosGeodeticos();

                intent.putParcelableArrayListExtra("Coord",valores);
                intent.putExtra("Inicial",start_epch);
                intent.putExtra("Final",final_epch);
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

        intervalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                definir_intervalo();
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

    private void definir_intervalo() {
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Definição do interalo");
        builder.setCancelable(true);
//        builder.setMessage("MENSAGEM");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_interval,
                findViewById(R.id.content), false);
// Set up the input
        final EditText input_min = (EditText) viewInflated.findViewById(R.id.interval_min);
        final EditText input_max = (EditText) viewInflated.findViewById(R.id.interval_max);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);


// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                start_epch = Integer.valueOf(input_min.getText().toString());
                final_epch = Integer.valueOf(input_max.getText().toString());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

//        builder.show();

        final AlertDialog alert = builder.create();
        alert.show();

        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                alert.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, 600000000); // the timer will count 5 seconds....

    }

}



















