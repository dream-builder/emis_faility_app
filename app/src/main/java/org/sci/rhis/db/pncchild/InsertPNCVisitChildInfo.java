package org.sci.rhis.db.pncchild;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;


/**
 * @author sabah.mugab
 * @created July, 2015
 */
public class InsertPNCVisitChildInfo {
	
	public static boolean createPNCVisitChild(JSONObject PNCChildInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(PNCChildInfo,"serviceId"), "PNCCHILD")));

			//StockDistributionRequest.insertDistributionInfoHandler(rs.next(), PNCChildInfo, dbOp, dbObject, dynamicQueryBuilder);

			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}