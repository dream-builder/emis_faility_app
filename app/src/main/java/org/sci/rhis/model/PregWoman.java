package org.sci.rhis.model;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.fwc.R;

import java.text.DateFormat;
import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jamil.zaman on 24/08/2015.
 */
public class PregWoman extends GeneralPerson implements Parcelable{
    private Date lmp;
    private Date edd;
    private Date actualDelivery;
    private int pregNo; //current pregnancy no
    private String husbandName;
    private String para;
    private String gravida;
    private String nBoy;
    private String nGirl;
    private String lastChildAge;
    private String height;
    private String bloodGroup;
    private String historyComplicated;
    private String historyContent;
    private int hasDeliveryInfo;
    private int hasAbortionInfo;
    private DateFormat df;
    private Calendar edd_cal;

    private static PregWoman client;

    //constants
    public static enum PREG_SERVICE {NEW, ANC, DELIVERY, NEWBORN, PNC, PAC}

    public final static int PREG_PERIOD    = 280; //We are only considering 280 days now
    public final static int PNC_THRESHOLD  = 42; //days
    public final static int ANC_THRESHOLD  = PREG_PERIOD + (2 * 7); //  PREG_PERIOD +  (2*7); -> 42 weeks from LMP

    //Parcelable overrides
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (df == null) {
             df = new CustomSimpleDateFormat("yyyy-MM-dd");
        }

        //Never ever change the order, if it absolutely necessary reflect the changes also in
        //the constructor that takes in a Parcel.
        dest.writeString(getName());
        dest.writeString(getGuardianName());
        dest.writeInt(getAge());
        dest.writeString(getSex());
        dest.writeString(getMobile());
        dest.writeLong(getHealthId());
        dest.writeString(lmp != null ? df.format(lmp) : "");
        dest.writeInt(getPregNo());
        dest.writeInt(getDeliveryInfo());
        dest.writeInt(getAbortionInfo());
        if(actualDelivery!=null) {
            dest.writeString(df.format(actualDelivery));
        } else {
            dest.writeString("");
        }
        dest.writeInt(isDead()? 1:0);
    }

    public static final Parcelable.Creator<PregWoman> CREATOR= new Parcelable.Creator<PregWoman>() {

        @Override
        public PregWoman createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new PregWoman(source);  //using parcelable constructor
        }

        @Override
        public PregWoman[] newArray(int size) {
            // TODO Auto-generated method stub
            return new PregWoman[size];
        }
    };

    //Parcel Constructor
    public PregWoman(Parcel data) {
        //"IMPORTANT" ->Do not change the order by which the 'data' is accessed

        super(
                data.readString(),      //name
                data.readString(),      //guardian name
                data.readInt(),         //age
                data.readString(),      //sex
                data.readString()       //mobile
        );
        setHealthId(data.readLong());   //healthId
        if(df == null) {
            df = new CustomSimpleDateFormat("yyyy-MM-dd");
        }
        lmp = getDate(data.readString());
        setPregNo(data.readInt()); //pregnancyNo
        setDeliveryInfo(data.readInt()); //hasDeliveryInfo
        setAbortionInfo(data.readInt()); //hasAbortionInfo

        String deliveryDate = data.readString(); //actualDelivery
        if(!deliveryDate.equals("")) {
            actualDelivery = getDate(deliveryDate);
        }

        if(data.readInt() == 1) {
            reportDead();
        }
        UpdateEdd();
    }

    private Date getDate(String dateStr) {
        try {
            return  dateStr.equals("") ? null : df.parse(dateStr);
        } catch (ParseException pe) {
            System.out.println("Parsing Exception:");
        }

        return null;
    }

    private Date getDate(String dateStr, String format) {
        CustomSimpleDateFormat convert = new CustomSimpleDateFormat(format);
        try {
            return convert.parse(dateStr);
        } catch (ParseException pe) {
            System.out.println("Parsing Exception:");
            pe.printStackTrace();
        }
        return null;
    }

    public void setActualDelivery(String date, String format) {
        actualDelivery = getDate(date, format);
    }

    public Date getActualDelivery() {
        return actualDelivery;
    }

    public PregWoman(JSONObject clientInfo, boolean isPregWoman) {
        super(clientInfo);
    }

    //has to optimized later........................
    public static PregWoman CreateGeneralWoman(JSONObject clientInfo) throws JSONException {

        //Only create PregWOman when it is confirmed she is pregnant
        //meaning pregnancy related information i s present
        if(clientInfo.getString("cNewMCHClient").equals("true")) {
            client = new PregWoman(clientInfo, false);
        } else {
            client = null;
        }
        return client;
    }


    public static PregWoman CreatePregWoman(JSONObject clientInfo) throws JSONException {

        //Only create PregWOman when it is confirmed she is pregnant
        //meaning pregnancy related information i s present
        if(clientInfo.getString("cNewMCHClient").equals("false")) {
            client = new PregWoman(clientInfo);
        } else {
            client = null;
        }
        return client;
    }

    //default
    public PregWoman() {
        super("", "", 0, "F","");
    }

    //create from JSON
    public PregWoman(JSONObject clientInfo) {
        super(clientInfo);
        try {
            //client = new PregWoman(JSONObject jso );
            initialize( clientInfo.getString("cLMP"),
                        clientInfo.getString("cEDD"),
                        clientInfo.getInt("cPregNo"),
                        clientInfo.getString("cPara"),
                        clientInfo.getString("cGravida"),
                        clientInfo.getString("cBoy"),
                        clientInfo.getString("cGirl"),
                        clientInfo.getString("cLastChildAge"),
                        clientInfo.getString("cHeight"),
                        clientInfo.getString("cBloodGroup"),
                        clientInfo.getString("cHistoryComplicated"),
                        clientInfo.getString("cHistoryComplicatedContent"),
                        clientInfo.getString("hasDeliveryInformation"),
                        clientInfo.getString("hasAbortionInformation")
                    );
        } catch (JSONException JSE) {
            System.out.println("JSON Exception:");
            JSE.printStackTrace();
        }
    }

    public PregWoman( String name,
                      String guardianName,
                      int age,
                      String sex,
                      String mobile
                      ) {
        super(name, guardianName, age, sex, mobile);
    }

    void initialize(
                    String _lmp,
                    String _edd,
                    int    _pregNo,
                    String _para,
                    String _gravida,
                    String _nBoy,
                    String _nGirl,
                    String _lastChildAge,
                    String _height,
                    String _bloodGroup,
                    String _historyComplicated,
                    String _historyContent,
                    String _hasDeliveryInfo,
                    String _hasAbortionInfo
                    ) {
        df = new CustomSimpleDateFormat("yyyy-MM-dd");
        try {
            this.lmp = df.parse(_lmp);
            UpdateEdd();
        } catch (ParseException PE) {
            this.lmp = new Date();
            System.out.println("Parsing Exception:");
            PE.printStackTrace();
        }
        pregNo = _pregNo;
        para = _para;
        gravida = _gravida;
        nBoy = _nBoy;
        nGirl = _nGirl;
        lastChildAge = _lastChildAge;
        height = _height;
        bloodGroup =_bloodGroup;
        historyComplicated = _historyComplicated;
        historyContent = _historyContent;
        hasDeliveryInfo = (_hasDeliveryInfo.equals("Yes") ? 1: 0);
        hasAbortionInfo =  _hasAbortionInfo.equals("Yes") ? 1: 0 ;
    }

    private Date addDateOffset(Date given, int days) {
        edd_cal = Calendar.getInstance();
        edd_cal.setTime(given);

        edd_cal.add(Calendar.DATE, days);
        return edd_cal.getTime();
    }

    private void UpdateEdd() {
        try{
            if(edd == null && lmp != null) {
                edd = addDateOffset(lmp, PREG_PERIOD);
            }
        } catch (Exception PE) {
            System.out.println("Exception:");
            PE.printStackTrace();
        }
    }

    public Date getAncThreshold() {
        return addDateOffset(lmp, ANC_THRESHOLD);
    }

    public void UpdateUIField(Activity activity) {

        ((EditText)activity.findViewById(R.id.gravida)).setText(gravida);
        ((EditText)activity.findViewById(R.id.para)).setText(para);
        ((EditText)activity.findViewById(R.id.SonNum)).setText(nBoy);
        ((EditText)activity.findViewById(R.id.DaughterNum)).setText(nGirl);
        ((EditText)activity.findViewById(R.id.lastChildYear)).setText(lastChildAge.equals("")? "" : String.valueOf(Integer.valueOf(lastChildAge) / 12));
        ((EditText)activity.findViewById(R.id.lastChildMonth)).setText(lastChildAge.equals("")? "" : String.valueOf(Integer.valueOf(lastChildAge) % 12));
        ((EditText)activity.findViewById(R.id.heightFeet)).setText(height.equals("")? "" :String.valueOf(Integer.valueOf(height)/12));
        ((EditText)activity.findViewById(R.id.heightInch)).setText(height.equals("")? "" :String.valueOf(Integer.valueOf(height)%12));
    }

    public boolean isEligibleFor(PREG_SERVICE service) {
        boolean eligible = false;
        Date today = new Date();
        switch(service) {
            case NEW:
            case ANC:
                if(today.before(getAncThreshold()))
                    eligible = true;
                break;
            case DELIVERY:
                if(today.before(getAncThreshold()) && (getAbortionInfo() == 0))
                    eligible = true;
                break;
            case NEWBORN:
                if(today.before(getAncThreshold()))
                    eligible = true;
                break;

            case PNC:
                //disable PNC entry if delivery info is present
                eligible = ((getDeliveryInfo() == 1) && (getAbortionInfo() == 0));
                break;
            case PAC:
                eligible = (getDeliveryInfo() == 1 && getAbortionInfo() == 1) ||
                           (getDeliveryInfo() == 0 && getAbortionInfo() == 0);
                break;
            default:
                eligible = false;
        }

        return eligible;

    }

    public Date getLmp() {
        return lmp;
    }

    public void setLmp(String lmp) {
        try {
            this.lmp = df.parse(lmp);
        } catch (ParseException PE) {
            System.out.println("Parsing Exception:");
            PE.printStackTrace();
        }
    }

    public Date getEdd() {
        return edd;
    }

    public void setEdd(Date edd) {
        this.edd = edd;
    }

    public int getPregNo() {
        return pregNo;
    }

    public void setPregNo(int pregNo) {
        this.pregNo = pregNo;
    }

    public int getDeliveryInfo() {
        return hasDeliveryInfo;
    }

    public void setDeliveryInfo(int hasDeliveryInfo) {
        this.hasDeliveryInfo = hasDeliveryInfo;
    }

    public int getAbortionInfo() {
        return hasAbortionInfo;
    }

    public void setAbortionInfo(int abortionInfo) {
        this.hasAbortionInfo = abortionInfo;
    }

    public String getnBoy() {
        return nBoy;
    }

    public void setnBoy(String nBoy) {
        this.nBoy = nBoy;
    }

    public String getnGirl() {
        return nGirl;
    }

    public void setnGirl(String nGirl) {
        this.nGirl = nGirl;
    }

    public String getLastChildAge() {
        return lastChildAge;
    }

    public void setLastChildAge(String lastChildAge) {
        this.lastChildAge = lastChildAge;
    }

    public String getHusbandName() {
        return husbandName;
    }

    public void setHusbandName(String husbandName) {
        this.husbandName = husbandName;
    }
}
