package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jamil.zaman on 05/11/15.
 */
public class ItemQuantityAdapter extends ArrayAdapter<Pair<String, Integer>> {

    Context context;
    int layoutResourceId;
    Pair<String, Integer>[] itemData = null;

    public ItemQuantityAdapter(Context context, int layoutResourceId, Pair<String, Integer>[] data) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.itemData = data;
    }

    public ItemQuantityAdapter(Context context, int layoutResourceId, ArrayList<Pair<String, Integer>> data) {
        //
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        //Pair<String, Integer>[] personArray = {};
        //this.personData = data.toArray(personArray);
    }

    public ItemQuantityAdapter(Context context, int layoutResourceId) {
        //
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        //Pair<String, Integer>[] personArray = {};
        //this.personData = data.toArray(personArray);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder = null;

        if(itemData == null) {
           // return row;
        }

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ItemHolder();

            holder.itemName = (TextView)row.findViewById(R.id.tvTreatmentItemName);
            holder.itemQuantity = (EditText)row.findViewById(R.id.edTreatmentItemQuantity);

            row.setTag(holder);
        }
        else
        {
            holder = (ItemHolder)row.getTag();
        }

        Pair<String, Integer> item = getItem(position);
        holder.itemName.setText(item.first.substring(item.first.indexOf(":")+1));
        holder.itemQuantity.setText(String.valueOf(item.second));


        return row;
    }

    static class ItemHolder
    {
        TextView itemName;
        EditText itemQuantity;
    }

}
