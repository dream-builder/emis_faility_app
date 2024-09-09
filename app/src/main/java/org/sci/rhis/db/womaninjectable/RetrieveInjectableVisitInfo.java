package org.sci.rhis.db.womaninjectable;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since February, 2016
 */
public class RetrieveInjectableVisitInfo {

    public static JSONObject getInjectableVisits(JSONObject injectableInfo, JSONObject injectableVisits, QueryBuilder dynamicQueryBuilder) {

        try {
            String sql = "SELECT " + dynamicQueryBuilder.getColumn("table", "WOMANINJECTABLE_providerId") + ", "
                    + dynamicQueryBuilder.getColumn("table", "WOMANINJECTABLE_doseId") + ", "
                    + dynamicQueryBuilder.getColumn("table", "WOMANINJECTABLE_doseDate") + ", "
                    + dynamicQueryBuilder.getColumn("table", "WOMANINJECTABLE_injectionId") + ", "
                    + dynamicQueryBuilder.getColumn("table", "WOMANINJECTABLE_isNewClient") + ", "
                    + dynamicQueryBuilder.getTable("FPEXAMINATION_WOMANINJECTABLE") + ".*"
                    + " FROM " + dynamicQueryBuilder.getTable("WOMANINJECTABLE")
                    + " LEFT JOIN " + dynamicQueryBuilder.getTable("FPEXAMINATION_WOMANINJECTABLE") + " ON "
                    + dynamicQueryBuilder.getPartialCondition("table", "FPEXAMINATION_WOMANINJECTABLE_healthId", "table", "WOMANINJECTABLE_healthId", "=") + " AND "
                    + dynamicQueryBuilder.getPartialCondition("table", "FPEXAMINATION_WOMANINJECTABLE_serviceId", "table", "WOMANINJECTABLE_doseId", "=") + " AND "
                    + dynamicQueryBuilder.getColumn("table", "FPEXAMINATION_WOMANINJECTABLE_FPType", new String[]{injectableInfo.getString("FPType")}, "=")
                    + " WHERE " + dynamicQueryBuilder.getColumn("table", "WOMANINJECTABLE_healthId", new String[]{injectableInfo.getString("healthId")}, "=")
                    //+ " ORDER BY " + dynamicQueryBuilder.getColumn("table", "WOMANINJECTABLE_doseId") + " ASC";
                    + " ORDER BY " + dynamicQueryBuilder.getColumn("table", "WOMANINJECTABLE_doseDate") + " ASC";


            SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql, null));
            injectableVisits.put("count", 0);
            injectableInfo.put("distributionJson", "treatment");

            while (rs.next()) {
                injectableVisits.put("count", (injectableVisits.getInt("count") + 1));

                //injectableVisits.put(rs.getString(dynamicQueryBuilder.getColumn("WOMANINJECTABLE_doseId")),
                injectableVisits.put(injectableVisits.getString("count"),
                        new JsonHandler().getResponse(rs, new JsonHandler().getServiceDetail(rs,
                                injectableInfo, "FPEXAMINATION_WOMANINJECTABLE", dynamicQueryBuilder, 2), "WOMANINJECTABLE", 1));

                //injectableVisits.put("count", (injectableVisits.getInt("count")+1));
            }

            if (!rs.isClosed()) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return injectableVisits;
    }
}