package org.sci.rhis.fwc;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.Utilities;

import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.HashMap;


public class ClientInfoFragment extends Fragment implements OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImageButton ib;
    private CustomDatePickerDialog datePickerDialog,datePickerDialogNormal;
    private HashMap<Integer, EditText> datePickerPair;
    CheckBox bleeding,delayed,blocked,placenta,deadBirth,deadNewBorn,caesar;

    View view;

    EditText para,gravida,sonNum,dauNum,height_feet,height_inch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_client_info, container, false);

        ib = (ImageButton) view.findViewById(R.id.Date_Picker_Button);
        ib.setOnClickListener(this);

        view.findViewById(R.id.buttonUnknownLMPDate).setOnClickListener(this);

        setPreviousDeliverCheckbox(view);

        datePickerDialog = new CustomDatePickerDialog(getActivity(), new CustomSimpleDateFormat("dd/MM/yyyy"));
        datePickerDialogNormal = new CustomDatePickerDialog(getActivity(), new CustomSimpleDateFormat("dd/MM/yyyy"));
        datePickerPair = new HashMap<Integer, EditText>();

        datePickerPair.put(R.id.Date_Picker_Button, (EditText) view.findViewById(R.id.lmpDate));
        datePickerPair.put(R.id.Clients_TT_Tika1, (EditText)view.findViewById(R.id.ttDate1));
        datePickerPair.put(R.id.Clients_TT_Tika2, (EditText)view.findViewById(R.id.ttDate2));
        datePickerPair.put(R.id.Clients_TT_Tika3, (EditText)view.findViewById(R.id.ttDate3));
        datePickerPair.put(R.id.Clients_TT_Tika4, (EditText)view.findViewById(R.id.ttDate4));
        datePickerPair.put(R.id.Clients_TT_Tika5, (EditText)view.findViewById(R.id.ttDate5));

        CheckBox ttArray [] = {
                getCheckbox(view, R.id.Clients_TT_Tika1),
                getCheckbox(view, R.id.Clients_TT_Tika2),
                getCheckbox(view, R.id.Clients_TT_Tika3),
                getCheckbox(view, R.id.Clients_TT_Tika4),
                getCheckbox(view, R.id.Clients_TT_Tika5)
        };

        for (int i = 0 ; i < ttArray.length; i++ ) {
            ttArray[i].setOnClickListener(this);
        }

        para = (EditText)view.findViewById(R.id.para);
        para.addTextChangedListener(new CustomTextWatcher(getActivity(),para));
        gravida = (EditText)view.findViewById(R.id.gravida);
        gravida.addTextChangedListener(new CustomTextWatcher(getActivity(),gravida));
        sonNum = (EditText)view.findViewById(R.id.SonNum);
        sonNum.addTextChangedListener(new CustomTextWatcher(getActivity(),sonNum));
        dauNum = (EditText)view.findViewById(R.id.DaughterNum);
        dauNum.addTextChangedListener(new CustomTextWatcher(getActivity(),dauNum));
        height_feet = (EditText)view.findViewById(R.id.heightFeet);
        height_feet.addTextChangedListener(new CustomTextWatcher(getActivity(),height_feet));
        height_inch = (EditText)view.findViewById(R.id.heightInch);
        height_inch.addTextChangedListener(new CustomTextWatcher(getActivity(),height_inch));

        changeMandatorySignColor(view);

        view.findViewById(R.id.textViewDirectPNC).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SecondActivity)getActivity()).deliveryWithoutPregInfo();
            }
        });

        view.findViewById(R.id.TextViewClients_Para).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogCreator.SimpleMessageDialog(getActivity(),getString(R.string.para_gravida_info),getString(R.string.para_gravida_info_title),android.R.drawable.ic_dialog_info);
            }
        });

        view.findViewById(R.id.TextViewClients_Gravida).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogCreator.SimpleMessageDialog(getActivity(),getString(R.string.para_gravida_info),getString(R.string.para_gravida_info_title),android.R.drawable.ic_dialog_info);
            }
        });

        view.findViewById(R.id.imageViewRiskyPregnancyInfo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogCreator.SimpleMessageDialog(getActivity(),getString(R.string.risky_pregnancy_reference),getString(R.string.risky_pregnancy),android.R.drawable.ic_dialog_info);
            }
        });
        return view;
    }

    private void changeMandatorySignColor(View view){
        ((TextView)view.findViewById(R.id.TextViewLMPDate)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.TextViewLMPDate)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.TextViewDeliveryDate)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.TextViewDeliveryDate)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.TextViewClients_Para)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.TextViewClients_Para)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.TextViewClients_Gravida)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.TextViewClients_Gravida)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.TextViewLive_son_Num)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.TextViewLive_son_Num)).getText().toString(), 0, 1));
        ((TextView)view.findViewById(R.id.TextViewLast_Child_Age)).setText(Utilities.changePartialTextColor(Color.RED,
                ((TextView)view.findViewById(R.id.TextViewLast_Child_Age)).getText().toString(), 0, 1));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.lmpDate || v.getId() == R.id.Date_Picker_Button) {
            datePickerDialogNormal.show(datePickerPair.get(v.getId()));

        } else if(v.getTag() != null && v.getTag().equals("TT")) {
            SecondActivity.show_TT_UI(SecondActivity.checkMaxTTfromUI());
            if (datePickerPair.containsKey(v.getId()) ) {
                if(getCheckbox(v, v.getId()).isChecked()) {
                    datePickerDialog.showWithCustomButton(datePickerPair.get(v.getId()),
                            getString(R.string.unknown_date),getString(R.string.okay_text));
                    //datePickerDialog.show(datePickerPair.get(v.getId()));
                }else{
                    EditText ttDateEditText = datePickerPair.get(v.getId());
                    ttDateEditText.setText("");
                }
            }
        }

        else if(v.getId() == R.id.buttonUnknownLMPDate) {
            AlertDialogCreator.showDateInputDialog(getActivity(), "শেষ মাসিক আনুমানিক কতদিন আগে?");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d("ClientInfo", "OnActivityResult");
    }

    CheckBox getCheckbox(View view, int id) {
        return (CheckBox)view.findViewById(id);
    }

    void setPreviousDeliverCheckbox(View view){
        //NOTE: only risk factors are considered................
        bleeding = (CheckBox) view.findViewById(R.id.previousDeliveryBleedingCheckBox);
        bleeding.setOnCheckedChangeListener(this);
        delayed = (CheckBox) view.findViewById(R.id.delayedBirthCheckBox);
        delayed.setOnCheckedChangeListener(this);
        blocked = (CheckBox) view.findViewById(R.id.blockedDeliveryCheckBox);
        blocked.setOnCheckedChangeListener(this);
        placenta = (CheckBox) view.findViewById(R.id.placentaInsideUterusCheckBox);
        placenta.setOnCheckedChangeListener(this);
        deadBirth = (CheckBox) view.findViewById(R.id.giveBirthDeadCheckBox);
        deadBirth.setOnCheckedChangeListener(this);
        deadNewBorn = (CheckBox) view.findViewById(R.id.newbornDieWithin48hoursCheckBox);
        deadNewBorn.setOnCheckedChangeListener(this);
        caesar = (CheckBox) view.findViewById(R.id.caesarCheckBox);
        caesar.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean b) {

        LinearLayout previousDeliveryLayout = (LinearLayout) getActivity().findViewById(R.id.Previous_Delivery);

        if(checkRisk()){
            previousDeliveryLayout.setBackgroundResource(R.drawable.layoutbordersimple_red);
        }else{
            previousDeliveryLayout.setBackgroundResource(R.drawable.layoutbordersimple);
        }
    }

    private boolean checkRisk(){
        boolean hasRisk = false;

        if(bleeding.isChecked()) hasRisk=true;
        if(delayed.isChecked()) hasRisk=true;
        if(blocked.isChecked()) hasRisk=true;
        if(placenta.isChecked()) hasRisk=true;
        if(deadBirth.isChecked()) hasRisk=true;
        if(deadNewBorn.isChecked()) hasRisk=true;
        if(caesar.isChecked()) hasRisk=true;

        return hasRisk;
    }
}


