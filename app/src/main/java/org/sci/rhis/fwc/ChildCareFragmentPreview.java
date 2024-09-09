package org.sci.rhis.fwc;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.ConstantJSONs;
import org.sci.rhis.utilities.Utilities;

import java.util.Arrays;


/**
 * Created by hajjaz.ibrahim on 3/14/2018.
 */

public class ChildCareFragmentPreview extends CustomFragment {

    public static View fragmentView;
    TextView previewLabel;
    Button btnPreviewPrevious, btnPreviewNext;
    int age=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_childcare_preview, container, false);

        initFragment();

        if(ChildCareActivity.jsonObject!=null) loadDataIfAny();

        return fragmentView;
    }

    private void loadDataIfAny(){
        setInputValues(ChildCareActivity.jsonObject);
    }

    private void initFragment() {
        initiateFields();

        //Preview Fragment Button Functionality
        btnPreviewPrevious.setOnClickListener(view -> ChildCareActivity.viewPager.setCurrentItem(1));
        btnPreviewNext.setOnClickListener(view -> {
            getInputValues(ChildCareActivity.getJSONObject());
            ChildCareActivity.viewPager.setCurrentItem(3);
        });

        initialize(); //super class
        setInputValues(ChildCareActivity.getJSONObject());
        try {
            previewLabel.setText(ChildCareActivity.getJSONObject().getString("preview"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initiateFields() {
        btnPreviewPrevious = (Button) fragmentView.findViewById(R.id.btnPreviewPrevious);
        btnPreviewNext = (Button) fragmentView.findViewById(R.id.btnPreviewNext);
        previewLabel = (TextView) fragmentView.findViewById(R.id.previewLabel);


        try {
            age = Integer.valueOf(ChildCareActivity.getJSONObject().getString("age"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getInputValues(JSONObject jsonObject) {
        Utilities.getTextViews(jsonTextViewsMap, jsonObject);
    }

    public void setInputValues(JSONObject jsonObject) {
        Utilities.setTextViews(jsonTextViewsMap, jsonObject);
    }

    @Override
    protected void initiateCheckboxes() {

    }

    @Override
    protected void initiateEditTexts() {

    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewsMap.put("preview", previewLabel);
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


}
