package org.sci.rhis.db.dbhelper;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.sci.rhis.utilities.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by jamil.zaman on 03/01/16.
 */
public class DatabaseWrapper extends SQLiteOpenHelper {
    private Context mycontext;
    private String LOGTAG = "FWC-DATABASE-WRAPPER";
    private static String DB_NAME = "rhis_fwc.sqlite3";
    public static SQLiteDatabase myDataBase;

    public DatabaseWrapper(Context context) throws IOException {
        super(context,  Constants.getDB_PATH() + DB_NAME, null, 1);
        this.mycontext=context;
        if (checkdatabase()) {
            Log.d(LOGTAG, Constants.getDB_PATH() + DB_NAME+" Database exists");
            opendatabase();
        } else {
            Log.d(LOGTAG,Constants.getDB_PATH() + DB_NAME+" Database doesn't exist create one");
            createdatabase();
        }
    }

    public static SQLiteDatabase getDatabase() {
        return myDataBase;
    }

    public void onCreate(SQLiteDatabase db) {
        //super.onCreate(db);
        //db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if(dbexist) {
            Log.d(LOGTAG, " Database exists.");
        } else {
            //this.getReadableDatabase();
            try {
                copydatabase();
            } catch(IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkdatabase() {

        boolean checkdb = false;
        try {
            String myPath = Constants.getDB_PATH() + DB_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch(SQLiteException e) {
            Log.d(LOGTAG, "Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myinput = mycontext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outfilename = Constants.getDB_PATH() + DB_NAME;

        File checkFolder = new File(Constants.getDB_PATH());
        if(!checkFolder.exists()) {
            checkFolder.mkdirs();
        }

        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream(outfilename);

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer))>0) {
            myoutput.write(buffer,0,length);
        }

        //Close the streams
        myoutput.flush();
        myoutput.close();
        myinput.close();

        //open database to set myDataBase
        opendatabase();
    }

    public void opendatabase() throws SQLException {
        //Open the database
        String mypath = Constants.getDB_PATH() + DB_NAME;
        if(myDataBase == null) {
            myDataBase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    public synchronized void switchdatabase(String mypath) throws SQLException {
        //Open the database
        //String mypath = Constants.getDB_PATH() + DB_NAME;
        if(myDataBase != null) {
            myDataBase.close();
            myDataBase =  SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    public synchronized void switchdatabase() throws SQLException {
        //Open the database from default path
        switchdatabase(Constants.getDB_PATH() + DB_NAME);
    }

    public synchronized void close() {
        if(myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    public static void closeDatabase() {
        if(myDataBase != null) {
            myDataBase.close();
            myDataBase = null;
        }
    }
    // --
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
}
