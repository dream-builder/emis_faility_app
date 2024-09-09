package org.sci.rhis.db.dbhelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

public class PregInfo {

	public static JSONObject retrievePregInfo(JSONObject clientInformation) throws JSONException{

		boolean status = false;
		SQLiteDatabase db = DatabaseWrapper.getDatabase();
		clientInformation.put("highRiskPreg", "No");
		clientInformation.put("outcomeDate", "");
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();
		JsonHandler result = new JsonHandler();

		try{
			String sql = "SELECT \"pw\".*, "
					+ dynamicQueryBuilder.getColumn("e","PREGWOMEN_ELCO_marriagedate") + " AS " + dynamicQueryBuilder.getColumn("PREGWOMEN_ELCO_marriagedate") + ", "
					+ dynamicQueryBuilder.getColumn("e","PREGWOMEN_ELCO_boy") + " AS " + dynamicQueryBuilder.getColumn("PREGWOMEN_ELCO_boy") + ", "
					+ dynamicQueryBuilder.getColumn("e","PREGWOMEN_ELCO_girl") + " AS " + dynamicQueryBuilder.getColumn("PREGWOMEN_ELCO_girl") + ", "
					+ "(SELECT COUNT(*) AS cnt FROM " + dynamicQueryBuilder.getTable("DELIVERY") + " WHERE "
					+ dynamicQueryBuilder.getPartialCondition("table","DELIVERY_healthid","pw","PREGWOMEN_healthId","=") + " AND "
					+ dynamicQueryBuilder.getPartialCondition("table","DELIVERY_pregno","pw","PREGWOMEN_pregNo","=") + ") AS cnt, "
					+ "(SELECT " + dynamicQueryBuilder.getColumn("","DELIVERY_dDate")
					+ " FROM " + dynamicQueryBuilder.getTable("DELIVERY")
					+ " WHERE " + dynamicQueryBuilder.getPartialCondition("table","DELIVERY_healthid","pw","PREGWOMEN_healthId","=") + " AND "
					+ dynamicQueryBuilder.getPartialCondition("table","DELIVERY_pregno","pw","PREGWOMEN_pregNo","=") + ") AS "
					+ dynamicQueryBuilder.getColumn("DELIVERY_dDate") + ", "
					+ "(SELECT " + dynamicQueryBuilder.getColumn("","DELIVERY_abortion")
					+ " FROM " + dynamicQueryBuilder.getTable("DELIVERY")
					+ " WHERE " + dynamicQueryBuilder.getPartialCondition("table","DELIVERY_healthid","pw","PREGWOMEN_healthId","=") + " AND "
					+ dynamicQueryBuilder.getPartialCondition("table","DELIVERY_pregno","pw","PREGWOMEN_pregNo","=") + ") AS "
					+ dynamicQueryBuilder.getColumn("DELIVERY_abortion") + ", "
					+ "(SELECT " + dynamicQueryBuilder.getColumn("","CM_mobileNo")
					+ " FROM " + dynamicQueryBuilder.getTable("CM")
					+ " WHERE " + dynamicQueryBuilder.getPartialCondition("table","CM_generatedid","pw","PREGWOMEN_healthId","=") + ") AS \"mobileNoCM\" "
					+ "FROM (SELECT * FROM " + dynamicQueryBuilder.getTable("PREGWOMEN") + " WHERE "
					+ dynamicQueryBuilder.getColumn("table", "PREGWOMEN_healthId",new String[]{clientInformation.getString("cHealthID")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table","PREGWOMEN_pregNo") + " IN (SELECT MAX("
					+ dynamicQueryBuilder.getColumn("table","PREGWOMEN_pregNo") + ") "
					+ "FROM " + dynamicQueryBuilder.getTable("PREGWOMEN") + " WHERE "
					+ dynamicQueryBuilder.getColumn("table", "PREGWOMEN_healthId",new String[]{clientInformation.getString("cHealthID")},"=")
					+ ")) AS \"pw\" "
					+ "LEFT OUTER JOIN (SELECT * FROM " + dynamicQueryBuilder.getTable("PREGWOMEN_ELCO") + " WHERE "
					+ dynamicQueryBuilder.getColumn("table", "PREGWOMEN_ELCO_healthId",new String[]{clientInformation.getString("cHealthID")},"=")
					+ ") AS \"e\" ON "
					+ dynamicQueryBuilder.getPartialCondition("pw", "PREGWOMEN_healthId","e","PREGWOMEN_ELCO_healthId","=");

			SimpleCursor rs =new SimpleCursor(db.rawQuery(sql,null));

			int count = rs.count();
            System.out.println(count);

			if(rs.next()){
				status = true;

				clientInformation = result.getResponse(rs, clientInformation, "PREGWOMENINFO", 1);

				if(clientInformation.getString("cMobileNo").equals("")){
					clientInformation.put("cMobileNo",rs.getObject("mobileNoCM")==null?"":rs.getString("mobileNoCM"));
				}


				if(rs.getInt("cnt")==1){
					clientInformation.put("hasDeliveryInformation", "Yes");
					clientInformation.put("outcomeDate", result.getResultSetValue(rs, dynamicQueryBuilder.getColumn("DELIVERY_dDate")));
					clientInformation.put("hasAbortionInformation",rs.getObject(dynamicQueryBuilder.getColumn("DELIVERY_abortion"))==null
							?"No":rs.getString(dynamicQueryBuilder.getColumn("DELIVERY_abortion")).equals("1")?"Yes":"No");
				}
				else{
					clientInformation.put("hasDeliveryInformation", "No");
					clientInformation.put("hasAbortionInformation", "No");
					clientInformation.put("outcomeDate", "");
				}
				if(rs.getInt(dynamicQueryBuilder.getColumn("PREGWOMEN_pregNo")) >=1 ){
					clientInformation.put("cNewMCHClient","false");
				}
				else{
					clientInformation.put("cNewMCHClient","true");
				}

				/*
				 * checking the difference between previous delivery date and current LMP
				 * if this difference is less than 2 years then it should flag this pregnancy as high risk
				 */
				if(status){
					if(clientInformation.getInt("cPregNo")>1 && clientInformation.get("hasDeliveryInformation").equals("Yes")){
						sql = "SELECT " + dynamicQueryBuilder.getColumn("table","DELIVERY_dDate")
								+ " FROM " + dynamicQueryBuilder.getTable("DELIVERY")
								+ " WHERE " + dynamicQueryBuilder.getColumn("table", "DELIVERY_healthid",new String[]{clientInformation.getString("cHealthID")},"=")
								+ " AND " + dynamicQueryBuilder.getColumn("table", "DELIVERY_pregno",new String[]{String.valueOf(clientInformation.getInt("cPregNo") - 1)},"=");
						rs = new SimpleCursor(db.rawQuery(sql,null));
						CustomSimpleDateFormat formatter = new CustomSimpleDateFormat("yyyy-MM-dd");

						if(rs.next()){
							clientInformation.put("hasDeliveryInformation", "Yes");
							if(clientInformation.getString("outcomeDate").equals("")){
								clientInformation.put("outcomeDate", result.getResultSetValue(rs, dynamicQueryBuilder.getColumn("DELIVERY_dDate")));
							}

							long days = Utilities.getDateDiff(rs.getDate(dynamicQueryBuilder.getColumn("DELIVERY_dDate")),
									formatter.parse(clientInformation.getString("cLMP")), TimeUnit.DAYS);
							if(days < (365*2)){
								clientInformation.put("highRiskPreg", "Yes");
							}
							else{
								clientInformation.put("highRiskPreg", "No");
							}
						}
					}
				}

				if(!rs.isClosed()){
					rs.close();
				}
			}
			else{
				clientInformation.put("cNewMCHClient","true");
				clientInformation.put("hasDeliveryInformation", "No");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return clientInformation;
	}
}