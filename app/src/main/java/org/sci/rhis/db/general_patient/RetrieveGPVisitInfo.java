package org.sci.rhis.db.general_patient;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author jamil.zaman
 * @created April, 2016
 */
public class RetrieveGPVisitInfo {

	public static JSONObject getGPVisits(JSONObject GPInfo, JSONObject GPVisits, QueryBuilder dynamicQueryBuilder) {

		try{
			String sql = "SELECT * FROM " + dynamicQueryBuilder.getTable("GP") + " "
					+ "WHERE " + dynamicQueryBuilder.getColumn("table", "GP_healthId",new String[]{GPInfo.getString("healthId")},"=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("table", "GP_serviceId") + " ASC";

			SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));
			GPVisits.put("count", 0);
			GPInfo.put("distributionJson","treatment");

			while(rs.next()){
				GPVisits.put(rs.getString(dynamicQueryBuilder.getColumn("GP_serviceId")),  new JsonHandler().getServiceDetail(rs, GPInfo, "GP", dynamicQueryBuilder, 2));
				GPVisits.put("count", (GPVisits.getInt("count")+1));
			}

			if(!rs.isClosed()){
				rs.close();
			}
			return GPVisits;
		}
		catch(Exception e){
			e.printStackTrace();
			return new JSONObject();
		}
	}
}