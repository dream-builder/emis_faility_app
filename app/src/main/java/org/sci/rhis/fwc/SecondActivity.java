
package org.sci.rhis.fwc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.AsyncClientInfoUpdate;
import org.sci.rhis.connectivityhandler.AsyncPregnancyInfoUpdate;
import org.sci.rhis.connectivityhandler.SendPostRequestAsyncTask;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.model.FPExaminations;
import org.sci.rhis.model.FPScreeningResponse;
import org.sci.rhis.model.GeneralPerson;
import org.sci.rhis.model.PregWoman;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.ArrayIndexValues;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.FPScreeningEngine;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.NetworkUtility;
import org.sci.rhis.utilities.ScreeningHelper;
import org.sci.rhis.utilities.SharedPref;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.math.BigInteger;
import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class SecondActivity extends ClinicalServiceActivity implements ArrayIndexValues,
        MinimumDeliveryInfoFragment.PatientAndProviderDetailsRequiredListener,
        MinimumDeliveryInfoFragment.DeliverySavedListener,
        AlertDialogCreator.DialogButtonClickedListener, AlertDialogCreator.SingleInputListener {

    public PregWoman woman;
    public GeneralPerson generalPerson;
    ProviderInfo provider;
    boolean pregInfoEditMode = false, updateMode = false;
    StringBuilder validationMsg = null;
    private Vector<Pair<String, Integer>> deliveryHistoryMapping;
    public String boyChild, girlChild;
    //TODO:
    public String clientAge;

    public int dialogFlag = 0;
    public static int prevMaxTT = 0;

    private int countSaveClick = 0, intAgeInDays = 0;
    private SendPostRequestAsyncTask clientInfoUpdateTask;

    final private String SERVLET = "handlepregwomen";
    final private String ROOTKEY = "pregWomen";
    private final String LOGTAG = "FWC-INFO";
    private HashMap<String, String> lmp_edd = null;
    private static HashMap<String, Integer> TTcheckboxMap = new HashMap<>();

    public BigInteger responseID = BigInteger.valueOf(0);
    private EditText lmpEditText;
    public JSONObject client;
    private CustomDatePickerDialog datePicker = null;
    private static SecondActivity act;
    private boolean pregInfoExists = false, isDataLoading = false;
    private Dialog shortDelivery = null;
    private FPScreeningResponse fpScreening;
    private FPExaminations fpExaminations;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        provider = ProviderInfo.getProvider();

        getTextView(R.id.textViewLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDataLoading) {
                    AlertDialogCreator.ExitActivityDialog(SecondActivity.this, (provider.getmCsba().equals("1") && provider.getCommunityActive().equals("1")));
                } else {
                    Utilities.showAlertToast(SecondActivity.this, getString(R.string.str_data_loading_wait));
                }

            }
        });
        getImageView(R.id.imageViewHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MethodUtils.showLongTimedColoredToast(SecondActivity.this,
                        "হোম পেজ লোড হচ্ছে, অনুগ্রহ করে অপেক্ষা করুন",R.color.seagreen);
                finish();
                startActivity(getIntent());
            }
        });
        String satelliteName = getIntent().getStringExtra("satellite");
        if (!satelliteName.equals("")) {
            getRelativeLayout(R.id.button_layoutAssesment).setVisibility(View.GONE);
        }
        getTextView(R.id.fwc_heading).setText(satelliteName.equals("") ? provider.getProviderFacility() : getText(R.string.str_satelite) + satelliteName);
        if (provider.getmCsba().equals("1")) {
            getTextView(R.id.fwc_heading).setText(provider.getProviderName());
            getRelativeLayout(R.id.button_layoutAssesment).setVisibility(View.GONE);
            getRelativeLayout(R.id.button_layoutMIS3).setVisibility(View.GONE);
            getRelativeLayout(R.id.button_layout_meeting_minutes).setVisibility(View.GONE);
        }

        Log.i("SecondActivity", "" + provider.getProviderFacility());

        initialize();//super class
        Spinner staticSpinner = (Spinner) findViewById(R.id.ClientsIdentityDropdown);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this,
                provider.getmProviderType().equals("6") ? R.array.Search_Criteria_SACMO : R.array.search_parameter, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int position, long row_id) {
                final Intent intent = new Intent(SecondActivity.this, ADVSearchActivity.class);
                if (provider.getmProviderType().equals("6")) {
                    if (position == 6) position = position + 1;
                }
                switch (position) {
                    case ADV_SEARCH_INDEX:
                        getLinearLayout(R.id.fragment_dashboard_scroll).setVisibility(View.GONE);
                        intent.putExtra(Constants.KEY_OPTION, "2");
                        startActivityForResult(intent, ActivityResultCodes.ADV_SEARCH_ACTIVITY);
                        break;
                    case MOBILE_NO_INDEX:
                        intent.putExtra(Constants.KEY_OPTION, "1");
                        startActivityForResult(intent, ActivityResultCodes.ADV_SEARCH_ACTIVITY);
                        break;
                    case SERVICE_INDEX:
                        intent.putExtra(Constants.KEY_OPTION, "3");
                        startActivityForResult(intent, ActivityResultCodes.ADV_SEARCH_ACTIVITY);
                        break;
                    case PREGWOMAN_INDEX:
                        intent.putExtra(Constants.KEY_OPTION, "4");
                        startActivityForResult(intent, ActivityResultCodes.ADV_SEARCH_ACTIVITY);
                        break;
                    case LAST_SEARCH_INDEX:
                        String lastResult = SharedPref.getLastSearchResult(SecondActivity.this);
                        if(lastResult==null || lastResult.equals("")){
                            MethodUtils.showSnackBar(findViewById(R.id.layoutHome),"সর্বশেষ বিশেষ সার্চের কোন ফলাফল পাওয়া যায় নি!",true);
                        }else{
                            intent.putExtra(Constants.KEY_OPTION, "5");
                            startActivityForResult(intent, ActivityResultCodes.ADV_SEARCH_ACTIVITY);
                        }
                        break;


                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        lmpEditText = (EditText) findViewById(R.id.lmpDate);
        lmpEditText.addTextChangedListener(new CustomTextWatcher(SecondActivity.this, lmpEditText));

        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
        //starting barcode scanner
        searchButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(SecondActivity.this);
                integrator.initiateScan();
                return true;
            }
        });
        //..........................
       /* getRelativeLayout(R.id.layoutNRC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getRelativeLayout(R.id.button_layoutNRC).setBackgroundColor(Color.GRAY);
                Intent intent = new Intent(SecondActivity.this, NRCActivity.class);
                if (checkClientInfo(false)) {
                    intent.putExtra("PregWoman", woman);
                }
                intent.putExtra("Provider", ProviderInfo.getProvider());
                startActivityForResult(intent, ActivityResultCodes.REGISTRATION_ACTIVITY);
            }
        });*/

        getRelativeLayout(R.id.button_layoutAssesment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkUtility networkUtility = new NetworkUtility(SecondActivity.this);
                if (networkUtility != null && !networkUtility.isOnline()) {
                    AlertDialogCreator.SimpleMessageDialog(SecondActivity.this,
                            getString(R.string.str_service_offline_not_available), getString(R.string.str_internet_need),
                            android.R.drawable.ic_dialog_info);
                } else {
                    Intent intent = new Intent(SecondActivity.this, AssesmentActivity.class);
                    intent.putExtra("Provider", ProviderInfo.getProvider());
                    startActivity(intent);
                }
            }
        });

        getRelativeLayout(R.id.layoutETicketing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkUtility networkUtility = new NetworkUtility(SecondActivity.this);
                if (networkUtility != null && !networkUtility.isOnline()) {
                    AlertDialogCreator.SimpleMessageDialog(SecondActivity.this,
                            getString(R.string.str_service_offline_not_available), getString(R.string.str_internet_need),
                            android.R.drawable.ic_dialog_info);
                } else {
                    Intent intent = new Intent(SecondActivity.this, WebViewActivity.class);
                    intent.putExtra("Provider", ProviderInfo.getProvider());
                    intent.putExtra("Content",Flag.E_TICKETING);
                    startActivity(intent);
                }
            }
        });

        getRelativeLayout(R.id.button_layoutMIS3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkUtility networkUtility = new NetworkUtility(SecondActivity.this);
                if (networkUtility != null && !networkUtility.isOnline()) {
                    AlertDialogCreator.SimpleMessageDialog(SecondActivity.this,
                            getString(R.string.str_service_offline_not_available), getString(R.string.str_internet_need),
                            android.R.drawable.ic_dialog_info);
                } else {
                    Intent intent = new Intent(SecondActivity.this, MIS3Activity.class);
                    intent.putExtra("Provider", ProviderInfo.getProvider());
                    startActivity(intent);
                }
            }
        });
        new DataLoader(this).execute();
        getRelativeLayout(R.id.button_layoutSatellite).setOnClickListener(view -> {
            Intent intent = new Intent(SecondActivity.this, SatelliteSessionPlanningActivity.class);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);
        });
        act = this;

        //show hide mechanism of action tools for report
        RelativeLayout layoutReportActionTool = getRelativeLayout(R.id.layout_report_action_tool);
        layoutReportActionTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getLinearLayout(R.id.report_layout_container).getVisibility() == View.VISIBLE) {
                    Utilities.MakeInvisible(SecondActivity.this, R.id.report_layout_container);
                    layoutReportActionTool.setBackgroundColor(getResources().getColor(R.color.mediumseagreen));
                    getImageView(R.id.img_action_icon).setImageDrawable(getDrawable(R.drawable.down_arrow));
                } else {
                    Utilities.MakeVisible(SecondActivity.this, R.id.report_layout_container);
                    layoutReportActionTool.setBackgroundColor(getResources().getColor(R.color.disabled_color));
                    getImageView(R.id.img_action_icon).setImageDrawable(getDrawable(R.drawable.up_arrow));
                }
            }
        });

        //navigating to webView activity on button_layout_meeting_minutes click
        getRelativeLayout(R.id.button_layout_meeting_minutes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkUtility networkUtility = new NetworkUtility(SecondActivity.this);
                if (networkUtility != null && !networkUtility.isOnline()) {
                    AlertDialogCreator.SimpleMessageDialog(SecondActivity.this,
                            getString(R.string.str_service_offline_not_available), getString(R.string.str_internet_need),
                            android.R.drawable.ic_dialog_info);
                } else {
                    Intent intent = new Intent(SecondActivity.this, WebViewActivity.class);
                    intent.putExtra("Provider", ProviderInfo.getProvider());
                    intent.putExtra("Content",Flag.MEETING_MINUTES);
                    startActivity(intent);
                }

            }
        });

        //navigating to distribution details activity on button_layout_distribution click
        getRelativeLayout(R.id.button_layout_distribution).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MethodUtils.showSnackBarFromTop(findViewById(R.id.layoutHome),"এই ফিচারটি পরবর্তী ভার্সনে পাওয়া যাবে",false);
                //startActivity(new Intent(SecondActivity.this, DistributionDetailsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SharedPref.hasLoggedInToday(this)) {
            finish();//logout directly
            Utilities.showAlertToast(this, getString(R.string.str_auto_logout));
        } else if (woman != null) {
            statusofEditButtonForPregWomenDetails();
        }
    }

    public void startSearch(View view) {

        Spinner searchOptions = (Spinner) findViewById(R.id.ClientsIdentityDropdown);
        EditText searchableId = (EditText) findViewById(R.id.searchableTextId);

        long index = (searchOptions.getSelectedItemId() + 1);
        String stringId = (String) searchOptions.getSelectedItem();
        long id;
        try {
            id = Long.valueOf(searchableId.getText().toString());
        } catch (NumberFormatException nfe) {
            Utilities.showAlertToast(this, getString(R.string.str_wrong_id_search));
            getView_NoClient();
            return;
        }

        String queryString = "{" +
                "sOpt:" + String.valueOf(index) + "," +
                "sStr:\"" + String.valueOf(id) + "\"," +
                "providerid:" + ProviderInfo.getProvider().getProviderCode() +
                "}";
        String servlet = "client";
        String jsonRootkey = "sClient";
        AsyncClientInfoUpdate retrieveClient = new AsyncClientInfoUpdate(this, this);
        Utilities.Reset(this, R.id.client_intro_layout);
        Utilities.Reset(this, R.id.clients_info_layout);
        Utilities.Reset(this, R.id.fp_client_info_layout);
        getLinearLayout(R.id.fragment_dashboard_scroll).setVisibility(View.GONE);
        retrieveClient.execute(queryString, servlet, jsonRootkey);

        Utilities.MakeVisible(this, R.id.health_id);
        getTextView(R.id.health_id).setText(String.valueOf(stringId) + ": " + String.valueOf(id));

    }

    private void populateClientDetails(JSONObject json, HashMap<String, Integer> fieldMapping) {
        Iterator<String> i = fieldMapping.keySet().iterator();
        String key;
        try {
            if (json.has("cMobileNo")) {
                String mobileNumber = json.getString("cMobileNo");
                if (!mobileNumber.equals("")) {
                    if (mobileNumber.charAt(0) != '0') {
                        mobileNumber = "0" + mobileNumber;
                    }
                    json.put("cMobileNo", mobileNumber);
                }
            }
            clientAge=MethodUtils.calculatePreciseAge(json.getString("cDob"));
            json.put("cAgeReal", clientAge);
            getEditText(R.id.Clients_Father).setText(json.getString("cFatherName"));
            getEditText(R.id.Clients_Mother).setText(json.getString("cMotherName"));
            getEditText(R.id.Clients_Sex).setText(json.getString("cSex").equals("1") ? "পুরুষ" : json.getString("cSex").equals("2") ? "মহিলা" : "হিজড়া");

            if (json.getString("cSex").equals("1")) {
                getTextView(R.id.Clients_Husband).setBackgroundColor(Color.GRAY);
            } else {
                getTextView(R.id.Clients_Husband).setBackgroundResource(R.drawable.edittext_round);
            }

            intAgeInDays = (int) ((Utilities.getDateDiff(Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE, json.getString("cDob")), new Date(),
                    TimeUnit.DAYS)));

            if (intAgeInDays <= 1825) {
                getTextView(R.id.Clients_Husband).setBackgroundColor(Color.GRAY);
                getTextView(R.id.Clients_ElcoNo).setBackgroundColor(Color.GRAY);

            } else {
                getTextView(R.id.Clients_ElcoNo).setBackgroundResource(R.drawable.edittext_round);
                getLinearLayout(R.id.clients_sex_layout).setVisibility(View.VISIBLE);
                getLinearLayout(R.id.husband_dist).setVisibility(View.VISIBLE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        while (i.hasNext()) {
            key = i.next();
            if (fieldMapping.get(key) != null) { //If the field exist in the mapping table
                try {
                    ((EditText) findViewById(fieldMapping.get(key))).setText(json.getString(key));
                } catch (JSONException jse) {
                    Log.e(LOGTAG, "JSON Exception Thrown(populateClientDetails): " + jse.getMessage() + "\n ");
                    Utilities.printTrace(jse.getStackTrace(), 10);
                }
            }
        }

        Utilities.setSpinners(jsonSpinnerMap, json);
        Utilities.setCheckboxes(jsonCheckboxMap, json);
        Utilities.setEditTextDates(jsonEditTextDateMap, json);
        setSpecialCase(json);

    }

    private void setSpecialCase(JSONObject json) {
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
                getTextView(R.id.reg_NO).setText(Utilities.ConvertNumberToBangla(regSerial));
            }

        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Exception Thrown(setSpecialCase): " + jse.getMessage() + "\n ");
            Utilities.printTrace(jse.getStackTrace(), 10);
        } catch (ParseException pe) {

        }
    }

    @Override
    public void callbackAsyncTask(String result) { //Get results back from healthId search
        Log.d("result:", result);

        try {
            JSONObject json = new JSONObject(result);

            if (json.has("cSex") && json.getString("cSex").isEmpty()) {
                Utilities.showAlertToast(this, getString(R.string.incomplete_client_error_message));
                finish();
                startActivity(getIntent());
            }

            ///////////////////////callback for client servlet////////////////////////////////////////////////////

            if (!json.has("responseType")) {
                if (client != null) {
                    client = null;//clear previous data if any
                }
                specialResetAfterClientSearch();
                client = json;
                if (json.getString("False").equals("")) { //Client exists
                    populateClientDetails(json, DatabaseFieldMapping.CLIENT_INTRO);
                    getView_NoClient();
                    Utilities.MakeVisible(this, R.id.fragment_client_intro_scroll);
                    Utilities.SetVisibility(this, R.id.Clients_Age, View.GONE);
                    Utilities.Disable(this, R.id.client_intro_layout);
                    if (getSpinner(R.id.ClientsIdentityDropdown).getSelectedItemPosition() == NRC_ID_INDEX ||
                            (json.has("is_registered") && json.getString("is_registered").equalsIgnoreCase("No"))) {
                        getButton(R.id.buttonEditNRC).setVisibility(View.VISIBLE);
                        getButton(R.id.buttonEditNRC).setClickable(true);
                        getButton(R.id.buttonEditNRC).setEnabled(true);
                    }
                    else {
                        getButton(R.id.buttonEditNRC).setVisibility(View.GONE);
                    }
                    Utilities.MakeVisible(this, R.id.serviceHeaderLayout);
                    //hide non MNC services for CSBA
                    if (provider.getmCsba().equals("1")) {
                        Utilities.MakeInvisible(this, R.id.FPButton);
                        Utilities.MakeInvisible(this, R.id.GPButton);
                    }
                    //hide non MNC services for SACMO-HS
                    if (provider.getmProviderType().equals("6")) {
                        Utilities.MakeInvisible(this, R.id.MNCHButton);
                        Utilities.MakeInvisible(this, R.id.FPButton);
                        Utilities.MakeInvisible(this, R.id.DeathButton);
                    }

                    int age = (int) ((Utilities.getDateDiff(Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE, json.getString("cDob")), new Date(),
                            TimeUnit.DAYS)) / 365);

                    responseID = new BigInteger(json.get("cHealthID").toString());
                    if (json.getString("cSex").equals("2") && Validation.isElco(age)) { //only woman cSex = 2
                        //Elco Women
                        Log.d(LOGTAG, "CREATING PREGNANCY REMOTE" + client.toString());
                        woman = PregWoman.CreatePregWoman(json); //Creating pregWomen Object
                        responseID = new BigInteger(json.get("cHealthID").toString());
                        if (woman != null) {//Elco Women with pregInfo
                            manipulateJson(json);
                            populateClientDetails(json, DatabaseFieldMapping.CLIENT_INFO);
                            setTT_UI();
                            pregInfoExists = true;

                        } else { //Elco Women without pregInfo
                            woman = PregWoman.CreateGeneralWoman(json);
                            pregInfoExists = false;
                            setTT_UI();
                        }
                    } else {//Men & Not-Elco Women
                        generalPerson = new GeneralPerson(json);
                        woman = null; //not elco or male
                    }

                    if (json.has("deathStatus") && json.getString("deathStatus").equals("1")) { //dead
                        if (woman != null) {
                            generalPerson = woman;
                        }
                        generalPerson.reportDead();
                        Utilities.showBiggerToast(this, R.string.reportedDeath);
                    }
                    //if age < 5 than child service will be active

                   int childAgeInDays = (int) ((Utilities.getDateDiff(Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE, json.getString("cDob")), new Date(),
                            TimeUnit.DAYS)));
                    if (childAgeInDays <= 1825) {
                        addChildServiceButtons(childAgeInDays);
                    } else {
                        Utilities.MakeInvisible(this, R.id.zerototwomonthButton);
                    }

                } else {//Client doesn't exist
                    Utilities.showAlertToast(this, getString(R.string.str_none_found_with_this_id));
                    getView_NoClient();
                }
            }

            //////////////////callback for PregInfo servlet//////////////////////////////////////////////////////

            else {
                if (!json.getString("pregNo").equals("")) {
                    client.put("regSerialNo", json.get("regSerialNo"));
                    client.put("regDate", json.get("regDate"));
                    client.put("highRiskPreg", json.get("highRiskPreg"));
                    client.put("cPregNo", json.get("pregNo"));
                    client.put("cNewMCHClient", "false");
                    if (json.has("hasDeliveryInformation")) client.put("hasDeliveryInformation", json.get("hasDeliveryInformation"));
                    if (json.has("hasAbortionInformation")) client.put("hasAbortionInformation", json.get("hasAbortionInformation"));

                    Log.d(LOGTAG, "CREATING PREGNANCY LOCAL" + client.toString());
                    if (woman != null) {
                        woman = null;
                    }
                    woman = PregWoman.CreatePregWoman(client);
                    responseID = new BigInteger(client.get("cHealthID").toString());
                    for (int i = 1; i <= 5; i++) {
                        client.put("cTT" + i, json.get("cTT" + i));
                        client.put("cTT" + i + "Date", json.get("cTT" + i + "Date"));
                    }
                    setTT_UI();
                    pregInfoExists = true;
                    getView_WomenWithPregInfo();


                } else {
                    Toast.makeText(this, getText(R.string.str_provider_not_valid), Toast.LENGTH_LONG).show();
                    getView_WomenWithOutPregInfo();
                }
            }
        } catch (JSONException jse) {
            Log.d(LOGTAG, "JSON Exception Thrown (callbackAsyncTask): " + jse.getMessage() + "\n");
            jse.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

    }

    public void addChildServiceButtons(int ageInDays) {
        System.out.println("Age in Days : " + ageInDays);
        getButton(R.id.GPButton).setVisibility(View.GONE);
        getButton(R.id.MNCHButton).setVisibility(View.GONE);
        getButton(R.id.FPButton).setVisibility(View.GONE);

        getButton(R.id.zerototwomonthButton).setVisibility(View.VISIBLE);


    }

    public void editNRC(View v) {
        Intent intent = new Intent(this, NRCActivity.class);
        if (checkClientInfo(false)) {
            intent.putExtra("PregWoman", woman);
        }
        intent.putExtra("Provider", ProviderInfo.getProvider());
        intent.putExtra("editMode", client.toString());
        startActivityForResult(intent, ActivityResultCodes.REGISTRATION_ACTIVITY);
    }

    public void startANC(View view) {
        if (!checkClientInfo(true)) {
            return;
        }
        Intent intent = new Intent(this, ANCActivity.class);
        intent.putExtra("PregWoman", woman);
        intent.putExtra("Provider", ProviderInfo.getProvider());
        startActivity(intent);

    }

    public void startDelivery(View view) {
        if (checkClientInfo(false)) {
            if (woman.getAbortionInfo() == 1) {
                askToStartPAC();
            } else {
                Intent intent = new Intent(this, DeliveryActivity.class);
                intent.putExtra("PregWoman", woman);
                intent.putExtra("Provider", ProviderInfo.getProvider());
                startActivityForResult(intent, ActivityResultCodes.DELIVERY_ACTIVITY);
            }
        } else if (woman != null && woman.getAbortionInfo() == 1) {
            askToStartPAC();
        } else {
            deliveryWithoutPregInfo();
        }
    }

    public void startDeath(View view) {
        Intent intent = new Intent(this, DeathActivity.class);
        boolean isWoman = woman != null ? Integer.valueOf(woman.getSex()) == 2 ? true : false : false;

        intent.putExtra("Patient", woman == null ? generalPerson : woman);
        intent.putExtra("Provider", ProviderInfo.getProvider());
        intent.putExtra("isWoman", isWoman);
        startActivityForResult(intent, ActivityResultCodes.DEATH_ACTIVITY);

    }

    public void startGeneralPatient(View view) {
        //Utilities.showBiggerToast(this, R.string.NotReadyYet);
        GeneralPerson patient = (woman == null ? generalPerson : woman);
        if (patient.isDead()) {
            Toast.makeText(this, R.string.reportedDeath, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, GeneralPatientActivity.class);

        intent.putExtra("Patient", patient);
        intent.putExtra("Provider", ProviderInfo.getProvider());
        startActivity(intent);
    }

    //Child Register Code
    public void start0to2MonthService(View view) {
        GeneralPerson patient0to2Month = (woman == null ? generalPerson : woman);
        if (patient0to2Month.isDead()) {
            Toast.makeText(SecondActivity.this, R.string.reportedDeath, Toast.LENGTH_LONG).show();
            return;
        }
        patient0to2Month.setAge(intAgeInDays);
        //Intent intent0to2Month = new Intent(SecondActivity.this, PSBIServiceActivity0To2Months.class);
        Intent intent0to2Month = new Intent(SecondActivity.this, ChildCareActivity.class);
        intent0to2Month.putExtra("Patient", patient0to2Month);
        intent0to2Month.putExtra("Provider", ProviderInfo.getProvider());
        intent0to2Month.putExtra("ageInDays", intAgeInDays);
        intent0to2Month.putExtra("age", clientAge);
        System.out.println("Age Sent : " + intAgeInDays);
        startActivity(intent0to2Month);
    }

    private void openFPMethod(Intent intent) {
        boolean healthIdFound = false;
        if (woman != null) {
            intent.putExtra(Constants.KEY_HEALTH_ID, String.valueOf(woman.getHealthId()));
            healthIdFound = true;
        } else {
            if (generalPerson != null) {
                intent.putExtra(Constants.KEY_HEALTH_ID, String.valueOf(generalPerson.getHealthId()));
                healthIdFound = true;
            }
        }
        if (healthIdFound) {
            intent.putExtra(Constants.KEY_PROVIDER, ProviderInfo.getProvider());
            String mobile = getEditText(R.id.Clients_Mobile_no).getEditableText().toString();
            intent.putExtra(Constants.KEY_MOBILE, mobile);
            if (fpExaminations != null) {
                intent.putExtra(Constants.KEY_FP_EXAMINATION, fpExaminations.toJsonString());
                FPScreeningEngine fpEngine = new FPScreeningEngine(fpScreening, fpExaminations);
                intent.putExtra(Constants.KEY_IS_SUKHI_ELIGIBLE, fpEngine.isSukhiEligible());
                intent.putExtra(Constants.KEY_IS_APON_ELIGIBLE, fpEngine.isAponEligible());

            }
            //startActivity(intent);
            startActivityForResult(intent, ActivityResultCodes.FP_ACTIVITY);
        } else {
            Utilities.showBiggerToast(this, R.string.FPWarning);
        }
    }

    public void startPAC(View view) {
        //Utilities.showBiggerToast(this, R.string.NotReadyYet);
        Intent intent = new Intent(this, PACActivity.class);

        if (checkClientInfo(false) && woman.isEligibleFor(PregWoman.PREG_SERVICE.PAC)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivityForResult(intent, ActivityResultCodes.PAC_ACTIVITY);
        } else {
            Utilities.showBiggerToast(this, R.string.PACWarning);
        }
    }

    public void startPNC(View view) {
        Intent intent = new Intent(this, PNCActivity.class);

        if (woman.getAbortionInfo() == 1) {
            Utilities.showAlertToast(this, getString(R.string.PNCWarning_PAC));
            return;
        }

        if (woman.isEligibleFor(PregWoman.PREG_SERVICE.PNC)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);
        } else {
            Utilities.showBiggerToast(this, R.string.PNCWarning);
        }
    }

    private boolean checkClientInfo(boolean showMessage) {
        boolean result = true;
        int stringId = 0;
        if (woman != null) {
            if (!woman.isDead()) {
                if (woman.getLmp() == null) {
                    stringId = R.string.clientInfoMissing;
                    result = false;
                }
            } else {
                stringId = R.string.reportedDeath;
                result = false;
            }

        } else {
            stringId = R.string.clientInfoMissing;
            result = false;
        }
        if (showMessage && stringId != 0) {
            Utilities.showBiggerToast(this, stringId);
        }
        return result;
    }

    private void askToStartPAC() {
        AlertDialog alertDialog = new AlertDialog.Builder(SecondActivity.this).create();
        alertDialog.setTitle("PAC CONFIRMATION");
        alertDialog.setMessage(getString(R.string.StartPACfromDeliveryMessage));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startPAC(findViewById(R.id.pacButton));
                    }
                });

        alertDialog.show();
    }

    public void deliveryWithoutPregInfo() {
        AlertDialog alertDialog = new AlertDialog.Builder(SecondActivity.this).create();
        alertDialog.setTitle("DELIVERY CONFIRMATION");
        alertDialog.setMessage(getString(R.string.DeliveryWithoutPregnancyPrompt));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handleDeliveryDatePopUp();
                    }
                });

        alertDialog.show();
    }

    private void startDeliveryWithoutPregInfo(String result) {
        Log.d(LOGTAG, "PSUDO PREG INFO RETURNED:\n\t" + result);
        callbackAsyncTask(result); //will create PregWoman
        woman.setActualDelivery(lmp_edd.get("edd"), "dd/MM/yyyyy");
        woman.setDeliveryInfo(1);
        //in this case actual delivery date was the edd

        Intent intent = new Intent(this, DeliveryActivity.class);
        intent.putExtra("PregWoman", woman);
        intent.putExtra("Provider", ProviderInfo.getProvider());
        intent.putExtra("actualDeliveryDateAvailable", true);
        startActivityForResult(intent, ActivityResultCodes.DELIVERY_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        //Do nothing
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            getEditText(R.id.searchableTextId).setText(scanResult.getContents());
            getSpinner(R.id.ClientsIdentityDropdown).setSelection(HEALTH_ID_INDEX);
            startSearch(findViewById(R.id.searchButton));
        }
        //...............................................

        if (requestCode == ActivityResultCodes.REGISTRATION_ACTIVITY && resultCode == RESULT_OK) {
            //setting spinner by intent extra from NRCActivity
            if (!data.hasExtra("existingDataCheckIndex")) {
                doSearchNRCRegistered(data.getStringExtra("generatedId"));
            } else {
                getSpinner(R.id.ClientsIdentityDropdown).setSelection(data.getIntExtra("existingDataCheckIndex", 0));
                getEditText(R.id.searchableTextId).setText(data.getStringExtra("existingClientSearchedValue"));
                startSearch(findViewById(R.id.searchButton));
            }
        } else if (requestCode == ActivityResultCodes.ADV_SEARCH_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                //check if nrc added on search activity
                if (data.hasExtra("nrcAdded") && data.getBooleanExtra("nrcAdded", false)) {
                    doSearchNRCRegistered(data.getStringExtra("generatedId"));
                } else {
                    String str = data.getStringExtra("HealthId");
                    getSpinner(R.id.ClientsIdentityDropdown).setSelection(data.getIntExtra("HealthIdType", 0));
                    getEditText(R.id.searchableTextId).setText(str);
                    startSearch(findViewById(R.id.searchButton));
                }
            } else if (resultCode == RESULT_CANCELED) {
                //getSpinner(R.id.ClientsIdentityDropdown).setSelection(0); //deafults to healthId search;

                //on cancelled/back restart the activity
                finish();
                startActivity(getIntent());
            }
        } else if (requestCode == ActivityResultCodes.DELIVERY_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                boolean hasDeliveryInformation = data.getBooleanExtra("hasDeliveryInformation", false);
                woman.setDeliveryInfo(hasDeliveryInformation ? 1 : 0);
            }
        } else if (requestCode == ActivityResultCodes.PAC_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                boolean hasAbortionInformation = data.getBooleanExtra("hasAbortionInformation", false);
                woman.setDeliveryInfo(hasAbortionInformation ? 1 : 0);
                woman.setAbortionInfo(hasAbortionInformation ? 1 : 0);
            }
        } else if (requestCode == ActivityResultCodes.DEATH_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                startSearch(findViewById(R.id.searchButton));
            }
        } else if (requestCode == ActivityResultCodes.FP_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                showFP(this.findViewById(android.R.id.content));// just add the rootview of this activity as argument
            }

        } else if (requestCode == ActivityResultCodes.DASHBOARDDATA_ACTIVITY) {

            new DataLoader(this).execute();
        } else if (resultCode != Activity.RESULT_OK && data != null) {
            startSearch(findViewById(R.id.searchButton));
        }
    }

    public void onClickSaveClient(View view) {
        updateMode = (view.getId() == R.id.client_update_Button) ? true : false;

        countSaveClick++;
        if (countSaveClick == 2) {
            saveClientToJson();
            getButton(R.id.client_Save_Button).setText("Save");
            Utilities.MakeInvisible(this, R.id.client_Save_Button);
            countSaveClick = 0;
            updateMode = false;

        } else if (countSaveClick == 1) {
            if (!hasTheRequiredFileds()) {
                countSaveClick = 0;
                String errorMsg = "অনুগ্রহ করে নিম্নোক্ত তারকা চিহ্নিত তথ্য/তথ্যাবলী প্রদান করুনঃ " + "\n\n" + validationMsg.toString();
                AlertDialogCreator.SimpleMessageDialog(this, errorMsg, getString(R.string.str_mandatory_field), android.R.drawable.ic_dialog_alert);
                validationMsg = null;
                return;
            }

            Utilities.Disable(this, R.id.clients_info_layout);
            Utilities.DisableField(this, R.id.Clients_House_No);
            Utilities.DisableField(this, R.id.Clients_Mobile_no);
            Utilities.MakeInvisible(this, R.id.client_update_Button);

            getButton(R.id.client_Save_Button).setText("Confirm");
            Utilities.Enable(this, R.id.client_Cancel_Button);
            Utilities.Enable(this, R.id.client_Save_Button);
            Utilities.MakeVisible(this, R.id.client_Save_Button);
            Utilities.MakeVisible(this, R.id.client_Cancel_Button);

            Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);
        }
    }

    public void onClickCancelClient(View view) {
        if (countSaveClick == 1) {
            countSaveClick = 0;
            if (updateMode) {
                editFields(view);
                Utilities.MakeInvisible(this, R.id.client_Save_Button);
            } else {
                Utilities.Enable(this, R.id.clients_info_layout);
                Utilities.EnableField(this, R.id.Clients_House_No, "edit");
                Utilities.EnableField(this, R.id.Clients_Mobile_no, "edit");

                getButton(R.id.client_Save_Button).setText("Save");
                Utilities.MakeInvisible(this, R.id.client_Cancel_Button);

                pregInfoEditMode = false;
            }


        } else {
            //TODO - Review
            Utilities.Disable(this, R.id.clients_info_layout);
            Utilities.DisableField(this, R.id.Clients_House_No);
            Utilities.DisableField(this, R.id.Clients_Mobile_no);

            Utilities.MakeInvisible(this, R.id.client_Save_Button);
            Utilities.MakeInvisible(this, R.id.client_update_Button);
            Utilities.MakeInvisible(this, R.id.client_Cancel_Button);
            Utilities.MakeVisible(this, R.id.client_New_preg_Button);
            Utilities.MakeVisible(this, R.id.client_edit_Button);

            Utilities.Enable(this, R.id.client_edit_Button);
            Utilities.Enable(this, R.id.client_New_preg_Button);

            updateMode = false;

            updateMode = false;

        }
    }

    //common work after nrc register activity result
    private void doSearchNRCRegistered(String healthId) {
        getSpinner(R.id.ClientsIdentityDropdown).setSelection(NRC_ID_INDEX);
        getEditText(R.id.searchableTextId).setText(healthId);
        startSearch(findViewById(R.id.searchButton));
    }

    ////for complication history
    private void initializeJsonManipulation() {
        deliveryHistoryMapping = new Vector<Pair<String, Integer>>(9);
        //The order is important
        deliveryHistoryMapping.addElement(Pair.create("bleeding", R.id.previousDeliveryBleedingCheckBox)); //0
        deliveryHistoryMapping.addElement(Pair.create("delayedDelivery", R.id.delayedBirthCheckBox));//1
        deliveryHistoryMapping.addElement(Pair.create("blockedDelivery", R.id.blockedDeliveryCheckBox));//2
        deliveryHistoryMapping.addElement(Pair.create("blockedPlacenta", R.id.placentaInsideUterusCheckBox));//3
        deliveryHistoryMapping.addElement(Pair.create("deadBirth", R.id.giveBirthDeadCheckBox));//4
        deliveryHistoryMapping.addElement(Pair.create("lived48Hour", R.id.newbornDieWithin48hoursCheckBox));//5
        deliveryHistoryMapping.addElement(Pair.create("edemaSwelling", R.id.swellingLegsOrWholeBodyCheckBox));//6
        deliveryHistoryMapping.addElement(Pair.create("convulsion", R.id.withConvulsionSenselessCheckBox));//7
        deliveryHistoryMapping.addElement(Pair.create("caesar", R.id.caesarCheckBox));//8
    }

    private void manipulateJson(JSONObject json) {
        try {
            String history = json.getString("cHistoryComplicatedContent");
            String[] array = {};
            if (!history.equals("")) {
                array = history.split(",");
            }

            for (int i = 0; i < array.length; i++) {
                json.put(deliveryHistoryMapping.get(Integer.valueOf(array[i]) - 1).first, 1);// 1- checked, 2 - unchecked
            }
        } catch (JSONException jse) {
            jse.getMessage();
            jse.printStackTrace();
        }
    }

    //The following methods are all required for all the activities that updates information
    //from user interface
    @Override
    protected void initiateCheckboxes() {
        //TT
        jsonCheckboxMap.put("cTT1", getCheckbox(R.id.Clients_TT_Tika1));
        jsonCheckboxMap.put("cTT2", getCheckbox(R.id.Clients_TT_Tika2));
        jsonCheckboxMap.put("cTT3", getCheckbox(R.id.Clients_TT_Tika3));
        jsonCheckboxMap.put("cTT4", getCheckbox(R.id.Clients_TT_Tika4));
        jsonCheckboxMap.put("cTT5", getCheckbox(R.id.Clients_TT_Tika5));

        jsonCheckboxMapSave.put("tt1", getCheckbox(R.id.Clients_TT_Tika1));
        jsonCheckboxMapSave.put("tt2", getCheckbox(R.id.Clients_TT_Tika2));
        jsonCheckboxMapSave.put("tt3", getCheckbox(R.id.Clients_TT_Tika3));
        jsonCheckboxMapSave.put("tt4", getCheckbox(R.id.Clients_TT_Tika4));
        jsonCheckboxMapSave.put("tt5", getCheckbox(R.id.Clients_TT_Tika5));

        //Complicated Delivery History
        //NOTE: The following JSON keys are not present in the Servlet response
        //The response will be manipulated to trick Checkbox handlers
        //so everything is handled in a general way.
        initializeJsonManipulation();
        for (Pair<String, Integer> pair : deliveryHistoryMapping) {
            jsonCheckboxMap.put(pair.first, getCheckbox(pair.second));
        }
    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("para", getEditText(R.id.para));
        jsonEditTextMap.put("gravida", getEditText(R.id.gravida));
        jsonEditTextMap.put("boy", getEditText(R.id.SonNum));
        jsonEditTextMap.put("girl", getEditText(R.id.DaughterNum));
        jsonEditTextMap.put("houseGRHoldingNo", getEditText(R.id.Clients_House_No));
        jsonEditTextMap.put("mobileNo", getEditText(R.id.Clients_Mobile_no));

        jsonEditTextMapChild.put("son", getEditText(R.id.editTextFPSonNum));
        jsonEditTextMapChild.put("dau", getEditText(R.id.editTextFPDaughterNum));
        jsonEditTextMapChild.put("menstrualDuration", getEditText(R.id.editTextFpCycleDuration));
    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewsMap.put("FacilityName", getTextView(R.id.fwc_heading));
    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMapSave.put("bloodGroup", getSpinner(R.id.Blood_Group_Dropdown));
        jsonSpinnerMap.put("cBloodGroup", getSpinner(R.id.Blood_Group_Dropdown));

        jsonSpinnerMapChild.put("currentMethod", getSpinner(R.id.spinnerMethodFPStartup));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {
    }

    @Override
    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("cLMP", getEditText(R.id.lmpDate));
        jsonEditTextDateMap.put("cEDD", getEditText(R.id.edd));

        jsonEditTextDateMap.put("cTT1Date", getEditText(R.id.ttDate1));
        jsonEditTextDateMap.put("cTT2Date", getEditText(R.id.ttDate2));
        jsonEditTextDateMap.put("cTT3Date", getEditText(R.id.ttDate3));
        jsonEditTextDateMap.put("cTT4Date", getEditText(R.id.ttDate4));
        jsonEditTextDateMap.put("cTT5Date", getEditText(R.id.ttDate5));

        jsonEditTextDateMapSave.put("lmp", getEditText(R.id.lmpDate));
        jsonEditTextDateMapSave.put("edd", getEditText(R.id.edd));

        jsonEditTextDateMapChild.put("marrDate", getEditText(R.id.editTextFPMarriageDate));
        jsonEditTextDateMapChild.put("lastDeliveryDate", getEditText(R.id.editTextFPLastDeliveryDate));
        jsonEditTextDateMapChild.put("LMP", getEditText(R.id.editTextFPLmpDate));

        jsonEditTextDateMapSave.put("ttDate1", getEditText(R.id.ttDate1));
        jsonEditTextDateMapSave.put("ttDate2", getEditText(R.id.ttDate2));
        jsonEditTextDateMapSave.put("ttDate3", getEditText(R.id.ttDate3));
        jsonEditTextDateMapSave.put("ttDate4", getEditText(R.id.ttDate4));
        jsonEditTextDateMapSave.put("ttDate5", getEditText(R.id.ttDate5));
    }

    @Override
    protected void initiateRadioGroups() {
        jsonRadioGroupButtonMapChild.put("childTakeBreastmilkOnly", Pair.create(
                getRadioGroup(R.id.fpFeedBreastMilkRadioGroup), Pair.create(
                        getRadioButton(R.id.radioFpBreastmilk_yes),
                        getRadioButton(R.id.radioFpBreastmilk_no)
                )
                )
        );
        jsonRadioGroupButtonMapChild.put("isPregnant", Pair.create(
                getRadioGroup(R.id.fpIsPregnantRadioGroup), Pair.create(
                        getRadioButton(R.id.radioFpInfoIsPregnantYes),
                        getRadioButton(R.id.radioFpInfoIsPregnantNo)
                )
                )
        );

        jsonRadioGroupButtonMapChild.put("menstrualCycle", Pair.create(
                getRadioGroup(R.id.fpCycleRadioGroup), Pair.create(
                        getRadioButton(R.id.radioFpCycleRegular),
                        getRadioButton(R.id.radioFpCycleIrregular)
                )
                )
        );
        jsonRadioGroupButtonMapChild.put("menstrualAmount", Pair.create(
                getRadioGroup(R.id.fpMenstrualAmountRadioGroup), Pair.create(
                        getRadioButton(R.id.radioFpMenstrualAmountNormal),
                        getRadioButton(R.id.radioFpMenstrualAmountNotNormal)
                )
                )
        );
        jsonRadioGroupButtonMapChild.put("menstrualPain", Pair.create(
                getRadioGroup(R.id.fpMenstrualPainRadioGroup), Pair.create(
                        getRadioButton(R.id.radioFpMenstrualPain_yes),
                        getRadioButton(R.id.radioFpMenstrualPain_no)
                )
                )
        );

        /*jsonRadioGroupButtonMapChild.put("isNewClient", Pair.create(
                getRadioGroup(R.id.radioGroupHasCurrentMethod), Pair.create(
                        getRadioButton(R.id.radioFpHasCurrentMethod_yes),
                        getRadioButton(R.id.radioFpHasCurrentMethod_no)
                )
                )
        );*/
    }

    private void saveClientToJson() {
        saveClientToJson(this, true);
    }

    private void saveClientToJson(AsyncCallback callback, boolean storeLocalJson) {
        clientInfoUpdateTask = new AsyncPregnancyInfoUpdate(callback, SecondActivity.this);
        JSONObject json;

        try {
            json = buildQueryHeader(false);

            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMapSave, json);
            Utilities.getCheckboxesBlank(jsonCheckboxMapSave, json);
            Utilities.getSpinners(jsonSpinnerMapSave, json);
            getSpecialCases(json);
            if (!storeLocalJson) {

                json.put("lmp", new CustomSimpleDateFormat("yyyy-MM-dd").format(new CustomSimpleDateFormat("dd/MM/yyyy").parse(lmp_edd.get("lmp"))));
                json.put("edd", new CustomSimpleDateFormat("yyyy-MM-dd").format(Utilities.addDateOffset(new CustomSimpleDateFormat("dd/MM/yyyy").parse(lmp_edd.get("lmp")), PregWoman.PREG_PERIOD)));
            }
            //.................
            String sateliteCenter = ProviderInfo.getProvider().getSatelliteName();
            json.put("sateliteCenterName", sateliteCenter == null ? "" : sateliteCenter);
            //.....................
            Log.d(LOGTAG, "PREPARE PREGNANCY JSON:\n\t" + json.toString());
            storeInfoToJsonfirst(json);
            clientInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Exception: " + jse.getMessage());
        } catch (ParseException pe) {
            Log.e(LOGTAG, "Date parsing Exception");
            Utilities.printTrace(pe.getStackTrace());
        }
    }

    private void storeInfoToJsonfirst(JSONObject json) {
        try {
            client.put("cLMP", json.get("lmp"));
            client.put("cEDD", json.get("edd"));
            client.put("cLastChildAge", json.get("lastChildAge"));
            client.put("cHistoryComplicated", json.get("complicatedHistory"));
            client.put("cHistoryComplicatedContent", 0);
            client.put("cBloodGroup", json.get("bloodGroup"));
            client.put("cBoy", json.get("boy"));
            client.put("cGirl", json.get("girl"));
            client.put("cPara", json.get("para"));
            client.put("cGravida", json.get("gravida"));
            client.put("cHeight", json.get("height"));
            client.put("cMobileNo", json.get("mobileNo"));
            client.put("chGRHNo", json.get("houseGRHoldingNo"));
            client.put("hasAbortionInformation", "No");
            client.put("deathStatus", "");
            client.put("cLMPStatus", 0);

            for (int i = 1; i <= 5; i++) {
                client.put("cTT" + i, json.get("tt" + i));
                client.put("cTT" + i + "Date", json.get("tt" + "Date" + i));
            }

        } catch (JSONException JSE) {
            Log.e(LOGTAG, "JSON Exception:");
            Utilities.printTrace(JSE.getStackTrace());
        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString = "{" +
                "healthId:\"" + responseID + "\"," +
                "providerId:\"" + ProviderInfo.getProvider().getProviderCode() + "\"," +
                "complicatedHistory:\"\"," +  //temp
                "complicatedHistoryNote:\"9\",";    //temp

        queryString = queryString.substring(0, queryString.length() - 1);
        queryString += "}";
        return new JSONObject(queryString);
    }

    private void getSpecialCheckBoxes(JSONObject json) {
        String complicatedHistories = "";
        for (int i = 0; i < deliveryHistoryMapping.size(); i++) {
            if (getCheckbox(deliveryHistoryMapping.get(i).second).isChecked()) {
                complicatedHistories += String.valueOf(i + 1) + ",";
            }
        }
        if (!complicatedHistories.equals("")) { //Get rid of trailing ,
            complicatedHistories = complicatedHistories.substring(0, complicatedHistories.length() - 1);
        }
        try {
            json.put("complicatedHistoryNote", complicatedHistories);
        } catch (JSONException JSE) {
            Log.e(LOGTAG, "Error:\n\t");
            Utilities.printTrace(JSE.getStackTrace());
        }
    }

    public void getSpecialCases(JSONObject json) {
        try {
            json.put("pregNo", (woman != null && woman.getPregNo() != 0 && pregInfoEditMode) ? woman.getPregNo() : "");
            pregInfoEditMode = false;
            //To enter 0 if ""
            int year = (getEditText(R.id.lastChildYear).getText().toString()).isEmpty() ? 0 : Integer.parseInt(getEditText(R.id.lastChildYear).getText().toString());
            int month = (getEditText(R.id.lastChildMonth).getText().toString()).isEmpty() ? 0 : Integer.parseInt(getEditText(R.id.lastChildMonth).getText().toString());
            int feet = (getEditText(R.id.heightFeet).getText().toString()).isEmpty() ? 0 : Integer.parseInt(getEditText(R.id.heightFeet).getText().toString());
            int inch = (getEditText(R.id.heightInch).getText().toString()).isEmpty() ? 0 : Integer.parseInt(getEditText(R.id.heightInch).getText().toString());

            month = year * 12 + month;
            feet = feet * 12 + inch;

            json.put("lastChildAge", month);
            json.put("height", feet);
            if (prevMaxTT > 0) {
                preventPreviousTTInfoSubmit(json);
            }
            getSpecialCheckBoxes(json);

        } catch (JSONException jse) {

        }
    }

    private void preventPreviousTTInfoSubmit(JSONObject json) {
        for (int i = 1; i <= prevMaxTT; i++) {
            String key = "tt" + i;
            try {
                json.put(key, "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleAddNewPregnancy(View view) {
        if (woman != null) {
            if(woman.isDead()) {
                Toast.makeText(this, R.string.reportedDeath, Toast.LENGTH_LONG).show();
                return;
            }
            if (woman.getDeliveryInfo() == 0 && woman.getAbortionInfo() == 0) { //no delivery or abortion recorded for this pregnancy
                //get minimum delivery details
                shortDelivery = new Dialog(this);
                shortDelivery.setTitle(R.string.title_activity_minimum_delivery);
                shortDelivery.setContentView(R.layout.minimum_delivery_fragment_wrapper);
                shortDelivery.show();

            } else {
                resetFields(view);
            }
        } else {
            resetFields(view);
        }
    }

    private HashMap<String, String> handleDeliveryDatePopUp() {
        final Dialog dialog = new Dialog(this);

        if (lmp_edd == null) {
            lmp_edd = new HashMap<>();
        }

        //Remove title bar
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.date_selector);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //Guide lmp back calculation

        final EditText dDate = (EditText) dialog.findViewById(R.id.id_delivery_date);

        dDate.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        CustomSimpleDateFormat uiFormat = new CustomSimpleDateFormat("dd/MM/yyyy");

                        try {
                            EditText estLmp = (EditText) dialog.findViewById(R.id.estimatedLmpDate);
                            String deliveryDate = dDate.getText().toString();
                            if (!deliveryDate.equals("")) {
                                Date d_date = Utilities.addDateOffset(uiFormat.parse(deliveryDate), -PregWoman.PREG_PERIOD);
                                estLmp.setText(uiFormat.format(d_date));
                            }

                        } catch (NumberFormatException NFE) {
                            Log.e(LOGTAG, NFE.getMessage());
                            Utilities.printTrace(NFE.getStackTrace());
                        } catch (ParseException pe) {
                            Log.e(LOGTAG, pe.getMessage());
                            Utilities.printTrace(pe.getStackTrace());
                        }
                    }
                }
        );


        ((Button) dialog.findViewById(R.id.saveEstimatedDeliveryOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deliveryDate = ((EditText) dialog.findViewById(R.id.id_delivery_date)).getText().toString();
                String estimatedLmp = ((EditText) dialog.findViewById(R.id.estimatedLmpDate)).getText().toString();
                if (!deliveryDate.equals("") && !estimatedLmp.equals("")) {
                    lmp_edd.put("edd", deliveryDate);
                    lmp_edd.put("lmp", estimatedLmp);
                    SecondActivity.this.jsonEditTextDateMapSave.get("lmp").setText(estimatedLmp);
                    handleDialogButtonClick(dialog, v);
                } else {
                    Utilities.showBiggerToast(SecondActivity.this, R.string.GeneralSaveWarning);
                }
            }
        });

        ((Button) dialog.findViewById(R.id.saveEstimatedDeliveryCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDialogButtonClick(dialog, v);
            }
        });


        int listenables[] = {R.id.Date_Picker_Button, R.id.imageViewDeliveryDate};

        for (int i : listenables) {

            dialog.findViewById(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickDate(v, dialog);
                }
            });
        }

        return lmp_edd;
    }

    private void handleDialogButtonClick(Dialog dialog, View view) {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (view.getId() == R.id.saveEstimatedDeliveryOk) {
            saveClientToJson(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    startDeliveryWithoutPregInfo(result);
                }
            }, false);
        }
    }

    void pickDate(View view, Dialog dialog) {
        if (datePicker == null) {
            datePicker = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        }
        switch (view.getId()) {
            case R.id.imageViewDeliveryDate:
                datePicker.show((EditText) dialog.findViewById(R.id.id_delivery_date));
                break;
            case R.id.Date_Picker_Button:
                datePicker.show((EditText) dialog.findViewById(R.id.estimatedLmpDate));
                break;
        }
    }

    public void resetFields(View view) {
        Utilities.Reset(this, R.id.clients_info_layout);
        Utilities.Enable(this, R.id.clients_info_layout);
        Utilities.EnableField(this, R.id.Clients_House_No, "edit");
        Utilities.EnableField(this, R.id.Clients_Mobile_no, "edit");

        Utilities.MakeVisible(this, R.id.textViewDirectPNC);
        Utilities.MakeVisible(this, R.id.client_Save_Button);
        //TODO: NEED to review more precisely later.......
        if (woman != null) {
            for (int i = prevMaxTT; i > 0; i--) {
                String keyParseTT = "R.id.Clients_TT_Tika" + i;
                Utilities.Disable(this, TTcheckboxMap.get(keyParseTT));
                getCheckbox(TTcheckboxMap.get(keyParseTT)).setChecked(true);
            }
        } else {
            prevMaxTT = 0;
            setTT_UI();
        }
        pregInfoExists = false;

    }

    public void editFields(View view) {
        pregInfoEditMode = true;

        Utilities.Enable(this, R.id.clients_info_layout);
        Utilities.EnableField(this, R.id.Clients_House_No, "edit");
        Utilities.EnableField(this, R.id.Clients_Mobile_no, "edit");

        Utilities.MakeInvisible(this, R.id.client_edit_Button);
        Utilities.MakeInvisible(this, R.id.client_New_preg_Button);
        Utilities.MakeVisible(this, R.id.client_update_Button);
        //TODO Need review why save button is enabled
        Utilities.Enable(this, R.id.client_Save_Button);
        Utilities.MakeVisible(this, R.id.client_Cancel_Button);
        Utilities.Enable(this, R.id.client_Cancel_Button);

        if (pregInfoExists && prevMaxTT > 0) {
            for (int i = prevMaxTT; i > 0; i--) {
                String keyParseTT = "R.id.Clients_TT_Tika" + i;
                Utilities.Disable(this, TTcheckboxMap.get(keyParseTT));
            }
        }
    }

    public void getView_WomenWithPregInfo() {
        woman.UpdateUIField(this);

        Utilities.Disable(this, R.id.clients_info_layout);
        Utilities.DisableField(this, R.id.Clients_House_No);
        Utilities.DisableField(this, R.id.Clients_Mobile_no);

        Utilities.MakeInvisible(this, R.id.textViewDirectPNC);

        Utilities.MakeInvisible(this, R.id.client_Save_Button);
        Utilities.MakeInvisible(this, R.id.client_update_Button);
        Utilities.MakeInvisible(this, R.id.client_Cancel_Button);
        Utilities.MakeVisible(this, R.id.client_New_preg_Button);

        Utilities.Enable(this, R.id.client_New_preg_Button);

        Utilities.VisibleLayout(this, R.id.mnchServicesLayout);
        statusofEditButtonForPregWomenDetails();
        //hide non MNC services for CSBA
        if (provider.getmCsba().equals("1")) {
            Utilities.MakeInvisible(this, R.id.pacButton);
        }

        Utilities.InVisibleLayout(this, R.id.fragment_fp_startup_scroll);
        Utilities.VisibleLayout(this, R.id.fragment_client_info_scroll);
    }

    public void statusofEditButtonForPregWomenDetails() {
        if (woman.getDeliveryInfo() == 1 || woman.getAbortionInfo() == 1) {
            Utilities.MakeInvisible(this, R.id.client_edit_Button);
            Utilities.Disable(this, R.id.client_edit_Button);
        } else {
            Utilities.MakeVisible(this, R.id.client_edit_Button);
            Utilities.Enable(this, R.id.client_edit_Button);
        }
    }

    public void getView_WomenWithOutPregInfo() {
        Utilities.Enable(this, R.id.client_Save_Button);

        Utilities.MakeVisible(this, R.id.textViewDirectPNC);

        //TODO review is required
        try {
            if (client.has("cBoy")) getEditText(R.id.SonNum).setText(client.getString("cBoy"));
            if (client.has("cGirl"))
                getEditText(R.id.DaughterNum).setText(client.getString("cGirl"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utilities.MakeVisible(this, R.id.client_Save_Button);
        Utilities.MakeInvisible(this, R.id.client_Cancel_Button);
        Utilities.MakeInvisible(this, R.id.client_update_Button);
        Utilities.MakeInvisible(this, R.id.client_edit_Button);
        Utilities.MakeInvisible(this, R.id.client_New_preg_Button);

        Utilities.InVisibleLayout(this, R.id.mnchServicesLayout);
        Utilities.InVisibleLayout(this, R.id.fragment_fp_startup_scroll);
        Utilities.VisibleLayout(this, R.id.fragment_client_info_scroll);
    }

    public void getView_WomenWithoutFPInfo() {
        Utilities.Reset(this, R.id.fpStartupFragment);

        Utilities.DisableField(this, R.id.Clients_House_No);
        Utilities.DisableField(this, R.id.Clients_Mobile_no);

        Utilities.InVisibleLayout(this, R.id.mnchServicesLayout);
        Utilities.InVisibleLayout(this, R.id.fragment_client_info_scroll);

        Utilities.VisibleLayout(this, R.id.fragment_fp_startup_scroll);
    }

    public void getView_NoClient() {
        Utilities.InVisibleLayout(this, R.id.fragment_client_intro_scroll);
        Utilities.InVisibleLayout(this, R.id.fragment_client_info_scroll);
        Utilities.InVisibleLayout(this, R.id.fragment_fp_startup_scroll);
        Utilities.InVisibleLayout(this, R.id.mnchServicesLayout);
        Utilities.InVisibleLayout(this, R.id.serviceHeaderLayout);

    }

    private boolean hasTheRequiredFileds() {
        boolean hasAllValues = true;
        validationMsg = new StringBuilder();

        if (!Validation.hasText(jsonEditTextDateMapSave.get("lmp"))) {
            validationMsg.append(Utilities.ConvertNumberToBangla("* " + "শেষ মাসিকের তারিখ\n"));
            hasAllValues = false;
        }

        String textFields[] = {"para", "gravida", "boy", "girl"};
        String minTextFields[] = {"para", "gravida"};
        HashMap<String, String> labelMapping = new HashMap<>();
        labelMapping.put(textFields[0], "প্যারা");
        labelMapping.put(textFields[1], "গ্রাভিডা");
        labelMapping.put(textFields[2], "ছেলে");
        labelMapping.put(textFields[3], "মেয়ে");

        boolean isParaZero;

        try {
            isParaZero = (Integer.valueOf(jsonEditTextMap.get("para").getText().toString()) == 0);
        } catch (NumberFormatException nfe) {
            isParaZero = false;
        }

        String searchableFields[] = isParaZero ? minTextFields : textFields;

        for (int i = 0; i < searchableFields.length; ++i) {
            String mandatoryField = searchableFields[i];

            if (!Validation.hasText(jsonEditTextMap.get(mandatoryField))) {
                validationMsg.append("* " + labelMapping.get(mandatoryField) + "\n");
                hasAllValues = false;
            }
        }

        if ((!Validation.hasText(getEditText(R.id.lastChildYear)) ||
                !Validation.hasText(getEditText(R.id.lastChildMonth))) & !isParaZero) {
            validationMsg.append("* " + "শেষ সন্তানের বয়স\n");
            hasAllValues = false;
        }

        return hasAllValues;
    }

    private static void initiateTTcheckboxMap() {
        TTcheckboxMap.put("R.id.Clients_TT_Tika1", R.id.Clients_TT_Tika1);
        TTcheckboxMap.put("R.id.Clients_TT_Tika2", R.id.Clients_TT_Tika2);
        TTcheckboxMap.put("R.id.Clients_TT_Tika3", R.id.Clients_TT_Tika3);
        TTcheckboxMap.put("R.id.Clients_TT_Tika4", R.id.Clients_TT_Tika4);
        TTcheckboxMap.put("R.id.Clients_TT_Tika5", R.id.Clients_TT_Tika5);
    }

    public void setTT_UI() {
        int maxTT = checkMaxTTfromJSON();
        initiateTTcheckboxMap();

        for (int i = 1; i <= maxTT; i++) {
            String keyParseTT = "R.id.Clients_TT_Tika" + i;
            Utilities.Disable(this, TTcheckboxMap.get(keyParseTT));
        }
        show_TT_UI(maxTT);
    }

    public static void show_TT_UI(int maxTT) {
        final int maximumTT = maxTT;
        for (int i = 1; i <= 5; i++) {
            String keyParseTT = "R.id.Clients_TT_Tika" + i;
            if (i < (maxTT + 2)) {
                Utilities.MakeVisible(act, TTcheckboxMap.get(keyParseTT));

                if (i < maxTT) {
                    Utilities.Disable(act, TTcheckboxMap.get(keyParseTT));
                } else {
                    if (i > prevMaxTT) {
                        Utilities.Enable(act, TTcheckboxMap.get(keyParseTT));
                    }

                }
            } else {
                Utilities.MakeInvisible(act, TTcheckboxMap.get(keyParseTT));
            }
        }
        if (maxTT >= 1) {
            Utilities.MakeVisible(act, R.id.viewTTDateImageView);
            act.findViewById(R.id.viewTTDateImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringBuilder ttInfo = new StringBuilder();
                    for (int i = 1; i <= maximumTT; i++) {
                        String date = act.jsonEditTextDateMapSave.get("ttDate" + i).getEditableText().toString();
                        if (date.equals("")) {
                            ttInfo.append("TT-" + i + ": তারিখ জানা নেই" + "\n");
                        } else {
                            ttInfo.append("TT-" + i + ": " + date + "\n");
                        }

                    }
                    AlertDialogCreator.SimpleMessageDialog(act, ttInfo.toString(), "টিটি টিকার তথ্য", android.R.drawable.ic_dialog_info);
                }
            });
        } else {
            Utilities.MakeInvisible(act, R.id.viewTTDateImageView);
        }
    }

    public int checkMaxTTfromJSON() {
        int maxTT = 0;
        for (int i = 5; i >= 1; i--) {
            String keyParseTT = "cTT" + i;
            try {
                if (client.getString(keyParseTT).equals("1")) {
                    maxTT = i;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        prevMaxTT = maxTT;
        return maxTT;
    }

    public static int checkMaxTTfromUI() {
        int maxChecked = 0;
        String keyParseTT;

        for (int i = 5; i >= 1; i--) {
            keyParseTT = "R.id.Clients_TT_Tika" + i;
            CheckBox box = (CheckBox) act.findViewById(TTcheckboxMap.get(keyParseTT));
            if (box.isChecked()) {
                maxChecked = i;
                break;
            }
        }

        return maxChecked;
    }

    public void specialResetAfterClientSearch() {
        getButton(R.id.MNCHButton).setBackground(getResources().getDrawable(R.drawable.button_style));
        getButton(R.id.FPButton).setBackground(getResources().getDrawable(R.drawable.button_style));
        if (fpScreening != null) fpScreening = null;
        if (fpExaminations != null) fpExaminations = null;
    }

    public void showMNCH(View view) {
        if (woman == null) {
            Utilities.showAlertToast(this, getString(R.string.str_service_not_for_you));
            return;
        }
        //TODO: Need to check rigorously before deleting the codes
        if (woman.isDead() && woman.getDeliveryInfo()!=1) {
            Toast.makeText(this, R.string.reportedDeath, Toast.LENGTH_LONG).show();
            return;
        }
        boolean isWoman = woman != null ? Integer.valueOf(woman.getSex()) == 2 ? true : false : false;
        if (Validation.isElco(woman.getAge()) || !isWoman) {//according to field request, age filtering is off for male client
            getButton(R.id.MNCHButton).setBackground(getResources().getDrawable(R.drawable.button_style_highlighted));
            getButton(R.id.FPButton).setBackground(getResources().getDrawable(R.drawable.button_style));
            if (pregInfoExists){
                getView_WomenWithPregInfo();

            } else{
                getView_WomenWithOutPregInfo();
            }

        } else {
            Utilities.showAlertToast(this, getString(R.string.str_service_not_for_you));
            return;
        }
    }

    public void showFP(View view) {
        //Utilities.showBiggerToast(this, R.string.NotReadyYet);
        GeneralPerson patient = (woman == null ? generalPerson : woman);
        if (patient.isDead()) {
            Toast.makeText(this, R.string.reportedDeath, Toast.LENGTH_LONG).show();
            return;
        }
        boolean isWoman = Integer.valueOf(patient.getSex()) == 2 ? true : false;
        //age filtering is off for male client according to field request. Only minimum age validation is added for male
        if (Validation.isElco(woman == null ? generalPerson.getAge() : woman.getAge()) || (!isWoman && patient.getAge() >= 16)) {
            getButton(R.id.FPButton).setBackground(getResources().getDrawable(R.drawable.button_style_highlighted));
            getButton(R.id.MNCHButton).setBackground(getResources().getDrawable(R.drawable.button_style));
            getView_WomenWithoutFPInfo();
            ((FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment)).loadPreviousRecord();
            ((FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment)).hideViewIfMale();

        } else {
            Utilities.showAlertToast(this, getString(R.string.str_service_not_for_you));

        }
    }

    @Override
    public Pair<PregWoman, ProviderInfo> onProviderAndPatientRequired() {

        return Pair.create(this.woman, ProviderInfo.getProvider());

    }

    @Override
    public void onDeliverySaved(String result) {
        if (shortDelivery != null) {
            shortDelivery.dismiss();
            if (!result.equals("cancel")) {
                resetFields(getButton(R.id.client_New_preg_Button));
            }
        }
    }

    @Override
    public void onPositiveButtonClicked(DialogInterface dialog) {
        switch (dialogFlag) {
            case Flag.CHANGE:
                dialog.cancel();
                showScreeningDialog();
                dialogFlag = 0;
                break;

            case Flag.RESULT:
                dialog.cancel();
                showEligibleFPMethodsCounselling();
                dialogFlag = 0;
                break;

            case Flag.COUNSEL_DECISION:
                showScreeningDialog();
                dialogFlag = 0;
                dialog.cancel();
                break;

            default:
                //do nothing
                break;
        }
    }

    @Override
    public void onNegativeButtonClicked(DialogInterface dialog) {
        switch (dialogFlag) {
            case Flag.CHANGE:
                dialog.cancel();
                dialogFlag = 0;
                //do nothing
                break;

            default:
                //do nothing
                break;
        }
    }

    @Override
    public void onNeutralButtonClicked(DialogInterface dialog) {
        switch (dialogFlag) {
            case Flag.RESULT:
                if (fpExaminations != null || fpScreening != null) {
                    showPreview();
                }
                break;
            case Flag.COUNSEL_DECISION:
                Utilities.showAlertToast(this, getString(R.string.str_counceling_about_fp));
                break;
            default:
                //do nothing
                break;
        }

    }

    private void showPreview() {
        StringBuilder previewMessage = new StringBuilder();

        if (fpScreening != null) {
            previewMessage.append(fpScreening.toStringPreview(this));
        }
        if (fpExaminations != null) {
            previewMessage.append(fpExaminations.toStringPreview(this));
        }
        if (((FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment)).getChildCount() == 0) {
            previewMessage.append("\n\n**সন্তান নেই (নালিপ্যারাস)");
        }
        AlertDialogCreator.SimpleScrollableMessageDialog(this,
                previewMessage.toString(), "স্ক্রিনিং প্রিভিঊ", android.R.drawable.ic_dialog_info);
    }

    public void showScreeningDialog() {
        final Dialog dialog = new Dialog(SecondActivity.this);

        //Remove title bar
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.view_fp_screening);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w = dm.widthPixels;
        int h = dm.heightPixels;

        dialog.getWindow().setLayout((int) (w * 0.97), (int) (h * 0.85));

        ((FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment))
                .hideQuestionsGenderWise((ViewGroup) dialog.findViewById(R.id.fpQuestionnaireContainer));

        MethodUtils.prepareRadioGroupToRemoveErrorSign((ViewGroup) dialog.findViewById(R.id.fpQuestionnaireContainer));

        if (fpScreening != null) ScreeningHelper.setQuestionnaireValues(fpScreening, dialog);
        ((FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment))
                .setPreDefinedValues(dialog);

        final Button ok = (Button) dialog.findViewById(R.id.screeningSubmitButton);
        ok.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MethodUtils.hasUnselectedRadioGroup((ViewGroup) dialog.findViewById(R.id.fpQuestionnaireContainer))) {
                    Utilities.showAlertToast(act, getString(R.string.str_all_question_answer_mandatory));
                    return;
                }
                fpScreening = ScreeningHelper.getQuestionnaireValues(dialog);
                showExamDialog();
                dialog.dismiss();
            }
        });
    }

    public void setDialogFlag(int dialogFlag) {
        this.dialogFlag = dialogFlag;
    }

    private void showExamDialog() {
        final Dialog dialog = new Dialog(SecondActivity.this);

        //Remove title bar
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_fp_examination);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        final Button submit = (Button) dialog.findViewById(R.id.fpExamSubmitButton);
        ScreeningHelper.prepareMapping(dialog);
        ScreeningHelper.changeMandatorySignColor(dialog);
        if (fpExaminations != null) {
            JSONObject fpExamJson = null;
            try {
                fpExamJson = new JSONObject(fpExaminations.toJsonString());
                ScreeningHelper.setPreviousValuesByJson(fpExamJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ((FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment))
                .setPreDefinedValuesForExams(dialog);

        boolean isWoman = woman != null ? Integer.valueOf(woman.getSex()) == 2 ? true : false : false;
        if (!isWoman) {
            ((FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment))
                    .hideExamForMale((ViewGroup) dialog.findViewById(R.id.fp_clients_info_layout), new Integer[]{R.id.fpUrineSugarLayout, R.id.screeningTitleLayout, R.id.fpExamNextButtonLayout});
        }
        submit.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isWoman = woman != null ? Integer.valueOf(woman.getSex()) == 2 ? true : false : false;
                if (isWoman && !hasMandatoryValues(dialog)) return;
                setExamValues(dialog);
                FPScreeningEngine fpEngine = new FPScreeningEngine(fpScreening, fpExaminations);
                int childCount = ((FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment)).getChildCount();
                AlertDialogCreator.SpecialDecisionCustomDialog(act, fpEngine.getMessageWithEligibleMethodList(isWoman, childCount == 0 ? true : false),
                        "গ্রহীতার জন্য উপযুক্ত পদ্ধতি সমূহ", "গ্রহীতাকে তার জন্য উপযুক্ত পদ্ধতি সমূহের প্রতিটি পদ্ধতি বিষয়ে কাউন্সেলিং করুন।",
                        android.R.drawable.ic_dialog_info, new String[]{"OK", "SCREENING PREVIEW"}, false);
                dialogFlag = Flag.RESULT;
                dialog.dismiss();
                if (fpExaminations != null || fpScreening != null) {
                    showPreview();
                }
            }
        });
    }

    private boolean hasMandatoryValues(Dialog dialog) {
        boolean hasValues = true;
        if (!Validation.hasSelected((Spinner) dialog.findViewById(R.id.fpAnemiaSpinner)))
            hasValues = false;
        if (!Validation.hasSelected((Spinner) dialog.findViewById(R.id.fpJaundiceSpinner)))
            hasValues = false;
        if (!Validation.hasSelected((Spinner) dialog.findViewById(R.id.fpBreastConditionSpinner)))
            hasValues = false;
        if (!Validation.hasText((EditText) dialog.findViewById(R.id.fpBloodPressureValueSystolic)))
            hasValues = false;
        if (!Validation.hasText((EditText) dialog.findViewById(R.id.fpBloodPressureValueDiastolic)))
            hasValues = false;

        return hasValues;
    }

    private void setExamValues(Dialog dialog) {
        fpExaminations = ScreeningHelper.setExamValues(dialog);
    }

    public void showEligibleFPMethodsCounselling() {
        final Dialog dialog = new Dialog(SecondActivity.this);

        //Remove title bar
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_screening_counselling);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        boolean isWoman = woman != null ? Integer.valueOf(woman.getSex()) == 2 ? true : false : false;
        int childCount = ((FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment)).getChildCount();
        boolean isNulipara = childCount == 0 ? true : false;

        final ArrayList<Integer> eligibleMethodList = ScreeningHelper.getEligibleMethodArray(fpScreening, fpExaminations, isWoman, isNulipara);
        if (eligibleMethodList.contains(Flag.PILL_SUKHI))
            dialog.findViewById(R.id.layoutFPScreeningSukhi).setVisibility(View.VISIBLE);
        if (eligibleMethodList.contains(Flag.PILL_APON))
            dialog.findViewById(R.id.layoutFPScreeningApon).setVisibility(View.VISIBLE);
        if (eligibleMethodList.contains(Flag.INJECTION_DMPA))
            dialog.findViewById(R.id.layoutFPScreeningInjection).setVisibility(View.VISIBLE);
        if (eligibleMethodList.contains(Flag.IMPLANT_IMPLANON))
            dialog.findViewById(R.id.layoutFPScreeningImplant).setVisibility(View.VISIBLE);
        if (eligibleMethodList.contains(Flag.IUD))
            dialog.findViewById(R.id.layoutFPScreeningIUD).setVisibility(View.VISIBLE);
        if (eligibleMethodList.contains(Flag.PERMANENT_METHOD_MAN))
            dialog.findViewById(R.id.layoutFPScreeningPermanentMethodMale).setVisibility(View.VISIBLE);
        if (eligibleMethodList.contains(Flag.PERMANENT_METHOD_WOMAN))
            dialog.findViewById(R.id.layoutFPScreeningPermanentMethodFemale).setVisibility(View.VISIBLE);

        ScreeningHelper.addRadioGroupListenerButton(dialog, eligibleMethodList);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w = dm.widthPixels;
        int h = dm.heightPixels;

        dialog.getWindow().setLayout((int) (w * 0.985), (int) (h * 0.85));

        final Button next = (Button) dialog.findViewById(R.id.buttonFPCounselling);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ScreeningHelper.checkedAllFirstItem(dialog, eligibleMethodList)) {
                    showDialogForMethodSelection();
                    dialog.dismiss();
                } else {
                    Utilities.showAlertToast(act, getString(R.string.str_counceling_must));
                }

            }
        });
    }

    public void showDialogForMethodSelection() {
        boolean isWoman = woman != null ? Integer.valueOf(woman.getSex()) == 2 ? true : false : false;
        int childCount = ((FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment)).getChildCount();
        boolean isNulipara = childCount == 0 ? true : false;
        final String[] eligibleMethods = ScreeningHelper.getEligibleMethodArrayByName(fpScreening, fpExaminations, isWoman, isNulipara);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView titleTV = new TextView(this);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //llp.setMargins(10, 10, 10, 10);

        titleTV.setLayoutParams(llp);
        titleTV.setTypeface(null, Typeface.BOLD);
        titleTV.setTextSize(20);
        titleTV.setPadding(10, 10, 10, 10);
        titleTV.setTextColor(Color.WHITE);
        titleTV.setBackgroundColor(getResources().getColor(R.color.darkblue));
        titleTV.setText(getText(R.string.str_correct_fp_method_selection));
        builder.setCustomTitle(titleTV);
        builder.setSingleChoiceItems(eligibleMethods, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int methodCode = ConstantMaps.TagWiseMethodCodes.get(eligibleMethods[which]);
                openSpecificRegister(methodCode, true);
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void openSpecificRegister(int methodCode, boolean isNew) {
        Intent fpIntent = null;
        GeneralPerson patient = (woman == null ? generalPerson : woman);
        boolean isReferMethod = false;
        switch (methodCode) {
            case Flag.CONDOM:
                fpIntent = new Intent(this, PillCondomActivity.class);
                fpIntent.putExtra(Constants.KEY_METHOD_CODE, Flag.CONDOM);
                break;

            case Flag.PILL_SUKHI:
                fpIntent = new Intent(this, PillCondomActivity.class);
                fpIntent.putExtra(Constants.KEY_METHOD_CODE, Flag.PILL_SUKHI);
                fpIntent.putExtra(Constants.KEY_IS_SUKHI_ELIGIBLE, true);
                break;

            case Flag.PILL_APON:
                fpIntent = new Intent(this, PillCondomActivity.class);
                fpIntent.putExtra(Constants.KEY_METHOD_CODE, Flag.PILL_APON);
                fpIntent.putExtra(Constants.KEY_IS_APON_ELIGIBLE, true);
                break;

            case Flag.IUD:
                fpIntent = new Intent(this, IUDServiceActivity.class);
                fpIntent.putExtra(Constants.KEY_CLIENT_NAME, getEditText(R.id.Client_name).getEditableText().toString());
                fpIntent.putExtra(Constants.KEY_FP_DELIVERY_DATE, getEditText(R.id.editTextFPLastDeliveryDate).getText().toString());
                break;

            case Flag.INJECTION_DMPA:
                fpIntent = new Intent(this, InjectableActivity.class);
                break;

            case Flag.IMPLANT_IMPLANON:
                fpIntent = new Intent(this, ImplantActivity.class);
                fpIntent.putExtra(Constants.KEY_CLIENT_NAME, getEditText(R.id.Client_name).getEditableText().toString());
                fpIntent.putExtra(Constants.KEY_FP_DELIVERY_DATE, getEditText(R.id.editTextFPLastDeliveryDate).getText().toString());
                break;
            case Flag.IMPLANT_JADELLE:
                fpIntent = new Intent(this, ImplantActivity.class);
                fpIntent.putExtra(Constants.KEY_CLIENT_NAME, getEditText(R.id.Client_name).getEditableText().toString());
                fpIntent.putExtra(Constants.KEY_FP_DELIVERY_DATE, getEditText(R.id.editTextFPLastDeliveryDate).getText().toString());
                break;
            case Flag.PERMANENT_METHOD_MAN:
            case Flag.PERMANENT_METHOD_WOMAN:
                FPStartupFragment fragment = (FPStartupFragment) getFragmentManager().findFragmentById(R.id.fpStartupFragment);
                boyChild = fragment.getMaleChildDetails();
                girlChild = fragment.getFemaleChildDetails();
                fpIntent = new Intent(this, PermanentMethodServiceActivity.class);
                fpIntent.putExtra(Constants.KEY_CLIENT_NAME, getEditText(R.id.Client_name).getEditableText().toString());
                fpIntent.putExtra(Constants.KEY_FP_DELIVERY_DATE, getEditText(R.id.editTextFPLastDeliveryDate).getText().toString());
                fpIntent.putExtra(Constants.KEY_GENDER, patient.getSex());
                fpIntent.putExtra(Constants.KEY_CLIENT_NAME, patient.getName());
                fpIntent.putExtra(Constants.KEY_BOYCOUNT, boyChild);
                fpIntent.putExtra(Constants.KEY_GIRLCOUNT, girlChild);
                break;

            default:
                isReferMethod = true;
                break;
        }
        if (!isReferMethod) {
            fpIntent.putExtra(Constants.KEY_IS_NEW, isNew);
            if (isNew) {
                fpIntent.putExtra(Constants.KEY_FP_LMP_DATE, ((FPStartupFragment) getFragmentManager()
                        .findFragmentById(R.id.fpStartupFragment)).getLMPDateFromFPInfo());
            }
            openFPMethod(fpIntent);
        } else {
            AlertDialogCreator.SimpleMessageWithNoTitle(this, getString(R.string.str_refer_properly));
        }

    }

    /**
     * Initialize the dashboard fragment
     */
    private void initDashboard() {
        ((DashboardFragment) getFragmentManager().findFragmentById(R.id.fragment_dashboard))
                .initDashboardFragment(getFragmentManager().findFragmentById(R.id.fragment_dashboard).getView());
    }

    @Override
    public void onNeutralButtonClicked(DialogInterface dialog, String input) {
        if(!input.equals("")){
            ((EditText) findViewById(R.id.lmpDate)).setText(input);
        }
        MethodUtils.hideSoftKeyboard(this);
    }

    class DataLoader extends AsyncTask<String, Integer, Integer> {
        Context context;

        DataLoader(Context c) {
            context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((DashboardFragment) getFragmentManager().findFragmentById(R.id.fragment_dashboard))
                    .preLoading(getFragmentManager().findFragmentById(R.id.fragment_dashboard).getView());
            isDataLoading = true;
        }

        protected Integer doInBackground(String... params) {
            if (!DatabaseWrapper.getDatabase().isDbLockedByCurrentThread()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initDashboard();
                    }
                });
                return 0;
            }
            return 1;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (result == 0) {
                ((DashboardFragment) getFragmentManager().findFragmentById(R.id.fragment_dashboard))
                        .showDashboardFollowupData(getFragmentManager().findFragmentById(R.id.fragment_dashboard).getView());
            } else {
                ((DashboardFragment) getFragmentManager().findFragmentById(R.id.fragment_dashboard))
                        .postSpecialLoading(getFragmentManager().findFragmentById(R.id.fragment_dashboard).getView());
            }

            isDataLoading = false;
        }
    }

}