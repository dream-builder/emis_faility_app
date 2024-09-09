package org.sci.rhis.db.pncchild;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.JSONKeyMapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @created July, 2015
 */
public class PNCVisitChild {

	final static int PNCCHILDSERVICETYPE = 4;

	public static JSONObject getDetailInfo(JSONObject PNCChildInfo) throws JSONException{

		JSONObject PNCVisitsChild = new JSONObject();
		PNCChildInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(PNCChildInfo, "PNCCHILD"), PNCCHILDSERVICETYPE);
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		try{
			if(PNCChildInfo.get("pncCLoad").equals("")){
				InsertPNCVisitChildInfo.createPNCVisitChild(PNCChildInfo, dynamicQueryBuilder);
			}
			else if(PNCChildInfo.get("pncCLoad").equals("update")){
				UpdatePNCVisitChildInfo.updatePNCVisitChild(PNCChildInfo, dynamicQueryBuilder);
			}
			else if(PNCChildInfo.get("pncCLoad").equals("delete")){
				DeletePNCVisitChildInfo.deletePNCVisitChild(PNCChildInfo, dynamicQueryBuilder);
			}

			PNCVisitsChild = RetrievePNCVisitChildInfo.getPNCVisitsChild(PNCChildInfo,PNCVisitsChild, dynamicQueryBuilder);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return PNCVisitsChild;
	}
}
