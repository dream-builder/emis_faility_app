package org.sci.rhis.db.implant;

import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class RetrieveImplantInfo {

	public static JSONObject getImplant(JSONObject implantInfo, JSONObject implantInformation,QueryBuilder dynamicQueryBuilder) {

		try{
			String sql = "SELECT " + dynamicQueryBuilder.getTable("IMPLANT") + ".*,"
					+ dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_boy") + ","
					+ dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_girl") + ","
					+ dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_marrDate")
					+ " FROM " + dynamicQueryBuilder.getTable("IMPLANT")
					+ " LEFT JOIN " + dynamicQueryBuilder.getTable("IU_FPINFO_ELCO") + " ON "
					+ dynamicQueryBuilder.getPartialCondition("table", "IU_FPINFO_ELCO_healthId","table","IMPLANT_healthId","=")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "IMPLANT_healthId",new String[]{implantInfo.getString("healthId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "IMPLANT_implantCount") + " IN (SELECT MAX("
					+ dynamicQueryBuilder.getColumn("table", "IMPLANT_implantCount") + ") FROM " + dynamicQueryBuilder.getTable("IMPLANT")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "IMPLANT_healthId",new String[]{implantInfo.getString("healthId")},"=") + ")";

			SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));
			implantInfo.put("distributionJson","treatment");

			if(rs.next()){
				implantInformation = new JsonHandler().getServiceDetail(rs,
						implantInfo, "IMPLANT", dynamicQueryBuilder, 1);
				implantInformation = new JsonHandler().getResponse(rs, implantInformation, "IU_FPINFO_ELCO", 1);

				implantInformation.put("implantRetrieve","1");
			}
			else{
				implantInformation.put("implantRetrieve","2");
				implantInformation.put("implantCount","");
			}

			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return implantInformation;
	}
}