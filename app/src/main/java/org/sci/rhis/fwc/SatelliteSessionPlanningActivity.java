package org.sci.rhis.fwc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.AsyncSatelliteSessionInfoUpate;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.satellite_session_planning.DBOperationSatellitePlanningInfo;
import org.sci.rhis.db.satellite_session_planning.RetrieveSatellitePlanningInfo;
import org.sci.rhis.model.LocationHolder;
import org.sci.rhis.model.SatelliteSessionEvents;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.AlertDialogCreator;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.NetworkUtility;
import org.sci.rhis.utilities.Utilities;

import org.sci.rhis.utilities.CustomSimpleDateFormat;
import org.sci.rhis.utilities.Validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by arafat.hasan on 3/6/2016.
 */
public class SatelliteSessionPlanningActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    /**
     * Variables........................................................................
     */

    protected ProviderInfo provider;

    private ArrayList<String> submittedPlanningIds = new ArrayList();

    private String LOGTAG = "SatelliteSP";


    private int intSateliteCount = 0;
    public static int intNoOfSatelliteInputByProvider;
    public static int[] intArrDays;
    public static String[] strArrSatelliteNames, strArrSatelliteNames1, strArrSatelliteNames2,
            strArrFWANames, strArrayFWAName1, strArrayFWAName2, strArrFWAIDs, strArrayFWAID1, strArrayFWAID2,
            strArrVillageNames, strArrayVillage1, strArrayVillage2, mouzaVillageIdPairs, mouzaVillageIdPairs1, mouzaVillageIdPairs2;
    private int intSatelliteCountPerWeek = 0;
    final int colorGreen = Color.rgb(143, 188, 143);

    private ArrayList<String> strListOfDaysInWeek = new ArrayList<>();
    private static List<SatelliteSessionEvents> listSatelliteEvents;
    public static boolean clicked = false;
    private static boolean is_save = false;
    private String TAG = "Satellite";

    ArrayList<LocationHolder> villageList;

    ArrayAdapter<LocationHolder> villageAdapter;

    public static boolean editModeOn = false;
    public int satelliteYear = 0;
    CalendarCustomView calendarCustomView;

    private String[] monthListSubmit;//Months Name
    private boolean[] checkedItems, disabledItems;
    ;
    private ArrayList<Integer> selectedMonths = new ArrayList<>();
    private AsyncSatelliteSessionInfoUpate sspUpdate;

    final private String SERVLET = "ssp";
    final private String ROOTKEY = "SSPInfo";

    Switch switchEdit;
    private String yearText;
    private int selectedYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satellite_session_planning);
        initialInit();
        initiateYearSpinner();

        switchEdit = (Switch) findViewById(R.id.switchEditSatellite);


        switchEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    editModeOn = true;
                    calendarCustomView = (CalendarCustomView) findViewById(R.id.custom_calendar);
                    calendarCustomView.setEnabled(true);

                    calendarCustomView.initializeUILayout();
                    calendarCustomView.setUpCalendarAdapter();
                    //calendarCustomView.setPreviousButtonClickEvent();
                    //calendarCustomView.setNextButtonClickEvent();

                    getTextView(R.id.textSatelliteHint).setText(R.string.SatellitePlanningEditInfo);
                    getLinearLayout(R.id.linearLayoutSatelliteHint).setBackgroundColor(getResources().getColor(R.color.lightsalmon));
                } else {
                    editModeOn = false;
                    calendarCustomView.setEnabled(false);
                    getTextView(R.id.textSatelliteHint).setText(R.string.SatellitePlanningAddInfo);
                    getButton(R.id.buttonSubmitSatelliteSessionPlanning).setVisibility(View.VISIBLE);
                    getLinearLayout(R.id.linearLayoutSatelliteHint).setBackgroundColor(getResources().getColor(R.color.fragment));
                }
            }
        });

    }



    /**
     * Initializing everything from UI elements to all necessary data
     */
    public void initialInit() {
        initialize();//Initializing all JSON Hashmaps

        provider = getIntent().getParcelableExtra("Provider");

        //This things are needed when we want to generate the satellite plan
        initDaysInBangla();
        listSatelliteEvents = new ArrayList<>();
        intArrDays = new int[]{-1, -1};
        intNoOfSatelliteInputByProvider = 0;
        strArrSatelliteNames1 = new String[4];
        strArrSatelliteNames2 = new String[4];
        strArrayFWAName1 = new String[4];
        strArrayFWAName2 = new String[4];
        strArrayFWAID1 = new String[4];
        strArrayFWAID2 = new String[4];
        strArrayVillage1 = new String[4];
        strArrayVillage2 = new String[4];
        mouzaVillageIdPairs1 = new String[4];
        mouzaVillageIdPairs2 = new String[4];

        villageList = new ArrayList<>();

        initiateSatelliteCountSpinner();

        initVillageList();

    }

    /**
     * Initiating current and next year in spinner
     * for provider to choose which year planning
     * s/he want to do
     */
    private void initiateYearSpinner() {
        ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add("");
        years.add(String.valueOf(thisYear));
        years.add(String.valueOf(thisYear + 1));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        getSpinner(R.id.satelliteYearSpinner).setBackground(getResources().getDrawable(R.drawable.gradient_spinner));
        getSpinner(R.id.satelliteYearSpinner).setGravity(Gravity.CENTER_VERTICAL);
        getSpinner(R.id.satelliteYearSpinner).setAdapter(adapter);
        getSpinner(R.id.satelliteYearSpinner).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 yearText = adapterView.getItemAtPosition(i).toString();
                /**
                 * Checking if the selected year is empty or not
                 * if empty - it will ask the user to select a year
                 * not empty - will load data and show it in the calender
                 *
                 * If year is selected and there is no data available it will ask the user for information
                 * if data is available in the selected year, then it will show it
                 * */
                if (!yearText.equals("")) {
                    satelliteYear = Integer.valueOf(yearText);
                    calendarCustomView = (CalendarCustomView) findViewById(R.id.custom_calendar);
                    calendarCustomView.setCalendar(Integer.valueOf(satelliteYear));

                    if (!loadExistingDataIfAny(satelliteYear)) {
                        findViewById(R.id.satelliteLayoutScrollview).setVisibility(View.VISIBLE);
                        Utilities.MakeVisible(SatelliteSessionPlanningActivity.this, R.id.LinearLayoutSatelliteCountPerWeekEntry);
                    } else {//Retrieve Satellite Session Planning
                        findViewById(R.id.LinearLayoutSatelliteCountPerWeekEntry).setVisibility(View.GONE);
                        findViewById(R.id.satelliteDaySelectorLayout).setVisibility(View.GONE);
                        findViewById(R.id.LinearLayoutSatelliteEntry).setVisibility(View.GONE);
                        findViewById(R.id.satelliteLayoutScrollview).setVisibility(View.VISIBLE);
                        showSatelliteCalendar();
                        editModeOn = false;
                        calendarCustomView.setEnabled(false);
                    }

                } else {
                    findViewById(R.id.satelliteLayoutScrollview).setVisibility(View.GONE);
                    Utilities.Reset(SatelliteSessionPlanningActivity.this, R.id.satelliteLayoutScrollview);
                    MethodUtils.showSnackBar(findViewById(R.id.layoutSateliteSessionPlanning),
                            "অনুগ্রহ করে বছর সিলেক্ট করুন", true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Load previous data based on the year
     */
    private boolean loadExistingDataIfAny(int planningYear) {
        boolean hasData = false;
        for (int i = 1; i <= 12; i++) { //Getting the whole year data
            listSatelliteEvents.addAll(RetrieveSatellitePlanningInfo.getSatelliteSessionEvents(i, planningYear, villageList));
        }
        if (listSatelliteEvents.size() > 0) hasData = true;
        return hasData;

    }


    /**
     * Loading village names in spinner based on the provider information
     */
    private void initVillageList() {
        villageAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        villageAdapter.clear();

        loadVillageFromJson((provider.getZillaID()).split("_")[0], provider.getUpazillaID(), provider.getUnionID(), villageList);
        for (LocationHolder holder : villageList) {
            Log.d(LOGTAG, "Village: -> " + holder.getBanglaName());
        }

        villageAdapter.addAll(villageList);
        getSpinner(R.id.Clients_Village01).setAdapter(villageAdapter);
        getSpinner(R.id.Clients_Village02).setAdapter(villageAdapter);
        getSpinner(R.id.Clients_Village03).setAdapter(villageAdapter);
        getSpinner(R.id.Clients_Village04).setAdapter(villageAdapter);
        getSpinner(R.id.Clients_Village05).setAdapter(villageAdapter);
        getSpinner(R.id.Clients_Village06).setAdapter(villageAdapter);
        getSpinner(R.id.Clients_Village07).setAdapter(villageAdapter);
        getSpinner(R.id.Clients_Village08).setAdapter(villageAdapter);

        //Changing FWA Names and ID based on the village
        //TODO: Have to delete below codes when releasing
        /*getSpinner(R.id.Clients_Village01).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0 || position == 1 || position == 2) {
                    getEditText(R.id.editTextFWAName01).setText("NANDA DEB");
                    getEditText(R.id.editTextFWAId01).setText("363027");
                } else if (position == 3 || position == 4 || position == 5) {
                    getEditText(R.id.editTextFWAName01).setText("BINA RANI DAS");
                    getEditText(R.id.editTextFWAId01).setText("363026");
                } else if (position == 6 || position == 7 || position == 8) {
                    getEditText(R.id.editTextFWAName01).setText("SWAPNA RANI DAS (CHW)");
                    getEditText(R.id.editTextFWAId01).setText("363025");
                } else if (position == 9 || position == 10 || position == 11) {
                    getEditText(R.id.editTextFWAName01).setText("SHARMIN AKTER");
                    getEditText(R.id.editTextFWAId01).setText("363024");
                } else {
                    getEditText(R.id.editTextFWAName01).setText("RUJINA AKTER (CHW)");
                    getEditText(R.id.editTextFWAId01).setText("363028");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSpinner(R.id.Clients_Village02).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0 || position == 1 || position == 2) {
                    getEditText(R.id.editTextFWAName02).setText("NANDA DEB");
                    getEditText(R.id.editTextFWAId02).setText("363027");
                } else if (position == 3 || position == 4 || position == 5) {
                    getEditText(R.id.editTextFWAName02).setText("BINA RANI DAS");
                    getEditText(R.id.editTextFWAId02).setText("363026");
                } else if (position == 6 || position == 7 || position == 8) {
                    getEditText(R.id.editTextFWAName02).setText("SWAPNA RANI DAS (CHW)");
                    getEditText(R.id.editTextFWAId02).setText("363025");
                } else if (position == 9 || position == 10 || position == 11) {
                    getEditText(R.id.editTextFWAName02).setText("SHARMIN AKTER");
                    getEditText(R.id.editTextFWAId02).setText("363024");
                } else {
                    getEditText(R.id.editTextFWAName02).setText("RUJINA AKTER (CHW)");
                    getEditText(R.id.editTextFWAId02).setText("363028");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSpinner(R.id.Clients_Village03).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0 || position == 1 || position == 2) {
                    getEditText(R.id.editTextFWAName03).setText("NANDA DEB");
                    getEditText(R.id.editTextFWAId03).setText("363027");
                } else if (position == 3 || position == 4 || position == 5) {
                    getEditText(R.id.editTextFWAName03).setText("BINA RANI DAS");
                    getEditText(R.id.editTextFWAId03).setText("363026");
                } else if (position == 6 || position == 7 || position == 8) {
                    getEditText(R.id.editTextFWAName03).setText("SWAPNA RANI DAS (CHW)");
                    getEditText(R.id.editTextFWAId03).setText("363025");
                } else if (position == 9 || position == 10 || position == 11) {
                    getEditText(R.id.editTextFWAName03).setText("SHARMIN AKTER");
                    getEditText(R.id.editTextFWAId03).setText("363024");
                } else {
                    getEditText(R.id.editTextFWAName03).setText("RUJINA AKTER (CHW)");
                    getEditText(R.id.editTextFWAId03).setText("363028");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSpinner(R.id.Clients_Village04).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0 || position == 1 || position == 2) {
                    getEditText(R.id.editTextFWAName04).setText("NANDA DEB");
                    getEditText(R.id.editTextFWAId04).setText("363027");
                } else if (position == 3 || position == 4 || position == 5) {
                    getEditText(R.id.editTextFWAName04).setText("BINA RANI DAS");
                    getEditText(R.id.editTextFWAId04).setText("363026");
                } else if (position == 6 || position == 7 || position == 8) {
                    getEditText(R.id.editTextFWAName04).setText("SWAPNA RANI DAS (CHW)");
                    getEditText(R.id.editTextFWAId04).setText("363025");
                } else if (position == 9 || position == 10 || position == 11) {
                    getEditText(R.id.editTextFWAName04).setText("SHARMIN AKTER");
                    getEditText(R.id.editTextFWAId04).setText("363024");
                } else {
                    getEditText(R.id.editTextFWAName04).setText("RUJINA AKTER (CHW)");
                    getEditText(R.id.editTextFWAId04).setText("363028");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSpinner(R.id.Clients_Village05).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0 || position == 1 || position == 2) {
                    getEditText(R.id.editTextFWAName05).setText("NANDA DEB");
                    getEditText(R.id.editTextFWAId05).setText("363027");
                } else if (position == 3 || position == 4 || position == 5) {
                    getEditText(R.id.editTextFWAName05).setText("BINA RANI DAS");
                    getEditText(R.id.editTextFWAId05).setText("363026");
                } else if (position == 6 || position == 7 || position == 8) {
                    getEditText(R.id.editTextFWAName05).setText("SWAPNA RANI DAS (CHW)");
                    getEditText(R.id.editTextFWAId05).setText("363025");
                } else if (position == 9 || position == 10 || position == 11) {
                    getEditText(R.id.editTextFWAName05).setText("SHARMIN AKTER");
                    getEditText(R.id.editTextFWAId05).setText("363024");
                } else {
                    getEditText(R.id.editTextFWAName05).setText("RUJINA AKTER (CHW)");
                    getEditText(R.id.editTextFWAId05).setText("363028");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSpinner(R.id.Clients_Village06).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0 || position == 1 || position == 2) {
                    getEditText(R.id.editTextFWAName06).setText("NANDA DEB");
                    getEditText(R.id.editTextFWAId06).setText("363027");
                } else if (position == 3 || position == 4 || position == 5) {
                    getEditText(R.id.editTextFWAName06).setText("BINA RANI DAS");
                    getEditText(R.id.editTextFWAId06).setText("363026");
                } else if (position == 6 || position == 7 || position == 8) {
                    getEditText(R.id.editTextFWAName06).setText("SWAPNA RANI DAS (CHW)");
                    getEditText(R.id.editTextFWAId06).setText("363025");
                } else if (position == 9 || position == 10 || position == 11) {
                    getEditText(R.id.editTextFWAName06).setText("SHARMIN AKTER");
                    getEditText(R.id.editTextFWAId06).setText("363024");
                } else {
                    getEditText(R.id.editTextFWAName06).setText("RUJINA AKTER (CHW)");
                    getEditText(R.id.editTextFWAId06).setText("363028");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSpinner(R.id.Clients_Village07).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0 || position == 1 || position == 2) {
                    getEditText(R.id.editTextFWAName07).setText("NANDA DEB");
                    getEditText(R.id.editTextFWAId07).setText("363027");
                } else if (position == 3 || position == 4 || position == 5) {
                    getEditText(R.id.editTextFWAName07).setText("BINA RANI DAS");
                    getEditText(R.id.editTextFWAId07).setText("363026");
                } else if (position == 6 || position == 7 || position == 8) {
                    getEditText(R.id.editTextFWAName07).setText("SWAPNA RANI DAS (CHW)");
                    getEditText(R.id.editTextFWAId07).setText("363025");
                } else if (position == 9 || position == 10 || position == 11) {
                    getEditText(R.id.editTextFWAName07).setText("SHARMIN AKTER");
                    getEditText(R.id.editTextFWAId07).setText("363024");
                } else {
                    getEditText(R.id.editTextFWAName07).setText("RUJINA AKTER (CHW)");
                    getEditText(R.id.editTextFWAId07).setText("363028");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSpinner(R.id.Clients_Village08).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0 || position == 1 || position == 2) {
                    getEditText(R.id.editTextFWAName08).setText("NANDA DEB");
                    getEditText(R.id.editTextFWAId08).setText("363027");
                } else if (position == 3 || position == 4 || position == 5) {
                    getEditText(R.id.editTextFWAName08).setText("BINA RANI DAS");
                    getEditText(R.id.editTextFWAId08).setText("363026");
                } else if (position == 6 || position == 7 || position == 8) {
                    getEditText(R.id.editTextFWAName08).setText("SWAPNA RANI DAS (CHW)");
                    getEditText(R.id.editTextFWAId08).setText("363025");
                } else if (position == 9 || position == 10 || position == 11) {
                    getEditText(R.id.editTextFWAName08).setText("SHARMIN AKTER");
                    getEditText(R.id.editTextFWAId08).setText("363024");
                } else {
                    getEditText(R.id.editTextFWAName08).setText("RUJINA AKTER (CHW)");
                    getEditText(R.id.editTextFWAId08).setText("363028");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
    }


    @Override
    public void callbackAsyncTask(String result) {

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

    public static List<SatelliteSessionEvents> getEventObjects() {
        return listSatelliteEvents;
    }

    public static void setEventObjects(List<SatelliteSessionEvents> listEvents) {
        listSatelliteEvents = listEvents;
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ButtonSat:
                resetAllButtons(R.id.ButtonSat, 7);
                break;
            case R.id.ButtonSun:
                resetAllButtons(R.id.ButtonSun, 1);
                break;
            case R.id.ButtonMon:
                resetAllButtons(R.id.ButtonMon, 2);
                break;
            case R.id.ButtonTue:
                resetAllButtons(R.id.ButtonTue, 3);
                break;
            case R.id.ButtonWed:
                resetAllButtons(R.id.ButtonWed, 4);
                break;
            case R.id.ButtonThu:
                resetAllButtons(R.id.ButtonThu, 5);
                break;

            case R.id.buttonGenerate:
                if (!hasTheRequiredFileds()) { }
                else {
                    getSatelliteNames();
                    setEvents();
                    Utilities.MakeInvisible(this, R.id.satelliteDaySelectorLayout);
                    Utilities.MakeInvisible(this, R.id.LinearLayoutSatelliteCountPerWeekEntry);
                    Utilities.MakeInvisible(this, R.id.LinearLayoutSatelliteEntry);

                    for (int i = 1; i <= 12; i++) {
                        String newID = satelliteYear +
                                String.format(Locale.ENGLISH,"%02d", i) + String.valueOf(provider.getmFacilityId());//e.g.201901367117
                                //String.format("%02d", i) + String.valueOf(provider.getmFacilityId());//e.g.201901367117
                        if(DBOperationSatellitePlanningInfo.insertSatelliteSessionPlanning(listSatelliteEvents, newID, satelliteYear, i, provider)){
                            is_save = true;
                        }else{
                            is_save = false;
                            break;
                            //TODO:has to write code for rollback if the process is failed in any loop
                        }

                    }
                    if(is_save){
                        AlertDialogCreator.SimpleMessageWithNoTitle(this, "স্যাটেলাইট ক্লিনিক পরিকল্পনা সফল ভাবে সংরক্ষিত হয়েছে।");
                    }else{

                        Utilities.showAlertToast(SatelliteSessionPlanningActivity.this,"স্যাটেলাইট ক্লিনিক পরিকল্পনা সফল ভাবে সংরক্ষিত হয়নি, অনুগ্রহ করে আবার চেষ্টা করুন অথবা উর্ধ্বতন কর্তৃপক্ষকে অবহিত করুন");
                    }


                    findViewById(R.id.satelliteLayoutScrollview).setVisibility(View.VISIBLE);

                    /*findViewById(R.id.LinearLayoutSatelliteCountPerWeekEntry).setVisibility(View.GONE);
                    findViewById(R.id.satelliteDaySelectorLayout).setVisibility(View.GONE);
                    findViewById(R.id.LinearLayoutSatelliteEntry).setVisibility(View.GONE);
                    findViewById(R.id.satelliteLayoutScrollview).setVisibility(View.VISIBLE);*/

                    showSatelliteCalendar();
                    showButtonsAfterSave();
                }
                break;

            case R.id.buttonSubmitSatelliteSessionPlanning:

                //this checking is required to be executed here as the submission portion is online only
                NetworkUtility networkUtility = new NetworkUtility(SatelliteSessionPlanningActivity.this);
                if (networkUtility != null && !networkUtility.isOnline()) {
                    AlertDialogCreator.SimpleMessageDialog(SatelliteSessionPlanningActivity.this,
                            getString(R.string.str_service_offline_not_available), getString(R.string.str_internet_need),
                            android.R.drawable.ic_dialog_info);
                } else {
                    //TODO: this array should be dynamic
                    //Holding the Months list

                    ArrayList<String> monthnameList = MethodUtils.sortKeysByMapValues(ConstantMaps.MonthIndexing);
                    monthListSubmit = new String[monthnameList.size()];
                    monthListSubmit = monthnameList.toArray(monthListSubmit);
                    checkedItems = new boolean[monthListSubmit.length];
                    disabledItems = new boolean[monthListSubmit.length];
                    disableSubmittedPlanInDialog(satelliteYear);

                    AlertDialog.Builder monthSelection = new AlertDialog.Builder(SatelliteSessionPlanningActivity.this);
                    monthSelection.setTitle(R.string.satellite_months_selection);

                    monthSelection.setMultiChoiceItems(monthListSubmit, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                            if (isChecked) {
                                selectedMonths.add(ConstantMaps.MonthIndexing.get(monthListSubmit[position]));
                            } else {
                                selectedMonths.remove(ConstantMaps.MonthIndexing.get(monthListSubmit[position]));
                            }
                        }
                    });
                    monthSelection.setCancelable(false);
                    monthSelection.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {


                            if (selectedMonths.size() == 0) {
                                Utilities.showAlertToast(SatelliteSessionPlanningActivity.this, "সাবমিট করার জন্য আপনি নতুন কোন মাস সিলেক্ট করেননি !");
                            } else {
                                JSONObject completeInsertJSON = new JSONObject();
                                try {
                                    JSONObject insertDataPartAsJSON = null;
                                    insertDataPartAsJSON = new JSONObject();

                                    for (int i = 0; i < selectedMonths.size(); i++) {
                                        String query = "select planning_id from satelite_session_planning where year =" + satelliteYear + " and month  in (" + selectedMonths.get(i) + ")";
                                        Log.i("QUERY", query);
                                        String planningID = CommonQueryExecution.getSingleColumnFromSingleQueryResult(query);
                                        submittedPlanningIds.add(planningID);

                                        JSONObject singleDataJSON = null;
                                        singleDataJSON = new JSONObject();


                                        singleDataJSON.put("main",
                                                CommonQueryExecution.getSingleRowJSONObject(Constants.TABLE_SATELLITE_SESSION_PLANNING, "planning_id=" + planningID));
                                        singleDataJSON.put("detail",
                                                CommonQueryExecution.getJSONObjectFromTable(Constants.TABLE_SATELLITE_SESSION_PLANNING_DETAIL, "planning_id=" + planningID));

                                        insertDataPartAsJSON.put(planningID, singleDataJSON);

                                    }

                                    completeInsertJSON.put("operation", "insert");
                                    completeInsertJSON.put("planning", insertDataPartAsJSON);
                                    startOnlineSubmission(completeInsertJSON);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.i("Final Submit JSON", completeInsertJSON.toString());


                            }
                        }
                    });

                    monthSelection.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });


                    AlertDialog mDialog = monthSelection.create();
                    mDialog.getListView().setOnHierarchyChangeListener(
                            new ViewGroup.OnHierarchyChangeListener() {
                                @Override
                                public void onChildViewAdded(View parent, View child) {
                                    CharSequence text = ((AppCompatCheckedTextView) child).getText();
                                    int itemIndex = Arrays.asList(monthListSubmit).indexOf(text);
                                    child.setEnabled(disabledItems[itemIndex]);
                                    if (!disabledItems[itemIndex])
                                        child.setOnClickListener(null);
                                }

                                @Override
                                public void onChildViewRemoved(View view, View view1) {
                                }
                            });
                    mDialog.show();
                }

                break;

            case R.id.linearLayoutSatelliteNameDay01:
                makeCollapsible(R.id.linearLayoutSatelliteNameContainer01);
                break;

            case R.id.linearLayoutSatelliteNameDay02:
                makeCollapsible(R.id.linearLayoutSatelliteNameContainer02);
                break;

            case R.id.linearLayoutSatelliteNameDay03:
                makeCollapsible(R.id.linearLayoutSatelliteNameContainer03);
                break;

            case R.id.linearLayoutSatelliteNameDay04:
                makeCollapsible(R.id.linearLayoutSatelliteNameContainer04);
                break;

            case R.id.linearLayoutSatelliteNameDay05:
                makeCollapsible(R.id.linearLayoutSatelliteNameContainer05);
                break;

            case R.id.linearLayoutSatelliteNameDay06:
                makeCollapsible(R.id.linearLayoutSatelliteNameContainer06);
                break;

            case R.id.linearLayoutSatelliteNameDay07:
                makeCollapsible(R.id.linearLayoutSatelliteNameContainer07);
                break;

            case R.id.linearLayoutSatelliteNameDay08:
                makeCollapsible(R.id.linearLayoutSatelliteNameContainer08);
                break;

            default:
                break;

        }

        /**
         * Showing satellite Center Name in the collapsible view
         * */
        getEditText(R.id.editTextSatelliteName01).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getTextView(R.id.textViewSatelliteName01).setText(getResources().getString(R.string.first) + " " + strListOfDaysInWeek.get(intArrDays[0]) + ":        " + getEditText(R.id.editTextSatelliteName01).getText().toString());
            }
        });
        getEditText(R.id.editTextSatelliteName02).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getTextView(R.id.textViewSatelliteName02).setText(getResources().getString(R.string.first) + " " + strListOfDaysInWeek.get(intArrDays[1]) + ":       " + getTextView(R.id.editTextSatelliteName02).getText().toString());
            }
        });
        getEditText(R.id.editTextSatelliteName03).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getTextView(R.id.textViewSatelliteName03).setText(getResources().getString(R.string.second) + " " + strListOfDaysInWeek.get(intArrDays[0]) + ":       " + getTextView(R.id.editTextSatelliteName03).getText().toString());
            }
        });
        getEditText(R.id.editTextSatelliteName04).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getTextView(R.id.textViewSatelliteName04).setText(getResources().getString(R.string.second) + " " + strListOfDaysInWeek.get(intArrDays[1]) + ":        " + getTextView(R.id.editTextSatelliteName04).getText().toString());
            }
        });
        getEditText(R.id.editTextSatelliteName05).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getTextView(R.id.textViewSatelliteName05).setText(getResources().getString(R.string.third) + " " + strListOfDaysInWeek.get(intArrDays[0]) + ":        " + getEditText(R.id.editTextSatelliteName05).getText().toString());
            }
        });
        getEditText(R.id.editTextSatelliteName06).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getTextView(R.id.textViewSatelliteName06).setText(getResources().getString(R.string.third) + " " + strListOfDaysInWeek.get(intArrDays[1]) + ":        " + getEditText(R.id.editTextSatelliteName06).getText().toString());
            }
        });
        getEditText(R.id.editTextSatelliteName07).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getTextView(R.id.textViewSatelliteName07).setText(getResources().getString(R.string.fourth) + " " + strListOfDaysInWeek.get(intArrDays[0]) + ":        " + getEditText(R.id.editTextSatelliteName07).getText().toString());
            }
        });
        getEditText(R.id.editTextSatelliteName08).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getTextView(R.id.textViewSatelliteName08).setText(getResources().getString(R.string.fourth) + " " + strListOfDaysInWeek.get(intArrDays[1]) + ":        " + getEditText(R.id.editTextSatelliteName08).getText().toString());
            }
        });


    }

    private void makeCollapsible(int view) {
        if (getLinearLayout(view).getVisibility() == View.VISIBLE) {
            Utilities.MakeInvisible(this, view);
        } else {
            Utilities.MakeVisible(this, view);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        AlertDialogCreator.ExitActivityDialogWithResult(this, "", RESULT_OK, ActivityResultCodes.FP_ACTIVITY);
    }

    /**
     * Initializing the List of days of a week in bengali
     */
    private void initDaysInBangla() {
        strListOfDaysInWeek.add("");
        strListOfDaysInWeek.add(getResources().getString(R.string.sunday));
        strListOfDaysInWeek.add(getResources().getString(R.string.monday));
        strListOfDaysInWeek.add(getResources().getString(R.string.tuesday));
        strListOfDaysInWeek.add(getResources().getString(R.string.wednesday));
        strListOfDaysInWeek.add(getResources().getString(R.string.thursday));
        strListOfDaysInWeek.add("");
        strListOfDaysInWeek.add(getResources().getString(R.string.saturday));
    }

    /**
     * make the day buttons visible or invisible accordingly
     *
     * @param button button id
     * @param value  value of the selected button/day
     */
    private void resetAllButtons(int button, int value) {
        if (intSateliteCount < intSatelliteCountPerWeek) {
            getButton(button).setBackgroundColor(colorGreen);
            intArrDays[intSateliteCount] = value;
            if(intSateliteCount==0)
            {
                intSateliteCount++;
            }
            else if(intArrDays[1]!=intArrDays[0])
            {
                intSateliteCount++;
            }
            //intSateliteCount++;

            if (intSateliteCount == intSatelliteCountPerWeek) {
                //intArrDays[intSateliteCount] = value;
                String strSatelliteNames = Utilities.ConvertNumberToBangla(String.valueOf(intNoOfSatelliteInputByProvider)) + getResources().getString(R.string.satelliteNames);
                getTextView(R.id.textViewEnterSatellinteNames).setText(strSatelliteNames);
                getLinearLayout(R.id.LinearLayoutSatelliteEntry).setVisibility(View.VISIBLE);
                if (intNoOfSatelliteInputByProvider == 4) {
                    reset();
                    makeVisible4SatelliteNames();
                } else {
                    //reset();
                    makeVisible8SatelliteNames();
                }

            }
        } else {
            intSateliteCount = 0;
            getButton(R.id.ButtonSat).setBackground(getResources().getDrawable(R.drawable.rectangle));
            getButton(R.id.ButtonSun).setBackground(getResources().getDrawable(R.drawable.rectangle));
            getButton(R.id.ButtonMon).setBackground(getResources().getDrawable(R.drawable.rectangle));
            getButton(R.id.ButtonTue).setBackground(getResources().getDrawable(R.drawable.rectangle));
            getButton(R.id.ButtonWed).setBackground(getResources().getDrawable(R.drawable.rectangle));
            getButton(R.id.ButtonThu).setBackground(getResources().getDrawable(R.drawable.rectangle));

            getLinearLayout(R.id.LinearLayoutSatelliteEntry).setVisibility(View.GONE);
        }
    }

    /**
     * make first 4 input text field visible
     */
    private void reset(){
        /*getLinearLayout(R.id.linearLayoutSatelliteName01).setVisibility(View.GONE);
        getLinearLayout(R.id.linearLayoutSatelliteName03).setVisibility(View.GONE);
        getLinearLayout(R.id.linearLayoutSatelliteName05).setVisibility(View.GONE);
        getLinearLayout(R.id.linearLayoutSatelliteName07).setVisibility(View.GONE);*/

        getLinearLayout(R.id.linearLayoutSatelliteName02).setVisibility(View.GONE);
        getLinearLayout(R.id.linearLayoutSatelliteName04).setVisibility(View.GONE);
        getLinearLayout(R.id.linearLayoutSatelliteName06).setVisibility(View.GONE);
        getLinearLayout(R.id.linearLayoutSatelliteName08).setVisibility(View.GONE);
    }
    private void makeVisible4SatelliteNames() {

        getTextView(R.id.textViewSatelliteName01).setText(getResources().getString(R.string.first) + " " + strListOfDaysInWeek.get(intArrDays[0]));
        getTextView(R.id.textViewSatelliteName03).setText(getResources().getString(R.string.second) + " " + strListOfDaysInWeek.get(intArrDays[0]));
        getTextView(R.id.textViewSatelliteName05).setText(getResources().getString(R.string.third) + " " + strListOfDaysInWeek.get(intArrDays[0]));
        getTextView(R.id.textViewSatelliteName07).setText(getResources().getString(R.string.fourth) + " " + strListOfDaysInWeek.get(intArrDays[0]));
        getLinearLayout(R.id.linearLayoutSatelliteName01).setVisibility(View.VISIBLE);
        getLinearLayout(R.id.linearLayoutSatelliteName03).setVisibility(View.VISIBLE);
        getLinearLayout(R.id.linearLayoutSatelliteName05).setVisibility(View.VISIBLE);
        getLinearLayout(R.id.linearLayoutSatelliteName07).setVisibility(View.VISIBLE);
        getButton(R.id.buttonGenerate).setVisibility(View.VISIBLE);


    }

    /**
     * make all the input text field visible
     */
    private void makeVisible8SatelliteNames() {
        makeVisible4SatelliteNames();
        getTextView(R.id.textViewSatelliteName02).setText(getResources().getString(R.string.first) + " " + strListOfDaysInWeek.get(intArrDays[1]));
        getTextView(R.id.textViewSatelliteName04).setText(getResources().getString(R.string.second) + " " + strListOfDaysInWeek.get(intArrDays[1]));
        getTextView(R.id.textViewSatelliteName06).setText(getResources().getString(R.string.third) + " " + strListOfDaysInWeek.get(intArrDays[1]));
        getTextView(R.id.textViewSatelliteName08).setText(getResources().getString(R.string.fourth) + " " + strListOfDaysInWeek.get(intArrDays[1]));
        getLinearLayout(R.id.linearLayoutSatelliteName02).setVisibility(View.VISIBLE);
        getLinearLayout(R.id.linearLayoutSatelliteName04).setVisibility(View.VISIBLE);
        getLinearLayout(R.id.linearLayoutSatelliteName06).setVisibility(View.VISIBLE);
        getLinearLayout(R.id.linearLayoutSatelliteName08).setVisibility(View.VISIBLE);
        getButton(R.id.buttonGenerate).setVisibility(View.VISIBLE);
    }

    /**
     * get the satellite names into 2 different string array for 2 different day of week
     */
    private void getSatelliteNames() {

        strArrSatelliteNames = new String[intNoOfSatelliteInputByProvider];
        strArrFWANames = new String[intNoOfSatelliteInputByProvider];
        strArrFWAIDs = new String[intNoOfSatelliteInputByProvider];
        strArrVillageNames = new String[intNoOfSatelliteInputByProvider];
        mouzaVillageIdPairs = new String[intNoOfSatelliteInputByProvider];
        //Satellite Names
        strArrSatelliteNames1[0] = getTextView(R.id.editTextSatelliteName01).getText().toString();
        strArrSatelliteNames1[1] = getTextView(R.id.editTextSatelliteName03).getText().toString();
        strArrSatelliteNames1[2] = getTextView(R.id.editTextSatelliteName05).getText().toString();
        strArrSatelliteNames1[3] = getTextView(R.id.editTextSatelliteName07).getText().toString();
        strArrSatelliteNames2[0] = getTextView(R.id.editTextSatelliteName02).getText().toString();
        strArrSatelliteNames2[1] = getTextView(R.id.editTextSatelliteName04).getText().toString();
        strArrSatelliteNames2[2] = getTextView(R.id.editTextSatelliteName06).getText().toString();
        strArrSatelliteNames2[3] = getTextView(R.id.editTextSatelliteName08).getText().toString();
        List<String> strArrayList = new ArrayList<>();
        Collections.addAll(strArrayList, strArrSatelliteNames1);
        if (intNoOfSatelliteInputByProvider > 4) {
            Collections.addAll(strArrayList, strArrSatelliteNames2);
        }
        strArrSatelliteNames = strArrayList.toArray(new String[strArrayList.size()]);


        //FWA Names
        strArrayFWAName1[0] = getTextView(R.id.editTextFWAName01).getText().toString();
        strArrayFWAName1[1] = getTextView(R.id.editTextFWAName03).getText().toString();
        strArrayFWAName1[2] = getTextView(R.id.editTextFWAName05).getText().toString();
        strArrayFWAName1[3] = getTextView(R.id.editTextFWAName07).getText().toString();
        strArrayFWAName2[0] = getTextView(R.id.editTextFWAName02).getText().toString();
        strArrayFWAName2[1] = getTextView(R.id.editTextFWAName04).getText().toString();
        strArrayFWAName2[2] = getTextView(R.id.editTextFWAName06).getText().toString();
        strArrayFWAName2[3] = getTextView(R.id.editTextFWAName08).getText().toString();
        List<String> strArrayFWAList = new ArrayList<>();
        Collections.addAll(strArrayFWAList, strArrayFWAName1);
        if (intNoOfSatelliteInputByProvider > 4) {
            Collections.addAll(strArrayFWAList, strArrayFWAName2);
        }
        strArrFWANames = strArrayFWAList.toArray(new String[strArrayFWAList.size()]);

        //FWA IDs
        strArrayFWAID1[0] = getTextView(R.id.editTextFWAId01).getText().toString();
        strArrayFWAID1[1] = getTextView(R.id.editTextFWAId03).getText().toString();
        strArrayFWAID1[2] = getTextView(R.id.editTextFWAId05).getText().toString();
        strArrayFWAID1[3] = getTextView(R.id.editTextFWAId07).getText().toString();
        strArrayFWAID2[0] = getTextView(R.id.editTextFWAId02).getText().toString();
        strArrayFWAID2[1] = getTextView(R.id.editTextFWAId04).getText().toString();
        strArrayFWAID2[2] = getTextView(R.id.editTextFWAId06).getText().toString();
        strArrayFWAID2[3] = getTextView(R.id.editTextFWAId08).getText().toString();
        List<String> strArrayFWAIDList = new ArrayList<>();
        Collections.addAll(strArrayFWAIDList, strArrayFWAID1);
        if (intNoOfSatelliteInputByProvider > 4) {
            Collections.addAll(strArrayFWAIDList, strArrayFWAID2);
        }
        strArrFWAIDs = strArrayFWAIDList.toArray(new String[strArrayFWAIDList.size()]);

        //Village Names
        strArrayVillage1[0] = getSpinner(R.id.Clients_Village01).getSelectedItem().toString();
        strArrayVillage1[1] = getSpinner(R.id.Clients_Village03).getSelectedItem().toString();
        strArrayVillage1[2] = getSpinner(R.id.Clients_Village05).getSelectedItem().toString();
        strArrayVillage1[3] = getSpinner(R.id.Clients_Village07).getSelectedItem().toString();
        strArrayVillage2[0] = getSpinner(R.id.Clients_Village02).getSelectedItem().toString();
        strArrayVillage2[1] = getSpinner(R.id.Clients_Village04).getSelectedItem().toString();
        strArrayVillage2[2] = getSpinner(R.id.Clients_Village06).getSelectedItem().toString();
        strArrayVillage2[3] = getSpinner(R.id.Clients_Village08).getSelectedItem().toString();
        //setting mouza & village Id Pair
        mouzaVillageIdPairs1[0] = ((LocationHolder) getSpinner(R.id.Clients_Village01).getSelectedItem()).getCode();
        mouzaVillageIdPairs1[1] = ((LocationHolder) getSpinner(R.id.Clients_Village03).getSelectedItem()).getCode();
        mouzaVillageIdPairs1[2] = ((LocationHolder) getSpinner(R.id.Clients_Village05).getSelectedItem()).getCode();
        mouzaVillageIdPairs1[3] = ((LocationHolder) getSpinner(R.id.Clients_Village07).getSelectedItem()).getCode();
        mouzaVillageIdPairs2[0] = ((LocationHolder) getSpinner(R.id.Clients_Village02).getSelectedItem()).getCode();
        mouzaVillageIdPairs2[1] = ((LocationHolder) getSpinner(R.id.Clients_Village04).getSelectedItem()).getCode();
        mouzaVillageIdPairs2[2] = ((LocationHolder) getSpinner(R.id.Clients_Village06).getSelectedItem()).getCode();
        mouzaVillageIdPairs2[3] = ((LocationHolder) getSpinner(R.id.Clients_Village08).getSelectedItem()).getCode();
        List<String> strArrayVillageList = new ArrayList<>();
        List<String> mouzaVillageIdList = new ArrayList<>();
        Collections.addAll(strArrayVillageList, strArrayVillage1);
        Collections.addAll(mouzaVillageIdList, mouzaVillageIdPairs1);
        if (intNoOfSatelliteInputByProvider > 4) {
            Collections.addAll(strArrayVillageList, strArrayVillage2);
            Collections.addAll(mouzaVillageIdList, mouzaVillageIdPairs2);
        }
        strArrVillageNames = strArrayVillageList.toArray(new String[strArrayVillageList.size()]);
        mouzaVillageIdPairs = mouzaVillageIdList.toArray(new String[mouzaVillageIdList.size()]);

    }

    /**
     * sets the events to the List<SatelliteSessionEvents> listSatelliteEvents
     */
    private void setEvents() {


        try {

            int noOfSatellite = SatelliteSessionPlanningActivity.intNoOfSatelliteInputByProvider;
            HashMap<String, Integer> hashMapSatellite = new HashMap<>();
            List list = new ArrayList(Arrays.asList(strArrSatelliteNames1));

            if (noOfSatellite > 4) {
                list.addAll(Arrays.asList(strArrSatelliteNames2));
            }

            for (int i = 1; i <= list.size(); i++) {
                hashMapSatellite.put(list.get(i - 1).toString(), i);
            }
            String s = "";
            for (Object entry : hashMapSatellite.keySet()) {
                s += "name = " + entry + ", id = " + hashMapSatellite.get(entry) + "; ";
            }

            for (int i = 0; i < 12; i++) {
                int intCount1 = 0, intCount2 = 0;
                int satellite_id = 1;
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, satelliteYear);
                c.set(Calendar.MONTH, i);
                c.set(Calendar.DAY_OF_MONTH,c.getActualMinimum(Calendar.DAY_OF_MONTH));

                for (int day = 1; day <= c.getActualMaximum(Calendar.DAY_OF_MONTH); day++) {

                    int dayValue = day;
                    c.set(Calendar.DAY_OF_MONTH, dayValue);
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
//change by fazlur
                    if (intNoOfSatelliteInputByProvider == 4) {
                        if (dayOfWeek == intArrDays[0]) {
                            if (intCount1 < 4) {
                                SatelliteSessionEvents satelliteSessionEvents = new SatelliteSessionEvents(hashMapSatellite.get(strArrSatelliteNames1[intCount1]),
                                        SatelliteSessionPlanningActivity.strArrSatelliteNames1[intCount1], c.getTime(), strArrayFWAName1[intCount1],
                                        strArrayFWAID1[intCount1], mouzaVillageIdPairs1[intCount1].split("_")[1], mouzaVillageIdPairs1[intCount1].split("_")[0],
                                        strArrayVillage1[intCount1]);
                                listSatelliteEvents.add(satelliteSessionEvents);
                                intCount1++;
                                satellite_id++;
                            }
                        }

                    }

                    if (intNoOfSatelliteInputByProvider != 4) {
                        if (dayOfWeek == intArrDays[0]) {
                            if (intCount1 < 4) {
                                SatelliteSessionEvents satelliteSessionEvents = new SatelliteSessionEvents(hashMapSatellite.get(strArrSatelliteNames1[intCount1]),
                                        SatelliteSessionPlanningActivity.strArrSatelliteNames1[intCount1], c.getTime(), strArrayFWAName1[intCount1],
                                        strArrayFWAID1[intCount1], mouzaVillageIdPairs1[intCount1].split("_")[1], mouzaVillageIdPairs1[intCount1].split("_")[0],
                                        strArrayVillage1[intCount1]);
                                listSatelliteEvents.add(satelliteSessionEvents);
                                intCount1++;
                                satellite_id++;
                            }
                        }
                        if (dayOfWeek == intArrDays[1]) {
                            if (intCount2 < 4) {
                                SatelliteSessionEvents satelliteSessionEvents = new SatelliteSessionEvents(hashMapSatellite.get(strArrSatelliteNames2[intCount2]),
                                        SatelliteSessionPlanningActivity.strArrSatelliteNames2[intCount2], c.getTime(), strArrayFWAName2[intCount2],
                                        strArrayFWAID2[intCount2], mouzaVillageIdPairs2[intCount2].split("_")[1], mouzaVillageIdPairs2[intCount2].split("_")[0],
                                        strArrayVillage2[intCount2]);
                                listSatelliteEvents.add(satelliteSessionEvents);
                                intCount2++;
                                satellite_id++;
                            }
                        }
                    }

                    if (intCount1 == 4 && intCount2 == 4) {
                        break;
                    }
                    if (satellite_id > 8) {
                        satellite_id = 0;
                    }
                }
            }
        }
        catch (Exception e) {
            Log.d("Satellite_session",  e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * removes a particular object from the events list whose date is matched
     *
     * @param dateObj
     */
    public static String[] removeEvent(Date dateObj) {
        String satelliteIDAndName[] = new String[2];
        for (int j = 0; j < getEventObjects().size(); j++) {
            SatelliteSessionEvents eventObject = getEventObjects().get(j);
            CustomSimpleDateFormat df = new CustomSimpleDateFormat("dd-MMM-yyyy");
            String formattedDateClicked = df.format(dateObj);
            String formattedDateObject = df.format(eventObject.getDate());
            if (formattedDateObject.equals(formattedDateClicked)) {
                //found, delete.
                satelliteIDAndName[0] = String.valueOf(eventObject.getId());
                satelliteIDAndName[1] = eventObject.getSatellite();

                if (editModeOn) {
                    DBOperationSatellitePlanningInfo.removeEvent(getEventObjects().get(j).getDate());
                    getEventObjects().remove(j);
                } else {
                    getEventObjects().remove(j);
                }
                break;
            }
        }
        return satelliteIDAndName;
    }

    /**
     * adds a event to the List of events
     *
     * @param satellite the name of the satellite
     * @param date      the date of the event
     */
    public static void addEvent(String satellite, Date date, String fwaName, String fwaID, String mouzaId, String villageId, String village) {

        SatelliteSessionEvents eventObject = new SatelliteSessionEvents(satellite, date, fwaName, fwaID, mouzaId, villageId, village);
        if (editModeOn) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH) + 1;
            long currentMonthId = RetrieveSatellitePlanningInfo.getCurrentMonthID(month);
            int planningDetailId = RetrieveSatellitePlanningInfo.getPlanningDetailID(currentMonthId);
            planningDetailId += 1;
            DBOperationSatellitePlanningInfo.addEvent(planningDetailId, currentMonthId, satellite, date, fwaName,
                    fwaID, mouzaId, villageId);
            getEventObjects().add(eventObject);
        } else {
            getEventObjects().add(eventObject);
        }
    }

    public static void loadVillageFromJson(String zilla, String upazila, String union, ArrayList<LocationHolder> holderList) {
        JSONObject villJson = null;
        try {
            if (villJson == null) {
                villJson = LocationHolder.getVillageJson();
            }

            if (union.equals("none") || upazila.equals("none") || zilla.equals("none") ||
                    union.equals("") || upazila.equals("") || zilla.equals("")) {
                return;
            }

            JSONObject unionJson =
                    villJson.getJSONObject(zilla).getJSONObject(String.valueOf(Integer.valueOf(upazila))).getJSONObject(union);

            for (Iterator<String> mouzaKey = unionJson.keys(); mouzaKey.hasNext(); ) {
                String mouza = mouzaKey.next();
                JSONObject mouzaJson = unionJson.getJSONObject(mouza);

                for (Iterator<String> villageCode = mouzaJson.keys(); villageCode.hasNext(); ) {
                    String code = villageCode.next();
                    holderList.add(
                            new LocationHolder(
                                    code + "_" + mouza,
                                    mouzaJson.getString(code),
                                    mouzaJson.getString(code),
                                    ""));

                }

            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }

    /**
     * Showing Calender and respective buttons after successful save
     */
    private void showSatelliteCalendar() {
        getLinearLayout(R.id.linearLayoutAddSatelliteYear).setVisibility(View.GONE);
        getLinearLayout(R.id.linearLayoutSatelliteCalendar).setVisibility(View.VISIBLE);

    }

    private void startOnlineSubmission(JSONObject insertJSONObject) {
        sspUpdate = new AsyncSatelliteSessionInfoUpate(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                handleResponse(result);
            }
        }, this);
        sspUpdate.execute(insertJSONObject.toString(), SERVLET, ROOTKEY);

    }

    private void handleResponse(String response) {
        try {
            if (response.contains("success") && new JSONObject(response).getString("success").equals("1")) {
                //updating status in satelite_session_planning table for the submitted plans
                for (String submittedPlanId : submittedPlanningIds) {
                    String updateQuery = "update satelite_session_planning set status=0 where planning_id = " + submittedPlanId;
                    CommonQueryExecution.executeQuery(updateQuery);
                }
                Toast.makeText(SatelliteSessionPlanningActivity.this, "স্যাটেলাইট সেশন প্লানিং সফল ভাবে সাবমিট হয়েছে।",
                        Toast.LENGTH_LONG).show();
                finish();
                Log.d(TAG, response);
            } else {
                Utilities.showAlertToast(SatelliteSessionPlanningActivity.this, "অনিবার্য কারন বশত আপনার স্যাটেলাইট সেশন প্লানিং সফলভাবে সাবমিট হয়নি ।" +
                        " অনুগ্রহপূর্বক আপনার ইন্টারনেট সংযোগ চেক করে পুনরায় চেষ্টা করুন ");
            }

        } catch (Exception e) {
            Utilities.showAlertToast(SatelliteSessionPlanningActivity.this, "অনিবার্য কারন বশত আপনার স্যাটেলাইট সেশন প্লানিং সফলভাবে সাবমিট হয়নি ।" +
                    " অনুগ্রহপূর্বক আপনার ইন্টারনেট সংযোগ চেক করে পুনরায় চেষ্টা করুন ");
        }
    }

    private void disableSubmittedPlanInDialog(int year) {
        for (int i = 1; i <= 12; i++) {
            //setting false to disable if any month's plan is submitted(0) or approved(1)
            disabledItems[i - 1] = !Arrays.asList(new Integer[]{0, 1})
                    .contains(DBOperationSatellitePlanningInfo.getStatus(year, i, ProviderInfo.getProvider().getmFacilityId()));
            //setting true to be checked if any month's plan is submitted(0) or approved(1)
            checkedItems[i - 1] = Arrays.asList(new Integer[]{0, 1})
                    .contains(DBOperationSatellitePlanningInfo.getStatus(year, i, ProviderInfo.getProvider().getmFacilityId()));

        }
    }

    /**
     * Based on how many days of satellite will be done, satellite input details input layout will be visible
     */
    private void initiateSatelliteCountSpinner() {
        ArrayList<String> satelliteCount = new ArrayList<>();
        satelliteCount.addAll(Arrays.asList(getResources().getStringArray(R.array.Satellite_Count_DropDown)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, satelliteCount);
        getSpinner(R.id.satelliteCountSpinner).setAdapter(adapter);
        getSpinner(R.id.satelliteCountSpinner).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long row_id) {
                String count = adapterView.getItemAtPosition(position).toString();
                if (!count.equals("")) {
                    switch (position) {
                        case 1:
                            //intSateliteCount = 0;
                            intSatelliteCountPerWeek = 1;
                            intNoOfSatelliteInputByProvider = 4;
                            getLinearLayout(R.id.satelliteDaySelectorLayout).setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            //intSateliteCount = 0;
                            intSatelliteCountPerWeek = 2;
                            intNoOfSatelliteInputByProvider = 8;
                            getLinearLayout(R.id.satelliteDaySelectorLayout).setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                } else {
                    getLinearLayout(R.id.satelliteDaySelectorLayout).setVisibility(View.GONE);
                    MethodUtils.showSnackBar(findViewById(R.id.layoutSateliteSessionPlanning),
                            "অনুগ্রহ করে সপ্তাহে কয়টি স্যাটেলাইট করবেন সিলেক্ট করুন", true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private boolean hasTheRequiredFileds() {
        boolean valid = true;
        if (!Validation.hasText(getEditText(R.id.editTextSatelliteName01))) valid = false;
        if (!Validation.hasText(getEditText(R.id.editTextSatelliteName03))) valid = false;
        if (!Validation.hasText(getEditText(R.id.editTextSatelliteName05))) valid = false;
        if (!Validation.hasText(getEditText(R.id.editTextSatelliteName07))) valid = false;
        if (intNoOfSatelliteInputByProvider == 8) {
            if (!Validation.hasText(getEditText(R.id.editTextSatelliteName02))) valid = false;
            if (!Validation.hasText(getEditText(R.id.editTextSatelliteName04))) valid = false;
            if (!Validation.hasText(getEditText(R.id.editTextSatelliteName06))) valid = false;
            if (!Validation.hasText(getEditText(R.id.editTextSatelliteName08))) valid = false;
        }
        if (!valid) {
            Toast toast = Toast.makeText(this, R.string.SatelliteNameWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        }

       /* if (!Validation.hasText(getEditText(R.id.editTextFWAName01))) valid = false;
        if (!Validation.hasText(getEditText(R.id.editTextFWAName03))) valid = false;
        if (!Validation.hasText(getEditText(R.id.editTextFWAName05))) valid = false;
        if (!Validation.hasText(getEditText(R.id.editTextFWAName07))) valid = false;
        if (intNoOfSatelliteInputByProvider == 8) {
            if (!Validation.hasText(getEditText(R.id.editTextFWAName02))) valid = false;
            if (!Validation.hasText(getEditText(R.id.editTextFWAName04))) valid = false;
            if (!Validation.hasText(getEditText(R.id.editTextFWAName06))) valid = false;
            if (!Validation.hasText(getEditText(R.id.editTextFWAName08))) valid = false;
        }
        if (!valid) {
            Toast toast = Toast.makeText(this, R.string.FWANameWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        }


        if (!Validation.hasText(getEditText(R.id.editTextFWAId01))) valid = false;
        if (!Validation.hasText(getEditText(R.id.editTextFWAId03))) valid = false;
        if (!Validation.hasText(getEditText(R.id.editTextFWAId05))) valid = false;
        if (!Validation.hasText(getEditText(R.id.editTextFWAId07))) valid = false;
        if (intNoOfSatelliteInputByProvider == 8) {
            if (!Validation.hasText(getEditText(R.id.editTextFWAId02))) valid = false;
            if (!Validation.hasText(getEditText(R.id.editTextFWAId04))) valid = false;
            if (!Validation.hasText(getEditText(R.id.editTextFWAId06))) valid = false;
            if (!Validation.hasText(getEditText(R.id.editTextFWAId08))) valid = false;
        }
        if (!valid) {
            Toast toast = Toast.makeText(this, R.string.FWAIDWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        }*/

        else {
            return true;
        }




    }

    private void showButtonsAfterSave()
    {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = (calendar.get(Calendar.MONTH) + 1);
        if(!yearText.equals("")){
            selectedYear = Integer.parseInt(yearText);
        }
        int status = DBOperationSatellitePlanningInfo.getStatus(selectedYear, currentMonth,ProviderInfo.getProvider().getmFacilityId());
        if(status <=1){
            findViewById(R.id.layoutEditSatellitePlanning).setVisibility(View.GONE);
            findViewById(R.id.buttonSubmitSatelliteSessionPlanning).setVisibility(View.GONE);
            findViewById(R.id.linearLayoutSatelliteHint).setVisibility(View.GONE);
        } else {
            findViewById(R.id.layoutEditSatellitePlanning).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonSubmitSatelliteSessionPlanning).setVisibility(View.VISIBLE);
            findViewById(R.id.linearLayoutSatelliteHint).setVisibility(View.VISIBLE);
        }

    }

}
