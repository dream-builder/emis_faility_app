package org.sci.rhis.fwc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.AsyncDeliveryInfoUpdate;
import org.sci.rhis.connectivityhandler.AsyncNewbornInfoUpdate;
import org.sci.rhis.model.PregWoman;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.CustomTimePickerDialog;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import org.sci.rhis.utilities.CustomSimpleDateFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DeliveryActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                               View.OnClickListener,
                                                                                AlertDialogCreator.DialogButtonClickedListener,
                                                                               CompoundButton.OnCheckedChangeListener{

    //UI References
    private CustomDatePickerDialog datePickerDialog;
    private CustomTimePickerDialog timePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;
    private int currentChildCount = 0, newBornButtonID;
    private JSONObject dJson;
    public PregWoman mother; //TODO : WTF (JZ) ... why it is public
    private ProviderInfo provider;
    private ArrayAdapter<String> childAdapter;
    private ArrayList<String> childList;
    private Intent passJson;
    private String existingChildInfo;
    final private String SERVLET = "delivery";
    final private String ROOTKEY = "deliveryInfo";
    private  final String LOGTAG = "FWC-DELIVERY";
    private boolean hasDeliveryInfo = false;
    private int countSaveClick = 0;
    final private CustomSimpleDateFormat uiFormat = new CustomSimpleDateFormat("dd/MM/yyyy");

    AsyncDeliveryInfoUpdate deliveryInfoQueryTask;
    AsyncDeliveryInfoUpdate deliveryInfoUpdateTask;

    private MultiSelectionSpinner multiSelectionSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        // For Multi Select Spinner
        final List<String> dtreatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.id_spinner_treatment);
        multiSelectionSpinner.setItems(dtreatmentlist);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> dadvicelist = Arrays.asList(getResources().getStringArray(R.array.Delivery_Advice_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.id_spinner_advice);
        multiSelectionSpinner.setItems(dadvicelist);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> dreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.Delivery_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.id_spinner_refer_delivery_cause);
        multiSelectionSpinner.setItems(dreferreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});


        initialize(); //super class
        childList  = new ArrayList<>(); //childList
        Spinner spinners[] = new Spinner[3];
        spinners[0] = (Spinner) findViewById(R.id.delivery_placeDropdown);
        spinners[1] = (Spinner) findViewById(R.id.id_facility_name_Dropdown);
        spinners[2] = (Spinner) findViewById(R.id.delivery_typeDropdown);

        for(int i = 0; i < spinners.length; ++i) {
            spinners[i].setOnItemSelectedListener(this);
        }

        getEditText(R.id.id_delivery_date).setOnClickListener(this);
        getEditText(R.id.id_admissionDate).setOnClickListener(this);
        getCheckbox(R.id.id_delivery_refer).setOnCheckedChangeListener(this);
        getCheckbox(R.id.deliveryMyselfCheckbox).setOnCheckedChangeListener(this);

        //custom time picker
        timePickerDialog = new CustomTimePickerDialog(this);

        Intent intent = getIntent();

        //create the mother and hte provider
        mother = intent.getParcelableExtra("PregWoman");
        provider = intent.getParcelableExtra("Provider");


        //custom date picker to restrict the dates which is less than LMP date
        Date minDeliveryDate = Utilities.addDateOffset(mother.getLmp(),1);
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH,minDeliveryDate);
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.imageViewDeliveryDate, (EditText)findViewById(R.id.id_delivery_date));
        datePickerPair.put(R.id.imageViewAdmissionDate, (EditText)findViewById(R.id.id_admissionDate));

        //is deliveryInfo present
        hasDeliveryInfo = false;

        if(mother.getDeliveryInfo() == 1 &&
            intent.hasExtra("actualDeliveryDateAvailable") &&
            intent.getBooleanExtra("actualDeliveryDateAvailable", false)) {
            Date tempDate = mother.getActualDelivery();
            jsonEditTextDateMap.get("dDate").setText( tempDate != null ? uiFormat.format(mother.getActualDelivery()): "");
        }

        //Query DB for existing mother and child info
        getMotherInfo();
        getExistingChild();

        passJson = new Intent(this, DeliveryNewbornActivity.class);


        //disable delivery result
        Utilities.Disable(this, R.id.id_deliveryResultLayout);
        Utilities.SetVisibility(this, R.id.id_deliveryResultLayout, View.GONE);
        Utilities.SetVisibility(this, R.id.newborn_Tabla_Layout, View.INVISIBLE);
    }


    private void changeUIForCSBA(){
        Utilities.MakeInvisible(this,R.id.id_facililties_admission_layout);
        Utilities.MakeInvisible(this,R.id.id_Epcotomi_section_layout);
        Utilities.MakeInvisible(this,R.id.deliveryMyselfLayout);
        Utilities.MakeInvisible(this,R.id.deliveryAttendantDetails);
        Utilities.MakeInvisible(this,R.id.attendantTitleLayout);
        if(mother.getDeliveryInfo()!=1){
            getSpinner(R.id.delivery_typeDropdown).setSelection(1);
        }
        Utilities.Disable(this, R.id.delivery_typeDropdown);
    }

    @Override
    public void callbackAsyncTask(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            String key;

            for (Iterator<String> ii = json.keys(); ii.hasNext(); ) {
                key = ii.next();
                Log.d(LOGTAG, "Rcvd Key:" + key + " Value:\'" + json.get(key) + "\'");
            }

            //populate the fields if the previous delivery information exist
            if(json.getString("dNew").equals("No")) {
                Utilities.setCheckboxes(jsonCheckboxMap, json);
                Utilities.setSpinners(jsonSpinnerMap, json);
                Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap, json);
                updateRadioButtons(json);
                Utilities.setEditTexts(jsonEditTextMap, json);
                Utilities.setEditTextDates(jsonEditTextDateMap, json);
                Utilities.setTextViews(jsonTextViewsMap,json);
                updateEditTextTimes(json);
                Log.d(LOGTAG, "Delivery Response Received:\n\t" + json.toString());
                dJson = json;

                //TODO Make the fields non-modifiable
                Utilities.Disable(this, R.id.delivery_info_layout);
                Utilities.Enable(this, R.id.btn_save_add_child);
                Utilities.MakeVisible(this, R.id.editDeliveryButton);
                Utilities.MakeInvisible(this, R.id.saveDeliveryButton);
                Utilities.MakeInvisible(this, R.id.dynamicCancelButton);
                mother.setDeliveryInfo(1);
                hasDeliveryInfo = true;
                mother.setActualDelivery(json.getString("dDate"), "yyyy-MM-dd");
                Utilities.SetVisibility(this, R.id.newborn_Tabla_Layout, View.VISIBLE);
                Utilities.SetVisibility(this,R.id.layoutEditNewbornInDelivery,View.GONE);
                Utilities.SetVisibility(this, R.id.id_deliveryResultLayout, View.VISIBLE);
                Utilities.Disable(this, R.id.id_deliveryResultLayout);

            }

        } catch (JSONException jse) {
            jse.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        LinearLayout [] section;
        switch (parent.getId()) {
            case R.id.delivery_placeDropdown:

                section = new LinearLayout[3];
                section [0] = (LinearLayout) findViewById(R.id.id_facililties_section_layout);
                section [1] = (LinearLayout) findViewById(R.id.id_facililties_admission_layout);
                section [2] = (LinearLayout) findViewById(R.id.id_Epcotomi_section_layout);

                for(int i = 0; i < section.length; ++i) {

                    if(position == 1 || provider.getmCsba().equals("1")) { // home delivery or CSBA assisted
                        section[i].setVisibility(View.GONE); //hide irrelevant section e.g. facility details
                        getSpinner(R.id.delivery_typeDropdown).setSelection(1); //normal delivery
                        Utilities.Disable(this, R.id.delivery_typeDropdown);
                    } else {
                        section[i].setVisibility(View.VISIBLE);
                        if(!provider.getmCsba().equals("1")){
                            //getSpinner(R.id.delivery_typeDropdown).setSelection(0); //let the client choose
                            //
                        }
                        Utilities.Enable(this, R.id.delivery_typeDropdown);
                    }
                }
                break;
            case R.id.id_facility_name_Dropdown:

                LinearLayout  faclityAdmissionWard = (LinearLayout) findViewById(R.id.id_facililties_admission_ward_layout);
                faclityAdmissionWard.setVisibility((position == 5 || position == 6) ? View.GONE:View.VISIBLE);
                //5 - UH&FWC 6 - CC
                break;
            case R.id.delivery_typeDropdown:
                section = new LinearLayout[2];
                section [0] = (LinearLayout) findViewById(R.id.id_deliveryResultLayout);
                section [1] = (LinearLayout) findViewById(R.id.id_DeliveryManagementLayout);

                for(int i = 0; i < section.length; ++i) {
                    //do nothing now
                    //section[i].setVisibility( position != 2 ? View.GONE:View.VISIBLE); //0 - abortion
                }

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (buttonView.getId() == R.id.id_delivery_refer) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            int layouts[] = {R.id.reason, R.id.id_referCenterDetails};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i], visibility);
            }
        }

        if (buttonView.getId() == R.id.deliveryMyselfCheckbox) {
            if(isChecked) {
                getEditText(R.id.id_attendantName).setText(provider.getProviderName());
                getSpinner(R.id.id_attendantTitleDropdown).setSelection(getProviderType(provider.getmProviderType()));
                Utilities.Disable(this, R.id.id_attendantName);
                Utilities.Disable(this, R.id.attendantTitleLayout);
                if(provider.getmCsba().equals("1")){
                    changeUIForCSBA();
                }
            }
            else {
                getEditText(R.id.id_attendantName).setText("");
                getSpinner(R.id.id_attendantTitleDropdown).setSelection(0);
                Utilities.Enable(this, R.id.id_attendantName);
                Utilities.Enable(this, R.id.attendantTitleLayout);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getTag() != null && view.getTag().equals("DateField")) {
            datePickerDialog.show(datePickerPair.get(view.getId()));
        } else {
            if (view.getId() == R.id.saveDeliveryButton) {
                countSaveClick++;
                if (countSaveClick == 2) {
                    if(!hasTheRequiredFields()) {
                        countSaveClick = 0;
                        return;
                    }
                    saveToJson();
                    Utilities.MakeVisible(this, R.id.newborn_Tabla_Layout);
                    getButton(R.id.saveDeliveryButton).setText(getText(R.string.string_save_and_new_born));
                    countSaveClick = 0;

                } else if (countSaveClick == 1) {
                    if(!hasTheRequiredFields()) {
                        countSaveClick = 0;
                        return;
                    }
                    Utilities.Disable(this, R.id.delivery_info_layout);
                    Utilities.Enable(this, R.id.btn_save_add_child);
                    getButton(R.id.saveDeliveryButton).setText(getText(R.string.string_confirm));
                    Utilities.MakeVisible(this, R.id.dynamicCancelButton);

                    Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toastTV.setTextSize(20);
                    toast.show();
                }
            } else if (view.getId() == R.id.dynamicCancelButton) {
                if (countSaveClick == 1) {
                    countSaveClick = 0;
                    Utilities.MakeInvisible(this, R.id.dynamicCancelButton);
                    if(getButton(R.id.saveDeliveryButton).getText().equals("Confirm")) {
                        Utilities.Enable(this, R.id.delivery_info_layout);
                        getButton(R.id.saveDeliveryButton).setText(getText(R.string.string_save_and_new_born));
                    }
                    else if(getButton(R.id.saveDeliveryButton).getText().equals("Update")) {
                        Utilities.Disable(this, R.id.delivery_info_layout);
                        getButton(R.id.editDeliveryButton).setText(getText(R.string.string_edit));
                    }
                }
            } else if (view.getId() == R.id.editDeliveryButton) {
                countSaveClick++;
                if (countSaveClick == 2) {
                    saveToJson();
                    getButton(R.id.editDeliveryButton).setText(getText(R.string.string_edit));
                    countSaveClick = 0;
                }
                else if (countSaveClick == 1) {
                    Utilities.Enable(this, R.id.delivery_info_layout);
                    Utilities.Disable(this, R.id.id_deliveryResultLayout);
                    Utilities.MakeVisible(this, R.id.saveDeliveryButton);
                    Utilities.MakeVisible(this, R.id.dynamicCancelButton);
                    Utilities.MakeInvisible(this, R.id.editDeliveryButton);
                    if(provider.getmCsba().equals("1") && getCheckbox(R.id.deliveryMyselfCheckbox).isChecked()){
                        changeUIForCSBA();
                    }
                }
            }
        }
        if( view.getId()== R.id.newbornAddButton ||
                view.getId()== R.id.deathFreshButton ||
                view.getId()== R.id.deathmaceratedButton ) {
            newBornButtonID = view.getId();

            //showing dialog for adding death newborn
            if((newBornButtonID == R.id.deathFreshButton || newBornButtonID == R.id.deathmaceratedButton)) {
                AlertDialogCreator.SpecialDecisionDialog(this, getString(R.string.add_dead_newborn_query), getString(R.string.warning), android.R.drawable.ic_dialog_alert, new String[]{getString(R.string.symptom_response_yes), getString(R.string.symptom_response_no)}, false, AlertDialog.THEME_HOLO_DARK, Color.RED, Color.RED);
            }
            //showing dialog for adding newborn more then 1 child in current womb
            else if (newBornButtonID == R.id.newbornAddButton && currentChildCount > 0) {
                AlertDialogCreator.SpecialDecisionDialog(this, getString(R.string.add_newborn_query_part1) + " " + Utilities.ConvertNumberToBangla(String.valueOf(currentChildCount)) + " " +  getString(R.string.add_newborn_query_part2), getString(R.string.warning), android.R.drawable.ic_dialog_alert, new String[]{getString(R.string.symptom_response_yes), getString(R.string.symptom_response_no)}, false, AlertDialog.THEME_HOLO_DARK, Color.RED, Color.RED);
            } else { //add first newborn
                callForNewBornActivityResult();
            }
        }
    }

    private int getLayoutType (int id) {
        int layoutType = -1; //default newborn

        switch (id) {
            case R.id.newbornAddButton:
                layoutType = 1;
                break;
            case R.id.deathFreshButton:
                layoutType = 2;
                break;
            case R.id.deathmaceratedButton:
                layoutType = 3;
                break;
            default:
                Log.e(LOGTAG, "Unknown type: " + id + findViewById(id).getClass().getName());
        }

        return layoutType;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ActivityResultCodes.NEWBORN_ACTIVITY) {
            if(data.hasExtra("ReloadNewborn") && data.getBooleanExtra("ReloadNewborn", false)) {
                Log.d(LOGTAG, "Returned from new born loading updates ...");
                getMotherInfo();
                getExistingChild();
            }
            if(data.hasExtra("newBornSavingFlag") && data.getBooleanExtra("newBornSavingFlag",false)){
                AlertDialogCreator.SimpleMessageWithNoTitle(this,"নবজাতকের তথ্য সফলভাবে সংরক্ষিত হয়েছে।");
                //TODO: Move the string to string file with proper translation
            }
        }
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    public void pickTime(View view) {

        timePickerDialog.show(
                (EditText) findViewById(R.id.delivery_time_hour),
                (EditText) findViewById(R.id.delivery_time_minute),
                (Spinner) findViewById(R.id.delivery_time_Dropdown)
        );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void initiateCheckboxes() {
        //AMTSL
        jsonCheckboxMap.put("dOxytocin",        getCheckbox(R.id.oxytocin));
        jsonCheckboxMap.put("dTraction",        getCheckbox(R.id.controlChordTraction));
        jsonCheckboxMap.put("dUMassage",        getCheckbox(R.id.uterusMassage));

        //Complicacy
        jsonCheckboxMap.put("dLateDelivery",    getCheckbox(R.id.id_delayedDelivery));
        jsonCheckboxMap.put("dBloodLoss",       getCheckbox(R.id.id_BloodLoss));
        jsonCheckboxMap.put("dBlockedDelivery", getCheckbox(R.id.id_blockedDelivery));
        jsonCheckboxMap.put("dPlacenta",        getCheckbox(R.id.id_blockedPlacenta));
        jsonCheckboxMap.put("dHeadache",        getCheckbox(R.id.id_heavyHedache));
        jsonCheckboxMap.put("dBVision",         getCheckbox(R.id.id_blurryVision));
        jsonCheckboxMap.put("dOBodyPart",       getCheckbox(R.id.id_onlyHead));
        jsonCheckboxMap.put("dConvulsions",     getCheckbox(R.id.id_convulsion));
        jsonCheckboxMap.put("dOthers",          getCheckbox(R.id.id_deleiveryExtra));
        jsonCheckboxMap.put("dAttendantThisProvider", getCheckbox(R.id.deliveryMyselfCheckbox));
        //refer
        jsonCheckboxMap.put("dRefer",           getCheckbox(R.id.id_delivery_refer));

    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("dPlace", getSpinner(R.id.delivery_placeDropdown)); //place
        jsonSpinnerMap.put("dType", getSpinner(R.id.delivery_typeDropdown)); //type
        jsonSpinnerMap.put("dCenterName", getSpinner(R.id.id_facility_name_Dropdown));
        jsonSpinnerMap.put("dAttendantDesignation", getSpinner(R.id.id_attendantTitleDropdown)); //deliveryAttendant
        jsonSpinnerMap.put("dReferCenter", getSpinner(R.id.id_spinner_refer_facilities)); //refercenter
    }

    @Override
    protected void initiateMultiSelectionSpinners(){
        jsonMultiSpinnerMap.put("dTreatment", getMultiSelectionSpinner(R.id.id_spinner_treatment)); //treatment
        jsonMultiSpinnerMap.put("dAdvice", getMultiSelectionSpinner(R.id.id_spinner_advice)); //advice
        jsonMultiSpinnerMap.put("dReferReason", getMultiSelectionSpinner(R.id.id_spinner_refer_delivery_cause)); //refer reason
    }

    @Override
    protected void initiateEditTexts() {
        //admission details
        jsonEditTextMap.put("dWard", getEditText(R.id.id_ward));
        jsonEditTextMap.put("dBed", getEditText(R.id.id_bed));

        //new born details
        jsonEditTextMap.put("dNoLiveBirth", getEditText(R.id.Live_born));
        jsonEditTextMap.put("dStillFresh",getEditText(R.id.Dead_born_fresh));
        jsonEditTextMap.put("dStillMacerated",getEditText(R.id.Dead_born_macerated));
        jsonEditTextMap.put("dNewBornBoy",getEditText(R.id.son));
        jsonEditTextMap.put("dNewBornGirl",getEditText(R.id.daughter));
        jsonEditTextMap.put("dNewBornUnidentified",getEditText(R.id.notDetected));
        //attendant name
        jsonEditTextMap.put("dAttendantName",getEditText(R.id.id_attendantName));
        getEditText(R.id.id_attendantName).setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        getEditText(R.id.id_delivery_date).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.id_delivery_date)));
        //set all CAP
    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewsMap.put("dNoStillBirth", getTextView(R.id.TextViewDead_bornNumber));

        getTextView(R.id.delivery_place_label).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.delivery_place_label).getText().toString(), 0, 1));
        getTextView(R.id.delivery_type).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.delivery_type).getText().toString(), 0, 1));
        getTextView(R.id.TextViewdelivery_date).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.TextViewdelivery_date).getText().toString(), 0, 1));
        getTextView(R.id.id_refer_facility_name).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.id_refer_facility_name).getText().toString(), 0, 1));
        getTextView(R.id.id_refer_delivery_cause).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.id_refer_delivery_cause).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("dDate", getEditText(R.id.id_delivery_date));
        jsonEditTextDateMap.put("dAdmissionDate", getEditText(R.id.id_admissionDate));
    }

    private void updateEditTextTimes(JSONObject jso) {

        //Too much internal knowledge assumed
        try {
            String time = jso.getString("dTime");
            if(!time.equals("")) {
                getEditText(R.id.delivery_time_hour).setText(time.substring(0,time.indexOf(':')));
                getEditText(R.id.delivery_time_minute).setText(time.substring(time.indexOf(':') + 1, time.indexOf(' ')));
                String ampm = time.substring(time.indexOf(' ') + 1);
                getSpinner(R.id.delivery_time_Dropdown).setSelection(ampm.equalsIgnoreCase("AM")? 0:1);
            }
        } catch (JSONException jse) {
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    private void getEditTextTime(JSONObject json) {

        String time = getEditText(R.id.delivery_time_hour).getText().toString();
        time += ":" + getEditText(R.id.delivery_time_minute).getText().toString();
        time += " " + getSpinner(R.id.delivery_time_Dropdown).getSelectedItem().toString();
        try {
            String convertedTime = Converter.convertSdfFormat(Constants.TIME_FORMAT_AMPM, time,Constants.TIME_FORMAT_24);
            json.put("dTime", convertedTime);
        } catch (JSONException jse) {
            Utilities.printTrace(jse.getStackTrace());
        } catch (ParseException pe) {
            Utilities.printTrace(pe.getStackTrace());
        }
    }

    private void updateRadioButtons(JSONObject json) {
        try {
            if (json.getString("dEpisiotomy").equals("1")) {
                getRadioGroup(R.id.id_radioGroupEpctiomi).check(R.id.radioEpc_yes);

            } else if (json.getString("dEpisiotomy").equals("2")) {
                getRadioGroup(R.id.id_radioGroupEpctiomi).check(R.id.radioEpc_no);
            }

            if (json.getString("dMisoprostol").equals("1")) {
                getRadioGroup(R.id.id_radioGroupMisoprostol).check(R.id.radioMisoprostol_yes);

            } else if (json.getString("dMisoprostol").equals("2")) {
                getRadioGroup(R.id.id_radioGroupMisoprostol).check(R.id.radioMisoprostol_no);
            }
        } catch (JSONException jse) {
            System.out.println("The JSON key:  does not exist");
        }
    }

    private void saveToJson() {
        deliveryInfoUpdateTask = new AsyncDeliveryInfoUpdate(this, this);
        JSONObject json;
        try {
            json = buildQueryHeader(true, false); //mother, no retireve
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            getEditTextTime(json);
            getSpecialCases(json);
            //............
            String sateliteCenter=provider.getSatelliteName();
            json.put("sateliteCenterName", sateliteCenter == null ? "" : sateliteCenter);
            //..........
            deliveryInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);
            Log.d(LOGTAG, "In Save, Delivery Json in Query:" + json.toString());
            passJson.putExtra("DeliveryJson", json.toString());

        } catch (JSONException jse) {
            Log.e("Delivery", "JSON Exception: " + jse.getMessage());
        }

    }

    public void getSpecialCases(JSONObject json) {
        try {
            json.put("dOther", ""); //other delivery complicacies
            json.put("dOtherReason", ""); //other delivery complicacies
            int id[] = {R.id.Dead_born_fresh, R.id.Dead_born_macerated};
            int stillBirth = 0;
            String temp = "";
            for(int i = 0; i < 2; i++) {
                temp = getEditText(id[i]).getText().toString();
                if(!temp.equals("")) {
                    stillBirth += Integer.valueOf(temp);
                }
            }
            json.put("dNoStillBirth", stillBirth);

            if(!json.has("dAdmissionDate")){
                json.put("dAdmissionDate","");
            }
        } catch (JSONException jse) {
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    @Override
    protected void initiateRadioGroups() {

        jsonRadioGroupButtonMap.put("dEpisiotomy", Pair.create(
                getRadioGroup(R.id.id_radioGroupEpctiomi), Pair.create(
                        getRadioButton(R.id.radioEpc_yes),
                        getRadioButton(R.id.radioEpc_no))
                )
        );

        jsonRadioGroupButtonMap.put("dMisoprostol", Pair.create(
                getRadioGroup(R.id.id_radioGroupMisoprostol), Pair.create(
                        getRadioButton(R.id.radioMisoprostol_yes),
                        getRadioButton(R.id.radioMisoprostol_no))
                )
        );
    }
    private JSONObject buildQueryHeader(boolean isMother, boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
                (isRetrieval ? "": "providerid:\""+String.valueOf(provider.getProviderCode())+"\",") +
                "pregno:" + mother.getPregNo() + "," +
                (isMother ?"deliveryLoad:":"newbornLoad:") + (isRetrieval? "retrieve":"\"\"") +
                "}";
        return new JSONObject(queryString);
    }
    private boolean checkClientInfo() {
        if(mother== null ) {
            Toast.makeText(this, R.string.string_toast_client_miss, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void getExistingChild() {
        boolean isMother = false; //just being verbose for better readability
        boolean retrieve = true; //just being verbose for better readability
        try {
            JSONObject childJson = buildQueryHeader(isMother, retrieve);
            AsyncNewbornInfoUpdate childInfoUpdate = new AsyncNewbornInfoUpdate(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    Log.d(LOGTAG, "NEWBORN Response Received:\n\t" + result);
                    handleExistingChild(result);
                }
            }, this);

            childInfoUpdate.execute(childJson.toString(), "newborn", "newbornInfo");
            //queryString, SERVLET, ROOTKEY
        } catch (JSONException JSE) {
            Log.e(LOGTAG, "Building child query\n\t:" + JSE.getStackTrace().toString());
        }
    }

    private void getMotherInfo() {
        String queryString = "";
        try {
            boolean isMother = true; //just being verbose for better readability
            boolean retireve = true; //just being verbose for better readability
            queryString = buildQueryHeader(isMother, retireve).toString();
            Log.d(LOGTAG, "Sending JSON:\n\t" + queryString);
        } catch (JSONException JSE) {
            Log.e("Delivery", "Could not build query String: " + JSE.getMessage());
        }
        deliveryInfoQueryTask = new AsyncDeliveryInfoUpdate(this, this);
        deliveryInfoQueryTask.execute(queryString, SERVLET, ROOTKEY); //Get Mother Info
    }

    private void startChildActivity(int index, JSONObject child) throws JSONException{
        child.put("childno", index);
        child.put("lastchildno", currentChildCount);
        passJson.putExtra("Layout", child.has("birthStatus") ? (
                child.getString("birthStatus").equals("") ? 0: child.getInt("birthStatus")): 0);// flag sets to 0 when there is no birth status e.g. from fwa
        passJson.putExtra("DeliveryJson", dJson.toString());
        passJson.putExtra("NewbornJson", child.toString());
        //put extra last child ?

        if(mother.getDeliveryInfo()==1 /*|| mother.isEligibleFor(PregWoman.PREG_SERVICE.NEWBORN)*/) {
            passJson.putExtra("PregWoman", mother);
            passJson.putExtra("Provider", ProviderInfo.getProvider());
            Log.d(LOGTAG, dJson.toString());

            startActivityForResult(passJson, ActivityResultCodes.NEWBORN_ACTIVITY);
        } else {
            Toast.makeText(this, "Newborn cannot be added, verify ...", Toast.LENGTH_LONG).show();
        }
    }

    private void handleExistingChild(String result) {
        existingChildInfo = result;
        try {
            final JSONObject childJson = new JSONObject(result);
            if (childJson.has("hasNewbornInfo")) {
                if (childJson.getString("hasNewbornInfo").equals("Yes")) {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_newborn_container);
                    linearLayout.removeAllViews();

                    currentChildCount = childJson.getInt("count");
                    if(currentChildCount>0){
                        Utilities.SetVisibility(this,R.id.layoutEditNewbornInDelivery,View.VISIBLE);
                    }else{
                        Utilities.SetVisibility(this,R.id.layoutEditNewbornInDelivery,View.GONE);
                    }

                    for (int i = 0; i < currentChildCount; i++) {
                        int index = i+1;

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMarginEnd(10);

                        Button childButton = new Button(this);
                        childButton.setText("নবজাতক: " + Utilities.ConvertNumberToBangla(String.valueOf(index)));
                        childButton.setBackground(getDrawable(R.drawable.button_style));
                        childButton.setLayoutParams(params);
                        linearLayout.addView(childButton);
                        childButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                try {
                                    startChildActivity(index, childJson.getJSONObject(String.valueOf(index)));
                                } catch (JSONException JSE) {
                                    JSE.printStackTrace();
                                }
                            }
                        });
                    }
                } else { //delivery contains no newborn information
                    if(childAdapter != null && !childAdapter.isEmpty()) { //no child bur adapter contains data means, child is deleted
                        currentChildCount = 0; //reset count
                        childAdapter.clear(); //list cleared of deleted child
                        Utilities.SetVisibility(this,R.id.layoutEditNewbornInDelivery,View.GONE);
                    }
                }
            }else {
                Utilities.SetVisibility(this,R.id.layoutEditNewbornInDelivery,View.GONE);
            }
        } catch (JSONException JSE) {
            JSE.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasTheRequiredFields() {

        boolean valid = true;
        boolean specialInvalid = false;


        if (!Validation.hasSelected(getSpinner(R.id.delivery_placeDropdown))) valid=false;
        if (!Validation.hasSelected(getSpinner(R.id.delivery_typeDropdown))) valid=false;
        if (!Validation.hasText(getEditText(R.id.id_delivery_date))) valid=false;

        if (getCheckbox(R.id.id_delivery_refer).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.id_spinner_refer_facilities)))
                specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.id_spinner_refer_delivery_cause)))
                specialInvalid = true;
        }
        if (!valid) {
            Utilities.showAlertToast(this,getString(R.string.GeneralSaveWarning));
            return false;
        } else if (specialInvalid) {
            MethodUtils.showSnackBar(findViewById(R.id.id_deliveryDetails), getResources().getString(R.string.refer_validation_message), true);
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        //check if any child have been added for this delivery
        String noChildMessage = "";

        AlertDialog alertDialog = new AlertDialog.Builder(DeliveryActivity.this).create();
        if(hasDeliveryInfo && getEditText(R.id.Live_born).getText().toString().equals("0") &&
                getTextView(R.id.TextViewDead_bornNumber).getText().toString().equals("0")) { //everything is empty
            noChildMessage = "কোনো সন্তানের তথ্য দেওয়া হয় নাই, প্রসবকালীন সেবা থেকে বের হওয়ার আগে নবজাতক/মৃত জন্মের তথ্য দিন। ";
        }
        alertDialog.setTitle(noChildMessage.equals("")?"EXIT CONFIRMATION":"সন্তানের তথ্য নাই");
        alertDialog.setMessage( noChildMessage.equals("") ?
                " আপনি কি প্রসবকালীন সেবা ( Delivery ) থেকে বের হয়ে যেতে চান? \nনিশ্চিত করতে OK চাপুন, ফিরে যেতে CANCEL চাপুন " :  noChildMessage );

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if(noChildMessage.equals("")) {
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent finishIntent = new Intent();

                            finishIntent.putExtra("hasDeliveryInformation", hasDeliveryInfo);

                            setResult(RESULT_OK, finishIntent);
                            finishActivity(ActivityResultCodes.DELIVERY_ACTIVITY);
                            finish();
                            dialog.dismiss();
                        }
                    });
        }

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        newBornButtonID = 0; //clearing the previous reference
    }

    private void callForNewBornActivityResult() {
        if (passJson.hasExtra("NewbornJson")) { //when adding new child remove the reference of the old
            passJson.removeExtra("NewbornJson");
        }

        passJson.putExtra("childno", currentChildCount + 1);
        passJson.putExtra("Layout", getLayoutType(newBornButtonID));
        passJson.putExtra("DeliveryJson", dJson.toString());

        if (checkClientInfo() /*&& mother.isEligibleFor(PregWoman.PREG_SERVICE.NEWBORN)*/) {
            passJson.putExtra("PregWoman", mother);
            passJson.putExtra("Provider", ProviderInfo.getProvider());
            Log.d(LOGTAG, dJson.toString());
            startActivityForResult(passJson, ActivityResultCodes.NEWBORN_ACTIVITY);
        } else {
            Toast.makeText(this, R.string.string_toast_msg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPositiveButtonClicked(DialogInterface dialog) {
        dialog.cancel();

        callForNewBornActivityResult(); //add newborn death/alive
    }

    @Override
    public void onNegativeButtonClicked(DialogInterface dialog) {
        dialog.cancel();
    }

    @Override
    public void onNeutralButtonClicked(DialogInterface dialog) {
    }

    private int getProviderType(String jsonProviderType) {

        int provType = 3; // FWV
        try {
            provType = Integer.valueOf(jsonProviderType);
        } catch (NumberFormatException nfe) {
            Log.e(LOGTAG, "Caanot map provider type: " + jsonProviderType);
            Utilities.printTrace(nfe.getStackTrace());
        }


        switch (provType) {
            case 2: //HA - CSBA
                provType = 6;
                break;
            case 3: //FWA - CSBA
                provType = 7;
                break;
            case 4: //FWV
                provType = 3;
                break;
            case 5: //SACMO-FP
            case 6: //SACMO-HS
                provType = 4;
                break;
            case 17: //Midwife
                provType = 10;
                break;
            case 101: //Paramedic
                provType = 5;
                break;
            default: //other-relative
                provType = 9;
                break;

        }

        return provType;
    }
}
