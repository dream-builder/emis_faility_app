package org.sci.rhis.connectivityhandler;

import android.app.Activity;
import android.database.SQLException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.NonRegisteredInfo;
import org.sci.rhis.utilities.Utilities;

/**
 * Created by mohammod.alamin on 10/20/2015.
 */
public class AsyncNonRegisterClientInfoUpdate  extends SendPostRequestAsyncTask{
    private final String LOGTAG = "FWC-NRC-ASYNCTASK";

    public AsyncNonRegisterClientInfoUpdate(AsyncCallback cb, Activity activity) {
        super(cb, activity);
    }

    protected String doOfflineJobs(String queryString) {
        String result = null;
        try {
            JSONObject searchJson = new JSONObject(queryString);
            result = NonRegisteredInfo.getDetailInfo(searchJson).toString();
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