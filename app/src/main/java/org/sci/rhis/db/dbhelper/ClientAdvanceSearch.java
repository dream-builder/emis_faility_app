package org.sci.rhis.db.dbhelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author sabah.mugab
 * @since September, 2015
 */
public class ClientAdvanceSearch {

	static JSONObject serviceDetail = new JSONObject();

	public static JSONObject getSearchResult(JSONObject searchClient) throws JSONException{

		ClientInfoUtil clientHelper = new ClientInfoUtil();
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		initializeServiceDetail(dynamicQueryBuilder);
		JSONObject searchList = new JSONObject();
		String sql="", sqlPOP="", sqlNRC="";
		String table = "", columnHealthId="", column ="";
		searchList.put("count", 0);
		searchList.put("options", searchClient.getString("options"));

		if(searchClient.getString("options").equals("4")){
			sql = "SELECT " + dynamicQueryBuilder.getColumn("cm", "CM_healthid") + " AS \"healthId\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_generatedid") + " AS \"generatedId\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_name") + " AS \"cmname\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_husbandname") + " AS \"cmhusbandname\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_age") + " AS \"age\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_dob") + " AS \"dob\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_zillaid") + " AS \"zillaId\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_upazilaid") + " AS \"upazilaId\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_unionid") + " AS \"unionId\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_mouzaid") + " AS \"mouzaId\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_villageid") + " AS \"villageId\","
					+ dynamicQueryBuilder.getColumn("cm", "CM_mobileNo") + " AS \"mobileNo\","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_nameeng") + " AS \"name\","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_age") + " AS \"Age\","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_dob") + " AS \"DOB\","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_mobilenumber") + " AS \"MobileNo1\","
					+ dynamicQueryBuilder.getColumn("e", "ELCO_boy") + " AS \"son\","
					+ dynamicQueryBuilder.getColumn("e", "ELCO_girl") + " AS \"dau\","
					+ dynamicQueryBuilder.getColumn("e", "ELCO_elconumber") + " AS \"elcoNo\","
					+ dynamicQueryBuilder.getColumn("pw", "PREGWOMEN_gravida") + " AS \"gravida\","
					+ dynamicQueryBuilder.getColumn("pw", "PREGWOMEN_lmp") + " AS \"LMP\","
					+ dynamicQueryBuilder.getColumn("pw", "PREGWOMEN_edd") + " AS \"EDD\","
					+ dynamicQueryBuilder.getColumn("pw", "PREGWOMEN_providerId") + " AS \"providerId\","
					//+ dynamicQueryBuilder.getColumn("v", "VILLAGE_name") + " AS \"VILLAGENAME\","
					+ clientHelper.getSpouseSQL(searchClient.getInt("options"), dynamicQueryBuilder)
					+ "FROM " + dynamicQueryBuilder.getTable("PREGWOMEN") + " AS pw "
					+ "LEFT JOIN " + dynamicQueryBuilder.getTable("DELIVERY") + " AS d ON "
					+ dynamicQueryBuilder.getPartialCondition("pw", "PREGWOMEN_healthId","d","DELIVERY_healthid","=") + " AND "
					+ dynamicQueryBuilder.getPartialCondition("pw", "PREGWOMEN_pregNo","d","DELIVERY_pregno","=") + " "
					+ "LEFT JOIN " + dynamicQueryBuilder.getTable("ELCO") + " AS e ON "
					+ dynamicQueryBuilder.getPartialCondition("pw", "PREGWOMEN_healthId","e","ELCO_healthid","=") + " "
					+ "INNER JOIN " + dynamicQueryBuilder.getTable("CM") + " AS cm ON "
					+ dynamicQueryBuilder.getPartialCondition("pw", "PREGWOMEN_healthId","cm","CM_generatedid","=") + " AND "
					+ dynamicQueryBuilder.getColumn("cm", "CM_healthid",new String[]{},"isnotnull") + " AND ";


			if(!searchClient.get("zilla").equals("none") && !searchClient.get("zilla").equals("") && !searchClient.get("zilla").equals(null)){
				sql = sql + dynamicQueryBuilder.getColumn("cm", "CM_zillaid",new String[]{searchClient.getString("zilla").split("_")[0]},"=");
				if(!searchClient.get("upz").equals("none") && !searchClient.get("upz").equals("") && !searchClient.get("upz").equals(null)){
					sql = sql + " AND " + dynamicQueryBuilder.getColumn("cm", "CM_upazilaid",new String[]{searchClient.getString("upz")},"=");
					if(!searchClient.get("union").equals("none") && !searchClient.get("union").equals("") && !searchClient.get("union").equals(null)){
						sql = sql + " AND " + dynamicQueryBuilder.getColumn("cm", "CM_unionid",new String[]{searchClient.getString("union")},"=");
						if(!searchClient.get("villagemouza").equals("none") && !searchClient.get("villagemouza").equals("") && !searchClient.get("villagemouza").equals(null)){
							sql = sql + " AND " + dynamicQueryBuilder.getColumn("cm", "CM_mouzaid",new String[]{searchClient.getString("villagemouza").split("_")[1]},"=")
									+ " AND " + dynamicQueryBuilder.getColumn("cm", "CM_villageid",new String[]{searchClient.getString("villagemouza").split("_")[0]},"=");

							System.out.println("Pregnant Women List: " + sql);
						}
					}
				}
			}

			sql = sql + " LEFT JOIN " + dynamicQueryBuilder.getTable("MEMBER") + " AS m ON "
					+ dynamicQueryBuilder.getPartialCondition("cm", "CM_healthid","m","MEMBER_healthid","=") + " AND "
					+ "(" + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{"0"},"=")
					+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{},"isnull") + ") AND "
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_healthid",new String[]{},"isnotnull") + " "
					+ "WHERE CURRENT_DATE BETWEEN date(" + dynamicQueryBuilder.getColumn("pw", "PREGWOMEN_lmp") + ") AND "
					+ "date(" + dynamicQueryBuilder.getColumn("pw", "PREGWOMEN_edd") + " ,'+14 day') AND "
					+ dynamicQueryBuilder.getColumn("pw", "PREGWOMEN_pregNo") + " IN (SELECT MAX("
					+ dynamicQueryBuilder.getColumn("", "PREGWOMEN_pregNo") + ") FROM " + dynamicQueryBuilder.getTable("PREGWOMEN")
					+ " WHERE " + dynamicQueryBuilder.getPartialCondition("", "PREGWOMEN_healthId","pw","PREGWOMEN_healthId","=") + ") AND "
					+ dynamicQueryBuilder.getColumn("d", "DELIVERY_pregno",new String[]{},"isnull");

			System.out.println("Pregnant Women List: " + sql);
		}
		else{
			//query of part SELECT portion (main table)..........
			if(searchClient.getString("options").equals("3")){
				table = serviceDetail.getJSONObject(searchClient.getString("serviceType")).getString("table");
				columnHealthId = serviceDetail.getJSONObject(searchClient.getString("serviceType")).getString("column_healthid");
				column = serviceDetail.getJSONObject(searchClient.getString("serviceType")).getString("column");
			}

			sqlPOP = "SELECT " + dynamicQueryBuilder.getColumn("m", "MEMBER_healthid") + " AS \"HealthID\","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_nameeng") + " AS \"NameEng\","
					+ dynamicQueryBuilder.getColumn("m", "MEMBER_dob") + " AS \"DOB\",";

			if(searchClient.getString("options").equals("3")){
				sqlPOP = sqlPOP + clientHelper.getFatherSQL(dynamicQueryBuilder)
						+ clientHelper.getSpouseSQL(dynamicQueryBuilder);
			}
			else{

				//query part of getting father's Name and husband's name
				sqlPOP = sqlPOP + "(CASE WHEN (" + dynamicQueryBuilder.getColumn("m", "MEMBER_fathername",new String[]{},"isnotnull")
						+ " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_fathername",new String[]{""},"!=")
						+ ") THEN " + dynamicQueryBuilder.getColumn("m", "MEMBER_fathername")
						+ " ELSE (CASE WHEN (" + dynamicQueryBuilder.getColumn("cm", "CM_fathername",new String[]{},"isnotnull")
						+ " AND " + dynamicQueryBuilder.getColumn("cm", "CM_fathername",new String[]{""},"!=")
						+ ") THEN " + dynamicQueryBuilder.getColumn("cm", "CM_fathername")
						+ " ELSE " + dynamicQueryBuilder.getColumn("memf", "MEMBER_nameeng") + " END) END) AS \"Father\", "
						+ "(CASE WHEN (" + dynamicQueryBuilder.getColumn("e", "ELCO_husbandname",new String[]{},"isnotnull")
						+ " AND " + dynamicQueryBuilder.getColumn("e", "ELCO_husbandname",new String[]{""},"!=")
						+ ") THEN " + dynamicQueryBuilder.getColumn("e", "ELCO_husbandname")
						+ " ELSE (CASE WHEN (" + dynamicQueryBuilder.getColumn("cm", "CM_husbandname",new String[]{},"isnotnull")
						+ " AND " + dynamicQueryBuilder.getColumn("cm", "CM_husbandname",new String[]{""},"!=")
						+ ") THEN " + dynamicQueryBuilder.getColumn("cm", "CM_husbandname")
						+ " ELSE " + dynamicQueryBuilder.getColumn("memh", "MEMBER_nameeng") + " END) END) AS \"husbandName\" ";
			}

			sqlNRC = "SELECT " + dynamicQueryBuilder.getTable("CM") + ".*, " + dynamicQueryBuilder.getTable("NRCEXTENSION") + ".*";

			if(searchClient.getString("options").equals("3")){
				sqlPOP = sqlPOP + ",\"service\".\"" + column + "\" as \""+column +"\" FROM "
						+ "(SELECT \"" + table + "\".\"" + columnHealthId + "\",\"" + table + "\".\"" + column + "\" "
						+ "FROM \"" + table + "\" WHERE \"" + column + "\" BETWEEN "
						+ "('" + searchClient.get("startDate") + "') AND ('" + searchClient.get("endDate") +"')"
						+ ") AS \"service\" "
						+ "LEFT JOIN (SELECT " + dynamicQueryBuilder.getColumn("", "CM_generatedid") + ","
						+ dynamicQueryBuilder.getColumn("", "CM_healthid") + " FROM " + dynamicQueryBuilder.getTable("CM") + ") AS \"clientMap\" ON "
						+ "\"service\".\"" + columnHealthId + "\" = " + dynamicQueryBuilder.getColumn("clientMap","CM_generatedid") + " LEFT JOIN "
						+ "(SELECT * FROM ";
				sqlNRC = sqlNRC + ",\"service\".\"" + column + "\" as \""+column +"\" FROM "
						+ "(SELECT \"" + columnHealthId + "\",\"" + column + "\" FROM \"" + table + "\" WHERE \"" + column + "\" BETWEEN "
						+ "('" + searchClient.get("startDate") + "') AND ('" + searchClient.get("endDate") +"')"
						+ ") as \"service\", ";
			}
			else{
				sqlPOP = sqlPOP + " FROM ";
				sqlNRC = sqlNRC + " FROM ";
			}

			if(searchClient.getString("options").equals("3")){
				sqlPOP = sqlPOP + dynamicQueryBuilder.getTable("MEMBER") + " AS m WHERE ";
			}
			else{
				//query of part from portion (main table)..........
				if(searchClient.getString("options").equals("2")) {
					//filtering main table initially with where condition before joining
					sqlPOP = sqlPOP + "(SELECT * from " + dynamicQueryBuilder.getTable("MEMBER") + " AS m WHERE";
					if (!searchClient.get("zilla").equals("none") && !searchClient.get("zilla").equals("") && !searchClient.get("zilla").equals(null)) {
						sqlPOP = sqlPOP + dynamicQueryBuilder.getColumn("m", "MEMBER_zillaid", new String[]{searchClient.getString("zilla").split("_")[0].replaceFirst("^0+(?!$)", "")}, "=");


						if (!searchClient.get("upz").equals("none") && !searchClient.get("upz").equals("") && !searchClient.get("upz").equals(null)) {
							sqlPOP = sqlPOP + " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_upazilaid", new String[]{searchClient.getString("upz").split("_")[0].replaceFirst("^0+(?!$)", "")}, "=");


							if (!searchClient.get("union").equals("none") && !searchClient.get("union").equals("") && !searchClient.get("union").equals(null)) {
								sqlPOP = sqlPOP + " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_unionid", new String[]{searchClient.getString("union").split("_")[0].replaceFirst("^0+(?!$)", "")}, "=");


								if (!searchClient.get("villagemouza").equals("none") && !searchClient.get("villagemouza").equals("") && !searchClient.get("villagemouza").equals(null)) {
									sqlPOP = sqlPOP + " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_mouzaid", new String[]{searchClient.getString("villagemouza").split("_")[1].replaceFirst("^0+(?!$)", "")}, "=")
											+ " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_villageid", new String[]{searchClient.getString("villagemouza").split("_")[0].replaceFirst("^0+(?!$)", "")}, "=");

								}
							}
						}

						sqlPOP = sqlPOP + " AND ";
						sqlPOP = sqlPOP + dynamicQueryBuilder.getColumn("m", "MEMBER_gender",new String[]{searchClient.getString("gender")},"=")
								+ " AND (" + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{"0"},"=")
								+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{},"isnull") + ")"
								+ " AND (" + dynamicQueryBuilder.getColumn("m", "MEMBER_nameeng",new String[]{searchClient.getString("name")},"likeafter")
								+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_nameeng",new String[]{" " + searchClient.getString("name")},"likeboth")
								+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_nameeng",new String[]{"." + searchClient.getString("name")},"likeboth")
								+ ")) AS m";
					}
				}else{
					sqlPOP = sqlPOP + dynamicQueryBuilder.getTable("MEMBER") + " AS m ";
				}

				//..................................filtering query end............................
				sqlPOP = sqlPOP+ "LEFT JOIN " + dynamicQueryBuilder.getTable("CM") + " AS cm ON "
						+ dynamicQueryBuilder.getPartialCondition("cm", "CM_healthid","m","MEMBER_healthid","=") + " ";
				if(searchClient.getString("options").equals("1")){
					sqlPOP = sqlPOP + "AND " + dynamicQueryBuilder.getColumn("cm", "CM_mobileNo",new String[]{},"isnull") + " OR "
							+ dynamicQueryBuilder.getColumn("cm", "CM_mobileNo",new String[]{searchClient.getString("mobileNo")},"!=") + ") ";
				}
				sqlPOP = sqlPOP + " LEFT JOIN " + dynamicQueryBuilder.getTable("ELCO") + " AS e ON "
						+ dynamicQueryBuilder.getPartialCondition("e", "ELCO_healthid","cm","CM_generatedid","=") + " "
						+ " LEFT JOIN " + dynamicQueryBuilder.getTable("MEMBER") + " AS memf ON "
						+ dynamicQueryBuilder.getPartialCondition("memf", "MEMBER_zillaid","m","MEMBER_zillaid","=")
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memf", "MEMBER_upazilaid","m","MEMBER_upazilaid","=")
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memf", "MEMBER_unionid","m","MEMBER_unionid","=")
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memf", "MEMBER_mouzaid","m","MEMBER_mouzaid","=")
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memf", "MEMBER_villageid","m","MEMBER_villageid","=")
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memf", "MEMBER_householdid","m","MEMBER_householdid","=")
						+ " AND (" + dynamicQueryBuilder.getColumn("memf", "MEMBER_exittype",new String[]{"0"},"=")
						+ " OR " + dynamicQueryBuilder.getColumn("memf", "MEMBER_exittype",new String[]{},"isnull") + ")"
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memf", "MEMBER_healthid","m","MEMBER_healthid","<>")
                        + " AND " + dynamicQueryBuilder.getPartialCondition("m", "MEMBER_fatherserialnumber","memf","MEMBER_serialnumber","=")
						+ " LEFT JOIN " + dynamicQueryBuilder.getTable("MEMBER") + " AS memh ON "
						+ dynamicQueryBuilder.getPartialCondition("memh", "MEMBER_zillaid","m","MEMBER_zillaid","=")
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memh", "MEMBER_upazilaid","m","MEMBER_upazilaid","=")
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memh", "MEMBER_unionid","m","MEMBER_unionid","=")
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memh", "MEMBER_mouzaid","m","MEMBER_mouzaid","=")
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memh", "MEMBER_villageid","m","MEMBER_villageid","=")
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memh", "MEMBER_householdid","m","MEMBER_householdid","=")
						+ " AND (" + dynamicQueryBuilder.getColumn("memh", "MEMBER_exittype",new String[]{"0"},"=")
						+ " OR " + dynamicQueryBuilder.getColumn("memh", "MEMBER_exittype",new String[]{},"isnull") + ")"
						+ " AND " + dynamicQueryBuilder.getPartialCondition("memh", "MEMBER_healthid","m","MEMBER_healthid","<>")
                        + " AND " + dynamicQueryBuilder.getPartialCondition("m", "MEMBER_spousenumber","memh","MEMBER_serialnumber","=")
						+ ((searchClient.getString("options").equals("2"))?"": " WHERE ");
			}
			sqlNRC = sqlNRC + dynamicQueryBuilder.getTable("CM") + " LEFT JOIN "
					+ dynamicQueryBuilder.getTable("NRCEXTENSION") + " ON "
					+ dynamicQueryBuilder.getPartialCondition("table", "CM_generatedid","table","NRCEXTENSION_generatedId","=")
					+ " WHERE ";

			/*
			 * generating sql based on the available information -- where condition portion
			*/
			if(searchClient.getString("options").equals("1")){
				sqlPOP = sqlPOP + "(" + dynamicQueryBuilder.getColumn("m", "MEMBER_mobilenumber",new String[]{searchClient.getString("mobileNo")},"=")
						+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_mobilenumber2",new String[]{searchClient.getString("mobileNo")},"=") + ")"
						+ " AND (" + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{"0"},"=")
						+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{},"isnull") + ")"
						+ " AND " + dynamicQueryBuilder.getPartialCondition("m", "MEMBER_healthid","cm","CM_healthid","=") + " ";
				sqlNRC = sqlNRC + dynamicQueryBuilder.getColumn("table", "CM_mobileNo",new String[]{searchClient.getString("mobileNo")},"=")
						+ " OR " + dynamicQueryBuilder.getColumn("table", "NRCEXTENSION_cellNo2",new String[]{searchClient.getString("mobileNo")},"=");
			}
			else{
				if(searchClient.getString("options").equals("3")){
					sqlNRC = sqlNRC + dynamicQueryBuilder.getColumn("table", "CM_generatedid") + " IN (\"service\".\"" + columnHealthId + "\") AND "
							+ dynamicQueryBuilder.getPartialCondition("table", "CM_generatedid","table","CM_healthid","=") + " AND ";
				}

				if(!searchClient.get("zilla").equals("none") && !searchClient.get("zilla").equals("") && !searchClient.get("zilla").equals(null)){
					if(!searchClient.getString("options").equals("2")){
						sqlPOP = sqlPOP + dynamicQueryBuilder.getColumn("m", "MEMBER_zillaid",new String[]{searchClient.getString("zilla").split("_")[0].replaceFirst("^0+(?!$)", "")},"=");
					}
					sqlNRC = sqlNRC + dynamicQueryBuilder.getColumn("table", "CM_zillaid",new String[]{searchClient.getString("zilla").split("_")[0]},"=");

					if(!searchClient.get("upz").equals("none") && !searchClient.get("upz").equals("") && !searchClient.get("upz").equals(null)){
						if(!searchClient.getString("options").equals("2")){
							sqlPOP = sqlPOP + " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_upazilaid",new String[]{searchClient.getString("upz").split("_")[0].replaceFirst("^0+(?!$)", "")},"=");
						}
						sqlNRC = sqlNRC + " AND " + dynamicQueryBuilder.getColumn("table", "CM_upazilaid",new String[]{searchClient.getString("upz")},"=");

						if(!searchClient.get("union").equals("none") && !searchClient.get("union").equals("") && !searchClient.get("union").equals(null)){
							if(!searchClient.getString("options").equals("2")){
								sqlPOP = sqlPOP + " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_unionid",new String[]{searchClient.getString("union").split("_")[0].replaceFirst("^0+(?!$)", "")},"=");
							}
							sqlNRC = sqlNRC + " AND " + dynamicQueryBuilder.getColumn("table", "CM_unionid",new String[]{searchClient.getString("union")},"=");

							if(!searchClient.get("villagemouza").equals("none") && !searchClient.get("villagemouza").equals("") && !searchClient.get("villagemouza").equals(null)){
								if(!searchClient.getString("options").equals("2")){
									sqlPOP = sqlPOP + " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_mouzaid",new String[]{searchClient.getString("villagemouza").split("_")[1].replaceFirst("^0+(?!$)", "")},"=")
											+ " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_villageid",new String[]{searchClient.getString("villagemouza").split("_")[0].replaceFirst("^0+(?!$)", "")},"=");
								}

								sqlNRC = sqlNRC + " AND " + dynamicQueryBuilder.getColumn("table", "CM_mouzaid",new String[]{searchClient.getString("villagemouza").split("_")[1]},"=")
										+ " AND " + dynamicQueryBuilder.getColumn("table", "CM_villageid",new String[]{searchClient.getString("villagemouza").split("_")[0]},"=");
							}
						}
					}
					if(!searchClient.getString("options").equals("3")){
						if(!searchClient.getString("options").equals("2")){
							sqlPOP = sqlPOP + " AND ";
						}
						sqlNRC = sqlNRC + " AND ";
					}

					if(searchClient.getString("options").equals("3")){
						sqlPOP = sqlPOP + " AND (" + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{"0"},"=")
								+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{},"isnull") + ")) AS m ON "
								+ dynamicQueryBuilder.getPartialCondition("clientMap", "CM_healthid", "m", "MEMBER_healthid", "=")
								+ " WHERE " + dynamicQueryBuilder.getPartialCondition("clientMap", "CM_healthid", "m", "MEMBER_healthid", "=");
					}
					if(!searchClient.getString("options").equals("3")){
						sqlPOP = sqlPOP + dynamicQueryBuilder.getColumn("m", "MEMBER_gender",new String[]{searchClient.getString("gender")},"=")
								+ " AND (" + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{"0"},"=")
								+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{},"isnull") + ")"
								+ " AND (" + dynamicQueryBuilder.getColumn("m", "MEMBER_nameeng",new String[]{searchClient.getString("name")},"likeafter")
								+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_nameeng",new String[]{" " + searchClient.getString("name")},"likeboth")
								+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_nameeng",new String[]{"." + searchClient.getString("name")},"likeboth")
								+ ")"
								+ " AND " + dynamicQueryBuilder.getPartialCondition("m", "MEMBER_healthid", "cm", "CM_healthid", "=");
						sqlNRC = sqlNRC + dynamicQueryBuilder.getColumn("table", "CM_gender",new String[]{searchClient.getString("gender")},"=")
								+ " AND (" + dynamicQueryBuilder.getColumn("table", "CM_name",new String[]{searchClient.getString("name")},"likeafter")
								+ " OR " + dynamicQueryBuilder.getColumn("table", "CM_name",new String[]{" " + searchClient.getString("name")},"likeboth")
								+ " OR " + dynamicQueryBuilder.getColumn("table", "CM_name",new String[]{"." + searchClient.getString("name")},"likeboth")
								+ ")";
					}
				}

			}

			searchList.put("column", column);
			System.out.println("POP: " + sqlPOP);
			System.out.println("NRC: " + sqlNRC);
		}

		try{
			if(searchClient.getString("options").equals("4")){
				AdvanceSearchExecution.getPregwomenResult(sql, searchList);
			}
			else{
				AdvanceSearchExecution.getPOPResult(clientHelper, sqlPOP, searchList, dynamicQueryBuilder);
				AdvanceSearchExecution.getNRCResult(clientHelper, sqlNRC, searchList, dynamicQueryBuilder);
			}
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		return searchList;
	}

	private static void initializeServiceDetail(final QueryBuilder dynamicQueryBuilder) throws JSONException{

		serviceDetail.put("1", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("ANC"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("ANC_healthid"));
			putOpt("column",dynamicQueryBuilder.getColumn("ANC_ancdate"));}});

		serviceDetail.put("2", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("DELIVERY"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("DELIVERY_healthid"));
			putOpt("column",dynamicQueryBuilder.getColumn("DELIVERY_dDate"));}});

		serviceDetail.put("3", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("PNCMOTHER"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("PNCMOTHER_healthid"));
			putOpt("column",dynamicQueryBuilder.getColumn("PNCMOTHER_pncdate"));}});

		serviceDetail.put("4", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("PILLCONDOM"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("PILLCONDOM_healthId"));
			putOpt("column",dynamicQueryBuilder.getColumn("PILLCONDOM_visitDate"));}});

		serviceDetail.put("5", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("WOMANINJECTABLE"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("WOMANINJECTABLE_healthId"));
			putOpt("column",dynamicQueryBuilder.getColumn("WOMANINJECTABLE_doseDate"));}});

		serviceDetail.put("6", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("IUD"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("IUD_healthId"));
			putOpt("column",dynamicQueryBuilder.getColumn("IUD_iudImplantDate"));}});

		serviceDetail.put("7", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("IUDFOLLOWUP"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("IUDFOLLOWUP_healthId"));
			putOpt("column",dynamicQueryBuilder.getColumn("IUDFOLLOWUP_followupDate"));}});

		serviceDetail.put("8", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("GP"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("GP_healthId"));
			putOpt("column",dynamicQueryBuilder.getColumn("GP_visitDate"));}});

		serviceDetail.put("9", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("PAC"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("PAC_healthId"));
			putOpt("column",dynamicQueryBuilder.getColumn("PAC_visitDate"));}});

		serviceDetail.put("10", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("IMPLANT"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("IMPLANT_healthId"));
			putOpt("column",dynamicQueryBuilder.getColumn("IMPLANT_implantImplantDate"));}});

		serviceDetail.put("11", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("IMPLANTFOLLOWUP"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("IMPLANTFOLLOWUP_healthId"));
			putOpt("column",dynamicQueryBuilder.getColumn("IMPLANTFOLLOWUP_followupDate"));}});

		serviceDetail.put("12", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("PMS"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("PMS_healthId"));
			putOpt("column",dynamicQueryBuilder.getColumn("PMS_implantOperationDate"));}});

        serviceDetail.put("13", new JSONObject(){{
            putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("PMSFOLLOWUP"));
            putOpt("column_healthid",dynamicQueryBuilder.getColumn("PMSFOLLOWUP_healthId"));
            putOpt("column",dynamicQueryBuilder.getColumn("PMSFOLLOWUP_followupDate"));}});

		serviceDetail.put("14", new JSONObject(){{
			putOpt("table",dynamicQueryBuilder.getTableWithoutQuote("CHILD"));
			putOpt("column_healthid",dynamicQueryBuilder.getColumn("CHILD_healthId"));
			putOpt("column",dynamicQueryBuilder.getColumn("CHILD_visitDate"));}});




	}
}