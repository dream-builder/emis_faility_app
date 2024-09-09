package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.sci.rhis.model.Person;
import org.sci.rhis.model.PregnantMother;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Converter;

import java.text.ParseException;
import java.util.List;

/**
 * Created by arafat.hasan on 4/16/2017.
 */

public class PregnantMotherAdapter extends ArrayAdapter<PregnantMother> {
    Context context;
    int layoutResourceId;
    PregnantMother[] personData = null;

    public PregnantMotherAdapter(Context context, int resource,List<PregnantMother> data) {
        super(context, resource, data.toArray(new PregnantMother[]{}));
        this.layoutResourceId = resource;
        this.context = context;
        PregnantMother[] motherArray = {};
        this.personData = data.toArray(motherArray);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PregMotherHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PregMotherHolder();
            holder.slTv = (TextView)row.findViewById(R.id.pregSearchSlTextView);
            holder.elcoTv = (TextView)row.findViewById(R.id.pregSearchElcoTextView);
            holder.villageTv = (TextView)row.findViewById(R.id.pregSearchVillageTextView);
            holder.nameOtherTv = (TextView)row.findViewById(R.id.pregSearchDetailTextView);
            holder.ageTv = (TextView)row.findViewById(R.id.pregSearchAgeTextView);
            holder.lccTv = (TextView)row.findViewById(R.id.pregSearchLccTextView);
            holder.gravidaTv = (TextView)row.findViewById(R.id.pregSearchGravidaTextView);
            holder.lmpTv = (TextView)row.findViewById(R.id.pregSearchLmpTextView);
            holder.eddTv = (TextView)row.findViewById(R.id.pregSearchEddTextView);

            row.setTag(holder);
        }
        else
        {
            holder = (PregMotherHolder)row.getTag();
        }

        PregnantMother mother = personData[position];

        holder.slTv.setText(mother.getSl());
        holder.elcoTv.setText(mother.getElcoNo());
        holder.villageTv.setText(mother.getVillageName());
        holder.nameOtherTv.setText(mother.getName()+"\nHusband: "+mother.getHusbandName()+
                "\n"+((!mother.getMobileNo().equals("") && mother.getMobileNo().charAt(0)!='0')?
                "0"+mother.getMobileNo():mother.getMobileNo()));
        holder.ageTv.setText(mother.getAge());
        holder.lccTv.setText(mother.getLiveChildCount());
        holder.gravidaTv.setText(mother.getGravida());
        try {
            holder.lmpTv.setText(Converter.convertSdfFormat(Constants.SHORT_HYPHEN_FORMAT_DATABASE,mother.getLmp(),Constants.SHORT_SLASH_FORMAT_BRITISH));
            holder.eddTv.setText(Converter.convertSdfFormat(Constants.SHORT_HYPHEN_FORMAT_DATABASE,mother.getEdd(),Constants.SHORT_SLASH_FORMAT_BRITISH));
        } catch (ParseException e) {
            holder.lmpTv.setText(mother.getLmp());
            holder.eddTv.setText(mother.getEdd());
            e.printStackTrace();
        }

        return row;
    }

    static class PregMotherHolder
    {
        TextView slTv;
        TextView elcoTv;
        TextView villageTv;
        TextView nameOtherTv;
        TextView ageTv;
        TextView lccTv;
        TextView gravidaTv;
        TextView lmpTv;
        TextView eddTv;
    }
}
