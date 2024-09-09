package org.sci.rhis.db.dbhelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Properties;

/**
 * @author sabah.mugab
 * @since December, 2016
 */
public class JSONKeyMapper extends Mapper{

    public JSONKeyMapper(){}

    JSONKeyMapper(DBTableInfoHandler dbTableInfoHandler){

        try {
            dbTableInfoHandler.serviceJSON.put("table", PropertiesInfo.SERVICE_JSON_PROPERTIES.getProperty(dbTableInfoHandler.serviceName + /*"_" + dbTableInfoHandler.serviceType +*/ "_table"));
            dbTableInfoHandler.serviceJSON.put("condition", PropertiesInfo.SERVICE_JSON_PROPERTIES.getProperty(dbTableInfoHandler.serviceName + "_" + dbTableInfoHandler.serviceType + "_condition"));
            dbTableInfoHandler.serviceJSON.put("fields", PropertiesInfo.SERVICE_JSON_PROPERTIES.getProperty(dbTableInfoHandler.serviceName + "_" + dbTableInfoHandler.serviceType + "_json"));

            this.resetObject();
            this.setKeyMap(dbTableInfoHandler);
            this.setConditionKeyMap(dbTableInfoHandler);

            dbTableInfoHandler.serviceJSON.put("keyMapField", this.getJSONKeyDetail());
            dbTableInfoHandler.serviceJSON.put("keyMapCondition", this.getConditionJSONKeyDetail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void resetObject(){

        serviceMapper = new JSONObject();
        conditionMapper = new JSONObject();
    }

    private void setKeyMap(DBTableInfoHandler dbTableInfoHandler){

        this.setInnerKeys(dbTableInfoHandler, serviceMapper, "fields");
    }

    private void setConditionKeyMap(DBTableInfoHandler dbTableInfoHandler){

        this.setInnerKeys(dbTableInfoHandler, conditionMapper, "condition");
    }

    private void setInnerKeys(DBTableInfoHandler dbTableInfoHandler, JSONObject mapper, String keyDetail){

        JSONObject innerKey;

        try {
            for(String keyName:dbTableInfoHandler.serviceJSON.getString(keyDetail).split(",")){
                innerKey = new JSONObject();
                if(PropertiesInfo.SERVICE_JSON_MAP_PROPERTIES.getProperty(dbTableInfoHandler.serviceName + "_" + keyName)!=null){
                    String[] dbInfo = PropertiesInfo.SERVICE_JSON_MAP_PROPERTIES.getProperty(dbTableInfoHandler.serviceName + "_" + keyName).split(",");

                    innerKey.put("dbColumnName", dbInfo[0]);
                    innerKey.put("dbColumnType", dbInfo[1]);
                    innerKey.put("dbColumnDefaultValue", dbInfo[2]);
                    innerKey.put("responseDefaultValue", dbInfo[3]);
                }

                mapper.put(keyName, innerKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject setRequiredKeys(JSONObject serviceJSON, String serviceName) throws JSONException {

        Properties serviceKeys = PropertiesInfo.SERVICE_JSON_MAP_PROPERTIES;
        Iterator<String> keys = serviceJSON.keys();
        String key = "";
        JSONObject additionalKeys = new JSONObject();

        while( keys.hasNext() ) {
            key = keys.next();

            if(serviceKeys.getProperty(serviceName + "_" + key)!=null){
                additionalKeys.put(serviceKeys.getProperty(serviceName + "_" + key).split(",")[0],serviceJSON.getString(key));
            }

            additionalKeys.put(key,serviceJSON.getString(key));
        }
        additionalKeys.put("serviceName",serviceName);
        return additionalKeys;
    }

    public JSONObject getJSONKeyDetail(){
        return serviceMapper;
    }

    public JSONObject getConditionJSONKeyDetail(){
        return conditionMapper;
    }
}