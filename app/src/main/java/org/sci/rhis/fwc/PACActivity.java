package org.sci.rhis.fwc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.AsyncPACInfoUpdate;
import org.sci.rhis.model.PregWoman;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomFocusChangeListener;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.DisplayValue;
import org.sci.rhis.utilities.HistoryListMaker;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PACActivity extends ClinicalServiceActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, MinimumDeliveryInfoFragment.DeliverySavedListener {

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;
    private boolean editMode = false;
    private JSONObject lastServiceJSON;

    AsyncPACInfoUpdate pacInfoUpdateTask;

    private int pacSaveClick = 0,lastPacVisit = 0, serviceId=0;
    private String serviceProvider = "";
    private JSONObject jsonResponse = null;

    final private String SERVLET = "pac";
    final private String ROOTKEY = "PACInfo";

    private final String LOGTAG = "FWC-PAC";
    private MultiSelectionSpinner multiSelectionSpinner;

    private Context con;
    private PregWoman mother;
    private ProviderInfo provider;

    ExpandableListView expListView;

    List<String> listDataHeader;
    LinearLayout ll;
    private LinearLayout history_layout;
    private ArrayList<DisplayValue> displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pac);
        init();

    }

    private void init(){
        con = this;
        setMultiSelectSpinners();

        history_layout = (LinearLayout)(findViewById(R.id.historyFragmentLayout));

        Log.d("------>>>------------", "START-----PAC----" );
        ll = (LinearLayout)findViewById(R.id.llay);

        expListView = new ExpandableListView(this);
        ll.addView(expListView);

        initialize();

        datePickerDialog = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.Date_Picker_Button, (EditText) findViewById(R.id.pacServiceDateValue));
        datePickerPair.put(R.id.imageViewDeliveryDate, (EditText) findViewById(R.id.id_delivery_date));

        mother = getIntent().getParcelableExtra("PregWoman");
        provider = getIntent().getParcelableExtra("Provider");
        //pacvisit
        lastPacVisit=0;
        getTextView(R.id.pacVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPacVisit + 1)));

        if (mother.getAbortionInfo() == 0) {
            showAbortionLayout();
        } else {
            getAbortionInformation();
        }

        setCompositeMap("bpSystolic", "bpDiastolic");
        setHistoryLabelMapping();
    }

    private void getAbortionInformation() {
        AsyncPACInfoUpdate sendPostReqAsyncTask = new AsyncPACInfoUpdate(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                handleResponse(result);
            }
        },PACActivity.this);

        String queryString =   "{" +
                "pregNo:" + mother.getPregNo() + "," +
                "healthId:" + mother.getHealthId() + "," +
                "pacLoad:" + "retrieve" +
                "}";

        String servlet = "pac";
        String jsonRootkey = "PACInfo";
        Log.d(LOGTAG, "Mother Part:\n" + queryString);
        sendPostReqAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, queryString, servlet, jsonRootkey);
        //make history collapsible
        MethodUtils.makeHistoryCollapsible(getTextView(R.id.pacBlanLabelLabel),history_layout);
    }

    private void showAbortionLayout() {
        //Disable PAC and History Layout first
        Utilities.MakeInvisible(this, R.id.historyFragmentLayout);
        Utilities.MakeVisible(this, R.id.idMinDeliveryFragmentHolder);
        getTextView(R.id.pacHistoryLabel).setText(getText(R.string.str_title_label));
        Utilities.MakeInvisible(this, R.id.pacEntryMasterLayout);
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private void setMultiSelectSpinners() {
        final List<String> pncmdrawbacklist = Arrays.asList(getResources().getStringArray(R.array.PACProblemDropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDrawbackSpinner);
        multiSelectionSpinner.setItems(pncmdrawbacklist);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> pncmhematurialist = Arrays.asList(getResources().getStringArray(R.array.PACHematuriaDropdown));
        final List<String> dangersignlist = Arrays.asList(getResources().getStringArray(R.array.PAC_Complication_DropDown));
        final List<String> diseaselist = Arrays.asList(getResources().getStringArray(R.array.PAC_Disease_DropDown));
        //final List<String> treatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> advicelist = Arrays.asList(getResources().getStringArray(R.array.PAC_Advice_DropDown));
        final List<String> referreasonlist = Arrays.asList(getResources().getStringArray(R.array.PAC_Refer_Reason_DropDown));
        final List<String> pacabdomenlist = Arrays.asList(getResources().getStringArray(R.array.PACAbdomenDropdown));
        final List<String> paccervixlist = Arrays.asList(getResources().getStringArray(R.array.PACcervixDropDown));

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDangerSignsSpinner);
        if (multiSelectionSpinner == null) {
            Log.d("------" + dangersignlist.get(1), ".........");
        }
        multiSelectionSpinner.setItems(dangersignlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacAdviceSpinner);
        multiSelectionSpinner.setItems(advicelist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacReasonSpinner);
        multiSelectionSpinner.setItems(referreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacAbdomenSpinner);
        multiSelectionSpinner.setItems(pacabdomenlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacHematuriaSpinner);
        multiSelectionSpinner.setItems(pncmhematurialist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacCervixSpinner);
        multiSelectionSpinner.setItems(paccervixlist);
        multiSelectionSpinner.setSelection(new int[]{});

    }

    private void initPage() {
        expListView = new ExpandableListView(this);
        expListView.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        expListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView.setIndicatorBounds(0, 0);
        expListView.setChildIndicatorBounds(0, 0);
        expListView.setStackFromBottom(true);
    }

    public void handleResponse(String result) {
        try {
            jsonResponse = new JSONObject(result);
            Log.d(LOGTAG, "PAC Response Received:" + jsonResponse.toString());
            lastPacVisit = (jsonResponse.has("count") ? jsonResponse.getInt("count") : 0 );
            if(lastPacVisit>0) {
                serviceProvider = jsonResponse.getJSONObject(String.valueOf(lastPacVisit)).getString("providerId");
                lastServiceJSON = jsonResponse.getJSONObject(String.valueOf(lastPacVisit));
                showHideHistoryModifyButton();
            }else{
                //hide modify button if all the history is cleared by the provider
                Utilities.MakeInvisible(this, R.id.modifyLastPACButton);
            }
            getTextView(R.id.pacVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPacVisit + 1))); //next visit
            setAbortionDateAndPlace(result, "outcomeDate", "outcomePlace");
        } catch (JSONException jse) {
            Log.e(LOGTAG, "PAC Error:\n\t\t" + result);
            Utilities.printTrace(jse.getStackTrace());
        }

        ll.removeAllViews();
        Log.d(LOGTAG, "Handle Abortion:\n" + result);

        try {
            for (int i = 1; i <= lastPacVisit && lastPacVisit !=0 ; i++) {
                JSONObject singleVisitJson = jsonResponse.getJSONObject(String.valueOf(i));
                displayList = new ArrayList<>();

                HistoryListMaker<PACActivity>  historyListMaker = new HistoryListMaker<>(
                     this, /*activity*/
                     singleVisitJson, /*json containing keys*/
                     historyLabelMap, /*history details*/
                     compositeMap /*fields whose bvalues are given against nultiple keys*/
                );

                try {
                    displayList.addAll(historyListMaker.getDisplayList());
                } catch(Exception e) {
                    Log.e(LOGTAG, String.format("ERROR: %s" , e.getMessage()));
                    Utilities.printTrace(e.getStackTrace(), 10);
                }

                listDataHeader = new ArrayList<>();
                listDataHeader.add(getString(R.string.visit) + Utilities.ConvertNumberToBangla(String.valueOf(i)));

                HashMap<String, List<DisplayValue>> listDisplayValues = new HashMap<>();
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
                    getTextView(R.id.pacBlanLabelLabel).
                            setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                }
                //...........................................................................

                ll.invalidate();
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        }

    }

    void setItemVisible(int ItemId, boolean isChecked) {
        Spinner Item = (Spinner) findViewById(ItemId);
        Item.setSelection(0);
        findViewById(ItemId).setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void callbackAsyncTask(String result) {
        handleResponse(result);
    }

    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("refer", getCheckbox(R.id.pacReferCheckBox));
        getCheckbox(R.id.pacReferCheckBox).setOnCheckedChangeListener(this);

        /*getCheckbox(R.id.pacOtherCheckBox).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.pacOtherCheckBox:
                        setItemVisible(R.id.pacOtherCenterNameSpinner, isChecked);
                        break;
                }
            }
        });*/
    }

    protected void initiateEditTexts() {
        jsonEditTextMap.put("temperature", getEditText(R.id.pacTemperatureValue));
        jsonEditTextMap.put("bpSystolic", getEditText(R.id.pacBloodPresserValueSystolic));
        jsonEditTextMap.put("bpDiastolic", getEditText(R.id.pacBloodPresserValueDiastolic));
        jsonEditTextMap.put("hemoglobin", getEditText(R.id.pacHemoglobinValue));

        getEditText(R.id.pacBloodPresserValueSystolic).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.pacBloodPresserValueSystolic)));
        getEditText(R.id.pacBloodPresserValueSystolic).setOnFocusChangeListener(new CustomFocusChangeListener(this));
        getEditText(R.id.pacBloodPresserValueDiastolic).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.pacBloodPresserValueDiastolic)));
        getEditText(R.id.pacBloodPresserValueDiastolic).setOnFocusChangeListener(new CustomFocusChangeListener(this));
    }
    protected void initiateTextViews(){
        jsonTextViewsMap.put("outcomeDate", getTextView(R.id.pacDate));
        jsonTextViewsMap.put("outcomePlace", getTextView(R.id.pacPlace));
        getTextView(R.id.pacServiceDateLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.pacServiceDateLabel).getText().toString(), 0, 1));
        getTextView(R.id.pacReferCenterNameLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.pacReferCenterNameLabel).getText().toString(), 0, 1));
        getTextView(R.id.pacReasonLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.pacReasonLabel).getText().toString(), 0, 1));
    }

    protected void initiateSpinners() {
        jsonSpinnerMap.put("anemia", getSpinner(R.id.pacAnemiaSpinner));
        jsonSpinnerMap.put("uterusInvolution", getSpinner(R.id.pacUterusHeightSpinner));
        jsonSpinnerMap.put("perineum", getSpinner(R.id.pacPerineumSpinner));
        jsonSpinnerMap.put("FPMethod", getSpinner(R.id.pacFamilyPlanningMethodsSpinner));
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.pacReferCenterNameSpinner));
        jsonSpinnerMap.put("disease", getSpinner(R.id.pacDiseaseSpinner));
        //jsonSpinnerMap.put("serviceSource", getSpinner(R.id.pacOtherCenterNameSpinner));

    }

    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("symptom", getMultiSelectionSpinner(R.id.pacDrawbackSpinner));
        jsonMultiSpinnerMap.put("complicationSign", getMultiSelectionSpinner(R.id.pacDangerSignsSpinner));
        //jsonMultiSpinnerMap.put("disease", getMultiSelectionSpinner(R.id.pacDiseaseSpinner));
        //jsonMultiSpinnerMap.put("treatment", getMultiSelectionSpinner(R.id.pacTreatmentSpinner));
        jsonMultiSpinnerMap.put("advice", getMultiSelectionSpinner(R.id.pacAdviceSpinner));
        jsonMultiSpinnerMap.put("referReason", getMultiSelectionSpinner(R.id.pacReasonSpinner));
        jsonMultiSpinnerMap.put("cervix", getMultiSelectionSpinner(R.id.pacCervixSpinner));
        jsonMultiSpinnerMap.put("hematuria", getMultiSelectionSpinner(R.id.pacHematuriaSpinner));
        jsonMultiSpinnerMap.put("abdomen", getMultiSelectionSpinner(R.id.pacAbdomenSpinner));

        //getMultiSelectionSpinner(R.id.pacTreatmentSpinner).setEnabled(false);
    }

    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("visitDate", getEditText(R.id.pacServiceDateValue));
    }

    protected void initiateRadioGroups() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Date_Picker_Button:
                datePickerDialog.show(datePickerPair.get(v.getId()));

                break;
            case R.id.pacSaveButton:
                if(!Validation.hasText(getEditText(R.id.pacServiceDateValue))) {
                    return;
                }
                if (!hasTheRequiredFileds()) {
                    return;
                }
                try {
                    String cVisitDate=getEditText(R.id.pacServiceDateValue).getText().toString();
                    if(lastPacVisit>0 && !Validation.isValidVisitDate(this,cVisitDate,lastServiceJSON.getString("visitDate"),false,true)){
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pacSaveMode();

                break;
            case R.id.pacEditButton:
                pacEditMode();
                break;

            case R.id.modifyLastPACButton:
                setLastServiceData();
                break;

            case R.id.pacCancelButton:
                cancelPAC();
                break;

            case R.id.pacDeleteButton:
                deleteLastPAC();
                break;

            default:
                break;
        }
    }

    private void cancelPAC(){
        Utilities.Reset(this, R.id.pacText);
        getTextView(R.id.pacVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPacVisit >= 0 ? lastPacVisit + 1 : 1)));
        getButton(R.id.pacSaveButton).setText(getText(R.string.string_save));
        getButton(R.id.pacDeleteButton).setVisibility(View.GONE);
        getButton(R.id.pacCancelButton).setVisibility(View.GONE);
        editMode = false;
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
        getTextView(R.id.pacVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPacVisit)));


        getButton(R.id.pacSaveButton).setText(getText(R.string.string_update));
        getButton(R.id.pacDeleteButton).setVisibility(View.VISIBLE);
        getButton(R.id.pacCancelButton).setVisibility(View.VISIBLE);
        Utilities.Disable(this,R.id.pacServiceDate);
        editMode = true;
    }

    private void pacSaveMode(){
        pacSaveClick++;
        if( pacSaveClick == 2 ) {
            pacSaveToJson();
            getButton(R.id.pacSaveButton).setText(getText(R.string.string_save));
            Utilities.Enable(this, R.id.pacEntryMasterLayout);
            Utilities.MakeInvisible(this, R.id.pacEditButton);
            pacSaveClick = 0;
        }

        else if(pacSaveClick == 1) {

            Utilities.Disable(this, R.id.pacEntryMasterLayout);
            getButton( R.id.pacSaveButton).setText(getText(R.string.string_confirm));
            Utilities.Enable(this, R.id.pacSaveButton);
            getButton( R.id.pacEditButton).setText(getText(R.string.string_cancel));
            Utilities.Enable(this, R.id.pacEditButton);
            Utilities.MakeVisible(this, R.id.pacEditButton);

            Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);
        }

    }

    private void pacEditMode(){
        if(pacSaveClick == 1) {
            pacSaveClick = 0;
            Utilities.Enable(this, R.id.pacEntryMasterLayout);
            getButton(R.id.pacSaveButton).setText(editMode?getText(R.string.string_update):getText(R.string.string_save));
            Utilities.MakeInvisible(this, R.id.pacEditButton);
        }
    }

    private void setAbortionDateAndPlace(String result, String dateKey, String placeKey) {
        try {
            JSONObject jso = new JSONObject(result);
            getTextView(R.id.pacDate).setText(Utilities.getDateStringUIFormat(jso.getString(dateKey)));
            //changing date picker dialog for blocking previous dates of abortion
            datePickerDialog = null;
            CustomSimpleDateFormat sdf = new CustomSimpleDateFormat(Constants.SHORT_SLASH_FORMAT_BRITISH);
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(Utilities.getDateStringUIFormat(jso.getString(dateKey))));
            datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH,c.getTime());
            //......................................................................
            getTextView(R.id.pacPlace).setText(getResources().getStringArray(R.array.FacilityPlace_DropDown)[Integer.valueOf(jso.getString(placeKey))]);
        } catch (JSONException jse) {
            Log.e(LOGTAG, String.format("JSON Parsing Error: %s", jse.getMessage()));
        } catch (Exception e ) {
            Log.e(LOGTAG, String.format("Unknown Error: %s", e.getMessage()));
        }
    }

    @Override
    public void onDeliverySaved(String result) {

        if(!result.equals("cancel")) {
            Log.d(LOGTAG, "Abortion Info saved response:\n\t\t" + result);
            Utilities.MakeInvisible(this, R.id.idMinDeliveryFragmentHolder);
            Utilities.MakeVisible(this, R.id.pacEntryMasterLayout);
            Utilities.MakeVisible(this, R.id.historyFragmentLayout);
            Utilities.Enable(this, R.id.historyFragmentLayout);
            Utilities.Enable(this, R.id.pacEntryMasterLayout);
            getTextView(R.id.pacHistoryLabel).setText(getText(R.string.str_pac_history_label));
            mother.setAbortionInfo(1);
            setAbortionDateAndPlace(result, "dDate", "dPlace");
            //Resetting full layout
            int layouts[] = {R.id.pacReferCenterNameLayout, R.id.pacReasonLayout};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],View.GONE);
            }
            getButton(R.id.pacEditButton).setVisibility(View.GONE);
            getButton(R.id.pacDeleteButton).setVisibility(View.GONE);
            getButton(R.id.pacCancelButton).setVisibility(View.GONE);
            editMode = false;
        } else {
            onBackPressed(); //Ask to exit the PAC activity
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /*if (buttonView.getId() == R.id.pacOtherCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;

            if(!isChecked)
                getSpinner(R.id.pacOtherCenterNameSpinner).setSelection(0);
            else
                getSpinner(R.id.pacOtherCenterNameSpinner).setSelection(0);

            getSpinner(R.id.pacOtherCenterNameSpinner).setVisibility(visibility);

        }*/

        if (buttonView.getId() == R.id.pacReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            int layouts[] = {R.id.pacReferCenterNameLayout, R.id.pacReasonLayout};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],visibility);
            }
        }
    }

    private void pacSaveToJson() {
        pacInfoUpdateTask = new AsyncPACInfoUpdate(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                handleResponse(result);
            }
        },PACActivity.this);
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            //............
            String sateliteCenter=provider.getSatelliteName();
            json.put("sateliteCenterName", sateliteCenter == null ? "" : sateliteCenter);
            if(editMode){
                json.put("serviceId",serviceId);
            }
            json.put("treatment","");
            json.put("serviceSource","");
            //..........
            pacInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);

            System.out.print("PAC Save Json in Servlet:" + ROOTKEY + ":{" + json.toString() + "}");

            Utilities.Reset(this, R.id.pacText);
            getButton(R.id.pacDeleteButton).setVisibility(View.GONE);
            getButton(R.id.pacCancelButton).setVisibility(View.GONE);
            editMode = false;

        } catch (JSONException jse) {
            Log.e("PAC JSON Exception: ", jse.getMessage());
        }

    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString = "{" +
                "healthId:" + mother.getHealthId() + "," +
                (isRetrieval ? "" : "providerId:\"" + String.valueOf(provider.getProviderCode()) + "\",") +
                "pregNo:" + mother.getPregNo() + "," +
                "pacLoad:" + (isRetrieval? "retrieve":(editMode?"update":"\"\"")) +
                "}";

        return new JSONObject(queryString);
    }

    private void showHideHistoryModifyButton() {
        Log.d("provider Id's", provider.getProviderCode().toString() + " & " + serviceProvider);
        Utilities.SetVisibility(this, R.id.modifyLastPACButton, ((lastPacVisit > 0) && provider.getProviderCode().equals(serviceProvider)) ? View.VISIBLE : View.GONE);
    }

    private void deleteConfirmed() {
        try {
            JSONObject deleteJson = buildQueryHeader(false);
            deleteJson.put("pacLoad", "delete");

            pacInfoUpdateTask = new AsyncPACInfoUpdate(this,this);
            pacInfoUpdateTask.execute(deleteJson.toString(), SERVLET, ROOTKEY);

            Utilities.Reset(this, R.id.pacText);
            getButton(R.id.pacSaveButton).setText("Save");
            getButton(R.id.pacDeleteButton).setVisibility(View.GONE);
            getButton(R.id.pacCancelButton).setVisibility(View.GONE);
            editMode = false;
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete PAC request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    public void deleteLastPAC() {
        AlertDialog alertDialog = new AlertDialog.Builder(PACActivity.this).create();
        alertDialog.setIcon(android.R.drawable.ic_delete);
        alertDialog.setTitle("Delete Service?");
        alertDialog.setMessage(getString(R.string.ServiceDeletionWarning));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
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

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(PACActivity.this).create();
        alertDialog.setTitle(getText(R.string.str_exit_label));
        alertDialog.setMessage(getString(R.string.str_exit_confirmation));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent finishIntent = new Intent();

                        finishIntent.putExtra("hasAbortionInformation", mother.getAbortionInfo()==1?true:false);

                        setResult(RESULT_OK, finishIntent);
                        finishActivity(ActivityResultCodes.PAC_ACTIVITY);
                        finish();
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    void setHistoryLabelMapping() {
        //The array primarily contains 3 things, the string array from resource id, lower limit, upper limit
        historyLabelMap.put("visitDate",  Pair.create(getString(R.string.date), new Integer[] {0}));
        historyLabelMap.put("symptom", Pair.create(getString(R.string.complicationsign), new Integer[]{R.array.PACProblemDropDown}));
        historyLabelMap.put("temperature",Pair.create(getString(R.string.temperature), new Integer[]{0}) );
        historyLabelMap.put("bpSystolic", Pair.create(getString(R.string.bpSystolic), new Integer[]{0, 139, 89}));
        historyLabelMap.put("anemia", Pair.create(getString(R.string.anemia), new Integer[]{R.array.Anemia_Dropdown}));
        historyLabelMap.put("hemoglobin", Pair.create(getString(R.string.hemoglobin), new Integer[]{0, 20, 100}));
        historyLabelMap.put("abdomen", Pair.create(getString(R.string.abdomen), new Integer[]{R.array.PACAbdomenDropdown}));
        historyLabelMap.put("uterusInvolution", Pair.create(getString(R.string.uterusInvolution), new Integer[]{R.array.PACUterusDropdown}));
        historyLabelMap.put("cervix", Pair.create(getString(R.string.cervix), new Integer[]{R.array.PACcervixDropDown}));
        historyLabelMap.put("hematuria", Pair.create(getString(R.string.hematuria), new Integer[]{R.array.PACHematuriaDropdown}));

        historyLabelMap.put("perineum", Pair.create(getString(R.string.perineum), new Integer[]{R.array.Perineum_DropDown}));
        historyLabelMap.put("FPMethod", Pair.create(getString(R.string.family_planning_methods), new Integer[]{R.array.Family_Planning_Methods_DropDown}));

        //historyLabelMap.put("serviceSource", Pair.create(getString(R.string.other_source), new Integer[]{R.array.FacilityType_DropDown}));

        historyLabelMap.put("complicationSign", Pair.create(getString(R.string.complication), new Integer[]{R.array.PAC_Complication_DropDown})); //Check

        historyLabelMap.put("disease", Pair.create(getString(R.string.disease), new Integer[]{R.array.PAC_Disease_DropDown}));
        //historyLabelMap.put("treatment", Pair.create(getString(R.string.treatment), new Integer[]{R.array.Treatment_DropDown}));
        historyLabelMap.put("advice", Pair.create(getString(R.string.advice), new Integer[]{R.array.PAC_Advice_DropDown}));
        historyLabelMap.put("refer", Pair.create(getString(R.string.refer), new Integer[]{0}));
        historyLabelMap.put("referCenterName", Pair.create(getString(R.string.referCenterName), new Integer[]{R.array.FacilityType_DropDown}));
        historyLabelMap.put("referReason", Pair.create(getString(R.string.referReason), new Integer[]{R.array.PAC_Refer_Reason_DropDown}));

    }

    private boolean hasTheRequiredFileds() {
        boolean valid = true;
        boolean specialInvalid = false;

        if (getCheckbox(R.id.pacReferCheckBox).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.pacReferCenterNameSpinner)))
                specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.pacReasonSpinner)))
                specialInvalid = true;
        }
        if (!valid) {
            Toast toast = Toast.makeText(this, R.string.GeneralSaveWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        } else if (specialInvalid) {
            MethodUtils.showSnackBar(findViewById(R.id.pac_activity_layout), getResources().getString(R.string.refer_validation_message), true);
            return false;
        }
        else {
            return true;
        }

    }

}
