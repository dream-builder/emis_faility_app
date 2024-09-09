package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.sci.rhis.model.Person;

import java.util.ArrayList;

/**
 * Created by jamil.zaman on 05/11/15.
 */
public class PersonAdapter extends ArrayAdapter<Person> {

    Context context;
    int layoutResourceId;
    Person[] personData = null;
    int searchFlag=0;
    boolean showRadioLayout = false;
    private int selectedPosition = -1;

    public PersonAdapter(Context context, int layoutResourceId, Person[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.personData = data;
    }

    public PersonAdapter(Context context, int layoutResourceId, ArrayList<Person> data, int searchFlag) {
        //
        super(context, layoutResourceId, data.toArray(new Person[]{}));
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        Person[] personArray = {};
        this.personData = data.toArray(personArray);
        this.searchFlag=searchFlag;
    }

    public PersonAdapter(Context context, int layoutResourceId, ArrayList<Person> data, boolean showRadioLayout) {
        super(context, layoutResourceId, data.toArray(new Person[]{}));
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        Person[] personArray = {};
        this.personData = data.toArray(personArray);
        this.showRadioLayout=showRadioLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PersonHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PersonHolder();

            if (showRadioLayout) {
                holder.name = (TextView) row.findViewById(R.id.tv_name);
                holder.information = (TextView) row.findViewById(R.id.tv_information);
                holder.radioButton = (RadioButton) row.findViewById(R.id.radio_list_item_select);
            } else {
                holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
                holder.name = (TextView) row.findViewById(R.id.advSearch_name);
                holder.fatherName = (TextView) row.findViewById(R.id.advSearch_fatherName);
                holder.husbandName = (TextView) row.findViewById(R.id.advSearch_husbandName);
                holder.healthId = (TextView) row.findViewById(R.id.advSearch_healthId);
            }

            row.setTag(holder);
        }
        else
        {
            holder = (PersonHolder)row.getTag();
        }

        Person person = personData[position];

        holder.name.setText(person.getName());

        if (showRadioLayout) {
            holder.information.setText("Father: " + person.getFatherName() +
                    (!person.getHusbandName().isEmpty() ? (" | Husband: " + person.getHusbandName()): ""));
            holder.radioButton.setChecked(selectedPosition == position);
        } else {
            if (searchFlag == 3) {
                holder.healthId.setText(person.getServiceDate());
            } else {
                holder.healthId.setText(person.getHealthId());
            }
            holder.husbandName.setText(person.getHusbandName());
            holder.fatherName.setText(person.getFatherName());
            if (person.getIcon() != 0) {
                holder.imgIcon.setImageResource(person.getIcon());
            } else {
                holder.imgIcon.setVisibility(View.GONE);
            }
        }

        return row;
    }

    static class PersonHolder
    {
        ImageView imgIcon;
        TextView name;
        TextView fatherName;
        TextView husbandName;
        TextView healthId;

        RadioButton radioButton;
        TextView information;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

}
