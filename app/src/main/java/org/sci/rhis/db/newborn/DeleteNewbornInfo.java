package org.sci.rhis.db.newborn;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;
//import org.sci.rhis.util.CalendarDate;

/**
 * @author armaan.islam
 * @created December, 2015
 */

public class DeleteNewbornInfo {

	public static boolean deleteNewborn(JSONObject newbornInfo, QueryBuilder dynamicQueryBuilder) {

		boolean status = false;
		int i=0;

		try{
			i = CommonQueryExecution.executeQueryWithConfirmation(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(newbornInfo, "NEWBORN")));

			if(i!=0){
				String sqlString = "";
				switch(newbornInfo.getString("birthStatus")){
					case "1":
						sqlString = sqlString + getPartialQuery(newbornInfo, "DELIVERY_dNoLiveBirth", dynamicQueryBuilder);
						break;
					case "2":
						sqlString = sqlString + getPartialQuery(newbornInfo, "DELIVERY_dNoStillBirth", dynamicQueryBuilder);
						sqlString = sqlString + "," + getPartialQuery(newbornInfo, "DELIVERY_dStillFresh", dynamicQueryBuilder);
						break;
					case "3":
						sqlString = sqlString + getPartialQuery(newbornInfo, "DELIVERY_dNoStillBirth", dynamicQueryBuilder);
						sqlString = sqlString + "," + getPartialQuery(newbornInfo, "DELIVERY_dStillMacerated", dynamicQueryBuilder);
						//sqlString = sqlString + ", \"nUnidentified\"=(SELECT COUNT(\"nUnidentified\") FROM \"delivery\" WHERE \"healthId\"= " + newbornInfo.get("healthid") + " AND \"pregNo\"= " + newbornInfo.get("pregno") + ") - 1";
						break;
				}

				if(!sqlString.equals("")){
					sqlString = sqlString + ", ";
				}

				switch(newbornInfo.getString("gender")){
					case "1":
						sqlString = sqlString + getPartialQuery(newbornInfo, "DELIVERY_dNewBornBoy", dynamicQueryBuilder) + ",";
						break;
					case "2":
						sqlString = sqlString + getPartialQuery(newbornInfo, "DELIVERY_dNewBornGirl", dynamicQueryBuilder) + ",";
						break;
					case "3":
						sqlString = sqlString + getPartialQuery(newbornInfo, "DELIVERY_dNewBornUnidentified", dynamicQueryBuilder) + ",";
						break;
				}

				if(!sqlString.equals("")){
					String sql = "UPDATE " + dynamicQueryBuilder.getTable("DELIVERY") + " SET "
							+ sqlString
							+ dynamicQueryBuilder.getColumn("", "DELIVERY_modifyDate",new String[]{Utilities.getDateTimeWithoutTimeStampStringDBFormat()},"=")
							+ " WHERE " + dynamicQueryBuilder.getColumn("", "DELIVERY_healthid",new String[]{newbornInfo.getString("healthid")},"=")
							+ " AND " + dynamicQueryBuilder.getColumn("", "DELIVERY_pregno",new String[]{newbornInfo.getString("pregno")},"=");

					CommonQueryExecution.executeQuery(sql);
					status = true;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return status;
	}

	private static String getPartialQuery(JSONObject newbornInfo, String fieldIdentifier, QueryBuilder dynamicQueryBuilder) throws JSONException{

		String sql = dynamicQueryBuilder.getColumn("",fieldIdentifier) + " = (SELECT (CASE WHEN ("
				+ dynamicQueryBuilder.getColumn("", fieldIdentifier,new String[]{"0"},"=") + " OR "
				+ dynamicQueryBuilder.getColumn("", fieldIdentifier,new String[]{},"isnull") + ") THEN NULL ELSE "
				+ dynamicQueryBuilder.getColumn("",fieldIdentifier) + " END) "
				+ "FROM " + dynamicQueryBuilder.getTable("DELIVERY")
				+ " WHERE " + dynamicQueryBuilder.getColumn("", "DELIVERY_healthid",new String[]{newbornInfo.getString("healthid")},"=")
				+ " AND " + dynamicQueryBuilder.getColumn("", "DELIVERY_pregno",new String[]{newbornInfo.getString("pregno")},"=")
				+ ") - 1";

		return sql;
	}
}
