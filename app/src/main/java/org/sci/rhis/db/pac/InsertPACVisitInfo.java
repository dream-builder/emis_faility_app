package org.sci.rhis.db.pac;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

/**
 * @author sabah.mugab
 * @since October, 2015
 */

public class InsertPACVisitInfo {

	public static boolean createPACVisit(JSONObject PACInfo, QueryBuilder dynamicQueryBuilder) {
		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(PACInfo,"serviceId"), "PAC")));
			/*StockDistributionRequest.insertDistributionInfoHandler(rs.next(), PACInfo, dbOp, dbObject, dynamicQueryBuilder);*/

			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
