package com.rogeriocarmo.gnss_mobilecalculator.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rogeriocarmo.gnss_mobilecalculator.Controller.FTPHandler;
import com.rogeriocarmo.gnss_mobilecalculator.Controller.SingletronController;
import com.rogeriocarmo.gnss_mobilecalculator.R;

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.rogeriocarmo.gnss_mobilecalculator.Controller.FileHelper.getPrivateStorageDir;
import static com.rogeriocarmo.gnss_mobilecalculator.View.Activity_Main.definir_sidebar_ativa;


/**
 * A simple {@link Fragment} subclass.
 * Activities that Fragment_Import this fragment must implement the
 * {@link Fragment_ShowResults.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Import#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Import extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String LOG_fileName;
    private String LOG_directory;
    private String RINEX_fileName;
    private String RINEX_directory;
    Dialog_FileOpen FileOpenDialog_LOG;
    Dialog_FileOpen FileOpenDialog_RINEX;

    SingletronController controller;

    Button btnOpenLOG;
    TextView txtOpenLOG;
    Button btnOpenRINEX;
    TextView txtOpenRINEX;
    Button btnExecutar;
    ProgressDialog progressDialog;

    File downloadedFile;
    File extractedFile;

    public Fragment_Import() {
        // Required empty public constructor
    }

    private void open_logger() {
        FileOpenDialog_LOG =  new Dialog_FileOpen(getContext(), "FileOpen",
                new Dialog_FileOpen.SimpleFileDialogListener() {
                    @Override
                    public void onChosenDir(String chosenDir) {
                        // The code in this function will be executed when the dialog OK button is pushed
//                        String m_chosen = chosenDir;
                        LOG_fileName = FileOpenDialog_LOG.getSelected_File_Name();
                        LOG_directory = FileOpenDialog_LOG.getSelected_File_Directory();

                        txtOpenLOG.setText(LOG_fileName);
                        txtOpenLOG.setTextColor(Color.GREEN);

                        btnOpenRINEX.setEnabled(true);
                    }
                });
        //You can change the default filename using the public variable "Default_File_Name"
        FileOpenDialog_LOG.Default_File_Name = "";
        FileOpenDialog_LOG.chooseFile_or_Dir();
    }

    private void open_RINEX() {
        FileOpenDialog_RINEX =  new Dialog_FileOpen(getContext(), "FileOpen",
                new Dialog_FileOpen.SimpleFileDialogListener() {
                    @Override
                    public void onChosenDir(String chosenDir) {
                        // The code in this function will be executed when the dialog OK button is pushed
//                        String m_chosen = chosenDir;
                        RINEX_fileName = FileOpenDialog_RINEX.getSelected_File_Name();
                        RINEX_directory = FileOpenDialog_RINEX.getSelected_File_Directory();

                        txtOpenRINEX.setText(RINEX_fileName);
                        txtOpenRINEX.setTextColor(Color.GREEN);

                        btnExecutar.setEnabled(true);
                    }
                });
        //You can change the default filename using the public variable "Default_File_Name"
        FileOpenDialog_RINEX.Default_File_Name = "";
        FileOpenDialog_RINEX.chooseFile_or_Dir();
    }

    private void download_RINEX_FTP() {
        downloadedFile = null;
        try {
            downloadedFile = getPrivateStorageDir(getContext(),"EpheFTP.18n.Z");
            if (!downloadedFile.exists()){
                downloadedFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String server_name = "cddis.gsfc.nasa.gov"; // TODO CRIAR CONSTANTS
        int port_number = 21;
        String user = "anonymous";
        String senha = "";
        String fileName_toDownload = "gps/data/daily/2018/304/18n/brdc3040.18n.Z";

        FTPHandler ftp;

        try {
            ftp = new FTPHandler(getContext(), server_name, port_number, user, senha, fileName_toDownload, downloadedFile);
            ftp.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String name_extracted = fileName_toDownload.split("/")[6];
        String new_name = name_extracted.substring(0,name_extracted.length() - 2);

        extractedFile = null;
        try {
            extractedFile = getPrivateStorageDir(getContext(),new_name);
            if (!extractedFile.exists()){
                extractedFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        RINEX_fileName = new_name;
        RINEX_directory = extractedFile.getAbsoluteFile().getParent();

        txtOpenRINEX.setText(new_name);
        txtOpenRINEX.setTextColor(Color.GREEN);

        btnExecutar.setEnabled(true);
    }

    private void convert_RINEX() {
        try {
            FileInputStream fin = new FileInputStream(downloadedFile);
            BufferedInputStream in = new BufferedInputStream(fin);

            FileOutputStream out = new FileOutputStream(extractedFile);
            ZCompressorInputStream zIn = new ZCompressorInputStream(in);

            final byte[] buffer = new byte[(int) downloadedFile.length() / Byte.SIZE];

            int n = 0;
            while (-1 != (n = zIn.read(buffer))) {
                out.write(buffer, 0, n);
            }

            out.close();
            zIn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void show_dialog_noInternet(){
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Sem conexão com a Internet")
                .setMessage("Impossível acessar o servidor FTP")
                .setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
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
    public static Fragment_Import newInstance(String param1, String param2) {
        Fragment_Import fragment = new Fragment_Import();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import, container, false);

        txtOpenLOG = view.findViewById(R.id.txtLogName);
        btnOpenLOG = view.findViewById(R.id.idBTN_OpenLOG);
        btnOpenLOG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_logger();
            }
        });

        txtOpenRINEX = view.findViewById(R.id.txtRINEXName);
        btnOpenRINEX = view.findViewById(R.id.idBTN_OpenRINEX);
        btnOpenRINEX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_RINEX();
            }
        });

        btnExecutar = view.findViewById(R.id.btnExecutar);

        btnExecutar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller = SingletronController.getInstance();
                controller.reiniciar_dados();

                convert_RINEX(); //Unzipping RINEX file

                controller.carregar_loger(LOG_fileName, LOG_directory);
                controller.carregar_RINEX(RINEX_fileName, RINEX_directory);

                if (controller.isLogOpen() && controller.isRINEXOpen()){
                    controller.processamento_completo();

                    progressDialog = ProgressDialog.show(getContext(),
                            "Processando arquivos...",
                            "Calculando resultados");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Thread.sleep(3000); // 3 seconds

                                progressDialog.dismiss();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    definir_sidebar_ativa();
//                    Toast.makeText(getContext(), "Processamento concluído!!!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Abra os arquivos de log e efemérides!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button btnDownFTP = view.findViewById(R.id.btnFTP);
        btnDownFTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()){
                    download_RINEX_FTP();
                }else{
                    show_dialog_noInternet();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        btnExecutar.setEnabled(false);
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
