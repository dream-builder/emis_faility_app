package org.sci.rhis.utilities;

import org.sci.rhis.fwc.R;

/**
 * Created by arafat.hasan on 5/17/2016.
 */
public class Flag {
    public static final int CHANGE = 1;
    public static final int RESULT = 2;
    public static final int COUNSEL_DECISION = 3;

    public static final int HIGHBP = 1;
    public static final int NORMALBP = 2;

    public static final int UPDATE_THRESHOLD = 40;

    //MISCELLANEOUS
    public static final int CONDOM = 2;
    public static final int PILL_SUKHI = 1;
    public static final int PILL_APON = 10;
    public static final int PILL_OTHER = 999;
    public static final int INJECTION_DMPA = 3;
    public static final int IUD = 4;
    public static final int IMPLANT_IMPLANON = 6;
    public static final int IMPLANT_JADELLE = 7;
    public static final int PERMANENT_METHOD_MAN = 998;
    public static final int PERMANENT_METHOD_WOMAN = 997;

    public static final String CONDOM_TEXT = GlobalActivity.context.getResources().getString(R.string.condom);
    public static final String PILL_SUKHI_TEXT = GlobalActivity.context.getResources().getString(R.string.pill_sukhi);
    public static final String PILL_APON_TEXT = GlobalActivity.context.getResources().getString(R.string.pill_apon);
    public static final String PILL_OTHER_TEXT = GlobalActivity.context.getResources().getString(R.string.pill_other);
    public static final String INJECTION_DMPA_TEXT = GlobalActivity.context.getResources().getString(R.string.injection_dmpa);
    public static final String IUD_TEXT = GlobalActivity.context.getResources().getString(R.string.iud);
    public static final String IMPLANT_TEXT = GlobalActivity.context.getResources().getString(R.string.implant);
    public static final String IMPLANT_IMPLANON_TEXT = GlobalActivity.context.getResources().getString(R.string.implant_implanon);
    public static final String IMPLANT_JADELLE_TEXT = GlobalActivity.context.getResources().getString(R.string.implant_jadelle);
    public static final String PERMANENT_METHOD_MAN_TEXT = GlobalActivity.context.getResources().getString(R.string.pm_male);
    public static final String PERMANENT_METHOD_WOMAN_TEXT = GlobalActivity.context.getResources().getString(R.string.pm_female);

    public static final String LANG_BENGALI = "বাংলা";
    public static final String LANG_BENGALI_CODE = "bn";
    public static final String LANG_ENGLISH_US = "English(US)";
    public static final String LANG_ENGLISH_US_CODE = "en_US";
    public static final String LANG_MALAYALAM = "മലയാളം";
    public static final String LANG_MALAYALAM_CODE = "ml";
    public static final String LANG_ARABIC = "العربية";
    public static final String LANG_ARABIC_CODE = "ar";
    public static final String LANG_GERMAN = "Deutsche";
    public static final String LANG_GERMAN_CODE = "de";


    //TODO: Don't change the previous order during adding a new language into the list
    public static final String[] langauageArray = {LANG_ENGLISH_US,LANG_BENGALI,LANG_MALAYALAM,LANG_ARABIC,LANG_GERMAN};

    public static final String clSevereIllnessCriticalCondition = "খুব মারাত্মক রোগ - সংকটাপন্ন অসুস্থতা";
    public static final String clSevereIllnessBacteriaInfection = "খুব মারাত্মক রোগ - সম্ভাব্য মারাত্মক ব্যাকটেরিয়াল সংক্রমণ";
    public static final String clSeverePneumonia = "খুব মারাত্মক রোগ - দ্রুত শ্বাস নিউমোনিয়া";
    public static final String clPneumonia = "দ্রুত শ্বাস নিউমোনিয়া";
    public static final String clLocalBacterialInfection = "স্থানীয় ব্যাকটেরিয়াল সংক্রমণ";
    public static final String clNotSevereNorBacterialInfection = "মারাত্মক রোগ অথবা সম্ভাব্য মারাত্মক ব্যাকটেরিয়াল সংক্রমণ নয় ";
    public static final String clSevereJaundice = "মারাত্মক জন্ডিস  ";
    public static final String clJaundice = "জন্ডিস";
    public static final String clDiarrhoeaSevereDehydration = "চরম পানি স্বল্পতা";
    public static final String clDiarrhoeaDehydration = "কিছু পানি স্বল্পতা";
    public static final String clDiarrhoeaNoDehydration = "পানিস্বল্পতা নাই";
    public static final String clEatingProblem = "খাওয়ানোর সমস্যা";
    public static final String clLowWeight = "কম ওজন ";
    public static final String clNoEatingProblem = "খাওয়ানোর সমস্যা নাই";
    public static final String clVeryCriticalDiseaseTwoMonths = "খুব মারাত্মক রোগ";
    public static final String clPneumoniaTwoMonths = "নিউমোনিয়া";
    public static final String clNormalColdTwoMonths = "নিউমোনিয়া নয় কাশি অথবা সর্দি";
    public static final String clDiarrhoeaTwoMonths = "চরম পানি স্বল্পতা";
    public static final String clDiarrhoeaTwoMonths2 = "কিছু পানি স্বল্পতা";
    public static final String clDiarrhoeaTwoMonths3 = "পানি স্বল্পতা নাই";
    public static final String clDiarrhoeaTwoMonths4 = "মারাত্মক দীর্ঘমেয়াদি ডায়রিয়া";
    public static final String clDiarrhoeaTwoMonths5 = "দীর্ঘমেয়াদী ডায়রিয়া";
    public static final String clDiarrhoeaTwoMonths6 = "আমাশয়";
    public static final String clEarProblemTwoMonths = "কানের সমস্যা: ম্যাসটয়ডাইটিস";
    public static final String clCriticalFeverTwoMonths = "খুব মারাত্মক জ্বর জনিত রোগ";
    public static final String clFeverMalariaTwoMonths = "জ্বর ম্যালেরিয়া";
    public static final String clFeverTwoMonths = "জ্বর ম্যালেরিয়া নয়";
    public static final String clMeaslesTwoMonths = "মারাত্মক জটিলতাসহ হাম";
    public static final String clMeaslesTwoTwoMonths = "চোখ ও মুখের জটিলতাসহ হাম";
    public static final String clMeasles = " হাম";
    public static final String clComplexSevereAcuteMalnutrition = "জটিল মারাত্মক তীব্র অপুষ্টি";
    public static final String clSevereAcuteMalnutritionWithoutComplications = "জটিলতা বিহীন মারাত্মক তীব্র অপুষ্টি";
    public static final String clModeratetoSevereMalnutrition = "মাঝারি তীব্র অপুষ্টি";
    public static final String clNoAcuteMalnutrition = "তীব্র অপুষ্টি নেই";
    public static final String clOtherTwoMonths = "অন্যান্য";
    public static final String clEarProblemTwoTwoMonths = "কানের তীব্র সংক্রমণ";
    public static final String clEarProblemThreeTwoMonths = "দীর্ঘস্থায়ী কান সংক্রমণ";

    public static final String clSevereIllnessCriticalConditionCODE = "1";
    public static final String clSevereIllnessBacteriaInfectionCODE = "2";
    public static final String clSeverePneumoniaCODE = "3";
    public static final String clPneumoniaCODE = "4";
    public static final String clLocalBacterialInfectionCODE = "5";
    public static final String clNotSevereNorBacterialInfectionCODE = "6";
    public static final String clSevereJaundiceCODE = "7";
    public static final String clJaundiceCODE = "8";
    public static final String clDiarrhoeaSevereDehydrationCODE = "9";
    public static final String clDiarrhoeaDehydrationCODE = "10";
    public static final String clDiarrhoeaNoDehydrationCODE = "11";
    public static final String clEatingProblemCODE = "12";
    public static final String clLowWeightCODE = "13";
    public static final String clNoEatingProblemCODE = "14";
    public static final String clVeryCriticalDiseaseTwoMonthsCODE = "15";
    public static final String clPneumoniaTwoMonthsCODE = "16";
    public static final String clNormalColdTwoMonthsCODE = "17";
    public static final String clDiarrhoeaTwoMonthsCODE = "33";
    public static final String clDiarrhoeaTwoMonthsCODE2 = "34";
    public static final String clDiarrhoeaTwoMonthsCODE3 = "37";
    public static final String clDiarrhoeaTwoMonthsCODE4 = "18";
    public static final String clDiarrhoeaTwoMonthsCODE5 = "35";
    public static final String clDiarrhoeaTwoMonthsCODE6 = "36";
    public static final String clEarProblemTwoMonthsCODE = "19";
    public static final String clCriticalFeverTwoMonthsCODE = "20";
    public static final String clFeverMalariaTwoMonthsCODE = "21";
    public static final String clFeverTwoMonthsCODE = "22";
    public static final String clMeaslesTwoMonthsCODE = "23";
    public static final String clComplexSevereAcuteMalnutritionCODE = "24";
    public static final String clSevereAcuteMalnutritionWithoutComplicationsCODE = "30";
    public static final String clModeratetoSevereMalnutritionCODE = "31";
    public static final String clNoAcuteMalnutritionCODE = "32";
    public static final String clOtherTwoMonthsCODE = "25";
    public static final String clEarProblemTwoMonthsTwoCODE = "26";
    public static final String clEarProblemTwoMonthsThreeCODE = "27";
    public static final String clMeaslesTwoTwoMonthsCODE = "28";
    public static final String clMeaslesCODE = "29";


    //Setting options
    public static final String OPTION_CHANGE_LANG = "Change Language";
    public static final String OPTION_CHANGE_DISTRICT = "Change Working District";
    public static final String OPTION_EXIT = "Cancel";
    public static final String[] optionsArray = {OPTION_CHANGE_LANG, OPTION_CHANGE_DISTRICT,OPTION_EXIT};


    //constant values of satellite session planning status
    public static final int PENDING = 3;
    public static final int SUBMITTED = 0;
    public static final int APPROVED = 1;
    public static final int REJECTED = 2;



    //The most priority one must be the smallest number
    public static final String[] criticalGroupZeroToFiftynineDays = {"1","2","3","4","5"};
    public static final String[] jaundiceGroupZeroToFiftynineDays = {"7","8"};
    public static final String[] diarrhoeaGroupZeroToFiftynineDays = {"9","10","11"};
    public static final String[] eatingProblemGroupZeroToFiftynineDays = {"12","13","14"};
    public static final String[] criticalDiseaseTwoMonths = {"15"};
    public static final String[] pneumoniaTwoMonths = {"16"};
    public static final String[] fluTwoMonths = {"17"};
    public static final String[] earProblemTwoMonths = {"19","26","27"};
    public static final String[] noDisease = {"6","25"};
    public static final String[] criticalFever = {"20","21","22"};
    public static final String[] measles = {"23","28","29"};
    public static final String[] malntration = {"24","30","31","32"};
    public static final String[] diarrhoea2Mto5Y = {"18","33","34","35","36","37"};

    public static final int SPECIAL = 99;

    public static final String SACMO_HS = "6";
    public static final String SACMO_FP = "5";
    public static final String FWV = "4";
    public static final String PARAMEDIC = "101";
    public static final String MIDWIFE = "17";


    public static final int CONDOM_COMMON = 2;
    public static final int PILL_SUKHI_COMMON = 8;
    public static final int PILL_APON_COMMON = 9;
    public static final int PILL_OTHER_COMMON = 1;
    public static final int INJECTION_DMPA_COMMON = 3;
    public static final int IUD_COMMON = 4;
    public static final int IMPLANT_IMPLANON_COMMON = 5;
    public static final int IMPLANT_JADELLE_COMMON = 10;
    public static final int PERMANENT_METHOD_MAN_COMMON = 6;
    public static final int PERMANENT_METHOD_WOMAN_COMMON = 7;

    public static final int E_TICKETING=1;
    public static final int MEETING_MINUTES=2;


}
