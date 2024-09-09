package org.sci.rhis.fwc;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncImplantInfoUpate;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.DisplayValue;
import org.sci.rhis.utilities.HistoryListMaker;
import org.sci.rhis.utilities.MethodUtils;
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

/**
 * Created by arafat.hasan on 3/10/2016.
 */
public class ImplantFollowupActivity extends ClinicalServiceActivity implements CompoundButton.OnCheckedChangeListener,
        CustomTextWatcher.CustomWatcherListner,
        AlertDialogCreator.DialogButtonClickedListener{

    //Variable declarations.................................................................................

    private Context mContext;
    private ProviderInfo provider;

    final private String SERVLET = "implant";
    final private String ROOTKEY = "implantInfo";
    private String LOGTAG = "FP-Implant_FOLLOWUP";
    int implantCount=0;
    private boolean isFixed = false;
    int range=0;
    String healthId,providerId,mobileNo,sateliteCenterName,clientName;
    private boolean editMode = false;
    private JSONObject lastServiceJSON;

    LinearLayout historyContentLayout;
    LinearLayout history_layout;

    private int countSaveClick=0;

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;
    private int fixedCount=0,serviceId=0,notFixedCount=0;

    AsyncImplantInfoUpate asyncImplantInfoUpate;

    private MultiSelectionSpinner multiSelectionSpinner;

    JSONObject jsonStr;
    private int lastImplantFollowup;
    private HashMap<Integer, Pair<Date, Date>> dateRangeMap;

    ExpandableListView expandableListViewImplant;
    List<String> listDataHeader;

    private enum Durations{
        FIXED_FIRST1(23),FIXED_FIRST2(37),FIXED_SECOND1(5),FIXED_SECOND2(7),FIXED_THIRD1(11),FIXED_THIRD2(13);
        private int value;
        Durations(int value) {
            this.value = value;
        }
    }

    //......................................................................................................

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_implant_followup);
        initialInit();
    }

    private void initialInit(){
        mContext = this;
        lastImplantFollowup=0;

        initialize();
        setPredefinedValues();
        setHistoryLabelMapping();
        loadHistory();
    }

    private void setPredefinedValues(){
        provider = getIntent().getParcelableExtra(Constants.KEY_PROVIDER);
        providerId = String.valueOf(provider.getProviderCode());
        healthId  = getIntent().getStringExtra(Constants.KEY_HEALTH_ID);
        mobileNo = getIntent().getStringExtra(Constants.KEY_MOBILE);
        implantCount = getIntent().getIntExtra(Constants.KEY_Implant_COUNT, 0);
        sateliteCenterName = provider.getSatelliteName();
        if(getIntent().hasExtra(Constants.KEY_CLIENT_NAME)){
            clientName=getIntent().getStringExtra(Constants.KEY_CLIENT_NAME);
        }else{
            clientName="";
        }
        getEditText(R.id.implantClientNameEditText).setText(clientName);

        setSuggestedFollowupDates();

    }

    private void setSuggestedFollowupDates(){
        try {
            Date implantDate = Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,
                    getIntent().getStringExtra(Constants.KEY_Implant_IMPLANT_DATE));
            CustomSimpleDateFormat sdf = new CustomSimpleDateFormat(Constants.SPECIAL_FORMAT_1);
            dateRangeMap = new HashMap<>();
            dateRangeMap.put(1,Pair.create(Utilities.addDateOffset(implantDate, Durations.FIXED_FIRST1.value),
                    Utilities.addDateOffset(implantDate, Durations.FIXED_FIRST2.value)));
            dateRangeMap.put(2,Pair.create(Utilities.manipulateDateByMonth(implantDate,Durations.FIXED_SECOND1.value,Constants.ADD_CODE),
                    Utilities.manipulateDateByMonth(implantDate,Durations.FIXED_SECOND2.value,Constants.ADD_CODE)));
            dateRangeMap.put(3,Pair.create(Utilities.manipulateDateByMonth(implantDate,Durations.FIXED_THIRD1.value,Constants.ADD_CODE),
                    Utilities.manipulateDateByMonth(implantDate,Durations.FIXED_THIRD2.value,Constants.ADD_CODE)));

            getTextView(R.id.implantFollowup1Date).setText(sdf.format(dateRangeMap.get(1).first)+" - "+sdf.format(dateRangeMap.get(1).second));
            getTextView(R.id.implantFollowup2Date).setText(sdf.format(dateRangeMap.get(2).first)+" - "+sdf.format(dateRangeMap.get(2).second));
            getTextView(R.id.implantFollowup3Date).setText(sdf.format(dateRangeMap.get(3).first)+" - "+sdf.format(dateRangeMap.get(3).second));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("providerAsAttendant",getCheckbox(R.id.implantFollowupMyselfCheckbox));
        jsonCheckboxMap.put("refer", getCheckbox(R.id.implantReferCheckBox));
        jsonCheckboxMap.put("allowance", getCheckbox(R.id.implantFollowupAllowanceGivenCheckBox));

        getCheckbox(R.id.implantFPMethodGivenCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.implantFollowupMyselfCheckbox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.implantRemoveCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.implantRemoveMyselfCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.implantReferCheckBox).setOnCheckedChangeListener(this);
    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("attendantName", getEditText(R.id.implantFollowupAttendantNameEditText));
        jsonEditTextMap.put("implantRemoverName", getEditText(R.id.implantRemoverNameEditText));
        jsonEditTextMap.put("fpAmount", getEditText(R.id.implantFPAmountEditText));
        jsonEditTextMap.put("monitoringOfficerName", getEditText(R.id.implantMonitoringOfficerEditText));
        jsonEditTextMap.put("comment", getEditText(R.id.implantMonitorCommentEditText));
    }

    @Override
    protected void initiateTextViews() {
        getTextView(R.id.implantFollowupValueTextView).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastImplantFollowup >= 0 ? lastImplantFollowup + 1 : 1)));

        getTextView(R.id.implantFollowupDateTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.implantFollowupDateTextView).getText().toString(), 0, 1));
        getTextView(R.id.implantReferCenterNameTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.implantReferCenterNameTextView).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("implantRemoverDesignation",getSpinner(R.id.implantRemoverDesignationSpinner));
        jsonSpinnerMap.put("fpMethod",getSpinner(R.id.implantFPMethodSpinner));
        jsonSpinnerMap.put("referCenterName",getSpinner(R.id.implantReferCenterNameSpinner));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("complication", getMultiSelectionSpinner(R.id.implantComplicacySpinner));
        jsonMultiSpinnerMap.put("treatment", getMultiSelectionSpinner(R.id.implantFollowupTreatmentSpinner));
        jsonMultiSpinnerMap.put("management", getMultiSelectionSpinner(R.id.implantManagementSpinner));
        jsonMultiSpinnerMap.put("implantRemoveReason", getMultiSelectionSpinner(R.id.implantRemoveReasonSpinner));
        jsonMultiSpinnerMap.put("referReason", getMultiSelectionSpinner(R.id.implantChildReasonSpinner));

        final List<String> removeReasonList = Arrays.asList(getResources().getStringArray(R.array.Implant_Remove_Reason_DropDown));

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.implantRemoveReasonSpinner);
        multiSelectionSpinner.setItems(removeReasonList);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> complicacyList = Arrays.asList(getResources().getStringArray(R.array.Implant_Complication_DropDown));
        final List<String> treatmentList = Arrays.asList(getResources().getStringArray(R.array.Implant_Treatment_DropDown));
        final List<String> managementList = Arrays.asList(getResources().getStringArray(R.array.Implant_Management_DropDown));
        final List<String> referReasonList = Arrays.asList(getResources().getStringArray(R.array.Implant_Refer_Reason_DropDown));

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.implantComplicacySpinner);
        multiSelectionSpinner.setItems(complicacyList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.implantFollowupTreatmentSpinner);
        multiSelectionSpinner.setItems(treatmentList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.implantManagementSpinner);
        multiSelectionSpinner.setItems(managementList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.implantChildReasonSpinner);
        multiSelectionSpinner.setItems(referReasonList);
        multiSelectionSpinner.setSelection(new int[]{});
    }

    @Override
    protected void initiateEditTextDates() {
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH);
        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.implantFollowupDatePickerButton, (EditText) findViewById(R.id.implantFollowupDateEditText));
        datePickerPair.put(R.id.implantRemoveDatePickerButton, (EditText) findViewById(R.id.implantRemoveDateEditText));

        jsonEditTextDateMap.put("followupDate", getEditText(R.id.implantFollowupDateEditText));
        getEditText(R.id.implantFollowupDateEditText).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.implantFollowupDateEditText)));
        jsonEditTextDateMap.put("implantRemoveDate", getEditText(R.id.implantRemoveDateEditText));
    }

    @Override
    public void onTextChanged() {
        if(!editMode){
            Date followupDate = null;
            String givenDate = getEditText(R.id.implantFollowupDateEditText).getEditableText().toString();
            if(givenDate.equals("")){
                return;
            }
            range=0;
            try {
                followupDate = Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,givenDate);
            } catch (ParseException e) {
                e.printStackTrace();
                getTextView(R.id.implantFollowupLabel).setText("");
            }
            if(fixedCount<3 && followupDate!=null){
                CheckDateRange:
                for(int i=fixedCount+1; i<=3; i++ ){
                    boolean isFound = Utilities.checkDateInRange(dateRangeMap.get(i).first,
                            dateRangeMap.get(i).second, followupDate);
                    if(isFound){
                        range = i;
                        break CheckDateRange;
                    }
                }
            }
            if(range!=0){
                isFixed = true;
                getTextView(R.id.implantFollowupLabel).setText(getResources().getString(R.string.fixed_followup)+
                        ": "+ Utilities.ConvertNumberToBangla(String.valueOf(range)));
            }else{
                isFixed = false;
                getTextView(R.id.implantFollowupLabel).setText(getResources().getString(R.string.unfixed_followup)+
                        ": "+ Utilities.ConvertNumberToBangla(String.valueOf(notFixedCount+1)));
            }
        }

    }

    @Override
    protected void initiateRadioGroups() {

    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean checked) {
        int visibility = checked? View.VISIBLE: View.GONE;
        switch (view.getId()){
            case R.id.implantFollowupMyselfCheckbox:
                if(checked) {
                    getEditText(R.id.implantFollowupAttendantNameEditText).setText(provider.getProviderName());
                    Utilities.Disable(this, R.id.implantFollowupAttendantNameEditText);
                }else {
                    getEditText(R.id.implantFollowupAttendantNameEditText).setText("");
                    Utilities.Enable(this, R.id.implantFollowupAttendantNameEditText);
                }
                break;
            case R.id.implantRemoveCheckBox:
                int removeLayouts[] = {R.id.implantRemoverNameLayout, R.id.implantRemoverDesignationLayout, R.id.implantRemoveDateLayout, R.id.implantRemoveReasonLayout, R.id.implantFPMethodGivenLayout};
                changeLayoutVisibility(visibility , removeLayouts);
                break;

            case R.id.implantRemoveMyselfCheckBox:
                if(checked) {
                    getEditText(R.id.implantRemoverNameEditText).setText(provider.getProviderName());
                    getSpinner(R.id.implantRemoverDesignationSpinner).setSelection(1);
                    Utilities.Disable(this, R.id.implantRemoverNameEditText);
                    Utilities.Disable(this, R.id.implantRemoverDesignationLayout);
                }else {
                    getEditText(R.id.implantRemoverNameEditText).setText("");
                    getSpinner(R.id.implantRemoverDesignationSpinner).setSelection(0);
                    Utilities.Enable(this, R.id.implantRemoverNameEditText);
                    Utilities.Enable(this, R.id.implantRemoverDesignationLayout);
                }
                break;

            case R.id.implantFPMethodGivenCheckBox:
                int fpLayouts[] = {R.id.implantFPMethodLayout, R.id.implantFPAmountLayout};
                changeLayoutVisibility(visibility , fpLayouts);
                break;

            case R.id.implantReferCheckBox:
                int referLayouts[] = {R.id.implantReferCenterNameLayout, R.id.implantReasonLayout};
                changeLayoutVisibility(visibility , referLayouts);
                if(visibility==View.VISIBLE){
                    Utilities.SetVisibility(ImplantFollowupActivity.this,R.id.implantReasonLayout,View.GONE);
                }
                break;
            default:
                break;
        }

    }

    private void changeLayoutVisibility(int visibility, int[] layouts){
        for (int layout : layouts) {
            Utilities.SetVisibility(this, layout, visibility);
        }
    }

    private void loadHistory(){
        historyContentLayout = (LinearLayout)findViewById(R.id.llay);
        history_layout = (LinearLayout)findViewById(R.id.history_lay_implant);
        expandableListViewImplant = new ExpandableListView(this);

        JSONObject json;
        try {
            json = buildQueryHeader(true);
            asyncImplantInfoUpate = new AsyncImplantInfoUpate(this, this);
            asyncImplantInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("ImpFo JSON Exception: ", jse.getMessage());
        }

        MethodUtils.makeHistoryCollapsible(getTextView(R.id.implantFOllowupBlanLabelLabel),history_layout);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.implantFollowupSaveButton:
                if(!Validation.hasText(jsonEditTextDateMap.get("followupDate"))) {
                    return;
                }
                if (!hasTheRequiredFileds()) {
                    return;
                }
                if(!editMode){
                    try {
                        String cVisitDate=jsonEditTextDateMap.get("followupDate").getText().toString();
                        if(lastImplantFollowup>0 && !Validation.isValidVisitDate(this,cVisitDate,lastServiceJSON.getString("followupDate"),false,true)){
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                saveImplantFollowup();

                break;
            case R.id.implantFollowupEditButton:
                editImplantFollowup();
                break;

            case R.id.implantFollowupDeleteButton:
                deleteLastImplantFollowup();
                break;

            case R.id.modifyLastImplantFollowupButton:
                setLastServiceData();
                break;

            case R.id.implantFollowupEdiCancelButton:
                resetActivity();
                break;
        }
    }

    private void saveImplantFollowup(){
        countSaveClick++;
        if( countSaveClick == 2 ) {
            implantFollowupSaveAsJson();
            getButton(R.id.implantFollowupSaveButton).setText(getText(R.string.string_save));
            Utilities.Enable(this, R.id.implantFollowupEntryMasterLayout);
            Utilities.MakeInvisible(this, R.id.implantFollowupEditButton);
            countSaveClick = 0;
        }

        else if(countSaveClick == 1) {

            Utilities.Disable(this, R.id.implantFollowupEntryMasterLayout);
            getButton( R.id.implantFollowupSaveButton).setText(getText(R.string.string_confirm));
            Utilities.Enable(this, R.id.implantFollowupSaveButton);
            getButton( R.id.implantFollowupEditButton).setText(getText(R.string.string_cancel));
            Utilities.Enable(this, R.id.implantFollowupEditButton);
            Utilities.MakeVisible(this, R.id.implantFollowupEditButton);

            Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);
        }

    }

    private void editImplantFollowup(){
        if(countSaveClick == 1) {
            countSaveClick = 0;
            Utilities.Enable(this, R.id.implantFollowupEntryMasterLayout);
            getButton(R.id.implantFollowupSaveButton).setText(editMode?"Update":"Save");
            Utilities.MakeInvisible(this, R.id.implantFollowupEditButton);
        }
    }

    private void setLastServiceData(){
        editMode = true;
        Utilities.setEditTexts(jsonEditTextMap,lastServiceJSON);
        Utilities.setEditTextDates(jsonEditTextDateMap,lastServiceJSON);
        Utilities.setSpinners(jsonSpinnerMap,lastServiceJSON);
        Utilities.setCheckboxes(jsonCheckboxMap,lastServiceJSON);
        Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap,lastServiceJSON);
        //TODO: Should add more conditions for the following checking.............
        if(!getEditText(R.id.implantRemoverNameEditText).getText().toString().equals("")) getCheckbox(R.id.implantRemoveCheckBox).setChecked(true);
        if(jsonSpinnerMap.get("fpMethod").getSelectedItemPosition()!=0) getCheckbox(R.id.implantFPMethodGivenCheckBox).setChecked(true);
        //......
        try {
            serviceId = lastServiceJSON.getInt("serviceId");
        } catch (JSONException e) {
            e.printStackTrace();
            serviceId = 0;
        }
        getEditText(R.id.implantClientNameEditText).setText(clientName);
        getEditText(R.id.implantClientNameEditText).setEnabled(false);

        getButton(R.id.implantFollowupSaveButton).setText(getText(R.string.string_update));
        getButton(R.id.implantFollowupDeleteButton).setVisibility(View.VISIBLE);
        getButton(R.id.implantFollowupEdiCancelButton).setVisibility(View.VISIBLE);
        Utilities.Disable(this,R.id.implantFollowupDateEditText);
    }

    private void implantFollowupSaveAsJson(){
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            json = saveHeader(json);

            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            if(editMode){
                json.put("isFixedVisit",lastServiceJSON.get("isFixedVisit"));
                json.put("serviceId",serviceId);
            }else {
                json.put("isFixedVisit",isFixed?range:"");
            }

            Log.d("Imp Followup Save Json", ROOTKEY + ":{" + json.toString() + "}");

            asyncImplantInfoUpate = new AsyncImplantInfoUpate(this, this);
            asyncImplantInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

            resetActivity();

        } catch (JSONException jse) {
            Log.e("Imp JSON Exception: ", jse.getMessage());
        }
    }

    private void resetActivity(){
        Utilities.Reset(this, R.id.implantTextLayout);
        Utilities.Disable(this,R.id.implantClientNameEditText);
        getEditText(R.id.implantClientNameEditText).setText(clientName);
        getTextView(R.id.implantFollowupLabel).setText(getText(R.string.empty_string));
        getButton(R.id.implantFollowupSaveButton).setText(getText(R.string.string_save));
        getButton(R.id.implantFollowupDeleteButton).setVisibility(View.GONE);
        getButton(R.id.implantFollowupEdiCancelButton).setVisibility(View.GONE);
        editMode = false;
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        String queryString = "{" +
                "healthId:" + healthId + "," +
                "implantLoad:\"\"," +
                (isRetrieval ? "" : "providerId:" + providerId + ",") +
                "implantFollowupLoad:" + (isRetrieval ? "\"\"," : (editMode?"update,":"insert,")) +
                "implantCount:" + implantCount+
                "}";
        return new JSONObject(queryString);
    }

    private JSONObject saveHeader(JSONObject json) throws JSONException {
        json.put("sateliteCenterName",sateliteCenterName==null?"":sateliteCenterName);
        json.put("mobileNo",mobileNo);
        json.put("serviceSource", "");

        return json;
    }

    @Override
    public void callbackAsyncTask(String result) {
        historyContentLayout.removeAllViews(); //clear the history list first.

        //setting initial values..........
        fixedCount = 0;
        notFixedCount = 0;
        boolean fixedFlag;

        Log.d(LOGTAG, "Implant-ANDROID response received:\n\t" + result);

        try {
            jsonStr = new JSONObject(result);
            lastImplantFollowup = (jsonStr.has("followupCount") ? jsonStr.getInt("followupCount") : 0 );
            getTextView(R.id.implantFollowupValueTextView).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastImplantFollowup >= 0 ? lastImplantFollowup + 1 : 1)));
            Log.d(LOGTAG, "JSON Response:\n" + jsonStr.toString());
            Log.d(LOGTAG, "Followup Count:"+lastImplantFollowup);
            if(lastImplantFollowup>=1){
                showHideHistoryModifyButton(jsonStr);

                JSONObject followupJSON = jsonStr.getJSONObject("followup");
                lastServiceJSON = followupJSON.getJSONObject(String.valueOf(lastImplantFollowup));
                for (int i = 1; i <= lastImplantFollowup && lastImplantFollowup !=0 ; i++) {
                    JSONObject singleVisit = followupJSON.getJSONObject(String.valueOf(i));
                    displayList = new ArrayList<>();

                    String fCount = singleVisit.getString("isFixedVisit");
                    if(!fCount.equals("")){
                        fixedFlag = true;
                        fixedCount = Integer.parseInt(fCount);
                    }else{
                        fixedFlag = false;
                        notFixedCount = notFixedCount + 1;
                    }

                    HistoryListMaker<ImplantFollowupActivity> historyListMaker = new HistoryListMaker<>(
                            this, /*activity*/
                            singleVisit, /*json containing keys*/
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

                    listDataHeader.add(getString(R.string.followup) + Utilities.ConvertNumberToBangla(String.valueOf(i))+" ("+
                            (fixedFlag?getString(R.string.fixed):getString(R.string.unfixed))+")");
                    listDisplayValues.put(listDataHeader.get(0), displayList);

                    ExpandableDisplayListAdapter displayListAdapter = new ExpandableDisplayListAdapter(this, listDataHeader, listDisplayValues, true);

                    initPage();

                    historyContentLayout.addView(expandableListViewImplant);
                    expandableListViewImplant.setScrollingCacheEnabled(true);
                    expandableListViewImplant.setAdapter(displayListAdapter);

                    //keep history visible and its child expandable
                    for(int j=0; j < displayListAdapter.getGroupCount(); j++){
                        expandableListViewImplant.expandGroup(j);
                    }
                    if(history_layout.getVisibility()==View.GONE){
                        history_layout.setVisibility(View.VISIBLE);
                        getTextView(R.id.implantFOllowupBlanLabelLabel).
                                setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                    }
                    //...........................................................................

                    historyContentLayout.invalidate();
                }
            }else{
                //hide delete button if all the history is cleared by the provider
                getButton(R.id.modifyLastImplantFollowupButton).setVisibility(View.INVISIBLE);
            }
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        } catch (Exception e) {
            Utilities.printTrace(e.getStackTrace(), 10);
        }
    }

    private void initPage() {
        expandableListViewImplant = new ExpandableListView(this);
        expandableListViewImplant.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        expandableListViewImplant.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expandableListViewImplant.setIndicatorBounds(0, 0);
        expandableListViewImplant.setChildIndicatorBounds(0, 0);
        expandableListViewImplant.setStackFromBottom(true);
    }

    private void showHideHistoryModifyButton(JSONObject jso) {
        Utilities.SetVisibility(this, R.id.modifyLastImplantFollowupButton, isLastImplantFollowupModifiable(jso) ? View.VISIBLE :View.INVISIBLE);
    }

    private boolean isLastImplantFollowupModifiable(JSONObject jso) {
        String lastImplantKey = "";
        lastImplantKey = String.valueOf(lastImplantFollowup);

        JSONObject lastVisit = null;
        String providerCode = null;
        try {
            lastVisit = jso.getJSONObject("followup").getJSONObject(lastImplantKey);
            providerCode = lastVisit.getString("providerId");
            Log.d(LOGTAG,"Last Service "+lastImplantKey+" was provide by: \t" + providerCode);

            return (provider.getProviderCode().equals(providerCode));

        } catch (JSONException jse) {
            Log.e(LOGTAG, String.format(" JSON Error (Implant FOllow-up History): %s", jse.getMessage()));
            Utilities.printTrace(jse.getStackTrace(), 10);
        }

        return false;
    }

    public void deleteLastImplantFollowup(){
        AlertDialogCreator.SimpleDecisionDialog(ImplantFollowupActivity.this,getString(R.string.ServiceDeletionWarning),
                "Delete Service?",android.R.drawable.ic_delete);
    }

    @Override
    public void onPositiveButtonClicked(DialogInterface dialog) {
        dialog.cancel();
        deleteConfirmed();
    }

    @Override
    public void onNegativeButtonClicked(DialogInterface dialog) {
        dialog.cancel();
    }

    @Override
    public void onNeutralButtonClicked(DialogInterface dialog) {
        dialog.cancel();
    }

    private void deleteConfirmed() {
        try {
            JSONObject deleteJson = buildQueryHeader(false);
            deleteJson.put("implantFollowupLoad", "delete");
            asyncImplantInfoUpate = new AsyncImplantInfoUpate(this, this);
            asyncImplantInfoUpate.execute(deleteJson.toString(), SERVLET, ROOTKEY);

            resetActivity();

        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete Implant Followup request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    void setHistoryLabelMapping() {
        historyLabelMap.put("followupDate",  Pair.create(getString(R.string.date), new Integer[] {0}));
        historyLabelMap.put("allowance",Pair.create(getString(R.string.implant_followup_allowance),new Integer[] {0}));
        historyLabelMap.put("attendantName",  Pair.create(getString(R.string.followup_attendant_name), new Integer[] {0}));
        historyLabelMap.put("complication",Pair.create(getString(R.string.drawback), new Integer[] {R.array.Implant_Complication_DropDown}));
        //historyLabelMap.put("management",Pair.create(getString(R.string.management), new Integer[] {R.array.Implant_Management_DropDown}));
        historyLabelMap.put("treatment",Pair.create(getString(R.string.treatment), new Integer[] {R.array.Implant_Treatment_DropDown}));
        historyLabelMap.put("implantRemoverName",  Pair.create(getString(R.string.implant_remover_name), new Integer[] {0}));
        historyLabelMap.put("implantRemoverDesignation",  Pair.create(getString(R.string.implant_remover_designation), new Integer[] {R.array.ImplantRemoverDesignation_DropDown}));
        historyLabelMap.put("implantRemoveDate",  Pair.create(getString(R.string.implant_remove_date), new Integer[] {0}));
        historyLabelMap.put("implantRemoveReason",Pair.create(getString(R.string.implant_remove_reason), new Integer[] {R.array.Implant_Remove_Reason_DropDown}));
        historyLabelMap.put("fpMethod",  Pair.create(getString(R.string.fp_methods), new Integer[] {R.array.Family_Planning_Methods_DropDown}));
        historyLabelMap.put("fpAmount",  Pair.create(getString(R.string.amount), new Integer[] {0}));
        historyLabelMap.put("refer",Pair.create(getString(R.string.refer), new Integer[] {0}));
        historyLabelMap.put("referCenterName", Pair.create(getString(R.string.history_c_name), new Integer[] {R.array.FacilityType_DropDown}));
        //historyLabelMap.put("referReason",Pair.create(getString(R.string.referReason), new Integer[] {R.array.Implant_Refer_Reason_DropDown}));
        historyLabelMap.put("monitoringOfficerName",  Pair.create(getString(R.string.monitoring_officer_name), new Integer[] {0}));
        historyLabelMap.put("comment",  Pair.create(getString(R.string.monitoring_officer_comment), new Integer[] {0}));
    }

    private boolean hasTheRequiredFileds() {
        boolean valid = true;
        boolean specialInvalid = false;

        if (!Validation.hasText(getEditText(R.id.implantFollowupDateEditText))) valid = false;

        if (getCheckbox(R.id.implantReferCheckBox).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.implantReferCenterNameSpinner)))
                specialInvalid = true;
            /*if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.implantChildReasonSpinner)))
                specialInvalid = true;*/
        }
        if (!valid) {
            Toast toast = Toast.makeText(this, R.string.GeneralSaveWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        } /*else if (specialInvalid) {
            MethodUtils.showSnackBar(findViewById(R.id.implant_followup_layout_main), getResources().getString(R.string.refer_validation_message), true);
            return false;
        }*/
        else {
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //AlertDialogCreator.ExitActivityDialog(this,getResources().getString(R.string.topTextImplantFollowup));
    }
}
