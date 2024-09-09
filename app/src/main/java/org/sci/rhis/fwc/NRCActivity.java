package org.sci.rhis.fwc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import org.sci.rhis.connectivityhandler.AsyncADVSearchUpdate;
import org.sci.rhis.connectivityhandler.AsyncClientInfoUpdate;
import org.sci.rhis.model.LocationHolder;
import org.sci.rhis.model.Person;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.ArrayIndexValues;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.connectivityhandler.AsyncNonRegisterClientInfoUpdate;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class NRCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener, AlertDialogCreator.SingleInputListener,
        AlertDialogCreator.ListViewItemClickListener,
        ArrayIndexValues,
        AlertDialogCreator.DialogButtonClickedListener,
        CompoundButton.OnCheckedChangeListener {

    AsyncNonRegisterClientInfoUpdate NRCInfoUpdateTask;
    private String SERVLET = "nonRegisteredClient";
    private String ROOTKEY = "nonRegisteredClientGeneralInfo";
    private String LOGTAG = "FWC-REGISTRATION";

    private EditText cName, cFatherName, cMotherName, cMobileNo;

    private String getString, md5Result;
    private String prevZilla, prevUpazila, prevUnion, prevVillage, prevGeneratedId;
    private long generatedId;

    private boolean editMode = false;
    private JSONObject lastNRCData = null;
    ProviderInfo provider;

    private int divValue, distValue, upValue, unValue, vilValue, mouzaValue;
    private int countSaveClick = 0;

    private String zillaString = "", selectedExistingClientHealthID = "";

    ArrayList<LocationHolder> districtList;
    ArrayList<LocationHolder> upazillaList;
    ArrayList<LocationHolder> unionList;
    ArrayList<LocationHolder> villageList;

    ArrayAdapter<LocationHolder> zillaAdapter;
    ArrayAdapter<LocationHolder> upazilaAdapter;
    ArrayAdapter<LocationHolder> unionAdapter;
    ArrayAdapter<LocationHolder> villageAdapter;

    private JSONObject villJson = null;
    private LocationHolder blanc = new LocationHolder();

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    private ArrayList<Person> personsList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private int viewIdAlertDialogShown = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nrc);
        provider = getIntent().getParcelableExtra("Provider");
        editMode = getIntent().hasExtra("editMode") ? true : false;


        cMobileNo = (EditText) findViewById(R.id.NrcClients_Mobile_no);

        cName = (EditText) findViewById(R.id.Client_name);
        cFatherName = (EditText) findViewById(R.id.Clients_Father);
        cMotherName = (EditText) findViewById(R.id.Clients_Mother);

        generatedId = 0;

        // ---- JAMIL START---- //
        districtList = new ArrayList<>();
        upazillaList = new ArrayList<>();
        unionList = new ArrayList<>();
        villageList = new ArrayList<>();

        initialize();
        extraInit();
        addAndSetSpinners();
        // ---- JAMIL END----- //

    }

    private void initiateLocationHolder(){
        loadLocations();
        getSpinner(R.id.Clients_District).setAdapter(zillaAdapter);
        if (editMode) {
            setLastData();
        }
        else{
            setProviderLocationByDefault();
            jsonSpinnerMap.get("gender").setSelection(1); //select woman by default
        }

    }

    private void extraInit() {
        getImageView(R.id.imageViewAddNewMobile).setOnClickListener(this);

        datePickerDialog = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.DOBDatePicker, getEditText(R.id.EditTextClientsDate));
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private void setLastData() {
        try {
            lastNRCData = new JSONObject(getIntent().getStringExtra("editMode"));
            getEditText(R.id.Client_name).setText(lastNRCData.getString("cName"));
            try {
                getEditText(R.id.EditTextClientsDate).setText(Converter.convertSdfFormat(Constants.SHORT_HYPHEN_FORMAT_DATABASE,
                        lastNRCData.getString("cDob"), Constants.SHORT_SLASH_FORMAT_BRITISH));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (lastNRCData.has("cHusbandName"))
                getEditText(R.id.Clients_Husband).setText(lastNRCData.getString("cHusbandName"));
                getEditText(R.id.Clients_Father).setText(lastNRCData.getString("cFatherName"));
                getEditText(R.id.Clients_Mother).setText(lastNRCData.getString("cMotherName"));
                getEditText(R.id.nrcBirthWeightValue).setText(lastNRCData.getString("cBirthWeight"));
            if (lastNRCData.has("chGRHNo"))
                getEditText(R.id.Clients_House_No).setText(lastNRCData.getString("chGRHNo"));
            if (lastNRCData.has("cMobileNo"))
                getEditText(R.id.NrcClients_Mobile_no).setText(lastNRCData.getString("cMobileNo"));
            if (lastNRCData.has("cMobileNo2") && !lastNRCData.getString("cMobileNo2").equals("")) {
                Utilities.SetVisibility(this, R.id.layoutMobileAlternate, View.VISIBLE);
                boolean startWithZero = lastNRCData.getString("cMobileNo2").startsWith("0");
                getEditText(R.id.editTextNRCClientMobileAlternate).setText((startWithZero ? "" : "0") + lastNRCData.getString("cMobileNo2"));
                getImageView(R.id.imageViewAddNewMobile).setImageResource(android.R.drawable.ic_delete);

            }
            if (lastNRCData.has("cMemberCount"))
                getEditText(R.id.Clients_MemberCount).setText(lastNRCData.getString("cMemberCount"));
            if (lastNRCData.has("cNID") && !lastNRCData.getString("cNID").equals("")) {
                getEditText(R.id.edNrcNRCNID).setText(lastNRCData.getString("cNID"));
                getCheckbox(R.id.checkboxNoNID).setChecked(false);
            } else {
                getCheckbox(R.id.checkboxNoNID).setChecked(true);
            }

            if (lastNRCData.has("cBrID") && !lastNRCData.getString("cBrID").equals("")) {
                getEditText(R.id.edNrcBRID).setText(lastNRCData.getString("cBrID"));
                getCheckbox(R.id.checkboxNoBrID).setChecked(false);
            } else {
                getCheckbox(R.id.checkboxNoBrID).setChecked(true);
            }
            if (lastNRCData.has("cMemberInfo"))
                getEditText(R.id.Clients_Member).setText(lastNRCData.getString("cMemberInfo"));

            getSpinner(R.id.ClientsSexSpinner).setSelection(Integer.valueOf(lastNRCData.getString("cSex")) - 1);
            getSpinner(R.id.ClientsMaritalStatusSpinner).setSelection(lastNRCData.getString("cMaritalStatus").equals("") ? 0 :
                    Integer.valueOf(lastNRCData.getString("cMaritalStatus")));

            MethodUtils.setSpinnerCustomValue(this, getSpinner(R.id.ClientsNotHavingBRIdSpinner), lastNRCData.getString("cBrIDStatus"), R.array.missing_reason, "-");
            MethodUtils.setSpinnerCustomValue(this, getSpinner(R.id.ClientsNotHavingNIDSpinner), lastNRCData.getString("cNIDStatus"), R.array.missing_reason, "-");
            MethodUtils.setSpinnerCustomValue(this, getSpinner(R.id.ClientsReligionSpinner), lastNRCData.getString("cReligion"), R.array.religion, "-");
            MethodUtils.setSpinnerCustomValue(this, getSpinner(R.id.ClientsEducationSpinner), lastNRCData.getString("cEducation"), R.array.education, "-");
            MethodUtils.setSpinnerCustomValue(this, getSpinner(R.id.ClientsOccupationSpinner), lastNRCData.getString("cOccupation"), R.array.occupation, "-");
            prevGeneratedId = lastNRCData.getString("cHealthID");
            setPreviousLocation(lastNRCData);
            getButton(R.id.nrcProceed).setText("Update");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setProviderLocationByDefault(){
        prevZilla = ProviderInfo.getProvider().getZillaID()+"_"+ProviderInfo.getProvider().getDivID();

        prevUpazila = ProviderInfo.getProvider().getUpazillaID();
        prevUnion = ProviderInfo.getProvider().getUnionID();

        Log.d("Loc: ",prevZilla+" : "+prevUpazila+" : "+prevUnion);

        LocationHolder prevDistrictObjectVal=null;
        for(LocationHolder singleLoc:districtList){
            if(singleLoc.getCode().equals(prevZilla)){
                prevDistrictObjectVal=singleLoc;
            }
        }
        int pos=0;
        if(prevDistrictObjectVal!=null){
            pos = zillaAdapter.getPosition(prevDistrictObjectVal);
        }
        getSpinner(R.id.Clients_District).setSelection(pos);

    }

    //this method is called during editing
    private void setPreviousLocation(JSONObject client) {
        try {
            prevZilla = client.getString("zillaId") + "_" + client.getString("divisionId");

            prevUpazila = client.getString("upazilaId");
            prevUnion = client.getString("unionId");
            prevVillage = client.getString("villageId") + "_" + client.getString("mouzaId");

            Log.d("Loc: ", prevZilla + " : " + prevUpazila + " : " + prevUnion + " : " + prevVillage);

            LocationHolder prevDistrictObjectVal = null;
            for (LocationHolder singleLoc : districtList) {
                if (singleLoc.getCode().equals(prevZilla)) {
                    prevDistrictObjectVal = singleLoc;
                }
            }
            int pos = 0;
            if (prevDistrictObjectVal != null) {
                pos = zillaAdapter.getPosition(prevDistrictObjectVal);
            }
            getSpinner(R.id.Clients_District).setSelection(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initiateLocationHolder();
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

    private String getBaseStringForMD5() {
        Log.d("FoundSumOfStrings", cName.getText().toString() + "  " + cFatherName.getText().toString() + " " + cMotherName.getText().toString());
        //get username and password entered
        divValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_District).getSelectedItem()).getCode().split("_")[1]);
        distValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_District).getSelectedItem()).getCode().split("_")[0]);
        upValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_Upazila).getSelectedItem()).getCode());
        unValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_Union).getSelectedItem()).getCode());
        if (getSpinner(R.id.Clients_Village).getCount() > 1) {
            mouzaValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_Village).getSelectedItem()).getCode().split("_")[1]);
            vilValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_Village).getSelectedItem()).getCode().split("_")[0]);
        }
        getString = distValue + upValue + unValue + mouzaValue + vilValue + Utilities.getDateTimeStringMs();

        Log.d("FoundSumOfStrings!", getString);
        return getString;
    }

    public String computeMD5Hash(String getString) {

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(getString.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }

            md5Result = MD5Hash.toString();
            Log.d("MD5Result B4taken14", md5Result);
            md5Result = md5Result.toString().substring(0, 14);
            Log.d("MD5ResultAfterTaken14", md5Result);
            generatedId = Long.parseLong(md5Result, 16);
            Log.d("MD5ResultAfterTaken16", String.valueOf(generatedId));
            md5Result = Long.toString(generatedId);
            Log.d("MD5ResultAftertoString", md5Result);
            md5Result = md5Result.toString().substring(0, 14);
            Log.d("MD5ResultAfter14again", md5Result);
        } catch (NoSuchAlgorithmException e) {
            //assigning empty string
            md5Result = "";
            e.printStackTrace();
        }
        return md5Result;
    }

    private JSONObject buildQueryHeader() throws JSONException {
        Log.d("Selected District Value", String.valueOf(distValue));
        //TODO - check why this extra call to getBaseStringForMD5()
        getBaseStringForMD5();
        //get info from database
        String queryString = "{" +
                "\"generatedId\":" + (editMode ? prevGeneratedId : computeMD5Hash(getBaseStringForMD5())) + "," +
                "\"providerid\":" + String.valueOf(provider.getProviderCode()) + "," +
                "\"division\":" + String.valueOf(divValue) + "," +
                "\"district\":" + String.valueOf(distValue) + "," +
                "\"upazila\":" + String.valueOf(upValue) + "," +
                "\"union\":" + String.valueOf(unValue) + "," +
                "\"mouza\":" + String.valueOf(mouzaValue) + "," +
                "\"village\":" + String.valueOf(vilValue) +
                "}";
        Log.d("QueryStrig", queryString);
        return new JSONObject(queryString);
    }

    private void nrcSaveToJson() {

        hasTheRequiredFileds();
        NRCInfoUpdateTask = new AsyncNonRegisterClientInfoUpdate(this, this);

        //TODO - Check if the only place this two strings are used, is MD5 generation/build header
        //       Then the following two lines should be placed right there.
        /*int givenAge = Integer.valueOf(getEditText(R.id.Clients_Age).getEditableText().toString());
        dobValue = Converter.dobFromAge(givenAge, Constants.SHORT_HYPHEN_FORMAT_DATABASE);*/

        JSONObject json;
        try {
            json = buildQueryHeader();

            //closing this activity if json data has empty generatedId
            if (!json.has("generatedId") || json.getString("generatedId").isEmpty()) {
                Utilities.showAlertToast(this, getString(R.string.temporary_error_with_retry));
                finish();
                return;
            }

            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getSpinnersValuesAsCustom(jsonSpinnerMapForValues, json, 0, "-");

            getSpecialCases(json);

            Log.d(LOGTAG, json.toString());

            NRCInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);

            Log.d("NRC JSON Sent2SERVLET", json.toString());

        } catch (JSONException jse) {
            Log.e("NRC", "JSON Exception: " + jse.getMessage());
        }

    }

    private void getSpecialCases(JSONObject json) throws JSONException {
        String key = "gender";
        json.put(key, jsonSpinnerMap.get(key).getSelectedItemPosition() + 1);
        //json.put("dob", dobValue);
        if (editMode) json.put("nrcUpdate", "1");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nrc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callbackAsyncTask(String result) {
        Log.d(LOGTAG, result);
        JSONObject json;
        try {
            json = new JSONObject(result);
            String key;

            //DEBUG
            for (Iterator<String> ii = json.keys(); ii.hasNext(); ) {
                key = ii.next();
                System.out.println("1.Key:" + key + " Value:\'" + json.get(key) + "\'");
            }

            Intent intent = new Intent();
            intent.putExtra("generatedId", editMode ? prevGeneratedId : json.getString("cVisibleID"));
            setResult(RESULT_OK, intent);
            finishActivity(ActivityResultCodes.REGISTRATION_ACTIVITY);
            finish();
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }

    @Override
    protected void initiateCheckboxes() {
        //new for partial prs
        jsonCheckboxMap.put("nationality", getCheckbox(R.id.checkBoxNationality));

        getCheckbox(R.id.CBAgeNotKnown).setOnCheckedChangeListener(this);
        getCheckbox(R.id.checkboxNoNID).setOnCheckedChangeListener(this);
        getCheckbox(R.id.checkboxNoBrID).setOnCheckedChangeListener(this);

    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("name", getEditText(R.id.Client_name));
        jsonEditTextMap.put("age", getEditText(R.id.Clients_AgeYear));
        jsonEditTextMap.put("husbandname", getEditText(R.id.Clients_Husband));
        jsonEditTextMap.put("fathername", getEditText(R.id.Clients_Father));
        jsonEditTextMap.put("mothername", getEditText(R.id.Clients_Mother));
        jsonEditTextMap.put("hhgrholdingno", getEditText(R.id.Clients_House_No));
        jsonEditTextMap.put("cellno", getEditText(R.id.NrcClients_Mobile_no));
        jsonEditTextMap.put("birthWeight", getEditText(R.id.nrcBirthWeightValue));

        getEditText(R.id.Clients_AgeDay).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.Clients_AgeDay)));
        getEditText(R.id.Clients_AgeMonth).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.Clients_AgeMonth)));
        getEditText(R.id.Clients_AgeYear).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.Clients_AgeYear)));
        getEditText(R.id.nrcBirthWeightValue).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.nrcBirthWeightValue)));

        //check mobile number exists on text change - adding only
        if (!editMode) {
            getEditText(R.id.NrcClients_Mobile_no).addTextChangedListener(new CustomTextWatcher(this,
                    getEditText(R.id.NrcClients_Mobile_no)));
            getEditText(R.id.editTextNRCClientMobileAlternate).addTextChangedListener(new CustomTextWatcher(this,
                    getEditText(R.id.editTextNRCClientMobileAlternate)));
            getEditText(R.id.edNrcNRCNID).setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus && !getEditText(v.getId()).getText().toString().isEmpty()) {
                    existingDataChecker(v.getId());
                }
            });
            getEditText(R.id.edNrcBRID).setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus && !getEditText(v.getId()).getText().toString().isEmpty()) {
                    existingDataChecker(v.getId());
                }
            });
        }

        //new for partial prs
        jsonEditTextMap.put("cellNo2", getEditText(R.id.editTextNRCClientMobileAlternate));
        jsonEditTextMap.put("memberInfo", getEditText(R.id.Clients_Member));
        jsonEditTextMap.put("memberCount", getEditText(R.id.Clients_MemberCount));
        jsonEditTextMap.put("nid", getEditText(R.id.edNrcNRCNID));
        jsonEditTextMap.put("brId", getEditText(R.id.edNrcBRID));

    }

    private boolean hasTheRequiredFileds() {
        boolean valid = true;
        boolean specialInValid = false;
        if (!Validation.hasText(getEditText(R.id.Client_name))) valid = false;
        if (!Validation.hasText(getEditText(R.id.Clients_Father))) valid = false;
        if (!Validation.hasText(getEditText(R.id.Clients_Mother))) valid = false;
        if (!Validation.hasText(getEditText(R.id.EditTextClientsDate))) valid = false;

        //if(!Validation.hasSelected(getSpinner(R.id.ClientsSexSpinner))) valid=false;
        if (!Validation.hasSelected(getSpinner(R.id.Clients_District))) valid = false;
        if (!Validation.hasSelected(getSpinner(R.id.Clients_Upazila))) valid = false;
        if (!Validation.hasSelected(getSpinner(R.id.Clients_Union))) valid = false;
        if (!Validation.hasSelected(getSpinner(R.id.Clients_Village))) valid = false;


        if (!Validation.isValidMobileNo(jsonEditTextMap.get("cellno"))) {
            specialInValid = true;
        }
        if (!Validation.isValidMobileNo(jsonEditTextMap.get("cellNo2"))) {
            specialInValid = true;
        }

        if (!Validation.isValidNID(jsonEditTextMap.get("nid"), this)) {
            specialInValid = true;
        }

        if (!Validation.isValidBRID(jsonEditTextMap.get("brId"), this)) {
            specialInValid = true;
        }

        if (!valid) {
            Toast toast = Toast.makeText(this, R.string.GeneralSaveWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        } else if (specialInValid) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void initiateTextViews() {

        getTextView(R.id.TextViewClient_name).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.TextViewClient_name).getText().toString(), 0, 1));
        getTextView(R.id.TextViewClientsFather).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.TextViewClientsFather).getText().toString(), 0, 1));
        getTextView(R.id.TextViewClientsMother).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.TextViewClientsMother).getText().toString(), 0, 1));
        getTextView(R.id.TextViewClientsSex).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.TextViewClientsSex).getText().toString(), 0, 1));
        getTextView(R.id.TextViewClientsDist).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.TextViewClientsDist).getText().toString(), 0, 1));
        getTextView(R.id.ClientsUpazila).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.ClientsUpazila).getText().toString(), 0, 1));
        getTextView(R.id.TextViewClientsUnion).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.TextViewClientsUnion).getText().toString(), 0, 1));
        getTextView(R.id.TextViewClientsVillage).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.TextViewClientsVillage).getText().toString(), 0, 1));
        getTextView(R.id.TextViewClientsAge).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.TextViewClientsAge).getText().toString(), 0, 1));
        getTextView(R.id.TextViewClientsDate).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.TextViewClientsDate).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("gender", getSpinner(R.id.ClientsSexSpinner));
        jsonSpinnerMap.put("maritalStatus", getSpinner(R.id.ClientsMaritalStatusSpinner));
        //new for Partial PRS
        jsonSpinnerMapForValues.put("religion", getSpinner(R.id.ClientsReligionSpinner));
        jsonSpinnerMapForValues.put("education", getSpinner(R.id.ClientsEducationSpinner));
        jsonSpinnerMapForValues.put("occupation", getSpinner(R.id.ClientsOccupationSpinner));
        jsonSpinnerMapForValues.put("notHavingNIDReason", getSpinner(R.id.ClientsNotHavingNIDSpinner));
        jsonSpinnerMapForValues.put("notHavingBrIDReason", getSpinner(R.id.ClientsNotHavingBRIdSpinner));

    }

    @Override
    protected void initiateMultiSelectionSpinners() {
    }

    @Override
    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("dob", getEditText(R.id.EditTextClientsDate));
        getEditText(R.id.EditTextClientsDate).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.EditTextClientsDate)));
    }

    @Override
    protected void initiateRadioGroups() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkboxNoNID:
                Utilities.SetVisibility(NRCActivity.this, R.id.layoutNRCNotHavingNID, isChecked ? View.VISIBLE : View.GONE);
                getEditText(R.id.edNrcNRCNID).setEnabled(isChecked ? false : true);
                if(isChecked){
                    getEditText(R.id.edNrcNRCNID).setText("");
                }
                break;
            case R.id.checkboxNoBrID:
                Utilities.SetVisibility(NRCActivity.this, R.id.layoutNRCNotHavingBRId, isChecked ? View.VISIBLE : View.GONE);
                if(isChecked){
                    getEditText(R.id.edNrcBRID).setText("");
                }
                getEditText(R.id.edNrcBRID).setEnabled(isChecked ? false : true);
                break;

            case R.id.CBAgeNotKnown:
                Utilities.SetVisibility(NRCActivity.this, R.id.layoutNRCAgeNext, isChecked ? View.VISIBLE : View.GONE);
                break;
            default:
                //do nothing
                break;
        }

    }

    @Override
    public void onClick(View view) {
        boolean alreadyHidden = findViewById(R.id.layoutMobileAlternate).getVisibility() == View.GONE ? true : false;
        switch (view.getId()) {
            case R.id.imageViewAddNewMobile:
                if (alreadyHidden) {
                    Utilities.SetVisibility(NRCActivity.this, R.id.layoutMobileAlternate, View.VISIBLE);
                    getImageView(R.id.imageViewAddNewMobile).setImageResource(android.R.drawable.ic_delete);
                } else {
                    Utilities.SetVisibility(NRCActivity.this, R.id.layoutMobileAlternate, View.GONE);
                    getEditText(R.id.editTextNRCClientMobileAlternate).setText("");
                    getImageView(R.id.imageViewAddNewMobile).setImageResource(android.R.drawable.ic_input_add);
                }
                break;

            case R.id.nrcOtherMemberView:
                if (!getTextView(R.id.Clients_Member).getText().toString().equals("")) {
                    AlertDialogCreator.SimpleMessageDialog(NRCActivity.this, getMembersInfo(), "সদস্যের তথ্য", 1);
                } else {
                    Utilities.showAlertToast(NRCActivity.this, "প্রথমে সদস্য যোগ করুন, তারপর দেখুন।");
                }

                break;

            case R.id.nrcAddOtherMember:
                if (!getEditText(R.id.Clients_MemberCount).getText().toString().equals("")) {
                    int totalMember = Integer.parseInt(getEditText(R.id.Clients_MemberCount).getText().toString());
                    if (totalMember == getOtherMemberCount()) {
                        Utilities.showAlertToast(NRCActivity.this, "খানার অন্যান্য সদস্যের সংখ্যা মোট সদস্য হতে বেশি হতে পারবে না।");
                    } else {
                        AlertDialogCreator.showSpecialInputDialog(NRCActivity.this, "সদস্যের তথ্য দিন", false);

                    }
                } else {
                    AlertDialogCreator.SimpleMessageWithNoTitle(this, "সদস্যের তথ্য প্রদানের পূর্বে খানার সদস্য সংখ্যা প্রদান করুন।");
                }

                break;

            case R.id.nrcDeleteOtherMember:
                if (getOtherMemberCount() != 0) {
                    viewIdAlertDialogShown = view.getId();
                    AlertDialogCreator.SimpleDecisionDialog(this,
                            getString(R.string.delete_confirmation),
                            getString(R.string.title_confirmation), android.R.drawable.ic_delete);
                }

                break;
        }

    }

    private String getMembersInfo() {
        String strText = getTextView(R.id.Clients_Member).getText().toString();
        String[] arrMembers = strText.split(";");
        String strMembers = "";
        String strMembersInfo = "";
        int intMemberCount = 0;
        for (int i = 0; i < arrMembers.length; i++) {
            strMembers = arrMembers[i].replace("(", "");
            strMembers = strMembers.replace(")", "");
            String[] arrMembersInfo = strMembers.split(",");

            intMemberCount = i + 1;
            strMembersInfo += "সদস্য #" + intMemberCount + "\n";
            strMembersInfo += "নাম: " + arrMembersInfo[0] + "\n";
            strMembersInfo += "বয়স: " + arrMembersInfo[1] + "\n";
            int intSex = Integer.parseInt(arrMembersInfo[2]);
            String sex = intSex == 1 ? "পুরুষ" : intSex == 2 ? "মহিলা" : intSex == 3 ? "হিজড়া" : "";
            strMembersInfo += "লিঙ্গ: " + sex + "\n";
            strMembersInfo += "\n";

        }
        return strMembersInfo;
    }

    private int getOtherMemberCount() {
        String strText = getTextView(R.id.Clients_Member).getText().toString();
        if (!strText.equals("")) {
            String[] arrMembers = strText.split(";");
            return arrMembers.length;
        } else {
            return 0;
        }
    }

    /**
     * deletes the last member added in the EditText field
     * <p></p>
     *
     * @param
     * @return void
     */
    private void deleteMemberFromEditText() {
        EditText memberEditText = getEditText(R.id.Clients_Member);
        String prevText = memberEditText.getEditableText().toString();
        if (!prevText.equals("")) {
            String strRest = "";

            String[] arrText = prevText.split(";");
            for (int i = 0; i < arrText.length - 1; i++) {
                strRest = (!strRest.equals("") ? strRest + ";" : "") + arrText[i];
            }
            memberEditText.setText(strRest);
        } else {
            Utilities.showAlertToast(this, "মুছে ফেলার মত কিছু নেই! ");
        }

    }

    @Override
    public void onNeutralButtonClicked(DialogInterface dialog, String input) {
        EditText memberEditText = getEditText(R.id.Clients_Member);
        String prevText = memberEditText.getEditableText().toString();
        if (!input.equals("")) {
            memberEditText.setText((!prevText.equals("") ? prevText + ";" : "") + input);
        }
        MethodUtils.hideSoftKeyboard(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.Clients_District:

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
                getSpinner(R.id.Clients_Upazila).setAdapter(upazilaAdapter);
                //TODO:has to perform proper regression testing as the edit mode is checking is removed
                /*if (editMode) {*/
                    //during retrieval....
                    if (prevUpazila != null) {
                        LocationHolder prevUpazillaObjectVal = null;
                        for (LocationHolder singleLoc : upazillaList) {
                            if (singleLoc.getCode().equals(prevUpazila)) {
                                prevUpazillaObjectVal = singleLoc;
                            }
                        }
                        int pos = 0;
                        if (prevUpazillaObjectVal != null) {
                            pos = upazilaAdapter.getPosition(prevUpazillaObjectVal);
                        }
                        getSpinner(R.id.Clients_Upazila).setSelection(pos);
                    }
                /*}*/

                break;
            case R.id.Clients_Upazila:

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
                getSpinner(R.id.Clients_Union).setAdapter(unionAdapter);

                //TODO:has to perform proper regression testing as the edit mode is checking is removed
                /*if (editMode) {*/
                //during retrieval....
                    if (prevUnion != null) {
                        LocationHolder prevUnionObjectVal = null;
                        for (LocationHolder singleLoc : unionList) {
                            if (singleLoc.getCode().equals(prevUnion)) {
                                prevUnionObjectVal = singleLoc;
                            }
                        }
                        int pos = 0;
                        if (prevUnionObjectVal != null) {
                            pos = unionAdapter.getPosition(prevUnionObjectVal);
                        }
                        getSpinner(R.id.Clients_Union).setSelection(pos);
                    }
                /*}*/
                break;
            case R.id.Clients_Union:
                villageList.clear();
                villageAdapter.clear();
                villageList.add(blanc);

                loadVillageFromJson(
                        ((LocationHolder) getSpinner(R.id.Clients_District).getSelectedItem()).getCode().split("_")[0],
                        ((LocationHolder) getSpinner(R.id.Clients_Upazila).getSelectedItem()).getCode(),
                        ((LocationHolder) getSpinner(R.id.Clients_Union).getSelectedItem()).getCode(),
                        villageList);
                for (LocationHolder holder : villageList) {
                    Log.d(LOGTAG, "Village: -> " + holder.getBanglaName());
                }


                villageAdapter.addAll(villageList);
                getSpinner(R.id.Clients_Village).setAdapter(villageAdapter);

                if (editMode) {
                    //during retrieval....
                    if (prevVillage != null) {
                        LocationHolder prevVillObjectVal = null;
                        for (LocationHolder singleLoc : villageList) {
                            if (singleLoc.getCode().equals(prevVillage)) {
                                prevVillObjectVal = singleLoc;
                            }
                        }
                        int pos = 0;
                        if (prevVillObjectVal != null) {
                            pos = villageAdapter.getPosition(prevVillObjectVal);
                        }
                        getSpinner(R.id.Clients_Village).setSelection(pos);
                    }
                }

                break;
            case R.id.Clients_Village:
                Log.d(LOGTAG, "Village Case: -> ");
                break;

            case R.id.ClientsSexSpinner:
                if (position != 1) Utilities.Reset(NRCActivity.this, R.id.layoutNRCHusband);
                boolean isMarried = getSpinner(R.id.ClientsMaritalStatusSpinner).getSelectedItemPosition() == 2 ? true : false;
                findViewById(R.id.layoutNRCHusband).setVisibility(isMarried && position == 1 ? View.VISIBLE : View.GONE);
                findViewById(R.id.layoutNRCMaritalStatus).setVisibility(position==2?View.GONE:View.VISIBLE);


                break;
            case R.id.ClientsMaritalStatusSpinner:
                if (position != 2) Utilities.Reset(NRCActivity.this, R.id.layoutNRCHusband);
                boolean isWoman = getSpinner(R.id.ClientsSexSpinner).getSelectedItemPosition() == 1 ? true : false;
                findViewById(R.id.layoutNRCHusband).setVisibility(isWoman && position == 2 ? View.VISIBLE : View.GONE);

                break;

            case R.id.ClientsNotHavingNIDSpinner:
            case R.id.ClientsNotHavingBRIdSpinner:
                if (position != 5) {
                    if (getSpinner(R.id.ClientsNotHavingNIDSpinner).getSelectedItemPosition() != 5 &&
                            getSpinner(R.id.ClientsNotHavingBRIdSpinner).getSelectedItemPosition() != 5)
                        getCheckbox(R.id.checkBoxNationality).setEnabled(true);
                    getCheckbox(R.id.checkBoxNationality).setChecked(true);
                } else {
                    getCheckbox(R.id.checkBoxNationality).setChecked(false);
                    getCheckbox(R.id.checkBoxNationality).setEnabled(false);
                }


                break;

            default:
                Log.e(LOGTAG, "Unknown spinner: " + parent.getId()
                        + " -> " + getResources().getResourceEntryName(parent.getId()));

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

            Log.d(LOGTAG, "Union deatails:\n\t" + union.toString());

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


    private void loadJsonFile(String fileName, StringBuilder jsonBuilder) throws IOException {
        InputStream is = getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        jsonBuilder.append(new String(buffer, "UTF-8"));

    }

    private void addAndSetSpinners() {
        getSpinner(R.id.Clients_District).setOnItemSelectedListener(this);
        getSpinner(R.id.Clients_Upazila).setOnItemSelectedListener(this);
        getSpinner(R.id.Clients_Union).setOnItemSelectedListener(this);
        getSpinner(R.id.Clients_Village).setOnItemSelectedListener(this);

        getSpinner(R.id.ClientsSexSpinner).setOnItemSelectedListener(this);
        getSpinner(R.id.ClientsMaritalStatusSpinner).setOnItemSelectedListener(this);
        getSpinner(R.id.ClientsNotHavingBRIdSpinner).setOnItemSelectedListener(this);
        getSpinner(R.id.ClientsNotHavingNIDSpinner).setOnItemSelectedListener(this);


        zillaAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        upazilaAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        unionAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        villageAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);

        unionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    public void onClickSaveNRC(View view) {
        countSaveClick++;
        if (countSaveClick == 2) {
            getButton(R.id.nrcProceed).setText(getText(R.string.string_save));
            countSaveClick = 0;
            nrcSaveToJson();
        } else if (countSaveClick == 1) {
            if (!hasTheRequiredFileds()) {
                countSaveClick = 0;
                return;
            }
            Utilities.Disable(this, R.id.nrc_layout);
            Utilities.Enable(this, R.id.nrcProceed);
            Utilities.Enable(this, R.id.nrcCancel);


            getButton(R.id.nrcProceed).setText(getText(R.string.string_confirm));
            Utilities.MakeVisible(this, R.id.nrcCancel);

            Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
        }
    }

    public void onClickCancelNRC(View view) {
        if (countSaveClick == 1) {
            countSaveClick = 0;
            Utilities.Enable(this, R.id.nrc_layout);
            Utilities.MakeInvisible(this, R.id.Clients_House_No);

            getButton(R.id.nrcProceed).setText(editMode ? getText(R.string.string_update) : getText(R.string.string_save));
            Utilities.MakeInvisible(this, R.id.nrcCancel);
        }
    }

    @Override
    public void onPositiveButtonClicked(DialogInterface dialog) {
        //check if the alert dialog from delete event
        if (viewIdAlertDialogShown == R.id.nrcDeleteOtherMember) {
            deleteMemberFromEditText();
        }
        //check if the alert dialog from mobile, nid, brid number
        else if ((viewIdAlertDialogShown == R.id.NrcClients_Mobile_no ||
                viewIdAlertDialogShown == R.id.editTextNRCClientMobileAlternate ||
                viewIdAlertDialogShown == R.id.edNrcNRCNID || viewIdAlertDialogShown == R.id.edNrcBRID) &&
                !selectedExistingClientHealthID.isEmpty()) {
            int existingDataCheckIndex = 0;
            if (viewIdAlertDialogShown == R.id.edNrcNRCNID) {
                existingDataCheckIndex = NATIONAL_ID_INDEX;
            } else if (viewIdAlertDialogShown == R.id.edNrcBRID) {
                existingDataCheckIndex = BIRTH_REG_NO_INDEX;
            }

            //navigate to second activity
            navigateToClientInfo(viewIdAlertDialogShown, selectedExistingClientHealthID, existingDataCheckIndex);
        }

        //resetting default parameters
        viewIdAlertDialogShown = -1;
        selectedExistingClientHealthID = "";

        dialog.cancel();
    }

    @Override
    public void onNegativeButtonClicked(DialogInterface dialog) {
        //resetting default parameter
        viewIdAlertDialogShown = -1;
        selectedExistingClientHealthID = "";

        dialog.cancel();
    }

    @Override
    public void onNeutralButtonClicked(DialogInterface dialog) {
    }

    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialog(NRCActivity.this,
                "এই রেজিষ্ট্রেশন ফর্ম");

    }

    //check if mobile number, nid, brid already exists
    public void existingDataChecker(int viewId) {
        //setting selected id to class variable to use on alert dialog button click event
        viewIdAlertDialogShown = viewId;

        //configure progress dialog
        progressDialog = MethodUtils.ConfigureProgressDialog(this, ProgressDialog.STYLE_SPINNER, getProgressMessage(viewId));
        progressDialog.show();

        existingDataCheck(viewId);
    }

    private String getProgressMessage(int viewId) {
        if (viewId == R.id.NrcClients_Mobile_no || viewId == R.id.editTextNRCClientMobileAlternate) {
            return getString(R.string.mobile_number_checking_in_progress);
        } else if (viewId == R.id.edNrcNRCNID) {
            return getString(R.string.nid_number_checking_in_progress);
        } else if (viewId == R.id.edNrcBRID) {
            return getString(R.string.brid_number_checking_in_progress);
        }
        return "";
    }

    private void existingDataCheck(int viewId) {
        if (viewId == R.id.NrcClients_Mobile_no || viewId == R.id.editTextNRCClientMobileAlternate) {
            existingDataCheckMobile(viewId);
        } else if (viewId == R.id.edNrcNRCNID || viewId == R.id.edNrcBRID) {
            existingDataCheckNID_BRID(viewId);
        }
    }

    //mobile number checking request
    private void existingDataCheckMobile(int viewId) {
        AsyncADVSearchUpdate asyncADVSearchUpdate = new AsyncADVSearchUpdate(result -> {
            progressDialog.dismiss();
            try {
                JSONObject json = new JSONObject(result);
                if (json.has("count") && json.getInt("count") > 0) {
                    personsList.clear();
                    for (int i = 1; i <= json.getInt("count"); i++) {
                        JSONObject jsonPerson = json.getJSONObject(String.valueOf(i));
                        personsList.add(new Person(jsonPerson.getString("name") +
                                ((jsonPerson.getString("age").equals(""))?"":(" (Age: "+jsonPerson.getString("age")+")")),
                                jsonPerson.getString("fatherName"),
                                jsonPerson.getString("husbandName"),
                                jsonPerson.getString("healthId"),
                                jsonPerson.getInt("healthIdPop") == 1 ? 0 : 4,
                                Integer.parseInt("1") !=2? 0:
                                        jsonSpinnerMap.get("gender").getSelectedItemPosition() != 1 ? R.drawable.man : R.drawable.woman,
                                jsonPerson.has("serviceDate") ? jsonPerson.getString("serviceDate"):""));
                    }

                    AlertDialogCreator.ListViewDialog(NRCActivity.this,
                            getString(R.string.existing_mobile_number_warning),
                            getString(R.string.mobile_number_search), android.R.drawable.ic_dialog_info, personsList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, this);

        try {
            HashMap<String, EditText> editTextHashMap = new HashMap<>();
            JSONObject json = new JSONObject();
            json.put(Constants.KEY_OPTION, "1");
            editTextHashMap.put("mobileNo", getEditText(viewId));

            Utilities.getEditTexts(editTextHashMap, json);

            String SEARCH_SERVLET = "advancesearch";
            String SEARCH_ROOTKEY = "advanceSearch";
            asyncADVSearchUpdate.execute(json.toString(), SEARCH_SERVLET, SEARCH_ROOTKEY, "dismiss");
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Exception: " + jse.getMessage());
        }
    }

    //nid/birth registration request
    private void existingDataCheckNID_BRID(int viewId) {
        String queryString = "{" +
                "sOpt:" + ((viewId == R.id.edNrcNRCNID ? NATIONAL_ID_INDEX : BIRTH_REG_NO_INDEX)+1) + "," +
                "sStr:\"" + getEditText(viewId).getText().toString() + "\"," +
                "providerid:" + ProviderInfo.getProvider().getProviderCode() +
                "}";
        AsyncClientInfoUpdate retrieveClient = new AsyncClientInfoUpdate(result -> {
            progressDialog.dismiss();
            try {
                JSONObject json = new JSONObject(result);
                if (json.has("cHealthID") && !json.getString("cVisibleID").isEmpty()) {
                    String message = "প্রদত্ত " + (viewId == R.id.edNrcNRCNID ? "এনআইডি" : "জন্ম নিবন্ধন") +
                            " নম্বর দিয়ে " + json.getString("cName") +
                            " সেবাগ্রহীতাকে পাওয়া গিয়েছে। এই গ্রহীতাকে সেবা দিতে OK চাপুন";

                    selectedExistingClientHealthID = json.getString("cVisibleID");

                    AlertDialogCreator.SpecialDecisionDialog(this, message,
                            getString(viewId == R.id.edNrcNRCNID ? R.string.nid_number_search : R.string.brid_number_search),
                            android.R.drawable.ic_dialog_info,
                            new String[]{"OK"},
                            false, AlertDialog.THEME_HOLO_DARK, Color.WHITE, Color.WHITE
                    );
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "JSON Exception: " + jse.getMessage());
            }
        }, this);

        String SERVLET = "client";
        String ROOTKEY = "sClient";
        retrieveClient.execute(queryString, SERVLET, ROOTKEY, "dismiss");
    }

    public void navigateToClientInfo(int viewId, String healthID, int existingDataCheckIndex) {
        Intent intent = new Intent();
        intent.putExtra("generatedId", healthID);

        if (existingDataCheckIndex != 0)  {
            intent.putExtra("existingDataCheckIndex", existingDataCheckIndex);
            intent.putExtra("existingClientSearchedValue", getEditText(viewId).getText().toString());
        }

        setResult(RESULT_OK, intent);
        finishActivity(ActivityResultCodes.REGISTRATION_ACTIVITY);

        finish();
    }

    @Override
    public void onListItemClicked(DialogInterface dialog, int position) {
        selectedExistingClientHealthID = personsList.get(position).getHealthId();
    }
}