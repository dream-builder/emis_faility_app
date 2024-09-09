package org.sci.rhis.fwc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.model.SymptomDataModel;
import org.sci.rhis.utilities.ChildCareLogicHelper;
import org.sci.rhis.utilities.ChildCareSymptomAdapter;
import org.sci.rhis.utilities.ConstantJSONs;
import org.sci.rhis.utilities.Utilities;

import java.util.ArrayList;

import static org.sci.rhis.utilities.Utilities.ConvertNumberToBangla;
import static org.sci.rhis.utilities.Utilities.gramToKg;


/**
 * Created by hajjaz.ibrahim on 3/14/2018.
 */

public class ChildCareFragmentSymptom extends CustomFragment {

    private static ChildCareFragmentSymptom instance = null;

    View fragmentView;
    Button btnSymptomPrevious, btnSymptomNext;
    CheckBox veryCriticalDisease1, veryCriticalDisease2, veryCriticalDisease3, veryCriticalDisease4, veryCriticalDisease5, veryCriticalDisease6, veryCriticalDisease7, veryCriticalDisease8, veryCriticalDisease9, veryCriticalDisease10, veryCriticalDisease11,
            veryCriticalBacteriaDisease1, veryCriticalBacteriaDisease2, veryCriticalBacteriaDisease3, veryCriticalBacteriaDisease4, veryCriticalBacteriaDisease5,
            veryCriticalPneumonia, pneumonia, localBacteriaDisease1, localBacteriaDisease2,
            childFeedingProblem, childVommiting, childItchy, childSenseless, childTooStable,
            otherSymptom0to59days, otherSymptom2Monthsto5years,
            lowerChestSit, fastBreathing2Mto1Y, fastBreathing1Yto5Y, normalCold2Mto5Y,
            severeDihydration1, severeDihydration2, severeDihydration3, severeDihydration4, severeDihydration5, severeDihydration6, severeDihydration7, severeDihydration8, severeDihydration9, severeDihydration10, severeDihydration11, severeDihydration12, earProblem1, earProblem2, earProblem3, feverCause1, feverCause2, feverCause3, feverCause4, feverCause5, feverCause6, feverCause7, feverCause8, feverCause9, feverCause10, feverCause11, feverCause12, feverCause13, feverCause14, measlesCause1, measlesCause2, measlesCause3;
    EditText otherDisease, ohterDisease2Mto5Y;
    RadioGroup jaundiceQuestion1, jaundiceQuestion2, jaundiceQuestion3, jaundiceQuestion4, zScoreMinus2, diarrhoea1, diarrhoea2, diarrhoea3, breastFeedingAttachment, breastSucking, diarrhoea4, diarrhoea5;
    public static boolean editMode = false;

    int age=0;

    private static final String TAG = "Child Care Symptom";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_childcare_symptom, container, false);
        initLayoutView(fragmentView);

        initFragment();

        if (ChildCareActivity.jsonObject != null) loadDataIfAny();

        return fragmentView;
    }


    private void loadDataIfAny() {
        setInputValues(ChildCareActivity.jsonObject);
    }

    //For calling onCreate method by force to get the JSON object send from Physical Examination Fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    public static ChildCareFragmentSymptom getInstance() {
        return instance;
    }

    //Layout loaded with respect to age
    public void initLayoutView(View view) {
        btnSymptomPrevious = (Button) fragmentView.findViewById(R.id.btnSymptomPrevious);
        btnSymptomNext = (Button) fragmentView.findViewById(R.id.btnSymptomNext);
        try {
            age = Integer.valueOf(ChildCareActivity.getJSONObject().getString("age"));


            //Hiding Visibility according to age
            if (age <= 59) {
                view.findViewById(R.id.layout_very_dangerous_two_months).setVisibility(View.GONE);
                view.findViewById(R.id.layout_pneumonia_2_months_to_5_years).setVisibility(View.GONE);
                view.findViewById(R.id.layout_normal_cold).setVisibility(View.GONE);
                view.findViewById(R.id.layout_diarrhoea_2_months).setVisibility(View.GONE);
                view.findViewById(R.id.layout_ear_problem_2_months).setVisibility(View.GONE);
                view.findViewById(R.id.layout_very_dangerous_fever_2_months).setVisibility(View.GONE);
                view.findViewById(R.id.layout_fever_maleria_2_months).setVisibility(View.GONE);
                view.findViewById(R.id.layout_fever_not_maleria_2_months).setVisibility(View.GONE);
                view.findViewById(R.id.layout_measles_2_months).setVisibility(View.GONE);
                view.findViewById(R.id.layout_malnutration_2_months).setVisibility(View.GONE);
                view.findViewById(R.id.layout_other_symptom_2_months).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.layout_very_dangerous_symptom).setVisibility(View.GONE);
                view.findViewById(R.id.layout_very_dangerous_bacteria).setVisibility(View.GONE);
                view.findViewById(R.id.layout_pneumonia_0_to_6_days).setVisibility(View.GONE);
                view.findViewById(R.id.layout_pneumonia_7_to_59_days).setVisibility(View.GONE);
                view.findViewById(R.id.layout_local_bacteria).setVisibility(View.GONE);
                view.findViewById(R.id.layout_other_symptom).setVisibility(View.GONE);
                view.findViewById(R.id.layout_jaundice).setVisibility(View.GONE);
                view.findViewById(R.id.layout_diarrhoea).setVisibility(View.GONE);
                view.findViewById(R.id.layout_low_weight).setVisibility(View.GONE);
                view.findViewById(R.id.layout_breast_feeding).setVisibility(View.GONE);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getInputValues(JSONObject jsonObject) {
        Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, jsonObject);
        Utilities.getRadioGroupButtonsArray(jsonRadioGroupButtonMapMultiple, jsonObject);
        Utilities.getEditTexts(jsonEditTextMap, jsonObject);
        Utilities.getCheckboxes(jsonCheckboxMap, jsonObject);
    }

    public void setInputValues(JSONObject jsonObject) {
        Utilities.setCheckboxes(jsonCheckboxMap, jsonObject);
        Utilities.setEditTexts(jsonEditTextMap, jsonObject);
        Utilities.setRadioGroupButtonsArray(jsonRadioGroupButtonMapMultiple, jsonObject);
        Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap, jsonObject);
    }

    public void initFragment() {

        initiateFields();

        initialize(); //super class

        if (editMode) {
            Utilities.Enable(getActivity(), fragmentView.findViewById(R.id.symptomContainerLayout));
        } else {
            Utilities.Disable(getActivity(),fragmentView.findViewById(R.id.symptomContainerLayout));
            Utilities.Enable(getActivity(),fragmentView.findViewById(R.id.btnSymptomPrevious));
            Utilities.Enable(getActivity(),fragmentView.findViewById(R.id.btnSymptomNext));
        }
        Log.i(TAG, ChildCareActivity.getJSONObject().toString());
        //Automatically Setting Checkbox value from Physical Examination Value (Breathing per minute) and disabling the other one
        if (ChildCareActivity.jsonObject.has("3")) {
            try {
                int breathingPerMinute = Integer.parseInt(ChildCareActivity.getJSONObject().getString("3"));
                age = Integer.valueOf(ChildCareActivity.getJSONObject().getString("age"));
                if (age < 60 && breathingPerMinute >= 60) {
                    if (age <= 6) //Checking if child is less than or equal 6 Days
                    {
                        if(((CheckBox) fragmentView.findViewById(R.id.childcaresymptom17checkbox)).getVisibility() == View.GONE)
                        {
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom17checkbox)).setVisibility(View.VISIBLE);
                        }
                        fragmentView.findViewById(R.id.childcaresymptom17checkbox).setEnabled(true);
                        ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom17checkbox)).setChecked(true);
                        fragmentView.findViewById(R.id.childcaresymptom18checkbox).setVisibility(View.GONE);
                    } else if (age > 6 && age < 60) {
                        if(((CheckBox) fragmentView.findViewById(R.id.childcaresymptom18checkbox)).getVisibility() == View.GONE)
                        {
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom18checkbox)).setVisibility(View.VISIBLE);
                        }
                        fragmentView.findViewById(R.id.childcaresymptom18checkbox).setEnabled(true);
                        ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom18checkbox)).setChecked(true);
                        fragmentView.findViewById(R.id.childcaresymptom17checkbox).setVisibility(View.GONE);
                    }
                } else if(age >= 60 && age <365 && breathingPerMinute >=50) {
                    if(((CheckBox) fragmentView.findViewById(R.id.childcaresymptom55checkbox)).getVisibility() == View.GONE)
                    {
                        ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom55checkbox)).setVisibility(View.VISIBLE);
                    }
                        fragmentView.findViewById(R.id.childcaresymptom55checkbox).setEnabled(true);
                        ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom55checkbox)).setChecked(true);
                        fragmentView.findViewById(R.id.childcaresymptom56Segement).setVisibility(View.GONE);

                } else if (age > 365 && breathingPerMinute >=40) {
                    if(((CheckBox) fragmentView.findViewById(R.id.childcaresymptom56checkbox)).getVisibility() == View.GONE)
                    {
                        ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom56checkbox)).setVisibility(View.VISIBLE);
                    }
                    fragmentView.findViewById(R.id.childcaresymptom56checkbox).setEnabled(true);
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom56checkbox)).setChecked(true);
                    fragmentView.findViewById(R.id.childcaresymptom55Segement).setVisibility(View.GONE);
                } else {
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom17checkbox)).setVisibility(View.GONE);
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom17checkbox)).setChecked(false);
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom18checkbox)).setVisibility(View.GONE);
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom18checkbox)).setChecked(false);
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom55checkbox)).setVisibility(View.GONE);
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom55checkbox)).setChecked(false);
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom56checkbox)).setVisibility(View.GONE);
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom56checkbox)).setChecked(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Auto selecting and Disabling Temperature Related Fields
        if (ChildCareActivity.jsonObject.has("1")) {
            try {
                double temp = Double.parseDouble(ChildCareActivity.getJSONObject().getString("1"));
                try {
                    age = Integer.valueOf(ChildCareActivity.getJSONObject().getString("age"));
                    if (age <= 59) {
                        if (temp >= 100.4) {
                            if(( fragmentView.findViewById(R.id.childcaresymptom13checkbox)).getVisibility() == View.GONE)
                            {
                                ( fragmentView.findViewById(R.id.childcaresymptom13checkbox)).setVisibility(View.VISIBLE);
                            }
                            if(((CheckBox) fragmentView.findViewById(R.id.childcaresymptom14checkbox)).isChecked())
                            {
                                ( fragmentView.findViewById(R.id.childcaresymptom14checkbox)).setVisibility(View.GONE);
                                ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom14checkbox)).setChecked(false);
                            }
                            fragmentView.findViewById(R.id.childcaresymptom13checkbox).setEnabled(true);
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom13checkbox)).setChecked(true);
                            Utilities.Disable(getActivity(), R.id.childcaresymptom14Segement);

                        } else if (temp <= 95.5) {
                            if(( fragmentView.findViewById(R.id.childcaresymptom14checkbox)).getVisibility() == View.GONE)
                            {
                                ( fragmentView.findViewById(R.id.childcaresymptom14checkbox)).setVisibility(View.VISIBLE);
                            }
                            if(((CheckBox) fragmentView.findViewById(R.id.childcaresymptom13checkbox)).isChecked())
                            {
                                ( fragmentView.findViewById(R.id.childcaresymptom13checkbox)).setVisibility(View.GONE);
                                ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom13checkbox)).setChecked(false);
                            }
                            fragmentView.findViewById(R.id.childcaresymptom14checkbox).setEnabled(true);
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom14checkbox)).setChecked(true);
                            Utilities.Disable(getActivity(), R.id.childcaresymptom13Segement);
                        } else {
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom13checkbox)).setChecked(false);
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom14checkbox)).setChecked(false);
                            fragmentView.findViewById(R.id.childcaresymptom13checkbox).setVisibility(View.GONE);
                            fragmentView.findViewById(R.id.childcaresymptom14checkbox).setVisibility(View.GONE);
                        }
                    } else if (age >= 60) {
                        if (temp >= 99.5) {
                            if(( fragmentView.findViewById(R.id.childcaresymptom81checkbox)).getVisibility() == View.GONE)
                            {
                                ( fragmentView.findViewById(R.id.childcaresymptom81checkbox)).setVisibility(View.VISIBLE);
                            }
                            if(( fragmentView.findViewById(R.id.childcaresymptom85checkbox)).getVisibility() == View.GONE)
                            {
                                ( fragmentView.findViewById(R.id.childcaresymptom85checkbox)).setVisibility(View.VISIBLE);
                            }
                            if(( fragmentView.findViewById(R.id.childcaresymptom91checkbox)).getVisibility() == View.GONE)
                            {
                                ( fragmentView.findViewById(R.id.childcaresymptom91checkbox)).setVisibility(View.VISIBLE);
                            }
                            if(( fragmentView.findViewById(R.id.childcaresymptom95checkbox)).getVisibility() == View.GONE)
                            {
                                ( fragmentView.findViewById(R.id.childcaresymptom95checkbox)).setVisibility(View.VISIBLE);
                            }
                            fragmentView.findViewById(R.id.childcaresymptom91checkbox).setEnabled(true);
                            fragmentView.findViewById(R.id.childcaresymptom85checkbox).setEnabled(true);
                            fragmentView.findViewById(R.id.childcaresymptom81checkbox).setEnabled(true);
                            fragmentView.findViewById(R.id.childcaresymptom95checkbox).setEnabled(true);
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom81checkbox)).setChecked(true);
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom85checkbox)).setChecked(true);
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom91checkbox)).setChecked(true);
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom95checkbox)).setChecked(true);
                        } else {
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom81checkbox)).setChecked(false);
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom85checkbox)).setChecked(false);
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom91checkbox)).setChecked(false);
                            ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom95checkbox)).setChecked(false);
                            ( fragmentView.findViewById(R.id.childcaresymptom81checkbox)).setVisibility(View.GONE);
                            ( fragmentView.findViewById(R.id.childcaresymptom85checkbox)).setVisibility(View.GONE);
                            ( fragmentView.findViewById(R.id.childcaresymptom91checkbox)).setVisibility(View.GONE);
                            ( fragmentView.findViewById(R.id.childcaresymptom95checkbox)).setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Showing EditText Field for Other Symptom (0-59 Days)
        ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom21checkbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    fragmentView.findViewById(R.id.childcaresymptom21EditText).setVisibility(View.VISIBLE);
                } else {
                    fragmentView.findViewById(R.id.childcaresymptom21EditText).setVisibility(View.GONE);
                }
            }
        });

        //Showing EditText Field for Other Symptom (2 Months - 5 Year)
        ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom103checkbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    fragmentView.findViewById(R.id.childcaresymptom103EditText).setVisibility(View.VISIBLE);
                } else {
                    fragmentView.findViewById(R.id.childcaresymptom103EditText).setVisibility(View.GONE);
                }
            }
        });

        ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom112AnswerOptionOnecheckbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom63AnswerOptionFourcheckbox)).setChecked(true);
                }
            }
        });

        ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom63AnswerOptionFourcheckbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    ((CheckBox) fragmentView.findViewById(R.id.childcaresymptom112AnswerOptionOnecheckbox)).setChecked(true);
                }
            }
        });

        //Symptom Segment Button Functionality
        btnSymptomPrevious.setOnClickListener(view -> ChildCareActivity.viewPager.setCurrentItem(0));
        btnSymptomNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInputValues(ChildCareActivity.jsonObject);
                setInputValues(ChildCareActivity.jsonObject);
                setPreview();
                ChildCareActivity.viewPager.setCurrentItem(2);
            }
        });
    }


    //For Showing Selected Input Values in Preview
    private void setPreview() {

        TextView tv = (TextView) ChildCareFragmentPreview.fragmentView.findViewById(R.id.previewLabel);
        String previewText = "",physicalVal = "";

        try {
            JSONObject jsonLogic = ChildCareLogicHelper.getClassifications(ChildCareActivity.getJSONObject());
            if (jsonLogic != null) {
                physicalVal +=" শারীরিক পরীক্ষাঃ\n তাপমাত্রাঃ "+ConvertNumberToBangla(ChildCareActivity.getJSONObject().getString("1"))+"\u00B0"+"ফা \n ওজনঃ "+
                        ConvertNumberToBangla(gramToKg(ChildCareActivity.getJSONObject().getString("2")))+"\n শ্বাস মিনিটেঃ "+
                        ConvertNumberToBangla(ChildCareActivity.getJSONObject().getString("3"))+ " বার\n";
                previewText += physicalVal;
                previewText += "\n" + jsonLogic.getString("previewText");

                ChildCareActivity.jsonObject.put("classifications", jsonLogic.getString("classifications"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv.setText(previewText);

    }

    //Binding layout elements
    private void initiateFields() {

        //Very Critical Disease 0-59 Days
        veryCriticalDisease1 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom1checkbox);
        veryCriticalDisease2 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom2checkbox);
        veryCriticalDisease3 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom3checkbox);
        veryCriticalDisease4 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom4checkbox);
        veryCriticalDisease5 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom5checkbox);
        veryCriticalDisease6 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom6checkbox);
        veryCriticalDisease7 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom7checkbox);
        veryCriticalDisease8 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom8checkbox);
        veryCriticalDisease9 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom9checkbox);
        veryCriticalDisease10 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom10checkbox);
        veryCriticalDisease11 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom11checkbox);

        //Very Critical Bacteria 0-59 Days
        veryCriticalBacteriaDisease1 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom12checkbox);
        veryCriticalBacteriaDisease2 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom13checkbox);
        veryCriticalBacteriaDisease3 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom14checkbox);
        veryCriticalBacteriaDisease4 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom15checkbox);
        veryCriticalBacteriaDisease5 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom16checkbox);

        //Pneumonia 0-6 days
        veryCriticalPneumonia = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom17checkbox);

        //Pneumonia 7-59 days
        pneumonia = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom18checkbox);

        //Local Bacteria 0-59 Days
        localBacteriaDisease1 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom19checkbox);
        localBacteriaDisease2 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom20checkbox);

        //Other Disease 0-59 Days
        otherSymptom0to59days = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom21checkbox);
        otherDisease = (EditText) fragmentView.findViewById(R.id.childcaresymptom21EditText);

        //jaundice 0-59 days
        jaundiceQuestion1 = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom23RadioGroup);
        jaundiceQuestion2 = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom24RadioGroup);
        jaundiceQuestion3 = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom25RadioGroup);
        jaundiceQuestion4 = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom26RadioGroup);

        //diarrhoea 0-59 Days
        diarrhoea1 = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom28RadioGroup);
        diarrhoea2 = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom31RadioGroup);
        diarrhoea3 = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom32RadioGroup);
        diarrhoea4 = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptomDiarrhoeaRadioGroup);
        diarrhoea5 = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptomDiarrhoeaRadioGroup2);

        //Eating Problem 0-59 Days
        breastFeedingAttachment = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom39RadioGroup);
        breastSucking = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom41RadioGroup);


        //2 Months to 5 Years
        //Critical Disease 2M to 5Y
        childFeedingProblem = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom49checkbox);
        childVommiting = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom50checkbox);
        childItchy = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom51checkbox);
        childSenseless = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom52checkbox);
        childTooStable = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom53checkbox);

        //Pneumonia 2M to 5Y
        lowerChestSit = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom54checkbox);
        fastBreathing2Mto1Y = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom55checkbox);
        fastBreathing1Yto5Y = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom56checkbox);

        //Normal Cold
        normalCold2Mto5Y = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom57checkbox);

        //diarrhoea
        severeDihydration1 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom111AnswerOptionOnecheckbox);
        severeDihydration2 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom112AnswerOptionOnecheckbox);
        severeDihydration3 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom113AnswerOptionOnecheckbox);
        severeDihydration4 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom114AnswerOptionOnecheckbox);

        severeDihydration5 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom63AnswerOptionOnecheckbox);
        severeDihydration6 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom63AnswerOptionTwocheckbox);
        severeDihydration7 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom63AnswerOptionThreecheckbox);
        severeDihydration8 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom63AnswerOptionFourcheckbox);
        severeDihydration9 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom67AnswerOptionOnecheckbox);
        severeDihydration10 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom69AnswerOptionOnecheckbox);
        severeDihydration11 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom71AnswerOptionOnecheckbox);
        severeDihydration12 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom73AnswerOptionOnecheckbox);

        //Ear Problem
        earProblem1 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom75AnswerOptionOnecheckbox);
        earProblem2 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom77AnswerOptionOnecheckbox);
        earProblem3 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom79AnswerOptionOnecheckbox);

        //Fever Related Issue
        feverCause1 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom81checkbox);
        feverCause2 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom82checkbox);
        //feverCause3 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom83checkbox);
        feverCause4 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom84checkbox);
        feverCause5 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom85checkbox);
        feverCause6 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom86checkbox);
        feverCause7 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom87checkbox);
        feverCause8 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom88checkbox);
        feverCause9 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom89checkbox);
        feverCause10 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom90checkbox);
        feverCause11 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom91checkbox);
        feverCause12 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom92checkbox);
        feverCause13 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom93checkbox);
        feverCause14 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom94checkbox);


        //Measles
        measlesCause1 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom95checkbox);
        measlesCause2 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom96checkbox);
        measlesCause3 = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom97checkbox);

        //Malnutrition
        zScoreMinus2 = (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom101RadioGroup);

        //Other
        otherSymptom2Monthsto5years = (CheckBox) fragmentView.findViewById(R.id.childcaresymptom103checkbox);
        ohterDisease2Mto5Y = (EditText) fragmentView.findViewById(R.id.childcaresymptom103EditText);
    }

    @Override
    protected void initiateCheckboxes() {

        jsonCheckboxMap.put("4", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom1checkbox));
        jsonCheckboxMap.put("5", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom2checkbox));
        jsonCheckboxMap.put("6", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom3checkbox));
        jsonCheckboxMap.put("7", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom4checkbox));
        jsonCheckboxMap.put("8", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom5checkbox));
        jsonCheckboxMap.put("9", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom6checkbox));
        jsonCheckboxMap.put("10", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom7checkbox));
        jsonCheckboxMap.put("11", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom8checkbox));
        jsonCheckboxMap.put("12", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom9checkbox));
        jsonCheckboxMap.put("13", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom10checkbox));
        jsonCheckboxMap.put("14", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom11checkbox));

        //Severe Illness Bacterial Infection ( 0 - 59 Days)
        jsonCheckboxMap.put("15", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom12checkbox));
        jsonCheckboxMap.put("16", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom13checkbox));
        jsonCheckboxMap.put("17", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom14checkbox));
        jsonCheckboxMap.put("18", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom16checkbox));
        jsonCheckboxMap.put("19", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom15checkbox));
        jsonCheckboxMap.put("20", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom13Bacteriacheckbox));

        //Severe Illness Pneumonia ( 0 - 59 Days)
        jsonCheckboxMap.put("21", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom17checkbox));

        //Pneumonia ( 0 - 59 Days)
        jsonCheckboxMap.put("22", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom18checkbox));

        //Local Bacterial Infection ( 0 - 59 Days)
        jsonCheckboxMap.put("23", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom19checkbox));
        jsonCheckboxMap.put("24", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom20checkbox));

        //No serious illness / Others ( 0 - 59 Days)
        jsonCheckboxMap.put("25", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom21checkbox));

        //( 2 Months - 5 years)
        //Severe Illness ( 2 Months - 5 years)
        jsonCheckboxMap.put("45", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom49checkbox));
        jsonCheckboxMap.put("46", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom50checkbox));
        jsonCheckboxMap.put("47", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom51checkbox));
        jsonCheckboxMap.put("48", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom52checkbox));
        jsonCheckboxMap.put("49", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom53checkbox));

        //Pneumonia ( 2 Months - 5 years)
        jsonCheckboxMap.put("50", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom54checkbox));
        jsonCheckboxMap.put("51", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom55checkbox));
        jsonCheckboxMap.put("52", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom56checkbox));

        //Cold ( 2 Months - 5 years)
        jsonCheckboxMap.put("53", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom57checkbox));

        //Diarrhoea ( 2 Months - 5 years)
        jsonCheckboxMap.put("54", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom111AnswerOptionOnecheckbox));
        jsonCheckboxMap.put("55", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom112AnswerOptionOnecheckbox));
        jsonCheckboxMap.put("56", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom113AnswerOptionOnecheckbox));
        jsonCheckboxMap.put("57", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom114AnswerOptionOnecheckbox));
        jsonCheckboxMap.put("58", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom63AnswerOptionOnecheckbox));
        jsonCheckboxMap.put("59", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom63AnswerOptionTwocheckbox));
        jsonCheckboxMap.put("60", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom63AnswerOptionThreecheckbox));
        jsonCheckboxMap.put("55", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom63AnswerOptionFourcheckbox));
        jsonCheckboxMap.put("61", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom67AnswerOptionOnecheckbox));
        jsonCheckboxMap.put("62", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom69AnswerOptionOnecheckbox));
        jsonCheckboxMap.put("63", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom71AnswerOptionOnecheckbox));
        jsonCheckboxMap.put("64", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom73AnswerOptionOnecheckbox));


        //Ear Problem ( 2 Months - 5 years)
        jsonCheckboxMap.put("65", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom75AnswerOptionOnecheckbox));
        jsonCheckboxMap.put("66", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom77AnswerOptionOnecheckbox));
        jsonCheckboxMap.put("67", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom79AnswerOptionOnecheckbox));

        //Severe Fever ( 2 Months - 5 years)
        jsonCheckboxMap.put("68", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom81checkbox));
        jsonCheckboxMap.put("69", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom82checkbox));
        jsonCheckboxMap.put("70", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom84checkbox));

        //Fever Malaria ( 2 Months - 5 years)
        jsonCheckboxMap.put("68", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom85checkbox));
        jsonCheckboxMap.put("71", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom86checkbox));
        jsonCheckboxMap.put("72", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom87checkbox));
        jsonCheckboxMap.put("73", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom88checkbox));
        jsonCheckboxMap.put("74", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom89checkbox));
        jsonCheckboxMap.put("75", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom90checkbox));

        //Fever Not Malaria ( 2 Months - 5 years)
        jsonCheckboxMap.put("68", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom91checkbox));
        jsonCheckboxMap.put("76", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom92checkbox));
        jsonCheckboxMap.put("77", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom93checkbox));
        jsonCheckboxMap.put("78", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom94checkbox));

        //Measles ( 2 Months - 5 years)
        jsonCheckboxMap.put("68", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom95checkbox));
        jsonCheckboxMap.put("79", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom96checkbox));
        jsonCheckboxMap.put("80", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom97checkbox));
        jsonCheckboxMap.put("81", (CheckBox) fragmentView.findViewById(R.id.severeMeaslesCheckbox));
        jsonCheckboxMap.put("82", (CheckBox) fragmentView.findViewById(R.id.nonSevereMeaslesCheckbox));

        //Malnutrition (2 Months - 5 Year)
        jsonCheckboxMap.put("83", (CheckBox) fragmentView.findViewById(R.id.malNutritionSevere1));
        jsonCheckboxMap.put("84", (CheckBox) fragmentView.findViewById(R.id.malNutritionSevere2));
        jsonCheckboxMap.put("152", (CheckBox) fragmentView.findViewById(R.id.malNutritionSevere3));
        jsonCheckboxMap.put("85", (CheckBox) fragmentView.findViewById(R.id.malNutritionLow1));
        jsonCheckboxMap.put("86", (CheckBox) fragmentView.findViewById(R.id.malNutrition));

        //Other Illness ( 2 Months - 5 years)
        jsonCheckboxMap.put("89", (CheckBox) fragmentView.findViewById(R.id.childcaresymptom103checkbox));


    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("26", (EditText) fragmentView.findViewById(R.id.childcaresymptom21EditText));
        jsonEditTextMap.put("90", (EditText) fragmentView.findViewById(R.id.childcaresymptom103EditText));
    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateSpinners() {

    }

    @Override
    protected void initiateMultiSelectionSpinners() {

    }

    @Override
    protected void initiateEditTextDates() {

    }

    @Override
    protected void initiateRadioGroups() {

        jsonRadioGroupButtonMap.put("27", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom23RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom23RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom23RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("28", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom24RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom24RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom24RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("29", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom25RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom25RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom25RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("30", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom26RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom26RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom26RadioGroupNo)
                )
                )
        );

        //diarrhoea 0-59 Days
        jsonRadioGroupButtonMap.put("31", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptomDiarrhoeaRadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptomDiarrhoeaRadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptomDiarrhoeaRadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("32", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptomDiarrhoeaRadioGroup2), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptomDiarrhoeaRadioGroup2Yes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptomDiarrhoeaRadioGroup2No)
                )
                )
        );


        jsonRadioGroupButtonMap.put("33", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom28RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom28RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom28RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("34", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom31RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom31RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom31RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("35", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom32RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom32RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom32RadioGroupNo)
                )
                )
        );

        //eating problem 0-59 days
        jsonRadioGroupButtonMap.put("36", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom33RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom33RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom33RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("37", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom34RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom34RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom34RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("38", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom35RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom35RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom35RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("39", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom36RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom36RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom36RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("40", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom37RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom37RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom37RadioGroupNo)
                )
                )
        );

        //breast feeding 0-59 days
        jsonRadioGroupButtonMap.put("41", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom38RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom38RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom38RadioGroupNo)
                )
                )
        );


        jsonRadioGroupButtonMapMultiple.put("42", Pair.create(
                breastFeedingAttachment, new RadioButton[]{
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom39RadioGroupOne),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom39RadioGroupTwo),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom39RadioGroupThree)
                }
                )
        );

        jsonRadioGroupButtonMap.put("43", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresymptom40RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom40RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom40RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMapMultiple.put("44", Pair.create(
                breastSucking, new RadioButton[]{
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom41RadioGroupOne),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom41RadioGroupTwo),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom41RadioGroupThree)
                }
                )
        );



        jsonRadioGroupButtonMap.put("87", Pair.create(
                (RadioGroup) fragmentView.findViewById(R.id.childcaresympto100RadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom100RadioGroupYes),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom100RadioGroupNo)
                )
                )
        );

        jsonRadioGroupButtonMapMultiple.put("88", Pair.create(
                zScoreMinus2, new RadioButton[]{
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom101RadioGroupOne),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom101RadioGroupTwo),
                        (RadioButton) fragmentView.findViewById(R.id.childcaresymptom101RadioGroupThree)
                }
                )
        );


    }

}
