package org.sci.rhis.utilities;

import org.sci.rhis.fwc.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arafat.hasan on 9/29/2016.
 */
public class ConstantMaps {

    public static final Map<Integer, String> codeWiseFPTags = Collections.unmodifiableMap(
            new HashMap<Integer, String>() {{
                put(0,"");
                put(Flag.CONDOM,Flag.CONDOM_TEXT);
                put(Flag.PILL_SUKHI,Flag.PILL_SUKHI_TEXT);
                put(Flag.PILL_APON,Flag.PILL_APON_TEXT);
                put(Flag.PILL_OTHER,Flag.PILL_OTHER_TEXT);
                put(Flag.INJECTION_DMPA,Flag.INJECTION_DMPA_TEXT);
                put(Flag.IUD,Flag.IUD_TEXT);
                put(Flag.IMPLANT_IMPLANON,Flag.IMPLANT_IMPLANON_TEXT);
                put(Flag.IMPLANT_JADELLE,Flag.IMPLANT_JADELLE_TEXT);
                put(Flag.PERMANENT_METHOD_MAN,Flag.PERMANENT_METHOD_MAN_TEXT);
                put(Flag.PERMANENT_METHOD_WOMAN,Flag.PERMANENT_METHOD_WOMAN_TEXT);
            }});

    public static final Map<String, Integer> FPTagWiseCodes = Collections.unmodifiableMap(
            new HashMap<String, Integer>() {{
                put("",0);
                put(Flag.CONDOM_TEXT,Flag.CONDOM);
                put(Flag.PILL_SUKHI_TEXT,Flag.PILL_SUKHI);
                put(Flag.PILL_APON_TEXT,Flag.PILL_APON);
                put(Flag.PILL_OTHER_TEXT,Flag.PILL_OTHER);
                put(Flag.INJECTION_DMPA_TEXT,Flag.INJECTION_DMPA);
                put(Flag.IUD_TEXT,Flag.IUD);
                put(Flag.IMPLANT_IMPLANON_TEXT,Flag.IMPLANT_IMPLANON);
                put(Flag.IMPLANT_JADELLE_TEXT,Flag.IMPLANT_JADELLE);
                put(Flag.PERMANENT_METHOD_MAN_TEXT,Flag.PERMANENT_METHOD_MAN);
                put(Flag.PERMANENT_METHOD_WOMAN_TEXT,Flag.PERMANENT_METHOD_WOMAN);
            }});

    public static final Map<String, Integer> TagWiseMethodCodes = Collections.unmodifiableMap(
            new HashMap<String, Integer>() {{
                put("",0);
                put(Flag.CONDOM_TEXT,Flag.CONDOM);
                put(Flag.PILL_SUKHI_TEXT,Flag.PILL_SUKHI);
                put(Flag.PILL_APON_TEXT,Flag.PILL_APON);
                put(Flag.PILL_OTHER_TEXT,Flag.PILL_OTHER);
                put(Flag.INJECTION_DMPA_TEXT,Flag.INJECTION_DMPA);
                put(Flag.IUD_TEXT,Flag.IUD);
                put(Flag.IMPLANT_TEXT,Flag.IMPLANT_IMPLANON);
                put(Flag.PERMANENT_METHOD_MAN_TEXT,Flag.PERMANENT_METHOD_MAN);
                put(Flag.PERMANENT_METHOD_WOMAN_TEXT,Flag.PERMANENT_METHOD_WOMAN);
            }});

    public static final Map<String, String> localeMapping = Collections.unmodifiableMap(
            new HashMap<String, String>() {{
                put(Flag.LANG_ENGLISH_US,Flag.LANG_ENGLISH_US_CODE);
                put(Flag.LANG_BENGALI,Flag.LANG_BENGALI_CODE);
                put(Flag.LANG_MALAYALAM,Flag.LANG_MALAYALAM_CODE);
                put(Flag.LANG_ARABIC,Flag.LANG_ARABIC_CODE);
                put(Flag.LANG_GERMAN,Flag.LANG_GERMAN_CODE);
            }});

    /*
*/
                //put(Flag.HABIGANJ,"http://mamoni.net:8080/eMIS_SSP_DEV/");//Madhabpur only 36_68_DEV
    public static final Map<String, String> MonthNameEnglishToBengali = Collections.unmodifiableMap(
            new HashMap<String, String>() {{
                put("January","জানুয়ারী");
                put("February","ফেব্রুয়ারী");
                put("March","মার্চ");
                put("April","এপ্রিল");
                put("May","মে");
                put("June","জুন");
                put("July","জুলাই");
                put("August","আগস্ট");
                put("September","সেপ্টেম্বর");
                put("October","অক্টোবর");
                put("November","নভেম্বর");
                put("December","ডিসেম্বর");
            }});

    public static final Map<String, String> referredChildClassifications = Collections.unmodifiableMap(
            new HashMap<String, String>() {{
                put(Flag.clSevereIllnessCriticalConditionCODE,Flag.clSevereIllnessCriticalCondition);
                put(Flag.clSevereIllnessBacteriaInfectionCODE,Flag.clSevereIllnessBacteriaInfection);
                put(Flag.clSeverePneumoniaCODE,Flag.clSeverePneumonia);
                put(Flag.clSevereJaundiceCODE,Flag.clSevereJaundice);
                put(Flag.clJaundiceCODE,Flag.clJaundice);
                put(Flag.clDiarrhoeaSevereDehydrationCODE,Flag.clDiarrhoeaSevereDehydration);
                put(Flag.clDiarrhoeaTwoMonthsCODE,Flag.clDiarrhoeaTwoMonths);
                put(Flag.clDiarrhoeaTwoMonthsCODE3,Flag.clDiarrhoeaTwoMonths3);
                put(Flag.clDiarrhoeaTwoMonthsCODE4,Flag.clDiarrhoeaTwoMonths4);
                put(Flag.clVeryCriticalDiseaseTwoMonthsCODE,Flag.clVeryCriticalDiseaseTwoMonths);
                put(Flag.clNormalColdTwoMonthsCODE,Flag.clNormalColdTwoMonths);
                put(Flag.clCriticalFeverTwoMonthsCODE,Flag.clCriticalFeverTwoMonths);
                put(Flag.clEarProblemTwoMonthsCODE,Flag.clEarProblemTwoMonths);
                put(Flag.clLowWeightCODE,Flag.clLowWeight);
                put(Flag.clPneumoniaTwoMonthsCODE,Flag.clPneumoniaTwoMonths);
                put(Flag.clFeverMalariaTwoMonthsCODE,Flag.clFeverMalariaTwoMonths);
                put(Flag.clFeverTwoMonths,Flag.clFeverTwoMonthsCODE);
                put(Flag.clFeverTwoMonthsCODE,Flag.clFeverTwoMonths);
                put(Flag.clMeaslesTwoMonthsCODE,Flag.clMeaslesTwoMonths);
                put(Flag.clComplexSevereAcuteMalnutritionCODE,Flag.clComplexSevereAcuteMalnutrition);
            }});

    public static final Map<String, String[]> tableListWithPKey = Collections.unmodifiableMap(
            new HashMap<String, String[]>() {{
                put("pregwomen",new String[]{"healthid","pregno"});
                put("ancservice",new String[]{"healthid","pregno","serviceid"});
                put("delivery",new String[]{"healthid","pregno"});
                put("newborn",new String[]{"healthid","pregno","childno"});
                put("pncservicechild",new String[]{"healthid","pregno","childno", "serviceid"});
                put("pncservicemother",new String[]{"healthid","pregno","serviceid"});
                put("pacservice",new String[]{"healthid","pregno","serviceid"});
                put("fpinfo",new String[]{"healthid"});
                put("fpexamination",new String[]{"healthid","serviceid","fptype"});
                put("pillcondomservice",new String[]{"healthid","serviceid"});
                put("womaninjectable",new String[]{"healthid","doseid"});
                put("iudservice",new String[]{"healthid","iudcount"});
                put("iudfollowupservice",new String[]{"healthid","iudcount","serviceid"});
                put("implantservice",new String[]{"healthid","implantcount"});
                put("implantfollowupservice",new String[]{"healthid","implantcount","serviceid"});
                put("permanent_method_service",new String[]{"healthid","pmscount"});
                put("permanent_method_followup_service",new String[]{"healthid","pmscount","serviceid"});
                put("elco",new String[]{"healthid"});
                put("death",new String[]{"healthid","pregno","childno"});
                put("gpservice",new String[]{"healthid","serviceid"});
                put("child_care_service",new String[]{"healthid","systementrydate"});
                put("child_care_service_detail",new String[]{"healthid","entrydate","inputid"});

            }});


    public static final Map<Integer, Integer> FP_METHOD_MAPPING_FOR_HISTORY = Collections.unmodifiableMap(
            new HashMap<Integer, Integer>() {{
                put(Flag.CONDOM,Flag.CONDOM_COMMON);
                put(Flag.PILL_SUKHI,Flag.PILL_SUKHI_COMMON);
                put(Flag.PILL_APON,Flag.PILL_APON_COMMON);
                put(Flag.PILL_OTHER,Flag.PILL_OTHER_COMMON);
                put(Flag.INJECTION_DMPA,Flag.INJECTION_DMPA_COMMON);
                put(Flag.IUD,Flag.IUD_COMMON);
                put(Flag.IMPLANT_IMPLANON,Flag.IMPLANT_IMPLANON_COMMON);
                put(Flag.IMPLANT_JADELLE,Flag.IMPLANT_JADELLE_COMMON);
                put(Flag.PERMANENT_METHOD_MAN,Flag.PERMANENT_METHOD_MAN_COMMON);
                put(Flag.PERMANENT_METHOD_WOMAN,Flag.PERMANENT_METHOD_WOMAN_COMMON);
            }});

    public static final Map<Integer,String> SateliteStatusTextsMapping =  Collections.unmodifiableMap(
            new HashMap<Integer, String>() {{
                put(Flag.PENDING,"Not Submitted");
                put(Flag.SUBMITTED,"Submitted");
                put(Flag.APPROVED,"Approved");
                put(Flag.REJECTED,"Rejected");
            }});


    public static final Map<Integer,Integer> SateliteStatusColorMapping =  Collections.unmodifiableMap(
            new HashMap<Integer, Integer>() {{
                put(Flag.PENDING, GlobalActivity.context.getResources().getColor(R.color.yellow));
                put(Flag.SUBMITTED,GlobalActivity.context.getResources().getColor(R.color.royalblue));
                put(Flag.APPROVED,GlobalActivity.context.getResources().getColor(R.color.forestgreen));
                put(Flag.REJECTED,GlobalActivity.context.getResources().getColor(R.color.darkred));
            }});

    public static final Map<String,Integer> MonthIndexing =  Collections.unmodifiableMap(
            new HashMap<String, Integer>() {{
                put("জানুয়ারী",1);
                put("ফেব্রুয়ারী",2);
                put("মার্চ",3);
                put("এপ্রিল",4);
                put("মে",5);
                put("জুন",6);
                put("জুলাই",7);
                put("আগস্ট",8);
                put("সেপ্টেম্বর",9);
                put("অক্টোবর",10);
                put("নভেম্বর",11);
                put("ডিসেম্বর",12);
            }});

}
