package org.sci.rhis.db.dbhelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @created June, 2015
 */
public class DeliveryInfo {

	final static int DELIVERYSERVICETYPE = 2;

	public static JSONObject getDetailInfo(JSONObject deliveryInfo) throws JSONException{

		JSONObject deliveryInformation = new JSONObject();
		deliveryInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(deliveryInfo, "DELIVERY"), DELIVERYSERVICETYPE);
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		try{
			if(deliveryInfo.get("deliveryLoad").equals("")){
				InsertDeliveryInfo.createDelivery(deliveryInfo, dynamicQueryBuilder);
			}

			deliveryInformation = RetrieveDeliveryInfo.getDeliveryInfo(deliveryInfo, deliveryInformation, dynamicQueryBuilder);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return deliveryInformation;
	}
}