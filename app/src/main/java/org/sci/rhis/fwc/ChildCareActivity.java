package org.sci.rhis.fwc;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.AsyncChildInfoUpdate;
import org.sci.rhis.model.GeneralPerson;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.DisplayValue;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.Utilities;

import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;

import static org.sci.rhis.utilities.Utilities.ConvertNumberToBangla;


/**
 * Created by arafat.hasan on 3/6/2016.
 */
public class ChildCareActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    /**
     * Variables........................................................................
     */
    public static String SERVLET = "child";
    public static String ROOTKEY = "childInfo";
    public static String LOGTAG = "ChildCare";


    public static int childCountVal = 0;

    public static GeneralPerson patient;
    public static ProviderInfo provider;
    public static boolean onEditMode=false;
    public static boolean onViewMode=false;

    public static String healthId,providerId,mobileNo,sateliteCenterName,clientName;


    private JSONObject jsonResponse = null;

    AsyncChildInfoUpdate asyncChildInfoUpdate;

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;



    private static ChildCareActivity activity;


    public static List<String> infectionClassification;

    public static ViewPager viewPager= null;
    public static JSONObject jsonObject;
    public static boolean isPreview;
    private boolean editMode = false;

    ChildCarePagerAdapter adapter;
    ExpandableListView expListView, lastColored;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    LinearLayout ll;
    TextView textChildAge;
    private LinearLayout history_layout;
    ArrayList<String> list;
    private ArrayList<DisplayValue> displayList;
    private HashMap<String, String> compositMap;

    private LinkedHashMap<String, Pair<String, Integer[]>> historyLabelMap;
    TabLayout tabLayout;
    public String lastChildServiceDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_care);
        activity = this;


        enableDisableEditMode(true);
        onEditMode = false;
        onViewMode=false;



        historyLabelMap = new LinkedHashMap<>();
        history_layout = (LinearLayout)(findViewById(R.id.history_lay_childcare));

        ll = (LinearLayout)findViewById(R.id.llay);

        textChildAge = (TextView) findViewById(R.id.textViewClient_info_tab);
        textChildAge.setText("শিশু সেবা ( "+getString(R.string.ClientsAge)+" "+getIntent().getStringExtra("age")+" )");


        expListView = new ExpandableListView(this);
        ll.addView(expListView);
        history_layout.setVisibility(View.VISIBLE);

        initialInit();
        generateTabLayout();
        //make history collapsible
        MethodUtils.makeHistoryCollapsible(getTextView(R.id.childBlanLabelLabel),history_layout);
    }

    //Initializing
    public void generateTabLayout(){
        viewPager = null;

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ChildCarePagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initialInit(){

        jsonObject = new JSONObject();
        isPreview = false;

        initTabLayout();

        initialize();
        setPredefinedValues();
        loadPreviousRecord(); //if any

    }

    //Initializing total tab
    private void initTabLayout(){
        tabLayout =  (TabLayout) findViewById(R.id.tab_layout);
        //tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Physical Examination"));
        tabLayout.addTab(tabLayout.newTab().setText("Symptoms"));
        tabLayout.addTab(tabLayout.newTab().setText("Preview"));
        tabLayout.addTab(tabLayout.newTab().setText("Classification"));
        tabLayout.addTab(tabLayout.newTab().setText("Management"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void setPredefinedValues(){
        patient = getIntent().getParcelableExtra("Patient");
        provider = getIntent().getParcelableExtra(Constants.KEY_PROVIDER);
        providerId = String.valueOf(provider.getProviderCode());

        healthId  = String.valueOf(patient.getHealthId());
        mobileNo = String.valueOf(patient.getMobile());
        sateliteCenterName = provider.getSatelliteName();

        try {
            jsonObject.put("age",patient.getAge());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static JSONObject getJSONObject(){
        return jsonObject;
    }

    private void loadPreviousRecord(){
        JSONObject json;
        try {
            json = buildQueryHeader(true);
            asyncChildInfoUpdate = new AsyncChildInfoUpdate(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    handleResponse(result);
                }
            },this);
            asyncChildInfoUpdate.execute(json.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e("Child JSON Exception: ", jse.getMessage());
        }
    }




    @Override
    public void callbackAsyncTask(String result) {
        try {
            Log.d(LOGTAG, "CHILD-ANDROID response received:\n\t" + result);
            jsonResponse = new JSONObject(result);
            showSerialNumber(jsonResponse);

            if(jsonResponse.has("childRetrieve")){
                if(jsonResponse.getInt("childRetrieve")==1){
                    childCountVal = jsonResponse.getInt("serviceId");
                }
            }else{
                if(jsonResponse.has("childInsertSuccess")){
                    if(jsonResponse.getInt("childInsertSuccess")==1){
                        childCountVal = jsonResponse.getInt("serviceId");
                    }
                }else if(jsonResponse.has("childUpdateSuccess")) {
                    if(jsonResponse.getInt("childUpdateSuccess")==1){
                        childCountVal = jsonResponse.getInt("serviceId");
                    }
                }
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        }

    }



    @Override
    protected void initiateCheckboxes() {
    }

    @Override
    protected void initiateEditTexts() {
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

    }


    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.modifyButton:
                if(!onEditMode) {
                    enableDisableEditMode(true);
                    onEditMode = true;
                    onViewMode = false;
                    generateTabLayout();
                    getButton(R.id.modifyButton).setText("Cancel");
                    getButton(R.id.buttonDeleteChild).setVisibility(View.GONE);
                }
                else {
                    getButton(R.id.modifyButton).setVisibility(View.GONE);
                    getButton(R.id.buttonDeleteChild).setVisibility(View.VISIBLE);
                    if(lastColored != null)
                    {
                        lastColored.setBackgroundColor(Color.TRANSPARENT);
                        lastColored.invalidate();
                    }
                    resetChildFragments();
                }
                break;

            case R.id.buttonDeleteChild:
                deleteConfirmed();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d(LOGTAG,String.valueOf(parent.getId()));
        switch (parent.getId()) {
            default:
                Log.e(LOGTAG, "Unknown spinner: " + parent.getId()
                        + " -> " + getResources().getResourceEntryName(parent.getId()));

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }




    /**
     * used to save the child service record in database
     * */
    public  void childSaveAsJson(){

        JSONObject json;
        try {
            json = saveHeader(false,jsonObject);


            json.put("service_source", "");

            Log.d(LOGTAG, "Child Register Mode: "+ editMode+ ", Save Json " + ROOTKEY + ":{" + json.toString() + "}");


            asyncChildInfoUpdate = new AsyncChildInfoUpdate(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    handleResponse(result);
                }
            },this);
            asyncChildInfoUpdate.execute(json.toString(), SERVLET, ROOTKEY);
            resetChildFragments();


        } catch (JSONException jse) {
            Log.e(LOGTAG, "Child JSON Exception: " + jse.getMessage());
        }
    }

    /**
     * used to reset the fragments
     * */
    private  void resetChildFragments()
    {
        MethodUtils.clearJSONObject(jsonObject);
        try {
            jsonObject.put("age", patient.getAge());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        generateTabLayout();

    }



    public static JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {

        String queryString = "{" +
                "healthId:" + patient.getHealthId() + "," +
                (isRetrieval ? "" : "provider_id:" + providerId + ",") +
                "childLoad:" + (isRetrieval ? "retrieve," : onEditMode ? "update," : "insert,") +
                "serviceId:" + childCountVal+
                "}";
        return new JSONObject(queryString);
    }

    public static JSONObject saveHeader(boolean isRetrieval, JSONObject json) throws JSONException {

        json.put("healthId",patient.getHealthId());
        json.put("providerId", isRetrieval ? "" : providerId );
        json.put("childLoad" , (isRetrieval ? "retrieve" : onEditMode ? "update" : "insert"));
        json.put("serviceId", childCountVal);
        json.put("satelitecentername",sateliteCenterName==null?"":sateliteCenterName);
        json.put("mobileNo",mobileNo);

        return json;
    }

    private void showSerialNumber(JSONObject json) {
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
                getTextView(R.id.textViewChildRegisterRegNo).setText(Utilities.ConvertNumberToBangla(regSerial));
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error coverting registration number");
            Utilities.printTrace(jse.getStackTrace());
        } catch (ParseException pe) {
            Log.e(LOGTAG, "Parsing Error coverting registration date");
            Utilities.printTrace(pe.getStackTrace());
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialogWithResult(this,getResources().getString(R.string.childService),RESULT_OK,ActivityResultCodes.FP_ACTIVITY);
    }




    private void enableDisableEditMode(boolean status){
        ChildCareFragmentPhysicalExamination.editMode = status;
        ChildCareFragmentSymptom.editMode = status;
        ChildCareFragmentManagement.editMode = status;
        ChildCareFragmentClassification.editMode = status;
        onEditMode=status;
    }


    public void handleResponse(String result) {
        ll.removeAllViews(); //clear the history list first.

        Log.d(LOGTAG, "Child-ANDROID response received:\n\t" + result);

        try {
            JSONObject jsonStr = new JSONObject(result);

            int lastChildServiceIndex = (jsonStr.has("count") ? jsonStr.getInt("count") : 0 );
            Log.d(LOGTAG, "JSON Response:\n"+jsonStr.toString());

            int i=1;

            //TODO: Has to change the bad code (for working iterator)
            jsonStr.remove("count");
            if(jsonStr.has("regSerialNo")) jsonStr.remove("regSerialNo");
            if(jsonStr.has("regDate")) jsonStr.remove("regDate");
            for(Iterator<String> ii = jsonStr.keys();ii.hasNext();)
            {
                String key=ii.next();
                if(i > lastChildServiceIndex)//jsonStr.getInt("count"))
                    break;

                if(i==lastChildServiceIndex){
                    lastChildServiceDate=key;
                    Utilities.SetVisibility(this, R.id.buttonDeleteChild, isLastChildDeletable(jsonStr) ? View.VISIBLE :View.INVISIBLE);
                }

                JSONObject singleVisitJson = jsonStr.getJSONObject(key);

                list = new ArrayList<>();
                displayList = new ArrayList();
                compositMap = new HashMap<>();

                Log.d(LOGTAG, String.format("Visit: %d, Size after adding [MIV] to the list is %d", i, displayList.size()));

                listDataHeader = new ArrayList<>();
                listDataChild = new HashMap<>();

                HashMap<String, List<DisplayValue>> listDisplayValues = new HashMap();

                listDataHeader.add("সেবাগ্রহণের তারিখঃ "+
                        Utilities.ConvertNumberToBangla(Utilities.getDateStringUIFormat(singleVisitJson.getString("visitDate"))));
                listDisplayValues.put(listDataHeader.get(0), displayList);

                // Adding child data
                List<String> listData = new ArrayList<String>();
                listData.add("লক্ষণ শুরুর তারিখঃ "+(ConvertNumberToBangla(Utilities.getDateStringUIFormat(singleVisitJson.getString("indicationStartDate")))));
                listData.add(Utilities.getClassificationDetailsFromID(singleVisitJson.getString("classifications")));
                listData.add("বিস্তারিত দেখতে ক্লিক করুন। ");
                listData.add("তথ্য পরিবর্তন/ফলোআপ দিতে ক্লিক করুন। ");

                final HashMap<String, List<String>> listDataChild = new HashMap<>();
                listDataChild.put(listDataHeader.get(0), listData); // Header, Child data
                final ExpandableListAdapterForChild listAdapter = new ExpandableListAdapterForChild(this, listDataHeader, listDataChild);

                initPage();

                expListView.setScrollingCacheEnabled(true);
                expListView.setAdapter(listAdapter);

                expListView.isClickable();

                // Listview on child click listener
                expListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                    // TODO Auto-generated method stub
                    final String childText = (String) listAdapter.getChild(groupPosition, childPosition);
                    String str = null;
                    str = childText;

                    if(childPosition == 1 && str.length()>=1) {
                        AlertMessage.showMessage(activity, "শ্রেণী বিভাগ", childText);
                    }else if(childPosition == 2) {

                        enableDisableEditMode(false);
                        onViewMode = true;
                        setPreviousData(parent,singleVisitJson);

                    }else if(childPosition == 3) {
                        enableDisableEditMode(true);
                        onEditMode = true;
                        onViewMode = false;
                        setPreviousData(parent,singleVisitJson);
                        getButton(R.id.modifyButton).setVisibility(View.VISIBLE);
                        getButton(R.id.modifyButton).setText("Cancel");
                        getButton(R.id.buttonDeleteChild).setVisibility(View.GONE);


                    }

                    return false;
                });

                ll.addView(expListView);

                //keep history visible and its child expandable
                for(int j=0; j < listAdapter.getGroupCount(); j++){
                    expListView.expandGroup(j);
                }
                if(history_layout.getVisibility()==View.GONE){
                    history_layout.setVisibility(View.VISIBLE);
                }
                //...........................................................................

                ll.invalidate();
                i++;


            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        } catch (Exception e) {
            Utilities.printTrace(e.getStackTrace(), 10);
        }
    }

    private void setPreviousData(ExpandableListView parent, JSONObject singleVisitJson){

        lastColored = parent;
        parent.setBackgroundColor(Color.LTGRAY);

        ChildCareActivity.jsonObject = singleVisitJson;
        setAge();

        isPreview = true;

        generateTabLayout();
    }


    private void setAge(){
        try {
            Date currentDate = Calendar.getInstance().getTime();

            String dateStr = jsonObject.getString("systemEntryDate");
            CustomSimpleDateFormat curFormater = new CustomSimpleDateFormat("yyyy-MM-dd");
            Date systemEntryDate = curFormater.parse(dateStr);

            int dayDiff = Integer.valueOf(String.valueOf(Utilities.getDateDiff(currentDate, systemEntryDate, TimeUnit.DAYS)));

            jsonObject.put("age", patient.getAge() + dayDiff);
        }catch (JSONException ex){
            ex.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initPage() {
        expListView = new ExpandableListView(this);
        expListView.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        expListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView.setIndicatorBounds(0, 0);
        expListView.setChildIndicatorBounds(0, 0);
        expListView.setStackFromBottom(true);
    }

    private boolean isLastChildDeletable(JSONObject jso) {

        JSONObject lastVisit = null;
        String providerCode = null;
        boolean thresholdPeriodPassed = false;
        try {
            lastVisit = jso.getJSONObject(lastChildServiceDate);
            if(lastVisit.has("systemEntryDate") && !lastVisit.getString("systemEntryDate").equals("")){
                Date lastVisitEntryDate = Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE,lastVisit.getString("systemEntryDate"));
                thresholdPeriodPassed = Utilities.getDateDiff(lastVisitEntryDate,new Date(), TimeUnit.DAYS)> Flag.UPDATE_THRESHOLD;
            }

            providerCode = lastVisit.getString("providerId");

            return (provider.getProviderCode().equals(providerCode) && !thresholdPeriodPassed);

        } catch (JSONException jse) {
            Log.e(LOGTAG, String.format(" JSON Error (Childcare History): %s", jse.getMessage()));
            Utilities.printTrace(jse.getStackTrace(), 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void deleteConfirmed() {
        try {

            JSONObject deleteJson = buildQueryHeader(false);
            deleteJson.put("childLoad", "delete");
            deleteJson.put("systemEntryDate",lastChildServiceDate);
            asyncChildInfoUpdate = new AsyncChildInfoUpdate(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    handleResponse(result);
                }
            },this);
            asyncChildInfoUpdate.execute(deleteJson.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete Child Register request");
            Utilities.printTrace(jse.getStackTrace());
        }
        MethodUtils.showSnackBar(findViewById(R.id.main_layout_child),"তথ্য সফলভাবে ডিলিট করা হয়েছে!",false);

        resetChildFragments();
    }


}
