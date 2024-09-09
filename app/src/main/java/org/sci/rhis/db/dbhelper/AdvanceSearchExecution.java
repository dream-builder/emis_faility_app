package org.sci.rhis.db.dbhelper;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author sabah.mugab
 * @since September, 2015
 */
public class AdvanceSearchExecution {

	public static void getPOPResult(ClientInfoUtil clientHelper, String sqlPOP, JSONObject searchList, QueryBuilder dynamicQueryBuilder) {

		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		try{
			SimpleCursor rs = new SimpleCursor(db.rawQuery(sqlPOP, null));
			Log.d("SQLITE-DB", String.format("Adv-Search Query return %d rows:\n%s", rs.count(), sqlPOP));

			while(rs.next()){
				JSONObject resultSet = new JSONObject();

				searchList.put("count", searchList.getInt("count") + 1 );

				resultSet.put("healthId",rs.getString("HealthID"));
				resultSet.put("name",rs.getString("NameEng"));
				resultSet.put("fatherName",rs.getObject("Father")==null?"":rs.getString("Father"));
				resultSet.put("husbandName",rs.getObject("husbandName")==null?"":rs.getString("husbandName"));
				if(rs.getObject("DOB")!=null){
					long days = Utilities.getDateDiff(rs.getDate("DOB"), new Date(), TimeUnit.DAYS);
					resultSet.put("age", String.valueOf(days/365));
				}else{
					resultSet.put("age", "");
				}
				resultSet.put("healthIdPop",1);

				if(searchList.getString("options").equals("3")){
					String serviceDate = rs.getString(searchList.getString("column"));
					resultSet.put("serviceDate", serviceDate==null?"":
							Converter.convertSdfFormat(serviceDate.contains(" ")?Constants.LONG_HYPHEN_FORMAT_DATABASE: Constants.SHORT_HYPHEN_FORMAT_DATABASE,
									serviceDate,Constants.SHORT_HYPHEN_FORMAT_DATABASE));
				}
				else{
					resultSet.put("serviceDate", "");
				}

				searchList.put(searchList.getString("count"), resultSet);

			}
			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public static void getNRCResult(ClientInfoUtil clientHelper, String sqlNRC, JSONObject searchList, QueryBuilder dynamicQueryBuilder) {
		JsonHandler result = new JsonHandler();
		String sql = "SELECT " + dynamicQueryBuilder.getColumn("m", "MEMBER_healthid") + " AS \"HealthID\","
				+ dynamicQueryBuilder.getColumn("m", "MEMBER_nameeng") + " AS \"NameEng\","
				+ dynamicQueryBuilder.getColumn("m", "MEMBER_dob") + " AS \"DOB\","
				+ clientHelper.getFatherSQL(dynamicQueryBuilder)
				+ clientHelper.getSpouseSQL(dynamicQueryBuilder)
				+ "FROM " + dynamicQueryBuilder.getTable("MEMBER") + " AS m "
				+ "WHERE (" + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{""},"=")
				+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{"0"},"=")
				+ " OR " + dynamicQueryBuilder.getColumn("m", "MEMBER_exittype",new String[]{},"isnull") + ")"
				+ " AND " + dynamicQueryBuilder.getColumn("m", "MEMBER_healthid") + " IN (";
		SQLiteDatabase db = DatabaseWrapper.getDatabase();

		try{
			SimpleCursor rs = new SimpleCursor(db.rawQuery(sqlNRC, null));
			String healthId = "";

			while(rs.next()){
				JSONObject resultSet = new JSONObject();
				/*
				 * checking if this is still a NRC (temporary) ID
				 */
				if(rs.getLong(dynamicQueryBuilder.getColumn("CM_healthid")) == rs.getLong(dynamicQueryBuilder.getColumn("CM_generatedid"))){

					searchList.put("count", searchList.getInt("count") + 1 );

					resultSet.put("healthId",result.getResultSetValue(rs,dynamicQueryBuilder.getColumn("CM_healthid")));
					resultSet.put("name",result.getResultSetValue(rs,dynamicQueryBuilder.getColumn("CM_name")));
					resultSet.put("fatherName",result.getResultSetValue(rs,dynamicQueryBuilder.getColumn("CM_fathername")));
					resultSet.put("husbandName",result.getResultSetValue(rs,dynamicQueryBuilder.getColumn("CM_husbandname")));
					resultSet.put("age",rs.getObject(dynamicQueryBuilder.getColumn("CM_dob"))==null?"":
						String.valueOf(Utilities.getDateDiff(rs.getDate(dynamicQueryBuilder.getColumn("CM_dob")), new Date(), TimeUnit.DAYS)/365));
					resultSet.put("healthIdPop",0);

					if(searchList.getString("options").equals("3")){
						resultSet.put("serviceDate",result.getResultSetValue(rs,searchList.getString("column")));//, rs.getObject(searchList.getString("column"))==null?"":rs.getString(searchList.getString("column")));
					}
					else{
						resultSet.put("serviceDate", "");
					}

					searchList.put(searchList.getString("count"), resultSet);
				}
				else if(searchList.getString("options").equals("1")){
					healthId = healthId + rs.getString(dynamicQueryBuilder.getColumn("CM_healthid")) + ",";
				}
			}

			if(healthId.length() > 5){
				sql = sql + healthId.substring(0, healthId.length()-1) + ")";
				System.out.println(sql);
				getPOPResult(clientHelper, sql, searchList, dynamicQueryBuilder);
			}

			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}


	public static void getPregwomenResult(String sql, JSONObject searchList) {
		JsonHandler result = new JsonHandler();
		SQLiteDatabase db = DatabaseWrapper.getDatabase();
		try{
			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql, null));

			while(rs.next()){
				JSONObject resultSet = new JSONObject();

				searchList.put("count", searchList.getInt("count") + 1 );

				resultSet.put("healthId",result.getResultSetValue(rs,"healthId"));
				resultSet.put("name",result.getResultSetValue(rs,"name"));
				resultSet.put("age",rs.getObject("DOB")==null?(rs.getObject("Age")==null?"":rs.getString("Age")):String.valueOf(Utilities.getDateDiff(rs.getDate("DOB"), new Date(), TimeUnit.DAYS)/365));
				resultSet.put("husbandName",result.getResultSetValue(rs,"husbandName"));//rs.getObject("husbandName")==null?"":rs.getString("husbandName"));
				resultSet.put("mobileNo",rs.getObject("mobileNo")==null?(rs.getObject("MobileNo1")==null?"":rs.getObject("MobileNo1")):rs.getString("mobileNo"));
				resultSet.put("elcoNo",result.getResultSetValue(rs,"elcoNo"));//,rs.getObject("elcoNo")==null?"":rs.getString("elcoNo"));
				resultSet.put("son",result.getResultSetValue(rs,"son","0"));//,rs.getObject("son")==null?"0":rs.getString("son"));
				resultSet.put("dau",result.getResultSetValue(rs,"dau","0"));//,rs.getObject("dau")==null?"0":rs.getString("dau"));
				resultSet.put("gravida",result.getResultSetValue(rs,"gravida"));//,rs.getObject("gravida")==null?"":rs.getString("gravida"));
				resultSet.put("LMP",result.getResultSetValue(rs,"LMP"));//,rs.getObject("LMP")==null?"":rs.getString("LMP"));
				resultSet.put("EDD",result.getResultSetValue(rs,"EDD"));//,rs.getObject("EDD")==null?"":rs.getString("EDD"));
				//resultSet.put("VILLAGENAME",result.getResultSetValue(rs,"VILLAGENAME"));//,rs.getObject("VILLAGENAME")==null?"":rs.getString("VILLAGENAME"));
				if(rs.getLong("healthId") == rs.getLong("generatedId")){
					resultSet.put("healthIdPop",0);
					resultSet.put("name",result.getResultSetValue(rs,"cmname"));//,rs.getObject("cmname")==null?"":rs.getString("cmname"));
					resultSet.put("husbandName",result.getResultSetValue(rs,"cmhusbandname"));
					resultSet.put("age",rs.getObject("dob")==null?(rs.getObject("age")==null?"":rs.getString("age")):String.valueOf(Utilities.getDateDiff(rs.getDate("dob"), new Date(), TimeUnit.DAYS)/365));
				}
				else{
					resultSet.put("healthIdPop",1);
				}

				resultSet.put("zillaId",result.getResultSetValue(rs,"zillaId"));//,rs.getObject("zillaId")==null?"":rs.getString("zillaId"));
				resultSet.put("upazilaId",result.getResultSetValue(rs,"upazilaId"));//,rs.getObject("upazilaId")==null?"":rs.getString("upazilaId"));
				resultSet.put("unionId",result.getResultSetValue(rs,"unionId"));//,rs.getObject("unionId")==null?"":rs.getString("unionId"));
				resultSet.put("mouzaId",result.getResultSetValue(rs,"mouzaId"));//,rs.getObject("mouzaId")==null?"":rs.getString("mouzaId"));
				resultSet.put("villageId",result.getResultSetValue(rs,"villageId"));//,rs.getObject("villageId")==null?"":rs.getString("villageId"));

				searchList.put(searchList.getString("count"), resultSet);

			}
			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}