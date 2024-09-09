package org.sci.rhis.db.dbhelper;

import android.database.sqlite.SQLiteDatabase;

import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @created August, 2015
 */
public class CreateRegNo {

	public static boolean pushReg(JSONObject serviceRequest, JSONObject serviceDetail) {

		SQLiteDatabase db = DatabaseWrapper.getDatabase();
        boolean status = false;

		try{

			QueryBuilder dynamicQueryBuilder = new QueryBuilder();
			serviceRequest.put("serial",0);
			Calendar currentDate = Calendar.getInstance();
			String sql = "SELECT " + dynamicQueryBuilder.getColumn("","REGISTRATION_SERIAL_serial") + ","
					+ dynamicQueryBuilder.getColumn("","REGISTRATION_SERIAL_systemEntryDate") + " "
					+ " FROM " + dynamicQueryBuilder.getTable("REGISTRATION_SERIAL")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "REGISTRATION_SERIAL_providerId",new String[]{serviceRequest.getString("providerId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "REGISTRATION_SERIAL_serviceCategory",new String[]{serviceRequest.getString("serviceCategory")},"=")
					+ " AND (SELECT strftime('%Y',\"systementrydate\") from \"regserial\" WHERE strftime('%Y',\"systementrydate\") = strftime('%Y','now'))"

					/*+ " AND (SELECT EXTRACT (year FROM " + dynamicQueryBuilder.getColumn("","REGISTRATION_SERIAL_systemEntryDate")
					+ ") IN (SELECT EXTRACT(year FROM now()))) "*/
					+ "ORDER BY " + dynamicQueryBuilder.getColumn("","REGISTRATION_SERIAL_serial") + " DESC";

			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql,null));

			if(rs.next()){

				int maxSerial = rs.getInt(dynamicQueryBuilder.getColumn("REGISTRATION_SERIAL_serial"));
				Calendar lastEntryDate = Calendar.getInstance();
				lastEntryDate.setTime(rs.getDate(dynamicQueryBuilder.getColumn("REGISTRATION_SERIAL_systemEntryDate")));

				if(currentDate.get(Calendar.YEAR) > lastEntryDate.get(Calendar.YEAR)){
					serviceRequest.put("serial",1);
				}
				else{
					serviceRequest.put("serial", (maxSerial + 1));
				}
			}
			else{
				serviceRequest.put("serial",1);
			}

			CommonQueryExecution.executeQuery((dynamicQueryBuilder.getInsertQuery(
					new JsonHandler().addJsonKeyValueEdit(serviceRequest, "REGISTRATION_SERIAL"))));

			status = true;
			serviceDetail.put("regSerialNo", serviceRequest.get("serial"));
			serviceDetail.put("regDate", Converter.dateToString(Constants.SHORT_HYPHEN_FORMAT_DATABASE,new Date()));


			if(!rs.isClosed()){
				rs.close();
			}


		}
		catch(Exception e){
			e.printStackTrace();

			try {
				serviceDetail.put("regDate", "");
				serviceDetail.put("regSerialNo", "");
				status=false;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		return status;
	}

	public static boolean pushRegForUnderscore(JSONObject pregInfo, JSONObject pregnancyInfo) {

		boolean status = false;

		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		int serial = 0;
		String entrySystemDate = "";

		try{
			Calendar currentDate = Calendar.getInstance();

			String sql = "select \"serialNo\", \"systemEntryDate\" "
					+ "FROM \"regSerial\" "
					+ "WHERE \"regSerial\".\"providerId\"= " + pregInfo.get("provider_id")
					+ " AND \"regSerial\".\"serviceCategory\"= " + pregInfo.get("serviceCategory")
					+ " AND (SELECT strftime('%Y',\"systemEntryDate\") from \"regSerial\" WHERE strftime('%Y',\"systemEntryDate\") = strftime('%Y','now'))"
					+ " ORDER BY \"serialNo\" DESC";

			/*SELECT EXTRACT(year FROM now()))*/
			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql, null));
			if(rs.next()){
				CustomSimpleDateFormat date1 = new CustomSimpleDateFormat("yyyy-MM-dd");
				int maxSerial = rs.getInt("serialNo");
				Calendar lastEntryDate = Calendar.getInstance();
				lastEntryDate.setTime(date1.parse(rs.getString("systemEntryDate")));

				if(currentDate.get(Calendar.YEAR) > lastEntryDate.get(Calendar.YEAR)){
					//System.out.println("If" + currentDate.get(Calendar.YEAR) + "----" + lastEntryDate.get(Calendar.YEAR));
					serial = 1;
					entrySystemDate = date1.format(currentDate.getTime());
				}
				else{
					//System.out.println("Else" + currentDate.get(Calendar.YEAR) + "----" + lastEntryDate.get(Calendar.YEAR));
					serial = maxSerial + 1;
					entrySystemDate = date1.format(currentDate.getTime());
				}
			}
			else{
				serial = 1;
				CustomSimpleDateFormat date1 = new CustomSimpleDateFormat("yyyy-MM-dd");
				entrySystemDate = date1.format(currentDate.getTime());
			}
			if(!rs.isClosed()) {
				rs.close();
			}
			//System.out.println("Final" + serial + "&---&" + entrySystemDate);

			sql = "INSERT INTO \"regSerial\" ("
					+ "\"healthId\",\"providerId\",\"serviceCategory\",\"serialNo\",\"systemEntryDate\",\"modifyDate\") "
					+ "VALUES(" + pregInfo.get("health_id") + ","
					+ pregInfo.get("provider_id") + ","
					+ pregInfo.get("serviceCategory") + ","
					+ serial + ","
					+ "'" + entrySystemDate + "',"
					+ "'" + entrySystemDate + "') "
			//	+ "RETURNING \"serialNo\",\"systemEntryDate\""
			;



			//rs = new SimpleCursor(db.rawQuery(sql, null));
			try {
				db.execSQL(sql);
				status = true;
				pregnancyInfo.put("regSerialNo", serial);
				pregnancyInfo.put("regDate", entrySystemDate);
			} catch (Exception e) {
				pregnancyInfo.put("regSerialNo", "");
				pregnancyInfo.put("regDate", "");
			}


			/*if(rs.next()){
				status = true;
				pregnancyInfo.put("regSerialNo", rs.getInt("serialNo"));
				pregnancyInfo.put("regDate", rs.getString("systemEntryDate"));
			}
			else{
				pregnancyInfo.put("regSerialNo", "");
				pregnancyInfo.put("regDate", "");
			}

			if(!rs.isClosed()){
				rs.close();
			}*/
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		return status;
	}
}
