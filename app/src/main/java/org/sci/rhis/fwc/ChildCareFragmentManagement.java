package org.sci.rhis.fwc;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.ChildCareLogicHelper;
import org.sci.rhis.utilities.ConstantJSONs;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.Utilities;

import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.sci.rhis.utilities.Utilities.ConvertNumberToBangla;
import static org.sci.rhis.utilities.Utilities.getDateStringUIFormat;


/**
 * Created by hajjaz.ibrahim on 3/14/2018.
 */

public class ChildCareFragmentManagement extends CustomFragment {

    public static View fragmentView;
    private MultiSelectionSpinner multiSelectionSpinner;
    private static final String TAG = "Final JSON";
    private int countSaveClick = 0;
    private int age, weight;
    private Spinner spinner;
    private ArrayList<Integer> referredClArray = new ArrayList<>();

    String gentamicinDose, amoxicillinDose, jentamycinFollowupDate, visitdate, kotramoxasolfollowupdate, followupDate;
    List<String> advicelist, referReason;

    Button btnEPIPrevious, btnChildRegisterSave, btnEPIEdit;
    Context context;
    LinearLayout layoutClassificationOTReferTreatment, layoutInjectionJentamycinSegment, layoutAmoxilinSegment,
            layoutSevereJaundiceTreatmentSegment, layoutInjectionGentamicinForNonRefer, layoutAmoxacinDropForNonReferSegmentOne,
            layoutAmoxacinDropForNonReferSegmentTwo, layoutFollowupNonReferSegment, layoutNoEatingOrLowWeightTreatmentSegment,
            layoutEatingOrLowWeightTreatmentSegment, diarrhoeaSevereDehydrationLayout, severeDehydrationTreatmentPartOne,
            diarrhoeaSevereDehydrationQuestionTwoLayout, severeDehydrationTreatmentPartTwo, diarrhoeaSevereDehydrationQuestionThreeLayout,
            severeDehydrationTreatmentPartThreeFour, diarrhoeaSevereDehydrationQuestionFourLayout,
            severeDehydrationTreatmentPartLast, severeDehydrationTreatment, diarrhoeaSomeDehydrationLayout,
            childReferCenterNameLayout, childReferReasonLayout, layoutNoDehydrationTreatmentSegment,
            layoutClassification6, layoutClassification12ReferAdvice, layoutClassification12NonReferAdvice, layoutClassification34NonReferAdvice,
            layoutClassification5Advice, layoutClassification8Advice, layoutFirstFollowupNonReferSegment,
            layoutSecondFollowupNonReferSegment, layoutpneumonia2mto5y, layoutClassification17Refer,
            layoutClassification17Advice, earproblem2mto5yC1, layoutClassification26NonRefer, layoutClassification27NonRefer,
            layoutClassification20Refer, classification21SegmentRefer, layoutClassification21NonRefer,
            layoutClassification23, layoutClassification24, layoutClassification30, layoutClassification31,
            layoutClassification32Advice, layoutClassification35, layoutClassification36, layoutClassification37,
            layoutClassification15Advice, layoutClassification16Advice, layoutClassification13Advice,
            layoutSomeDehydrationAdvice2Months, someDehydrationUnder2Months, noDehydrationUnder2Months, noDehydrationAbove2Months;
    EditText childCareCommentEditText, classification6TreatmentEditText;
    TextView textViewReferTreatmentLabel, textAmoxacinDropForNonReferSegmentTwo, injectionJentamycinDose,
            textViewAmoxacilinDropQuantityLabel, textViewInjectionJentamycinDoseNonReferLabel,
            textViewAmoxacilinDropQuantityNonReferLabel, textViewInjectionJentamycinFollowupDoseDate,
            diarrhoeaSomeDehydrationTreatmentOrsalaineQuantity, textKotraiMoxelTablet2Mto12M, textKotraiMoxelSyrup1Yto5Y,
            textAmoxacilinTablet2Mto12M, textAmoxacilin1Yto3Y, textAmoxacilin3Yto5Y, textPneumoniaFollowup, c20DoseLabel, c20DoseLabel2,
            severeDehydrationTreatmentPartOneTreatmentFourText, severeDehydrationTreatmentPartOneTreatmentFiveText,
            severeDehydrationTreatmentPartOneTreatmentTwoText, severeDehydrationTreatmentPartOneTreatmentThreeText,
            classification21NonReferMedicine2, classification21NonReferMedicine1, classification23Label,
            classification28Label, classification29Label, classification23Medicine1, classification24DoseLabel,
            classification30DoseLabel, diarrhoeaSevereDehydrationDose, classification37Dose, text1;

    RadioButton radioButtonReferYes, radioButtonReferNo, diarrhoeaSevereDehydrationQuestionOneRadioButtonYes,
            diarrhoeaSevereDehydrationQuestionOneRadioButtonNo, radioButtonReferAgreeYes,
            radioButtonReferAgreeNo, diarrhoeaSevereDehydrationQuestionTwoRadioButtonYes,
            diarrhoeaSevereDehydrationQuestionTwoRadioButtonNo, diarrhoeaSevereDehydrationQuestionThreeRadioButtonYes,
            diarrhoeaSevereDehydrationQuestionThreeRadioButtonNo;
    RadioGroup radioGroupRefer, diarrhoeaSevereDehydrationQuestionOneRadioGroup, radioGroupReferDecision,
            diarrhoeaSevereDehydrationQuestionTwoRadioGroup, diarrhoeaSevereDehydrationQuestionThreeRadioGroup,
            diarrhoeaSevereDehydrationQuestionFourRadioGroup, psbiFirstFollowupRadioGroup, psbiSecondFollowupRadioGroup;
    RelativeLayout referStatusLayout, referChild;
    CheckBox severeDehydrationTreatmentCheckbox, injectiongentamicinDoseGiven, injectionamoxicillinDoseGiven,
            tabKotraiMoxelDoseGiven, injectionamoxicillinDoseGivenSctionTwo, severeJaundiceTreatmentcheckbox,
            someDehydrationTreatmentTreatmentOneCheckbox, someDehydrationTreatmentTreatmentTwoCheckbox,
            someDehydrationTreatmentTreatmentThreeCheckbox, someDehydrationTreatmentTreatmentFourCheckbox,
            noDehydrationTreatmentcheckboxtwo, noDehydrationTreatmentcheckboxthree, eatingOrLowWeightTreatmentcheckboxThree,
            injectiongentamicinSecondDoseGiven, advicegivencheckbox, pneumonia2Mto5YTreatment2checkbox,
            kotraimoxaxolinSecondDoseGiven, c21T1, c22T2, c23T2, c23T3, c28T3, c28T4, c23T4, c32T1, c31T3,
            c30T2, referDiarrhoeaSevere, doseGiven, referSomeDehydration, c15Treatment;


    public static boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_childcare_management, container, false);
        initFragment();

        if (ChildCareActivity.jsonObject != null) loadDataIfAny();
        setReferredClassification();

        return fragmentView;
    }

    private void loadDataIfAny() {
        setInputValues(ChildCareActivity.jsonObject);
    }

    private void initFragment() {
        initiateFields();


        btnEPIPrevious.setOnClickListener((View view) -> {
            ChildCareActivity.viewPager.setCurrentItem(3);
        });

        btnChildRegisterSave.setOnClickListener((View view) -> {
            getInputValues(ChildCareActivity.getJSONObject());

            countSaveClick++;
            if (countSaveClick == 1) {
                btnChildRegisterSave.setText(getText(R.string.string_confirm));
                btnEPIEdit.setVisibility(View.VISIBLE);
                btnEPIPrevious.setVisibility(View.GONE);
                btnEPIEdit.setText(getText(R.string.string_cancel));
                Toast.makeText(getActivity(), R.string.DeliverySavePrompt, Toast.LENGTH_LONG).show();
                btnEPIEdit.setOnClickListener((View view2) -> {
                    countSaveClick = 0;
                    btnEPIEdit.setVisibility(View.GONE);
                    btnChildRegisterSave.setText(getText(R.string.string_save));
                });
            } else if (countSaveClick == 2) {
                ((ChildCareActivity) getActivity()).childSaveAsJson();
                countSaveClick = 0;
            }
        });

        initialize(); //super class
        setTreatmentLayout();
        setInputValues(ChildCareActivity.getJSONObject());
        //show/hide refer section
        if(!ChildCareLogicHelper.isReferRequired(ChildCareActivity.getJSONObject())){
            hideReferSegment();
        }else{
            (fragmentView.findViewById(R.id.layoutReferSegment)).setVisibility(View.VISIBLE);
        }


        if (editMode) {
            Utilities.Enable(getActivity(), fragmentView.findViewById(R.id.containerEPILayout));
        } else {
            Utilities.Disable(getActivity(), fragmentView.findViewById(R.id.containerEPILayout));
        }

        if (ChildCareActivity.onEditMode) {
            btnChildRegisterSave.setText("Update");
        } else {
            btnChildRegisterSave.setText("Save");
            ChildCareActivity.onEditMode = false;
        }

        if (ChildCareActivity.onViewMode) {
            btnEPIPrevious.setEnabled(true);
            btnEPIPrevious.setClickable(true);
            btnChildRegisterSave.setVisibility(View.GONE);
        } else {
            btnChildRegisterSave.setEnabled(true);
        }

        ((RadioButton) fragmentView.findViewById(R.id.childReferRadioButtonYes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childReferCenterNameLayout.setVisibility(View.VISIBLE);
                childReferReasonLayout.setVisibility(View.VISIBLE);
                referStatusLayout.setVisibility(View.VISIBLE);
            }
        });

        ((RadioButton) fragmentView.findViewById(R.id.childReferRadioButtonNo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childReferCenterNameLayout.setVisibility(View.GONE);
                childReferReasonLayout.setVisibility(View.GONE);
                referStatusLayout.setVisibility(View.GONE);
            }
        });


    }


    /**
     * initiating the layout files
     */
    private void initiateFields() {

        //Button Segment
        btnEPIPrevious = (Button) fragmentView.findViewById(R.id.btnEPIPrevious);
        btnChildRegisterSave = (Button) fragmentView.findViewById(R.id.btnEPINext);
        btnEPIEdit = (Button) fragmentView.findViewById(R.id.btnEPIEdit);

        //Classification 1 & 2 Refer
        textViewReferTreatmentLabel = (TextView) fragmentView.findViewById(R.id.textViewReferTreatmentLabel);
        layoutClassificationOTReferTreatment = (LinearLayout) fragmentView.findViewById(R.id.layoutClassificationOTReferTreatment);
        layoutInjectionJentamycinSegment = (LinearLayout) fragmentView.findViewById(R.id.layoutInjectionJentamysinSegment);
        layoutAmoxilinSegment = (LinearLayout) fragmentView.findViewById(R.id.layoutAmoxacilinDropSegment);
        injectionJentamycinDose = (TextView) fragmentView.findViewById(R.id.textViewInjectionJentamycinDoseLabel);
        textViewAmoxacilinDropQuantityLabel = (TextView) ChildCareFragmentManagement.fragmentView.findViewById(R.id.textViewAmoxacilinDropQuantityLabel);
        layoutClassification12ReferAdvice = (LinearLayout) ChildCareFragmentManagement.fragmentView.findViewById(R.id.layoutClassification12ReferAdvice);
        layoutClassification12NonReferAdvice = (LinearLayout) ChildCareFragmentManagement.fragmentView.findViewById(R.id.layoutClassification12NonReferAdvice);

        //Classification 2 Non Refer Injection
        layoutInjectionGentamicinForNonRefer = (LinearLayout) fragmentView.findViewById(R.id.layoutInjectionGentamicinForNonRefer);
        textViewInjectionJentamycinDoseNonReferLabel = (TextView) ChildCareFragmentManagement.fragmentView.findViewById(R.id.textViewInjectionJentamycinDoseNonReferLabel);
        textViewInjectionJentamycinFollowupDoseDate = (TextView) fragmentView.findViewById(R.id.textViewInjectionJentamycinFollowupDoseDate);
        textAmoxacinDropForNonReferSegmentTwo = (TextView) fragmentView.findViewById(R.id.textAmoxacinDropForNonReferSegmentTwo);
        injectiongentamicinSecondDoseGiven = (CheckBox) fragmentView.findViewById(R.id.injectiongentamicinSecondDoseGiven);

        //Classification 2,3,4 Non Refer Treatment with Followup
        layoutAmoxacinDropForNonReferSegmentOne = (LinearLayout) fragmentView.findViewById(R.id.layoutAmoxacinDropForNonReferSegmentOne);
        layoutAmoxacinDropForNonReferSegmentTwo = (LinearLayout) fragmentView.findViewById(R.id.layoutAmoxacinDropForNonReferSegmentTwo);
        textViewAmoxacilinDropQuantityNonReferLabel = (TextView) ChildCareFragmentManagement.fragmentView.findViewById(R.id.textViewAmoxacilinDropQuantityNonReferLabel);

        //Classification 2,3,4,5 followup and advice segment
        layoutFollowupNonReferSegment = (LinearLayout) fragmentView.findViewById(R.id.layoutFollowupNonReferSegment);
        layoutFirstFollowupNonReferSegment = (LinearLayout) fragmentView.findViewById(R.id.layoutFirstFollowupNonReferSegment);
        layoutSecondFollowupNonReferSegment = (LinearLayout) fragmentView.findViewById(R.id.layoutSecondFollowupNonReferSegment);
        psbiFirstFollowupRadioGroup = (RadioGroup) fragmentView.findViewById(R.id.psbiFirstFollowupRadioGroup);
        psbiSecondFollowupRadioGroup = (RadioGroup) fragmentView.findViewById(R.id.psbiSecondFollowupRadioGroup);
        layoutClassification34NonReferAdvice = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification34NonReferAdvice);
        layoutClassification5Advice = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification5Advice);

        //Antibiotics treatment Given
        injectionamoxicillinDoseGiven = (CheckBox) fragmentView.findViewById(R.id.injectionamoxicillinDoseGiven);
        injectiongentamicinDoseGiven = (CheckBox) fragmentView.findViewById(R.id.injectiongentamicinDoseGiven);

        //Classification 6 Segment
        layoutClassification6 = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification6);
        classification6TreatmentEditText = (EditText) fragmentView.findViewById(R.id.classification6TreatmentEditText);

        //Classification 7 Segment
        layoutSevereJaundiceTreatmentSegment = (LinearLayout) fragmentView.findViewById(R.id.layoutSevereJaundiceTreatmentSegment);
        severeJaundiceTreatmentcheckbox = (CheckBox) fragmentView.findViewById(R.id.severeJaundiceTreatmentcheckbox);

        //Classification 8 Segment
        layoutClassification8Advice = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification8Advice);

        //Classification 9 Segment
        diarrhoeaSevereDehydrationLayout = (LinearLayout) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationLayout);
        severeDehydrationTreatmentPartOne = (LinearLayout) fragmentView.findViewById(R.id.severeDehydrationTreatmentPartOne);
        diarrhoeaSevereDehydrationQuestionTwoLayout = (LinearLayout) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionTwoLayout);
        severeDehydrationTreatmentPartTwo = (LinearLayout) fragmentView.findViewById(R.id.severeDehydrationTreatmentPartTwo);
        diarrhoeaSevereDehydrationQuestionThreeLayout = (LinearLayout) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionThreeLayout);
        severeDehydrationTreatmentPartThreeFour = (LinearLayout) fragmentView.findViewById(R.id.severeDehydrationTreatmentPartThreeFour);
        diarrhoeaSevereDehydrationQuestionFourLayout = (LinearLayout) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionFourLayout);
        severeDehydrationTreatment = (LinearLayout) fragmentView.findViewById(R.id.severeDehydrationTreatment);
        severeDehydrationTreatmentPartLast = (LinearLayout) fragmentView.findViewById(R.id.severeDehydrationTreatmentPartLast);
        diarrhoeaSevereDehydrationQuestionOneRadioGroup = (RadioGroup) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionOneRadioGroup);
        diarrhoeaSevereDehydrationQuestionFourRadioGroup = (RadioGroup) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionFourRadioGroup);
        diarrhoeaSevereDehydrationQuestionOneRadioButtonYes = (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionOneRadioButtonYes);
        diarrhoeaSevereDehydrationQuestionOneRadioButtonNo = (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionOneRadioButtonNo);
        diarrhoeaSevereDehydrationQuestionTwoRadioButtonYes = (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionTwoRadioButtonYes);
        diarrhoeaSevereDehydrationQuestionTwoRadioButtonNo = (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionTwoRadioButtonNo);
        diarrhoeaSevereDehydrationQuestionThreeRadioButtonYes = (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionThreeRadioButtonYes);
        diarrhoeaSevereDehydrationQuestionThreeRadioButtonNo = (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionThreeRadioButtonNo);
        diarrhoeaSevereDehydrationQuestionTwoRadioGroup = (RadioGroup) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionTwoRadioGroup);
        diarrhoeaSevereDehydrationQuestionThreeRadioGroup = (RadioGroup) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionThreeRadioGroup);
        severeDehydrationTreatmentCheckbox = (CheckBox) fragmentView.findViewById(R.id.severeDehydrationTreatmentCheckbox);
        severeDehydrationTreatmentPartOneTreatmentThreeText = (TextView) fragmentView.findViewById(R.id.severeDehydrationTreatmentPartOneTreatmentThreeText);
        severeDehydrationTreatmentPartOneTreatmentTwoText = (TextView) fragmentView.findViewById(R.id.severeDehydrationTreatmentPartOneTreatmentTwoText);
        severeDehydrationTreatmentPartOneTreatmentFiveText = (TextView) fragmentView.findViewById(R.id.severeDehydrationTreatmentPartOneTreatmentFiveText);
        severeDehydrationTreatmentPartOneTreatmentFourText = (TextView) fragmentView.findViewById(R.id.severeDehydrationTreatmentPartOneTreatmentFourText);
        diarrhoeaSevereDehydrationDose = (TextView) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationDose);
        referDiarrhoeaSevere = (CheckBox) fragmentView.findViewById(R.id.referDiarrhoeaSevere);
        doseGiven = (CheckBox) fragmentView.findViewById(R.id.doseGiven);

        //Classification 10 Segment
        diarrhoeaSomeDehydrationLayout = (LinearLayout) fragmentView.findViewById(R.id.diarrhoeaSomeDehydrationLayout);
        layoutSomeDehydrationAdvice2Months = (LinearLayout) fragmentView.findViewById(R.id.layoutSomeDehydrationAdvice2Months);
        someDehydrationUnder2Months = (LinearLayout) fragmentView.findViewById(R.id.someDehydrationUnder2Months);
        diarrhoeaSomeDehydrationTreatmentOrsalaineQuantity = (TextView) ChildCareFragmentManagement.fragmentView.findViewById(R.id.diarrhoeaSomeDehydrationTreatmentOrsalaineQuantity);
        someDehydrationTreatmentTreatmentOneCheckbox = (CheckBox) fragmentView.findViewById(R.id.someDehydrationTreatmentTreatmentOneCheckbox);
        someDehydrationTreatmentTreatmentTwoCheckbox = (CheckBox) fragmentView.findViewById(R.id.someDehydrationTreatmentTreatmentTwoCheckbox);
        someDehydrationTreatmentTreatmentThreeCheckbox = (CheckBox) fragmentView.findViewById(R.id.someDehydrationTreatmentTreatmentThreeCheckbox);
        someDehydrationTreatmentTreatmentFourCheckbox = (CheckBox) fragmentView.findViewById(R.id.someDehydrationTreatmentTreatmentFourCheckbox);
        referSomeDehydration = (CheckBox) fragmentView.findViewById(R.id.referSomeDehydration);

        //Classification 11 Segment
        layoutNoDehydrationTreatmentSegment = (LinearLayout) fragmentView.findViewById(R.id.layoutNoDehydrationTreatmentSegment);
        noDehydrationUnder2Months = (LinearLayout) fragmentView.findViewById(R.id.noDehydrationUnder2Months);
        noDehydrationAbove2Months = (LinearLayout) fragmentView.findViewById(R.id.noDehydrationAbove2Months);
        noDehydrationTreatmentcheckboxtwo = (CheckBox) fragmentView.findViewById(R.id.noDehydrationTreatmentcheckboxtwo);
        noDehydrationTreatmentcheckboxthree = (CheckBox) fragmentView.findViewById(R.id.noDehydrationTreatmentcheckboxthree);

        //Classification 12,13 Segment
        layoutEatingOrLowWeightTreatmentSegment = (LinearLayout) fragmentView.findViewById(R.id.layoutEatingOrLowWeightTreatmentSegment);
        layoutClassification13Advice = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification13Advice);
        eatingOrLowWeightTreatmentcheckboxThree = (CheckBox) fragmentView.findViewById(R.id.jeatingOrLowWeightTreatmentcheckboxThree);

        //Classification 14 Segment
        layoutNoEatingOrLowWeightTreatmentSegment = (LinearLayout) fragmentView.findViewById(R.id.layoutNoEatingOrLowWeightTreatmentSegment);


        //Classification 15 Segment
        textKotraiMoxelTablet2Mto12M = (TextView) ChildCareFragmentManagement.fragmentView.findViewById(R.id.textKotraiMoxelTablet2Mto12M);
        textKotraiMoxelSyrup1Yto5Y = (TextView) ChildCareFragmentManagement.fragmentView.findViewById(R.id.textKotraiMoxelSyrup1Yto5Y);
        textAmoxacilinTablet2Mto12M = (TextView) ChildCareFragmentManagement.fragmentView.findViewById(R.id.textAmoxacilinTablet2Mto12M);
        textAmoxacilin1Yto3Y = (TextView) ChildCareFragmentManagement.fragmentView.findViewById(R.id.textAmoxacilin1Yto3Y);
        textAmoxacilin3Yto5Y = (TextView) ChildCareFragmentManagement.fragmentView.findViewById(R.id.textAmoxacilin3Yto5Y);
        tabKotraiMoxelDoseGiven = (CheckBox) fragmentView.findViewById(R.id.tabKotraiMoxelDoseGiven);
        injectionamoxicillinDoseGivenSctionTwo = (CheckBox) fragmentView.findViewById(R.id.injectionamoxicillinDoseGivenSctionTwo);
        c15Treatment = (CheckBox) fragmentView.findViewById(R.id.c15Treatment);
        layoutClassification15Advice = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification15Advice);

        //Classification 16 Segment
        layoutpneumonia2mto5y = (LinearLayout) fragmentView.findViewById(R.id.layoutpneumonia2mto5y);
        layoutClassification16Advice = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification16Advice);
        textPneumoniaFollowup = (TextView) fragmentView.findViewById(R.id.textPneumoniaFollowup);
        pneumonia2Mto5YTreatment2checkbox = (CheckBox) fragmentView.findViewById(R.id.pneumonia2Mto5YTreatment2checkbox);
        kotraimoxaxolinSecondDoseGiven = (CheckBox) fragmentView.findViewById(R.id.kotraimoxaxolinSecondDoseGiven);


        //Classification 17 Segment
        layoutClassification17Refer = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification17Refer);
        layoutClassification17Advice = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification17Advice);

        //Classification 19 Segment
        earproblem2mto5yC1 = (LinearLayout) fragmentView.findViewById(R.id.earproblem2mto5yC1);

        //Classification 20 Segment
        layoutClassification20Refer = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification20Refer);
        c20DoseLabel = (TextView) fragmentView.findViewById(R.id.c20DoseLabel);
        c20DoseLabel2 = (TextView) fragmentView.findViewById(R.id.c20DoseLabel2);

        //Classification 21,22 Segment
        classification21SegmentRefer = (LinearLayout) fragmentView.findViewById(R.id.classification21SegmentRefer);
        layoutClassification21NonRefer = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification21NonRefer);
        classification21NonReferMedicine1 = (TextView) fragmentView.findViewById(R.id.classification21NonReferMedicine1);
        classification21NonReferMedicine2 = (TextView) fragmentView.findViewById(R.id.classification21NonReferMedicine2);
        c22T2 = (CheckBox) fragmentView.findViewById(R.id.c22T2);
        c21T1 = (CheckBox) fragmentView.findViewById(R.id.c21T1);

        //Classifiation 26 Segment
        layoutClassification26NonRefer = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification26NonRefer);

        //Classification 27 Segment
        layoutClassification27NonRefer = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification27NonRefer);

        //Classification 23,28,29 Segment
        layoutClassification23 = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification23);
        classification23Label = (TextView) fragmentView.findViewById(R.id.classification23Label);
        classification28Label = (TextView) fragmentView.findViewById(R.id.classification28Label);
        classification29Label = (TextView) fragmentView.findViewById(R.id.classification29Label);
        classification23Medicine1 = (TextView) fragmentView.findViewById(R.id.classification23Medicine1);
        c23T2 = (CheckBox) fragmentView.findViewById(R.id.c23T2);
        c23T3 = (CheckBox) fragmentView.findViewById(R.id.c23T3);
        c28T3 = (CheckBox) fragmentView.findViewById(R.id.c28T3);
        c28T4 = (CheckBox) fragmentView.findViewById(R.id.c28T4);
        c23T4 = (CheckBox) fragmentView.findViewById(R.id.c23T4);

        //Classification 24,30,31,32 Segment
        layoutClassification24 = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification24);
        classification24DoseLabel = (TextView) fragmentView.findViewById(R.id.classification24DoseLabel);
        text1 = (TextView) fragmentView.findViewById(R.id.text1);
        layoutClassification30 = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification30);
        classification30DoseLabel = (TextView) fragmentView.findViewById(R.id.classification30DoseLabel);
        layoutClassification31 = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification31);
        layoutClassification32Advice = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification32Advice);

        c31T3 = (CheckBox) fragmentView.findViewById(R.id.c31T3T);
        c30T2 = (CheckBox) fragmentView.findViewById(R.id.c30T2);

        //Classification 35 Segment
        layoutClassification35 = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification35);

        //Classification 36 Segment
        layoutClassification36 = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification36);

        //Classification 37 Segment
        layoutClassification37 = (LinearLayout) fragmentView.findViewById(R.id.layoutClassification37);
        classification37Dose = (TextView) fragmentView.findViewById(R.id.classification37Dose);

        //Refer,Refer Center Name, Refer Reason,Refer Status,Comment,Advice Segment
        radioGroupRefer = (RadioGroup) fragmentView.findViewById(R.id.childReferRadioGroup);
        radioButtonReferYes = (RadioButton) fragmentView.findViewById(R.id.childReferRadioButtonYes);
        radioButtonReferNo = (RadioButton) fragmentView.findViewById(R.id.childReferRadioButtonNo);
        childReferCenterNameLayout = (LinearLayout) fragmentView.findViewById(R.id.childReferCenterNameLayout);
        childReferReasonLayout = (LinearLayout) fragmentView.findViewById(R.id.childReferReasonLayout);
        referStatusLayout = (RelativeLayout) fragmentView.findViewById(R.id.referStatusLayout);
        referChild = (RelativeLayout) fragmentView.findViewById(R.id.referChild);
        radioGroupReferDecision = (RadioGroup) fragmentView.findViewById(R.id.psbiReferStatusRadioGroup);
        radioButtonReferAgreeYes = (RadioButton) fragmentView.findViewById(R.id.psbiReferStatusRadioButtonYes);
        radioButtonReferAgreeNo = (RadioButton) fragmentView.findViewById(R.id.psbiReferStatusRadioButtonNo);
        childCareCommentEditText = (EditText) fragmentView.findViewById(R.id.childCareCommentEditText);
        advicegivencheckbox = (CheckBox) fragmentView.findViewById(R.id.advicegivencheckbox);


    }

    /**
     * Calling treatment method based on classification
     */
    public void setTreatmentLayout() {
        try {
            int Age = Integer.valueOf(ChildCareActivity.getJSONObject().getString("age"));
            int Weight = Integer.valueOf(ChildCareActivity.getJSONObject().getString("2"));
            int dayDiff = (int) ((Utilities.getDateDiff(Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE, ChildCareActivity.getJSONObject().getString("indicationStartDate")), new Date(),
                    TimeUnit.DAYS)));
            if (ChildCareActivity.jsonObject.has("classifications")) {
                String[] strArrayClassifications = ChildCareActivity.jsonObject.getString("classifications").split(",");
                int[] array = new int[strArrayClassifications.length];
                for (int i = 0; i < strArrayClassifications.length; i++) {
                    array[i] = Integer.valueOf(strArrayClassifications[i]);
                }
                Arrays.sort(array);

                for (int i = 0; i < array.length; i++) {
                    if (array[i] == 1) {
                        LoadCriticalDiseaseSevereConditionLayout();
                    } else if (array[i] == 2) {
                        LoadCriticalDiseaseBacterialInfectionLayout();
                    } else if (array[i] == 3) {
                        LoadFastBreathingPneuomoniaLayout();
                    } else if (array[i] == 4) {
                        LoadPneoumoniaLayout();
                    } else if (array[i] == 5) {
                        LoadLocalBacteriaLayout();
                    } else if (array[i] == 6) {
                        loadClassification6Treatment();
                    } else if (array[i] == 7) {
                        LoadSevereJaundiceLayout();
                    } else if (array[i] == 8) {
                        LoadJaundiceLayout(Age);
                    } else if (array[i] == 9 || array[i] == 33) {
                        LoadSevereDiarrhoeaLayout(Age);
                    } else if (array[i] == 10 || array[i] == 34) {
                        calculateORSQuantity(Age, Weight);
                    } else if (array[i] == 11 || array[i] == 37) {
                        noDehydrationTreatment(Age);
                    } else if (array[i] == 12) {
                        LoadEatingOrLowWeightLayout();
                    } else if (array[i] == 13) {
                        loadLowWeightLayout(Weight);
                    } else if (array[i] == 14) {
                        LoadNoEatingOrLowWeightLayout();
                    } else if (array[i] == 15) {
                        criticalDisease2Mto5Y(Age);
                    } else if (array[i] == 16) {
                        classification16Treatment(Age,dayDiff);
                    } else if (array[i] == 17) {
                        classification17Treatment(dayDiff);
                    } else if (array[i] == 19) {
                        classification19Treatment(Age);
                    } else if (array[i] == 26) {
                        claasification26Treatment(Age);
                    } else if (array[i] == 27) {
                        classification27Treatment();
                    } else if (array[i] == 20) {
                        classification20Treatment(Age, Weight);
                    } else if (array[i] == 21) {
                        classification21Treatment(Age, dayDiff);
                    } else if (array[i] == 22) {
                        classification22Treatment(dayDiff);
                    } else if (array[i] == 23) {
                        classification23Treatment(Age);
                    } else if (array[i] == 28) {
                        classification28Treatment();
                    } else if (array[i] == 29) {
                        classification29Treatment();
                    } else if (array[i] == 25) {
                        loadClassification6Treatment();
                    } else if (array[i] == 24) {
                        classification24Treatment(Age);
                    } else if (array[i] == 30) {
                        classification30Treatment(Age);
                    } else if (array[i] == 31) {
                        classification31Treatment();
                    } else if (array[i] == 32) {
                        classification32Treatment(Age);
                    } else if (array[i] == 18) {
                        classification35Treatment();
                    } else if (array[i] == 35) {
                        classification36Treatment();
                    } else if (array[i] == 36) {
                        classification37Treatment(Age, Weight);
                    }

                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Critical Disease Severe Condition Medicine Segment
     */
    private void LoadSeverDiseaseCriticalCondition() {
        setGentamycinTreatment();
        setAmoxacilinTreatment();

    }

    //Loading UI Based on Classification

    /**
     * Classification 1 - Critical Disease Severe Condition Treatment
     * Here refer is selected automatically, so just recording refer agreed or not
     * 0 - referred
     * 1 - not referred
     */
    private void LoadCriticalDiseaseSevereConditionLayout() {
        LoadSeverDiseaseCriticalCondition();
        showReferSegment();
        radioGroupReferDecision.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View referDecision = radioGroupReferDecision.findViewById(i);
                int referDecisionIdnex = radioGroupReferDecision.indexOfChild(referDecision);
                switch (referDecisionIdnex) {
                    case 0:
                        layoutClassification12ReferAdvice.setVisibility(View.VISIBLE);
                        layoutClassification12NonReferAdvice.setVisibility(View.GONE);
                        break;
                    case 1:
                        layoutClassification12NonReferAdvice.setVisibility(View.VISIBLE);
                        layoutClassification12ReferAdvice.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    /**
     * Classification 2 -  Critical Disease - Possible Bacterial Infection Treatment
     * Updating UI based on user input of refer and agreed to be refer
     * 0 - referred
     * 1 - not referred
     * If not referred then hide the refer agreement segment
     */
    private void LoadCriticalDiseaseBacterialInfectionLayout() {
        //showing Medicine Segment First
        criticalDiseaseReferAgreeTreatment();
        radioGroupRefer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View refer = radioGroupRefer.findViewById(i);
                int referIndex = radioGroupRefer.indexOfChild(refer);
                switch (referIndex){
                    case 0:
                        hideNonReferTreatmentSegment();
                        layoutClassification12ReferAdvice.setVisibility(View.VISIBLE);
                        layoutClassification12NonReferAdvice.setVisibility(View.GONE);
                    case 1:
                        textViewReferTreatmentLabel.setVisibility(View.GONE);
                        referStatusLayout.setVisibility(View.GONE);
                        layoutAmoxilinSegment.setVisibility(View.GONE);
                        criticalDiseasePossibleBacterialInfectionReferDisagree();
                        layoutInjectionJentamycinSegment.setVisibility(View.GONE);
                        layoutClassification12ReferAdvice.setVisibility(View.GONE);
                        layoutClassification12NonReferAdvice.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Classification 3 - Critical Disease Fast Breathing Pneumonia Treatment
     * Updating UI based on user input of refer and agreed to be refer
     * 0 - referred
     * 1 - not referred
     * If not referred then hide the refer agreement segment
     */
    private void LoadFastBreathingPneuomoniaLayout() {
        //Treatment and Refer Done
        textViewReferTreatmentLabel.setVisibility(View.VISIBLE);
        setAmoxacilinTreatment();
        showReferSegment();
        //Setting treatment based on Refer Yes or No
        radioGroupReferDecision.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View referDecision = radioGroupReferDecision.findViewById(i);
                int referDecisionIdnex = radioGroupReferDecision.indexOfChild(referDecision);
                switch (referDecisionIdnex) {
                    case 0:
                        layoutClassification12ReferAdvice.setVisibility(View.VISIBLE);
                        layoutClassification34NonReferAdvice.setVisibility(View.GONE);
                        hideNonReferTreatmentSegment();
                        break;
                    case 1:
                        setAmoxacilinReferDisagreeDose();
                        layoutClassification12ReferAdvice.setVisibility(View.GONE);
                        layoutClassification34NonReferAdvice.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

    }


    /**
     * Classification 4 - Fast Breathing Pneumonia Treatment
     * Here refer is selected automatically, so just recording refer agreed or not
     */
    private void LoadPneoumoniaLayout() {
        setAmoxacilinReferDisagreeDose();
        firstFollowUpLayoutLoad();
        layoutClassification34NonReferAdvice.setVisibility(View.VISIBLE);
    }

    /**
     * Classification 5 - Local Bacterial  Treatment
     * Here refer is selected automatically, so just recording refer agreed or not
     */
    private void LoadLocalBacteriaLayout() {
        layoutAmoxacinDropForNonReferSegmentTwo.setVisibility(View.VISIBLE);
        injectionamoxicillinDoseGiven.setVisibility(View.VISIBLE);
        layoutClassification5Advice.setVisibility(View.VISIBLE);
        try {
            amoxicillinDose = Utilities.ChildCareAmoxicillinDose(Integer.parseInt(ChildCareActivity.getJSONObject().getString("2")));
            textAmoxacinDropForNonReferSegmentTwo.setText(amoxicillinDose);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * Classification 6 - No Disease Treatment
     */
    private void loadClassification6Treatment() {
        layoutClassification6.setVisibility(View.VISIBLE);
    }

    /**
     * Classification 7 - Severe Jaundice  Treatment
     * Here refer is selected automatically, so just recording refer agreed or not
     */
    private void LoadSevereJaundiceLayout() {
        showReferSegment();
        layoutSevereJaundiceTreatmentSegment.setVisibility(View.VISIBLE);
    }

    /**
     * Classification 8 - Jaundice  Treatment
     */
    private void LoadJaundiceLayout(int age) {
        if(age < 14) {
            hideReferSegment();
            layoutClassification8Advice.setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.adviceFourC8)).setVisibility(View.GONE);
        }  else{
            showReferSegment();
            (fragmentView.findViewById(R.id.layoutClassification8Advice)).setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.adviceOneC8)).setVisibility(View.GONE);
            (fragmentView.findViewById(R.id.adviceTwoC8)).setVisibility(View.GONE);
            (fragmentView.findViewById(R.id.adviceThreeC8)).setVisibility(View.GONE);
            (fragmentView.findViewById(R.id.adviceFiveC8)).setVisibility(View.GONE);
        }
    }

    /**
     * Classification 9 - Severe Dehydration Treatment
     * A series of question asked to provider to judge her qualification and based on that treatment is given
     */
    private void LoadSevereDiarrhoeaLayout(int age) {
        radioGroupRefer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.childReferRadioButtonYes:
                        referChecked();
                        referStatusLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.childReferRadioButtonNo:
                        referStatusLayout.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }

            }
        });

        diarrhoeaSevereDehydrationLayout.setVisibility(View.VISIBLE);
        if (age < 60) {
            referDiarrhoeaSevere.setVisibility(View.GONE);
            diarrhoeaSevereDehydrationDose.setVisibility(View.GONE);
            doseGiven.setVisibility(View.GONE);
        }
        diarrhoeaSevereDehydrationQuestionOneRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (checkedId) {
                    case R.id.diarrhoeaSevereDehydrationQuestionOneRadioButtonYes:
                        LoadSeverDehydrationQuestionOneYesTreatment(age);
                        break;
                    case R.id.diarrhoeaSevereDehydrationQuestionOneRadioButtonNo:
                        LoadSevereDehydrationQuestionOneNoTreatment();
                        break;
                    default:
                        break;
                }
            }
        });

        diarrhoeaSevereDehydrationQuestionTwoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.diarrhoeaSevereDehydrationQuestionTwoRadioButtonYes:
                        LoadSevereDehydrationQuestionTwoYesTreatment();
                        break;
                    case R.id.diarrhoeaSevereDehydrationQuestionTwoRadioButtonNo:
                        LoadSevereDehydrationQuestionTwoNoTreatment();
                        break;
                    default:
                        break;
                }
            }
        });


        diarrhoeaSevereDehydrationQuestionThreeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.diarrhoeaSevereDehydrationQuestionThreeRadioButtonYes:
                        LoadSevereDehydrationQuestionThreeYesTreatment();
                        break;
                    case R.id.diarrhoeaSevereDehydrationQuestionThreeRadioButtonNo:
                        LoadSevereDehydrationQuestionThreeNoTreatment();
                        break;
                    default:
                        break;
                }
            }
        });


        diarrhoeaSevereDehydrationQuestionFourRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.diarrhoeaSevereDehydrationQuestionFourRadioButtonYes:
                        LoadSevereDehydrationQuestionThreeYesTreatment();
                        break;
                    case R.id.diarrhoeaSevereDehydrationQuestionFourRadioButtonNo:
                        LoadSevereDehydrationFourNoTreatment();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Classification 12 - Eating Problem Treatment
     */
    private void LoadEatingOrLowWeightLayout() {
        layoutEatingOrLowWeightTreatmentSegment.setVisibility(View.VISIBLE);
    }

    /**
     * Classification 13 - Low Weight Treatment
     */
    private void loadLowWeightLayout(int weight) {

        if(weight < 2000){
            showReferSegment();
            layoutClassification13Advice.setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.c13RT1)).setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.c13RT2)).setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.c13RT3)).setVisibility(View.VISIBLE);
        } else {
            layoutClassification13Advice.setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.c13NRT1)).setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.c13NRT2)).setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.c13NRT3)).setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.c13NRT4)).setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.c13NRT5)).setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.c13NRT6)).setVisibility(View.VISIBLE);
        }

    }

    /**
     * Classification 14 - No eating Problem treatment
     */
    private void LoadNoEatingOrLowWeightLayout() {
        layoutNoEatingOrLowWeightTreatmentSegment.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when provider tick yes for severe dehydration question 1
     */
    private void LoadSeverDehydrationQuestionOneYesTreatment(int age) {
        severeDehydrationTreatmentPartOne.setVisibility(View.VISIBLE);
        diarrhoeaSevereDehydrationQuestionTwoLayout.setVisibility(View.GONE);
        severeDehydrationTreatment.setVisibility(View.VISIBLE);
        if (age < 366) {
            severeDehydrationTreatmentPartOneTreatmentFourText.setVisibility(View.GONE);
            severeDehydrationTreatmentPartOneTreatmentFiveText.setVisibility(View.GONE);
        } else {
            severeDehydrationTreatmentPartOneTreatmentTwoText.setVisibility(View.GONE);
            severeDehydrationTreatmentPartOneTreatmentThreeText.setVisibility(View.GONE);
        }
    }

    /**
     * This method is called when provider tick No for severe dehydration question 1
     */
    private void LoadSevereDehydrationQuestionOneNoTreatment() {
        severeDehydrationTreatmentPartOne.setVisibility(View.GONE);
        diarrhoeaSevereDehydrationQuestionTwoLayout.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when provider tick yes for severe dehydration question 2
     */
    private void LoadSevereDehydrationQuestionTwoYesTreatment() {
        severeDehydrationTreatmentPartTwo.setVisibility(View.VISIBLE);
        diarrhoeaSevereDehydrationQuestionThreeLayout.setVisibility(View.GONE);
        severeDehydrationTreatment.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when provider tick No for severe dehydration question 2
     */
    private void LoadSevereDehydrationQuestionTwoNoTreatment() {
        severeDehydrationTreatmentPartTwo.setVisibility(View.GONE);
        diarrhoeaSevereDehydrationQuestionThreeLayout.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when provider tick yes for severe dehydration question 3
     */
    private void LoadSevereDehydrationQuestionThreeYesTreatment() {
        severeDehydrationTreatmentPartThreeFour.setVisibility(View.VISIBLE);
        diarrhoeaSevereDehydrationQuestionFourLayout.setVisibility(View.GONE);
        severeDehydrationTreatment.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when provider tick no for severe dehydration question 3
     */
    private void LoadSevereDehydrationQuestionThreeNoTreatment() {
        severeDehydrationTreatmentPartThreeFour.setVisibility(View.GONE);
        diarrhoeaSevereDehydrationQuestionFourLayout.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when provider tick NO for severe dehydration question 4
     */
    private void LoadSevereDehydrationFourNoTreatment() {
        severeDehydrationTreatmentPartThreeFour.setVisibility(View.GONE);
        severeDehydrationTreatmentPartLast.setVisibility(View.VISIBLE);
        severeDehydrationTreatment.setVisibility(View.VISIBLE);
    }


    public void getInputValues(JSONObject jsonObject) {
        Utilities.getEditTexts(jsonEditTextMap, jsonObject);
        Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, jsonObject);
        Utilities.getTextViews(jsonTextViewsMap, jsonObject);
        Utilities.getCheckboxes(jsonCheckboxMap, jsonObject);
        Utilities.getSpinners(jsonSpinnerMap, jsonObject);

        try {
            if (jsonObject.getString("91").equals("1")) {
                if (referredClArray.size() != 0) {
                    //manually creating multispinner value of refer reason
                    StringBuilder referReasonList = new StringBuilder();
                    referReasonList.append("[");
                    for (int i = 0; i < referredClArray.size(); i++) {
                        referReasonList.append("\"" + referredClArray.get(i)
                                + (i == (referredClArray.size() - 1) ? "\"]" : "\","));
                    }
                    jsonObject.put("93", referReasonList.toString());
                }
            }else{
                jsonObject.put("93", "");
            }
            //TODO:has to remove below's redundant line later
            if (!jsonObject.has("93")) jsonObject.put("93", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setInputValues(JSONObject jsonObject) {
        Utilities.setEditTexts(jsonEditTextMap, jsonObject);
        Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap, jsonObject);
        Utilities.setTextViews(jsonTextViewsMap, jsonObject);
        Utilities.setCheckboxes(jsonCheckboxMap, jsonObject);
        Utilities.setSpinners(jsonSpinnerMap, jsonObject);
    }


    @Override
    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("106", (CheckBox) fragmentView.findViewById(R.id.severeDehydrationTreatmentCheckbox)); // 91 - Severe Dehydration Treatment Given According Classification
        jsonCheckboxMap.put("95", (CheckBox) fragmentView.findViewById(R.id.injectiongentamicinDoseGiven)); // 85 - Gentamicin Dose Given
        jsonCheckboxMap.put("96", (CheckBox) fragmentView.findViewById(R.id.injectionamoxicillinDoseGiven)); // 86 - Amoxicilin Dose Given
        jsonCheckboxMap.put("101", (CheckBox) fragmentView.findViewById(R.id.severeJaundiceTreatmentcheckbox)); // 93 - Severe Jaundice 0 - 59 days
        jsonCheckboxMap.put("107", (CheckBox) fragmentView.findViewById(R.id.noDehydrationTreatmentcheckboxtwo)); // 104 - No Dehydration 0 - 59 days
        jsonCheckboxMap.put("108", (CheckBox) fragmentView.findViewById(R.id.noDehydrationTreatmentcheckboxthree)); // 105 - No Dehydration 0 - 59 days
        jsonCheckboxMap.put("109", (CheckBox) fragmentView.findViewById(R.id.someDehydrationTreatmentTreatmentOneCheckbox)); // 99 - Some Dehydration 0 - 59 days
        jsonCheckboxMap.put("110", (CheckBox) fragmentView.findViewById(R.id.someDehydrationTreatmentTreatmentTwoCheckbox)); // 100 - Some Dehydration 0 - 59 days
        jsonCheckboxMap.put("111", (CheckBox) fragmentView.findViewById(R.id.someDehydrationTreatmentTreatmentThreeCheckbox)); // 101 - Some Dehydration 0 - 59 days
        jsonCheckboxMap.put("112", (CheckBox) fragmentView.findViewById(R.id.someDehydrationTreatmentTreatmentFourCheckbox)); // 102 - Some Dehydration 0 - 59 days
        jsonCheckboxMap.put("113", (CheckBox) fragmentView.findViewById(R.id.jeatingOrLowWeightTreatmentcheckboxThree)); // 110 - Eating Problem or Low Weight
        jsonCheckboxMap.put("98", (CheckBox) fragmentView.findViewById(R.id.injectiongentamicinSecondDoseGiven)); // 113 - Classification 2 Second Day Injection Given
        jsonCheckboxMap.put("151", (CheckBox) fragmentView.findViewById(R.id.advicegivencheckbox)); // 114 - Advice Given According to Classification


        jsonCheckboxMap.put("114", (CheckBox) fragmentView.findViewById(R.id.c15Treatment)); // 182 - Classification 15 Treatment
        jsonCheckboxMap.put("115", (CheckBox) fragmentView.findViewById(R.id.tabKotraiMoxelDoseGiven)); // 92 - Kotraimoxel Dose Given
        jsonCheckboxMap.put("97", (CheckBox) fragmentView.findViewById(R.id.injectionamoxicillinDoseGivenSctionTwo)); // 176 - Amoxicilin Dose Given 2M to 5Y
        jsonCheckboxMap.put("116", (CheckBox) fragmentView.findViewById(R.id.kotraimoxaxolinSecondDoseGiven)); // 113 - 2nd dose of Antibiotic given 2m to 5y Pneumonia
        jsonCheckboxMap.put("117", (CheckBox) fragmentView.findViewById(R.id.pneumonia2Mto5YTreatment2checkbox)); // 118 - Pneumonia Treatment 2M to 5Y
        jsonCheckboxMap.put("118", (CheckBox) fragmentView.findViewById(R.id.noramlFlue2Mto5YReferTreatmentcheckbox)); // 122 - Normal Flue Refer Treatment 2M to 5Y

        jsonCheckboxMap.put("119", (CheckBox) fragmentView.findViewById(R.id.earproblem2mto5yD1T1checkbox));// 126 - Classification 19 Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("92", (CheckBox) fragmentView.findViewById(R.id.earproblem2mto5yD1T4checkbox));// 126 - Classification 19 Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("120", (CheckBox) fragmentView.findViewById(R.id.earproblem2mto5yD1T2checkbox)); // 127 - Classification 19 Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("121", (CheckBox) fragmentView.findViewById(R.id.earproblem2mto5yD1T3checkbox)); // 128 - Classification 19 Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("122", (CheckBox) fragmentView.findViewById(R.id.earproblem2mto5yD2T1checkbox)); // 129 - Classification 26 Non Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("123", (CheckBox) fragmentView.findViewById(R.id.earproblem2mto5yD2T2checkbox)); // 130 - Classification 26 Non Refer Treatment 2M to 5Y

        jsonCheckboxMap.put("124", (CheckBox) fragmentView.findViewById(R.id.c20T1)); // 135 - Classification 20  Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("125", (CheckBox) fragmentView.findViewById(R.id.c20T2)); // 136 - Classification 20 Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("126", (CheckBox) fragmentView.findViewById(R.id.c20T3)); // 137 - Classification 20 Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("127", (CheckBox) fragmentView.findViewById(R.id.c20T4)); // 138 - Classification 20 Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("128", (CheckBox) fragmentView.findViewById(R.id.c20T5)); // 139 - Classification 20 Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("129", (CheckBox) fragmentView.findViewById(R.id.classification21ReferTreatment)); // 140 - Classification 21 Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("130", (CheckBox) fragmentView.findViewById(R.id.c21T1)); // 141 - Classification 21 Non Refer Treatment 2M to 5Y
        jsonCheckboxMap.put("131", (CheckBox) fragmentView.findViewById(R.id.c21T2)); // 142 - Classification 21 Non Refer Treatment 2M to 5Y
        //Measles - Classification 23,28,29
        jsonCheckboxMap.put("132", (CheckBox) fragmentView.findViewById(R.id.c23T1));
        jsonCheckboxMap.put("133", (CheckBox) fragmentView.findViewById(R.id.c23T2));
        jsonCheckboxMap.put("134", (CheckBox) fragmentView.findViewById(R.id.c23T3));
        jsonCheckboxMap.put("135", (CheckBox) fragmentView.findViewById(R.id.c23T4));
        jsonCheckboxMap.put("136", (CheckBox) fragmentView.findViewById(R.id.c28T3));
        jsonCheckboxMap.put("137", (CheckBox) fragmentView.findViewById(R.id.c28T4));
        //Classification 24 Segment
        jsonCheckboxMap.put("138", (CheckBox) fragmentView.findViewById(R.id.c24T1));
        jsonCheckboxMap.put("139", (CheckBox) fragmentView.findViewById(R.id.c24T2));
        jsonCheckboxMap.put("140", (CheckBox) fragmentView.findViewById(R.id.c24T4));
        //Classification 30 Segment
        jsonCheckboxMap.put("141", (CheckBox) fragmentView.findViewById(R.id.c30T1));
        jsonCheckboxMap.put("142", (CheckBox) fragmentView.findViewById(R.id.c30T2));
        //Classification 31,32 Segment
        jsonCheckboxMap.put("143", (CheckBox) fragmentView.findViewById(R.id.c31T3T));
        //Classification 18 Segment
        jsonCheckboxMap.put("144", (CheckBox) fragmentView.findViewById(R.id.referDiarrhoeaSevere));
        jsonCheckboxMap.put("145", (CheckBox) fragmentView.findViewById(R.id.doseGiven));
        //Classification 33 Segment
        jsonCheckboxMap.put("146", (CheckBox) fragmentView.findViewById(R.id.referSomeDehydration));
        //Classification 35 Segment
        jsonCheckboxMap.put("147", (CheckBox) fragmentView.findViewById(R.id.c35T1));
        //Classification 36 Segment
        jsonCheckboxMap.put("148", (CheckBox) fragmentView.findViewById(R.id.c36T2));
        //Classification 37 Segment
        jsonCheckboxMap.put("149", (CheckBox) fragmentView.findViewById(R.id.c37T1));
        //jsonCheckboxMap.put("152", (CheckBox) fragmentView.findViewById(R.id.malNutritionSevere3));

    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("comment", (EditText) fragmentView.findViewById(R.id.childCareCommentEditText));
        jsonEditTextMap.put("150", (EditText) fragmentView.findViewById(R.id.classification6TreatmentEditText)); // Classification 6 Treatment

    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("94", (Spinner) fragmentView.findViewById(R.id.childReferCenterNameSpinner));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {

    }

    @Override
    protected void initiateEditTextDates() {

    }

    @Override
    protected void initiateRadioGroups() {
        jsonRadioGroupButtonMap.put("91", Pair.create( //refer
                (RadioGroup) fragmentView.findViewById(R.id.childReferRadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.childReferRadioButtonYes),
                        (RadioButton) fragmentView.findViewById(R.id.childReferRadioButtonNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("92", Pair.create( //refer agree or disagree
                (RadioGroup) fragmentView.findViewById(R.id.psbiReferStatusRadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.psbiReferStatusRadioButtonYes),
                        (RadioButton) fragmentView.findViewById(R.id.psbiReferStatusRadioButtonNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("99", Pair.create( //4th Day Follow Up
                (RadioGroup) fragmentView.findViewById(R.id.psbiFirstFollowupRadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.psbiFirstFollowupRadioButtonYes),
                        (RadioButton) fragmentView.findViewById(R.id.psbiFirstFollowupRadioButtonNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("100", Pair.create( //8th Day Follow Up
                (RadioGroup) fragmentView.findViewById(R.id.psbiSecondFollowupRadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.psbiSecondFollowupRadioButtonYes),
                        (RadioButton) fragmentView.findViewById(R.id.psbiSecondFollowupRadioButtonNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("102", Pair.create( //Severe Dehydration Question One
                (RadioGroup) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionOneRadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionOneRadioButtonYes),
                        (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionOneRadioButtonNo)
                )
                )
        );


        jsonRadioGroupButtonMap.put("103", Pair.create( //Severe Dehydration Question Two
                (RadioGroup) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionTwoRadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionTwoRadioButtonYes),
                        (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionTwoRadioButtonNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("104", Pair.create( //Severe Dehydration Question Three
                (RadioGroup) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionThreeRadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionThreeRadioButtonYes),
                        (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionThreeRadioButtonNo)
                )
                )
        );

        jsonRadioGroupButtonMap.put("105", Pair.create( //Severe Dehydration Question Four
                (RadioGroup) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionFourRadioGroup), Pair.create(
                        (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionFourRadioButtonYes),
                        (RadioButton) fragmentView.findViewById(R.id.diarrhoeaSevereDehydrationQuestionFourRadioButtonNo)
                )
                )
        );


    }

    /**
     * Setting gentamycin Dose quantity
     */
    private void setGentamycinTreatment() {
        textViewReferTreatmentLabel.setVisibility(View.VISIBLE);
        layoutInjectionJentamycinSegment.setVisibility(View.VISIBLE);
        injectiongentamicinDoseGiven.setVisibility(View.VISIBLE);
        try {
            //Calculating gentamycin based on weight of child
            gentamicinDose = Utilities.ChildCareGentamicinDose(Integer.parseInt(ChildCareActivity.getJSONObject().getString("2")));
            injectionJentamycinDose.setText(gentamicinDose);
            textViewInjectionJentamycinDoseNonReferLabel.setText(gentamicinDose);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setting Amoxicilin Dose quantity
     */
    private void setAmoxacilinTreatment() {
        layoutAmoxilinSegment.setVisibility(View.VISIBLE);
        injectionamoxicillinDoseGiven.setVisibility(View.VISIBLE);
        try {
            //Calculating amoxicilin dose based on weight of child
            amoxicillinDose = Utilities.ChildCareAmoxicillinDose(Integer.parseInt(ChildCareActivity.getJSONObject().getString("2")));
            textViewAmoxacilinDropQuantityLabel.setText(amoxicillinDose);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Critical Disease Refer Yes and Agreed to refer Treatment Segment
     */
    private void criticalDiseaseReferAgreeTreatment() {
        setGentamycinTreatment();
        setAmoxacilinTreatment();
    }

    /**
     * Hiding Layout files of Non-Refer Treatment - Patient agreed to go to Refer Center
     */
    private void hideNonReferTreatmentSegment() {
        layoutInjectionGentamicinForNonRefer.setVisibility(View.GONE);
        layoutAmoxacinDropForNonReferSegmentOne.setVisibility(View.GONE);
        layoutFollowupNonReferSegment.setVisibility(View.GONE);
    }

    /**
     * Hiding Layout files of Refer Treatment - Patient don't want to go to Refer Center
     */
    private void hideReferTreatmentSegment() {
        textViewReferTreatmentLabel.setVisibility(View.GONE);
        layoutInjectionJentamycinSegment.setVisibility(View.GONE);
        layoutAmoxilinSegment.setVisibility(View.GONE);
    }

    /**
     * Loading treatment segment of not agreed to go refer
     * gentamicin and amoxicilin dose calculating
     */
    private void showReferDisagreeTreatment() {
        injectionJentamycinReferDisagreeTreatment();
        setAmoxacilinReferDisagreeDose();
        firstFollowUpLayoutLoad();
    }

    /**
     * Calculating gentamicin dose quantity when patient don't want to go to refer center
     * Also display when to come for taking next dose
     * next visit date count is done by adding days interval with visit date of patient
     */
    private void injectionJentamycinReferDisagreeTreatment() {
        layoutInjectionGentamicinForNonRefer.setVisibility(View.VISIBLE);
        injectiongentamicinDoseGiven.setVisibility(View.VISIBLE);
        try {
            //Calculating gentamycin based on weight of child
            gentamicinDose = Utilities.ChildCareGentamicinDose(Integer.parseInt(ChildCareActivity.getJSONObject().getString("2")));
            textViewInjectionJentamycinDoseNonReferLabel.setText(gentamicinDose);
            CustomSimpleDateFormat sdf = new CustomSimpleDateFormat("yyyy-MM-dd");
            jentamycinFollowupDate = ConvertNumberToBangla(getDateStringUIFormat(calculateFollowupDate()));
            textViewInjectionJentamycinFollowupDoseDate.setText(jentamycinFollowupDate + " তারিখে ইনজেকশন জেন্টামাইসিন ডোজ নিতে আসতে বলা হয়েছে");
            Date currentDate = Calendar.getInstance().getTime();
            visitdate = sdf.format(currentDate);
            followupDate = calculateFollowupDate();
            if (followupDate.equals(visitdate)) {
                injectiongentamicinSecondDoseGiven.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Calculating amoxicilin dose quantity when patient don't want to go to refer center
     */
    private void setAmoxacilinReferDisagreeDose() {
        layoutAmoxacinDropForNonReferSegmentOne.setVisibility(View.VISIBLE);
        firstFollowUpLayoutLoad();
        injectionamoxicillinDoseGiven.setVisibility(View.VISIBLE);
        try {
            amoxicillinDose = Utilities.ChildCareAmoxicillinDose(Integer.parseInt(ChildCareActivity.getJSONObject().getString("2")));
            textViewAmoxacilinDropQuantityNonReferLabel.setText(amoxicillinDose);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Calling methods for displaying treatment of Bacterial Infection and not agreed to go to Refer Center
     */
    private void criticalDiseasePossibleBacterialInfectionReferDisagree() {
        showReferDisagreeTreatment();
        //hideReferTreatmentSegment();

    }

    /**
     * Calling methods for displaying treatment of Pneumonia and not agreed to go to Refer Center
     */
    private void criticalDiseasePneumoniaReferDisagree() {
        setAmoxacilinReferDisagreeDose();
        //hideReferTreatmentSegment();
    }

    /**
     * Classification 10 - Some Dehydration
     * Calculating ORS quantity based on child age and weight
     * Also showing treatment layout
     */
    private void calculateORSQuantity(int age, int weight) {
        diarrhoeaSomeDehydrationLayout.setVisibility(View.VISIBLE);
        String orsQuantity = " ";
        int quantity = 0;
        String ors;

        if (age < 60) {
            referSomeDehydration.setVisibility(View.GONE);
            someDehydrationUnder2Months.setVisibility(View.VISIBLE);
        } else {
            layoutSomeDehydrationAdvice2Months.setVisibility(View.VISIBLE);
        }

        quantity = weight * 75 / 1000;
        ors = ConvertNumberToBangla(String.valueOf(quantity));
        orsQuantity = "চার ঘন্টা ব্যাপী ওআরএস পরিমাণ : " + ors + " মি:লি:";
        diarrhoeaSomeDehydrationTreatmentOrsalaineQuantity.setText(orsQuantity);


    }

    /**
     * Classification 11 - No Dehydration
     * Zink Tablet is given based on Age but Age limit starts from 2 Months. under 2 months Zink Tablet is not given
     */
    private void noDehydrationTreatment(int age) {
        layoutNoDehydrationTreatmentSegment.setVisibility(View.VISIBLE);
        if (age < 60) {
            noDehydrationTreatmentcheckboxthree.setVisibility(View.GONE);
            noDehydrationTreatmentcheckboxtwo.setVisibility(View.GONE);

            noDehydrationUnder2Months.setVisibility(View.VISIBLE);
        } else if (age >= 60 && age <= 180) {
            noDehydrationTreatmentcheckboxthree.setVisibility(View.GONE);
            noDehydrationAbove2Months.setVisibility(View.VISIBLE);
        } else if (age >= 181) {
            noDehydrationTreatmentcheckboxtwo.setVisibility(View.GONE);
            noDehydrationAbove2Months.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Classification 15 - Very Critical Disease 2 months to 5 Years
     * Setting Kotraimoxasol Dose Depending on weight or Age
     * Dose differ in two age groups: 2 Months to 1 Year and 1 year to 5 Years
     */
    private void criticalDisease2Mto5Y(int param) {
        showReferSegment();
        if (param >= 60 && param <= 365) {
            twoMonthstoOneYearAntiiotic();
        } else if (param > 365) {
            oneYeartoFiveYearAntibiotic(param);
        }
        loadDiazipumDose(param);
        c15Treatment.setVisibility(View.VISIBLE);
        layoutClassification15Advice.setVisibility(View.VISIBLE);
    }

    /**
     * Classification 16 - Pneumonia 2 months to 5 year
     * Setting Kotraimoxasol Dose Depending on weight or Age
     * Dose differ in two age groups: 2 Months to 1 Year and 1 year to 5 Years
     */
    private void classification16Treatment(int age, int daydiff) {
        if(daydiff < 14) {
            if (age >= 60 && age <= 365) {
                twoMonthstoOneYearAntiiotic();
            } else if (age > 365) {
                oneYeartoFiveYearAntibiotic(age);
            }
            layoutpneumonia2mto5y.setVisibility(View.VISIBLE);
            layoutClassification16Advice.setVisibility(View.VISIBLE);
            CustomSimpleDateFormat sdf = new CustomSimpleDateFormat("yyyy-MM-dd");
            kotramoxasolfollowupdate = ConvertNumberToBangla(getDateStringUIFormat(calculateFollowupDate()));
            textPneumoniaFollowup.setText(kotramoxasolfollowupdate + " তারিখে কোট্রাইমোক্সাজোল ডোজ নিতে আসতে বলা হয়েছে");
            Date currentDate = Calendar.getInstance().getTime();
            visitdate = sdf.format(currentDate);
            followupDate = calculateFollowupDate();
            if (followupDate.equals(visitdate)) {
                kotraimoxaxolinSecondDoseGiven.setVisibility(View.VISIBLE);
            }
        } else {
            showReferSegment();
            (fragmentView.findViewById(R.id.textPneumoniaReferTreatment)).setVisibility(View.VISIBLE);
        }

    }

    /**
     * Classification 17 - Normal Flue Treatment
     * Layout change based on Refer Status
     */
    private void classification17Treatment(int param) {
        if(param >14)
        {
            showReferSegment();
            layoutClassification17Refer.setVisibility(View.VISIBLE);
        } else {
            layoutClassification17Advice.setVisibility(View.VISIBLE);
            layoutClassification17Refer.setVisibility(View.GONE);
            childReferReasonLayout.setVisibility(View.GONE);
            childReferCenterNameLayout.setVisibility(View.GONE);
            referStatusLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Classification 19 Treatment
     */
    private void classification19Treatment(int age) {
        showReferSegment();
        if (age >= 60 && age <= 365) {
            textKotraiMoxelTablet2Mto12M.setVisibility(View.VISIBLE);
            textAmoxacilinTablet2Mto12M.setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.textPeracitemol3Y)).setVisibility(View.VISIBLE);
        } else if (age > 365 && age < 1096) {
            textKotraiMoxelSyrup1Yto5Y.setVisibility(View.VISIBLE);
            textAmoxacilin1Yto3Y.setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.textPeracitemol3Y)).setVisibility(View.VISIBLE);
        } else {
            textKotraiMoxelSyrup1Yto5Y.setVisibility(View.VISIBLE);
            textAmoxacilin3Yto5Y.setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.textPeracitemol5Y)).setVisibility(View.VISIBLE);
        }
        earproblem2mto5yC1.setVisibility(View.VISIBLE);

    }

    /**
     * Classification 26 Segment
     */
    private void claasification26Treatment(int age) {
        if (age >= 60 && age <= 365) {
            textKotraiMoxelTablet2Mto12M.setVisibility(View.VISIBLE);
            textAmoxacilinTablet2Mto12M.setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.textPeracitemol3Y)).setVisibility(View.VISIBLE);
        } else if (age > 365 && age < 1096) {
            textKotraiMoxelSyrup1Yto5Y.setVisibility(View.VISIBLE);
            textAmoxacilin1Yto3Y.setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.textPeracitemol3Y)).setVisibility(View.VISIBLE);
        } else {
            textKotraiMoxelSyrup1Yto5Y.setVisibility(View.VISIBLE);
            textAmoxacilin3Yto5Y.setVisibility(View.VISIBLE);
            (fragmentView.findViewById(R.id.textPeracitemol5Y)).setVisibility(View.VISIBLE);
        }
        layoutClassification26NonRefer.setVisibility(View.VISIBLE);
    }

    /**
     * Classification 27 Segment
     */
    private void classification27Treatment() {
        layoutClassification27NonRefer.setVisibility(View.VISIBLE);
    }

    /**
     * Classification 20 Segment
     */
    private void classification20Treatment(int age, int weight) {
        showReferSegment();
        amoxacilinDose(age);
        layoutClassification20Refer.setVisibility(View.VISIBLE);
        qunaineDose(age, weight);
        peracitemolDose(age);
    }

    /**
     * Classification 21 Segment
     */
    private void classification21Treatment(int age, int dayDiff) {
        if (dayDiff > 7) {
            feverReferTreatment();
        } else {
            layoutClassification21NonRefer.setVisibility(View.VISIBLE);
            c22T2.setVisibility(View.GONE);
            if (age < 366 && weight < 10000) {
                classification21NonReferMedicine1.setText(R.string.classification21Dose1);
                classification21NonReferMedicine2.setText(R.string.paracitemolDose1);
            } else {
                classification21NonReferMedicine1.setText(R.string.classification21Dose2);
                classification21NonReferMedicine2.setText(R.string.paracitemolDose2);
            }
        }

    }

    /**
     * Classification 22 Treatment
     */
    private void classification22Treatment(int dayDiff) {
        if (dayDiff > 7) {
            feverReferTreatment();
        } else {
            layoutClassification21NonRefer.setVisibility(View.VISIBLE);
            c21T1.setVisibility(View.GONE);
            classification21NonReferMedicine1.setVisibility(View.GONE);
            if (age < 366 && weight < 10000) {
                classification21NonReferMedicine2.setText(R.string.paracitemolDose1);
            } else {
                classification21NonReferMedicine2.setText(R.string.paracitemolDose2);
            }
        }
    }

    /**
     * Classification 23 Treatment Segment
     */
    private void classification23Treatment(int age) {
        showReferSegment();
        layoutClassification23.setVisibility(View.VISIBLE);
        classification28Label.setVisibility(View.GONE);
        classification29Label.setVisibility(View.GONE);
        c28T3.setVisibility(View.GONE);
        c28T4.setVisibility(View.GONE);
        if (age < 366) {
            classification23Medicine1.setText(R.string.amoxicilintabletdoseone);
        } else {
            classification23Medicine1.setText(R.string.amoxicilintabletdosetwo);
        }
    }

    /**
     * Classification 28 Segment
     */
    private void classification28Treatment() {
        layoutClassification23.setVisibility(View.VISIBLE);
        classification23Label.setVisibility(View.GONE);
        classification29Label.setVisibility(View.GONE);
        classification23Medicine1.setVisibility(View.GONE);
        c23T2.setVisibility(View.GONE);
        c23T4.setVisibility(View.GONE);

    }

    /**
     * Classification 29 Treatment
     */
    private void classification29Treatment() {
        layoutClassification23.setVisibility(View.VISIBLE);
        classification23Label.setVisibility(View.GONE);
        classification28Label.setVisibility(View.GONE);
        classification23Medicine1.setVisibility(View.GONE);
        c23T2.setVisibility(View.GONE);
        c23T3.setVisibility(View.GONE);
        c23T4.setVisibility(View.GONE);
        c28T3.setVisibility(View.GONE);
        c28T4.setVisibility(View.GONE);
    }

    /**
     * Classification 24 Segment
     */
    private void classification24Treatment(int age) {
        showReferSegment();
        layoutClassification24.setVisibility(View.VISIBLE);
        if (age < 366) {
            classification24DoseLabel.setText(R.string.amoxicilintabletdoseone);
        } else {
            classification24DoseLabel.setText(R.string.amoxicilintabletdosetwo);
        }
    }

    /**
     * Classification 30 Segment
     */
    private void classification30Treatment(int age) {
        if (age < 180) {
            c30T2.setVisibility(View.GONE);
        }
        layoutClassification30.setVisibility(View.VISIBLE);
        if (age < 366) {
            classification30DoseLabel.setText(R.string.amoxicilintabletdoseone);
        } else {
            classification30DoseLabel.setText(R.string.amoxicilintabletdosetwo);
        }
    }

    /**
     * Classification 31 Segment
     */
    private void classification31Treatment() {
        layoutClassification31.setVisibility(View.VISIBLE);

    }

    /**
     * Classification 32 Segment
     */
    private void classification32Treatment(int age) {
        layoutClassification32Advice.setVisibility(View.VISIBLE);
        if (age > 730) {
            text1.setVisibility(View.GONE);
        }

    }

    //Classification 35 segment
    private void classification35Treatment() {
        layoutClassification35.setVisibility(View.VISIBLE);
        showReferSegment();

    }

    //Classification 36 segment
    private void classification36Treatment() {
        layoutClassification36.setVisibility(View.VISIBLE);
    }

    //Classification 37 Segment
    private void classification37Treatment(int age, int weight) {
        layoutClassification37.setVisibility(View.VISIBLE);
        if ((age >= 60 && age <= 120) && (weight >= 4000 && weight <= 5999)) {
            classification37Dose.setText(R.string.amasoyDose1);
        } else if ((age >= 121 && age <= 1095) && (weight >= 6000 && weight <= 13999)) {
            classification37Dose.setText(R.string.amasoyDose2);
        } else {
            classification37Dose.setText(R.string.amasoyDose3);
        }
    }


    /**
     * Classification 21, 22 Refer Segment
     */
    private void feverReferTreatment() {
        classification21SegmentRefer.setVisibility(View.VISIBLE);
        radioButtonReferYes.setChecked(true);
        radioButtonReferNo.setVisibility(View.GONE);
        referStatusLayout.setVisibility(View.VISIBLE);
        referChecked();
    }

    /**
     * Qunaine Dose Segment
     */
    private void qunaineDose(int age, int weight) {
        if ((age >= 60 && age <= 120) && (weight >= 4000 && weight <= 5999)) {
            c20DoseLabel.setText(R.string.quinieDose1);
        } else if ((age >= 121 && age <= 270) && (weight >= 6000 && weight <= 9999)) {
            c20DoseLabel.setText(R.string.quinieDose2);
        } else if ((age >= 271 && age <= 365) && (weight >= 10000 && weight <= 11999)) {
            c20DoseLabel.setText(R.string.quinieDose3);
        } else if ((age >= 366 && age <= 1080) && (weight >= 12000 && weight <= 13999)) {
            c20DoseLabel.setText(R.string.quinieDose4);
        } else {
            c20DoseLabel.setText(R.string.quinieDose5);
        }
    }

    /**
     * Paracetemol Dose Segment
     */
    private void amoxacilinDose(int age) {
        if (age >= 60 && age <= 365) {
            textAmoxacilinTablet2Mto12M.setVisibility(View.VISIBLE);
        } else if (age > 365 && age < 1096) {
            textAmoxacilin1Yto3Y.setVisibility(View.VISIBLE);
        } else {

        }
    }

    /**
     * Injection Axomacilin Dose
     */
    private void peracitemolDose(int age) {
        if (age >= 60 && age <= 1080) {
            c20DoseLabel2.setText(R.string.paracitemolDose1);
        } else if (age > 1080) {
            c20DoseLabel2.setText(R.string.paracitemolDose2);
        }
    }

    /**
     * Antibiotic Kotraimoxel and Amoxacilin Segment 2 Months to 1 year
     */
    private void twoMonthstoOneYearAntiiotic() {
        textKotraiMoxelTablet2Mto12M.setVisibility(View.VISIBLE);
        textAmoxacilinTablet2Mto12M.setVisibility(View.VISIBLE);
        injectionamoxicillinDoseGivenSctionTwo.setVisibility(View.VISIBLE);
        tabKotraiMoxelDoseGiven.setVisibility(View.VISIBLE);
    }

    private void loadDiazipumDose(int param)
    {
        if(param <181) {
            ( fragmentView.findViewById(R.id.textDiazepam6M)).setVisibility(View.VISIBLE);
        } else if(param >180 && param < 366) {
            (fragmentView.findViewById(R.id.textDiazepam1Y)).setVisibility(View.VISIBLE);
        } else if(param > 365 && param < 1096) {
            (fragmentView.findViewById(R.id.textDiazepam3Y)).setVisibility(View.VISIBLE);
        } else {
            (fragmentView.findViewById(R.id.textDiazepam5Y)).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Antibiotic Kotraimoxel and Amoxacilin Segment 1 year to 5 Years
     */
    private void oneYeartoFiveYearAntibiotic(int param) {
        textKotraiMoxelSyrup1Yto5Y.setVisibility(View.VISIBLE);
        if (param > 365 && param < 1096) {
            textAmoxacilin1Yto3Y.setVisibility(View.VISIBLE);
        } else {
            textAmoxacilin3Yto5Y.setVisibility(View.VISIBLE);
        }
        injectionamoxicillinDoseGivenSctionTwo.setVisibility(View.VISIBLE);
        tabKotraiMoxelDoseGiven.setVisibility(View.VISIBLE);
    }

    /**
     * If refer is checked then refer center name and reason will show
     */
    private void referChecked() {
        childReferCenterNameLayout.setVisibility(View.VISIBLE);
        spinner = (Spinner) fragmentView.findViewById(R.id.childReferCenterNameSpinner);

        childReferReasonLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Calculating 2nd Day Follow up visit
     */
    private String calculateFollowupDate() {
        String visitdate = "";
        try {
            //Calculating gentamycin based on weight of child
            visitdate = ChildCareActivity.getJSONObject().getString("visitDate");
            CustomSimpleDateFormat sdf = new CustomSimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(visitdate));
            c.add(Calendar.DATE, 1);  // number of days to add
            visitdate = sdf.format(c.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return visitdate;
    }

    /**
     * Setting up Followup Segment for Non refer Treatment
     */
    private void firstFollowUpLayoutLoad() {
        layoutFollowupNonReferSegment.setVisibility(View.VISIBLE);
        String visitDate, firstFollowupDate;
        try {
            visitDate = ChildCareActivity.getJSONObject().getString("visitDate");
            CustomSimpleDateFormat sdf = new CustomSimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(visitDate));
            c.add(Calendar.DATE, 4);  // number of days to add
            visitDate = sdf.format(c.getTime());
            Date currentDate = Calendar.getInstance().getTime();
            firstFollowupDate = sdf.format(currentDate);
            if (visitDate.equals(firstFollowupDate)) {
                injectiongentamicinSecondDoseGiven.setVisibility(View.VISIBLE);
                layoutFirstFollowupNonReferSegment.setVisibility(View.VISIBLE);
            }
            secondFollowupLayoutLoad();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setReferredClassification() {
        LinearLayout ll = (LinearLayout) fragmentView.findViewById(R.id.layoutClassificationAsReferReason);
        try {
            if (ChildCareActivity.jsonObject.has("classifications")) {
                String[] strArrayClassifications = ChildCareActivity.jsonObject.getString("classifications").split(",");

                for (int i = 0; i < strArrayClassifications.length; i++) {
                    int classificationCode = Integer.valueOf(strArrayClassifications[i]);
                    if (ConstantMaps.referredChildClassifications.containsKey(strArrayClassifications[i])) {
                        referredClArray.add(classificationCode);
                    }
                }
                Collections.sort(referredClArray);

                for (int i = 0; i < referredClArray.size(); i++) {
                    TextView tv = new TextView(this.getActivity());
                    String title = ConstantJSONs.classificationDetail()
                            .getJSONObject(String.valueOf(referredClArray.get(i))).getString("name");
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(0, 0, 0, 5);
                    tv.setLayoutParams(layoutParams);
                    tv.setPadding(5, 5, 5, 5);
                    //Setting Classification List in Classification Segment
                    tv.setText(title);
                    tv.setTextSize(20);
                    ll.addView(tv);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * Method for showing Refer Segment with auto selection Refer
     * */
    private void showReferSegment()
    {
        radioButtonReferYes.setChecked(true);
        radioButtonReferNo.setVisibility(View.GONE);
        referStatusLayout.setVisibility(View.VISIBLE);
        referChecked();
    }

    /**
     * Hide Refer Segment if not Reffered
     * */
    private void hideReferSegment()
    {
        Utilities.Reset(getActivity(), fragmentView.findViewById(R.id.layoutReferSegment));
        (fragmentView.findViewById(R.id.layoutReferSegment)).setVisibility(View.GONE);
    }


    /**
     * Loading Second Follow up Segment
     */
    private void secondFollowupLayoutLoad() {
        String visitDateForSecond, secondFollowupDate;
        try {
            visitDateForSecond = ChildCareActivity.getJSONObject().getString("visitDate");
            CustomSimpleDateFormat sdf = new CustomSimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(visitDateForSecond));
            c.add(Calendar.DATE, 8);
            visitDateForSecond = sdf.format(c.getTime());
            Date currentDate = Calendar.getInstance().getTime();
            secondFollowupDate = sdf.format(currentDate);
            if (visitDateForSecond.equals(secondFollowupDate)) {
                injectiongentamicinSecondDoseGiven.setVisibility(View.VISIBLE);
                layoutFirstFollowupNonReferSegment.setVisibility(View.VISIBLE);
                layoutSecondFollowupNonReferSegment.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
