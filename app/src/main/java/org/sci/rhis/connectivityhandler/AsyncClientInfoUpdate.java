package org.sci.rhis.connectivityhandler;

import android.app.Activity;
import android.database.SQLException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientGeneralInfo;
import org.sci.rhis.db.dbhelper.ClientInfo;
import org.sci.rhis.db.dbhelper.ClientMapGeneralInfo;
import org.sci.rhis.db.dbhelper.ImmunizationTT;
import org.sci.rhis.db.dbhelper.PregInfo;
import org.sci.rhis.db.dbhelper.RetrieveRegNo;
import org.sci.rhis.utilities.Utilities;

/**
 * Created by jamil.zaman on 14/08/2015.
 */
public class AsyncClientInfoUpdate extends SendPostRequestAsyncTask{
    String mName;
    static boolean searchResultGen, searchResultReg, searchResultImmu, searchResultPreg;
    private final String LOGTAG = "FWC-SEARCH-ASYNCTASK";

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getName() {
        return mName;
    }
    public AsyncClientInfoUpdate(AsyncCallback cb, Activity activity) {
        super(cb, activity);
    }

    @Override
    protected String doOfflineJobs(String jsonRequest) {
        String result = null;
        try {
            JSONObject searchJson = new JSONObject(jsonRequest);
            Log.d(LOGTAG, "No Network - System Offline");
            result = ClientInfo.getDetailInfo(searchJson).toString();
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

    /*public JSONObject getDetailInfo(JSONObject searchClient) throws Exception {

        JSONObject clientInformation = new JSONObject();
        int searchItem = searchClient.getInt("sOpt");
        searchClient.put("colName","");
        clientInformation.put("idType", "1");

        switch(searchItem){
            case 1:
                searchClient.put("colName","\"clientMap\".\"healthId\"");
                searchResultGen = ClientMapGeneralInfo.retrieveDetailInfo(searchClient, clientInformation);
                break;
            *//*case 2:
                searchClient.put("colName","\"Member\".\"MobileNo1\"");
                searchResultGen = ClientGeneralInfo.retrieveInfo(searchClient,clientInformation);
                //searchClient.put("colName","\"Member\".\"NameEng\"");
                //searchResultGen = ClientGeneralInfo.retrieveInfo(searchClient,clientInformation);
                break;*//*
            case 3:
                searchClient.put("colName","\"Member\".\"NID\"");
                searchResultGen = ClientGeneralInfo.retrieveInfo(searchClient, clientInformation);
                break;
            case 4:
                searchClient.put("colName","\"Member\".\"BRID\"");
                searchResultGen = ClientGeneralInfo.retrieveInfo(searchClient,clientInformation);
                break;
            case 5:
                searchClient.put("colName","\"clientMap\".\"healthId\"");
                searchResultGen = ClientMapGeneralInfo.retrieveDetailInfo(searchClient,clientInformation);
                clientInformation.put("idType", "5");
                break;
        }

        //searchResultGen = ClientGeneralInfo.retrieveInfo(searchClient,clientInformation);
        searchClient.put("serviceCategory", 1);
        searchResultReg = RetrieveRegNo.pullReg(searchClient, clientInformation);

        if(!clientInformation.get("False").equals("false")){
            clientInformation.put("False","");
            searchResultImmu = ImmunizationTT.getImmunizationHistory(clientInformation);
            searchResultPreg = PregInfo.retrievePregInfo(clientInformation);
        }
        else{
            clientInformation.put("False", "false");
        }
        return clientInformation;
    }*/

}
