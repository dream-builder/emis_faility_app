package org.sci.rhis.fwc;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncIUDInfoUpate;
import org.sci.rhis.connectivityhandler.AsyncInjectablesInfoUpdate;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomFocusChangeListener;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by arafat.hasan on 2/4/2016.
 */
public class InjectableActivity extends ClinicalServiceActivity implements CompoundButton.OnCheckedChangeListener,
                                                                AdapterView.OnItemSelectedListener,
                                                                CustomTextWatcher.CustomWatcherListner,
                                                                AlertDialogCreator.DialogButtonClickedListener{

    //Variable declarations.................................................................................

    private Context mContext;
    private ProviderInfo provider;

    final private String SERVLET = "womaninjectable";
    final private String ROOTKEY = "injectableInfo";
    private  final String LOGTAG    = "FP-INJECTION";

    String healthId,providerId,mobileNo,sateliteCenterName;

    private int lastInjectableVisit;
    private String dueDoseDate="";
    private boolean editMode = false;
    private JSONObject lastServiceJSON;

    LinearLayout historyContentLayout;
    LinearLayout history_layout;

    private int countSaveClick=0, doseId=0;

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    AsyncInjectablesInfoUpdate injectablesInfoAsyncUpdate;

    private MultiSelectionSpinner multiSelectionSpinner;

    private JSONObject jsonResponse = null;
    boolean isCollapsed = false;

    ExpandableListView expandableListViewInjectable;
    ExpandableListAdapterForInjectable injectableHistoryAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    //......................................................................................................

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_injectible_service);
        initialInit();
    }

    private void initialInit(){
        mContext =this;

        lastInjectableVisit=0;

        provider = getIntent().getParcelableExtra(Constants.KEY_PROVIDER);

        getTextView(R.id.injectableVisitValueTextView)
                .setText(Utilities.ConvertNumberToBangla(String.valueOf(lastInjectableVisit >= 0 ? lastInjectableVisit + 1 : 1)));
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH);
        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.injectableActualDatePickerButton, (EditText) findViewById(R.id.injectableActualDateEditText));
        datePickerPair.put(R.id.injectableLmpDatePickerButton, (EditText) findViewById(R.id.injectableLmpDateEditText));

        initialize();

        initiateEditTexts();
        initiateSpinners();
        initiateMultiSelectionSpinners();
        initiateCustomMultiSelectionSpinners();
        initiateCheckboxes();
        getCheckbox(R.id.injectableReferCheckBox).setOnCheckedChangeListener(this);
        initiateTextViews();

        setPredefinedValues();

        setHistoryLabelMapping();
        setCompositeMap("bpSystolic", "bpDiastolic");

        loadHistory();

    }

    private void initiateCustomMultiSelectionSpinners(){
        final List<String> sideEffectList = Arrays.asList(getResources().getStringArray(R.array.Injectable_Side_Effect_DropDown));
        final List<String> treatmentList = Arrays.asList(getResources().getStringArray(R.array.Injectable_Treatment_DropDown));
        final List<String> adviceList = Arrays.asList(getResources().getStringArray(R.array.Injectable_Advice_DropDown));
        final List<String> referReasonList = Arrays.asList(getResources().getStringArray(R.array.Injectable_Refer_Reason_DropDown));


        multiSelectionSpinner = getMultiSelectionSpinner(R.id.injectableSideEffectSpinner);
        multiSelectionSpinner.setItems(sideEffectList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.injectableTreatmentSpinner);
        multiSelectionSpinner.setItems(treatmentList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.injectableAdviceSpinner);
        multiSelectionSpinner.setItems(adviceList);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = getMultiSelectionSpinner(R.id.injectableChildReasonSpinner);
        multiSelectionSpinner.setItems(referReasonList);
        multiSelectionSpinner.setSelection(new int[]{});
    }

    private void setPredefinedValues(){
        healthId  = getIntent().getStringExtra(Constants.KEY_HEALTH_ID);
        providerId = String.valueOf(provider.getProviderCode());
        mobileNo = getIntent().getStringExtra(Constants.KEY_MOBILE);
        sateliteCenterName = provider.getSatelliteName();

        if(getIntent().hasExtra(Constants.KEY_FP_EXAMINATION)){
            try {
                JSONObject examJson = new JSONObject(getIntent().getStringExtra(Constants.KEY_FP_EXAMINATION));
                setExamValues(examJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if(getIntent().hasExtra(Constants.KEY_IS_NEW)){
            setNewClientLayout(getIntent().getBooleanExtra(Constants.KEY_IS_NEW,false));
        }
    }

    private void showHidePhysicalExam(int visibility){
        findViewById(R.id.injectableWeightLayout).setVisibility(visibility);
        findViewById(R.id.injectablePulseLayout).setVisibility(visibility);
        findViewById(R.id.injectableBPLayout).setVisibility(visibility);
        findViewById(R.id.injectableBreastConditionLayout).setVisibility(visibility);
        findViewById(R.id.injectablePVSegment).setVisibility(visibility);
        findViewById(R.id.injectableLmpDateLayout).setVisibility(visibility);
    }

    private void setNewClientLayout(boolean isNew){
        if(isNew){
            getRadioButton(R.id.injectableIsNewRadioButton).setChecked(true);
            getEditText(R.id.injectableActualDateEditText).setText(Converter.dateToString(Constants.SHORT_SLASH_FORMAT_BRITISH,new Date()));
            Utilities.MakeInvisible(this,R.id.injectableDueDateLayout);
            getSpinner(R.id.injectableInjectionNameSpinner).setSelection(1);
            if(getIntent().hasExtra(Constants.KEY_FP_LMP_DATE)){
                getEditText(R.id.injectableLmpDateEditText).setText(getIntent().getStringExtra(Constants.KEY_FP_LMP_DATE));
            }
            findViewById(R.id.injectableSideEffectLayout).setVisibility(View.GONE);
            findViewById(R.id.injectableTreatmentLayout).setVisibility(View.GONE);
            findViewById(R.id.injectableAdviceLayout).setVisibility(View.GONE);
            findViewById(R.id.injectableAdviceLayout).setVisibility(View.GONE);
            showHidePhysicalExam(View.VISIBLE);
            isCollapsed = false;
        }else{
            getRadioButton(R.id.injectableIsOldRadioButton).setChecked(true);
            showHidePhysicalExam(View.GONE);
            isCollapsed = true;
        }
    }

    private void setExamValues(JSONObject json){
        Utilities.setSpinners(jsonSpinnerMap,json);
        Utilities.setEditTexts(jsonEditTextMap,json);
    }

    private void loadHistory(){
        historyContentLayout = (LinearLayout)findViewById(R.id.injectableFragmentHeaderLayout);
        history_layout = (LinearLayout)findViewById(R.id.history_lay_injectable);
        expandableListViewInjectable = new ExpandableListView(this);

        JSONObject json;
        try {
            json = buildQueryHeader(true);
            injectablesInfoAsyncUpdate = new AsyncInjectablesInfoUpdate(this, mContext);
            injectablesInfoAsyncUpdate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("Injectable Exception: ", jse.getMessage());
        }
        MethodUtils.makeHistoryCollapsible(getTextView(R.id.injectableBlancLabelLabel),history_layout);
    }

    @Override
    public void callbackAsyncTask(String result) {
        /*{
            Utilities.Disable(this,R.id.injectableIsNewClientLayout);
            try {
                jsonResponse = new JSONObject(result);

                showSerialNumber(jsonResponse);

                lastInjectableVisit=jsonResponse.getInt("count");
                getTextView(R.id.injectableVisitValueTextView)
                        .setText(Utilities.ConvertNumberToBangla(String.valueOf(lastInjectableVisit >= 0 ? lastInjectableVisit + 1 : 1)));
                if(lastInjectableVisit>0){
                    showHideInjectableModifyButton(jsonResponse);
                    lastServiceJSON = jsonResponse.getJSONObject(String.valueOf(lastInjectableVisit));
                }else{
                    //hide delete button if all the history is cleared by the provider
                    getButton(R.id.modifyLastInjectableButton).setVisibility(View.INVISIBLE);
                }

                int in=1;
                historyContentLayout.removeAllViews();

                for (Iterator<String> ii = jsonResponse.keys(); ii.hasNext(); ) {
                    ii.next();
                    if(in > jsonResponse.getInt("count"))
                        break;

                    JSONObject jsonRootObject = jsonResponse.getJSONObject("" + in);

                    String doseDate = jsonRootObject.getString("doseDate");
                    String isNewClient = jsonRootObject.getString("isNewClient");
                    String LMP = jsonRootObject.getString("LMP");
                    String weight = jsonRootObject.getString("weight");
                    String pulse = jsonRootObject.getString("pulse");
                    String bpSystolic = jsonRootObject.getString("bpSystolic");
                    String bpDiastolic = jsonRootObject.getString("bpDiastolic");
                    int breastConditionVal = Integer.valueOf(jsonRootObject.getString("breastCondition").equals("")?"0":jsonRootObject.getString("breastCondition"));
                    String breastCondition = getResources()
                            .getStringArray(R.array.Injectable_Breast_Condition_DropDown)[breastConditionVal];
                    int uterusShapeVal = Integer.valueOf(jsonRootObject.getString("uterusShape").equals("")?"0":jsonRootObject.getString("uterusShape"));
                    String uterusShape = getResources()
                            .getStringArray(R.array.Uterus_Shape_DropDown)[uterusShapeVal];
                    int uterusPositionVal = Integer.valueOf(jsonRootObject.getString("uterusPosition").equals("")?"0":jsonRootObject.getString("uterusPosition"));
                    String uterusPosition = getResources()
                            .getStringArray(R.array.Uterus_Position_DropDown)[uterusPositionVal];
                    int uterusMovementVal = Integer.valueOf(jsonRootObject.getString("uterusMovement").equals("")?"0":jsonRootObject.getString("uterusMovement"));
                    String uterusMovement = getResources()
                            .getStringArray(R.array.Uterus_Movement_DropDown)[uterusMovementVal];
                    int uterusMovementPainVal = Integer.valueOf(jsonRootObject.getString("uterusCervixMovePain").equals("")?"0":jsonRootObject.getString("uterusCervixMovePain"));
                    String uterusMovementPain = getResources()
                            .getStringArray(R.array.Uterus_Movement_Pain_DropDown)[uterusMovementPainVal];
                    int injectionVal = Integer.valueOf(jsonRootObject.getString("injectionId").equals("")?"0":jsonRootObject.getString("injectionId"));
                    String injection = getResources()
                            .getStringArray(R.array.Injection_Name_DropDown)[injectionVal];
                    String sideEffect = jsonRootObject.getString("sideEffect");
                    sideEffect.replaceAll("(\"|\\[|\\])", "");
                    sideEffect.trim();
                    String treatment = jsonRootObject.getString("treatment");
                    treatment.replaceAll("(\"|\\[|\\])", "");//remove all non digit characters.......
                    treatment.trim();
                    String advice = jsonRootObject.getString("advice");
                    advice.replaceAll("(\"|\\[|\\])", "");//remove all non digit characters.......
                    advice.trim();
                    int referVal = Integer.valueOf(jsonRootObject.getString("refer").equals("")?"0":jsonRootObject.getString("refer"));
                    int referCenterVal = Integer.valueOf(jsonRootObject.getString("referCenterName").equals("")?"0":jsonRootObject.getString("referCenterName"));
                    String referCenter = getResources()
                            .getStringArray(R.array.FacilityType_DropDown)[referCenterVal];
                    String referReason = jsonRootObject.getString("referReason");
                    referReason.replaceAll("(\"|\\[|\\])", "");//remove all non digit characters.......
                    referReason.trim();


                    ArrayList<String> list = new ArrayList<String>();

                    if(isNewClient.trim().equalsIgnoreCase("1"))
                        list.add("" + getString(R.string.client_status) + getString(R.string.new_client));
                    else
                        list.add("" + getString(R.string.client_status) + getString(R.string.old_client));

                    if(in!=1 && !isNewClient.trim().equalsIgnoreCase("1")){
                        list.add("" + getString(R.string.injection_due_dose_date_text) + " \n" + dueDoseDate);

                    }else{
                        list.add("" + getString(R.string.injection_due_dose_date_text) + " নেই");
                    }
                    dueDoseDate = manipulateDoseDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,doseDate);
                    list.add("" + getString(R.string.injection_actual_dose_date_text) + " \n" + doseDate);
                    list.add("" + getString(R.string.LMPDateNormal) + " " + LMP);
                    list.add("" + getString(R.string.weight) + " " + weight);
                    list.add("" + getString(R.string.pulse) + " " + pulse);
                    list.add("" + getString(R.string.blood_presser) + " \n" + bpSystolic+"/"+bpDiastolic);
                    list.add("" + getString(R.string.breast_condition) + " " + breastCondition);
                    list.add("" + getString(R.string.uterus_size) + " " + uterusShape);
                    list.add("" + getString(R.string.uterus_position) + " " + uterusPosition);
                    list.add("" + getString(R.string.uterus_movement) + " " + uterusMovement);
                    list.add("" + getString(R.string.uterus_movement_pain) + " " + uterusMovementPain);
                    list.add("" + getString(R.string.injection_name_text) + " " + injection);
                    list.add(Constants.SIDE_EFFECT_TAG+sideEffect);
                    list.add(Constants.TREATMENT_TAG+treatment);
                    list.add(Constants.ADVICE_TAG+advice);
                    if(referVal == 1){
                        list.add("" + getString(R.string.refer) + " " + "হ্যাঁ");
                    }else{
                        list.add("" + getString(R.string.refer) + " " + "না");
                    }
                    list.add("" + getString(R.string.referCenterName) + " " + referCenter);
                    list.add(Constants.REFER_REASON_TAG+referReason);

                    try {
                        listDataHeader = new ArrayList<String>();
                        listDataChild = new HashMap<String, List<String>>();

                        listDataHeader.add(getString(R.string.str_visit) + Utilities.ConvertNumberToBangla(String.valueOf(in)) + ":");
                        listDataChild.put(listDataHeader.get(0), list);
                        injectableHistoryAdapter = new ExpandableListAdapterForInjectable(mContext, listDataHeader, listDataChild);
                        in++;

                        initPage();

                        historyContentLayout.addView(expandableListViewInjectable);
                        expandableListViewInjectable.setScrollingCacheEnabled(true);
                        expandableListViewInjectable.setAdapter(injectableHistoryAdapter);

                        //keep history visible and its child expandable
                        for(int j=0; j < injectableHistoryAdapter.getGroupCount(); j++){
                            expandableListViewInjectable.expandGroup(j);
                        }
                        if(history_layout.getVisibility()==View.GONE){
                            history_layout.setVisibility(View.VISIBLE);
                            getTextView(R.id.injectableBlancLabelLabel).
                                    setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                        }
                        //...........................................................................
                        historyContentLayout.invalidate();

                    } catch (Exception e) {
                        Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                        e.printStackTrace();
                    }
                }
                String manipulatedDate="";
                if(!dueDoseDate.equals("")){
                    try {
                        manipulatedDate = Converter
                                .convertSdfFormat(Constants.SHORT_HYPHEN_FORMAT_DATABASE, dueDoseDate, Constants.SHORT_SLASH_FORMAT_BRITISH);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                getTextView(R.id.injectableDueDateValueTextView).setText(manipulatedDate);

                if(lastInjectableVisit!=0){
                    getTextView(R.id.injectableNextDateValueTextView).setText("");
                }

            } catch (JSONException jse) {
                System.out.println("JSON Exception Thrown::\n ");
                jse.printStackTrace();
            }

        }*/

        Utilities.Disable(this, R.id.injectableIsNewClientLayout);
        historyContentLayout.removeAllViews(); //clear the history list first.

        try {
            jsonResponse = new JSONObject(result);

            lastInjectableVisit = (jsonResponse.has("count") ? jsonResponse.getInt("count") : 0 );
            if(lastInjectableVisit > 0) {
                showHideInjectableModifyButton(jsonResponse);
                lastServiceJSON = jsonResponse.getJSONObject(String.valueOf(lastInjectableVisit));
            } else {
                //hide modify button if all the history is cleared by the provider
                Utilities.MakeInvisible(this, R.id.modifyLastInjectableButton);
            }

            getTextView(R.id.injectableVisitValueTextView).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastInjectableVisit >= 0 ? lastInjectableVisit + 1 : 1)));

            Log.d(LOGTAG, "JSON Response:\n" + jsonResponse.toString());

            //Utilities.Enable(this, R.id.injectableEntryMasterLayout);

            showSerialNumber(jsonResponse);

            for (int i = 1; i <= lastInjectableVisit && lastInjectableVisit !=0 ; i++) {
                JSONObject singleVisitJson = jsonResponse.getJSONObject(String.valueOf(i));
                displayList = new ArrayList<>();

                if(i != 1 && !singleVisitJson.getString("isNewClient").trim().equalsIgnoreCase("1")){
                    singleVisitJson.put("dueDoseDate", dueDoseDate);
                }else{
                    singleVisitJson.put("dueDoseDate", "নেই");
                }
                dueDoseDate = manipulateDoseDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE, singleVisitJson.getString("doseDate"));

                HistoryListMaker<InjectableActivity> historyListMaker = new HistoryListMaker<>(
                        this, /*activity*/
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

                ExpandableDisplayListAdapter displayListAdapter = new ExpandableDisplayListAdapter(this, listDataHeader, listDisplayValues, true);

                initPage();

                historyContentLayout.addView(expandableListViewInjectable);
                expandableListViewInjectable.setScrollingCacheEnabled(true);
                expandableListViewInjectable.setAdapter(displayListAdapter);

                //keep history visible and its child expandable
                for(int j=0; j < displayListAdapter.getGroupCount(); j++){
                    expandableListViewInjectable.expandGroup(j);
                }
                if(history_layout.getVisibility()==View.GONE){
                    history_layout.setVisibility(View.VISIBLE);
                    getTextView(R.id.injectableBlancLabelLabel).
                            setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                }
                //...........................................................................

                historyContentLayout.invalidate();
            }
            String manipulatedDate="";
            if(!dueDoseDate.equals("")){
                try {
                    manipulatedDate = Converter
                            .convertSdfFormat(Constants.SHORT_HYPHEN_FORMAT_DATABASE, dueDoseDate, Constants.SHORT_SLASH_FORMAT_BRITISH);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            getTextView(R.id.injectableDueDateValueTextView).setText(manipulatedDate);

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        } catch (Exception e) {
            Utilities.printTrace(e.getStackTrace(), 10);
        }
    }

    private void initPage() {
        expandableListViewInjectable = new ExpandableListView(this);
        expandableListViewInjectable.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        expandableListViewInjectable.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expandableListViewInjectable.setIndicatorBounds(0, 0);
        expandableListViewInjectable.setChildIndicatorBounds(0, 0);
        expandableListViewInjectable.setStackFromBottom(true);
    }

    @Override
    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("refer",getCheckbox(R.id.injectableReferCheckBox));
    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("weight",getEditText(R.id.injectableWeightEditText));
        jsonEditTextMap.put("pulse",getEditText(R.id.injectablePulseEditText));
        jsonEditTextMap.put("bpSystolic",getEditText(R.id.injectableBPSystolicEditText));
        jsonEditTextMap.put("bpDiastolic", getEditText(R.id.injectableBPDiastolicEditText));

        getEditText(R.id.injectableBPSystolicEditText).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.injectableBPSystolicEditText)));
        getEditText(R.id.injectableBPSystolicEditText).setOnFocusChangeListener(new CustomFocusChangeListener(this));
        getEditText(R.id.injectableBPDiastolicEditText).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.injectableBPDiastolicEditText)));
        getEditText(R.id.injectableBPDiastolicEditText).setOnFocusChangeListener(new CustomFocusChangeListener(this));


    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewMapChild.put("dueDoseDate", getTextView(R.id.injectableDueDateValueTextView));

        getTextView(R.id.injectableActualDateTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.injectableActualDateTextView).getText().toString(), 0, 1));
        getTextView(R.id.injectabledReferCenterNameTextView).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.injectabledReferCenterNameTextView).getText().toString(), 0, 1));
        getTextView(R.id.injectableChildReasonLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.injectableChildReasonLabel).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateSpinners() {
        //json mapping for spinner values
        jsonSpinnerMap.put("breastCondition",getSpinner(R.id.injectableBreastConditionSpinner));
        jsonSpinnerMap.put("uterusShape",getSpinner(R.id.injectableUterusShapeSpinner));
        jsonSpinnerMap.put("uterusPosition", getSpinner(R.id.injectableUterusPositionSpinner));
        jsonSpinnerMap.put("uterusMovement", getSpinner(R.id.injectableUterusMovementSpinner));
        jsonSpinnerMap.put("uterusCervixMovePain", getSpinner(R.id.injectableUterusMovementPainSpinner));
        jsonSpinnerMap.put("injectionId", getSpinner(R.id.injectableInjectionNameSpinner));
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.injectableReferCenterNameSpinner));

    }

    @Override
    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("sideEffect", getMultiSelectionSpinner(R.id.injectableSideEffectSpinner));
        jsonMultiSpinnerMap.put("treatment", getMultiSelectionSpinner(R.id.injectableTreatmentSpinner));
        jsonMultiSpinnerMap.put("advice", getMultiSelectionSpinner(R.id.injectableAdviceSpinner));
        jsonMultiSpinnerMap.put("referReason", getMultiSelectionSpinner(R.id.injectableChildReasonSpinner));

    }

    @Override
    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("doseDate",getEditText(R.id.injectableActualDateEditText));
        jsonEditTextDateMap.put("LMP", getEditText(R.id.injectableLmpDateEditText));

        //change the next dose text view according to current dose date
        getEditText(R.id.injectableActualDateEditText).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.injectableActualDateEditText)));
    }

    @Override
    protected void initiateRadioGroups() {
        jsonRadioGroupButtonMap.put("isNewClient", Pair.create(
                getRadioGroup(R.id.injectableIsNewRadioGroup), Pair.create(
                        getRadioButton(R.id.injectableIsNewRadioButton),
                        getRadioButton(R.id.injectableIsOldRadioButton)
                )
                )
        );
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.injectableSaveButton:
                try {
                    String currDoseDate=jsonEditTextDateMap.get("doseDate").getText().toString();

                    if (!hasTheRequiredFields()) {
                        return;
                    }

                    if(!editMode && lastInjectableVisit>0 ){
                        boolean isOld = getRadioButton(R.id.injectableIsOldRadioButton).isChecked();
                        String prevDate = lastServiceJSON.getString("doseDate");
                        Date recommendedDate = Utilities.manipulateDateByMonth(Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,prevDate),3,Constants.ADD_CODE);
                        Date lastValidDoseDate = Utilities.addDateOffset(recommendedDate,28);
                        if(!Validation.isValidVisitDate(this,currDoseDate,prevDate,true,false)){
                            Utilities.showAlertToast(this,getString(R.string.str_toast_not_applicable));
                        }else if(isOld && Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,currDoseDate).after(lastValidDoseDate)){
                            AlertDialogCreator.SimpleMessageDialog(this,"নির্ধারিত ডিউ ডোজের তারিখের থেকেও ২৮ দিন বেশি হয়ে  গিয়েছে।" +
                                    " এ অবস্থায় এই গ্রহীতাকে ইঞ্জেক্টেবল  সেবার তথ্য দিতে হলে স্ক্রীনিং করে নতুন গ্রহীতা হিসেবে  সেবার তথ্য দিতে হবে। অথবা নিশ্চিত হন, এই গ্রহীতা আপনার প্রদানকৃত তারিখের" +
                                    " পূর্বে আর কোন ডোজ নিয়েছেন কিনা, যার তথ্য সিস্টেমে এন্ট্রি করা হয়নি।","নিশ্চিত হন",android.R.drawable.ic_dialog_alert);
                        }else{
                            saveInjectableInfo();
                        }

                    }else{
                        saveInjectableInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    saveInjectableInfo();
                } catch (ParseException pe){
                    pe.printStackTrace();
                    saveInjectableInfo();
                }

                break;
            case R.id.injectableEditButton:
                editInjectableInfo();
                break;

            case R.id.modifyLastInjectableButton:
                setLastServiceData();
                break;

            case R.id.injectableDeleteButton:
                deleteLastInjectable();
                break;

            case R.id.injectableEditCancelButton:
                resetActivity(false);
                break;

            case R.id.injectableCollapsibleButton:
                if(isCollapsed){
                    showHidePhysicalExam(View.VISIBLE);
                    isCollapsed = false;
                }else{
                    showHidePhysicalExam(View.GONE);
                    isCollapsed = true;
                }
                break;
        }
    }

    private void resetActivity(boolean afterSave){
        editMode=false;
        getTextView(R.id.injectableVisitValueTextView)
                .setText(Utilities.ConvertNumberToBangla(String.valueOf(lastInjectableVisit >= 0 ? lastInjectableVisit + 1 : 1)));
        Utilities.Reset(this, R.id.injectableTextLayout);
        if(afterSave || lastInjectableVisit>0){
            Utilities.MakeVisible(this,R.id.injectableDueDateLayout);
            getRadioButton(R.id.injectableIsOldRadioButton).setChecked(true);
        }else{
            getRadioButton(R.id.injectableIsNewRadioButton).setChecked(true);
        }
        Utilities.Disable(this,R.id.injectableIsNewClientLayout);


        getButton(R.id.injectableSaveButton).setText(getText(R.string.string_save));
        getButton(R.id.injectableDeleteButton).setVisibility(View.GONE);
        getButton(R.id.injectableEditCancelButton).setVisibility(View.GONE);

    }

    private void setLastServiceData(){
        editMode = true;
        Utilities.setEditTexts(jsonEditTextMap,lastServiceJSON);
        Utilities.setEditTextDates(jsonEditTextDateMap,lastServiceJSON);
        Utilities.setSpinners(jsonSpinnerMap,lastServiceJSON);
        Utilities.setCheckboxes(jsonCheckboxMap,lastServiceJSON);
        Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap,lastServiceJSON);
        Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap,lastServiceJSON);
        try {
            doseId = lastServiceJSON.getInt("doseId");
        } catch (JSONException e) {
            e.printStackTrace();
            doseId = 0;
        }
        getTextView(R.id.injectableVisitValueTextView).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastInjectableVisit)));
        getButton(R.id.injectableSaveButton).setText(getText(R.string.string_update));
        getButton(R.id.injectableDeleteButton).setVisibility(View.VISIBLE);
        getButton(R.id.injectableEditCancelButton).setVisibility(View.VISIBLE);
        Utilities.Disable(this,R.id.injectableActualDateEditText);
    }

    private void saveInjectableInfo(){
        countSaveClick++;
        if( countSaveClick == 2 ) {
            injectableSaveAsJson();
            getButton(R.id.injectableSaveButton).setText(getText(R.string.string_save));
            Utilities.Enable(this, R.id.injectableEntryMasterLayout);
            Utilities.MakeInvisible(this, R.id.injectableEditButton);
            countSaveClick = 0;
        }

        else if(countSaveClick == 1) {

            Utilities.Disable(this, R.id.injectableEntryMasterLayout);
            getButton( R.id.injectableSaveButton).setText(getText(R.string.string_confirm));
            Utilities.Enable(this, R.id.injectableSaveButton);
            getButton( R.id.injectableEditButton).setText(getText(R.string.string_cancel));
            Utilities.Enable(this, R.id.injectableEditButton);
            Utilities.MakeVisible(this, R.id.injectableEditButton);

            Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);
        }

    }

    private void editInjectableInfo(){
        if(countSaveClick == 1) {
            countSaveClick = 0;
            Utilities.Enable(this, R.id.injectableEntryMasterLayout);
            getButton(R.id.injectableSaveButton).setText(editMode?"Update":"Save");
            Utilities.MakeInvisible(this, R.id.injectableEditButton);
        }
    }

    private void injectableSaveAsJson(){
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            json = saveHeader(json);

            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);

            Log.d("Injectable Save Json", ROOTKEY + ":{" + json.toString() + "}");

            injectablesInfoAsyncUpdate = new AsyncInjectablesInfoUpdate(this, mContext);
            injectablesInfoAsyncUpdate.execute(json.toString(), SERVLET, ROOTKEY);

            resetActivity(true);

        } catch (JSONException jse) {
            Log.e("Injection Exception: ", jse.getMessage());
        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString = "{" +
                "healthId:" + healthId + "," +
                (isRetrieval ? "" : "providerId:\"" + providerId + "\",") +
                "injectableLoad:" + (isRetrieval ? "retrieve" : (editMode?"update":"\"\"")) +
                "}";
        return new JSONObject(queryString);
    }

    private JSONObject saveHeader(JSONObject json) throws JSONException {
        json.put("sateliteCenterName",sateliteCenterName==null?"":sateliteCenterName);
        json.put("mobileNo",mobileNo);
        if(editMode){
            json.put("doseId",doseId);
        }

        return json;
    }

    private String manipulateDoseDate(String format, String date){
        String manipulatedDate="";
        Date actualDate;
        try {
            actualDate = Converter.stringToDate(format, date);
        } catch (ParseException e) {
            e.printStackTrace();
            actualDate = null;
        }
        if (!date.equals("")) {
            Date nextDoseDate = Utilities.manipulateDateByMonth(actualDate, 3, Constants.ADD_CODE);
            manipulatedDate = Converter.dateToString(format, nextDoseDate);
        }
        return manipulatedDate;
    }

    /**
     *abstract method of OnCheckedChangeListener interface
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (buttonView.getId() == R.id.injectableReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            int layouts[] = {R.id.injectableReferCenterNameLayout, R.id.injectableReasonLayout};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],visibility);
            }
        }
    }

    /**
     *abstract methods of OnItemSelectedListener interface
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    //**************************************************************

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    public void showWindowPeriod(View v) throws ParseException {
        if(!dueDoseDate.equals("")){
            Date startDateVal = Utilities.addDateOffset(Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,
                                                        dueDoseDate),-14);
            Date endDateVal = Utilities.addDateOffset(Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,
                    dueDoseDate),28);
            String startDate = Converter.dateToString(Constants.LONG_FORMAT_BRITISH,startDateVal);
            String endDate = Converter.dateToString(Constants.LONG_FORMAT_BRITISH,endDateVal);

            AlertDialogCreator.SimpleMessageDialog(this,startDate+" \t- "+endDate,"ডিউ ডোজের উইন্ডো পিরিয়ড",android.R.drawable.ic_dialog_info);
        }
    }

    @Override
    public void onTextChanged() {
        String date = getEditText(R.id.injectableActualDateEditText).getEditableText().toString();
        if (!date.equals("")) {
            String nextDoseDate = manipulateDoseDate(Constants.SHORT_SLASH_FORMAT_BRITISH,date);
            if (!nextDoseDate.equals("")) {
                getTextView(R.id.injectableNextDateValueTextView)
                        .setText(nextDoseDate);
            }
        }
    }

    private void showHideInjectableModifyButton(JSONObject jso) {
        Utilities.SetVisibility(this, R.id.modifyLastInjectableButton, isLastInjectableModifiable(jso) ? View.VISIBLE :View.INVISIBLE);
    }

    private boolean isLastInjectableModifiable(JSONObject jso) {
        String lastInjectableKey = String.valueOf(lastInjectableVisit);

        boolean thresholdPeriodPassed = false;
        try {
            JSONObject lastVisit = lastVisit = jso.getJSONObject(lastInjectableKey);
            String providerCode = providerCode = lastVisit.getString("providerId");
            if(lastVisit.has("systemEntryDate") && !lastVisit.getString("systemEntryDate").equals("")){
                Date lastVisitEntryDate = Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,lastVisit.getString("systemEntryDate"));
                thresholdPeriodPassed = Utilities.getDateDiff(lastVisitEntryDate,new Date(), TimeUnit.DAYS)> Flag.UPDATE_THRESHOLD;
            }
            Log.d(LOGTAG,"Last Service "+lastInjectableKey+" was provide by: \t" + providerCode);
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

    public void deleteLastInjectable(){
        AlertDialogCreator.SimpleDecisionDialog(InjectableActivity.this,getString(R.string.ServiceDeletionWarning),
                "Delete Service?",android.R.drawable.ic_delete);
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
                getTextView(R.id.injectableRegNoTextView).setText(Utilities.ConvertNumberToBangla(regSerial));
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error converting registration number");
            Utilities.printTrace(jse.getStackTrace());
        } catch (ParseException pe) {
            Log.e(LOGTAG, "Parsing Error converting registration date");
            Utilities.printTrace(pe.getStackTrace());
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialogWithResult(this,getResources().getString(R.string.title_injectables),
                RESULT_OK,ActivityResultCodes.FP_ACTIVITY);
    }

    @Override
    public void onPositiveButtonClicked(DialogInterface dialog) {
        dialog.cancel();
        deleteConfirmed();
    }

    @Override
    public void onNegativeButtonClicked(DialogInterface dialog) {

    }

    @Override
    public void onNeutralButtonClicked(DialogInterface dialog) {

    }

    private void deleteConfirmed() {
        try {

            JSONObject deleteJson = buildQueryHeader(false);
            deleteJson.put("injectableLoad", "delete");
            injectablesInfoAsyncUpdate = new AsyncInjectablesInfoUpdate(this, this);
            injectablesInfoAsyncUpdate.execute(deleteJson.toString(), SERVLET, ROOTKEY);
            resetActivity(false);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete Injectable request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    private boolean hasTheRequiredFields() {
        boolean valid = true;
        boolean specialInvalid = false;

        if (!Validation.hasText(getEditText(R.id.injectableActualDateEditText))) valid = false;

        if (getCheckbox(R.id.injectableReferCheckBox).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.injectableReferCenterNameSpinner)))
                specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.injectableChildReasonSpinner)))
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
            MethodUtils.showSnackBar(findViewById(R.id.injectable_activity_main), getResources().getString(R.string.refer_validation_message), true);
            return false;
        }
        else {
            return true;
        }

    }

    private void setHistoryLabelMapping() {
        //The array primarily contains 3 things, the string array from resource id, lower limit, upper limit
        historyLabelMap.put("isNewClient",  Pair.create(getString(R.string.client_status), new Integer[] {0}));
        historyLabelMap.put("dueDoseDate",  Pair.create(getString(R.string.injection_due_dose_date_text), new Integer[] {0}));
        historyLabelMap.put("doseDate",  Pair.create(getString(R.string.injection_actual_dose_date_text), new Integer[] {0}));
        historyLabelMap.put("LMP",  Pair.create(getString(R.string.LMPDateNormal), new Integer[] {0}));
        historyLabelMap.put("weight",  Pair.create(getString(R.string.weight), new Integer[] {0}));
        historyLabelMap.put("pulse",  Pair.create(getString(R.string.pulse), new Integer[] {0}));
        historyLabelMap.put("bpSystolic", Pair.create(getString(R.string.blood_presser), new Integer[] {0, 139, 80}));
        historyLabelMap.put("breastCondition",  Pair.create(getString(R.string.breast_condition), new Integer[] {R.array.Breast_Condition_DropDown}));
        historyLabelMap.put("uterusShape",  Pair.create(getString(R.string.uterus_size), new Integer[] {R.array.Uterus_Shape_DropDown}));
        historyLabelMap.put("uterusPosition",  Pair.create(getString(R.string.uterus_position), new Integer[] {R.array.Uterus_Position_DropDown}));
        historyLabelMap.put("uterusMovement",  Pair.create(getString(R.string.uterus_movement), new Integer[] {R.array.Uterus_Movement_DropDown}));
        historyLabelMap.put("uterusCervixMovePain",  Pair.create(getString(R.string.uterus_movement_pain), new Integer[] {R.array.Uterus_Movement_Pain_DropDown}));
        historyLabelMap.put("injectionId",  Pair.create(getString(R.string.injection_name_text), new Integer[] {R.array.Injection_Name_DropDown}));
        historyLabelMap.put("sideEffect",  Pair.create(getString(R.string.side_effect), new Integer[] {R.array.Injectable_Side_Effect_DropDown}));
        historyLabelMap.put("treatment",  Pair.create(getString(R.string.treatment), new Integer[] {R.array.Injectable_Treatment_DropDown}));
        historyLabelMap.put("advice",  Pair.create(getString(R.string.advice), new Integer[] {R.array.Injectable_Advice_DropDown}));
        historyLabelMap.put("refer",  Pair.create(getString(R.string.refer), new Integer[] {0}));
        historyLabelMap.put("referCenterName",  Pair.create(getString(R.string.referCenterName), new Integer[] {R.array.FacilityType_DropDown}));
        historyLabelMap.put("referReason",  Pair.create(getString(R.string.referReason), new Integer[] {R.array.Injectable_Refer_Reason_DropDown}));
    }
}
