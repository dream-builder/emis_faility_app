package org.sci.rhis.db.dbhelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.ConfInfoRetrieve;
import org.sci.rhis.utilities.GlobalActivity;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author sabah.mugab
 * @since December, 2016
 */
public class DBTableInfoHandler extends DBTableInfo{

    public DBTableInfoHandler(String serviceN,String serviceT){
        serviceJSON = new JSONObject();
        serviceName = serviceN;
        serviceType = serviceT;
        this.setProp();
    }

    private void setProp(){
        try {
            keyMapper = new JSONKeyMapper(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getDetail(){
        return serviceJSON;
    }
}
