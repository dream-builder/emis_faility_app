package org.sci.rhis.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.fwc.R;
import org.sci.rhis.fwc.SecondActivity;
import org.sci.rhis.model.DashboardDataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hajjaz.ibrahim on 11/27/2017.
 */

public class CustomAdapter extends ArrayAdapter<DashboardDataModel> {
    private List<DashboardDataModel> items;
    int color;
    Context con;
    String service_name;
    ArrayList<Boolean> checkBoxMobile;

    public CustomAdapter(Context context, List<DashboardDataModel> items, int color, String service_name) {
        super(context, R.layout.dash_custom_list, items);
        this.items = items;
        this.color = color;
        this.con = context;
        this.service_name = service_name;
        checkBoxMobile = new ArrayList<>();
        for (int i=0; i< items.size(); i++){
            checkBoxMobile.add(false);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;



        if(v == null) {
            LayoutInflater li = LayoutInflater.from(getContext());
            v = li.inflate(R.layout.dash_custom_list, null);
        }

        final DashboardDataModel item = items.get(position);

        if(item != null) {

            RelativeLayout itemLayout = (RelativeLayout)v.findViewById(R.id.listItemLayout);
            ImageView icon = (ImageView)v.findViewById(R.id.appIcon);
            final TextView nameText = (TextView)v.findViewById(R.id.titleTxt);
            LinearLayout llDescription = (LinearLayout)v.findViewById(R.id.descriptionLayout);
            final TextView mobileText = (TextView)v.findViewById(R.id.tvMobile);
            final TextView dateText = (TextView)v.findViewById(R.id.tvIUDDate);
            TextView descriptionText = (TextView)v.findViewById(R.id.descriptionTxt);
            CheckBox chkFollowup = (CheckBox)v.findViewById(R.id.chkFollowup);
            TextView checkBoxLabel = (TextView)v.findViewById(R.id.tvCheckBoxLabel);

            if(color == 1){
                itemLayout.setBackgroundColor(Color.rgb(252, 108, 108));//(255,51,51));
            }
            else{
                itemLayout.setBackgroundColor(Color.rgb(80, 252, 156));//(60,179,113)); //46,139,87
                chkFollowup.setVisibility(View.GONE);
                checkBoxLabel.setVisibility(View.GONE);
            }

            if(nameText != null) nameText.setText("নামঃ "+item.getName());

            if(dateText != null) {
                if(item.getDate().equals("null")) {
                    if (service_name.equals("IUD") || service_name.equals("Implant")) {
                        dateText.setText("প্রয়োগের তারিখঃ ");
                    } else if (service_name.equals("ANC")) {
                        dateText.setText("শেষ মাসিকের তারিখঃ ");
                    } else if (service_name.equals("PNC")) {
                        dateText.setText("তারিখঃ ");
                    }
                }
                else {
                    if (service_name.equals("IUD") || service_name.equals("Implant")) {
                        dateText.setText("প্রয়োগের তারিখঃ " + Utilities.getDateStringUIFormat(item.getDate()));
                    } else if (service_name.equals("ANC")) {
                        String lmpDate="",eddDate="";
                        if(item.getDate().contains(",")){
                            lmpDate = item.getDate().split(",")[0];
                            eddDate = item.getDate().split(",")[1];

                        }
                        dateText.setText("LMP: " + Utilities.getDateStringUIFormat(lmpDate)+
                                "\nEDD: "+Utilities.getDateStringUIFormat(eddDate));
                    } else if (service_name.equals("PNC")) {
                        dateText.setText("তারিখঃ " + Utilities.getDateStringUIFormat(item.getDate()));
                    }
                }

            }
            chkFollowup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    checkBoxMobile.set(position,isChecked);
                    if(isChecked){
                        compoundButton.setEnabled(false);
                        CommonQueryExecution.executeQuery("Insert into followup_notification(healthid, service_name, " +
                                " systementrydate) values ("+item.getHealthId()+", '"+service_name+"', DateTime('now','localtime')); ");

                    }
                }
            });

            chkFollowup.setChecked(checkBoxMobile.get(position));
            chkFollowup.setEnabled(!checkBoxMobile.get(position));

            if(mobileText != null) {
                if(item.getMobile().equals("null") || item.getMobile().equals("")||item.getMobile().equals("0")){
                    mobileText.setText("মোবাইল: নেই");
                    chkFollowup.setVisibility(View.GONE);
                    checkBoxLabel.setVisibility(View.GONE);
                }
                else if(item.getMobile().length() == 10){
                    mobileText.setText("মোবাইল: 0"+item.getMobile());
                    chkFollowup.setVisibility(color==1?View.VISIBLE:View.GONE);
                    checkBoxLabel.setVisibility(color==1?View.VISIBLE:View.GONE);
                }


                else{
                    mobileText.setText("মোবাইল: "+item.getMobile());

                    chkFollowup.setVisibility(color==1?View.VISIBLE:View.GONE);
                    checkBoxLabel.setVisibility(color==1?View.VISIBLE:View.GONE);
                }

            }

            if(descriptionText != null)
            {
                if(item.getGuardianName().equals("null"))
                    descriptionText.setText("No Description");
                else
                    descriptionText.setText(" বয়স: "+item.getAge()+"   স্বামী: "+item.getGuardianName() );
            }


        }

        return v;
    }
}

