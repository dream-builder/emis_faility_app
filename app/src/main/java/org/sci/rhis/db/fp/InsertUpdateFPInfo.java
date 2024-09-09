package org.sci.rhis.db.fp;

import org.json.JSONException;
import org.json.JSONObject;

import org.jumpmind.db.sql.SqlException;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since September, 2016
 */
public class InsertUpdateFPInfo {

	public static JSONObject insertUpdateFPInfo(JSONObject fpInfo, JSONObject fpInformation,
												QueryBuilder dynamicQueryBuilder) throws JSONException{

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(fpInfo,""), "IU_FPINFO")));
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(fpInfo,""), "IU_FPINFO")));

				fpInformation.put("fpInfoSuccess","1");
				fpInformation.put("healthId",fpInfo.get("healthId"));

			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(fpInfo,""), "IU_FPINFO_ELCO")));
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery
					(new JsonHandler().addJsonKeyValueEdit(new JsonHandler().addJsonKeyIncrementalField
							(fpInfo,""), "IU_FPINFO_ELCO")));
		} catch (SqlException sqe){
			fpInformation.put("fpInfoSuccess","2");
			fpInformation.put("healthId","");
		} catch(Exception e){
			e.printStackTrace();

		}
		return fpInformation;
	}

	public static void insertUpdateCurrentFP(JSONObject fpInfo, JSONObject fpInformation,
											 QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpsertQuery(fpInfo, "IUC_FP"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}