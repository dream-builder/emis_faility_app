package org.sci.rhis.db.dbhelper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.model.DashboardDataModel;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.Constants;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hajjaz.ibrahim on 1/1/2018.
 */

public class DashboardDBHelper {

    public static String deliveryCount(String providerCode, String startDate,String endDate){
        String count = "";
        String sql = "select count(*) from \"delivery\" where \"providerid\"=" + providerCode + " and deliverydonebythisprovider=1 and \"outcomedate\" between '"+startDate+"' and '"+endDate+"'";
        JSONArray jsonArrayDelivery = CommonQueryExecution.getQueryDataAsJSONArray(sql.toLowerCase());

        for (int i = 0; i < jsonArrayDelivery.length(); i++) {
            try {
                JSONObject jObject = (JSONObject) jsonArrayDelivery.get(i);
                count = jObject.getString("count(*)");
            } catch (JSONException e) {
                Log.e("JSON", "There was an error parsing the JSON", e);
            }
        }
        return count;
    }

    public static String commonServiceCount(Constants.dbTables tableName, String dateColumnName){
        String count = "";
        String providerCode= ProviderInfo.getProvider().getProviderCode();
        Calendar c = Calendar.getInstance();
        String year = String.format(Locale.ENGLISH,"%02d", c.get(Calendar.YEAR));
        String month = String.format(Locale.ENGLISH,"%02d", (c.get(Calendar.MONTH) + 1));
        String startDate = year + "-" + month + "-01";
        String endDate = year + "-" + month + "-31";
        String sql = "select count(*) from "+tableName.toString()+" where providerid=" + providerCode +
                " and "+dateColumnName+" between '"+startDate+"' and '"+endDate+"'";
        JSONArray jsonArrayGP = CommonQueryExecution.getQueryDataAsJSONArray(sql.toLowerCase());

        for (int i = 0; i < jsonArrayGP.length(); i++) {
            try {
                JSONObject jObject = (JSONObject) jsonArrayGP.get(i);
                count = jObject.getString("count(*)");
            } catch (JSONException e) {
                Log.e("JSON", "There was an error parsing the JSON", e);
            }
        }
        return count;
    }

    public static String[] ANCVisitCount(String providerCode, String startDate,String endDate){

        String[] ancCount = new String[4];
        String sql = "select count(*),\"serviceid\" from \"ancservice\" where \"providerid\"=" + providerCode + " and \"visitdate\" between '"+startDate+"' " +
                "and '"+endDate+"' group by \"serviceid\"";
        System.out.println("SQL = "+sql);
        JSONArray jsonArrayANC = CommonQueryExecution.getQueryDataAsJSONArray(sql.toLowerCase());
        System.out.println("ANC Visit....... "+CommonQueryExecution.getQueryDataAsJSONArray(sql.toLowerCase()));

        for (int i = 0; i < jsonArrayANC.length(); i++) {
            DashboardDataModel gp = null;
            try {
                JSONObject jObject = (JSONObject) jsonArrayANC.get(i);
                if(Integer.valueOf(jObject.getString("serviceid")) == 1){
                    ancCount[0]=jObject.getString("count(*)");
                }
                else if(Integer.valueOf(jObject.getString("serviceid")) == 2){
                    ancCount[1]=jObject.getString("count(*)");
                }
                else if(Integer.valueOf(jObject.getString("serviceid")) == 3){
                    ancCount[2]=jObject.getString("count(*)");
                }
                else if(Integer.valueOf(jObject.getString("serviceid")) == 4){
                    ancCount[3]=jObject.getString("count(*)");
                }
            } catch (JSONException e) {
                Log.e("JSON", "There was an error parsing the JSON", e);
            }
        }
        return ancCount;
    }

    public static String[] PNCVisitCount(String providerCode, String startDate,String endDate){
        String[] pncCount = new String[8];

        String sql = "select count(*),\"serviceid\" from \"pncservicemother\" where \"providerId\"=" + providerCode + " and \"visitdate\" between '"+startDate+"' " +
                "and '"+endDate+"' group by \"serviceId\"";
        System.out.println("SQL = "+sql);
        JSONArray jsonArrayPNC = CommonQueryExecution.getQueryDataAsJSONArray(sql);
        System.out.println("PNC Visit....... "+CommonQueryExecution.getQueryDataAsJSONArray(sql.toLowerCase()));

        for (int i = 0; i < jsonArrayPNC.length(); i++) {
            DashboardDataModel gp = null;
            try {
                JSONObject jObject = (JSONObject) jsonArrayPNC.get(i);
                if(Integer.valueOf(jObject.getString("serviceid")) == 1){
                    pncCount[0] = jObject.getString("count(*)");
                }
                else if(Integer.valueOf(jObject.getString("serviceid")) == 2){
                    pncCount[1] = jObject.getString("count(*)");
                }
                else if(Integer.valueOf(jObject.getString("serviceid")) == 3){
                    pncCount[2] = jObject.getString("count(*)");
                }
                else if(Integer.valueOf(jObject.getString("serviceid")) == 4){
                    pncCount[3] = jObject.getString("count(*)");
                }
            } catch (JSONException e) {
                Log.e("JSON", "There was an error parsing the JSON", e);
            }
        }

        //PNC Child
        String sqlChild = "select count(*),\"serviceid\" from \"pncServiceChild\" where \"providerId\"=" + providerCode + " and \"visitdate\" between '"+startDate+"' " +
                "and '"+endDate+"' group by \"serviceid\"";
        System.out.println("SQL = "+sqlChild);
        JSONArray jsonArrayPNCN = CommonQueryExecution.getQueryDataAsJSONArray(sqlChild.toLowerCase());
        System.out.println("PNC Visit....... "+CommonQueryExecution.getQueryDataAsJSONArray(sqlChild));

        for (int i = 0; i < jsonArrayPNCN.length(); i++) {
            DashboardDataModel gp = null;
            try {
                JSONObject jObject = (JSONObject) jsonArrayPNCN.get(i);
                if(Integer.valueOf(jObject.getString("serviceid")) == 1){
                    pncCount[4] = jObject.getString("count(*)");
                }
                else if(Integer.valueOf(jObject.getString("serviceid")) == 2){
                    pncCount[5] = jObject.getString("count(*)");
                }
                else if(Integer.valueOf(jObject.getString("serviceid")) == 3){
                    pncCount[6] = jObject.getString("count(*)");
                }
                else if(Integer.valueOf(jObject.getString("serviceid")) == 4){
                    pncCount[7] = jObject.getString("count(*)");
                }
            } catch (JSONException e) {
                Log.e("JSON", "There was an error parsing the JSON", e);
            }
        }
        return pncCount;
    }

}

