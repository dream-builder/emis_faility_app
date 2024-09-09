package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.connectivityhandler.AsyncDeliveryInfoUpdate;
import org.sci.rhis.model.PregWoman;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.util.HashMap;


public class MinimumDeliveryInfoFragment extends Fragment {

    public interface DeliverySavedListener {
        void onDeliverySaved(String result);
    }

    public interface PatientAndProviderDetailsRequiredListener {
        Pair<PregWoman, ProviderInfo> onProviderAndPatientRequired();
    }

    private final String LOGTAG = "FWC-MIN-DELIVERY";
    final private String SERVLET = "delivery";
    final private String ROOTKEY = "deliveryInfo";


    private View masterView = null;
    private PregWoman woman = null;
    private ProviderInfo provider = null;
    private Activity parentActivity = null;
    private AsyncDeliveryInfoUpdate infoUpdate = null;
    private HashMap<String, EditText> jsonEditTextMap;
    private HashMap<String, Spinner> jsonSpinnerMap;
    private HashMap<String, EditText> jsonEditTextDateMap;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.fragment_minimal_delivery,
                container, false);


        masterView.findViewById(R.id.idSaveAbobortion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validation.hasText((EditText) masterView.findViewById(R.id.id_delivery_date))){
                    saveMinimumDelivery(v);
                }

            }
        });

        masterView.findViewById(R.id.idCancelAbobortion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelFragment(v);
            }
        });

        masterView.findViewById(R.id.imageViewDeliveryDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomDatePickerDialog(getActivity()).show((EditText) masterView.findViewById(R.id.id_delivery_date));
            }
        });

        woman = getActivity().getIntent().getParcelableExtra("PregWoman");
        provider = getActivity().getIntent().getParcelableExtra("Provider");

        infoUpdate = new AsyncDeliveryInfoUpdate(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                handleAbortionSaveResponse(result);
            }
        },getActivity());

        initialize();


        return masterView;
    }

    public void cancelFragment(View view) {
        ((DeliverySavedListener)parentActivity).onDeliverySaved("cancel");
    }

    public void pickDate(View v) {
        Log.d(LOGTAG, "Clicked Pick date");
    }

    private void initialize() {
        jsonEditTextMap = new HashMap<>();
        jsonSpinnerMap = new HashMap<>();
        jsonEditTextDateMap  = new HashMap<>();

        //EditTexts

        jsonEditTextMap.put("dNoLiveBirth", (EditText)masterView.findViewById(R.id.Live_born));
        jsonEditTextMap.put("dStillFresh", (EditText)masterView.findViewById(R.id.Dead_born_fresh));
        jsonEditTextMap.put("dStillMacerated",(EditText)masterView.findViewById(R.id.Dead_born_macerated));
        jsonEditTextMap.put("dNewBornGirl",(EditText)masterView.findViewById(R.id.daughter));
        jsonEditTextMap.put("dNewBornBoy",(EditText)masterView.findViewById(R.id.son));
        jsonEditTextMap.put("dNewBornUnidentified",(EditText)masterView.findViewById(R.id.notDetected));

        jsonEditTextDateMap.put("dDate", (EditText)masterView.findViewById(R.id.id_delivery_date));

        jsonSpinnerMap.put("dPlace",(Spinner) masterView.findViewById(R.id.idAbortionPlaceDropdown) );
        jsonSpinnerMap.put("dCenterName",(Spinner) masterView.findViewById(R.id.id_facility_name_Dropdown) );
        jsonSpinnerMap.put("dType", (Spinner) masterView.findViewById(R.id.spMinDeliveryDtype));

    }

    private void handleAbortionSaveResponse(String result) {
        //pass hte call back the listener
        ((DeliverySavedListener)parentActivity).onDeliverySaved(result);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
        if(masterView == null) {
            masterView = getView();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
        if(woman == null && provider == null) {
            woman = ((PatientAndProviderDetailsRequiredListener) parentActivity).onProviderAndPatientRequired().first;
            provider = ((PatientAndProviderDetailsRequiredListener) parentActivity).onProviderAndPatientRequired().second;
        }

        Spinner abortionPlace = (Spinner)masterView.findViewById(R.id.idAbortionPlaceDropdown);

        abortionPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    masterView.findViewById( R.id.idAbortionFacility).setVisibility(View.GONE);
                } else if(position ==2 ) {
                    masterView.findViewById( R.id.idAbortionFacility).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner minDeliveryType = (Spinner)masterView.findViewById(R.id.spMinDeliveryDtype);
        if(getActivity().getClass().equals(PACActivity.class)){
            minDeliveryType.setSelection(3);
            if(minDeliveryType.isEnabled()) minDeliveryType.setEnabled(false);

        }
        final int hidableLayout [] = {R.id.new_born, R.id.sex};
        minDeliveryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int visibility = View.VISIBLE;
                switch (position) {
                    case 1:
                    case 2:
                        visibility = View.VISIBLE;
                        break;
                    case 3:
                    case 4:
                        visibility = View.GONE;
                }

                for(int layout: hidableLayout) {
                    masterView.findViewById(layout).setVisibility(visibility);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d("ClientIntro", "OnActivityResult");
    }

    private void saveMinimumDelivery(View view) {
        Log.d(LOGTAG, "Handled Button Click");
        try {
            JSONObject saveDelivery = buildQueryHeader(false);
            getSpecialCases(saveDelivery);
            Utilities.getEditTexts(jsonEditTextMap, saveDelivery);
            Utilities.getEditTextDates(jsonEditTextDateMap, saveDelivery);
            Utilities.getSpinners(jsonSpinnerMap, saveDelivery);

            infoUpdate.execute(saveDelivery.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {

        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthid:" + woman.getHealthId() + "," +
                (isRetrieval ? "": "providerid:\""+String.valueOf(provider.getProviderCode())+"\",") +
                "pregno:" + woman.getPregNo() + "," +
                "deliveryLoad:" + (isRetrieval? "retrieve":"\"\"") +
                "}";

        return new JSONObject(queryString);
    }

    public void getSpecialCases(JSONObject json) {
        try {
            json.put("dOther", ""); //other delivery complicacies
            json.put("dOtherReason", ""); //other delivery complicacies

            json.put("dPlace","2");
            json.put("dCenterName","3");
            json.put("dAdmissionDate","");
            json.put("dWard", "");
            json.put("dBed","");
            json.put("dDate","2015-11-25");
            json.put("dTime","");
            json.put("dType","3");
            json.put("dNoLiveBirth","");
            json.put("dNoStillBirth","");
            json.put("dStillFresh","");
            json.put("dStillMacerated","");
            json.put("dAbortion","");
            json.put("dNewBornBoy","");
            json.put("dNewBornGirl","");
            json.put("dNewBornUnidentified","");
            json.put("dOxytocin", "2");
            json.put("dTraction","2");
            json.put("dUMassage","2");
            json.put("dEpisiotomy","2");
            json.put("dMisoprostol","2");
            json.put("dAttendantName", "");
            json.put("dAttendantDesignation","");
            json.put("dAttendantThisProvider", "2");
            json.put("dBloodLoss","2");
            json.put("dLateDelivery","2");
            json.put("dBlockedDelivery","2");
            json.put("dPlacenta","2");
            json.put("dHeadache","2");
            json.put("dBVision","2");
            json.put("dOBodyPart","2");
            json.put("dConvulsions","2");
            json.put("dOthers","2");
            json.put("dOthersReason","");
            json.put("dTreatment","[]");
            json.put("dAdvice","[]"); //it should always return some value due to multipleSelect.js plugin
            json.put("dRefer","2");
            json.put("dReferCenter","");
            json.put("dReferReason","[]");
            json.put("sateliteCenterName", provider.getSatelliteName());

            ///////////
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Error Building dummy keys for delivery");
        }
    }
}

