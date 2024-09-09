package org.sci.rhis.db.anc;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.JSONKeyMapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since June, 2015
 */
public class ANCVisit {

	final static int ANCSERVICETYPE = 1;

	public static JSONObject getDetailInfo(JSONObject ANCInfo) {

		try{
			JSONObject ANCVisits = new JSONObject();
			ANCInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(ANCInfo, "ANC"), ANCSERVICETYPE);
			QueryBuilder dynamicQueryBuilder = new QueryBuilder();
			if(ANCInfo.get("ancLoad").equals("")){
				InsertANCVisitInfo.createANCVisit(ANCInfo, dynamicQueryBuilder);
			}
			else if(ANCInfo.get("ancLoad").equals("update")){
				UpdateANCVisitInfo.updateANCVisit(ANCInfo, dynamicQueryBuilder);
			}
			else if(ANCInfo.get("ancLoad").equals("delete")){
				DeleteANCVisitInfo.deleteANCVisit(ANCInfo, dynamicQueryBuilder);
			}

			ANCVisits = RetrieveANCVisitInfo.getANCVisits(ANCInfo, ANCVisits, dynamicQueryBuilder);
			return ANCVisits;
		}
		catch(Exception e){
			e.printStackTrace();
			return new JSONObject();

		}

	}
}