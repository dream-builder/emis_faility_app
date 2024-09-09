package org.sci.rhis.fwc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;


import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.HashMap;
import static org.sci.rhis.utilities.Utilities.fahrenheitToCelcious;
import static org.sci.rhis.utilities.Utilities.gramToKg;



/**
 * Created by hajjaz.ibrahim on 3/14/2018.
 */

public class ChildCareFragmentPhysicalExamination extends CustomFragment implements View.OnClickListener {

    public static View fragmentView;
    private ImageButton ib,psbiVisitingDatePicker;
    private CustomDatePickerDialog datePickerDialogNormal;
    TextView psbiWeightLabel;
    private HashMap<Integer, EditText> datePickerPair;
    EditText psbiTemperatureValueEditText, psbiWeightValueEditText, psbiBreathValueEditText, psbiSymptomStartDateEditText, psbiVisitingDateValueEditText;
    Button btnPhysicalExaminationNext;
    public static boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_childcare_physicalexamination, container, false);
        initFragment();

        ib = (ImageButton) fragmentView.findViewById(R.id.sympdomstartdatePicker);
        psbiVisitingDatePicker = (ImageButton) fragmentView.findViewById(R.id.psbiVisitingDatePicker);
        ib.setOnClickListener(this);
        psbiVisitingDatePicker.setOnClickListener(this);
        fragmentView.findViewById(R.id.buttonChildKgEntry).setOnClickListener(this);

        datePickerDialogNormal = new CustomDatePickerDialog(getActivity(), new CustomSimpleDateFormat("dd/MM/yyyy"));
        datePickerPair = new HashMap<Integer, EditText>();

        datePickerPair.put(R.id.psbiVisitingDatePicker, (EditText) fragmentView.findViewById(R.id.psbiVisitingDateValueEditText));
        datePickerPair.put(R.id.sympdomstartdatePicker, (EditText) fragmentView.findViewById(R.id.sympdomstartdateValueEditText));


        if(ChildCareActivity.jsonObject!=null && ChildCareActivity.jsonObject.length()>1) loadDataIfAny();

        changeMandatorySignColor(fragmentView);

        return fragmentView;
    }

    private void loadDataIfAny(){
        setInputValues(ChildCareActivity.jsonObject);
    }



    private void initFragment() {
        initiateFields();

        //Next Button Functionality
        btnPhysicalExaminationNext.setOnClickListener(view -> {
            getInputValues(ChildCareActivity.jsonObject);
            if(!Validation.hasText(psbiBreathValueEditText) ||
                    !Validation.hasText(psbiTemperatureValueEditText) || !Validation.hasText(psbiWeightValueEditText)
                    || !Validation.hasText(psbiVisitingDateValueEditText) || !Validation.hasText(psbiSymptomStartDateEditText)){
                Toast.makeText(getActivity(),getString(R.string.mandatory_field_validation_message),Toast.LENGTH_LONG).show();
            }
            else{
            ChildCareActivity.viewPager.setCurrentItem(1);
            //Calling initFragment() method forcefully to set checkbox automatically
            ChildCareFragmentSymptom.getInstance().initFragment();
            }
        });

        initialize(); //super class

        if (editMode) {
            Utilities.Enable(getActivity(), fragmentView.findViewById(R.id.containerPhysicalExamination));
        } else {
            Utilities.Disable(getActivity(), fragmentView.findViewById(R.id.containerPhysicalExamination));
            Utilities.Enable(getActivity(),fragmentView.findViewById(R.id.btnPhysicalExaminationNext));
        }

    }

    //Binding layout
    private void initiateFields() {
        psbiWeightLabel = (TextView) fragmentView.findViewById(R.id.psbiWeightLabel);
        btnPhysicalExaminationNext = (Button) fragmentView.findViewById(R.id.btnPhysicalExaminationNext);
        psbiTemperatureValueEditText = (EditText) fragmentView.findViewById(R.id.psbiTemperatureValueEditText);
        psbiWeightValueEditText = (EditText) fragmentView.findViewById(R.id.psbiWeightValueEditText);
        psbiBreathValueEditText = (EditText) fragmentView.findViewById(R.id.psbiBreathValueEditText);
        psbiSymptomStartDateEditText = (EditText) fragmentView.findViewById(R.id.sympdomstartdateValueEditText);
        psbiVisitingDateValueEditText = (EditText) fragmentView.findViewById(R.id.psbiVisitingDateValueEditText);
    }

    private void changeMandatorySignColor(View view){
        ((TextView)view.findViewById(R.id.psbiVisitingDateLabel)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.psbiVisitingDateLabel)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.psbiTemperatureLabel)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.psbiTemperatureLabel)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.psbiWeightLabel)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.psbiWeightLabel)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.psbiBreathLabel)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.psbiBreathLabel)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.psbiSymptomStartDateLabel)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.psbiSymptomStartDateLabel)).getText().toString(), 0, 1));
    }

    public void getInputValues(JSONObject jsonObject) {
        Utilities.getEditTexts(jsonEditTextMap, jsonObject);
        Utilities.getEditTextDates(jsonEditTextDateMap,jsonObject);
    }

    public void setInputValues(JSONObject jsonObject) {
        Utilities.setEditTexts(jsonEditTextMap, jsonObject);
        Utilities.setEditTextDates(jsonEditTextDateMap, jsonObject);
    }

    @Override
    protected void initiateCheckboxes() {

    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("1", (EditText) fragmentView.findViewById(R.id.psbiTemperatureValueEditText)); //Temperature
        jsonEditTextMap.put("2", (EditText) fragmentView.findViewById(R.id.psbiWeightValueEditText)); //Weight
        jsonEditTextMap.put("3", (EditText) fragmentView.findViewById(R.id.psbiBreathValueEditText)); //Breath Per Minute

        psbiTemperatureValueEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String strFahrenheit = psbiTemperatureValueEditText.getText().toString();
                TextView textView = (TextView) fragmentView.findViewById(R.id.psbiTemperatureCelciousUnitLabel);
                textView.setText(fahrenheitToCelcious(strFahrenheit));
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        psbiWeightValueEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String strWeightGram = psbiWeightValueEditText.getText().toString();
                TextView textView = (TextView) fragmentView.findViewById(R.id.psbiWeightKgUnitLabel);
                textView.setText(gramToKg(strWeightGram));
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });


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
        jsonEditTextDateMap.put("indicationStartDate", (EditText) fragmentView.findViewById(R.id.sympdomstartdateValueEditText));
        jsonEditTextDateMap.put("visitDate",(EditText) fragmentView.findViewById(R.id.psbiVisitingDateValueEditText));

    }

    @Override
    protected void initiateRadioGroups() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sympdomstartdateValueEditText:
            case R.id.sympdomstartdatePicker:
            case R.id.psbiVisitingDateValueEditText:
            case R.id.psbiVisitingDatePicker:
                datePickerDialogNormal.show(datePickerPair.get(view.getId()));
                break;
            case R.id.buttonChildKgEntry:
                weightInKg();
                break;

            default:
                break;

        }
    }

    private void weightInKg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog kgEntryDialog;
        builder.setTitle("Weight in KG");
        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);
        builder.setCancelable(false);

        builder.setPositiveButton("OKAY", (dialog, which) -> {
            String weightInKgValue = input.getText().toString();
            if (weightInKgValue != null && !weightInKgValue.isEmpty()){
                //TODO:has to get the threshold value from any domain expert
                if(Double.valueOf(weightInKgValue)>=50){
                    Toast.makeText(getActivity(),"Invalid Weight in KG!!!",Toast.LENGTH_LONG).show();
                }else{
                    ((EditText)fragmentView.findViewById(R.id.psbiWeightValueEditText))
                            .setText(String.valueOf(Converter.kgToGram(Double.valueOf(weightInKgValue))));
                    dialog.dismiss();
                }

            }
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.cancel();
        });

       kgEntryDialog = builder.create();
       kgEntryDialog.show();

    }
}
