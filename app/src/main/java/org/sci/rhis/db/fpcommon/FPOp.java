package org.sci.rhis.db.fpcommon;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * Created by arafat.hasan on 3/13/2018.
 */

public class FPOp {

    public static boolean deleteFPExamination(JSONObject FPInfo,QueryBuilder dynamicQueryBuilder,
                                              String columnName) {

        try{
            CommonQueryExecution.executeQuery( new QueryBuilder().getDeleteQuery
                    (new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
                            (FPInfo,""), "FPEXAMINATIONCOMMON")));
            /*StockDistributionRequest.deleteDistributionInfoHandler(rs, columnName,
                    FPInfo, dbOp, dbObject, dynamicQueryBuilder);*/

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
