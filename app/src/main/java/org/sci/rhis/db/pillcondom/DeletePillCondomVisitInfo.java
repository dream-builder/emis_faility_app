package org.sci.rhis.db.pillcondom;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.fpcommon.FPOp;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since January,2016
 */

public class DeletePillCondomVisitInfo {

	public static boolean deletePillCondomVisit(JSONObject pillCondomInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			int serviceId = CommonQueryExecution.generateServiceID(pillCondomInfo.getString("healthId"),"pillCondomService");

			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
							(pillCondomInfo,"serviceId"), "PILLCONDOM")));

			pillCondomInfo.put("serviceId",serviceId);
			FPOp.deleteFPExamination(pillCondomInfo, dynamicQueryBuilder,
					dynamicQueryBuilder.getColumn("FPEXAMINATION_PILLCONDOM_treatment"));

			pillCondomInfo.put("isNewClient", 2);
			pillCondomInfo.put("currentMethod", "");
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(
					new JsonHandler().addJsonKeyValueEdit(pillCondomInfo, "FPSTATUS")));

			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

}
