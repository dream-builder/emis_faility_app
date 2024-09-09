package org.sci.rhis.fwc;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.sci.rhis.fwc.R;
import org.sci.rhis.model.DashboardDataModel;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.CustomAdapter;

import java.util.ArrayList;

/**
 * Created by hajjaz.ibrahim on 11/27/2017.
 */

public class FollowupNotificationListActivity extends ListActivity {

    ArrayList<DashboardDataModel> listObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_itemlist_activity);

        initView();

    }

    private void initView() {

        listObj= new ArrayList<>();
        listObj = getIntent().getParcelableArrayListExtra("ListObject");

        int color = getIntent().getIntExtra("Color", 0);
        String service_name = getIntent().getStringExtra("service_name");
        System.out.println("intColor = "+color);

        TextView tvFWC = (TextView) findViewById(R.id.fwc_heading);
        if(color == 1){
            tvFWC.setText(service_name+ getText(R.string.str_phone_call_due));
        }
        else{
            tvFWC.setText(service_name+ getText(R.string.str_followupd_due));
        }


        // create new adapter
        CustomAdapter adapter = new CustomAdapter(this, listObj,color,service_name);
        // set the adapter to list

        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
    }

}