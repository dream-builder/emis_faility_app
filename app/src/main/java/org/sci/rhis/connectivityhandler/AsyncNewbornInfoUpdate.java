package org.sci.rhis.connectivityhandler;

import android.app.Activity;
import android.database.SQLException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.db.newborn.NewbornInfo;

/**
 * Created by mohammod.alamin on 10/4/2015.
 */
public class AsyncNewbornInfoUpdate extends SendPostRequestAsyncTask{

    private final String LOGTAG = "FWC-NEWBORN-ASYNCTASK";
    //AsyncClientInfoUpdate(Activity activity) { super(activity);}
    public AsyncNewbornInfoUpdate(AsyncCallback cb, Activity activity) {
        super(cb, activity);
    }

    @Override
    protected String doOfflineJobs(String jsonRequest) {
        String result=null;
        try {
            JSONObject json = new JSONObject(jsonRequest);
            result = NewbornInfo.getDetailInfo(json).toString();
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