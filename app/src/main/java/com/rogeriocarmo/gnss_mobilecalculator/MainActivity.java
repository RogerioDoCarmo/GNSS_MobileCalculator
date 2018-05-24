package com.rogeriocarmo.gnss_mobilecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

import static Codigos.Reader.calcPseudoranges;
import static Codigos.Reader.calcularWLSpvt;
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
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Erro ao abrir o arquivo de efemérides!",
                    Toast.LENGTH_SHORT).show();
        }

        try {
            readLogger_RawAssets(MainActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Erro ao abrir o arquivo de log!",
                    Toast.LENGTH_SHORT).show();
        }

        calcPseudoranges();

        calcularWLSpvt();

    }
}
