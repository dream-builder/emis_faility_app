package org.sci.rhis.db.permanent_method.followup;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

public class RetrievePMSFollowupInfo {
    public static JSONObject getPMSFollowup(JSONObject pmsInfo, JSONObject pmsInformation, QueryBuilder dynamicQueryBuilder) {

        try{
            String sql = "SELECT * FROM " + dynamicQueryBuilder.getTable("PMSFOLLOWUP")
                    + " WHERE " + dynamicQueryBuilder.getColumn("table", "PMSFOLLOWUP_healthId",new String[]{pmsInfo.getString("healthId")},"=")
                    + " AND " + dynamicQueryBuilder.getColumn("table", "PMSFOLLOWUP_pmsCount",new String[]{pmsInformation.getString("pmsCount")},"=")
                    + " ORDER BY " + dynamicQueryBuilder.getColumn("table", "PMSFOLLOWUP_serviceId") + " ASC";

            SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));

            JSONObject PMSFollowupVisits = new JSONObject();
            pmsInformation.put("followupCount", 0);
            pmsInfo.put("distributionJson","treatment");

            while(rs.next()){
                PMSFollowupVisits.put(rs.getString(dynamicQueryBuilder.getColumn("PMSFOLLOWUP_serviceId")),
                        new JsonHandler().getServiceDetail(rs, pmsInfo, "PMSFOLLOWUP", dynamicQueryBuilder, 2));

                pmsInformation.put("followupCount", (pmsInformation.getInt("followupCount")+1));
            }
            pmsInformation.put("followup", PMSFollowupVisits);

            if(!rs.isClosed()){
                rs.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return pmsInformation;
    }
}
