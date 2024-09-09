package org.sci.rhis.db.dbhelper;

import android.database.sqlite.SQLiteDatabase;

import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;


/**
 * @author sabah.mugab
 * @created June, 2015
 */

public class CreatePreg {

	public static JSONObject insertPregInfo(JSONObject pregInfo) throws JSONException{

		QueryBuilder dynamicQueryBuilder = new QueryBuilder();
		JsonHandler result = new JsonHandler();
		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		JSONObject pInfo = new JSONObject();
		pInfo.put("pregNo", "");
		pInfo.put("highRiskPreg", "No");
		pInfo.put("False", "");
		pInfo.put("responseType", "insert");
		pInfo.put("hasDeliveryInformation", "No");
		pInfo.put("hasAbortionInformation", "No");
		int statusLMP = 0;

		try{
			String sql = "";
			pInfo.put("tempLMP", pregInfo.getString("lmp"));
			pInfo.put("statusLMP", statusLMP);
			pregInfo.put("tempLMP", pregInfo.getString("lmp"));
			pregInfo.put("statusLMP", statusLMP);

			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(result.addJsonKeyValueEdit(result.addJsonKeyIncrementalField
							(pregInfo,"pregNo"), "PREGWOMEN")));

			sql = "SELECT COUNT(" + dynamicQueryBuilder.getColumn("","PREGWOMEN_pregNo")+") AS \"" + dynamicQueryBuilder.getColumn("PREGWOMEN_pregNo") + "\" From "
					+ dynamicQueryBuilder.getTable("PREGWOMEN")
					+ " WHERE " + dynamicQueryBuilder.getColumn("","PREGWOMEN_healthId",new String[]{pregInfo.getString("healthId")},"=");
			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql,null));

			if(rs.next()){
				pInfo.put("pregNo", result.getResultSetValue(rs, dynamicQueryBuilder.getColumn("PREGWOMEN_pregNo")));
			}
			if(pInfo.getInt("pregNo")>1){
				sql = "SELECT " + dynamicQueryBuilder.getColumn("table","DELIVERY_dDate")
						+ " FROM " + dynamicQueryBuilder.getTable("DELIVERY")
						+ " WHERE " + dynamicQueryBuilder.getColumn("table", "DELIVERY_healthid",new String[]{pregInfo.getString("healthId")},"=")
						+ " AND " + dynamicQueryBuilder.getColumn("table", "DELIVERY_pregno",new String[]{String.valueOf(pInfo.getInt("pregNo") - 1)},"=");

				rs = new SimpleCursor(db.rawQuery(sql,null));
				CustomSimpleDateFormat formatter = new CustomSimpleDateFormat("yyyy-MM-dd");

				if(rs.next()){
					long days = Utilities.getDateDiff(rs.getDate(dynamicQueryBuilder.getColumn("DELIVERY_dDate")), formatter.parse(pregInfo.getString("lmp")), TimeUnit.DAYS);
					if(days < (365*2)){
						pInfo.put("highRiskPreg", "Yes");
					}
				}
			}
			rs.close();

			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery
					(result.addJsonKeyValueEdit(pregInfo, "PREGWOMEN_ELCO")));
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(result.addJsonKeyValueEdit(pregInfo, "PREGWOMEN_ELCO")));
			ClientInfoUtil.updateClientMobileNo(pregInfo);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return pInfo;
	}
}