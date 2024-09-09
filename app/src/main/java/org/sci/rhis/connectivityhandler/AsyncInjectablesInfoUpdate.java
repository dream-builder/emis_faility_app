package org.sci.rhis.connectivityhandler;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.db.womaninjectable.WomanInjectableService;

/**
 * Created by arafat.hasan on 2/7/2016.
 */
public class AsyncInjectablesInfoUpdate extends SendPostRequestAsyncTask{

    private final String LOGTAG = "FWC-INJECT-ASYNCTASK";

    public AsyncInjectablesInfoUpdate(AsyncCallback origin, Context eContext) {
        super(origin);
        super.setActivity((Activity)eContext);
    }

    @Override
    protected String doOfflineJobs(String jsonRequest) {
        String result=null;
        try {
            JSONObject json = new JSONObject(jsonRequest);
            Log.d(LOGTAG, "No Network - System Offline");

            result = WomanInjectableService.getDetailInfo(json).toString();
            Log.d(LOGTAG, String.format("Offline Response Received:%s", result));

        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Exception Thrown:\n " );
            jse.printStackTrace();
        } catch (SQLException jse) {
            Log.e(LOGTAG, "SQL Exception Thrown:\n " );
            Utilities.printTrace(jse.getStackTrace());
        } catch (Exception jse) {
            Log.e(LOGTAG, "Exception Thrown:\n " );
            jse.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        dismissProgressDialog();
        getCallee().callbackAsyncTask(result);
    }
}
