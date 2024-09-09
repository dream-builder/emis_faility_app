package org.sci.rhis.db.iud.followup;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.db.dbhelper.HandleStockDistribution;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class RetrieveIUDFollowupInfo {

	public static JSONObject getIUDFollowup(JSONObject iudInfo, JSONObject iudInformation,QueryBuilder dynamicQueryBuilder) {

		try{
			String sql = "SELECT * FROM " + dynamicQueryBuilder.getTable("IUDFOLLOWUP")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "IUDFOLLOWUP_healthId",new String[]{iudInfo.getString("healthId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "IUDFOLLOWUP_iudCount",new String[]{iudInformation.getString("iudCount")},"=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("table", "IUDFOLLOWUP_serviceId") + " ASC";

			SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));

			JSONObject IUDFollowupVisits = new JSONObject();
			iudInformation.put("followupCount", 0);
			iudInfo.put("distributionJson","treatment");

			while(rs.next()){
				IUDFollowupVisits.put(rs.getString(dynamicQueryBuilder.getColumn("IUDFOLLOWUP_serviceId")),
						new JsonHandler().getServiceDetail(rs, iudInfo, "IUDFOLLOWUP", dynamicQueryBuilder, 2));

				iudInformation.put("followupCount", (iudInformation.getInt("followupCount")+1));
			}
			iudInformation.put("followup", IUDFollowupVisits);

			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return iudInformation;
	}
}