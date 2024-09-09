package org.sci.rhis.model;

import android.content.Context;

import org.sci.rhis.fwc.R;

/**
 * Created by arafat.hasan on 4/6/2016.
 */
public class FPExaminations {
    /*private String weight;
    private String temperature;*/
    private String anemia;
    private String jaundice;
    /*private String pulse;
    private String edema;*/
    private String bpSystolic;
    private String bpDiastolic;
    /*private String heart;
    private String lungs;*/
    private String breastCondition;
    private String cervix;
    private String menstruation;
    private String vaginalWall;
    private String cervicitis;
    private String cervicalErosion;
    private String cervicalPolyp;
    private String contactBleeding;
    private String uterusSize;
    private String uterusShape;
    private String uterusPosition;
    private String uterusMovement;
    private String uterusMovementPain;
    private String vaginalFornix;
    private boolean isCurrentlyPregnant;
    private String urineSugar;
    private String hemoglobin;

    public FPExaminations() {
    }

    /*public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }*/

    public String getAnemia() {
        return anemia;
    }

    public void setAnemia(String anemia) {
        this.anemia = anemia;
    }

    public String getJaundice() {
        return jaundice;
    }

    public void setJaundice(String jaundice) {
        this.jaundice = jaundice;
    }

    /*public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getEdema() {
        return edema;
    }

    public void setEdema(String edema) {
        this.edema = edema;
    }*/

    public String getBpSystolic() {
        return bpSystolic;
    }

    public void setBpSystolic(String bpSystolic) {
        this.bpSystolic = bpSystolic;
    }

    public String getBpDiastolic() {
        return bpDiastolic;
    }

    public void setBpDiastolic(String bpDiastolic) {
        this.bpDiastolic = bpDiastolic;
    }

    /*public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public String getLungs() {
        return lungs;
    }

    public void setLungs(String lungs) {
        this.lungs = lungs;
    }*/

    public String getBreastCondition() {
        return breastCondition;
    }

    public void setBreastCondition(String breastCondition) {
        this.breastCondition = breastCondition;
    }

    public String getCervix() {
        return cervix;
    }

    public void setCervix(String cervix) {
        this.cervix = cervix;
    }

    public String getMenstruation() {
        return menstruation;
    }

    public void setMenstruation(String menstruation) {
        this.menstruation = menstruation;
    }

    public String getVaginalWall() {
        return vaginalWall;
    }

    public void setVaginalWall(String vaginalWall) {
        this.vaginalWall = vaginalWall;
    }

    public String getCervicitis() {
        return cervicitis;
    }

    public void setCervicitis(String cervicitis) {
        this.cervicitis = cervicitis;
    }

    public String getCervicalErosion() {
        return cervicalErosion;
    }

    public void setCervicalErosion(String cervicalErosion) {
        this.cervicalErosion = cervicalErosion;
    }

    public String getCervicalPolyp() {
        return cervicalPolyp;
    }

    public void setCervicalPolyp(String cervicalPolyp) {
        this.cervicalPolyp = cervicalPolyp;
    }

    public String getContactBleeding() {
        return contactBleeding;
    }

    public void setContactBleeding(String contactBleeding) {
        this.contactBleeding = contactBleeding;
    }

    public String getUterusSize() {
        return uterusSize;
    }

    public void setUterusSize(String uterusSize) {
        this.uterusSize = uterusSize;
    }

    public String getUterusShape() {
        return uterusShape;
    }

    public void setUterusShape(String uterusShape) {
        this.uterusShape = uterusShape;
    }

    public String getUterusPosition() {
        return uterusPosition;
    }

    public void setUterusPosition(String uterusPosition) {
        this.uterusPosition = uterusPosition;
    }

    public String getUterusMovement() {
        return uterusMovement;
    }

    public void setUterusMovement(String uterusMovement) {
        this.uterusMovement = uterusMovement;
    }

    public String getUterusMovementPain() {
        return uterusMovementPain;
    }

    public void setUterusMovementPain(String uterusMovementPain) {
        this.uterusMovementPain = uterusMovementPain;
    }

    public String getVaginalFornix() {
        return vaginalFornix;
    }

    public void setVaginalFornix(String vaginalFornix) {
        this.vaginalFornix = vaginalFornix;
    }

    public boolean isCurrentlyPregnant() {
        return isCurrentlyPregnant;
    }

    public void setCurrentlyPregnant(boolean currentlyPregnant) {
        isCurrentlyPregnant = currentlyPregnant;
    }

    public String getUrineSugar() {
        return urineSugar;
    }

    public void setUrineSugar(String urineSugar) {
        this.urineSugar = urineSugar;
    }

    public String getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(String hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public String toJsonString() {
        return "{" +
                /*"weight:\"" + weight + "\"," +
                "temperature:\"" + temperature + "\"," +*/
                "anemia:\"" + anemia + "\"," +
                "jaundice:\"" + jaundice + "\"," +
                /*"pulse:\"" + pulse + "\"," +
                "edema:\"" + edema + "\"," +*/
                "bpSystolic:\"" + bpSystolic + "\"," +
                "bpDiastolic:\"" + bpDiastolic + "\"," +
                /*"heart:\"" + heart + "\"," +
                "lungs:\"" + lungs + "\"," +*/
                "breastCondition:\"" + breastCondition + "\"," +
                "cervix:\"" + cervix + "\"," +
                "menstruation:\"" + menstruation + "\"," +
                "vaginalWall:\"" + vaginalWall + "\"," +
                "cervicitis:\"" + cervicitis + "\"," +
                "cervicalErosion:\"" + cervicalErosion + "\"," +
                "cervicalPolyp:\"" + cervicalPolyp + "\"," +
                "contactBleeding:\"" + contactBleeding + "\"," +
                "uterusSize:\"" + uterusSize + "\"," +
                "uterusShape:\"" + uterusShape + "\"," +
                "uterusPosition:\"" + uterusPosition + "\"," +
                "uterusMovement:\"" + uterusMovement + "\"," +
                "uterusCervixMovePain:\"" + uterusMovementPain + "\"," + //uterusMovementPain is uterusCervixMovePain in injectables
                "vaginalFornix:\"" + vaginalFornix + "\"," +
                "isCurrentlyPregnant:" + isCurrentlyPregnant + "," +
                "urineSugar:\"" + urineSugar + "\"," +
                "hemoglobin:\"" + hemoglobin + "\"" +
                "}";
    }

    public String toStringPreview(Context eContext) {
        return "\n শারীরিক, পিভি ও ল্যাবরেটরী পরীক্ষাঃ \n" +
                "____________________________________________\n" +
                (!anemia.equals("0")?" - রক্তস্বল্পতাঃ " + eContext.getResources().getStringArray(R.array.Anemia_Dropdown_Full)[Integer.valueOf(anemia)] + " \n":"")+
                (!jaundice.equals("0")?" - জন্ডিসঃ " + eContext.getResources().getStringArray(R.array.Jaundice_Edima_Dropdown)[Integer.valueOf(jaundice)] + " \n":"")+
                /*(!edema.equals("0")?"ইডিমাঃ " + eContext.getResources().getStringArray(R.array.Jaundice_Edima_Dropdown)[Integer.valueOf(edema)] + " \n":"")+*/
                (!bpSystolic.equals("") && !bpDiastolic.equals("")?" - রক্তচাপঃ " + bpSystolic+"/"+bpDiastolic+ " \n":"")+
                (!breastCondition.equals("0")?" - স্তন পরীক্ষাঃ " + eContext.getResources().getStringArray(R.array.Injectable_Breast_Condition_DropDown)[Integer.valueOf(breastCondition)] + " \n":"")+
                (!cervix.equals("0")?" - জরায়ুর মুখঃ " + eContext.getResources().getStringArray(R.array.Abnormality_Dropdown)[Integer.valueOf(cervix)] + " \n":"")+
                (!menstruation.equals("0")?" - স্রাবঃ "+ eContext.getResources().getStringArray(R.array.Abnormality_Dropdown)[Integer.valueOf(menstruation)] + " \n":"")+
                (!vaginalWall.equals("0")?" - যোনীপথের দেয়ালঃ " + eContext.getResources().getStringArray(R.array.VaginalWall_DropDown)[Integer.valueOf(vaginalWall)] + " \n":"")+
                (!cervicitis.equals("0")?" - জরায়ু মুখে ঘা/পুঁজঃ "+ eContext.getResources().getStringArray(R.array.YesNoLong_Dropdown)[Integer.valueOf(cervicitis)] + " \n":"")+
                (!cervicalErosion.equals("0")?" - ইরোশনঃ "+ eContext.getResources().getStringArray(R.array.YesNoLong_Dropdown)[Integer.valueOf(cervicalErosion)] + " \n":"")+
                (!cervicalPolyp.equals("0")?" - পলিপঃ "+ eContext.getResources().getStringArray(R.array.YesNoLong_Dropdown)[Integer.valueOf(cervicalPolyp)] + " \n":"")+
                (!contactBleeding.equals("0")?" - জরায়ু মুখ স্পর্শ করামাত্র রক্তপাতঃ "+ eContext.getResources().getStringArray(R.array.YesNo_Dropdown)[Integer.valueOf(contactBleeding)] + " \n":"")+
                (!uterusSize.equals("0")?" - জরায়ুর আকারঃ "+ eContext.getResources().getStringArray(R.array.Uterus_Shape_DropDown)[Integer.valueOf(uterusSize)] + " \n":"")+
                (!uterusShape.equals("0")?" - জরায়ুর আকৃতি "+ eContext.getResources().getStringArray(R.array.Uterus_Shape_DropDown)[Integer.valueOf(uterusShape)] + " \n":"")+
                (!uterusPosition.equals("0")?" - জরায়ুর অবস্থানঃ "+ eContext.getResources().getStringArray(R.array.Uterus_Position_DropDown)[Integer.valueOf(uterusPosition)] + " \n":"")+
                (!uterusMovement.equals("0")?" - জরায়ুর নড়াচড়াঃ "+ eContext.getResources().getStringArray(R.array.Uterus_Movement_DropDown)[Integer.valueOf(uterusMovement)] + " \n":"")+
                (!uterusMovementPain.equals("0")?" - জরায়ু নাড়ানোর সময় ব্যাথা হয় কিনা?: " + eContext.getResources().getStringArray(R.array.Uterus_Movement_Pain_DropDown)[Integer.valueOf(uterusMovementPain)] + " \n":"")+
                (!vaginalFornix.equals("0")?" - যোনির উর্ধ্বপার্শ্ব (ফোরনিক্স): "+ eContext.getResources().getStringArray(R.array.CloseOpen_Dropdown)[Integer.valueOf(vaginalFornix)] + " \n":"")+
                (isCurrentlyPregnant?" - বর্তমানে গর্ভবতী?: হ্যাঁ"+" \n":"")+
                (!urineSugar.equals("0")?" - প্রস্রাব পরীক্ষা: সুগারঃ " + eContext.getResources().getStringArray(R.array.Urine_Test_Dropdown)[Integer.valueOf(urineSugar)] + " \n":"")+
                (!hemoglobin.equals("")?" - হিমোগ্লোবিনঃ " + hemoglobin +" \n":"");
    }
}
