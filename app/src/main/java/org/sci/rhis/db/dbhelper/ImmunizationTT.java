package org.sci.rhis.db.dbhelper;

import android.database.sqlite.SQLiteDatabase;


import org.json.JSONObject;
import org.sci.rhis.utilities.JsonHandler;


/**
 * @author sabah.mugab
 * @created August, 2015
 */
public class ImmunizationTT {

	public static JSONObject getImmunizationHistory(JSONObject clientInformation){

		QueryBuilder dynamicQueryBuilder = new QueryBuilder();
		SQLiteDatabase db = DatabaseWrapper.getDatabase();
		int i, cnt=0;

		try{
			for(i=1;i<=5;i++){
				clientInformation.put("cTT" + i,"");
				clientInformation.put("cTT" + i + "Date","");
			}

			String sql = "SELECT " + dynamicQueryBuilder.getColumn("table","TT_ttDate") + ","
					+ dynamicQueryBuilder.getColumn("table","TT_imuDose") + " "
					+ " FROM " + dynamicQueryBuilder.getTable("TT")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "TT_healthId",new String[]{clientInformation.getString("cHealthID")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "TT_imuCode",new String[]{"16"},"=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("table","TT_imuDose") + " DESC LIMIT 5";

			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql,null));
			cnt = rs.count();

			while(rs.next()){
				clientInformation.put("cTT" + cnt, "1");
				/**
				 * if rs.getString("imuDate") is null then set "".
				 */
				clientInformation.put("cTT" + cnt + "Date", new JsonHandler().getResultSetValue(rs, dynamicQueryBuilder.getColumn("TT_ttDate")));

				cnt = cnt - 1;
			}
			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return clientInformation;
	}

	public static void pushImmunizationHistory(JSONObject immuHistory){

		try{
			String tt = "", ttDate = "";
			immuHistory.put("imuCode", 16);
			immuHistory.put("imuDose", "");
			for(int i=1; i<=5; i++){
				tt = "tt" + i;
				ttDate = "ttDate" + i;
				if(!immuHistory.get(tt).equals("")){
					CommonQueryExecution.executeQuery(new QueryBuilder().getInsertQuery
							(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
									(immuHistory,"imuDose"), "TT")));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}