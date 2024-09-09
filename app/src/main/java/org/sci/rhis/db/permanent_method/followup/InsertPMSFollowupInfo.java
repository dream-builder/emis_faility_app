package org.sci.rhis.db.permanent_method.followup;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.StockDistributionRequest;
import org.sci.rhis.utilities.JsonHandler;

public class InsertPMSFollowupInfo {
    public static boolean createPMSFollowup(JSONObject pmsInfo, JSONObject pmsInformation,
                                            QueryBuilder dynamicQueryBuilder) {

        try{
            CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
                    (new JsonHandler().addJsonKeyValueEdit
                            (new JsonHandler().addJsonKeyIncrementalField
                                    (pmsInfo,"serviceId"), "PMSFOLLOWUP")));

            pmsInfo.put("pmsFollowupInsertSuccess","1");

            int serviceId = 0;
            Cursor rs =  DatabaseWrapper.getDatabase().rawQuery("SELECT COUNT("
                    + dynamicQueryBuilder.getColumn("PMSFOLLOWUP_serviceId")+") FROM "+dynamicQueryBuilder.getTable("PMSFOLLOWUP") +
                    " WHERE "+ dynamicQueryBuilder.getColumn("table", "PMSFOLLOWUP_healthId",new String[]{pmsInfo.getString("healthId")},"=")
                    + " AND " + dynamicQueryBuilder.getColumn("table", "PMSFOLLOWUP_pmsCount",new String[]{pmsInformation.getString("pmsCount")},"="), null);

            if(rs.moveToFirst()){
                serviceId = rs.getInt(0);
            }

            pmsInfo.put("healthId",pmsInfo.get("healthId"));
            pmsInfo.put("pmsCount",pmsInfo.get("pmsCount"));
            pmsInfo.put("serviceId",serviceId);
            pmsInfo.put("treatment",pmsInfo.get("treatment"));

            StockDistributionRequest.insertDistributionInfoHandler(true, pmsInfo, dynamicQueryBuilder);

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
