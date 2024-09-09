package org.sci.rhis.db.pncmother;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.JSONKeyMapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @created July, 2015
 */
public class PNCVisitMother {

	final static int PNCMOTHERSERVICETYPE = 3;

	public static JSONObject getDetailInfo(JSONObject PNCMotherInfo) throws JSONException{

		JSONObject PNCVisitsMother = new JSONObject();
		PNCMotherInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(PNCMotherInfo, "PNCMOTHER"), PNCMOTHERSERVICETYPE);
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		try{
			if(PNCMotherInfo.get("pncMLoad").equals("")){
				InsertPNCVisitMotherInfo.createPNCVisitMother(PNCMotherInfo, dynamicQueryBuilder);
			}
			else if(PNCMotherInfo.get("pncMLoad").equals("update")){
				UpdatePNCVisitMotherInfo.updatePNCVisitMother(PNCMotherInfo, dynamicQueryBuilder);
			}
			else if(PNCMotherInfo.get("pncMLoad").equals("delete")){
				DeletePNCVisitMotherInfo.deletePNCVisitMother(PNCMotherInfo, dynamicQueryBuilder);
			}

			PNCVisitsMother = RetrievePNCVisitMotherInfo.getPNCVisitsMother(PNCMotherInfo,PNCVisitsMother, dynamicQueryBuilder);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return PNCVisitsMother;
	}
}
