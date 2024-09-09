package org.sci.rhis.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jamil.zaman on 24/08/2015.
 */
public class DashboardDataModel implements Parcelable{

    private String name;
    private String guardianName;
    private int age;
    private String sex;
    private long healthId;
    private String mobile;
    private String iudDate;
    private boolean isDead;


    protected DashboardDataModel(Parcel in) {
        name = in.readString();
        guardianName = in.readString();
        age = in.readInt();
        sex = in.readString();
        healthId = in.readLong();
        mobile = in.readString();
        iudDate = in.readString();
        isDead = in.readByte() != 0;
    }

    public static final Creator<DashboardDataModel> CREATOR = new Creator<DashboardDataModel>() {
        @Override
        public DashboardDataModel createFromParcel(Parcel in) {
            return new DashboardDataModel(in);
        }

        @Override
        public DashboardDataModel[] newArray(int size) {
            return new DashboardDataModel[size];
        }
    };

    public long getHealthId() {
        return healthId;
    }

    public void setHealthId(long healthId) {
        this.healthId = healthId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDate() {
        return iudDate;
    }

    public void setDate(String iudDate) {
        this.iudDate = iudDate;
    }

    public DashboardDataModel(JSONObject clientInfo) {
        try {
            name = clientInfo.getString("cName");
            guardianName = clientInfo.getString("cHusbandName");
            age = clientInfo.getInt("cAge");
            sex = clientInfo.getString("cSex");
            healthId = clientInfo.getLong("cHealthID");
            mobile = clientInfo.getString("cMobileNo");
            iudDate = clientInfo.getString("cIUDDate");
        } catch (JSONException JSE) {
            System.out.println("JSON Exception:");
            JSE.printStackTrace();
        }
    }

    public DashboardDataModel(Long healthId, String name, String guardianName, int age, String sex, String mobile, String iudDate) {
        this.healthId = healthId;
        this.name = name;
        this.guardianName = guardianName;
        this.age = age;
        this.sex = sex;
        this.mobile = mobile;
        this.iudDate = iudDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(guardianName);
        parcel.writeInt(age);
        parcel.writeString(sex);
        parcel.writeLong(healthId);
        parcel.writeString(mobile);
        parcel.writeString(iudDate);
        parcel.writeByte((byte) (isDead ? 1 : 0));
    }
}
