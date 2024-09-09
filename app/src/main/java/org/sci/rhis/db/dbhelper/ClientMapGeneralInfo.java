package org.sci.rhis.db.dbhelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import org.json.JSONObject;
import org.sci.rhis.model.LocationHolder;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * @author sabah.mugab
 * @created August, 2015
 */
public class ClientMapGeneralInfo {

	public final static int yearInDays=365;
	public static JSONObject retrieveDetailInfo(JSONObject searchClient, QueryBuilder dynamicQueryBuilder) {

		JSONObject clientInformation = new JSONObject();
		JsonHandler result = new JsonHandler();

		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		try{
			String sql = "SELECT " + dynamicQueryBuilder.getTable("CM") + ".*, "
					+ dynamicQueryBuilder.getTable("NRCEXTENSION") + ".* "
					+ "FROM " + dynamicQueryBuilder.getTable("CM") + " "
					+ "LEFT JOIN " + dynamicQueryBuilder.getTable("NRCEXTENSION") + " ON "
					+ dynamicQueryBuilder.getPartialCondition("table", "CM_generatedid","table","NRCEXTENSION_generatedId","=") + " "
					+ "WHERE " + searchClient.get("colName") + " = ?";

			System.out.println("sql: "+sql);

			String conditionVal = searchClient.getString("sStr");

			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql,searchClient.getInt("sOpt")==2? new String[]{conditionVal,conditionVal}:new String[]{conditionVal}));

			if(rs.next()){
				clientInformation.put("False","");
				sql = "SELECT " + dynamicQueryBuilder.getColumn("table", "DEATH_healthId") + " "
						+ "FROM " + dynamicQueryBuilder.getTable("DEATH") + " "
						+ "WHERE " + dynamicQueryBuilder.getColumn("table", "DEATH_healthId",new String[]{result.getResultSetValue(rs,dynamicQueryBuilder.getColumn("CM_generatedid"))},"=")
						+ " AND " + dynamicQueryBuilder.getColumn("table", "DEATH_pregNo",new String[]{"0"},"=")
						+ " AND " + dynamicQueryBuilder.getColumn("table", "DEATH_childNo",new String[]{"0"},"=");


				SimpleCursor rs1 = new SimpleCursor(db.rawQuery(sql, null));

				if(rs1.next()){
					clientInformation.put("deathStatus", "1");
				}
				else{
					clientInformation.put("deathStatus", "");
				}
				if(!rs1.isClosed()){
					rs1.close();
				}

				if(rs.getLong(dynamicQueryBuilder.getColumn("CM_healthid"))==rs.getLong(dynamicQueryBuilder.getColumn("CM_generatedid"))){
					if(searchClient.getInt("sOpt")!=1 && (searchClient.getInt("sOpt")==5 || rs.getObject(dynamicQueryBuilder.getColumn("NRCEXTENSION_generatedId"))!=null)){

						clientInformation = result.getResponse(rs, clientInformation, "CLIENTINFO",1);
						clientInformation.put("is_registered", "No");
						clientInformation.put("cElcoNo", "");

						if(!clientInformation.getString("cDob").equals("")){
							long days = Utilities.getDateDiff(rs.getDate(dynamicQueryBuilder.getColumn("CM_dob")), new Date(), TimeUnit.DAYS);
							clientInformation.put("cAge", String.valueOf(days/365));
						}

						clientInformation = ClientGeneralInfo.getClientGeoInfo(clientInformation,dynamicQueryBuilder);
					}
					else{
						clientInformation.put("False", "false");
					}
				}
				else{
					if(searchClient.getInt("sOpt")==1){
						searchClient.put("colName",dynamicQueryBuilder.getColumn("m", "MEMBER_healthid"));
						searchClient.put("sStr",rs.getLong(dynamicQueryBuilder.getColumn("CM_healthid")));
						clientInformation = ClientGeneralInfo.retrieveInfo(searchClient, dynamicQueryBuilder);
					}
					else{
						clientInformation.put("False", "false");
					}
				}
			}
			else{
				clientInformation.put("False", "false");
			}
			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return clientInformation;
	}
}