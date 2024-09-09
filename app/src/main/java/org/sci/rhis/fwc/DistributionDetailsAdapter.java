package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.sci.rhis.model.DistributionDetail;

import java.util.ArrayList;

public class DistributionDetailsAdapter extends ArrayAdapter<DistributionDetail> {
    Context context;
    private int layoutResourceId;
    private ArrayList<DistributionDetail> distributionDetailList;

    public DistributionDetailsAdapter(Context context, int layoutResourceId, ArrayList<DistributionDetail> distributionDetailList) {
        super(context, layoutResourceId, distributionDetailList);
        this.layoutResourceId = layoutResourceId;

        this.context = context;
        this.distributionDetailList = distributionDetailList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DistributionDetailHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new DistributionDetailHolder();
            holder.itemName = (TextView) row.findViewById(R.id.item_name);
            holder.quantity = (TextView) row.findViewById(R.id.quantity);

            row.setTag(holder);
        } else {
            holder = (DistributionDetailHolder) row.getTag();
        }

        DistributionDetail distributionDetail = distributionDetailList.get(position);
        holder.itemName.setText(distributionDetail.getItemName());
        holder.quantity.setText(String.valueOf(distributionDetail.getQuantity()));

        return row;
    }

    static class DistributionDetailHolder {
        TextView itemName;
        TextView quantity;
    }
}
