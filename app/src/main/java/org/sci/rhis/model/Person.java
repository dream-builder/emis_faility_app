package org.sci.rhis.model;

/**
 * Created by jamil.zaman on 05/11/15.
 */
public class Person {


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public int getIcon() {
        return icon;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public Person(){
    }

    public Person(String name, String fatherName, String husbandName, String healthId, int idIndex, int icon, String serviceDate) {
        this.name = name;
        this.fatherName = fatherName;
        this.healthId = healthId;
        this.idIndex = idIndex;
        this.icon = icon;
        this.husbandName = husbandName;
        this.serviceDate = serviceDate;
    }

    //customized constructor for PregnantWoman
    public Person(String name,String husbandName,String healthId,int idIndex){
        this.name = name;
        this.healthId = healthId;
        this.idIndex = idIndex;
        this.husbandName = husbandName;
    }

    private String name;
    private String fatherName;
    private String husbandName;
    private String healthId;
    private int idIndex;
    private int icon;
    private String serviceDate;

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getHusbandName() {
        return husbandName;
    }

    public void setHusbandName(String husbandName) {
        this.husbandName = husbandName;
    }
}
