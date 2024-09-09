package org.sci.rhis.db.dbhelper;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

/**
 * @author sabah.mugab
 * @created August, 2015
 */
public class RetrieveRegNo {

	public static JSONObject pullReg(JSONObject sClient, JSONObject clientInformation) {
		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		try{
			String sql = "";
			SimpleCursor rs = null;
			QueryBuilder dynamicQueryBuilder = new QueryBuilder();
			//doesn't handle multiple FWC entry
			if(!clientInformation.get("False").equals("false")){
				sql = "SELECT " + dynamicQueryBuilder.getColumn("","REGISTRATION_SERIAL_serial") + ","
						+ dynamicQueryBuilder.getColumn("","REGISTRATION_SERIAL_systemEntryDate") + " "
						+ " FROM " + dynamicQueryBuilder.getTable("REGISTRATION_SERIAL")
						+ " WHERE " + dynamicQueryBuilder.getColumn("table", "REGISTRATION_SERIAL_healthId",new String[]{clientInformation.getString("cHealthID")},"=")
						+ " AND " + dynamicQueryBuilder.getColumn("table", "REGISTRATION_SERIAL_serviceCategory",new String[]{sClient.getString("serviceCategory")},"=")
						//+ " AND " + dynamicQueryBuilder.getColumn("table", "REGISTRATION_SERIAL_providerId",new String[]{sClient.getString("providerid")},"=")
						//+ " AND (SELECT EXTRACT (year FROM " + dynamicQueryBuilder.getColumn("","REGISTRATION_SERIAL_systemEntryDate")
						//+ ") IN (SELECT EXTRACT(year FROM now()))) "
						+ " ORDER BY " + dynamicQueryBuilder.getColumn("","REGISTRATION_SERIAL_systemEntryDate") + " DESC,"
						+ dynamicQueryBuilder.getColumn("","REGISTRATION_SERIAL_serial") + " DESC";
			}
			if(!sql.equals("")){

				rs = new SimpleCursor(db.rawQuery(sql,null));
				if(rs.next()){
					clientInformation.put("regSerialNo", rs.getInt(dynamicQueryBuilder.getColumn("REGISTRATION_SERIAL_serial")));
					clientInformation.put("regDate", rs.getString(dynamicQueryBuilder.getColumn("REGISTRATION_SERIAL_systemEntryDate")));
				}
				else {
					clientInformation.put("regSerialNo", "");
					clientInformation.put("regDate", "");
				}
			}

			if(rs!=null){
				if(!rs.isClosed()){
					rs.close();
				}
			}
			return clientInformation;
		}
		catch(Exception e){
			e.printStackTrace();
			return clientInformation;
		}
	}

}
