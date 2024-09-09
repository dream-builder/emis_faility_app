package org.sci.rhis.db.pillcondom;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CreateRegNo;
import org.sci.rhis.db.dbhelper.QueryBuilder;

/**
 * @author sabah.mugab
 * @since January, 2016
 */

public class PillCondomService {

	static final int pillCondom = 2; //for registration number
	static final int FPType = 1; //for FPExamination

	public static JSONObject getDetailInfo(JSONObject pillCondomInfo) {

		JSONObject pillCondomVisits = new JSONObject();
		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		try {
			pillCondomInfo.put("serviceCategory", pillCondom);
			pillCondomInfo.put("FPType", FPType);

			if (pillCondomInfo.get("pillCondomLoad").equals("")) {
				pillCondomInfo = InsertPillCondomVisitInfo.createPillCondomVisit(pillCondomInfo, dynamicQueryBuilder);
				if (pillCondomInfo.getString("serviceId").equals("1")) {
					CreateRegNo.pushReg(pillCondomInfo, pillCondomVisits);
				}
			} else if (pillCondomInfo.get("pillCondomLoad").equals("update")) {
				UpdatePillCondomVisitInfo.updatePillCondomVisit(pillCondomInfo, dynamicQueryBuilder);
			} else if (pillCondomInfo.get("pillCondomLoad").equals("delete")) {
				DeletePillCondomVisitInfo.deletePillCondomVisit(pillCondomInfo, dynamicQueryBuilder);
			}
			pillCondomVisits = RetrievePillCondomVisitInfo.getPillCondomVisits(pillCondomInfo, pillCondomVisits, dynamicQueryBuilder);
			pillCondomVisits = ClientInfoUtil.getRegNumber(pillCondomInfo, pillCondomVisits);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pillCondomVisits;
	}
}