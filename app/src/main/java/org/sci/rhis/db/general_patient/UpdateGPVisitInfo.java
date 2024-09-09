package org.sci.rhis.db.general_patient;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * Created by arafat.hasan on 3/1/2017.
 */

public class UpdateGPVisitInfo {

    public static boolean updateGPVisit(JSONObject GPInfo, QueryBuilder dynamicQueryBuilder) {

        try{
            CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery
                    (new JsonHandler().addJsonKeyValueEdit(GPInfo, "GP")));

            /*StockDistributionRequest.updateDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("GP_treatment"),
                    GPInfo, dbOp, dbObject, dynamicQueryBuilder);

            if(!rs.isClosed()){
                rs.close();
            }*/
			/*
			if(status && !GPInfo.getString("treatment").equals("") && GPInfo.getString("treatment").contains("_")){
				HandleStockDistribution.updateDistributionInfo(GPInfo, dbOp, dbObject);
			}*/
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
