package org.sci.rhis.db.pncchild;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author armaan.islam
 * @created November, 2015
 */

public class DeletePNCVisitChildInfo {

	public static boolean deletePNCVisitChild(JSONObject PNCChildInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
							(PNCChildInfo,"serviceId"), "PNCCHILD")));

			/*StockDistributionRequest.deleteDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("PNCCHILD_pnctreatment"),
					PNCChildInfo, dbOp, dbObject, dynamicQueryBuilder);*/


			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
