package org.sci.rhis.db.iud.followup;


import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.HandleStockDistribution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class DeleteIUDFollowupInfo {

	public static boolean deleteIUDFollowup(JSONObject iudInfo, JSONObject iudInformation, QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
							(iudInfo,"serviceId"), "IUDFOLLOWUP")));

			/*StockDistributionRequest.deleteDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("IUDFOLLOWUP_treatment"),
					iudInfo, dbOp, dbObject, dynamicQueryBuilder);*/

			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}