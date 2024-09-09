package org.sci.rhis.db.permanent_method;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.db.dbhelper.HandleStockDistribution;
import org.sci.rhis.db.dbhelper.StockDistributionRequest;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

public class UpdatePMInfo {
    public static JSONObject updatePM(JSONObject pmsInfo, JSONObject pmsInformation, QueryBuilder dynamicQueryBuilder) {

        try{
            CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(
                    new JsonHandler().addJsonKeyValueEdit(pmsInfo, "PMS")));

            pmsInfo.put("pmsUpdateSuccess","1");

            String sql = dynamicQueryBuilder.getSelectQuery(pmsInfo);
            SimpleCursor rs = new SimpleCursor (DatabaseWrapper.getDatabase().rawQuery(sql, null));

            StockDistributionRequest.updateDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("PMS_treatment"),
                    pmsInfo, dynamicQueryBuilder);

            if(!pmsInfo.get("mobileNo").equals("")){
                ClientInfoUtil.updateClientMobileNo(pmsInfo);
            }

            pmsInformation = RetrivePMInfo.getPM(pmsInfo, pmsInformation, dynamicQueryBuilder);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return pmsInformation;
    }
}