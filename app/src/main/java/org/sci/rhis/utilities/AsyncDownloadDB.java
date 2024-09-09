package org.sci.rhis.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.Calendar;

/**
 * Created by hajjaz.ibrahim on 2/28/2018.
 */

public class AsyncDownloadDB extends AsyncTask<String, Integer, String>{

    // Progress Dialog
    ProgressDialog mProgressDialog;
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private AsyncCallback callee;

    public AsyncCallback getCallee() {
        return callee;
    }

    public AsyncDownloadDB(AsyncCallback cb, Activity activity) {
        callee = cb;
        this.context = activity;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // instantiate ProgressDialog
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Downloading Database...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... sUrl) {
        JSONObject jsonResult = new JSONObject();
        String result = "";
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(Environment
                    .getExternalStorageDirectory().toString()
                    + "/rhis_fwc/databases/Downloaded_DB.zip");

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
            result = "Success";
        } catch (Exception e) {
            result = "Failed";
            System.out.println("Exception : "+e.toString());
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ex) {
                result = "IOException";
                System.out.println("IOException : "+ex.toString());
            }

            if (connection != null)
                connection.disconnect();
        }

        try {
            jsonResult.put("Result",result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result == null || result.length() == 0) {
            System.out.println("Download error: " + result);
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
        }
        else if(result.equals("Failed")){
            System.out.println("Download Failed: " + result);
            Toast.makeText(context, "Download Failed: " + result, Toast.LENGTH_LONG).show();
        }
        else if(result.equals("Success")){
            System.out.println("File downloaded.");
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }
        getCallee().callbackAsyncTask(result);
    }


}

