package org.sci.rhis.fwc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.AsyncPNCInfoUpdate;
import org.sci.rhis.model.PregWoman;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomFocusChangeListener;
import org.sci.rhis.utilities.CustomTextWatcher;
import org.sci.rhis.utilities.DisplayValue;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.HistoryListMaker;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PNCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    final static int FIRST_PNC_1 = 0; //DAYS
    final static int FIRST_PNC_2 = 1; //DAYS
    final static int SECOND_PNC_1 = 2; //DAYS
    final static int SECOND_PNC_2 = 3; //DAYS
    final static int THIRD_PNC_1 = 7; //DAYS
    final static int THIRD_PNC_2 = 14; //DAYS
    final static int FOURTH_PNC_1 = 35; //DAYS
    final static int FOURTH_PNC_2 = 42; //DAYS

    private MultiSelectionSpinner multiSelectionSpinner;
    // For Date pick added by Al Amin
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    private JSONObject lastServiceJSON;
    private JSONObject lastServiceJSONChild;

    ExpandableListView expListView;
    ExpandableListView expListView_child;
    List<String> listDataHeader;

    LinearLayout ll, ll_pnc_child, lay_frag_mother, pnclay_child, pnclay_mother, lay_frag_child, historyLayout;
    private int selected_child = 1;
    private String child_result = "";

    private boolean editMode = false, isChild = false;
    private PregWoman mother;
    private ProviderInfo provider;
    AsyncPNCInfoUpdate pncInfoUpdateTask;

    final private String SERVLET_MOTHER = "pncmother";
    final private String ROOTKEY_MOTHER = "PNCMotherInfo";
    final private String SERVLET_CHILD = "pncchild";
    final private String ROOTKEY_CHILD = "PNCChildInfo";
    private final String LOGTAG = "FWC-PNC";

    ArrayList<HashMap<String, String>> contactList;

    private View mPNCLayout;
    Boolean flag = false, mother_flag = false, child_flag = false, child_tree = true;

    private Button pnc_mother;
    private Context con;

    private ArrayAdapter<String> childAdapter;
    private ArrayList<String> childList;

    int lastPncVisit = 0, serviceId = 0, lastVisit = 0;
    private int pncSaveClick = 0;

    private JSONObject jsonRespChild = null;
    private JSONObject jsonRespMother = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnc);
        con = this;

        mPNCLayout = findViewById(R.id.pncScroll);
        // Find our buttons
        Button visibleButton = (Button) findViewById(R.id.pncLabel);

        View.OnClickListener mVisibleListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (flag == false) {
                    mPNCLayout.setVisibility(View.VISIBLE);
                    flag = true;
                } else {
                    mPNCLayout.setVisibility(View.INVISIBLE);
                    flag = false;
                }
            }
        };

        mother = getIntent().getParcelableExtra("PregWoman");
        provider = getIntent().getParcelableExtra("Provider");

        //pncvisit
        lastPncVisit = 0;

        pnc_mother = (Button) findViewById(R.id.pncmother);

        pnc_mother.setOnClickListener(this);

        //Radio button listener
        getRadioGroup(R.id.pncMotherChildSelector).setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        handleRadioButton(group, checkedId);
                    }
                });

        child_tree = true;
        childList = new ArrayList<>(); //childList

        pnclay_child = (LinearLayout) findViewById(R.id.pncChildInfo);
        pnclay_mother = (LinearLayout) findViewById(R.id.pncMotherInfo);

        lay_frag_mother = (LinearLayout) findViewById(R.id.pnc_mother_frag);
        lay_frag_child = (LinearLayout) findViewById(R.id.pnc_child_frag);

        lay_frag_child.setVisibility(View.GONE);
        pnclay_child.setVisibility(View.GONE);


        // Wire each button to a click listener
        visibleButton.setOnClickListener(mVisibleListener);

        // Generic works common to all the clinical service sub classes
        initialize(); //super class
        Spinner[] spinners = new Spinner[6];
        spinners[0] = (Spinner) findViewById(R.id.pncBreastConditionSpinner);
        spinners[1] = (Spinner) findViewById(R.id.pncDischargeBleedingSpinner);
        spinners[2] = (Spinner) findViewById(R.id.pncPerineumSpinner);
        spinners[3] = (Spinner) findViewById(R.id.pncFamilyPlanningMethodsSpinner);
        spinners[4] = (Spinner) findViewById(R.id.pncReferCenterNameSpinner);
        spinners[5] = (Spinner) findViewById(R.id.pncAnemiaSpinner);

        // Multi Select Spinner Initialisation
        final List<String> pncmdangersignlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Danger_Sign_DropDown));
        final List<String> pnccdangersignlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Danger_Sign_DropDown));
        final List<String> pncmdrawbacklist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Drawback_DropDown));
        final List<String> pnccdrawbackblist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Drawback_DropDown));
        final List<String> pncmdiseaselist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Disease_DropDown));
        final List<String> pnccdiseaselist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Disease_DropDown));
        final List<String> pncmtreatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> pncctreatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> pncmadvicelist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Advice_DropDown));
        final List<String> pnccadvicelist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Advice_DropDown));
        final List<String> pncmreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Refer_Reason_DropDown));
        final List<String> pnccreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Refer_Reason_DropDown));

        HashMap<Integer, List<String>> multiSpinnerList = new HashMap<Integer, List<String>>();

        multiSpinnerList.put(R.id.pncDangerSignsSpinner, pncmdangersignlist); // m - danger
        multiSpinnerList.put(R.id.pncChildDangerSignsSpinner, pnccdangersignlist); //c - danger
        multiSpinnerList.put(R.id.pncDrawbackSpinner, pncmdrawbacklist); //c - danger
        multiSpinnerList.put(R.id.pncChildDrawbackSpinner, pnccdrawbackblist); //c - danger
        multiSpinnerList.put(R.id.pncDiseaseSpinner, pncmdiseaselist); //c - danger
        multiSpinnerList.put(R.id.pncChildDiseaseSpinner, pnccdiseaselist); //c - danger
        multiSpinnerList.put(R.id.pncTreatmentSpinner, pncmtreatmentlist); //c - danger
        multiSpinnerList.put(R.id.pncChildTreatmentSpinner, pncctreatmentlist); //c - danger
        multiSpinnerList.put(R.id.pncAdviceSpinner, pncmadvicelist); //c - danger
        multiSpinnerList.put(R.id.pncChildAdviceSpinner, pnccadvicelist); //c - danger
        multiSpinnerList.put(R.id.pncReasonSpinner, pncmreferreasonlist); //c - danger
        multiSpinnerList.put(R.id.pncChildReasonSpinner, pnccreferreasonlist); //c - danger

        for (int key : multiSpinnerList.keySet()) {
            multiSelectionSpinner = (MultiSelectionSpinner) findViewById(key);
            multiSelectionSpinner.setItems(multiSpinnerList.get(key));
            multiSelectionSpinner.setSelection(new int[]{});
        }
        ll = (LinearLayout) findViewById(R.id.llay);
        ll_pnc_child = (LinearLayout) findViewById(R.id.llay_frag);

        contactList = new ArrayList<HashMap<String, String>>();

        expListView = new ExpandableListView(this);
        expListView_child = new ExpandableListView(this);

        ll.addView(expListView);
        ll_pnc_child.addView(expListView_child);

        getEditText(R.id.pncServiceDateValue).setOnClickListener(this);
        getEditText(R.id.pncChildServiceDateValue).setOnClickListener(this);
        getCheckbox(R.id.pncReferCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.pncChildReferCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.pncOthersCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.pncChildOthersCheckBox).setOnCheckedChangeListener(this);

        //custom date picker Added By Al Amin
        datePickerDialog = new CustomDatePickerDialog(this);
        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.Date_Picker_Button, (EditText) findViewById(R.id.pncServiceDateValue));
        datePickerPair.put(R.id.Date_Picker_Button_Child, (EditText) findViewById(R.id.pncChildServiceDateValue));

        if (mother.isDead() == false) {
            getRadioButton(R.id.pncMotherSelector).setChecked(true); //select mother by default
        } else {
            getRadioButton(R.id.pncMotherSelector).setChecked(true);
            pnclay_mother.setVisibility(View.INVISIBLE);
        }

        setHistoryLabelMapping();
        setHistoryLabelMappingChild();
        setCompositeMap("pncbpsys", "pncbpdias");

        //SendPostRequestAsyncTask
        AsyncPNCInfoUpdate sendPostReqAsyncTask = new AsyncPNCInfoUpdate(PNCActivity.this, this);

        String queryString = "{" +
                "pregno:" + mother.getPregNo() + "," +
                "healthid:" + mother.getHealthId() + "," +
                "pncMLoad:" + "retrieve" +
                "}";

        String servlet = "pncmother";
        String jsonRootkey = "PNCMotherInfo";
        Log.d(LOGTAG, "Mother Part:\n" + queryString);
        sendPostReqAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, queryString, servlet, jsonRootkey);

        AsyncPNCInfoUpdate sendPostReqAsyncTaskChild = new AsyncPNCInfoUpdate(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                child_result = result;

                try {
                    JSONObject jsonStr = new JSONObject(result);
                    populateChild(jsonStr);
                } catch (JSONException jse) {
                    System.out.println("JSON Exception Thrown::\n ");
                    jse.printStackTrace();
                }
            }
        }, this);

        String queryString_child = "{" +
                "pregno:" + mother.getPregNo() + "," +
                "healthid:" + mother.getHealthId() + "," +
                "pncCLoad:" + "retrieve" +
                "}";

        String servlet_child = "pncchild";
        String jsonRootkey_child = "PNCChildInfo";
        Log.d(LOGTAG, "Child Part:\n" + queryString_child);
        sendPostReqAsyncTaskChild.execute(queryString_child, servlet_child, jsonRootkey_child);

        Log.d(LOGTAG, "---=====>" + queryString);

        historyLayout = (LinearLayout)findViewById(R.id.PNCHistoryLayout);
        MethodUtils.makeHistoryCollapsible(getTextView(R.id.PNCHistoryLabel), historyLayout);
    }

    private void handleRadioButton(RadioGroup group, int checkedId) {
        group.getCheckedRadioButtonId();
        if (checkedId == R.id.pncMotherSelector) {
            if (mother.isDead()) {
                enableDeadMotherLayout();
                Utilities.MakeInvisible(this, R.id.id_pncChildListDropdown);
            } else {
                enableMotherLayout();
                Utilities.MakeInvisible(this, R.id.id_pncChildListDropdown);
                showHidePncModifyButton(jsonRespMother, true);
            }

        } else if (checkedId == R.id.pncChildSelector) {
            Utilities.SetVisibility(this, R.id.modifyLastPncButton, View.INVISIBLE); //always first set it to invisible until a child is selected
            Utilities.MakeVisible(this, R.id.id_pncChildListDropdown);
            lay_frag_mother.setVisibility(View.INVISIBLE);
            pnclay_mother.setVisibility(View.INVISIBLE);
        }
    }

    private void setPncVisitAdvices() {
        Date deliveryDate = mother.getActualDelivery();
        //custom date picker to start from delivery date if mother has actual delivery date
        if (deliveryDate != null) {
            datePickerDialog = null;
            datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH, deliveryDate);
        }
        CustomSimpleDateFormat sdf = new CustomSimpleDateFormat("ddMMMyy");
        getTextView(R.id.pncVisit1Date).setText(sdf.format(Utilities.addDateOffset(deliveryDate, FIRST_PNC_1)) + " - " + sdf.format(Utilities.addDateOffset(deliveryDate, FIRST_PNC_2)));
        getTextView(R.id.pncVisit2Date).setText(sdf.format(Utilities.addDateOffset(deliveryDate, SECOND_PNC_1)) + " - " + sdf.format(Utilities.addDateOffset(deliveryDate, SECOND_PNC_2)));
        getTextView(R.id.pncVisit3Date).setText(sdf.format(Utilities.addDateOffset(deliveryDate, THIRD_PNC_1)) + " - " + sdf.format(Utilities.addDateOffset(deliveryDate, THIRD_PNC_2)));
        getTextView(R.id.pncVisit4Date).setText(sdf.format(Utilities.addDateOffset(deliveryDate, FOURTH_PNC_1)) + " - " + sdf.format(Utilities.addDateOffset(deliveryDate, FOURTH_PNC_2)));
    }

    public void pickDate(View view) {
        try {
            datePickerDialog.show(datePickerPair.get(view.getId()));
        } catch (NullPointerException NPE) {
            Log.e(LOGTAG, "Caught NPE fro custom date picker");
            NPE.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pnc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callbackAsyncTask(String result) {
        ll.removeAllViews();
        Log.d(LOGTAG, "Handle Mother:\n" + result);

        try {
            jsonRespMother = new JSONObject(result);

            lastPncVisit = jsonRespMother.getInt("count");
            getTextView(R.id.pncVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPncVisit + 1))); //next visit

            //Check if eligible for new PNC
            if (jsonRespMother.has("pncStatus") && jsonRespMother.getBoolean("pncStatus")) {
                Utilities.MakeInvisible(this, R.id.pncMotherInfo);
                Toast.makeText(this, "Mother is not eligible for new PNC", Toast.LENGTH_LONG).show();
            } else {
                //get outcome date and populate ideal pnc visit info
                mother.setActualDelivery(jsonRespMother.getString("outcomeDate"), "yyyy-MM-dd");
                setPncVisitAdvices();
                if (lastPncVisit > 0) {
                    isChild = false;
                    showHidePncModifyButton(jsonRespMother, !isChild);
                    lastServiceJSON = null;
                    lastServiceJSON = jsonRespMother.getJSONObject(String.valueOf(lastPncVisit));
                } else {
                    //hide modify button if all the history is cleared by the provider
                    Utilities.MakeInvisible(this, R.id.modifyLastPncButton);
                }
                showHidePncModifyButton(jsonRespMother, true);
            }

            /*int in = 1;

            for (Iterator<String> ii = jsonRespMother.keys(); ii.hasNext(); ) {
                String key = ii.next();


                System.out.println("1.Key:" + key + " Value:\'" + jsonRespMother.get(key) + "\'");

                //if(in == item-3)
                if (in > jsonRespMother.getInt("count"))
                    break;
                //It's just json and not so hard to understand, keep getting exception at this point

                JSONObject jsonRootObject = jsonRespMother.getJSONObject("" + in);
                Log.d("--:::>", "---serviceSource=====>" + jsonRootObject.getString("serviceSource"));

                String complicationsign = jsonRootObject.getString("complicationsign");
                String serviceSource = jsonRootObject.getString("serviceSource");
                String anemia = jsonRootObject.getString("anemia");
                String referCenterName = jsonRootObject.getString("referCenterName");
                String treatment = jsonRootObject.getString("treatment");
                String perineum = jsonRootObject.getString("perineum");
                String uterusInvolution = jsonRootObject.getString("uterusInvolution");
                String visitDate = jsonRootObject.getString("visitDate");
                String bpDiastolic = jsonRootObject.getString("bpDiastolic");
                String disease = jsonRootObject.getString("disease");
                String bpSystolic = jsonRootObject.getString("bpSystolic");
                String hematuria = jsonRootObject.getString("hematuria");
                String temperature = jsonRootObject.getString("temperature");
                String referReason = jsonRootObject.getString("referReason");
                String refer = jsonRootObject.getString("refer");
                String edema = jsonRootObject.getString("edema");
                String serviceID = jsonRootObject.getString("serviceID");
                String hemoglobin = jsonRootObject.getString("hemoglobin");
                String FPMethod = jsonRootObject.getString("FPMethod");
                String breastCondition = jsonRootObject.getString("breastCondition");
                String advice = jsonRootObject.getString("advice");
                String symptom = jsonRootObject.getString("symptom");


                ArrayList<String> list = new ArrayList<String>();

                String[] details;
                Resources res1 = con.getResources();
                String str1 = "";

                list.add("" + getString(R.string.visitDate) + " " + visitDate);
                list.add("" + getString(R.string.complicationsign) + " " + symptom);
                list.add("" + getString(R.string.temperature) + " " + temperature);
                list.add("" + getString(R.string.bpSystolic) + " " + bpSystolic + "/" + bpDiastolic);

                // for anemia value
                str1 = "";
                str1 = anemia;
                Log.d("--:::>", "---complicationsign=====>" + str1);
                String[] animals = str1.split(" ");
                String temp = "";
                details = res1.getStringArray(R.array.Anemia_Dropdown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if (animal.length() > 0)
                        temp = temp + " " + details[Integer.parseInt(animal)];
                }
                list.add("" + getString(R.string.anemia) + " " + temp);


                list.add("" + getString(R.string.hemoglobin) + " " + hemoglobin + "%");


                // for edema value
                str1 = "";
                str1 = edema;

                animals = str1.split(" ");
                temp = "";
                details = res1.getStringArray(R.array.Jaundice_Edima_Dropdown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if (animal.length() > 0)
                        temp = temp + " " + details[Integer.parseInt(animal)];
                }
                list.add("" + getString(R.string.edema) + " " + temp.trim());

                // for breastCondition value
                str1 = "";
                str1 = breastCondition;

                animals = str1.split(" ");
                temp = "";
                details = res1.getStringArray(R.array.Breast_Condition_DropDown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if (animal.length() > 0)
                        temp = temp + " " + details[Integer.parseInt(animal)];
                }

                list.add("" + getString(R.string.breastCondition) + " " + temp);

                // for hematuria value
                str1 = "";
                str1 = uterusInvolution;

                animals = str1.split(" ");
                temp = "";
                details = res1.getStringArray(R.array.Cervix_Involution_DropDown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if (animal.length() > 0)
                        temp = temp + " " + details[Integer.parseInt(animal)];
                }

                list.add("" + getString(R.string.uterusInvolution) + " " + temp);


                // for hematuria value
                str1 = "";
                str1 = hematuria;

                animals = str1.split(" ");
                temp = "";
                details = res1.getStringArray(R.array.Discharge_Bleeding_DropDown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if (animal.length() > 0)
                        temp = temp + " " + details[Integer.parseInt(animal)];
                }

                list.add("" + getString(R.string.hematuria) + " " + temp);


                // for perineum value
                str1 = "";
                str1 = perineum;


                animals = str1.split(" ");
                temp = "";
                details = res1.getStringArray(R.array.Perineum_DropDown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if (animal.length() > 0)
                        temp = temp + " " + details[Integer.parseInt(animal)];
                }


                list.add("" + getString(R.string.perineum) + " " + temp);

                // for Family_Planning value
                str1 = "";
                str1 = FPMethod;

                animals = str1.split(" ");
                temp = "";
                details = res1.getStringArray(R.array.Family_Planning_Methods_DropDown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if (animal.length() > 0)
                        temp = temp + " " + details[Integer.parseInt(animal)];
                }

                list.add("" + getString(R.string.family_planning_methods) + " " + temp);
                list.add("" + getString(R.string.complication) + " " + complicationsign);
                list.add("" + getString(R.string.disease) + " " + disease);
                list.add("" + getString(R.string.treatment) + " " + treatment);
                list.add("" + getString(R.string.advice) + " " + advice);
                if (!refer.equals("") && Integer.parseInt(refer) == 1) {
                    list.add("" + getString(R.string.refer) + " " + "Yes");
                } else if (!refer.equals("") && Integer.parseInt(refer) == 2)
                    list.add("" + getString(R.string.refer) + " " + "No");
                else
                    list.add("" + getString(R.string.refer) + " " + "No");

                list.add("" + getString(R.string.referCenterName) + " " + referCenterName);
                list.add("" + getString(R.string.referReason) + " " + referReason);


                try {
                    listDataHeader = new ArrayList<String>();
                    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();

                    listDataHeader.add("পরিদর্শন " + Utilities.ConvertNumberToBangla(String.valueOf(in)) + ":");
                    listDataChild.put(listDataHeader.get(0), list);

                    ExpandableListAdapterforPNC listAdapter = new ExpandableListAdapterforPNC(this, listDataHeader, listDataChild);

                    in++;

                    initPage();

                    ll.addView(expListView);
                    expListView.setScrollingCacheEnabled(true);
                    expListView.setAdapter(listAdapter);
                    ll.invalidate();

                } catch (Exception e) {
                    Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                    e.printStackTrace();
                }
            }*/

            for (int i = 1; i <= lastPncVisit && lastPncVisit != 0 ; i++) {
                JSONObject singleVisitJson = jsonRespMother.getJSONObject(String.valueOf(i));
                prepareMotherJSONForDifferentKey(singleVisitJson);

                displayList = new ArrayList<>();

                HistoryListMaker<PNCActivity> historyListMaker = new HistoryListMaker<>(
                        this, //activity
                        singleVisitJson, //json containing keys
                        historyLabelMap, //history details
                        compositeMap //fields whose values are given against multiple keys
                );

                try {
                    displayList.addAll(historyListMaker.getDisplayList());
                } catch(Exception e) {
                    Log.e(LOGTAG, String.format("ERROR: %s" , e.getMessage()));
                    Utilities.printTrace(e.getStackTrace(), 10);
                }

                Log.d(LOGTAG, String.format("Visit: %d, Size after adding [MIV] to the list is %d", i, displayList.size()));

                listDataHeader = new ArrayList<>();
                listDataHeader.add(getString(R.string.visit) + Utilities.ConvertNumberToBangla(String.valueOf(i)));

                HashMap<String, List<DisplayValue>> listDisplayValues = new HashMap<>();
                listDisplayValues.put(listDataHeader.get(0), displayList);

                ExpandableDisplayListAdapter displayListAdapter = new ExpandableDisplayListAdapter(this, listDataHeader, listDisplayValues, true);

                initPage();

                ll.addView(expListView);
                expListView.setScrollingCacheEnabled(true);
                expListView.setAdapter(displayListAdapter);

                //keep history visible and its child expandable
                for(int j=0; j < displayListAdapter.getGroupCount(); j++){
                    expListView.expandGroup(j);
                }

                if(historyLayout.getVisibility()==View.GONE){
                    historyLayout.setVisibility(View.VISIBLE);
                    getTextView(R.id.PNCHistoryLabel).
                            setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                }

                ll.invalidate();
            }
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        } catch (Exception e) {
            Utilities.printTrace(e.getStackTrace(), 10);
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

    private void initPage_child() {
        expListView_child = new ExpandableListView(this);
        expListView_child.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        expListView_child.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView_child.setIndicatorBounds(0, 0);
        expListView_child.setChildIndicatorBounds(0, 0);
        expListView_child.setStackFromBottom(true);
    }

    private void pncMotherSaveToJson() {
        pncInfoUpdateTask = new AsyncPNCInfoUpdate(this, this);
        JSONObject json;
        try {
            json = buildQueryHeaderMother(false);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            //............
            String sateliteCenter = provider.getSatelliteName();
            json.put("sateliteCenterName", sateliteCenter == null ? "" : sateliteCenter);
            if (editMode) {
                json.put("serviceId", serviceId);
            }
            //..........
            pncInfoUpdateTask.execute(json.toString(), SERVLET_MOTHER, ROOTKEY_MOTHER);

            Utilities.Reset(this, R.id.pncMotherInfo);
            cancelMotherUpdating(true);

        } catch (JSONException jse) {
            Log.e("PNCM JSON Exception: ", jse.getMessage());
        }

    }

    private JSONObject buildQueryHeaderMother(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString = "{" +
                "healthid:" + mother.getHealthId() + "," +
                (isRetrieval ? "" : "providerid:\"" + String.valueOf(provider.getProviderCode()) + "\",") +
                "pregno:" + mother.getPregNo() + "," +
                "pncMLoad:" + (isRetrieval ? "retrieve" : (editMode ? "update" : "\"\"")) +
                "}";

        return new JSONObject(queryString);
    }

    private void pncChildSaveToJson() {

        pncInfoUpdateTask = new AsyncPNCInfoUpdate(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                handleChild(result);
            }
        }, this);
        JSONObject json;
        try {
            json = buildQueryHeaderChild(false);
            Utilities.getCheckboxes(jsonCheckboxMapChild, json);
            Utilities.getEditTexts(jsonEditTextMapChild, json);
            Utilities.getEditTextDates(jsonEditTextDateMapChild, json);
            Utilities.getSpinners(jsonSpinnerMapChild, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMapChild, json);
            getSpeciaTextViews(jsonTextViewMapChild, json);
            //............
            String sateliteCenter = provider.getSatelliteName();
            json.put("sateliteCenterName", sateliteCenter == null ? "" : sateliteCenter);
            if (editMode) {
                json.put("serviceId", serviceId);
            }
            //..........
            pncInfoUpdateTask.execute(json.toString(), SERVLET_CHILD, ROOTKEY_CHILD);

            Utilities.Reset(this, R.id.pncChildInfo);
            cancelChildUpdating(true);


        } catch (JSONException jse) {
            Log.e("PNCC JSON Exception: ", jse.getMessage());
        }

    }

    private void getSpeciaTextViews(HashMap<String, TextView> keyMap, JSONObject json) {
        for (String key : keyMap.keySet()) {
            try {
                json.put(key, (Utilities.ConvertNumberToEnglish(keyMap.get(key).getText().toString()))); //converted any number back to english
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                Utilities.printTrace(jse.getStackTrace());
            }
        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval, boolean isMother) throws JSONException {
        //get info from database
        JSONObject query = null;
        if (isMother) {
            query = buildQueryHeaderMother(isRetrieval);
        } else {
            query = buildQueryHeaderChild(isRetrieval);
        }
        return query;
    }

    private JSONObject buildQueryHeaderChild(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString = "{" +
                "healthid:" + mother.getHealthId() + "," +
                "providerid:" + String.valueOf(provider.getProviderCode()) + "," +
                "pregno:" + mother.getPregNo() + "," +
                "pncCLoad:" + (isRetrieval ? "retrieve" : (editMode ? "update" : "\"\"")) +
                "}";
        return new JSONObject(queryString);
    }

    private void saveService(int saveButton, int cancelButton, int masterLayoutId, boolean saveButtonPressed, boolean isMother) {
        if (saveButtonPressed) {
            if (!Validation.hasText(isMother ? jsonEditTextDateMap.get("pncdate") : jsonEditTextDateMapChild.get("pncdate"))) {
                Utilities.showAlertToast(this, getString(R.string.string_service_info_absent));
                return;
            }

            pncSaveClick++;
            if (pncSaveClick == 2) {
                if (isMother) {
                    pncMotherSaveToJson();
                } else {
                    pncChildSaveToJson();
                }
                Toast.makeText(this, getText(R.string.str_saving_info), Toast.LENGTH_LONG).show();
                pncSaveClick = 0;
                Utilities.Enable(this, masterLayoutId);
                Utilities.MakeInvisible(this, cancelButton);
                getButton(saveButton).setText(getText(R.string.string_save));

            } else if (pncSaveClick == 1) {

                Utilities.Disable(this, masterLayoutId);
                getButton(saveButton).setText(getText(R.string.string_confirm));
                Utilities.Enable(this, cancelButton);
                Utilities.Enable(this, saveButton);
                Utilities.MakeVisible(this, cancelButton);
                Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(20);
                toast.show();
            }
        } else {
            pncSaveClick = 0;
            getButton(saveButton).setText(editMode ? getText(R.string.string_update) : getText(R.string.string_save));
            Utilities.Enable(this, masterLayoutId);
            Utilities.MakeInvisible(this, cancelButton);
        }
    }

    @Override
    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("pncrefer", getCheckbox(R.id.pncReferCheckBox));
        jsonCheckboxMap.put("pncservicesource", getCheckbox(R.id.pncOthersCheckBox));

        // for Child
        jsonCheckboxMapChild.put("pncbreastfeedingonly", getCheckbox(R.id.pncChildOnlyBreastFeedingCheckBox));
        jsonCheckboxMapChild.put("pncrefer", getCheckbox(R.id.pncChildReferCheckBox));
        jsonCheckboxMapChild.put("pncservicesource", getCheckbox(R.id.pncChildOthersCheckBox));
    }

    @Override
    protected void initiateRadioGroups() {
    }

    @Override
    protected void initiateSpinners() {
        // PNC Mother Info
        jsonSpinnerMap.put("pncservicesource", getSpinner(R.id.pncServiceOthersSpinner));
        jsonSpinnerMap.put("pncanemia", getSpinner(R.id.pncAnemiaSpinner));
        jsonSpinnerMap.put("pncedema", getSpinner(R.id.pncEdemaSpinner));
        jsonSpinnerMap.put("pncbreastcondition", getSpinner(R.id.pncBreastConditionSpinner));
        jsonSpinnerMap.put("pncuterusinvolution", getSpinner(R.id.pncCervixInvolutionSpinner));
        jsonSpinnerMap.put("pnchematuria", getSpinner(R.id.pncDischargeBleedingSpinner));
        jsonSpinnerMap.put("pncperineum", getSpinner(R.id.pncPerineumSpinner));
        jsonSpinnerMap.put("pncfpmethod", getSpinner(R.id.pncFamilyPlanningMethodsSpinner));
        jsonSpinnerMap.put("pncrefercentername", getSpinner(R.id.pncReferCenterNameSpinner));

        // PNC Child Info
        jsonSpinnerMapChild.put("pncservicesource", getSpinner(R.id.pncChildServiceOthersSpinner));
        jsonSpinnerMapChild.put("pncrefercentername", getSpinner(R.id.pncChildReferCenterNameSpinner));

    }

    @Override
    protected void initiateMultiSelectionSpinners() {
        // for mother
        jsonMultiSpinnerMap.put("pncsymptom", getMultiSelectionSpinner(R.id.pncDrawbackSpinner));
        jsonMultiSpinnerMap.put("pnccomplicationsign", getMultiSelectionSpinner(R.id.pncDangerSignsSpinner));
        jsonMultiSpinnerMap.put("pncdisease", getMultiSelectionSpinner(R.id.pncDiseaseSpinner));
        jsonMultiSpinnerMap.put("pnctreatment", getMultiSelectionSpinner(R.id.pncTreatmentSpinner));
        jsonMultiSpinnerMap.put("pncadvice", getMultiSelectionSpinner(R.id.pncAdviceSpinner));
        jsonMultiSpinnerMap.put("pncreferreason", getMultiSelectionSpinner(R.id.pncReasonSpinner));

        // for Child
        jsonMultiSpinnerMapChild.put("pncsymptom", getMultiSelectionSpinner(R.id.pncChildDrawbackSpinner));
        jsonMultiSpinnerMapChild.put("pncdangersign", getMultiSelectionSpinner(R.id.pncChildDangerSignsSpinner));
        jsonMultiSpinnerMapChild.put("pncdisease", getMultiSelectionSpinner(R.id.pncChildDiseaseSpinner));
        jsonMultiSpinnerMapChild.put("pnctreatment", getMultiSelectionSpinner(R.id.pncChildTreatmentSpinner));
        jsonMultiSpinnerMapChild.put("pncadvice", getMultiSelectionSpinner(R.id.pncChildAdviceSpinner));
        jsonMultiSpinnerMapChild.put("pncreferreason", getMultiSelectionSpinner(R.id.pncChildReasonSpinner));
    }

    @Override
    protected void initiateEditTexts() {
        //PNC Mother visit
        jsonEditTextMap.put("pnctemperature", getEditText(R.id.pncTemperatureValue));
        jsonEditTextMap.put("pncbpsys", getEditText(R.id.pncBloodPresserValue));
        jsonEditTextMap.put("pncbpdias", getEditText(R.id.pncBloodPresserValueD));
        jsonEditTextMap.put("pnchemoglobin", getEditText(R.id.pncHemoglobinValue));

        getEditText(R.id.pncBloodPresserValue).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.pncBloodPresserValue)));
        getEditText(R.id.pncBloodPresserValue).setOnFocusChangeListener(new CustomFocusChangeListener(this));
        getEditText(R.id.pncBloodPresserValueD).addTextChangedListener(new CustomTextWatcher(this,
                getEditText(R.id.pncBloodPresserValueD)));
        getEditText(R.id.pncBloodPresserValueD).setOnFocusChangeListener(new CustomFocusChangeListener(this));

        getEditText(R.id.pncChildWeightValue).addTextChangedListener(new CustomTextWatcher(this, getEditText(R.id.pncChildWeightValue)));


        //PNC Child visit
        jsonEditTextMapChild.put("pnctemperature", getEditText(R.id.pncChildTemperatureValue));
        jsonEditTextMapChild.put("pncweight", getEditText(R.id.pncChildWeightValue));
        jsonEditTextMapChild.put("pncbreathingperminute", getEditText(R.id.pncChildBreathValue));
    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewMapChild.put("pncchildno", getTextView(R.id.pncNewBornNumber));
        //mother section--changing mandatory sign color
        getTextView(R.id.pncServiceDateLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.pncServiceDateLabel).getText().toString(), 0, 1));
        getTextView(R.id.pncReferCenterNameLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.pncReferCenterNameLabel).getText().toString(), 0, 1));
        getTextView(R.id.pncReasonLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.pncReasonLabel).getText().toString(), 0, 1));
        //child section--changing mandatory sign color
        getTextView(R.id.pncChildServiceDateLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.pncChildServiceDateLabel).getText().toString(), 0, 1));
        getTextView(R.id.pncChildReferCenterNameLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.pncChildReferCenterNameLabel).getText().toString(), 0, 1));
        getTextView(R.id.pncChildReasonLabel).setText(Utilities.changePartialTextColor(Color.RED,
                getTextView(R.id.pncChildReasonLabel).getText().toString(), 0, 1));
    }

    @Override
    protected void initiateEditTextDates() {
        // PNC Mother Service Date
        jsonEditTextDateMap.put("pncdate", getEditText(R.id.pncServiceDateValue));
        // PNC Child Service Date
        jsonEditTextDateMapChild.put("pncdate", getEditText(R.id.pncChildServiceDateValue));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.pncOthersCheckBox) {
            int visibility = isChecked ? View.VISIBLE : View.GONE;

            if (!isChecked) getSpinner(R.id.pncServiceOthersSpinner).setSelection(0);
            getSpinner(R.id.pncServiceOthersSpinner).setVisibility(visibility);

        }

        if (buttonView.getId() == R.id.pncChildOthersCheckBox) {
            int visibility = isChecked ? View.VISIBLE : View.GONE;

            if (!isChecked) getSpinner(R.id.pncChildServiceOthersSpinner).setSelection(0);
            getSpinner(R.id.pncChildServiceOthersSpinner).setVisibility(visibility);

        }
        if (buttonView.getId() == R.id.pncReferCheckBox) {
            int visibility = isChecked ? View.VISIBLE : View.GONE;
            int layouts[] = {R.id.pncReferCenterName, R.id.pncReason};

            for (int i = 0; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i], visibility);
            }
        }

        if (buttonView.getId() == R.id.pncChildReferCheckBox) {
            int visibility = isChecked ? View.VISIBLE : View.GONE;
            int layouts[] = {R.id.pncChildReferCenterName, R.id.pncChildReason};

            for (int i = 0; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i], visibility);
            }
        }
    }

    private void handleChild(String result) {
        if (!result.equals(child_result)) {
            child_result = result;
        }

        Log.d(LOGTAG, "Handle child:\n\t" + result);
        ll_pnc_child.removeAllViews();
        try {
            jsonRespChild = new JSONObject(result);
            if (selected_child == 0) { //no child is selected
                return;
            }

            getTextView(R.id.pncNewBornNumber).setText(Utilities.ConvertNumberToBangla(String.valueOf(selected_child)));

            JSONObject childJson = jsonRespChild.getJSONObject(String.valueOf(selected_child));

            int serviceCount = childJson.getInt("serviceCount");
            getTextView(R.id.pncChildVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(serviceCount + 1)));
            if (serviceCount > 0) {
                isChild = true;
                showHidePncModifyButton(childJson, !isChild);
                if (lastServiceJSONChild != null) lastServiceJSONChild = null;
                lastServiceJSONChild = childJson.getJSONObject(String.valueOf(serviceCount));

                /*for (int in = 1; in <= serviceCount; in++) {

                    JSONObject jsonObject = childJson.getJSONObject("" + in);

                    Log.d("--====>", "---serviceSource child=====>" + jsonObject.toString());

                    String visitDate = jsonObject.getString("visitDate");


                    String symptom = jsonObject.getString("symptom");
                    String weight = jsonObject.getString("weight");
                    String referCenterName = jsonObject.getString("referCenterName");
                    String childNo = jsonObject.getString("childNo");
                    String treatment = jsonObject.getString("treatment");
                    String breastFeedingOnly = jsonObject.getString("breastFeedingOnly");

                    String breathingPerMinute = jsonObject.getString("breathingPerMinute");
                    String disease = jsonObject.getString("disease");
                    String dangerSign = jsonObject.getString("dangerSign");
                    String temperature = jsonObject.getString("temperature");
                    String advice = jsonObject.getString("advice");
                    String refer = jsonObject.getString("refer");
                    String referReason = jsonObject.getString("referReason");

                    ArrayList<String> list = new ArrayList<String>();
                    list.add("" + getString(R.string.visitDate) + " " + visitDate);
                    list.add("" + getString(R.string.complicationsign) + " " + symptom);
                    list.add("" + getString(R.string.temperature) + " " + temperature);
                    list.add("" + getString(R.string.weight) + " " + weight);
                    list.add("" + getString(R.string.breath_per_minute) + " " + breathingPerMinute);
                    list.add("" + getString(R.string.complication) + " " + dangerSign);
                    if (breastFeedingOnly.equals("1")) {
                        breastFeedingOnly = "হ্যাঁ ";
                    } else if (breastFeedingOnly.equals("2")) {
                        breastFeedingOnly = "না";
                    } else {
                        breastFeedingOnly = "";
                    }

                    list.add("" + getString(R.string.only_breast_feeding) + " " + breastFeedingOnly);

                    list.add("" + getString(R.string.disease) + " " + disease);
                    list.add("" + getString(R.string.treatment) + " " + treatment);
                    list.add("" + getString(R.string.advice) + " " + advice);
                    list.add("" + getString(R.string.refer) + " " + refer);
                    list.add("" + getString(R.string.referCenterName) + " " + referCenterName);
                    list.add("" + getString(R.string.referReason) + " " + referReason);


                    try {
                        listDataHeader = new ArrayList<String>();
                        listDataChild = new HashMap<String, List<String>>();

                        //listDataHeader.add("Visit " + in + ":");//jsonArray.get(0).toString()
                        listDataHeader.add("পরিদর্শন " + Utilities.ConvertNumberToBangla(String.valueOf(in)) + ":");
                        listDataChild.put(listDataHeader.get(0), list);
                        listAdapter_child = new ExpandableListAdapterforPNC_Child(PNCActivity.this, listDataHeader, listDataChild);
                        initPage_child();
                        ll_pnc_child.addView(expListView_child);
                        expListView_child.setScrollingCacheEnabled(true);
                        expListView_child.setAdapter(listAdapter_child);
                        ll_pnc_child.invalidate();

                    } catch (Exception e) {
                        Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                        e.printStackTrace();
                    }
                }*/

                for (int i = 1; i <= serviceCount ; i++) {
                    JSONObject singleVisitJson = childJson.getJSONObject(String.valueOf(i));
                    prepareChildJSONForDifferentKey(singleVisitJson);

                    displayList = new ArrayList<>();

                    HistoryListMaker<PNCActivity> historyListMaker = new HistoryListMaker<>(
                            this, /*activity*/
                            singleVisitJson, /*json containing keys*/
                            historyLabelMapTwo, /*history details*/
                            compositeMap /*fields whose values are given against multiple keys*/
                    );
                    historyListMaker.setChildJSON();

                    try {
                        displayList.addAll(historyListMaker.getDisplayList());
                    } catch(Exception e) {
                        Log.e(LOGTAG, String.format("ERROR: %s" , e.getMessage()));
                        Utilities.printTrace(e.getStackTrace(), 10);
                    }

                    Log.d(LOGTAG, String.format("Visit: %d, Size after adding [MIV] to the list is %d", i, displayList.size()));

                    listDataHeader = new ArrayList<>();
                    listDataHeader.add(getString(R.string.visit) + Utilities.ConvertNumberToBangla(String.valueOf(i)));

                    HashMap<String, List<DisplayValue>> listDisplayValues = new HashMap<>();
                    listDisplayValues.put(listDataHeader.get(0), displayList);

                    ExpandableDisplayListAdapter displayListAdapter = new ExpandableDisplayListAdapter(this, listDataHeader, listDisplayValues, true);

                    initPage_child();

                    ll_pnc_child.addView(expListView_child);
                    expListView_child.setScrollingCacheEnabled(true);
                    expListView_child.setAdapter(displayListAdapter);

                    //keep history visible and its child expandable
                    for(int j=0; j < displayListAdapter.getGroupCount(); j++){
                        expListView_child.expandGroup(j);
                    }

                    if(historyLayout.getVisibility()==View.GONE){
                        historyLayout.setVisibility(View.VISIBLE);
                        getTextView(R.id.PNCHistoryLabel).
                                setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.arrow_up_float,0);
                    }

                    ll_pnc_child.invalidate();
                }
            } else {
                Utilities.MakeInvisible(this, R.id.modifyLastPncButton);
            }
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        }
    }

    private void enableMotherLayout() {
        lay_frag_child.setVisibility(View.GONE);
        pnclay_child.setVisibility(View.GONE);
        getSpinner(R.id.id_pncChildListDropdown).setSelection(0);// un-select selected children

        if (mother_flag == false) {
            lay_frag_mother.setVisibility(View.VISIBLE);
            pnclay_mother.setVisibility(View.VISIBLE);
        } else {
            mother_flag = false;
        }
    }

    private void enableDeadMotherLayout() {
        lay_frag_child.setVisibility(View.GONE);
        pnclay_child.setVisibility(View.GONE);
        getSpinner(R.id.id_pncChildListDropdown).setSelection(0);
        if (mother_flag == false) {
            lay_frag_mother.setVisibility(View.VISIBLE);
            pnclay_mother.setVisibility(View.INVISIBLE);
        } else {
            mother_flag = false;
        }
        Utilities.showBiggerToast(this, R.string.PNCMotherdead);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pncServiceDateValue:
            case R.id.Date_Picker_Button:
            case R.id.Date_Picker_Button_Child:
                datePickerDialog.show(datePickerPair.get(v.getId()));
                break;
            case R.id.pncmother:
                enableMotherLayout();
                break;

            case R.id.modifyLastPncButton:
                if (isChild) {
                    setLastChildServiceData();
                } else {
                    setLastMotherServiceData();
                }

                break;

            case R.id.pncChildSaveButton:
                if (!Validation.hasText(getEditText(R.id.pncChildServiceDateValue))) {
                    return;
                }
                if (!hasTheRequiredFields()) {
                    return;
                }
                try {
                    String cVisitDate = getEditText(R.id.pncChildServiceDateValue).getText().toString();
                    //TODO: has to remove direct null checking .......
                    if (lastServiceJSONChild != null && !Validation.isValidVisitDate(this, cVisitDate, lastServiceJSONChild.getString("visitDate"), false, true)) {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                saveService(R.id.pncChildSaveButton,
                        R.id.pncChildCancelButton, R.id.pncChildInfo, true, false);
                break;

            case R.id.pncChildCancelButton:
                saveService(R.id.pncChildSaveButton,
                        R.id.pncChildCancelButton, R.id.pncChildInfo, false, false);
                break;

            case R.id.pncChildUpdateCancelButton:
                cancelChildUpdating(false);
                break;

            case R.id.pncSaveButton:
                if (!Validation.hasText(getEditText(R.id.pncServiceDateValue))) {
                    return;
                }
                if (!hasTheRequiredFields()) {
                    return;
                }
                try {
                    String cVisitDate = getEditText(R.id.pncServiceDateValue).getText().toString();
                    if (lastPncVisit > 0 && !Validation.isValidVisitDate(this, cVisitDate, lastServiceJSON.getString("visitDate"), false, true)) {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                saveService(R.id.pncSaveButton,
                        R.id.pncCancelButton,
                        R.id.pncMotherInfo, true, true);
                break;

            case R.id.pncCancelButton:
                saveService(R.id.pncSaveButton,
                        R.id.pncCancelButton,
                        R.id.pncMotherInfo, false, true);
                break;

            case R.id.pncUpdateCancelButton:
                cancelMotherUpdating(false);
                break;

            case R.id.pncDeleteButton:
            case R.id.pncChildDeleteButton:
                deleteLastPNC();
                break;

        }

    }

    private void cancelMotherUpdating(boolean afterCallback) {
        Utilities.Reset(this, R.id.pncMotherInfo);
        if (!afterCallback) {
            getTextView(R.id.pncVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastVisit >= 0 ? lastVisit + 1 : 1)));
        }
        getButton(R.id.pncSaveButton).setText(getText(R.string.string_save));
        getButton(R.id.pncDeleteButton).setVisibility(View.GONE);
        getButton(R.id.pncUpdateCancelButton).setVisibility(View.GONE);
        editMode = false;
    }

    private void cancelChildUpdating(boolean afterCallback) {
        Utilities.Reset(this, R.id.pncChildInfo);
        if (!afterCallback) {
            getTextView(R.id.pncChildVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastVisit >= 0 ? lastVisit + 1 : 1)));
        }
        getButton(R.id.pncChildSaveButton).setText(getText(R.string.string_save));
        getButton(R.id.pncChildDeleteButton).setVisibility(View.GONE);
        getButton(R.id.pncChildUpdateCancelButton).setVisibility(View.GONE);
        editMode = false;
    }

    private void setLastMotherServiceData() {
        prepareMotherJSONForDifferentKey(lastServiceJSON);
        Utilities.setEditTexts(jsonEditTextMap, lastServiceJSON);
        Utilities.setEditTextDates(jsonEditTextDateMap, lastServiceJSON);
        Utilities.setSpinners(jsonSpinnerMap, lastServiceJSON);
        Utilities.setCheckboxes(jsonCheckboxMap, lastServiceJSON);
        Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap, lastServiceJSON);
        Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap, lastServiceJSON);
        try {
            serviceId = lastServiceJSON.getInt("serviceID");
        } catch (JSONException e) {
            e.printStackTrace();
            serviceId = 0;
        }

        getTextView(R.id.pncVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPncVisit)));


        getButton(R.id.pncSaveButton).setText(getText(R.string.string_update));
        getButton(R.id.pncDeleteButton).setVisibility(View.VISIBLE);
        getButton(R.id.pncUpdateCancelButton).setVisibility(View.VISIBLE);
        Utilities.Disable(this, R.id.pncServiceDate);
        editMode = true;
    }

    private void setLastChildServiceData() {
        prepareChildJSONForDifferentKey(lastServiceJSONChild);
        Utilities.setEditTexts(jsonEditTextMapChild, lastServiceJSONChild);
        Utilities.setEditTextDates(jsonEditTextDateMapChild, lastServiceJSONChild);
        Utilities.setSpinners(jsonSpinnerMapChild, lastServiceJSONChild);
        Utilities.setCheckboxes(jsonCheckboxMapChild, lastServiceJSONChild);
        Utilities.setMultiSelectSpinners(jsonMultiSpinnerMapChild, lastServiceJSONChild);
        Utilities.setRadioGroupButtons(jsonRadioGroupButtonMapChild, lastServiceJSONChild);
        try {
            serviceId = lastServiceJSONChild.getInt("serviceId");
        } catch (JSONException e) {
            e.printStackTrace();
            serviceId = 0;
        }

        getTextView(R.id.pncChildVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastVisit)));


        getButton(R.id.pncChildSaveButton).setText(getText(R.string.string_update));
        getButton(R.id.pncChildDeleteButton).setVisibility(View.VISIBLE);
        getButton(R.id.pncChildUpdateCancelButton).setVisibility(View.VISIBLE);
        Utilities.Disable(this, R.id.pncChildServiceDate);
        editMode = true;
    }

    private void prepareChildJSONForDifferentKey(JSONObject jsonObject) {
        try {
            jsonObject.put("pncdate", jsonObject.getString("visitDate"));
            jsonObject.put("pnctemperature", jsonObject.getString("temperature"));
            jsonObject.put("pncweight", jsonObject.getString("weight"));
            jsonObject.put("pncbreathingperminute", jsonObject.getString("breathingPerMinute"));
            jsonObject.put("pncsymptom", jsonObject.getString("symptom"));
            jsonObject.put("pncdangersign", jsonObject.getString("dangerSign"));
            jsonObject.put("pncdisease", jsonObject.getString("disease"));
            jsonObject.put("pnctreatment", jsonObject.getString("treatment"));
            jsonObject.put("pncadvice", jsonObject.getString("advice"));
            jsonObject.put("pncreferreason", jsonObject.getString("referReason"));
            jsonObject.put("pncservicesource", jsonObject.getString("serviceSource"));
            jsonObject.put("pncrefercentername", jsonObject.getString("referCenterName"));
            jsonObject.put("pncbreastfeedingonly", jsonObject.getString("breastFeedingOnly"));
            jsonObject.put("pncrefer", jsonObject.getString("refer"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void prepareMotherJSONForDifferentKey(JSONObject jsonObject) {
        try {
            jsonObject.put("pnccomplicationsign", jsonObject.getString("complicationsign"));
            jsonObject.put("pncanemia", jsonObject.getString("anemia"));
            jsonObject.put("pncservicesource", jsonObject.getString("serviceSource"));
            jsonObject.put("pncrefercentername", jsonObject.getString("referCenterName"));
            jsonObject.put("pnctreatment", jsonObject.getString("treatment"));
            jsonObject.put("pncperineum", jsonObject.getString("perineum"));
            jsonObject.put("pncuterusinvolution", jsonObject.getString("uterusInvolution"));
            jsonObject.put("pncdate", jsonObject.getString("visitDate"));
            jsonObject.put("pncbpdias", jsonObject.getString("bpDiastolic"));
            jsonObject.put("pncdisease", jsonObject.getString("disease"));
            jsonObject.put("pncbpsys", jsonObject.getString("bpSystolic"));
            jsonObject.put("pnchematuria", jsonObject.getString("hematuria"));
            jsonObject.put("pnctemperature", jsonObject.getString("temperature"));
            jsonObject.put("pncreferreason", jsonObject.getString("referReason"));
            jsonObject.put("pncrefer", jsonObject.getString("refer"));
            jsonObject.put("pncedema", jsonObject.getString("edema"));
            jsonObject.put("pnchemoglobin", jsonObject.getString("hemoglobin"));
            jsonObject.put("pncfpmethod", jsonObject.getString("FPMethod"));
            jsonObject.put("pncbreastcondition", jsonObject.getString("breastCondition"));
            jsonObject.put("pncadvice", jsonObject.getString("advice"));
            jsonObject.put("pncsymptom", jsonObject.getString("symptom"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(con, "done" + position, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void populateChild(JSONObject childJson) {
        Spinner childDropdown = getSpinner(R.id.id_pncChildListDropdown);
        childList.clear();
        String childArray[] = {};

        childList.add("");

        try {
            String childValues = childJson.getString("childMapping");
            if (!childValues.equals("")) {
                childArray = childValues.split(",");
            }

            for (int i = 0; i < childArray.length; i++) {
                childList.add(childArray[i]);
            }

        } catch (JSONException JSE) {
            JSE.printStackTrace();
        }

        childAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, childList);
        childAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childDropdown.setAdapter(childAdapter);
        if (childArray.length > 0) {
            childDropdown.setVisibility(getRadioButton(R.id.pncChildSelector).isChecked() ? View.VISIBLE : View.INVISIBLE);
            childDropdown.setSelection(0);
        }

        childDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    handleChildSelected(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void handleChildSelected(int childno) {
        String child = childList.get(childno);

        Log.d("-------child test------", "---------");
        selected_child = childno;
        ll_pnc_child.removeAllViews();

        handleChild(child_result);
        Log.d("------------------" + childno, "-----------" + child);
        lay_frag_mother.setVisibility(View.GONE);
        pnclay_mother.setVisibility(View.GONE);

        if (child_flag == false) {
            lay_frag_child.setVisibility(View.VISIBLE);
            pnclay_child.setVisibility(View.VISIBLE);
            ll_pnc_child.invalidate();
        } else {
            ll_pnc_child.invalidate();
        }
    }

    private void showHidePncModifyButton(JSONObject jso, boolean isMother) {
        String key = (isMother ? "count" : "serviceCount");
        lastVisit = 0; // no last visit

        try {
            if (jso != null && jso.has(key)) {
                lastVisit = jso.getInt(key);

                //if (lastVisit > 0) {
                Utilities.SetVisibility(
                        this,
                        R.id.modifyLastPncButton,
                        (lastVisit > 0) && isLastPncDeletable(jso.getJSONObject(String.valueOf(lastVisit))) ? View.VISIBLE : View.GONE);
                //}
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could determine visibility:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    private boolean isLastPncDeletable(JSONObject jso) {

        if (jso == null) { //initial response
            return false;
        }
        boolean thresholdPeriodPassed = false;
        try {

            String providerCode = jso.getString("providerId");
            if (jso.has("systemEntryDate") && !jso.getString("systemEntryDate").equals("")) {
                Date lastVisitEntryDate = Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE, jso.getString("systemEntryDate"));
                thresholdPeriodPassed = Utilities.getDateDiff(lastVisitEntryDate, new Date(), TimeUnit.DAYS) > Flag.UPDATE_THRESHOLD;
            }
            Log.d(LOGTAG, "Last Service was provide by: \t" + providerCode);
            return (provider.getProviderCode().equals(providerCode) && !thresholdPeriodPassed);
            //return (provider.getProviderCode().equals(providerCode));

        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Exception Caught\n\t\t");
            if (jso != null) {
                Log.e(LOGTAG, "JSON ->" + jso.toString());
            }
            Utilities.printTrace(jse.getStackTrace(), 10);
        } catch (ParseException pse) {
            pse.printStackTrace();
        }

        return false;
    }

    private void deleteConfirmed(boolean isMother) {
        try {

            JSONObject deleteJson = buildQueryHeader(false, isMother);
            String servlet = "";
            String rootkey = "";
            String loadKey = "";

            if (isMother) {
                servlet = SERVLET_MOTHER;
                rootkey = ROOTKEY_MOTHER;
                loadKey = "pncMLoad";
                pncInfoUpdateTask = new AsyncPNCInfoUpdate(this, this);
            } else {
                servlet = SERVLET_CHILD;
                rootkey = ROOTKEY_CHILD;
                loadKey = "pncCLoad";
                deleteJson.put("pncchildno", selected_child);
                pncInfoUpdateTask = new AsyncPNCInfoUpdate(new AsyncCallback() {
                    @Override
                    public void callbackAsyncTask(String result) {
                        handleChild(result);
                    }
                }, this);
            }

            deleteJson.put(loadKey, "delete");

            pncInfoUpdateTask.execute(deleteJson.toString(), servlet, rootkey);
            if (isMother) {
                cancelMotherUpdating(true);
            } else {
                cancelChildUpdating(true);
            }

        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete ANC request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    public void deleteLastPNC() {
        AlertDialog alertDialog = new AlertDialog.Builder(PNCActivity.this).create();
        alertDialog.setIcon(android.R.drawable.ic_delete);
        alertDialog.setTitle("Delete Service?");
        alertDialog.setMessage(getString(R.string.ServiceDeletionWarning));

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
                        deleteConfirmed(findViewById(R.id.pncMotherInfo).getVisibility() == View.VISIBLE);
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialog(con, "প্রসবোত্তর সেবা ( PNC )");
    }

    private boolean hasTheRequiredFields() {
        boolean valid = true;
        boolean specialInvalid = false;

        if (getCheckbox(R.id.pncReferCheckBox).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.pncReferCenterNameSpinner)))
                specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.pncReasonSpinner)))
                specialInvalid = true;
        }

        if (getCheckbox(R.id.pncChildReferCheckBox).isChecked()) {
            if (!Validation.hasSelected(getSpinner(R.id.pncChildReferCenterNameSpinner)))
                specialInvalid = true;
            if (!Validation.hasMinimumItemSelected(getMultiSelectionSpinner(R.id.pncChildReasonSpinner)))
                specialInvalid = true;
        }

        if (!valid) {
            Toast toast = Toast.makeText(this, R.string.GeneralSaveWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        } else if (specialInvalid) {
            MethodUtils.showSnackBar(findViewById(R.id.pnc_activity_layout), getResources().getString(R.string.refer_validation_message), true);
            return false;
        } else {
            return true;
        }
    }

    void setHistoryLabelMapping() {
        //The array primarily contains 3 things, the string array from resource id, lower limit, upper limit
//        historyLabelMap.put("pncservicesource",Pair.create(getString(R.string.service_center_name), new Integer[] {R.array.FacilityType_DropDown, 0, 0}));
        historyLabelMap.put("pncdate", Pair.create(getString(R.string.visitDate), new Integer[] {0}));
        historyLabelMap.put("pncsymptom", Pair.create(getString(R.string.complicationsign), new Integer[]{R.array.PNC_Mother_Drawback_DropDown}));
        historyLabelMap.put("pnctemperature", Pair.create(getString(R.string.temperature), new Integer[]{0}) );
        historyLabelMap.put("pncbpsys", Pair.create(getString(R.string.bpSystolic), new Integer[]{0, 139, 89}));
        historyLabelMap.put("pncanemia", Pair.create(getString(R.string.anemia), new Integer[]{R.array.Anemia_Dropdown}));
        historyLabelMap.put("pncedema", Pair.create(getString(R.string.edema), new Integer[]{R.array.Jaundice_Edima_Dropdown}));
        historyLabelMap.put("pncbreastcondition", Pair.create(getString(R.string.breast_condition), new Integer[]{R.array.Breast_Condition_DropDown}));
        historyLabelMap.put("pnchemoglobin", Pair.create(getString(R.string.hemoglobin), new Integer[]{0, 20, 100}));
        historyLabelMap.put("pncuterusinvolution", Pair.create(getString(R.string.uterusInvolution), new Integer[]{R.array.Cervix_Involution_DropDown}));
        historyLabelMap.put("pnchematuria", Pair.create(getString(R.string.hematuria), new Integer[]{R.array.Discharge_Bleeding_DropDown}));
        historyLabelMap.put("pncperineum", Pair.create(getString(R.string.perineum), new Integer[]{R.array.Perineum_DropDown}));
        historyLabelMap.put("pncfpmethod", Pair.create(getString(R.string.family_planning_methods), new Integer[]{R.array.Family_Planning_Methods_DropDown}));
//        historyLabelMap.put("serviceSource", Pair.create(getString(R.string.service_center_name), new Integer[]{R.array.FacilityType_DropDown}));
        historyLabelMap.put("pnccomplicationsign", Pair.create(getString(R.string.complication), new Integer[]{R.array.PNC_Mother_Danger_Sign_DropDown})); //Check
        historyLabelMap.put("pncdisease", Pair.create(getString(R.string.disease), new Integer[]{R.array.PNC_Mother_Disease_DropDown}));
        historyLabelMap.put("pnctreatment", Pair.create(getString(R.string.treatment), new Integer[]{R.array.Treatment_DropDown}));
        historyLabelMap.put("pncadvice", Pair.create(getString(R.string.advice), new Integer[]{R.array.PNC_Mother_Advice_DropDown}));
        historyLabelMap.put("pncrefer", Pair.create(getString(R.string.refer), new Integer[]{R.array.YesNo_Dropdown}));
        historyLabelMap.put("pncrefercentername", Pair.create(getString(R.string.referCenterName), new Integer[]{R.array.FacilityType_DropDown}));
        historyLabelMap.put("pncreferreason", Pair.create(getString(R.string.referReason), new Integer[]{R.array.PNC_Mother_Refer_Reason_DropDown}));
    }

    void setHistoryLabelMappingChild() {
        //The array primarily contains 3 things, the string array from resource id, lower limit, upper limit
        historyLabelMapTwo.put("pncdate", Pair.create(getString(R.string.visitDate), new Integer[] {0}));
        historyLabelMapTwo.put("pncsymptom", Pair.create(getString(R.string.complicationsign), new Integer[]{R.array.PNC_Child_Drawback_DropDown}));
        historyLabelMapTwo.put("pnctemperature",Pair.create(getString(R.string.temperature), new Integer[]{0}) );
        historyLabelMapTwo.put("pncweight", Pair.create(getString(R.string.weight), new Integer[]{0}) );
        historyLabelMapTwo.put("pncdisease", Pair.create(getString(R.string.disease), new Integer[]{R.array.PNC_Child_Disease_DropDown}));
        historyLabelMapTwo.put("pncdangersign", Pair.create(getString(R.string.complication), new Integer[]{R.array.PNC_Child_Danger_Sign_DropDown}));
        historyLabelMapTwo.put("pncbreathingperminute", Pair.create(getString(R.string.breath_per_minute), new Integer[]{0}));
        historyLabelMapTwo.put("pncbreastfeedingonly", Pair.create(getString(R.string.only_breast_feeding), new Integer[]{R.array.YesNo_Dropdown}));
        historyLabelMapTwo.put("pncadvice", Pair.create(getString(R.string.advice), new Integer[]{R.array.PNC_Child_Advice_DropDown}));
        historyLabelMapTwo.put("pnctreatment", Pair.create(getString(R.string.treatment), new Integer[]{R.array.Treatment_DropDown}));
        historyLabelMapTwo.put("pncrefer", Pair.create(getString(R.string.refer), new Integer[]{R.array.YesNo_Dropdown}));
        historyLabelMapTwo.put("pncrefercentername", Pair.create(getString(R.string.referCenterName), new Integer[]{R.array.FacilityType_DropDown}));
        historyLabelMapTwo.put("pncreferreason", Pair.create(getString(R.string.referReason), new Integer[]{R.array.PNC_Child_Refer_Reason_DropDown}));
    }
}
