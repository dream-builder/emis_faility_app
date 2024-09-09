package org.sci.rhis.db.dbhelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author sabah.mugab
 * @created June, 2015
 */
public class ClientInfo {

	public static JSONObject getDetailInfo(JSONObject searchClient) throws JSONException{

		JSONObject clientInformation = new JSONObject();
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		int searchItem = searchClient.getInt("sOpt");
		searchClient.put("colName","");

		try{
			switch(searchItem){
				case 1:
					searchClient.put("colName",dynamicQueryBuilder.getColumn("table","CM_healthid"));
					clientInformation = ClientMapGeneralInfo.retrieveDetailInfo(searchClient, dynamicQueryBuilder);
					break;
				case 2:
					searchClient.put("colName",dynamicQueryBuilder.getColumn("m","MEMBER_mobilenumber"));
					clientInformation = ClientGeneralInfo.retrieveInfo(searchClient, dynamicQueryBuilder);
					break;
				case 3:
					searchClient.put("colName",dynamicQueryBuilder.getColumn("m","MEMBER_nid"));
					clientInformation = ClientGeneralInfo.retrieveInfo(searchClient, dynamicQueryBuilder);
					break;
				case 4:
					searchClient.put("colName",dynamicQueryBuilder.getColumn("m","MEMBER_brid"));
					clientInformation = ClientGeneralInfo.retrieveInfo(searchClient, dynamicQueryBuilder);
					break;
				case 5:
					searchClient.put("colName",dynamicQueryBuilder.getColumn("table","CM_healthid"));
					clientInformation = ClientMapGeneralInfo.retrieveDetailInfo(searchClient, dynamicQueryBuilder);
					clientInformation.put("idType", "5");
					break;
			}

			if(!clientInformation.has("idType")){
				clientInformation.put("idType", "1");
			}
			searchClient.put("serviceCategory", 1);
			clientInformation = RetrieveRegNo.pullReg(searchClient,clientInformation);

			if(!clientInformation.get("False").equals("false")){
				clientInformation.put("False","");
				clientInformation = ImmunizationTT.getImmunizationHistory(clientInformation);
				clientInformation = PregInfo.retrievePregInfo(clientInformation);
			}
			else{
				clientInformation.put("False", "false");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return clientInformation;
	}
}
