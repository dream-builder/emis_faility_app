package org.sci.rhis.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author sabah.mugab
 * @since April, 2018
 */
public class SchemaDetail {

    static JSONObject table_detail = null;
    static JSONObject service_table = null;

    public static void getServiceTableJson(String tableName, JSONObject table_detail, SQLiteDatabase db){

        try{
            String sql = "SELECT * FROM sqlite_master WHERE type='table' AND tbl_name='" + tableName + "'";
            String col = "", jsonCol = "", jsonPriCol = "";
            JSONObject jsonKey = new JSONObject();
            Cursor rs = db.rawQuery(sql, null);
            if(rs.moveToNext()){

                if(!table_detail.has(rs.getString(rs.getColumnIndex("tbl_name")))){
                    table_detail.put(rs.getString(rs.getColumnIndex("tbl_name")), new JSONObject());
                    for(String column : getColumns(rs.getString(rs.getColumnIndex("sql")))){
                        col = column.replace("\n", "");
                        if(!col.contains("PRIMARY KEY (")){
                            if(col.contains("[")){
                                jsonCol = getColumnDetail(col.split("\\[")[1].split("\\]")[0], col, jsonCol, jsonKey);
                            }
                            else if(col.contains("\"")){
                                jsonCol = getColumnDetail(col.split("\"")[1].split("\"")[0], col, jsonCol, jsonKey);
                            }
                            else{
                                jsonCol = getColumnDetail(col.trim().split(" ")[0], col.trim().split(" ")[1], jsonCol, jsonKey);
                            }

                            if(col.contains("PRIMARY KEY")){//System.out.println("Primary: " + col);
                                if(col.contains("[")){
                                    jsonPriCol = getColumnDetail(col.split("\\[")[1].split("\\]")[0], col, jsonPriCol, jsonKey);
                                }
                                else if(col.contains("\"")){
                                    jsonPriCol = getColumnDetail(col.split("\"")[1].split("\"")[0], col, jsonPriCol, jsonKey);
                                }
                                else{
                                    jsonPriCol = getColumnDetail(col.trim().split(" ")[0], col.trim().split(" ")[1], jsonPriCol, jsonKey);
                                }
                                //jsonPriCol = getColumnDetail(col.split("\"")[1].split("\"")[0], col, jsonPriCol, jsonKey);
                            }
                        }
                        else{
                            Matcher match = Pattern.compile("\\((.*?)\\)").matcher(col);
                            while(match.find()) {
                                for (String primaryColumn : match.group(1).split(",")){
                                    if(primaryColumn.contains("[")){
                                        jsonPriCol = getColumnDetail(primaryColumn.split("\\[")[1].split("\\]")[0], jsonPriCol, jsonKey);
                                    }
                                    else if(primaryColumn.contains("\"")){
                                        jsonPriCol = getColumnDetail(primaryColumn.split("\"")[1].split("\"")[0], jsonPriCol, jsonKey);
                                    }
                                    else{
                                        jsonPriCol = getColumnDetail(primaryColumn, jsonPriCol, jsonKey);
                                    }
                                }
                            }
                        }
                    }
                    table_detail.getJSONObject(rs.getString(rs.getColumnIndex("tbl_name"))).put("column", jsonCol);
                    table_detail.getJSONObject(rs.getString(rs.getColumnIndex("tbl_name"))).put("primary_key", jsonPriCol);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String getColumnDetail(String colName, String columns, JSONObject json){
        return setValue(columns,colName,json);
    }

    private static String getColumnDetail(String colName, String colDetail, String columns, JSONObject json){

        try{
            if(colDetail.toUpperCase().contains("INTEGER")||colDetail.toUpperCase().contains("BIGINT")
                    ||colDetail.toUpperCase().contains("REAL")||colDetail.toUpperCase().contains("DOUBLE")){
                columns = setValue(columns, colName, "int");
                json.put(colName, "int");
            }
            else{
                columns = setValue(columns, colName, "string");
                json.put(colName, "string");
            }
        } catch(JSONException e){
            e.printStackTrace();
        }

        return columns;
    }

    private static String setValue(String columns, String colName, JSONObject colDetail){

        try {
            if (!columns.equals("")) {
                columns = columns + "," + colName + ":" + colDetail.getString(colName);
            } else {
                columns = colName + ":" + colDetail.getString(colName);
            }

        } catch(JSONException e){
            e.printStackTrace();
        }

        return columns;
    }

    private static String setValue(String columns, String colName, String type){

        if(!columns.equals("")){
            columns = columns + "," + colName + ":" + type;
        }
        else{
            columns = colName + ":" + type;
        }
        return columns;
    }

    private static String[] getColumns(String tableDetail){
        return tableDetail.substring(tableDetail.indexOf("(")+1,tableDetail.length()-1).split("\\,(?![^\\(]*\\))");
    }
}