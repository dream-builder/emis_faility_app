package org.sci.rhis.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hajjaz.ibrahim on 3/19/2018.
 */

public class SymptomDataModel implements Parcelable {

    private String title;
    private int sequence;
    private int id;
    private boolean isChecked;



    protected SymptomDataModel(Parcel in) {
        title = in.readString();
        sequence = in.readInt();
        id = in.readInt();
        isChecked = in.readByte() != 0;

    }

    public static final Creator<SymptomDataModel> CREATOR = new Creator<SymptomDataModel>() {
        @Override
        public SymptomDataModel createFromParcel(Parcel in) {
            return new SymptomDataModel(in);
        }

        @Override
        public SymptomDataModel[] newArray(int size) {
            return new SymptomDataModel[size];
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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }



    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }



    public SymptomDataModel(JSONObject clientInfo) {
        try {
            title = clientInfo.getString("title");
            sequence = clientInfo.getInt("sequence");
            id = clientInfo.getInt("id");
            isChecked = clientInfo.getBoolean("isChecked");
        } catch (JSONException JSE) {
            System.out.println("JSON Exception:");

            JSE.printStackTrace();
        }
    }

    public SymptomDataModel(int id, String title, int sequence) {
        this.id = id;
        this.title = title;
        this.sequence = sequence;

    }

    public SymptomDataModel(int id, boolean isChecked, String value) {
        this.id = id;
        this.isChecked = isChecked;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeInt(sequence);
        parcel.writeByte((byte) (isChecked ? 1 : 0));

    }
}
