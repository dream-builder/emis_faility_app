package org.sci.rhis.fwc;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.AsyncFPInfoUpdate;
import org.sci.rhis.model.PregWoman;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
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
import java.util.concurrent.TimeUnit;



/**
 * Created by arafat.hasan on 5/30/2016.
 */
public class FPStartupFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private Button proceedBtn, editBtn, buttonUnknownFPLMPDate;
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;
    final private String SERVLET = "fpinfo";
    final private String ROOTKEY = "fpInfo";

    private final String LOGTAG = "FP-INFO-LOG";

    public boolean saveMode,hasPerformSave;

    EditText ageMonthEt,ageYearEt,lmpEt,sonNumEt,dauNumEt,deliveryEditText;

    View view;

    AsyncFPInfoUpdate asyncFPInfoUpdate;
    String lastChildAgeFull="";

    public String maleChild,femaleChild;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fp_startup, container, false);


        ImageButton[] ibArray = {
               getImageButton(view,R.id.imageButtonFpInfoLmpDate),
               getImageButton(view,R.id.imageButtonFPLastDeliveryDate),
               getImageButton(view,R.id.imageButtonFPMarriageDate)
        };
        for (int i = 0 ; i < ibArray.length; i++ ) {
            ibArray[i].setOnClickListener(this);
        }
        proceedBtn = (Button) view.findViewById(R.id.buttonFPStartupProceed);
        proceedBtn.setOnClickListener(this);

        editBtn = (Button) view.findViewById(R.id.buttonfpInfoEdit);
        editBtn.setOnClickListener(this);

        buttonUnknownFPLMPDate = (Button) view.findViewById(R.id.buttonUnknownFPLMPDate);
        buttonUnknownFPLMPDate.setOnClickListener(this);

        datePickerDialog = new CustomDatePickerDialog(getActivity(), new CustomSimpleDateFormat("dd/MM/yyyy"));
        datePickerPair = new HashMap<Integer, EditText>();

        datePickerPair.put(R.id.imageButtonFpInfoLmpDate, (EditText) view.findViewById(R.id.editTextFPLmpDate));
        datePickerPair.put(R.id.imageButtonFPLastDeliveryDate, (EditText) view.findViewById(R.id.editTextFPLastDeliveryDate));
        datePickerPair.put(R.id.imageButtonFPMarriageDate, (EditText) view.findViewById(R.id.editTextFPMarriageDate));

        ageMonthEt = (EditText)view.findViewById(R.id.editTextFPLastChildMonth);
        ageMonthEt.addTextChangedListener(new CustomTextWatcher(getActivity(),ageMonthEt));
        ageYearEt = (EditText)view.findViewById(R.id.editTextFPLastChildYear);
        ageYearEt.addTextChangedListener(new CustomTextWatcher(getActivity(),ageYearEt));
        sonNumEt = (EditText)view.findViewById(R.id.editTextFPSonNum);
        sonNumEt.addTextChangedListener(new CustomTextWatcher(getActivity(),sonNumEt));
        dauNumEt = (EditText)view.findViewById(R.id.editTextFPDaughterNum);
        dauNumEt.addTextChangedListener(new CustomTextWatcher(getActivity(),dauNumEt));
        lmpEt = (EditText)view.findViewById(R.id.editTextFPLmpDate);
        lmpEt.addTextChangedListener(new CustomTextWatcher(getActivity(),lmpEt));
        deliveryEditText = (EditText)view.findViewById(R.id.editTextFPLastDeliveryDate);
        deliveryEditText.addTextChangedListener(new CustomTextWatcher(getActivity(),deliveryEditText));

        maleChild= String.valueOf(sonNumEt.getText());
        femaleChild = String.valueOf(dauNumEt.getText());

        getRadioButton(view, R.id.radioFpHasCurrentMethod_yes).setOnCheckedChangeListener(this);
        getRadioGroup(view, R.id.radioGroupHasCurrentMethod).setOnCheckedChangeListener((radioGroup, i) -> MethodUtils.removeErrorSignFromRadioGroup(radioGroup));
        changeMandatorySignColor(view);
        return view;


    }

    private void changeMandatorySignColor(View view){
        ((TextView)view.findViewById(R.id.textViewFPStartupLMP)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.textViewFPStartupLMP)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.textViewHasCurrentMethod)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.textViewHasCurrentMethod)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.fpStartupMethodLabel)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.fpStartupMethodLabel)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.fpStartupVisitReasonLabel)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.fpStartupVisitReasonLabel)).getText().toString(), 0, 1));
    }

    public void hideViewIfMale(){
        int sexVal = Integer.valueOf(((SecondActivity)getActivity()).woman!=null ?
                ((SecondActivity)getActivity()).woman.getSex():((SecondActivity)getActivity()).generalPerson.getSex());
        if(sexVal == 1){ //in case of male
            ((ViewGroup)view.findViewById(R.id.lastDeliveryLayout)).setVisibility(View.GONE);
            ((ViewGroup)view.findViewById(R.id.fpStartupLmpLayout)).setVisibility(View.GONE);
            ((ViewGroup)view.findViewById(R.id.fpStartupIsPregnantLayout)).setVisibility(View.GONE);
            ((ViewGroup)view.findViewById(R.id.fpCycleLayout)).setVisibility(View.GONE);
            ((ViewGroup)view.findViewById(R.id.fpCycleDurationLayout)).setVisibility(View.GONE);
            ((ViewGroup)view.findViewById(R.id.fpMenstrualAmountLayout)).setVisibility(View.GONE);
            ((ViewGroup)view.findViewById(R.id.fpMenstrualPainLayout)).setVisibility(View.GONE);

            ((SecondActivity) getActivity()).getSpinner(R.id.spinnerMethodFPStartup)
                    .setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.FP_Methods_Screening_DropDown_Male)));
            ((SecondActivity) getActivity()).getSpinner(R.id.spinnerMethodFPStartup)
                    .setBackground(getActivity().getResources().getDrawable(R.drawable.gradient_spinner));

        }else{
            ((ViewGroup)view.findViewById(R.id.lastDeliveryLayout)).setVisibility(View.VISIBLE);
            ((ViewGroup)view.findViewById(R.id.fpStartupLmpLayout)).setVisibility(View.VISIBLE);
            ((ViewGroup)view.findViewById(R.id.fpStartupIsPregnantLayout)).setVisibility(View.VISIBLE);
            ((ViewGroup)view.findViewById(R.id.fpCycleLayout)).setVisibility(View.VISIBLE);
            ((ViewGroup)view.findViewById(R.id.fpCycleDurationLayout)).setVisibility(View.VISIBLE);
            ((ViewGroup)view.findViewById(R.id.fpMenstrualAmountLayout)).setVisibility(View.VISIBLE);
            ((ViewGroup)view.findViewById(R.id.fpMenstrualPainLayout)).setVisibility(View.VISIBLE);

            ((SecondActivity) getActivity()).getSpinner(R.id.spinnerMethodFPStartup)
                    .setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.FP_Methods_Screening_DropDown)));
            ((SecondActivity) getActivity()).getSpinner(R.id.spinnerMethodFPStartup)
                    .setBackground(getActivity().getResources().getDrawable(R.drawable.gradient_spinner));


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageButtonFpInfoLmpDate:
                datePickerDialog.show(datePickerPair.get(v.getId()));
                break;
            case R.id.imageButtonFPLastDeliveryDate:
                datePickerDialog.show(datePickerPair.get(v.getId()));
                break;
            case R.id.imageButtonFPMarriageDate:
                datePickerDialog.show(datePickerPair.get(v.getId()));
                break;

            case R.id.buttonfpInfoEdit:
                if(saveMode){
                    editBtn.setText("EDIT");
                    Utilities.Disable(getActivity(),R.id.layoutFPInfoForm);
                    saveMode = false;
                }else{
                    editBtn.setText("CANCEL");
                    Utilities.Enable(getActivity(),R.id.layoutFPInfoForm);
                    saveMode = true;
                }
                break;

            case R.id.buttonFPStartupProceed:
                if(saveMode){
                    if(hasRequiredFields()){
                        saveFPInfo();
                    }
                }else{
                    if(hasRequiredFields()){
                        proceedAfterSave();
                    }
                }

                break;
            case R.id.buttonUnknownFPLMPDate:
                /*
                * onclick on unknown lmp date button:
                * calculated 280 days earlier from last delivery date and set it to the lmp date field
                * */
                CustomSimpleDateFormat uiFormat = new CustomSimpleDateFormat("dd/MM/yyyy");
                try {
                    String deliveryDate = deliveryEditText.getText().toString();
                    Date d_date = Utilities.addDateOffset(uiFormat.parse(deliveryDate), -PregWoman.PREG_PERIOD);
                    lmpEt.setText(uiFormat.format(d_date));

                } catch (NumberFormatException | ParseException exception) {
                    Log.e(LOGTAG, exception.getMessage());
                    Utilities.printTrace(exception.getStackTrace());
                }

                break;
            default:
                break;
        }
    }

    RadioButton getRadioButton(View view, int id) {
        return (RadioButton)view.findViewById(id);
    }

    RadioGroup getRadioGroup(View view, int id) {
        return (RadioGroup)view.findViewById(id);
    }

    ImageButton getImageButton(View view, int id) {
        return (ImageButton) view.findViewById(id);
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean checked) {
        //Note: VIEW.VISIBLE = 0 & VIEW.GONE=8;
        switch (view.getId()){
            case R.id.radioFpHasCurrentMethod_yes:
                Utilities.SetVisibility(getActivity(),R.id.fpStartupMethodLayout,checked?0: 8);
                Utilities.SetVisibility(getActivity(),R.id.fpStartupVisitReasonLayout,checked?0: 8);
                break;
            default:
                break;
        }
    }

    private void currentMethod(int eMethodId){
        int decisionId = ((Spinner)view.findViewById(R.id.spinnerMethodFPStartup)).getSelectedItemPosition();
        switch (decisionId){
            case 0:
                //do nothing
                break;
            case 1:
                //((SecondActivity)getActivity()).openSpecificRegister(eMethodId,false);
                Utilities.SetVisibility(getActivity(),R.id.fpStartupMethodLayout,true?0: 8);
                Utilities.SetVisibility(getActivity(),R.id.fpStartupVisitReasonLayout,true?0: 8);
                break;

            default:
                break;

        }
    }

    private void makeDecision(int eMethodId){
        int decisionId = ((Spinner)view.findViewById(R.id.spinnerFPVisitReasons)).getSelectedItemPosition();
        switch (decisionId){
            case 0:
                //do nothing
                break;
            case 1:
                ((SecondActivity)getActivity()).openSpecificRegister(eMethodId,false);
                break;
            case 2:
                ((SecondActivity)getActivity()).showScreeningDialog();
                break;

        }
    }

    public void loadPreviousRecord(){
        JSONObject json;
        asyncFPInfoUpdate = null;
        asyncFPInfoUpdate = new AsyncFPInfoUpdate(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                retrieveFPHistory(result);
            }
        }, getActivity());
        try {
            json = buildQueryHeader(true);
            asyncFPInfoUpdate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("FP INFO Exception: ", jse.getMessage());
        }
    }

    private void saveFPInfo(){
        hasPerformSave = true;
        JSONObject json;
        asyncFPInfoUpdate = null;
        asyncFPInfoUpdate = new AsyncFPInfoUpdate(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                retrieveFPHistory(result);
            }
        }, getActivity());
        try {
            json = buildQueryHeader(false);
            prepareJsonFromFields(json);
            //.....................
            Log.d(LOGTAG, "FP INFO JSON:\n\t" + json.toString());
            asyncFPInfoUpdate.execute(json.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Exception: " + jse.getMessage());
        }finally {
            proceedAfterSave();
        }
        getMaleChildDetails();
        getFemaleChildDetails();
    }

    public String getMaleChildDetails()
    {
        maleChild = sonNumEt.getText().toString();
        //maleChild = getView().findViewById(R.id.editTextFPSonNum).toString();
        return  maleChild;
    }

    public String getFemaleChildDetails()
    {
        femaleChild = dauNumEt.getText().toString();
        //femaleChild = getView().findViewById(R.id.editTextFPDaughterNum).toString();
        return  femaleChild;
    }

    public JSONObject prepareJsonFromFields(JSONObject json){
        Utilities.getEditTexts(((SecondActivity)getActivity()).jsonEditTextMapChild, json);
        Utilities.getEditTextDates(((SecondActivity)getActivity()).jsonEditTextDateMapChild, json);
        Utilities.getSpinners(((SecondActivity)getActivity()).jsonSpinnerMapChild, json);
        Utilities.getRadioGroupButtons(((SecondActivity)getActivity()).jsonRadioGroupButtonMapChild, json);
        getSpecialCases(json);

        return json;
    }

    private void proceedAfterSave(){
        hasPerformSave = false;
        String selectedMethod = ((Spinner)view.findViewById(R.id.spinnerMethodFPStartup)).getSelectedItem().toString();
        if(getRadioButton(view, R.id.radioFpInfoIsPregnantYes).isChecked()){
            Utilities.showAlertToast(getActivity(),"এই ব্যক্তিকে পরিবার পরিকল্পনা সেবা দেয়া যাবে না");
        }else{
            if(getRadioButton(view, R.id.radioFpHasCurrentMethod_yes).isChecked()){
                int originalMethodCode = ConstantMaps.FPTagWiseCodes.get(selectedMethod);
                makeDecision(originalMethodCode);
            }else{
                ((SecondActivity)getActivity()).setDialogFlag(Flag.COUNSEL_DECISION);
                AlertDialogCreator.SpecialDecisionDialog(getActivity(),getString(R.string.str_counceling),
                        "",android.R.drawable.ic_dialog_alert,new String[]{"হ্যাঁ" ,"না"},false);
                AlertDialogCreator.SimpleMessageWithNoTitle(getActivity(),getString(R.string.str_counceling_confirm));
            }
        }


    }

    public String getLMPDateFromFPInfo(){
        String lmpDate = ((EditText) view.findViewById(R.id.editTextFPLmpDate)).getText().toString();
        return lmpDate;
    }

    public void retrieveFPHistory(String result){
        JSONObject jsonResponse;
        Log.d(LOGTAG, "RESPONSE:" + result);
        try {
            jsonResponse = new JSONObject(result);
            if(jsonResponse.getInt("fpInfoRetrieve")==1){
                saveMode = false;
                Utilities.setEditTexts(((SecondActivity)getActivity()).jsonEditTextMapChild, jsonResponse);
                Utilities.setEditTextDates(((SecondActivity)getActivity()).jsonEditTextDateMapChild, jsonResponse);
                //Utilities.setSpinners(((SecondActivity)getActivity()).jsonSpinnerMapChild, jsonResponse);
                //if()
                Utilities.setRadioGroupButtons(((SecondActivity)getActivity()).jsonRadioGroupButtonMapChild, jsonResponse);
                setSpecialCases(jsonResponse);
                Utilities.MakeVisible(getActivity(),R.id.buttonfpInfoEdit);
                editBtn.setText("Edit");
                Utilities.Disable(getActivity(),R.id.layoutFPInfoForm);
                Utilities.Reset(getActivity(),R.id.spinnerFPVisitReasons);
                if(hasPerformSave){
                    proceedAfterSave();
                }else{
                    ((Spinner)view.findViewById(R.id.spinnerFPVisitReasons)).setSelection(0);
                }

                /*
                * set buttonUnknownFPLMPDate visibility gone
                * where lastDeliveryDate is not empty from network response
                * */
                if (!jsonResponse.getString("lastDeliveryDate").isEmpty() &&
                        !jsonResponse.getString("LMP").isEmpty()) {
                    Utilities.SetVisibility(getActivity(), R.id.buttonUnknownFPLMPDate, View.GONE);
                }
            }else{
                saveMode = true;
                Utilities.Reset(getActivity(),R.id.layoutFPInfoForm);
                ((Spinner)view.findViewById(R.id.spinnerFPVisitReasons)).setSelection(0);
                Utilities.SetVisibility(getActivity(),R.id.buttonfpInfoEdit,View.GONE);
                lastChildAgeFull="";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            setRelationalValuesIfAny();
        }
    }

    public void getSpecialCases(JSONObject json) {
        try {
            //To enter 0 if ""
            int year = (ageYearEt.getText().toString()).isEmpty()?0:Integer.parseInt(ageYearEt.getText().toString());
            int month = (ageMonthEt.getText().toString()).isEmpty()?0:Integer.parseInt(ageMonthEt.getText().toString());

            month = year * 12 + month;

            json.put("lastChildAge", month);
            //int spinnerKey = Integer.valueOf(json.getString("currentMethod"));
            String spinnerVal = ((SecondActivity) getActivity()).getSpinner(R.id.spinnerMethodFPStartup).getSelectedItem().toString();
            json.put("currentMethod", ConstantMaps.FPTagWiseCodes.get(spinnerVal));

        } catch (JSONException jse) {

        }
    }

    public void setSpecialCases(JSONObject json) {
        try {
            lastChildAgeFull = json.getString("lastChildAge");
            setLastChildAge(lastChildAgeFull);

            int methodKey = json.has("currentMethod") && !json.getString("currentMethod").equals("")?
                                    Integer.valueOf(json.getString("currentMethod")):0;
            int sexVal = Integer.valueOf(((SecondActivity)getActivity()).woman!=null ?
                    ((SecondActivity)getActivity()).woman.getSex():((SecondActivity)getActivity()).generalPerson.getSex());

            //New Code
            if(methodKey!=0)
            {
                ((SecondActivity)getActivity()).getRadioButton(R.id.radioFpHasCurrentMethod_yes).setChecked(true);
                Utilities.SetVisibility(getActivity(),R.id.fpStartupMethodLayout,true?0: 8);
                Utilities.SetVisibility(getActivity(),R.id.fpStartupVisitReasonLayout,true?0: 8);
            }

            int methodSequence = Arrays.asList(getResources().getStringArray(sexVal==1?R.array.FP_Methods_Screening_DropDown_Male:R.array.FP_Methods_Screening_DropDown)).indexOf(ConstantMaps.codeWiseFPTags.get(methodKey));
            /*((SecondActivity)getActivity()).getSpinner(R.id.spinnerMethodFPStartup)
                    .setSelection(ConstantMaps.codeWiseFPSequence.get(methodKey));*/
            ((SecondActivity)getActivity()).getSpinner(R.id.spinnerMethodFPStartup)
                    .setSelection(methodSequence);

        } catch (JSONException jse) {

        }
    }

    private void setLastChildAge(String lastChildAge){
        EditText yearEt = (EditText)getActivity().findViewById(R.id.editTextFPLastChildYear);
        EditText monthEt = (EditText)getActivity().findViewById(R.id.editTextFPLastChildMonth);
        yearEt.setText(lastChildAge.equals("")? "" : String.valueOf(Integer.valueOf(lastChildAge) / 12));
        monthEt.setText(lastChildAge.equals("")? "" : String.valueOf(Integer.valueOf(lastChildAge) % 12));
        if(yearEt.getText().toString().equals("0") && monthEt.getText().toString().equals("0")){
            yearEt.setText("");
            monthEt.setText("");
        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        String sateliteCenter =ProviderInfo.getProvider().getSatelliteName();
        String queryString = "{" +
                "healthId:" + ((SecondActivity)getActivity()).responseID + "," +
                "providerId:" + ProviderInfo.getProvider().getProviderCode() + ","+
                //"sateliteCenterName:" + (!sateliteCenter.equalsIgnoreCase("") ? sateliteCenter : "\"\"")  + ","+
                "fpInfoLoad:" + (isRetrieval ? "retrieve" : "insert") +//here, insert or update does same thing
                "}";
        JSONObject headerJson = new JSONObject(queryString);
        headerJson.put("sateliteCenterName",sateliteCenter);
        return headerJson;


    }

    public int getChildCount(){
        JSONObject json = new JSONObject();
        prepareJsonFromFields(json);
        int childCount=0;
        try {
            childCount = (json.getString("son").equals("")?0:Integer.valueOf(json.getString("son"))) +
                    (json.getString("dau").equals("")?0:Integer.valueOf(json.getString("dau")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return childCount;
    }

    private boolean hasRequiredFields(){
        boolean hasFields=true;
        int sexVal = Integer.valueOf(((SecondActivity)getActivity()).woman!=null ?
                ((SecondActivity)getActivity()).woman.getSex():((SecondActivity)getActivity()).generalPerson.getSex());
        if (sexVal == 2) { //in case of female client
            if(!Validation.hasText(((SecondActivity)getActivity()).getEditText(R.id.editTextFPLmpDate))) hasFields = false;
        }
        if(!Validation.isAnyChildChecked(((SecondActivity)getActivity())
                .getRadioGroup(R.id.radioGroupHasCurrentMethod))) hasFields = false;
        if(((SecondActivity)getActivity()).getRadioButton(R.id.radioFpHasCurrentMethod_yes).isChecked()){
            if(!Validation.hasSelected(((SecondActivity)getActivity()).getSpinner(R.id.spinnerMethodFPStartup))) hasFields = false;
            if(!Validation.hasSelected(((SecondActivity)getActivity()).getSpinner(R.id.spinnerFPVisitReasons))) hasFields = false;
        }


        return hasFields;
    }

    public void hideQuestionsGenderWise(ViewGroup viewGroup){
        String deleteTag = "";
        int sexVal = Integer.valueOf(((SecondActivity)getActivity()).woman!=null ?
                ((SecondActivity)getActivity()).woman.getSex():((SecondActivity)getActivity()).generalPerson.getSex());
        if (sexVal == 1){ //in case of male client
            deleteTag = "FEMALE";
        }else if(sexVal == 2){ //in case of female client
            deleteTag = "MALE";
        }
        for( int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if(view instanceof LinearLayout){
                if(view.getTag()!=null && view.getTag().toString().equals(deleteTag)){
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    public void hideExamForMale(ViewGroup viewGroup, Integer[] resourceIdArray){
        for( int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if(view instanceof LinearLayout){
                if(!Arrays.asList(resourceIdArray).contains(Integer.valueOf(view.getId()))){
                    view.setVisibility(View.GONE);
                }
            }
        }
    }
    //This is method is used to set predefined values in the questionnaire
    public void setPreDefinedValues(Dialog dialog){
        JSONObject json = new JSONObject();
        int sexVal = Integer.valueOf(((SecondActivity)getActivity()).woman!=null ?
                ((SecondActivity)getActivity()).woman.getSex():((SecondActivity)getActivity()).generalPerson.getSex());
        prepareJsonFromFields(json);
        try {
            //menstrual cycle................
            if(sexVal==2){
                if(!json.getString("menstrualCycle").equals("")){

                    if(json.getString("menstrualCycle").equals("2")){
                        ((RadioButton)dialog.findViewById(R.id.squestion1radioButtonYes)).setChecked(true);
                    }else{
                        ((RadioButton)dialog.findViewById(R.id.squestion1radioButtonNo)).setChecked(true);
                    }
                    dialog.findViewById(R.id.squestion1radioButtonYes).setEnabled(false);
                    dialog.findViewById(R.id.squestion1radioButtonNo).setEnabled(false);
                }
            }
            //.................

            //menstrual pain................
            if(sexVal==2){
                if(!json.getString("menstrualPain").equals("")){

                    if(json.getString("menstrualPain").equals("1")){
                        ((RadioButton)dialog.findViewById(R.id.squestion4radioButtonYes)).setChecked(true);
                    }else{
                        ((RadioButton)dialog.findViewById(R.id.squestion4radioButtonNo)).setChecked(true);
                    }
                    dialog.findViewById(R.id.squestion4radioButtonYes).setEnabled(false);
                    dialog.findViewById(R.id.squestion4radioButtonNo).setEnabled(false);
                }
            }
            //.................

            //pregnancy status................
            if(sexVal==2){
                if(!json.getString("isPregnant").equals("")||!json.getString("LMP").equals("")){
                    long days = Utilities.getDateDiff(Converter
                            .stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,json.getString("LMP")), new Date(), TimeUnit.DAYS);

                    if(json.getString("isPregnant").equals("1")/*||days>(4*7)*/){//TODO: has to review immediately through discussing with Abrar bhai
                        ((RadioButton)dialog.findViewById(R.id.squestion5radioButtonYes)).setChecked(true);
                    }else{
                        ((RadioButton)dialog.findViewById(R.id.squestion5radioButtonNo)).setChecked(true);
                    }
                    dialog.findViewById(R.id.squestion5radioButtonYes).setEnabled(false);
                    dialog.findViewById(R.id.squestion5radioButtonNo).setEnabled(false);
                }
            }

            //.................


            //last child age.................
            if(!json.getString("lastChildAge").equals("0")){
                boolean disableRequired = false; //disable both 8 no question only
                boolean fullDisableRequired = false;//disable both 8 & 9 no question
                if((Integer.valueOf(json.getString("lastChildAge"))*30)<(6*7)){
                    ((RadioButton)dialog.findViewById(R.id.squestion7radioButtonYes)).setChecked(true);
                    disableRequired=true;

                }else {
                    ((RadioButton)dialog.findViewById(R.id.squestion7radioButtonNo)).setChecked(true);
                    disableRequired=true;
                }
                if((Integer.valueOf(json.getString("lastChildAge")))<=6
                        && json.getString("childTakeBreastmilkOnly").equals("1")){
                    ((RadioButton)dialog.findViewById(R.id.squestion8radioButtonYes)).setChecked(true);
                    disableRequired=true;
                    fullDisableRequired = true;


                }else if((Integer.valueOf(json.getString("lastChildAge")))>6){
                    ((RadioButton)dialog.findViewById(R.id.squestion7radioButtonNo)).setChecked(true);
                    ((RadioButton)dialog.findViewById(R.id.squestion8radioButtonNo)).setChecked(true);
                    disableRequired=true;
                    fullDisableRequired = true;
                }
                if(disableRequired){
                    dialog.findViewById(R.id.squestion7radioButtonYes).setEnabled(false);
                    dialog.findViewById(R.id.squestion7radioButtonNo).setEnabled(false);
                    if(fullDisableRequired){
                        dialog.findViewById(R.id.squestion8radioButtonYes).setEnabled(false);
                        dialog.findViewById(R.id.squestion8radioButtonNo).setEnabled(false);
                    }

                }

            }
            //.....................................
            if(!json.getString("son").equals("") || !json.getString("dau").equals("")){
                int childCount = (json.getString("son").equals("")?0:Integer.valueOf(json.getString("son"))) +
                        (json.getString("dau").equals("")?0:Integer.valueOf(json.getString("dau")));
                if(childCount==1){
                    ((RadioButton)dialog.findViewById(R.id.squestion22radioButtonYes)).setChecked(true);
                    ((RadioButton)dialog.findViewById(R.id.squestion23radioButtonNo)).setChecked(true);

                }else if(childCount==2){
                    ((RadioButton)dialog.findViewById(R.id.squestion22radioButtonNo)).setChecked(true);
                    ((RadioButton)dialog.findViewById(R.id.squestion23radioButtonYes)).setChecked(true);
                }else{
                    ((RadioButton)dialog.findViewById(R.id.squestion22radioButtonNo)).setChecked(true);
                    ((RadioButton)dialog.findViewById(R.id.squestion23radioButtonNo)).setChecked(true);
                    if(childCount==0){
                        (dialog.findViewById(R.id.squestion24Layout)).setVisibility(View.GONE);
                    }
                }
                dialog.findViewById(R.id.squestion22radioButtonYes).setEnabled(false);
                dialog.findViewById(R.id.squestion22radioButtonNo).setEnabled(false);
                dialog.findViewById(R.id.squestion23radioButtonYes).setEnabled(false);
                dialog.findViewById(R.id.squestion23radioButtonNo).setEnabled(false);
            }

            //.....................................
            if(sexVal==2){
                if((((SecondActivity)getActivity()).woman!=null && ((SecondActivity)getActivity()).woman.getAge()<=35)){

                    ((RadioButton)dialog.findViewById(R.id.squestion17radioButtonNo)).setChecked(true);
                    dialog.findViewById(R.id.squestion17radioButtonYes).setEnabled(false);
                    dialog.findViewById(R.id.squestion17radioButtonNo).setEnabled(false);
                }
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setPreDefinedValuesForExams(Dialog dialog){
        JSONObject json = new JSONObject();
        prepareJsonFromFields(json);

        //................
        try {
            if(!json.getString("isPregnant").equals("")){

                if(json.getString("isPregnant").equals("1")){
                    ((Spinner)dialog.findViewById(R.id.fpIsCurrentlyPregnantSpinner)).setSelection(2);
                }else{
                    ((Spinner)dialog.findViewById(R.id.fpIsCurrentlyPregnantSpinner)).setSelection(1);
                }
                dialog.findViewById(R.id.fpIsCurrentlyPregnantSpinner).setEnabled(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //.................

    }

    private void setRelationalValuesIfAny(){
        if(((SecondActivity)getActivity()).woman!=null){
            //TODO: I think this setting is not that much required, but need discussion
            /*if(((SecondActivity)getActivity()).woman.getLmp()!=null){
                EditText lmpEditText = (EditText) view.findViewById(R.id.editTextFPLmpDate);
                if(lmpEditText.getText().toString().equals("")){
                    lmpEditText.setText(Converter.dateToString(Constants.SHORT_SLASH_FORMAT_BRITISH,((SecondActivity)getActivity()).woman.getLmp()));
                }
            }*/
            if(((SecondActivity)getActivity()).woman.getActualDelivery()!=null){
                if(deliveryEditText.getText().toString().equals("")){
                    deliveryEditText.setText(Converter.dateToString(Constants.SHORT_SLASH_FORMAT_BRITISH,((SecondActivity)getActivity()).woman.getActualDelivery()));
                }
            }
            /*if(((SecondActivity)getActivity()).woman.getnBoy()!=null){
                EditText nBoyEditText = (EditText) view.findViewById(R.id.editTextFPSonNum);
                if(nBoyEditText.getText().toString().equals("")){
                    nBoyEditText.setText(((SecondActivity)getActivity()).woman.getnBoy());
                }
            }
            if(((SecondActivity)getActivity()).woman.getnGirl()!=null){
                EditText nGirlEditText = (EditText) view.findViewById(R.id.editTextFPDaughterNum);
                if(nGirlEditText.getText().toString().equals("")){
                    nGirlEditText.setText(((SecondActivity)getActivity()).woman.getnGirl());
                }
            }*/
            try {
                //TODO: ** uncomment ASAP
                /*if(((SecondActivity)getActivity()).client.has("cBoy")) {
                    ((EditText)view.findViewById(R.id.editTextFPSonNum)).setText(((SecondActivity)getActivity()).client.getString("cBoy"));
                }
                if(((SecondActivity)getActivity()).client.has("cGirl")) {
                    ((EditText)view.findViewById(R.id.editTextFPDaughterNum)).setText(((SecondActivity)getActivity()).client.getString("cGirl"));
                }*/
                //***
                if(((SecondActivity)getActivity()).client.has("cMarrDate")) {
                    String marrDate = Converter.convertSdfFormat(Constants.SHORT_HYPHEN_FORMAT_DATABASE,//previousFormat
                            ((SecondActivity)getActivity()).client.getString("cMarrDate"),//inputDate
                            Constants.SHORT_SLASH_FORMAT_BRITISH);//convertedFormat
                    ((EditText)view.findViewById(R.id.editTextFPMarriageDate)).setText(marrDate);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (ParseException pe){
                pe.printStackTrace();
            }
            //TODO: ** uncomment ASAP
            /*if(((SecondActivity)getActivity()).woman.getLastChildAge()!=null){
                int fpAge = lastChildAgeFull.equals("")?0:Integer.valueOf(lastChildAgeFull);
                if(Integer.valueOf(((SecondActivity)getActivity()).woman.getLastChildAge()) > fpAge){
                    setLastChildAge(((SecondActivity)getActivity()).woman.getLastChildAge());
                }
            }*/
            //**

        }

    }




}

