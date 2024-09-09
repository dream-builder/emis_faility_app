package org.sci.rhis.db.pillcondom;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since January, 2016
 */
public class RetrievePillCondomVisitInfo {

	public static JSONObject getPillCondomVisits(JSONObject pillCondomInfo,
												 JSONObject pillCondomVisits, QueryBuilder dynamicQueryBuilder) {
		try{
			String sql = "SELECT " + dynamicQueryBuilder.getTable("PILLCONDOM") + ".*, "
					+ dynamicQueryBuilder.getTable("FPEXAMINATION_PILLCONDOM") + ".* "
					+ " FROM " + dynamicQueryBuilder.getTable("PILLCONDOM")
					+ " LEFT JOIN " + dynamicQueryBuilder.getTable("FPEXAMINATION_PILLCONDOM") + " ON "
					+ dynamicQueryBuilder.getPartialCondition("table", "FPEXAMINATION_PILLCONDOM_healthId","table","PILLCONDOM_healthId","=") + " AND "
					+ dynamicQueryBuilder.getPartialCondition("table", "FPEXAMINATION_PILLCONDOM_serviceId","table","PILLCONDOM_serviceId","=") + " AND "
					+ dynamicQueryBuilder.getColumn("table", "FPEXAMINATION_PILLCONDOM_FPType",new String[]{pillCondomInfo.getString("FPType")},"=")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "PILLCONDOM_healthId",new String[]{pillCondomInfo.getString("healthId")},"=")
					+ " ORDER BY " + dynamicQueryBuilder.getColumn("table", "PILLCONDOM_serviceId") + " ASC";

			SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));
			pillCondomVisits.put("count", 0);

			while(rs.next()){
				pillCondomVisits.put(rs.getString(dynamicQueryBuilder.getColumn("PILLCONDOM_serviceId")),
						new JsonHandler().getResponse(rs, new JsonHandler().getResponse(rs,
								new JSONObject(), "FPEXAMINATION_PILLCONDOM", 2), "PILLCONDOM", 1));
				pillCondomVisits.put("count", (pillCondomVisits.getInt("count")+1));
			}

			if(!rs.isClosed()){
				rs.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return pillCondomVisits;
	}
}