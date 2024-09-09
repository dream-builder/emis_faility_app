package org.sci.rhis.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.sci.rhis.connectivityhandler.AsyncCallback;

public class CustomAsynctask extends AsyncTask<String,String,String> {

    private Activity activity;
    private AsyncCallback getCallee;
    private String loaderText;
    ProgressDialog pDialog;

    public CustomAsynctask(AsyncCallback asyncCallback,Activity eContext, String eLoaderText){
        activity = eContext;
        loaderText = eLoaderText;
        getCallee = asyncCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(loaderText);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        //set empty for distinguishing the callbackAsynctask to do a special task in doInBackground
        getCallee.callbackAsyncTask("");
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
        //setting a constant string for distinguishing this method to do the task after doInBackground
        getCallee.callbackAsyncTask("onPostExecute");
    }
}
