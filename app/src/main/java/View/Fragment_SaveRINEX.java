package View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rogeriocarmo.gnss_mobilecalculator.R;

import java.util.ArrayList;

import Model.EpocaObs;
import Model.Rinex2Writer;

import static Controller.ProcessamentoPPS.getObservacoes;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_SaveRINEX.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_SaveRINEX#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_SaveRINEX extends Fragment {

    private OnFragmentInteractionListener mListener;

    public Fragment_SaveRINEX() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_SaveTXT.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_SaveRINEX newInstance(String param1, String param2) {
        Fragment_SaveRINEX fragment = new Fragment_SaveRINEX();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save_rinex, container, false);

        Button button = view.findViewById(R.id.btnSalvarRINEX2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<EpocaObs> observacoes = getObservacoes();

                Rinex2Writer RINEX = new Rinex2Writer(getContext(),observacoes);
                if (RINEX.gravarRINEX()){
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
