package org.sci.rhis.db.general_patient;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CreateRegNo;
import org.sci.rhis.db.dbhelper.JSONKeyMapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author jamil.zaman
 * @created April, 2016
 */
public class GPVisit {

	final static int generalPatient = 5; //for registration number
	final static int GPSERVICETYPE = 6; //for item distribution

	public static JSONObject getDetailInfo(JSONObject GPInfo) {

		JSONObject GPVisits = new JSONObject();


		try{
			GPInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(GPInfo, "GP"), GPSERVICETYPE);
			QueryBuilder dynamicQueryBuilder = new QueryBuilder();
			GPInfo.put("serviceCategory", generalPatient);

			if(GPInfo.get("gpLoad").equals("")){
				GPInfo = InsertGPVisitInfo.createGPVisit(GPInfo, dynamicQueryBuilder);
			}
			else if(GPInfo.get("gpLoad").equals("update")){
				UpdateGPVisitInfo.updateGPVisit(GPInfo, dynamicQueryBuilder);
			}
			else if(GPInfo.get("gpLoad").equals("delete")){
				DeleteGPVisitInfo.deleteGPVisit(GPInfo, dynamicQueryBuilder);
			}

			if(GPInfo.get("gpLoad").equals("") && GPInfo.getString("serviceId").equals("1")){
				CreateRegNo.pushReg(GPInfo, GPVisits);
			}

			GPVisits = RetrieveGPVisitInfo.getGPVisits(GPInfo,GPVisits, dynamicQueryBuilder);
			GPVisits = ClientInfoUtil.getRegNumber(GPInfo, GPVisits);

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return GPVisits;
	}
}