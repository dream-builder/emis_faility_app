package org.sci.rhis.fwc;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by arafat.hasan on 2/25/2016.
 */
public class MIS3Activity extends FWCServiceActivity implements RadioGroup.OnCheckedChangeListener {
    private Context mContext;

    StringBuilder reportURL;
    int misDateType = 1; //1 for visit wise, 2 for standard duration wise

    String startDate, endDate, providerId, zillaId, upazilaId, unionId, password,strfacility_id;
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    boolean isMonthWise = false;

    private ProviderInfo provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_report_rhs);

        init();

    }

    private void init() {
        mContext = this;
        provider = getIntent().getParcelableExtra("Provider");

        providerId = provider.getProviderCode();
        zillaId = provider.getZillaID();
        upazilaId = provider.getUpazillaID();
        unionId = provider.getUnionID();
        strfacility_id = provider.getmFacilityId();

        JSONObject providerJSON = CommonQueryExecution.getSingleResultFromQuery("providerdb",
                "provcode is not null");//this is just to keep a where condition
        try {
            password = providerJSON.getString("provpass");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        getRadioGroup(R.id.misSearchCriteriaRadioGroup).setOnCheckedChangeListener(this);
        getRadioGroup(R.id.misSearchCriteria2RadioGroup).setOnCheckedChangeListener(this);




        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.misMonthPickerButton, getEditText(R.id.misMonthEditText));
        datePickerPair.put(R.id.misStartDatePickerButton, getEditText(R.id.misStartDateEditText));
        datePickerPair.put(R.id.misEndDatePickerButton, getEditText(R.id.misEndDateEditText));


    }

    @Override
    public void callbackAsyncTask(String result) {

    }

    private void doCommonTask(){
        try {

            if (isMonthWise) {
                String temp = getEditText(R.id.misMonthEditText).getEditableText().toString();
                int month = Integer.valueOf(temp.substring(0, temp.indexOf('/'))) - 1;
                int year = Integer.valueOf(temp.substring(temp.indexOf('/') + 1));
                startDate = Converter.getFirstDateAsString(year, month, Constants.SHORT_SLASH_FORMAT_BRITISH);
                endDate = Converter.getEndDateAsString(year, month, Constants.SHORT_SLASH_FORMAT_BRITISH);

            } else {
                startDate = getEditText(R.id.misStartDateEditText).getEditableText().toString();
                endDate = getEditText(R.id.misEndDateEditText).getEditableText().toString();
            }
            reportURL = new StringBuilder();
            reportURL.append("providerId=" + providerId + "&password=" + password + "&report1_zilla=" + zillaId +
                    "&report1_upazila=" + upazilaId + "&report1_union=" + unionId + "&report1_start_date=");
            reportURL.append(Converter.convertSdfFormat(Constants.SHORT_SLASH_FORMAT_BRITISH, startDate, Constants.SHORT_HYPHEN_FORMAT_DATABASE) + "&report1_end_date=");
            reportURL.append(Converter.convertSdfFormat(Constants.SHORT_SLASH_FORMAT_BRITISH, endDate, Constants.SHORT_HYPHEN_FORMAT_DATABASE));
            //reportURL.append("&mis3ViewType=" + misDateType);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public void sendMisRequest(View v) {
        if (checkValidation()) {
            doCommonTask();
            reportURL.append("&mis3ViewType=" + misDateType);
            String url = "http://mamoni.net:8080/rhis_fwc_monitoring/mis3report?" + Base64.encodeToString(reportURL.toString().getBytes(), Base64.NO_WRAP);
            //TODO:uncomment the above line before preparing release
            //String url = "http://119.148.6.164:8080/monitoring_test/mis3report?" + Base64.encodeToString(reportURL.toString().getBytes(), Base64.NO_WRAP);
            WebView webView = (WebView) findViewById(R.id.webViewMIS);
            MethodUtils.startWebView(url, webView, this);


        }
    }

    public void submitMIS3(View v) {
        if (checkValidationForSubmit()) {
            doCommonTask();
            reportURL.append("&facilityId="+strfacility_id);
            reportURL.append("&mis3Submit="+1);
            String url = "http://mamoni.net:8080/rhis_fwc_monitoring/mis3report?" + Base64.encodeToString(reportURL.toString().getBytes(), Base64.NO_WRAP);
            //TODO:uncomment the above line before preparing release
            //String url = "http://119.148.6.164:8080/monitoring_test/mis3report?" + Base64.encodeToString(reportURL.toString().getBytes(), Base64.NO_WRAP);
            WebView webView = (WebView) findViewById(R.id.webViewMIS);
            MethodUtils.startWebView(url, webView, this);


        }
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (getRadioGroup(R.id.misSearchCriteriaRadioGroup).getCheckedRadioButtonId() == -1) {
            ret = false;
        } else {
            if (isMonthWise) {
                if (!Validation.hasText(getEditText(R.id.misMonthEditText))) ret = false;
            } else {
                if (!Validation.hasText(getEditText(R.id.misStartDateEditText))) ret = false;
                if (!Validation.hasText(getEditText(R.id.misEndDateEditText))) ret = false;
            }
        }

        return ret;
    }

    private boolean checkValidationForSubmit() {
        boolean ret = true;

        if (!Validation.hasText(getEditText(R.id.misMonthEditText))){
            ret = false;
            Utilities.showAlertToast(this,"রিপোর্ট সমন্বয় ও সাবমিট করতে মাস সিলেক্ট করতে হবে।");
        }else{
            String temp = getEditText(R.id.misMonthEditText).getEditableText().toString();
            int month = Integer.valueOf(temp.substring(0, temp.indexOf('/'))) - 1;
            int year = Integer.valueOf(temp.substring(temp.indexOf('/') + 1));
            if(year == (Calendar.getInstance().get(Calendar.YEAR))
                    && month == (Calendar.getInstance().get(Calendar.MONTH))){
                Utilities.showAlertToast(this,"এই মাসের রিপোর্ট এখনো তৈরী হয়নি। পূর্ববর্তী মাস সিলেক্ট করুন।");
                ret = false;
            }

        }


        return ret;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.misDateRadioButton:
                isMonthWise = false;
                Utilities.MakeInvisible(this, R.id.misMonthLayout);
                Utilities.MakeVisible(this, R.id.misDateLayout);
                getEditText(R.id.misStartDateEditText).setText("");
                getEditText(R.id.misEndDateEditText).setText("");
                break;
            case R.id.misMonthRadioButton:
                isMonthWise = true;
                Utilities.MakeVisible(this, R.id.misMonthLayout);
                Utilities.MakeInvisible(this, R.id.misDateLayout);
                getEditText(R.id.misMonthEditText).setText("");
                break;
            case R.id.misVisitRadioButton:
                misDateType = 1;
                break;
            case R.id.misStandardRadioButton:
                misDateType = 2;
                break;

            default:
                break;
        }

    }

    public void pickDate(View view) {
        if (view.getId() == R.id.misMonthPickerButton) {
            datePickerDialog = null;
            datePickerDialog = new CustomDatePickerDialog(mContext, true);
        } else {
            datePickerDialog = null;
            datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH);
        }
        datePickerDialog.show(datePickerPair.get(view.getId()));

    }
}
