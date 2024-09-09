package org.sci.rhis.db.implant.followup;


import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class DeleteImplantFollowupInfo {

	public static boolean deleteImplantFollowup(JSONObject implantInfo, JSONObject implantInformation,
												QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
							(implantInfo,"serviceId"), "IMPLANTFOLLOWUP")));

			/*StockDistributionRequest.deleteDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("IMPLANTFOLLOWUP_treatment"),
					implantInfo, dbOp, dbObject, dynamicQueryBuilder);*/

			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}