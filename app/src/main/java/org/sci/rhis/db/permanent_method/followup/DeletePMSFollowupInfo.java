package org.sci.rhis.db.permanent_method.followup;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.db.dbhelper.StockDistributionRequest;
import org.sci.rhis.utilities.JsonHandler;

public class DeletePMSFollowupInfo {
    public static boolean deletePMSFollowup(JSONObject pmsInfo, JSONObject pmsInformation, QueryBuilder dynamicQueryBuilder) {

        try{
            CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
                    (new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
                            (pmsInfo,"serviceId"), "PMSFOLLOWUP")));

            String sql = dynamicQueryBuilder.getSelectQuery(pmsInfo);
            SimpleCursor rs = new SimpleCursor (DatabaseWrapper.getDatabase().rawQuery(sql, null));

            StockDistributionRequest.deleteDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("PMSFOLLOWUP_treatment"),
                    pmsInfo, dynamicQueryBuilder);

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
