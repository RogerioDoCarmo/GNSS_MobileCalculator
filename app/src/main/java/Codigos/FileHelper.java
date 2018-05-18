package com.rogeriocarmo.simplecrud;

/**
 * Created by Rogerio on 28/03/2018.
 */

import com.rogeriocarmo.gnss_mobilecalculator.R;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Tan on 2/18/2016.
 */
public class FileHelper {

//    public static String readFromRawAssets(Context context, String filename) throws IOException { // FIXME Codigo antigo
////        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
////        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.lista)));
////
//        // do reading, usually loop until end of file reading
//        StringBuilder sb = new StringBuilder();
//        String mLine = reader.readLine();
//        while (mLine != null) {
//            sb.append(mLine + "\n"); // process line
//            mLine = reader.readLine();
//        }
//        reader.close();
//        return sb.toString();
//    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }



    public static File getPrivateStorageDir(Context context, String fileName) throws IOException {
        // Get the directory for the app's private pictures directory.
        File file = null;
        file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName); // TODO
// !file.getParentFile().mkdirs())
        if (!file.exists()) { // fixme
            file.getParentFile().mkdir();
            file.createNewFile();

            Log.i("OK", "Arquivo foi criado!!!!!!!!!!!!!"); // FIXME
        }
        return file;
    }

    public static void writeTxtFile2External(File file, String[] content){
        try {
//            if (file.isFile()){
                FileOutputStream fileOutput = null;
                fileOutput = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);

                for (int i = 0; i < content.length; i++){
                    outputStreamWriter.write(content[i]);
                }

                outputStreamWriter.flush();
                fileOutput.getFD().sync();
                outputStreamWriter.close();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}