package org.sci.rhis.db.iud;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CreateRegNo;
import org.sci.rhis.db.dbhelper.JSONKeyMapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.iud.followup.DeleteIUDFollowupInfo;
import org.sci.rhis.db.iud.followup.InsertIUDFollowupInfo;
import org.sci.rhis.db.iud.followup.RetrieveIUDFollowupInfo;
import org.sci.rhis.db.iud.followup.UpdateIUDFollowupInfo;
import org.sci.rhis.utilities.JsonHandler;

import java.util.Calendar;

/**
 * @author sabah.mugab
 * @since February, 2016
 */
public class IUDInfo {

	final static int iud = 4; //for registration number
	final static int IUDSERVICTYPE = 9; //for item distribution
	final static int IUDFOLLOWUPSERVICTYPE = 10; //for item distribution

	public static JSONObject getDetailInfo(JSONObject iudInfo) {

		JSONObject iudInformation = new JSONObject();
		
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();
		

		try{
			iudInformation = new JSONObject();
			iudInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(iudInfo, "IUD"), IUDSERVICTYPE);
			iudInfo.put("serviceCategory", iud);

			if(iudInfo.get("iudLoad").equals("insert")){
				iudInformation = InsertIUDInfo.createIUD(iudInfo, iudInformation, dynamicQueryBuilder);
				if(iudInformation.getString("iudInsertSuccess").equals("1")){
					CreateRegNo.pushReg(iudInfo, iudInformation);
				}
			}
			else if(iudInfo.get("iudLoad").equals("update")){
				iudInformation = UpdateIUDInfo.updateIUD(iudInfo, iudInformation, dynamicQueryBuilder);
			}
			else if(iudInfo.get("iudLoad").equals("retrieve")){
				iudInformation = RetrieveIUDInfo.getIUD(iudInfo, iudInformation, dynamicQueryBuilder);
			}
			else if(iudInfo.get("iudLoad").equals("")){
				iudInformation.put("iudCount", iudInfo.getString("iudCount"));
				iudInfo.put("serviceType", IUDFOLLOWUPSERVICTYPE);

				if(iudInfo.get("iudFollowupLoad").equals("insert")){
					InsertIUDFollowupInfo.createIUDFollowup(iudInfo, iudInformation, dynamicQueryBuilder);
				}
				else if(iudInfo.get("iudFollowupLoad").equals("update")){
					UpdateIUDFollowupInfo.updateIUDFollowup(iudInfo, iudInformation, dynamicQueryBuilder);
				}
				else if(iudInfo.get("iudFollowupLoad").equals("delete")){
					DeleteIUDFollowupInfo.deleteIUDFollowup(iudInfo, iudInformation, dynamicQueryBuilder);
				}
			}

			if(!iudInformation.getString("iudCount").equals("")){
				iudInformation = RetrieveIUDFollowupInfo.getIUDFollowup(iudInfo, iudInformation, dynamicQueryBuilder);
			}

			iudInformation = ClientInfoUtil.getRegNumber(iudInfo, iudInformation);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return iudInformation;
	}
}