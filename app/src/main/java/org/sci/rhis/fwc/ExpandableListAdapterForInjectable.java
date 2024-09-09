package org.sci.rhis.fwc;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.sci.rhis.utilities.Constants;

import java.util.HashMap;
import java.util.List;

/**
 * Created by arafat.hasan on 2/10/2016.
 */
public class ExpandableListAdapterForInjectable extends BaseExpandableListAdapter{
    private Context mContext;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;


    public ExpandableListAdapterForInjectable(Context mContext, List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        this.mContext = mContext;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        //all multiSpinner tag contain * at first
        if(childText.startsWith("*")){
            if(childText.contains(Constants.SIDE_EFFECT_TAG)){
                String tempChild = childText.replaceAll("[^\\d,]", "");
                if(!tempChild.equals("")){
                    txtListChild.setText(mContext.getString(R.string.side_effect) + " " + mContext.getString(R.string.detail));
                }else{
                    txtListChild.setText(mContext.getString(R.string.side_effect) + "");
                }
            }else if(childText.contains(Constants.TREATMENT_TAG)){
                String tempChild = childText.replaceAll("[^\\d,]", "");
                if(!tempChild.equals("")){
                    txtListChild.setText(mContext.getString(R.string.treatment) + " " + mContext.getString(R.string.detail));
                }else{
                    txtListChild.setText(mContext.getString(R.string.treatment) + "");
                }
            }else if(childText.contains(Constants.ADVICE_TAG)){
                String tempChild = childText.replaceAll("[^\\d,]", "");
                if(!tempChild.equals("")){
                    txtListChild.setText(mContext.getString(R.string.advice) + " " + mContext.getString(R.string.detail));
                }else{
                    txtListChild.setText(mContext.getString(R.string.advice) + "");
                }
            }else if(childText.contains(Constants.REFER_REASON_TAG)){
                String tempChild = childText.replaceAll("[^\\d,]", "");
                if(!tempChild.equals("")){
                    txtListChild.setText(mContext.getString(R.string.referReason) + " " + mContext.getString(R.string.detail));
                }else{
                    txtListChild.setText(mContext.getString(R.string.referReason) + "");
                }
            }


        }else{
            txtListChild.setText(childText);
        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(childText.startsWith("*")){
                    if(childText.contains(Constants.SIDE_EFFECT_TAG)){
                        String tempChild = childText.replaceAll("[^\\d,]", "");
                        if(!tempChild.equals("")){
                            String detail ="";
                            String keyStr[] = tempChild.split(",");
                            for (int i = 0; i < keyStr.length; ++i) {
                                int index = Integer.valueOf(keyStr[i]);
                                detail = detail+"\n"+mContext.getResources()
                                        .getStringArray(R.array.Injectable_Side_Effect_DropDown)[index];
                            }
                            AlertMessage.showMessage(mContext, mContext.getResources().getString(R.string.side_effect), detail);
                        }
                    }else if(childText.contains(Constants.TREATMENT_TAG)){
                        String tempChild = childText.replaceAll("[^\\d,]", "");
                        if(!tempChild.equals("")){
                            String detail ="";
                            String keyStr[] = tempChild.split(",");
                            for (int i = 0; i < keyStr.length; ++i) {
                                int index = Integer.valueOf(keyStr[i]);
                                detail = detail+"\n"+mContext.getResources()
                                        .getStringArray(R.array.Injectable_Treatment_DropDown)[index];
                            }
                            AlertMessage.showMessage(mContext, mContext.getResources().getString(R.string.treatment), detail);
                        }

                    }else if(childText.contains(Constants.ADVICE_TAG)){
                        String tempChild = childText.replaceAll("[^\\d,]", "");
                        if(!tempChild.equals("")){
                            String detail ="";
                            String keyStr[] = tempChild.split(",");
                            for (int i = 0; i < keyStr.length; ++i) {
                                int index = Integer.valueOf(keyStr[i]);
                                detail = detail+"\n"+mContext.getResources()
                                        .getStringArray(R.array.Injectable_Advice_DropDown)[index];
                            }
                            AlertMessage.showMessage(mContext, mContext.getResources().getString(R.string.advice), detail);
                        }
                    }else if(childText.contains(Constants.REFER_REASON_TAG)){
                        String tempChild = childText.replaceAll("[^\\d,]", "");
                        if(!tempChild.equals("")){
                            String detail ="";
                            String keyStr[] = tempChild.split(",");
                            for (int i = 0; i < keyStr.length; ++i) {
                                int index = Integer.valueOf(keyStr[i]);
                                detail = detail+"\n"+mContext.getResources()
                                        .getStringArray(R.array.Injectable_Refer_Reason_DropDown)[index];
                            }
                            AlertMessage.showMessage(mContext, mContext.getResources().getString(R.string.reason), detail);
                        }
                    }


                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
