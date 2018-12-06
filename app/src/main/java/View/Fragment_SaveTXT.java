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

import static Controller.ProcessamentoPPS.gravar_epocas;
import static Controller.ProcessamentoPPS.gravar_resultados;
import static Controller.ProcessamentoPPS.send_txt;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_SaveTXT.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_SaveTXT#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_SaveTXT extends Fragment {

    private OnFragmentInteractionListener mListener;

    public Fragment_SaveTXT() {
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
    public static Fragment_SaveTXT newInstance(String param1, String param2) {
        Fragment_SaveTXT fragment = new Fragment_SaveTXT();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save_txt, container, false);

        Button buttonEpch = view.findViewById(R.id.btnEpch);
        buttonEpch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gravar_epocas(getContext())) {
                    Toast.makeText(getContext(), "Arquivo de épocas gravado com sucesso!", Toast.LENGTH_LONG).show();
                    send_txt();
                }else{
                    Toast.makeText(getContext(), "Erro ao gravar o arquivo!", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button buttonResult = view.findViewById(R.id.btnResult);
        buttonResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gravar_resultados(getContext())) {
                    Toast.makeText(getContext(), "Arquivo de resultados gravado com sucesso!", Toast.LENGTH_LONG).show();
                    send_txt();
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
