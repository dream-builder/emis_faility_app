package org.sci.rhis.utilities;

import android.content.Context;
import android.util.Log;

/**
 * Created by jamil.zaman on 15/02/16.
 */
public class DisplayValue {
    protected String key;
    protected String value;
    protected String displayLabel;
    protected Context context;
    protected final String LOGTAG = "DISPLAY-LIST-VALUES";
    protected int lowerThreshhold;
    protected int upperThreshhold;





    public DisplayValue(Context context, String key, String value, String displayLabel) {
        this.key = key;
        this.value = value;
        this.displayLabel = displayLabel;
        this.context = context;
        this.lowerThreshhold = 0;
        this.upperThreshhold = 0;
    }

    public DisplayValue(Context context, String key, String value, String displayLabel, Integer [] arguments) {
        this.key = key;
        this.value = value;
        this.displayLabel = displayLabel;
        this.context = context;
        this.lowerThreshhold = 0;
        this.upperThreshhold = 0;
        if(arguments.length > 1) {
            lowerThreshhold = arguments[1];
        }

        if(arguments.length > 2) {
            upperThreshhold = arguments[2];
        }
    }

    public boolean isDangerous() {
        try {
            if(lowerThreshhold == upperThreshhold && lowerThreshhold == 0) { //threshold not applicable
                return false;
            }
            return (!value.equals("") && (Integer.valueOf(value) < lowerThreshhold || Integer.valueOf(value) > upperThreshhold));
        } catch(Exception e) {
            Log.d(LOGTAG, String.format("Error:\t%s", e.getMessage()));
            Utilities.printTrace(e.getStackTrace(), 10);
        }
        return false;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    @Override
    public String toString() {
        return displayLabel + " " + value;
    }
}

