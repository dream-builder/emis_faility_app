package org.sci.rhis.db.pillcondom;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * Created by arafat.hasan on 4/12/2017.
 */

public class UpdatePillCondomVisitInfo {

    public static boolean updatePillCondomVisit(JSONObject pillCondomInfo, QueryBuilder dynamicQueryBuilder) {
        @SuppressWarnings("unused")
        boolean status=false, statusExamination=false, statusFP=false;

        try{
            DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(pillCondomInfo, "PILLCONDOM")));

            if(pillCondomInfo.getString("methodType").equals("1") || pillCondomInfo.getString("methodType").equals("2")
                    || pillCondomInfo.getString("methodType").equals("10")){
                pillCondomInfo.put("isNewClient", 1);
                pillCondomInfo.put("currentMethod", pillCondomInfo.get("methodType"));
            }
            else{
                pillCondomInfo.put("isNewClient", 2);
                pillCondomInfo.put("currentMethod", "");
            }

            DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(pillCondomInfo, "FPEXAMINATION_PILLCONDOM")));
            DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(pillCondomInfo, "FPSTATUS_PILLCONDOM")));
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        return status;
    }
}
