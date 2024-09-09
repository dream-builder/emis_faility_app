package org.sci.rhis.db.pncchild;

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
public class RetrievePNCVisitChildInfo {

	public static JSONObject getPNCVisitsChild(JSONObject PNCChildInfo, JSONObject PNCVisitsChild, QueryBuilder dynamicQueryBuilder) {

		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		try{
			//retrieve all the child who are live(birthStatus = 1)
			String sql = "SELECT " + dynamicQueryBuilder.getColumn("table", "NEWBORN_childno")
					+ " FROM " + dynamicQueryBuilder.getTable("NEWBORN")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "NEWBORN_healthid",new String[]{PNCChildInfo.getString("healthid")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "NEWBORN_pregno",new String[]{PNCChildInfo.getString("pregno")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "NEWBORN_birthStatus",new String[]{"2","3"},"notin")
					+ " AND NOT EXISTS (SELECT " + dynamicQueryBuilder.getColumn("", "DEATH_childNo")
					+ " FROM " + dynamicQueryBuilder.getTable("DEATH")
					+ " WHERE " + dynamicQueryBuilder.getPartialCondition("table", "NEWBORN_healthid","table","DEATH_healthId","=")
					+ " AND " + dynamicQueryBuilder.getPartialCondition("table", "NEWBORN_pregno","table","DEATH_pregNo","=")
					+ " AND " + dynamicQueryBuilder.getPartialCondition("table", "NEWBORN_childno","table","DEATH_childNo","=")
					+ ") ORDER BY " + dynamicQueryBuilder.getColumn("table", "NEWBORN_childno") + " ASC";

			SimpleCursor rsChild=null, rsService = null;
			rsChild = new SimpleCursor(db.rawQuery(sql,null));

			PNCVisitsChild.put("childCount", 0);

			String childNo, childMapping = "";
			int count = 1;
			PNCChildInfo.put("distributionJson", "treatment");

			while(rsChild.next()){
				childNo = rsChild.getString(dynamicQueryBuilder.getColumn("NEWBORN_childno"));
				childMapping += childNo + ",";

				JSONObject individualPNCServiceChild = new JSONObject();

				sql = "SELECT * FROM " + dynamicQueryBuilder.getTable("PNCCHILD")
						+ " WHERE " + dynamicQueryBuilder.getColumn("table", "PNCCHILD_healthid",new String[]{PNCChildInfo.getString("healthid")},"=")
						+ " AND " + dynamicQueryBuilder.getColumn("table", "PNCCHILD_pregno",new String[]{PNCChildInfo.getString("pregno")},"=")
						+ " AND " + dynamicQueryBuilder.getColumn("table", "PNCCHILD_pncchildno",new String[]{childNo},"=")
						+ " ORDER BY " + dynamicQueryBuilder.getColumn("table", "PNCCHILD_serviceId") + " ASC";

				rsService = new SimpleCursor(db.rawQuery(sql,null));

				individualPNCServiceChild.put("serviceCount", 0);
				int cnt = 1;
				while(rsService.next()){
					individualPNCServiceChild.put(rsService.getString(dynamicQueryBuilder.getColumn("PNCCHILD_serviceId")),
							new JsonHandler().getServiceDetail(rsService, PNCChildInfo, "PNCCHILD", dynamicQueryBuilder,2));
					individualPNCServiceChild.put("pncStatus", false);
					individualPNCServiceChild.put("serviceCount", cnt);
					cnt = cnt + 1;
				}

				PNCVisitsChild.put(childNo, individualPNCServiceChild);
				PNCVisitsChild.put("pncStatus", false);
				PNCVisitsChild.put("childCount", count);
				count = count + 1;
			}
			if(childMapping.length()> 0){
				childMapping = childMapping.substring(0, childMapping.length()-1);
			}
			PNCVisitsChild.put("childMapping", childMapping);
			/*
			 * If current date is more than 7 weeks from delivery date
			 * then provider should not be able to insert anymore pnc information
			 */
			sql = "SELECT " + dynamicQueryBuilder.getColumn("table", "DELIVERY_dDate")
					+ " FROM " + dynamicQueryBuilder.getTable("DELIVERY")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "DELIVERY_healthid",new String[]{PNCChildInfo.getString("healthid")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "DELIVERY_pregno",new String[]{PNCChildInfo.getString("pregno")},"=");

			rsChild = new SimpleCursor(db.rawQuery(sql,null));

			if(rsChild.next()){
				PNCVisitsChild.put("hasDeliveryInformation", "Yes");
				if(rsChild.getObject(dynamicQueryBuilder.getColumn("DELIVERY_dDate"))!=null){
                    long days = Utilities.getDateDiff(rsChild.getDate(dynamicQueryBuilder.getColumn("DELIVERY_dDate")), new Date(), TimeUnit.DAYS);
					if(days > (7*7)){
						PNCVisitsChild.put("pncStatus", true);
					}
				}
			}
			else{
				PNCVisitsChild.put("hasDeliveryInformation", "No");
			}

			if(rsService!=null){
				if(!rsService.isClosed()){
					rsService.close();
				}
			}
			if(rsChild!=null){
				if(!rsChild.isClosed()){
					rsChild.close();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return PNCVisitsChild;
	}
}
