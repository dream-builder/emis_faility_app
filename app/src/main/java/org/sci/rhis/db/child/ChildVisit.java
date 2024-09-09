package org.sci.rhis.db.child;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CreateRegNo;
import org.sci.rhis.db.dbhelper.JSONKeyMapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2018
 */
public class ChildVisit {
	
	final static int childService = 7; //for registration number
	final static int CHILDSERVICETYPE = 13; //for item distribution
	
	public static JSONObject getDetailInfo(JSONObject childServiceInfo) {

		JSONObject childVisits = new JSONObject();

		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		try{
			childServiceInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(childServiceInfo, "CHILD"), CHILDSERVICETYPE);
			childServiceInfo.put("serviceCategory", childService);

			if(childServiceInfo.get("childLoad").equals("insert")){
				childServiceInfo = InsertChildVisitInfo.createChildVisit(childServiceInfo, dynamicQueryBuilder);
				if(childServiceInfo.getString("serviceId").equals("1")){
					CreateRegNo.pushReg(childServiceInfo, childVisits);
				}
			}
			else if(childServiceInfo.get("childLoad").equals("update")){
				UpdateChildVisitInfo.updateChildVisit(childServiceInfo, dynamicQueryBuilder);
			}
			else if(childServiceInfo.get("childLoad").equals("delete")){
				DeleteChildVisitInfo.deleteChildVisit(childServiceInfo, dynamicQueryBuilder);
			}

			childVisits = RetrieveChildVisitInfo.getChildVisits(childServiceInfo,childVisits, dynamicQueryBuilder);
			childVisits = ClientInfoUtil.getRegNumber(childServiceInfo, childVisits);

		}catch (JSONException jse){
			jse.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return childVisits;
	}
}