package org.sci.rhis.db.permanent_method;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.HandleStockDistribution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.db.dbhelper.StockDistributionRequest;
import org.sci.rhis.db.fpcommon.FPStatus;
import org.sci.rhis.model.GeneralPerson;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

public class InsertPMInfo {

    public static JSONObject createPMS(JSONObject pmsInfo, JSONObject pmsInformation, QueryBuilder dynamicQueryBuilder) {

        try{
            CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
                    (new JsonHandler().addJsonKeyValueEdit
                            (new JsonHandler().addJsonKeyIncrementalField
                                    (pmsInfo,"pmsCount"), "PMS")));
            int pmsCount = CommonQueryExecution.generateServiceID(pmsInfo.getString("healthId"),"permanent_method_service");
            pmsInfo.put("pmsInsertSuccess","1");
            pmsInfo.put("pmsCount",pmsCount);

            pmsInfo.put("isNewClient", 1);
            pmsInfo.put("currentMethod", pmsInfo.getString(Constants.KEY_GENDER).equals("2")?997:998);
            CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(pmsInfo, "FPSTATUS")));

            StockDistributionRequest.insertDistributionInfoHandler(true, pmsInfo, dynamicQueryBuilder);


            if(!pmsInfo.get("mobileNo").equals("")){
                ClientInfoUtil.updateClientMobileNo(pmsInfo);
            }

            //inserting a row in fp history
            if(!pmsInfo.getString("currentMethod").equals("")){
                pmsInfo.put("fpCommonCode", ConstantMaps.FP_METHOD_MAPPING_FOR_HISTORY
                        .get(Integer.valueOf(pmsInfo.getString("currentMethod"))));
                CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(
                        new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
                                (pmsInfo,""), "FP_HISTORY")));
            }

            pmsInformation = RetrivePMInfo.getPM(pmsInfo, pmsInformation, dynamicQueryBuilder);

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return pmsInformation;
    }

}
