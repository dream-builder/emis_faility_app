package org.sci.rhis.connectivityhandler;

import android.app.Activity;
import android.content.Context;

import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.SendPostRequestAsyncTask;

/**
 * Created by arafat.hasan on 2/25/2016.
 */
public class AsyncMisMNCHLoad extends SendPostRequestAsyncTask {

    public AsyncMisMNCHLoad(AsyncCallback origin, Context eContext) {
        super(origin);
        super.setActivity((Activity)eContext);
    }

    @Override
    protected String doOfflineJobs(String jsonRequest) {
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
