package org.sci.rhis.model;

import java.util.Date;

/**
 * Created by hajjaz.ibrahim on 12/20/2017.
 */


public class SatelliteSessionEvents {
    private int id;
    private String strSatellite;
    private String strFWAName;
    private String strFWAId;
    private String strMouzaId;
    private String strVillageId;
    private String strVillage;
    private Date date;
    private String comments;


    public SatelliteSessionEvents(String satellite, Date date, String FWAName,String FWAID, String strMouzaId, String strVillageId, String village) {
        this.date = date;
        this.strSatellite = satellite;
        this.strFWAName = FWAName;
        this.strFWAId = FWAID;
        this.strMouzaId = strMouzaId;
        this.strVillageId = strVillageId;
        this.strVillage = village;
    }

    public SatelliteSessionEvents(int id,String satellite, Date date, String FWAName,String FWAID, String strMouzaId, String strVillageId, String village) {
        this.id=id;
        this.date = date;
        this.strSatellite = satellite;
        this.strFWAName = FWAName;
        this.strFWAId = FWAID;
        this.strMouzaId = strMouzaId;
        this.strVillageId = strVillageId;
        this.strVillage = village;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSatellite() {
        return strSatellite;
    }

    public Date getDate() {
        return date;
    }

    public String getFWAName() {
        return strFWAName;
    }

    public String getStrFWAId() {
        return strFWAId;
    }

    public String getStrVillage() {
        return strVillage;
    }

    public void setStrVillage(String strVillage) {
        this.strVillage = strVillage;
    }

    public void setStrFWAId(String strFWAId) {
        this.strFWAId = strFWAId;
    }

    public String getStrMouzaId() {
        return strMouzaId;
    }

    public void setStrMouzaId(String strMouzaId) {
        this.strMouzaId = strMouzaId;
    }

    public String getStrVillageId() {
        return strVillageId;
    }

    public void setStrVillageId(String strVillageId) {
        this.strVillageId = strVillageId;
    }


}