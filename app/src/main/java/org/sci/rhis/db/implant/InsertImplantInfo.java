package org.sci.rhis.db.implant;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.fpcommon.FPStatus;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class InsertImplantInfo {

	public static JSONObject createImplant(JSONObject implantInfo, JSONObject implantInformation, QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit
							(new JsonHandler().addJsonKeyIncrementalField
									(implantInfo,"implantCount"), "IMPLANT")));

			int implantCount = CommonQueryExecution.generateServiceID(implantInfo.getString("healthId"),dynamicQueryBuilder.getTable("IMPLANT"));



			implantInfo.put("implantInsertSuccess","1");
			//implantInfo.put("healthId",implantInfo.get("healthId"));
			implantInfo.put("implantCount",implantCount);

			implantInfo.put("isNewClient",1);
			implantInfo.put("currentMethod", ((implantInfo.getString("implantType").equals("1"))? Flag.IMPLANT_IMPLANON:Flag.IMPLANT_JADELLE));
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(implantInfo, "FPSTATUS")));

				/*StockDistributionRequest.insertDistributionInfoHandler(true, implantInfo, dbOp, dbObject, dynamicQueryBuilder);*/

			//inserting a row in fp history
			if(!implantInfo.getString("currentMethod").equals("")){
				implantInfo.put("fpCommonCode", ConstantMaps.FP_METHOD_MAPPING_FOR_HISTORY
						.get(Integer.valueOf(implantInfo.getString("currentMethod"))));
				CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(
						new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
								(implantInfo,""), "FP_HISTORY")));
			}

			if(!implantInfo.get("mobileNo").equals("")){
				ClientInfoUtil.updateClientMobileNo(implantInfo);
			}

			implantInformation = RetrieveImplantInfo.getImplant(implantInfo, implantInformation,
					dynamicQueryBuilder);

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return implantInformation;
	}
}