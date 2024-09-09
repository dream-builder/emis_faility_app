package org.sci.rhis.db.fpcommon;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.utilities.Utilities;

/**
 * @author sabah.mugab
 * @since October, 2016
 */
public class FPStatus {
	
	public static boolean updateStatus(JSONObject FPInfo) {

		boolean status = false;
		SQLiteDatabase db = DatabaseWrapper.getDatabase();
		
		try{
	        String sql = "UPDATE \"fpInfo\" SET "
	        		+ "\"providerId\"=" + FPInfo.get("providerId") + ","
	        		+ "\"isNewClient\"=" + (FPInfo.get("isNewClient").equals("") ? null : FPInfo.get("isNewClient")) + ","
	        		+ "\"currentMethod\"=" + (FPInfo.get("currentMethod").equals("") ? null : FPInfo.get("currentMethod")) + ","
	        		+ "\"modifyDate\"='" + Utilities.getDateStringDBFormat() + "' "
	        		+ "Where \"healthId\"=" + FPInfo.get("healthId");

			db.execSQL(sql);
			status = true;
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		return status;		
	}
}