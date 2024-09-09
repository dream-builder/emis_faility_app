package org.sci.rhis.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by hajjaz.ibrahim on 2/28/2018.
 */

public class AsyncGenerateFileToUpload extends AsyncTask<String, String, String> {

    private final String LOGTAG = "FWC-ASYNC-TASK-STATUS";
    private final int READ_TIMEOUT = 60000; //ms - 10 sec
    private final int CONNECTION_TIMEOUT = 60000; //ms - 10 sec

    protected NetworkUtility networkUtility;
    // Progress Dialog
    ProgressDialog mProgressDialog;
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private AsyncCallback callee;

    String zilla, upazilla, provCode, dbDownloadSuccess, uploadFileOrDB;

    public AsyncCallback getCallee() {
        return callee;
    }

    public AsyncGenerateFileToUpload(AsyncCallback cb, Activity activity) {
        callee = cb;
        this.context = activity;
        networkUtility = new NetworkUtility(activity);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // instantiate ProgressDialog
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Generating Unsync Data...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.setProgressPercentFormat(null);

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... sUrl) {

        provCode = sUrl[0];
        zilla = sUrl[1];
        upazilla = sUrl[2];
        dbDownloadSuccess = sUrl[3];
        uploadFileOrDB = sUrl[4];

        String result = "";
        if(uploadFileOrDB.equals(Constants.UPLOADFILE)) {
            result = QueryGenerator.generateSQLFile(context, sUrl[0]);
        }
        else if(uploadFileOrDB.equals(Constants.UPLOADDB)) {
            result = "Success";
        }


        if (result.equals("Success")) {
            publishProgress("Sending Status....");
            result = sendStatus();
            if (result.equals("acknowledged")) {
                DatabaseWrapper dbWrapper = null;
                try {
                    dbWrapper = new DatabaseWrapper(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dbWrapper.switchdatabase();

                publishProgress(uploadFileOrDB.equals(Constants.UPLOADFILE)?"Uploading Unsync Data...":"Uploading Database...");
                result = uploadFile();
            } else {
                result = sendStatus();
            }
        } else {
            if(uploadFileOrDB.equals(Constants.UPLOADFILE)) {
                result = QueryGenerator.generateSQLFile(context, sUrl[0]);
            }
            else if(uploadFileOrDB.equals(Constants.UPLOADDB)) {
                result = "Success";
            }

        }

        return result;
    }


    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result == null || result.length() == 0) {
            Toast.makeText(context, "Error: " + result, Toast.LENGTH_LONG).show();
        } else if (result.equals("Success")) {
            Toast.makeText(context, "Generating Unsync Data: " + result, Toast.LENGTH_LONG).show();
        } else if (result.equals("acknowledged")) {
            Toast.makeText(context, "Sending Status Success: " + result, Toast.LENGTH_LONG).show();
        } else if (result.equals("complete")) {
            Toast.makeText(context, "Uploading Status: " + result, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Failed: " + result, Toast.LENGTH_LONG).show();
        }
        getCallee().callbackAsyncTask(result);
    }

    private String sendStatus() {

        String checksumUploadFile = "";

        if(uploadFileOrDB.equals(Constants.UPLOADFILE)) {
            checksumUploadFile = Utilities.getMD5Checksum(Environment.getExternalStorageDirectory().toString() + "/rhis_fwc/databases/" + provCode);
        }
        else if(uploadFileOrDB.equals(Constants.UPLOADDB)) {
            String[] dbFile = {Environment.getExternalStorageDirectory().toString() + "/rhis_fwc/databases/" + "rhis_fwc.sqlite3"};
            Utilities.zip(dbFile,Environment.getExternalStorageDirectory().toString() + "/rhis_fwc/databases/" +provCode+"_"+Utilities.getDateStringDBFormat()+".zip");
            checksumUploadFile = Utilities.getMD5Checksum(Environment.getExternalStorageDirectory().toString() + "/rhis_fwc/databases/" + provCode+"_"+Utilities.getDateStringDBFormat()+".zip");
        }

        System.out.println("Checksum upload file = " + checksumUploadFile);

        JSONObject json = new JSONObject();

        try {
            json.put("providerid", provCode);
            json.put("zillaid", zilla);
            json.put("upazilaid", upazilla);
            //in case of full db upload, it is required to have the key of "uploadstatus" regardless its value
            json.put(dbDownloadSuccess.equalsIgnoreCase("success")?"downloadstatus":
                    "uploadstatus", dbDownloadSuccess);
            json.put("checksum", checksumUploadFile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String servlet = "updownstatus";
        String jsonRootkey = "info";

        String result = "";

        Log.i(LOGTAG, "Sending JSON:\t" + json);

        Log.i(LOGTAG, "RootKey:\t" + jsonRootkey + "\tServlet:\t" + servlet);

        // In a POST request, we don't pass the values in the URL.
        //Therefore we use only the web page URL as the parameter of the HttpPost argument

        URL url;
        try {

            url = new URL(Constants.getBaseUrl(context) + servlet);
            Log.d("BaseUrl: ", Constants.getBaseUrl(context) + servlet);

            if ((networkUtility != null && !networkUtility.isOnline())  //if offline OR
                /*|| isSavingLocal(servlet)*/ /*ugly temporary hack to enable saving to the localdb first*/) {
                Log.d(LOGTAG, servlet + " Servlet: Saving to Offline DB, Net status: " + (networkUtility.isOnline() ? "ONLINE" : "OFFLINE"));
                result = "Failed";
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer =
                    new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonRootkey + "=" + json);

            writer.flush();
            writer.close();
            os.close();
            //TODO - could have been online
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.d(LOGTAG, "HTTP Connection Issue Response Code: " + String.valueOf(responseCode));
                result = "Failed";
                return result;
            }

            //Read the response
            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String bufferedStrChunk;
            while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                stringBuilder.append(bufferedStrChunk);
                Log.d(LOGTAG, "Read line :" + bufferedStrChunk);
            }
            System.out.println(stringBuilder.toString());

            JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
            //result = jsonResponse.getString("downloadstatus");
            result = jsonResponse.getString("checksum");

        } catch (MalformedURLException mul) {
            Log.e(LOGTAG, String.format("URL Malformed Error: %s", mul.getMessage()));
            Utilities.printTrace(mul.getStackTrace(), 10);
        } catch (ProtocolException pe) {
            Log.e(LOGTAG, String.format("Protocol Error : %s", pe.getMessage()));
            Utilities.printTrace(pe.getStackTrace(), 10);
        } catch (IOException io) {
            Log.e(LOGTAG, String.format("IO Error: %s", io.getMessage()));
            Utilities.printTrace(io.getStackTrace(), 15);

        } catch (Exception e) {
            Log.e(LOGTAG, "Unknown Error " + e.getMessage());
            Utilities.printTrace(e.getStackTrace());
        }

        return result;
    }

    private String uploadFile() {

        String result = "";
        String servlet = "upload";
        String jsonRootkey = "info";
        String charset = "UTF-8";
        File textFile = null;

        if(uploadFileOrDB.equals(Constants.UPLOADFILE)) {
            textFile = new File(Environment.getExternalStorageDirectory().toString() + "/rhis_fwc/databases/" + provCode );
        }
        else if(uploadFileOrDB.equals(Constants.UPLOADDB)) {
            textFile = new File(Environment.getExternalStorageDirectory().toString() + "/rhis_fwc/databases/" + provCode + "_" + Utilities.getDateStringDBFormat() + ".zip");
        }


        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.


        HttpURLConnection connection = null;
        DataOutputStream out = null;

        try {
            URL url = new URL(Constants.getBaseUrl(context) + servlet);
            Log.d("BaseUrl: ", Constants.getBaseUrl(context) + servlet);

            connection = (HttpURLConnection) url.openConnection();
//            connection.setReadTimeout(READ_TIMEOUT);
//            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("CustomHeader", provCode+":"+zilla+":"+upazilla);
            connection.setDoOutput(true);
            //connection.connect();

            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

            // Send text file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("content-disposition: form-data; name=\"file\"; filename=\"" + textFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: \"file\"; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!  text/plain
            writer.append(CRLF).flush();
            FileUtils.copyFile(textFile, output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            writer.append("--" + boundary + "--").append(CRLF).flush();

            // ------------------ read the SERVER RESPONSE

            // Request is lazily fired whenever you need to obtain information about response.
            int responseCode = ((HttpURLConnection) connection).getResponseCode();
            System.out.println("Response Code : " + responseCode); // Should be 200
            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println("Response : " + sb.toString());
            JSONObject jsonResponse = new JSONObject(sb.toString());
            result = jsonResponse.getString("uploadstatus");


        } catch (MalformedURLException mul) {
            Log.e(LOGTAG, String.format("URL Malformed Error: %s", mul.getMessage()));
            Utilities.printTrace(mul.getStackTrace(), 10);
        } catch (ProtocolException pe) {
            Log.e(LOGTAG, String.format("Protocol Error : %s", pe.getMessage()));
            Utilities.printTrace(pe.getStackTrace(), 10);
        } catch (IOException io) {
            Log.e(LOGTAG, String.format("IO Error: %s", io.getMessage()));
            Utilities.printTrace(io.getStackTrace(), 15);

        } catch (Exception e) {
            Log.e(LOGTAG, "Unknown Error " + e.getMessage());
            Utilities.printTrace(e.getStackTrace());
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException ex) {
                result = "IOException";
                System.out.println("IOException : " + ex.toString());
            }

            if (connection != null)
                connection.disconnect();
        }

        return result;
    }

}

