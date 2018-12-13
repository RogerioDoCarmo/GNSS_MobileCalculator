package com.rogeriocarmo.gnss_mobilecalculator.Controller;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileHelper {
    /* Checks if external storage is available for read and write */

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
               Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static File getPrivateStorageDir(Context context, String dirName) throws IOException {
        // Get the directory for the app's private pictures directory.
        File file = null;
        file =  new File(context.getExternalFilesDir(null), dirName); // TODO
// !file.getParentFile().mkdirs())
        if (!file.exists()) { // fixme
            file.getParentFile().mkdir();
            file.createNewFile();

            Log.i("OK", "Arquivo foi criado!!!!!!!!!!!!!"); // FIXME
        }
        return file;
    }

    public static void writeTextFile2External(File file, String[] content) {
        try {
            FileOutputStream fileOutput = null;
            fileOutput = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);

            for (int i = 0; i < content.length; i++){
                outputStreamWriter.write(content[i]);
            }

            outputStreamWriter.flush();
            fileOutput.getFD().sync();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a text file and return its content as a String
     * @param fileName The file name with the extension
     * @param directory The directory of the file on pohone
     * @return The content of file as a String
     */
    public static String readTXTFile(String fileName, String directory) {
        //Get the text file
        File file = new File(directory, fileName);

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

        return text.toString();
    }
}
