package org.sci.rhis.fwc;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by jamil.zaman on 8/28/2015.
 */
public abstract class CustomFragment extends Fragment {

    protected HashMap<String, CheckBox> jsonCheckboxMap;
    protected HashMap<String, CheckBox> jsonCheckboxGroupMap;
    protected HashMap<String, Spinner> jsonSpinnerMap;
    protected HashMap<String, MultiSelectionSpinner> jsonMultiSpinnerMap;
    protected HashMap<String, Pair<RadioGroup, RadioButton[]>> jsonRadioGroupButtonMapMultiple;
    protected HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> jsonRadioGroupButtonMap;
    protected HashMap<String, EditText> jsonEditTextMap;
    protected HashMap<String, EditText> jsonEditTextDateMap;
    protected HashMap<String, TextView> jsonTextViewsMap;

    public HashMap<String, CheckBox> getJsonCheckboxMap() {
        return jsonCheckboxMap;
    }

    public HashMap<String, Spinner> getJsonSpinnerMap() {
        return jsonSpinnerMap;
    }

    public HashMap<String, MultiSelectionSpinner> getJsonMultiSpinnerMap() {
        return jsonMultiSpinnerMap;
    }

    public HashMap<String, EditText> getJsonEditTextMap() {
        return jsonEditTextMap;
    }

    public HashMap<String, EditText> getJsonEditTextDateMap() {
        return jsonEditTextDateMap;
    }


    protected void initialize() {
        //populate checkboxes
        jsonCheckboxMap = new HashMap<>();
        initiateCheckboxes();

        jsonCheckboxGroupMap = new HashMap<>();
        initiateCheckboxesGroup();

        //populate Spinners
        jsonSpinnerMap = new HashMap<String, Spinner>();
        initiateSpinners();

        //populate Spinners
        jsonMultiSpinnerMap = new HashMap<String, MultiSelectionSpinner>();
        initiateMultiSelectionSpinners();


        //populate RadioGroupButtons
        jsonRadioGroupButtonMapMultiple = new HashMap<String, Pair<RadioGroup, RadioButton[]>>();
        jsonRadioGroupButtonMap = new HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>>();
        initiateRadioGroups();

        //populate EditTexts
        jsonEditTextMap = new HashMap<String, EditText>();
        initiateEditTexts();

        //populate TextViews
        jsonTextViewsMap = new HashMap<String, TextView>();
        initiateTextViews();

        //populate EditTextsDate
        jsonEditTextDateMap = new HashMap<String, EditText>();
        initiateEditTextDates();

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
}
