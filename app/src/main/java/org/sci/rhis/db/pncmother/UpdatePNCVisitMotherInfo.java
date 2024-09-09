package org.sci.rhis.db.pncmother;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * Created by arafat.hasan on 3/1/2017.
 */

public class UpdatePNCVisitMotherInfo {

    public static boolean updatePNCVisitMother(JSONObject PNCVisitMother, QueryBuilder dynamicQueryBuilder) {

        try{
            CommonQueryExecution.executeQuery((dynamicQueryBuilder.getUpdateQuery(
                    new JsonHandler().addJsonKeyValueEdit(PNCVisitMother, "PNCMOTHER"))));

            /*StockDistributionRequest.updateDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("PNCMOTHER_pnctreatment"),
                    PNCVisitMother, dbOp, dbObject, dynamicQueryBuilder);*/

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
