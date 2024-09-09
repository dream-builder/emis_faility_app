package org.sci.rhis.db.death;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.QueryBuilder;


/**
 * @author sabah.mugab
 * @since September, 2015
 */
public class DeathInfo {

	public static JSONObject reportDeath(JSONObject deathInfo) {

		JSONObject deathDetail = new JSONObject();
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		try{
			if(deathInfo.get("deathLoad").equals("")){
                InsertDeathInfo.submitDeathInfo(deathInfo, dynamicQueryBuilder);
			}else if(deathInfo.get("deathLoad").equals("update")){
                UpdateDeathInfo.updateInfo(deathInfo, dynamicQueryBuilder);
            }
			if(deathInfo.get("deathLoad").equals("retrieveChild")){
				deathDetail = RetrieveChildInfo.getChild(deathInfo, dynamicQueryBuilder);
			}
			else{
				deathDetail = RetrieveDeathInfo.getDeath(deathInfo, dynamicQueryBuilder);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return deathDetail;
	}
}