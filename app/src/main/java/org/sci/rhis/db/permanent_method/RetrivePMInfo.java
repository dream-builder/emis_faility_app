package org.sci.rhis.db.permanent_method;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.utilities.JsonHandler;

public class RetrivePMInfo {
    public static JSONObject getPM(JSONObject pmsInfo, JSONObject pmsInformation, QueryBuilder dynamicQueryBuilder) {

        try{
            //Have to write Query
            String sql ="SELECT " + dynamicQueryBuilder.getTable("PMS") + ".*,"
                    + dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_boy") + ","
                    + dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_girl") + ","
                    + dynamicQueryBuilder.getColumn("table", "IU_FPINFO_ELCO_marrDate")
                    + " FROM " + dynamicQueryBuilder.getTable("PMS")
                    + " LEFT JOIN " + dynamicQueryBuilder.getTable("IU_FPINFO_ELCO") + " ON "
                    + dynamicQueryBuilder.getPartialCondition("table", "IU_FPINFO_ELCO_healthId","table","PMS_healthId","=")
                    + " WHERE " + dynamicQueryBuilder.getColumn("table", "PMS_healthId",new String[]{pmsInfo.getString("healthId")},"=")
                    + " AND " + dynamicQueryBuilder.getColumn("table", "PMS_pmsCount") + " IN (SELECT MAX("
                    + dynamicQueryBuilder.getColumn("table", "PMS_pmsCount") + ") FROM " + dynamicQueryBuilder.getTable("PMS")
                    + " WHERE " + dynamicQueryBuilder.getColumn("table", "PMS_healthId",new String[]{pmsInfo.getString("healthId")},"=") + ")";

            SimpleCursor rs =new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));
            pmsInfo.put("distributionJson","treatment");

            if(rs.next()){
                pmsInformation = new JsonHandler().getServiceDetail(rs,
                        pmsInfo, "PMS", dynamicQueryBuilder, 1);
//In this line Treatment don't return
                pmsInformation = new JsonHandler().getResponse(rs, pmsInformation, "IU_FPINFO_ELCO", 1);

                pmsInformation.put("pmsRetrieve","1");
            }
            else{
                pmsInformation.put("pmsRetrieve","2");
                pmsInformation.put("pmsCount","");
            }

            if(!rs.isClosed()){
                rs.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return pmsInformation;

    }
}
