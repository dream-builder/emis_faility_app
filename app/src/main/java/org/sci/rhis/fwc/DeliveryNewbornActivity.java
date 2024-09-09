package org.sci.rhis.fwc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncNewbornInfoUpdate;
import org.sci.rhis.model.PregWoman;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeliveryNewbornActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{


    private PregWoman mother;
    private JSONObject deliveryJsonObj;
    private JSONObject newbornDeleteObj;
    private ProviderInfo provider;
    private int flag =0;
    private int integerRecd = 0;
    private int countSaveClick=0;
    private int childNo=0;
    private boolean editMode = false,isSavedOrUpdated=false;

    final private String SERVLET = "newborn";
    final private String ROOTKEY= "newbornInfo";
    private  final String LOGTAG    = "FWC-NEWBORN";

    AsyncNewbornInfoUpdate newbornInfoQueryTask;
    AsyncNewbornInfoUpdate newbornInfoUpdateTask;
    Intent returnIntent;

    private MultiSelectionSpinner multiSelectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_newborn);

        initialize(); //initialize the inherited maps
        /**get the intent*/
        Intent intent = getIntent();

        integerRecd = intent.getIntExtra("Layout", flag);

        switch(integerRecd) {   /*Design different Layouts for different types*/
            case 0: Utilities.SetVisibility(this,R.id.deliveryNewBorn,View.GONE);
                jsonEditTextMap.get("birthStatus").setText("0");
                break;
            case 1:
                Utilities.MakeInvisible(this, R.id.deliveryNewBornNotDetected);
                jsonEditTextMap.get("birthStatus").setText("1");
                break;
            case 2:
                Utilities.MakeInvisible(this, R.id.deliveryNewBornNotDetected);
                Utilities.MakeInvisible(this, R.id.layout_only_for_neborn);
                jsonEditTextMap.get("birthStatus").setText("2");
                break;

            case 3:
                Utilities.MakeInvisible(this, R.id.deliveryWipe);
                Utilities.MakeInvisible(this, R.id.deliveryResastation);
                Utilities.MakeInvisible(this, R.id.StimulationBagNMask);
                Utilities.MakeInvisible(this, R.id.layout_only_for_neborn);
                //Utilities.MakeVisible(this, R.id.deliveryNewBornNotDetected);

                jsonEditTextMap.get("birthStatus").setText("3");
                break;
        }

        newbornDeleteObj=new JSONObject();

        String str = intent.getStringExtra("DeliveryJson");
        Log.d(LOGTAG, "Delivery JSON:\t"+ str);


        final List<String> newbornReferReasonList = Arrays.asList(getResources().getStringArray(R.array.Delivery_Newborn_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.deliveryChildReferReasonSpinner);
        multiSelectionSpinner.setItems(newbornReferReasonList);
        multiSelectionSpinner.setSelection(new int[]{});

        //set listeners
        getCheckbox(R.id.deliveryChildReferCheckBox).setOnCheckedChangeListener(this);
        getRadioGroup(R.id.id_newBornResasscitationRadioGroup).setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        handleRadioButton(group, checkedId);
                    }
                });
        //create the mother
        mother = intent.getParcelableExtra("PregWoman");
        provider = intent.getParcelableExtra("Provider");

        try {
            deliveryJsonObj = new JSONObject(str);
            checkSetMaturity();
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }

        try {

            if(intent.hasExtra("NewbornJson")) { //display existing data passed from delivery
                Log.d(LOGTAG, "Restoring Child Info");
                JSONObject restoreJson=new JSONObject(getIntent().getStringExtra("NewbornJson"));
                newbornDeleteObj.put("gender", restoreJson.getString("gender"));
                newbornDeleteObj.put("childno",restoreJson.getString("childno"));
                newbornDeleteObj.put("birthStatus", String.valueOf(integerRecd));
                restoreNewbornFromJSON(restoreJson);

            } else { //new child data
                Utilities.Enable(this, R.id.DeliveryNewBornLayout);
                Utilities.MakeInvisible(this,R.id.editNewBornButton);
                if(intent.hasExtra("childno")){ //set child no in UI
                    //int childno = intent.getIntExtra("childno", 0);
                    childNo = intent.getIntExtra("childno", 0);

                    String bStatus = integerRecd==0?"":(integerRecd==1?"(জীবিত)":"(মৃত)");
                    jsonTextViewsMap.get("childno").setText(Utilities.ConvertNumberToBangla(String.valueOf(childNo))+bStatus);
                }
            }

        }catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(LOGTAG, "Resumed Activity");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(LOGTAG, "Stopped Activity");
        Utilities.Reset(this, R.id.DeliveryNewBornLayout);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(LOGTAG, "Paused Activity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delivery_newborn, menu);
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
        returnIntent = new Intent();
        returnIntent.putExtra("ReloadNewborn", true);
        returnIntent.putExtra("ChildDetails", result);
        returnIntent.putExtra("newBornSavingFlag", isSavedOrUpdated);
        setResult(RESULT_OK, returnIntent);
        finishActivity(ActivityResultCodes.NEWBORN_ACTIVITY);
        finish();
    }

    @Override
    protected void initiateCheckboxes() {
// For New born
        jsonCheckboxMap.put("stimulation", getCheckbox(R.id.stimulation));
        jsonCheckboxMap.put("bagNMask", getCheckbox(R.id.bag_n_mask));
        jsonCheckboxMap.put("refer", getCheckbox(R.id.deliveryChildReferCheckBox));
    }

    @Override
    protected void initiateEditTexts() {
        // for New born layout
        jsonEditTextMap.put("birthStatus", getEditText(R.id.deliveryNewBornConditionValue));
        jsonEditTextMap.put("weight", getEditText(R.id.deliveryNewBornWeightValue));
        EditText weightEt = getEditText(R.id.deliveryNewBornWeightValue);
        weightEt.addTextChangedListener(new CustomTextWatcher(this,weightEt));
    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewsMap.put("childno", getTextView(R.id.deliveryNewBornNo));
        jsonTextViewsMap.put("immature", getTextView(R.id.deliveryNewBornMaturity));
    }

    @Override
    protected void initiateSpinners() {
        // for New born Layout
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.deliveryChildReferCenterNameSpinner));
    }

    @Override
    protected void initiateMultiSelectionSpinners(){
        jsonMultiSpinnerMap.put("referReason", getMultiSelectionSpinner(R.id.deliveryChildReferReasonSpinner));
    }

    @Override
    protected void initiateEditTextDates() {

    }

    @Override
    protected void initiateRadioGroups() {
        //for NewBorn
        jsonRadioGroupButtonMap.put("gender", Pair.create(
                getRadioGroup(R.id.id_newBornSexRadioGroup), Pair.create(
                        getRadioButton(R.id.deliveryNewBornSon),
                        getRadioButton(R.id.deliveryNewBornDaughter)
                        //getRadioButton(R.id.deliveryNewBornNotDetected)
                )
                )
        );

        jsonRadioGroupButtonMap.put("dryingAfterBirth", Pair.create(
                getRadioGroup(R.id.id_newBornWipeRadioGroup), Pair.create(
                        getRadioButton(R.id.deliveryWipeYesButton),
                        getRadioButton(R.id.deliveryWipeNoButton))
                )
        );
        jsonRadioGroupButtonMap.put("resassitation", Pair.create(
                getRadioGroup(R.id.id_newBornResasscitationRadioGroup), Pair.create(
                        getRadioButton(R.id.deliveryResastationYesButton),
                        getRadioButton(R.id.deliveryResastationNoButton))
                )
        );
        jsonRadioGroupButtonMap.put("chlorehexidin", Pair.create(
                getRadioGroup(R.id.id_newBornChlorohexidineRadioGroup), Pair.create(
                        getRadioButton(R.id.deliveryPlacentaCuttingYesButton),
                        getRadioButton(R.id.deliveryPlacentaCuttingNoButton))
                )
        );
        jsonRadioGroupButtonMap.put("skinTouch", Pair.create(
                getRadioGroup(R.id.id_newBornChordCareRadioGroup), Pair.create(
                        getRadioButton(R.id.deliveryFittedWithMotherSkinYesButton),
                        getRadioButton(R.id.deliveryFittedWithMotherSkinNoButton))
                )
        );

        jsonRadioGroupButtonMap.put("breastFeed", Pair.create(
                getRadioGroup(R.id.id_newBornBreastFeedingRadioGroup), Pair.create(
                        getRadioButton(R.id.deliveryBreastFeedingYesButton),
                        getRadioButton(R.id.deliveryBreastFeedingNoButton))
                )
        );

        getRadioButton(R.id.deliveryNewBornSon).setText(Utilities.changePartialTextColor(Color.RED,
                getRadioButton(R.id.deliveryNewBornSon).getText().toString(), 0, 1));
        getRadioButton(R.id.deliveryNewBornDaughter).setText(Utilities.changePartialTextColor(Color.RED,
                getRadioButton(R.id.deliveryNewBornDaughter).getText().toString(), 0, 1));
    }
    private HashMap<RadioGroup, Pair<RadioButton, RadioButton>> getRadioMap(int groupId, int yesId, int noId) {

        HashMap<RadioGroup, Pair<RadioButton, RadioButton>> temp
                = new HashMap<RadioGroup, Pair<RadioButton, RadioButton>>();
        temp.put(
                getRadioGroup(groupId),
                Pair.create(
                        getRadioButton(yesId),
                        getRadioButton(noId)
                )
        );
        return temp;
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch(buttonView.getId()) {

            case R.id.deliveryChildReferCheckBox :
                int visibility = isChecked ? View.VISIBLE : View.INVISIBLE;
                int layouts[] = {R.id.deliveryChildReferCenterName, R.id.deliveryChildReferReason};

                for (int i = 0; i < layouts.length; i++) {
                    Utilities.SetVisibility(this, layouts[i], visibility);
                }
                break;

            case R.id.deliveryNewBornNotDetected :
                if(isChecked) {
                    int genderGroup  [] = {R.id.deliveryNewBornSon, R.id.deliveryNewBornDaughter};
                    for(int id : genderGroup) {
                        //getRadioGroup(R.id.id_newBornSexRadioGroup).s;
                        getRadioButton(id).setChecked(!isChecked);
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_saveNewbornButton:
                saveNewBorn();
                break;
            case R.id.id_OkNewbornButton:
                editNewBorn();
                break;
            case R.id.DeleteLastNewbornButton:
                deleteLastNewborn(v);
                break;

            case R.id.editNewBornButton:
                if(editMode){
                    editMode=false;
                    Utilities.Disable(this, R.id.DeliveryNewBornLayout);
                    Utilities.MakeVisible(this, R.id.DeleteLastNewbornButton);
                    Utilities.Enable(this,R.id.DeleteLastNewbornButton);
                    Utilities.Enable(this,R.id.editNewBornButton);
                    getButton(R.id.editNewBornButton).setText(getText(R.string.string_edit));
                }else{
                    editMode=true;
                    Utilities.Enable(this, R.id.DeliveryNewBornLayout);
                    Utilities.Disable(this,R.id.deliveryNewBorn);
                    Utilities.MakeInvisible(this,R.id.DeleteLastNewbornButton);
                    getButton(R.id.id_saveNewbornButton).setText(getText(R.string.string_update));
                    getButton(R.id.editNewBornButton).setText(getText(R.string.string_cancel));
                }

                break;

        }
    }

    private void saveNewBorn(){
        countSaveClick++;
        if (countSaveClick == 2) {
            newbornSaveToJson();
            isSavedOrUpdated=true;
            countSaveClick = 0;

        } else if (countSaveClick == 1) {
            if(!hasTheRequiredFileds()) {
                countSaveClick = 0;
                return;
            }
            Utilities.Disable(this, R.id.NewBorn);
            Utilities.Disable(this,R.id.editNewBornButton);
            Utilities.Enable(this, R.id.id_saveNewbornButton);
            Utilities.Enable(this, R.id.id_OkNewbornButton);
            Utilities.Enable(this, R.id.DeleteLastNewbornButton);

            getButton(R.id.id_saveNewbornButton).setText(getText(R.string.string_confirm));
            Utilities.MakeVisible(this, R.id.id_OkNewbornButton);

            Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
        }
    }

    private void editNewBorn(){
        if(countSaveClick == 0) {
            finishActivity(ActivityResultCodes.NEWBORN_ACTIVITY);
            finish();
        }

        else if(countSaveClick == 1) {
            countSaveClick = 0;
            Utilities.Enable(this, R.id.NewBorn);
            getButton(R.id.id_saveNewbornButton).setText(getText(R.string.string_save));
            Utilities.Enable(this,R.id.editNewBornButton);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void handleRadioButton(RadioGroup group, int checkedId) {
        if(checkedId == R.id.deliveryResastationYesButton) {
            Utilities.MakeVisible(this, R.id.StimulationBagNMask);
            getCheckbox(R.id.stimulation).setChecked(true);
            //getCheckbox(R.id.bag_n_mask).setChecked(false);
        } else if (checkedId == R.id.deliveryResastationNoButton) {
            Utilities.MakeInvisible(this, R.id.StimulationBagNMask);
            getCheckbox(R.id.stimulation).setChecked(false);
            getCheckbox(R.id.bag_n_mask).setChecked(false);
        }
    }
    private void newbornSaveToJson() {

        newbornInfoUpdateTask = new AsyncNewbornInfoUpdate(this, this);
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            getSpecialCases(json);
            //............
            String sateliteCenter=provider.getSatelliteName();
            json.put("sateliteCenterName", sateliteCenter == null ? "" : sateliteCenter);
            if(editMode) json.put("birthWeight",json.get("weight"));
            //..........
            Log.d("DeliveryJsonFoundinSave", json.toString());
            Log.e("Servlet and Rootkey", SERVLET + " " + ROOTKEY);
            newbornInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {
            Log.d("Newborn", "JSON Exception: " + jse.getMessage());
        }
    }

    private void restoreNewbornFromJSON(JSONObject json) {
        Utilities.setCheckboxes(jsonCheckboxMap, json);
        Utilities.setSpinners(jsonSpinnerMap, json);
        Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap, json);
        //updateRadioButtons(json);
        Utilities.setEditTexts(jsonEditTextMap, json);
        Utilities.setEditTextDates(jsonEditTextDateMap, json);
        try {
            Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap, json);
        } catch (IllegalArgumentException  IAE) {

        }
        Utilities.setTextViews(jsonTextViewsMap,json);
        try {
            childNo = Integer.valueOf(json.getString("childno"));
            String bStatus = integerRecd==0?"":(integerRecd==1?"(জীবিত)":"(মৃত)");
            jsonTextViewsMap.get("childno").setText(Utilities.ConvertNumberToBangla(json.getString("childno"))+bStatus);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //updateEditTextTimes(json);
        setSpecialCases(json);
        Log.d(LOGTAG, "Delivery Response Received:\n\t" + json.toString());
        //dJson = json;

        showHideNewbornDeleteButton(json);
        Utilities.Disable(this, R.id.DeliveryNewBornLayout);
        Utilities.MakeVisible(this,R.id.editNewBornButton);
        Utilities.Enable(this, R.id.editNewBornButton);
        Utilities.Enable(this, R.id.id_OkNewbornButton);
        Utilities.Enable(this, R.id.DeleteLastNewbornButton);
    }

    public void setSpecialCases(JSONObject json) {
        //check if massarated
        try {
            if(integerRecd == 3) {
                if(json.has("gender") &&
                        json.getString("gender").equals("3")) {
                    getRadioButton(R.id.deliveryNewBornNotDetected).setChecked(true);
                }
            }
        }  catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    private void showHideNewbornDeleteButton(JSONObject json) {
        Utilities.SetVisibility(this, R.id.DeleteLastNewbornButton, isLastNewborn(json) ? View.VISIBLE :View.INVISIBLE);
    }

    private boolean isLastNewborn(JSONObject json){
        boolean thresholdPeriodPassed = false;
        String providerId = "";
        try {
            if(json.has("systemEntryDate") && !json.getString("systemEntryDate").equals("")){
                Date lastVisitEntryDate = Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,json.getString("systemEntryDate"));
                thresholdPeriodPassed = Utilities.getDateDiff(lastVisitEntryDate,new Date(), TimeUnit.DAYS)> Flag.UPDATE_THRESHOLD;
            }
            if(json.has("providerId")){
                providerId = json.getString("providerId");
            }
            return (provider.getProviderCode().equals(providerId) &&
                    json.getString("lastchildno").equals(json.getString("childno")) && !thresholdPeriodPassed);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException pse){
            pse.printStackTrace();
        }
        return false;
    }

    public void getSpecialCases(JSONObject json) {
        try {
            json.put("childno",childNo);
            json.put("immature", (jsonTextViewsMap.get("immature").getVisibility()==View.VISIBLE) ? "1" : "2");
            json.put("outcomeplace", deliveryJsonObj.getInt("dPlace"));
            json.put("outcomedate", deliveryJsonObj.getString("dDate"));
            json.put("outcometime", deliveryJsonObj.getString("dTime"));
            json.put("outcometype", deliveryJsonObj.getInt("dType"));
            if ( getRadioButton(R.id.deliveryNewBornNotDetected).isChecked() ) {
                json.put("gender", 3);
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }
    }


    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {

        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
                "providerid:"+ String.valueOf(provider.getProviderCode()) + "," +
                "pregno:" + mother.getPregNo() + "," +
                "newbornLoad:" + (isRetrieval? "retrieve":(editMode?"update":"\"\"")) +
                "}";

        return new JSONObject(queryString);
    }

    private void checkSetMaturity() {
        try {
            if (deliveryJsonObj.has("immatureBirth") && deliveryJsonObj.getInt("immatureBirth") == 1 ) {
                jsonTextViewsMap.get("immature").setText(" ( "+getString(R.string.premature_birth_before_37_weeks_full)
                        + Utilities.ConvertNumberToBangla(deliveryJsonObj.getString("immatureBirthWeek"))
                        + getString(R.string.week)+" )");
                jsonTextViewsMap.get("immature").setTextColor(Color.RED);
                Utilities.SetVisibility(this, R.id.deliveryNewBornMaturity, View.VISIBLE);
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        } catch (NumberFormatException nfe) {
            Log.e(LOGTAG, "Could nt convert Response to Bangla");
            Utilities.printTrace(nfe.getStackTrace());
        }
    }

    private boolean isImmature(PregWoman mother, String date) { // receive date in json format that is
        CustomSimpleDateFormat sdf = new CustomSimpleDateFormat("dd/MM/yyyy");
        long days = 0;
        try {
            Date deliveryDate = sdf.parse(date);
            days = TimeUnit.DAYS.convert(mother.getLmp().getTime() - deliveryDate.getTime(), TimeUnit.DAYS);

        } catch (ParseException PE) {

        }
        return (days < (37 * 7) );
    }

    private void deleteConfirmed() {
        try {

            JSONObject deleteJson = buildQueryHeader(false);
            deleteJson.put("newbornLoad", "delete");
            deleteJson.put("birthStatus", newbornDeleteObj.getString("birthStatus"));
            deleteJson.put("gender", newbornDeleteObj.getString("gender"));
            deleteJson.put("childno", newbornDeleteObj.getString("childno"));

            Log.d("look", deleteJson.toString());
            isSavedOrUpdated=false;

            newbornInfoQueryTask = new AsyncNewbornInfoUpdate(this, this);
            newbornInfoQueryTask.execute(deleteJson.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete ANC request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    public void deleteLastNewborn(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(DeliveryNewbornActivity.this).create();
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

    private boolean hasTheRequiredFileds() {


        RadioButton boy,girl, notDetected;

        boolean valid = true;
        boolean specialInvalid = false;

        boy = (RadioButton) findViewById(R.id.deliveryNewBornSon);
        girl=(RadioButton) findViewById(R.id.deliveryNewBornDaughter);
        notDetected=(RadioButton) findViewById(R.id.deliveryNewBornNotDetected);

        boolean allSelected= boy.isChecked() || girl.isChecked() || notDetected.isChecked();
        //TODO(arafat): has to add dialog and other review later
        if(!getEditText(R.id.deliveryNewBornWeightValue).getText().toString().equals("")){
            double weight = Double.valueOf(getEditText(R.id.deliveryNewBornWeightValue).getText().toString());
            if(weight <= 2.0 && !getCheckbox(R.id.deliveryChildReferCheckBox).isChecked()){
                AlertDialogCreator.SimpleMessageDialog(this,getString(R.string.low_newBorn_weight),
                        "নিশ্চিত হোন",android.R.drawable.ic_dialog_alert);
                //Utilities.showAlertToast(this, this.getResources().getString(R.string.low_newBorn_weight));
                return false;
            }
        }
        if (getCheckbox(R.id.deliveryChildReferCheckBox).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.deliveryChildReferCenterNameSpinner)))
                specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.deliveryChildReferReasonSpinner)))
                specialInvalid = true;
        }
        if(!allSelected) {
            Utilities.showAlertToast(this,getResources().getString(R.string.GeneralSaveWarning));
            valid=false;
        }
        if (!valid) {
            Toast toast = Toast.makeText(this, R.string.GeneralSaveWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        } else if (specialInvalid) {
            MethodUtils.showSnackBar(findViewById(R.id.activity_delivery), getResources().getString(R.string.refer_validation_message), true);
            return false;
        }

        return true;
    }
}
