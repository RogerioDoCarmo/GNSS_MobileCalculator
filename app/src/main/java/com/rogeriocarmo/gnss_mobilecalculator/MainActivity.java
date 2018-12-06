package com.rogeriocarmo.gnss_mobilecalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import Controller.SingletronController;
import View.SideBar;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SingletronController controller = SingletronController.getInstance();

        Intent intent = new Intent (getApplicationContext(), SideBar.class);
        startActivity(intent);

        controller.processamento_completo(MainActivity.this);

        /*
         *Epocas Boas
         * ID = 313
         * ID = 298 ==> A MELHOR!!!!
         * ID = 212
         * ID = 227
         * */
    }

}



















