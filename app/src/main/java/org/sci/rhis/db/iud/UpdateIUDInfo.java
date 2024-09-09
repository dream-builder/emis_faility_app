package org.sci.rhis.db.iud;

import android.database.sqlite.SQLiteDatabase;


import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.ClientInfoUtil;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.db.dbhelper.SimpleCursor;
import org.sci.rhis.db.dbhelper.HandleStockDistribution;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class UpdateIUDInfo {

	public static JSONObject updateIUD(JSONObject iudInfo, JSONObject iudInformation,QueryBuilder dynamicQueryBuilder) {

		try{
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery(
					new JsonHandler().addJsonKeyValueEdit(iudInfo, "IUD")));

			iudInfo.put("iudUpdateSuccess","1");
			//iudInfo.put("treatment",new JsonHandler().getResultSetValue(rs,dynamicQueryBuilder.getColumn("IUD_treatment")));


			/*rs.beforeFirst();
			StockDistributionRequest.updateDistributionInfoHandler(rs, dynamicQueryBuilder.getColumn("IUD_treatment"),
					iudInfo, dbOp, dbObject, dynamicQueryBuilder);*/

			if(!iudInfo.get("mobileNo").equals("")){
				ClientInfoUtil.updateClientMobileNo(iudInfo);
			}

			iudInformation = RetrieveIUDInfo.getIUD(iudInfo, iudInformation, dynamicQueryBuilder);

			/*if(!rs.isClosed()){
				rs.close();
			}*/
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return iudInformation;
	}
}