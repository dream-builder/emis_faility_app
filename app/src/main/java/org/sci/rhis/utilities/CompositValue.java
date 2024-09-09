package org.sci.rhis.utilities;

import android.content.Context;
import android.util.Log;

/**
 * Created by jamil.zaman on 15/02/16.
 */
public class CompositValue extends DisplayValue{

    protected String secondValue;
    protected String connectorString;

    public CompositValue(Context context, String key, String value, String displayLabel, String secondValue, String connectorString) {
        super(context, key, value, displayLabel);
        this.secondValue = secondValue;
        this.connectorString = connectorString;
    }

    public CompositValue(Context context, String key, String value, String displayLabel, String secondValue, String connectorString, Integer[] values) {
        super(context, key, value, displayLabel, values);
        this.secondValue = secondValue;
        this.connectorString = connectorString;
    }

    public String getSecondValue() {
        return secondValue;
    }

    public boolean isDangerous() {
        try {
            if(lowerThreshhold == upperThreshhold && lowerThreshhold == 0) { //threshold not applicable
                return false;
            }
            return (!value.equals("") && !secondValue.equals("") && (Integer.valueOf(value) > lowerThreshhold || Integer.valueOf(secondValue) > upperThreshhold));
        } catch(Exception e) {
            Log.d(LOGTAG, String.format("Error:\t%s", e.getMessage()));
            Utilities.printTrace(e.getStackTrace(), 10);
        }
        return false;
    }

    @Override
    public String toString() {
        String result = displayLabel + " " + getValue() + connectorString + secondValue;


        return result;
    }
}

