package org.sci.rhis.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONObject;
import org.sci.rhis.fwc.MultiSelectionSpinner;
import org.sci.rhis.fwc.R;
import org.sci.rhis.model.FPExaminations;
import org.sci.rhis.model.FPScreeningResponse;
import org.sci.rhis.model.ProviderInfo;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by arafat.hasan on 9/29/2016.
 */
public class ScreeningHelper {
    static HashMap<String, Spinner> jsonSpinnerMap;
    static HashMap<String, EditText> jsonEditTextMap;

    public static void setQuestionnaireValues(FPScreeningResponse questionnaires, Dialog dialog){
        final RadioButton q1= (RadioButton) dialog.findViewById(R.id.squestion1radioButtonYes);
        final RadioButton q1No= (RadioButton) dialog.findViewById(R.id.squestion1radioButtonNo);
        final RadioButton q2= (RadioButton) dialog.findViewById(R.id.squestion2radioButtonYes);
        final RadioButton q2No= (RadioButton) dialog.findViewById(R.id.squestion2radioButtonNo);
        final RadioButton q3= (RadioButton) dialog.findViewById(R.id.squestion3radioButtonYes);
        final RadioButton q3No= (RadioButton) dialog.findViewById(R.id.squestion3radioButtonNo);
        final RadioButton q4= (RadioButton) dialog.findViewById(R.id.squestion4radioButtonYes);
        final RadioButton q4No= (RadioButton) dialog.findViewById(R.id.squestion4radioButtonNo);
        final RadioButton q5= (RadioButton) dialog.findViewById(R.id.squestion5radioButtonYes);
        final RadioButton q5No= (RadioButton) dialog.findViewById(R.id.squestion5radioButtonNo);
        final RadioButton q6= (RadioButton) dialog.findViewById(R.id.squestion6radioButtonYes);
        final RadioButton q6No= (RadioButton) dialog.findViewById(R.id.squestion6radioButtonNo);
        final RadioButton q7= (RadioButton) dialog.findViewById(R.id.squestion7radioButtonYes);
        final RadioButton q7No= (RadioButton) dialog.findViewById(R.id.squestion7radioButtonNo);
        final RadioButton q8= (RadioButton) dialog.findViewById(R.id.squestion8radioButtonYes);
        final RadioButton q8No= (RadioButton) dialog.findViewById(R.id.squestion8radioButtonNo);
        final RadioButton q9= (RadioButton) dialog.findViewById(R.id.squestion9radioButtonYes);
        final RadioButton q9No= (RadioButton) dialog.findViewById(R.id.squestion9radioButtonNo);
        final RadioButton q10= (RadioButton) dialog.findViewById(R.id.squestion10radioButtonYes);
        final RadioButton q10No= (RadioButton) dialog.findViewById(R.id.squestion10radioButtonNo);
        final RadioButton q11= (RadioButton) dialog.findViewById(R.id.squestion11radioButtonYes);
        final RadioButton q11No= (RadioButton) dialog.findViewById(R.id.squestion11radioButtonNo);
        final RadioButton q12= (RadioButton) dialog.findViewById(R.id.squestion12radioButtonYes);
        final RadioButton q12No= (RadioButton) dialog.findViewById(R.id.squestion12radioButtonNo);
        final RadioButton q13= (RadioButton) dialog.findViewById(R.id.squestion13radioButtonYes);
        final RadioButton q13No= (RadioButton) dialog.findViewById(R.id.squestion13radioButtonNo);
        final RadioButton q14= (RadioButton) dialog.findViewById(R.id.squestion14radioButtonYes);
        final RadioButton q14No= (RadioButton) dialog.findViewById(R.id.squestion14radioButtonNo);
        final RadioButton q15= (RadioButton) dialog.findViewById(R.id.squestion15radioButtonYes);
        final RadioButton q15No= (RadioButton) dialog.findViewById(R.id.squestion15radioButtonNo);
        final RadioButton q16= (RadioButton) dialog.findViewById(R.id.squestion16radioButtonYes);
        final RadioButton q16No= (RadioButton) dialog.findViewById(R.id.squestion16radioButtonNo);
        final RadioButton q17= (RadioButton) dialog.findViewById(R.id.squestion17radioButtonYes);
        final RadioButton q17No= (RadioButton) dialog.findViewById(R.id.squestion17radioButtonNo);
        final RadioButton q18= (RadioButton) dialog.findViewById(R.id.squestion18radioButtonYes);
        final RadioButton q18No= (RadioButton) dialog.findViewById(R.id.squestion18radioButtonNo);
        final RadioButton q19= (RadioButton) dialog.findViewById(R.id.squestion19radioButtonYes);
        final RadioButton q19No= (RadioButton) dialog.findViewById(R.id.squestion19radioButtonNo);
        final RadioButton q20= (RadioButton) dialog.findViewById(R.id.squestion20radioButtonYes);
        final RadioButton q20No= (RadioButton) dialog.findViewById(R.id.squestion20radioButtonNo);
        final RadioButton q21= (RadioButton) dialog.findViewById(R.id.squestion21radioButtonYes);
        final RadioButton q21No= (RadioButton) dialog.findViewById(R.id.squestion21radioButtonNo);
        final RadioButton q22= (RadioButton) dialog.findViewById(R.id.squestion22radioButtonYes);
        final RadioButton q22No= (RadioButton) dialog.findViewById(R.id.squestion22radioButtonNo);
        final RadioButton q23= (RadioButton) dialog.findViewById(R.id.squestion23radioButtonYes);
        final RadioButton q23No= (RadioButton) dialog.findViewById(R.id.squestion23radioButtonNo);
        final RadioButton q24= (RadioButton) dialog.findViewById(R.id.squestion24radioButtonYes);
        final RadioButton q24No= (RadioButton) dialog.findViewById(R.id.squestion24radioButtonNo);
        final RadioButton q25= (RadioButton) dialog.findViewById(R.id.squestion25radioButtonYes);
        final RadioButton q25No= (RadioButton) dialog.findViewById(R.id.squestion25radioButtonNo);
        final RadioButton q26= (RadioButton) dialog.findViewById(R.id.squestion26radioButtonYes);
        final RadioButton q26No= (RadioButton) dialog.findViewById(R.id.squestion26radioButtonNo);
        final RadioButton q27= (RadioButton) dialog.findViewById(R.id.squestion27radioButtonYes);
        final RadioButton q27No= (RadioButton) dialog.findViewById(R.id.squestion27radioButtonNo);
        final RadioButton q28= (RadioButton) dialog.findViewById(R.id.squestion28radioButtonYes);
        final RadioButton q28No= (RadioButton) dialog.findViewById(R.id.squestion28radioButtonNo);
        final RadioButton q29= (RadioButton) dialog.findViewById(R.id.squestion29radioButtonYes);
        final RadioButton q29No= (RadioButton) dialog.findViewById(R.id.squestion29radioButtonNo);

        if(questionnaires.isResponse1()) q1.setChecked(true); else q1No.setChecked(true);
        if(questionnaires.isResponse2()) q2.setChecked(true); else q2No.setChecked(true);
        if(questionnaires.isResponse3()) q3.setChecked(true); else q3No.setChecked(true);
        if(questionnaires.isResponse4()) q4.setChecked(true); else q4No.setChecked(true);
        if(questionnaires.isResponse5()) q5.setChecked(true); else q5No.setChecked(true);
        if(questionnaires.isResponse6()) q6.setChecked(true); else q6No.setChecked(true);
        if(questionnaires.isResponse7()) q7.setChecked(true); else q7No.setChecked(true);
        if(questionnaires.isResponse8()) q8.setChecked(true); else q8No.setChecked(true);
        if(questionnaires.isResponse9()) q9.setChecked(true); else q9No.setChecked(true);
        if(questionnaires.isResponse10()) q10.setChecked(true); else q10No.setChecked(true);
        if(questionnaires.isResponse11()) q11.setChecked(true); else q11No.setChecked(true);
        if(questionnaires.isResponse12()) q12.setChecked(true); else q12No.setChecked(true);
        if(questionnaires.isResponse13()) q13.setChecked(true); else q13No.setChecked(true);
        if(questionnaires.isResponse14()) q14.setChecked(true); else q14No.setChecked(true);
        if(questionnaires.isResponse15()) q15.setChecked(true); else q15No.setChecked(true);
        if(questionnaires.isResponse16()) q16.setChecked(true); else q16No.setChecked(true);
        if(questionnaires.isResponse17()) q17.setChecked(true); else q17No.setChecked(true);
        if(questionnaires.isResponse18()) q18.setChecked(true); else q18No.setChecked(true);
        if(questionnaires.isResponse19()) q19.setChecked(true); else q19No.setChecked(true);
        if(questionnaires.isResponse20()) q20.setChecked(true); else q20No.setChecked(true);
        if(questionnaires.isResponse21()) q21.setChecked(true); else q21No.setChecked(true);
        if(questionnaires.isResponse22()) q22.setChecked(true); else q22No.setChecked(true);
        if(questionnaires.isResponse23()) q23.setChecked(true); else q23No.setChecked(true);
        if(questionnaires.isResponse24()) q24.setChecked(true); else q24No.setChecked(true);
        if(questionnaires.isResponse25()) q25.setChecked(true); else q25No.setChecked(true);
        if(questionnaires.isResponse26()) q26.setChecked(true); else q26No.setChecked(true);
        if(questionnaires.isResponse27()) q27.setChecked(true); else q27No.setChecked(true);
        if(questionnaires.isResponse28()) q28.setChecked(true); else q28No.setChecked(true);
        if(questionnaires.isResponse29()) q29.setChecked(true); else q29No.setChecked(true);
    }

    public static FPScreeningResponse getQuestionnaireValues(Dialog dialog){
        FPScreeningResponse response = null;
        final RadioButton q1= (RadioButton) dialog.findViewById(R.id.squestion1radioButtonYes);
        final RadioButton q2= (RadioButton) dialog.findViewById(R.id.squestion2radioButtonYes);
        final RadioButton q3= (RadioButton) dialog.findViewById(R.id.squestion3radioButtonYes);
        final RadioButton q4= (RadioButton) dialog.findViewById(R.id.squestion4radioButtonYes);
        final RadioButton q5= (RadioButton) dialog.findViewById(R.id.squestion5radioButtonYes);
        final RadioButton q6= (RadioButton) dialog.findViewById(R.id.squestion6radioButtonYes);
        final RadioButton q7= (RadioButton) dialog.findViewById(R.id.squestion7radioButtonYes);
        final RadioButton q8= (RadioButton) dialog.findViewById(R.id.squestion8radioButtonYes);
        final RadioButton q9= (RadioButton) dialog.findViewById(R.id.squestion9radioButtonYes);
        final RadioButton q10= (RadioButton) dialog.findViewById(R.id.squestion10radioButtonYes);
        final RadioButton q11= (RadioButton) dialog.findViewById(R.id.squestion11radioButtonYes);
        final RadioButton q12= (RadioButton) dialog.findViewById(R.id.squestion12radioButtonYes);
        final RadioButton q13= (RadioButton) dialog.findViewById(R.id.squestion13radioButtonYes);
        final RadioButton q14= (RadioButton) dialog.findViewById(R.id.squestion14radioButtonYes);
        final RadioButton q15= (RadioButton) dialog.findViewById(R.id.squestion15radioButtonYes);
        final RadioButton q16= (RadioButton) dialog.findViewById(R.id.squestion16radioButtonYes);
        final RadioButton q17= (RadioButton) dialog.findViewById(R.id.squestion17radioButtonYes);
        final RadioButton q18= (RadioButton) dialog.findViewById(R.id.squestion18radioButtonYes);
        final RadioButton q19= (RadioButton) dialog.findViewById(R.id.squestion19radioButtonYes);
        final RadioButton q20= (RadioButton) dialog.findViewById(R.id.squestion20radioButtonYes);
        final RadioButton q21= (RadioButton) dialog.findViewById(R.id.squestion21radioButtonYes);
        final RadioButton q22= (RadioButton) dialog.findViewById(R.id.squestion22radioButtonYes);
        final RadioButton q23= (RadioButton) dialog.findViewById(R.id.squestion23radioButtonYes);
        final RadioButton q24= (RadioButton) dialog.findViewById(R.id.squestion24radioButtonYes);
        final RadioButton q25= (RadioButton) dialog.findViewById(R.id.squestion25radioButtonYes);
        final RadioButton q26= (RadioButton) dialog.findViewById(R.id.squestion26radioButtonYes);
        final RadioButton q27= (RadioButton) dialog.findViewById(R.id.squestion27radioButtonYes);
        final RadioButton q28= (RadioButton) dialog.findViewById(R.id.squestion28radioButtonYes);
        final RadioButton q29= (RadioButton) dialog.findViewById(R.id.squestion29radioButtonYes);

        response = new FPScreeningResponse();
        response.setResponse1(q1.isChecked());
        response.setResponse2(q2.isChecked());
        response.setResponse3(q3.isChecked());
        response.setResponse4(q4.isChecked());
        response.setResponse5(q5.isChecked());
        response.setResponse6(q6.isChecked());
        response.setResponse7(q7.isChecked());
        response.setResponse8(q8.isChecked());
        response.setResponse9(q9.isChecked());
        response.setResponse10(q10.isChecked());
        response.setResponse11(q11.isChecked());
        response.setResponse12(q12.isChecked());
        response.setResponse13(q13.isChecked());
        response.setResponse14(q14.isChecked());
        response.setResponse15(q15.isChecked());
        response.setResponse16(q16.isChecked());
        response.setResponse17(q17.isChecked());
        response.setResponse18(q18.isChecked());
        response.setResponse19(q19.isChecked());
        response.setResponse20(q20.isChecked());
        response.setResponse21(q21.isChecked());
        response.setResponse22(q22.isChecked());
        response.setResponse23(q23.isChecked());
        response.setResponse24(q24.isChecked());
        response.setResponse25(q25.isChecked());
        response.setResponse26(q26.isChecked());
        response.setResponse27(q27.isChecked());
        response.setResponse28(q28.isChecked());
        response.setResponse29(q29.isChecked());
        response.setProviderId(ProviderInfo.getProvider().getProviderCode());
        return response;

    }

    public static FPExaminations setExamValues(Dialog dialog){
        FPExaminations fpExaminations = new FPExaminations();
        //setting EditTexts' values.............
        fpExaminations.setBpSystolic(((EditText) dialog.findViewById(R.id.fpBloodPressureValueSystolic)).getText().toString());
        fpExaminations.setBpDiastolic(((EditText) dialog.findViewById(R.id.fpBloodPressureValueDiastolic)).getText().toString());
        fpExaminations.setHemoglobin(((EditText) dialog.findViewById(R.id.fpHemoglobinValue)).getText().toString());

        //setting spinner values............
        fpExaminations.setAnemia(String.valueOf(((Spinner) dialog.findViewById(R.id.fpAnemiaSpinner)).getSelectedItemPosition()));
        fpExaminations.setJaundice(String.valueOf(((Spinner) dialog.findViewById(R.id.fpJaundiceSpinner)).getSelectedItemPosition()));
        fpExaminations.setBreastCondition(String.valueOf(((Spinner) dialog.findViewById(R.id.fpBreastConditionSpinner)).getSelectedItemPosition()));
        fpExaminations.setCervix(String.valueOf(((Spinner) dialog.findViewById(R.id.fpCervixSpinner)).getSelectedItemPosition()));
        fpExaminations.setMenstruation(String.valueOf(((Spinner) dialog.findViewById(R.id.fpMenstruationSpinner)).getSelectedItemPosition()));
        fpExaminations.setVaginalWall(String.valueOf(((Spinner) dialog.findViewById(R.id.fpVaginalWallSpinner)).getSelectedItemPosition()));
        fpExaminations.setCervicitis(String.valueOf(((Spinner) dialog.findViewById(R.id.fpCervicitisSpinner)).getSelectedItemPosition()));
        fpExaminations.setCervicalErosion(String.valueOf(((Spinner) dialog.findViewById(R.id.fpErosionSpinner)).getSelectedItemPosition()));
        fpExaminations.setCervicalPolyp(String.valueOf(((Spinner) dialog.findViewById(R.id.fpPolypSpinner)).getSelectedItemPosition()));
        fpExaminations.setContactBleeding(String.valueOf(((Spinner) dialog.findViewById(R.id.fpContactBleedingSpinner)).getSelectedItemPosition()));
        fpExaminations.setUterusSize(String.valueOf(((Spinner) dialog.findViewById(R.id.fpUterusSizeSpinner)).getSelectedItemPosition()));
        fpExaminations.setUterusShape(String.valueOf(((Spinner) dialog.findViewById(R.id.fpUterusShapeSpinner)).getSelectedItemPosition()));
        fpExaminations.setUterusPosition(String.valueOf(((Spinner) dialog.findViewById(R.id.fpUterusPositionSpinner)).getSelectedItemPosition()));
        fpExaminations.setUterusMovement(String.valueOf(((Spinner) dialog.findViewById(R.id.fpUterusMovementSpinner)).getSelectedItemPosition()));
        fpExaminations.setUterusMovementPain(String.valueOf(((Spinner) dialog.findViewById(R.id.fpUterusMovementPainSpinner)).getSelectedItemPosition()));
        fpExaminations.setVaginalFornix(String.valueOf(((Spinner) dialog.findViewById(R.id.fpFornixSpinner)).getSelectedItemPosition()));
        int currentlyPregnantVal = ((Spinner) dialog.findViewById(R.id.fpIsCurrentlyPregnantSpinner)).getSelectedItemPosition();
        fpExaminations.setCurrentlyPregnant(currentlyPregnantVal==2?true:false);
        fpExaminations.setUrineSugar(String.valueOf(((Spinner) dialog.findViewById(R.id.fpUrineSugarSpinner)).getSelectedItemPosition()));

        return fpExaminations;
    }

    public static ArrayList<Integer> getEligibleMethodArray(FPScreeningResponse questionnaire, FPExaminations exams, boolean isWoman, boolean isNulipara){
        FPScreeningEngine fpEngine= new FPScreeningEngine(questionnaire,exams);
        ArrayList<Integer> eligibleMethods = new ArrayList<>();
        if(!isWoman){
            if(!isNulipara){
                if(fpEngine.isPermanentForManEligible()) eligibleMethods.add(Flag.PERMANENT_METHOD_MAN);
            }
        }else{
            if(!isNulipara){
                if(fpEngine.isPermanentForWomanEligible()) eligibleMethods.add(Flag.PERMANENT_METHOD_WOMAN);
            }
            if(fpEngine.isImplantEligible()){
                eligibleMethods.add(Flag.IMPLANT_IMPLANON);
            }
            if(!isNulipara){if(fpEngine.isIudEligible()) eligibleMethods.add(Flag.IUD);
                if(fpEngine.isInjectableEligible()) eligibleMethods.add(Flag.INJECTION_DMPA);
            }
            if(fpEngine.isAponEligible())eligibleMethods.add(Flag.PILL_APON);
            if(fpEngine.isSukhiEligible()) eligibleMethods.add(Flag.PILL_SUKHI);
        }
        eligibleMethods.add(Flag.CONDOM);
        return eligibleMethods;
    }

    public static String[] getEligibleMethodArrayByName(FPScreeningResponse questionnaire, FPExaminations exams, boolean isWoman, boolean isNulipara){
        FPScreeningEngine fpEngine= new FPScreeningEngine(questionnaire,exams);
        ArrayList<String> eligibleMethods = new ArrayList<>();

        if(!isWoman){
            if(!isNulipara){
                if(fpEngine.isPermanentForManEligible()) eligibleMethods.add(Flag.PERMANENT_METHOD_MAN_TEXT);
            }

        }else{
            if(!isNulipara){
                if(fpEngine.isPermanentForWomanEligible()){
                    eligibleMethods.add(Flag.PERMANENT_METHOD_WOMAN_TEXT);
                }
            }
            if(fpEngine.isImplantEligible()) eligibleMethods.add(Flag.IMPLANT_TEXT);
            if(!isNulipara){
                if(fpEngine.isIudEligible()) eligibleMethods.add(Flag.IUD_TEXT);
                if(fpEngine.isInjectableEligible()) eligibleMethods.add(Flag.INJECTION_DMPA_TEXT);
            }
            if(fpEngine.isAponEligible())eligibleMethods.add(Flag.PILL_APON_TEXT);
            if(fpEngine.isSukhiEligible()) eligibleMethods.add(Flag.PILL_SUKHI_TEXT);
        }
        eligibleMethods.add(Flag.CONDOM_TEXT);

        String[] eligibleMethodNames = eligibleMethods.toArray(new String[eligibleMethods.size()]);
        return eligibleMethodNames;
    }

    public static void addRadioGroupListenerButton(Dialog dialog, ArrayList<Integer> eligibleMethodList){
        if(eligibleMethodList.contains(Flag.PILL_SUKHI)) {
            MethodUtils.setAllFirstChildCheckedListener((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningSukhi));
        }
        if(eligibleMethodList.contains(Flag.PILL_APON)) {
            MethodUtils.setAllFirstChildCheckedListener((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningApon));
        }
        if(eligibleMethodList.contains(Flag.INJECTION_DMPA)) {
            MethodUtils.setAllFirstChildCheckedListener((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningInjection));

        }
        if(eligibleMethodList.contains(Flag.IMPLANT_IMPLANON)) {
            MethodUtils.setAllFirstChildCheckedListener((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningImplant));
        }
        if(eligibleMethodList.contains(Flag.IUD)) {
            MethodUtils.setAllFirstChildCheckedListener((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningIUD));
        }
        if(eligibleMethodList.contains(Flag.PERMANENT_METHOD_MAN)) {
            MethodUtils.setAllFirstChildCheckedListener((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningPermanentMethodMale));
        }
        if(eligibleMethodList.contains(Flag.PERMANENT_METHOD_WOMAN)) {
            MethodUtils.setAllFirstChildCheckedListener((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningPermanentMethodFemale));
        }

    }

    public static boolean checkedAllFirstItem(Dialog dialog, ArrayList<Integer> eligibleMethodList){
        boolean isCounsellingDone = true;
        if(eligibleMethodList.contains(Flag.PILL_SUKHI)) {
            if(MethodUtils.hasUnselectedFirstChildRadioGroup((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningSukhi))){
                isCounsellingDone = false;
            }
        }
        if(eligibleMethodList.contains(Flag.PILL_APON)) {
            if(MethodUtils.hasUnselectedFirstChildRadioGroup((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningApon))){
                isCounsellingDone = false;
            }
        }
        if(eligibleMethodList.contains(Flag.INJECTION_DMPA)) {
            if(MethodUtils.hasUnselectedFirstChildRadioGroup((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningInjection))){
                isCounsellingDone = false;
            }
        }
        if(eligibleMethodList.contains(Flag.IMPLANT_IMPLANON)) {
            if(MethodUtils.hasUnselectedFirstChildRadioGroup((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningImplant))){
                isCounsellingDone = false;
            }
        }
        if(eligibleMethodList.contains(Flag.IUD)) {
            if(MethodUtils.hasUnselectedFirstChildRadioGroup((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningIUD))){

                isCounsellingDone = false;
            }
        }
        if(eligibleMethodList.contains(Flag.PERMANENT_METHOD_MAN)) {
            if(MethodUtils.hasUnselectedFirstChildRadioGroup((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningPermanentMethodMale))){

                isCounsellingDone = false;
            }
        }
        if(eligibleMethodList.contains(Flag.PERMANENT_METHOD_WOMAN)) {
            if(MethodUtils.hasUnselectedFirstChildRadioGroup((ViewGroup) dialog.findViewById(R.id.layoutFPScreeningPermanentMethodFemale))){

                isCounsellingDone = false;
            }
        }


        return isCounsellingDone;
    }

    /**
     * View Mapping as JSON
     */
    public static void prepareMapping(Dialog dialog){
        jsonSpinnerMap= new HashMap<>();
        jsonSpinnerMap.put("anemia",getSpinner(dialog,R.id.fpAnemiaSpinner));
        jsonSpinnerMap.put("jaundice",getSpinner(dialog,R.id.fpJaundiceSpinner));
        jsonSpinnerMap.put("breastCondition",getSpinner(dialog,R.id.fpBreastConditionSpinner));
        jsonSpinnerMap.put("cervix",getSpinner(dialog,R.id.fpCervixSpinner));
        jsonSpinnerMap.put("menstruation",getSpinner(dialog,R.id.fpMenstruationSpinner));
        jsonSpinnerMap.put("vaginalWall",getSpinner(dialog,R.id.fpVaginalWallSpinner));
        jsonSpinnerMap.put("cervicitis",getSpinner(dialog,R.id.fpCervicitisSpinner));
        jsonSpinnerMap.put("cervicalErosion",getSpinner(dialog,R.id.fpErosionSpinner));
        jsonSpinnerMap.put("cervicalPolyp",getSpinner(dialog,R.id.fpPolypSpinner));
        jsonSpinnerMap.put("contactBleeding",getSpinner(dialog,R.id.fpContactBleedingSpinner));
        jsonSpinnerMap.put("uterusSize",getSpinner(dialog,R.id.fpUterusSizeSpinner));
        jsonSpinnerMap.put("uterusShape",getSpinner(dialog,R.id.fpUterusShapeSpinner));
        jsonSpinnerMap.put("uterusPosition",getSpinner(dialog,R.id.fpUterusPositionSpinner));
        jsonSpinnerMap.put("uterusMovement",getSpinner(dialog,R.id.fpUterusMovementSpinner));
        jsonSpinnerMap.put("uterusCervixMovePain",getSpinner(dialog,R.id.fpUterusMovementPainSpinner));
        jsonSpinnerMap.put("vaginalFornix",getSpinner(dialog,R.id.fpFornixSpinner));
        jsonSpinnerMap.put("isCurrentlyPregnant",getSpinner(dialog,R.id.fpIsCurrentlyPregnantSpinner));
        jsonSpinnerMap.put("urineSugar",getSpinner(dialog,R.id.fpUrineSugarSpinner));
        jsonEditTextMap= new HashMap<>();
        jsonEditTextMap.put("bpSystolic",getEditText(dialog,R.id.fpBloodPressureValueSystolic));
        jsonEditTextMap.put("bpDiastolic",getEditText(dialog,R.id.fpBloodPressureValueDiastolic));
        jsonEditTextMap.put("hemoglobin",getEditText(dialog,R.id.fpHemoglobinValue));
    }

    public static void changeMandatorySignColor(Dialog dialog){
        getTextView(dialog,R.id.fpAnemiaLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(dialog,R.id.fpAnemiaLabel).getText().toString(), 0, 1));
        getTextView(dialog,R.id.fpJaundiceLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(dialog,R.id.fpJaundiceLabel).getText().toString(), 0, 1));
        getTextView(dialog,R.id.fpBloodPressureLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(dialog,R.id.fpBloodPressureLabel).getText().toString(), 0, 1));
        getTextView(dialog,R.id.fpBreastConditionLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(dialog,R.id.fpBreastConditionLabel).getText().toString(), 0, 1));

    }

    public static void setPreviousValuesByJson(JSONObject json){

        Utilities.setSpinners(jsonSpinnerMap, json);
        Utilities.setEditTexts(jsonEditTextMap, json);

    }

    private static Spinner getSpinner(Dialog dialog, int id){
        return (Spinner)dialog.findViewById(id);
    }
    private static EditText getEditText(Dialog dialog, int id){
        return (EditText)dialog.findViewById(id);
    }
    private static TextView getTextView(Dialog dialog, int id){
        return (TextView)dialog.findViewById(id);
    }


}
