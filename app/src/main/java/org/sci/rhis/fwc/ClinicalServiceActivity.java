package org.sci.rhis.fwc;

import android.util.Pair;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;
import org.sci.rhis.utilities.DisplayValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by jamil.zaman on 8/28/2015.
 */
public abstract class ClinicalServiceActivity extends FWCServiceActivity {
    protected HashMap<String, CheckBox> jsonCheckboxMap;
    protected HashMap<String, CheckBox> jsonCheckboxMapSave;
    protected HashMap<String, CheckBox> jsonCheckboxMapChild;
    protected HashMap<String, CheckBox> jsonCheckboxGroupMap;
    protected HashMap<String, Spinner> jsonSpinnerMap;
    protected HashMap<String, Spinner> jsonSpinnerMapForValues; //special spinner map for values............
    protected HashMap<String, Spinner> jsonSpinnerMapChild;
    protected HashMap<String, Spinner> jsonSpinnerMapSave;
    protected HashMap<String, MultiSelectionSpinner> jsonMultiSpinnerMap;
    protected HashMap<String, MultiSelectionSpinner> jsonMultiSpinnerMapChild;
    protected HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> jsonRadioGroupButtonMap;
    protected HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> jsonRadioGroupButtonMapChild;
    protected HashMap<String, EditText> jsonEditTextMap;
    protected HashMap<String, EditText> jsonEditTextMapChild;
    protected HashMap<String, EditText> jsonEditTextRetrieveMap;
    protected HashMap<String, EditText> jsonEditTextDateMap;
    protected HashMap<String, EditText> jsonEditTextDateMapSave;
    protected HashMap<String, EditText> jsonEditTextDateMapChild;
    protected HashMap<String, TextView> jsonTextViewsMap;
    protected HashMap<String, TextView> jsonTextViewMapChild;

    protected LinkedHashMap<String, Pair<String, Integer[]>> historyLabelMap;
    protected LinkedHashMap<String, Pair<String, Integer[]>> historyLabelMapTwo;
    protected HashMap<String, String> compositeMap;
    protected ArrayList<DisplayValue> displayList;

    public HashMap<String, CheckBox> getJsonCheckboxMap() {
        return jsonCheckboxMap;
    }

    public HashMap<String, CheckBox> getJsonCheckboxMapChild() {
        return jsonCheckboxMapChild;
    }

    public HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> getJsonRadioGroupMap() {
        return jsonRadioGroupButtonMap;
    }

    public HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> getJsonRadioGroupButtonMapChild() {
        return jsonRadioGroupButtonMapChild;
    }

    public HashMap<String, Spinner> getJsonSpinnerMap() {
        return jsonSpinnerMap;
    }

    public HashMap<String, Spinner> getJsonSpinnerMapChild() {
        return jsonSpinnerMapChild;
    }

    public HashMap<String, MultiSelectionSpinner> getJsonMultiSpinnerMap() {
        return jsonMultiSpinnerMap;
    }

    public HashMap<String, MultiSelectionSpinner> getJsonMultiSpinnerMapChild() {
        return jsonMultiSpinnerMapChild;
    }

    public HashMap<String, EditText> getJsonEditTextMap() {
        return jsonEditTextMap;
    }

    public HashMap<String, EditText> getJsonEditTextMapChild() {
        return jsonEditTextMapChild;
    }

    public HashMap<String, EditText> getJsonEditTextDateMap() {
        return jsonEditTextDateMap;
    }

    public HashMap<String, EditText> getJsonEditTextDateMapChild() {
        return jsonEditTextDateMapChild;
    }

    public HashMap<String, TextView> getJsonTextView() {
        return jsonTextViewMapChild;
    }

    @Override
    public abstract void callbackAsyncTask(String result);

    protected void initialize() {
        //populate checkboxes
        jsonCheckboxMap = new HashMap<>();
        jsonCheckboxMapSave = new HashMap<>();
        jsonCheckboxMapChild = new HashMap<>();
        initiateCheckboxes();

        jsonCheckboxGroupMap = new HashMap<>();
        initiateCheckboxesGroup();

        //populate Spinners
        jsonSpinnerMap = new HashMap<String, Spinner>();
        jsonSpinnerMapSave = new HashMap<String, Spinner>();
        jsonSpinnerMapChild = new HashMap<String, Spinner>();
        jsonSpinnerMapForValues = new HashMap<String, Spinner>();
        initiateSpinners();

        //populate Spinners
        jsonMultiSpinnerMap = new HashMap<String, MultiSelectionSpinner>();
        jsonMultiSpinnerMapChild = new HashMap<String, MultiSelectionSpinner>();
        initiateMultiSelectionSpinners();


        //populate RadioGroupButtons
        jsonRadioGroupButtonMap = new HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>>();
        jsonRadioGroupButtonMapChild = new HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>>();
        initiateRadioGroups();

        //populate EditTexts
        jsonEditTextMap = new HashMap<String, EditText>();
        jsonEditTextMapChild = new HashMap<String, EditText>();
        initiateEditTexts();

        //populate TextViews
        jsonTextViewsMap = new HashMap<String, TextView>();
        jsonTextViewMapChild = new HashMap<String, TextView>();
        initiateTextViews();

        //populate EditTextsDate
        jsonEditTextDateMap = new HashMap<String, EditText>();
        jsonEditTextDateMapSave = new HashMap<String, EditText>();
        jsonEditTextDateMapChild = new HashMap<String, EditText>();
        initiateEditTextDates();

        historyLabelMap = new LinkedHashMap<>();
        historyLabelMapTwo = new LinkedHashMap<>();
        compositeMap = new HashMap<>();
        displayList = new ArrayList<>();
    }

    protected abstract void initiateCheckboxes();
    protected abstract void initiateEditTexts();
    protected abstract void initiateTextViews();
    protected abstract void initiateSpinners();
    protected abstract void initiateMultiSelectionSpinners();
    protected abstract void initiateEditTextDates();
    protected void initiateCheckboxesGroup() {};
    protected abstract void initiateRadioGroups();

    public void RetriveHistory() {

    }

    public void DisplayHistory() {

    }

    public void Record() {

    }

    public void Update () {

    }

    public void setCompositeMap(String firstKey, String secondKey) {
        compositeMap.clear();
        compositeMap.put(firstKey, secondKey);
    }
}
