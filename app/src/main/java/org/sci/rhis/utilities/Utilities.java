package org.sci.rhis.utilities;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.fwc.MultiSelectionSpinner;
import org.sci.rhis.fwc.R;
import org.sci.rhis.utilities.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.Format;
import java.text.DecimalFormat;
import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by jamil.zaman on 8/30/2015.
 */
public class Utilities {

    public final static int SPINNER_INDEX_OFFSET = 0; //no offset is required
    private final static String LOGTAG = "FWC-UTILITIES";

    private final static CustomSimpleDateFormat dbFormat = new CustomSimpleDateFormat("yyyy-MM-dd");
    private final static CustomSimpleDateFormat uiFormat = new CustomSimpleDateFormat("dd/MM/yyyy");
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link #@setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static void SetVisibility(Activity activity, int id, int visibity) {
        switch (visibity) {
            case View.VISIBLE:
                MakeVisible(activity, id);
                break;
            case View.INVISIBLE:
                Reset(activity, id);
                MakeInvisible(activity, activity.findViewById(id), visibity);
                break;
            case View.GONE:
                Reset(activity, id);
                MakeInvisible(activity, id);
                break;
        }
    }

    public static void Disable(Activity activity, int id) {
        Disable(activity, activity.findViewById(id));
    }

    public static void Disable(Activity activity, View view) {

        ViewGroup testgroup = null;
        //View view  = activity.findViewById(id);
        if(view instanceof ViewGroup) { //if not a layout but single button is passed
            testgroup  = (ViewGroup) view;
        }

        for( int i = 0, count = testgroup != null ? testgroup.getChildCount(): 1; //if not a viewgroup only 1 item
             i <count && view != null; i++) {
            view = testgroup != null && !(testgroup instanceof  Spinner) ? testgroup.getChildAt(i) : view;

            if(view instanceof LinearLayout  || //LinearLayout is also view group so exclude it
               ((view instanceof  ViewGroup) && !(view instanceof  Spinner))) {
                Disable(activity, view);
            }

            else if (view instanceof CheckBox) {

                view.setClickable(false);
                view.setEnabled(false);
            }

            else if (view instanceof Spinner) {

                (view).setClickable(false);
                (view).setEnabled(false);
            }

            else if (view instanceof RadioButton) {

                view.setClickable(false);
                view.setEnabled(false);
             }

            else if (view instanceof ImageButton) {

                (view).setClickable(false);
                (view).setEnabled(false);
            }

            else if (view instanceof Button) {

                view.setEnabled(false);
                (view).setClickable(false);
            }

            else if (view instanceof EditText )
            {
               (view).setFocusable(false);
               (view).setEnabled(false);

            }

            else if (view instanceof TextView)
            {
                (view).setFocusable(false);
            }

            else {
                System.out.print(testgroup);
            }
        }
    }

    public static void MakeInvisible(Activity activity, int id)
    {
        MakeInvisible(activity, activity.findViewById(id), View.GONE);
    }

    public static void MakeInvisible(Activity activity, View view, int visibility)
    {
        ViewGroup testgroup = null;
        //View view  = activity.findViewById(id);
        if(view instanceof ViewGroup) { //if not a layout but single button is passed
            testgroup  = (ViewGroup) view;
        }

        for( int i = 0, count = testgroup != null ? testgroup.getChildCount(): 0; //if not a viewgroup only 1 item
             i <count && view != null; i++) {
            View child = testgroup.getChildAt(i);

            if(child instanceof LinearLayout || //LinearLayout is also view group so exclude it
            ((child instanceof  ViewGroup) && !(child instanceof  Spinner))) {
                MakeInvisible(activity, child, visibility);
                Disable(activity, child);
            }
            child.setVisibility(visibility);
        }
        view.setVisibility(visibility);
        /////
    }
    public static void MakeVisible(Activity activity, int id)
    {
       MakeVisible(activity, activity.findViewById(id));
    }

    public static void MakeVisible(Activity activity, View view)
    {
        ViewGroup testgroup = null;
        if(view instanceof ViewGroup) { //if not a layout but single button is passed
            testgroup  = (ViewGroup) view;
        }

        for( int i = 0, count = testgroup != null ? testgroup.getChildCount(): 0; //if not a viewgroup only 1 item
             i <count && view != null; i++) {
            View child = testgroup.getChildAt(i);

            if(view instanceof  LinearLayout || //LinearLayout is also view group so exclude it
                    ((child instanceof  ViewGroup) && !(child instanceof  Spinner))) {
                MakeVisible(activity, child);
                Enable(activity, child);
            }
            child.setVisibility(View.VISIBLE);
        }
        view.setVisibility(View.VISIBLE);
    }

    public static void VisibleLayout(Activity activity,int id)
    {
        LinearLayout visibility = (LinearLayout)activity.findViewById(id);
        visibility.setVisibility(View.VISIBLE);
    }

    public static void InVisibleLayout(Activity activity,int id)
    {
        LinearLayout visibility = (LinearLayout)activity.findViewById(id);
        visibility.setVisibility(View.GONE);
    }


    public static void Reset(Activity activity, int id) {
        Reset(activity, activity.findViewById(id));
    }

    public static void Reset(Activity activity, View view) {

        ViewGroup testgroup = null;
        if(view instanceof ViewGroup) { //if not a layout but single button is passed
            testgroup  = (ViewGroup) view;
        }

        for(int i = 0, count = testgroup != null ? testgroup.getChildCount(): 1; i <count; i++) {
            view = testgroup != null ? testgroup.getChildAt(i) : view;

            if(view instanceof LinearLayout) {
                if(view instanceof RadioGroup){
                    ((RadioGroup) view).clearCheck();
                }
                Reset(activity, view.getId());
            }

            else if (view instanceof CheckBox) {

                ((CheckBox) view).setChecked(false);
                view.setClickable(true);
                view.setEnabled(true);
            }
            else if (view instanceof MultiSelectionSpinner) {

                (view).setClickable(true);
                (view).setEnabled(true);
                ((MultiSelectionSpinner) view).setSelection(new int[]{});
            }
            else if (view instanceof Spinner) {

                (view).setClickable(true);
                (view).setEnabled(true);
                ((Spinner) view).setSelection(0);
            }
            else if (view instanceof RadioButton) {

                ((RadioButton) view).setChecked(false);
                (view).setClickable(true);
                (view).setEnabled(true);
             }

            else if (view instanceof ImageButton) {

                (view).setClickable(true);
                (view).setEnabled(true);
            }

            else if (view instanceof Button) {

                (view).setEnabled(true);
                (view).setClickable(true);
            }

            else if (view instanceof EditText ) {

                if(((EditText) view).getInputType()==(InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_DATE)){
                    ((EditText)view).setText("");
                    ((EditText)view).setHint("dd/mm/yyyy");
                }
                else
                {
                    (view).setFocusable(true);
                    (view).setFocusableInTouchMode(true);
                    (view).setEnabled(true);
                    ((EditText)view).setText("");
                }
            }

            else if (view instanceof TextView) {

                (view).setFocusable(true);
                (view).setFocusableInTouchMode(true);
            }

            else {
                System.out.print(testgroup);
            }
        }
    }

    public static void Enable(Activity activity, int id) {
        Enable(activity, activity.findViewById(id));
    }

    public static void Enable(Activity activity, View view) {

        ViewGroup testgroup = null;
        //View view  = activity.findViewById(id);
        if(view instanceof ViewGroup) { //if not a layout but single button is passed
            testgroup  = (ViewGroup) view;
        }

        for( int i = 0, count = testgroup != null ? testgroup.getChildCount(): 1; //if not a viewgroup only 1 item
             i <count && view != null; i++) {
             view = testgroup != null && !(testgroup instanceof  Spinner) ? testgroup.getChildAt(i) : view;

            if(view instanceof LinearLayout || //LinearLayout is also view group so exclude it
               ((view instanceof  ViewGroup) && !(view instanceof  Spinner))) {
                Enable(activity, view);
            }

            else if (view instanceof CheckBox) {

                view.setClickable(true);
                view.setEnabled(true);
            }
            else if (view instanceof Spinner) {
                if(view.getTag()!=null && view.getTag().toString().equals("DISABLE")){

                }else{
                    (view).setClickable(true);
                    (view).setEnabled(true);
                }

            }

            else if (view instanceof RadioButton) {

                (view).setClickable(true);
                (view).setEnabled(true);
            }

            else if (view instanceof ImageButton) {

                (view).setClickable(true);
                (view).setEnabled(true);
            }

            else if (view instanceof Button) {

                (view).setEnabled(true);
                (view).setClickable(true);
            }

            else if (view instanceof EditText ) {

                if(view.getId()==R.id.Clients_House_No || view.getId()==R.id.Clients_Mobile_no ||
                        ((EditText) view).getInputType()== (InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE)){

                }else if(view.getTag()!=null && view.getTag().toString().equals("DISABLE")){

                }else
                {
                    (view).setFocusable(true);
                    (view).setFocusableInTouchMode(true);
                    (view).setEnabled(true);
                }
            }

            else if (view instanceof TextView) {

                (view).setFocusable(true);
                (view).setFocusableInTouchMode(true);
            }

            else {
                System.out.print(testgroup);
            }
        }
    }

    public static void EnableField(Activity activity,int id,String type)
    {
        EditText e = (EditText)activity.findViewById(id);

        e.setFocusableInTouchMode(true);
        e.setFocusable(true);
        e.setEnabled(true);
        if(type=="reset")
            (e).setText("");
        else if(type=="edit")
            //do nothing
            ;
    }

    public static void DisableField(Activity activity,int id)
    {
        EditText e = (EditText)activity.findViewById(id);

        e.setFocusable(false);
        e.setEnabled(false);
    }

    public static void getSpinners(HashMap<String, Spinner> keyMap, JSONObject json) {
        Spinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    //spinner.setSelection((json.getInt(key) - 1));
                    json.put(key, String.valueOf(spinner.getSelectedItemPosition() + SPINNER_INDEX_OFFSET));
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                printTrace(jse.getStackTrace());
            }
        }
    }

    //Get values instead of indices from spinner
    public static void getSpinnerValues(HashMap<String, Spinner> keyMap, JSONObject json) {
        Spinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    //spinner.setSelection((json.getInt(key) - 1));
                    json.put(key, "\"" + String.valueOf(spinner.getSelectedItem()) + "\"");
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key+ "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    //Get custom values instead of indices from spinner
    public static void getSpinnersValuesAsCustom(HashMap<String, Spinner> keyMap, JSONObject json, int startPos, String endOffset) {
        Spinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    //spinner.setSelection((json.getInt(key) - 1));
                    String selectedItem = String.valueOf(spinner.getSelectedItem());
                    json.put(key, selectedItem.equals("")?"":selectedItem.substring(startPos,selectedItem.indexOf(endOffset)));
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    //TODO: Error prone -> only handles keys related to treatment modules, will get NullPointer Exception if the key does not exist.
    public static JSONObject getMultiSelectSpinnerIndices( HashMap<String, MultiSelectionSpinner> multiSpinnerMap, JSONObject json) {
        for (String key: multiSpinnerMap.keySet()) {
            try {
                //TODO: JamilZ - There must be a way of getting done in one go
                String value = "[\"" + TextUtils.join("\",\"",
                        multiSpinnerMap.get(key).getSelectedIndicesInText(SPINNER_INDEX_OFFSET))
                        + "\"]";
                json.put(key, value.equals("[\"\"]") ? "" : value);

            } catch (JSONException JSE) {
                Log.e("Utilities", "JSON Exception:\n" + JSE.toString());
            } catch (NullPointerException NPE) {
                Log.e("Utilities", "Key does not exist in map: " + key + "\n" +
                        "NullPointerException Exception:\n" + NPE.toString());
            }
        }
        return json;
    }

    //update by position
    public static void setMultiSelectSpinners(HashMap<String, MultiSelectionSpinner> keyMap, JSONObject json) {
        MultiSelectionSpinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    String value = json.getString(key);
                    if(!value.equals("[\"\"]") && !value.equals("[]") && !value.equals("")) {
                        String keyStr[] = value.replaceAll("(\"|\\[|\\])", "").split(",");
                        int values[] = new int[keyStr.length];
                        for (int i = 0; i < keyStr.length; ++i) {
                            values[i] = Integer.valueOf(keyStr[i]);
                        }

                        spinner.setSelection(values);
                    }
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                printTrace(jse.getStackTrace());
            } catch (NumberFormatException nfe) {
                Log.e(LOGTAG, "Could not convert value for key: '" + key + "' JSON:\n\t{"+ json.toString() +"}\n\t" + nfe.getStackTrace());
            }
        }
    }

    //update by position
    public static void setSpinners(HashMap<String, Spinner> keyMap, JSONObject json) {
        Spinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                int total = spinner.getCount();
                if(spinner != null ) {
                    spinner.setSelection((json.getString(key).equals("")?0:
                            json.getInt(key) < total ? json.getInt(key) : 0)); //handle index out of bounds
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    //Update by value
    public static void setSpinners(HashMap<String, Pair<Spinner, Integer>> keyMap, JSONObject json, Context context) {
        Spinner spinner;
        String compareValue;

        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key).first;
                if(spinner != null) {
                    //spinner.setSelection((json.getInt(key) - 1));
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, keyMap.get(key).second, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    compareValue = json.getString(key);
                    if (!compareValue.equals(null)) {
                        int spinnerPosition = adapter.getPosition(compareValue);
                        spinner.setSelection(spinnerPosition);
                    }
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    public static void setCheckboxes(HashMap<String, CheckBox> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setChecked((json.getString(key).equals("1")));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }
    public static void setTextViews(HashMap<String, TextView> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setText(json.getString(key));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    public static void getTextViews(HashMap<String, TextView> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setText(json.getString(key));
                json.put(key, (keyMap.get(key).getText()));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    public static void getCheckboxes(HashMap<String, CheckBox> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setChecked((json.getInt(key) == 1));
                json.put(key, (keyMap.get(key).isChecked() ? 1 : 2));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }
    public static void getCheckboxesBlank(HashMap<String, CheckBox> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setChecked((json.getInt(key) == 1));
                json.put(key, (keyMap.get(key).isChecked() ? 1 : ""));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    public static void getEditTexts(HashMap<String, EditText> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setText(json.getString(key));
                json.put(key, (keyMap.get(key).getText()));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                printTrace(jse.getStackTrace());
            }
        }
    }

    public static void setEditTexts(HashMap<String, EditText> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setText(json.getString(key));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                printTrace(jse.getStackTrace());
            }
        }
    }

    public static void setEditTextDates(HashMap<String, EditText> keyMap, JSONObject json) {

        //CustomSimpleDateFormat dbFormat = new CustomSimpleDateFormat("yyyy-MM-dd");
        //CustomSimpleDateFormat uiFormat = new CustomSimpleDateFormat("dd/MM/yyyy");
        String currentDate;
        for (String key: keyMap.keySet()) {
            try {
                currentDate = json.getString(key);
                if(!currentDate.equals("")) {
                    Date date = dbFormat.parse(currentDate);
                    String dateStr = uiFormat.format(date);
                    keyMap.get(key).setText(dateStr);
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            } catch (ParseException pe) {
                Log.e(LOGTAG, "Parsing Exception: Could not parse date:"
                        + " Key: "+ key + " "
                        + keyMap.get(key).getText().toString());
                StackTraceElement ste [] = pe.getStackTrace();
                for(int i = 0; i< 9; i++) {
                    Log.e(LOGTAG, ste[i].toString());
                }
            }
        }
    }

    public static void getEditTextDates(HashMap<String, EditText> keyMap, JSONObject json) {

        //CustomSimpleDateFormat uiFormat = new CustomSimpleDateFormat("dd/MM/yyyy");
        //CustomSimpleDateFormat dbFormat = new CustomSimpleDateFormat("yyyy-MM-dd");
        String currentDate;
        for (String key: keyMap.keySet()) {
            try {
                currentDate = (keyMap.get(key).getText()).toString();
                if(!currentDate.equals("")) {
                    /*Date date = uiFormat.parse(currentDate);
                    String dateStr = dbFormat.format(date);
                    json.put(key, dateStr);*/
                    json.put(key, dbFormat.format(uiFormat.parse(currentDate)));
                }else{
                    json.put(key, "");//sending empty string when date is not set
                }

            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            } catch (ParseException pe) {
                Log.e(LOGTAG, "Parsing Exception: Could not parse date:"
                        + " Key: " + key + " "
                        + keyMap.get(key).getText().toString());
                StackTraceElement ste [] = pe.getStackTrace();
                for(int i = 0; i< 9; i++) {
                    Log.e(LOGTAG, ste[i].toString());
                }
            } catch (NullPointerException NP) {
                Log.e(LOGTAG, "Parse:\n\t" + NP.getMessage());
            }
        }
    }

    public static void getRadioGroupButtons(HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {

                String value = "";
                if(keyMap.get(key).first.getCheckedRadioButtonId() == keyMap.get(key).second.first.getId()) {
                    value = "1";
                } else if (keyMap.get(key).first.getCheckedRadioButtonId() == keyMap.get(key).second.second.getId()) {
                    value = "2";
                }
                json.put(key, value);

            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                printTrace(jse.getStackTrace());
            } catch (NullPointerException NP) {
                Log.e("Null Pointer", NP.getMessage());
            }
        }
    }

    public static void getRadioGroupButtonsArray(HashMap<String, Pair<RadioGroup, RadioButton[]>> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                String value = "";
                for(int i=0; i<keyMap.get(key).second.length; i++){

                    if(keyMap.get(key).first.getCheckedRadioButtonId() == keyMap.get(key).second[i].getId()){
                        value = String.valueOf(i+1);
                        break;
                    }
                }

                json.put(key, value);

            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                printTrace(jse.getStackTrace());
            } catch (NullPointerException NP) {
                Log.e("Null Pointer", NP.getMessage());
            }
        }
    }

    public static void setRadioGroupButtonsArray(HashMap<String, Pair<RadioGroup, RadioButton[]>> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {

                if(!json.getString(key).equals("")) {
                    int val = Integer.valueOf(json.getString(key));
                    keyMap.get(key).first.check(keyMap.get(key).second[val-1].getId());
                }

            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                printTrace(jse.getStackTrace());
            } catch (NullPointerException NP) {
                Log.e(LOGTAG, NP.getMessage());
            }
        }
    }

    public static void setRadioGroupButtons(HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {

                if (json.getString(key).equals("1")) {
                    keyMap.get(key).first.check(keyMap.get(key).second.first.getId());

                } else if (json.getString(key).equals("2")) {
                    keyMap.get(key).first.check(keyMap.get(key).second.second.getId());
                } else {
                    //throw new IllegalArgumentException("Not an yes no response");
                }

            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                printTrace(jse.getStackTrace());
            } catch (NullPointerException NP) {
                Log.e(LOGTAG, NP.getMessage());
            }
        }
    }




    public static Date addDateOffset(Date given, int days) {
        Calendar edd_cal
                = Calendar.getInstance();
        edd_cal.setTime(given);

        edd_cal.add(Calendar.DATE, days);
        return edd_cal.getTime();
    }

    public static void printTrace(StackTraceElement ste []) {
        printTrace(ste, 7); //default to first 3 lines
    }

    public static void printTrace(StackTraceElement ste [], int level) {
        //
        for(int i = 0; i< level; i++) {
            Log.e(LOGTAG, ste[i].toString());
        }
    }

    public static String ConvertNumberToBangla(String givenNumber) {

        char[] numberMap = {'0','1','2','3','4','5','6','7','8','9'}; //first 40 byte is wastage but its acceptable
        char[] banglaMap = {'০','১','২','৩','৪','৫','৬','৭','৮','৯'};
        String banglaResponse = "";

        for(int i = 0; i< givenNumber.length(); i++) {
            try {
                banglaResponse += banglaMap[givenNumber.charAt(i) - 48];
            } catch (ArrayIndexOutOfBoundsException aiob) {
                if(givenNumber.charAt(i) > 57 || givenNumber.charAt(i) < 48) {
                       banglaResponse += givenNumber.charAt(i);
                } else {
                    Log.e(LOGTAG, "Number out of range:" + givenNumber);
                }
            }
        }

        return banglaResponse;
    }


    public static String ConvertNumberToEnglish(String givenNumber) {

        char[] numberMap = {'0','1','2','3','4','5','6','7','8','9'}; //first 40 byte is wastage but its acceptable

        String englishResponse = "";

        for(int i = 0; i< givenNumber.length(); i++) {
            try {
                englishResponse += numberMap[givenNumber.charAt(i) - 2534];
            } catch (ArrayIndexOutOfBoundsException aiob) {
                if(givenNumber.charAt(i) > 2543 || givenNumber.charAt(i) < 2534) {
                    englishResponse += givenNumber.charAt(i);
                } else {
                    Log.e(LOGTAG, "Number out of range:" + givenNumber);
                }
            }
        }

        return englishResponse;
    }

    public static void showBiggerToast(Context context, int stringId ) {
        Toast toast = Toast.makeText(context, stringId, Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(20);
        toast.show();
    }


    public static String getDateStringDBFormat(Date date) {
        return getDateStringFormat(date, dbFormat);
    }


    public static String getDateStringUIFormat(Date date) {
        return getDateStringFormat(date, uiFormat);
    }

    public static String getDateStringUIFormat(String date) {
        try {
            return getDateStringFormat(dbFormat.parse(date), uiFormat);
        } catch (ParseException pe) {
            Log.e(LOGTAG, String.format("Date Parsing Error: %s", pe.getMessage()));
        }
        return "";
    }

    public static String getDateTimeStringMs() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    public static String getDateTimeStringDBFormat() {
        return getDateStringFormat(Calendar.getInstance().getTime(), new CustomSimpleDateFormat(Constants.SHORT_HYPHEN_FORMAT_WITH_TIMESTAMP_BRITISH));
    }

    public static String getDateTimeWithoutTimeStampStringDBFormat() {
        return getDateStringFormat(Calendar.getInstance().getTime(), new CustomSimpleDateFormat(Constants.SHORT_HYPHEN_FORMAT_WITH_TIMESTAMP_DATABASE));
    }

    public static String getDateStringDBFormat() {
        return getDateStringDBFormat(Calendar.getInstance().getTime());
    }

    public static String getDateStringFormat(Date date, CustomSimpleDateFormat dateFormat) {
        String formattedDate = "";
        try {
            formattedDate = dateFormat.format(date);
        } catch (Exception pe) {
            Log.e(LOGTAG, pe.getMessage());
            printTrace(pe.getStackTrace());
            formattedDate = "";
        }
        return formattedDate;
    }
    /**
     * manipulate date according to task code/type and number of month
     * @param date
     * @param noOfMonth
     * @param taskCode
     * @return
     */
    public static Date manipulateDateByMonth(Date date, int noOfMonth, int taskCode){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);

        if(date!=null){
            switch (taskCode){
                case Constants.ADD_CODE:
                    cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) + noOfMonth));
                    break;
                case Constants.SUBTRACT_CODE:
                    cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) - noOfMonth));
                    break;
            }
        }

        return cal.getTime();
    }

    /**
     * Get the diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static void showAlertToast(Context context, String msg){
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View alertToastView = inflater.inflate(R.layout.view_alert_toast, (ViewGroup) ((Activity) context).findViewById(R.id.toastcustom));
        ((TextView) alertToastView.findViewById(R.id.texttoast)).setText(msg);

        Toast alertToast = new Toast(context);
        alertToast.setView(alertToastView);
        alertToast.setDuration(Toast.LENGTH_LONG);
        alertToast.show();
    }

    public static boolean checkDateInRange(Date startDate, Date endDate, Date checkDate){
        if(checkDate.compareTo(startDate)>=0 && checkDate.compareTo(endDate)<= 0) {
            return true;
        }
        return false;
    }

    public static Spannable changePartialTextColor(int intColorCode, String strText, int intFrom, int intTo)
    {
        Spannable word = new SpannableString(strText);
        word.setSpan(new ForegroundColorSpan(intColorCode), intFrom, intTo, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return word;
    }

    /**
     * Decompress a zip file or Unzip a zip file
     * @param zipFile zip file name with path as file
     * @param targetDirectory location of the unzipped files to be stored
     * @return
     */
    public static String unzip(File zipFile, File targetDirectory) {
        String result = "Unzipping failed.";

        if(zipFile.exists()) {
            ZipInputStream zis = null;
            try {
                zis = new ZipInputStream(
                        new BufferedInputStream(new FileInputStream(zipFile)));
                ZipEntry ze = null;
                int count;
                byte[] buffer = new byte[8192];
                while ((ze = zis.getNextEntry()) != null) {
                    File file = new File(targetDirectory, ze.getName());
                    File dir = ze.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " +
                                dir.getAbsolutePath());
                    if (ze.isDirectory())
                        continue;
                    FileOutputStream fout = new FileOutputStream(file);
                    try {
                        while ((count = zis.read(buffer)) != -1)
                            fout.write(buffer, 0, count);
                    } finally {
                        fout.close();
                    }
                }
                result = "Unzipping done.";
            } catch (IOException ex) {
                result = ex.toString();
            } finally {
                try {
                    zis.close();
                } catch (IOException e) {
                    result = e.toString();
                }
            }
        }
        else{
            result = "No file exists..";
        }
        return result;
    }

    /**
     * Compress a file as Zip
     * @param arrFiles array of files with path to be zipped
     * @param zipFileName name of the zip file with path
     */
    public static void zip(String[] arrFiles, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[8192];

            for (int i = 0; i < arrFiles.length; i++) {
                Log.v("Compress", "Adding: " + arrFiles[i]);
                System.out.println("Compress File Adding: " + arrFiles[i]);
                FileInputStream fi = new FileInputStream(arrFiles[i]);
                origin = new BufferedInputStream(fi, 8192);

                ZipEntry entry = new ZipEntry(arrFiles[i].substring(arrFiles[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, 8192)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Rename a file
     * @param fileName old file name with path as File
     * @param renamedFile new file name wth path as File
     * @return
     */
    public static String renameFile(File fileName, File renamedFile){
        String result;

        if(fileName.exists()) {
            fileName.renameTo(renamedFile);
            System.out.println("Renaminging done.");
            result = "Rename done.";
        }
        else
        {
            result = "No file exists..";
        }
        return result;
    }

    /**
     * Get the chacksum of a file
     * @param filePath file name with path as string
     * @return the checksum as string
     */
    public static String getMD5Checksum(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead);
            }
            byte [] md5Bytes = digest.digest();
            return convertHashToString(md5Bytes);
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) { }
            }
        }
    }

    public static String fahrenheitToCelcious(String strFahrenheit){
        String strResult = "";
        if (strFahrenheit != null && !strFahrenheit.equals("")) {
            DecimalFormat df = new DecimalFormat("#,###,##0.00");
            Double dblFarhenheit = Double.parseDouble(strFahrenheit);
            Double dblCelcious = (dblFarhenheit - 32) / 1.8;
            String strCelciousEnglish = String.valueOf(df.format(dblCelcious));
            strResult = " "+strCelciousEnglish+"\u00B0"+"সে )";
        }
        else
        {
            strResult = " 0"+"\u00B0"+"সে )";
        }
        return strResult;
    }

    public static String gramToKg(String weightInGram)
    {
        String strResult = "";
        if(weightInGram != null && !weightInGram.equals(""))
        {
            DecimalFormat df = new DecimalFormat("#,###,##0.00");
            Double dblGramWeight = Double.parseDouble(weightInGram);
            Double dblKgWeight = dblGramWeight/1000;
            String strKgEnglish = String.valueOf(df.format(dblKgWeight));
            strResult = " "+strKgEnglish+" কেজি";
        }
        else{
            strResult = "0 কেজি";
        }
        return strResult;
    }

    private static String convertHashToString(byte[] md5Bytes) {
        String returnVal = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
        }
        return returnVal.toUpperCase();
    }
    
    public static String convertEnglishDateToBengali(String dbFormattedDate){
        String returnVal = "";
        Format formatter = new CustomSimpleDateFormat("MMMM");
        String month="";
        try {
            month = formatter.format(Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,dbFormattedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] datePartArray = dbFormattedDate.split("-");
        returnVal+= ConvertNumberToBangla(datePartArray[2]);
        returnVal+=" " + ConstantMaps.MonthNameEnglishToBengali.get(month)+",";
        returnVal+=" " + ConvertNumberToBangla(datePartArray[0]);

        return returnVal;
    }
    
    /**
     * Get the Dose of Gentamicin for PSBI according to Weight of the children
     * @param weight the weight of the children
     * @return Returns the Dose as String with M.L., M.G. and Insuline Unit
     */
    public static String ChildCareGentamicinDose(int weight)
    {
        int[] intWeightRange = {1500,1749,1750,1999,2000,2499,2500,2999,3000,3499,3500,3999,4000,4499,4500,4999,5000,5499,5500,5999,6000,6500};
        Double[] dblMLDose = {0.15,0.17,0.2,0.35,0.45,0.5,0.55,0.6,0.7,0.75,0.8};
        Double[] dblMGDose = {6.0,6.8,8.0,14.0,18.0,20.0,22.0,24.0,28.0,30.0,32.0};
        int[] intInsulineDose = {15,17,20,35,45,50,55,60,70,75,80};
        String strResult = "";
        int index = 0;
        for(int i =0; i< intWeightRange.length; i+=2)
        {
            if(weight >= intWeightRange[i] && weight <= intWeightRange[i+1])
            {
                strResult =  ConvertNumberToBangla(String.valueOf(dblMLDose[index]))+" মি.লি./ "+
                        ConvertNumberToBangla(String.valueOf(dblMGDose[index]))+
                        " মি.গ্রা./ "+ConvertNumberToBangla(String.valueOf(intInsulineDose[index]))+"  ইউনিট (ইনসুলিন সিরিঞ্জ) " +
                        "মাংশপেশিতে।";
            }
            else if (weight < 1500 || weight > 6500)
            {
                strResult = "প্রতি কেজিতে ৫.০ - ৭.৫ মি.গ্রা. দিনে ১ বার মোট ২ দিন মাংশপেশিতে। ";
            }
            index++;
        }

        return strResult;
    }

    /**
     * Get the Dose of Amoxicillin for PSBI according to Weight of the children
     * @param weight the weight of the children
     * @return Returns the Dose as String with M.L., M.G. Unit
     */
    public static String ChildCareAmoxicillinDose(int weight)
    {
        int[] intWeightRange = {1500,1749,1750,1999,2000,2499,2500,2999,3000,3499,3500,3999,4000,4499,4500,4999,5000,5499,5500,5999,6000,6500};
        int[] dblMGDose = {80,90,110,140,160,190,210,240,260,290,310};
        Double[] dblMLDose = {0.8,0.9,1.1,1.4,1.6,1.9,2.1,2.4,2.6,2.9,3.1};
        String strResult = "";
        int index = 0;
        for(int i =0; i< intWeightRange.length; i+=2)
        {
            if(weight >= intWeightRange[i] && weight <= intWeightRange[i+1])
            {

                strResult =  ConvertNumberToBangla(String.valueOf(dblMGDose[index]))+" মি.গ্রা./ "+
                        ConvertNumberToBangla(String.valueOf(dblMLDose[index]))+" মি.লি. মুখে";
            }
            else if (weight < 1500 || weight > 6500)
            {
                strResult =  "প্রতি কেজিতে ৫০ মি.গ্রা. ১২ ঘণ্টা পর পর দিনে ২ বার মোট ৭ দিন মুখে।";
            }
            index++;
        }

        return strResult;
    }

    /**
    * Get the dose for Little Dehydration for child according to age and weight
    * @param weight of the children and age of the children
    * @return Returns the dose as M.L. unit
    */
    public static String ChildCareLittleDehydration(int ageInDays, int weight)
    {
        int[] dblMLDose = {};
        return "";
    }


    public static String getClassificationDetailsFromID(String str){
        String classificationText = "";
        String[] arrString = str.split(",");
        try {
            for (int i = 0; i < arrString.length; i++) {
                String title = ConstantJSONs.classificationDetail().getJSONObject(String.valueOf(arrString[i])).getString("name");
                classificationText += title+"\n";
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return classificationText;
    }



}
