package org.sci.rhis.utilities;

import android.content.Context;
import android.util.Log;

import org.sci.rhis.utilities.CustomSimpleDateFormat;

/**
 * Created by jamil.zaman on 15/02/16.
 */
public class DateValue extends DisplayValue{


    private CustomSimpleDateFormat dbDateFormat;
    private CustomSimpleDateFormat uiDateFormat;

    public DateValue(Context context, String key, String value, String displayLabel, String dbFormat, String uiFormat) {
        super(context, key, value, displayLabel);
        this.dbDateFormat = new CustomSimpleDateFormat(dbFormat);
        this.uiDateFormat = new CustomSimpleDateFormat(uiFormat);
    }

    @Override
    public String toString() {
        String result = displayLabel;

        if (!value.isEmpty()) {
            try {
                result += " " + uiDateFormat.format(dbDateFormat.parse(value));
            } catch (NumberFormatException nfe) {
                Log.e(LOGTAG, "Invalid date: \'" + value +"\'");
                result = "";
            } catch (Exception e) {
                Log.e(LOGTAG,"Exception: " +e.getMessage()+ " \n\t :: ->");
                Utilities.printTrace(e.getStackTrace(), 10);
            }
        }
        //TODO - Prone to throw Number Format Exception
        return result;
    }
}

