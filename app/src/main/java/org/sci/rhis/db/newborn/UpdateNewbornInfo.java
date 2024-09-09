package org.sci.rhis.db.newborn;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * Created by arafat.hasan on 3/1/2017.
 */

public class UpdateNewbornInfo {
    static boolean status;

    public static boolean updateNewborn(JSONObject newbornInfo, QueryBuilder dynamicQueryBuilder) {

        try{
            CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(newbornInfo, "NEWBORN")));
            status=true;
        }
        catch(Exception e){
            status=false;
            e.printStackTrace();
        }
        return status;
    }
}
