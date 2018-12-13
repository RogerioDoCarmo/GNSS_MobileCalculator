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

import com.rogeriocarmo.gnss_mobilecalculator.Controller.FileHelper;
import com.rogeriocarmo.gnss_mobilecalculator.R;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Activities that Fragment_Import this fragment must implement the
 * {@link Fragment_SaveTXT.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Import#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Import extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String dirFile;
    private String fileName;
    SimpleFileDialog FileOpenDialog;

    public Fragment_Import() {
        // Required empty public constructor
    }

    private void abrir_arquivo(){
//        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
//        FileDialog fileDialog = new FileDialog(this, mPath, ".txt");
//        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
//            public void fileSelected(File file) {
//                Log.d(getClass().getName(), "selected file " + file.toString());
//            }
//        });
//        //fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
//        //  public void directorySelected(File directory) {
//        //      Log.d(getClass().getName(), "selected dir " + directory.toString());
//        //  }
//        //});
//        //fileDialog.setSelectDirectoryOption(false);
//        fileDialog.showDialog();

        /////////////////////////////////////////////////////////////////////////////////////////////////
        //Create FileOpenDialog and register a callback
        /////////////////////////////////////////////////////////////////////////////////////////////////
        final String copy;

         FileOpenDialog =  new SimpleFileDialog(getContext(), "FileOpen",
                new SimpleFileDialog.SimpleFileDialogListener() {
                    @Override
                    public void onChosenDir(String chosenDir)
                    {
                        // The code in this function will be executed when the dialog OK button is pushed
                        String m_chosen = chosenDir;
//                        copy = m_chosen;
                        File newFile = new File(m_chosen);
//                        fileCopy = newFile;
                        Toast.makeText(getContext(), "Chosen FileOpenDialog File: " +
                                m_chosen, Toast.LENGTH_LONG).show();

                        fileName = FileOpenDialog.getSelected_File_Name();
                        dirFile = FileOpenDialog.getSelected_File_Directory();
                    }
                });

        //You can change the default filename using the public variable "Default_File_Name"
        FileOpenDialog.Default_File_Name = "";
        FileOpenDialog.chooseFile_or_Dir();

        /////////////////////////////////////////////////////////////////////////////////////////////////



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
    public static Fragment_Import newInstance(String param1, String param2) {
        Fragment_Import fragment = new Fragment_Import();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        abrir_arquivo();
        Toast.makeText(getContext(), "LALALA", Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import, container, false);

        Button btn = view.findViewById(R.id.idBtn);
        TextView txt = view.findViewById(R.id.txtImport);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "FileNAMEEE: " + fileName, Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), "FilePASTAA: " + dirFile, Toast.LENGTH_LONG).show();

                String leitura = FileHelper.readTXTFile(fileName,dirFile);
                txt.setText(leitura);

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
