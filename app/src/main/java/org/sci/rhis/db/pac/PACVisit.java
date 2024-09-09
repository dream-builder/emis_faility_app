package org.sci.rhis.db.pac;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.JSONKeyMapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since October, 2015
 */
public class PACVisit {

	final static int PACSERVICETYPE = 5;

	public static JSONObject getDetailInfo(JSONObject PACInfo) {

		JSONObject PACVisits = new JSONObject();
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		try{
			PACInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(PACInfo, "PAC"), PACSERVICETYPE);

			if(PACInfo.get("pacLoad").equals("")){
				InsertPACVisitInfo.createPACVisit(PACInfo, dynamicQueryBuilder);
			}
			if(PACInfo.get("pacLoad").equals("update")){
				UpdatePACVisitInfo.updatePACVisit(PACInfo, dynamicQueryBuilder);
			}
			else if(PACInfo.get("pacLoad").equals("delete")){
				DeletePACVisitInfo.deletePACVisit(PACInfo, dynamicQueryBuilder);
			}

			PACVisits = RetrievePACVisitInfo.getPACVisits(PACInfo,PACVisits, dynamicQueryBuilder);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return PACVisits;
	}
}