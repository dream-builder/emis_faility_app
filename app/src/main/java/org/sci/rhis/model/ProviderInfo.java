package org.sci.rhis.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jamil.zaman on 16/08/2015.
 */
public class ProviderInfo implements Parcelable {

    public enum PROVIDER_TYPE {
        HA(1), FWA(2), FWV(3);
        private final int id;
        PROVIDER_TYPE(int id) { this.id = id; }
        public int getValue() { return id; }
    }

    //Parcelable overrides
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //"IMPORTANT" ->Do not change the order by which the 'data' is written

        dest.writeString(mProviderCode);
        dest.writeString(mProviderName);
        dest.writeString(mProviderFacility);
        dest.writeString(mFacilityId);
        dest.writeString(mSatelliteName);
        dest.writeString(mProviderType);
        dest.writeString(mCsba);
        dest.writeString(divID);
        dest.writeString(zillaID);
        dest.writeString(upazillaID);
        dest.writeString(unionID);
        dest.writeString(communityActive);
    }

    public static final Parcelable.Creator<ProviderInfo> CREATOR= new Parcelable.Creator<ProviderInfo>() {

        @Override
        public ProviderInfo createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new ProviderInfo(source);  //using parcelable constructor
        }

        @Override
        public ProviderInfo[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ProviderInfo[size];
        }
    };

    //Parcel Constructor
    public ProviderInfo(Parcel data) {
        //"IMPORTANT" ->Do not change the order by which the 'data' is accessed
        mProviderCode       = data.readString();
        mProviderName       = data.readString();
        mProviderFacility   = data.readString();
        mFacilityId         = data.readString();
        mSatelliteName      = data.readString();
        mProviderType       = data.readString();
        mCsba               = data.readString();
        divID               = data.readString();
        zillaID             = data.readString();
        upazillaID          = data.readString();
        unionID             = data.readString();
        communityActive     = data.readString();
    }

    public ProviderInfo() {
    }

    public String getProviderCode() {
        return mProviderCode;
    }

    public void setProviderCode(String mProviderCode) {
        this.mProviderCode = mProviderCode;
    }

    public String getProviderName() {
        return mProviderName;
    }

    public void setProviderName(String mProviderName) {
        this.mProviderName = mProviderName;
    }

    public String getProviderFacility() {
        return mProviderFacility;
    }

    public void setProviderFacility(String mProviderFacility) {
        this.mProviderFacility = mProviderFacility;
    }

    public String getSatelliteName() {
        return mSatelliteName;
    }

    public void setSatelliteName(String mSatelliteName) {
        this.mSatelliteName = mSatelliteName;
    }

    public static ProviderInfo getProvider() {
        if ( provider == null ) {
            provider = new ProviderInfo();
        }
        return provider;
    }

    public String getmProviderType() {
        return mProviderType;
    }

    public void setmProviderType(String mProviderType) {
        this.mProviderType = mProviderType;
    }

    public String getmCsba() {
        return mCsba;
    }

    public void setmCsba(String mCsba) {
        this.mCsba = mCsba;
    }

    public String getDivID() { return divID; }

    public void setDivID(String divID) { this.divID = divID; }

    private String mProviderCode;
    private String mProviderName;
    private String mProviderFacility;
    private String mFacilityId; //lately added
    private String mSatelliteName;
    private String mProviderType; //lately added
    private String mCsba;
    private String divID;
    private String zillaID;
    private String upazillaID;
    private String unionID;
    private String communityActive;

    public String getZillaID() {
        return zillaID;
    }

    public String getCommunityActive() {
        return communityActive;
    }

    public void setCommunityActive(String communityActive) {
        this.communityActive = communityActive;
    }

    public void setZillaID(String zillaID) {
        this.zillaID = zillaID;
    }

    public String getUpazillaID() {
        return upazillaID;
    }

    public void setUpazilaID(String upazillaID) {
        this.upazillaID = upazillaID;
    }

    public String getUnionID() {
        return unionID;
    }

    public void setUnionID(String unionID) {
        this.unionID = unionID;
    }

    public String getmFacilityId() {
        return mFacilityId;
    }

    public void setmFacilityId(String mFacilityId) {
        this.mFacilityId = mFacilityId;
    }

    public static Creator<ProviderInfo> getCREATOR() {
        return CREATOR;
    }

    private static ProviderInfo provider;
}
