package org.sci.rhis.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jamil.zaman on 24/08/2015.
 */
public class GeneralPerson implements Parcelable{

    private String name;
    private String guardianName;
    private int age;
    private String sex;
    private long healthId;
    private String mobile;
    private boolean isDead;


    protected GeneralPerson(Parcel in) {
        name = in.readString();
        guardianName = in.readString();
        age = in.readInt();
        sex = in.readString();
        healthId = in.readLong();
        mobile = in.readString();
        isDead = in.readByte() != 0;
    }

    public static final Creator<GeneralPerson> CREATOR = new Creator<GeneralPerson>() {
        @Override
        public GeneralPerson createFromParcel(Parcel in) {
            return new GeneralPerson(in);
        }

        @Override
        public GeneralPerson[] newArray(int size) {
            return new GeneralPerson[size];
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

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isDead() {return isDead;}
    public void reportDead() { isDead = true;}

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public GeneralPerson(JSONObject clientInfo) {
        try {
            name = clientInfo.getString("cName");
            guardianName = clientInfo.getString("cHusbandName");
            age = clientInfo.getInt("cAge");
            sex = clientInfo.getString("cSex");
            healthId = clientInfo.getLong("cHealthID");
            mobile = clientInfo.getString("cMobileNo");
        } catch (JSONException JSE) {
            System.out.println("JSON Exception:");

            JSE.printStackTrace();
        }
    }

    public GeneralPerson(String name, String guardianName, int age, String sex, String mobile) {
        this.name = name;
        this.guardianName = guardianName;
        this.age = age;
        this.sex = sex;
        this.mobile = mobile;
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
        parcel.writeByte((byte) (isDead ? 1 : 0));
    }
}
