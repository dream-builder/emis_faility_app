package org.sci.rhis.connectivityhandler;

import android.app.Activity;
import android.database.SQLException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CreatePreg;
import org.sci.rhis.db.dbhelper.CreateRegNo;
import org.sci.rhis.db.dbhelper.ImmunizationTT;
import org.sci.rhis.db.dbhelper.RetrieveRegNo;
import org.sci.rhis.db.dbhelper.UpdatePreg;
import org.sci.rhis.utilities.Utilities;

/**
 * Created by jamil.zaman on 14/08/2015.
 */
public class AsyncPregnancyInfoUpdate extends SendPostRequestAsyncTask{
    String mName;
    private final String LOGTAG = "FWC-SEARCH-ASYNCTASK";

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getName() {
        return mName;
    }
    public AsyncPregnancyInfoUpdate(AsyncCallback cb, Activity activity) {
        super(cb, activity);
    }

    @Override
    protected String doOfflineJobs(String jsonRequest) {
        return jsonRequest;
    }

    @Override
    protected void onPostExecute(String result) {
        dismissProgressDialog();

        try {
            JSONObject pregnancyInfo;
            JSONObject pregInfo = new JSONObject(result);
            if (result == null) {
                Log.e(LOGTAG, "No network Response");
            } else if(pregInfo.has("netStatus") && pregInfo.getString("netStatus").equals("OFFLINE")) {
                Log.d(LOGTAG, "No Network - System Offline");

                pregInfo.put("serviceCategory", 1);

                /**
                 * creation of new pregnancy information
                 */
                if(pregInfo.get("pregNo").equals("")){

                    pregnancyInfo = CreatePreg.insertPregInfo(pregInfo);
                    CreateRegNo.pushReg(pregInfo, pregnancyInfo);
                }
                /**
                 * update of pregnancy information
                 */
                else{

                    pregnancyInfo = UpdatePreg.updatePregInfo(pregInfo);
                    RetrieveRegNo.pullReg(pregInfo, pregnancyInfo);
                }

                /**
                 * insert new tt information
                 */
                ImmunizationTT.pushImmunizationHistory(pregInfo);

                pregnancyInfo.put("cHealthID", pregInfo.get("healthId"));
                ImmunizationTT.getImmunizationHistory(pregnancyInfo);

                //System.out.println(pregnancyInfo);

                result = pregnancyInfo.toString();
                //result = getDetailInfo(searchJson).toString();
                Log.d(LOGTAG, String.format("Offline Response Received:%s", result));
            } else {
                Log.d(LOGTAG, String.format("Network Response Received:%s", result));
            }
            getCallee().callbackAsyncTask(result);
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
    }

    public JSONObject updatePregInfo(JSONObject pregInfo) throws JSONException{

        return UpdatePreg.updatePregInfo(pregInfo);

    }

    public JSONObject insertPregInfo(JSONObject result) {
        JSONObject json = result;
        try {
            //json = new JSONObject(result);
        } catch (Exception jse) {
            Utilities.printTrace(jse.getStackTrace());
        }

        return json;
    }
}
