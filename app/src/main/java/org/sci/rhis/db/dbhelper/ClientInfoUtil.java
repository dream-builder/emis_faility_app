package org.sci.rhis.db.dbhelper;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since May, 2016
 */
public class ClientInfoUtil {

	static String sql = "";
	static boolean status = false;

	public static boolean updateClientMobileNo(JSONObject clientInformation){
		status = false;
		SQLiteDatabase db = DatabaseWrapper.getDatabase();
		try{
			db.execSQL(new QueryBuilder().getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(clientInformation, "CM")));
			status=true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return status;
	}

	public static boolean updateClientMobileNoForUnderscore(JSONObject clientInformation){
		status = false;
		SQLiteDatabase db = DatabaseWrapper.getDatabase();
		try{

			sql = "UPDATE \"clientMap\" SET \"mobileNo\"= " + (clientInformation.get("mobileNo").equals("")?null:clientInformation.get("mobileNo"))
					+ " WHERE \"clientMap\".\"generatedId\" = " + clientInformation.get("healthId");
			db.execSQL(sql);
			status = true;
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		return status;
	}

	public static void returnRegNumber(JSONObject clientInfo, JSONObject clientInformation){
		try{
			clientInformation.put("False","");
			clientInformation.put("cHealthID",clientInfo.get("healthId"));
			RetrieveRegNo.pullReg(clientInfo,clientInformation);

			clientInformation.put("False",null);
			clientInformation.put("cHealthID",null);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static JSONObject getRegNumber(JSONObject clientInfo, JSONObject clientInformation){
		try{
			clientInformation.put("False","");
			clientInformation.put("cHealthID",clientInfo.get("healthId"));
			clientInformation = RetrieveRegNo.pullReg(clientInfo,clientInformation);

			clientInformation.put("False",null);
			clientInformation.put("cHealthID",null);
			return clientInformation;
		}
		catch(Exception e){
			e.printStackTrace();
			return clientInformation;
		}
	}

	public String getSpouseSQL(QueryBuilder dynamicQueryBuilder){

		String sql = "";

		try{
			sql = "(SELECT (CASE WHEN (SELECT (CASE WHEN " + dynamicQueryBuilder.getColumn("elco", "ELCO_husbandname",new String[]{},"isnull")
					+ " THEN 'NULL'"
					+ " WHEN " + dynamicQueryBuilder.getColumn("elco", "ELCO_husbandname",new String[]{""},"=")
					+ " THEN 'NULL' ELSE 'NOT NULL' END)"
					+ " FROM " + dynamicQueryBuilder.getTable("ELCO")
					+ " AS elco WHERE " + dynamicQueryBuilder.getColumn("elco", "ELCO_healthid") + " IN "
					+ "(SELECT " + dynamicQueryBuilder.getColumn("cm", "CM_generatedid") + " FROM " + dynamicQueryBuilder.getTable("CM")
					+ " AS cm WHERE " + dynamicQueryBuilder.getColumn("cm", "CM_healthid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_healthid") + ")) = 'NOT NULL' "
					+ "THEN (SELECT " + dynamicQueryBuilder.getColumn("elco", "ELCO_husbandname") + " FROM " + dynamicQueryBuilder.getTable("ELCO")
					+ " AS elco WHERE " + dynamicQueryBuilder.getColumn("elco", "ELCO_healthid") + " IN "
					+ "(SELECT " + dynamicQueryBuilder.getColumn("cm", "CM_generatedid") + " FROM " + dynamicQueryBuilder.getTable("CM")
					+ " AS cm WHERE " + dynamicQueryBuilder.getColumn("cm", "CM_healthid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_healthid") + "))"
					+ " ELSE (SELECT " + dynamicQueryBuilder.getColumn("mem", "MEMBER_nameeng") + " FROM " + dynamicQueryBuilder.getTable("MEMBER")  + " AS mem "
					+ "WHERE " + dynamicQueryBuilder.getColumn("mem", "MEMBER_zillaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_zillaid")
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_upazilaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_upazilaid")
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_unionid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_unionid")
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_mouzaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_mouzaid")
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_villageid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_villageid")
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_householdid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_householdid")
					+ " AND (" //+ dynamicQueryBuilder.getColumn("mem", "MEMBER_exittype",new String[]{""},"=")
					//+ " OR "
					+ dynamicQueryBuilder.getColumn("mem", "MEMBER_exittype",new String[]{"0"},"=")
					+ " OR " + dynamicQueryBuilder.getColumn("mem", "MEMBER_exittype",new String[]{},"isnull") + ")"
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_healthid") + "<>" + dynamicQueryBuilder.getColumn("m", "MEMBER_healthid")
					//+ " AND cast( TRIM( leading '0' from " + dynamicQueryBuilder.getColumn("m", "MEMBER_spousenumber") + ") as text) = cast ( "
					//+ dynamicQueryBuilder.getColumn("mem", "MEMBER_serialnumber") + " AS text)) "
					+ " AND " + dynamicQueryBuilder.getPartialCondition("m", "MEMBER_spousenumber","mem","MEMBER_serialnumber","=")
					+ ") END)) AS \"husbandName\" ";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return sql;
	}


	public String getSpouseSQL(int option, QueryBuilder dynamicQueryBuilder){

		String sql = "";

		try{
			sql = "(CASE WHEN " + dynamicQueryBuilder.getColumn("e", "ELCO_husbandname",new String[]{},"isnotnull")
					+ " AND " + dynamicQueryBuilder.getColumn("e", "ELCO_husbandname",new String[]{""},"!=")
					+ " THEN " + dynamicQueryBuilder.getColumn("e", "ELCO_husbandname")
					+ " ELSE (SELECT " + dynamicQueryBuilder.getColumn("mem", "MEMBER_nameeng") + " FROM " + dynamicQueryBuilder.getTable("MEMBER")  + " AS mem"
					+ " WHERE " + dynamicQueryBuilder.getColumn("mem", "MEMBER_zillaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_zillaid")
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_upazilaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_upazilaid")
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_unionid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_unionid")
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_mouzaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_mouzaid")
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_villageid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_villageid")
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_householdid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_householdid")
					+ " AND (" + dynamicQueryBuilder.getColumn("mem", "MEMBER_exittype",new String[]{"0"},"=")
					+ " OR " + dynamicQueryBuilder.getColumn("mem", "MEMBER_exittype",new String[]{},"isnull") + ")"
					+ " AND " + dynamicQueryBuilder.getColumn("mem", "MEMBER_healthid") + "<>" + dynamicQueryBuilder.getColumn("m", "MEMBER_healthid")
					+ " AND " + dynamicQueryBuilder.getPartialCondition("m", "MEMBER_spousenumber","mem","MEMBER_serialnumber","=")
					+ ") END) AS \"husbandName\" ";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return sql;
	}

	public String getFatherSQL(QueryBuilder dynamicQueryBuilder){

		String sql = "";

		try{
			sql = "(CASE WHEN (" + dynamicQueryBuilder.getColumn("m", "MEMBER_fathername",new String[]{},"isnotnull")
					+ " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_fathername",new String[]{""},"!=")
					+ ") THEN " + dynamicQueryBuilder.getColumn("m", "MEMBER_fathername")
					+ " ELSE (SELECT " + dynamicQueryBuilder.getColumn("", "MEMBER_nameeng") + " FROM " + dynamicQueryBuilder.getTable("MEMBER")
					+ " WHERE " + dynamicQueryBuilder.getColumn("", "MEMBER_zillaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_zillaid")
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_upazilaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_upazilaid")
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_unionid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_unionid")
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_mouzaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_mouzaid")
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_villageid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_villageid")
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_householdid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_householdid")
					+ " AND (" + dynamicQueryBuilder.getColumn("", "MEMBER_exittype",new String[]{"0"},"=")
					+ " OR " + dynamicQueryBuilder.getColumn("", "MEMBER_exittype",new String[]{},"isnull") + ")"
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_healthid") + "<>" + dynamicQueryBuilder.getColumn("m", "MEMBER_healthid")
					+ " AND " + dynamicQueryBuilder.getPartialCondition("m", "MEMBER_fatherserialnumber","","MEMBER_serialnumber","=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("", "MEMBER_nameeng")
					+ " ASC limit 1) END) "
					+ "AS \"Father\", ";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return sql;
	}

	public String getMotherSQL(QueryBuilder dynamicQueryBuilder){

		String sql = "";

		try{
			sql = "(CASE WHEN (" + dynamicQueryBuilder.getColumn("m", "MEMBER_mothername",new String[]{},"isnotnull")
					+ " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_mothername",new String[]{""},"!=")
					+ ") THEN " + dynamicQueryBuilder.getColumn("m", "MEMBER_mothername")
					+ " ELSE (SELECT " + dynamicQueryBuilder.getColumn("", "MEMBER_nameeng") + " FROM " + dynamicQueryBuilder.getTable("MEMBER")
					+ " WHERE " + dynamicQueryBuilder.getColumn("", "MEMBER_zillaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_zillaid")
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_upazilaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_upazilaid")
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_unionid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_unionid")
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_mouzaid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_mouzaid")
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_villageid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_villageid")
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_householdid") + "=" + dynamicQueryBuilder.getColumn("m", "MEMBER_householdid")
					+ " AND (" + dynamicQueryBuilder.getColumn("", "MEMBER_exittype",new String[]{"0"},"=")
					+ " OR " + dynamicQueryBuilder.getColumn("", "MEMBER_exittype",new String[]{},"isnull") + ")"
					+ " AND " + dynamicQueryBuilder.getColumn("", "MEMBER_healthid") + "<>" + dynamicQueryBuilder.getColumn("m", "MEMBER_healthid")
					+ " AND " + dynamicQueryBuilder.getPartialCondition("m", "MEMBER_motherserialnumber","","MEMBER_serialnumber","=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("", "MEMBER_nameeng")
					+ " ASC limit 1) END) "
					+ "AS \"Mother\", ";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return sql;
	}
}