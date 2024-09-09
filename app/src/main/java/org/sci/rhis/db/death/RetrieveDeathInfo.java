package org.sci.rhis.db.death;

import android.database.sqlite.SQLiteDatabase;

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
public class RetrieveDeathInfo {

	public static JSONObject getDeath(JSONObject deathInfo, QueryBuilder dynamicQueryBuilder) {

		JSONObject result = new JSONObject();
		SQLiteDatabase db = DatabaseWrapper.getDatabase();
		try{
			String sql = "SELECT " + dynamicQueryBuilder.getTable("DEATH") + ".*"
					+ " FROM " + dynamicQueryBuilder.getTable("DEATH")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "DEATH_healthId",new String[]{deathInfo.getString("healthId")},"=");

			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql,null));
			result.put("count","0");
			result.put("operation","retrieve");
			result.put("clientDeathStatus", 0);
			result.put("childDeathStatus", 0);

			while(rs.next()){

				if(rs.getInt(dynamicQueryBuilder.getColumn("DEATH_pregNo"))==0 &&
						rs.getInt(dynamicQueryBuilder.getColumn("DEATH_childNo"))==0){
					result.put("clientDeathStatus", 1);
				}
				else{
					result.put("childDeathStatus", 1);
				}

				result.put("count",(result.getInt("count") + 1) );
				result.put(result.getString("count"), new JsonHandler().getResponse(rs, deathInfo, "DEATH",1));
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