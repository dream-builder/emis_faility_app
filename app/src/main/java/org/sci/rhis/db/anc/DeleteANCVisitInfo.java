package org.sci.rhis.db.anc;

import java.sql.ResultSet;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.HandleStockDistribution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author armaan.islam
 * @since November, 2015
 */
public class DeleteANCVisitInfo {
	public static boolean deleteANCVisit(JSONObject ANCInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			//String returningSql = " RETURNING " + dynamicQueryBuilder.getColumn("", "ANC_anctreatment");
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
							(ANCInfo,"serviceId"), "ANC")));

			/*StockDistributionRequest.deleteDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("ANC_anctreatment"),
					ANCInfo, dbOp, dbObject, dynamicQueryBuilder);*/
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}