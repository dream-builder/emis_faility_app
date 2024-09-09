package org.sci.rhis.db.pac;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

import java.sql.ResultSet;

/**
 * @author sabah.mugab
 * @since December,2015
 */
public class DeletePACVisitInfo {

	public static boolean deletePACVisit(JSONObject PACInfo, QueryBuilder dynamicQueryBuilder) {
		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
							(PACInfo,"serviceId"), "PAC")));

			/*StockDistributionRequest.deleteDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("PAC_treatment"),
					PACInfo, dbOp, dbObject, dynamicQueryBuilder);*/

			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}