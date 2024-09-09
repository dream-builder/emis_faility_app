package org.sci.rhis.db.iud.followup;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * Created by arafat.hasan on 3/1/2017.
 */

public class UpdateIUDFollowupInfo {
    public static void updateIUDFollowup(JSONObject iudInfo, JSONObject iudInformation, QueryBuilder dynamicQueryBuilder) {

        try{
            DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(iudInfo, "IUDFOLLOWUP")));
            boolean status = true;
            if(status){
                iudInformation.put("iudCount", iudInfo.get("iudCount"));
				/*iudInfo.put("distributionId", rs.getString("treatment"));
				if(!iudInfo.getString("distributionId").equals("")){
					HandleStockDistribution.deleteDistributionInfo(iudInfo, dbOp, dbObject);
				}*/
            }
            else{
                iudInformation.put("iudCount", "");
            }
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
