package org.sci.rhis.db.general_patient;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author jamil.zaman
 * @created April, 2016
 */

public class DeleteGPVisitInfo {

	public static boolean deleteGPVisit(JSONObject GPInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
							(GPInfo,"serviceId"), "GP")));

			/*StockDistributionRequest.deleteDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("GP_treatment"),
					GPInfo, dbOp, dbObject, dynamicQueryBuilder);*/


			return true;

			/*if(status && !GPInfo.getString("distributionId").equals("") && !GPInfo.getString("distributionId").startsWith("[")){
				HandleStockDistribution.deleteDistributionInfo(GPInfo, dbOp, dbObject);
			}*/
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}