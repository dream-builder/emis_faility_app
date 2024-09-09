package org.sci.rhis.db.womaninjectable;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CreateRegNo;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.JSONKeyMapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since February, 2016
 */
public class WomanInjectableService {

	final static int injectables = 3; //for registration number
	final static int FPType = 2; //for FPExamination
	final static int WOMENINJECTBLESERVICETYPE = 8; //for item distribution

	public static JSONObject getDetailInfo(JSONObject injectableInfo) {

		JSONObject injectableVisits = new JSONObject();

		
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		try{
            injectableInfo = new JsonHandler().addJsonKeyValueStockDistribution(new JSONKeyMapper().setRequiredKeys(injectableInfo, "FPEXAMINATION_WOMANINJECTABLE"), WOMENINJECTBLESERVICETYPE);
            injectableInfo.put("serviceCategory", injectables);
            injectableInfo.put("FPType", FPType);

			if(injectableInfo.get("injectableLoad").equals("")){
				injectableInfo = InsertInjectableVisitInfo.createInjectableVisit(injectableInfo, dynamicQueryBuilder);
				if(injectableInfo.getString("serviceId").equals("1")){
					CreateRegNo.pushReg(injectableInfo, injectableVisits);
				}
			}
			else if(injectableInfo.get("injectableLoad").equals("update")){
				UpdateInjectableVisitInfo.updateInjectableVisit(injectableInfo, dynamicQueryBuilder);
			}
			else if(injectableInfo.get("injectableLoad").equals("delete")){
				DeleteInjectableVisitInfo.deleteInjectableVisit(injectableInfo, dynamicQueryBuilder);
			}
			injectableVisits = RetrieveInjectableVisitInfo.getInjectableVisits(injectableInfo,injectableVisits, dynamicQueryBuilder);
			injectableVisits = ClientInfoUtil.getRegNumber(injectableInfo, injectableVisits);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return injectableVisits;
	}
}