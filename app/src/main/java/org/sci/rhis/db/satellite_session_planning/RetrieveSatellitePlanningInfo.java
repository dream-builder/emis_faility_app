package org.sci.rhis.db.satellite_session_planning;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.model.LocationHolder;
import org.sci.rhis.model.SatelliteSessionEvents;
import org.sci.rhis.utilities.MethodUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hajjaz.ibrahim on 2/5/2018.
 */

public class RetrieveSatellitePlanningInfo {

    public static Long getCurrentMonthID(int month){
        Calendar dateCal = Calendar.getInstance();
        int year = dateCal.get(Calendar.YEAR);
        Long count= Long.valueOf(0);
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        String query = "SELECT planning_id FROM satelite_session_planning where " +
                "year ="+year+" and month="+month;
        System.out.println(query);
        Cursor rs = null;
        try{
            rs =  db.rawQuery(query, null);
            if(rs.moveToFirst()){
                count = rs.getLong(0);
            }
        }catch(SQLiteException ex){
            System.out.println("SQLiteException: "+ex.toString());
        }finally {
            if(rs != null) {
                if (!rs.isClosed()) {
                    rs.close();
                }
            }
        }
        return count;
    }


    public static int getPlanningDetailID(long currentMonthId){
        int count = 0;
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        String query = "SELECT max(planning_detail_id) FROM satelite_planning_detail where planning_id="+currentMonthId;
        System.out.println(query);
        Cursor rs = null;
        try{
            rs =  db.rawQuery(query, null);
            if(rs.moveToFirst()){
                count = rs.getInt(0);
            }
        }catch(SQLiteException ex){
            System.out.println("SQLiteException: "+ex.toString());
        }finally {
            if(rs != null) {
                if (!rs.isClosed()) {
                    rs.close();
                }
            }
        }
        return count;
    }

    public static int getCurrentYearsID(String tableName, int providerId){
        Calendar dateCal = Calendar.getInstance();
        int year = dateCal.get(Calendar.YEAR);
        int count = 0;
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        String query = "SELECT planning_id FROM " + tableName+ " where providerid ='"+providerId+"' and year ="+year+" ORDER BY planning_id DESC LIMIT 1";
        System.out.println(query);
        Cursor rs = null;
        try{
            rs =  db.rawQuery(query, null);
            if(rs.moveToFirst()){
                count = rs.getInt(0);
            }
        }catch(SQLiteException ex){
            System.out.println("SQLiteException: "+ex.toString());
        }finally {
            if(rs != null) {
                if (!rs.isClosed()) {
                    rs.close();
                }
            }
        }
        return count;
    }

    /**
     * Use to retrieve satellite planning id
     * */
    public static long getSpecificSatelliteID(int month, int year){
        long id=0;
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        String query = "SELECT planning_id FROM satelite_session_planning where " +
                /*"provider_id ='"+providerId+"' and " +*/
                "year ="+year+" and month="+month;
        System.out.println(query);
        Cursor rs = null;
        try{
            rs =  db.rawQuery(query, null);
            if(rs.moveToFirst()){
                id = rs.getLong(0);
            }
        }catch(SQLiteException ex){
            System.out.println("SQLiteException: "+ex.toString());
        }finally {
            if(rs != null) {
                if (!rs.isClosed()) {
                    rs.close();
                }
            }
        }
        return id;
    }

    /**
     * Based on the parameter month, year it will look into the detail table for data
     * If data available it will add data in the SatelliteSessionEvents Object (it will be one month data)
     * If not it will return empty
     * */
    public static List<SatelliteSessionEvents> getSatelliteSessionEvents(int month, int year,
                                                                         ArrayList<LocationHolder> villageList){
        List<SatelliteSessionEvents> listSatelliteSessionEvents = new ArrayList<>();
        long satelite_planning_id = getSpecificSatelliteID(month,year);
        String query = "SELECT planning_detail_id, satelite_name, fwa_name,fwa_id,mouzaid, villageid, schedule_date" +
                " FROM satelite_planning_detail where planning_id ="+satelite_planning_id+";";
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        try{
            Cursor rs = db.rawQuery(query, null);
            if(rs.getCount()>0) {
                rs.moveToFirst();
                while(rs.isAfterLast() == false) {
                    //getting village name by mouza_village code
                    LocationHolder villageNameHolder = MethodUtils.
                            getLocationObjectByCode(villageList,rs.getString(5)+"_"+rs.getString(4));
                    String villageName = villageNameHolder.getBanglaName();
                    SatelliteSessionEvents object = new SatelliteSessionEvents(rs.getInt(0),
                            rs.getString(1),Date.valueOf(rs.getString(6)), rs.getString(2),
                            rs.getString(3), rs.getString(4),rs.getString(5),villageName);
                    listSatelliteSessionEvents.add(object);
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
        return listSatelliteSessionEvents;
    }


    public static String getSatelliteSubmitDate(int providerId){

        Calendar dateCal = Calendar.getInstance();
        int year = dateCal.get(Calendar.YEAR);
        String submitDate = "";

        int satelite_planning_id = getCurrentYearsID("satelite_session_planning", providerId);

        String query = "select submit_date from satelite_session_planning where " + "planning_id ="+satelite_planning_id +" and providerid ='"+providerId+ "' and year ="+year;

        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        System.out.println(query);
        Cursor rs = null;
        try{
            rs =  db.rawQuery(query, null);
            if(rs.moveToFirst()){
                submitDate = rs.getString(0);
            }
        }catch(SQLiteException ex){
            System.out.println("SQLiteException: "+ex.toString());
        }finally {
            if(rs != null) {
                if (!rs.isClosed()) {
                    rs.close();
                }
            }
        }
        return submitDate;
    }
}
