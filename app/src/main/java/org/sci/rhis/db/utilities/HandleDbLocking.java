package org.sci.rhis.db.utilities;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.sci.rhis.db.dbhelper.DatabaseWrapper;


/**
 * @author jamil.zaman
 * @created August, 2016
 */
public class HandleDbLocking {

	static boolean status;
	private static final String LOGTAG  = "SQLITE-LOCK";
	
	public static boolean isLocked() {
		status = false;
		SQLiteDatabase db = DatabaseWrapper.getDatabase();
		try{		
	        		
			if( db.isDbLockedByCurrentThread() || db.isReadOnly()) {
				status = true;
			}
		} catch (SQLiteException sqe) {
			Log.e (LOGTAG, sqe.getMessage());
			sqe.printStackTrace();
		} catch(Exception e){
			Log.e (LOGTAG, e.getMessage());
			e.printStackTrace();
		}					
		return status;		
	}
}