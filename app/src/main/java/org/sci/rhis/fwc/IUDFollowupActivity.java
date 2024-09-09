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
import org.sci.rhis.connectivityhandler.AsyncIUDInfoUpate;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.DisplayValue;
import org.sci.rhis.utilities.Flag;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by arafat.hasan on 3/10/2016.
 */
public class IUDFollowupActivity extends ClinicalServiceActivity implements CompoundButton.OnCheckedChangeListener,
        CustomTextWatcher.CustomWatcherListner,
        AlertDialogCreator.DialogButtonClickedListener{

    //Variable declarations.................................................................................

    private Context mContext;
    private ProviderInfo provider;

    final private String SERVLET = "iud";
    final private String ROOTKEY = "iudInfo";
    private String LOGTAG = "FP-IUD_FOLLOWUP";
    int iudCount=0;
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

    AsyncIUDInfoUpate asyncIUDInfoUpate;

    private MultiSelectionSpinner multiSelectionSpinner;

    JSONObject jsonStr;
    String[] details;
    private int lastIUDFollowup;
    private HashMap<Integer, Pair<Date, Date>> dateRangeMap;

    ExpandableListView exppandableListViewIUD;
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
        setContentView(R.layout.activity_iud_followup);
        initialInit();
    }

    private void initialInit(){
        mContext = this;
        lastIUDFollowup=0;

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
        iudCount = getIntent().getIntExtra(Constants.KEY_IUD_COUNT, 0);
        sateliteCenterName = provider.getSatelliteName();
        if(getIntent().hasExtra(Constants.KEY_CLIENT_NAME)){
            clientName=getIntent().getStringExtra(Constants.KEY_CLIENT_NAME);
        }else{
            clientName="";
        }
        getEditText(R.id.iudClientNameEditText).setText(clientName);

        setSuggestedFollowupDates();

    }

    private void setSuggestedFollowupDates(){
        try {
            Date implantDate = Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,
                    getIntent().getStringExtra(Constants.KEY_IUD_IMPLANT_DATE));
            CustomSimpleDateFormat sdf = new CustomSimpleDateFormat(Constants.SPECIAL_FORMAT_1);
            dateRangeMap = new HashMap<>();
            dateRangeMap.put(1,Pair.create(Utilities.addDateOffset(implantDate, Durations.FIXED_FIRST1.value),
                    Utilities.addDateOffset(implantDate, Durations.FIXED_FIRST2.value)));
            dateRangeMap.put(2,Pair.create(Utilities.manipulateDateByMonth(implantDate,Durations.FIXED_SECOND1.value,Constants.ADD_CODE),
                    Utilities.manipulateDateByMonth(implantDate,Durations.FIXED_SECOND2.value,Constants.ADD_CODE)));
            dateRangeMap.put(3,Pair.create(Utilities.manipulateDateByMonth(implantDate,Durations.FIXED_THIRD1.value,Constants.ADD_CODE),
                    Utilities.manipulateDateByMonth(implantDate,Durations.FIXED_THIRD2.value,Constants.ADD_CODE)));

            getTextView(R.id.iudFollowup1Date).setText(sdf.format(dateRangeMap.get(1).first)+" - "+sdf.format(dateRangeMap.get(1).second));
            getTextView(R.id.iudFollowup2Date).setText(sdf.format(dateRangeMap.get(2).first)+" - "+sdf.format(dateRangeMap.get(2).second));
            getTextView(R.id.iudFollowup3Date).setText(sdf.format(dateRangeMap.get(3).first)+" - "+sdf.format(dateRangeMap.get(3).second));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("providerAsAttendant",getCheckbox(R.id.iudFollowupMyselfCheckbox));
        jsonCheckboxMap.put("refer", getCheckbox(R.id.iudReferCheckBox));
        jsonCheckboxMap.put("allowance", getCheckbox(R.id.iudFollowupAllowanceGivenCheckBox));

        getCheckbox(R.id.iudFPMethodGivenCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.iudFollowupMyselfCheckbox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.iudRemoveCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.iudRemoveMyselfCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.iudReferCheckBox).setOnCheckedChangeListener(this);
    }

    @Override
    protected void initiateEditTexts() {

        jsonEditTextMap.put("attendantName", getEditText(R.id.iudFollowupAttendantNameEditText));
        jsonEditTextMap.put("iudRemoverName", getEditText(R.id.iudRemoverNameEditText));
        jsonEditTextMap.put("fpAmount", getEditText(R.id.iudFPAmountEditText));
        jsonEditTextMap.put("monitoringOfficerName", getEditText(R.id.iudMonitoringOfficerEditText));
        jsonEditTextMap.put("comment", getEditText(R.id.iudMonitorCommentEditText));
    }

    @Override
    protected void initiateTextViews() {
        getTextView(R.id.iudFollowupValueTextView).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastIUDFollowup >= 0 ? lastIUDFollowup + 1 : 1)));

        getTextView(R.id.iudFollowupDateTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.iudFollowupDateTextView).getText().toString(), 0, 1));
        getTextView(R.id.iudReferCenterNameTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.iudReferCenterNameTextView).getText().toString(), 0, 1));
        getTextView(R.id.iudChildReasonLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.iudChildReasonLabel).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("iudRemoverDesignation",getSpinner(R.id.iudRemoverDesignationSpinner));
        jsonSpinnerMap.put("fpMethod",getSpinner(R.id.iudFPMethodSpinner));
        jsonSpinnerMap.put("referCenterName",getSpinner(R.id.iudReferCenterNameSpinner));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("complication", getMultiSelectionSpinner(R.id.iudComplicacySpinner));
        jsonMultiSpinnerMap.put("treatment", getMultiSelectionSpinner(R.id.iudFollowupTreatmentSpinner));
        jsonMultiSpinnerMap.put("management", getMultiSelectionSpinner(R.id.iudManagementSpinner));
        jsonMultiSpinnerMap.put("iudRemoveReason", getMultiSelectionSpinner(R.id.iudRemoveReasonSpinner));
        jsonMultiSpinnerMap.put("referReason", getMultiSelectionSpinner(R.id.iudChildReasonSpinner));

        final List<String> removeReasonList = Arrays.asList(getResources().getStringArray(R.array.IUD_Remove_Reason_DropDown));

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.iudRemoveReasonSpinner);
        multiSelectionSpinner.setItems(removeReasonList);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> complicacyList = Arrays.asList(getResources().getStringArray(R.array.IUD_Complication_DropDown));
        final List<String> treatmentList = Arrays.asList(getResources().getStringArray(R.array.IUD_Treatment_DropDown));
        final List<String> managementList = Arrays.asList(getResources().getStringArray(R.array.IUD_Management_DropDown));
        final List<String> referReasonList = Arrays.asList(getResources().getStringArray(R.array.IUD_Refer_Reason_DropDown));


        multiSelectionSpinner = getMultiSelectionSpinner(R.id.iudComplicacySpinner);
        multiSelectionSpinner.setItems(complicacyList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.iudFollowupTreatmentSpinner);
        multiSelectionSpinner.setItems(treatmentList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.iudManagementSpinner);
        multiSelectionSpinner.setItems(managementList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.iudChildReasonSpinner);
        multiSelectionSpinner.setItems(referReasonList);
        multiSelectionSpinner.setSelection(new int[]{});
    }

    @Override
    protected void initiateEditTextDates() {
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH);
        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.iudFollowupDatePickerButton, (EditText) findViewById(R.id.iudFollowupDateEditText));
        datePickerPair.put(R.id.iudRemoveDatePickerButton, (EditText) findViewById(R.id.iudRemoveDateEditText));

        jsonEditTextDateMap.put("followupDate", getEditText(R.id.iudFollowupDateEditText));
        getEditText(R.id.iudFollowupDateEditText).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.iudFollowupDateEditText)));
        jsonEditTextDateMap.put("iudRemoveDate", getEditText(R.id.iudRemoveDateEditText));
    }

    @Override
    public void onTextChanged() {
        if(!editMode){
            Date followupDate = null;
            String givenDate = getEditText(R.id.iudFollowupDateEditText).getEditableText().toString();
            if(givenDate.equals("")){
                return;
            }
            range=0;
            try {
                followupDate = Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,givenDate);
            } catch (ParseException e) {
                e.printStackTrace();
                getTextView(R.id.iudFollowupLabel).setText("");
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
                getTextView(R.id.iudFollowupLabel).setText(getResources().getString(R.string.fixed_followup)+
                        ": "+ Utilities.ConvertNumberToBangla(String.valueOf(range)));
            }else{
                isFixed = false;
                getTextView(R.id.iudFollowupLabel).setText(getResources().getString(R.string.unfixed_followup)+
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
            case R.id.iudFollowupMyselfCheckbox:
                if(checked) {
                    getEditText(R.id.iudFollowupAttendantNameEditText).setText(provider.getProviderName());
                    Utilities.Disable(this, R.id.iudFollowupAttendantNameEditText);
                }else {
                    getEditText(R.id.iudFollowupAttendantNameEditText).setText("");
                    Utilities.Enable(this, R.id.iudFollowupAttendantNameEditText);
                }
                break;
            case R.id.iudRemoveCheckBox:
                int removeLayouts[] = {R.id.iudRemoverNameLayout, R.id.iudRemoverDesignationLayout, R.id.iudRemoveDateLayout, R.id.iudRemoveReasonLayout, R.id.iudFPMethodGivenLayout};
                changeLayoutVisibility(visibility , removeLayouts);
                break;

            case R.id.iudRemoveMyselfCheckBox:
                if(checked) {
                    getEditText(R.id.iudRemoverNameEditText).setText(provider.getProviderName());
                    getSpinner(R.id.iudRemoverDesignationSpinner).setSelection(1);
                    Utilities.Disable(this, R.id.iudRemoverNameEditText);
                    Utilities.Disable(this, R.id.iudRemoverDesignationLayout);
                }else {
                    getEditText(R.id.iudRemoverNameEditText).setText("");
                    getSpinner(R.id.iudRemoverDesignationSpinner).setSelection(0);
                    Utilities.Enable(this, R.id.iudRemoverNameEditText);
                    Utilities.Enable(this, R.id.iudRemoverDesignationLayout);
                }
                break;

            case R.id.iudFPMethodGivenCheckBox:
                int fpLayouts[] = {R.id.iudFPMethodLayout, R.id.iudFPAmountLayout};
                changeLayoutVisibility(visibility , fpLayouts);
                break;

            case R.id.iudReferCheckBox:
                int referLayouts[] = {R.id.iudReferCenterNameLayout, R.id.iudReasonLayout};
                changeLayoutVisibility(visibility , referLayouts);
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
        history_layout = (LinearLayout)findViewById(R.id.history_lay_iud);
        exppandableListViewIUD = new ExpandableListView(this);

        JSONObject json;
        try {
            json = buildQueryHeader(true);
            asyncIUDInfoUpate = new AsyncIUDInfoUpate(this, this);
            asyncIUDInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("IUDFo JSON Exception: ", jse.getMessage());
        }

        MethodUtils.makeHistoryCollapsible(getTextView(R.id.iudFOllowupBlanLabelLabel),history_layout);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.iudFollowupSaveButton:
                if (!hasTheRequiredFields()) {
                    return;
                }

                if(!editMode){
                    try {
                        String cVisitDate=jsonEditTextDateMap.get("followupDate").getText().toString();
                        if(lastIUDFollowup>0 && !Validation.isValidVisitDate(this,cVisitDate,lastServiceJSON.getString("followupDate"),false,true)){
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                saveIUDFollowup();

                break;
            case R.id.iudFollowupEditButton:
                editIUDFOllowup();
                break;

            case R.id.iudFollowupDeleteButton:
                deleteLastIUDFollowup();
                break;

            case R.id.modifyLastIUDFollowupButton:
                setLastServiceData();
                break;

            case R.id.iudFollowupEdiCancelButton:
                resetActivity();
                break;
        }
    }

    private void saveIUDFollowup(){
        countSaveClick++;
        if( countSaveClick == 2 ) {
            iudFollowupSaveAsJson();
            getButton(R.id.iudFollowupSaveButton).setText(getText(R.string.string_save));
            Utilities.Enable(this, R.id.iudFollowupEntryMasterLayout);
            Utilities.MakeInvisible(this, R.id.iudFollowupEditButton);
            countSaveClick = 0;
        }

        else if(countSaveClick == 1) {

            Utilities.Disable(this, R.id.iudFollowupEntryMasterLayout);
            getButton( R.id.iudFollowupSaveButton).setText(getText(R.string.string_confirm));
            Utilities.Enable(this, R.id.iudFollowupSaveButton);
            getButton( R.id.iudFollowupEditButton).setText(getText(R.string.string_cancel));
            Utilities.Enable(this, R.id.iudFollowupEditButton);
            Utilities.MakeVisible(this, R.id.iudFollowupEditButton);

            Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);
        }

    }

    private void editIUDFOllowup(){
        if(countSaveClick == 1) {
            countSaveClick = 0;
            Utilities.Enable(this, R.id.iudFollowupEntryMasterLayout);
            getButton(R.id.iudFollowupSaveButton).setText(editMode?"Update":"Save");
            Utilities.MakeInvisible(this, R.id.iudFollowupEditButton);
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
        if(!getEditText(R.id.iudRemoverNameEditText).getText().toString().equals("")) getCheckbox(R.id.iudRemoveCheckBox).setChecked(true);
        if(jsonSpinnerMap.get("fpMethod").getSelectedItemPosition()!=0) getCheckbox(R.id.iudFPMethodGivenCheckBox).setChecked(true);
        //......
        try {
            serviceId = lastServiceJSON.getInt("serviceId");
        } catch (JSONException e) {
            e.printStackTrace();
            serviceId = 0;
        }
        getEditText(R.id.iudClientNameEditText).setText(clientName);
        getEditText(R.id.iudClientNameEditText).setEnabled(false);

        getButton(R.id.iudFollowupSaveButton).setText(getText(R.string.string_update));
        getButton(R.id.iudFollowupDeleteButton).setVisibility(View.VISIBLE);
        getButton(R.id.iudFollowupEdiCancelButton).setVisibility(View.VISIBLE);
        Utilities.Disable(this,R.id.iudFollowupDateEditText);
    }

    private void iudFollowupSaveAsJson(){
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

            Log.d("IUD Followup Save Json", ROOTKEY + ":{" + json.toString() + "}");

            asyncIUDInfoUpate = new AsyncIUDInfoUpate(this, this);
            asyncIUDInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

            resetActivity();

        } catch (JSONException jse) {
            Log.e("IUD JSON Exception: ", jse.getMessage());
        }
    }

    private void resetActivity(){
        Utilities.Reset(this, R.id.iudTextLayout);
        Utilities.Disable(this,R.id.iudClientNameEditText);
        getEditText(R.id.iudClientNameEditText).setText(clientName);
        getTextView(R.id.iudFollowupLabel).setText("");
        getButton(R.id.iudFollowupSaveButton).setText(getText(R.string.string_save));
        getButton(R.id.iudFollowupDeleteButton).setVisibility(View.GONE);
        getButton(R.id.iudFollowupEdiCancelButton).setVisibility(View.GONE);
        editMode = false;
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        String queryString = "{" +
                "healthId:" + healthId + "," +
                "iudLoad:\"\"," +
                (isRetrieval ? "" : "providerId:" + providerId + ",") +
                "iudFollowupLoad:" + (isRetrieval ? "\"\"," : (editMode?"update,":"insert,")) +
                "iudCount:" + iudCount+
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

        Log.d(LOGTAG, "IUD-ANDROID response received:\n\t" + result);

        try {
            jsonStr = new JSONObject(result);
            lastIUDFollowup = (jsonStr.has("followupCount") ? jsonStr.getInt("followupCount") : 0 );
            getTextView(R.id.iudFollowupValueTextView).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastIUDFollowup >= 0 ? lastIUDFollowup + 1 : 1)));
            Log.d(LOGTAG, "JSON Response:\n" + jsonStr.toString());
            Log.d(LOGTAG, "Followup Count:"+lastIUDFollowup);
            if(lastIUDFollowup>=1){
                showHideHistoryModifyButton(jsonStr);

                JSONObject followupJSON = jsonStr.getJSONObject("followup");
                lastServiceJSON = followupJSON.getJSONObject(String.valueOf(lastIUDFollowup));
                for (int i = 1; i <= lastIUDFollowup && lastIUDFollowup !=0 ; i++) {
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

                    HistoryListMaker<IUDFollowupActivity> historyListMaker = new HistoryListMaker<>(
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

                    historyContentLayout.addView(exppandableListViewIUD);
                    exppandableListViewIUD.setScrollingCacheEnabled(true);
                    exppandableListViewIUD.setAdapter(displayListAdapter);

                    //keep history visible and its child expandable
                    for(int j=0; j < displayListAdapter.getGroupCount(); j++){
                        exppandableListViewIUD.expandGroup(j);
                    }
                    if(history_layout.getVisibility()==View.GONE){
                        history_layout.setVisibility(View.VISIBLE);
                        getTextView(R.id.iudFOllowupBlanLabelLabel).
                                setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                    }
                    //...........................................................................

                    historyContentLayout.invalidate();
                }
            }else{
                //hide delete button if all the history is cleared by the provider
                getButton(R.id.modifyLastIUDFollowupButton).setVisibility(View.INVISIBLE);
            }


        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        } catch (Exception e) {
            Utilities.printTrace(e.getStackTrace(), 10);
        }
    }

    private void initPage() {
        exppandableListViewIUD = new ExpandableListView(this);
        exppandableListViewIUD.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        exppandableListViewIUD.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        exppandableListViewIUD.setIndicatorBounds(0, 0);
        exppandableListViewIUD.setChildIndicatorBounds(0, 0);
        exppandableListViewIUD.setStackFromBottom(true);
    }

    private void showHideHistoryModifyButton(JSONObject jso) {
        Utilities.SetVisibility(this, R.id.modifyLastIUDFollowupButton, isLastIUDFollowupModifiable(jso) ? View.VISIBLE :View.INVISIBLE);
    }

    private boolean isLastIUDFollowupModifiable(JSONObject jso) {
        String lastIUDKey = String.valueOf(lastIUDFollowup);
        boolean thresholdPeriodPassed = false;
        try {
            JSONObject lastVisit = jso.getJSONObject("followup").getJSONObject(lastIUDKey);
            String providerCode  = lastVisit.getString("providerId");
            Log.d(LOGTAG,"Last Service "+lastIUDKey+" was provide by: \t" + providerCode);
            if(lastVisit.has("systemEntryDate") && !lastVisit.getString("systemEntryDate").equals("")){
                Date lastVisitEntryDate = Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,lastVisit.getString("systemEntryDate"));
                thresholdPeriodPassed = Utilities.getDateDiff(lastVisitEntryDate,new Date(), TimeUnit.DAYS)> Flag.UPDATE_THRESHOLD;
            }
            return (provider.getProviderCode().equals(providerCode) && !thresholdPeriodPassed);
            //return (provider.getProviderCode().equals(providerCode));

        } catch (JSONException jse) {
            Log.e(LOGTAG, String.format(" JSON Error (IUD FOllow-up History): %s", jse.getMessage()));
            Utilities.printTrace(jse.getStackTrace(), 10);
        } catch (ParseException pse){
            pse.printStackTrace();
        }

        return false;
    }

    public void deleteLastIUDFollowup(){
        AlertDialogCreator.SimpleDecisionDialog(IUDFollowupActivity.this,getString(R.string.ServiceDeletionWarning),
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
            deleteJson.put("iudFollowupLoad", "delete");
            asyncIUDInfoUpate = new AsyncIUDInfoUpate(this, this);
            asyncIUDInfoUpate.execute(deleteJson.toString(), SERVLET, ROOTKEY);

            resetActivity();

        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete IUD Followup request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    void setHistoryLabelMapping() {
        historyLabelMap.put("followupDate",  Pair.create(getString(R.string.date), new Integer[] {0}));
        historyLabelMap.put("allowance",Pair.create(getString(R.string.iud_followup_allowance),new Integer[] {0}));
        historyLabelMap.put("attendantName",  Pair.create(getString(R.string.followup_attendant_name), new Integer[] {0}));
        historyLabelMap.put("complication",Pair.create(getString(R.string.drawback), new Integer[] {R.array.IUD_Complication_DropDown}));
        historyLabelMap.put("management",Pair.create(getString(R.string.management), new Integer[] {R.array.IUD_Management_DropDown}));
        historyLabelMap.put("treatment",Pair.create(getString(R.string.treatment), new Integer[] {R.array.IUD_Treatment_DropDown}));
        historyLabelMap.put("iudRemoverName",  Pair.create(getString(R.string.iud_remover_name), new Integer[] {0}));
        historyLabelMap.put("iudRemoverDesignation",  Pair.create(getString(R.string.iud_remover_designation), new Integer[] {R.array.IUDRemoverDesignation_DropDown}));
        historyLabelMap.put("iudRemoveDate",  Pair.create(getString(R.string.iud_remove_date), new Integer[] {0}));
        historyLabelMap.put("iudRemoveReason",Pair.create(getString(R.string.iud_remove_reason), new Integer[] {R.array.IUD_Remove_Reason_DropDown}));
        historyLabelMap.put("fpMethod",  Pair.create(getString(R.string.fp_methods), new Integer[] {R.array.Family_Planning_Methods_DropDown}));
        historyLabelMap.put("fpAmount",  Pair.create(getString(R.string.amount), new Integer[] {0}));
        historyLabelMap.put("refer",Pair.create(getString(R.string.refer), new Integer[] {0}));
        historyLabelMap.put("referCenterName", Pair.create(getString(R.string.history_c_name), new Integer[] {R.array.FacilityType_DropDown}));
        historyLabelMap.put("referReason",Pair.create(getString(R.string.referReason), new Integer[] {R.array.IUD_Refer_Reason_DropDown}));
        historyLabelMap.put("monitoringOfficerName",  Pair.create(getString(R.string.monitoring_officer_name), new Integer[] {0}));
        historyLabelMap.put("comment",  Pair.create(getString(R.string.monitoring_officer_comment), new Integer[] {0}));
    }

    private boolean hasTheRequiredFields() {
        boolean valid = true;
        boolean specialInvalid = false;

        if (!Validation.hasText(getEditText(R.id.iudFollowupDateEditText))) valid = false;

        if (getCheckbox(R.id.iudReferCheckBox).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.iudReferCenterNameSpinner)))
                specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.iudChildReasonSpinner)))
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
            MethodUtils.showSnackBar(findViewById(R.id.iud_followup_main_layout), getResources().getString(R.string.refer_validation_message), true);
            return false;
        }
        else {
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //AlertDialogCreator.ExitActivityDialog(this,getResources().getString(R.string.topTextIudFollowup));
    }
}
