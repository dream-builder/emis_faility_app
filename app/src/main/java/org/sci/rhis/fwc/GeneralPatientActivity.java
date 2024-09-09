package org.sci.rhis.fwc;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncGpInfoUpdate;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.model.GeneralPerson;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomFocusChangeListener;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.DisplayValue;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.HistoryListMaker;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.SelectableItemAdapter;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GeneralPatientActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener, OnClickListener,
        CompoundButton.OnCheckedChangeListener{

    private GeneralPerson patient;
    private ProviderInfo provider;
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;
    private boolean editMode = false;
    private JSONObject lastServiceJSON;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    LinearLayout ll;

    AsyncGpInfoUpdate gpInfoUpdate;

    final private String SERVLET = "gp";
    final private String ROOTKEY = "GPInfo";
    private  final String LOGTAG    = "FWC-GP";

    private MultiSelectionSpinner multiSelectionSpinner;

    private ArrayList<DisplayValue> displayList;

    JSONObject jsonStr;

    private LinearLayout history_layout;

    private int lastGpVisit;
    private Context con;
    private int countSaveClick = 0, serviceId=0;

    @Override
    protected void onCreate(Bundle savedInstgpeState) {
        super.onCreate(savedInstgpeState);
        lastGpVisit = 0; // assume no visit

        setContentView(R.layout.activity_general_patient);
        con = this;

        history_layout = (LinearLayout)(findViewById(R.id.history_lay_gp));

        JSONArray symptomArray = Constants.getGPJSONData("symptom");
        final List<String> symptomList = Converter.StringArrayListFromJSONArray(symptomArray,"detail");

        JSONArray diseaseArray = Constants.getGPJSONData("disease");
        final List<String> diseaseList = Converter.StringArrayListFromJSONArray(diseaseArray,"detail");

        final JSONArray treatmentArray = Constants.getGPJSONData("treatment");
//        final List<String> treatmentlist = Converter.StringArrayListFromJSONArray(treatmentArray,"detail");

        final List<String> advicelist = Arrays.asList(getResources().getStringArray(R.array.GP_Advice_DropDown));
        final List<String> referreasonlist = Arrays.asList(getResources().getStringArray(R.array.GP_Refer_Reason_DropDown));

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.gpSymptomSpinner);
        multiSelectionSpinner.setItems(symptomList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.gpDiseaseSpinner);
        multiSelectionSpinner.setItems(diseaseList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.gpTreatmentSpinner);

        SelectableItemAdapter adapter = new SelectableItemAdapter(this, android.R.layout.simple_spinner_item,treatmentArray, "detail");

        try {
            multiSelectionSpinner.setAdapter(adapter);
        } catch (IllegalAccessException iae) {
            Log.e (LOGTAG, iae.getMessage());
        }

        multiSelectionSpinner.setItems(treatmentArray, "detail");

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.gpAdviceSpinner);
        multiSelectionSpinner.setItems(advicelist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.gpReasonSpinner);
        multiSelectionSpinner.setItems(referreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});

        ll = (LinearLayout)findViewById(R.id.llay);

        expListView = new ExpandableListView(this);
        ll.addView(expListView);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) listAdapter.getChild(
                        groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                        .show();

                return true;
            }
        });
        // get the listview
        setPredefinedValues();

        initialize(); //super class
        Spinner[] spinners = {
                (Spinner) findViewById(R.id.gpReferCenterNameSpinner)
        };

        for (Spinner spinner : spinners) {
            spinner.setOnItemSelectedListener(this);
        }

        getCheckbox(R.id.gpReferCheckBox).setOnCheckedChangeListener(this);
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH);
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.Date_Picker_Button, (EditText)findViewById(R.id.gpServiceDateValue));

        setHistoryLabelMapping();
        setCompositeMap("bpSystolic", "bpDiastolic");
        loadGPHistory();
    }

    private void setPredefinedValues(){
        patient = getIntent().getParcelableExtra("Patient");
        provider = getIntent().getParcelableExtra("Provider");
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private void loadGPHistory() {
        AsyncGpInfoUpdate gpUpdate = new AsyncGpInfoUpdate(
                new AsyncCallback() {
                    @Override
                    public void callbackAsyncTask(String result) {
                        handleResponse(result);
                    }
                },
                this);

        Log.d(LOGTAG, String.format("Laoding GP history for patient: %s", patient.getHealthId()));
        String queryString =  "";
        try {
            queryString = buildQueryHeader(true).toString();
        } catch (JSONException jse) {
            Utilities.printTrace(jse.getStackTrace());
        }

        gpUpdate.execute(queryString, SERVLET, ROOTKEY);
        //make history collapsible
        MethodUtils.makeHistoryCollapsible(getTextView(R.id.gpBlanLabelLabel),history_layout);
    }

    public void handleResponse(String result) {
        ll.removeAllViews(); //clear the history list first.

        Log.d(LOGTAG, "GP-ANDROID response received:\n\t" + result);

        try {
            jsonStr = new JSONObject(result);
            lastGpVisit = (jsonStr.has("count") ? jsonStr.getInt("count") : 0 );
            getTextView(R.id.gpVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastGpVisit >= 0 ? lastGpVisit + 1 : 1)));
            Log.d(LOGTAG, "JSON Response:\n"+jsonStr.toString());

            Utilities.Enable(this, R.id.gpEntryMasterLayout);
            if(lastGpVisit> 0 ) {
                showHideHistoryModifyButton(jsonStr);
                lastServiceJSON = jsonStr.getJSONObject(String.valueOf(lastGpVisit));
            }else{
                //hide modify button if all the history is cleared by the provider
                Utilities.MakeInvisible(this, R.id.modifyLastGPButton);
            }

            showSerialNumber(jsonStr);

            for (int i = 1; i <= lastGpVisit && lastGpVisit !=0 ; i++) {
                JSONObject singleVisitJson = jsonStr.getJSONObject(String.valueOf(i));
                displayList = new ArrayList<>();

                HistoryListMaker<GeneralPatientActivity>  historyListMaker = new HistoryListMaker<>(
                        this, /*activity*/
                        singleVisitJson, /*json containing keys*/
                        historyLabelMap, /*history details*/
                        compositeMap /*fields whose values are given against multiple keys*/
                );

                try {
                    displayList.addAll(historyListMaker.getDisplayList());
                } catch(Exception e) {
                    Log.e(LOGTAG, String.format("ERROR: %s" , e.getMessage()));
                    Utilities.printTrace(e.getStackTrace(), 10);
                }

                Log.d(LOGTAG, String.format("Visit: %d, Size after adding [MIV] to the list is %d", i, displayList.size()));

                listDataHeader = new ArrayList<>();

                HashMap<String, List<DisplayValue>> listDisplayValues = new HashMap<>();

                listDataHeader.add(getString(R.string.visit) + Utilities.ConvertNumberToBangla(String.valueOf(i)));
                listDisplayValues.put(listDataHeader.get(0), displayList);

                ExpandableDisplayListAdapter displayListAdapter = new ExpandableDisplayListAdapter(this, listDataHeader, listDisplayValues, true);

                initPage();

                ll.addView(expListView);
                expListView.setScrollingCacheEnabled(true);
                expListView.setAdapter(displayListAdapter);

                //keep history visible and its child expandable
                for(int j=0; j < displayListAdapter.getGroupCount(); j++){
                    expListView.expandGroup(j);
                }
                if(history_layout.getVisibility()==View.GONE){
                    history_layout.setVisibility(View.VISIBLE);
                    getTextView(R.id.gpBlanLabelLabel).
                            setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                }
                //...........................................................................

                ll.invalidate();
                if(displayList != null && !displayList.isEmpty()) {
                    //displayList.clear();
                }
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        } catch (Exception e) {
            Utilities.printTrace(e.getStackTrace(), 10);
        }
    }

    @Override
    public void callbackAsyncTask(String result) {
        handleResponse(result);
    }


    private void initPage() {
        expListView = new ExpandableListView(this);
        expListView.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        expListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView.setIndicatorBounds(0, 0);
        expListView.setChildIndicatorBounds(0, 0);
        expListView.setStackFromBottom(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (buttonView.getId() == R.id.gpReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            int layouts[] = {R.id.gpReferCenterNameLayout, R.id.gpReasonLayout};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],visibility);
            }
        }
    }

    private void cancelGP(){
        Utilities.Reset(this, R.id.gpText);
        getTextView(R.id.gpVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastGpVisit >= 0 ? lastGpVisit + 1 : 1)));
        getButton(R.id.gpSaveButton).setText("Save");
        getButton(R.id.gpDeleteButton).setVisibility(View.GONE);
        getButton(R.id.gpCancelButton).setVisibility(View.GONE);
        editMode = false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.Date_Picker_Button:
                datePickerDialog.show(datePickerPair.get(v.getId()));
                break;

            case R.id.gpSaveButton:
                if (!hasTheRequiredFields()) {
                    return;
                }
                try {
                    String cVisitDate=jsonEditTextDateMap.get("visitDate").getText().toString();
                    if(lastGpVisit>0 && !Validation.isValidVisitDate(this,cVisitDate,lastServiceJSON.getString("visitDate"),false,true)){
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ll.removeAllViews();
                //-- confirm first
                countSaveClick++;
                if( countSaveClick == 2 ) {
                    saveGp();
                    getButton(R.id.gpSaveButton).setText("Save");
                    Utilities.Enable(this, R.id.gpEntryMasterLayout);
                    Utilities.MakeInvisible(this, R.id.gpEditButton);
                    countSaveClick = 0;

                } else if(countSaveClick == 1) {
                    Utilities.Disable(this, R.id.gpEntryMasterLayout);
                    getButton( R.id.gpSaveButton).setText("Confirm");
                    Utilities.Enable(this, R.id.gpSaveButton);
                    getButton( R.id.gpEditButton).setText("Cancel");
                    Utilities.Enable(this, R.id.gpEditButton);
                    Utilities.MakeVisible(this, R.id.gpEditButton);

                    Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toastTV.setTextSize(20);
                    getButton( R.id.gpSaveButton).requestFocus();
                    toast.show();
                }
                break;

            case R.id.gpEditButton:
                if(countSaveClick == 1) {
                    countSaveClick = 0;
                    Utilities.Enable(this, R.id.gpEntryMasterLayout);
                    getButton(R.id.gpSaveButton).setText(editMode?"Update":"Save");
                    //TODO - Review
                    Utilities.MakeInvisible(this, R.id.gpEditButton);
                }
                break;

            case R.id.modifyLastGPButton:
                setLastServiceData();
                break;

            case R.id.gpCancelButton:
                cancelGP();
                break;

            case R.id.gpDeleteButton:
                deleteLastGP();
                break;

            default:
                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    protected void initiateCheckboxes(){
        jsonCheckboxMap.put("refer", getCheckbox(R.id.gpReferCheckBox));
    }

    @Override
    protected void initiateRadioGroups(){}


    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("edema", getSpinner(R.id.gpEdemaSpinner));
        jsonSpinnerMap.put("anemia", getSpinner(R.id.gpAnemiaSpinner));

        jsonSpinnerMap.put("jaundice", getSpinner(R.id.gpJaundiceSpinner));
        //jsonSpinnerMap.put("urineSugar", getSpinner(R.id.gpUrineSugarSpinner));
        //jsonSpinnerMap.put("urineAlbumin", getSpinner(R.id.gpUrineAlbuminSpinner));
        jsonSpinnerMap.put("lungs", getSpinner(R.id.gpLungsSpinner));
        jsonSpinnerMap.put("liver", getSpinner(R.id.gpLiverSpinner));
        jsonSpinnerMap.put("splin", getSpinner(R.id.gpSplinSpinner));
        jsonSpinnerMap.put("tonsil", getSpinner(R.id.gpTonsilSpinner));
        jsonSpinnerMap.put("referCenterName",     getSpinner(R.id.gpReferCenterNameSpinner));
        //jsonSpinnerMap.put("serviceSource",  getSpinner(R.id.gpOtherCenterNameSpinner));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {
        //jsonMultiSpinnerMap.put("complicationSign",  getMultiSelectionSpinner(R.id.gpComplicationSpinner));
        jsonMultiSpinnerMap.put("symptom",       getMultiSelectionSpinner(R.id.gpSymptomSpinner));
        jsonMultiSpinnerMap.put("disease",       getMultiSelectionSpinner(R.id.gpDiseaseSpinner));
        jsonMultiSpinnerMap.put("treatment",     getMultiSelectionSpinner(R.id.gpTreatmentSpinner));
        jsonMultiSpinnerMap.put("advice",        getMultiSelectionSpinner(R.id.gpAdviceSpinner));
        jsonMultiSpinnerMap.put("referReason",   getMultiSelectionSpinner(R.id.gpReasonSpinner));
    }

    @Override
    protected void initiateEditTexts() {
        //gp visit
        jsonEditTextMap.put("pulse",              getEditText(R.id.gpPulseValue));
        jsonEditTextMap.put("bpSystolic",         getEditText(R.id.gpBloodPresserValueSystolic));
        jsonEditTextMap.put("bpDiastolic",        getEditText(R.id.gpBloodPresserValueDiastolic));
        jsonEditTextMap.put("weight",             getEditText(R.id.gpWeightValue));
        jsonEditTextMap.put("temperature",        getEditText(R.id.gpTemperatureValue));
        jsonEditTextMap.put("hemoglobin",         getEditText(R.id.gpHemoglobinValue));
        jsonEditTextMap.put("other",              getEditText(R.id.gpOtherEditText));

        getEditText(R.id.gpTemperatureValue).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.gpTemperatureValue)));
        getEditText(R.id.gpBloodPresserValueSystolic).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.gpBloodPresserValueSystolic)));
        getEditText(R.id.gpBloodPresserValueSystolic).setOnFocusChangeListener(new CustomFocusChangeListener(this));
        getEditText(R.id.gpBloodPresserValueDiastolic).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.gpBloodPresserValueDiastolic)));
        getEditText(R.id.gpBloodPresserValueDiastolic).setOnFocusChangeListener(new CustomFocusChangeListener(this));
    }

    @Override
    protected void initiateTextViews() {
        getTextView(R.id.gpServiceDateLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.gpServiceDateLabel).getText().toString(), 0, 1));
        getTextView(R.id.gpReferCenterNameLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.gpReferCenterNameLabel).getText().toString(), 0, 1));
        getTextView(R.id.gpReasonLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.gpReasonLabel).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("visitDate", getEditText(R.id.gpServiceDateValue));
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


    public void saveGp() {
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            getSpecialCases(json);
            gpInfoUpdate = new AsyncGpInfoUpdate(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    handleResponse(result);
                }
            }, this);
            gpInfoUpdate.execute(json.toString(), SERVLET, ROOTKEY);

            Log.i("GP", "Save Succeeded");
            Log.d("GP", "JSON:\n" + json.toString());
            Utilities.Reset(this, R.id.gpText);
            getButton(R.id.gpSaveButton).setText("Save");
            getButton(R.id.gpDeleteButton).setVisibility(View.GONE);
            getButton(R.id.gpCancelButton).setVisibility(View.GONE);
            editMode = false;

        } catch (JSONException jse) {
            Log.e("GP", "JSON Exception: " + jse.getMessage());
        }
    }

    public void getSpecialCases(JSONObject json) {
        try {
            json.put("sateliteCenterName", provider.getSatelliteName()); //If the service was given from satellite
            //if(jsonSpinnerMap.get("serviceSource").getVisibility() != View.VISIBLE) {
            json.put("serviceSource", "");
            //}
            json.put("mobileNo",patient.getMobile());
            json.put("age",patient.getAge());
            //TODO should remove the follwoing codes
            json.put("urineSugar","");
            json.put("urineAlbumin","");
            json.put("complicationSign","");
            if(editMode){
                json.put("serviceId",serviceId);
            }

        } catch (JSONException jse) {

        }
    }

    private void showHideHistoryModifyButton(JSONObject jso) {
        Utilities.SetVisibility(this, R.id.modifyLastGPButton, isLastGpModifiable(jso) ? View.VISIBLE :View.INVISIBLE);
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthId:" + patient.getHealthId() + "," +
                (isRetrieval ? "": "providerId:\""+String.valueOf(provider.getProviderCode())+"\",") +
                "gpLoad:" + (isRetrieval? "retrieve":(editMode?"update":"\"\"")) +
                "}";
        return new JSONObject(queryString);
    }

    private boolean isLastGpModifiable(JSONObject jso) {
        String lastGpKey = String.valueOf(lastGpVisit);

        JSONObject lastVisit = null;
        String providerCode = null;
        boolean thresholdPeriodPassed = false;
        try {
            lastVisit = jso.getJSONObject(lastGpKey);
            providerCode = lastVisit.getString("providerId");
            if(lastVisit.has("systemEntryDate") && !lastVisit.getString("systemEntryDate").equals("")){
                Date lastVisitEntryDate = Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,lastVisit.getString("systemEntryDate"));
                thresholdPeriodPassed = Utilities.getDateDiff(lastVisitEntryDate,new Date(), TimeUnit.DAYS)> Flag.UPDATE_THRESHOLD;
            }

            Log.d(LOGTAG,"Last Service "+lastGpKey+" was provide by: \t" + providerCode);
            return (provider.getProviderCode().equals(providerCode) && !thresholdPeriodPassed);

            //return (provider.getProviderCode().equals(providerCode));

        } catch (JSONException jse) {
            Log.e(LOGTAG, String.format(" JSON Error (GP History): %s", jse.getMessage()));
            Utilities.printTrace(jse.getStackTrace(), 10);
        } catch (ParseException pse) {
            Log.e(LOGTAG, String.format(" Parse Error (GP History): %s", pse.getMessage()));
            Utilities.printTrace(pse.getStackTrace(), 10);
        }

        return false;
    }

    private void deleteConfirmed() {
        try {

            JSONObject deleteJson = buildQueryHeader(false);
            deleteJson.put("gpLoad", "delete");
            gpInfoUpdate = new AsyncGpInfoUpdate(this, this);
            gpInfoUpdate.execute(deleteJson.toString(), SERVLET, ROOTKEY);

            Utilities.Reset(this, R.id.gpText);
            getButton(R.id.gpSaveButton).setText("Save");
            getButton(R.id.gpDeleteButton).setVisibility(View.GONE);
            getButton(R.id.gpCancelButton).setVisibility(View.GONE);
            editMode = false;
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete GP request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    private void setLastServiceData(){
        Utilities.setEditTexts(jsonEditTextMap,lastServiceJSON);
        Utilities.setEditTextDates(jsonEditTextDateMap,lastServiceJSON);
        Utilities.setSpinners(jsonSpinnerMap,lastServiceJSON);
        Utilities.setCheckboxes(jsonCheckboxMap,lastServiceJSON);
        Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap,lastServiceJSON);
        Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap,lastServiceJSON);
        try {
            serviceId = lastServiceJSON.getInt("serviceId");
        } catch (JSONException e) {
            e.printStackTrace();
            serviceId = 0;
        }

        getTextView(R.id.gpVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastGpVisit)));


        getButton(R.id.gpSaveButton).setText("Update");
        getButton(R.id.gpDeleteButton).setVisibility(View.VISIBLE);
        getButton(R.id.gpCancelButton).setVisibility(View.VISIBLE);
        Utilities.Disable(this,R.id.gpServiceDate);
        editMode = true;
    }

    public void deleteLastGP() {
        AlertDialog alertDialog = new AlertDialog.Builder(GeneralPatientActivity.this).create();
        alertDialog.setIcon(android.R.drawable.ic_delete);
        alertDialog.setTitle("Delete Service?");
        alertDialog.setMessage(getString(R.string.ServiceDeletionWarning));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteConfirmed();
                    }
                });

        alertDialog.show();
    }

    void setHistoryLabelMapping() {
        //The array primarily contains 3 things, the string array from resource id, lower limit, upper limit
        historyLabelMap.put("visitDate",  Pair.create(getString(R.string.date), new Integer[] {0}));
        historyLabelMap.put("symptom",Pair.create(getString(R.string.gp_drawback), new Integer[] {Constants.SYMPTOM_LIST_IDENTIFIER, 0, 0}));

        historyLabelMap.put("bpSystolic", Pair.create(getString(R.string.blood_presser), new Integer[] {0, 139, 89}));
        historyLabelMap.put("edema", Pair.create(getString(R.string.edema), new Integer[] {R.array.Jaundice_Edima_Dropdown, 3, 4}));
        historyLabelMap.put("weight",Pair.create(getString(R.string.weight),  new Integer[] {0,0,100}));
        historyLabelMap.put("temperature",Pair.create(getString(R.string.temperature),  new Integer[] {0,0,101}));

        historyLabelMap.put("pulse",Pair.create(getString(R.string.pulse),  new Integer[] {0,0,100}));
        historyLabelMap.put("anemia",Pair.create(getString(R.string.anemia),  new Integer[] {R.array.Anemia_Dropdown_Full, 3,4}));
        historyLabelMap.put("hemoglobin",Pair.create(getString(R.string.hemoglobin)+"(%)",  new Integer[] {0}));
        historyLabelMap.put("jaundice",Pair.create(getString(R.string.jaundice),  new Integer[] {R.array.Jaundice_Edima_Dropdown, 3, 4}));
        //historyLabelMap.put("urineSugar",Pair.create(getString(R.string.urine_test_sugar),  new Integer[] {R.array.Urine_Test_Dropdown}));
        //historyLabelMap.put("urineAlbumin",Pair.create(getString(R.string.urine_test_albumin),  new Integer[] {R.array.Urine_Test_Dropdown}));
        historyLabelMap.put("lungs",Pair.create(getString(R.string.lungs),  new Integer[] {R.array.Abnormality_Dropdown, 1, 2}));
        historyLabelMap.put("liver",Pair.create(getString(R.string.liver),  new Integer[] {R.array.Abnormality_Dropdown, 1, 2}));
        historyLabelMap.put("splin",Pair.create(getString(R.string.splin),  new Integer[] {R.array.Abnormality_Dropdown, 1, 2}));
        historyLabelMap.put("tonsil",Pair.create(getString(R.string.tonsil),  new Integer[] {R.array.Abnormality_Dropdown, 1, 2}));
        historyLabelMap.put("other",Pair.create(getString(R.string.other),  new Integer[] {0}));

        //historyLabelMap.put("complicationSign", Pair.create(getString(R.string.complication), new Integer[] {R.array.ANC_Complication_DropDown, 0, 0}));
        historyLabelMap.put("disease",Pair.create(getString(R.string.disease), new Integer[] {Constants.DISEASE_LIST_IDENTIFIER, 0, 0}));
        //historyLabelMap.put("treatment",Pair.create(getString(R.string.treatment), new Integer[] {R.array.Treatment_DropDown, 0, 0}));
        historyLabelMap.put("treatment",Pair.create(getString(R.string.treatment), new Integer[] {Constants.TREATMENT_LIST_IDENTIFIER, 0, 0}));
        historyLabelMap.put("advice", Pair.create(getString(R.string.advice), new Integer[] {R.array.GP_Advice_DropDown, 0, 0}));
        historyLabelMap.put("serviceSource",Pair.create(getString(R.string.other_source), new Integer[] {R.array.FacilityType_DropDown, 0, 0})); //Check
        historyLabelMap.put("refer",Pair.create(getString(R.string.refer), new Integer[] {0}));
        historyLabelMap.put("referCenterName", Pair.create(getString(R.string.history_c_name), new Integer[] {R.array.FacilityType_DropDown, 0, 0}));
        historyLabelMap.put("referReason",Pair.create(getString(R.string.referReason), new Integer[] {R.array.GP_Refer_Reason_DropDown, 0, 0}));
    }

    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialog(con,getResources().getString(R.string.GPservice));
    }

    private void showSerialNumber(JSONObject json) {
        try {
            if (json.has("regDate") && json.has("regSerialNo")) {
                CustomSimpleDateFormat sdf = new CustomSimpleDateFormat("yyyy-MM-dd");
                String regDate = json.getString("regDate");
                if (regDate.equals(""))
                    return;
                Date regD = sdf.parse(regDate);
                String regSerial = json.getString("regSerialNo") + "/"
                        + json.getString("regDate").split("-")[0].substring(2);
                regSerial += " \t " + new CustomSimpleDateFormat("dd/MM/yyyy").format(regD);
                getTextView(R.id.reg_NO).setText(Utilities.ConvertNumberToBangla(regSerial));
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error converting registration number");
            Utilities.printTrace(jse.getStackTrace());
        } catch (ParseException pe) {
            Log.e(LOGTAG, "Parsing Error converting registration date");
            Utilities.printTrace(pe.getStackTrace());
        }
    }

    private boolean hasTheRequiredFields() {
        boolean valid = true;
        boolean specialInvalid = false;

        if (!Validation.hasText(getEditText(R.id.gpServiceDateValue))) valid = false;

        if (getCheckbox(R.id.gpReferCheckBox).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.gpReferCenterNameSpinner)))
                specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.gpReasonSpinner)))
                specialInvalid = true;
        }
        if (!valid) {
            Utilities.showAlertToast(this,getString(R.string.GeneralSaveWarning));
            return false;
        } else if (specialInvalid) {
            MethodUtils.showSnackBar(findViewById(R.id.general_patient_activity), getResources().getString(R.string.refer_validation_message), true);
            return false;
        }
        else {
            return true;
        }

    }

    /**
     *
     * Option for recording Temperature in Celsius
     * @param view
     */
    public void tempInCelsius(View view)
    {
        Dialog celsiusDialog = new Dialog(GeneralPatientActivity.this);
        celsiusDialog.setContentView(R.layout.dialog_temp_celcius);
        celsiusDialog.setTitle("Temperature in Celsius");
        final EditText temp_celsius_value = (EditText)celsiusDialog.findViewById(R.id.temp_celsius_value);
        Button button_submit = (Button)celsiusDialog.findViewById(R.id.button_submit);

        button_submit.setOnClickListener(view1 -> {
            String tempCelsiusValue = temp_celsius_value.getText().toString();
            celsiusDialog.dismiss();
            EditText gpTemperatureValue =(EditText)findViewById(R.id.gpTemperatureValue);
            if (tempCelsiusValue != null && !tempCelsiusValue.isEmpty())
            {
                gpTemperatureValue.setText(String.valueOf(Converter.celsiusToFarenhite(Double.valueOf(tempCelsiusValue))));
            }
        });
        celsiusDialog.show();

    }

}
