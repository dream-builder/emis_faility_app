package org.sci.rhis.db.dbhelper;


import org.json.JSONObject;
import org.sci.rhis.utilities.Utilities;

/**
 * @author sabah.mugab
 * @since March, 2016
 */
public class HandleStockDistribution {

	public static boolean insertDistributionInfo(JSONObject distributionInfo, QueryBuilder dynamicQueryBuilder) {

		boolean status = false;

		try{
			String sql = "INSERT INTO " + dynamicQueryBuilder.getTable("ITEMDISTRIBUTION")
					+ " (" + dynamicQueryBuilder.getColumn("", "ITEMDISTRIBUTION_healthid") + ","
					+ dynamicQueryBuilder.getColumn("", "ITEMDISTRIBUTION_providerid") + ","
					+ dynamicQueryBuilder.getColumn("", "ITEMDISTRIBUTION_itemcode") + ","
					+ dynamicQueryBuilder.getColumn("", "ITEMDISTRIBUTION_itemqty") + ","
					+ dynamicQueryBuilder.getColumn("", "ITEMDISTRIBUTION_source") + ","
					+ dynamicQueryBuilder.getColumn("", "ITEMDISTRIBUTION_distributionid") + ","
					+ dynamicQueryBuilder.getColumn("", "ITEMDISTRIBUTION_servicetype") + ","
					+ dynamicQueryBuilder.getColumn("", "ITEMDISTRIBUTION_systemEntryDate") + ","
					+ dynamicQueryBuilder.getColumn("", "ITEMDISTRIBUTION_modifyDate") + ") VALUES ";

			for(String singleItem : distributionInfo.getString("treatment").replaceAll("\\[|\\]","").replaceAll("\"", "").split(",")){
				sql = sql + "(" + distributionInfo.get("healthId") + ","
						+ distributionInfo.get("providerId") + ","
						+ singleItem.split("_")[0] + ","
						+ singleItem.split("_")[1].split(":")[1] + ","
						+ singleItem.split("_")[1].split(":")[0] + ","
						+ distributionInfo.get("distributionId") + ","
						+ distributionInfo.get("serviceType") + ","
						+ "'" + Utilities.getDateStringDBFormat() + "',"
						+ "'" + Utilities.getDateStringDBFormat() + "')," ;
			}

			sql = sql.substring(0, sql.length() - 1);
			CommonQueryExecution.executeQuery(sql);
			status=true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return status;
	}

	public static String getDistributionInfo(JSONObject distributionInfo, QueryBuilder dynamicQueryBuilder) {

		String treatmentDetail = "";

		try{
			String sql = "SELECT " + dynamicQueryBuilder.getTable("ITEMDISTRIBUTION") + ".*"
					+ " FROM " + dynamicQueryBuilder.getTable("ITEMDISTRIBUTION")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "ITEMDISTRIBUTION_healthid",new String[]{distributionInfo.getString("healthId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "ITEMDISTRIBUTION_distributionid",new String[]{distributionInfo.getString("distributionId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "ITEMDISTRIBUTION_servicetype",new String[]{distributionInfo.getString("serviceType")},"=");

			SimpleCursor rs = new SimpleCursor(DatabaseWrapper.getDatabase().rawQuery(sql,null));

			while(rs.next()){
				treatmentDetail = treatmentDetail + "\"";
				treatmentDetail = treatmentDetail + rs.getString(dynamicQueryBuilder.getColumn("ITEMDISTRIBUTION_itemcode"))
						+ "_" + rs.getString(dynamicQueryBuilder.getColumn("ITEMDISTRIBUTION_source")) + ":"
						+ rs.getString(dynamicQueryBuilder.getColumn("ITEMDISTRIBUTION_itemqty"));
				treatmentDetail = treatmentDetail + "\",";
			}
			treatmentDetail = (treatmentDetail.equals("") ? "" : ("[".concat(treatmentDetail.substring(0, treatmentDetail.length() - 1)).concat("]")));

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return treatmentDetail;
	}

	public static void deleteDistributionInfo(JSONObject distributionInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			String sql = "DELETE FROM " + dynamicQueryBuilder.getTable("ITEMDISTRIBUTION")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "ITEMDISTRIBUTION_healthid",new String[]{distributionInfo.getString("healthId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "ITEMDISTRIBUTION_distributionid",new String[]{distributionInfo.getString("distributionId")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "ITEMDISTRIBUTION_servicetype",new String[]{distributionInfo.getString("serviceType")},"=");

			CommonQueryExecution.executeQuery(sql);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void updateDistributionInfo(JSONObject distributionInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			deleteDistributionInfo(distributionInfo, dynamicQueryBuilder);
			insertDistributionInfo(distributionInfo, dynamicQueryBuilder);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}