package org.sci.rhis.db.child;

import android.util.Log;

import java.sql.ResultSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.PropertiesInfo;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2018
 */
public class RetrieveChildVisitInfo {


	public static JSONObject getChildVisits(JSONObject childServiceInfo, JSONObject childVisits, QueryBuilder dynamicQueryBuilder) {
			
		try{
			String sql = "SELECT * FROM " + dynamicQueryBuilder.getTable("CHILD") + " "
						+ "WHERE " + dynamicQueryBuilder.getColumn("table", "CHILD_healthId",new String[]{childServiceInfo.getString("healthId")},"=")
						+ " ORDER BY " + dynamicQueryBuilder.getColumn("table", "CHILD_systemEntryDate") + " ASC";
									
			SimpleCursor rs =new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));
			childVisits.put("count", 0);
			//childServiceInfo.put("distributionJson","treatment");			
						
			while(rs.next()){
				System.out.println("Shuvo "+rs.getString(dynamicQueryBuilder.getColumn("CHILD_systemEntryDate")));
				childVisits.put(rs.getString(dynamicQueryBuilder.getColumn("CHILD_systemEntryDate")),
						addAdditionalKey(new JsonHandler().getServiceDetail(rs, childServiceInfo, "CHILD", dynamicQueryBuilder, 2)));
				childVisits.put("count", (childVisits.getInt("count")+1));				
			}
			
			sql = "SELECT * FROM " + dynamicQueryBuilder.getTable("CHILDSERVICERECORD") + " "
					+ "WHERE " + dynamicQueryBuilder.getColumn("table", "CHILDSERVICERECORD_healthId",new String[]{childServiceInfo.getString("healthId")},"=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("table", "CHILDSERVICERECORD_entryDate") + " ASC";
			
			rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));
			
			while(rs.next()){
				childVisits.getJSONObject(rs.getString(dynamicQueryBuilder.getColumn("CHILDSERVICERECORD_entryDate"))).
										put(rs.getString(dynamicQueryBuilder.getColumn("CHILDSERVICERECORD_inputId")), rs.getString(dynamicQueryBuilder.getColumn("CHILDSERVICERECORD_inputValue")));
			}
			System.out.println("Hakuna : "+ childVisits.toString());
			
			if(!rs.isClosed()){
				rs.close();
			}
			return childVisits;
		}
		catch(Exception e){
			e.printStackTrace();
			return new JSONObject();
		}	
	}
	
	private static JSONObject addAdditionalKey(JSONObject json) throws JSONException{
		System.out.println("Bingo : "+PropertiesInfo.SERVICE_JSON_PROPERTIES.getProperty("CHILDSERVICERECORD_retrieve_json"));
		for (String key : PropertiesInfo.SERVICE_JSON_PROPERTIES.getProperty("CHILDSERVICERECORD_retrieve_json").split(",")){
			json.put(key, "");
		}				
		return json;
	}
}