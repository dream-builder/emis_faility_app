package org.sci.rhis.db.womaninjectable;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.JsonHandler;

/**
 * Created by arafat.hasan on 4/12/2017.
 */

public class UpdateInjectableVisitInfo {

    public static boolean updateInjectableVisit(JSONObject injectableInfo, QueryBuilder dynamicQueryBuilder) {
        boolean status=true;

        try{
            injectableInfo.put("sideEffectCore",injectableInfo.get("sideEffect").equals("")? 2 : 1);
            DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(injectableInfo, "WOMANINJECTABLE")));

            injectableInfo.put("serviceId",injectableInfo.get("doseId"));
            injectableInfo.put("isNewClient", 1);
            injectableInfo.put("currentMethod", Flag.INJECTION_DMPA);
            DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(injectableInfo, "FPEXAMINATION_WOMANINJECTABLE")));
            DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(injectableInfo, "FPSTATUS")));
        }
        catch(Exception e){
            status=false;
            System.out.println(e);
            e.printStackTrace();
        }
        return status;
    }
}
