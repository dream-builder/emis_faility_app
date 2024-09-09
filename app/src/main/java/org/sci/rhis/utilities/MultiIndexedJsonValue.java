package org.sci.rhis.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.sci.rhis.fwc.R;

/**
 * Created by jamil.zaman on 15/02/16.
 */
public class MultiIndexedJsonValue extends IndexedValue{

    protected int dropdownArrayResource;
    private JSONArray jsonValues;

    public MultiIndexedJsonValue(Context context, String key, String value, String displayLabel, Integer [] arguments) {
        super(context, key, value, displayLabel, arguments);
        this.dropdownArrayResource = arguments[0];
    }

    public MultiIndexedJsonValue(Context context, String key, String value, String displayLabel, JSONArray values) {
        super(context, key, value, displayLabel);
        this.jsonValues = values;
    }

    @Override
    public String getValue() {
        String result = "";
        String selectedValueIndexes[] = value.replaceAll("(\"|\\[|\\])", "").split(",");

        SharedPreferences preferences = context.getSharedPreferences(String.valueOf(dropdownArrayResource), Context.MODE_PRIVATE);

        try {
            for(String value : selectedValueIndexes) {
                result += "\n" + jsonValues.getJSONObject(Integer.valueOf(value)).toString();
            }
        } catch (NumberFormatException nfe) {
            Log.e(LOGTAG, String.format("Invalid index for %s: \'" + value +"\'", getDisplayLabel()));
            result += "\n Invalid";
            Utilities.printTrace(nfe.getStackTrace(),10);
        } catch (ArrayIndexOutOfBoundsException aiob) {
            Log.e(LOGTAG, "Invalid index: \'" + value +"\'" + aiob.getMessage());
            result += "\n Invalid";
            Utilities.printTrace(aiob.getStackTrace(),10);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Unknown json: \'" + value +"\'" + jse.getMessage());
            result += "\n Invalid";
            Utilities.printTrace(jse.getStackTrace(),10);
        }
        //TODO - Prone to throw Number Format Exception
        return result.trim();
    }

    @Override
    public boolean isDangerous() {
        return false;
    }


    @Override
    public String toString() {
        String result = displayLabel + ((value.equals("[]") || value.equals("[\"\"]"))?
                "" : context.getResources().getString(R.string.detail));
        return result.trim();
    }
}

