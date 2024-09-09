package org.sci.rhis.db.pillcondom;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since January, 2016
 */
public class InsertPillCondomVisitInfo {

	public static JSONObject createPillCondomVisit(JSONObject pillCondomInfo, QueryBuilder dynamicQueryBuilder) {

		int serviceId=0;

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(pillCondomInfo,"serviceId"), "PILLCONDOM")));

			serviceId = CommonQueryExecution.generateServiceID(pillCondomInfo.getString("healthId"),dynamicQueryBuilder.getTable("PILLCONDOM"));

			pillCondomInfo.put("serviceId",serviceId);
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(
					new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(pillCondomInfo,""), "FPEXAMINATION_PILLCONDOM")));


			/*CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(
					new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(pillCondomInfo,""), "FP_HISTORY")));*/

			if(pillCondomInfo.getString("methodType").equals("1") || pillCondomInfo.getString("methodType").equals("2")
					|| pillCondomInfo.getString("methodType").equals("10")){
				pillCondomInfo.put("isNewClient", 1);
				pillCondomInfo.put("currentMethod", pillCondomInfo.get("methodType"));
			}
			else{
				pillCondomInfo.put("isNewClient", 2);
				pillCondomInfo.put("currentMethod", "");
			}
			//inserting a row in fp history
			if(!pillCondomInfo.getString("currentMethod").equals("")){
				pillCondomInfo.put("fpCommonCode", ConstantMaps.FP_METHOD_MAPPING_FOR_HISTORY
						.get(Integer.valueOf(pillCondomInfo.getString("currentMethod"))));
				CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(
						new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
								(pillCondomInfo,""), "FP_HISTORY")));
			}
			//update fpinfo
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(
					new JsonHandler().addJsonKeyValueEdit(pillCondomInfo, "FPSTATUS")));

			if(!pillCondomInfo.get("mobileNo").equals("")){
				ClientInfoUtil.updateClientMobileNo(pillCondomInfo);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return pillCondomInfo;
	}
}