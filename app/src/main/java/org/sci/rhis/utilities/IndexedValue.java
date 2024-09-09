package org.sci.rhis.utilities;

import android.content.Context;
import android.util.Log;

/**
 * Created by jamil.zaman on 15/02/16.
 */
public class IndexedValue extends DisplayValue{

    protected int dropdownArrayResource;

    protected IndexedValue(Context context, String key, String value, String displayLabel){
        super(context, key, value, displayLabel);
    }

    public IndexedValue(Context context, String key, String value, String displayLabel, Integer arguments[]) {
        super(context, key, value, displayLabel);
        this.dropdownArrayResource = arguments[0];
        lowerThreshhold = 0;
        upperThreshhold = 0;
        if(arguments.length > 1) {
            lowerThreshhold = arguments[1];
        }

        if(arguments.length > 2) {
            upperThreshhold = arguments[2];
        }
    } 

    public int getIndex() {
        return dropdownArrayResource;
    }
    @Override
    public boolean isDangerous() {
        try {
            if(lowerThreshhold == upperThreshhold && lowerThreshhold == 0) { //threshold not applicable
                return false;
            }
            return (!value.equals("") && (Integer.valueOf(value) > lowerThreshhold));
        } catch(Exception e) {
            Log.d(LOGTAG, String.format("Error:\t%s", e.getMessage()));
            Utilities.printTrace(e.getStackTrace(),10);
        }
        return false;
    }

    @Override
    public String toString() {
        String result = displayLabel + " ";

        try {
            if(!value.isEmpty()) {
                result += context.getResources().getStringArray(dropdownArrayResource)[Integer.parseInt(value)];
            }
        } catch (NumberFormatException nfe) {
            Log.e(LOGTAG, String.format("%s, -> Invalid index for %s: \'" + value +"\'", nfe.getMessage(), getDisplayLabel()));
            result += value.equals("") ? "" :" Invalid";
            Utilities.printTrace(nfe.getStackTrace(),10);
        } catch (ArrayIndexOutOfBoundsException aiob) {
            Log.e(LOGTAG, "Invalid index: \'" + value +"\'" + aiob.getMessage());
            result += value.equals("") ? "" :" Invalid";
            Utilities.printTrace(aiob.getStackTrace(),10);
        } catch (Exception e) {
            Log.e(LOGTAG,String.format("Unknown Value for %s\n\t :: ->%s", result, e.getMessage()));
            Utilities.printTrace(e.getStackTrace(), 10);
        }
        //TODO - Prone to throw Number Format Exception
        return result;
    }
}

