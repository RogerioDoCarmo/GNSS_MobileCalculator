package Codigos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static Codigos.FileHelper.getPrivateStorageDir;
import static Codigos.FileHelper.isExternalStorageWritable;
import static Codigos.FileHelper.writeTextFile2External;

public class TextWritter {

    private File newFile;
    private final Context mContext;

    public TextWritter(Context mContext) {
        this.mContext = mContext;
    }

    public boolean gravar_txtSD(String[] textContent, String fileName){
        if (isExternalStorageWritable()){
            newFile = startNewFile(fileName);
            try {
                writeTextFile2External(newFile, textContent);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }

    public File startNewFile(String fileName) {
        File baseDirectory = null;
        if ( isExternalStorageWritable() ) {
            try {
                 baseDirectory = getPrivateStorageDir(mContext,fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("RINEX", "Cannot read external storage.");
            return null;
        }

        return baseDirectory;
    }

    public void send(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse(newFile.getAbsolutePath());
        intent.setDataAndType(uri, "text/plain");
        mContext.startActivity(intent);
    }

}
