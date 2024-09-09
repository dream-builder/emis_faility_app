package org.sci.rhis.db.anc;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since February, 2017
 */
public class UpdateANCVisitInfo {
	
	static int status;

	public static boolean updateANCVisit(JSONObject ANCInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			//String returningSql = " RETURNING " + dynamicQueryBuilder.getColumn("", "ANC_anctreatment");

			DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(ANCInfo, "ANC")));

				/*StockDistributionRequest.updateDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("ANC_anctreatment"),
						ANCInfo, dbOp, dbObject, dynamicQueryBuilder);*/
				return true;

		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}