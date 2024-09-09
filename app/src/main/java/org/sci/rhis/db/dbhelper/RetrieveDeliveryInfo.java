package org.sci.rhis.db.dbhelper;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.sci.rhis.utilities.JsonHandler;
import org.sci.rhis.utilities.Utilities;

/**
 * @author sabah.mugab
 * @created June, 2015
 */
public class RetrieveDeliveryInfo {

	public static JSONObject getDeliveryInfo(JSONObject deliveryInfo,
											 JSONObject deliveryInformation, QueryBuilder dynamicQueryBuilder) {
		SQLiteDatabase db = DatabaseWrapper.getDatabase();
		try{
			String sql = "SELECT " + dynamicQueryBuilder.getTable("DELIVERY") + ".*, "
					+ dynamicQueryBuilder.getColumn("table", "PREGWOMEN_lmp")
					+ " FROM " + dynamicQueryBuilder.getTable("DELIVERY") + ", " + dynamicQueryBuilder.getTable("PREGWOMEN")
					+ " WHERE " + dynamicQueryBuilder.getColumn("table", "DELIVERY_healthid",new String[]{deliveryInfo.getString("healthid")},"=")
					+ " AND " + dynamicQueryBuilder.getColumn("table", "DELIVERY_pregno",new String[]{deliveryInfo.getString("pregno")},"=")
					+ " AND " + dynamicQueryBuilder.getPartialCondition("table", "DELIVERY_healthid","table","PREGWOMEN_healthId","=")
					+ " AND " + dynamicQueryBuilder.getPartialCondition("table", "DELIVERY_pregno","table","PREGWOMEN_pregNo","=");

			SimpleCursor rs = new SimpleCursor(db.rawQuery(sql,null));
			deliveryInfo.put("distributionJson","dTreatment");
			if(rs.next()){
				deliveryInformation = new JsonHandler().getServiceDetail(rs, deliveryInfo, "DELIVERY", dynamicQueryBuilder,1);

				if(deliveryInformation.getString("dTime").split(":").length > 2){
					String time = "";
					//re-formatting the dTime value if it is not in HH:mm:ss and in date time format
					if(deliveryInformation.getString("dTime").split(" ").length>1){
						deliveryInformation.put("dTime",deliveryInformation.getString("dTime").split(" ")[1]);
					}
					if(Integer.valueOf(deliveryInformation.getString("dTime").split(":")[0]) > 12){
						time = String.valueOf(Integer.valueOf(deliveryInformation.getString("dTime").split(":")[0]) - 12)
								+ ":" + deliveryInformation.getString("dTime").split(":")[1] + " PM";
					}
					else if(Integer.valueOf(deliveryInformation.getString("dTime").split(":")[0]) == 12){
						time = (deliveryInformation.getString("dTime").split(":")[0])
								+ ":" + deliveryInformation.getString("dTime").split(":")[1] + " PM";
					}
					else if(Integer.valueOf(deliveryInformation.getString("dTime").split(":")[0]) == 00){
						time = String.valueOf(Integer.valueOf(deliveryInformation.getString("dTime").split(":")[0]) + 12)
								+ ":" + deliveryInformation.getString("dTime").split(":")[1] + " AM";
					}
					else {
						time = (deliveryInformation.getString("dTime").split(":")[0])
								+ ":" + deliveryInformation.getString("dTime").split(":")[1] + " AM";
					}
					deliveryInformation.put("dTime",time);
				}

				deliveryInformation.put("LMP",new JsonHandler().getResultSetValue(rs,dynamicQueryBuilder.getColumn("PREGWOMEN_lmp")));

				if(!deliveryInformation.get("dDate").equals("") && !deliveryInformation.get("LMP").equals("")){
					long days = Utilities.getDateDiff(rs.getDate(dynamicQueryBuilder.getColumn("PREGWOMEN_lmp")),
							rs.getDate(dynamicQueryBuilder.getColumn("DELIVERY_dDate")), TimeUnit.DAYS);
					if(days < (37*7)){
						deliveryInformation.put("immatureBirth", "1");
						deliveryInformation.put("immatureBirthWeek", (int) Math.floor(days/7));
					}
					else{
						deliveryInformation.put("immatureBirth", "2");
						deliveryInformation.put("immatureBirthWeek", (int) Math.floor(days/7));
					}
				}

				deliveryInformation.put("dNew","No");
				if(!rs.isClosed()){
					rs.close();
				}
			}
			else{
				deliveryInformation.put("dNew","Yes");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return deliveryInformation;
	}
}
