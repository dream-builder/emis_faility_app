package org.sci.rhis.db.womaninjectable;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.fpcommon.FPOp;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since January,2016
 */

public class DeleteInjectableVisitInfo {

	public static boolean deleteInjectableVisit(JSONObject injectableInfo, QueryBuilder dynamicQueryBuilder) {
		try{
			int doseId = CommonQueryExecution.generateServiceID(injectableInfo.getString("healthId"),
					dynamicQueryBuilder.getTable("WOMANINJECTABLE"));

			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyMaxField
							(injectableInfo,"doseId"), "WOMANINJECTABLE")));

			injectableInfo.put("serviceId",doseId);
			FPOp.deleteFPExamination(injectableInfo,  dynamicQueryBuilder,
					dynamicQueryBuilder.getColumn("FPEXAMINATION_WOMANINJECTABLE_treatment"));

			injectableInfo.put("isNewClient", 2);
			injectableInfo.put("currentMethod", "");
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(
					new JsonHandler().addJsonKeyValueEdit(injectableInfo, "FPSTATUS")));
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
