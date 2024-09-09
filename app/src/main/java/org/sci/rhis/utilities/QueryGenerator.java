package org.sci.rhis.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;

public class QueryGenerator {

    public static String generateSQLFile(Context context, String fileName) {

        String result = "";
        DatabaseWrapper dbWrapper = null;
        try {
            dbWrapper = new DatabaseWrapper(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File dbFile = new File(Environment.getExternalStorageDirectory().toString() + "/rhis_fwc/databases/rhis_fwc.sqlite3");
        File dbFileBackup = new File(Environment.getExternalStorageDirectory().toString() + "/rhis_fwc/databases/rhis_fwc_old.sqlite3");

        if(dbFileBackup.exists()){
            dbWrapper.switchdatabase(dbFileBackup.getPath());
        }
        else{
            dbWrapper.switchdatabase(dbFile.getPath());
        }

        SQLiteDatabase db = DatabaseWrapper.getDatabase();

        Cursor rs = null;
        JSONObject table_detail = new JSONObject();

        //Generate file
        File file = new File(Environment.getExternalStorageDirectory().toString()+
                "/rhis_fwc/databases/", fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            else{
                file.delete();
                file.createNewFile();
            }
        } catch(IOException e){
            e.printStackTrace();
        }

        try {
            String sql =  "SELECT sym_data.* FROM sym_data "
                    + "INNER JOIN sym_data_event USING (data_id) "
                    + "INNER JOIN sym_outgoing_batch USING (batch_id) "
                    + "WHERE sym_outgoing_batch.status!='OK' and sym_outgoing_batch.channel_id!='heartbeat' "
                    + "ORDER BY sym_data.create_time";

            rs = db.rawQuery(sql, null);
            String query = "/*sym_data*/";
            int columnCount = 0;
            while(rs.moveToNext()){

                columnCount = rs.getColumnCount();
                for(int i = 0; i< columnCount; i++){
                    query += rs.getString(i)+"|";
                }

                if(!query.equals("")){
                    try
                    {
                        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                        outputStreamWriter.append(query+";");
                        outputStreamWriter.close();

                        fileOutputStream.flush();
                        fileOutputStream.close();
                        result = "Success";
                    }
                    catch (IOException e)
                    {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                }
                query = "";
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            try
            {
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.append("");

                outputStreamWriter.close();
                fileOutputStream.flush();
                fileOutputStream.close();
                result = "Success";
            }
            catch (IOException e)
            {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            result = "Failed";
        }
        finally{
            //dbOp.dbObjectNullify(dbObject);
        }
        try {
            String sql =  "SELECT * from sym_trigger_hist where trigger_id not like 'sym_%'";

            rs = db.rawQuery(sql, null);
            String query = "/*sym_trigger_hist*/";
            int columnCount = 0;
            while(rs.moveToNext()){

                columnCount = rs.getColumnCount();
                for(int i = 0; i< columnCount; i++){
                    query += rs.getString(i)+"|";
                }

                if(!query.equals("")){
                    try
                    {
                        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                        //outputStreamWriter.append(XORCrypt.encrypt((query),"emis_facility")+";");
                        //System.out.println(XORCrypt.decrypt(XORCrypt.encrypt((query),"emis_facility"), "emis_facility"));

                        //System.out.println(query+";");
                        outputStreamWriter.append(query+";");
                        outputStreamWriter.close();

                        fileOutputStream.flush();
                        fileOutputStream.close();
                        result = "Success";
                    }
                    catch (IOException e)
                    {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                }
                query = "";
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            try
            {
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.append("");

                outputStreamWriter.close();
                fileOutputStream.flush();
                fileOutputStream.close();
                result = "Success";
            }
            catch (IOException e)
            {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            result = "Failed";
        }
        finally{
            //dbOp.dbObjectNullify(dbObject);
        }
        return result;
    }
}