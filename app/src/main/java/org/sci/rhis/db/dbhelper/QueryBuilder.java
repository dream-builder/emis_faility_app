package org.sci.rhis.db.dbhelper;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since December, 2016
 */
public class QueryBuilder{

    @SuppressWarnings("unchecked")
    public String getUpdateQuery(JSONObject requestJson) throws JSONException{

        JSONObject detailJson = new DBTableInfoHandler(requestJson.getString("serviceName"),"update").getDetail();
        String query = "UPDATE \"" + detailJson.getString("table")+ "\" SET ";

        Iterator<String> keys = detailJson.getJSONObject("keyMapField").keys();

        if(!requestJson.has("treatment_key")){
            requestJson.put("treatment_key","");
        }

        while(keys.hasNext()){
            String key = keys.next();
            if(requestJson.has(key)){
                if(requestJson.getString("treatment_key").equals(key)){
                    if(!requestJson.getString("treatment").contains("_")){
                        query = query + getPartialQuery(detailJson.getJSONObject("keyMapField").getJSONObject(key), requestJson.getString(key)) + ",";
                    }
                }
                else{
                    query = query + getPartialQuery(detailJson.getJSONObject("keyMapField").getJSONObject(key), requestJson.getString(key)) + ",";
                }
            }
        }
        query = query.substring(0, query.length()-1);
        query = query + " WHERE " + getConditionString(detailJson, requestJson);

        System.out.println("###\n" + requestJson.getString("serviceName") + " - Update Query: \n" + query + "\n###");
        return query;
    }

    @SuppressWarnings("unchecked")
    public String getInsertQuery(JSONObject requestJson) throws JSONException{

        JSONObject detailJson = new DBTableInfoHandler(requestJson.getString("serviceName"),"insert").getDetail();
        String query = "INSERT INTO \"" + detailJson.getString("table")+ "\" (";
        String columnName = "", columnValues = "";

        Iterator<String> keys = detailJson.getJSONObject("keyMapField").keys();

        while(keys.hasNext()){
            String key = keys.next();
            if(requestJson.has(key)){
                columnName = columnName + "\"" + detailJson.getJSONObject("keyMapField").getJSONObject(key).getString("dbColumnName") + "\"," ;
                if(key.equals(requestJson.has("incrementalField")?(requestJson.getString("incrementalField")):"none")){
                    columnValues = columnValues + "(SELECT COUNT(\"" + detailJson.getJSONObject("keyMapField").getJSONObject(key).getString("dbColumnName")
                            + "\") FROM \"" + detailJson.getString("table")+ "\" WHERE "
                            + getConditionString(detailJson, requestJson) + ") + 1,";
                }
                else{
                    columnValues = columnValues + getInsertValues(detailJson.getJSONObject("keyMapField").getJSONObject(key), requestJson.getString(key)) + ",";
                }
            }
        }
        columnName = columnName.substring(0, columnName.length()-1);
        columnValues = columnValues.substring(0, columnValues.length()-1);

        if(requestJson.has("special")){
            switch(requestJson.getInt("special")){
                case 1:
                    query = query + columnName + ") SELECT " + columnValues;
                    break;
            }
        }
        else{
            query = query + columnName + ") VALUES (" + columnValues + ")";
        }

        System.out.println("###\n" + requestJson.getString("serviceName") + " - Insert Query: \n" + query + "\n###");
        return query;
    }

    public String getUpsertQuery(JSONObject requestJson, String serviceName) throws JSONException{
        String query = "";
        requestJson.put("special", 1);
        query = "WITH upsert AS ("
                + getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(requestJson, serviceName))
                + " RETURNING *), INSERTED AS ("
                + getInsertQuery(new JsonHandler().addJsonKeyValueEdit(requestJson, serviceName))
                + " WHERE NOT EXISTS (SELECT * FROM upsert) RETURNING *) "
                + "SELECT * FROM UPSERT "
                + "UNION ALL "
                + "SELECT * FROM INSERTED";
        requestJson.put("special", null);
        return query;
    }

    @SuppressWarnings("unchecked")
    public String getOnConflictInsertQuery(JSONObject requestJson) throws JSONException{

        JSONObject detailJson = new DBTableInfoHandler(requestJson.getString("serviceName"),"insert").getDetail();
        String columnName = "", columnValues = "";

        Iterator<String> keys = detailJson.getJSONObject("keyMapField").keys();

        while(keys.hasNext()){
            String key = keys.next();
            if(requestJson.has(key)){
                columnName = columnName + "\"" + detailJson.getJSONObject("keyMapField").getJSONObject(key).getString("dbColumnName") + "\"," ;
                columnValues = columnValues + getInsertValues(detailJson.getJSONObject("keyMapField").getJSONObject(key), requestJson.getString(key)) + ",";
            }
        }
        columnName = columnName.substring(0, columnName.length()-1);
        columnValues = columnValues.substring(0, columnValues.length()-1);

        String insertQuery = "INSERT INTO \"" + detailJson.getString("table")+ "\" ("
                + columnName + ") SELECT " + columnValues + " WHERE NOT EXISTS "
                + "(SELECT 1 FROM s)";


        String query = "WITH s AS (" + getSelectQuery(requestJson) + "), "
                + "i AS (" + insertQuery + ") "
                + "SELECT * FROM i "
                + "UNION ALL "
                + "SELECT * FROM s";

        System.out.println("###\n" + requestJson.getString("serviceName") + " - Insert Query: \n" + query + "\n###");
        return query;
    }


    public String getSelectQuery(JSONObject requestJson) throws JSONException{

        JSONObject detailJson = new DBTableInfoHandler(requestJson.getString("serviceName"),"retrieve").getDetail();

        String query = "SELECT * FROM \"" + detailJson.getString("table")+ "\" WHERE " + getConditionString(detailJson, requestJson);

        System.out.println("###\n" + requestJson.getString("serviceName") + " - Select Query: \n" + query + "\n###");
        return query;
    }

    public String getMultiInsertQuery(JSONObject requestJson) throws JSONException{

        JSONObject detailJson = new DBTableInfoHandler(requestJson.getString("serviceName"),"insert").getDetail();
        String query = "INSERT INTO \"" + detailJson.getString("table")+ "\" (";
        String columnName = "";

        for(String key : detailJson.getString("fields").split(",")){
            columnName = columnName + "\"" + detailJson.getJSONObject("keyMapField").getJSONObject(key).getString("dbColumnName") + "\"," ;
        }
        columnName = columnName.substring(0, columnName.length()-1);

        query = query + columnName + ") VALUES ";

        System.out.println("###\n" + requestJson.getString("serviceName") + " - Multi-insert Query: \n" + query + "\n###");
        return query;
    }

    public String getDeleteQuery(JSONObject requestJson) throws JSONException{

        JSONObject detailJson = new DBTableInfoHandler(requestJson.getString("serviceName"),"delete").getDetail();
        String query = "DELETE FROM \"" + detailJson.getString("table")+ "\" WHERE ";
        String conditionString = "";
        String maxConditionString = "";
        if(!requestJson.has("maxField")){
            requestJson.put("maxField","");
        }

        for(String individualCondition:detailJson.getString("condition").split(",")){
            if(!requestJson.get("maxField").equals(individualCondition)){
                conditionString = conditionString + getPartialQuery(detailJson.getJSONObject("keyMapCondition").getJSONObject(individualCondition),requestJson.getString(individualCondition));
                conditionString = conditionString + " AND ";
            }
        }

        if(!requestJson.getString("maxField").equals("") && !conditionString.equals("")){
            maxConditionString = "\"" + detailJson.getJSONObject("keyMapCondition").getJSONObject(requestJson.getString("maxField")).getString("dbColumnName") + "\" = (";
            maxConditionString = maxConditionString + "SELECT MAX(\"" + detailJson.getJSONObject("keyMapCondition").getJSONObject(requestJson.getString("maxField")).getString("dbColumnName") + "\") "
                    + "FROM \"" + detailJson.getString("table")+ "\" WHERE ";
            maxConditionString = maxConditionString + conditionString.substring(0, conditionString.length()-5);
            maxConditionString = maxConditionString + ")";

            conditionString = conditionString + maxConditionString;
        }else{
            conditionString = conditionString + conditionString.substring(0, conditionString.length()-5);
        }

        query = query + conditionString;

        System.out.println("###\n" + requestJson.getString("serviceName") + " - Delete Query: \n" + query + "\n###");
        return query;
    }

    private String getInsertValues(JSONObject keyDetail, String val) throws JSONException{
        return this.getValues(keyDetail, val);
    }

    private String getPartialQuery(JSONObject keyDetail, String val) throws JSONException{
        String queryLine = "";

        queryLine = queryLine + "\"" + keyDetail.getString("dbColumnName") + "\"=";
        queryLine = queryLine + this.getValues(keyDetail, val);

        return queryLine;
    }

    private String getConditionString(JSONObject condition,JSONObject requestJson) throws JSONException{
        String conditionString = "";

        for(String individualCondition:condition.getString("condition").split(",")){
            conditionString = conditionString + getPartialQuery(condition.getJSONObject("keyMapCondition").getJSONObject(individualCondition),requestJson.getString(individualCondition));
            conditionString = conditionString + " AND ";
        }
        return conditionString.substring(0, conditionString.length()-5);
    }

    private String getValues(JSONObject keyDetail, String val) throws JSONException{
        String queryLine = "";

        String columnVal = keyDetail.getString("dbColumnType").equals("int") ? val : ("'" + val + "'");
        queryLine = queryLine + (val.equals("") ? keyDetail.get("dbColumnDefaultValue") : columnVal);

        return queryLine;
    }

    public String getTableWithoutQuote(String service){
        return PropertiesInfo.SERVICE_JSON_PROPERTIES.getProperty(service + "_table");
    }

    public String getTable(String service){
        String tableName = "\"" + PropertiesInfo.SERVICE_JSON_PROPERTIES.getProperty(service + "_table") + "\"";
        return tableName;
    }

    public String getColumn(String field){
        return PropertiesInfo.SERVICE_JSON_MAP_PROPERTIES.getProperty(field).split(",")[0];
    }

    public String getColumn(String alias, String field){
        String columnName = "";
        String tableNameIni = "";
        String[] fieldDetail = PropertiesInfo.SERVICE_JSON_MAP_PROPERTIES.getProperty(field).split(",");
        switch(alias){
            case "table":
                if(field.split("_").length > 2){
                    for(int i = 0; i < (field.split("_").length-1); i++){
                        tableNameIni = tableNameIni + field.split("_")[i] + "_";
                    }
                    tableNameIni = tableNameIni.substring(0, tableNameIni.length()-1);
                }
                else{
                    tableNameIni = field.split("_")[0];
                }
                columnName = getTable(tableNameIni) + ".\"" + fieldDetail[0] + "\"";
                break;
            case "":
                columnName = "\"" + fieldDetail[0] + "\"";
                break;
            default:
                columnName = "\"" + alias + "\".\"" + fieldDetail[0] + "\"";
                break;
        }
        return columnName;
    }

    public String getColumn(String alias, String field, String[] value, String condition){
        String columnName = "";
        String[] fieldDetail = PropertiesInfo.SERVICE_JSON_MAP_PROPERTIES.getProperty(field).split(",");
        switch(condition){
            case "=":
                columnName = getColumn(alias, field) + " = " + getValue(fieldDetail[1],value[0]);
                break;
            case "!=":
                columnName = getColumn(alias, field) + " != " + getValue(fieldDetail[1],value[0]);
                break;
            case ">":
                columnName = getColumn(alias, field) + " > " + getValue(fieldDetail[1],value[0]);
                break;
            case ">=":
                columnName = getColumn(alias, field) + " >= " + getValue(fieldDetail[1],value[0]);
                break;
            case "isnull":
                columnName = getColumn(alias, field) + " IS NULL";
                break;
            case "isnotnull":
                columnName = getColumn(alias, field) + " IS NOT NULL";
                break;
            case "likeafter":
                columnName = getColumn(alias, field) + " LIKE " + getValue(fieldDetail[1],value[0] + "%");
                break;
            case "likeboth":
                columnName = getColumn(alias, field) + " LIKE " + getValue(fieldDetail[1], "%" + value[0] + "%");
                break;
            case "between":
                columnName = getColumn(alias, field) + " BETWEEN " + getValue(fieldDetail[1],value[0]) + " AND " + getValue(fieldDetail[1],value[1]);
                break;
            case "in":
                columnName = getColumn(alias, field) + " IN (";
                for(String eachVal : value){
                    columnName = columnName + getValue(fieldDetail[1],eachVal) + ",";
                }
                columnName = columnName.substring(0, columnName.length()-1) + ")";
                break;
            case "notin":
                columnName = getColumn(alias, field) + " NOT IN (";
                for(String eachVal : value){
                    columnName = columnName + getValue(fieldDetail[1],eachVal) + ",";
                }
                columnName = columnName.substring(0, columnName.length()-1) + ")";
                break;
        }
        return columnName;
    }

    public String getPartialCondition(String alias1, String field1, String alias2, String field2, String condition){
        String columnName = "";

        switch(condition){
            case "=":
                columnName = getColumn(alias1, field1) + " = " + getColumn(alias2, field2);
                break;
            case "<>":
                columnName = getColumn(alias1, field1) + " <> " + getColumn(alias2, field2);
                break;
        }
        return columnName;
    }

    private String getValue(String type, String value){
        String val = type.equals("String")?("'" + value + "'"):value;
        return val;
    }
}