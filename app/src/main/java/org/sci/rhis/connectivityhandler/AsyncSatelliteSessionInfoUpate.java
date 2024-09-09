package org.sci.rhis.connectivityhandler;

import android.app.Activity;

public class AsyncSatelliteSessionInfoUpate extends SendPostRequestAsyncTask {

    private final String LOGTAG = "FWC-SSPINFO-ASYNCTASK";

    public AsyncSatelliteSessionInfoUpate(AsyncCallback origin, Activity activity) {
        super(origin, activity);
    }

    @Override
    protected String doOfflineJobs(String jsonRequest) {
        //its fully online based, so nothing has to be done here
        return jsonRequest;
    }

    @Override
    protected void onPostExecute(String result) {
        dismissProgressDialog();
        getCallee().callbackAsyncTask(result);

    }
}

