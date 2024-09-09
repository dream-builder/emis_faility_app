package org.sci.rhis.db.dbhelper;

import org.json.JSONObject;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.JsonHandler;

/**
 * Created by arafat.hasan on 3/4/2018.
 */

public class StockDistributionRequest extends HandleStockDistribution{

    public static boolean insertDistributionInfoHandler(boolean status, JSONObject distributionInfo,
                                                        QueryBuilder dynamicQueryBuilder){

        try{
            if(status && !distributionInfo.getString("treatment").equals("") && distributionInfo.getString("treatment").contains("_")){
                insertDistributionInfo(distributionInfo, dynamicQueryBuilder);
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static JSONObject insertDistributionInfoHandler(SimpleCursor rs, String columnName,
                                                           JSONObject distributionInfo, QueryBuilder dynamicQueryBuilder){

        try{
            if(rs.next()){
                distributionInfo.put("distributionId",new JsonHandler().getResultSetValue(rs,columnName));
                if(!distributionInfo.getString("treatment").equals("") && distributionInfo.getString("treatment").contains("_")){
                    insertDistributionInfo(distributionInfo, dynamicQueryBuilder);
                }
            }
            else{
                distributionInfo.put("serviceId","");
            }
            return distributionInfo;
        }
        catch (Exception e){
            e.printStackTrace();
            return distributionInfo;
        }
    }

    public static boolean updateDistributionInfoHandler(SimpleCursor rs, String columnName, JSONObject distributionInfo,
                                                        QueryBuilder dynamicQueryBuilder){

        try{
            if(rs.next() && !distributionInfo.getString("treatment").equals("") && distributionInfo.getString("treatment").contains("_")){
                distributionInfo.put("distributionId", new JsonHandler().getResultSetValue(rs,columnName));
                updateDistributionInfo(distributionInfo, dynamicQueryBuilder);
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean upsertDistributionInfoHandler(SimpleCursor rs, String columnName,
                                                        JSONObject distributionInfo, QueryBuilder dynamicQueryBuilder){

        try{
            if(rs.next() && !distributionInfo.getString("treatment").equals("") && distributionInfo.getString("treatment").contains("_")){
                distributionInfo.put("distributionId", new JsonHandler().getResultSetValue(rs,columnName));
                if(getDistributionInfo(distributionInfo, dynamicQueryBuilder).equals("")){
                    insertDistributionInfo(distributionInfo, dynamicQueryBuilder);
                }
                else{
                    updateDistributionInfo(distributionInfo, dynamicQueryBuilder);
                }
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteDistributionInfoHandler(SimpleCursor rs, String columnName,
                                                        JSONObject distributionInfo, QueryBuilder dynamicQueryBuilder){

        try{
            if(rs.next()){
                distributionInfo.put("distributionId",new JsonHandler().getResultSetValue(rs,columnName));
                if(!distributionInfo.getString("distributionId").equals("") && !distributionInfo.getString("distributionId").startsWith("[")){
                    deleteDistributionInfo(distributionInfo, dynamicQueryBuilder);
                }
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
