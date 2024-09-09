package org.sci.rhis.utilities;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.fwc.R;

/**
 * Created by arafat.hasan on 3/25/2018.
 */

public class ConstantJSONs {
    /**
     * business logics of very severe disease - critical illness 0 - 59 Days
     * @return
     */
    public static JSONObject clSevereIllnessCriticalCondition(){
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom1));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("4",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom2));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("5",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.childcaresymptom3));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("6",singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal4.put("value",valJSON4);
            singleLogicVal4.put("text",GlobalActivity.context.getString(R.string.childcaresymptom4));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("7",singleLogicVal4);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value",valJSON5);
            singleLogicVal5.put("text",GlobalActivity.context.getString(R.string.childcaresymptom5));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("8",singleLogicVal5);

            JSONObject singleLogicVal6 = new JSONObject();
            JSONObject valJSON6 = new JSONObject();
            valJSON6.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal6.put("value",valJSON6);
            singleLogicVal6.put("text",GlobalActivity.context.getString(R.string.childcaresymptom6));
            singleLogicVal6.put("operation", "=");
            singleLogicVal6.put("type", "int");
            singleLogic.put("9",singleLogicVal6);

            JSONObject singleLogicVal7 = new JSONObject();
            JSONObject valJSON7 = new JSONObject();
            valJSON7.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal7.put("value",valJSON7);
            singleLogicVal7.put("text",GlobalActivity.context.getString(R.string.childcaresymptom7));
            singleLogicVal7.put("operation", "=");
            singleLogicVal7.put("type", "int");
            singleLogic.put("10",singleLogicVal7);

            JSONObject singleLogicVal8 = new JSONObject();
            JSONObject valJSON8 = new JSONObject();
            valJSON8.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal8.put("value",valJSON8);
            singleLogicVal8.put("text",GlobalActivity.context.getString(R.string.childcaresymptom8));
            singleLogicVal8.put("operation", "=");
            singleLogicVal8.put("type", "int");
            singleLogic.put("11",singleLogicVal8);

            JSONObject singleLogicVal9 = new JSONObject();
            JSONObject valJSON9 = new JSONObject();
            valJSON9.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal9.put("value",valJSON9);
            singleLogicVal9.put("text",GlobalActivity.context.getString(R.string.childcaresymptom9));
            singleLogicVal9.put("operation", "=");
            singleLogicVal9.put("type", "int");
            singleLogic.put("12",singleLogicVal9);

            JSONObject singleLogicVal10 = new JSONObject();
            JSONObject valJSON10 = new JSONObject();
            valJSON10.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal10.put("value",valJSON10);
            singleLogicVal10.put("text",GlobalActivity.context.getString(R.string.childcaresymptom10));
            singleLogicVal10.put("operation", "=");
            singleLogicVal10.put("type", "int");
            singleLogic.put("13",singleLogicVal10);

            JSONObject singleLogicVal11 = new JSONObject();
            JSONObject valJSON11 = new JSONObject();
            valJSON11.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal11.put("value",valJSON11);
            singleLogicVal11.put("text",GlobalActivity.context.getString(R.string.childcaresymptom11));
            singleLogicVal11.put("operation", "=");
            singleLogicVal11.put("type", "int");
            singleLogic.put("14",singleLogicVal11);

            classifiedJSON.put("logics",singleLogic);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return classifiedJSON;

    }

    /**
     * business logics of very severe disease - possible bacteria attack 0 - 59 days
     * @return
     */
    public static JSONObject clSevereIllnessBacteriaInfection() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom12));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("15",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));// first part of key is sequence
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom13));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("16",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.childcaresymptom14));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("17",singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal4.put("value",valJSON4);
            singleLogicVal4.put("text",GlobalActivity.context.getString(R.string.childcaresymptom16));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("18",singleLogicVal4);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value",valJSON5);
            singleLogicVal5.put("text",GlobalActivity.context.getString(R.string.childcaresymptom15));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("19",singleLogicVal5);

            JSONObject singleLogicVal6 = new JSONObject();
            JSONObject valJSON6 = new JSONObject();
            valJSON6.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal6.put("value",valJSON6);
            singleLogicVal6.put("text",GlobalActivity.context.getString(R.string.childcaresymptom104));
            singleLogicVal6.put("operation", "=");
            singleLogicVal6.put("type", "int");
            singleLogic.put("20",singleLogicVal6);



            classifiedJSON.put("logics",singleLogic);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of very severe disease - pneumonia 0 - 6 days
     * @return
     */
    public static JSONObject clSeverePneumonia() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON = new JSONObject();
            valJSON.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom17));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("21",singleLogicVal1);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of very severe disease - pneumonia 7 - 59 days
     * @return
     */
    public static JSONObject clPneumonia() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON = new JSONObject();
            valJSON.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom18));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("22",singleLogicVal1);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of local bacterial disease
     * @return
     */
    public static JSONObject clLocalBacterialInfection() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON = new JSONObject();
            valJSON.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom19));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("23",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom20));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("24",singleLogicVal2);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of not severe disease - other illness
     * @return
     */
    public static JSONObject clNotSevereNorBacterialInfection() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON = new JSONObject();
            valJSON.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom21));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("25",singleLogicVal1);

            classifiedJSON.put("logics",singleLogic);
        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of very severe disease - Jaundice
     * @return
     */
    public static JSONObject clSevereJaundice() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification", 1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value", valJSON3);
            singleLogicVal3.put("text", GlobalActivity.context.getString(R.string.childcaresymptom25));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("29", singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal4.put("value", valJSON4);
            singleLogicVal4.put("text", GlobalActivity.context.getString(R.string.childcaresymptom26));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("30", singleLogicVal4);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of - Jaundice
     * @return
     */
    public static JSONObject clJaundice() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification", 1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value", valJSON1);
            singleLogicVal1.put("text", GlobalActivity.context.getString(R.string.childcaresymptom23));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("27", singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value", valJSON2);
            singleLogicVal2.put("text", GlobalActivity.context.getString(R.string.childcaresymptom24));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("28", singleLogicVal2);


            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of diarrhea Severe Dehydration
     * @return
     */
    public static JSONObject clDiarrhoeaSevereDehydration() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",2);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value", valJSON1);
            singleLogicVal1.put("text", GlobalActivity.context.getString(R.string.childcaresymptom15));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("31", singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value", valJSON2);
            singleLogicVal2.put("text", GlobalActivity.context.getString(R.string.childcaresymptom16));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("32", singleLogicVal2);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal4.put("value", valJSON4);
            singleLogicVal4.put("text", GlobalActivity.context.getString(R.string.childcaresymptom29));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("34", singleLogicVal4);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value", valJSON5);
            singleLogicVal5.put("text", GlobalActivity.context.getString(R.string.childcaresymptom30));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("35", singleLogicVal5);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of very severe disease - diarrhea Little Dehydration
     * @return
     */
    public static JSONObject clDiarrhoeaDehydration() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",2);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value", valJSON3);
            singleLogicVal3.put("text", GlobalActivity.context.getString(R.string.childcaresymptom28));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("33", singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal4.put("value", valJSON4);
            singleLogicVal4.put("text", GlobalActivity.context.getString(R.string.childcaresymptom29));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("34", singleLogicVal4);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value", valJSON5);
            singleLogicVal5.put("text", GlobalActivity.context.getString(R.string.childcaresymptom30));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("35", singleLogicVal5);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of diarrhea No Dehydration
     * @return
     */
    public static JSONObject clDiarrhoeaNoDehydration() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",5);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("2", GlobalActivity.context.getString(R.string.general_no));
            singleLogicVal1.put("value", valJSON1);
            singleLogicVal1.put("text", GlobalActivity.context.getString(R.string.childcaresymptom15));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("31", singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("2", GlobalActivity.context.getString(R.string.general_no));
            singleLogicVal2.put("value", valJSON2);
            singleLogicVal2.put("text", GlobalActivity.context.getString(R.string.childcaresymptom16));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("32", singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("2", GlobalActivity.context.getString(R.string.general_no));
            singleLogicVal3.put("value", valJSON3);
            singleLogicVal3.put("text", GlobalActivity.context.getString(R.string.childcaresymptom28));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("33", singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("2", GlobalActivity.context.getString(R.string.general_no));
            singleLogicVal4.put("value", valJSON4);
            singleLogicVal4.put("text", GlobalActivity.context.getString(R.string.childcaresymptom29));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("34", singleLogicVal4);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("2", GlobalActivity.context.getString(R.string.general_no));
            singleLogicVal5.put("value", valJSON5);
            singleLogicVal5.put("text", GlobalActivity.context.getString(R.string.childcaresymptom30));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("35", singleLogicVal5);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of - Eating Problem
     * @return
     */
    public static JSONObject clEatingProblem() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",3);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value", valJSON1);
            singleLogicVal1.put("text", GlobalActivity.context.getString(R.string.childcaresymptom32));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("36", singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("2", GlobalActivity.context.getString(R.string.general_no));
            singleLogicVal2.put("value", valJSON2);
            singleLogicVal2.put("text", GlobalActivity.context.getString(R.string.childcaresymptom33));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("37", singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1", GlobalActivity.context.getString(R.string.childcaresymptom46));
            valJSON3.put("2", GlobalActivity.context.getString(R.string.childcaresymptom47));
            singleLogicVal3.put("value", valJSON3);
            singleLogicVal3.put("text", GlobalActivity.context.getString(R.string.childcaresymptom37));
            singleLogicVal3.put("operation", "in");
            singleLogicVal3.put("type", "int");
            singleLogic.put("44", singleLogicVal3);


            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value", valJSON5);
            singleLogicVal5.put("text", GlobalActivity.context.getString(R.string.childcaresymptom36));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("40", singleLogicVal5);

            JSONObject singleLogicVal6 = new JSONObject();
            JSONObject valJSON6 = new JSONObject();
            valJSON6.put("2", GlobalActivity.context.getString(R.string.childcaresymptom45));
            singleLogicVal6.put("value", valJSON6);
            singleLogicVal6.put("text", GlobalActivity.context.getString(R.string.childcaresymptom43));
            singleLogicVal6.put("operation", "=");
            singleLogicVal6.put("type", "int");
            singleLogic.put("43", singleLogicVal6);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of - Low Weight
     * @return
     */
    public static JSONObject clLowWeight() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1", GlobalActivity.context.getString(R.string.general_yes));
            valJSON4.put("2", GlobalActivity.context.getString(R.string.general_no));
            singleLogicVal4.put("value", valJSON4);
            singleLogicVal4.put("text", GlobalActivity.context.getString(R.string.childcaresymptom35));
            singleLogicVal4.put("operation", "in");
            singleLogicVal4.put("type", "int");
            singleLogic.put("39", singleLogicVal4);


            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }


    /**
     * business logics of very severe disease - Breast Feeding
     * @return
     */
    public static JSONObject clNoEatingProblem() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",3);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value", valJSON2);
            singleLogicVal2.put("text", GlobalActivity.context.getString(R.string.childcaresymptom33));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("37", singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value", valJSON3);
            singleLogicVal3.put("text", GlobalActivity.context.getString(R.string.childcaresymptom38));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("41", singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("3", GlobalActivity.context.getString(R.string.childcaresymptom48));
            singleLogicVal4.put("value", valJSON4);
            singleLogicVal4.put("text", GlobalActivity.context.getString(R.string.childcaresymptom37));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("44", singleLogicVal4);



            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    //2 months to 5 year
    /**
     * business logics of very severe disease - critical illness
     * @return
     */
    public static JSONObject clVeryCriticalDiseaseTwoMonths() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom49));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("45",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom50));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("46",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.childcaresymptom51));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("47",singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal4.put("value",valJSON4);
            singleLogicVal4.put("text",GlobalActivity.context.getString(R.string.childcaresymptom52));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("48",singleLogicVal4);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value",valJSON5);
            singleLogicVal5.put("text",GlobalActivity.context.getString(R.string.childcaresymptom53));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("49",singleLogicVal5);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of Pneumonia
     * @return
     */
    public static JSONObject clPneumoniaTwoMonths() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification", 1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value", valJSON1);
            singleLogicVal1.put("text", GlobalActivity.context.getString(R.string.childcaresymptom54));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("50", singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value", valJSON2);
            singleLogicVal2.put("text", GlobalActivity.context.getString(R.string.childcaresymptom55));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("51", singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value", valJSON3);
            singleLogicVal3.put("text", GlobalActivity.context.getString(R.string.childcaresymptom56));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("52", singleLogicVal3);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of Normal Cold
     * @return
     */
    public static JSONObject clNormalColdTwoMonths() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification", 1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value", valJSON1);
            singleLogicVal1.put("text", GlobalActivity.context.getString(R.string.childcaresymptom57));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("53", singleLogicVal1);

            classifiedJSON.put("logics",singleLogic);
        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }


    /**
     * business logics of diarrhoea
     * @return
     */
    public static JSONObject clDiarrhoeaTwoMonths(){
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom59));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("54",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom60));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("55",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.childcaresymptom61));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("56",singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal4.put("value",valJSON4);
            singleLogicVal4.put("text",GlobalActivity.context.getString(R.string.childcaresymptom62));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("57",singleLogicVal4);

            classifiedJSON.put("logics",singleLogic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classifiedJSON;

    }

    public static JSONObject clDiarrhoeaTwoMonths2(){
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom60));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("55",singleLogicVal2);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value",valJSON5);
            singleLogicVal5.put("text",GlobalActivity.context.getString(R.string.childcaresymptom64));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("58",singleLogicVal5);

            JSONObject singleLogicVal7 = new JSONObject();
            JSONObject valJSON7 = new JSONObject();
            valJSON7.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal7.put("value",valJSON7);
            singleLogicVal7.put("text",GlobalActivity.context.getString(R.string.childcaresymptom65));
            singleLogicVal7.put("operation", "=");
            singleLogicVal7.put("type", "int");
            singleLogic.put("59",singleLogicVal7);

            JSONObject singleLogicVal8 = new JSONObject();
            JSONObject valJSON8 = new JSONObject();
            valJSON8.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal8.put("value",valJSON8);
            singleLogicVal8.put("text",GlobalActivity.context.getString(R.string.childcaresymptom66));
            singleLogicVal8.put("operation", "=");
            singleLogicVal8.put("type", "int");
            singleLogic.put("60",singleLogicVal8);

            classifiedJSON.put("logics",singleLogic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classifiedJSON;

    }

    public static JSONObject clDiarrhoeaTwoMonths3(){
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal9 = new JSONObject();
            JSONObject valJSON9 = new JSONObject();
            valJSON9.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal9.put("value",valJSON9);
            singleLogicVal9.put("text",GlobalActivity.context.getString(R.string.childcaresymptom68));
            singleLogicVal9.put("operation", "=");
            singleLogicVal9.put("type", "int");
            singleLogic.put("61",singleLogicVal9);

            classifiedJSON.put("logics",singleLogic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classifiedJSON;

    }

    public static JSONObject clDiarrhoeaTwoMonths4(){
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom70));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("62",singleLogicVal1);

            classifiedJSON.put("logics",singleLogic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classifiedJSON;

    }

    public static JSONObject clDiarrhoeaTwoMonths5(){
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal11 = new JSONObject();
            JSONObject valJSON11 = new JSONObject();
            valJSON11.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal11.put("value",valJSON11);
            singleLogicVal11.put("text",GlobalActivity.context.getString(R.string.childcaresymptom72));
            singleLogicVal11.put("operation", "=");
            singleLogicVal11.put("type", "int");
            singleLogic.put("63",singleLogicVal11);

            classifiedJSON.put("logics",singleLogic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classifiedJSON;

    }

    public static JSONObject clDiarrhoeaTwoMonths6(){
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal12 = new JSONObject();
            JSONObject valJSON12 = new JSONObject();
            valJSON12.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal12.put("value",valJSON12);
            singleLogicVal12.put("text",GlobalActivity.context.getString(R.string.childcaresymptom74));
            singleLogicVal12.put("operation", "=");
            singleLogicVal12.put("type", "int");
            singleLogic.put("64",singleLogicVal12);

            classifiedJSON.put("logics",singleLogic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classifiedJSON;

    }

    /**
     * business logics of ear problem
     * clEarProblemTwoMonths - mastoiditis
     * clEarProblemTwoMonthsTwo - Ear Infection (Severe)
     * clEarProblemTwoMonthsThree - Long term Ear Infection
     * @return
     */
    public static JSONObject clEarProblemTwoMonths() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification", 1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value", valJSON1);
            singleLogicVal1.put("text", GlobalActivity.context.getString(R.string.childcaresymptom76));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("65", singleLogicVal1);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    public static JSONObject clEarProblemTwoMonthsTwo() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification", 1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value", valJSON1);
            singleLogicVal1.put("text", GlobalActivity.context.getString(R.string.childcaresymptom78));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("66", singleLogicVal1);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    public static JSONObject clEarProblemTwoMonthsThree() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification", 1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1", GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value", valJSON3);
            singleLogicVal3.put("text", GlobalActivity.context.getString(R.string.childcaresymptom80));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("67", singleLogicVal3);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }

    /**
     * business logics of very severe disease - critical Fever
     * @return
     */
    public static JSONObject clCriticalFeverTwoMonths() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",2); //TODO: Need to change Later
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON = new JSONObject();
            valJSON.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom81));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("68",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom111));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("69",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.childcaresymptom84));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("70",singleLogicVal3);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of very severe disease - Malaria Fever
     * @return
     */
    public static JSONObject clFeverMalariaTwoMonths() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",2); //TODO: Need to change later
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom81));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("68",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom82));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("71",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.childcaresymptom83));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("72",singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal4.put("value",valJSON4);
            singleLogicVal4.put("text",GlobalActivity.context.getString(R.string.childcaresymptom85));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("73",singleLogicVal4);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value",valJSON5);
            singleLogicVal5.put("text",GlobalActivity.context.getString(R.string.childcaresymptom86));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("74",singleLogicVal5);

            JSONObject singleLogicVal6 = new JSONObject();
            JSONObject valJSON6 = new JSONObject();
            valJSON6.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal6.put("value",valJSON6);
            singleLogicVal6.put("text",GlobalActivity.context.getString(R.string.childcaresymptom88));
            singleLogicVal6.put("operation", "=");
            singleLogicVal6.put("type", "int");
            singleLogic.put("75",singleLogicVal6);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of Fever Not Malaria
     * @return
     */
    public static JSONObject clFeverTwoMonths() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",2); //TODO: Need to change Later
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom81));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("68",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom87));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("76",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.childcaresymptom89));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("77",singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal4.put("value",valJSON4);
            singleLogicVal4.put("text",GlobalActivity.context.getString(R.string.childcaresymptom90));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("78",singleLogicVal4);


            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of  Measles
     * @return
     */
    public static JSONObject clMeaslesTwoMonths() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",2);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom95));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("81",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom92));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("68",singleLogicVal2);



            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.childcaresymptom94));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("80",singleLogicVal3);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of  Measles
     * @return
     */
    public static JSONObject clMeaslesTwoTwoMonths() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",2);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom96));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("82",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom92));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("68",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.childcaresymptom94));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("80",singleLogicVal3);


            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of  Measles
     * @return
     */
    public static JSONObject clMeasles() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",2);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom92));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("68",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom93));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("79",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.childcaresymptom94));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("80",singleLogicVal3);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }


    /**
     * business logics of complex Severe Acute Malnutrition
     * @return
     */
    public static JSONObject clComplexSevereAcuteMalnutrition() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",Flag.SPECIAL);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON1 = new JSONObject();
            valJSON1.put("1",GlobalActivity.context.getString(R.string.general_yes));
            valJSON1.put("2",GlobalActivity.context.getString(R.string.general_no));
            singleLogicVal1.put("value",valJSON1);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom97));
            singleLogicVal1.put("operation", "in");
            singleLogicVal1.put("type", "int");
            singleLogic.put("87",singleLogicVal1);

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.childcaresymptom99));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom98));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("88",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.malNutritionSever));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("83",singleLogicVal3);

            JSONObject singleLogicVal4 = new JSONObject();
            JSONObject valJSON4 = new JSONObject();
            valJSON4.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal4.put("value",valJSON4);
            singleLogicVal4.put("text",GlobalActivity.context.getString(R.string.malNutritionSever1));
            singleLogicVal4.put("operation", "=");
            singleLogicVal4.put("type", "int");
            singleLogic.put("84",singleLogicVal4);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value",valJSON5);
            singleLogicVal5.put("text",GlobalActivity.context.getString(R.string.childcaresymptom102));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("86",singleLogicVal5);

            JSONObject singleLogicVal6 = new JSONObject();
            JSONObject valJSON6 = new JSONObject();
            valJSON6.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal6.put("value",valJSON6);
            singleLogicVal6.put("text",GlobalActivity.context.getString(R.string.malNutritionSever2));
            singleLogicVal6.put("operation", "=");
            singleLogicVal6.put("type", "int");
            singleLogic.put("152",singleLogicVal6);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of Severe Acute Malnutrition Without Complications
     * @return
     */
    public static JSONObject clSevereAcuteMalnutritionWithoutComplications() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",Flag.SPECIAL);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("1",GlobalActivity.context.getString(R.string.childcaresymptom99));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom98));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("88",singleLogicVal2);

            JSONObject singleLogicVal3 = new JSONObject();
            JSONObject valJSON3 = new JSONObject();
            valJSON3.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal3.put("value",valJSON3);
            singleLogicVal3.put("text",GlobalActivity.context.getString(R.string.malNutritionLow));
            singleLogicVal3.put("operation", "=");
            singleLogicVal3.put("type", "int");
            singleLogic.put("85",singleLogicVal3);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value",valJSON5);
            singleLogicVal5.put("text",GlobalActivity.context.getString(R.string.childcaresymptom102));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("86",singleLogicVal5);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of Moderate to Severe Malnutrition
     * @return
     */
    public static JSONObject clModeratetoSevereMalnutrition() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("2",GlobalActivity.context.getString(R.string.childcaresymptom100));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom98));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("88",singleLogicVal2);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value",valJSON5);
            singleLogicVal5.put("text",GlobalActivity.context.getString(R.string.childcaresymptom102));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("86",singleLogicVal5);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of No Acute Malnutrition
     * @return
     */
    public static JSONObject clNoAcuteMalnutrition() {
        JSONObject classifiedJSON = new JSONObject();
        try {
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal2 = new JSONObject();
            JSONObject valJSON2 = new JSONObject();
            valJSON2.put("3",GlobalActivity.context.getString(R.string.childcaresymptom101));
            singleLogicVal2.put("value",valJSON2);
            singleLogicVal2.put("text",GlobalActivity.context.getString(R.string.childcaresymptom98));
            singleLogicVal2.put("operation", "=");
            singleLogicVal2.put("type", "int");
            singleLogic.put("88",singleLogicVal2);

            JSONObject singleLogicVal5 = new JSONObject();
            JSONObject valJSON5 = new JSONObject();
            valJSON5.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal5.put("value",valJSON5);
            singleLogicVal5.put("text",GlobalActivity.context.getString(R.string.childcaresymptom102));
            singleLogicVal5.put("operation", "=");
            singleLogicVal5.put("type", "int");
            singleLogic.put("86",singleLogicVal5);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return classifiedJSON;
    }

    /**
     * business logics of not severe disease - other illness
     * @return
     */
    public static JSONObject clOtherTwoMonths() {
        JSONObject classifiedJSON = new JSONObject();
        try{
            classifiedJSON.put("minConditionForClassification",1);
            JSONObject singleLogic = new JSONObject();

            JSONObject singleLogicVal1 = new JSONObject();
            JSONObject valJSON = new JSONObject();
            valJSON.put("1",GlobalActivity.context.getString(R.string.general_yes));
            singleLogicVal1.put("value",valJSON);
            singleLogicVal1.put("text",GlobalActivity.context.getString(R.string.childcaresymptom103));
            singleLogicVal1.put("operation", "=");
            singleLogicVal1.put("type", "int");
            singleLogic.put("89",singleLogicVal1);

            classifiedJSON.put("logics",singleLogic);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return classifiedJSON;
    }


    public static JSONObject classificationDetail(){
        JSONObject cDeatail = new JSONObject();
        try {
            //0 - 59 Days
            cDeatail.put(Flag.clSevereIllnessCriticalConditionCODE,getSingleJSON(clSevereIllnessCriticalCondition(),Flag.clSevereIllnessCriticalCondition ));
            cDeatail.put(Flag.clSevereIllnessBacteriaInfectionCODE,getSingleJSON(clSevereIllnessBacteriaInfection(),Flag.clSevereIllnessBacteriaInfection ));
            cDeatail.put(Flag.clSeverePneumoniaCODE,getSingleJSON(clSeverePneumonia(),Flag.clSeverePneumonia ));
            cDeatail.put(Flag.clPneumoniaCODE,getSingleJSON(clPneumonia(),Flag.clPneumonia ));
            cDeatail.put(Flag.clLocalBacterialInfectionCODE,getSingleJSON(clLocalBacterialInfection(),Flag.clLocalBacterialInfection ));
            cDeatail.put(Flag.clNotSevereNorBacterialInfectionCODE,getSingleJSON(clNotSevereNorBacterialInfection(),Flag.clNotSevereNorBacterialInfection ));
            cDeatail.put(Flag.clSevereJaundiceCODE,getSingleJSON(clSevereJaundice(),Flag.clSevereJaundice));
            cDeatail.put(Flag.clJaundiceCODE,getSingleJSON(clJaundice(),Flag.clJaundice ));
            cDeatail.put(Flag.clDiarrhoeaSevereDehydrationCODE,getSingleJSON(clDiarrhoeaSevereDehydration(),Flag.clDiarrhoeaSevereDehydration ));
            cDeatail.put(Flag.clDiarrhoeaDehydrationCODE,getSingleJSON(clDiarrhoeaDehydration(),Flag.clDiarrhoeaDehydration ));
            cDeatail.put(Flag.clDiarrhoeaNoDehydrationCODE,getSingleJSON(clDiarrhoeaNoDehydration(),Flag.clDiarrhoeaNoDehydration ));
            cDeatail.put(Flag.clLowWeightCODE,getSingleJSON(clLowWeight(),Flag.clLowWeight ));
            cDeatail.put(Flag.clEatingProblemCODE,getSingleJSON(clEatingProblem(),Flag.clEatingProblem ));
            cDeatail.put(Flag.clNoEatingProblemCODE,getSingleJSON(clNoEatingProblem(),Flag.clNoEatingProblem ));
            //2 Months - 5 Years
            cDeatail.put(Flag.clVeryCriticalDiseaseTwoMonthsCODE,getSingleJSON(clVeryCriticalDiseaseTwoMonths(),Flag.clVeryCriticalDiseaseTwoMonths ));
            cDeatail.put(Flag.clPneumoniaTwoMonthsCODE,getSingleJSON(clPneumoniaTwoMonths(),Flag.clPneumoniaTwoMonths ));
            cDeatail.put(Flag.clNormalColdTwoMonthsCODE,getSingleJSON(clNormalColdTwoMonths(),Flag.clNormalColdTwoMonths ));
            cDeatail.put(Flag.clDiarrhoeaTwoMonthsCODE4,getSingleJSON(clDiarrhoeaTwoMonths4(),Flag.clDiarrhoeaTwoMonths4 ));
            cDeatail.put(Flag.clDiarrhoeaTwoMonthsCODE,getSingleJSON(clDiarrhoeaTwoMonths(),Flag.clDiarrhoeaTwoMonths ));
            cDeatail.put(Flag.clDiarrhoeaTwoMonthsCODE2,getSingleJSON(clDiarrhoeaTwoMonths2(),Flag.clDiarrhoeaTwoMonths2 ));
            cDeatail.put(Flag.clDiarrhoeaTwoMonthsCODE5,getSingleJSON(clDiarrhoeaTwoMonths5(),Flag.clDiarrhoeaTwoMonths5 ));
            cDeatail.put(Flag.clDiarrhoeaTwoMonthsCODE6,getSingleJSON(clDiarrhoeaTwoMonths6(),Flag.clDiarrhoeaTwoMonths6 ));
            cDeatail.put(Flag.clDiarrhoeaTwoMonthsCODE3,getSingleJSON(clDiarrhoeaTwoMonths3(),Flag.clDiarrhoeaTwoMonths3 ));
            cDeatail.put(Flag.clEarProblemTwoMonthsCODE,getSingleJSON(clEarProblemTwoMonths(),Flag.clEarProblemTwoMonths ));
            cDeatail.put(Flag.clEarProblemTwoMonthsTwoCODE,getSingleJSON(clEarProblemTwoMonthsTwo(),Flag.clEarProblemTwoTwoMonths ));
            cDeatail.put(Flag.clEarProblemTwoMonthsThreeCODE,getSingleJSON(clEarProblemTwoMonthsThree(),Flag.clEarProblemThreeTwoMonths ));
            cDeatail.put(Flag.clCriticalFeverTwoMonthsCODE,getSingleJSON(clCriticalFeverTwoMonths(),Flag.clCriticalFeverTwoMonths ));
            cDeatail.put(Flag.clFeverMalariaTwoMonthsCODE,getSingleJSON(clFeverMalariaTwoMonths(),Flag.clFeverMalariaTwoMonths));
            cDeatail.put(Flag.clFeverTwoMonthsCODE,getSingleJSON(clFeverTwoMonths(),Flag.clFeverTwoMonths ));
            cDeatail.put(Flag.clMeaslesTwoMonthsCODE,getSingleJSON(clMeaslesTwoMonths(),Flag.clMeaslesTwoMonths ));
            cDeatail.put(Flag.clMeaslesTwoTwoMonthsCODE,getSingleJSON(clMeaslesTwoTwoMonths(),Flag.clMeaslesTwoTwoMonths ));
            cDeatail.put(Flag.clMeaslesCODE,getSingleJSON(clMeasles(),Flag.clMeasles ));
            cDeatail.put(Flag.clComplexSevereAcuteMalnutritionCODE,getSingleJSON(clComplexSevereAcuteMalnutrition(),Flag.clComplexSevereAcuteMalnutrition));
            cDeatail.put(Flag.clSevereAcuteMalnutritionWithoutComplicationsCODE,getSingleJSON(clSevereAcuteMalnutritionWithoutComplications(),Flag.clSevereAcuteMalnutritionWithoutComplications));
            cDeatail.put(Flag.clModeratetoSevereMalnutritionCODE,getSingleJSON(clModeratetoSevereMalnutrition(),Flag.clModeratetoSevereMalnutrition));
            cDeatail.put(Flag.clNoAcuteMalnutritionCODE,getSingleJSON(clNoAcuteMalnutrition(),Flag.clNoAcuteMalnutrition));
            cDeatail.put(Flag.clOtherTwoMonthsCODE,getSingleJSON(clOtherTwoMonths(),Flag.clOtherTwoMonths ));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cDeatail;
    }

    public static JSONObject getSingleJSON(JSONObject cJSON, String cName)throws JSONException{
        JSONObject single = new JSONObject();
        single.put("json",cJSON);
        single.put("name",cName);
        return single;
    }

}
