package org.sci.rhis.db.anc;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since June, 2015
 */
public class RetrieveANCVisitInfo {

  public static JSONObject getANCVisits(JSONObject ANCInfo, JSONObject ANCVisits, QueryBuilder dynamicQueryBuilder) {

    try{
      String sql = "SELECT * FROM " + dynamicQueryBuilder.getTable("ANC")
              + " WHERE " + dynamicQueryBuilder.getColumn("table", "ANC_healthid",new String[]{ANCInfo.getString("healthid")},"=")
              + " AND " + dynamicQueryBuilder.getColumn("table", "ANC_pregNo",new String[]{ANCInfo.getString("pregNo")},"=")
              + " ORDER BY " + dynamicQueryBuilder.getColumn("table", "ANC_serviceId") + " ASC";

      SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));
      ANCVisits.put("count", 0);
      ANCInfo.put("distributionJson","anctreatment");

      while(rs.next()){
        ANCVisits.put(rs.getString(dynamicQueryBuilder.getColumn("ANC_serviceId")), new JsonHandler().getServiceDetail(rs,
                ANCInfo, "ANC", dynamicQueryBuilder,2));
        ANCVisits.put("count", (ANCVisits.getInt("count")+1));
        ANCVisits.put("ancStatus", false);
      }

      return ANCVisits;
    }
    catch(Exception e){
      e.printStackTrace();
      return new JSONObject();
    }
  }
}