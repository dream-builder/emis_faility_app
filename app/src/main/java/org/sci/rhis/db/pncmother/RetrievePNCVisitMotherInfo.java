package org.sci.rhis.db.pncmother;

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
 * @created July, 2015
 */
public class RetrievePNCVisitMotherInfo {

	public static JSONObject getPNCVisitsMother(JSONObject PNCMotherInfo,
												JSONObject PNCVisitsMother, QueryBuilder dynamicQueryBuilder) {

		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		try{
			String sql = "SELECT * FROM " + dynamicQueryBuilder.getTable("PNCMOTHER") + " "
					+ "WHERE " + dynamicQueryBuilder.getColumn("table", "PNCMOTHER_healthid",new String[]{PNCMotherInfo.getString("healthid")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "PNCMOTHER_pregno",new String[]{PNCMotherInfo.getString("pregno")},"=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("table", "PNCMOTHER_serviceId") + " ASC";

			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql,null));

			PNCVisitsMother.put("count", 0);
			int count =1;
			PNCMotherInfo.put("distributionJson", "treatment");

			while(rs.next()){

				PNCVisitsMother.put(rs.getString(dynamicQueryBuilder.getColumn("PNCMOTHER_serviceId")),
						new JsonHandler().getServiceDetail(rs, PNCMotherInfo, "PNCMOTHER", dynamicQueryBuilder,2));
				PNCVisitsMother.put("outcomeDate", "");
				PNCVisitsMother.put("pncStatus", false);
				PNCVisitsMother.put("count", count);
				count = count + 1;
			}

			/*
			 * If current date is more than 7 weeks from delivery date
			 * then provider should not be able to insert anymore pnc information
			 */
			sql = "SELECT " + dynamicQueryBuilder.getColumn("table", "DELIVERY_dDate")
					+ " FROM " + dynamicQueryBuilder.getTable("DELIVERY")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "DELIVERY_healthid",new String[]{PNCMotherInfo.getString("healthid")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "DELIVERY_pregno",new String[]{PNCMotherInfo.getString("pregno")},"=");

			rs = new SimpleCursor(db.rawQuery(sql,null));

			if(rs.next()){
				PNCVisitsMother.put("hasDeliveryInformation", "Yes");
				if(rs.getObject(dynamicQueryBuilder.getColumn("DELIVERY_dDate"))!=null){
					PNCVisitsMother.put("outcomeDate", rs.getString(dynamicQueryBuilder.getColumn("DELIVERY_dDate")));

                    long days = Utilities.getDateDiff(rs.getDate(dynamicQueryBuilder.getColumn("DELIVERY_dDate")), new Date(), TimeUnit.DAYS);
					if(days > (7*7)){
						PNCVisitsMother.put("pncStatus", true);
					}
				}
			}
			else{
				PNCVisitsMother.put("hasDeliveryInformation", "No");
			}

			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return PNCVisitsMother;
	}
}
