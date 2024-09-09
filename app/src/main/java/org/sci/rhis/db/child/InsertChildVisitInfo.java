package org.sci.rhis.db.child;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.PropertiesInfo;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2018
 */
public class InsertChildVisitInfo {
	
	public static JSONObject createChildVisit(JSONObject childServiceInfo, QueryBuilder dynamicQueryBuilder) {
		
		try{	
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
					(childServiceInfo,"serviceId"), "CHILD")));
			
			insertChildVisitDetail(childServiceInfo, dynamicQueryBuilder);


			if(!childServiceInfo.get("mobileNo").equals("")){			
				ClientInfoUtil.updateClientMobileNo(childServiceInfo);
			}
			return childServiceInfo;
		}
		catch(Exception e){
			e.printStackTrace();
			return childServiceInfo;
		}			
	}
	
	public static boolean insertChildVisitDetail(JSONObject childServiceInfo, QueryBuilder dynamicQueryBuilder){
		try{
			String sql = "";
			String commonVal = "";
			
			if(!childServiceInfo.getString("serviceId").equals("")){

				childServiceInfo.put("entryDate", childServiceInfo.getString("systemEntryDate"));
				System.out.println(childServiceInfo.getString("entryDate"));
				sql = dynamicQueryBuilder.getMultiInsertQuery(new JsonHandler().addJsonKeyValueEdit(childServiceInfo, "CHILDSERVICERECORD"));

				commonVal = getValue("CHILDSERVICERECORD_healthId",childServiceInfo.getString("healthId")) + ","
							+ getValue("CHILDSERVICERECORD_systemEntryDate",childServiceInfo.getString("entryDate")) + ",";
				
				for (String key : PropertiesInfo.SERVICE_JSON_PROPERTIES.getProperty("CHILDSERVICERECORD_insert_variable_json").split(",")){
					if(!childServiceInfo.getString(key).equals("")){
						sql = sql + "(" + commonVal + getValue("CHILDSERVICERECORD_inputId",key) + ","
									+ getValue("CHILDSERVICERECORD_inputValue",childServiceInfo.getString(key)) + "),";
					}
				}
				CommonQueryExecution.executeQuery(sql.substring(0, sql.length()-1));
			}			
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}		
	}
	
	private static String getValue(String column, String val){
		String[] columnDetail = PropertiesInfo.SERVICE_JSON_MAP_PROPERTIES.getProperty(column).split(",");
		return val.equals("") ? columnDetail[2] : (columnDetail[1].equals("int") ? val : ("'" + val + "'"));		
	}
}