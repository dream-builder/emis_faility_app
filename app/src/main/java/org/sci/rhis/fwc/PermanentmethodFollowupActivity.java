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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncPMSInfoUpate;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
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

public class PermanentmethodFollowupActivity extends ClinicalServiceActivity implements CompoundButton.OnCheckedChangeListener,
        CustomTextWatcher.CustomWatcherListner,
        AlertDialogCreator.DialogButtonClickedListener {

    private Context mContext;
    private ProviderInfo provider;

    final private String SERVLET = "pms";
    final private String ROOTKEY = "pmsInfo";
    private String LOGTAG = "FP-PMS_FOLLOWUP";
    int pmsCount = 0;
    int range = 0;
    String healthId, providerId, mobileNo;
    private boolean editMode = false;
    private JSONObject lastServiceJSON;

    LinearLayout historyContentLayout;
    LinearLayout history_layout;

    private int countSaveClick = 0;
    private String gender = "";

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;
    private int fixedCount = 0, serviceId = 0;

    AsyncPMSInfoUpate asyncPMSInfoUpate;

    private MultiSelectionSpinner multiSelectionSpinner;

    ArrayList<String> list;
    ArrayList<DisplayValue> displayList;

    JSONObject jsonStr;
    String[] details;
    private int lastPMSFollowup;
    private HashMap<Integer, Pair<Date, Date>> dateRangeMap;

    ExpandableListView expandableListViewPMS;
    List<String> listDataHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pms_followup);
        initialInit();
    }

    private void initialInit() {
        mContext = this;
        lastPMSFollowup = 0;

        initialize();
        setPredefinedValues();
        setHistoryLabelMapping();
        loadHistory();
    }

    private void setPredefinedValues() {
        provider = getIntent().getParcelableExtra(Constants.KEY_PROVIDER);
        providerId = String.valueOf(provider.getProviderCode());
        healthId = getIntent().getStringExtra(Constants.KEY_HEALTH_ID);
        gender = getIntent().getStringExtra(Constants.KEY_GENDER);
        mobileNo = getIntent().getStringExtra(Constants.KEY_MOBILE);
        pmsCount = getIntent().getIntExtra(Constants.KEY_PMS_COUNT, 0);
        getEditText(R.id.pmsFollowupDoctorNameEditText).setText(getText(R.string.str_doc));
        getEditText(R.id.pmsFollowupDateEditText).setText(Converter.dateToString(Constants.SHORT_SLASH_FORMAT_BRITISH, new Date()));
    }

    @Override
    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("refer", getCheckbox(R.id.pmsFollowupReferCheckBox));
        getCheckbox(R.id.pmsFollowupReferCheckBox).setOnCheckedChangeListener(this);
    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("doctorName", getEditText(R.id.pmsFollowupDoctorNameEditText));
    }

    @Override
    protected void initiateTextViews() {
        getTextView(R.id.pmsFollowupDateTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pmsFollowupDateTextView).getText().toString(), 0, 1));
        getTextView(R.id.pmsFollowupReasonTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pmsFollowupReasonTextView).getText().toString(), 0, 1));
        getTextView(R.id.pmsFollowupDoctorNameTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pmsFollowupDoctorNameTextView).getText().toString(), 0, 1));
        getTextView(R.id.pmsReferCenterNameTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pmsReferCenterNameTextView).getText().toString(), 0, 1));
        getTextView(R.id.pmsChildReasonLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pmsChildReasonLabel).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("doctorDesignation", getSpinner(R.id.pmsFollowupDoctorDesignationDropdown));
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.pmsFollowupReferCenterNameSpinner));
        jsonSpinnerMap.put("followupReason", getSpinner(R.id.pmsFollowupReasonDropdown));
        jsonSpinnerMap.put("examination", getSpinner(R.id.pmsFollowupExaminationListView));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("treatment", getMultiSelectionSpinner(R.id.pmsFollowupMedicineListView));
        jsonMultiSpinnerMap.put("complication", getMultiSelectionSpinner(R.id.pmsFollowupComplicacySpinner));
        jsonMultiSpinnerMap.put("management", getMultiSelectionSpinner(R.id.pmsFollowupManagementSpinner));
        jsonMultiSpinnerMap.put("referReason", getMultiSelectionSpinner(R.id.pmsFollowupReferReasonSpinner));
        final List<String> complicacyList;
        final List<String> managementList;

        // get the symptom list as jsonArray
//        JSONArray symptomArray = CommonQueryExecution.getDropDownListAsJSONArray(Constants.TABLE_SYMPTOM_LIST,
//                Utilities.getServiceId(Constants.IUD_SERVICE_IDENTIFIER,Constants.SYMPTOM_LIST_IDENTIFIER));//TODO: PMS_SERVICE_IDENTIFIER

        if (gender.equals("1")) {
            managementList = Arrays.asList(getResources().getStringArray(R.array.PM_Followup_Management_Male_DropDown));
            complicacyList = Arrays.asList(getResources().getStringArray(R.array.PM_Followup_Complication_Male_DropDown));

        } else {
            managementList = Arrays.asList(getResources().getStringArray(R.array.PM_Followup_Management_Female_DropDown));
            complicacyList = Arrays.asList(getResources().getStringArray(R.array.PM_Followup_Complication_Female_DropDown));
        }

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.pmsFollowupManagementSpinner);
        multiSelectionSpinner.setItems(managementList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.pmsFollowupComplicacySpinner);
        multiSelectionSpinner.setItems(complicacyList);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> treatmentList = Arrays.asList(getResources().getStringArray(R.array.PMS_Treatment_Dropdown));
        multiSelectionSpinner = getMultiSelectionSpinner(R.id.pmsFollowupMedicineListView);
        multiSelectionSpinner.setItems(treatmentList);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> referReasonList = Arrays.asList(getResources().getStringArray(R.array.PM_Refer_Reason_DropDown));
        multiSelectionSpinner = getMultiSelectionSpinner(R.id.pmsFollowupReferReasonSpinner);
        multiSelectionSpinner.setItems(referReasonList);
        multiSelectionSpinner.setSelection(new int[]{});
    }

    @Override
    protected void initiateEditTextDates() {
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH);
        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.pmsFollowupDatePickerButton, (EditText) findViewById(R.id.pmsFollowupDateEditText));


        jsonEditTextDateMap.put("followupDate", getEditText(R.id.pmsFollowupDateEditText));
        getEditText(R.id.pmsFollowupDateEditText).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.pmsFollowupDateEditText)));
    }

    @Override
    public void onTextChanged() {
        if (!editMode) {
            Date followupDate = null;
            String givenDate = getEditText(R.id.pmsFollowupDateEditText).getEditableText().toString();
            if (givenDate.equals("")) {
                return;
            }
            range = 0;
            try {
                followupDate = Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH, givenDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (fixedCount < 3 && followupDate != null) {
                CheckDateRange:
                for (int i = fixedCount + 1; i <= 3; i++) {
                    boolean isFound = Utilities.checkDateInRange(dateRangeMap.get(i).first,
                            dateRangeMap.get(i).second, followupDate);
                    if (isFound) {
                        range = i;
                        break CheckDateRange;
                    }
                }
            }
        }
    }

    @Override
    protected void initiateRadioGroups() {

    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private void changeLayoutVisibility(int visibility, int[] layouts) {
        for (int layout : layouts) {
            Utilities.SetVisibility(this, layout, visibility);
        }
    }

    private void loadHistory() {
        historyContentLayout = (LinearLayout) findViewById(R.id.llay);
        history_layout = (LinearLayout) findViewById(R.id.history_lay_pms);
        expandableListViewPMS = new ExpandableListView(this);

        JSONObject json;
        try {
            json = buildQueryHeader(true);
            asyncPMSInfoUpate = new AsyncPMSInfoUpate(this, this);
            asyncPMSInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("PMSFo JSON Exception: ", jse.getMessage());
        }

        MethodUtils.makeHistoryCollapsible(getTextView(R.id.pmsFOllowupBlanLabelLabel), history_layout);
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        int visibility = isChecked ? View.VISIBLE : View.GONE;
        switch (view.getId()) {
            case R.id.pmsFollowupReferCheckBox:
                int referLayouts[] = {R.id.pmsFollowupReferCenterNameLayout, R.id.pmsFollowupReferReasonLayout};
                changeLayoutVisibility(visibility, referLayouts);
                break;
            default:
                break;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pmsFollowupSaveButton:
                if (!hasTheRequiredFields()) {
                    return;
                }

                if (!editMode) {
                    try {
                        String cVisitDate = jsonEditTextDateMap.get("followupDate").getText().toString();
                        if (lastPMSFollowup > 0 && !Validation.isValidVisitDate(this, cVisitDate, lastServiceJSON.getString("followupDate"), false, true)) {
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                savePMSFollowup();

                break;
            case R.id.pmsFollowupEditButton:
                editPMSFOllowup();
                break;

            case R.id.pmsFollowupDeleteButton:
                deleteLastPMSFollowup();
                break;

            case R.id.modifyLastPMSFollowupButton:
                setLastServiceData();
                break;

            case R.id.pmsFollowupEdiCancelButton:
                resetActivity();
                break;
        }
    }

    private void savePMSFollowup() {
        countSaveClick++;
        if (countSaveClick == 2) {
            pmsFollowupSaveAsJson();
            getButton(R.id.pmsFollowupSaveButton).setText(getText(R.string.string_save));
            Utilities.Enable(this, R.id.pmsFollowupEntryMasterLayout);
            Utilities.MakeInvisible(this, R.id.pmsFollowupEditButton);
            countSaveClick = 0;
        } else if (countSaveClick == 1) {
            Utilities.Disable(this, R.id.pmsFollowupEntryMasterLayout);
            getButton(R.id.pmsFollowupSaveButton).setText(getText(R.string.string_confirm));
            Utilities.Enable(this, R.id.pmsFollowupSaveButton);
            getButton(R.id.pmsFollowupEditButton).setText(getText(R.string.string_cancel));
            Utilities.Enable(this, R.id.pmsFollowupEditButton);
            Utilities.MakeVisible(this, R.id.pmsFollowupEditButton);

            Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);
        }
    }

    private void editPMSFOllowup() {
        if (countSaveClick == 1) {
            countSaveClick = 0;
            Utilities.Enable(this, R.id.pmsFollowupEntryMasterLayout);
            getButton(R.id.pmsFollowupSaveButton).setText(editMode ? getText(R.string.string_update) : getText(R.string.string_save));
            Utilities.MakeInvisible(this, R.id.pmsFollowupEditButton);
        }
    }

    private void setLastServiceData() {
        editMode = true;
        Utilities.setEditTexts(jsonEditTextMap, lastServiceJSON);
        Utilities.setEditTextDates(jsonEditTextDateMap, lastServiceJSON);
        Utilities.setSpinners(jsonSpinnerMap, lastServiceJSON);
        Utilities.setCheckboxes(jsonCheckboxMap, lastServiceJSON);
        Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap, lastServiceJSON);
        Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap, lastServiceJSON);
        //TODO: Should add more conditions for the following checking.............
//        if(!getEditText(R.id.iudRemoverNameEditText).getText().toString().equals("")) getCheckbox(R.id.iudRemoveCheckBox).setChecked(true);
//        if(jsonSpinnerMap.get("fpMethod").getSelectedItemPosition()!=0) getCheckbox(R.id.iudFPMethodGivenCheckBox).setChecked(true);
        //......
        try {
            serviceId = lastServiceJSON.getInt("serviceId");
        } catch (JSONException e) {
            e.printStackTrace();
            serviceId = 0;
        }
        getButton(R.id.pmsFollowupSaveButton).setText(getText(R.string.string_update));
        getButton(R.id.pmsFollowupDeleteButton).setVisibility(View.VISIBLE);
        getButton(R.id.pmsFollowupEdiCancelButton).setVisibility(View.VISIBLE);
        Utilities.Disable(this, R.id.pmsFollowupDateEditText);
    }

    private void pmsFollowupSaveAsJson() {
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            json = saveHeader(json);

            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            if (editMode) {
                json.put("serviceId", serviceId);
            } else {
            }

            Log.d("PMS Followup Save Json", ROOTKEY + ":{" + json.toString() + "}");

            asyncPMSInfoUpate = new AsyncPMSInfoUpate(this, this);
            asyncPMSInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

            resetActivity();

        } catch (JSONException jse) {
            Log.e("PMS JSON Exception: ", jse.getMessage());
        }
    }

    private void resetActivity() {
        getButton(R.id.pmsFollowupSaveButton).setText(getText(R.string.string_save));
        getButton(R.id.pmsFollowupDeleteButton).setVisibility(View.GONE);
        getButton(R.id.pmsFollowupEdiCancelButton).setVisibility(View.GONE);
        editMode = false;
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        String queryString = "{" +
                "healthId:" + healthId + "," +
                "pmsLoad:\"\"," +
                (isRetrieval ? "" : "providerId:" + providerId + ",") +
                "pmsFollowupLoad:" + (isRetrieval ? "\"\"," : (editMode ? "update," : "insert,")) +
                "pmsCount:" + pmsCount +
                "}";
        return new JSONObject(queryString);
    }

    private JSONObject saveHeader(JSONObject json) throws JSONException {
        json.put("mobileNo", mobileNo);
        json.put("serviceSource", "");

        return json;
    }

    @Override
    public void callbackAsyncTask(String result) {
        historyContentLayout.removeAllViews(); //clear the history list first.

        //setting initial values..........
        fixedCount = 0;
        boolean fixedFlag = false;

        Log.d(LOGTAG, "PMS-ANDROID response received:\n\t" + result);

        try {
            jsonStr = new JSONObject(result);
            lastPMSFollowup = (jsonStr.has("followupCount") ? jsonStr.getInt("followupCount") : 0);
            Log.d(LOGTAG, "JSON Response:\n" + jsonStr.toString());
            Log.d(LOGTAG, "Followup Count:" + lastPMSFollowup);
            if (lastPMSFollowup >= 1) {
                showHideHistoryModifyButton(jsonStr);

                JSONObject followupJSON = jsonStr.getJSONObject("followup");
                lastServiceJSON = followupJSON.getJSONObject(String.valueOf(lastPMSFollowup));
                for (int i = 1; i <= lastPMSFollowup && lastPMSFollowup != 0; i++) {
                    JSONObject singleVisit = followupJSON.getJSONObject(String.valueOf(i));
                    displayList = new ArrayList<>();

                    HistoryListMaker<PermanentmethodFollowupActivity> historyListMaker = new HistoryListMaker<>(
                            this, /*pill condom service activity*/
                            singleVisit, /*json containing keys*/
                            historyLabelMap, /*history details*/
                            compositeMap /*fields whose values are given against multiple keys*/
                    );

                    try {
                        displayList.addAll(historyListMaker.getDisplayList());
                    } catch (Exception e) {
                        Log.e(LOGTAG, String.format("ERROR: %s", e.getMessage()));
                        Utilities.printTrace(e.getStackTrace(), 10);
                    }
                    Log.d(LOGTAG, String.format("Visit: %d, Size after adding [MIV] to the list is %d", i, displayList.size()));

                    listDataHeader = new ArrayList<>();

                    HashMap<String, List<DisplayValue>> listDisplayValues = new HashMap<>();

                    listDataHeader.add(getString(R.string.followup) + Utilities.ConvertNumberToBangla(String.valueOf(i)) + " (" +
                            (fixedFlag ? getString(R.string.fixed) : getString(R.string.unfixed)) + ")");
                    listDisplayValues.put(listDataHeader.get(0), displayList);

                    ExpandableDisplayListAdapter displayListAdapter = new ExpandableDisplayListAdapter(this, listDataHeader, listDisplayValues, true);

                    initPage();

                    historyContentLayout.addView(expandableListViewPMS);
                    expandableListViewPMS.setScrollingCacheEnabled(true);
                    expandableListViewPMS.setAdapter(displayListAdapter);

                    //keep history visible and its child expandable
                    for (int j = 0; j < displayListAdapter.getGroupCount(); j++) {
                        expandableListViewPMS.expandGroup(j);
                    }
                    if (history_layout.getVisibility() == View.GONE) {
                        history_layout.setVisibility(View.VISIBLE);
                        getTextView(R.id.pmsFOllowupBlanLabelLabel).
                                setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                    }
                    //...........................................................................

                    historyContentLayout.invalidate();
                }
            } else {
                //hide delete button if all the history is cleared by the provider
                getButton(R.id.modifyLastPMSFollowupButton).setVisibility(View.INVISIBLE);
            }
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        } catch (Exception e) {
            Utilities.printTrace(e.getStackTrace(), 10);
        }
    }

    private void initPage() {
        expandableListViewPMS = new ExpandableListView(this);
        expandableListViewPMS.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        expandableListViewPMS.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expandableListViewPMS.setIndicatorBounds(0, 0);
        expandableListViewPMS.setChildIndicatorBounds(0, 0);
        expandableListViewPMS.setStackFromBottom(true);
    }

    private void showHideHistoryModifyButton(JSONObject jso) {
        Utilities.SetVisibility(this, R.id.modifyLastPMSFollowupButton, isLastPMSFollowupModifiable(jso) ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean isLastPMSFollowupModifiable(JSONObject jso) {
        String lastPMSKey = String.valueOf(lastPMSFollowup);
        boolean thresholdPeriodPassed = false;
        try {
            JSONObject lastVisit = jso.getJSONObject("followup").getJSONObject(lastPMSKey);
            String providerCode = lastVisit.getString("providerId");
            Log.d(LOGTAG, "Last Service " + lastPMSKey + " was provide by: \t" + providerCode);
            if (lastVisit.has("systemEntryDate") && !lastVisit.getString("systemEntryDate").equals("")) {
                Date lastVisitEntryDate = Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE, lastVisit.getString("systemEntryDate"));
                thresholdPeriodPassed = Utilities.getDateDiff(lastVisitEntryDate, new Date(), TimeUnit.DAYS) > Flag.UPDATE_THRESHOLD;
            }
            return (provider.getProviderCode().equals(providerCode) && !thresholdPeriodPassed);
            //return (provider.getProviderCode().equals(providerCode));

        } catch (JSONException jse) {
            Log.e(LOGTAG, String.format(" JSON Error (PMS FOllow-up History): %s", jse.getMessage()));
            Utilities.printTrace(jse.getStackTrace(), 10);
        } catch (ParseException pse) {
            pse.printStackTrace();
        }

        return false;
    }

    public void deleteLastPMSFollowup() {
        AlertDialogCreator.SimpleDecisionDialog(PermanentmethodFollowupActivity.this, getString(R.string.ServiceDeletionWarning),
                "Delete Service?", android.R.drawable.ic_delete);
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
            deleteJson.put("pmsFollowupLoad", "delete");
            asyncPMSInfoUpate = new AsyncPMSInfoUpate(this, this);
            asyncPMSInfoUpate.execute(deleteJson.toString(), SERVLET, ROOTKEY);

            resetActivity();

        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete PMS Followup request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    void setHistoryLabelMapping() {
        historyLabelMap.put("followupDate", Pair.create(getString(R.string.followup_date), new Integer[]{0}));
        historyLabelMap.put("followupReason", Pair.create(getString(R.string.followup_reason_label), new Integer[]{R.array.Permanent_Method_Followup_Reason_DropDown}));
        if(gender.equals("1")) {
            historyLabelMap.put("complication", Pair.create(getString(R.string.difficulty), new Integer[]{R.array.PM_Followup_Complication_Male_DropDown}));
        } else {
            historyLabelMap.put("complication", Pair.create(getString(R.string.difficulty), new Integer[]{R.array.PM_Followup_Complication_Female_DropDown}));
        }
        historyLabelMap.put("management", Pair.create(getString(R.string.pms_management), new Integer[]{R.array.PM_Followup_Management_Female_DropDown}));
        historyLabelMap.put("examination", Pair.create(getString(R.string.followup_exam_label), new Integer[]{R.array.PM_Followup_Examination_DropDown}));
        historyLabelMap.put("treatment", Pair.create(getString(R.string.treatment), new Integer[]{R.array.PMS_Treatment_Dropdown}));
        historyLabelMap.put("doctorName", Pair.create(getString(R.string.followup_doctor_name), new Integer[]{0}));
        historyLabelMap.put("doctorDesignation", Pair.create(getString(R.string.followup_doctor_designation), new Integer[]{R.array.Permanent_Method_Provider_Doctor_Designation_DropDown}));
        historyLabelMap.put("refer", Pair.create(getString(R.string.refer), new Integer[]{0}));
        historyLabelMap.put("referCenterName", Pair.create(getString(R.string.history_c_name), new Integer[]{R.array.FacilityType_DropDown}));
        historyLabelMap.put("referReason", Pair.create(getString(R.string.referReason), new Integer[]{R.array.PM_Refer_Reason_DropDown}));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean hasTheRequiredFields() {
        boolean valid = true;
        boolean specialInvalid=false;

        if (!Validation.hasText(getEditText(R.id.pmsFollowupDateEditText))) valid = false;
        if (!Validation.hasText(getEditText(R.id.pmsFollowupDoctorNameEditText))) valid = false;
        if (!Validation.hasSelected(getSpinner(R.id.pmsFollowupReasonDropdown))) valid = false;

        if(getCheckbox(R.id.pmsFollowupReferCheckBox).isChecked()){
            if (!Validation.hasSelected(getSpinner(R.id.pmsFollowupReferCenterNameSpinner))) specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.pmsFollowupReferReasonSpinner))) specialInvalid = true;
        }

        if (!valid) {
            Toast toast = Toast.makeText(this, R.string.GeneralSaveWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        } else if(specialInvalid){
            MethodUtils.showSnackBar(findViewById(R.id.pmsFollowupMainLayout),getResources().getString(R.string.refer_validation_message),true);

            return false;
        } else {
            return true;
        }
    }
}
