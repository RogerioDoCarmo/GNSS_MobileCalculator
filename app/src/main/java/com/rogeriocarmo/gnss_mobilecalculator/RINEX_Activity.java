package com.rogeriocarmo.gnss_mobilecalculator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import Codigos.CoordenadaGPS;
import Codigos.EpocaGPS;
import Codigos.EpocaObs;
import Codigos.Rinex2Writer;

public class RINEX_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rinex_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ArrayList<EpocaObs> obs =  getIntent().getParcelableArrayListExtra("Obs");
//
        Rinex2Writer RINEX = new Rinex2Writer(getApplicationContext(),obs);
        RINEX.gravarRINEX();
        RINEX.send();
    }

}
