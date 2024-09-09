package org.sci.rhis.db.permanent_method.followup;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.db.dbhelper.StockDistributionRequest;
import org.sci.rhis.utilities.JsonHandler;

public class UpdatePMSFollowupInfo {

    public static void updatePMSFollowup(JSONObject pmsInfo, JSONObject pmsInformation, QueryBuilder dynamicQueryBuilder) {

        try{
            DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(pmsInfo, "PMSFOLLOWUP")));
            boolean status = true;
            if(status){
                pmsInformation.put("pmsCount", pmsInfo.get("pmsCount"));

                String sql = dynamicQueryBuilder.getSelectQuery(pmsInfo);
                SimpleCursor rs = new SimpleCursor (DatabaseWrapper.getDatabase().rawQuery(sql, null));

                pmsInfo.put("distributionId", rs.getString("treatment"));
                if(!pmsInfo.getString("distributionId").equals("")){
                    //TODO: get this line fixed after knowing what it does
//                    pmsInfo.updateDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("PMSFOLLOWUP_treatment"),
//                            pmsInfo, dynamicQueryBuilder);
                }
            }
            else{
                pmsInformation.put("pmsCount", "");
            }
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
}







