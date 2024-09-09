package org.sci.rhis.db.implant;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class UpdateImplantInfo {

	public static JSONObject updateImplant(JSONObject implantInfo, JSONObject implantInformation,QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery((dynamicQueryBuilder.getUpdateQuery(new JsonHandler().
					addJsonKeyValueEdit(implantInfo, "IMPLANT"))));

				implantInfo.put("implantUpdateSuccess","1");
				//implantInfo.put("implantCount",getString(dynamicQueryBuilder.getColumn("IMPLANT_implantCount")));
				//implantInfo.put("treatment",new JsonHandler().getResultSetValue(rs,dynamicQueryBuilder.getColumn("IMPLANT_treatment")));

			/*StockDistributionRequest.updateDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("IMPLANT_treatment"),
					implantInfo, dbOp, dbObject, dynamicQueryBuilder);*/

			if(!implantInfo.get("mobileNo").equals("")){
				ClientInfoUtil.updateClientMobileNo(implantInfo);
			}

			implantInformation = RetrieveImplantInfo.getImplant(implantInfo, implantInformation, dynamicQueryBuilder);

//			if(!rs.isClosed()){
//				rs.close();
//			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return implantInformation;
	}
}