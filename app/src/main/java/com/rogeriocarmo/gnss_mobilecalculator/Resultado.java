package com.rogeriocarmo.gnss_mobilecalculator;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import Codigos.CoordenadaGPS;
import Codigos.ResultEpch;

public class Resultado extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private final double EP02_LAT = -22.122500;
    private final double EP02_LONG = -51.407778;
    LatLng coordEP02 = new LatLng(EP02_LAT, EP02_LONG);
    ArrayList<CoordenadaGPS> resultados;

    float min_distance = Float.MAX_VALUE;
    float max_distance = Float.MIN_VALUE;
    LatLng coord_min;
    LatLng coord_max;
    int epch_min = 0;
    int epch_max = 0;
    int first_epch = -1;
    int last_epch = -1;

    private void definir_intervalo() {
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Resultado.this);
        builder.setTitle("Definição do intervalo de épocas");
        builder.setMessage("Escolha entre o intervalo [1," + resultados.size() + "]"); //FIXME REVER EPOCA FINAL
        builder.setCancelable(true);

        View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_interval,
                findViewById(R.id.content), false);

        final EditText input_min = viewInflated.findViewById(R.id.interval_min);
        final EditText input_max = viewInflated.findViewById(R.id.interval_max);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String first = (input_min.getText().toString());
                if (first.isEmpty()){
                    first_epch = 1;
                }else{
                    first_epch = Integer.valueOf(input_min.getText().toString());
                }

                String last = (input_max.getText().toString());
                if (last.isEmpty()){
                    last_epch = resultados.size();
                }else{
                    last_epch = Integer.valueOf(input_max.getText().toString());
                }

                if (resultados.size() > 1)
                {
                    marcar_todas_epocas();
                }else{
                    marcar_epoca_unica();
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        resultados =  getIntent().getParcelableArrayListExtra("Coord");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        definir_intervalo();

        mMap = googleMap;

//        googleMap.setMinZoomPreference(14.0f);
//        googleMap.setMaxZoomPreference(24.0f);
//
        mMap.addMarker(new MarkerOptions().
                position(coordEP02).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).
                title("Marco EP02").
                snippet("Coordenadas do IBGE").rotation(-90f));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordEP02,18));
    }

    private void marcar_epoca_unica() {
        Double latFinal  = resultados.get(0).getX();
        Double longFinal = resultados.get(0).getY();
        Double altFinal  = resultados.get(0).getZ();

        LatLng coordSolu = new LatLng(latFinal, longFinal);

        float[] result = new float[1];
        Location.distanceBetween(coordSolu.latitude,coordSolu.longitude,
                coordEP02.latitude,coordEP02.longitude, result);

        mMap.addMarker(new MarkerOptions().
                        alpha(0.6f).
                        position(coordSolu).
                        title("Solução Final").
                        snippet("Distância: " + new DecimalFormat("#.#### ").format(result[0]) + "m")
        );

        Snackbar snackbar = Snackbar
                .make(Objects.requireNonNull(mapFragment.getActivity()).findViewById(R.id.map),
                           "Lat: " +  new DecimalFormat("#.#### ").format(latFinal) +
                                "Lon: " + new DecimalFormat("#.#### ").format(longFinal)  +
                                "Alt: " + new DecimalFormat("#.### ").format(altFinal),
                        Snackbar.LENGTH_INDEFINITE)
                .setAction("Voltar", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
        snackbar.show();

        Toast.makeText(this,"Distância: " + new DecimalFormat("#.#### ").format(result[0]) + "m", Toast.LENGTH_LONG).show();
    }

    private void marcar_todas_epocas(){
        int numEpch = 1; // FIXME ERRO QUANDO A ACTIVITY É REABERTA!
        int inicio = first_epch - 1;
        int limite = last_epch;
        for (int i = inicio; i < limite ; i++){

            Double latDegrees = resultados.get(i).getX();
            Double longDegrees = resultados.get(i).getY();

            LatLng coord = new LatLng(latDegrees,longDegrees);

            float[] result = new float[1];
            Location.distanceBetween(coord.latitude,coord.longitude,
                    coordEP02.latitude,coordEP02.longitude, result);

            mMap.addMarker(new MarkerOptions().
                    position(coord).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).
                    alpha(0.5f).
                    title(numEpch + "a época").
                    snippet("Distância: " + String.format("%s", new DecimalFormat("###.###").format(result[0])) + "m")
            );
            numEpch++;

            if (result[0] < min_distance){
                min_distance = result[0];
                coord_min = coord;
                epch_min = i + 1;
            }else if (result[0] > max_distance){
                max_distance = result[0];
                coord_max = coord;
                epch_max = i + 1;
            }

        }
        numEpch--;

        marcar_centroide();

        marcar_min_max();

        Snackbar snackbar = Snackbar
                .make(Objects.requireNonNull(mapFragment.getActivity()).findViewById(R.id.map),
                        "Foram processadas " + numEpch + " épocas!",
                        Snackbar.LENGTH_INDEFINITE)
                .setAction("Voltar", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
        snackbar.show();
    }

    private void marcar_min_max() {
        mMap.addMarker(new MarkerOptions().
                position(coord_min).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).
                title(epch_min + "a época").
                snippet("Distância: " + new DecimalFormat("#.#### ").format(min_distance) + "m"
                        + "\n Marcador mais próximo!")
        );

        mMap.addMarker(new MarkerOptions().
                position(coord_max).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).
                title(epch_max + "a época").
                snippet("Distância: " + new DecimalFormat("#.#### ").format(max_distance) + "m"
                        + "\n Marcador mais distante!")
        );
    }

    private void marcar_centroide(){
        Double latDegrees = resultados.get(resultados.size() - 1).getX();
        Double longDegrees = resultados.get(resultados.size() - 1).getY();

        LatLng coord = new LatLng(latDegrees,longDegrees);

        float[] result = new float[1];
        Location.distanceBetween(coord.latitude,coord.longitude,
                coordEP02.latitude,coordEP02.longitude, result);

        mMap.addMarker(new MarkerOptions().
                position(coord).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).
                title("Centróide").
                snippet("Distância: " + new DecimalFormat("#.#### ").format(result[0]) + "m")
        );

        Toast.makeText(this,"Centróide calculado: "
                        + new DecimalFormat("#.#### ").format(result[0]) + "m",
                        Toast.LENGTH_LONG).show();
    }
}
