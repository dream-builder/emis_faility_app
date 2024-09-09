package org.sci.rhis.db.fp;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.QueryBuilder;

/**
 * @author sabah.mugab
 * @since September, 2016
 */
public class FPInfo {

	public static JSONObject getDetailInfo(JSONObject fpInfo) {

		QueryBuilder dynamicQueryBuilder = new QueryBuilder();

		try{
			JSONObject fpInformation = new JSONObject();

			if(fpInfo.get("fpInfoLoad").equals("insert")){
				fpInformation = InsertUpdateFPInfo.insertUpdateFPInfo(fpInfo, fpInformation,
						 dynamicQueryBuilder);
			}

			fpInformation = RetrieveFPInfo.getFPInfo(fpInfo, fpInformation, dynamicQueryBuilder);
			return fpInformation;
		}
		catch(Exception e){
			e.printStackTrace();
			return new JSONObject();
		}
	}
}