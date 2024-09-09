package org.sci.rhis.db.iud;

import android.database.sqlite.SQLiteDatabase;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.HandleStockDistribution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.db.fpcommon.FPStatus;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class InsertIUDInfo {

	public static JSONObject createIUD(JSONObject iudInfo, JSONObject iudInformation, QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit
							(new JsonHandler().addJsonKeyIncrementalField
									(iudInfo,"iudCount"), "IUD")));
			int iudCount = CommonQueryExecution.generateServiceID(iudInfo.getString("healthId"),"iudService");
			iudInfo.put("iudInsertSuccess","1");
			iudInfo.put("iudCount",iudCount);

			iudInfo.put("isNewClient", 1);
			iudInfo.put("currentMethod", 4);
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(iudInfo, "FPSTATUS")));

//				StockDistributionRequest.insertDistributionInfoHandler(true, iudInfo, dbOp, dbObject, dynamicQueryBuilder);

			//inserting a row in fp history
			if(!iudInfo.getString("currentMethod").equals("")){
				iudInfo.put("fpCommonCode", ConstantMaps.FP_METHOD_MAPPING_FOR_HISTORY
						.get(Integer.valueOf(iudInfo.getString("currentMethod"))));
				CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(
						new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
								(iudInfo,""), "FP_HISTORY")));
			}

			if(!iudInfo.get("mobileNo").equals("")){
				ClientInfoUtil.updateClientMobileNo(iudInfo);
			}

			iudInformation = RetrieveIUDInfo.getIUD(iudInfo, iudInformation, dynamicQueryBuilder);

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return iudInformation;
	}
}