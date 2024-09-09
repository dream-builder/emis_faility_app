package org.sci.rhis.utilities;

import android.content.Context;
import android.util.Log;

import org.sci.rhis.fwc.R;

/**
 * Created by jamil.zaman on 15/02/16.
 */
public class BooleanValue extends DisplayValue{


    public BooleanValue(Context context, String key, String value, String displayLabel, Integer[] arguments) {
        super(context, key, value, displayLabel);
    }

    public BooleanValue(Context context, String key, String value, String displayLabel) {
        super(context, key, value, displayLabel);
    }

    @Override
    public String toString() {
        String result = displayLabel;

        try {
            result += " " + context.getResources().getString(value.equals("1") ? R.string.general_yes : R.string.general_no);
        } catch (Exception e) {
            Log.e(LOGTAG,"Unknown \n\t :: ->");
            Utilities.printTrace(e.getStackTrace(), 10);
        }
        //TODO - Prone to throw Number Format Exception
        return result;
    }
}

