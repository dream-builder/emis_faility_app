package org.sci.rhis.fwc;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.distribution.RetrieveDistributions;
import org.sci.rhis.model.DistributionDetail;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.Utilities;
import org.sci.rhis.utilities.Validation;

import java.util.ArrayList;
import java.util.HashMap;

public class DistributionDetailsActivity extends FWCServiceActivity implements View.OnClickListener {
    private ListView distributionListView;
    private ArrayList<DistributionDetail> distributionDetailList;
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;
    private JSONObject dateTimeObject;

    private EditText etStartDate, etEndDate;
    private TextView tvNoInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribution_details);

        initialization();

        getTextView(R.id.txtHeader).setText(getString(R.string.distribution_report));

        dateTimeObject = new JSONObject();
        callbackAsyncTask(RetrieveDistributions.getDistributionData(dateTimeObject, new JSONObject()));
    }

    private void initialization() {
        distributionListView = (ListView) findViewById(R.id.list_view);
        etStartDate = getEditText(R.id.start_date_field);
        etEndDate = getEditText(R.id.end_date_field);
        tvNoInformation = getTextView(R.id.tv_no_information);

        distributionDetailList = new ArrayList<>();

        datePickerDialog = new CustomDatePickerDialog(this, Constants.SHORT_HYPHEN_FORMAT_DATABASE);

        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.start_date_picker_button, (EditText) findViewById(R.id.start_date_field));
        datePickerPair.put(R.id.end_date_picker_button, (EditText) findViewById(R.id.end_date_field));
    }

    @Override
    public void callbackAsyncTask(String result) {
        distributionDetailList.clear();

        //reset start and end date field with empty string
        etStartDate.setText("");
        etEndDate.setText("");

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonDataObject = jsonObject.getJSONObject("data");

            //no information text view visibility
            if (jsonDataObject.toString().equals("{}")) {
                tvNoInformation.setVisibility(View.VISIBLE);
            } else {
                tvNoInformation.setVisibility(View.GONE);
            }

            String searchResultText = getString(R.string.showing_result) + ": " + jsonObject.getString("result_for");
            getTextView(R.id.tv_result_for).setText(searchResultText);

            for (int i = 0; i < jsonDataObject.length(); i++) {
                JSONObject distributedObject = jsonDataObject.getJSONObject(String.valueOf(i));

                distributionDetailList.add(new DistributionDetail(distributedObject.getString("item_name"),
                        distributedObject.getInt("quantity")));
            }

            DistributionDetailsAdapter distributionDetailsAdapter = new DistributionDetailsAdapter(this, R.layout.listview_distribution_detail_row, distributionDetailList);
            distributionListView.setAdapter(distributionDetailsAdapter);

        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_date_picker_button:
            case R.id.end_date_picker_button:
                datePickerDialog.show(datePickerPair.get(v.getId()));
                break;

            case R.id.search_filter_button:
                //check for empty start and end date
                if(!Validation.hasText(etStartDate) || !Validation.hasText(etEndDate)) {
                    Toast.makeText(this, getString(R.string.fill_all_fields_requirement), Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    dateTimeObject.put("start_date", etStartDate.getText());
                    dateTimeObject.put("end_date", etEndDate.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callbackAsyncTask(RetrieveDistributions.getDistributionData(dateTimeObject, new JSONObject()));

                break;

            case R.id.date_filter_viewer_button:
                //show/hide search filter layout by checking visibility
                if (getLinearLayout(R.id.date_filter_container).getVisibility() == View.VISIBLE) {
                    Utilities.MakeInvisible(this, R.id.date_filter_container);
                    getButton(R.id.date_filter_viewer_button).setBackground(getDrawable(R.drawable.arrow_down));
                } else {
                    Utilities.MakeVisible(this, R.id.date_filter_container);
                    getButton(R.id.date_filter_viewer_button).setBackground(getDrawable(R.drawable.arrow_up));
                }
                break;
        }
    }
}