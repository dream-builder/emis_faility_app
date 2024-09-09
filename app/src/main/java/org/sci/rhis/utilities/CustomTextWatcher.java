package org.sci.rhis.utilities;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.sci.rhis.fwc.DeliveryActivity;
import org.sci.rhis.fwc.FPStartupFragment;
import org.sci.rhis.fwc.NRCActivity;
import org.sci.rhis.fwc.R;
import org.sci.rhis.fwc.SecondActivity;
import org.sci.rhis.model.PregWoman;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by arafat.hasan on 3/31/2016.
 */
public class CustomTextWatcher  implements TextWatcher{
    private EditText mEditText;
    private Activity mActivity;

    public interface CustomWatcherListner {
        void onTextChanged();
    }

    public CustomTextWatcher(Activity activity,EditText eEditText) {
        this.mActivity = activity;
        this.mEditText = eEditText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        int viewId = mEditText.getId();
        switch (viewId){
            case R.id.para:
                try {
                    String paraStr = mEditText.getText().toString();
                    EditText gravida = (EditText) mActivity.findViewById(R.id.gravida);
                    String gravidaStr = gravida.getText().toString();
                    if(!paraStr.equals("")) {

                        int value = Integer.valueOf(paraStr);
                        if(!gravidaStr.equals("") && value>=Integer.valueOf(gravidaStr)){
                            //as the para count increases 1 in case of twin/triplet/more at once
                            Utilities.showAlertToast(mActivity,"গ্রাভিডা প্যারা হতে ছোট বা সমান হতে পারে না");
                            mEditText.setText("");
                            gravida.setText("");
                            gravida.setBackgroundResource(R.drawable.edittext_round);
                            mEditText.setBackgroundResource(R.drawable.edittext_round);
                        }

                        Utilities.SetVisibility(mActivity, R.id.born_blood, (value == 0) ? View.GONE : View.VISIBLE);
                        Utilities.SetVisibility(mActivity, R.id.age_lasr_child_height, (value == 0) ? View.GONE : View.VISIBLE);
                    }

                } catch (NumberFormatException NFE) {
                    Utilities.printTrace(NFE.getStackTrace());
                }
                break;
            //.................................................................................................................
            case R.id.gravida:
                try {
                    String gravidaStr = mEditText.getText().toString();
                    EditText para = (EditText) mActivity.findViewById(R.id.para);
                    String paraStr = para.getText().toString();
                    if (!gravidaStr.equals("")) {
                        int value = Integer.valueOf(gravidaStr);
                        if(value == 0){
                            Utilities.showAlertToast(mActivity,"গর্ভবতী মায়ের গ্রাভিডা শূন্য হতে পারে না");
                            mEditText.setText("");
                            para.setBackgroundResource(R.drawable.edittext_round);
                            mEditText.setBackgroundResource(R.drawable.edittext_round);
                        }else if(!paraStr.equals("") && Integer.valueOf(paraStr)>=value){
                            //as the para count increases 1 in case of twin/triplet/more at once
                            Utilities.showAlertToast(mActivity,"গ্রাভিডা প্যারা হতে ছোট বা সমান হতে পারে না");
                            mEditText.setText("");
                            para.setBackgroundResource(R.drawable.edittext_round);
                            mEditText.setBackgroundResource(R.drawable.edittext_round);
                        }else if (value == 1 || value > 3) {
                            para.setBackgroundResource(R.drawable.edittext_round_red);
                            mEditText.setBackgroundResource(R.drawable.edittext_round_red);
                        } else {
                            para.setBackgroundResource(R.drawable.edittext_round);
                            mEditText.setBackgroundResource(R.drawable.edittext_round);
                        }
                        Utilities.SetVisibility(mActivity, R.id.Previous_Delivery, (value > 1) ? View.VISIBLE : View.GONE);
                    }else{
                        para.setBackgroundResource(R.drawable.edittext_round);
                        mEditText.setBackgroundResource(R.drawable.edittext_round);
                    }
                }
                catch (NumberFormatException NFE) {
                    Utilities.printTrace(NFE.getStackTrace());
                }
                break;
            case R.id.heightFeet:
                String feetStr = mEditText.getText().toString();
                EditText height_inch = (EditText) mActivity.findViewById(R.id.heightInch);
                if (!feetStr.equals("") && Integer.valueOf(feetStr)<4){
                    mEditText.setBackgroundResource(R.drawable.edittext_round_red);
                    height_inch.setBackgroundResource(R.drawable.edittext_round_red);
                } else {
                    mEditText.setBackgroundResource(R.drawable.edittext_round);
                    height_inch.setBackgroundResource(R.drawable.edittext_round);
                }
                break;
            //.................................................................................................................
            case R.id.heightInch:
                EditText height_feet = (EditText) mActivity.findViewById(R.id.heightFeet);
                String feetStr1 = height_feet.getText().toString();
                String inchStr = mEditText.getText().toString();
                if (!feetStr1.equals("") && !inchStr.equals("")) {
                    int feetValue = Integer.valueOf(feetStr1);
                    int inchValue = Integer.valueOf(inchStr);
                    if (feetValue == 4 && inchValue<10) {
                        height_feet.setBackgroundResource(R.drawable.edittext_round_red);
                        mEditText.setBackgroundResource(R.drawable.edittext_round_red);
                    } else {
                        height_feet.setBackgroundResource(R.drawable.edittext_round);
                        mEditText.setBackgroundResource(R.drawable.edittext_round);
                    }
                }
                break;

            case R.id.deliveryNewBornWeightValue:
                String newBornWeight = mEditText.getText().toString();
                try{
                    if(!newBornWeight.equals("") && Double.valueOf(newBornWeight)>9){
                        Utilities.showAlertToast(mActivity, mActivity.getResources().getString(R.string.invalid_newBorn_weight));
                        mEditText.setText("");
                    }else{
                        if(!newBornWeight.equals("") && Double.valueOf(newBornWeight)<2.5 && Double.valueOf(newBornWeight)> 2.0) {
                            mEditText.setBackgroundResource(R.drawable.edittext_round_yellow);
                        }else if(!newBornWeight.equals("") && Double.valueOf(newBornWeight)<= 2.0){
                            mEditText.setBackgroundResource(R.drawable.edittext_round_red);
                        }else{
                            mEditText.setBackgroundResource(R.drawable.edittext_round);
                        }
                    }
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }

                break;

            case R.id.iudFollowupDateEditText:
                ((CustomWatcherListner)mActivity).onTextChanged();
                break;

            case R.id.implantFollowupDateEditText:
                ((CustomWatcherListner)mActivity).onTextChanged();
                break;

            case R.id.injectableActualDateEditText:
                ((CustomWatcherListner)mActivity).onTextChanged();
                break;

            case R.id.editTextFPLastChildMonth:
                int sexVal = Integer.valueOf(((SecondActivity)mActivity).woman!=null ?
                        ((SecondActivity)mActivity).woman.getSex():((SecondActivity)mActivity).generalPerson.getSex());
                if(sexVal==2){
                    String monthVal = mEditText.getText().toString();
                    //checking invalid value in year segment
                    if(!monthVal.equals("")){
                        if(Integer.valueOf(monthVal)>11){
                            mEditText.setText("");
                            Utilities.showAlertToast(mActivity,"মাসের সংখ্যা ১১ এর বেশী হতে পারে না!");
                            break;
                        }
                    }
                    EditText year = (EditText) mActivity.findViewById(R.id.editTextFPLastChildYear);
                    String yearVal = year.getText().toString();
                    if((yearVal.equals("")|| yearVal.equals("0")) && (!monthVal.equals("") && Integer.valueOf(monthVal)<6)){
                        Utilities.MakeVisible(mActivity,R.id.fpLastChildFeedBreasMilkLayout);
                    }else{
                        Utilities.SetVisibility(mActivity,R.id.fpLastChildFeedBreasMilkLayout,View.GONE);
                    }
                }
                break;

            case R.id.editTextFPLastChildYear:
                int sexVal2 = Integer.valueOf(((SecondActivity)mActivity).woman!=null ?
                        ((SecondActivity)mActivity).woman.getSex():((SecondActivity)mActivity).generalPerson.getSex());
                if(sexVal2==2){
                    String yearValue = mEditText.getText().toString();
                    //checking invalid value in year segment
                    if(!yearValue.equals("")&&((SecondActivity)mActivity).woman.getAge()!=0){
                        int ageInYear = Integer.valueOf(yearValue);
                        int motherAge = Integer.valueOf(((SecondActivity)mActivity).woman.getAge());
                        if(ageInYear>(motherAge-10)){
                            Utilities.showAlertToast(mActivity,"বাচ্চার বয়স মায়ের বয়স হতে কম পক্ষে ১০ বছর ছোট হতে হবে");
                            mEditText.setText("");
                            break;
                        }
                    }
                    EditText month = (EditText) mActivity.findViewById(R.id.editTextFPLastChildMonth);
                    String monthValue = month.getText().toString();
                    if(!yearValue.equals("") && Integer.valueOf(yearValue)>=1){
                        Utilities.SetVisibility(mActivity,R.id.fpLastChildFeedBreasMilkLayout,View.GONE);
                    }else{
                        if(!monthValue.equals("") && Integer.valueOf(monthValue)<6){
                            Utilities.MakeVisible(mActivity,R.id.fpLastChildFeedBreasMilkLayout);
                        }
                    }
                }
                break;

            case R.id.editTextFPLmpDate:
                String lmpDate = mEditText.getText().toString();
                ((RadioGroup)mActivity.findViewById(R.id.fpIsPregnantRadioGroup)).clearCheck();
                if(!lmpDate.equals("")){
                    long days;
                    try {
                        days = Utilities.getDateDiff(Converter
                                .stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,lmpDate), new Date(), TimeUnit.DAYS);
                    } catch (ParseException e) {
                        days = -1;
                        e.printStackTrace();
                    }

                    if(days>(4*7)){ //4 weeks
                        if(!((RadioButton)mActivity.findViewById(R.id.radioFpInfoIsPregnantYes)).isChecked()){
                            if(((FPStartupFragment)mActivity.getFragmentManager().findFragmentById(R.id.fpStartupFragment)).saveMode) {
                                ((RadioGroup)mActivity.findViewById(R.id.fpIsPregnantRadioGroup)).clearCheck();
                                AlertDialogCreator.SimpleMessageWithNoTitle(mActivity,"গ্রহীতা গর্ভবতী কিনা নিশ্চিত করুন");
                            }
                        }
                    }else if(days>=0 && days <(4*7)){
                        ((RadioButton)mActivity.findViewById(R.id.radioFpInfoIsPregnantNo)).setChecked(true);
                    }
                }
                break;

            case R.id.editTextFPSonNum:
                int sexVal3 = Integer.valueOf(((SecondActivity)mActivity).woman!=null ?
                        ((SecondActivity)mActivity).woman.getSex():((SecondActivity)mActivity).generalPerson.getSex());
                int sonCount = Integer.valueOf(mEditText.getText().toString().equals("")?"0":mEditText.getText().toString());
                String dauCountVal = ((EditText) mActivity.findViewById(R.id.editTextFPDaughterNum)).getText().toString();
                int dauCount = Integer.valueOf(dauCountVal.equals("")?"0":dauCountVal);
                if((sonCount+dauCount)>0){
                    if(sexVal3==2){
                        Utilities.SetVisibility(mActivity,R.id.lastDeliveryLayout,View.VISIBLE);
                    }
                    Utilities.SetVisibility(mActivity,R.id.fpAgeLastChild,View.VISIBLE);

                }else{
                    Utilities.SetVisibility(mActivity,R.id.fpAgeLastChild,View.GONE);
                    if(sexVal3==2){
                        Utilities.SetVisibility(mActivity,R.id.lastDeliveryLayout,View.GONE);
                    }

                }
                break;

            case R.id.editTextFPDaughterNum:
                int sexVal4 = Integer.valueOf(((SecondActivity)mActivity).woman!=null ?
                        ((SecondActivity)mActivity).woman.getSex():((SecondActivity)mActivity).generalPerson.getSex());

                int dauCount1 = Integer.valueOf(mEditText.getText().toString().equals("")?"0":mEditText.getText().toString());
                String sonCountVal1 = ((EditText) mActivity.findViewById(R.id.editTextFPSonNum)).getText().toString();
                int sonCount1 = Integer.valueOf(sonCountVal1.equals("")?"0":sonCountVal1);
                if((sonCount1+dauCount1)>0){
                    if(sexVal4==2){
                        Utilities.SetVisibility(mActivity,R.id.lastDeliveryLayout,View.VISIBLE);
                    }
                    Utilities.SetVisibility(mActivity,R.id.fpAgeLastChild,View.VISIBLE);

                }else{
                    Utilities.SetVisibility(mActivity,R.id.fpAgeLastChild,View.GONE);
                    if(sexVal4==2){
                        Utilities.SetVisibility(mActivity,R.id.lastDeliveryLayout,View.GONE);
                    }

                }
                break;

            case R.id.editTextFPLastDeliveryDate:
                //UnknownLMPDate button visibility changed on LastDeliveryDate value changed
                if (!mEditText.getText().toString().isEmpty()) {
                    Utilities.SetVisibility(mActivity, R.id.buttonUnknownFPLMPDate, View.VISIBLE);
                } else {
                    Utilities.SetVisibility(mActivity, R.id.buttonUnknownFPLMPDate, View.GONE);
                }
                break;

            case R.id.Clients_Age:
                if(!mEditText.getText().toString().equals(""))
                {
                    int age = Integer.valueOf(mEditText.getText().toString());

                    if(age>150)
                    {
                        Utilities.showAlertToast(mActivity, "Invalid Age");
                        mEditText.setText("");
                    }
                }
                break;

            case R.id.gpTemperatureValue:
                if(!mEditText.getText().toString().equals(""))
                {
                    Double tempValue = Double.valueOf(mEditText.getText().toString());

                    if(tempValue>110)
                    {
                        Utilities.showAlertToast(mActivity, mActivity.getResources().getString(R.string.invalid_temperature));
                        mEditText.setText("");
                    }else if(tempValue<=110 && tempValue>101){
                        mEditText.setBackgroundResource(R.drawable.edittext_round_red);
                    }else {
                        mEditText.setBackgroundResource(R.drawable.edittext_round);
                    }
                }else{
                    mEditText.setBackgroundResource(R.drawable.edittext_round);
                }
                break;

            case R.id.pncChildWeightValue:
                if(!mEditText.getText().toString().equals(""))
                {
                    Double pncChildage = Double.valueOf(mEditText.getText().toString());

                    if(pncChildage>10)
                    {
                        Utilities.showAlertToast(mActivity, mActivity.getResources().getString(R.string.invalid_pncChild_weight));
                        mEditText.setText("");
                    }
                }
                break;


                /**
                * NRC Activity: If child age < 59 it will ask for birth weigh and here is validation added for birth weight
                * */
            case R.id.nrcBirthWeightValue:
            case R.id.psbiWeightValueEditText:
                String psbiChildAge = mEditText.getText().toString();
                try{
                    if(!psbiChildAge.equals("") && Double.valueOf(psbiChildAge)>9.0){
                        Utilities.showAlertToast(mActivity, mActivity.getResources().getString(R.string.invalid_newBorn_weight));
                        mEditText.setText("");
                    }else{
                        if(!psbiChildAge.equals("") && Double.valueOf(psbiChildAge)<2.5 && Double.valueOf(psbiChildAge)> 2.0) {
                            mEditText.setBackgroundResource(R.drawable.edittext_round_yellow);
                        }else if(!psbiChildAge.equals("") && Double.valueOf(psbiChildAge)<= 2.0){
                            mEditText.setBackgroundResource(R.drawable.edittext_round_red);
                        }else{
                            mEditText.setBackgroundResource(R.drawable.edittext_round);
                        }
                    }
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }

                break;


            case R.id.lmpDate:
                if(!mEditText.getText().toString().equals(""))
                {
                    try {
                    Date lmp = Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,mEditText.getText().toString());
                    Date edd = Utilities.addDateOffset(lmp, PregWoman.PREG_PERIOD);
                    EditText eddEditText = (EditText) mActivity.findViewById(R.id.edd);
                    eddEditText.setText(Converter.dateToString(Constants.SHORT_SLASH_FORMAT_BRITISH,edd));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                break;

            /**
             * Changing age in day,month and year according to the given DOB and showing birth-weight
             * layout if the age in days is less that 60 days
              */
            case R.id.EditTextClientsDate:
                String dateOFfBirth = ((EditText)mActivity.findViewById(R.id.EditTextClientsDate)).getText().toString();

                if(!((CheckBox)mActivity.findViewById(R.id.CBAgeNotKnown)).isChecked()) {
                    int[] intResult;

                    Utilities.SetVisibility(mActivity,R.id.layoutNRCAgeNext,View.VISIBLE);

                    try {
                        intResult = Converter.ageFromDOB(Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,dateOFfBirth));
                        ((EditText)mActivity.findViewById(R.id.Clients_AgeDay)).setText(String.valueOf(intResult[0]));
                        mActivity.findViewById(R.id.Clients_AgeDay).setEnabled(false);
                        ((EditText)mActivity.findViewById(R.id.Clients_AgeMonth)).setText(String.valueOf(intResult[1]));
                        mActivity.findViewById(R.id.Clients_AgeMonth).setEnabled(false);
                        ((EditText)mActivity.findViewById(R.id.Clients_AgeYear)).setText(String.valueOf(intResult[2]));
                        mActivity.findViewById(R.id.Clients_AgeYear).setEnabled(false);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                //Getting Age in days to compare
                int AgeInDays = 0;
                try {
                    AgeInDays = (int) ((Utilities.getDateDiff(Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH, dateOFfBirth), new Date(), TimeUnit.DAYS)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //If age< 60 days it will show birth weight segment
                Utilities.SetVisibility(mActivity,R.id.nrcBirthWeight,AgeInDays<60?View.VISIBLE:View.GONE);
                break;

            /**
             * Changing date of birth according to the given value of day/month/year
             */
            case R.id.Clients_AgeDay:
            case R.id.Clients_AgeMonth:
            case R.id.Clients_AgeYear:
                if(((CheckBox)mActivity.findViewById(R.id.CBAgeNotKnown)).isChecked()) {
                    int day = 0,month = 0,year = 0;

                    String strDay =((EditText)mActivity.findViewById(R.id.Clients_AgeDay)).getText().toString();
                    String strMonth = ((EditText)mActivity.findViewById(R.id.Clients_AgeMonth)).getText().toString();
                    String strYear = ((EditText)mActivity.findViewById(R.id.Clients_AgeYear)).getText().toString();

                    if (strDay != null && !strDay.equals("")) {
                        day = Integer.parseInt(strDay);
                        //validate date if someone tries to enter the day value as greater than 30
                        if(day>29){
                            mEditText.setText("");
                            Utilities.showAlertToast(mActivity,"দিনের সংখ্যা ২৯ এর বেশী হতে পারে না!");
                            day=0;//resetting the day value
                        }
                    }
                    if (strMonth != null && !strMonth.equals("")) {
                        month = Integer.parseInt(strMonth);
                        //validate date if someone tries to enter the month value as greater than 30
                        if(month>11){
                            mEditText.setText("");
                            Utilities.showAlertToast(mActivity,"মাসের সংখ্যা ১১ এর বেশী হতে পারে না!");
                            month=0;//resetting the month value
                        }
                    }
                    if (strYear != null && !strYear.equals("")) {
                        year = Integer.parseInt(strYear);
                        //validate date if someone tries to enter the year value as greater than 150
                        if(year>150){
                            mEditText.setText("");
                            Utilities.showAlertToast(mActivity,"অস্বাভাবিক বয়স!");
                            year=0;//resetting the year value
                        }
                    }
                    String strDOB = Converter.dateOfBirthFromAge(day, month, year, Constants.SHORT_SLASH_FORMAT_BRITISH);
                    ((EditText)mActivity.findViewById(R.id.EditTextClientsDate)).setText(strDOB);
                    //show/hide marital status according to age in year............
                    //TODO:has to modify the threshold age for marital status validation
                    if(year<=5){
                        ((Spinner)mActivity.findViewById(R.id.ClientsMaritalStatusSpinner)).setSelection(1);
                        mActivity.findViewById(R.id.ClientsMaritalStatusSpinner).setEnabled(false);
                    }else {
                        ((Spinner)mActivity.findViewById(R.id.ClientsMaritalStatusSpinner)).setSelection(0);
                        mActivity.findViewById(R.id.ClientsMaritalStatusSpinner).setEnabled(true);
                    }

                }
                break;




            /**
             * bp segment starts.................................................................................................
             */
            case R.id.ancBloodPresserValueSystolic:
            case R.id.pncBloodPresserValue:
            case R.id.gpBloodPresserValueSystolic:
            case R.id.pcBloodPresserValueSystolic:
            case R.id.injectableBPSystolicEditText:
            case R.id.pacBloodPresserValueSystolic:
                if(!mEditText.getText().toString().equals(""))
                {
                    int sysVal = Integer.valueOf(mEditText.getText().toString());
                    if(sysVal>=140){
                        mEditText.setBackgroundResource(R.drawable.edittext_round_red);
                        mEditText.setTag(Flag.HIGHBP);
                    }else {
                        mEditText.setBackgroundResource(R.drawable.edittext_round);
                        mEditText.setTag(Flag.NORMALBP);
                    }
                }else {
                    mEditText.setBackgroundResource(R.drawable.edittext_round);
                    mEditText.setTag(Flag.NORMALBP);
                }
                break;

            case R.id.ancBloodPresserValueDiastolic:
            case R.id.pncBloodPresserValueD:
            case R.id.gpBloodPresserValueDiastolic:
            case R.id.pcBloodPresserValueDiastolic:
            case R.id.injectableBPDiastolicEditText:
            case R.id.pacBloodPresserValueDiastolic:
                if(!mEditText.getText().toString().equals(""))
                {
                    int diastolVal = Integer.valueOf(mEditText.getText().toString());
                    if(diastolVal>=90){
                        mEditText.setBackgroundResource(R.drawable.edittext_round_red);
                        mEditText.setTag(Flag.HIGHBP);
                    }else {
                        mEditText.setBackgroundResource(R.drawable.edittext_round);
                        mEditText.setTag(Flag.NORMALBP);
                    }

                }else {
                    mEditText.setBackgroundResource(R.drawable.edittext_round);
                    mEditText.setTag(Flag.NORMALBP);
                }
                break;
            /**
             * bp segment ends.................................................................................................
             */
            case R.id.id_delivery_date:
                if(!mEditText.getText().toString().equals(""))
                {
                    try {
                        Date deliveryDate = Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,mEditText.getText().toString());
                        Date maxDeliveryDate = Utilities.addDateOffset(((DeliveryActivity)mActivity).mother.getLmp(),294);
                        Date minDeliveryDate = Utilities.addDateOffset(((DeliveryActivity)mActivity).mother.getLmp(),168);
                        //Restricting delivery date between 24 and 42 weeks of pregnancy
                        if(deliveryDate.after(maxDeliveryDate)){
                            Utilities.showAlertToast(mActivity, "প্রসবের তারিখ শেষ মাসিকের তারিখ হতে ৪২ সপ্তাহের বেশি হতে পারে না");
                            mEditText.setText("");
                        }else if(deliveryDate.before(minDeliveryDate)){
                            Utilities.showAlertToast(mActivity, "প্রসবের তারিখ শেষ মাসিকের তারিখ হতে ২৪ সপ্তাহের কম হতে পারে না");
                            //reference: https://pubmed.ncbi.nlm.nih.gov/11753511/#:~:text=In%20the%20United%20States%20viability,of%20Perinatal%20Medicine%2C%201998).
                            mEditText.setText("");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case R.id.NrcClients_Mobile_no:
            case R.id.editTextNRCClientMobileAlternate:
                if (editable.length() == 11) {
                    ((NRCActivity) mActivity).existingDataChecker(viewId);
                }
                break;
        }
    }
}
