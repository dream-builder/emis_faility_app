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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncIUDInfoUpate;
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

/**
 * Created by arafat.hasan on 3/6/2016.
 */
public class IUDServiceActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    /**
     * Variables........................................................................
     */
    boolean hasConfirmed=false;
    boolean onEditMode=false;
    boolean onUpdateMode=false;

    private int iudCountVal = 0;

    private ProviderInfo provider;

    final private String SERVLET = "iud";
    final private String ROOTKEY = "iudInfo";
    private String LOGTAG = "FP-IUD";

    String healthId,providerId,mobileNo,sateliteCenterName,clientName;

    private MultiSelectionSpinner multiSelectionSpinner;

    private JSONObject jsonResponse = null;

    private JSONObject villJson = null;
    private LocationHolder blanc = new LocationHolder();
    private String zillaString = "";

    AsyncIUDInfoUpate asyncIUDInfoUpate;

    ArrayList<LocationHolder> districtList;
    ArrayList<LocationHolder> upazillaList;
    ArrayList<LocationHolder> unionList;
    ArrayList<LocationHolder> villageList;

    ArrayAdapter<LocationHolder> zillaAdapter;
    ArrayAdapter<LocationHolder> upazilaAdapter;
    ArrayAdapter<LocationHolder> unionAdapter;
    ArrayAdapter<LocationHolder> villageAdapter;

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    private String divValue, distValue, upValue, unValue, vilValue, mouzaValue;
    private String prevZillaCode, prevUpCode, prevUnCode, prevVillCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iud_service);
        initialInit();
    }

    private void initialInit(){
        districtList    =  new ArrayList<>();
        upazillaList    =  new ArrayList<>();
        unionList       =  new ArrayList<>();
        villageList     =  new ArrayList<>();

        initiateLocationHolder();

        addAndSetSpinners();
        initialize();
        setPredefinedValues();
        loadPreviousRecord(); //if any

    }

    private void initiateLocationHolder(){
        loadLocations();
        getSpinner(R.id.iudCompanionZilaSpinner).setAdapter(zillaAdapter);
    }

    private void setPredefinedValues(){
        provider = getIntent().getParcelableExtra(Constants.KEY_PROVIDER);
        providerId = String.valueOf(provider.getProviderCode());
        healthId  = getIntent().getStringExtra(Constants.KEY_HEALTH_ID);
        mobileNo = getIntent().getStringExtra(Constants.KEY_MOBILE);
        sateliteCenterName = provider.getSatelliteName();
        if(getIntent().hasExtra(Constants.KEY_CLIENT_NAME)){
            clientName=getIntent().getStringExtra(Constants.KEY_CLIENT_NAME);
        }else{
            clientName="";
        }
        if(getIntent().getBooleanExtra(Constants.KEY_IS_NEW,false)){
            getEditText(R.id.iudImplantDateEditText).setText(Converter.dateToString(Constants.SHORT_SLASH_FORMAT_BRITISH,new Date()));
            if(getIntent().hasExtra(Constants.KEY_FP_LMP_DATE)){
                getEditText(R.id.iudLmpDateEditText).setText(getIntent().getStringExtra(Constants.KEY_FP_LMP_DATE));
            }
            if(getIntent().hasExtra(Constants.KEY_FP_DELIVERY_DATE)){
                String deliveryDate = getIntent().getStringExtra(Constants.KEY_FP_DELIVERY_DATE);
                int duration=366;
                try {
                    duration = (int) Utilities.getDateDiff(Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,deliveryDate),new Date(), TimeUnit.DAYS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(duration<365){
                    getCheckbox(R.id.iudDeliveryTagCheckBox).setChecked(true);
                }

            }
        }
        getSpinner(R.id.iudTypeSpinner).setSelection(1);
        if(getIntent().hasExtra(Constants.KEY_FP_EXAMINATION)){
            try {
                JSONObject examJson = new JSONObject(getIntent().getStringExtra(Constants.KEY_FP_EXAMINATION));
                setExamValues(examJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setExamValues(JSONObject json){
        Utilities.setSpinners(jsonSpinnerMap,json);
        Utilities.setEditTexts(jsonEditTextMap,json);
    }

    private void loadPreviousRecord(){
        JSONObject json;
        try {
            json = buildQueryHeader(true);
            asyncIUDInfoUpate = new AsyncIUDInfoUpate(this,this);
            asyncIUDInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

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
            Log.d(LOGTAG, "IUD-ANDROID response received:\n\t" + result);
            jsonResponse = new JSONObject(result);
            showSerialNumber(jsonResponse);

            if(jsonResponse.has("iudRetrieve")){
                if(jsonResponse.getInt("iudRetrieve")==1){
                    Utilities.setCheckboxes(jsonCheckboxMap, jsonResponse);
                    Utilities.setSpinners(jsonSpinnerMap, jsonResponse);
                    Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap, jsonResponse);
                    Utilities.setEditTexts(jsonEditTextMap, jsonResponse);
                    Utilities.setEditTextDates(jsonEditTextDateMap, jsonResponse);
                    iudCountVal = jsonResponse.getInt("iudCount");
                    if (!jsonResponse.getString("companionAddress").equals("")){
                        setPreviousLocation(jsonResponse.getString("companionAddress"));
                    }
                    enableEditMode();

                }
            }else{
                if(jsonResponse.has("iudInsertSuccess")){
                    if(jsonResponse.getInt("iudInsertSuccess")==1){
                        iudCountVal = jsonResponse.getInt("iudCount");
                        enableEditMode();
                    }
                }else if(jsonResponse.has("iudUpdateSuccess")) {
                    if(jsonResponse.getInt("iudUpdateSuccess")==1){
                        iudCountVal = jsonResponse.getInt("iudCount");
                        enableEditMode();
                    }
                }
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        }

    }

    private void setPreviousLocation(String locJson){
        String[] locationHeads = locJson.split(",");
        if(locationHeads.length>=2){
            String prevDivCode = locationHeads[0];
            String prevDistCode = locationHeads[1];
            prevZillaCode = prevDistCode+"_"+prevDivCode;
        }
        if(locationHeads.length>=3){
            prevUpCode = locationHeads[2];
        }
        if(locationHeads.length>=4){
            prevUnCode = locationHeads[3];
        }
        if(locationHeads.length>=5){
            String prevMouza = locationHeads[4];
            String prevVillage = locationHeads[5];
            prevVillCode = prevVillage+"_"+prevMouza;
        }
        LocationHolder prevDistrictObjectVal=null;
        for(LocationHolder singleLoc:districtList){
            if(singleLoc.getCode().equals(prevZillaCode)){
                prevDistrictObjectVal=singleLoc;
            }
        }
        int pos=0;
        if(prevDistrictObjectVal!=null){
            pos = zillaAdapter.getPosition(prevDistrictObjectVal);
        }
        getSpinner(R.id.iudCompanionZilaSpinner).setSelection(pos);
    }

    private void enableEditMode(){
        Utilities.Disable(this, R.id.iudServiceLayout);
        Utilities.MakeVisible(this, R.id.iudFollowupButton);
        Utilities.Enable(this, R.id.iudFollowupButton);
        getButton( R.id.iudSaveButton).setText(getText(R.string.string_edit));
        Utilities.Enable(this, R.id.iudSaveButton);
        getButton( R.id.iudAddNewButton).setText(getText(R.string.str_add_new));
        Utilities.Enable(this, R.id.iudAddNewButton);
        Utilities.MakeVisible(this, R.id.iudAddNewButton);
        onEditMode=true;
        onUpdateMode = false;
        hasConfirmed=false;
    }

    @Override
    protected void initiateCheckboxes() {
        getCheckbox(R.id.iudDeliveryTagCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.iudMyselfCheckbox).setOnCheckedChangeListener(this);
        jsonCheckboxMap.put("iudAfterDelivery", getCheckbox(R.id.iudDeliveryTagCheckBox));
        jsonCheckboxMap.put("providerAsAttendant", getCheckbox(R.id.iudMyselfCheckbox));
        jsonCheckboxMap.put("companionAllowance", getCheckbox(R.id.iudCompanionAllowanceGivenCheckBox));
        jsonCheckboxMap.put("clientAllowance", getCheckbox(R.id.iudClientAllowanceGivenCheckBox));
        jsonCheckboxMap.put("attendantAllowance", getCheckbox(R.id.iudAttendantAllowanceGivenCheckBox));
    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("companionName", getEditText(R.id.iudCompanionNameEditText));
        jsonEditTextMap.put("attendantName", getEditText(R.id.iudAttendantNameEditText));
    }

    @Override
    protected void initiateTextViews() {
        getTextView(R.id.iudImplantDateTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.iudImplantDateTextView).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("iudType", getSpinner(R.id.iudTypeSpinner));
        jsonSpinnerMap.put("attendantDesignation", getSpinner(R.id.iudAttendantDesignationDropdown));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("treatment", getMultiSelectionSpinner(R.id.iudTreatmentSpinner));

        final List<String> treatmentList = Arrays.asList(getResources().getStringArray(R.array.IUD_Treatment_DropDown));

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.iudTreatmentSpinner);
        multiSelectionSpinner.setItems(treatmentList);
        multiSelectionSpinner.setSelection(new int[]{});
    }

    @Override
    protected void initiateEditTextDates() {
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH);
        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.iudLmpDatePickerButton, (EditText) findViewById(R.id.iudLmpDateEditText));
        datePickerPair.put(R.id.iudImplantDatePickerButton, (EditText) findViewById(R.id.iudImplantDateEditText));

        jsonEditTextDateMap.put("iudImplantDate", getEditText(R.id.iudImplantDateEditText));
        jsonEditTextDateMap.put("LMP", getEditText(R.id.iudLmpDateEditText));
    }

    @Override
    protected void initiateRadioGroups() {

    }

    private void addAndSetSpinners() {
        getSpinner(R.id.iudCompanionZilaSpinner).setOnItemSelectedListener(this);
        getSpinner(R.id.iudCompanionUpazilaSpinner).setOnItemSelectedListener(this);
        getSpinner(R.id.iudCompanionUnionSpinner).setOnItemSelectedListener(this);
        getSpinner(R.id.iudCompanionVillageSpinner).setOnItemSelectedListener(this);


        zillaAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        upazilaAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        unionAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        villageAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);

        unionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()){
            case R.id.iudDeliveryTagCheckBox:
                //do nothing.....
                break;

            case R.id.iudMyselfCheckbox:
                if(isChecked) {
                    getEditText(R.id.iudAttendantNameEditText).setText(provider.getProviderName());
                    getSpinner(R.id.iudAttendantDesignationDropdown).setSelection(1);
                    Utilities.Disable(this, R.id.iudAttendantNameEditText);
                    Utilities.Disable(this, R.id.iudAttendantDesignationLayout);
                }
                else {
                    getEditText(R.id.iudAttendantNameEditText).setText("");
                    getSpinner(R.id.iudAttendantDesignationDropdown).setSelection(0);
                    Utilities.Enable(this, R.id.iudAttendantNameEditText);
                    Utilities.Enable(this, R.id.iudAttendantDesignationLayout);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iudSaveButton:
                if (!hasTheRequiredFileds()) {
                return;
            }
                if(hasConfirmed){
                    iudSaveAsJson();
                    if(onUpdateMode) onUpdateMode = false;
                }else{
                    if(onEditMode && !onUpdateMode){
                        Utilities.Enable(this, R.id.iudServiceLayout);
                        getButton( R.id.iudSaveButton).setText(getText(R.string.string_update));
                        Utilities.MakeInvisible(this, R.id.iudFollowupButton);
                        Utilities.Enable(this, R.id.iudSaveButton);
                        getButton( R.id.iudAddNewButton).setText(getText(R.string.string_cancel));
                        Utilities.Enable(this, R.id.iudAddNewButton);
                        Utilities.MakeVisible(this, R.id.iudAddNewButton);
                        onUpdateMode = true;
                    }else{
                        Utilities.Disable(this, R.id.iudServiceLayout);
                        getButton( R.id.iudSaveButton).setText(getText(R.string.string_confirm));
                        Utilities.Enable(this, R.id.iudSaveButton);
                        getButton( R.id.iudAddNewButton).setText(getText(R.string.string_cancel));
                        Utilities.Enable(this, R.id.iudAddNewButton);
                        Utilities.MakeVisible(this, R.id.iudAddNewButton);
                        //onUpdateMode = false;
                        Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);
                        hasConfirmed=true;
                    }


                }
                break;
            case R.id.iudFollowupButton:
                Intent iudFollowupIntent = new Intent(this, IUDFollowupActivity.class);
                iudFollowupIntent.putExtra(Constants.KEY_PROVIDER, provider);
                iudFollowupIntent.putExtra(Constants.KEY_MOBILE,mobileNo);
                iudFollowupIntent.putExtra(Constants.KEY_HEALTH_ID, healthId);
                iudFollowupIntent.putExtra(Constants.KEY_IUD_COUNT, iudCountVal);
                iudFollowupIntent.putExtra(Constants.KEY_CLIENT_NAME, clientName);
                iudFollowupIntent.putExtra(Constants.KEY_IUD_IMPLANT_DATE,
                            getEditText(R.id.iudImplantDateEditText).getEditableText().toString());
                startActivity(iudFollowupIntent);
                break;
            case R.id.iudAddNewButton:
                if(!onEditMode && hasConfirmed){
                    Utilities.Enable(this, R.id.iudServiceLayout);
                    getButton(R.id.iudSaveButton).setText(getText(R.string.string_save));
                    Utilities.MakeInvisible(this, R.id.iudAddNewButton);
                    hasConfirmed=false;
                }else if(onEditMode && onUpdateMode){
                    if(hasConfirmed){
                        Utilities.Enable(this, R.id.iudServiceLayout);
                        getButton(R.id.iudSaveButton).setText(getText(R.string.string_update));
                        getButton( R.id.iudAddNewButton).setText(getText(R.string.string_cancel));
                        Utilities.Enable(this, R.id.iudAddNewButton);
                        hasConfirmed=false;
                    }else{
                        //pressing cancel  during updating in onEditMode
                        enableEditMode();
                    }

                }else{
                    Utilities.Reset(this, R.id.iudServiceLayout);
                    Utilities.Reset(this, R.id.iudCompanionAddressInnerLayout);
                    getButton(R.id.iudSaveButton).setText(getText(R.string.string_save));
                    Utilities.MakeInvisible(this, R.id.iudAddNewButton);
                    Utilities.MakeInvisible(this, R.id.iudFollowupButton);
                    hasConfirmed=false;
                    onEditMode=false;
                }
                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.iudCompanionZilaSpinner:
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
                getSpinner(R.id.iudCompanionUpazilaSpinner).setAdapter(upazilaAdapter);
                //during retrieval....
                if(prevUpCode!=null){
                    LocationHolder prevUpazillaObjectVal=null;
                    for(LocationHolder singleLoc:upazillaList){
                        if(singleLoc.getCode().equals(prevUpCode)){
                            prevUpazillaObjectVal=singleLoc;
                        }
                    }
                    int pos=0;
                    if(prevUpazillaObjectVal!=null){
                        pos = upazilaAdapter.getPosition(prevUpazillaObjectVal);
                    }
                    getSpinner(R.id.iudCompanionUpazilaSpinner).setSelection(pos);
                }
                break;
            case R.id.iudCompanionUpazilaSpinner:

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
                getSpinner(R.id.iudCompanionUnionSpinner).setAdapter(unionAdapter);
                //during retrieval....
                if(prevUnCode!=null){
                    LocationHolder prevUnionObjectVal=null;
                    for(LocationHolder singleLoc:unionList){
                        if(singleLoc.getCode().equals(prevUnCode)){
                            prevUnionObjectVal=singleLoc;
                        }
                    }
                    int pos=0;
                    if(prevUnionObjectVal!=null){
                        pos = unionAdapter.getPosition(prevUnionObjectVal);
                    }
                    getSpinner(R.id.iudCompanionUnionSpinner).setSelection(pos);
                }
                break;
            case R.id.iudCompanionUnionSpinner:
                LocationHolder union = unionList.get(position);
                villageList.clear();
                villageAdapter.clear();
                villageList.add(blanc);

                loadVillageFromJson(
                        ((LocationHolder) getSpinner(R.id.iudCompanionZilaSpinner).getSelectedItem()).getCode().split("_")[0],
                        ((LocationHolder) getSpinner(R.id.iudCompanionUpazilaSpinner).getSelectedItem()).getCode(),
                        ((LocationHolder) getSpinner(R.id.iudCompanionUnionSpinner).getSelectedItem()).getCode(),
                        villageList);
                for (LocationHolder holder : villageList) {
                    Log.d(LOGTAG, "Village: -> " + holder.getBanglaName());
                }


                villageAdapter.addAll(villageList);
                getSpinner(R.id.iudCompanionVillageSpinner).setAdapter(villageAdapter);
                //during retrieval....
                if(prevVillCode!=null){
                    LocationHolder prevVillObjectVal=null;
                    for(LocationHolder singleLoc:villageList){
                        if(singleLoc.getCode().equals(prevVillCode)){
                            prevVillObjectVal=singleLoc;
                        }
                    }
                    int pos=0;
                    if(prevVillObjectVal!=null){
                        pos = villageAdapter.getPosition(prevVillObjectVal);
                    }
                    getSpinner(R.id.iudCompanionVillageSpinner).setSelection(pos);
                }
                Log.d(LOGTAG, "Union Case: -> ");

                break;
            case R.id.iudCompanionVillageSpinner:
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
            if(villJson == null) {
                villJson = LocationHolder.getVillageJson();
            }

            if( union.equals("none") || upazila.equals("none") || zilla.equals("none") ||
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
        if(getSpinner(R.id.iudCompanionZilaSpinner).getSelectedItemPosition()!=0){
            distValue = ((LocationHolder) getSpinner(R.id.iudCompanionZilaSpinner).getSelectedItem()).getCode().split("_")[0];
            divValue = ((LocationHolder) getSpinner(R.id.iudCompanionZilaSpinner).getSelectedItem()).getCode().split("_")[1];
            address.append(divValue+","+distValue);
        }
        if(getSpinner(R.id.iudCompanionUpazilaSpinner).getSelectedItemPosition()!=0){
            upValue = ((LocationHolder) getSpinner(R.id.iudCompanionUpazilaSpinner).getSelectedItem()).getCode();
            address.append(","+upValue);
        }
        if(getSpinner(R.id.iudCompanionUnionSpinner).getSelectedItemPosition()!=0){
            unValue = ((LocationHolder) getSpinner(R.id.iudCompanionUnionSpinner).getSelectedItem()).getCode();
            address.append(","+unValue);
        }
        if(getSpinner(R.id.iudCompanionVillageSpinner).getSelectedItemPosition()!=0){
            mouzaValue = ((LocationHolder) getSpinner(R.id.iudCompanionVillageSpinner).getSelectedItem()).getCode().split("_")[1];
            vilValue = ((LocationHolder) getSpinner(R.id.iudCompanionVillageSpinner).getSelectedItem()).getCode().split("_")[0];
            address.append(","+mouzaValue+","+vilValue);
        }
        return address.toString();
    }

    private void iudSaveAsJson(){
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

            Log.d("IUD Save Json", ROOTKEY + ":{" + json.toString() + "}");

            asyncIUDInfoUpate = new AsyncIUDInfoUpate(this, this);
            asyncIUDInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("PC JSON Exception: ", jse.getMessage());
        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {

        String queryString = "{" +
                "healthId:" + healthId + "," +
                (isRetrieval ? "" : "providerId:" + providerId + ",") +
                "iudLoad:" + (isRetrieval ? "retrieve," : onEditMode ? "update," : "insert,") +
                "iudCount:" + iudCountVal+
                "}";
        return new JSONObject(queryString);
    }

    private JSONObject saveHeader(JSONObject json) throws JSONException {

        json.put("sateliteCenterName",sateliteCenterName==null?"":sateliteCenterName);
        json.put("mobileNo",mobileNo);

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
                getTextView(R.id.iudRegNoTextView).setText(Utilities.ConvertNumberToBangla(regSerial));
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error coverting registration number");
            Utilities.printTrace(jse.getStackTrace());
        } catch (ParseException pe) {
            Log.e(LOGTAG, "Parsing Error coverting registration date");
            Utilities.printTrace(pe.getStackTrace());
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialogWithResult(this,getResources().getString(R.string.title_IUD),RESULT_OK,ActivityResultCodes.FP_ACTIVITY);
    }

    private boolean hasTheRequiredFileds() {
        boolean valid = true;

        if (!Validation.hasText(getEditText(R.id.iudImplantDateEditText))) valid = false;

        if (!valid) {
            Utilities.showAlertToast(this,getString(R.string.GeneralSaveWarning));
            return false;
        }else {
            return true;
        }

    }


}
