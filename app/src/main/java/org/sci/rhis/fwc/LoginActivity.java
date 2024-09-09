package org.sci.rhis.fwc;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jumpmind.symmetric.android.SQLiteOpenHelperRegistry;
import org.jumpmind.symmetric.android.SymmetricService;
import org.jumpmind.symmetric.common.ParameterConstants;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.AsyncFileDownload;
import org.sci.rhis.connectivityhandler.AsyncLoginTask;
import org.sci.rhis.connectivityhandler.AsyncSendingStatusOfDBOperation;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.satellite_session_planning.DBOperationSatellitePlanningInfo;
import org.sci.rhis.model.LocationHolder;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.AsyncGenerateFileToUpload;
import org.sci.rhis.utilities.ConstantQueries;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.AsyncDownloadDB;
import org.sci.rhis.utilities.CustomAsynctask;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.GPSTracker;
import org.sci.rhis.utilities.GlobalActivity;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.SharedPref;
import org.sci.rhis.utilities.UpdateApp;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class LoginActivity extends FWCServiceActivity implements AlertDialogCreator.SingleChoiceListener,
        AlertDialogCreator.DialogButtonClickedListener {
    private int placeIndex = 0,versionCode = 0, facilityCount = 0;
    private JSONObject facilityListJSON = null;
    private String placeName = "";
    private File renamedFile;
    private static boolean fileLoaded = false;
    private boolean removeDisability = false, switchToTest=false;
    public static String PACKAGE_NAME;
    private AsyncLoginTask client;
    final String HELPER_KEY = "RHIS-FWC";
    Set<String> set;
    GPSTracker gps;
    String sym_server="",sym_id="";
    String strChecksumSentFromServer = "";
    String zilla, upazilla, provCode;
    String table_data_count;
    int userSelection=0,decisionFlag=0,districtCode=0;
    static final int DISTRICT_CHOICES=1, LANG_CHOICES=2,TOOLS_CHOICE=3;

    boolean dbDownloadSuccess = false;
    boolean dbUploadSuccess = false;
    JSONObject josnLoginResponse;

    boolean initialDistrictSetupCheck = false;
    private String[] districtArray;
    private Map<Integer, String> districtMapping = new HashMap<>();
    private Map<Integer, Integer> districtMappingWithCode = new HashMap<>();

    class FileLoader extends AsyncTask<String, Integer, Integer> {
        Context context;
        FileLoader(Context c) {context = c;}
        protected Integer doInBackground(String... params) {
            loadLocations();
            return 0;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }

        protected void onProgressUpdate (Integer... progress) {
            Toast.makeText(context,"The Village list is Loading", Toast.LENGTH_LONG).show();
        }
    }



    private LoginActivity.FileLoader loader = null;


    private void loadLocations() {

        try {
            String zillaLocation = Constants.GEO_FILE_PATH + Constants.ZILLA_FILENAME;
            String villageLocation = Constants.GEO_FILE_PATH + Constants.VILLAGE_FILENAME;
            StringBuilder jsonBuilder = new StringBuilder();
            checkOrCreateJSONFiles();
            //this handler is used if the copying takes long time, then loading action won't start before completion of it
            while(!MethodUtils.isFileExists(zillaLocation) || !MethodUtils.isFileExists(villageLocation)) {
                try {
                    Thread.sleep(200, 500);
                } catch (InterruptedException ie) {
                    Log.e("GEO_LOADING", "Thread Exception: " + ie.getMessage());
                }
            }
            LocationHolder.loadJsonFile(zillaLocation, jsonBuilder);
            LocationHolder.setZillaUpazillaUnionString(jsonBuilder.toString());
            StringBuilder jsonBuilderVillage = new StringBuilder();
            LocationHolder.loadJsonFile(villageLocation, jsonBuilderVillage);
            LocationHolder.setVillageString(jsonBuilderVillage.toString());
            //executing some create scripts also in this stage
            if(!SharedPref.getQueryDoneStatus(this)){
                CommonQueryExecution.executeQuery(ConstantQueries.INDEX_QUERY_CM);
                CommonQueryExecution.executeQuery(ConstantQueries.INDEX_QUERY_MEMBER);
                SharedPref.performIndexQuery(this);
            }
            CommonQueryExecution.executeQuery(ConstantQueries.CREATE_FOLLOWUP_NOTIFICATION_SCRIPT);
            CommonQueryExecution.executeQuery(ConstantQueries.CREATE_PERMANENT_METHOD_SCRIPT);
            CommonQueryExecution.executeQuery(ConstantQueries.CREATE_PERMANENT_METHOD_FOLLOW_UP_SCRIPT);
            CommonQueryExecution.executeQuery(ConstantQueries.CREATE_CHILD_CARE_SERVICE_TABLE_SCRIPT);
            CommonQueryExecution.executeQuery(ConstantQueries.CREATE_CHILD_CARE_SERVICE_DETAIL_TABLE_SCRIPT);
            CommonQueryExecution.executeQuery(ConstantQueries.CREATE_SATELLITE_PLANNING_DETAIL);
            CommonQueryExecution.executeQuery(ConstantQueries.CREATE_SATELLITE_SESSION_PLANNING);
            fileLoaded = true;
        } catch (IOException IO) {
            Utilities.printTrace(IO.getStackTrace());
        } catch(JSONException jse) {
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    private void getTableColumnCount(String providerId){
        table_data_count = CommonQueryExecution.getTableColumnCount(providerId).toString();
    }

    private void checkOrCreateJSONFiles(){
        String zillaLocation = Constants.GEO_FILE_PATH + Constants.ZILLA_FILENAME;
        String villageLocation = Constants.GEO_FILE_PATH + Constants.VILLAGE_FILENAME;
        if(!MethodUtils.isFileExists(zillaLocation)){
            MethodUtils.copyFileFromAsset(Constants.ZILLA_FILENAME,Constants.GEO_FILE_PATH);
        }
        if(!MethodUtils.isFileExists(villageLocation)){
            MethodUtils.copyFileFromAsset(Constants.VILLAGE_FILENAME,Constants.GEO_FILE_PATH);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialSetup();
        PACKAGE_NAME = getApplicationContext().getPackageName();
        DatabaseFieldMapping.InitializeClientIntroduction();
        DatabaseFieldMapping.InitializeClientInformation();
        loader = new FileLoader(this);
        loader.execute();

        //load sharedpref
        SharedPref.clearStaticTable(this);
        SharedPref.loadStaticTable(this);


        client = new AsyncLoginTask(this, this);


        TextView tv = getTextView(R.id.appVersion);
        String footerText = "©"+ Calendar.getInstance().get(Calendar.YEAR)+
                " "+getString(R.string.app_name)+" ";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            footerText += String.format(Locale.ENGLISH, "%s (%d)", pInfo.versionName, pInfo.versionCode);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        tv.setText(footerText);

        tv.setOnLongClickListener(v -> {
            removeDisability=false;
            singleDataEntryDialog(LoginActivity.this,"Prove your authenticity",android.R.drawable.ic_dialog_alert);
            return true;
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg="১. স্যাটেলাইট প্ল্যানিং ফিচার সংযোজন করা হয়েছে \n" +
                        "২. পাক্ষিক মিটিংয়ের নোটিশ, কার্যপত্র ও কার্যবিবরণী দেখার অপশন সংযোজন করা হয়েছে\n" +
                        "৩. সদস্য একাধিকবার সংযোজন রোধকল্পে কিছু প্রক্রিয়া পরিবর্তন করা হয়েছে\n" +
                        "৪. শেষ মাসিকের তারিখ প্রদানের ক্ষেত্রে আনুমানিক তথ্য দেয়ার ফিচার সংযোজন করা হয়েছে\n" +
                        "৫. প্রসবের তারিখ প্রদানের ক্ষেত্রে নতুন ভ্যালিডেশন এড করা হয়েছে\n" +
                        "৬. ডিভাইসের স্টোরেজ এর তথ্য প্রতি অনলাইন লগিনে রিমোটলি সংগ্রহ করার অপশন সংযোজিত হয়েছে\n" +
                        "৭. মেডিসিন লিস্ট পরিবর্ধন করা হয়েছে\n" +
                        "৮. নবজাতক সংযোজন তথ্য প্রদান প্রক্রিয়া আরও সহজতর করা হয়েছে\n" +
                        "৯. কিছু মডেলের ট্যাবে ড্যাশবোর্ড লোড না হওয়ার সমস্যাটি সমাধান করা হয়েছে\n" +
                        "১০. ড্যাশবোর্ডে শিশুসেবার পরিসংখ্যান সংযোজন করা হয়েছে\n" +
                        "১১. বিবিধ ফিল্ড ইস্যু সমাধান, ভাষা ও ডিজাইনের পরিবর্তন করা হয়েছে\n" +
                        "";
                AlertDialogCreator.SimpleMessageDialog(LoginActivity.this,msg,
                        "রিলিজ নোট",android.R.drawable.ic_dialog_info);
            }
        });

    }

    private void setWorkingDistrict(){
        if(!SharedPref.getBaseUrl(this).equals(Constants.TEST_BASE_URL)){
            //get working district key by district_mapping_with_code data and shared preference district code
            Integer workingDistrictKey = MethodUtils.getKey(districtMappingWithCode, SharedPref.getDistrictCode(this));
            if(workingDistrictKey!=null) {
                getTextView(R.id.textViewDistrict).setText(districtArray[workingDistrictKey]+" জেলা");
                districtCode=districtMappingWithCode.get(workingDistrictKey);
                getTextView(R.id.textViewDistrict).setVisibility(View.VISIBLE);
            }
        }else{
            getTextView(R.id.textViewDistrict).setText(getText(R.string.str_training_app_label));
            getTextView(R.id.textViewDistrict).setVisibility(View.VISIBLE);
        }

    }

    private void initialSetup(){
        checkDistrictMapFile();

        setHelplineNumber();

        getImageView(R.id.imageViewTools).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decisionFlag=TOOLS_CHOICE;
                if (districtArray != null && districtArray.length > 0) {
                    AlertDialogCreator.showSingleChoiceDialog(LoginActivity.this, Flag.optionsArray,"Settings");
                } else {
                    AlertDialogCreator.SimpleMessageDialog(LoginActivity.this, "দয়া করে আপনার ইন্টারনেট কানেকশন ঠিক আছে কিনা দেখে অ্যাপ বন্ধ করে পুনরায় চালু করুন।\n\n*ইন্টারনেট সংযোগ থাকা বাধ্যতামূলক", "File Download Error!", android.R.drawable.stat_sys_warning);
                }
            }
        });

        getImageView(R.id.imageViewCheckServer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CheckingAsynchronously().execute(Constants.getBaseUrl(LoginActivity.this));
                getImageView(R.id.imageViewCheckServer).setClickable(false);

            }
        });

        if(SharedPref.getProviderCode(this)!=null && !SharedPref.getProviderCode(this).equals("")){
            EditText providerET = getEditText(R.id.providerId);
            providerET.setText(SharedPref.getProviderCode(this));
            Utilities.SetVisibility(this,R.id.nameLayout,View.VISIBLE);
            providerET.setEnabled(false);
            getTextView(R.id.providerNameLabel).setText(SharedPref.getProviderName(this));
        }

        if(SharedPref.getCrashFlag(this)){
            AlertDialogCreator.SimpleMessageDialog(this,
                    "অনিবার্য কারণবশতঃ এপ্লিকেশনটি বন্ধ হয়ে গেছে। অনুগ্রহ করে উর্ধ্বতন কর্তৃপক্ষকে এ ব্যপারে অবহিত করুন এবং পুনরায় লগ ইন করুন।","দুঃখিত...!",
                    android.R.drawable.ic_dialog_alert);
            SharedPref.clearCrashFlag(this);
        }

        getTextView(R.id.providerIdLabel).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeDisability=true;
                singleDataEntryDialog(LoginActivity.this,"Prove your authenticity",android.R.drawable.ic_dialog_alert);
                return true;
            }
        });

        getImageView(R.id.logo_image).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeDisability=false;
                switchToTest=true;
                singleDataEntryDialog(LoginActivity.this,"Prove your authenticity",android.R.drawable.ic_dialog_alert);
                return true;
            }
        });

    }

    private class CheckingAsynchronously extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... urls) {
            boolean isServerOkay= MethodUtils.isConnectedToServer(urls[0],5000);
            int result = isServerOkay?1:2;
            return result;
        }

        protected void onPostExecute(Integer result) {
            getImageView(R.id.imageViewCheckServer).setClickable(true);
            String successMsg = "অনলাইন যোগাযোগ ঠিক আছে।";
            String failureMsg = "এই মূহুর্তে অনলাইন যোগাযোগ সম্ভব নয়";
            AlertDialogCreator.SimpleMessageDialog(LoginActivity.this,result==1?successMsg:failureMsg,"অনলাইন যোগাযোগ চেক",android.R.drawable.ic_dialog_info);
        }
    }

    @Override
    protected void onResume() {
        if(SharedPref.getProviderCode(this)!=null && !SharedPref.getProviderCode(this).equals("")){
            EditText providerET = getEditText(R.id.providerId);
            providerET.setText(SharedPref.getProviderCode(this));
            getTextView(R.id.providerNameLabel).setText(SharedPref.getProviderName(this));
            Utilities.SetVisibility(this,R.id.nameLayout,View.VISIBLE);
            providerET.setEnabled(false);
        }
        getButton(R.id.loginbtn1).setEnabled(true);
        gps = new GPSTracker(this);
        if(!gps.canGetLocation()){
            gps.showSettingsAlert();
        }

        //check initial district setup check
        if (initialDistrictSetupCheck) checkDistrictMapFile();

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //update initialDistrictSetupCheck to true - then on resume file checker will check the district mapping file available or not
        initialDistrictSetupCheck = true;
    }

    public void openTutorial(View view) {
        MethodUtils.openOrInstallApp(this,"com.moodle.moodlemobile");
    }

    public void startLogin(View view) {
        if(districtCode==0){
            Utilities.showAlertToast(this,"জেলার তথ্য সঠিকভাবে সংরক্ষিত হয়নি! অনুগ্রহ করে আবার চেষ্টা করুন।");
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        getButton(R.id.loginbtn1).setEnabled(false);
        final EditText passwdText = (EditText)findViewById(R.id.providerPassword);
        final EditText providerText = (EditText)findViewById(R.id.providerId);
        boolean hasValue = true;
        if (!Validation.hasText(providerText)) hasValue =false;
        if (!Validation.hasText(passwdText)) hasValue =false;

        if(hasValue){
            if(!fileLoaded) {
                Utilities.showBiggerToast(this, R.string.loginWaitRequest);
                getButton(R.id.loginbtn1).setEnabled(true);
                return;
            }
            getTableColumnCount(providerText.getText().toString());

            if(client.getStatus() != AsyncTask.Status.PENDING) {
                client = new AsyncLoginTask(this, this);
            }

            String queryString =   "{" +
                    "uid:" + providerText.getText().toString() + "," +
                    "upass:" + passwdText.getText().toString() + "," +
                    "zillaid:" + districtCode + "," +
                    "table_data_count:" + table_data_count + "," +
                    "free_storage_space:\"" + MethodUtils.getInternalFreeSpace() + " MB\"," +
                    "imei_number:\"" + MethodUtils.getIMEInumber(this) + "\"," +
                    "localDbExist:\"" + (new File(Constants.getDB_PATH()).exists()?"Yes":"No")+ "\"," +
                    "device_serial:\"" + Build.SERIAL + "\"," + //TODO:deprecated from oreo
                    "version:" + versionCode +
                    ((SharedPref.getCrashDetail(this)!=null &&
                            !SharedPref.getCrashDetail(this).equals(""))?(",error_message:\""+SharedPref.getCrashDetail(this)
                            .replaceAll("'"," ").replaceAll("\"","DQ")+"\""):"")+
                    "}";
            String servlet = "login";
            String jsonRootkey = "loginInfo";
            client.execute(queryString, servlet, jsonRootkey);
        }else{
            getButton(R.id.loginbtn1).setEnabled(true);
        }

    }

    private void setHelplineNumber(){
        if(SharedPref.getHelpline(this)!=null && !SharedPref.getHelpline(this).equals("")){
            String number = Utilities.ConvertNumberToBangla(SharedPref.getHelpline(this));
            getTextView(R.id.helplineLabel).setText("হেল্পলাইনঃ "+number+
                    "\n(রবি-বৃহস্পতি সকাল ১০টা থেকে বিকাল ৪টা - ছুটির দিন ব্যাতীত)");
        }
    }

    @Override
    public void callbackAsyncTask(String result) {
        try {
            System.out.println("Login response from Activity"+result);
            sym_server="";
            sym_id="";
            JSONObject json = new JSONObject(result);
            //printing the full response sequentially
            for ( Iterator<String> i = json.keys(); i.hasNext(); ) {
                System.out.println("" + i.next());
            }
            //performing different operations setting from server
            if(json.has("changerequest") && !json.getString("changerequest").equals("")){
                try {
                   JSONObject remoteTask = new JSONObject(json.getString("changerequest"));
                   //check if there is any remote script to execute
                   if(remoteTask.has("sql") && !remoteTask.getString("sql").equals("")){
                       String script = remoteTask.getString("sql");
                       CommonQueryExecution.executeQuery(script);
                   }
                   if(remoteTask.has("helpline") && !remoteTask.getString("helpline").equals("")){
                        String helpline = remoteTask.getString("helpline");
                        SharedPref.setHelplineNumber(this,helpline);
                        setHelplineNumber();
                    }
                    if(remoteTask.has("notice") && !remoteTask.getString("notice").equals("")){
                        String notice = remoteTask.getString("notice");
                        SharedPref.setNewNotice(this,notice);
                    }
                }catch (JSONException jse){
                    jse.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //......................................................................................

            //csba & community_active keys are required, so these are not set in the server response,
            //we set these keys automatically to get rid of unwanted errors........
            if(!json.has("csba")) json.put("csba","2");
            if(!json.has("community_active")) json.put("community_active","2");

            josnLoginResponse = json;

            //...................................................

            if(json.has("update") && json.getBoolean("update")) {
                updateApplication(json.getString("version"));
            }else{
                if(json.has("dbdownloadstatus") && json.getInt("dbdownloadstatus") == 1){
                    zilla = json.has("zillaid") ? json.getString("zillaid") : "";
                    upazilla = json.has("upazilaid") ? json.getString("upazilaid") : "";
                    provCode = json.has("ProvCode") ? json.getString("ProvCode") : "";
                    String fileName = "RHIS_"+zilla+"_"+upazilla+".zip";
                    strChecksumSentFromServer = json.getString("offlinedbchecksum");

                    downloadDatabase(fileName);
                }
                //This is for database uploading purpose............................................
                else if(json.has("dbsyncrequest") && json.getInt("dbsyncrequest") == 2) {
                    zilla = json.has("zillaid") ? json.getString("zillaid") : "";
                    upazilla = json.has("upazilaid") ? json.getString("upazilaid") : "";
                    provCode = json.has("ProvCode") ? json.getString("ProvCode") : "";

                    generateFileToUpload(Constants.UPLOADDB);
                }
                else if(json.has("dbsyncrequest") && json.getInt("dbsyncrequest") == 5) {
                    String fileName = "geo_file.zip";
                    downloadGeoFiles(fileName);
                }
                //download district mapping file
                else if (json.has("dbsyncrequest") && json.getInt("dbsyncrequest") == 22) {
                    downloadFile(Constants.DISTRICT_MAP_DOWNLOAD_PATH, Constants.DISTRICT_MAP_FILE_PATH, Constants.DISTRICT_MAP_FILE_NAME);
                }
                else{
                    login(json);
                }
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown:\n " );
            jse.printStackTrace();
        }
    }

    private void login(JSONObject json){
        try {
            if (json.getBoolean("loginStatus")) { //if successful login
                if(!json.has("netStatus")){
                    SharedPref.setOnlineLoginDate(this);
                }
                if (json.getString("csba").equals("1") ||
                        json.getString("ProvType").equals(Flag.FWV) || json.getString("ProvType").equals(Flag.SACMO_FP) ||
                        json.getString("ProvType").equals(Flag.SACMO_HS) || json.getString("ProvType").equals(Flag.PARAMEDIC) ||
                        json.getString("ProvType").equals(Flag.MIDWIFE)) {
                    //first create the provider object
                    Log.d("++++++++++", "-----" + json.getString("ProvName"));
                    ProviderInfo provider = ProviderInfo.getProvider();
                    provider.setProviderName(json.getString("ProvName"));
                    provider.setProviderCode(json.getString("ProvCode"));
                    provider.setProviderFacility(json.getString("FacilityName"));
                    provider.setmProviderType(json.getString("ProvType"));
                    provider.setmCsba(json.getString("csba"));
                    provider.setCommunityActive(json.getString("community_active"));
                    provider.setDivID(json.has("divid") ? json.getString("divid") : "");
                    provider.setZillaID(json.has("zillaid") ? json.getString("zillaid") : "");
                    provider.setUpazilaID(json.has("upazilaid") ? json.getString("upazilaid") : "");
                    provider.setUnionID(json.has("unionid") ? json.getString("unionid") : "");

                    facilityListJSON = json.getJSONObject("assigned_facilities");
                    facilityCount = Integer.valueOf(facilityListJSON.getString("facility_count"));
                    provider.setmFacilityId(facilityListJSON.getJSONObject("1").getString("facility_id"));//set by default


                    if (!json.getString("csba").equals("1") && !json.getString("community_active").equals("1")){
                        //process satellite session planning if there is any approved or rejected plan
                        for(int i=1; i<=facilityCount; i++){
                            JSONObject singleFacilityPlan = facilityListJSON.getJSONObject(String.valueOf(i)).getJSONObject("planning");
                            singleFacilityPlan.remove("distributionJson");//TODO: has to recheck why this required
                            for (Iterator<String> ii = singleFacilityPlan.keys(); ii.hasNext(); ) {
                                String planningID = ii.next();
                                JSONObject singlePlan = singleFacilityPlan.getJSONObject(planningID);
                                String comment = singlePlan.getString("comments");
                                String approveDate = singlePlan.getString("approve_date");
                                String status = singlePlan.getString("status");
                                DBOperationSatellitePlanningInfo.updatePlanning(planningID,comment,approveDate,status);
                            }
                        }
                    }


                    //................................................

                    SharedPref.setProviderInfo(this, json.getString("ProvCode"), json.getString("ProvName"));

                    if (json.has(Constants.KEY_SYNC_URL)) {
                        SharedPref.setSyncUrl(this, json.getString(Constants.KEY_SYNC_URL));
                    }

                    sym_server = json.getString("server");
                    sym_id = json.getString("id");

                    if (json.getString("csba").equals("1")) {
                        if(json.getString("community_active").equals("1")){
                            try {
                                String db_name = provider.getmProviderType().equals("3")?"emisDatabase_fwa.db":"emisDatabase_ha.db";
                                updateLoginResponse(josnLoginResponse);
                                new DatabaseWrapper(this).switchdatabase(Constants.CSBA_DB+db_name);

                                startSecondActivity("");//for csba only, has to remove later
                            } catch (IOException ioe) {
                                Log.e("FWC-LOGIN", "Cannot open FWA/HA - CSBA DATABASE");
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setMessage("Cannot open FWA/HA - CSBA DATABASE !")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        }


                        startSecondActivity("");//for csba only, has to remove later

                    } else if (json.getString("ProvType").equals(Flag.SACMO_FP) || json.getString("ProvType").equals(Flag.SACMO_HS)) {
                        if (facilityCount > 1) {
                            popUpDialog();
                        } else {
                            startSecondActivity("");
                        }
                    } else {
                        popUpDialog();
                    }
                    Log.d("FWC-LOGIN", "" + provider.getProviderFacility());
                } else {
                    Utilities.showAlertToast(this, "আপনার জন্য এই রেজিস্টারটি প্রযোজ্য নয়");

                }

            } else {
                Utilities.showAlertToast(this, "Login Failed ...");
                getButton(R.id.loginbtn1).setEnabled(true);//enabling login button for another try
            }
        } catch(JSONException e){
            e.printStackTrace();
        }finally {
            getButton(R.id.loginbtn1).setEnabled(true);
        }
    }

    public void popUpDialog() {

        final Dialog dialog = new Dialog(this);

        //Remove title bar
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.place_pop);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        dialog.show();


        DisplayMetrics dm =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w = dm.widthPixels;

        dialog.getWindow().setLayout((int) (w * 0.98), ViewGroup.LayoutParams.WRAP_CONTENT);

        final Button select=(Button)dialog.findViewById(R.id.buttonPlacePopUpOK);
        final Button cancel=(Button)dialog.findViewById(R.id.buttonPlacePopUpCancel);
        final RadioGroup RadioPlaceGroupListener = (RadioGroup) dialog.findViewById(R.id.radioGroupPlace);
        final RadioButton satelliteRadioButton = (RadioButton) dialog.findViewById(R.id.radioButtonSatellite);
        final RadioButton centerRadioButton = (RadioButton) dialog.findViewById(R.id.radioButtonFacility);
        if(ProviderInfo.getProvider().getmProviderType().equals("6")||ProviderInfo.getProvider().getmProviderType().equals("5")){
            dialog.findViewById(R.id.radioButtonSatellite).setVisibility(View.GONE);
        }
        final Spinner facilitySpinner = (Spinner) dialog.findViewById(R.id.spinnerFacilityList);
        ArrayList<String> facilityList = new ArrayList<>();
        for(int i=1; i<=facilityCount; i++){
            try {
                facilityList.add(facilityListJSON.getJSONObject(String.valueOf(i)).getString("facility_name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        facilitySpinner.setAdapter(new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_list_item_1, facilityList));
        facilitySpinner.setBackgroundResource(R.drawable.gradient_spinner);

        final EditText satelliteNameACT = (EditText) dialog.findViewById(R.id.SatelliteName);




        //Satellite Center name and button Selected
        //Taking Date From System
        Date today = Calendar.getInstance().getTime();
        CustomSimpleDateFormat df = new CustomSimpleDateFormat("yyyy-MM-dd");
        String todayString = df.format(today);

        //Getting the satellite center name
        String satelliteName = CommonQueryExecution.getSingleColumnFromSingleQueryResult(ConstantQueries.GET_SATELLITE_NAME(todayString));

        //Checking if there is a Satellite on thr day provider login
        if(satelliteName!= null){
            satelliteRadioButton.setChecked(true);
            satelliteNameACT.setText(satelliteName);
            satelliteNameACT.setVisibility(View.VISIBLE);
            placeIndex=1;
        }else {
            centerRadioButton.setChecked(true);
        }

        RadioPlaceGroupListener.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radioButtonSatellite) {
                    placeIndex = 1;
                    satelliteNameACT.setVisibility(View.VISIBLE);
                } else {
                    placeIndex = 0;
                    satelliteNameACT.setVisibility(View.GONE);

                }
            }
        });

        select.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!Validation.isAnyChildChecked(RadioPlaceGroupListener) )
                {
                    Utilities.showAlertToast(LoginActivity.this, getString(R.string.ProviderPlaceTip));
                    return;
                }
                    //Setting the facility id and modifying the providerid
                    String pos = String.valueOf(facilitySpinner.getSelectedItemPosition() + 1);
                    try {
                        ProviderInfo provider = ProviderInfo.getProvider();
                        provider.setProviderCode(facilityListJSON.getJSONObject(pos).getString("provider_id"));

                        provider.setZillaID(facilityListJSON.getJSONObject(pos).getString("facility_zillaid"));
                        provider.setUpazilaID(facilityListJSON.getJSONObject(pos).getString("facility_upazilaid"));
                        provider.setUnionID(facilityListJSON.getJSONObject(pos).getString("facility_unionid"));

                        provider.setProviderFacility(facilitySpinner.getSelectedItem().toString());
                        provider.setmFacilityId(facilityListJSON.getJSONObject(pos).getString("facility_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                /*To get the place of service
                  0 facility
                  1 satellite*/

                    Log.d("placeIndex", String.valueOf(placeIndex));

                    if (placeIndex == 1) {
                        placeName = satelliteNameACT.getText().toString();
                        if (placeName.equals("")) {
                            satelliteNameACT.setError(Validation.REQUIRED_MSG);
                        } else {
                            if (set == null) {
                                set = new HashSet<>();
                                set.add(placeName);
                            } else {
                                if (!set.contains(placeName)) {
                                    set.add(placeName);
                                }
                            }

                            Log.d("placeName", placeName);
                            startSecondActivity(placeName);
                            dialog.dismiss();
                        }

                    } else {
                        placeName = "";

                        Log.d("placeName", placeName);
                        startSecondActivity(placeName);
                        dialog.dismiss();
                    }


                }

        });

        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getButton(R.id.loginbtn1).setEnabled(true);
            }
        });
    }


    public void startSynchronizationService(String server, String id) {

        try {
            SQLiteOpenHelperRegistry.register(HELPER_KEY, client.getDatabaseWrapper());

            Intent intent = new Intent(this, SymmetricService.class);

            if(isMyServiceRunning(SymmetricService.class)) {
                //stop service
                stopService(new Intent(this, SymmetricService.class));
            }

            // Notify the service of the database helper key
            intent.putExtra(SymmetricService.INTENTKEY_SQLITEOPENHELPER_REGISTRY_KEY,
                    HELPER_KEY);
            intent.putExtra(SymmetricService.INTENTKEY_REGISTRATION_URL,
                    Constants.getSymmetricdsUrl(LoginActivity.this) + server); /*"habiganj-36-71"*/
            intent.putExtra(SymmetricService.INTENTKEY_EXTERNAL_ID, id/*"NOAPARA01_TAB"*/);
            intent.putExtra(SymmetricService.INTENTKEY_NODE_GROUP_ID, "FWV");
            intent.putExtra(SymmetricService.INTENTKEY_START_IN_BACKGROUND, true);
            Properties properties = new Properties();
            properties.setProperty(ParameterConstants.AUTO_RELOAD_ENABLED, "false");
            properties.setProperty(ParameterConstants.INITIAL_LOAD_USE_EXTRACT_JOB, "false");
            //properties.setProperty(ParameterConstants.STREAM_TO_FILE_ENABLED, "true");
            properties.setProperty(ParameterConstants.AUTO_RELOAD_REVERSE_ENABLED, "true");
            properties.setProperty(ParameterConstants.TRANSPORT_HTTP_COMPRESSION_LEVEL, "9");
            properties.setProperty(ParameterConstants.ROUTING_PEEK_AHEAD_WINDOW, "20000");
            intent.putExtra(SymmetricService.INTENTKEY_PROPERTIES, properties);


            startService(intent);
            Log.d("FWC-LOGIN", "Started Symmetric Service");
        }  catch (Exception e) {
            Log.d("LOGIN ",  e.getMessage());
            e.printStackTrace();
        }
    }

    public void startSecondActivity(String satelliteCenter){
        //TODO:IMPORTANT - Ensure following line is uncommented before field release
        if(!sym_server.equals("")&& !sym_id.equals("") &&
                !ProviderInfo.getProvider().getCommunityActive().equals("1") && !SharedPref.isOnTestMode(this)) {
            startSynchronizationService(sym_server,sym_id);
        }
        if(SharedPref.isInternalDBSyncRequired(this)){
            performInternalDBSynchronization(satelliteCenter);
            //SharedPref.clearInternalDBSynchronizationFlag(this);
        }else{
            MethodUtils.showLongTimedToast(this,"অনুগ্রহ করে আপনার হোমপেজ (ড্যাশবোর্ড) না আসা পর্যন্ত অপেক্ষা করুন");
            setServiceLocation(satelliteCenter);
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra("satellite", satelliteCenter);
            SharedPref.setLoginDate(this);

            //json download/upload/mapping file flag changer to default
            if(!ProviderInfo.getProvider().getCommunityActive().equals("1")){
                updateLoginResponse(josnLoginResponse);
            }

            ProviderInfo.getProvider().setSatelliteName(satelliteCenter);
            startActivity(intent);
        }


    }

    private void setServiceLocation(String satelliteCenter){
        try {
            String insertSqlForLocation = "insert into login_location_tracking" +
                    " (providerid,login_time,latitude,longitude,satellite_name,gps_address) values (";
            insertSqlForLocation += ProviderInfo.getProvider().getProviderCode() +",DateTime('now','localtime'),";
            insertSqlForLocation += (gps.canGetLocation()?gps.getLatitude():"null")+",";
            insertSqlForLocation += (gps.canGetLocation()?gps.getLongitude():"null")+",";
            insertSqlForLocation += "'"+satelliteCenter+"',";
            String address = "";
            if(gps.canGetLocation()){
                Geocoder gcd = new Geocoder(getBaseContext(),
                        Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);
                    if (addresses.size() > 0)
                        address=addresses.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            insertSqlForLocation += "'"+(address.equals("null")?"":address)+"')";
            CommonQueryExecution.executeQuery(ConstantQueries.CREATE_LOGIN_LOCATION_TRACKING_SCRIPT);
            CommonQueryExecution.executeQuery(insertSqlForLocation);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateApplication(String version) {
        UpdateApp app = new UpdateApp();
        app.setContext(LoginActivity.this);
        String updateUrlPart = Constants.getBaseUrl(this).substring(0, StringUtils.ordinalIndexOf(Constants.getBaseUrl(this),"/",3));
        app.execute(updateUrlPart+"/update/"+version+"/app-release.apk");
    }

    private void downloadDatabase(String fileName) {
        //stop symmetric service
        stopService(new Intent(this, SymmetricService.class));

        AsyncDownloadDB downloadDB = new AsyncDownloadDB(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                startDownloadAfterWorks(result);
            }
        },LoginActivity.this);
        String downloadUrlPart = Constants.getBaseUrl(this).substring(0, StringUtils.ordinalIndexOf(Constants.getBaseUrl(this),"/",3));
        downloadDB.execute(downloadUrlPart+"/facility_offline_db/"+fileName);
    }

    private void downloadGeoFiles(String fileName) {
        AsyncFileDownload downloadFile= new AsyncFileDownload(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                //process downloaded files
                if(result.equals("Success")){
                    //Unzip downloaded file
                    File file = new File(Constants.GEO_FILE_PATH + "/geo_file.zip");
                    System.out.println("Unzipping....");
                    Utilities.unzip(file,new File(Constants.GEO_FILE_PATH));
                    System.out.println("Unzipping done....");
                    //restarting login activity to load geo files according to the newly downloaded JSONs
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        },LoginActivity.this, "Downloading GEO File...");
        String downloadUrlPart = Constants.getBaseUrl(this).substring(0, StringUtils.ordinalIndexOf(Constants.getBaseUrl(this),"/",3));
        //this is the location where the file will be kept after downloading
        String fullFilePath = Constants.GEO_FILE_PATH+"geo_file.zip";
        downloadFile.execute(downloadUrlPart+"/facility_offline_db/"+fileName,fullFilePath);
    }

    private void startDownloadAfterWorks(String result){

        try {

            if(result.equals("Success")){
                SharedPref.setInternalDBSynchronizationFlag(this);
                dbDownloadSuccess = true;
                //get Checksum of the downloaded DB
                String checksumDownloadedDB = Utilities.getMD5Checksum(Environment.getExternalStorageDirectory().toString()+ "/rhis_fwc/databases/Downloaded_DB.zip");
                if(strChecksumSentFromServer.equals(checksumDownloadedDB)){

                    sendDownloadStatus();

                }
                else{
                    Utilities.showAlertToast(this,"ডাটাবেজ ঠিকমত ডাউনলোড হয়নি, অনুগ্রহ করে আবার লগিন করে চেষ্টা করুন");
                }
            }
            else{
                Toast.makeText(this, "Database Download Failed.", Toast.LENGTH_LONG).show();
                System.out.println("Database Download Failed. Error : "+result);
                dbDownloadSuccess = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendDownloadStatus(){
        JSONObject statusJson = new JSONObject();
        try {
            statusJson.put("providerid", provCode);
            statusJson.put("zillaid", zilla);
            statusJson.put("upazilaid", upazilla);
            statusJson.put("downloadstatus", true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AsyncSendingStatusOfDBOperation asyncSendStatus = new AsyncSendingStatusOfDBOperation(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.has("downloadstatus") &&
                            jsonObject.getString("downloadstatus").equals("acknowledged")){
                        //closing the database connection
                        DatabaseWrapper.closeDatabase();
                        //Rename old DB
                        File dbFile = new File(Environment.getExternalStorageDirectory().toString()+
                                "/rhis_fwc/databases/rhis_fwc.sqlite3");
                        renamedFile = new File(Environment.getExternalStorageDirectory().toString()+
                                "/rhis_fwc/databases/rhis_fwc_old.sqlite3");
                        //renaming the old database
                        if(dbFile.exists()){
                            Utilities.renameFile(dbFile,renamedFile);
                        }


                        //Unzip downloaded file
                        File file = new File(Environment.getExternalStorageDirectory().toString()+ "/rhis_fwc/databases/Downloaded_DB.zip");
                        System.out.println("Unzipping....");
                        Utilities.unzip(file,new File(Environment.getExternalStorageDirectory().toString()+"/rhis_fwc/databases"));
                        System.out.println("Unzipping done....");

                        restartDatabase();

                        login(josnLoginResponse);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
        asyncSendStatus.execute(statusJson.toString(), "updownstatus", "info");
    }

    private void restartDatabase(){
        //open the database again
        try {
            new DatabaseWrapper(GlobalActivity.context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateFileToUpload(String fileOrDB){

        AsyncGenerateFileToUpload asyncGenerateFileToUpload = new AsyncGenerateFileToUpload(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                //fileGenerationAfterWorks(result);
                startUploadDBAfterWorks(result);
            }
        },LoginActivity.this);
        asyncGenerateFileToUpload.execute(provCode,zilla,upazilla,dbDownloadSuccess ? "Success" : "Failed",fileOrDB);
    }

    private void startUploadDBAfterWorks(String result){
        if(result.equals("complete")){
            dbUploadSuccess = true;
            login(josnLoginResponse);
        }
        else{
            //generateFileToUpload();
            dbUploadSuccess = false;
        }
    }

    public void singleDataEntryDialog(final Context eContext, String title, int iconId){
        AlertDialog alertDialog = new AlertDialog.Builder(eContext).create();
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        alertDialog.setTitle(title);
        alertDialog.setIcon(iconId);
        final EditText input = new EditText(eContext);
        alertDialog.setView(input);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(input.getText().toString().equals(removeDisability?"emis123":"mamoni@17")){//emis123 for icddrb and mamoni@17 for..
                            if(removeDisability){
                                removeDisability=false;
                                SharedPref.clearProviderData(eContext);
                                getEditText(R.id.providerId).setEnabled(true);
                            }else {
                                if(switchToTest){
                                    switchToTest=false;
                                    //switching database to either test or real
                                    try {
                                        if(!SharedPref.isOnTestMode(eContext)){
                                            SharedPref.setTestModeRequest(eContext,true);
                                            //stopping synchronization service during test mode
                                            if(isMyServiceRunning(SymmetricService.class)) {
                                                stopService(new Intent(eContext, SymmetricService.class));
                                            }
                                            getTextView(R.id.textViewDistrict).setText(getText(R.string.str_training_app_label));
                                            MethodUtils.showSnackBar(findViewById(R.id.layoutLoginMain),"Switch to Test Mode!",true);
                                            new DatabaseWrapper(eContext).switchdatabase(Constants.TEST_DB_PATH+"rhis_fwc.sqlite3");
                                        }else{
                                            SharedPref.setTestModeRequest(eContext,false);
                                            setWorkingDistrict();
                                            MethodUtils.showSnackBar(findViewById(R.id.layoutLoginMain),"Switch to Real Mode!",false);
                                            new DatabaseWrapper(eContext).switchdatabase();
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }else {
                                    permitDBView();
                                }
                            }

                        }else{
                            Utilities.showAlertToast(LoginActivity.this,"Oops! You are not an authorised person!");
                        }
                        MethodUtils.hideSoftKeyboard(LoginActivity.this);
                        dialog.cancel();
                    }
                });
        alertDialog.show();

    }

    private void permitDBView(){
        Intent dbmanager = new Intent(LoginActivity.this, AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

    @Override
    public void onChoiceSelected(DialogInterface dialog, int selection) {
        switch (decisionFlag){
            case DISTRICT_CHOICES:
                userSelection = selection;
                AlertDialogCreator.SimpleDecisionDialog(this,"আপনি "+
                                districtArray[selection]+" জেলায় কাজ করছেন। এ তথ্য ঠিক থাকলে OK চাপুন, অন্যথায় CANCEL চাপুন।","নিশ্চিত হোন",
                        android.R.drawable.ic_dialog_alert);
                break;

            case LANG_CHOICES:
                SharedPref.setLangSelection(this,selection);
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                break;

            case TOOLS_CHOICE:
                showSelectedSetting(selection);

                break;
            default:
                //do nothing.....
                break;
        }

    }

    private void showSelectedSetting(int choice){
        switch (choice){
            case 0:
                decisionFlag=LANG_CHOICES;
                AlertDialogCreator.showSingleChoiceDialog(LoginActivity.this, Flag.langauageArray,"Choose your language");
                break;

            case 1:
                decisionFlag=DISTRICT_CHOICES;
                AlertDialogCreator.showSingleChoiceDialog(LoginActivity.this, districtArray,getString(R.string.select_working_area));
                break;

            default:
                break;
        }
    }

    @Override
    public void onPositiveButtonClicked(DialogInterface dialog) {
        SharedPref.setBaseUrl(this, districtMapping.get(userSelection));

        getTextView(R.id.textViewDistrict).setText(districtArray[userSelection]+" জেলা");
        getTextView(R.id.textViewDistrict).setVisibility(View.VISIBLE);

        //get district code by using user choice district
        districtCode=districtMappingWithCode.get(userSelection);
        //set district code on shared preference
        SharedPref.setDistrictCode(this, districtCode);
        //provider selected working district - set district choice dialog disabled
        SharedPref.setDistrictChoiceDialog(LoginActivity.this, false);

        userSelection=0;
    }

    @Override
    public void onNegativeButtonClicked(DialogInterface dialog) {
        AlertDialogCreator.showSingleChoiceDialog(LoginActivity.this, districtArray,getString(R.string.select_working_area));
        userSelection=0;
    }

    @Override
    public void onNeutralButtonClicked(DialogInterface dialog) {

    }

    private void performInternalDBSynchronization(String satelliteCenter){
        CustomAsynctask customAsynctask = new CustomAsynctask(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                if(result.equals("")){
                    //this will executed in doInBackground
                    if(renamedFile!=null ||
                            //this logic is written to ensure performing the sync process again if
                            //the internal process was closed forcefully before completing in the previous login
                            (new File(Environment.getExternalStorageDirectory().toString()+
                            "/rhis_fwc/databases/rhis_fwc_old.sqlite3")).exists()){
                        while(!MethodUtils.isRegisteredForSymmetricdDS()) {
                            try {
                                Thread.sleep(7000, 500);
                            } catch (InterruptedException ie) {
                                Log.e("reg_checking thread", "Thread Exception: " + ie.getMessage());
                            }
                        }
                        CommonQueryExecution.startDeltaInsertionsInLocalDB(renamedFile!=null?(renamedFile.getAbsolutePath()):
                                (new File(Environment.getExternalStorageDirectory().toString()+
                                        "/rhis_fwc/databases/rhis_fwc_old.sqlite3").getAbsolutePath()));
                    }
                }else{
                    //this will be executed when in onPostExecute
                    //starting the synchronization service again if it has been stopped by any process
                    if(!sym_server.equals("")&& !sym_id.equals("")) {
                        startSynchronizationService(sym_server,sym_id);
                    }
                    //clearing the internal db synchronization flag after performing successful sync
                    SharedPref.clearInternalDBSynchronizationFlag(LoginActivity.this);
                    MethodUtils.showLongTimedToast(LoginActivity.this,"অনুগ্রহ করে আপনার হোমপেজ (ড্যাশবোর্ড) না আসা পর্যন্ত অপেক্ষা করুন");
                    setServiceLocation(satelliteCenter);
                    Intent intent = new Intent(LoginActivity.this, SecondActivity.class);
                    intent.putExtra("satellite", satelliteCenter);
                    SharedPref.setLoginDate(LoginActivity.this);
                    ProviderInfo.getProvider().setSatelliteName(satelliteCenter);

                    //json download/upload/mapping file flag changer to default
                    updateLoginResponse(josnLoginResponse);

                    startActivity(intent);
                }

            }
        },this,getString(R.string.data_sync_in_progress));
        customAsynctask.execute();
    }

    private void initialDistrictSetup() {
        if(SharedPref.isDistrictChoiceRequired(this)){
            decisionFlag=DISTRICT_CHOICES;
            AlertDialogCreator.showSingleChoiceDialog(LoginActivity.this, districtArray,getString(R.string.select_working_area));
        }else{
            if(!SharedPref.isOnTestMode(this)){
                setWorkingDistrict();
            }else{
                getTextView(R.id.textViewDistrict).setText(getText(R.string.str_training_app_label));
                getTextView(R.id.textViewDistrict).setVisibility(View.VISIBLE);
            }
        }
    }

    /*
     * Check district map file exists or not
     * if not - disable login button, do download the file
     * else - load district map file
     * */
    private void checkDistrictMapFile() {
        File file = new File(Constants.DISTRICT_MAP_FILE_PATH + Constants.DISTRICT_MAP_FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            getButton(R.id.loginbtn1).setEnabled(false);
            downloadFile(Constants.DISTRICT_MAP_DOWNLOAD_PATH, Constants.DISTRICT_MAP_FILE_PATH, Constants.DISTRICT_MAP_FILE_NAME);
        } else {
            loadDistrictMapping(Constants.DISTRICT_MAP_FILE_PATH + Constants.DISTRICT_MAP_FILE_NAME);
            initialDistrictSetup();
        }
    }

    //download file
    private void downloadFile(String url, String filePath, String fileName) {
        //check directory exists or not. if not create the directory
        File directory = new File(filePath);
        if (!directory.exists()) directory.mkdirs();

        String fullFilePath = filePath + fileName;

        //delete file before download again
        File file = new File(fullFilePath);
        if (file.exists()) file.delete();

        AsyncFileDownload downloadFile = new AsyncFileDownload(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                //process downloaded files
                if(result.equals("Success")){
                    loadDistrictMapping(fullFilePath);

                    getButton(R.id.loginbtn1).setEnabled(true);

                    //set district choice dialog enabled after every download of the file
                    SharedPref.setDistrictChoiceDialog(LoginActivity.this, true);

                    initialDistrictSetup();
                } else {
                    getButton(R.id.loginbtn1).setEnabled(false);
                    AlertDialogCreator.SimpleMessageDialog(LoginActivity.this, getString(R.string.file_download_error_internet_required), "File Download Error!", android.R.drawable.stat_sys_warning);
                }
            }
        },LoginActivity.this, "Downloading File...");

        //this is the location where the file will be kept after downloading
        downloadFile.execute(url, fullFilePath);
    }

    //district mapping data assigning
    private void loadDistrictMapping(String fullFilePath) {
        File file = new File(fullFilePath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String data = br.readLine();
            br.close();

            data = data.replaceFirst(Constants.BASE64_SALT, "");

            byte[] bytes = Base64.decode(data, Base64.DEFAULT);
            data = new String(bytes, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            districtArray = new String[jsonObject.length()];
            districtMapping.clear();
            districtMappingWithCode.clear();

            for (int i = 0; i < jsonObject.length(); i++) {
                JSONObject object = jsonObject.getJSONObject(String.valueOf(i));
                districtArray[i] = object.getString("district_name");
                districtMapping.put(i, object.getString("district_server"));
                districtMappingWithCode.put(i, Integer.valueOf(object.getString("district_code")));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateLoginResponse(JSONObject json) {
        try {
            String provCode = json.has("ProvCode") ? json.getString("ProvCode") : "";
            if (!provCode.equals("")) {
                json.put("dbdownloadstatus", "0");
                json.put("dbsyncrequest", "0");

                ContentValues updateValues = new ContentValues();
                updateValues.put("lastresponse", json.toString());

                DatabaseWrapper.getDatabase().update("providerdb", updateValues, "provcode = ?", new String[]{provCode});
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}