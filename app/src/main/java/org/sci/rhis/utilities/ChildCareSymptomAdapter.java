package org.sci.rhis.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.sci.rhis.fwc.ChildCareFragmentSymptom;
import org.sci.rhis.fwc.R;
import org.sci.rhis.model.SymptomDataModel;

import java.text.DecimalFormat;
import java.util.List;


/**
 * Created by hajjaz.ibrahim on 3/19/2018.
 */

public class ChildCareSymptomAdapter extends ArrayAdapter<SymptomDataModel> {

    private List<SymptomDataModel> items;
    Context context;

    public ChildCareSymptomAdapter(Context context, List<SymptomDataModel> items) {
        super(context, R.layout.childcare_symptom_list, items);
        this.items = items;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.childcare_symptom_list, null);

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.childCareSymptomCheckBox);
            holder.textView = (TextView) convertView.findViewById(R.id.childCareSymptomTextView);


            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.ref = position;


        //final SymptomDataModel item = items.get(holder.ref);

        if (items.get(holder.ref) != null) {



            if (holder.textView != null) holder.textView.setText(items.get(holder.ref).getTitle());


            if (holder.checkBox != null) {
                if (items.get(holder.ref).getIsChecked()) {
                    holder.checkBox.setChecked(true);
                } else {
                    holder.checkBox.setChecked(false);
                }
            }
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                           @Override
                                                           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                               //Toast.makeText(context, ""+holder.ref, Toast.LENGTH_SHORT).show();

                                                           }
                                                       }
            );


//            System.out.println("Hajjaz inside list Duration_required =" + items.get(holder.ref).getDuration_required() + ", title = " + items.get(holder.ref).getTitle() +
//                    ", value = " + items.get(holder.ref).getValue());





            holder.checkBox.setEnabled(ChildCareFragmentSymptom.editMode);
            //holder.textView.setEnabled(ChildCareFragmentSymptom.editMode);
            /*holder.editText.setEnabled(ChildCareFragmentSymptom.editMode);
            holder.spinner.setEnabled(ChildCareFragmentSymptom.editMode);*/

        }

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        CheckBox checkBox;
        int ref;
    }

}
