package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncImplantInfoUpate;
import org.sci.rhis.model.LocationHolder;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by anik.mahamood on 5/3/2017.
 */

public class ImplantActivity extends ClinicalServiceActivity implements
        View.OnClickListener{

    /**
     * Variables........................................................................
     */
    private Context mContext;
    boolean hasConfirmed=false;
    boolean onEditMode=false;
    boolean onUpdateMode=false;

    private int implantCountVal = 0;

    private ProviderInfo provider;

    final private String SERVLET = "implant";
    final private String ROOTKEY = "implantInfo";
    private String LOGTAG = "FP-Implant";

    String healthId,providerId,mobileNo,sateliteCenterName,clientName;

    private MultiSelectionSpinner multiSelectionSpinner;

    private JSONObject jsonResponse = null;

    AsyncImplantInfoUpate asyncImplantInfoUpate;

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_implant_service);
        initialInit();
    }

    private void initialInit(){
        mContext = this;

        initialize();
        setPredefinedValues();
        loadPreviousRecord(); //if any

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
            if(getIntent().hasExtra(Constants.KEY_FP_DELIVERY_DATE)){
                String deliveryDate = getIntent().getStringExtra(Constants.KEY_FP_DELIVERY_DATE);
                int duration=366;
                try {
                    duration = (int) Utilities.getDateDiff(Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,deliveryDate),new Date(), TimeUnit.DAYS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(duration<365){
                    getCheckbox(R.id.implantDeliveryTagCheckBox).setChecked(true);
                }

            }
        }
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
            asyncImplantInfoUpate = new AsyncImplantInfoUpate(this,this);
            asyncImplantInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("PC JSON Exception: ", jse.getMessage());
        }
    }

    @Override
    public void callbackAsyncTask(String result) {
        try {
            Log.d(LOGTAG, "Implant-ANDROID response received:\n\t" + result);
            jsonResponse = new JSONObject(result);
            showSerialNumber(jsonResponse);

            if(jsonResponse.has("implantRetrieve")){
                if(jsonResponse.getInt("implantRetrieve")==1){
                    Utilities.setCheckboxes(jsonCheckboxMap, jsonResponse);
                    Utilities.setSpinners(jsonSpinnerMap, jsonResponse);
                    Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap, jsonResponse);
                    Utilities.setEditTexts(jsonEditTextMap, jsonResponse);
                    Utilities.setEditTextDates(jsonEditTextDateMap, jsonResponse);
                    implantCountVal = jsonResponse.getInt("implantCount");
                    enableEditMode();

                }
            }else{
                if(jsonResponse.has("implantInsertSuccess")){
                    if(jsonResponse.getInt("implantInsertSuccess")==1){
                        implantCountVal = jsonResponse.getInt("implantCount");
                        enableEditMode();
                    }
                }else if(jsonResponse.has("implantUpdateSuccess")) {
                    if(jsonResponse.getInt("implantUpdateSuccess")==1){
                        implantCountVal = jsonResponse.getInt("implantCount");
                        enableEditMode();
                    }
                }
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        }

    }

    private void enableEditMode(){
        Utilities.Disable(this, R.id.implantServiceLayout);
        Utilities.MakeVisible(this, R.id.implantFollowupButton);
        Utilities.Enable(this, R.id.implantFollowupButton);
        getButton( R.id.implantSaveButton).setText("Edit");
        Utilities.Enable(this, R.id.implantSaveButton);
        getButton( R.id.implantAddNewButton).setText("Add New");
        Utilities.Enable(this, R.id.implantAddNewButton);
        Utilities.MakeVisible(this, R.id.implantAddNewButton);
        onEditMode=true;
        onUpdateMode = false;
        hasConfirmed=false;
    }

    @Override
    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("implantAfterDelivery", getCheckbox(R.id.implantDeliveryTagCheckBox));
        jsonCheckboxMap.put("providerAsAttendant", getCheckbox(R.id.implantMyselfCheckbox));//TODO: has to remove later
        jsonCheckboxMap.put("clientAllowance", getCheckbox(R.id.implantClientAllowanceGivenCheckBox));
        jsonCheckboxMap.put("attendantAllowance", getCheckbox(R.id.implantAttendantAllowanceGivenCheckBox));
    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("attendantName", getEditText(R.id.implantAttendantNameEditText));
    }

    @Override
    protected void initiateTextViews() {
        getTextView(R.id.implantTypeTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.implantTypeTextView).getText().toString(), 0, 1));
        getTextView(R.id.implantImplantDateTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.implantImplantDateTextView).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("implantType", getSpinner(R.id.implantTypeSpinner));
        jsonSpinnerMap.put("attendantDesignation", getSpinner(R.id.implantAttendantDesignationDropdown));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("treatment", getMultiSelectionSpinner(R.id.implantTreatmentSpinner));

        final List<String> treatmentList = Arrays.asList(getResources().getStringArray(R.array.Implant_Treatment_DropDown));

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.implantTreatmentSpinner);
        multiSelectionSpinner.setItems(treatmentList);
        multiSelectionSpinner.setSelection(new int[]{});
    }

    @Override
    protected void initiateEditTextDates() {
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH);
        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.implantImplantDatePickerButton, (EditText) findViewById(R.id.implantImplantDateEditText));

        jsonEditTextDateMap.put("implantImplantDate", getEditText(R.id.implantImplantDateEditText));
    }

    @Override
    protected void initiateRadioGroups() {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.implantSaveButton:
                if (!hasTheRequiredFileds()) {
                    return;
                }

                if(hasConfirmed){
                    implantSaveAsJson();
                    if(onUpdateMode) onUpdateMode = false;
                }else{
                    if(onEditMode && !onUpdateMode){
                        Utilities.Enable(this, R.id.implantServiceLayout);
                        getButton( R.id.implantSaveButton).setText("Update");
                        Utilities.MakeInvisible(this, R.id.implantFollowupButton);
                        Utilities.Enable(this, R.id.implantSaveButton);
                        getButton( R.id.implantAddNewButton).setText("Cancel");
                        Utilities.Enable(this, R.id.implantAddNewButton);
                        Utilities.MakeVisible(this, R.id.implantAddNewButton);
                        onUpdateMode = true;
                    }else{
                        Utilities.Disable(this, R.id.implantServiceLayout);
                        getButton( R.id.implantSaveButton).setText("Confirm");
                        Utilities.Enable(this, R.id.implantSaveButton);
                        getButton( R.id.implantAddNewButton).setText("Cancel");
                        Utilities.Enable(this, R.id.implantAddNewButton);
                        Utilities.MakeVisible(this, R.id.implantAddNewButton);
                        //onUpdateMode = false;
                        Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);
                        hasConfirmed=true;
                    }


                }
                break;
            case R.id.implantFollowupButton:
                Intent implantFollowupIntent = new Intent(this, ImplantFollowupActivity.class);
                implantFollowupIntent.putExtra(Constants.KEY_PROVIDER, provider);
                implantFollowupIntent.putExtra(Constants.KEY_MOBILE,mobileNo);
                implantFollowupIntent.putExtra(Constants.KEY_HEALTH_ID, healthId);
                implantFollowupIntent.putExtra(Constants.KEY_Implant_COUNT, implantCountVal);
                implantFollowupIntent.putExtra(Constants.KEY_CLIENT_NAME, clientName);
                implantFollowupIntent.putExtra(Constants.KEY_Implant_IMPLANT_DATE,
                        getEditText(R.id.implantImplantDateEditText).getEditableText().toString());
                startActivity(implantFollowupIntent);
                break;
            case R.id.implantAddNewButton:
                if(!onEditMode && hasConfirmed){
                    Utilities.Enable(this, R.id.implantServiceLayout);
                    getButton(R.id.implantSaveButton).setText(getText(R.string.string_save));
                    Utilities.MakeInvisible(this, R.id.implantAddNewButton);
                    hasConfirmed=false;
                }else if(onEditMode && onUpdateMode){
                    if(hasConfirmed){
                        Utilities.Enable(this, R.id.implantServiceLayout);
                        getButton(R.id.implantSaveButton).setText(getText(R.string.string_update));
                        getButton( R.id.implantAddNewButton).setText(getText(R.string.string_cancel));
                        Utilities.Enable(this, R.id.implantAddNewButton);
                        hasConfirmed=false;
                    }else{
                        //pressing cancel  during updating in onEditMode
                        enableEditMode();
                    }

                }else{
                    Utilities.Reset(this, R.id.implantServiceLayout);
                    getButton(R.id.implantSaveButton).setText("Save");
                    Utilities.MakeInvisible(this, R.id.implantAddNewButton);
                    Utilities.MakeInvisible(this, R.id.implantFollowupButton);
                    hasConfirmed=false;
                    onEditMode=false;
                }
                break;

        }
    }


    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }



    private void implantSaveAsJson(){
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            json = saveHeader(json);

            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            json.put("serviceSource", "");

            Log.d("Implant Save Json", ROOTKEY + ":{" + json.toString() + "}");

            asyncImplantInfoUpate = new AsyncImplantInfoUpate(this, this);
            asyncImplantInfoUpate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("PC JSON Exception: ", jse.getMessage());
        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {

        String queryString = "{" +
                "healthId:" + healthId + "," +
                (isRetrieval ? "" : "providerId:" + providerId + ",") +
                "implantLoad:" + (isRetrieval ? "retrieve," : onEditMode ? "update," : "insert,") +
                "implantCount:" + implantCountVal+
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
                getTextView(R.id.implantRegNoTextView).setText(Utilities.ConvertNumberToBangla(regSerial));
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error coverting registration number");
            Utilities.printTrace(jse.getStackTrace());
        } catch (ParseException pe) {
            Log.e(LOGTAG, "Parsing Error coverting registration date");
            Utilities.printTrace(pe.getStackTrace());
        }
    }

    private boolean hasTheRequiredFileds() {
        boolean valid = true;

        if (!Validation.hasText(getEditText(R.id.implantImplantDateEditText))) valid = false;
        if (!Validation.hasSelected(getSpinner(R.id.implantTypeSpinner))) valid = false;

        if (!valid) {
            Utilities.showAlertToast(this,getString(R.string.GeneralSaveWarning));
            return false;
        }else {
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialogWithResult(this,getResources().getString(R.string.title_Implant),RESULT_OK,ActivityResultCodes.FP_ACTIVITY);
    }
}
