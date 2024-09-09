package org.sci.rhis.connectivityhandler;

import android.app.Activity;
import android.content.ContentValues;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.utilities.GlobalActivity;
import org.sci.rhis.utilities.SharedPref;
import org.sci.rhis.utilities.Utilities;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by jamil.zaman on 16/08/2015.
 */
public class AsyncLoginTask extends SendPostRequestAsyncTask {
    //private DatabaseWrapper db;
    final private String LOGTAG = "FWC-LOGIN-TASK";
    final private String LOGIN_TABLE = "ProviderDB";
    private JSONObject lastQuery = null;

    public AsyncLoginTask(AsyncCallback cb, Activity activity) {
        super(cb, activity);
    }



    @Override
    protected String doInBackground(String... params) {
        String queryString = params[0];
        String servlet = params[1];
        String jsonRootkey = params[2];
        try {
            lastQuery = new JSONObject(queryString);

        } catch (JSONException jse) {
            Utilities.printTrace(jse.getStackTrace());
        } catch (Exception sqe) {
            Log.e(LOGTAG, String.format("Exception - %s", sqe.toString()));
            Utilities.printTrace(sqe.getStackTrace(), 10);
        }

        String response = super.doInBackground( params);
        return response;
    }

    @Override
    protected String doOfflineJobs(String jsonRequest) {
        String result=null;
        try {
            JSONObject jso = new JSONObject(jsonRequest);
            if (jsonRequest == null) {
                Log.d(LOGTAG, "Request is null");
            } else if(jso.has("netStatus") && jso.getString("netStatus").equals("OFFLINE")) {
                Log.d(LOGTAG, "No Network - System Offline");

                String query = "select * from providerdb where provcode="+jso.getString("uid")+
                        " and provpass='"+jso.getString("upass")+"'";

                Cursor queryResult = CommonQueryExecution.executeQueryForCursor(query);

                int resultcount = queryResult.getCount();

                if ( resultcount > 0 ) {
                    queryResult.moveToFirst();
                    Log.d(LOGTAG, String.format("Provider = %s, pass = %s, response =%s",
                            queryResult.getString(queryResult.getColumnIndex("provcode")),
                            queryResult.getString(queryResult.getColumnIndex("provpass")),
                            queryResult.getString(queryResult.getColumnIndex("lastresponse"))));

                    result = queryResult.getString(queryResult.getColumnIndex("lastresponse"));
                } else { //login failed
                    JSONObject providerInfo = new JSONObject();

                    providerInfo.put("ProvName", "");
                    providerInfo.put("ProvCode", "");
                    providerInfo.put("FacilityName", "");
                    providerInfo.put("loginStatus", false);

                    result = providerInfo.toString();
                }
            }
            return new JSONObject(result).put("netStatus",false).toString();
        } catch (Exception jse) {
            System.out.println("JSON Exception Thrown:\n " );
            jse.printStackTrace();
            return "";
        }

    }

    @Override
    protected void onPostExecute(String result) {
        dismissProgressDialog();

        try {
            JSONObject jso = new JSONObject(result);
            if(!jso.has("netStatus")){
                Log.d(LOGTAG, String.format("System ONLINE, Network Response Received:%s", result));

                String query = "select * from providerdb where provcode="+lastQuery.getString("uid");

                Cursor queryResult = CommonQueryExecution.executeQueryForCursor(query);

                int queryResultCount = queryResult.getCount();
                if(queryResultCount == 0) {
                    String values = String.format(Locale.ENGLISH," Values ('%s','%s', '%s');", lastQuery.getString("uid"), lastQuery.getString("upass"), result);
                    CommonQueryExecution.executeQuery("INSERT INTO " + LOGIN_TABLE + values );
                    Log.i(LOGTAG, "Inserted data successfully....");
                } else {
                    //update providerDB only when login is successful
                    if(jso.getBoolean("loginStatus"))
                    {
                        SharedPref.clearCrashDetail(GlobalActivity.context);//clearing last crash detail if any
                        ContentValues updateValues = new ContentValues();
                        updateValues.put("provpass", lastQuery.getString("upass"));
                        updateValues.put("lastresponse", result);

                        if( db.myDataBase.update(
                                "providerdb",
                                updateValues,
                                new String("provcode = ?"),
                                new String[]{lastQuery.getString("uid")}
                        )  == 0 ) {
                            Log.e(LOGTAG, "Could not update database");
                        }
                    }

                }

            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, String.format("JSON Exception - %s", jse.getMessage()));
            Utilities.printTrace(jse.getStackTrace());
        } catch (Exception sqe) {
            Log.e(LOGTAG, String.format("Exception - %s", sqe.toString()));
            Utilities.printTrace(sqe.getStackTrace(), 10);
        }

        getCallee().callbackAsyncTask(result);

    }

    public DatabaseWrapper getDatabaseWrapper() {
        return db;
    }
}
