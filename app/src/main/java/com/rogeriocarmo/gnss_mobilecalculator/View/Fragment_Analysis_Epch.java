package com.rogeriocarmo.gnss_mobilecalculator.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rogeriocarmo.gnss_mobilecalculator.Controller.AnaliseEpoca;
import com.rogeriocarmo.gnss_mobilecalculator.Controller.SingletronController;
import com.rogeriocarmo.gnss_mobilecalculator.R;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_Analysis_Epch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Analysis_Epch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Analysis_Epch extends Fragment {

    private OnFragmentInteractionListener mListener;

    SingletronController controller;
    AnaliseEpoca analise;
    int epocaAnalise;

    public Fragment_Analysis_Epch() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Fragment_Analysis_Epch.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Analysis_Epch newInstance() {
        Fragment_Analysis_Epch fragment = new Fragment_Analysis_Epch();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis_epch, container, false);

        controller = SingletronController.getInstance();

        TextView txtID = view.findViewById(R.id.txtID);


        Button btnGraph = view.findViewById(R.id.btnGraph);
        btnGraph.setEnabled(false);

        TextView txtResult = view.findViewById(R.id.txtAnalise);
        txtResult.setMovementMethod(new ScrollingMovementMethod());

        Button btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = txtID.getText().toString();

                if (!texto.isEmpty()) { //TODO CHECAR INTERVALO
                    epocaAnalise = Integer.valueOf(texto) - 1;
                    if (epocaAnalise == -1) epocaAnalise = 0;
                    if (epocaAnalise > controller.getNumEpocas()) epocaAnalise = controller.getNumEpocas() - 1;
                    analise = controller.analisarEpoca(epocaAnalise);
                    String result = analise.toString();
                    txtResult.setText(result);
                    btnGraph.setEnabled(true);
                }
            }
        });

        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XYMultipleSeriesRenderer renderer = getBarRenderer();
                myChartSettings(renderer);
                Intent intent = ChartFactory.getBarChartIntent(getContext(), getBarDataset(), renderer, BarChart.Type.DEFAULT,"Análise de Época");
                startActivity(intent);
            }
        });

        return view;
    }

    private XYMultipleSeriesDataset getBarDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        ArrayList<String> legendTitles = new ArrayList<String>();
        legendTitles.add("Cn0DbHz");

        for (int i = 0; i < legendTitles.size(); i++) {
            CategorySeries series = new CategorySeries(legendTitles.get(i));
            for (int k = 0; k < analise.getListCn0DbHz().size(); k++) {
                series.add(analise.getListCn0DbHz().get(k));
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    public XYMultipleSeriesRenderer getBarRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setMargins(new int[] { 30, 40, 15, 0 });
        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
        r.setColor(Color.BLUE);
        renderer.addSeriesRenderer(r);

        return renderer;
    }

    @SuppressLint("DefaultLocale")
    private void myChartSettings(XYMultipleSeriesRenderer renderer) {
        renderer.setChartTitle("Densidade Carrier-to-noise em dB-Hz");

        renderer.setXTitle("Satélites");
        renderer.setYTitle("Cn0DbHz");

        renderer.setXAxisMin(0.5);
        renderer.setXAxisMax(10.5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(40);

        for (int i = 0; i < analise.getListCn0DbHz().size(); i++){
            renderer.addXTextLabel(i + 1, "G" + String.format("%02d", analise.getListPRNs().get(i)));
        }

        renderer.setDisplayChartValues(true);

        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setBarSpacing(0.5);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.GRAY);
        renderer.setXLabels(0); // sets the number of integer labels to appear
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
}
