package com.priyankanandiraju.teleprompter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.priyankanandiraju.teleprompter.utils.TeleprompterFile;

import java.io.File;
import java.io.FileOutputStream;

import static com.priyankanandiraju.teleprompter.utils.Constants.TXT_EXTENSION;

/**
 * Created by priyankanandiraju on 8/23/17.
 */

public class DownloadAsyncTask extends AsyncTask<Void, Void, Boolean> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private TeleprompterFile teleprompterFile;

    public DownloadAsyncTask(Context context, TeleprompterFile file) {
        mContext = context;
        teleprompterFile = file;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Long currentTs = System.currentTimeMillis() / 1000;
        String timestamp = currentTs.toString();

        String filename = teleprompterFile.getTitle() + timestamp + TXT_EXTENSION;
        String content = teleprompterFile.getContent();
        FileOutputStream outputStream;
        File file;

        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        String status;
        if (aBoolean) {
            status = mContext.getString(R.string.download_success);
        } else {
            status = mContext.getString(R.string.download_fail);
        }
        Toast.makeText(mContext, status, Toast.LENGTH_SHORT).show();
    }
}