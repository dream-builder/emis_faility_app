package org.sci.rhis.connectivityhandler;

import android.app.Activity;

public class AsyncSendingStatusOfDBOperation extends SendPostRequestAsyncTask {

    public AsyncSendingStatusOfDBOperation(AsyncCallback origin, Activity activity) {
        super(origin,activity);
    }

    @Override
    protected String doOfflineJobs(String jsonRequest) {
        String result="Failed";
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        dismissProgressDialog();
        getCallee().callbackAsyncTask(result);
    }

}
