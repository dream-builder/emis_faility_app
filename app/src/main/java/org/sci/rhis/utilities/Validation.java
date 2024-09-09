package org.sci.rhis.utilities;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.sci.rhis.fwc.MultiSelectionSpinner;
import org.sci.rhis.fwc.R;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by arafat.hasan on 2/28/2016.
 */
public class Validation {

    // Error Messages
    public static final String REQUIRED_MSG = "বাধ্যতামূলক";

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    // return true if the input field is a valid 11 digit mobile no
    public static boolean isValidMobileNo(EditText editText) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (text.length()!=0 ) {
            if(text.length()<11){
                String restDigit = String.valueOf(11-text.length());
                editText.setError(Utilities.ConvertNumberToBangla(restDigit)+" ডিজিট কম");
                editText.requestFocus();
                return false;
            }
            if(!text.startsWith("0")){
                editText.setError("মোবাইল নম্বর শূন্য দিয়ে শুরু হতে হবে");
                editText.requestFocus();
                return false;
            }

        }

        return true;
    }

    // return true if the input field is a valid 13/17 digit NID no
    public static boolean isValidNID(EditText editText, Context eContext) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        if (text.length()!=0 ) {
            if(text.length()==10 || text.length()==13 || text.length()==17){
                return true;
            }else{
                editText.setText("");
                AlertDialogCreator.SimpleMessageWithNoTitle(eContext,"জাতীয় পরিচয়পত্র নম্বর ১০/১৩/১৭ সংখ্যা বিশিষ্ট হতে হবে।");
                return false;
            }

        }else{
            return true;
        }
    }

    // return true if the input field is a valid 13/17 digit NID no
    public static boolean isValidBRID(EditText editText, Context eContext) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        if (text.length()!=0 ) {
            if(Arrays.asList(new Integer[]{14,15,16,17,19}).contains(text.length())){
                return true;
            }else{
                editText.setText("");
                AlertDialogCreator.SimpleMessageWithNoTitle(eContext,"জন্ম নিবন্ধন নম্বর ১৪/১৫/১৬/১৭/১৯ সংখ্যা বিশিষ্ট হতে হবে।");
                return false;
            }

        }else{
            return true;
        }
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0 || text.equals("Dr.")) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }

    public static boolean hasSelected(Spinner eSpinner) {

        if(eSpinner.getCount() > 1){
            if(eSpinner.getSelectedItemPosition()==0){
                TextView errorText = (TextView)eSpinner.getSelectedView();
                errorText.setError("anything here, just to add the icon");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText(REQUIRED_MSG);
                return false;
            }
        }   else{
            return true;
        }

        return true;
    }

    public static boolean isAnyChildChecked(RadioGroup radioGroup){
        int id = radioGroup.getCheckedRadioButtonId();
        int childCount=radioGroup.getChildCount();
        if(id==-1) {
            //setting error sign beside the last radio button
            ((RadioButton)radioGroup.getChildAt(childCount-1)).setError("বাধ্যতামূলক");
            return false;
        }
        return true;
    }

    public static boolean isFirstChildChecked(RadioGroup radioGroup){
        RadioButton firstChild = (RadioButton) radioGroup.getChildAt(0);
        if(!firstChild.isChecked()) {
            firstChild.setError("কাউন্সেলিং বাধ্যতামূলক");
            return false;
        }
        return true;
    }

    /**
     * @param age
     * @return true if the person can be treated as a part of eligible couple
     */
    public static boolean isElco(int age){
        if(age>=10 && age<=49 ){
            return true;
        }
        return false;
    }

    public static boolean isValidVisitDate(Context eContext, String currDate, String prevDate, boolean checkSameDateAlso, boolean msgRequired) {

        if(!currDate.equals("") && !prevDate.equals("")){
            try {
                Date gDate = Converter.stringToDate(Constants.SHORT_SLASH_FORMAT_BRITISH,currDate);
                Date lastDate = Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,prevDate);
                if (gDate.before(lastDate)) {
                    if(msgRequired){
                        Utilities.showAlertToast(eContext,"পূর্ববর্তী পরিদর্শনের আগের কোন তারিখ নতুন পরিদর্শনের তারিখ হিসেবে দেয়া যাবে না");
                    }
                    return false;
                }
                if (checkSameDateAlso && gDate.equals(lastDate)) {
                    if(msgRequired){
                        Utilities.showAlertToast(eContext,"পূর্ববর্তী পরিদর্শনের তারিখ বা তার আগের কোন তারিখ নতুন পরিদর্শনের তারিখ হিসেবে দেয়া যাবে না");
                    }
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return true;
            }

        }
        return true;
    }

    /**
     *
     * @param multiSpinner
     * @return true if at least minimum item(s) selected
     */
    public static boolean hasMinimumItemSelected(MultiSelectionSpinner multiSpinner){
        String value = "[\"" + TextUtils.join("\",\"",
                multiSpinner.getSelectedIndicesInText(0))
                + "\"]";
        if (value.equals("[\"\"]")){
            return false;
        }
        return true;
    }
}
