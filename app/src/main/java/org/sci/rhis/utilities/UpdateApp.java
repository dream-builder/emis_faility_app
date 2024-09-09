package org.sci.rhis.utilities;

/**
 * Created by israt.jahan on 2/15/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.sci.rhis.fwc.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by israt.jahan on 2/15/2016.
 */
public class UpdateApp extends AsyncTask<String,String,String> {
    private Context context;
    ProgressDialog pDialog;
    public void setContext(Context contextf){
        context = contextf;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("এপ্লিকেশন আপডেট হচ্ছে। অপেক্ষা করুন...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            int lenghtOfFile = c.getContentLength();

            File sdCard = Environment.getExternalStorageDirectory();
            String fileStr = sdCard.getAbsolutePath() + "/Download";// + "app-release.apk";
            //File file = new File(fileStr, "app-release.apk");//change the name

            //String PATH = "/mnt/sdcard/Download/";
            File file = new File(fileStr);
            if(!file.exists()) {
                file.mkdirs();
            }

            File outputFile = new File(file, "app-release.apk"); // update.apk
            if(outputFile.exists()){
                outputFile.delete();
            }

            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1;
            long total = 0;
            while ((len1 = is.read(buffer)) != -1) {
                total += len1;
                publishProgress(""+(int)((total*100)/lenghtOfFile));

                fos.write(buffer, 0, len1);
            }

            fos.close();
            is.close();



            /////////////

            //atualizaApp.setContext(getApplicationContext());
            //atualizaApp.execute("http://www.mamoni.net:8080/update/app-release.apk");//change the address of the apk





            ///////////


        } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }

    protected void onProgressUpdate(String... progress) {
        pDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String file_url) {
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            File file = new File("/mnt/sdcard/Download/app-release.apk");
            Uri fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/app-release.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!  // update.ap
            context.startActivity(intent);
        }

    }
}