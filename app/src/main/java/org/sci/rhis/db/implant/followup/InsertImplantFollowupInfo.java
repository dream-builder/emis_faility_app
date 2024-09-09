package org.sci.rhis.db.implant.followup;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class InsertImplantFollowupInfo {

	public static boolean createImplantFollowup(JSONObject implantInfo, JSONObject implantInformation,QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit
							(new JsonHandler().addJsonKeyIncrementalField
									(implantInfo,"serviceId"), "IMPLANTFOLLOWUP")));

			/*implantInfo.put("implantFollowupInsertSuccess","1");
			implantInfo.put("healthId",rs.getString(dynamicQueryBuilder.getColumn("IMPLANTFOLLOWUP_healthId")));
			implantInfo.put("implantCount",rs.getString(dynamicQueryBuilder.getColumn("IMPLANTFOLLOWUP_implantCount")));
			implantInfo.put("serviceId",rs.getString(dynamicQueryBuilder.getColumn("IMPLANTFOLLOWUP_serviceId")));
			implantInfo.put("treatment",rs.getString(dynamicQueryBuilder.getColumn("IMPLANTFOLLOWUP_treatment")));*/

			//StockDistributionRequest.insertDistributionInfoHandler(true, implantInfo, dbOp, dbObject, dynamicQueryBuilder);


			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}