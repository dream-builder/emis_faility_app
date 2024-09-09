package org.sci.rhis.db.newborn;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @created July, 2015
 */
public class RetrieveNewbornInfo {

	public static JSONObject getNewbornInfo(JSONObject newbornInfo,
											JSONObject newbornInformation, QueryBuilder dynamicQueryBuilder) {

		boolean status = false;
		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		try{
			String sql = "SELECT " + dynamicQueryBuilder.getColumn("table", "DELIVERY_pregno") + ","
					+ dynamicQueryBuilder.getColumn("", "DELIVERY_dAbortion")
					+ " FROM " + dynamicQueryBuilder.getTable("DELIVERY")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "DELIVERY_healthid",new String[]{newbornInfo.getString("healthid")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "DELIVERY_pregno",new String[]{newbornInfo.getString("pregno")},"=");

			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql,null));

			if(rs.next()){
				status = true;
				newbornInformation.put("deliveryInfo","1");
				newbornInformation.put("abortion",new JsonHandler().getResultSetValue(rs,dynamicQueryBuilder.getColumn("DELIVERY_dAbortion")));
			}
			else{
				newbornInformation.put("deliveryInfo","0");
				newbornInformation.put("abortion","");
			}

			sql = "SELECT " + dynamicQueryBuilder.getTable("NEWBORN") + ".*"
					+ " FROM " + dynamicQueryBuilder.getTable("NEWBORN")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "NEWBORN_healthid",new String[]{newbornInfo.getString("healthid")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "NEWBORN_pregno",new String[]{newbornInfo.getString("pregno")},"=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("","NEWBORN_childno") + " ASC";

			rs = new SimpleCursor(db.rawQuery(sql,null));
			newbornInformation.put("hasNewbornInfo", "No");
			newbornInformation.put("count", 0);

			int cnt = 1;

			while(rs.next()){
				status = (status && true);

				newbornInformation.put("count", cnt);
				newbornInformation.put(rs.getString(dynamicQueryBuilder.getColumn("NEWBORN_childno")),
						new JsonHandler().getResponse(rs, newbornInfo, "NEWBORN",2));
				newbornInformation.put("hasNewbornInfo", "Yes");

				cnt = cnt + 1;
			}

			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return newbornInformation;
	}
}