package org.sci.rhis.fwc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncIUDInfoUpate;
import org.sci.rhis.connectivityhandler.AsyncPillCondomInfoUpdate;
import org.sci.rhis.connectivityhandler.AsyncPillCondomUpdate;
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
 * Created by armaan-ul.islam on 28-Dec-15.
 */
public class PillCondomActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                            AlertDialogCreator.DialogButtonClickedListener{

    private ProviderInfo provider;
    private Context con;
    private int lastPCVisit=0,serviceId=0;
    private boolean editMode = false;
    private JSONObject lastServiceJSON;

    private int useCase=0;
    final private String SERVLET = "pillcondom";
    final private String ROOTKEY = "PillCondomInfo";


    private  final String LOGTAG  = "FWC-PILL";
    private int countSaveClick=0;

    String healthId,mobile;

    private Spinner[] spinners = new Spinner[5];
    private LinearLayout[] layouts = new LinearLayout[5];

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    AsyncPillCondomInfoUpdate pcInfoUpdate;

    ExpandableListView expListView_pil;
    List<String> listDataHeader;
    LinearLayout ll_pil, historyLayout;
    private JSONObject jsonRespPil = null;
    JSONObject methodTypeJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pill_condom);
        init();

        setHistoryLabelMapping();
        setCompositeMap("bpSystolic", "bpDiastolic");

        AsyncPillCondomUpdate sendPostReqAsyncTask = new AsyncPillCondomUpdate(PillCondomActivity.this, this);

        String queryString =   "{" +
                "healthId:" + healthId + "," +
                "pillCondomLoad:" + "retrieve" +
                "}";
        String servlet = "pillcondom";
        String jsonRootKey = "PillCondomInfo";

        sendPostReqAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, queryString, servlet, jsonRootKey);
    }

    private void init(){

        lastPCVisit=0;
        con = this;

        createMethodJson();
        initialize();


        initiateCustomSpinners();
        initiateLinearLayouts();

        setIntentValues();

        for(int i = 0; i < spinners.length; i++) {
            spinners[i].setOnItemSelectedListener(this);
        }

        datePickerDialog = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.pac_Date_Picker_Button, (EditText) findViewById(R.id.pcServiceDateValue));

        getTextView(R.id.pcVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPCVisit >= 0 ? lastPCVisit + 1 : 1)));
        //showScreening();

        ll_pil = (LinearLayout)findViewById(R.id.llay);
        expListView_pil = new ExpandableListView(this);
        ll_pil.addView(expListView_pil);

        historyLayout = (LinearLayout)findViewById(R.id.history_lay_pil);
        MethodUtils.makeHistoryCollapsible(getTextView(R.id.PCHistoryLabel),historyLayout);
    }

    private void setIntentValues(){
        Intent intent = getIntent();

        healthId  = intent.getStringExtra(Constants.KEY_HEALTH_ID);
        mobile  = intent.getStringExtra(Constants.KEY_MOBILE);
        provider = intent.getParcelableExtra(Constants.KEY_PROVIDER);
        if(intent.hasExtra(Constants.KEY_FP_EXAMINATION)){
            try {
                JSONObject examJson = new JSONObject(intent.getStringExtra(Constants.KEY_FP_EXAMINATION));
                setExamValues(examJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if(intent.hasExtra(Constants.KEY_IS_NEW)){
            setNewClientLayout(intent.getBooleanExtra(Constants.KEY_IS_NEW,false));
        }
        if(intent.hasExtra(Constants.KEY_METHOD_CODE)){
            switch(intent.getIntExtra(Constants.KEY_METHOD_CODE,0)){
                case 0:
                    break;
                case Flag.CONDOM:
                    getSpinner(R.id.pcMethodSpinner).setSelection(2);
                    break;
                case Flag.PILL_SUKHI:
                    getSpinner(R.id.pcMethodSpinner).setSelection(1);
                    getSpinner(R.id.pillSpinner).setSelection(1);
                    break;
                case Flag.PILL_APON:
                    getSpinner(R.id.pcMethodSpinner).setSelection(1);
                    getSpinner(R.id.pillSpinner).setSelection(2);
                    break;
            }
        }
    }

    private void setNewClientLayout(boolean isNew){
        if(isNew){
            getRadioButton(R.id.pcIsNewRadioButton).setChecked(true);
            getEditText(R.id.pcServiceDateValue).setText(Converter.dateToString(Constants.SHORT_SLASH_FORMAT_BRITISH,new Date()));
            getCheckbox(R.id.pcScreeningCheckBox).setChecked(true);
        }else{
            getRadioButton(R.id.pcIsOldRadioButton).setChecked(true);
        }
    }

    public void createMethodJson(){
        try {
            final String methodJsonString = "{"+
                    "\"0\": \"\","+
                    "\"1\":\"সুখী বড়ি\","+
                    "\"10\":\"আপন বড়ি\","+
                    "\"999\": \"অন্যান্য বড়ি\","+
                    "\"2\":\"কনডম\","+
                    "\"3\": \"ইনজেকটেবলস\","+
                    "\"4\": \"আইইউডি\","+
                    "\"6\": \"ইমপ্ল্যান্ট\","+
                    "\"7\": \"স্থায়ী পদ্ধতি (পুরুষ)\","+
                    "\"8\": \"স্থায়ী পদ্ধতি(মহিলা)\","+
                    "\"9\": \"অন্য যে কোন অবস্থা\","+
                    "\"100\":\"পার্শ্ব প্রতিক্রিয়ার জন্য\","+
                    "\"101\":\"সন্তান নিবে\","+
                    "\"102\":\"মজুদ সংকট/সরবরাহ নাই\","+
                    "\"103\":\"স্বামী বিদেশে/কাছে নাই\"}";
            methodTypeJson = new JSONObject(methodJsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setExamValues(JSONObject json){
        Utilities.setSpinners(jsonSpinnerMap,json);
        Utilities.setEditTexts(jsonEditTextMap,json);
        //getCheckbox(R.id.pcScreeningCheckBox).setChecked(true);
        //getCheckbox(R.id.pcScreeningCheckBox).setEnabled(false);
    }

    @Override
    public void callbackAsyncTask(String result) {
        Log.d(LOGTAG, "Pil Part:>>>>>>>>>>" + "----------");
        Log.d(LOGTAG, "Pil Part:>>>>>>>>>>" + "----------"+result);
        /*Utilities.Disable(this,R.id.pcIsNewClientLayout);

        try {
            jsonRespPil = new JSONObject(result);
            showSerialNumber(jsonRespPil);
            String key;

            ll_pil.removeAllViews();

            lastPCVisit=jsonRespPil.getInt("count");
            if(lastPCVisit>0){
                showHidePCModifiableButton(jsonRespPil);
                lastServiceJSON = jsonRespPil.getJSONObject(String.valueOf(lastPCVisit));
            }else{
                //hide delete button if all the history is cleared by the provider
                getButton(R.id.modifyLastPCButton).setVisibility(View.INVISIBLE);
            }
            getTextView(R.id.pcVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPCVisit >= 0 ? lastPCVisit + 1 : 1)));

            int in=1;

            for (Iterator<String> ii = jsonRespPil.keys(); ii.hasNext(); ) {
                key = ii.next();


                System.out.println("1.Key:" + key + " Value:\'" + jsonRespPil.get(key) + "\'");

                //if(in == item-3)
                if(in > jsonRespPil.getInt("count"))
                    break;

                JSONObject jsonRootObject = jsonRespPil.getJSONObject("" + in);
                Log.d("--:::>", "---serviceSource=====>" + jsonRootObject.getString("visitDate"));

                String visitDate = jsonRootObject.getString("visitDate");
                String methodType = jsonRootObject.getString("methodType");
                String amount = jsonRootObject.getString("amount");
                if(amount.equals("0")){
                    amount = "মজুদ নাই";
                }
                String isNewClient = jsonRootObject.getString("isNewClient");
                String screening = jsonRootObject.getString("screening");
                String bpSystolic = jsonRootObject.getString("bpSystolic");
                String bpDiastolic = jsonRootObject.getString("bpDiastolic");
                String jaundice = jsonRootObject.getString("jaundice");
                String diabetes = jsonRootObject.getString("diabetes");

                ArrayList<String> list = new ArrayList<String>();

                list.add("" + getString(R.string.visitDate) + " " + visitDate);

                String temp = isNewClient.trim();
                if(temp.equalsIgnoreCase("1"))
                    list.add("" + getString(R.string.client_status) + " : " + getString(R.string.new_client));
                else
                    list.add("" + getString(R.string.client_status) + " : " + getString(R.string.old_client));

                temp = screening.trim();
                if(temp.equalsIgnoreCase("2"))
                    list.add("" + getString(R.string.screening) + " : " + getString(R.string.general_no));
                else
                    list.add("" + getString(R.string.screening) + " : " + getString(R.string.general_yes));

                list.add("" + getString(R.string.bpSystolic) + " " + bpSystolic+"/"+bpDiastolic);

                String[] detail;
                Resources resource = con.getResources();

                temp = jaundice.trim();
                detail = resource.getStringArray(R.array.Jaundice_Edima_Dropdown);
                temp = ""+detail[Integer.parseInt(temp)];

                list.add("" + getString(R.string.jaundice) + " " + temp);
                temp = diabetes.trim();
                detail = resource.getStringArray(R.array.YesNoLong_Dropdown);
                temp = ""+detail[Integer.parseInt(temp)];

                list.add("" + getString(R.string.diabetes) + " " + temp);

                temp = methodType.trim();
                int tempInt = Integer.parseInt(temp);
                temp = ""+methodTypeJson.getString(temp);
                if(Arrays.asList(new Integer[]{1, 10, 999,2}).contains(tempInt)){
                    list.add("" + getString(R.string.fp_methods) + " : " + temp +" - "+ amount);
                }else if(Arrays.asList(new Integer[]{3,4,6,7,8,9}).contains(tempInt)){
                    list.add("" + getString(R.string.fp_methods) + " : " + temp);
                }else if(Arrays.asList(new Integer[]{100, 101, 102,103}).contains(tempInt)){
                    list.add("" + getString(R.string.fp_methods) + " : ছেড়ে দিয়েছেন");
                    list.add(getString(R.string.str_give_up_reason)+temp);
                }else{
                   //nothing added in the list
                }
                try {
                    listDataHeader = new ArrayList<String>();
                    listDataPil = new HashMap<String, List<String>>();

                    listDataHeader.add(getString(R.string.str_visit) + Utilities.ConvertNumberToBangla(String.valueOf(in)) + ":");
                    listDataPil.put(listDataHeader.get(0), list);

                    listAdapter = new ExpandableListAdapterforPill(this, listDataHeader, listDataPil);

                    in++;

                    initPage();

                    ll_pil.addView(expListView_pil);
                    expListView_pil.setScrollingCacheEnabled(true);
                    expListView_pil.setAdapter(listAdapter);
                    //keep history visible and its child expandable
                    for(int i=0; i < listAdapter.getGroupCount(); i++){
                        expListView_pil.expandGroup(i);
                    }
                    if(historyLayout.getVisibility()==View.GONE){
                        historyLayout.setVisibility(View.VISIBLE);
                        getTextView(R.id.PCHistoryLabel).
                                setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                    }
                    //...........................................................................
                    ll_pil.invalidate();


                } catch (Exception e) {
                    Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                    e.printStackTrace();
                }
            }


        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        }*/

        ll_pil.removeAllViews(); //clear the history list first.
        Utilities.Disable(this,R.id.pcIsNewClientLayout);

        try {
            jsonRespPil = new JSONObject(result);

            lastPCVisit = (jsonRespPil.has("count") ? jsonRespPil.getInt("count") : 0 );
            if(lastPCVisit > 0) {
                showHidePCModifiableButton(jsonRespPil);
                lastServiceJSON = jsonRespPil.getJSONObject(String.valueOf(lastPCVisit));
            } else {
                //hide modify button if all the history is cleared by the provider
                getButton(R.id.modifyLastPCButton).setVisibility(View.INVISIBLE);
            }

            getTextView(R.id.pcVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPCVisit >= 0 ? lastPCVisit + 1 : 1)));

            Log.d(LOGTAG, "JSON Response:\n" + jsonRespPil.toString());

            showSerialNumber(jsonRespPil);

            for (int i = 1; i <= lastPCVisit && lastPCVisit !=0 ; i++) {
                JSONObject singleVisitJson = jsonRespPil.getJSONObject(String.valueOf(i));
                displayList = new ArrayList<>();

                HistoryListMaker<PillCondomActivity> historyListMaker = new HistoryListMaker<>(
                        this, /*pill condom service activity*/
                        singleVisitJson, /*json containing keys*/
                        historyLabelMap, /*history details*/
                        compositeMap /*fields whose values are given against multiple keys*/,
                        Pair.create("methodType", methodTypeJson)
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

                ll_pil.addView(expListView_pil);
                expListView_pil.setScrollingCacheEnabled(true);
                expListView_pil.setAdapter(displayListAdapter);

                //keep history visible and its child expandable
                for(int j=0; j < displayListAdapter.getGroupCount(); j++){
                    expListView_pil.expandGroup(j);
                }
                if(historyLayout.getVisibility()==View.GONE){
                    historyLayout.setVisibility(View.VISIBLE);
                    getTextView(R.id.PCHistoryLabel).
                            setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                }
                //...........................................................................

                ll_pil.invalidate();
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        } catch (Exception e) {
            Utilities.printTrace(e.getStackTrace(), 10);
        }

    }

    private void showHidePCModifiableButton(JSONObject jso) {
        Utilities.SetVisibility(this, R.id.modifyLastPCButton, isLastPCModifiable(jso) ? View.VISIBLE :View.INVISIBLE);
    }

    private boolean isLastPCModifiable(JSONObject jso) {
        String lastPCKey = String.valueOf(lastPCVisit);
        boolean thresholdPeriodPassed = false;
        try {
            JSONObject lastVisit = jso.getJSONObject(lastPCKey);
            String providerCode = lastVisit.getString("providerId");

            if(lastVisit.has("systemEntryDate") && !lastVisit.getString("systemEntryDate").equals("")){
                Date lastVisitEntryDate = Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,lastVisit.getString("systemEntryDate"));
                thresholdPeriodPassed = Utilities.getDateDiff(lastVisitEntryDate,new Date(), TimeUnit.DAYS)> Flag.UPDATE_THRESHOLD;
            }
            Log.d(LOGTAG,"Last Service "+lastPCKey+" was provided by: \t" + providerCode);
            return (provider.getProviderCode().equals(providerCode) && !thresholdPeriodPassed);
            //return (provider.getProviderCode().equals(providerCode));

        } catch (JSONException jse) {
            Log.e(LOGTAG, String.format(" JSON Error (PC History): %s", jse.getMessage()));
            Utilities.printTrace(jse.getStackTrace(), 10);
        } catch (ParseException pse){
            pse.printStackTrace();
        }

        return false;
    }

    public void deleteLastPC(){
        AlertDialogCreator.SimpleDecisionDialog(PillCondomActivity.this,getString(R.string.ServiceDeletionWarning),
                "Delete Service?",android.R.drawable.ic_delete);
    }

    private void initPage() {
        expListView_pil = new ExpandableListView(this);
        expListView_pil.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        expListView_pil.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView_pil.setIndicatorBounds(0, 0);
        expListView_pil.setChildIndicatorBounds(0, 0);
        expListView_pil.setStackFromBottom(true);
    }

    @Override
    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("screening",getCheckbox(R.id.pcScreeningCheckBox));
    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("methodType", getEditText(R.id.methodJson));
        jsonEditTextMap.put("amount", getEditText(R.id.amountJson));
        jsonEditTextMap.put("bpSystolic", getEditText(R.id.pcBloodPresserValueSystolic));
        jsonEditTextMap.put("bpDiastolic", getEditText(R.id.pcBloodPresserValueDiastolic));

        getEditText(R.id.pcBloodPresserValueSystolic).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.pcBloodPresserValueSystolic)));
        getEditText(R.id.pcBloodPresserValueSystolic).setOnFocusChangeListener(new CustomFocusChangeListener(this));
        getEditText(R.id.pcBloodPresserValueDiastolic).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.pcBloodPresserValueDiastolic)));
        getEditText(R.id.pcBloodPresserValueDiastolic).setOnFocusChangeListener(new CustomFocusChangeListener(this));
    }

    @Override
    protected void initiateTextViews() {
        getTextView(R.id.pcServiceDateLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pcServiceDateLabel).getText().toString(), 0, 1));
        getTextView(R.id.pcMethodLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pcMethodLabel).getText().toString(), 0, 1));
        getTextView(R.id.pillLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pillLabel).getText().toString(), 0, 1));
        getTextView(R.id.pillAmountLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pillAmountLabel).getText().toString(), 0, 1));
        getTextView(R.id.leftReasonLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.leftReasonLabel).getText().toString(), 0, 1));
        getTextView(R.id.pcOtherLabel).setText(Utilities.changePartialTextColor(Color.RED, getTextView(R.id.pcOtherLabel).getText().toString(), 0, 1));
    }

    @Override
    public void initiateSpinners() {
        jsonSpinnerMap.put("jaundice",getSpinner(R.id.pcJaundiceSpinner));
        jsonSpinnerMap.put("diabetes",getSpinner(R.id.pcDiabetesSpinner));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {

    }

    @Override
    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("visitDate",getEditText(R.id.pcServiceDateValue));
    }

    @Override
    protected void initiateRadioGroups() {
        jsonRadioGroupButtonMap.put("isNewClient", Pair.create(
                getRadioGroup(R.id.pcIsNewRadioGroup), Pair.create(
                        getRadioButton(R.id.pcIsNewRadioButton),
                        getRadioButton(R.id.pcIsOldRadioButton)
                )
                )
        );
    }

    protected void initiateLinearLayouts() {
        layouts[0]=(LinearLayout) findViewById(R.id.pillName);
        layouts[1]=(LinearLayout) findViewById(R.id.pillAmount);
        layouts[2]=(LinearLayout) findViewById(R.id.condomLayout);
        layouts[3]=(LinearLayout) findViewById(R.id.leftLayout);
        layouts[4]=(LinearLayout) findViewById(R.id.pcOtherLayout);
    }

    public void initiateCustomSpinners() {
        spinners[0] = (Spinner) findViewById(R.id.pcMethodSpinner);
        spinners[1] = (Spinner) findViewById(R.id.pillSpinner);
        spinners[2] = (Spinner) findViewById(R.id.leftReasonSpinner);
        spinners[3] = (Spinner) findViewById(R.id.pcOtherSpinner);
        spinners[4] = (Spinner) findViewById(R.id.pcJaundiceSpinner);
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private void hideAllLayouts(){
        for(int i=0;i<layouts.length;i++){
            Utilities.MakeInvisible(this,layouts[i],View.GONE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == spinners[0].getId()) {
            if(spinner.getSelectedItemPosition()==1 && (getSpinner(R.id.pcJaundiceSpinner).getSelectedItemPosition()>1 ||
                    (!getEditText(R.id.pcBloodPresserValueDiastolic).getEditableText().toString().equals("") &&
                            Integer.valueOf(getEditText(R.id.pcBloodPresserValueDiastolic).getEditableText().toString())>=90)||
                    (!getEditText(R.id.pcBloodPresserValueSystolic).getEditableText().toString().equals("") &&
                            Integer.valueOf(getEditText(R.id.pcBloodPresserValueSystolic).getEditableText().toString())>=140))){
                    Utilities.showAlertToast(this,getString(R.string.str_pill_cannot_given));
                    Utilities.Reset(this,layouts[0]);
                    Utilities.Reset(this,layouts[1]);
                    spinner.setSelection(0);
            }else{
                hideAllLayouts();
                switch (spinner.getSelectedItemPosition()){
                    case 1:
                        Utilities.MakeVisible(this,layouts[0]);
                        Utilities.MakeVisible(this,layouts[1]);
                        break;
                    case 2:
                        Utilities.MakeVisible(this,layouts[2]);
                        break;
                    case 3:
                        Utilities.MakeVisible(this,layouts[3]);
                        break;
                    case 4:
                        Utilities.MakeVisible(this,layouts[4]);
                        break;
                }
            }

        }else if(spinner.getId() == spinners[1].getId()) {
            if(spinner.getSelectedItemPosition()==1){
                if(!getIntent().getBooleanExtra(Constants.KEY_IS_SUKHI_ELIGIBLE,false)){
                    Utilities.showAlertToast(this,getString(R.string.str_pill_sukhi_cannot_given));
                    spinner.setSelection(0);
                }
            }else if(spinner.getSelectedItemPosition()==2){
                if(!getIntent().getBooleanExtra(Constants.KEY_IS_APON_ELIGIBLE,false)){
                    Utilities.showAlertToast(this,getString(R.string.str_pill_apon_cannot_given));
                    spinner.setSelection(0);
                }
            }
        }else if(spinner.getId() == spinners[4].getId()) {
            if(getSpinner(R.id.pcJaundiceSpinner).getSelectedItemPosition()>1 &&
                    getSpinner(R.id.pcMethodSpinner).getSelectedItemPosition()==1){
                getSpinner(R.id.pcMethodSpinner).setSelection(0);
                Utilities.Reset(this,layouts[0]);
                Utilities.Reset(this,layouts[1]);
                hideAllLayouts();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        //AlertDialogCreator.ExitActivityDialog(con,getResources().getString(R.string.title_activity_pc));
        AlertDialogCreator.ExitActivityDialogWithResult(con,getResources().getString(R.string.title_activity_pc),RESULT_OK,ActivityResultCodes.FP_ACTIVITY);
    }

    public void onClick(View v){

        switch (v.getId()){
            case R.id.pcSaveButton:
                try {
                    String cVisitDate=jsonEditTextDateMap.get("visitDate").getText().toString();
                    if(lastPCVisit>0 && !Validation.isValidVisitDate(this,cVisitDate,lastServiceJSON.getString("visitDate"),false,true)){
                        return;
                    }else{
                        doSaveButtonOperation();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                    doSaveButtonOperation();
                }catch (NullPointerException npe){
                    npe.printStackTrace();
                    doSaveButtonOperation();
                }
                break;

            case R.id.pcEditButton:
                if(countSaveClick == 1) {
                    countSaveClick = 0;
                    Utilities.Enable(this, R.id.PCEntryMasterLayout);
                    getButton(R.id.pcSaveButton).setText("Save");
                    //TODO - Review
                    Utilities.MakeInvisible(this, R.id.pcEditButton);
                }
                break;

            case R.id.pcDeleteButton:
                deleteLastPC();
                break;

            case R.id.pcEditCancelButton:
                resetActivity();
                break;

            case R.id.modifyLastPCButton:
                setLastServiceData();
                break;

            default:
                break;
        }
    }

    private void resetActivity(){
        editMode=false;
        getTextView(R.id.pcVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPCVisit >= 0 ? lastPCVisit + 1 : 1)));
        Utilities.Reset(this, R.id.pcText);
        getRadioButton(R.id.pcIsOldRadioButton).setChecked(true);
        Utilities.Disable(this,R.id.pcIsNewClientLayout);

        getButton(R.id.pcSaveButton).setText(getText(R.string.string_save));
        getButton(R.id.pcDeleteButton).setVisibility(View.GONE);
        getButton(R.id.pcEditCancelButton).setVisibility(View.GONE);

    }

    private void setLastServiceData(){
        editMode = true;
        Utilities.setEditTexts(jsonEditTextMap,lastServiceJSON);
        Utilities.setEditTextDates(jsonEditTextDateMap,lastServiceJSON);
        Utilities.setSpinners(jsonSpinnerMap,lastServiceJSON);
        Utilities.setCheckboxes(jsonCheckboxMap,lastServiceJSON);
        Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap,lastServiceJSON);
        try {
            serviceId = lastServiceJSON.getInt("serviceId");
        } catch (JSONException e) {
            e.printStackTrace();
            serviceId = 0;
        }
        setSpinners();
        getTextView(R.id.pcVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPCVisit)));
        getButton(R.id.pcSaveButton).setText(getText(R.string.string_update));
        getButton(R.id.pcDeleteButton).setVisibility(View.VISIBLE);
        getButton(R.id.pcEditCancelButton).setVisibility(View.VISIBLE);
        Utilities.Disable(this,R.id.pcServiceDateValue);
    }

    private void doSaveButtonOperation(){
        countSaveClick++;
        if( countSaveClick == 2 ) {
            pcSaveToJson();
            getButton(R.id.pcSaveButton).setText(getText(R.string.string_save));
            Utilities.Enable(this, R.id.PCEntryMasterLayout);
            getCheckbox(R.id.pcScreeningCheckBox).setEnabled(false);//restricted from user entry
            Utilities.MakeInvisible(this, R.id.pcEditButton);
            countSaveClick = 0;
        }

        else if(countSaveClick == 1) {
            if(!hasTheRequiredFileds()) {
                countSaveClick = 0;
                return;
            }
            Utilities.Disable(this, R.id.PCEntryMasterLayout);
            getButton( R.id.pcSaveButton).setText(getText(R.string.string_confirm));
            Utilities.Enable(this, R.id.pcSaveButton);
            getButton( R.id.pcEditButton).setText(getText(R.string.string_cancel));
            Utilities.Enable(this, R.id.pcEditButton);
            Utilities.MakeVisible(this, R.id.pcEditButton);

            Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);
        }
    }

    private boolean hasTheRequiredFileds() {

        boolean allSelected =  !(getEditText(R.id.pcServiceDateValue).getText().toString().equals(""))
                                &&
                                getSpinner(R.id.pcMethodSpinner).getSelectedItemPosition() != 0
                                &&
                                !(getSpinner(R.id.pcMethodSpinner).getSelectedItemPosition() == 1 &&
                                        (getSpinner(R.id.pillSpinner).getSelectedItemPosition() == 0 || getSpinner(R.id.pillAmountValue).getSelectedItemPosition() == 0)
                                  )
                                &&
                                !(getSpinner(R.id.pcMethodSpinner).getSelectedItemPosition() == 2 && getSpinner(R.id.condomAmountValue).getSelectedItemPosition() == 0)
                                &&
                                !(getSpinner(R.id.pcMethodSpinner).getSelectedItemPosition() == 3 && getSpinner(R.id.leftReasonSpinner).getSelectedItemPosition() == 0)
                                &&
                                !(getSpinner(R.id.pcMethodSpinner).getSelectedItemPosition() == 4 && getSpinner(R.id.pcOtherSpinner).getSelectedItemPosition() == 0)
                                ;

        if(!allSelected) {
            Utilities.showBiggerToast(this, R.string.GeneralSaveWarning);
            return false;
        }else if((!getEditText(R.id.pcBloodPresserValueDiastolic).getEditableText().toString().equals("") &&
                Integer.valueOf(getEditText(R.id.pcBloodPresserValueDiastolic).getEditableText().toString())>=90)||
                (!getEditText(R.id.pcBloodPresserValueSystolic).getEditableText().toString().equals("") &&
                        Integer.valueOf(getEditText(R.id.pcBloodPresserValueSystolic).getEditableText().toString())>=140)){
            if(getSpinner(R.id.pcMethodSpinner).getSelectedItemPosition()==1){
                getSpinner(R.id.pcMethodSpinner).setSelection(0);
                Utilities.Reset(this,layouts[0]);
                Utilities.Reset(this,layouts[1]);
                hideAllLayouts();

                Utilities.showAlertToast(this,getString(R.string.str_high_bp_pill_cannot_given));
                return false;
            }
        }
        return true;
    }

    private void pcSaveToJson() {
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            json = saveHeader(json);
            saveToHiddenFields();

            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);

            Log.d("PC Save Json", ROOTKEY + ":{" + json.toString() + "}");
            pcInfoUpdate = new AsyncPillCondomInfoUpdate(this,this);
            pcInfoUpdate.execute(json.toString(), SERVLET, ROOTKEY);

            resetActivity();

        } catch (JSONException jse) {
            Log.e("PC JSON Exception: ", jse.getMessage());
        }

    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString = "{" +
                "healthId:" + healthId + "," +
                (isRetrieval ? "" : "providerId:\"" + String.valueOf(provider.getProviderCode()) + "\",") +
                "pillCondomLoad:" + (isRetrieval ? "retrieve" : (editMode?"update":"\"\"")) +
                "}";
        return new JSONObject(queryString);
    }

    private JSONObject saveHeader(JSONObject json) throws JSONException {

        //............
        String sateliteCenter=provider.getSatelliteName();
        json.put("sateliteCenterName", sateliteCenter != null ? sateliteCenter : "");
        Log.i("Response I want",sateliteCenter);
        json.put("mobileNo",mobile);
        if(editMode){
            json.put("serviceId",serviceId);
        }

        return json;
    }

    private void saveToHiddenFields() {

        useCase=findCase();
        getEditText(R.id.methodJson).setText(String.valueOf(useCase));

        switch(useCase){
            case 1:
            case 10:
            case 999:
                getEditText(R.id.amountJson).setText(getSpinner(R.id.pillAmountValue).getSelectedItemPosition()==4?
                    "0":getSpinner(R.id.pillAmountValue).getSelectedItem().toString());
                break;
            case 2:
                getEditText(R.id.amountJson).setText(getSpinner(R.id.condomAmountValue).getSelectedItemPosition()==4?
                    "0":getSpinner(R.id.condomAmountValue).getSelectedItem().toString());

                break;
            default:
                getEditText(R.id.amountJson).setText("");
                break;
        }
    }

    private void setSpinners(){
        try {
            int mCode = Integer.valueOf(lastServiceJSON.getString("methodType"));
            int amount = Integer.valueOf(lastServiceJSON.getString("amount"));
            switch(mCode){
                //Pill section.....................................
                case 1:
                    getSpinner(R.id.pillSpinner).setSelection(1);
                    getSpinner(R.id.pillAmountValue).setSelection(amount==0?4:amount);
                    getSpinner(R.id.pcMethodSpinner).setSelection(1);
                    break;
                case 10:
                    getSpinner(R.id.pillSpinner).setSelection(2);
                    getSpinner(R.id.pillAmountValue).setSelection(amount==0?4:amount);
                    getSpinner(R.id.pcMethodSpinner).setSelection(1);
                    break;
                case 999:
                    getSpinner(R.id.pillSpinner).setSelection(3);
                    getSpinner(R.id.pillAmountValue).setSelection(amount==0?4:amount);
                    getSpinner(R.id.pcMethodSpinner).setSelection(1);
                    break;
                //...............................................................

                //condom
                case 2:
                    getSpinner(R.id.pcMethodSpinner).setSelection(2);
                    if(amount==12){
                        getSpinner(R.id.condomAmountValue).setSelection(1);
                    }else if(amount==24){
                        getSpinner(R.id.condomAmountValue).setSelection(2);
                    }else if(amount==36){
                        getSpinner(R.id.condomAmountValue).setSelection(3);
                    }else {
                        getSpinner(R.id.condomAmountValue).setSelection(4);
                    }

                    break;

                case 3:
                    getSpinner(R.id.pcMethodSpinner).setSelection(4);
                    getSpinner(R.id.pcOtherSpinner).setSelection(1);
                    break;
                case 4:
                    getSpinner(R.id.pcMethodSpinner).setSelection(4);
                    getSpinner(R.id.pcOtherSpinner).setSelection(2);
                    break;
                case 6:
                    getSpinner(R.id.pcMethodSpinner).setSelection(4);
                    getSpinner(R.id.pcOtherSpinner).setSelection(3);
                    break;
                case 7:
                    getSpinner(R.id.pcMethodSpinner).setSelection(4);
                    getSpinner(R.id.pcOtherSpinner).setSelection(4);
                    break;
                case 8:
                    getSpinner(R.id.pcMethodSpinner).setSelection(4);
                    getSpinner(R.id.pcOtherSpinner).setSelection(5);
                    break;
                case 9:
                    getSpinner(R.id.pcMethodSpinner).setSelection(4);
                    getSpinner(R.id.pcOtherSpinner).setSelection(6);
                    break;

                case 100:
                    getSpinner(R.id.leftReasonSpinner).setSelection(1);
                    getSpinner(R.id.pcMethodSpinner).setSelection(3);
                    break;
                case 101:
                    getSpinner(R.id.leftReasonSpinner).setSelection(2);
                    getSpinner(R.id.pcMethodSpinner).setSelection(3);
                    break;
                case 102:
                    getSpinner(R.id.leftReasonSpinner).setSelection(3);
                    getSpinner(R.id.pcMethodSpinner).setSelection(3);
                    break;
                case 103:
                    getSpinner(R.id.leftReasonSpinner).setSelection(4);
                    getSpinner(R.id.pcMethodSpinner).setSelection(3);
                    break;

                default:
                    //do nothing
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                getTextView(R.id.pcRegNoTextView).setText(Utilities.ConvertNumberToBangla(regSerial));
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error converting registration number");
            Utilities.printTrace(jse.getStackTrace());
        } catch (ParseException pe) {
            Log.e(LOGTAG, "Parsing Error converting registration date");
            Utilities.printTrace(pe.getStackTrace());
        }
    }

    private int findCase() {
        int caseType=0;
        int method=getSpinner(R.id.pcMethodSpinner).getSelectedItemPosition();
        int pill=getSpinner(R.id.pillSpinner).getSelectedItemPosition();
        int reason=getSpinner(R.id.leftReasonSpinner).getSelectedItemPosition();
        int other=getSpinner(R.id.pcOtherSpinner).getSelectedItemPosition();

        if(method==1){
            if(pill==1)
                caseType=1;
            else if(pill==2)
                caseType=10;
            else if(pill==3)
                caseType=999;
        }

        else if(method==2)
            caseType=2;

        else if(method==3){
            if(reason==1)
                caseType=100;
            else if(reason==2)
                caseType=101;
            else if(reason==3)
                caseType=102;
            else if(reason==4)
                caseType=103;
        }

        else if(method==4){
            if(other==1)
                caseType=3;
            else if(other==2)
                caseType=4;
            else if(other==3)
                caseType=6;
            else if(other==4)
                caseType=7;
            else if(other==5)
                caseType=8;
            else if(other==6)
                caseType=9;
        }
        return caseType;
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
            deleteJson.put("pillCondomLoad", "delete");
            pcInfoUpdate = new AsyncPillCondomInfoUpdate(this, this);
            pcInfoUpdate.execute(deleteJson.toString(), SERVLET, ROOTKEY);

            resetActivity();
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete Pill Condom Visit request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    private void setHistoryLabelMapping() {
        //The array primarily contains 3 things, the string array from resource id, lower limit, upper limit
        historyLabelMap.put("visitDate",  Pair.create(getString(R.string.visitDate), new Integer[] {0}));
        historyLabelMap.put("isNewClient",  Pair.create(getString(R.string.client_status), new Integer[] {0}));
        historyLabelMap.put("screening",  Pair.create(getString(R.string.screening), new Integer[] {0}));
        historyLabelMap.put("bpSystolic", Pair.create(getString(R.string.blood_presser), new Integer[] {0, 139, 89}));
        historyLabelMap.put("jaundice",  Pair.create(getString(R.string.jaundice), new Integer[] {R.array.Jaundice_Edima_Dropdown}));
        historyLabelMap.put("diabetes",  Pair.create(getString(R.string.diabetes), new Integer[] {R.array.YesNoLong_Dropdown}));
        historyLabelMap.put("methodType",  Pair.create(getString(R.string.pillLabel_mandatory), new Integer[] {R.array.pill_Dropdown}));
    }
}
