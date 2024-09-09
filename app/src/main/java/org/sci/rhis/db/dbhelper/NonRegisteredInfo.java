package org.sci.rhis.db.dbhelper;

import org.json.JSONObject;
import org.sci.rhis.utilities.JsonHandler;


/**
 * @author sabah.mugab
 * @created August, 2015
 */
public class NonRegisteredInfo {

    public static JSONObject getDetailInfo(JSONObject nonRegisteredInfo) {

        JSONObject tempClient = new JSONObject();
        QueryBuilder dynamicQueryBuilder = new QueryBuilder();
        JsonHandler result = new JsonHandler();

        try{
            nonRegisteredInfo.put("healthId",nonRegisteredInfo.get("generatedId"));
            nonRegisteredInfo.put("treatment_key", "");

            if(nonRegisteredInfo.has("nrcUpdate")){
                if(nonRegisteredInfo.getString("nrcUpdate").equals("1")){
                    CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(result.addJsonKeyValueEdit(nonRegisteredInfo, "NRC")));
                    if(nonRegisteredInfo.has("memberInfo")){
                        CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(result.addJsonKeyValueEdit(nonRegisteredInfo, "NRCEXTENSION")));
                    }
                }
            }else{
                CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(result.addJsonKeyValueEdit(nonRegisteredInfo, "NRC")));
                if(nonRegisteredInfo.has("memberInfo")){
                    CommonQueryExecution.executeQuery(dynamicQueryBuilder.getInsertQuery(result.addJsonKeyValueEdit(nonRegisteredInfo, "NRCEXTENSION")));
                }
            }

            JSONObject searchInfo = new JSONObject();
            searchInfo.put("providerid", nonRegisteredInfo.getString("providerid"));
            searchInfo.put("sStr", nonRegisteredInfo.getString("generatedId"));
            searchInfo.put("sOpt", "5");
            if(nonRegisteredInfo.has("providerType")){
                searchInfo.put("providerType", nonRegisteredInfo.get("providerType"));
                searchInfo.put("zillaid", nonRegisteredInfo.get("zillaid"));
                searchInfo.put("upazilaid", nonRegisteredInfo.get("upazilaid"));
                searchInfo.put("unionid", nonRegisteredInfo.get("unionid"));
            }
            else{
                searchInfo.put("providerType", "");
                searchInfo.put("zillaid", "");
                searchInfo.put("upazilaid", "");
                searchInfo.put("unionid", "");
            }
            tempClient = ClientInfo.getDetailInfo(searchInfo);


        }
        catch(Exception e){
            e.printStackTrace();
        }
        return tempClient;
    }
}