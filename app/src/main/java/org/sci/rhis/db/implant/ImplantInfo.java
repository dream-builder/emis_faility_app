package org.sci.rhis.db.implant;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CreateRegNo;
import org.sci.rhis.db.dbhelper.JSONKeyMapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.implant.followup.DeleteImplantFollowupInfo;
import org.sci.rhis.db.implant.followup.InsertImplantFollowupInfo;
import org.sci.rhis.db.implant.followup.RetrieveImplantFollowupInfo;
import org.sci.rhis.db.implant.followup.UpdateImplantFollowupInfo;
import org.sci.rhis.utilities.JsonHandler;

import java.util.Calendar;

/**
 * @author sabah.mugab
 * @since February, 2016
 */
public class ImplantInfo {

	final static int implant = 6;
	final static int IMPLANTSERVICTYPE = 11;
	final static int IMPLANTFOLLOWUPSERVICTYPE = 12;

	public static JSONObject getDetailInfo(JSONObject implantInfo) {

		JSONObject implantInformation = new JSONObject();
		
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();
		

		try{
			implantInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(implantInfo, "IMPLANT"), IMPLANTSERVICTYPE);
			implantInfo.put("serviceCategory", implant);
			
			implantInformation = new JSONObject();

			if(implantInfo.get("implantLoad").equals("insert")){
				implantInformation = InsertImplantInfo.createImplant(implantInfo, implantInformation,
						dynamicQueryBuilder);
				if(implantInformation.getString("implantInsertSuccess").equals("1")){
					CreateRegNo.pushReg(implantInfo, implantInformation);
				}
			}
			else if(implantInfo.get("implantLoad").equals("update")){
				implantInformation = UpdateImplantInfo.updateImplant(implantInfo, implantInformation, dynamicQueryBuilder);
			}
			else if(implantInfo.get("implantLoad").equals("retrieve")){
				implantInformation = RetrieveImplantInfo.getImplant(implantInfo, implantInformation, dynamicQueryBuilder);System.out.println(implantInformation);
			}
			else if(implantInfo.get("implantLoad").equals("")){
				implantInformation.put("implantCount", implantInfo.getString("implantCount"));
				implantInfo.put("serviceType", IMPLANTFOLLOWUPSERVICTYPE);

				if(implantInfo.get("implantFollowupLoad").equals("insert")){
					InsertImplantFollowupInfo.createImplantFollowup(implantInfo, implantInformation, dynamicQueryBuilder);
				}
				else if(implantInfo.get("implantFollowupLoad").equals("update")){
					UpdateImplantFollowupInfo.updateImplantFollowup(implantInfo, implantInformation, dynamicQueryBuilder);
				}
				else if(implantInfo.get("implantFollowupLoad").equals("delete")){
					DeleteImplantFollowupInfo.deleteImplantFollowup(implantInfo, implantInformation, dynamicQueryBuilder);
				}
			}

			if(!implantInformation.getString("implantCount").equals("")){
				implantInformation = RetrieveImplantFollowupInfo.getImplantFollowup(implantInfo, implantInformation, dynamicQueryBuilder);
			}

			implantInformation = ClientInfoUtil.getRegNumber(implantInfo, implantInformation);
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return implantInformation;
	}
}