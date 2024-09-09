package org.sci.rhis.db.dbhelper;

import org.json.JSONObject;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @created June, 2015
 */
public class InsertDeliveryInfo {

	public static boolean createDelivery(JSONObject deliveryInfo, QueryBuilder dynamicQueryBuilder) {
		try{
			deliveryInfo.put("abortion","");
			if(deliveryInfo.get("dType").equals("3")){
				deliveryInfo.put("abortion","1");
				deliveryInfo.put("dType","");
			}
			if(deliveryInfo.has("dTime") && deliveryInfo.getString("dTime").startsWith(":")){
				deliveryInfo.put("dTime","");
			}

			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(deliveryInfo,"serviceId"), "DELIVERY")));
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(deliveryInfo,"serviceId"), "DELIVERY")));
			/*StockDistributionRequest.upsertDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("DELIVERY_dTreatment"),
					deliveryInfo, dynamicQueryBuilder);*/
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
