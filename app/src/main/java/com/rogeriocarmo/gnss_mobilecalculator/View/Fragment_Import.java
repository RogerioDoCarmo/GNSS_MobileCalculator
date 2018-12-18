package com.rogeriocarmo.gnss_mobilecalculator.View;

import android.annotation.SuppressLint;
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
import com.rogeriocarmo.gnss_mobilecalculator.Controller.FileHelper;
import com.rogeriocarmo.gnss_mobilecalculator.Controller.SingletronController;
import com.rogeriocarmo.gnss_mobilecalculator.R;

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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

    private void download_rinex_ftp() {
        File arquivo = null;
        try {
            arquivo = getPrivateStorageDir(getContext(),"EpheDownloaded");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String server_name = "cddis.gsfc.nasa.gov"; //ftp:// e / final retirados! TODO
        int port_number = 21;
        String user = "anonymous";
        String senha = "";
        String file_Name = "gps/data/daily/2018/304/18n/brdc3040.18n.Z";

        FTPHandler ftp = null;

        try {
            ftp = new FTPHandler(getContext(), server_name,port_number,user,senha,file_Name,arquivo);
            ftp.execute();
//            FileHelper.downloadAndSaveFile(server_name,port_number,user,senha,file_Name,arquivo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String name_extracted = file_Name.split("/")[6];
        String new_name = name_extracted.substring(0,name_extracted.length() - 2);

        File descompactado = null;
        try {
            descompactado = getPrivateStorageDir(getContext(),new_name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File newFile = ftp.getmNewFile();

        try {
            FileInputStream fin = new FileInputStream(newFile);
            BufferedInputStream in = new BufferedInputStream(fin);

            FileOutputStream out = new FileOutputStream(descompactado);
            ZCompressorInputStream zIn = new ZCompressorInputStream(in);
            final byte[] buffer = new byte[(int) newFile.length() / Byte.SIZE];
            int n = 0;
            while (-1 != (n = zIn.read(buffer))) {
                out.write(buffer, 0, n);
            }
            out.close();
            zIn.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        txtOpenRINEX.setText(controller.getTXTFileAsString(descompactado));

    }

//    public  void zip( File files, File zipFile ) throws IOException {
//        final int BUFFER_SIZE = 2048;
//
//        BufferedInputStream origin = null;
//        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
//
//        try {
//            byte data[] = new byte[BUFFER_SIZE];
//
//
//                FileInputStream fileInputStream = new FileInputStream( files );
//
//                origin = new BufferedInputStream(fileInputStream, BUFFER_SIZE);
//
//                String filePath = files.getAbsolutePath();
//
//                try {
//                    ZipEntry entry = new ZipEntry( filePath.substring( filePath.lastIndexOf("/") + 1 ) );
//
//                    out.putNextEntry(entry);
//
//                    int count;
//                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
//                        out.write(data, 0, count);
//                    }
//                }
//                finally {
//                    origin.close();
//                }
//
//        }
//        finally {
//            out.close();
//        }
//    }

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

                controller.carregar_loger(LOG_fileName, LOG_directory);
                controller.carregar_RINEX(RINEX_fileName, RINEX_directory);

                // carregar rinex
                if (controller.isLogOpen() && controller.isRINEXOpen()){
                    Toast.makeText(getContext(), "Iniciando processamento...", Toast.LENGTH_SHORT).show(); //todo por 1 progress bar
                    controller.processamento_completo();
                    Toast.makeText(getContext(), "Processamento concluído!!!", Toast.LENGTH_LONG).show();
                    definir_sidebar_ativa();
                }

            }
        });

        Button btnTeste = view.findViewById(R.id.btnTeste);
        btnTeste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()){
                    download_rinex_ftp();
                }else{
                    show_dialog_noInternet();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        btnOpenRINEX.setEnabled(false);
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
