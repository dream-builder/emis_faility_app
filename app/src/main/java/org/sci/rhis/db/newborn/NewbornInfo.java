package org.sci.rhis.db.newborn;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.QueryBuilder;

/**
 * @author sabah.mugab
 * @created July, 2015
 */
public class NewbornInfo {

	public static JSONObject getDetailInfo(JSONObject newbornInfo) {

		JSONObject newbornInformation = new JSONObject();
		boolean newbornResult = false;
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		try{
			if(newbornInfo.get("newbornLoad").equals("")){
				newbornResult = InsertNewbornInfo.createNewborn(newbornInfo, dynamicQueryBuilder);
				newbornInformation.put("operation", "insert");
			}
			else if(newbornInfo.get("newbornLoad").equals("update")){
				newbornResult = UpdateNewbornInfo.updateNewborn(newbornInfo, dynamicQueryBuilder);
				newbornInformation.put("operation", "update");
			}
			else if(newbornInfo.get("newbornLoad").equals("delete")){
				newbornResult = DeleteNewbornInfo.deleteNewborn(newbornInfo, dynamicQueryBuilder);
				newbornInformation.put("operation", "delete");
			}
			else{
				newbornInformation = RetrieveNewbornInfo.getNewbornInfo(newbornInfo, newbornInformation, dynamicQueryBuilder);
				newbornResult = true;
				newbornInformation.put("operation", "retrieve");
			}
			newbornInformation.put("result", newbornResult);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return newbornInformation;
	}
}
