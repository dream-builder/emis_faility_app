package org.sci.rhis.utilities;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.fwc.ClinicalServiceActivity;
import org.sci.rhis.fwc.MultiSelectionSpinner;
import org.sci.rhis.fwc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by jamil.zaman on 05/04/16.
 */
public class HistoryListMaker<A> {

    private  final String LOGTAG    = "FWC-HISTORY-MAKER";
    private ClinicalServiceActivity activity;
    private ArrayList<DisplayValue> displayList;
    private JSONObject singleVisitJson = null;
    private LinkedHashMap<String, Pair<String, Integer[]>> historyLabelMap;
    private Context context;

    protected HashMap<String, EditText> jsonEditTextMap;
    protected HashMap<String, EditText> jsonEditTextDateMap;
    protected HashMap<String, TextView> jsonTextViewsMap;
    protected HashMap<String, CheckBox> jsonCheckboxMap;
    protected HashMap<String, Spinner> jsonSpinnerMap;
    protected HashMap<String, MultiSelectionSpinner> jsonMultiSpinnerMap;
    protected HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> jsonRadioGroupMap;

    private HashMap<String, String> compositeKeyMap;
    private Pair<String, JSONObject> conditionalKeyPair = new Pair<>("", new JSONObject());


    public HistoryListMaker(ClinicalServiceActivity activity, /*activity*/
                            JSONObject singleVisitJson, /*single visit details*/
                            LinkedHashMap<String, Pair<String, Integer[]>> historyLabelMap, /*historyLitDisplayOrder*/
                            HashMap<String, String> compositeKeyMap
    ) {
        this.activity = activity;
        this.singleVisitJson = singleVisitJson;
        this.historyLabelMap = historyLabelMap;
        jsonCheckboxMap = activity.getJsonCheckboxMap();
        jsonEditTextMap = activity.getJsonEditTextMap();
        jsonEditTextDateMap = activity.getJsonEditTextDateMap();
        jsonSpinnerMap = activity.getJsonSpinnerMap();
        jsonMultiSpinnerMap = activity.getJsonMultiSpinnerMap();
        jsonRadioGroupMap = activity.getJsonRadioGroupMap();
        jsonTextViewsMap = activity.getJsonTextView();

        displayList = new ArrayList<>();
        this.compositeKeyMap = compositeKeyMap;
        context = activity;
    }

    public HistoryListMaker(ClinicalServiceActivity activity, /*activity*/
                            JSONObject singleVisitJson, /*single visit details*/
                            LinkedHashMap<String, Pair<String, Integer[]>> historyLabelMap, /*historyLitDisplayOrder*/
                            HashMap<String, String> compositeKeyMap,
                            Pair<String, JSONObject> conditionalKeyPair
    ) {
        this.activity = activity;
        this.singleVisitJson = singleVisitJson;
        this.historyLabelMap = historyLabelMap;

        this.conditionalKeyPair = conditionalKeyPair;

        jsonCheckboxMap = activity.getJsonCheckboxMap();
        jsonEditTextMap = activity.getJsonEditTextMap();
        jsonEditTextDateMap = activity.getJsonEditTextDateMap();
        jsonSpinnerMap = activity.getJsonSpinnerMap();
        jsonMultiSpinnerMap = activity.getJsonMultiSpinnerMap();
        jsonRadioGroupMap = activity.getJsonRadioGroupMap();

        displayList = new ArrayList<>();
        this.compositeKeyMap = compositeKeyMap;
        context = activity;
    }

    public HistoryListMaker(ClinicalServiceActivity activity, /*activity*/
                            JSONObject singleVisitJson, /*single visit details*/
                            LinkedHashMap<String, Pair<String, Integer[]>> historyLabelMap, /*historyLitDisplayOrder*/
                            HashMap<String, CheckBox>  jsonCheckboxMap,
                            HashMap<String, EditText> jsonEditTextMap,
                            HashMap<String, EditText> jsonEditTextDateMap,
                            HashMap<String, Spinner> jsonSpinnerMap,
                            HashMap<String, MultiSelectionSpinner> jsonMultiSpinnerMap
    ) {
        this.activity = activity;
        this.singleVisitJson = singleVisitJson;
        this.historyLabelMap = historyLabelMap;
        this.jsonCheckboxMap     = jsonCheckboxMap;
        this.jsonEditTextMap     = jsonEditTextMap;
        this.jsonEditTextDateMap = jsonEditTextDateMap;
        this.jsonSpinnerMap      = jsonSpinnerMap;
        this.jsonMultiSpinnerMap = jsonMultiSpinnerMap;
        this.jsonRadioGroupMap = activity.getJsonRadioGroupMap();

        displayList = new ArrayList<>();
        context = activity;
    }

    public void setChildJSON() {
        jsonCheckboxMap = activity.getJsonCheckboxMapChild();
        jsonEditTextMap = activity.getJsonEditTextMapChild();
        jsonEditTextDateMap = activity.getJsonEditTextDateMapChild();
        jsonSpinnerMap = activity.getJsonSpinnerMapChild();
        jsonMultiSpinnerMap = activity.getJsonMultiSpinnerMapChild();
        jsonRadioGroupMap = activity.getJsonRadioGroupButtonMapChild();
    }

    public ArrayList<DisplayValue>  getDisplayList() throws JSONException {
        for(String orderedKey : historyLabelMap.keySet()) {
            Log.d(LOGTAG, "Key ->" + orderedKey);
            Log.d(LOGTAG, "TYPE -> DateEditText " + jsonEditTextDateMap.containsKey(orderedKey)
                        + " EditText " + jsonEditTextMap.containsKey(orderedKey)
                        + " MultipleSpinner " + jsonMultiSpinnerMap.containsKey(orderedKey)
                        + " Spinner " + jsonSpinnerMap.containsKey(orderedKey)
                        + " Checkbox " + jsonCheckboxMap.containsKey(orderedKey)
                        + " Radio " + jsonRadioGroupMap.containsKey(orderedKey)
                    );

            if(jsonEditTextDateMap.containsKey(orderedKey)) {
                displayList.add(new DateValue(context, /*context*/
                        orderedKey, /*jsonKey */
                        singleVisitJson.get(orderedKey).toString(), /*value*/
                        historyLabelMap.get(orderedKey).first, /*label*/
                        "yyyy-MM-dd", /*db format*/
                        "dd/MM/yyyy"));
            }

                    /*for( String valueKey  : jsonEditTextMap.keySet()) {*/
            //label += " " +singleVisit.get(valueKey);
            if (jsonEditTextMap.containsKey(orderedKey)) {
                if(compositeKeyMap != null && compositeKeyMap.containsKey(orderedKey)) { /*needed to handle blood pressure differently as it carries two values against one label*/
                    Log.d(LOGTAG, String.format("Add [CTV] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));
                    //String secondValue =
                    displayList.add(new CompositValue(context, /*context*/
                            orderedKey, /*jsonKey */
                            singleVisitJson.get(orderedKey).toString(), /*value*/
                            historyLabelMap.get(orderedKey).first, /*label*/
                            singleVisitJson.get(compositeKeyMap.get(orderedKey).toString()).toString(), /*second value*/
                            "/", /*connector string*/
                            historyLabelMap.get(orderedKey).second));
                } else if(!this.conditionalKeyPair.first.isEmpty() && this.conditionalKeyPair.first.equals(orderedKey)){
                    String value =singleVisitJson.get(orderedKey).toString().trim();
                    int valueTypeCasted = Integer.parseInt(value);
                    value = this.conditionalKeyPair.second.getString(value);

                    if(Arrays.asList(new Integer[]{1, 10, 999,2}).contains(valueTypeCasted)) {
                        displayList.add(new DisplayValue(context, /*context*/
                                orderedKey, /*jsonKey */
                                value + " - " + (singleVisitJson.get("amount") != null && singleVisitJson.get("amount").toString().equals("0") ? "মজুদ নাই" : singleVisitJson.get("amount").toString()), /*value*/
                                activity.getString(R.string.fp_methods) /*label*/));
                    } else if(Arrays.asList(new Integer[]{3,4,6,7,8,9}).contains(valueTypeCasted)) {
                        displayList.add(new DisplayValue(context, /*context*/
                                orderedKey, /*jsonKey */
                                value, /*value*/
                                activity.getString(R.string.fp_methods) /*label*/));
                    } else if(Arrays.asList(new Integer[]{100, 101, 102,103}).contains(valueTypeCasted)) {
                        displayList.add(new DisplayValue(context, /*context*/
                                orderedKey, /*jsonKey */
                                "ছেড়ে দিয়েছেন", /*value*/
                                activity.getString(R.string.fp_methods) /*label*/));
                        displayList.add(new DisplayValue(context, /*context*/
                                orderedKey, /*jsonKey */
                                activity.getString(R.string.str_give_up_reason) + value, /*value*/
                                ""/*label*/));
                    }

                } else {
                    Log.d(LOGTAG, String.format("Add [TV] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));
                    displayList.add(new DisplayValue(context, /*context*/
                            orderedKey, /*jsonKey */
                            singleVisitJson.get(orderedKey).toString(), /*value*/
                            historyLabelMap.get(orderedKey).first /*label*/,
                            historyLabelMap.get(orderedKey).second));
                }
            }
            //}

            Log.d(LOGTAG, String.format("Visit: , Size after adding [TV] to the list is %d",  displayList.size()));

            //for( String orderedKey  : jsonSpinnerMap.keySet()) {
            if (jsonSpinnerMap.containsKey(orderedKey)) {
                Log.d(LOGTAG, String.format("Add [IV] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));

                displayList.add(new IndexedValue(
                        context, /*context*/
                        orderedKey, /*jsonKey */
                        singleVisitJson.get(orderedKey).toString(), /*value*/
                        historyLabelMap.get(orderedKey).first, /*label*/
                        historyLabelMap.get(orderedKey).second /*Array Resource*/
                ));
            }

            if (jsonCheckboxMap.containsKey(orderedKey)) {
                Log.d(LOGTAG, String.format("Add [BV] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));

                displayList.add(new BooleanValue(
                        context, /*context*/
                        orderedKey, /*jsonKey */
                        singleVisitJson.get(orderedKey).toString(), /*value*/
                        historyLabelMap.get(orderedKey).first
                ));

            }
            //}

            Log.d(LOGTAG, String.format("Visit: , Size after adding [IV] to the list is %d",  displayList.size()));

            //for( String orderedKey  : jsonMultiSpinnerMap.keySet()) {
            if (jsonMultiSpinnerMap.containsKey(orderedKey)) {
                Log.d(LOGTAG, String.format("Add [MIV] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));
                displayList.add(new MultiIndexedValue(
                        context, /*context*/
                        orderedKey, /*jsonKey */
                        singleVisitJson.get(orderedKey).toString(), /*value*/
                        historyLabelMap.get(orderedKey).first, /*label*/
                        historyLabelMap.get(orderedKey).second /*Array Resource*/
                ));
            }
            //}

            if (jsonRadioGroupMap.containsKey(orderedKey)) {
                Log.d(LOGTAG, String.format("Add [RADIO] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));
                if (orderedKey.equals("isNewClient")) {
                    displayList.add(new DisplayValue(context, /*context*/
                            orderedKey, /*jsonKey */
                            singleVisitJson.get(orderedKey).toString().equals("1") ? context.getString(R.string.new_client) : context.getString(R.string.old_client), /*value*/
                            historyLabelMap.get(orderedKey).first /*label*/,
                            historyLabelMap.get(orderedKey).second));
                }
            }

            //TextViews
            if (jsonTextViewsMap != null && jsonTextViewsMap.size() > 0 && jsonTextViewsMap.containsKey(orderedKey)) {
                Log.d(LOGTAG, String.format("Add [TextView] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));
                displayList.add(new DisplayValue(context, /*context*/
                        orderedKey, /*jsonKey */
                        singleVisitJson.get(orderedKey).toString(), /*value*/
                        historyLabelMap.get(orderedKey).first /*label*/,
                        historyLabelMap.get(orderedKey).second));
            }
        }
        return displayList;
    }
}
