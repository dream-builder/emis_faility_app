package org.sci.rhis.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.fwc.R;
import org.sci.rhis.model.SymptomDataModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by hajjaz.ibrahim on 5/29/2018.
 */

public class MedicineCustomeView extends LinearLayout {

    private static final String TAG = MedicineCustomeView.class.getSimpleName();
    private ListView medicineListView;
    private Context context;
    List<MedicineDataModel> items = null;
    private MedicineAdapter mAdapter = null;
    String strMedicineNames = "";
    JSONObject jsonSelection = null;
    String strSelection = "";
    String[] _items = null;

    public MedicineCustomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        strMedicineNames = "";
        initializeUILayout();

        Log.d(TAG, "I need to call this method");
    }
    public MedicineCustomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeUILayout();
    }

    public void setItems(List<MedicineDataModel> item){
        this.items = item;
    }

    public void setItems(JSONArray item){
        _items = new String[item.length()];
        items = new ArrayList<MedicineDataModel>();
        for (int i = 0; i < _items.length; i++) {
            try {
                MedicineDataModel model = new MedicineDataModel(Integer.valueOf(item.getJSONObject(i).getString("id")), item.getJSONObject(i).getString("detail"),
                        Integer.valueOf(item.getJSONObject(i).has("source") ? item.getJSONObject(i).getString("source") : "0"));
                items.add(model);
            } catch (JSONException jse) {
                Log.e("MEDICINE_CUSTOM_VIEW", "Could not set items from JSON: " + jse.getMessage());
            }
        }
    }

    public void clear(){
        strMedicineNames = "";
        strSelection = "";
        initializeUILayout();
    }

    public String getSelectedItemsAsJSONString() {

        return jsonSelection.toString();
    }

    public String getSelectedItemsAsString() {

        return strSelection;
    }

    public void setSelection(String strRetrivedSelection) {
        //{0_1:5,2_1:10,4_1:15,6_1:20,8_1:25}
        strSelection = strRetrivedSelection.replace("{","");
        strSelection = strSelection.replace("}","");
        initializeUILayout();
    }

    private void initializeUILayout(){

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.medicine_layout, this);
        final RelativeLayout relativeLayoutMedicineDetails = (RelativeLayout) view.findViewById(R.id.rlMedicineDetails);
        //final LinearLayout linearLayoutMedicineView = (LinearLayout) view.findViewById(R.id.view_custom_medicine);
        medicineListView = (ListView)view.findViewById(R.id.medicineList);

        Button button = new Button(context);
        button.setText("Next");
        button.setBackgroundResource(R.drawable.button_style_moderateblue);
        button.setWidth(100);
        medicineListView.addFooterView (button);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view1 = (View) inflater.inflate(R.layout.medicine_layout, null);
                final RelativeLayout rlMedicineDetails = (RelativeLayout) view1.findViewById(R.id.rlMedicineDetails);
                ListView lv = (ListView) view1.findViewById(R.id.medicineList);
                final Dialog dialog = new Dialog(context);

                //Set Data
                //jsonSelection = new JSONObject();
                try {
                    //if(items != null) {
                        for (int i = 0; i < items.size(); i++) {

                            if(strSelection.length() > 0) {
                                strSelection = strSelection.replace("{", "");
                                strSelection = strSelection.replace("}", "");
                                String[] strArrayMedicines = strSelection.split(",");

                                boolean isChecked = false;
                                String value = "", source = "";
                                int id = items.get(i).getId();

                                for (int j = 0; j < strArrayMedicines.length; j++) {
                                    String strMedicineData = strArrayMedicines[j];
                                    String strId = strMedicineData.substring(0, strMedicineData.indexOf("_"));
                                    System.out.println("Hajjaz ID = " + strId + ", value = " + strMedicineData.substring(strMedicineData.indexOf(":") + 1, strMedicineData.length())
                                            + ", Source = " + strMedicineData.substring(strMedicineData.indexOf("_") + 1, strMedicineData.indexOf(":")));

                                    if (strId.equals(String.valueOf(id))) {
                                        isChecked = true;
                                        source = strMedicineData.substring(strMedicineData.indexOf("_") + 1, strMedicineData.indexOf(":"));
                                        value = strMedicineData.substring(strMedicineData.indexOf(":") + 1, strMedicineData.length());
                                        items.get(i).setIsChecked(isChecked);
                                        items.get(i).setValue(value);
                                        items.get(i).setSource(Integer.valueOf(source));
                                    }

                                }
                            }
                            else{
                                items.get(i).setIsChecked(false);
                                items.get(i).setValue("");
                            }
                        }
                    //}
                }catch(Exception e){
                    e.printStackTrace();
                }

                rlMedicineDetails.setVisibility(View.VISIBLE);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view1);
                dialog.setCanceledOnTouchOutside(false);

                mAdapter = new MedicineAdapter(context, items);

                Button button = new Button(context);
                button.setText("Ok");
                button.setBackgroundResource(R.drawable.button_style_moderateblue);
                button.setWidth(100);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Get Data
                        jsonSelection = new JSONObject();
                        strMedicineNames = "";
                        strSelection = "{";
                        JSONObject jsonMedicine = new JSONObject();
                        for (int i = 0; i < items.size(); i++) {
                            String id = String.valueOf(items.get(i).getId());
                            String value = items.get(i).getValue();
                            try {
                                if(items.get(i).getIsChecked()){
                                    strMedicineNames += items.get(i).getTitle() + ",";
                                    jsonMedicine.put(id, value);
                                    strSelection += id + "_" + items.get(i).getSource() + ":" + value + ",";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if(strSelection.length() > 1){
                            strSelection = strSelection.substring(0, (strSelection.length()-1));
                        }
                        strSelection += "}";

                        if(strMedicineNames.length() > 0) {
                            strMedicineNames = strMedicineNames.substring(0, (strMedicineNames.length() - 1));
                        }
                        TextView tv = (TextView) view.findViewById(R.id.tvMedicinDetails);
                        tv.setText(strMedicineNames);
                        tv.setTextColor(Color.BLACK);

                        jsonSelection = jsonMedicine;
                        System.out.println("Hajjaz jsonMedicine.toString() = " + jsonMedicine.toString());
                        System.out.println("Hajjaz strSelection = " + strSelection);
                        Toast.makeText(context,strSelection,Toast.LENGTH_LONG).show();

                        relativeLayoutMedicineDetails.setVisibility(View.VISIBLE);
                        relativeLayoutMedicineDetails.setBackgroundColor(Color.WHITE);
                        //linearLayoutMedicineView.setBackgroundResource(R.drawable.medicine_spinner);
                        dialog.dismiss();
                    }
                });
                lv.addFooterView (button);
                lv.setAdapter(mAdapter);
                dialog.show();
            }
        });
    }

    public static class MedicineDataModel implements Parcelable {

        //TODO {id_source:value,id_source:value} e.g. {12_1:20,11_2:10}

        private String title;
        private int source;
        private int id;
        private boolean isChecked;
        private String value;

        protected MedicineDataModel(Parcel in) {
            title = in.readString();
            source = in.readInt();
            id = in.readInt();
            isChecked = in.readByte() != 0;
            value = in.readString();
        }

        public final Creator<MedicineDataModel> CREATOR = new Creator<MedicineDataModel>() {
            @Override
            public MedicineDataModel createFromParcel(Parcel in) {
                return new MedicineDataModel(in);
            }

            @Override
            public MedicineDataModel[] newArray(int size) {
                return new MedicineDataModel[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public boolean getIsChecked() {
            return isChecked;
        }

        public void setIsChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public MedicineDataModel(JSONObject clientInfo) {
            try {
                title = clientInfo.getString("title");
                source = clientInfo.getInt("source");
                id = clientInfo.getInt("id");
                isChecked = clientInfo.getBoolean("isChecked");
                value = clientInfo.getString("value");
            } catch (JSONException JSE) {
                System.out.println("JSON Exception:");

                JSE.printStackTrace();
            }
        }

        public MedicineDataModel(int id, String title, int source) {
            this.id = id;
            this.title = title;
            this.source = source;
        }

        public MedicineDataModel(int id, boolean isChecked, String value) {
            this.id = id;
            this.isChecked = isChecked;
            this.value = value;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(title);
            parcel.writeInt(source);
            parcel.writeLong(id);
            parcel.writeByte((byte) (isChecked ? 1 : 0));
            parcel.writeString(value);
        }
    }
}

    class MedicineAdapter extends ArrayAdapter {

    private final String TAG = MedicineAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<MedicineCustomeView.MedicineDataModel> items;
    Context context = null;

    public MedicineAdapter(Context context, List<MedicineCustomeView.MedicineDataModel> items) {
        super(context, R.layout.medicine_list, items);
        this.items = items;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.medicine_list, null);

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.medicineCheckBox);
            holder.textView = (TextView) convertView.findViewById(R.id.medicineTextView);
            holder.editText = (EditText) convertView.findViewById(R.id.medicineValueEditText);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.ref = position;

        if (items.get(holder.ref) != null) {

            holder.editText.setVisibility(View.INVISIBLE);

            if (holder.textView != null){
                holder.textView.setText(items.get(holder.ref).getTitle());
            }

            if (holder.checkBox != null) {
                if (items.get(holder.ref).getIsChecked()) {
                    holder.checkBox.setChecked(true);
                    holder.editText.setVisibility(View.VISIBLE);
                } else {
                    holder.checkBox.setChecked(false);
                }
            }

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                           @Override
                                                           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                                               //Toast.makeText(context, ""+holder.ref, Toast.LENGTH_SHORT).show();
                                                               if (isChecked) {
                                                                   items.get(holder.ref).setIsChecked(true);
                                                                   holder.editText.setVisibility(View.VISIBLE);

                                                               } else {
                                                                   items.get(holder.ref).setIsChecked(false);
                                                                   holder.editText.setText("");
                                                                   items.get(holder.ref).setValue("");
                                                                   holder.editText.setVisibility(View.INVISIBLE);

                                                               }
                                                           }
                                                       }
            );

            if (holder.editText != null) {
                holder.editText.setText(items.get(holder.ref).getValue());
            }
//            System.out.println("Hajjaz inside list Duration_required =" + items.get(holder.ref).getDuration_required() + ", title = " + items.get(holder.ref).getTitle() +
//                    ", value = " + items.get(holder.ref).getValue());
            holder.editText.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    items.get(holder.ref).setValue(s.toString());
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        EditText editText;
        CheckBox checkBox;
        int ref;
    }

}
