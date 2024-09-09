package org.sci.rhis.db.distribution;

import android.database.Cursor;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import org.sci.rhis.utilities.Utilities;

import java.util.Calendar;
import java.util.Date;

public class RetrieveDistributions {

    public static String getDistributionData(JSONObject passedData, JSONObject distributionData) {

        try {
            String startDate, endDate;
            //initialize start and end date
            if (!passedData.has("start_date") || !passedData.has("end_date")) {
                Calendar cal = Calendar.getInstance();
                String currentYearMonth = new CustomSimpleDateFormat(Constants.SHORT_HYPHEN_FORMAT_YEAR_MONTH).format(new Date());

                startDate = currentYearMonth + "-01";
                endDate = currentYearMonth + "-" + cal.getActualMaximum(Calendar.DATE);
            } else {
                startDate = passedData.getString("start_date");
                endDate = passedData.getString("end_date");
            }

            String sql = "SELECT strftime('%Y-%m-%d', dd.service_entry_datetime) formatted_date, tl.detail item_name, SUM(dd.itemqty) quantity FROM distribution_detail dd" +
                    " INNER JOIN treatmentlist_master tl ON dd.itemcode = tl.id" +
                    " WHERE formatted_date BETWEEN '" + startDate + "' AND '" + endDate + "'" +
                    " GROUP BY dd.itemcode;";

            Cursor cursor = CommonQueryExecution.executeQueryForCursor(sql);

            JSONObject jsonDataObject = new JSONObject();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("item_name", cursor.getString(cursor.getColumnIndex("item_name")));
                    jsonObject.put("quantity", cursor.getString(cursor.getColumnIndex("quantity")));

                    jsonDataObject.put(String.valueOf(cursor.getPosition()), jsonObject);
                }
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
            distributionData.put("data", jsonDataObject);
            distributionData.put("result_for",  Utilities.convertEnglishDateToBengali(startDate) + " - " + Utilities.convertEnglishDateToBengali(endDate));

            return distributionData.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}