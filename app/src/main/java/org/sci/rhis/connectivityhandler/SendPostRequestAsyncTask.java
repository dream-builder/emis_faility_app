package org.sci.rhis.connectivityhandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.sci.rhis.db.utilities.HandleDbLocking;
import org.sci.rhis.fwc.R;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.SharedPref;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.utilities.NetworkUtility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by jamil.zaman on 13/08/2015.
 */
public abstract class SendPostRequestAsyncTask extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    private Activity activity;
    private AsyncCallback callee;
    protected NetworkUtility networkUtility;
    protected DatabaseWrapper db;

    private  final String LOGTAG   = "FWC-ASYNC-TASK-NET";
    private final int READ_TIMEOUT = 60000; //ms - 60 sec
    private final int CONNECTION_TIMEOUT  = 60000; //ms - 60 sec


    protected Activity getActivity() { return activity; }
    public void setActivity(Activity activity) {this.activity = activity;}

    protected Context getContext() { return activity; }
    public SendPostRequestAsyncTask() {
        this.activity = null;
    }

    public AsyncCallback getCallee() {
        return callee;
    }

    public SendPostRequestAsyncTask(AsyncCallback origin) {

        callee = origin;
    }

    public SendPostRequestAsyncTask(AsyncCallback origin, Activity activity) {

        callee = origin;
        this.activity = activity;
        networkUtility = new NetworkUtility(activity);
        try {
            db = new DatabaseWrapper(activity);
        } catch (IOException ioe) {
            Log.e(LOGTAG, "Could not open Database " + ioe.getMessage());
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(activity!=null){
            pDialog = new ProgressDialog(activity, R.style.MyTheme);
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            pDialog.setCancelable(false);
            pDialog.show();
        }

    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject dataJSON = null;

        //lately added......................
        try {
            dataJSON = new JSONObject(params[0]);
            dataJSON.put("client", Constants.ANDROID_CLIENT_CODE);
            if(ProviderInfo.getProvider().getZillaID()!=null){
                dataJSON.put("zillaid", ProviderInfo.getProvider().getZillaID());
            }
            if(ProviderInfo.getProvider().getUpazillaID()!=null){
                dataJSON.put("upazilaid", ProviderInfo.getProvider().getUpazillaID());
            }
            if(ProviderInfo.getProvider().getUnionID()!=null){
                dataJSON.put("unionid", ProviderInfo.getProvider().getUnionID());
            }
            dataJSON.put("providerType", ProviderInfo.getProvider().getmProviderType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String queryString = dataJSON.toString();
        //.................................
        String servlet = params[1];
        String jsonRootkey = params[2];
        String result = null;

        if (params.length == 4 && params[3].equals("dismiss")) dismissProgressDialog();

        Log.i(LOGTAG, "Sending JSON:\t" + queryString);

        Log.i(LOGTAG, "RootKey:\t" + jsonRootkey + "\tServlet:\t" + servlet);

        // In a POST request, we don't pass the values in the URL.
        //Therefore we use only the web page URL as the parameter of the HttpPost argument

        URL url;
        try {

            url = new URL(Constants.getBaseUrl(activity)+servlet);
            Log.d("BaseUrl: ",Constants.getBaseUrl(activity)+servlet);

            if((networkUtility != null && !networkUtility.isOnline())  //if offline OR
                || isSavingLocal(servlet) /*ugly temporary hack to enable saving to the localdb first*/) {
                Log.d(LOGTAG, servlet + " Servlet: Saving to Offline DB, Net status: " + (networkUtility.isOnline() ? "ONLINE":"OFFLINE"));
                return setOfflineStatusForServlet(queryString, servlet);
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
            writer.write(jsonRootkey + "="+  queryString);

            writer.flush();
            writer.close();
            os.close();
            //TODO - could have been online
            int responseCode=conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.d(LOGTAG, "HTTP Connection Issue Response Code: " + String.valueOf(responseCode));
                return setOfflineStatusForServlet(queryString, servlet); //if not OK then return null
            }

            //Read the response
            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String bufferedStrChunk;
            while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                stringBuilder.append(bufferedStrChunk);
                Log.d(LOGTAG, "Read line :" + bufferedStrChunk );
            }
            return stringBuilder.toString();

        } catch(MalformedURLException mul) {
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

        //The execution reaches here only if there is exception like conenction broke down
        // Protocol error etc.
        return setOfflineStatusForServlet(queryString, servlet);
    }

    private String setOfflineStatusForServlet(String queryString, String servlet) {
        JSONObject request;
        try {
            request = new JSONObject(queryString);
            request.put("client",Constants.ANDROID_CLIENT_OFFLINE_CODE);
            request.put("netStatus", "OFFLINE");
            request.put("servlet", servlet);
            /*check and free local database*/
            //TODO: Need to check this following while loop
            while(HandleDbLocking.isLocked()) {
                try {
                    Thread.sleep(200, 500);
                } catch (InterruptedException ie) {
                    Log.e(LOGTAG, "Thread Exception: " + ie.getMessage());
                }
            }
            return doOfflineJobs(request.toString());
        } catch (JSONException jse) {
            return doOfflineJobs("{}");
        }

    }

    protected abstract String doOfflineJobs(String jsonRequest);

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if (result == null) {
                //Toast.makeText(activity, "HTTP POST is working...", Toast.LENGTH_LONG).show();
                Log.d(LOGTAG, "No network Response");
            } else if(result.equals("OFFLINE")) {
                Log.d(LOGTAG, "No Network - System Offline");
            } else {
                Log.d(LOGTAG, "Network Respose Received");
            }
            callee.callbackAsyncTask(result);
        } catch (Exception e) {
            Log.e(LOGTAG, e.toString());
            e.printStackTrace();
        }finally {
            dismissProgressDialog();
        }
    }

    protected void dismissProgressDialog(){
        if(pDialog!=null){
            if(activity!=null && pDialog.isShowing()){
                pDialog.dismiss();
            }
        }
    }

    private boolean isSavingLocal(String servlet) {
        /*servlet type determines whether to use local database*/
            if(ProviderInfo.getProvider()!=null && !servlet.equals("updownstatus")){
                return !servlet.equals("login") && ProviderInfo.getProvider()!=null
                        && ProviderInfo.getProvider().getmCsba().equals("1")
                        && ProviderInfo.getProvider().getCommunityActive().equals("1");
            }else{
                return false;
            }


    }
}
