package org.sci.rhis.utilities;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by hajjaz.ibrahim on 3/21/2018.
 */

public class ChildCareLogicHelper {

    public static JSONObject getClassifications(JSONObject inputJSON) throws JSONException {
        JSONObject results = new JSONObject();
        StringBuilder previewString = new StringBuilder();
        JSONArray classifications = new JSONArray();
        String strClassifications = "";
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject classificationList = ConstantJSONs.classificationDetail();
        Iterator iterator = classificationList.keys();

        previewString.append("লক্ষণঃ \n ");
        while (iterator.hasNext()){
            String singleClass = (String) iterator.next();
            if(classificationList.getJSONObject(singleClass).getJSONObject("json") != null){
                JSONObject checkedJSON  = checkClassification(inputJSON,classificationList.getJSONObject(singleClass)
                        .getJSONObject("json"),singleClass);
                if(checkedJSON.has("classified")) {
                    classifications.put(singleClass);
                    strClassifications += singleClass+",";
                    previewString.append(checkedJSON.getString("impactedInputs"));
                }
            }
        }

        Log.i("Asgard",strClassifications);

        previewString.append("\n");
        if(classifications!=null && classifications.length()>0){
            results.put("previewText",previewString.toString());

            strClassifications = keepPrioritizedClassOnly(classifications,stringBuilder).toString();
            Log.i("Loki",strClassifications);
            results.put("classifications",strClassifications);

        }

        return results;
    }

    private static StringBuilder keepPrioritizedClassOnly(JSONArray classifications, StringBuilder stringBuilder){

        boolean setCriticalDisease = false;
        boolean setJaundiceDisease = false;
        boolean setDiarrhoeaDisease = false;
        boolean setEatingProblemDisease = false;
        boolean setEarProblem = false;
        boolean noDisease = false;
        boolean pneumoniaTwoMonths = false;
        boolean normalFlu = false;
        boolean criticalFever = false;
        boolean measles = false;
        boolean malnutrition = false;
        boolean diarrhoea2M = false;
        for(int i=0;i<classifications.length();i++)
        {

            try {
                if(Arrays.asList(Flag.criticalGroupZeroToFiftynineDays).contains(classifications.get(i).toString()))
                {
                    if(!setCriticalDisease) {
                        setCriticalDisease = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.jaundiceGroupZeroToFiftynineDays).contains(classifications.get(i).toString()))
                {
                    if(!setJaundiceDisease) {
                        setJaundiceDisease = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.diarrhoeaGroupZeroToFiftynineDays).contains(classifications.get(i).toString()))
                {
                    if(!setDiarrhoeaDisease) {
                        setDiarrhoeaDisease = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.eatingProblemGroupZeroToFiftynineDays).contains(classifications.get(i).toString()))
                {
                    if(!setEatingProblemDisease) {
                        setEatingProblemDisease = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.criticalDiseaseTwoMonths).contains(classifications.get(i).toString()))
                {

                    stringBuilder.append(classifications.get(i).toString());
                    stringBuilder.append(",");

                }
                else if(Arrays.asList(Flag.earProblemTwoMonths).contains(classifications.get(i).toString()))
                {
                    if(!setEarProblem) {
                        setEarProblem = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.noDisease).contains(classifications.get(i).toString()))
                {
                    if(!noDisease) {
                        noDisease = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.pneumoniaTwoMonths).contains(classifications.get(i).toString()))
                {
                    if(!pneumoniaTwoMonths) {
                        pneumoniaTwoMonths = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.fluTwoMonths).contains(classifications.get(i).toString()))
                {
                    if(!normalFlu) {
                        normalFlu = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.criticalFever).contains(classifications.get(i).toString()))
                {
                    if(!criticalFever) {
                        criticalFever = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.measles).contains(classifications.get(i).toString()))
                {
                    if(!measles) {
                        measles = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.malntration).contains(classifications.get(i).toString()))
                {
                    if(!malnutrition) {
                        malnutrition = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
                else if(Arrays.asList(Flag.diarrhoea2Mto5Y).contains(classifications.get(i).toString()))
                {
                    if(!diarrhoea2M) {
                        diarrhoea2M = true;
                        stringBuilder.append(classifications.get(i).toString());
                        stringBuilder.append(",");

                    }
                }
            } catch (JSONException e) {
            e.printStackTrace();
        }

        }

        return stringBuilder;
    }

    private static JSONObject checkClassification(JSONObject inputJSON, JSONObject classifiedJSON, String classCode){
        JSONObject classificationDetail = new JSONObject();
        ArrayList<String> matchedSymptoms = new ArrayList<>();
        int count=0;
        try {
            JSONObject logics = classifiedJSON.getJSONObject("logics");
            Iterator<String> keys = logics.keys();
            StringBuilder impactedInputs = new StringBuilder();
            while(keys.hasNext()){
                String key = keys.next();
                if(inputJSON.has(key)){
                    String actualVal = inputJSON.getString(key);
                    String dataType = logics.getJSONObject(key).getString("type");
                    String operation = logics.getJSONObject(key).getString("operation");
                    JSONObject values = logics.getJSONObject(key).getJSONObject("value");
                    boolean isActualRequired = logics.getJSONObject(key).has("actual_value_required");
                    if(!actualVal.equals("")){
                        //TODO: matchLogic should be called once
                        if(matchLogic(actualVal,values,dataType,operation,isActualRequired)!=null){
                            count += 1;
                            matchedSymptoms.add(key);
                            String answerText = matchLogic(actualVal,values,dataType,operation,isActualRequired);
                            impactedInputs.append(" - " + logics.getJSONObject(key).getString("text")+" : "+answerText+"\n");
                        }
                    }
                }
            }
            if(classifiedJSON.getInt("minConditionForClassification")==Flag.SPECIAL){
                if(matchSpecialLogic(matchedSymptoms,classCode)){
                    classificationDetail.put("classified",true);
                    classificationDetail.put("impactedInputs",impactedInputs.toString());
                }
            }else{
                if(count>=classifiedJSON.getInt("minConditionForClassification")){
                    classificationDetail.put("classified",true);
                    classificationDetail.put("impactedInputs",impactedInputs.toString());
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classificationDetail;
    }

    private static String matchLogic(String actualVal, JSONObject logicVal, String dataType, String operation, boolean isActualRequired){
        double doubleActualVal=-1;
        if(!dataType.equals("String")) doubleActualVal = Double.valueOf(actualVal);

        try {
            switch(operation){
                case "=":
                    if(doubleActualVal>0){
                        String checkingKey = logicVal.keys().next();
                        if (doubleActualVal==Double.valueOf(checkingKey)) {
                            return logicVal.getString(checkingKey);
                        }
                    }else{
                        //do later
                    }
                    break;

                case "in":
                    Iterator<String> multipleLogics = logicVal.keys();
                    while (multipleLogics.hasNext()){
                        if(doubleActualVal>0){
                            String checkingKey = multipleLogics.next();
                            if (doubleActualVal==Double.valueOf(checkingKey)) {
                                return logicVal.getString(checkingKey);
                            }
                        }else{
                            //do later
                        }
                    }
                    break;

                case "<>":
                    Iterator<String> comparativeLogics = logicVal.keys();
                    while (comparativeLogics.hasNext()){
                        String checkingKey = comparativeLogics.next();
                        if(doubleActualVal > 0){
                            if (checkingKey.split(":")[0].equals("1")){
                                if (doubleActualVal < Double.valueOf(checkingKey.split(":")[1])) {
                                    String s = logicVal.getString(String.valueOf(checkingKey));
                                    return (isActualRequired ? (actualVal + " - ") : "") + s;
                                }
                            }else{
                                if (doubleActualVal > Double.valueOf(checkingKey.split(":")[1])) {
                                    String s = logicVal.getString(String.valueOf(checkingKey));
                                    return (isActualRequired ? (actualVal + " - ") : "") + s;
                                }
                            }

                        }else{
                            //do later
                        }
                    }
                    break;

                case "<":
                    Iterator<String> comparativeLogic = logicVal.keys();
                    while (comparativeLogic.hasNext()){
                        if(doubleActualVal > 0){
                            String checkingKey = comparativeLogic.next();
                            if (doubleActualVal < Double.valueOf(checkingKey)) {
                                String s = logicVal.getString(String.valueOf(checkingKey));
                                return (isActualRequired ? (actualVal + " - ") : "") + s;
                            }

                        }else{
                            //do later
                        }
                    }
                    break;

                case ">":
                    Iterator<String> comparativeGreaterLogic = logicVal.keys();
                    while (comparativeGreaterLogic.hasNext()){
                        if(doubleActualVal > 0){
                            String checkingKey = comparativeGreaterLogic.next();
                            if (doubleActualVal > Double.valueOf(checkingKey)) {
                                String s = logicVal.getString(String.valueOf(checkingKey));
                                return (isActualRequired ? (actualVal + " - ") : "") + s;
                            }

                        }else{
                            //do later
                        }
                    }
                    break;
            }

        }catch (JSONException jse){
            jse.printStackTrace();
        }

        return null;
    }

    private static boolean matchSpecialLogic(ArrayList<String> matchedSymptoms, String classCode){
        switch (classCode){
            case Flag.clComplexSevereAcuteMalnutritionCODE:
                //TODO:has to replace the magic numbers
                if((matchedSymptoms.contains("86")||matchedSymptoms.contains("87")||matchedSymptoms.contains(88))
                            &&
                        (matchedSymptoms.contains("83")||matchedSymptoms.contains("84")||matchedSymptoms.contains("152"))){
                    return true;
                 }
                 break;
            case Flag.clSevereAcuteMalnutritionWithoutComplicationsCODE:
                if((matchedSymptoms.contains("86")||matchedSymptoms.contains("88"))
                        &&
                        (matchedSymptoms.contains("85"))){
                    return true;
                }
                break;
        }
        return false;

    }

    public static boolean isReferRequired(JSONObject detailJSON){
        boolean isRequired=false;

        if (detailJSON.has("classifications")) {
            String[] strArrayClassifications;
            try {
                int dayDiff = (int) ((Utilities.getDateDiff(Converter.stringToDate
                                (Constants.SHORT_HYPHEN_FORMAT_DATABASE,
                                        detailJSON.getString("indicationStartDate")),
                        new Date(),TimeUnit.DAYS)));
                strArrayClassifications = detailJSON.getString("classifications").split(",");
                for (int i = 0; i < strArrayClassifications.length; i++) {
                    int classificationCode = Integer.valueOf(strArrayClassifications[i]);
                    if (Arrays.asList(new Integer[]{1,2,3,7,8,9,15,18,19,20,23,24}).contains(classificationCode)) {
                        isRequired = true;
                    }else if(classificationCode==13){
                        //conditional refer due to low weight
                        if(detailJSON.has("2") && !detailJSON.getString("2").equals("")
                               && Integer.valueOf(detailJSON.getString("2"))<2000){
                            isRequired = true;
                        }
                    }else if(Arrays.asList(new Integer[]{16,17}).contains(classificationCode)){
                        //conditional refer due more 14 days old pneumonia child

                        if(dayDiff>14) isRequired = true;
                    }else if(classificationCode==33){
                        //depends on some direct refer classifications
                    }else if(Arrays.asList(new Integer[]{21,22}).contains(classificationCode)){
                        //conditional refer due more 7 days old fever/malaria affected child

                        if(dayDiff>7) isRequired = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException pe){
                pe.printStackTrace();
            }
        }

        return isRequired;
    }

}
