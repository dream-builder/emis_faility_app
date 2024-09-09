package org.sci.rhis.db.death;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since September, 2015
 */
public class InsertDeathInfo {

	public static void submitDeathInfo(JSONObject deathInfo, QueryBuilder dynamicQueryBuilder) throws JSONException{

		if(deathInfo.get("childNo").equals("") || deathInfo.get("childNo").equals("0")){
			deathInfo.put("pregNo","0");
		}

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(new JsonHandler().addJsonKeyValueEdit(deathInfo, "DEATH")));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}