package org.sci.rhis.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by arafat.hasan on 2/11/2016.
 */
public class Converter {
    public static Date stringToDate(String dateFormat, String date) throws ParseException {
        DateFormat sdf = new CustomSimpleDateFormat(dateFormat);
        Date convertedDate = sdf.parse(date);
        return convertedDate;
    }

    public static String dateToString(String desiredFormat, Date date){
        DateFormat sdf = new CustomSimpleDateFormat(desiredFormat);
        String dateAsString = sdf.format(date);
        return dateAsString;
    }

    public static String convertSdfFormat(String from, String inputDate, String to) throws ParseException{
        String convertedString = inputDate;
        CustomSimpleDateFormat inputFormat = new CustomSimpleDateFormat(from);
        CustomSimpleDateFormat outputFormat = new CustomSimpleDateFormat(to);
        try {
            Date date = inputFormat.parse(inputDate);
            convertedString = outputFormat.format(date);
        } catch (ParseException pe) {
            Log.e("CONVERTER-DATE", String.format("ERROR: %s, from format: %s, to format %s", pe.getMessage(),from, to));
            Utilities.printTrace(pe.getStackTrace(), 10);
        }
        return convertedString;
    }

    /**
     * @param year,month,desiredFormat
     * @return endDateAsString by given month and year
     */
    public static String getEndDateAsString(int year, int month, String desiredFormat){
        Calendar c = Calendar.getInstance();
        c.set(year,month,1);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        CustomSimpleDateFormat sdf = new CustomSimpleDateFormat(desiredFormat);
        String endDate = sdf.format(c.getTime()).toString();

        return endDate;
    }

    /**
     * @param year,month,desiredFormat
     * @return firstDateAsString by given month and year
     */
    public static String getFirstDateAsString(int year, int month, String desiredFormat){
        Calendar c = Calendar.getInstance();
        c.set(year,month,1);
        CustomSimpleDateFormat sdf = new CustomSimpleDateFormat(desiredFormat);
        String firstDate = sdf.format(c.getTime()).toString();

        return firstDate;
    }

    /**
     * @param age,desiredFormat
     * @return date of birth assuming birth date & month is current date & month
     */
    public static String dobFromAge(int age, String desiredFormat){
        Calendar c = Calendar.getInstance();
        int birthYear = c.get(Calendar.YEAR)-age;
        c.set(birthYear,c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        CustomSimpleDateFormat sdf = new CustomSimpleDateFormat(desiredFormat);
        String dob = sdf.format(c.getTime()).toString();

        return dob;
    }

    /**
     * returns the date of birth as String from provided integer values of day,month and year
     * @param day,month,year,desiredFormat
     * @return date of birth
     */
    public static String dateOfBirthFromAge(int day, int month, int year, String desiredFormat)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, year * -1);
        cal.add(Calendar.MONTH, month * -1);
        cal.add(Calendar.DAY_OF_YEAR, day * -1);
        CustomSimpleDateFormat sdf = new CustomSimpleDateFormat(desiredFormat);
        String dob = sdf.format(cal.getTime()).toString();
        return dob;
    }

    public static int[] ageFromDOB(Date dateDOB)

    {
        int result[] = new int[3];
        int years = 0;
        int months = 0;
        int days = 0;
        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(dateDOB.getTime());
        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }else{
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        result[0] = days;
        result[1] = months;
        result[2] = years;
        return result;
    }


    /**
     *
     * @param key means the column name of text typed column in a single jsonObject
     * @return String ArrayList
     */
    public static ArrayList<String> StringArrayListFromJSONArray(JSONArray sourceArray, String key){
        ArrayList<String> resultList = new ArrayList<>();
        if(sourceArray !=null){
            for (int i=0; i<sourceArray.length(); i++){
                try {
                    resultList.add(sourceArray.getJSONObject(i).getString(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else{
            resultList.add("");
        }
        return resultList;
    }

    /**
     * convert any farenhite value to celsius value
     * @param farenhiteVal
     * @return converted celsius value
     */
    public static double farenhiteToCelsius(double farenhiteVal){
        return ((farenhiteVal -32)*(5/9));
    }

    /**
     * convert any celsius value to farenhite value
     * @param celsiusVal
     * @return
     */
    public static double celsiusToFarenhite(double celsiusVal){
        return (celsiusVal*1.8)+32;
    }

    /**
     * convert any kg value to gram value
     * @param kgVal
     * @return
     */
    public static int kgToGram(double kgVal){
        return (int) (kgVal*1000);
    }
}
