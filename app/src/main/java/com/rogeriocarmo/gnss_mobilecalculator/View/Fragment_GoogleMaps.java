package com.rogeriocarmo.gnss_mobilecalculator.View;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rogeriocarmo.gnss_mobilecalculator.Controller.SingletronController;
import com.rogeriocarmo.gnss_mobilecalculator.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.rogeriocarmo.gnss_mobilecalculator.Model.CoordenadaGeodesica;

public class Fragment_GoogleMaps extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;

    SingletronController controller;
    private Fragment_GoogleMaps.OnFragmentInteractionListener mListener;

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

    Snackbar snackbar = null;
    Circle circleMin = null;
    Circle circleMax = null;
    Circle circleCentroide = null;

    private void show_dialog_interval() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        int limite_epch = resultGeoid.size();
        if (limite_epch > 1) limite_epch = limite_epch - 1;

        builder.setTitle("Definição do intervalo de épocas:");
        builder.setMessage("Escolha entre o intervalo [1," + limite_epch + "]");
        builder.setCancelable(true);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_interval,
                getActivity().findViewById(R.id.id_content), false);

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
                    marcar_epoca_unica(); //FIXME
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

    private void show_filterDistance_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final Float[] distance = new Float[1];

        builder.setTitle("Distância do marco referencial:");
        builder.setCancelable(true);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_distance,
                getActivity().findViewById(R.id.id_content), false);

        final EditText input_distance = viewInflated.findViewById(R.id.input_distance);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = (input_distance.getText().toString());
                if (!value.isEmpty()) {
                    distance[0] = Float.valueOf(value); //TODO Review

                    if (distance[0] <= 0.0f) dialog.cancel(); //FIXME

                    mMap.clear();
                    erase_circles();
                    marcar_EP02();
                    marcar_epocas_distancia(distance[0]);
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
        return new Fragment_GoogleMaps();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            resultGeoid = getArguments().getParcelableArrayList("Coord"); //TODO obter aqui direto do controller

            show_dialog_interval();
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
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        FloatingActionButton fab_filter = view.findViewById(R.id.fab_filter);
        fab_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_filterDistance_dialog();
            }
        });

        FloatingActionButton fab_add = view.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_addMaker_dialog();
            }
        });

        return view;
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
        if (snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        marcar_EP02();

        mMap.getUiSettings().setScrollGesturesEnabled(true);

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getMaxZoomLevel();
        mMap.getMinZoomLevel();
        mMap.getUiSettings();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    private void show_addMaker_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Novo marcador:");
        builder.setCancelable(true);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_marker,
                                    getActivity().findViewById(R.id.id_content), false);

        TabHost myTabs = viewInflated.findViewById(R.id.tabhost);

        myTabs.setup();
        TabHost.TabSpec spec = myTabs.newTabSpec("tag1");

        spec.setContent(R.id.add_maker_tab_1);
        spec.setIndicator("Lat/Long");
        myTabs.addTab(spec);

        spec = myTabs.newTabSpec("tag2");
        spec.setContent(R.id.add_maker_tab_2);
        spec.setIndicator("XYZ (WGS-84)");
        myTabs.addTab(spec);

        //Tab 1
        final EditText input_lat = viewInflated.findViewById(R.id.input_new_lat);
        final EditText input_long = viewInflated.findViewById(R.id.input_new_long);
        //Tab 2
        final EditText input_x = viewInflated.findViewById(R.id.input_new_x);
        final EditText input_y = viewInflated.findViewById(R.id.input_new_y);
        final EditText input_z = viewInflated.findViewById(R.id.input_new_z);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (myTabs.getCurrentTab() == 0){
                    String value_lat = (input_lat.getText().toString());
                    String value_lon = (input_long.getText().toString());

                    if ( !value_lat.isEmpty() && !value_lon.isEmpty() ) {
                        adicionar_marcador(Double.valueOf(value_lat), Double.valueOf(value_lon));
                    }

                }

                if (myTabs.getCurrentTab() == 1){
                    String value_x = (input_x.getText().toString());
                    String value_y = (input_y.getText().toString());
                    String value_z = (input_z.getText().toString());

                    if ( !value_x.isEmpty() && !value_y.isEmpty() && !value_z.isEmpty() ) {
                        controller = SingletronController.getInstance();
                        CoordenadaGeodesica resultConversion = controller.convertXYZ_to_LatLongAlt(Float.valueOf(value_x), Float.valueOf(value_y), Float.valueOf(value_z));
                        adicionar_marcador(resultConversion.getLatDegrees(), resultConversion.getLonDegrees()); //TODO REVER QUESTÃO DA CONVERSÃO PARA FLOAT
                    }

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

    private void voltar_fragment_inicial() {
        Fragment fragment = null;
        Class fragmentClass = Fragment_Main.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment);
//        fragmentTransaction.addToBackStack(null); TODO
        fragmentTransaction.commit();
    }

    private void adicionar_marcador(Double latitude, Double longitude) {
        LatLng coord = new LatLng((latitude), (longitude));

        float[] result = new float[1];
        Location.distanceBetween(coord.latitude,coord.longitude,
                coordEP02.latitude,coordEP02.longitude, result);

        mMap.addMarker(new MarkerOptions().
                position(coord).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).
                title("Novo marcador").
                snippet("Distância: " + String.format("%s", new DecimalFormat("###.###").format(result[0])) + "m"));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 20));
    }

    private void marcar_EP02(){
        mMap.addMarker(new MarkerOptions().
                position(coordEP02).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).
                title("Marco EP02").
                snippet("Coordenadas do IBGE").rotation(90f));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordEP02, 18));
    }

    private void draw_circles(){
        LatLng CENTER = new LatLng(EP02_LAT, EP02_LONG);

        CircleOptions circle_centroide = new CircleOptions();
        circle_centroide.center(CENTER).fillColor(Color.TRANSPARENT).strokeColor(Color.BLUE).strokeWidth(4).radius(centroide_distance);
        circleCentroide = mMap.addCircle(circle_centroide);

        CircleOptions circle_min = new CircleOptions();
        circle_min.center(CENTER).fillColor(Color.TRANSPARENT).strokeColor(Color.GREEN).strokeWidth(4).radius(min_distance).clickable(true);
        circleMin = mMap.addCircle(circle_min);

        CircleOptions circle_max = new CircleOptions();
        circle_max.center(CENTER).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(4).radius(max_distance).clickable(true);
        circleMax = mMap.addCircle(circle_max);
    }

    private void erase_circles(){
        if (circleCentroide != null) circleCentroide.remove();
        if (circleMin != null) circleMin.remove();
        if (circleMax != null) circleMax.remove();
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

    private void marcar_epoca_unica() { // TODO CONSIDERAR EXCLUIR
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
                        voltar_fragment_inicial();
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
                    alpha(0.6f).
                    title(resultGeoid.get(i).getNumEpch() + "ª época").
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

            draw_circles();

            snackbar = Snackbar
                    .make(getActivity().findViewById(android.R.id.content),
                            "Menor distância na " + epch_min + "ª época: " + new DecimalFormat("###.###").format(min_distance) + "m",
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction("Voltar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) { // FIXME SNACKBAR COM ERRO AO CLICAR EM OUTRAS ACTIVITIY
                            voltar_fragment_inicial();
                        }
                    });
            snackbar.show();
            Toast.makeText(getContext(),"Foram processadas " + numEpch + " épocas!",Toast.LENGTH_SHORT).show();
        }
    }

    private void marcar_epocas_distancia(float distancia) {
        int numEpch = 1; // FIXME ERRO QUANDO A ACTIVITY É REABERTA!
        int limite = resultGeoid.size() - 1;
        min_distance = Float.MAX_VALUE;
        max_distance = Float.MIN_VALUE;

        for (int i = 0; i < limite; i++){

            Double latDegrees = resultGeoid.get(i).getLatDegrees();
            Double longDegrees = resultGeoid.get(i).getLonDegrees();

            LatLng coord = new LatLng(latDegrees,longDegrees);

            float[] result = new float[1];
            Location.distanceBetween(coord.latitude,coord.longitude,
                    coordEP02.latitude,coordEP02.longitude, result);

            if (result[0] <= distancia) {
                mMap.addMarker(new MarkerOptions().
                        position(coord).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).
                        alpha(0.6f).
                        title(resultGeoid.get(i).getNumEpch() + "ª época").
                        snippet("Distância: " + String.format("%s", new DecimalFormat("###.###").format(result[0])) + "m")
                );
                numEpch++;

                if (result[0] < min_distance){
                    min_distance = result[0];
                    coord_min = coord;
                    epch_min = i + 1;
                }else if (result[0] > max_distance && result[0] < distancia){
                    max_distance = result[0];
                    coord_max = coord;
                    epch_max = i + 1;
                }
            }
        }
        numEpch--;

        if (last_epch > 1) {
            marcar_centroide();

            marcar_min_max();

            draw_circles();

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordEP02, 22));

            snackbar = Snackbar
                    .make(getActivity().findViewById(android.R.id.content),
                            "Foram encontradas " + numEpch + " épocas!",
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction("Voltar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) { // FIXME SNACKBAR COM ERRO AO CLICAR EM OUTRAS ACTIVITIY
                            voltar_fragment_inicial();
                        }
                    });
            snackbar.show();
        }
    }

    private void marcar_min_max() {
        mMap.addMarker(new MarkerOptions().
                position(coord_min).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).
                title(epch_min + "ª época").
                snippet("Distância: " + new DecimalFormat("#.#### ").format(min_distance) + "m"
                        + "\n Marcador mais próximo!").
                rotation(-90f)
        );

        mMap.addMarker(new MarkerOptions().
                position(coord_max).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).
                title(epch_max + "ª época").
                snippet("Distância: " + new DecimalFormat("#.#### ").format(max_distance) + "m"
                        + "\n Marcador mais distante!").
                rotation(-90f)
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
                snippet("Distância: " + new DecimalFormat("#.#### ").format(result[0]) + "m").
                rotation(-90f)
        );
    }

}
