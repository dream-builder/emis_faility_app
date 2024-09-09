package org.sci.rhis.db.dbhelper;

import org.json.JSONObject;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.sci.rhis.model.LocationHolder;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author sabah.mugab
 * @created June, 2015
 */
public class ClientGeneralInfo {

	public final static int yearInDays=365;

	public static JSONObject retrieveInfo(JSONObject searchClient, QueryBuilder dynamicQueryBuilder) {

		ClientInfoUtil clientHelper = new ClientInfoUtil();
		JSONObject clientInformation = new JSONObject();
		JsonHandler result = new JsonHandler();
		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		try{
			String sql = "SELECT " + dynamicQueryBuilder.getColumn("m", "MEMBER_healthid") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_nameeng") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_age") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_dob") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_gender") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_mobilenumber") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_householdid") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_zillaid") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_upazilaid") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_unionid") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_mouzaid") + ","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_villageid") + ","
					+ clientHelper.getFatherSQL(dynamicQueryBuilder)
					+ clientHelper.getMotherSQL(dynamicQueryBuilder)
					+ clientHelper.getSpouseSQL(dynamicQueryBuilder)
					+ "FROM " + dynamicQueryBuilder.getTable("MEMBER") + " AS m "
					+ "WHERE ("
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{"0"},"=")
					+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{},"isnull") + ")"
					+ " AND " + searchClient.get("colName") + " = ?";

			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql, new String[]{searchClient.getString("sStr")}));

			if(rs.next()){

				clientInformation = result.getResponse(rs, clientInformation, "MEMBERINFO",1);
				clientInformation.put("divisionId", "");
				clientInformation.put("is_registered", "Yes");

				if(!clientInformation.getString("cSex").equals("2")){
					clientInformation.put("cHusbandName", "");
				}

				if(!clientInformation.getString("cDob").equals("")){
					long days = Utilities.getDateDiff(Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,
							clientInformation.getString("cDob")), new Date(), TimeUnit.DAYS);
					clientInformation.put("cAge", String.valueOf(days/yearInDays));
				}

				clientInformation = getClientGeoInfo(clientInformation,dynamicQueryBuilder);

				sql = "SELECT " + dynamicQueryBuilder.getColumn("cm", "CM_generatedid") + ","
						+ dynamicQueryBuilder.getColumn("cm", "CM_mobileNo") + ","
						+ dynamicQueryBuilder.getColumn("e", "ELCO_elconumber") + ","
						+ dynamicQueryBuilder.getColumn("e", "ELCO_husbandname") + " "
						+ "FROM " + dynamicQueryBuilder.getTable("CM") + " AS cm "
						+ "LEFT JOIN " + dynamicQueryBuilder.getTable("ELCO") + " AS e ON "
						+ dynamicQueryBuilder.getPartialCondition("e", "ELCO_healthid","cm","CM_generatedid","=") + " "
						+ "WHERE " + dynamicQueryBuilder.getColumn("cm", "CM_healthid",new String[]{clientInformation.getString("cVisibleID")},"=");



				rs = new SimpleCursor(db.rawQuery(sql, null));

				if(rs.next()){
					clientInformation = result.getResponse(rs, clientInformation, "MEMBERINFOELCO",1);
					clientInformation.put("cHusbandName", clientInformation.getString("eHusbandName").equals("") ?
							clientInformation.getString("cHusbandName"):clientInformation.getString("eHusbandName"));
					clientInformation.put("cMobileNo", clientInformation.getString("eMobileNo").equals("") ?
							clientInformation.getString("cMobileNo"):clientInformation.getString("eMobileNo"));
				}
				else{
					clientInformation.put("cElcoNo", "");
					clientInformation.put("cHusbandName", "");
				}
				clientInformation.put("False","");
			}
			else{
				clientInformation.put("False", "false");
				if(searchClient.getInt("sOpt")==2){
					searchClient.put("colName",(dynamicQueryBuilder.getColumn("table","CM_mobileNo") + "=? OR " + dynamicQueryBuilder.getColumn("table","NRCEXTENSION_cellNo2")));
					clientInformation = ClientMapGeneralInfo.retrieveDetailInfo(searchClient, dynamicQueryBuilder);
				}
				else if(searchClient.getInt("sOpt")==3){
					searchClient.put("colName",dynamicQueryBuilder.getColumn("table","NRCEXTENSION_nid"));//"\"clientmap_extension\".\"nid\"");
					clientInformation = ClientMapGeneralInfo.retrieveDetailInfo(searchClient, dynamicQueryBuilder);
				}
				else if(searchClient.getInt("sOpt")==4){
					searchClient.put("colName",dynamicQueryBuilder.getColumn("table","NRCEXTENSION_brId"));//"\"clientmap_extension\".\"br_id\"");
					clientInformation = ClientMapGeneralInfo.retrieveDetailInfo(searchClient, dynamicQueryBuilder);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return clientInformation;
	}

	public static JSONObject getClientGeoInfo(JSONObject clientInformation,QueryBuilder dynamicQueryBuilder){

		try{

			JSONObject vill = LocationHolder.getVillageJson();
			JSONObject zillaUpazillaUnion = LocationHolder.getZillaUpazillaUnionJson();

			String zilla    = zillaUpazillaUnion.getJSONObject(clientInformation.getString("zillaId")).getString("nameBangla");
			String upazila  = zillaUpazillaUnion.getJSONObject(clientInformation.getString("zillaId")).getJSONObject("Upazila").getJSONObject(clientInformation.getString("upazilaId")).getString("nameBanglaUpazila");
			String union    = zillaUpazillaUnion.getJSONObject(clientInformation.getString("zillaId")).getJSONObject("Upazila").getJSONObject(clientInformation.getString("upazilaId")).getJSONObject("Union").getJSONObject(clientInformation.getString("unionId")).
					getString("nameBanglaUnion");

			Log.d("SQLITE-DB", String.format("Zilla: %s, upzila:%s, Union: %s", zilla, upazila, union));

			clientInformation.put("cDist", zilla);
			clientInformation.put("cUpz", upazila);
			clientInformation.put("cUnion", union);
			clientInformation.put("cMouza", clientInformation.getString("mouzaId"));

			clientInformation.put("cVill",
					vill.getJSONObject(clientInformation.getString("zillaId")).
							getJSONObject(clientInformation.getString("upazilaId")).
							getJSONObject(clientInformation.getString("unionId")). //sysmmetricds defect  transforms all caps fields ??
							getJSONObject(clientInformation.getString("mouzaId")).
							getString(clientInformation.getString("villageId")));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return clientInformation;
	}
}
