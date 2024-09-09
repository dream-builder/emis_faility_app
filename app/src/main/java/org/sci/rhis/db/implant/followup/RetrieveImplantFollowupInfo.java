package org.sci.rhis.db.implant.followup;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class RetrieveImplantFollowupInfo {

	public static JSONObject getImplantFollowup(JSONObject implantInfo, JSONObject implantInformation, QueryBuilder dynamicQueryBuilder) {

		try{
			String sql = "SELECT * FROM " + dynamicQueryBuilder.getTable("IMPLANTFOLLOWUP")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "IMPLANTFOLLOWUP_healthId",new String[]{implantInfo.getString("healthId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "IMPLANTFOLLOWUP_implantCount",new String[]{implantInformation.getString("implantCount")},"=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("table", "IMPLANTFOLLOWUP_serviceId") + " ASC";

			SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));

			JSONObject ImplantFollowupVisits = new JSONObject();
			implantInformation.put("followupCount", 0);
			implantInfo.put("distributionJson","treatment");

			while(rs.next()){
				ImplantFollowupVisits.put(rs.getString(dynamicQueryBuilder.getColumn("IMPLANTFOLLOWUP_serviceId")),
						new JsonHandler().getServiceDetail(rs, implantInfo, "IMPLANTFOLLOWUP", dynamicQueryBuilder, 2));

				implantInformation.put("followupCount", (implantInformation.getInt("followupCount")+1));
			}
			implantInformation.put("followup", ImplantFollowupVisits);

			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return implantInformation;
	}
}