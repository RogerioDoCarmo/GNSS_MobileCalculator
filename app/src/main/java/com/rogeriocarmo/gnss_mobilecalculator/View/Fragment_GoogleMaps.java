package com.rogeriocarmo.gnss_mobilecalculator.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rogeriocarmo.gnss_mobilecalculator.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.rogeriocarmo.gnss_mobilecalculator.Model.CoordenadaGeodesica;

public class Fragment_GoogleMaps extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;
    private SupportMapFragment mSupportMapFragment;

    private final double EP02_LAT = -22.122500;
    private final double EP02_LONG = -51.407778;

    LatLng coordEP02 = new LatLng(EP02_LAT, EP02_LONG);
    ArrayList<CoordenadaGeodesica> resultGeoid;

    float min_distance = Float.MAX_VALUE;
    float max_distance = Float.MIN_VALUE;
    float centroide_distance = 0;
    LatLng coord_min = null;
    LatLng coord_max = null;
    int epch_min = 0;
    int epch_max = 0;
    int first_epch = -1;
    int last_epch = -1;

    private void Dialog_interval() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        int limite_epch = resultGeoid.size();
        if (limite_epch > 1) limite_epch = limite_epch - 1;

        builder.setTitle("Definição do intervalo de épocas:");
        builder.setMessage("Escolha entre o intervalo [1," + limite_epch + "]");
        builder.setCancelable(true);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_interval,
                getActivity().findViewById(R.id.content), false);

        final EditText input_min = viewInflated.findViewById(R.id.interval_min);
        final EditText input_max = viewInflated.findViewById(R.id.interval_max);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String first = (input_min.getText().toString());
                if (first.isEmpty()) {
                    first_epch = 1;
                } else {
                    first_epch = Integer.valueOf(input_min.getText().toString());
                }

                String last = (input_max.getText().toString());
                if (last.isEmpty()) {
                    last_epch = resultGeoid.size();
                } else {
                    last_epch = Integer.valueOf(input_max.getText().toString());
                }

                if (resultGeoid.size() > 1) {
                    marcar_todas_epocas();
                } else {
                    marcar_epoca_unica();
                }

                dialog.dismiss();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordEP02, 18));
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

    private Fragment_GoogleMaps.OnFragmentInteractionListener mListener;

    public Fragment_GoogleMaps() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment Fragment_GoogleMaps.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_GoogleMaps newInstance() {//String param1, String param2
        Fragment_GoogleMaps fragment = new Fragment_GoogleMaps();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            resultGeoid = getArguments().getParcelableArrayList("Coord");

            Dialog_interval();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.mapa);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Fragment_GoogleMaps.OnFragmentInteractionListener) {
            mListener = (Fragment_GoogleMaps.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().
                position(coordEP02).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).
                title("Marco EP02").
                snippet("Coordenadas do IBGE").rotation(-90f));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordEP02, 18));

        mMap.getUiSettings().setScrollGesturesEnabled(true);

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getMaxZoomLevel();
        mMap.getMinZoomLevel();
        mMap.getUiSettings();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    private void desenhar_circulos(){
        LatLng CENTER = new LatLng(EP02_LAT, EP02_LONG);

        CircleOptions circle_centroide = new CircleOptions();
        circle_centroide.center(CENTER).fillColor(Color.TRANSPARENT).strokeColor(Color.BLUE).strokeWidth(4).radius(centroide_distance);
        mMap.addCircle(circle_centroide);

        CircleOptions circle_min = new CircleOptions();
        circle_min.center(CENTER).fillColor(Color.TRANSPARENT).strokeColor(Color.GREEN).strokeWidth(4).radius(min_distance).clickable(true);
        mMap.addCircle(circle_min);

        CircleOptions circle_max = new CircleOptions();
        circle_max.center(CENTER).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(4).radius(max_distance).clickable(true);
        mMap.addCircle(circle_max);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void marcar_epoca_unica() {
        Double latFinal  = resultGeoid.get(0).getLatDegrees();
        Double longFinal = resultGeoid.get(0).getLonDegrees();
        Double altFinal  = resultGeoid.get(0).getAltMeters();

        LatLng coordSolu = new LatLng(latFinal, longFinal);

        float[] result = new float[1];
        Location.distanceBetween(coordSolu.latitude,coordSolu.longitude,
                coordEP02.latitude,coordEP02.longitude, result);

        mMap.addMarker(new MarkerOptions().
                alpha(0.8f).
                position(coordSolu).
                title("Solução Final").
                snippet("Distância: " + new DecimalFormat("#.#### ").format(result[0]) + "m")
        );

        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content),
                        "Lat: " +  new DecimalFormat("#.#### ").format(latFinal) +
                                "Lon: " + new DecimalFormat("#.#### ").format(longFinal)  +
                                "Alt: " + new DecimalFormat("#.### ").format(altFinal),
                        Snackbar.LENGTH_INDEFINITE)
                .setAction("Voltar", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), Activity_Main.class);
                        startActivity(intent);
                    }
                });
        snackbar.show();

        Toast.makeText(getContext(),"Distância: " + new DecimalFormat("#.#### ").format(result[0]) + "m", Toast.LENGTH_LONG).show();
    }

    private void marcar_todas_epocas(){
        int numEpch = 1; // FIXME ERRO QUANDO A ACTIVITY É REABERTA!
        int inicio = first_epch - 1;
        int limite = last_epch;
        for (int i = inicio; i < limite ; i++){

            Double latDegrees = resultGeoid.get(i).getLatDegrees();
            Double longDegrees = resultGeoid.get(i).getLonDegrees();

            LatLng coord = new LatLng(latDegrees,longDegrees);

            float[] result = new float[1];
            Location.distanceBetween(coord.latitude,coord.longitude,
                    coordEP02.latitude,coordEP02.longitude, result);

            mMap.addMarker(new MarkerOptions().
                    position(coord).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).
                    alpha(0.8f).
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

        if (last_epch > 1) {
            marcar_centroide();

            marcar_min_max();

            desenhar_circulos();

            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(android.R.id.content),
                            "Foram processadas " + numEpch + " épocas!",
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction("Voltar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), Activity_Main.class);
                            startActivity(intent);
                        }
                    });
            snackbar.show();
        }
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
        Double latDegrees = resultGeoid.get(resultGeoid.size() - 1).getLatDegrees();
        Double longDegrees = resultGeoid.get(resultGeoid.size() - 1).getLonDegrees();

        LatLng coord = new LatLng(latDegrees,longDegrees);

        float[] result = new float[1];

        Location.distanceBetween(coord.latitude,coord.longitude,
                coordEP02.latitude,coordEP02.longitude, result);

        centroide_distance = result[0];

        mMap.addMarker(new MarkerOptions().
                position(coord).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).
                title("Centróide").
                snippet("Distância: " + new DecimalFormat("#.#### ").format(result[0]) + "m")
        );

        Toast.makeText(getContext(),"Centróide calculado: "
                        + new DecimalFormat("#.#### ").format(result[0]) + "m",
                Toast.LENGTH_LONG).show();
    }

}
