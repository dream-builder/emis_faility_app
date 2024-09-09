package org.sci.rhis.db.pac;

import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

/**
 * @author sabah.mugab
 * @since October, 2015
 */
public class RetrievePACVisitInfo {

	public static JSONObject getPACVisits(JSONObject PACInfo,
										  JSONObject PACVisits, QueryBuilder dynamicQueryBuilder) {

		try{
			String sql = "SELECT * FROM " + dynamicQueryBuilder.getTable("PAC")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "PAC_healthId",new String[]{PACInfo.getString("healthId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "PAC_pregNo",new String[]{PACInfo.getString("pregNo")},"=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("table", "PAC_serviceId") + " ASC";

			SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));

			PACVisits.put("pacStatus", true);
			PACVisits.put("count", 0);
			PACInfo.put("distributionJson", "treatment");

			while(rs.next()){
				PACVisits.put(rs.getString(dynamicQueryBuilder.getColumn("PAC_serviceId")),
						new JsonHandler().getServiceDetail(rs, PACInfo, "PAC", dynamicQueryBuilder, 2));
				PACVisits.put("outcomeDate", "");
				PACVisits.put("pacStatus", false);
				PACVisits.put("count", (PACVisits.getInt("count")+1));

			}

			sql = "SELECT " + dynamicQueryBuilder.getColumn("table", "DELIVERY_dDate") + ","
					+ dynamicQueryBuilder.getColumn("table", "DELIVERY_dPlace") + ","
					+ dynamicQueryBuilder.getColumn("table", "DELIVERY_abortion")
					+ " FROM " + dynamicQueryBuilder.getTable("DELIVERY")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "DELIVERY_healthid",new String[]{PACInfo.getString("healthId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "DELIVERY_pregno",new String[]{PACInfo.getString("pregNo")},"=");

			rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));

			if(rs.next()){
				if(rs.getObject(dynamicQueryBuilder.getColumn("DELIVERY_abortion"))!=null &&
						rs.getInt(dynamicQueryBuilder.getColumn("DELIVERY_abortion"))==1){
					PACVisits.put("hasAbortionInformation", "Yes");
					if(rs.getObject(dynamicQueryBuilder.getColumn("DELIVERY_dDate"))!=null){
						PACVisits.put("outcomeDate", rs.getString(dynamicQueryBuilder.getColumn("DELIVERY_dDate")));
						PACVisits.put("outcomePlace", rs.getString(dynamicQueryBuilder.getColumn("DELIVERY_dPlace")));

						long days = Utilities.getDateDiff(rs.getDate(dynamicQueryBuilder.getColumn("DELIVERY_dDate")), new Date(), TimeUnit.DAYS);
						if(days > (4*7)){
							PACVisits.put("pacStatus", true);
						}
						else{
							PACVisits.put("pacStatus", false);
						}
					}
				}
				else{
					PACVisits.put("hasAbortionInformation", "No");
				}
			}
			else{
				PACVisits.put("hasAbortionInformation", "No");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return PACVisits;
	}
}