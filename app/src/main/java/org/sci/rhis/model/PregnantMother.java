package org.sci.rhis.model;

/**
 * Created by arafat.hasan on 4/16/2017.
 */

public class PregnantMother extends Person {
    String sl;
    String elcoNo;
    String villageName;
    String mobileNo;
    String age;
    String liveChildCount;
    String gravida;
    String lmp;
    String edd;

    //empty constructor
    public PregnantMother(){
        super();
    }

    public PregnantMother(String name, String husbandName, String healthId, int idIndex,
                          String sl,String elcoNo, String villageName, String mobileNo, String age,
                          String liveChildCount, String gravida, String lmp, String edd) {
        super(name, husbandName, healthId, idIndex);
        this.sl = sl;
        this.elcoNo = elcoNo;
        this.villageName = villageName;
        this.mobileNo = mobileNo;
        this.age = age;
        this.liveChildCount = liveChildCount;
        this.gravida = gravida;
        this.lmp = lmp;
        this.edd = edd;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getElcoNo() {
        return elcoNo;
    }

    public void setElcoNo(String elcoNo) {
        this.elcoNo = elcoNo;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLiveChildCount() {
        return liveChildCount;
    }

    public void setLiveChildCount(String liveChildCount) {
        this.liveChildCount = liveChildCount;
    }

    public String getGravida() {
        return gravida;
    }

    public void setGravida(String gravida) {
        this.gravida = gravida;
    }

    public String getLmp() {
        return lmp;
    }

    public void setLmp(String lmp) {
        this.lmp = lmp;
    }

    public String getEdd() {
        return edd;
    }

    public void setEdd(String edd) {
        this.edd = edd;
    }
}
