package org.sci.rhis.db.anc;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.HandleStockDistribution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since June, 2015
 */
public class InsertANCVisitInfo {

	public static boolean createANCVisit(JSONObject ANCInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(ANCInfo,"serviceId"), "ANC")));

			//StockDistributionRequest.insertDistributionInfoHandler(rs.next(), ANCInfo, dbOp, dbObject, dynamicQueryBuilder);

			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}