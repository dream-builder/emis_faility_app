package org.sci.rhis.db.pncmother;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author armaan.islam
 * @created November,2015
 */
public class DeletePNCVisitMotherInfo {

	public static boolean deletePNCVisitMother(JSONObject PNCMotherInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
							(PNCMotherInfo,"serviceId"), "PNCMOTHER")));

			/*StockDistributionRequest.deleteDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("PNCMOTHER_pnctreatment"),
					PNCMotherInfo, dbOp, dbObject, dynamicQueryBuilder);*/
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
