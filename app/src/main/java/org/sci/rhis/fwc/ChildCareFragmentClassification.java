package org.sci.rhis.fwc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.ConstantJSONs;
import org.sci.rhis.utilities.Utilities;

import java.util.Arrays;

/**
 * Created by hajjaz.ibrahim on 3/14/2018.
 */

public class ChildCareFragmentClassification extends CustomFragment implements
        CompoundButton.OnCheckedChangeListener {

    public static View fragmentView;
    Context context;

    JSONObject jsonObject;
    Button btnClassificationNext, btnClassificationPrevious;

    private static final String TAG = "Classification";
    public static boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_childcare_classification, container, false);
        context = this.getActivity();
        initFragment();

        showClassification();

        return fragmentView;
    }

    private void initiateFields() {

        btnClassificationNext = (Button) fragmentView.findViewById(R.id.btnClassificationSave);
        btnClassificationPrevious = (Button) fragmentView.findViewById(R.id.btnClassificationPrevious);

    }

    public void initFragment() {
        jsonObject = ChildCareActivity.getJSONObject();
        initiateFields();

        btnClassificationPrevious.setOnClickListener(view -> ChildCareActivity.viewPager.setCurrentItem(2));

        btnClassificationNext.setOnClickListener(view ->{
            //setTreatment();
            //getInputValues(ChildCareActivity.getJSONObject());
            ChildCareActivity.viewPager.setCurrentItem(4);});


        initialize(); //super class

        if (editMode) {
            Utilities.Enable(getActivity(), fragmentView.findViewById(R.id.classificationContainer));
        } else {
            Utilities.Disable(getActivity(), fragmentView.findViewById(R.id.classificationContainer));
        }
    }

    private void showClassification() {
        LinearLayout ll = (LinearLayout) fragmentView.findViewById(R.id.classificationLabelLinearLayout);
        ll.removeAllViews();
        try {
            if(ChildCareActivity.jsonObject.has("classifications")){
                String[] strArrayClassifications = ChildCareActivity.jsonObject.getString("classifications").split(",");
                int[] array = new int[strArrayClassifications.length];
                for(int i = 0; i < strArrayClassifications.length; i++){
                    array[i] = Integer.valueOf(strArrayClassifications[i]);
                }
                Arrays.sort(array);

                for(int i = 0; i < array.length; i++){
                    TextView tv = new TextView(this.getActivity());
                    String title = ConstantJSONs.classificationDetail().getJSONObject(String.valueOf(array[i])).getString("name");
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(0, 0, 0, 5);
                    tv.setLayoutParams(layoutParams);
                    tv.setPadding(5,5,5,5);
                    //Setting Classification List in Classification Segment
                    tv.setText(title);
                    tv.setTextSize(20);
                    ll.addView(tv);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
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





}
