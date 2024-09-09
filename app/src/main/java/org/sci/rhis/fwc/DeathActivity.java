
package org.sci.rhis.fwc;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.AsyncDeathInfoUpdate;
import org.sci.rhis.model.GeneralPerson;
import org.sci.rhis.model.PregWoman;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.util.ArrayList;
import java.util.HashMap;

public class DeathActivity extends ClinicalServiceActivity {

    final private String SERVLET = "death";
    final private String ROOTKEY = "deathInfo";
    private final String LOGTAG = "FWC-DEATH-REPORTING";
    boolean updateMode = false;
    private boolean isPregWoman = false;
    private final static int MOTHER_DEATH_REASON_OFFSET = 14;
    private final static int NEONATAL_DEATH_REASON_OFFSET = 27;

    private PregWoman patient;
    private GeneralPerson generalPatient;
    private ProviderInfo provider;

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;
    private int childCount = 0;
    ArrayAdapter<String> maternalDeathReason;
    ArrayAdapter<String> normalDeathReason;
    ArrayAdapter<String> neonatalDeathReason;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_death);

        initialize();
        init();
    }

    private void init(){
        maternalDeathReason = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.MaternalDeathReason_DropDown));
        normalDeathReason = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.DeathReason_DropDown));
        neonatalDeathReason = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.Neonatal_DeathReason_DropDown));

        int checkbox_ids [] = {R.id.deathCBMother, R.id.deathCBNewborn};
        int radiobuttons [] = {R.id.deathRBSave, R.id.deathRBModify};

        setViewListeners(checkbox_ids, null); //when lamdas will be supported we can replace that
        setViewListeners(radiobuttons, new Runnable() {
            @Override
            public void run() {

            }
        });

        //custom date picker
        datePickerDialog = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.deathIMBDatePicker, getEditText(R.id.deathETDate));

        Intent intent = getIntent();
        provider = intent.getParcelableExtra("Provider");
        if(intent.getBooleanExtra("isWoman",false)){
            isPregWoman = true;
            patient = intent.getParcelableExtra("Patient");
            generalPatient = patient;
            updateChildCount();
        }else {
            isPregWoman = false;
            generalPatient = intent.getParcelableExtra("Patient");
            Utilities.SetVisibility(this,R.id.deathLLDeathType,View.GONE);
        }

        if(isReportedDead()) { //get death information
            AsyncDeathInfoUpdate deathInfoUpdate = new AsyncDeathInfoUpdate(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    try {
                        restoreDeathInfoFromJSON(new JSONObject(result));
                    } catch (JSONException jse) {
                        Log.e(LOGTAG, jse.getMessage());
                        Utilities.printTrace(jse.getStackTrace(),15);
                    }
                }
            }, this);

            try {
                JSONObject queryString = buildQueryHeader(true, false);
                deathInfoUpdate.execute(queryString.toString(), SERVLET, ROOTKEY);
            } catch (JSONException jse) {
                Log.e(LOGTAG, jse.getMessage());
                Utilities.printTrace(jse.getStackTrace());
            }

        }
    }

    private void restoreDeathInfoFromJSON(JSONObject response) {
        Log.d(LOGTAG, "Restoring Death Information from network response: " + response);
        JSONObject json = null;
        try {
            if(response.getInt("clientDeathStatus") == 1) {
                json = response.getJSONObject(response.getString("count"));
                if(json.has("causeOfDeath") && json.getString("deathOfPregWomen").equals("1")){
                    int causeOfDeath = Integer.valueOf(json.getString("causeOfDeath"))-MOTHER_DEATH_REASON_OFFSET;
                    json.put("causeOfDeath",String.valueOf(causeOfDeath));
                }
            }
            Utilities.setCheckboxes(jsonCheckboxMap, json);
            Utilities.setSpinners(jsonSpinnerMap, json);

            Utilities.setEditTextDates(jsonEditTextDateMap, json);
            Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap, json);
            Utilities.setTextViews(jsonTextViewsMap, json);
        } catch (IllegalArgumentException  iae) {
            Log.e(LOGTAG, iae.getMessage());
        } catch (JSONException jse) {
            Log.e(LOGTAG, jse.getMessage());
        }

        Utilities.Disable(this, R.id.deathLLDeathType);
        Utilities.Disable(this, R.id.deathLLDate);
        Utilities.Disable(this, R.id.deathLLButton);
    }

    private void setViewListeners(int ids[], final Runnable run ) { //could have been more optimized if lamdas were accepted
        for(int cb : ids) {
            //getCheckbox(cb)
            ((CompoundButton)findViewById(cb))
                    .
                    setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            toggleCheckBox(buttonView, isChecked);
                            //run.run();
                        }
                    });
        }
    }

    private void toggleCheckBox(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.deathCBMother:
                if(isChecked) {
                    getCheckbox(R.id.deathCBNewborn).setChecked(!isChecked);
                    getSpinner(R.id.deathSpChildNo).setVisibility(View.INVISIBLE);
                    getSpinner(R.id.deathSPReason).setAdapter(null);
                    getSpinner(R.id.deathSPReason).setAdapter(maternalDeathReason);
                }else{
                    getSpinner(R.id.deathSPReason).setAdapter(null);
                    getSpinner(R.id.deathSPReason).setAdapter(normalDeathReason);
                }
                break;
            case R.id.deathCBNewborn:
                if(isChecked) {
                    getCheckbox(R.id.deathCBMother).setChecked( !isChecked);
                    getSpinner(R.id.deathSpChildNo).setVisibility(View.VISIBLE);
                    getSpinner(R.id.deathSPReason).setAdapter(null);
                    getSpinner(R.id.deathSPReason).setAdapter(neonatalDeathReason);
                }else {
                    getSpinner(R.id.deathSPReason).setAdapter(null);
                    getSpinner(R.id.deathSPReason).setAdapter(normalDeathReason);
                }
                break;
            case R.id.deathRBSave:
                break;
            case R.id.deathRBModify:
                if(!isReportedDead()) {
                    Utilities.showBiggerToast(this, R.string.notRreportedDeath);
                    getRadioButton(R.id.deathRBModify).setChecked(false);
                    getRadioButton(R.id.deathRBSave).setChecked(true);
                } else {
                    if(isPregWoman){
                        Utilities.Enable(this, R.id.deathLLDeathType);
                    }else {
                        Utilities.SetVisibility(this,R.id.deathLLDeathType,View.GONE);
                    }

                    Utilities.Enable(this, R.id.deathLLDate);
                    Utilities.Enable(this, R.id.deathLLButton);
                    if(childCount==0){
                        Utilities.Disable(DeathActivity.this,R.id.deathCBNewborn);
                    }
                }
                break;
            default:
                getSpinner(R.id.deathSPReason).setAdapter(null);
                getSpinner(R.id.deathSPReason).setAdapter(normalDeathReason);
                Log.e(LOGTAG, "Checkbox is not identified");
        }
    }

    @Override
    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("deathOfPregWomen", getCheckbox(R.id.deathCBMother));
    }

    @Override
    protected void initiateEditTexts() {}

    @Override
    protected void initiateTextViews() {}

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("causeOfDeath", getSpinner(R.id.deathSPReason));
        jsonSpinnerMap.put("placeOfDeath", getSpinner(R.id.deathSPlace));
        jsonSpinnerMap.put("childNo", getSpinner(R.id.deathSpChildNo));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {}

    @Override
    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("deathDT",getEditText(R.id.deathETDate));
    }

    @Override
    protected void initiateCheckboxesGroup() {}

    @Override
    protected void initiateRadioGroups() {}

    @Override
    public void callbackAsyncTask(String result) {
        Log.d(LOGTAG, "Death Reported:\n\t" + result);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("reportedDead", true);
        try {
            JSONObject deathResponse = new JSONObject(result);
            int childNo = Integer.valueOf(deathResponse.getString("childNo").equals("") ? "0" : deathResponse.getString("childNo"));
            if(childNo>=1){
                returnIntent.putExtra("reportedDead", false);
            }else{
                Utilities.showBiggerToast(this, R.string.reportedDeath);
            }
        } catch (JSONException jse) {
            Log.e("DEATH JSON Exception: ", jse.getMessage());
        }
        setResult(RESULT_OK, returnIntent);
        finishActivity(ActivityResultCodes.DEATH_ACTIVITY);
        finish();
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private JSONObject buildQueryHeader(boolean isRetrieval, boolean isChild) throws JSONException {
        //get info from database
        String loadKey = (isChild ? "retrieveChild": "retrieve");
        String queryString = "{" +
                "healthId:" + (isPregWoman ? patient.getHealthId():generalPatient.getHealthId()) + "," +
                (isRetrieval ? "" : "providerId:\"" + String.valueOf(provider.getProviderCode()) + "\",") +
                "pregNo:" + (isPregWoman ? patient.getPregNo():0) + "," +
                "deathLoad:" + (isRetrieval ? loadKey : (updateMode?"update":"\"\"")) +
                "}";

        return new JSONObject(queryString);
    }

    private void saveToJson() {
        AsyncDeathInfoUpdate deathInfoUpdateTask = new AsyncDeathInfoUpdate(this, this);
        JSONObject json;
        try {
            json = buildQueryHeader(false, false);
            Utilities.getCheckboxesBlank(jsonCheckboxMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            //............
            String sateliteCenter=provider.getSatelliteName();
            json.put("sateliteCenterName", sateliteCenter == null ? "" : sateliteCenter);
            getSpecialCases(json);
            //..........
            deathInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);

            Log.d(LOGTAG, "Death Save Json in Servlet:" + ROOTKEY + ":{" + json.toString() + "}");

            Utilities.Reset(this, R.id.pacText);

        } catch (JSONException jse) {
            Log.e(LOGTAG, jse.getMessage());
        }

    }

    public void getSpecialCases(JSONObject json) {
        try {
            if(json.has("childNo") && json.getInt("childNo") == -1) {
                json.put("childNo", "");
            }
            if(json.getString("deathOfPregWomen").equals("1")){
                String reasonVal = String.valueOf(getSpinner(R.id.deathSPReason).getSelectedItemPosition()+MOTHER_DEATH_REASON_OFFSET);
                json.put("causeOfDeath",reasonVal);
            }else if(json.has("childNo") && json.getInt("childNo") >=1){
                String reasonVal = String.valueOf(getSpinner(R.id.deathSPReason).getSelectedItemPosition()+NEONATAL_DEATH_REASON_OFFSET);
                json.put("causeOfDeath",reasonVal);
            }

        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    public void reportDeath(View view) {
        if(isValidForSaving()) {
            if(getRadioButton(R.id.deathRBModify).isChecked()){
                updateMode=true;
            }else {
                updateMode=false;
            }
            saveToJson();
        }
    }

    private boolean isValidForSaving(){
        boolean valid =true;
        if(!Validation.hasText(getEditText(R.id.deathETDate))) valid=false;
        if(!Validation.hasSelected(getSpinner(R.id.deathSPlace))) valid=false;
        if(!Validation.hasSelected(getSpinner(R.id.deathSPReason))) valid=false;

        return valid;

    }

    public void cancelActivity(View view) {
        finish();
    }

    void populateChildSpinner() {
        Spinner child = getSpinner(R.id.deathSpChildNo);
        ArrayList<String> cCount = new ArrayList<String>();
        for(int i = 0; i <= childCount; i++) {
            cCount.add(i != 0 ? String.valueOf(i):"");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.addAll(cCount);

        child.setAdapter(adapter);
    }

    private void updateChildCount() {
        JSONObject json;
        try {
            json = buildQueryHeader(true, true);
            AsyncDeathInfoUpdate networkTask = new AsyncDeathInfoUpdate(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    try {
                        JSONObject childCountResponse = new JSONObject(result);
                        childCount = childCountResponse.getInt("count");
                        if(childCount==0){
                            Utilities.Disable(DeathActivity.this,R.id.deathCBNewborn);
                        }else{
                            populateChildSpinner();
                        }

                    } catch (JSONException jse) {
                        Log.e("DEATH JSON Exception: ", jse.getMessage());
                    }
                }
            }, this);
            networkTask.execute(json.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {
            Log.e("DEATH JSON Exception: ", jse.getMessage());
        }
    }

    boolean isReportedDead() {
        GeneralPerson deadPerson = (isPregWoman ? patient : generalPatient);
        return deadPerson.isDead();
    }
}
