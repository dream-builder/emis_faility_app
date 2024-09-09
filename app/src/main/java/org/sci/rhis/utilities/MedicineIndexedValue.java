package org.sci.rhis.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.fwc.R;

/**
 * Created by jamil.zaman on 15/02/16.
 */
public class MedicineIndexedValue extends IndexedValue {

    protected int dropdownArrayResource;
    private boolean readFromDb = true;
    private JSONArray jsonArray = null;

    public MedicineIndexedValue(Context context, String key, String value, String displayLabel, Integer [] arguments) {
        super(context, key, value, displayLabel, arguments);
        this.dropdownArrayResource = arguments[0];
        loadDbResult();
        if(((dropdownArrayResource ^ Constants.LIST_IDENTIFIER_MASK) & ~0xFFFF) == 0 &&
           ((dropdownArrayResource ^ Constants.LIST_IDENTIFIER_MASK) & 0xFFFF ) != 0 ) { // only 1,2,4,8 is allowed
            readFromDb = true;
            loadDbResult();
        }
    }

    private void loadDbResult() {
        SharedPreferences pref = context.getSharedPreferences(String.valueOf(Constants.SHARED_LIST_IDENTIFIER),0);
        String table_name = pref.getString(String.valueOf(dropdownArrayResource), null);
        //TODO - serviceid = 10 internal knowledge assumed should be dynamic
        //jsonArray = CommonQueryExecution.getMedicineListAsJSONArray(table_name,"serviceId=10");
    }

    @Override
    public String getValue() {
        String result = "";

        String [] values = {};
        if (!readFromDb ) {
            values = context.getResources().getStringArray(dropdownArrayResource);
        }

        String selectedValues[] = value.replaceAll("(\"|\\[|\\])|\\{|\\}", "").split(",");

        try {
            for (String value : selectedValues) {
                String strId = value.substring(0,value.indexOf("_"));
                String quantity = value.substring(value.indexOf(":")+1,value.length());
                if (readFromDb) {
                    boolean idFound = false;
                    for (int i = 0; i < jsonArray.length() && !idFound; i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        //TODO - revisit internal knowledge assumed for json key "id", "detail"
                        if(Integer.valueOf(strId) == item.getInt("id")) {
                            result += "\n"+item.getString("detail") + " " + quantity;
                            idFound = true;
                        }
                    }
                } else {
                    result += "\n" + values[Integer.valueOf(value)];
                }
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, String.format("JSON Parsing error %s: \'" + value +"\'", getDisplayLabel()));
            result += "\n Invalid";
            Utilities.printTrace(jse.getStackTrace(),10);
        } catch (NumberFormatException nfe) {
            Log.e(LOGTAG, String.format("Invalid index for %s: \'" + value +"\'", getDisplayLabel()));
            result += "\n Invalid";
            Utilities.printTrace(nfe.getStackTrace(),10);
        } catch (ArrayIndexOutOfBoundsException aiob) {
            Log.e(LOGTAG, "Invalid index: \'" + value +"\'" + aiob.getMessage());
            result += "\n Invalid";
            Utilities.printTrace(aiob.getStackTrace(),10);
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
        String result = displayLabel + ((value.equals("{}") || value.equals(""))? "" : context.getResources().getString(R.string.detail));
        return result.trim();
    }
}

