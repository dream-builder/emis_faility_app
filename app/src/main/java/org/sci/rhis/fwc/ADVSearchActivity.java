package org.sci.rhis.fwc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncADVSearchUpdate;
import org.sci.rhis.model.LocationHolder;
import org.sci.rhis.model.Person;
import org.sci.rhis.model.PregnantMother;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.SharedPref;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class ADVSearchActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private  Button cancelBtn, searchBtn;
    private  final String SERVLET   = "advancesearch";
    private  final String ROOTKEY   = "advanceSearch";
    private  final String LOGTAG    = "FWC-ADV-SEARCH";


    private ListView searchListView ;
    ArrayList<Person> personsList = new ArrayList<Person>();
    ArrayList<PregnantMother> pregMotherList = new ArrayList<PregnantMother>();

    AsyncADVSearchUpdate ADVSearchUpdateTask;

    private  String zillaString = "";
    private String searchTag_,searchString;
    private String prevZilla, prevUpazila, prevUnion, prevVillage;
    private int searchCount_;

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    ArrayList<LocationHolder> districtList;
    ArrayList<LocationHolder> upazillaList;
    ArrayList<LocationHolder> unionList;
    ArrayList<LocationHolder> villageList;

    ArrayAdapter<LocationHolder> zillaAdapter;
    ArrayAdapter<LocationHolder> upazilaAdapter;
    ArrayAdapter<LocationHolder> unionAdapter;
    ArrayAdapter<LocationHolder> villageAdapter;

    private FileLoader loader = null;

    private JSONObject villJson = null;
    private LocationHolder blanc = new LocationHolder();
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advsearch);
        searchTag_= getIntent().getStringExtra(Constants.KEY_OPTION);
        if(searchTag_.equals("5")){
            Utilities.SetVisibility(this,R.id.mobileSearchMainLayout,View.GONE);
            Utilities.SetVisibility(this,R.id.advSearchMainLayout,View.GONE);
            Utilities.SetVisibility(this,R.id.search_button_layout,View.GONE);
            String lastSearchResult=SharedPref.getLastSearchResult(this);
            this.callbackAsyncTask(lastSearchResult);
        }else {
            init();
        }

    }

    private void init(){
        cancelBtn=(Button)findViewById(R.id.cancelBtn);
        searchBtn=(Button)findViewById(R.id.advSearchBtn);

        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.searchStartDatePickerButton, getEditText(R.id.searchStartDateEditText));
        datePickerPair.put(R.id.searchEndDatePickerButton, getEditText(R.id.searchEndDateEditText));

        changeSearchViewAsOption(searchTag_);
        changeForCSBA();
        if(ProviderInfo.getProvider().getmProviderType().equals("6")){
            changeForSACMO_HS();
        }
        initialize();
        addListenerOnButton();


    }

    private void changeForSACMO_HS(){
        getSpinner(R.id.advSearchServiceTypeSpinner).setSelection(8);
        getSpinner(R.id.advSearchServiceTypeSpinner).setEnabled(false);
    }

    private void changeForCSBA(){
        if(ProviderInfo.getProvider().getmCsba().equals("1")){
            String services[] = {getString(R.string.empty_string),getString(R.string.anc_string),getString(R.string.delivery_string),getString(R.string.pnc_string)};//TODO: need to modify soon
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,services);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            getSpinner(R.id.advSearchServiceTypeSpinner).setAdapter(spinnerArrayAdapter);
        }
    }

    private void changeSearchViewAsOption(String option){
        switch (Integer.valueOf(option)){
            case 1:
                Utilities.MakeVisible(this,R.id.mobileSearchMainLayout);
                Utilities.MakeInvisible(this,R.id.advSearchMainLayout);
                getTextView(R.id.advSearch_husbandNameLabel).setText(R.string.spouse_string);
                searchString = getString(R.string.string_mobile_search);
                break;
            case 2:
                Utilities.MakeVisible(this,R.id.advSearchMainLayout);
                Utilities.MakeInvisible(this,R.id.mobileSearchMainLayout);
                Utilities.MakeInvisible(this,R.id.serviceTypeLayout);
                Utilities.MakeInvisible(this,R.id.dateRangeSearchLayout);
                searchString = getString(R.string.string_advance_search);
                //location related codes.....
                districtList    =  new ArrayList<>();
                upazillaList    =  new ArrayList<>();
                unionList       =  new ArrayList<>();
                villageList     =  new ArrayList<>();
                addAndSetSpinners();
                loader = new FileLoader(this);
                break;
            case 3:
                Utilities.MakeVisible(this,R.id.advSearchMainLayout);
                Utilities.MakeInvisible(this,R.id.mobileSearchMainLayout);
                Utilities.MakeInvisible(this,R.id.name_age);
                getTextView(R.id.advSearch_headerTextView).setText(R.string.string_service_search);
                searchString = getString(R.string.string_service_search);
                //location related codes.....
                districtList    =  new ArrayList<>();
                upazillaList    =  new ArrayList<>();
                unionList       =  new ArrayList<>();
                villageList     =  new ArrayList<>();
                addAndSetSpinners();
                loader = new FileLoader(this);
                break;

            case 4:
                Utilities.MakeVisible(this,R.id.advSearchMainLayout);
                Utilities.MakeInvisible(this,R.id.mobileSearchMainLayout);
                Utilities.MakeInvisible(this,R.id.name_age);
                Utilities.MakeInvisible(this,R.id.serviceTypeLayout);
                Utilities.MakeInvisible(this,R.id.dateRangeSearchLayout);
                getTextView(R.id.advSearch_headerTextView).setText(R.string.string_preg_women_search);
                searchString = getString(R.string.string_preg_women_search);
                //location related codes.....
                districtList    =  new ArrayList<>();
                upazillaList    =  new ArrayList<>();
                unionList       =  new ArrayList<>();
                villageList     =  new ArrayList<>();
                addAndSetSpinners();
                loader = new FileLoader(this);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(Arrays.asList(new Integer[]{2,3,4}).contains(Integer.valueOf(searchTag_)) && !loader.isCancelled()) {
            setSearchability(false);
            loader.execute();
            pDialog = new ProgressDialog(this,R.style.MyTheme);
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(searchTag_.equals("2")){
            jsonSpinnerMap.get("gender").setSelection(1); //select woman by default
        }

    }

    private void loadLocations() {

        try {

            zillaString = LocationHolder.getZillaUpazillaUnionString();

            districtList.add(blanc);
            LocationHolder.loadListFromJson(zillaString, "nameEnglish", "nameBangla", "Upazila", districtList);

            //set zilla spinner
            zillaAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, districtList);
            zillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            try {
                if(villJson == null) {
                    villJson = LocationHolder.getVillageJson();
                }
            } catch (Exception jse) {
                Log.e(LOGTAG, "JSON Exception in loading village");
                Utilities.printTrace(jse.getStackTrace());
            }

        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private void handlePersonListClick( Person person) {
        Intent intent = new Intent();
        intent.putExtra("HealthId", person.getHealthId());
        intent.putExtra("HealthIdType", person.getIdIndex());
        setResult(RESULT_OK, intent);
        finishActivity(ActivityResultCodes.ADV_SEARCH_ACTIVITY);
        finish();
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    private void handlePersonListClick(PregnantMother mother) {
        Intent intent = new Intent();
        intent.putExtra("HealthId", mother.getHealthId());
        intent.putExtra("HealthIdType", mother.getIdIndex());
        setResult(RESULT_OK, intent);
        finishActivity(ActivityResultCodes.ADV_SEARCH_ACTIVITY);
        finish();
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    private void initList(){
        //set visibility status of linear_layout_addable_new_client to View.VISIBLE
        Utilities.SetVisibility(this, R.id.linear_layout_addable_new_client, View.VISIBLE);

        searchListView = (ListView) findViewById(R.id.search_result);
        TextView headerTextView = (TextView) findViewById(R.id.txtHeader);
        if(searchCount_!=0){
            headerTextView.setText(searchTag_.equals("5")?"সর্বশেষ অনুসন্ধান": getString(R.string.string_search_result) +
                    Utilities.ConvertNumberToBangla(String.valueOf(searchCount_))+getString(R.string.string_search_result_person));
            if(searchTag_.equals("4")){
                Utilities.SetVisibility(this,R.id.pregSearchListMHeader,View.VISIBLE);
            }else{
                Utilities.SetVisibility(this,R.id.searchListHeader,View.VISIBLE);
            }
        }else{
            headerTextView.setText(R.string.string_search_result_null);
            Utilities.SetVisibility(this,R.id.searchListHeader,View.GONE);
            Utilities.SetVisibility(this,R.id.pregSearchListMHeader,View.GONE);
        }

        if(!searchTag_.equals("4")){
            PersonAdapter personAdapter = new PersonAdapter(this, R.layout.listview_item_row, personsList, Integer.valueOf(searchTag_));
            searchListView.setAdapter(personAdapter);

            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    handlePersonListClick(personsList.get(position));
                }
            });
        }else{
            PregnantMotherAdapter pregMotherAdapter= new PregnantMotherAdapter(this, R.layout.preglist_item_row, pregMotherList);
            searchListView.setAdapter(pregMotherAdapter);
            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    handlePersonListClick(pregMotherList.get(position));
                }
            });
        }

    }

    private void setProviderLocationByDefault(){
            prevZilla = ProviderInfo.getProvider().getZillaID()+"_"+ProviderInfo.getProvider().getDivID();

            prevUpazila = ProviderInfo.getProvider().getUpazillaID();
            prevUnion = ProviderInfo.getProvider().getUnionID();

            Log.d("Loc: ",prevZilla+" : "+prevUpazila+" : "+prevUnion);

            LocationHolder prevDistrictObjectVal=null;
            for(LocationHolder singleLoc:districtList){
                if(singleLoc.getCode().equals(prevZilla)){
                    prevDistrictObjectVal=singleLoc;
                }
            }
            int pos=0;
            if(prevDistrictObjectVal!=null){
                pos = zillaAdapter.getPosition(prevDistrictObjectVal);
            }
            getSpinner(R.id.advSearchDistrict).setSelection(pos);

    }

    private void loadVillageFromJson(
            String zilla,
            String upazila,
            String union,
            ArrayList<LocationHolder> holderList) {


        try{
            if(villJson == null) {
                villJson = LocationHolder.getVillageJson();
            }

            if( union.equals("none") || upazila.equals("none") || zilla.equals("none") ||
                    union.equals("") || upazila.equals("") || zilla.equals("")) {
                return;
            }
            JSONObject unionJson =
                    villJson.getJSONObject(zilla).getJSONObject(upazila).getJSONObject(union);

            Log.d(LOGTAG, "Union deatails:\n\t" + union);

            for(Iterator<String> mouzaKey = unionJson.keys(); mouzaKey.hasNext();) {
                String mouza = mouzaKey.next();
                JSONObject mouzaJson = unionJson.getJSONObject(mouza);

                for(Iterator<String> villageCode = mouzaJson.keys(); villageCode.hasNext();) {
                    String code = villageCode.next();
                    holderList.add(
                            new LocationHolder(
                                    code+"_"+mouza,
                                    mouzaJson.getString(code),
                                    mouzaJson.getString(code),
                                    ""));

                }

                Log.d(LOGTAG, "Mouja - Village: " + mouza + " -> " + unionJson.getString(mouza));

            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON KEY MISSING:" +
                    "\n\t");
            jse.printStackTrace();
        }
    }

    private void addAndSetSpinners() {
        getSpinner(R.id.advSearchDistrict).setOnItemSelectedListener(this);
        getSpinner(R.id.advSearchUpazila).setOnItemSelectedListener(this);
        getSpinner(R.id.advSearchUnion).setOnItemSelectedListener(this);
        getSpinner(R.id.advSearchVillage).setOnItemSelectedListener(this);


        zillaAdapter    = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        upazilaAdapter  = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        unionAdapter    = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        villageAdapter  = new ArrayAdapter(this, android.R.layout.simple_spinner_item);

        unionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private JSONObject buildQueryHeader() throws JSONException {
        //get info from database
        String queryString = "{}";
        try {
            queryString = "{" +
                    "\"zilla\":" + districtList.get(getSpinner(R.id.advSearchDistrict).getSelectedItemPosition()).getCode() + "," +
                    "\"upz\":" + upazillaList.get(getSpinner(R.id.advSearchUpazila).getSelectedItemPosition()).getCode() + "," +
                    "\"union\":" + unionList.get(getSpinner(R.id.advSearchUnion).getSelectedItemPosition()).getCode() + "," +
                    "\"villagemouza\":" + villageList.get(getSpinner(R.id.advSearchVillage).getSelectedItemPosition()).getCode() +
                    "}";
            Log.d("QueryString", queryString);
        } catch (ArrayIndexOutOfBoundsException aiob) {
            Log.e(LOGTAG, "System not ready yet ");
        }
        return new JSONObject(queryString);
    }

    private void advSearchSaveToJson() {
        ADVSearchUpdateTask = new AsyncADVSearchUpdate(this, this);
        JSONObject json;
        try {
            if(Arrays.asList(new Integer[]{2,3,4}).contains(Integer.valueOf(searchTag_))){
                json = buildQueryHeader();
                getSpecialCases(json);
            }else{
                json = new JSONObject();
            }
            json.put(Constants.KEY_OPTION,searchTag_);
            if(!searchTag_.equals("4")){
                Utilities.getEditTexts(jsonEditTextMap, json);
            }

            if(searchTag_.equals("3")){
                Utilities.getEditTextDates(jsonEditTextDateMap,json);
            }


            Log.d("ADVSearch JSON 2SERVLET", json.toString());

            ADVSearchUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);


        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Exception: " + jse.getMessage());
        }

    }

    private void getSpecialCases(JSONObject json) throws JSONException {
        if(searchTag_.equals("2")){
            json.put("gender", getSpinner(R.id.advClientsSexSpinner).getSelectedItemPosition() + 1);
        }else if(searchTag_.equals("3")){
            Utilities.getSpinners(jsonSpinnerMap, json);
        }

        json.put("compressed", true); //request for compression if applicable
    }

    public void addListenerOnButton() {

        final Context context = this;

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                AlertDialogCreator.ExitActivityDialogWithResult(ADVSearchActivity.this,
                        searchString, RESULT_CANCELED, ActivityResultCodes.ADV_SEARCH_ACTIVITY);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.advSearchBtn) {
                    //clearing previous results......
                    personsList.clear();
                    pregMotherList.clear();
                    //.................

                    boolean hasValues = true;
                    if (Arrays.asList(new Integer[]{2,3,4}).contains(Integer.valueOf(searchTag_))) {
                        if(searchTag_.equals("2")){
                            if(!Validation.hasText(getEditText(R.id.advClient_name))) hasValues=false;
                        }else if(searchTag_.equals("3")){
                            if(!Validation.hasText(getEditText(R.id.searchStartDateEditText))) hasValues=false;
                            if(!Validation.hasText(getEditText(R.id.searchEndDateEditText))) hasValues=false;
                            if(!Validation.hasSelected(getSpinner(R.id.advSearchServiceTypeSpinner))) hasValues=false;
                        }
                        if(!Validation.hasSelected(getSpinner(R.id.advSearchDistrict))) hasValues=false;
                        if(!Validation.hasSelected(getSpinner(R.id.advSearchUpazila))) hasValues=false;
                        if(!Validation.hasSelected(getSpinner(R.id.advSearchUnion))) hasValues=false;
                    } else {
                        if(!Validation.hasText(getEditText(R.id.search_et_mobile_no))) hasValues=false;
                    }
                    if (hasValues) advSearchSaveToJson();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_advsearch, menu);
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
        Log.d(LOGTAG, result);
        SharedPref.setLastSearchResult(this,result,"");
        JSONObject json;
        JSONObject jsonPerson;
        try {
            json = new JSONObject(result);
            searchCount_=0;
            if (json.has("count") && json.getInt("count") > 0) { // if the result is present
                searchCount_=json.getInt("count");
                personsList.clear();
                pregMotherList.clear();
                if(searchTag_.equals("2")){
                    if(jsonSpinnerMap.get("gender").getSelectedItemPosition() == 0){
                        getTextView(R.id.advSearch_husbandNameLabel).setText(getString(R.string.string_wife));
                    }else{
                        getTextView(R.id.advSearch_husbandNameLabel).setText(getString(R.string.string_husband));
                    }
                }else if(Arrays.asList(new Integer[]{1,3}).contains(Integer.valueOf(searchTag_))){
                    getTextView(R.id.advSearch_husbandNameLabel).setText(R.string.spouse_string);
                    if(searchTag_.equals("3")){
                        getTextView(R.id.advSearch_healthIdLabel).setText(R.string.string_service_date);
                    }
                }

            }

            if(json.has("compressed") && json.getBoolean("compressed")) {
                int decompressedLength = json.getInt("length");
                byte[] rawData = Base64.decode(json.getString("data"), Base64.DEFAULT);
                byte [] decompressed = decode(rawData, decompressedLength);
                //String jsonStr = decode(rawData);
                String jsonStr1 = new String(decompressed);

                json = new JSONObject(jsonStr1);
            }

            for (int i=1; i<= (json.getInt("count")); i++) {

                jsonPerson = json.getJSONObject(String.valueOf(i));
                if(searchTag_.equals("4")){
                    /*(String name, String husbandName, String healthId, int idIndex,
                    String sl,String elcoNo, String villageName, String mobileNo, String age,
                            String liveChildCount, String gravida, String lmp, String edd)*/
                    pregMotherList.add(new PregnantMother(jsonPerson.getString("name"),jsonPerson.getString("husbandName"),
                            jsonPerson.getString("healthId"),
                            jsonPerson.getInt("healthIdPop") == 1 ? 0 : 4,
                            String.valueOf(i),
                            jsonPerson.getString("elcoNo"),
                            villJson.getJSONObject(jsonPerson.getString("zillaId")).getJSONObject(jsonPerson.getString("upazilaId"))
                                    .getJSONObject(jsonPerson.getString("unionId")).getJSONObject(jsonPerson.getString("mouzaId"))
                                    .getString(jsonPerson.getString("villageId")),
                            /*jsonPerson.getString("VILLAGENAME"),*/
                            jsonPerson.getString("mobileNo"),
                            jsonPerson.getString("age"),
                            String.valueOf(Integer.valueOf(jsonPerson.getString("son"))+
                                    Integer.valueOf(jsonPerson.getString("dau"))),
                            jsonPerson.getString("gravida"),//gravida
                            jsonPerson.getString("LMP"),
                            jsonPerson.getString("EDD")));
                }else {
                    personsList.add(new Person(jsonPerson.getString("name") +
                            ((jsonPerson.getString("age").equals(""))?"":("(Age: "+jsonPerson.getString("age")+")")),
                            jsonPerson.getString("fatherName"),
                            jsonPerson.getString("husbandName"),
                            jsonPerson.getString("healthId"),
                            jsonPerson.getInt("healthIdPop") == 1 ? 0 : 4,
                            Integer.valueOf(searchTag_) !=2? 0:
                                    jsonSpinnerMap.get("gender").getSelectedItemPosition() != 1 ? R.drawable.man : R.drawable.woman,
                            jsonPerson.has("serviceDate") ? jsonPerson.getString("serviceDate"):""));
                }


            }
            initList();
        }
        catch (JSONException jse) {
            jse.printStackTrace();
        }            }
    @Override
    protected void initiateCheckboxes() {

    }

    @Override
    protected void initiateEditTexts() {
        if(searchTag_.equals("1")){
            jsonEditTextMap.put("mobileNo", getEditText(R.id.search_et_mobile_no));
        }else if(searchTag_.equals("2")){
            jsonEditTextMap.put("name", getEditText(R.id.advClient_name));
            getEditText(R.id.advClient_name).setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        }

    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateSpinners() {
        if(searchTag_.equals("2")){
            jsonSpinnerMap.put("gender", getSpinner(R.id.advClientsSexSpinner));
        }else if(searchTag_.equals("3")){
            jsonSpinnerMap.put("serviceType", getSpinner(R.id.advSearchServiceTypeSpinner));
        }

    }

    @Override
    protected void initiateMultiSelectionSpinners() {

    }

    @Override
    protected void initiateEditTextDates() {
        if(searchTag_.equals("3")){
            jsonEditTextDateMap.put("startDate", getEditText(R.id.searchStartDateEditText));
            jsonEditTextDateMap.put("endDate", getEditText(R.id.searchEndDateEditText));
        }
    }

    @Override
    protected void initiateRadioGroups() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onClick(View v) {
        //navigate to NRCActivity on add_new_member
        if (v.getId() == R.id.add_new_member) {
            Intent intent = new Intent(ADVSearchActivity.this, NRCActivity.class);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivityForResult(intent, ActivityResultCodes.REGISTRATION_ACTIVITY);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.advSearchDistrict:

                LocationHolder zilla = districtList.get(position);

                upazillaList.clear();
                upazilaAdapter.clear();
                upazillaList.add(blanc);
                LocationHolder.loadListFromJson(
                        zilla.getSublocation(),
                        "nameEnglishUpazila",
                        "nameBanglaUpazila",
                        "Union",
                        upazillaList);
                for (LocationHolder holder : upazillaList) {
                    Log.d(LOGTAG, "Upazila: -> " + holder.getBanglaName());
                }

                upazilaAdapter.addAll(upazillaList);
                upazilaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getSpinner(R.id.advSearchUpazila).setAdapter(upazilaAdapter);

                //during retrieval....
                if(prevUpazila!=null){
                    LocationHolder prevUpazillaObjectVal=null;
                    for(LocationHolder singleLoc:upazillaList){
                        if(singleLoc.getCode().equals(prevUpazila)){
                            prevUpazillaObjectVal=singleLoc;
                        }
                    }
                    int pos=0;
                    if(prevUpazillaObjectVal!=null){
                        pos = upazilaAdapter.getPosition(prevUpazillaObjectVal);
                    }
                    getSpinner(R.id.advSearchUpazila).setSelection(pos);
                }

                break;
            case R.id.advSearchUpazila:

                LocationHolder upazila = upazillaList.get(position);
                unionList.clear();
                unionAdapter.clear();
                unionList.add(new LocationHolder());
                LocationHolder.loadListFromJson(
                        upazila.getSublocation(),
                        "nameEnglishUnion",
                        "nameBanglaUnion",
                        "",
                        unionList);
                for (LocationHolder holder : unionList) {
                    Log.d(LOGTAG, "Union: -> " + holder.getBanglaName());
                }
                unionAdapter.addAll(unionList);
                getSpinner(R.id.advSearchUnion).setAdapter(unionAdapter);

                //during retrieval....
                if(prevUnion!=null){
                    LocationHolder prevUnionObjectVal=null;
                    for(LocationHolder singleLoc:unionList){
                        if(singleLoc.getCode().equals(prevUnion)){
                            prevUnionObjectVal=singleLoc;
                        }
                    }
                    int pos=0;
                    if(prevUnionObjectVal!=null){
                        pos = unionAdapter.getPosition(prevUnionObjectVal);
                    }
                    getSpinner(R.id.advSearchUnion).setSelection(pos);
                }
                break;
            case R.id.advSearchUnion:
                villageList.clear();
                villageAdapter.clear();
                villageList.add(blanc);

                loadVillageFromJson(
                        ((LocationHolder) getSpinner(R.id.advSearchDistrict).getSelectedItem()).getCode().split("_")[0],
                        ((LocationHolder) getSpinner(R.id.advSearchUpazila).getSelectedItem()).getCode(),
                        ((LocationHolder) getSpinner(R.id.advSearchUnion).getSelectedItem()).getCode(),
                        villageList);
                for (LocationHolder holder : villageList) {
                    Log.d(LOGTAG, "Village: -> " + holder.getBanglaName());
                }

                villageAdapter.addAll(villageList);
                getSpinner(R.id.advSearchVillage).setAdapter(villageAdapter);

                Log.d(LOGTAG, "Union Case: -> ");

                break;
            case R.id.advSearchVillage:
                Log.d(LOGTAG, "Village Case: -> ");
                break;
            default:
                Log.e(LOGTAG, "Unknown spinner: " + parent.getId()
                        + " -> " + getResources().getResourceEntryName(parent.getId()));

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setSearchability(boolean enable) {
        if(enable) {
            Utilities.Enable(this, R.id.advSearchBtn);
        } else {
            Utilities.Disable(this, R.id.advSearchBtn);
        }
    }

    private byte[]  decode(byte[] compressedBytes, int decompressedByteCount) {
        Inflater inflater = new Inflater(true);
        inflater.setInput(compressedBytes, 0, compressedBytes.length);
        byte[] decompressedBytes = new byte[decompressedByteCount];
        try {
            if (inflater.inflate(decompressedBytes) != decompressedByteCount) {
                throw new AssertionError();
            }
        } catch (DataFormatException DFE) {
            Log.e(LOGTAG, "Decompresseion Error: " + DFE.getMessage());
            Utilities.printTrace(DFE.getStackTrace());
        }
        inflater.end();

        return decompressedBytes;
    }

    public String decode(byte[] zbytes) {
        try {
            // Add extra byte to array when Inflater is set to true
            byte[] input = new byte[zbytes.length + 1];
            System.arraycopy(zbytes, 0, input, 0, zbytes.length);
            input[zbytes.length] = 0;
            ByteArrayInputStream bin = new ByteArrayInputStream(input);
            InflaterInputStream in = new InflaterInputStream(bin);
            ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
            int b;
            while ((b = in.read()) != -1) {
                bout.write(b);
            }
            bout.close();
            return bout.toString();
        } catch (IOException io) {
            Log.e(LOGTAG, "Unzipping failed" + io.getMessage());
            return null;
        }
    }

    class FileLoader extends AsyncTask<String, Integer, Integer> {
        Context context;
        FileLoader(Context c) {context = c;}
        protected Integer doInBackground(String... params) {
            loadLocations();
            return 0;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            getSpinner(R.id.advSearchDistrict).setAdapter(zillaAdapter);
            setProviderLocationByDefault();
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            setSearchability(true);
        }

        protected void onProgressUpdate (Integer... progress) {
            Toast.makeText(context,getText(R.string.string_village_loading), Toast.LENGTH_LONG).show();
        }
    }

    public void pickDate(View view) {
        datePickerDialog = null;
        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_SLASH_FORMAT_BRITISH);
        datePickerDialog.show(datePickerPair.get(view.getId()));

    }

    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialogWithResult(ADVSearchActivity.this,
                searchString, RESULT_CANCELED,ActivityResultCodes.ADV_SEARCH_ACTIVITY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityResultCodes.REGISTRATION_ACTIVITY && resultCode == RESULT_OK) {
            //passing NRCActivity result to SecondActivity
            data.putExtra("nrcAdded", true);
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
