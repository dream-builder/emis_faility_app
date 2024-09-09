package org.sci.rhis.db.general_patient;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

import java.sql.ResultSet;

/**
 * @author jamil.zaman
 * @created April, 2016
 */
public class InsertGPVisitInfo {

	public static JSONObject createGPVisit(JSONObject GPInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(GPInfo,"serviceId"), "GP")));

			/*GPInfo = StockDistributionRequest.insertDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("GP_treatment"),
					GPInfo, dbOp, dbObject, dynamicQueryBuilder);*/

			/*if(status && !GPInfo.getString("treatment").equals("") && GPInfo.getString("treatment").contains("_")){
				HandleStockDistribution.insertDistributionInfo(GPInfo, dbOp, dbObject);
			}*/
			if(!GPInfo.get("mobileNo").equals("")){
				ClientInfoUtil.updateClientMobileNo(GPInfo);
			}
			return GPInfo;
		}
		catch(Exception e){
			e.printStackTrace();
			return GPInfo;
		}
	}
}