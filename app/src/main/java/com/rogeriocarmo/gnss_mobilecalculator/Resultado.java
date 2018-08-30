package com.rogeriocarmo.gnss_mobilecalculator;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Objects;

import Codigos.Ecef2LlaConverter;

public class Resultado extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setMinZoomPreference(6.0f);
        googleMap.setMaxZoomPreference(20.0f);

        Ecef2LlaConverter.GeodeticLlaValues valores = (Ecef2LlaConverter.GeodeticLlaValues)
                getIntent().getSerializableExtra("Coord");

        Double latDegrees = Math.toDegrees(valores.latitudeRadians);
        Double longDegrees = Math.toDegrees(valores.longitudeRadians);
        Double altitudeMeters = valores.altitudeMeters;

        LatLng coord = new LatLng(latDegrees,longDegrees);

        mMap.addMarker(new MarkerOptions().position(coord).title("Solução Final"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord,14));

        Snackbar snackbar = Snackbar
                .make(Objects.requireNonNull(mapFragment.getActivity()).findViewById(R.id.map),
                         "Lat:  " +  new DecimalFormat("#.#### ").format(latDegrees) +
                             "Lon: " + new DecimalFormat("#.#### ").format(longDegrees)  +
                             "Alt: " + new DecimalFormat("#.### ").format(altitudeMeters),
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
}
