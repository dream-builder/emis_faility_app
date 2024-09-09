package org.sci.rhis.db.implant.followup;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * Created by arafat.hasan on 3/1/2017.
 */

public class UpdateImplantFollowupInfo {
    public static void updateImplantFollowup(JSONObject implantInfo, JSONObject implantInformation, QueryBuilder dynamicQueryBuilder) {

        try{
            DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(implantInfo, "ImplantFOLLOWUP")));
            boolean status = true;
            if(status){
                implantInformation.put("implantCount", implantInfo.get("implantCount"));
				/*implantInfo.put("distributionId", rs.getString("treatment"));
				if(!implantInfo.getString("distributionId").equals("")){
					HandleStockDistribution.deleteDistributionInfo(implantInfo, dbOp, dbObject);
				}*/
            }
            else{
                implantInformation.put("implantCount", "");
            }
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
