package org.sci.rhis.connectivityhandler;

import android.app.Activity;
import android.database.SQLException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.db.pncchild.PNCVisitChild;
import org.sci.rhis.db.pncmother.PNCVisitMother;

/**
 * Created by mohammod.alamin on 10/29/2015.
 */
public class AsyncPNCInfoUpdate extends SendPostRequestAsyncTask{

    private final String LOGTAG = "FWC-PNC-ASYNCTASK";

    public AsyncPNCInfoUpdate(AsyncCallback cb, Activity activity) {
        super(cb, activity);
    }

    @Override
    protected String doOfflineJobs(String jsonRequest) {
        String result = null;
        try {
            JSONObject json = new JSONObject(jsonRequest);
            if (jsonRequest == null) {
                Log.e(LOGTAG, "No network Response");
            } else if(json.has("netStatus") && json.getString("netStatus").equals("OFFLINE")) {
                Log.d(LOGTAG, "No Network - System Offline");
                if(json.has("pncMLoad")) {
                    result = PNCVisitMother.getDetailInfo(json).toString();
                } else if(json.has("pncCLoad")) {
                    result = PNCVisitChild.getDetailInfo(json).toString();
                } else {
                    Log.e(LOGTAG, "Missing Required Key");
                }
                Log.d(LOGTAG, String.format("Offline Response Received:%s", result));
            } else {
                Log.d(LOGTAG, String.format("Network Response Received:%s", result));
            }

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