package com.rogeriocarmo.gnss_mobilecalculator.View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rogeriocarmo.gnss_mobilecalculator.R;

import java.util.ArrayList;

import com.rogeriocarmo.gnss_mobilecalculator.Controller.SingletronController;
import com.rogeriocarmo.gnss_mobilecalculator.Model.EpocaObs;
import com.rogeriocarmo.gnss_mobilecalculator.Model.Rinex2Writer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_ShowRINEX.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_ShowRINEX#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_ShowRINEX extends Fragment {

    Rinex2Writer RINEX;
    ArrayList<EpocaObs> observacoes;
    SingletronController controller;
    private boolean isRINEXcreated;

    private void build_RINEX2(){
        observacoes = controller.getObservacoes();
        RINEX = new Rinex2Writer(getContext(),observacoes);
        isRINEXcreated = RINEX.gravarRINEX();
    }

    private OnFragmentInteractionListener mListener;

    public Fragment_ShowRINEX() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_ShowResults.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_ShowRINEX newInstance(String param1, String param2) {
        Fragment_ShowRINEX fragment = new Fragment_ShowRINEX();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        controller = SingletronController.getInstance();
        build_RINEX2();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_rinex, container, false);

        TextView txtRINEX = view.findViewById(R.id.txtRINEX);
        txtRINEX.setText(RINEX.getRINEXasString());

        Button button = view.findViewById(R.id.btnSalvarRINEX2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRINEXcreated){
                    Toast.makeText(getContext(), "Arquivo RINEX gravado com sucesso!", Toast.LENGTH_LONG).show();
                    RINEX.send();
                }else{
                    Toast.makeText(getContext(), "Erro ao gravar o arquivo!", Toast.LENGTH_LONG).show();
                }
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
