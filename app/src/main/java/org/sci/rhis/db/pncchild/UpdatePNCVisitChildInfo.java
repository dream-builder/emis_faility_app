package org.sci.rhis.db.pncchild;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * Created by arafat.hasan on 3/1/2017.
 */

public class UpdatePNCVisitChildInfo {
    static boolean status;

    public static boolean updatePNCVisitChild(JSONObject PNCChildInfo, QueryBuilder dynamicQueryBuilder) {

        try{
            CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(
                    new JsonHandler().addJsonKeyValueEdit(PNCChildInfo, "PNCCHILD")));

            /*StockDistributionRequest.updateDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("PNCCHILD_pnctreatment"),
                    PNCChildInfo, dbOp, dbObject, dynamicQueryBuilder);*/

            status = true;
        }
        catch(Exception e){
            status =false;
            e.printStackTrace();
        }
        return status;
    }
}
