package org.sci.rhis.db.dbhelper;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jumpmind.symmetric.android.SymmetricService;
import org.sci.rhis.db.utilities.HandleDbLocking;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.ConstantQueries;
import org.sci.rhis.utilities.GlobalActivity;
import org.sci.rhis.utilities.MethodUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arafat.hasan on 8/8/2016.
 */
public class CommonQueryExecution {


    public static JSONArray getDropDownListAsJSONArray(String tableName, String where){
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        JSONArray result = null;
        Cursor rs = db.query(tableName,null,where,null,null,null,null);
        try {
            if(rs.getCount()>0){
                rs.moveToFirst();
                result = new JSONArray();
                //TODO - UGLY HACK
                result.put(new JSONObject("{id:0,\"detail\":\"\"}"));

                while(rs.isAfterLast() == false) {
                    JSONObject singleData = new JSONObject();

                    for (int i = 0; i < rs.getColumnCount(); i++) {
                        singleData.put(rs.getColumnName(i), rs.getString(i));
                    }

                    result.put(singleData);
                    rs.moveToNext();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rs.close();
        return result;
    }


    public static JSONObject getJSONObjectFromTable(String tableName, String where){
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        JSONObject result = null;
        Cursor rs = db.query(tableName,null,where,null,null,null,null);
        try {
            if(rs.getCount()>0){
                rs.moveToFirst();
                result = new JSONObject();


                while(rs.isAfterLast() == false) {
                    JSONObject singleData = new JSONObject();

                    for (int i = 0; i < rs.getColumnCount(); i++) {
                        singleData.put(rs.getColumnName(i), rs.getString(i));

                    }

                    result.put(rs.getString(0),singleData);
                    rs.moveToNext();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rs.close();
        return result;
    }

    public static JSONObject getSingleRowJSONObject(String tableName, String where){
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        JSONObject singleData = null;
        Cursor rs = db.query(tableName,null,where,null,null,null,null);
        try {
            if(rs.getCount()>0){
                rs.moveToFirst();
                singleData = new JSONObject();

                for (int i = 0; i < rs.getColumnCount(); i++) {
                    singleData.put(rs.getColumnName(i), rs.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rs.close();
        return singleData;
    }

    public static int generateServiceID(String healthId, String tableName){
        int serviceId = 0;
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE \"healthid\" = " + healthId;

        Cursor rs =  db.rawQuery(query, null);
        if(rs.moveToFirst()){
            serviceId = rs.getInt(0);
        }

        return serviceId;
    }

    public static String generateSystemEntryDate(String healthId, String tableName)
    {
        String systemEntryDate = "";
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE \"healthid\" = " + healthId;

        Cursor rs =  db.rawQuery(query, null);
        if(rs.moveToFirst()){
            systemEntryDate = rs.getString(0);
        }

        return systemEntryDate;
    }

    public static JSONObject getSingleResultFromQuery(String tableName, String whereCondition){
        JSONObject resultJson = new JSONObject();
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        Cursor rs = db.query(tableName,null,whereCondition,null,null,null,null);
        if(rs.moveToFirst()){
            for (int i = 0; i < rs.getColumnCount(); i++) {
                try {
                    resultJson.put(rs.getColumnName(i), rs.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultJson;
    }

    public static int executeQueryWithConfirmation(String sql){
        try{
            SQLiteDatabase db = DatabaseWrapper.getDatabase();
            db.execSQL(sql);
            return 1;
        }catch (SQLException sqe){
            sqe.printStackTrace();
            return 0;
        }

    }

    public static boolean executeQuery(String sql){
        boolean isSuccess =false;
        try{
            SQLiteDatabase db = DatabaseWrapper.getDatabase();
            if(!db.isDbLockedByCurrentThread()) {
                db.execSQL(sql);
                isSuccess = true;
            }
        }catch (SQLException sqe){
            sqe.printStackTrace();
            isSuccess = false;
        }finally {
            return isSuccess;
        }


    }



    public static Cursor executeQueryForCursor(String sql){
        try{
            SQLiteDatabase db = DatabaseWrapper.getDatabase();
            return db.rawQuery(sql,null);//no extra condition, so null
        }catch (SQLException sqe){
            sqe.printStackTrace();
            return null;
        }

    }

    public static int getCount(String tableName, String providerId){
        int count = 0;
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        String query = "SELECT COUNT(*) FROM \"" + tableName+ "\" where providerid="+providerId;

        Cursor rs =  db.rawQuery(query, null);
        if(rs.moveToFirst()){
            count = rs.getInt(0);
        }

        return count;
    }

    /**
     * Get the DB data as JSONArray with column name and value
     * @param strSQL the sql query
     * @return the JSONArray with DB column name and value
     */
    public static JSONArray getQueryDataAsJSONArray(String strSQL)
    {
        JSONArray result = new JSONArray();
        SQLiteDatabase db = DatabaseWrapper.getDatabase();

        try{

            Cursor rs = db.rawQuery(strSQL.toLowerCase(), null);

            if(rs.getCount()>0) {
                rs.moveToFirst();

                while(rs.isAfterLast() == false) {
                    JSONObject singleData = new JSONObject();

                    for (int i = 0; i < rs.getColumnCount(); i++) {
                        //singleData.put(rs.getColumnName(i), rs.getString(i));
                        if(rs.getString(i) == null || rs.getString(i).equals("")){
                            singleData.put(rs.getColumnName(i), "");
                        }
                        else {
                            singleData.put(rs.getColumnName(i), rs.getString(i));
                        }
                    }

                    result.put(singleData);
                    rs.moveToNext();
                }
            }
            if (!rs.isClosed()) {
                rs.close();
            }
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Get all the names of the DB tables
     * @return returns the list of table names
     */
    public static List<String> getTableNameAsList(){
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        List<String> dbTables = new ArrayList<>();
        try {
            Cursor rs = db.rawQuery("select name from sqlite_master where type = 'table' and name not like 'sym\\_%' escape '\\'",null);

            if(rs.getCount()>0){
                rs.moveToFirst();

                while(rs.isAfterLast() == false) {
                    dbTables.add(rs.getString(0));
                    rs.moveToNext();
                }
            }
            if (!rs.isClosed()) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbTables;
    }

    /**
     * Get column count of the tables
     * @return returns a json object containing column count of all the tables of DB
     */
    public static JSONObject getTableColumnCount(String providerId){
        final List<String> dbTables = Arrays.asList("ancservice","clientmap","delivery","death",
                "fpinfo","immunizationhistory","newborn","pacservice","pncservicechild",
                "pncservicemother","pregwomen","gpservice","iudservice","iudfollowupservice",
                "implantservice","implantfollowupservice","pillcondomservice","womaninjectable",
                "permanent_method_service","permanent_method_followup_service","child_care_service");
        System.out.println("Table Name: " + dbTables);
        JSONObject dbTablesColumnCount = new JSONObject();

        try {
            for (String s : dbTables) {

                dbTablesColumnCount.put(s, getCount(s,providerId));
                System.out.println(s+" : " + getCount(s,providerId));
            }
        }catch(Exception ex){
            System.out.println("Exception : "+ex.toString());
        }
        System.out.println("Row Count :" + dbTablesColumnCount);
        return dbTablesColumnCount;

    }

    public static String getSingleColumnFromSingleQueryResult(String query){
        String result = null;
        boolean isExecuted = false;
        if(DatabaseWrapper.getDatabase()==null){
            try {
                new DatabaseWrapper(GlobalActivity.context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SQLiteDatabase db = DatabaseWrapper.getDatabase();
        //while(!isExecuted) {
            if(!db.isDbLockedByCurrentThread()){
                Cursor rs = db.rawQuery(query,null);
                if(rs.moveToFirst()){
                    result= rs.getString(0);
                }
                isExecuted = true;
            }

        //}

        return result;
    }

    public static void startDeltaInsertionsInLocalDB(String otherDBPath){
        if(MethodUtils.isServiceRunning(SymmetricService.class)){
            GlobalActivity.context.stopService(new Intent(GlobalActivity.context,SymmetricService.class));
        }
        SQLiteDatabase originalDb = DatabaseWrapper.getDatabase();
        try {
            if(!originalDb.isDbLockedByCurrentThread()){
                //attaching remote database to original
                originalDb.execSQL("ATTACH DATABASE \'"+otherDBPath+"\' AS rdb;");
                //Data insertion starts from here
                for (String tableName : ConstantMaps.tableListWithPKey.keySet()){
                    String syncSql = ConstantQueries.getServiceSyncQuery(tableName);
                    originalDb.execSQL(syncSql);
                }
                //sync query executions for special tables
                originalDb.execSQL(ConstantQueries.MEMBER_SYNC_QUERY);
                originalDb.execSQL(ConstantQueries.getClientmapSyncQuery());
                //confirming next time offline login...............
                originalDb.execSQL(ConstantQueries.PROVIDERDB_SYNC_QUERY);
                //delta insertion has completed.....
                System.out.println("Data insertion completed");
                originalDb.execSQL("DETACH DATABASE  rdb;");


            }
        }catch (SQLException sqe){
            originalDb.execSQL("DETACH DATABASE  rdb;");
            Log.d("Sync SQL Ex:",sqe.getMessage());
        }catch (Exception e){
            Log.d("Sync Exception:",e.getMessage());
        }

    }

}
