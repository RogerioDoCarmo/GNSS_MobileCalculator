package com.rogeriocarmo.gnss_mobilecalculator.Controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class FTPHandler extends AsyncTask<String, String, String> {

    private String mServerName;
    private Integer mPortNumber;
    private String mUserName;
    private String mPassword;
    private String mFileName;

    public File getmNewFile() {
        return mNewFile;
    }

    private File mNewFile;

    private String resp;
    private Context mContext;
    ProgressDialog progressDialog;


    public FTPHandler(Context context, String server, int portNumber, String user, String password, String fileName, File localFile){
        this.mContext = context;
        this.mServerName = server;
        this.mPortNumber = portNumber;
        this.mUserName = user;
        this.mPassword = password;
        this.mFileName = fileName;
        this.mNewFile = localFile;
    }


    private String downloadAndSaveFile() throws IOException {

        FTPClient ftp = null;

        String LOG_TAG = "FTP_TESTE";

        try {
            ftp = new FTPClient();

            ftp.connect(mServerName, mPortNumber);
            ftp.enterLocalPassiveMode();
            Log.d(LOG_TAG, "Connected. Reply: " + ftp.getReplyString());

            ftp.login(mUserName, mPassword);

            ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

            Log.d(LOG_TAG, "Logged in");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            Log.d(LOG_TAG, "Downloading");
            ftp.enterLocalPassiveMode();

            boolean success = false;

            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                    mNewFile))) {
                success = ftp.retrieveFile(mFileName, outputStream);
            }

//            return success; //FIXME
        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
            return resp;
        }
        finally {
            if (ftp != null) {
                ftp.logout();
                ftp.disconnect();
            }
        }
        return null;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param strings The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected String doInBackground(String... strings) {
        try {
            downloadAndSaveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
        progressDialog.dismiss();
//        finalResult.setText(result); //FIXME
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(mContext,
                "Download de RINEX de Navegação",
                "Efetuando o downlaod");
        publishProgress("Efetuando o Downlaod");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            resp = e.getMessage();
        }
    }


    @Override
    protected void onProgressUpdate(String... text) {
//        finalResult.setText(text[0]);
    }

}
