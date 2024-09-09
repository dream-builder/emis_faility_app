package org.sci.rhis.db.permanent_method;

import android.util.Log;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CreateRegNo;
import org.sci.rhis.db.dbhelper.JSONKeyMapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.permanent_method.followup.DeletePMSFollowupInfo;
import org.sci.rhis.db.permanent_method.followup.InsertPMSFollowupInfo;
import org.sci.rhis.db.permanent_method.followup.RetrievePMSFollowupInfo;
import org.sci.rhis.db.permanent_method.followup.UpdatePMSFollowupInfo;
import org.sci.rhis.utilities.JsonHandler;


public class PMInfo {


    final static int pms = 8; //for registration number
    final static int PMSSERVICTYPE = 14; //for item distribution
    final static int PMSFOLLOWUPSERVICTYPE = 15; //for item distribution

    public static JSONObject getDetailInfo(JSONObject pmsInfo) {

        JSONObject pmsInformation = new JSONObject();

        QueryBuilder dynamicQueryBuilder = new QueryBuilder();


        try{
            pmsInformation = new JSONObject();
            pmsInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(pmsInfo, "PMS"), PMSSERVICTYPE);
            pmsInfo.put("serviceCategory", pms);

            if(pmsInfo.get("pmsLoad").equals("insert")){
                pmsInformation = InsertPMInfo.createPMS(pmsInfo, pmsInformation, dynamicQueryBuilder);
                if(pmsInformation.getString("pmsInsertSuccess").equals("1")){
                    CreateRegNo.pushReg(pmsInfo, pmsInformation);
                }
            }
            else if(pmsInfo.get("pmsLoad").equals("update")){
                pmsInformation = UpdatePMInfo.updatePM(pmsInfo, pmsInformation, dynamicQueryBuilder);
            }
            else if(pmsInfo.get("pmsLoad").equals("retrieve")){
                pmsInformation = RetrivePMInfo.getPM(pmsInfo, pmsInformation, dynamicQueryBuilder);
            }
            else if(pmsInfo.get("pmsLoad").equals("")){
                pmsInformation.put("pmsCount", pmsInfo.getString("pmsCount"));
                pmsInfo.put("serviceType", PMSFOLLOWUPSERVICTYPE);

                if(pmsInfo.get("pmsFollowupLoad").equals("insert")){
                    InsertPMSFollowupInfo.createPMSFollowup(pmsInfo, pmsInformation, dynamicQueryBuilder);
                }
                else if(pmsInfo.get("pmsFollowupLoad").equals("update")){
                    UpdatePMSFollowupInfo.updatePMSFollowup(pmsInfo, pmsInformation, dynamicQueryBuilder);
                }
                else if(pmsInfo.get("pmsFollowupLoad").equals("delete")){
                    DeletePMSFollowupInfo.deletePMSFollowup(pmsInfo, pmsInformation, dynamicQueryBuilder);
                }
            }

            if(!pmsInformation.getString("pmsCount").equals("")){
                pmsInformation = RetrievePMSFollowupInfo.getPMSFollowup(pmsInfo, pmsInformation, dynamicQueryBuilder);
            }

            pmsInformation = ClientInfoUtil.getRegNumber(pmsInfo, pmsInformation);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return pmsInformation;
    }

}
