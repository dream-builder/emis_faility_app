package org.sci.rhis.db.iud;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.SimpleArrayMap;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class RetrieveIUDInfo {

	public static JSONObject getIUD(JSONObject iudInfo, JSONObject iudInformation, QueryBuilder dynamicQueryBuilder) {

		try{
			String sql = "SELECT " + dynamicQueryBuilder.getTable("IUD") + ".*,"
					+ dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_boy") + ","
					+ dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_girl") + ","
					+ dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_marrDate")
					+ " FROM " + dynamicQueryBuilder.getTable("IUD")
					+ " LEFT JOIN " + dynamicQueryBuilder.getTable("IU_FPINFO_ELCO") + " ON "
					+ dynamicQueryBuilder.getPartialCondition("table", "IU_FPINFO_ELCO_healthId","table","IUD_healthId","=")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "IUD_healthId",new String[]{iudInfo.getString("healthId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "IUD_iudCount") + " IN (SELECT MAX("
					+ dynamicQueryBuilder.getColumn("table", "IUD_iudCount") + ") FROM " + dynamicQueryBuilder.getTable("IUD")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "IUD_healthId",new String[]{iudInfo.getString("healthId")},"=") + ")";

			SimpleCursor rs =new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));
			iudInfo.put("distributionJson","treatment");

			if(rs.next()){
				iudInformation = new JsonHandler().getServiceDetail(rs,
						iudInfo, "IUD", dynamicQueryBuilder, 1);

				iudInformation = new JsonHandler().getResponse(rs, iudInformation, "IU_FPINFO_ELCO", 1);

				iudInformation.put("iudRetrieve","1");
			}
			else{
				iudInformation.put("iudRetrieve","2");
				iudInformation.put("iudCount","");
			}

			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return iudInformation;
	}
}