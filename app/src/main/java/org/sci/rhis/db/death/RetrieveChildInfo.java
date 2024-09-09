package org.sci.rhis.db.death;

import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.sql.ResultSet;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since September, 2015
 */
public class RetrieveChildInfo {

	public static JSONObject getChild(JSONObject deathInfo, QueryBuilder dynamicQueryBuilder) {

		JSONObject result = new JSONObject();
		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		try{
			String sql = "SELECT " + dynamicQueryBuilder.getColumn("table", "NEWBORN_childno")
					+ " FROM " + dynamicQueryBuilder.getTable("NEWBORN")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "NEWBORN_healthid",new String[]{deathInfo.getString("healthId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "NEWBORN_pregno",new String[]{deathInfo.getString("pregNo")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "NEWBORN_birthStatus",new String[]{"1"},"=")
					+ " AND NOT EXISTS (SELECT " + dynamicQueryBuilder.getColumn("", "DEATH_childNo")
					+ " FROM " + dynamicQueryBuilder.getTable("DEATH")
					+ " WHERE " + dynamicQueryBuilder.getPartialCondition("table", "NEWBORN_healthid","table","DEATH_healthId","=")
					+ " AND " + dynamicQueryBuilder.getPartialCondition("table", "NEWBORN_pregno","table","DEATH_pregNo","=")
					+ " AND " + dynamicQueryBuilder.getPartialCondition("table", "NEWBORN_childno","table","DEATH_childNo","=") + ")";

			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql,null));
			int count = 1;
			result.put("operation","retrieveChild");
			result.put("count", 0);

			while(rs.next()){
				result.put(String.valueOf(count), new JsonHandler().getResultSetValue(rs,dynamicQueryBuilder.getColumn("DEATH_childNo")));
				result.put("count", count);
				count = count + 1;
			}

			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}