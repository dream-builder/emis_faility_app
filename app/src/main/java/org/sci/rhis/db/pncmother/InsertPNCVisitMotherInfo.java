package org.sci.rhis.db.pncmother;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @created July, 2015
 */
public class InsertPNCVisitMotherInfo {

	public static boolean createPNCVisitMother(JSONObject PNCMotherInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(PNCMotherInfo,"serviceId"), "PNCMOTHER")));

			/*StockDistributionRequest.insertDistributionInfoHandler(rs.next(), PNCMotherInfo, dbOp, dbObject, dynamicQueryBuilder);*/
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}