package org.sci.rhis.fwc;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncAncInfoUpdate;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.model.PregWoman;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.BooleanValue;
import org.sci.rhis.utilities.CompositValue;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomFocusChangeListener;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.DateValue;
import org.sci.rhis.utilities.DisplayValue;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.HistoryListMaker;
import org.sci.rhis.utilities.IndexedValue;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.MultiIndexedValue;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ANCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,AlertDialogCreator.DialogButtonClickedListener{

    private PregWoman mother;
    private ProviderInfo provider;
    private boolean editMode = false;
    private JSONObject lastServiceJSON;

    private static final int DELETE=101;
    private static final int REFERCHECK=102;

    // For Date pick added by Al Amin
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    final static int FIRST_ANC_2    = 16; //WEEKS
    final static int SECOND_ANC_2   = 24; //WEEKS
    final static int THIRD_ANC_2    = 32; //WEEKS
    final static int FOURTH_ANC_2   = 36; //WEEKS

    ExpandableDisplayListAdapter displayListAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    LinearLayout ll;

    AsyncAncInfoUpdate ancInfoUpdate;

    final private String SERVLET = "anc_android";
    final private String ROOTKEY = "ANCInfo";
    private final String LOGTAG    = "FWC-ANC";

    private View mANCLayout;
    private MultiSelectionSpinner multiSelectionSpinner;
    ArrayList<DisplayValue> displayList;
    Boolean flag=false;

    JSONObject jsonStr;

    private LinearLayout history_layout;

    private int lastAncVisit,decisionFlag=0;
    private Context con;
    private int countSaveClick = 0, serviceId=0;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastAncVisit = 0; // assume no visit

        setContentView(R.layout.activity_anc);

        con = this;

        history_layout = (LinearLayout)(findViewById(R.id.history_lay_anc));

        // Find the view whose visibility will change
        mANCLayout = findViewById(R.id.ancLayoutScrollview);

        // Find our buttons
        Button visibleButton = (Button) findViewById(R.id.ancLabelButton);

        OnClickListener mVisibleListener = new OnClickListener() {
            public void onClick(View v) {
                if(flag==false) {
                    mANCLayout.setVisibility(View.VISIBLE);
                    flag=true;
                }
                else
                {
                    mANCLayout.setVisibility(View.INVISIBLE);
                    flag=false;
                }
            }
        };

        // Wire each button to a click listener
        visibleButton.setOnClickListener(mVisibleListener);
        getCheckbox(R.id.ancOtherCheckBox).setOnCheckedChangeListener((buttonView, isChecked) -> {
            switch (buttonView.getId()) {
                case R.id.ancOtherCheckBox:
                    setItemVisible(R.id.ancOtherCenterNameSpinner, isChecked);
                    break;
            }
        });

        final List<String> dangersignlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Complication_DropDown));
        final List<String> drabackblist = Arrays.asList(getResources().getStringArray(R.array.ANC_Symptom_Dropdown));
        final List<String> diseaselist = Arrays.asList(getResources().getStringArray(R.array.ANC_Disease_DropDown));
        final List<String> treatmentlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Treatment_DropDown));
        final List<String> advicelist = Arrays.asList(getResources().getStringArray(R.array.ANC_Advice_DropDown));
        final List<String> referreasonlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancComplicationSpinner);
        if(multiSelectionSpinner == null){
            Log.d("------"+ dangersignlist.get(1),".........");
        }
        multiSelectionSpinner.setItems(dangersignlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancSymptomSpinner);
        multiSelectionSpinner.setItems(drabackblist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancDiseaseSpinner);
        multiSelectionSpinner.setItems(diseaselist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancTreatmentSpinner);
        multiSelectionSpinner.setItems(treatmentlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancAdviceSpinner);
        multiSelectionSpinner.setItems(advicelist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancReasonSpinner);
        multiSelectionSpinner.setItems(referreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});


        ll = (LinearLayout)findViewById(R.id.llay);

        expListView = new ExpandableListView(this);
        ll.addView(expListView);

        //create the mother
        mother = getIntent().getParcelableExtra("PregWoman");
        provider = getIntent().getParcelableExtra("Provider");

        // Initialize Spinner added By Al Amin
        initialize(); //super class
        Spinner[] spinners = new Spinner[6];
        spinners[0] = (Spinner) findViewById(R.id.ancEdemaSpinner);
        spinners[1] = (Spinner) findViewById(R.id.ancFetalPresentationSpinner);
        spinners[2] = (Spinner) findViewById(R.id.ancJaundiceSpinner);
        spinners[3] = (Spinner) findViewById(R.id.ancUrineSugarSpinner);
        spinners[4] = (Spinner) findViewById(R.id.ancUrineAlbuminSpinner);
        spinners[5] = (Spinner) findViewById(R.id.ancReferCenterNameSpinner);

        for (Spinner spinner : spinners) {
            spinner.setOnItemSelectedListener(this);
        }

        getEditText(R.id.ancServiceDateValue).setOnClickListener(this);
        getEditText(R.id.ancServiceDateValue).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals("")) {
                    try {
                        Date visitDate = Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH, editable.toString());
                        if (Utilities.checkDateInRange(mother.getLmp(), Utilities.addDateOffset(mother.getLmp(), (16 * 7)), visitDate)) {
                            Utilities.SetVisibility(ANCActivity.this, R.id.ancUterusHeight, View.GONE);
                        } else {
                            Utilities.SetVisibility(ANCActivity.this, R.id.ancUterusHeight, View.VISIBLE);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        getCheckbox(R.id.ancReferCheckBox).setOnCheckedChangeListener(this);
        //restrict datePicker to start from a fixed date
        Date minAncDate = Utilities.addDateOffset(mother.getLmp(),1);
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH,minAncDate);
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.Date_Picker_Button, (EditText)findViewById(R.id.ancServiceDateValue));
        //hide fundal height in ANC 1 period
        if(Utilities.checkDateInRange(mother.getLmp(), Utilities.addDateOffset(mother.getLmp(),(16*7)),Calendar.getInstance().getTime())){

        }

        setHistoryLabelMapping();
        setCompositeMap("ancbpsys", "ancbpdias");
        loadANCHistory();

        //disable ANC entry if delivery info is present
        if(mother.getDeliveryInfo() == 1 || mother.getAbortionInfo() == 1) {
            Utilities.MakeInvisible(this, R.id.ancEntryMasterLayout);
            Toast.makeText(this,getString(R.string.string_toast_text_part_one) + (mother.getAbortionInfo()==1?getString(R.string.string_toast_text_part_two):getString(R.string.string_toast_text_part_three))+getString(R.string.string_toast_text_part_four),Toast.LENGTH_LONG).show();
        } else if (Utilities.addDateOffset(mother.getLmp(), PregWoman.ANC_THRESHOLD).before(new Date())) { //
            Utilities.MakeInvisible(this, R.id.ancEntryMasterLayout);
            Toast.makeText(this, getString(R.string.NewANCWarning),Toast.LENGTH_LONG).show();
        } else {
            //set ideal visit periods
            setAncVisitAdvices();
        }
    }

    private void setAncVisitAdvices() {
        Date lmp = mother.getLmp();
        CustomSimpleDateFormat sdf = new CustomSimpleDateFormat("ddMMMyy");
        getTextView(R.id.ancVisit1Date).setText(sdf.format(Utilities.addDateOffset(lmp, FIRST_ANC_2 * 7))+getString(R.string.string_anc_date_range));
        getTextView(R.id.ancVisit2Date).setText(sdf.format(Utilities.addDateOffset(lmp, (FIRST_ANC_2 * 7)+1)) + getString(R.string.string_anc_range) + sdf.format(Utilities.addDateOffset(lmp, SECOND_ANC_2 * 7)));
        getTextView(R.id.ancVisit3Date).setText(sdf.format(Utilities.addDateOffset(lmp, (SECOND_ANC_2 * 7)+1)) + getString(R.string.string_anc_range) + sdf.format(Utilities.addDateOffset(lmp, THIRD_ANC_2 * 7)));
        getTextView(R.id.ancVisit4Date).setText(sdf.format(Utilities.addDateOffset(lmp, (THIRD_ANC_2 * 7)+1)) + getString(R.string.string_anc_range) + sdf.format(Utilities.addDateOffset(lmp, FOURTH_ANC_2 * 7)));
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private void loadANCHistory() {
        AsyncAncInfoUpdate ancUpdate = new AsyncAncInfoUpdate(this,this);

        Log.d(LOGTAG, String.format("Loading ANC history for mother: %s", mother.getHealthId()));
        String queryString =  "";
        try {
            queryString = buildQueryHeader(true).toString();
        } catch (JSONException jse) {
            Utilities.printTrace(jse.getStackTrace());
        }
        ancUpdate.execute(queryString, SERVLET, ROOTKEY);
        //make history collapsible
        MethodUtils.makeHistoryCollapsible(getTextView(R.id.ancBlanLabelLabel),history_layout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_anc, menu);
        return true;
    }

    public void handleResponse(String result) {
        ll.removeAllViews(); //clear the history list first.

        Log.d(LOGTAG, "ANC-ANDROID response received:\n\t" + result);

        try {
            jsonStr = new JSONObject(result);
            lastAncVisit = (jsonStr.has("count") ? jsonStr.getInt("count") : 0 );
            getTextView(R.id.ancVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastAncVisit >= 0 ? lastAncVisit + 1 : 1)));
            Log.d(LOGTAG, "JSON Response:\n"+jsonStr.toString());

            //Check if eligible for new ANC
            if(jsonStr.has("ancStatus") && jsonStr.getBoolean("ancStatus")) {
                Utilities.MakeInvisible(this, R.id.ancEntryMasterLayout);
                //Utilities.Disable(this, R.id.ancEntryMasterLayout);
                Toast.makeText(this, getString(R.string.NewANCWarning),Toast.LENGTH_LONG).show();
            } else {
                Utilities.Enable(this, R.id.ancEntryMasterLayout);
                if(lastAncVisit > 0) {
                    showHideAncModifyButton(jsonStr);
                    lastServiceJSON = jsonStr.getJSONObject(String.valueOf(lastAncVisit));
                }
            }

            for (int i = 1; i <= lastAncVisit && lastAncVisit !=0 ; i++) {
                JSONObject singleVisitJson = jsonStr.getJSONObject(String.valueOf(i));
                displayList = new ArrayList<>();

                /*for(String orderedKey : historyLabelMap.keySet()) {
                    Log.d(LOGTAG, "Key ->" + orderedKey);

                    if(jsonEditTextDateMap.containsKey(orderedKey)) {
                        displayList.add(new DateValue(this, *//*context*//*
                                orderedKey, *//*jsonKey *//*
                                singleVisitJson.get(orderedKey).toString(), *//*value*//*
                                historyLabelMap.get(orderedKey).first, *//*label*//*
                                "yyyy-MM-dd", *//*db format*//*
                                "dd/MM/yyyy"));
                    }

                    if (jsonEditTextMap.containsKey(orderedKey)) {
                        if(orderedKey.equals("ancbpsys")) { *//*needed to handle blood pressure differently as it carries two values against one label*//*
                            Log.d(LOGTAG, String.format("Add [CTV] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));
                            displayList.add(new CompositValue(this, *//*context*//*
                                    orderedKey, *//*jsonKey *//*
                                    singleVisitJson.get(orderedKey).toString(), *//*value*//*
                                    historyLabelMap.get(orderedKey).first, *//*label*//*
                                    singleVisitJson.getString("ancbpdias"), *//*second value*//*
                                    "/", *//*connector string*//*
                                    historyLabelMap.get(orderedKey).second));
                        } else {
                            Log.d(LOGTAG, String.format("Add [TV] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));
                            displayList.add(new DisplayValue(this, *//*context*//*
                                    orderedKey, *//*jsonKey *//*
                                    singleVisitJson.get(orderedKey).toString(), *//*value*//*
                                    historyLabelMap.get(orderedKey).first *//*label*//*,
                                    historyLabelMap.get(orderedKey).second));
                        }
                    }
                    //}

                    Log.d(LOGTAG, String.format("Visit: %d, Size after adding [TV] to the list is %d", i, displayList.size()));

                    if (jsonSpinnerMap.containsKey(orderedKey)) {
                        Log.d(LOGTAG, String.format("Add [IV] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));
                        displayList.add(new IndexedValue(
                                this, *//*context*//*
                                orderedKey, *//*jsonKey *//*
                                singleVisitJson.get(orderedKey).toString(), *//*value*//*
                                historyLabelMap.get(orderedKey).first, *//*label*//*
                                historyLabelMap.get(orderedKey).second *//*Array Resource*//*
                        ));
                    }

                    if (jsonCheckboxMap.containsKey(orderedKey)) {
                        Log.d(LOGTAG, String.format("Add [BV] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));
                        displayList.add(new BooleanValue(
                                this, *//*context*//*
                                orderedKey, *//*jsonKey *//*
                                singleVisitJson.get(orderedKey).toString(), *//*value*//*
                                historyLabelMap.get(orderedKey).first
                        ));

                    }

                    Log.d(LOGTAG, String.format("Visit: %d, Size after adding [IV] to the list is %d", i, displayList.size()));

                    if (jsonMultiSpinnerMap.containsKey(orderedKey)) {
                        Log.d(LOGTAG, String.format("Add [MIV] %s: -> %s", historyLabelMap.get(orderedKey).first, singleVisitJson.get(orderedKey).toString()));
                        displayList.add(new MultiIndexedValue(
                                this, *//*context*//*
                                orderedKey, *//*jsonKey *//*
                                singleVisitJson.get(orderedKey).toString(), *//*value*//*
                                historyLabelMap.get(orderedKey).first, *//*label*//*
                                historyLabelMap.get(orderedKey).second *//*Array Resource*//*
                        ));
                    }
                }*/

                HistoryListMaker<ANCActivity> historyListMaker = new HistoryListMaker<>(
                        this, /*pill condom service activity*/
                        singleVisitJson, /*json containing keys*/
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

                listDataHeader.add(getString(R.string.visit) + Utilities.ConvertNumberToBangla(String.valueOf(i)));
                listDisplayValues.put(listDataHeader.get(0), displayList);

                displayListAdapter = new ExpandableDisplayListAdapter(this, listDataHeader, listDisplayValues, true);

                initPage();

                ll.addView(expListView);
                expListView.setScrollingCacheEnabled(true);
                expListView.setAdapter(displayListAdapter);

                //keep history visible and its child expandable
                for(int j=0; j < displayListAdapter.getGroupCount(); j++){
                    expListView.expandGroup(j);
                }
                if(history_layout.getVisibility()==View.GONE){
                    history_layout.setVisibility(View.VISIBLE);
                    getTextView(R.id.ancBlanLabelLabel).
                            setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                }

                ll.invalidate();
            }
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        } catch (Exception e) {
            Utilities.printTrace(e.getStackTrace(), 10);
        }
    }

    @Override
    public void callbackAsyncTask(String result) {
        handleResponse(result);
    }


    private void initPage() {
        expListView = new ExpandableListView(this);
        expListView.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        expListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView.setIndicatorBounds(0, 0);
        expListView.setChildIndicatorBounds(0, 0);
        expListView.setStackFromBottom(true);
    }


    void setItemVisible(int ItemId, boolean isChecked) {
        Spinner Item=(Spinner)findViewById(ItemId);
        Item.setSelection(0);
        findViewById(ItemId).setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (buttonView.getId() == R.id.ancReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            int layouts[] = {R.id.ancReferCenterNameLayout, R.id.ancReasonLayout};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],visibility);
            }
        }
    }

    // Added by Al Amin
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ancServiceDateValue:
            case R.id.Date_Picker_Button:
                datePickerDialog.show(datePickerPair.get(v.getId()));
                break;

            case R.id.ancSaveButton:
                if(!Validation.hasText(jsonEditTextDateMap.get("ancdate"))) {
                    Utilities.showAlertToast(this, getString(R.string.string_service_info_absent));
                    return;
                }
                if (!hasTheRequiredFileds()) {
                    return;
                }
                try {
                    String cVisitDate=jsonEditTextDateMap.get("ancdate").getText().toString();
                    if(lastAncVisit>0 && !Validation.isValidVisitDate(this,cVisitDate,lastServiceJSON.getString("ancdate"),false,true)){
                        return;
                    }else{
                        performSaveButtonOperations();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    performSaveButtonOperations();
                }
                break;

            case R.id.ancEditButton:
                if(countSaveClick == 1) {
                    countSaveClick = 0;
                    Utilities.Enable(this, R.id.ancEntryMasterLayout);
                    getButton(R.id.ancSaveButton).setText(editMode?getString(R.string.string_update):getString(R.string.string_save));
                    Utilities.MakeInvisible(this, R.id.ancEditButton);
                }
                break;

            case R.id.modifyLastAncButton:
                setLastServiceData();
                break;

            case R.id.ancCancelButton:
                cancelANCModification();
                break;

            case R.id.ancDeleteButton:
                deleteLastANC();
                break;

            default:
                break;
        }

    }

    private void performSaveButtonOperations(){
        ll.removeAllViews();
        countSaveClick++;
        if( countSaveClick == 2 ) {
            saveAnc();
            getButton(R.id.ancSaveButton).setText(R.string.string_save);
            Utilities.Enable(this, R.id.ancEntryMasterLayout);
            Utilities.MakeInvisible(this, R.id.ancEditButton);
            countSaveClick = 0;

        } else if( countSaveClick == 1 ) {
            if(!jsonEditTextMap.get("ancbpsys").equals("") || !jsonEditTextMap.get("ancbpdias").equals("")){
                try {
                    if((jsonEditTextMap.get("ancbpsys").getTag().equals(Flag.HIGHBP)||
                            jsonEditTextMap.get("ancbpdias").getTag().equals(Flag.HIGHBP)) && !jsonCheckboxMap.get("ancrefer").isChecked()){
                        AlertDialogCreator.SimpleDecisionDialog(this,
                                getString(R.string.anc_highbp_dialog),
                                getString(R.string.anc_highbp_dialog_title),android.R.drawable.ic_menu_help);
                        decisionFlag=REFERCHECK;
                    }else {
                        confirmBeforeSave();
                    }
                }catch (NullPointerException npe){
                    npe.printStackTrace();
                    confirmBeforeSave();
                }

            }else {
                confirmBeforeSave();
            }
        }
    }

    private void confirmBeforeSave(){
        Utilities.Disable(this, R.id.ancEntryMasterLayout);
        getButton( R.id.ancSaveButton).setText(R.string.string_confirm);
        Utilities.Enable(this, R.id.ancSaveButton);
        getButton( R.id.ancEditButton).setText(R.string.string_cancel);
        Utilities.Enable(this, R.id.ancEditButton);
        Utilities.MakeVisible(this, R.id.ancEditButton);

        Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(20);
        View view = this.getCurrentFocus();
        getButton( R.id.ancSaveButton).requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        toast.show();
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }


    @Override
    protected void initiateCheckboxes(){
        jsonCheckboxMap.put("ancrefer", getCheckbox(R.id.ancReferCheckBox));
    }

    @Override
    protected void initiateRadioGroups(){}


    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("ancedema",          getSpinner(R.id.ancEdemaSpinner));
        jsonSpinnerMap.put("ancfpresentation",  getSpinner(R.id.ancFetalPresentationSpinner));
        jsonSpinnerMap.put("ancanemia",         getSpinner(R.id.ancAnemiaSpinner));
        jsonSpinnerMap.put("ancjaundice",       getSpinner(R.id.ancJaundiceSpinner));
        jsonSpinnerMap.put("ancsugar",          getSpinner(R.id.ancUrineSugarSpinner));
        jsonSpinnerMap.put("ancalbumin",        getSpinner(R.id.ancUrineAlbuminSpinner));
        jsonSpinnerMap.put("anccentername",     getSpinner(R.id.ancReferCenterNameSpinner));
        jsonSpinnerMap.put("ancservicesource",  getSpinner(R.id.ancOtherCenterNameSpinner));
    }

    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("anccomplication",  getMultiSelectionSpinner(R.id.ancComplicationSpinner));
        jsonMultiSpinnerMap.put("ancsymptom",       getMultiSelectionSpinner(R.id.ancSymptomSpinner));
        jsonMultiSpinnerMap.put("ancdisease",       getMultiSelectionSpinner(R.id.ancDiseaseSpinner));
        jsonMultiSpinnerMap.put("anctreatment",     getMultiSelectionSpinner(R.id.ancTreatmentSpinner));
        jsonMultiSpinnerMap.put("ancadvice",        getMultiSelectionSpinner(R.id.ancAdviceSpinner));
        jsonMultiSpinnerMap.put("ancreferreason",   getMultiSelectionSpinner(R.id.ancReasonSpinner));
    }

    @Override
    protected void initiateEditTexts() {
        //anc visit
        jsonEditTextMap.put("ancbpsys",         getEditText(R.id.ancBloodPresserValueSystolic));
        jsonEditTextMap.put("ancbpdias",        getEditText(R.id.ancBloodPresserValueDiastolic));
        jsonEditTextMap.put("ancweight",        getEditText(R.id.ancWeightValue));
        jsonEditTextMap.put("ancuheight",       getEditText(R.id.ancUterusHeightValue));
        jsonEditTextMap.put("anchrate",         getEditText(R.id.ancHeartSpeedValue));
        jsonEditTextMap.put("anchemoglobin",    getEditText(R.id.ancHemoglobinValue));

        getEditText(R.id.ancBloodPresserValueSystolic).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.ancBloodPresserValueSystolic)));
        getEditText(R.id.ancBloodPresserValueSystolic).setOnFocusChangeListener(new CustomFocusChangeListener(this));
        getEditText(R.id.ancBloodPresserValueDiastolic).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.ancBloodPresserValueDiastolic)));
        getEditText(R.id.ancBloodPresserValueDiastolic).setOnFocusChangeListener(new CustomFocusChangeListener(this));
    }

    @Override
    protected void initiateTextViews() {
        getTextView(R.id.ancServiceDateLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.ancServiceDateLabel).getText().toString(), 0, 1));
        getTextView(R.id.ancReferCenterNameLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.ancReferCenterNameLabel).getText().toString(), 0, 1));
        getTextView(R.id.ancReasonLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.ancReasonLabel).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateEditTextDates() {
        // ANC Service Date
        jsonEditTextDateMap.put("ancdate", getEditText(R.id.ancServiceDateValue));
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void saveAnc() {
        JSONObject json;

        try {
            json = buildQueryHeader(false);

            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            //getEditTextTime(json);
            getSpecialCases(json);
            ancInfoUpdate = new AsyncAncInfoUpdate(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    handleResponse(result);
                }
            }, this);

            ancInfoUpdate.execute(json.toString(), SERVLET, ROOTKEY);
            Log.i("ANC", "Save Succeeded");
            Log.d("ANC", "JSON:\n" + json.toString());
            Utilities.Reset(this, R.id.ancText);
            getButton(R.id.ancDeleteButton).setVisibility(View.GONE);
            getButton(R.id.ancCancelButton).setVisibility(View.GONE);
            editMode = false;

        } catch (JSONException jse) {
            Log.e("ANC", "JSON Exception: " + jse.getMessage());
        }
    }

    public void getSpecialCases(JSONObject json) {
        try {
            json.put("ancsatelitecentername", provider.getSatelliteName()); //If the service was given from satellite
            if(jsonSpinnerMap.get("ancservicesource").getVisibility() != View.VISIBLE) {
                json.put("ancservicesource", "");
            }
            if(editMode){
                json.put("serviceId",serviceId);
            }
        } catch (JSONException jse) {

        }
    }

    private void showHideAncModifyButton(JSONObject jso) {
        Utilities.SetVisibility(this, R.id.modifyLastAncButton, isLastAncModifiable(jso) ? View.VISIBLE :View.INVISIBLE);
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
                (isRetrieval ? "": "providerid:\""+String.valueOf(provider.getProviderCode())+"\",") +
                "pregNo:\"" + String.valueOf(mother.getPregNo()) + "\"," +
                "ancLoad:" + (isRetrieval? "retrieve":(editMode?"update":"\"\"")) +
                "}";

        return new JSONObject(queryString);
    }

    private boolean isLastAncModifiable(JSONObject jso) {
        String lastAncKey = String.valueOf(lastAncVisit);

        JSONObject lastVisit = null;
        String providerCode = null;
        boolean thresholdPeriodPassed = false;
        try {
            lastVisit = jso.getJSONObject(lastAncKey);
            if(lastVisit.has("systemEntryDate") && !lastVisit.getString("systemEntryDate").equals("")){
                Date lastVisitEntryDate = Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,lastVisit.getString("systemEntryDate"));
                thresholdPeriodPassed = Utilities.getDateDiff(lastVisitEntryDate,new Date(), TimeUnit.DAYS)>Flag.UPDATE_THRESHOLD;
            }

            providerCode = lastVisit.getString("providerid");
            Log.d(LOGTAG,"Last Service "+lastAncKey+" was provide by: \t" + providerCode);

            return (provider.getProviderCode().equals(providerCode) && !thresholdPeriodPassed);

        } catch (JSONException jse) {
            Log.e(LOGTAG, String.format(" JSON Error (ANC History): %s", jse.getMessage()));
            Utilities.printTrace(jse.getStackTrace(), 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void deleteConfirmed() {
        try {

            JSONObject deleteJson = buildQueryHeader(false);
            deleteJson.put("ancLoad", "delete");
            ancInfoUpdate = new AsyncAncInfoUpdate(this, this);
            ancInfoUpdate.execute(deleteJson.toString(), SERVLET, ROOTKEY);

            Utilities.Reset(this, R.id.ancText);
            getButton(R.id.ancSaveButton).setText(R.string.string_save);
            getButton(R.id.ancDeleteButton).setVisibility(View.GONE);
            getButton(R.id.ancCancelButton).setVisibility(View.GONE);
            editMode = false;
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete ANC request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    void setHistoryLabelMapping() {
        //The array primarily contains 3 things, the string array from resource id, lower limit, upper limit
        historyLabelMap.put("ancdate",  Pair.create(getString(R.string.date), new Integer[] {0}));
        historyLabelMap.put("ancsymptom",Pair.create(getString(R.string.drawback), new Integer[] {R.array.ANC_Symptom_Dropdown, 0, 0}));
        historyLabelMap.put("ancbpsys", Pair.create(getString(R.string.blood_presser), new Integer[] {0, 139, 89}));
        historyLabelMap.put("ancedema", Pair.create(getString(R.string.edema), new Integer[] {R.array.Jaundice_Edima_Dropdown, 3, 4}));
        historyLabelMap.put("ancweight",Pair.create(getString(R.string.weight),  new Integer[] {0}));
        historyLabelMap.put("ancuheight",Pair.create(getString(R.string.uterus_height),  new Integer[] {0}));
        historyLabelMap.put("anchrate",Pair.create(getString(R.string.heart_speed), new Integer[] {0, 120, 160}));
        historyLabelMap.put("ancfpresentation", Pair.create(getString(R.string.fetal_presentation), new Integer[] {R.array.Abnormality_Dropdown, 1, 2}));
        historyLabelMap.put("ancanemia", Pair.create(getString(R.string.anemia), new Integer[] {R.array.Anemia_Dropdown, 2, 3}));
        historyLabelMap.put("anchemoglobin",Pair.create(getString(R.string.hemoglobin), new Integer[] {0, 20, 100}));
        historyLabelMap.put("ancjaundice",Pair.create(getString(R.string.jaundice), new Integer[] {R.array.Jaundice_Edima_Dropdown, 1, 4}));
        historyLabelMap.put("ancsugar", Pair.create(getString(R.string.urine_test_sugar), new Integer[]{R.array.Urine_Test_Dropdown,4, 5}));
        historyLabelMap.put("ancalbumin",Pair.create(getString(R.string.urine_test_albumin), new Integer[]{R.array.Urine_Test_Dropdown,4, 5}));
        historyLabelMap.put("anccomplication", Pair.create(getString(R.string.complication), new Integer[] {R.array.ANC_Complication_DropDown, 0, 0}));
        historyLabelMap.put("ancdisease",Pair.create(getString(R.string.disease), new Integer[] {R.array.ANC_Disease_DropDown, 0, 0}));
        historyLabelMap.put("anctreatment",Pair.create(getString(R.string.treatment), new Integer[] {R.array.ANC_Treatment_DropDown, 0, 0}));
        historyLabelMap.put("ancadvice", Pair.create(getString(R.string.advice), new Integer[] {R.array.ANC_Advice_DropDown, 0, 0}));
        historyLabelMap.put("ancservicesource",Pair.create(getString(R.string.service_center_name), new Integer[] {R.array.FacilityType_DropDown, 0, 0})); //Check
        historyLabelMap.put("ancrefer",Pair.create(getString(R.string.refer), new Integer[] {0}));
        historyLabelMap.put("anccentername", Pair.create(getString(R.string.history_c_name), new Integer[] {R.array.FacilityType_DropDown, 0, 0}));
        historyLabelMap.put("ancreferreason",Pair.create(getString(R.string.referReason), new Integer[] {R.array.ANC_Refer_Reason_DropDown, 0, 0}));
    }

    private void deleteLastANC() {
        AlertDialogCreator.SimpleDecisionDialog(ANCActivity.this,getString(R.string.ServiceDeletionWarning),
                "Delete Service?",android.R.drawable.ic_delete);
        decisionFlag=DELETE;
    }

    private void cancelANCModification(){
        Utilities.Reset(this, R.id.ancText);
        getTextView(R.id.ancVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastAncVisit >= 0 ? lastAncVisit + 1 : 1)));
        getButton(R.id.ancSaveButton).setText(R.string.string_save);
        getButton(R.id.ancDeleteButton).setVisibility(View.GONE);
        getButton(R.id.ancCancelButton).setVisibility(View.GONE);
        editMode = false;
    }

    private void setLastServiceData(){
        Utilities.setEditTexts(jsonEditTextMap,lastServiceJSON);
        Utilities.setEditTextDates(jsonEditTextDateMap,lastServiceJSON);
        try {
            serviceId = lastServiceJSON.getInt("serviceid");
            if(lastServiceJSON.getString("ancservicesource").equals("")){
                getCheckbox(R.id.ancOtherCheckBox).setChecked(false);
            }else {
                getCheckbox(R.id.ancOtherCheckBox).setChecked(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            serviceId = 0;
        }
        Utilities.setSpinners(jsonSpinnerMap,lastServiceJSON);
        Utilities.setCheckboxes(jsonCheckboxMap,lastServiceJSON);
        Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap,lastServiceJSON);
        Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap,lastServiceJSON);


        getTextView(R.id.ancVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastAncVisit)));


        getButton(R.id.ancSaveButton).setText(R.string.string_update);
        getButton(R.id.ancDeleteButton).setVisibility(View.VISIBLE);
        getButton(R.id.ancCancelButton).setVisibility(View.VISIBLE);
        Utilities.Disable(this,R.id.ancServiceDate);
        editMode = true;
    }

    @Override
    public void onPositiveButtonClicked(DialogInterface dialog) {
        switch (decisionFlag){
            case DELETE:
                dialog.cancel();
                deleteConfirmed();
                decisionFlag=0;
                break;

            case REFERCHECK:
                countSaveClick = 0;
            default:
                dialog.cancel();
                decisionFlag=0;
                break;
        }

    }

    @Override
    public void onNegativeButtonClicked(DialogInterface dialog) {
        switch (decisionFlag){

            case REFERCHECK:
                confirmBeforeSave();
                dialog.cancel();
                decisionFlag=0;
                break;

            case DELETE:
            default:
                dialog.cancel();
                decisionFlag=0;
                break;
        }
    }

    @Override
    public void onNeutralButtonClicked(DialogInterface dialog) {
        dialog.cancel();
    }

    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialog(ANCActivity.this,"গর্ভকালীন সেবা ( ANC )");

    }

    private boolean hasTheRequiredFileds() {
        boolean valid = true;
        boolean specialInvalid = false;

        if (getCheckbox(R.id.ancReferCheckBox).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.ancReferCenterNameSpinner)))
                specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.ancReasonSpinner)))
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
            MethodUtils.showSnackBar(findViewById(R.id.anc_layout_main), getResources().getString(R.string.refer_validation_message), true);
            return false;
        }
        else {
            return true;
        }

    }

}