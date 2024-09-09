package org.sci.rhis.db.iud.followup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.ResultSet;
import java.util.Calendar;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.HandleStockDistribution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class InsertIUDFollowupInfo {

	public static boolean createIUDFollowup(JSONObject iudInfo, JSONObject iudInformation,
											QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit
							(new JsonHandler().addJsonKeyIncrementalField
									(iudInfo,"serviceId"), "IUDFOLLOWUP")));

			iudInfo.put("iudFollowupInsertSuccess","1");

			int serviceId = 0;
			Cursor rs =  DatabaseWrapper.getDatabase().rawQuery("SELECT COUNT("
					+ dynamicQueryBuilder.getColumn("IUDFOLLOWUP_serviceId")+") FROM "+dynamicQueryBuilder.getTable("IUDFOLLOWUP") +
					" WHERE "+ dynamicQueryBuilder.getColumn("table", "IUDFOLLOWUP_healthId",new String[]{iudInfo.getString("healthId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "IUDFOLLOWUP_iudCount",new String[]{iudInformation.getString("iudCount")},"="), null);
			if(rs.moveToFirst()){
				serviceId = rs.getInt(0);
			}

			iudInfo.put("healthId",iudInfo.get("healthId"));
			iudInfo.put("iudCount",iudInfo.get("iudCount"));
			iudInfo.put("serviceId",serviceId);
			iudInfo.put("treatment",iudInfo.get("treatment"));

			/*StockDistributionRequest.insertDistributionInfoHandler(true, iudInfo, dbOp, dbObject, dynamicQueryBuilder);*/

			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}