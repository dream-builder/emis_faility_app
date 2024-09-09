package org.sci.rhis.utilities;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DBTableInfoHandler;
import org.sci.rhis.db.dbhelper.HandleStockDistribution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;

import java.util.Iterator;

/**
 * @author sabah.mugab
 * @since February, 2017
 */
public class JsonHandler {

	public JSONObject addJsonKeyValueEdit(JSONObject jsonObj, String service) throws JSONException{
		jsonObj.put("serviceName", service);
		jsonObj.put("systemEntryDate", Utilities.getDateTimeWithoutTimeStampStringDBFormat());
		jsonObj.put("modifyDate", Utilities.getDateTimeWithoutTimeStampStringDBFormat());

		return jsonObj;
	}

	public JSONObject addJsonKeyValueEdit(JSONObject jsonObj, String service, boolean systemEntryDateModifiable) throws JSONException{
		jsonObj.put("serviceName", service);
		if(systemEntryDateModifiable){
			jsonObj.put("systemEntryDate", Utilities.getDateTimeWithoutTimeStampStringDBFormat());
		}
		jsonObj.put("modifyDate", Utilities.getDateTimeWithoutTimeStampStringDBFormat());

		return jsonObj;
	}

	public JSONObject addJsonKeyIncrementalField(JSONObject jsonObj, String keyName) throws JSONException{
		if(!jsonObj.has(keyName)){
			jsonObj.put(keyName,"");
		}
		jsonObj.put("incrementalField", keyName);

		return jsonObj;
	}

	public JSONObject addJsonKeyMaxField(JSONObject jsonObj, String keyName) throws JSONException{
		if(!jsonObj.has(keyName)){
			jsonObj.put(keyName,"");
		}
		jsonObj.put("maxField", keyName);

		return jsonObj;
	}

	public JSONObject addJsonKeyValueStockDistribution(JSONObject jsonObj, int serviceType) throws JSONException{

		jsonObj.put("serviceType", serviceType);
		jsonObj.put("treatment_key", "distributionId");
		if(jsonObj.has("treatment")){
			if(jsonObj.getString("treatment").contains("_")){
				jsonObj.put("distributionId", Utilities.getDateTimeStringMs());
			}
			else{
				jsonObj.put("distributionId",jsonObj.getString("treatment"));
			}
		}
		else{
			jsonObj.put("distributionId","");
		}

		return jsonObj;
	}

	public String getStringValue(String stringValue, String comparisonStringValue){

		try {
			return (stringValue.equals(comparisonStringValue) ? "" : stringValue);
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getResultSetValue(SimpleCursor rs, String columnName){

		try {
			return (rs.getObject(columnName)==null?"":rs.getString(columnName));
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getResultSetValue(SimpleCursor rs, String columnName, String defaultValue){

		try {
			System.out.println(columnName);
			return (rs.getObject(columnName)==null ? defaultValue : rs.getString(columnName));
		}
		catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject getServiceDetail(SimpleCursor rs, JSONObject serviceInfo,
									   String serviceName, QueryBuilder dynamicQueryBuilder, int addAdditional) throws JSONException{

		JSONObject response = new JSONObject();
		if(addAdditional==1){
			response = serviceInfo;
		}
		String key = "";
		if(!serviceInfo.has("distributionJson")){
			serviceInfo.put("distributionJson", "");
		}

		try{
			JSONObject detailJson = new DBTableInfoHandler(serviceName,"retrieve").getDetail().getJSONObject("keyMapField");

			Iterator<String> keys = detailJson.keys();

			while(keys.hasNext()){
				key = keys.next();
				if(key.equals(serviceInfo.getString("distributionJson"))){
					serviceInfo.put("distributionId",getResultSetValue(rs,detailJson.getJSONObject(key).getString("dbColumnName")));
					response.put(key, (serviceInfo.getString("distributionId").equals("")||serviceInfo.getString("distributionId").startsWith("["))?
							serviceInfo.getString("distributionId"):
							HandleStockDistribution.getDistributionInfo(serviceInfo, dynamicQueryBuilder));
				}
				else{
					response.put(key,getResultSetValue(rs,detailJson.getJSONObject(key).getString("dbColumnName"),
							getStringValue(detailJson.getJSONObject(key).getString("responseDefaultValue"),"null")));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return response;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getResponse(SimpleCursor rs, JSONObject resultJson, String serviceName, int addAdditional){

		JSONObject response = new JSONObject();
        if(addAdditional==1){
            response = resultJson;
        }
		String key = "";

		try{
			JSONObject detailJson = new DBTableInfoHandler(serviceName,"retrieve").getDetail().getJSONObject("keyMapField");

			Iterator<String> keys = detailJson.keys();

			while(keys.hasNext()){
				key = keys.next();
				response.put(key,getResultSetValue(rs,detailJson.getJSONObject(key).getString("dbColumnName"),
						getStringValue(detailJson.getJSONObject(key).getString("responseDefaultValue"),"null")));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return response;
	}
}