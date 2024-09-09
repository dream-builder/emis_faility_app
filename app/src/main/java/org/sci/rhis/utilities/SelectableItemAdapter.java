package org.sci.rhis.utilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by jamil.zaman on 05/11/15.
 */
public class SelectableItemAdapter extends BaseAdapter {

    Context context;
    int layoutResourceId;
    Pair<Integer, String>[] itemData = null;
    JSONArray itemDataArray = null;
    private String columnName = "";

    public SelectableItemAdapter(Context context, int layoutResourceId, JSONArray data, String columnName) {
        //super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.itemDataArray = data;
        this.columnName = columnName;
    }
    /*public SelectableItemAdapter(Context context, int layoutResourceId, Pair<Integer, String>[] data) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.itemData = data;
    }

    public SelectableItemAdapter(Context context, int layoutResourceId, ArrayList<Pair<Integer, String>> data) {
        //
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        //Pair<String, Integer>[] personArray = {};
        //this.personData = data.toArray(personArray);
        itemData = new Pair[itemData.length];
        data.toArray(itemData);
    }

    public SelectableItemAdapter(Context context, int layoutResourceId) {
        //
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        //Pair<String, Integer>[] personArray = {};
        //this.personData = data.toArray(personArray);
    }

    public SelectableItemAdapter(Context context, int layoutResourceId, JSONArray data, String columnName) {
        //
        super(context, layoutResourceId, new JSONObject[data.length()]);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        //Pair<String, Integer>[] personArray = {};
        //this.personData = data.toArray(personArray);
        itemDataArray = data;
        columnName = columnName;
    }*/



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if(itemData == null) {
           // return row;
        }

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);

            holder = new ViewHolder();
            holder.tv = (TextView) row;

            row.setTag(holder);

        } else  {
            holder = (ViewHolder) row.getTag();
        }

        try
        {
            holder.tv.setText(itemDataArray.getJSONObject(position).getString(columnName));
        } catch (JSONException JSE) {
            Log.e("UTIL-CUSTOM-ADAPTER", "JSON PROBLEM:" + JSE.getMessage());
        }

        return row;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return itemDataArray == null ? 0 : itemDataArray.length();
    }

    public String getLabel(int position) {
        String object = "";
        try {
            object = itemDataArray.getJSONObject(position).getString(columnName);
        } catch (JSONException jse) {
            Log.e("Custom Adapter", "Could not get JSON object: " + jse.getMessage());
        }
        return object;
    }

    public Object getItem(int arg0)  {
        // TODO Auto-generated method stub
        Object object = null;
        try {
            object = itemDataArray.getJSONObject(arg0);
        } catch (JSONException jse) {
            Log.e("Custom Adapter", "Could not get JSON object: " + jse.getMessage());
        }
        return object;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        long id = -1;
        try {
            id  = itemDataArray.getJSONObject(position).getLong("id");
        } catch (JSONException jse) {
            Log.e("Custom Adapter", "Could not get JSON object: " + jse.getMessage());
        }
        return id;
    }
}

class ViewHolder {
    TextView tv;
}