package org.sci.rhis.db.womaninjectable;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since February, 2016
 */
public class InsertInjectableVisitInfo {

	public static JSONObject createInjectableVisit(JSONObject injectableInfo, QueryBuilder dynamicQueryBuilder) {
		try{
			injectableInfo.put("sideEffectCore",injectableInfo.get("sideEffect").equals("")? 2 : 1);

			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(injectableInfo,"doseId"), "WOMANINJECTABLE")));
			int doseId = CommonQueryExecution.generateServiceID(injectableInfo.getString("healthId"),"womanInjectable");

			injectableInfo.put("serviceId",doseId);
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(
					new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(injectableInfo,""), "FPEXAMINATION_WOMANINJECTABLE")));

			injectableInfo.put("isNewClient", 1);
			injectableInfo.put("currentMethod", Flag.INJECTION_DMPA);
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(
					new JsonHandler().addJsonKeyValueEdit(injectableInfo, "FPSTATUS")));
			/*StockDistributionRequest.insertDistributionInfoHandler(true, injectableInfo, dbOp, dbObject, dynamicQueryBuilder);*/

			//inserting a row in fp history
			if(!injectableInfo.getString("currentMethod").equals("")){
				injectableInfo.put("fpCommonCode", ConstantMaps.FP_METHOD_MAPPING_FOR_HISTORY
						.get(Integer.valueOf(injectableInfo.getString("currentMethod"))));
				CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(
						new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
								(injectableInfo,""), "FP_HISTORY_WOMANINJECTABLE")));
			}

			if(!injectableInfo.get("mobileNo").equals("")){
				ClientInfoUtil.updateClientMobileNo(injectableInfo);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return injectableInfo;
	}
}