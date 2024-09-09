package org.sci.rhis.db.fp;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since September, 2016
 */
public class RetrieveFPInfo {

	public static JSONObject getFPInfo(JSONObject fpInfo, JSONObject fpInformation,
									   QueryBuilder dynamicQueryBuilder) {
		try{
			String sql = "SELECT " + dynamicQueryBuilder.getTable("IU_FPINFO") + ".*, "
					+ dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_boy") + ","
					+ dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_girl") + ","
					+ dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_marrDate")
					+ " FROM " + dynamicQueryBuilder.getTable("IU_FPINFO")
					+ " LEFT JOIN " + dynamicQueryBuilder.getTable("IU_FPINFO_ELCO") + " ON "
					+ dynamicQueryBuilder.getPartialCondition("table", "IU_FPINFO_ELCO_healthId","table","IU_FPINFO_healthId","=")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "IU_FPINFO_healthId",new String[]{fpInfo.getString("healthId")},"=");

			SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));

			if(rs.next()){

				fpInformation = new JsonHandler().getResponse(rs, fpInformation, "IU_FPINFO", 2);
				fpInformation = new JsonHandler().getResponse(rs, fpInformation, "IU_FPINFO_ELCO", 1);

				fpInformation.put("fpInfoRetrieve","1");
			}
			else{
				fpInformation.put("fpInfoRetrieve","2");
			}
			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return fpInformation;
	}
}