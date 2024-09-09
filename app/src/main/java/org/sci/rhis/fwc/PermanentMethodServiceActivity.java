package org.sci.rhis.fwc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncPMSInfoUpate;
import org.sci.rhis.model.LocationHolder;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PermanentMethodServiceActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    /**
     * Variables........................................................................
     */
    boolean hasConfirmed = false;
    boolean onEditMode = false;
    boolean onUpdateMode = false;
    String gender = "";
    String client = "";

    private String divValue, distValue, upValue, unValue, vilValue, mouzaValue;
    private String prevZillaCode, prevUpCode, prevUnCode, prevVillCode;

    String referCenterName = null;


    private int permanentMethodCount = 0;

    private ProviderInfo provider;

    final private String SERVLET = "pms";
    final private String ROOTKEY = "pmsInfo";
    private String LOGTAG = "FP-PMS";

    String healthId, providerId, mobileNo, boyChildNo, girlChildNo;

    private MultiSelectionSpinner multiSelectionSpinner;


    private JSONObject jsonResponse = null;
    private JSONObject villJson = null;
    private LocationHolder blanc = new LocationHolder();
    private String zillaString = "";


    AsyncPMSInfoUpate asyncPMSInfoUpate;

    ArrayList<LocationHolder> districtList;
    ArrayList<LocationHolder> upazillaList;
    ArrayList<LocationHolder> unionList;
    ArrayList<LocationHolder> villageList;

    ArrayAdapter<LocationHolder> zillaAdapter;
    ArrayAdapter<LocationHolder> upazilaAdapter;
    ArrayAdapter<LocationHolder> unionAdapter;
    ArrayAdapter<LocationHolder> villageAdapter;

    ArrayAdapter<String> operationTypeMale;
    ArrayAdapter<String> operationTypeFemale;

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permanent_method_service);

        initialInit();
    }


    private void initialInit() {
        districtList = new ArrayList<>();
        upazillaList = new ArrayList<>();
        unionList = new ArrayList<>();
        villageList = new ArrayList<>();


        operationTypeMale = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.PM_Operation_Name_Male_DropDown));
        operationTypeFemale = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.PM_Operation_Name_Female_DropDown));

        initiateLocationHolder();
        addAndSetSpinners();
        setPredefinedValues();
        initialize();
        loadPreviousRecord(); //if any
    }

    private void initiateLocationHolder(){
        loadLocations();
        getSpinner(R.id.pmsCompanionZilaSpinner).setAdapter(zillaAdapter);
    }

    private void setPredefinedValues() {
        provider = getIntent().getParcelableExtra(Constants.KEY_PROVIDER);
        providerId = String.valueOf(provider.getProviderCode());
        healthId = getIntent().getStringExtra(Constants.KEY_HEALTH_ID);
        mobileNo = getIntent().getStringExtra(Constants.KEY_MOBILE);
        gender = getIntent().getStringExtra(Constants.KEY_GENDER);
        client = getIntent().getStringExtra(Constants.KEY_CLIENT_NAME);
        boyChildNo = getIntent().getStringExtra(Constants.KEY_BOYCOUNT);
        girlChildNo = getIntent().getStringExtra(Constants.KEY_GIRLCOUNT);

        getEditText(R.id.pmsAttendantDoctorNameEditText).setText(getText(R.string.str_doc));//Setting Prefix in Doctor Name
        //getEditText(R.id.pmsClientNameEditText).setText(client);

        //Living Child Numbers
        getEditText(R.id.pmsClientMaleChildInfoEdittext).setText(boyChildNo);
        getEditText(R.id.pmsClientMaleChildInfoEdittext).setEnabled(false);
        getEditText(R.id.pmsClientFemaleChildInfoEdittext).setText(girlChildNo);
        getEditText(R.id.pmsClientFemaleChildInfoEdittext).setEnabled(false);

        if (gender.equals("1")) {
            getCheckbox(R.id.pmsClientClothGivenCheckBox).setText(R.string.lungi_given);
            Utilities.SetVisibility(this, R.id.pmsImpTimeLayout, View.GONE);
            getSpinner(R.id.pmsOperationTypeDropdown).setAdapter(null);
            getSpinner(R.id.pmsOperationTypeDropdown).setAdapter(operationTypeMale);
        } else {
            getSpinner(R.id.pmsOperationTypeDropdown).setAdapter(null);
            getSpinner(R.id.pmsOperationTypeDropdown).setAdapter(operationTypeFemale);
        }


        if (getIntent().getBooleanExtra(Constants.KEY_IS_NEW, false)) {
            getEditText(R.id.pmsImplantDateEditText).setText(Converter.dateToString(Constants.SHORT_SLASH_FORMAT_BRITISH, new Date()));
        }

    }

    private void loadPreviousRecord() {
        JSONObject json;
        try {
            json = buildQueryHeader(true);
            asyncPMSInfoUpate = new AsyncPMSInfoUpate(this, this);
            asyncPMSInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("PC JSON Exception: ", jse.getMessage());
        }
    }

    private void loadLocations() {
        try {
            zillaString = LocationHolder.getZillaUpazillaUnionString();
            districtList.add(blanc);
            LocationHolder.loadListFromJson(zillaString, "nameEnglish", "nameBangla", "Upazila", districtList);

            //set zilla spinner
            zillaAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, districtList);
            zillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        } catch (Exception e) {
            Log.e(LOGTAG,e.getMessage());
        }
    }


    private void loadJsonFile(String fileName, StringBuilder jsonBuilder) throws IOException {
        InputStream is = getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        jsonBuilder.append(new String(buffer, "UTF-8"));

    }


    @Override
    public void callbackAsyncTask(String result) {
        try {
            Log.d(LOGTAG, "PMS-ANDROID response received:\n\t" + result);
            jsonResponse = new JSONObject(result);
            showSerialNumber(jsonResponse);
            String key;

            for (Iterator<String> ii = jsonResponse.keys(); ii.hasNext(); ) {
                key = ii.next();
                Log.d(LOGTAG, "Rcvd Key:" + key + " Value:\'" + jsonResponse.get(key) + "\'");
            }

            if (jsonResponse.has("pmsRetrieve")) {
                if (jsonResponse.getInt("pmsRetrieve") == 1) {
                    Utilities.setCheckboxes(jsonCheckboxMap, jsonResponse);
                    Utilities.setSpinners(jsonSpinnerMap, jsonResponse);
                    Utilities.setEditTexts(jsonEditTextMap, jsonResponse);
                    Utilities.setEditTextDates(jsonEditTextDateMap, jsonResponse);
                    Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap, jsonResponse);
                    permanentMethodCount = jsonResponse.getInt("pmsCount");
                    if (!jsonResponse.getString("companionAddress").equals("")) {
                        setPreviousLocation(jsonResponse.getString("companionAddress"));
                    }
                    enableEditMode();

                }
            } else {
                if (jsonResponse.has("pmsInsertSuccess")) {
                    if (jsonResponse.getInt("pmsInsertSuccess") == 1) {
                        permanentMethodCount = jsonResponse.getInt("pmsCount");
                        enableEditMode();
                    }
                } else if (jsonResponse.has("pmsUpdateSuccess")) {
                    if (jsonResponse.getInt("pmsUpdateSuccess") == 1) {
                        permanentMethodCount = jsonResponse.getInt("pmsCount");
                        enableEditMode();
                    }
                }
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        }

    }

    private void setPreviousLocation(String locJson) {
        String[] locationHeads = locJson.split(",");
        if (locationHeads.length >= 2) {
            String prevDivCode = locationHeads[0];
            String prevDistCode = locationHeads[1];
            prevZillaCode = prevDistCode + "_" + prevDivCode;
        }
        if (locationHeads.length >= 3) {
            prevUpCode = locationHeads[2];
        }
        if (locationHeads.length >= 4) {
            prevUnCode = locationHeads[3];
        }
        if (locationHeads.length >= 5) {
            String prevMouza = locationHeads[4];
            String prevVillage = locationHeads[5];
            prevVillCode = prevVillage + "_" + prevMouza;
        }
        LocationHolder prevDistrictObjectVal = null;
        for (LocationHolder singleLoc : districtList) {
            if (singleLoc.getCode().equals(prevZillaCode)) {
                prevDistrictObjectVal = singleLoc;
            }
        }
        int pos = 0;
        if (prevDistrictObjectVal != null) {
            pos = zillaAdapter.getPosition(prevDistrictObjectVal);
        }
        getSpinner(R.id.pmsCompanionZilaSpinner).setSelection(pos);
    }


    private void enableEditMode() {
        Utilities.Disable(this, R.id.pmsServiceLayout);
        Utilities.MakeVisible(this, R.id.pmsFollowupButton);
        Utilities.Enable(this, R.id.pmsFollowupButton);
        getButton(R.id.pmsSaveButton).setText(getText(R.string.string_edit));
        Utilities.Enable(this, R.id.pmsSaveButton);
        getButton(R.id.pmsAddNewButton).setText(getText(R.string.str_add_new));
        Utilities.Enable(this, R.id.pmsAddNewButton);
        Utilities.MakeVisible(this, R.id.pmsAddNewButton);
        onEditMode = true;
        onUpdateMode = false;
        hasConfirmed = false;
    }

    @Override
    protected void initiateCheckboxes() {
        //Initializing Checkboxes
        getCheckbox(R.id.pmsClientComeAloneCheckBox).setOnCheckedChangeListener(this);//Client Came Alone
        getCheckbox(R.id.pmsCompanionAllowanceGivenCheckBox).setOnCheckedChangeListener(this);// Companion Allowance
        getCheckbox(R.id.pmsClientAllowanceGivenCheckBox).setOnCheckedChangeListener(this);//Client Allowance
        getCheckbox(R.id.pmsClientClothGivenCheckBox).setOnCheckedChangeListener(this);//Cloth
        getCheckbox(R.id.pmsReferCheckBox).setOnCheckedChangeListener(this);//Refer (For Refer Layout To pop-out)

        //Storing JSON Key-Value
        jsonCheckboxMap.put("cloth", getCheckbox(R.id.pmsClientClothGivenCheckBox));
        jsonCheckboxMap.put("clientAllowance", getCheckbox(R.id.pmsClientAllowanceGivenCheckBox));
        jsonCheckboxMap.put("refer", getCheckbox(R.id.pmsReferCheckBox));
        jsonCheckboxMap.put("clientCameAlone", getCheckbox(R.id.pmsClientComeAloneCheckBox));
        jsonCheckboxMap.put("companionAllowance", getCheckbox(R.id.pmsCompanionAllowanceGivenCheckBox));


    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("doctorName", getEditText(R.id.pmsAttendantDoctorNameEditText));
        jsonEditTextMap.put("companionName", getEditText(R.id.pmsCompanionNameEditText));
        jsonEditTextMap.put("companionMobileNo", getEditText(R.id.Companion_Mobile_no));

    }

    @Override
    protected void initiateTextViews() {
        getTextView(R.id.pmsImplantDateTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pmsImplantDateTextView).getText().toString(), 0, 1));
        getTextView(R.id.pmsOperationTypeTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pmsOperationTypeTextView).getText().toString(), 0, 1));
        getTextView(R.id.pmsImpDateTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pmsImpDateTextView).getText().toString(), 0, 1));
        getTextView(R.id.pmsReferCenterNameTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pmsReferCenterNameTextView).getText().toString(), 0, 1));
        getTextView(R.id.pmsReferReasonLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pmsReferReasonLabel).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("doctorDesignation", getSpinner(R.id.pmsDoctorDesignationDropdown));
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.pmsReferCenterNameSpinner));
        jsonSpinnerMap.put("implantOperationTime", getSpinner(R.id.pmsOperationTimeDropdown));
        jsonSpinnerMap.put("companionDesignation", getSpinner(R.id.pmsCompanionDesignationDropdown));
        jsonSpinnerMap.put("operationType", getSpinner(R.id.pmsOperationTypeDropdown));

        referCenterName = getSpinner(R.id.pmsReferCenterNameSpinner).getSelectedItem().toString();
    }


    @Override
    protected void initiateEditTextDates() {
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH);
        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.pmsImplantDatePickerButton, (EditText) findViewById(R.id.pmsImplantDateEditText));

        jsonEditTextDateMap.put("implantOperationDate", getEditText(R.id.pmsImplantDateEditText));

    }

    @Override
    protected void initiateRadioGroups() {

    }

    @Override
    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("treatment", getMultiSelectionSpinner(R.id.pmsMedicineListView));
        jsonMultiSpinnerMap.put("complication", getMultiSelectionSpinner(R.id.pmsComplicacySpinner));
        jsonMultiSpinnerMap.put("management", getMultiSelectionSpinner(R.id.pmsManagementSpinner));
        jsonMultiSpinnerMap.put("referReason", getMultiSelectionSpinner(R.id.pmsReferReasonSpinner));
        jsonMultiSpinnerMap.put("advice", getMultiSelectionSpinner(R.id.pmsAdviceSpinner));
        final List<String> complicacyList;
        final List<String> managementList;
        final List<String> adviceList;

        final List<String> treatmentList = Arrays.asList(getResources().getStringArray(R.array.PMS_Treatment_Dropdown));
        final List<String> referReasonList = Arrays.asList(getResources().getStringArray(R.array.PM_Refer_Reason_DropDown));

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.pmsMedicineListView);
        multiSelectionSpinner.setItems(treatmentList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.pmsReferReasonSpinner);
        multiSelectionSpinner.setItems(referReasonList);
        multiSelectionSpinner.setSelection(new int[]{});


        if (gender.equals("1")) {
            complicacyList = Arrays.asList(getResources().getStringArray(R.array.PMS_Complication_Male_DropDown));
            managementList = Arrays.asList(getResources().getStringArray(R.array.PMS_Management_Male_DropDown));
            adviceList = Arrays.asList(getResources().getStringArray(R.array.PMS_Advice_Male_DropDown));
        } else {
            complicacyList = Arrays.asList(getResources().getStringArray(R.array.PMS_Complication_Female_DropDown));
            managementList = Arrays.asList(getResources().getStringArray(R.array.PMS_Management_Female_DropDown));
            adviceList = Arrays.asList(getResources().getStringArray(R.array.PMS_Advice_Female_DropDown));
        }

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.pmsComplicacySpinner);
        multiSelectionSpinner.setItems(complicacyList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.pmsManagementSpinner);
        multiSelectionSpinner.setItems(managementList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.pmsAdviceSpinner);
        multiSelectionSpinner.setItems(adviceList);
        multiSelectionSpinner.setSelection(new int[]{});
    }


    private void addAndSetSpinners() {
        getSpinner(R.id.pmsCompanionZilaSpinner).setOnItemSelectedListener(this);
        getSpinner(R.id.pmsCompanionUpazilaSpinner).setOnItemSelectedListener(this);
        getSpinner(R.id.pmsCompanionUnionSpinner).setOnItemSelectedListener(this);
        getSpinner(R.id.pmsCompanionVillageSpinner).setOnItemSelectedListener(this);

        zillaAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        upazilaAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        unionAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        villageAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);

        unionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }


    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        int visibility = isChecked ? View.VISIBLE : View.GONE;
        int invisibility = isChecked ? View.GONE : View.VISIBLE;
        switch (view.getId()) {
            case R.id.pmsClientComeAloneCheckBox:
                int referLayout[] = {R.id.pmsCompanionNameLayout, R.id.pmsCompanionDesignationLayout, R.id.pmsCompanionAddressLayout, R.id.pmsCompanionAllowanceLayout};
                Utilities.Reset(this, R.id.pmsCompanionAddressInnerLayout);
                changeLayoutVisibility(invisibility, referLayout);
                break;
            case R.id.pmsClientAllowanceGivenCheckBox:
                //TODO
                break;
            case R.id.pmsClientClothGivenCheckBox:
                //TODO
                break;
            case R.id.pmsReferCheckBox:
                int referLayouts[] = {R.id.pmsReferCenterNameLayout, R.id.pmsReasonLayout};
                changeLayoutVisibility(visibility, referLayouts);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pmsSaveButton:
                if (hasConfirmed) {
                    pmsSaveAsJson();
                    if (onUpdateMode) onUpdateMode = false;
                } else {
                    if (onEditMode && !onUpdateMode) {
                        //Utilities.Enable(this, R.id.pmsServiceLayout);
                        Utilities.Enable(this, R.id.pmsServiceLayout);
                        findViewById(R.id.pmsClientMaleChildInfoEdittext).setEnabled(false);
                        findViewById(R.id.pmsClientFemaleChildInfoEdittext).setEnabled(false);
                        getButton(R.id.pmsSaveButton).setText(getText(R.string.string_update));
                        Utilities.MakeInvisible(this, R.id.pmsFollowupButton);
                        Utilities.Enable(this, R.id.pmsSaveButton);
                        getButton(R.id.pmsAddNewButton).setText(getText(R.string.string_cancel));
                        Utilities.Enable(this, R.id.pmsAddNewButton);
                        Utilities.MakeVisible(this, R.id.pmsAddNewButton);
                        onUpdateMode = true;
                    } else {
                        //Mandatory Fields & Refer Validation Check
                        if (!hasTheRequiredFileds()) {
                            return;
                        } else {
                            findViewById(R.id.pmsClientMaleChildInfoEdittext).setEnabled(false);
                            findViewById(R.id.pmsClientFemaleChildInfoEdittext).setEnabled(false);
                            Utilities.Disable(this, R.id.pmsServiceLayout);
                            getButton(R.id.pmsSaveButton).setText(getText(R.string.string_confirm));
                            Utilities.Enable(this, R.id.pmsSaveButton);
                            getButton(R.id.pmsAddNewButton).setText(getText(R.string.string_cancel));
                            Utilities.Enable(this, R.id.pmsAddNewButton);
                            Utilities.MakeVisible(this, R.id.pmsAddNewButton);
                            //onUpdateMode = false;
                            Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);
                            hasConfirmed = true;
                        }
                    }
                }

                break;
            case R.id.pmsFollowupButton:
                Intent pmsFollowupIntent = new Intent(this, PermanentmethodFollowupActivity.class);
                pmsFollowupIntent.putExtra(Constants.KEY_PROVIDER, provider);
                pmsFollowupIntent.putExtra(Constants.KEY_MOBILE, mobileNo);
                pmsFollowupIntent.putExtra(Constants.KEY_HEALTH_ID, healthId);
                pmsFollowupIntent.putExtra(Constants.KEY_PMS_COUNT, permanentMethodCount);
                pmsFollowupIntent.putExtra(Constants.KEY_GENDER, gender);

                startActivity(pmsFollowupIntent);
                break;
            case R.id.pmsAddNewButton:
                if (!onEditMode && hasConfirmed) {
                    Utilities.Enable(this, R.id.pmsServiceLayout);
                    getButton(R.id.pmsSaveButton).setText(getText(R.string.string_save));
                    Utilities.MakeInvisible(this, R.id.pmsAddNewButton);
                    hasConfirmed = false;
                } else if (onEditMode && onUpdateMode) {
                    if (hasConfirmed) {
                        Utilities.Enable(this, R.id.pmsServiceLayout);
                        getButton(R.id.pmsSaveButton).setText(getText(R.string.string_update));
                        getButton(R.id.pmsAddNewButton).setText(getText(R.string.string_cancel));
                        Utilities.Enable(this, R.id.pmsAddNewButton);
                        hasConfirmed = false;
                    } else {
                        //pressing cancel  during updating in onEditMode
                        enableEditMode();
                    }
                } else {
                    Utilities.Reset(this, R.id.pmsServiceLayout);
                    findViewById(R.id.pmsClientMaleChildInfoEdittext).setEnabled(false);
                    findViewById(R.id.pmsClientFemaleChildInfoEdittext).setEnabled(false);
                    getButton(R.id.pmsSaveButton).setText(getText(R.string.string_save));
                    Utilities.MakeInvisible(this, R.id.pmsAddNewButton);
                    Utilities.MakeInvisible(this, R.id.pmsFollowupButton);
                    hasConfirmed = false;
                    onEditMode = false;
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        switch (parent.getId()) {
            case R.id.pmsCompanionZilaSpinner:
                LocationHolder zilla = districtList.get(position);

                upazillaList.clear();
                upazilaAdapter.clear();
                upazillaList.add(blanc);
                LocationHolder.loadListFromJson(
                        zilla.getSublocation(),
                        "nameEnglishUpazila",
                        "nameBanglaUpazila",
                        "Union",
                        upazillaList);
                for (LocationHolder holder : upazillaList) {
                    Log.d(LOGTAG, "Upazila: -> " + holder.getBanglaName());
                }

                upazilaAdapter.addAll(upazillaList);
                upazilaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getSpinner(R.id.pmsCompanionUpazilaSpinner).setAdapter(upazilaAdapter);
                //during retrieval....
                if (prevUpCode != null) {
                    LocationHolder prevUpazillaObjectVal = null;
                    for (LocationHolder singleLoc : upazillaList) {
                        if (singleLoc.getCode().equals(prevUpCode)) {
                            prevUpazillaObjectVal = singleLoc;
                        }
                    }
                    int pos = 0;
                    if (prevUpazillaObjectVal != null) {
                        pos = upazilaAdapter.getPosition(prevUpazillaObjectVal);
                    }
                    getSpinner(R.id.pmsCompanionUpazilaSpinner).setSelection(pos);
                }
                break;
            case R.id.pmsCompanionUpazilaSpinner:

                LocationHolder upazila = upazillaList.get(position);
                unionList.clear();
                unionAdapter.clear();
                unionList.add(blanc);
                LocationHolder.loadListFromJson(
                        upazila.getSublocation(),
                        "nameEnglishUnion",
                        "nameBanglaUnion",
                        "",
                        unionList);
                for (LocationHolder holder : unionList) {
                    Log.d(LOGTAG, "Union: -> " + holder.getBanglaName());
                }
                unionAdapter.addAll(unionList);
                getSpinner(R.id.pmsCompanionUnionSpinner).setAdapter(unionAdapter);
                //during retrieval....
                if (prevUnCode != null) {
                    LocationHolder prevUnionObjectVal = null;
                    for (LocationHolder singleLoc : unionList) {
                        if (singleLoc.getCode().equals(prevUnCode)) {
                            prevUnionObjectVal = singleLoc;
                        }
                    }
                    int pos = 0;
                    if (prevUnionObjectVal != null) {
                        pos = unionAdapter.getPosition(prevUnionObjectVal);
                    }
                    getSpinner(R.id.pmsCompanionUnionSpinner).setSelection(pos);
                }
                break;
            case R.id.pmsCompanionUnionSpinner:
                LocationHolder union = unionList.get(position);
                villageList.clear();
                villageAdapter.clear();
                villageList.add(blanc);

                loadVillageFromJson(
                        ((LocationHolder) getSpinner(R.id.pmsCompanionZilaSpinner).getSelectedItem()).getCode().split("_")[0],
                        ((LocationHolder) getSpinner(R.id.pmsCompanionUpazilaSpinner).getSelectedItem()).getCode(),
                        ((LocationHolder) getSpinner(R.id.pmsCompanionUnionSpinner).getSelectedItem()).getCode(),
                        villageList);
                for (LocationHolder holder : villageList) {
                    Log.d(LOGTAG, "Village: -> " + holder.getBanglaName());
                }


                villageAdapter.addAll(villageList);
                getSpinner(R.id.pmsCompanionVillageSpinner).setAdapter(villageAdapter);
                //during retrieval....
                if (prevVillCode != null) {
                    LocationHolder prevVillObjectVal = null;
                    for (LocationHolder singleLoc : villageList) {
                        if (singleLoc.getCode().equals(prevVillCode)) {
                            prevVillObjectVal = singleLoc;
                        }
                    }
                    int pos = 0;
                    if (prevVillObjectVal != null) {
                        pos = villageAdapter.getPosition(prevVillObjectVal);
                    }
                    getSpinner(R.id.pmsCompanionVillageSpinner).setSelection(pos);
                }
                Log.d(LOGTAG, "Union Case: -> ");

                break;
            case R.id.pmsCompanionVillageSpinner:
                Log.d(LOGTAG, "Village Case: -> ");

                break;
            default:
                Log.e(LOGTAG, "Unknown spinner: " + parent.getId()
                        + " -> " + getResources().getResourceEntryName(parent.getId()));

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }


    private void loadVillageFromJson(
            String zilla,
            String upazila,
            String union,
            ArrayList<LocationHolder> holderList) {


        try {
            if (villJson == null) {
                villJson = LocationHolder.getVillageJson();
            }

            if (union.equals("none") || upazila.equals("none") || zilla.equals("none") ||
                    union.equals("") || upazila.equals("") || zilla.equals("")) {
                return;
            }

            JSONObject unionJson =
                    villJson.getJSONObject(zilla).getJSONObject(upazila).getJSONObject(union);

            for (Iterator<String> mouzaKey = unionJson.keys(); mouzaKey.hasNext(); ) {
                String mouza = mouzaKey.next();
                JSONObject mouzaJson = unionJson.getJSONObject(mouza);

                for (Iterator<String> villageCode = mouzaJson.keys(); villageCode.hasNext(); ) {
                    String code = villageCode.next();
                    holderList.add(
                            new LocationHolder(
                                    code + "_" + mouza,
                                    mouzaJson.getString(code),
                                    mouzaJson.getString(code),
                                    ""));

                }

                Log.d(LOGTAG, "Mouja - Village: " + mouza + " -> " + unionJson.getString(mouza));

            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }

    private String createdAddressVal() {
        StringBuilder address = new StringBuilder();
        address.append("");
        if (getSpinner(R.id.pmsCompanionZilaSpinner).getSelectedItemPosition() != 0) {
            distValue = ((LocationHolder) getSpinner(R.id.pmsCompanionZilaSpinner).getSelectedItem()).getCode().split("_")[0];
            divValue = ((LocationHolder) getSpinner(R.id.pmsCompanionZilaSpinner).getSelectedItem()).getCode().split("_")[1];
            address.append(divValue + "," + distValue);
        }
        if (getSpinner(R.id.pmsCompanionUpazilaSpinner).getSelectedItemPosition() != 0) {
            upValue = ((LocationHolder) getSpinner(R.id.pmsCompanionUpazilaSpinner).getSelectedItem()).getCode();
            address.append("," + upValue);
        }
        if (getSpinner(R.id.pmsCompanionUnionSpinner).getSelectedItemPosition() != 0) {
            unValue = ((LocationHolder) getSpinner(R.id.pmsCompanionUnionSpinner).getSelectedItem()).getCode();
            address.append("," + unValue);
        }
        if (getSpinner(R.id.pmsCompanionVillageSpinner).getSelectedItemPosition() != 0) {
            mouzaValue = ((LocationHolder) getSpinner(R.id.pmsCompanionVillageSpinner).getSelectedItem()).getCode().split("_")[1];
            vilValue = ((LocationHolder) getSpinner(R.id.pmsCompanionVillageSpinner).getSelectedItem()).getCode().split("_")[0];
            address.append("," + mouzaValue + "," + vilValue);
        }
        return address.toString();
    }

    private void pmsSaveAsJson() {
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            json = saveHeader(json);

            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            json.put("companionAddress", createdAddressVal());
            json.put("serviceSource", "");

            Log.d("PMS Save Json", ROOTKEY + ":{" + json.toString() + "}");

            asyncPMSInfoUpate = new AsyncPMSInfoUpate(this, this);
            asyncPMSInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("PC JSON Exception: ", jse.getMessage());
        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {

        String queryString = "{" +
                "healthId:" + healthId + "," +
                (isRetrieval ? "" : "providerId:" + providerId + ",") +
                "pmsLoad:" + (isRetrieval ? "retrieve," : onEditMode ? "update," : "insert,") +
                "pmsCount:" + permanentMethodCount +
                ",gender:" + gender +
                "}";
        return new JSONObject(queryString);
    }

    private JSONObject saveHeader(JSONObject json) throws JSONException {

        json.put("mobileNo", mobileNo);

        return json;
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
                getTextView(R.id.pmsRegNoTextView).setText(Utilities.ConvertNumberToBangla(regSerial));
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error coverting registration number");
            Utilities.printTrace(jse.getStackTrace());
        } catch (ParseException pe) {
            Log.e(LOGTAG, "Parsing Error coverting registration date");
            Utilities.printTrace(pe.getStackTrace());
        }
    }


    private void changeLayoutVisibility(int visibility, int layouts[]) {
        for (int i = 0; i < layouts.length; i++) {
            Utilities.SetVisibility(this, layouts[i], visibility);
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialogWithResult(this, getResources().getString(R.string.permanent_method_service), RESULT_OK, ActivityResultCodes.FP_ACTIVITY);
    }

    private boolean hasTheRequiredFileds() {
        boolean valid = true;
        boolean specialInvalid = false;

        if (gender.equals("2")) {
            if (!Validation.hasSelected(getSpinner(R.id.pmsOperationTimeDropdown))) valid = false;
        }

        if (!Validation.hasText(getEditText(R.id.pmsImplantDateEditText))) valid = false;
        if (!Validation.hasText(getEditText(R.id.pmsAttendantDoctorNameEditText))) valid = false;
        if (!Validation.hasSelected(getSpinner(R.id.pmsOperationTypeDropdown))) valid = false;

        if (getCheckbox(R.id.pmsReferCheckBox).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.pmsReferCenterNameSpinner)))
                specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.pmsReferReasonSpinner)))
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
            MethodUtils.showSnackBar(findViewById(R.id.pmsServiceMainLayout), getResources().getString(R.string.refer_validation_message), true);
            return false;
        } else {
            return true;
        }
    }

}

