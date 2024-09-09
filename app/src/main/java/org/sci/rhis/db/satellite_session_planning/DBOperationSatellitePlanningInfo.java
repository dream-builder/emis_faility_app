package org.sci.rhis.db.satellite_session_planning;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.model.SatelliteSessionEvents;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.Utilities;

import java.util.Date;
import java.util.List;

/**
 * Created by hajjaz.ibrahim on 6/3/2018.
 */

public class DBOperationSatellitePlanningInfo {

    public static void removeEvent(Date date){
        String deleteQuery = "delete from satelite_planning_detail where schedule_date = " +
                " '" + Utilities.getDateStringDBFormat(date) + "'";
        System.out.println(deleteQuery);
        try {
            CommonQueryExecution.executeQuery(deleteQuery);
        } catch (SQLiteException ex) {
            System.out.println("SQLiteException: " + ex.toString());
        }
    }

    public static void addEvent(int planning_detail_id,long planning_id, String satelite_name, Date scheduled_date,
                                String fwaName,String fwaid,String mouzaid,String villageid){
        String addQuery = "INSERT INTO satelite_planning_detail(planning_detail_id,planning_id, schedule_date, satelite_name, fwa_name, fwa_id,mouzaid,villageid, " +
                        "  systementrydate, modifydate) VALUES ("+planning_detail_id+" ," + planning_id + ", '" + Utilities.getDateStringDBFormat(scheduled_date) + "'," +
                "'" + satelite_name + "', '" + fwaName + "', " + fwaid + ", " + mouzaid + ", "+ villageid + ", '" + Utilities.getDateStringDBFormat() + "', '" + Utilities.getDateStringDBFormat() + "')";

        System.out.println(addQuery);
        try {
            CommonQueryExecution.executeQuery(addQuery);
        } catch (SQLiteException ex) {
            System.out.println("SQLiteException: " + ex.toString());
        }
    }

    public static int getStatus(int year, int month, String facilityId){
        int status = -1;
        String statusGettingQuery = "select status  from satelite_session_planning where year = " +
                year+" and month = "+month+" and facility_id="+facilityId;
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        Cursor rs = null;
        try{
            rs =  db.rawQuery(statusGettingQuery, null);
            if(rs.moveToFirst()){
                status = rs.getInt(0);
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
        return status;
    }

    public static String getComments(int year, int month, String facilityId){
        String comments = "";
        String statusGettingQuery = "select comments  from satelite_session_planning where year = " +
                year+" and month = "+month+" and facility_id="+facilityId;
        SQLiteDatabase db  = DatabaseWrapper.getDatabase();
        Cursor rs = null;
        try{
            rs =  db.rawQuery(statusGettingQuery, null);
            if(rs.moveToFirst()){
                comments = rs.getString(0);
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
        return comments;
    }


    /**
     * Inserts the satellite planning into the DB
     *  @param list  the list of satellites planning
     * @param satelite_planning_id the satellites planning id for master table
     */
    public static boolean insertSatelliteSessionPlanning(List<SatelliteSessionEvents> list, String satelite_planning_id, int year, int month, ProviderInfo provider) {

        boolean masterSuccess = false, insertComplete=false;

        String sqlMaster = "INSERT OR IGNORE INTO [satelite_session_planning] ([planning_id],[facility_id],[providerid]," +
                "[year],[month],[status],[zillaid],[upazilaid],[unionid],[submit_date]," +
                "[systementrydate],[modifydate]) " +
                "VALUES(" + satelite_planning_id + ", "+provider.getmFacilityId()+", " + provider.getProviderCode() + ", " +
                year + ", " + month + ", " +
                3 + ", " +provider.getZillaID()+", "+provider.getUpazillaID()+", "+//status 3 = pending/not submitted
                provider.getUnionID()+", '"+
                Utilities.getDateTimeWithoutTimeStampStringDBFormat() + "','"+
                Utilities.getDateTimeWithoutTimeStampStringDBFormat() + "','"+
                Utilities.getDateTimeWithoutTimeStampStringDBFormat() + "')";
        System.out.println(sqlMaster);
        try {
            masterSuccess = CommonQueryExecution.executeQuery(sqlMaster);

        } catch (SQLiteException ex) {
            masterSuccess = false;
            System.out.println("SQLiteException: " + ex.toString());
        }

        //execute if the master entry is successful
        if(masterSuccess){
            try {

                String sqlQuery = "", commonQuery = "";
                commonQuery += "INSERT INTO [satelite_planning_detail]" +
                        "([planning_detail_id]" +
                        ",[planning_id]" +
                        ",[schedule_date]" +
                        ",[satelite_name]" +
                        ",[fwa_name]" +
                        ",[fwa_id]" +
                        ",[mouzaid]" +
                        ",[villageid]" +
                        ",[systementrydate]" +
                        ",[modifydate])" +
                        " VALUES";
                String dateStringDBFormat = Utilities.getDateStringDBFormat();
                int detailId=1;
                for (SatelliteSessionEvents obj : list) {
                    if(MethodUtils.getMonthFromDate(obj.getDate())==month){
                        commonQuery += "(" + detailId +  ","+satelite_planning_id+",'" + Utilities.getDateStringDBFormat(obj.getDate()) + "'," +
                                "'" + obj.getSatellite() + "'," +
                                "'" + obj.getFWAName() + "'," +
                                "" + (obj.getStrFWAId().equals("")?"0":obj.getStrFWAId()) + "," +
                                "" + obj.getStrMouzaId() + "," +
                                "" + obj.getStrVillageId() + "," +
                                "'" + dateStringDBFormat + "', " +
                                "'" + dateStringDBFormat + "'),";
                        detailId++;
                    }
                }

                sqlQuery += commonQuery.substring(0,commonQuery.length()-1);
                sqlQuery += ";";
                System.out.println("SQL = "+sqlQuery);
                insertComplete = CommonQueryExecution.executeQuery(sqlQuery);

            } catch (Exception ex) {
                insertComplete = false;
                System.out.println("Exception: " + ex.toString());

            }
        }

        return insertComplete;

    }

    public static void updatePlanning(String planning_id, String comment,String approvalDate,String status){
        String updateQuery = "update satelite_session_planning set approve_date = '"
                + approvalDate + "', status = "+status +", comments ='"+comment+"'" +
                " where planning_id =" + planning_id +";";
        System.out.println(updateQuery);
        try {
            CommonQueryExecution.executeQuery(updateQuery);
        } catch (SQLiteException ex) {
            System.out.println("SQLiteException: " + ex.toString());
        }
    }
}
