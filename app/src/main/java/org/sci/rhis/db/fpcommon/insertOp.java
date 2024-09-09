package org.sci.rhis.db.fpcommon;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.utilities.Utilities;

/**
 * @author sabah.mugab
 * @since January, 2016
 */
public class insertOp {
	
	public static boolean insertFPExamination(JSONObject FPInfo) {
		
		boolean status = false;
		SQLiteDatabase db = DatabaseWrapper.getDatabase();
		
		try{
	        String sql = "INSERT INTO \"FPExamination\" ("
	    			+ "\"healthId\",\"providerId\",\"serviceId\",\"FPType\",";
	        
	        switch(FPInfo.getInt("FPType")){
	        	case 1:
	        		sql = sql + "\"screening\",\"bpSystolic\",\"bpDiastolic\",\"jaundice\",\"diabetes\",";
	        		break;
	        	case 2:
	        		sql = sql + "\"bpSystolic\",\"bpDiastolic\",\"weight\",\"pulse\",\"LMP\",\"breastCondition\","
	        				+ "\"uterusShape\",\"uterusPosition\",\"uterusMovement\",\"uterusCervixMovePain\","
	        				+ "\"sideEffect\",\"advice\",\"treatment\",\"refer\",\"referCenterName\",\"referReason\",";
	        		break;
	        	case 3:
	        		break;
	        }
	        
	        sql = sql + "\"systemEntryDate\",\"modifyDate\") "
	    			+ "VALUES(" + FPInfo.get("healthId") + ","
	    			+ FPInfo.get("providerId") + ","
	    			+ FPInfo.get("serviceId") + ","
	    			+ FPInfo.get("FPType") + ",";
	    	
	        switch(FPInfo.getInt("FPType")){
	        	case 1:
	        		sql = sql + (FPInfo.get("screening").equals("")?null:FPInfo.get("screening")) + "," 
	        				+ (FPInfo.get("bpSystolic").equals("")?null:FPInfo.get("bpSystolic")) + ","
	        				+ (FPInfo.get("bpDiastolic").equals("")?null:FPInfo.get("bpDiastolic")) + ","
	        				+ (FPInfo.get("jaundice").equals("")?null:FPInfo.get("jaundice")) + ","
	        				+ (FPInfo.get("diabetes").equals("")?null:FPInfo.get("diabetes")) + ",";
	        		break;
	        	case 2:
	        		sql = sql + (FPInfo.get("bpSystolic").equals("")?null:FPInfo.get("bpSystolic")) + ","
	        				+ (FPInfo.get("bpDiastolic").equals("")?null:FPInfo.get("bpDiastolic")) + "," 
	        				+ (FPInfo.get("weight").equals("")?null:FPInfo.get("weight")) + ","
	        				+ (FPInfo.get("pulse").equals("")?null:FPInfo.get("pulse")) + ","
	        				+ (FPInfo.get("LMP").equals("")?null:("'"+FPInfo.get("LMP")+"'")) + ","
	        				+ (FPInfo.get("breastCondition").equals("")?null:FPInfo.get("breastCondition")) + ","
	        				+ (FPInfo.get("uterusShape").equals("")?null:FPInfo.get("uterusShape")) + ","
	        				+ (FPInfo.get("uterusPosition").equals("")?null:FPInfo.get("uterusPosition")) + ","
	        				+ (FPInfo.get("uterusMovement").equals("")?null:FPInfo.get("uterusMovement")) + ","
	        				+ (FPInfo.get("uterusCervixMovePain").equals("")?null:FPInfo.get("uterusCervixMovePain")) + ","
	        				+ (FPInfo.get("sideEffect").equals("")?null:("'"+FPInfo.get("sideEffect")+"'")) + ","
	        				+ (FPInfo.get("advice").equals("")?null:("'"+FPInfo.get("advice")+"'")) + ","
	        				+ (FPInfo.get("treatment").equals("")?null:("'"+FPInfo.get("treatment")+"'")) + ","
	        				+ (FPInfo.get("refer").equals("")?null:FPInfo.get("refer")) + ","
	        				+ (FPInfo.get("referCenterName").equals("")?null:FPInfo.get("referCenterName")) + ","
	        				+ (FPInfo.get("referReason").equals("")?null:("'"+FPInfo.get("referReason")+"'")) + ",";
	        		break;
	        	case 3:
	        		break;
	        }	
	        
	        
	        sql = sql + "'" + Utilities.getDateStringDBFormat() + "','"
	    			+ Utilities.getDateStringDBFormat() + "')";

	    	db.execSQL(sql);
	        status = true;
	        			
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		return status;		
	}


	public static boolean deleteFPExamination(JSONObject FPInfo) {

		boolean status = false;
		SQLiteDatabase db = DatabaseWrapper.getDatabase();


		try{
			String sql =  "DELETE FROM \"FPExamination\""
					+ "WHERE \"healthId\" = " + FPInfo.getLong("healthId")
					+ " AND \"serviceId\" = " + FPInfo.get("serviceId")
					+ " AND \"FPType\" = " + FPInfo.get("FPType");

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
