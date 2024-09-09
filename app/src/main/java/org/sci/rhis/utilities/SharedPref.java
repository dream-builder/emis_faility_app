package org.sci.rhis.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.Set;

/**
 * Created by arafat.hasan on 9/26/2016.
 */
public class SharedPref {
    static SharedPreferences sharedPreferences = null;
    /**
     * Setting Section Start.........................................................................................................
     **/

    public static void setSyncUrl(Context context, String syncUrl){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_URL_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_SYNC_URL, syncUrl);
        editor.commit();
    }

    public static void setBaseUrl(Context context, String baseUrl){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_URL_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_BASE_URL, baseUrl);
        editor.commit();
    }

    public static void setDistrictCode(Context context, int districtKey){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_DISTRICT_MAPPING_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.KEY_DISTRICT_CODE_PREF, districtKey);
        editor.commit();
    }

    public static void setDistrictChoiceDialog(Context context, boolean isDistrictSettingRequired){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_DISTRICT_MAPPING_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_DISTRICT_SELECT_PREF, isDistrictSettingRequired);
        editor.commit();
    }

    public static void loadStaticTable(Context context) {
        sharedPreferences = context.getSharedPreferences(String.valueOf(Constants.SHARED_LIST_IDENTIFIER), 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(String.valueOf(Constants.TREATMENT_LIST_IDENTIFIER), Constants.TABLE_TREATMENT_LIST);
        editor.putString(String.valueOf(Constants.DISEASE_LIST_IDENTIFIER), Constants.TABLE_DISEASE_LIST);
        editor.putString(String.valueOf(Constants.SYMPTOM_LIST_IDENTIFIER), Constants.TABLE_SYMPTOM_LIST);
        //TODO - uncomment the following line when the advice list available in local database
        //editor.putString(String.valueOf(Constants.ADVICE_LIST_IDENTIFIER), Constants.TABLE_ADVICE_LIST);
        editor.commit();

    }

    public static void setCrashFlag(Context context, boolean flag){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_CRASH_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_CRASH_FLAG, flag);
        editor.commit();
    }

    public static void setLangSelection(Context context, int languageSelection){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_LANG_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.KEY_LANG_SELECTION, languageSelection);
        editor.commit();
    }

    public static void setProviderInfo(Context context, String provCode, String provName){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PROVIDER_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_PROVIDER, provCode);
        editor.putString(Constants.KEY_PROVIDER_NAME, provName);
        editor.commit();
    }

    public static void setFWANameHistory(Context context, Set<String> satList){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_FWA_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(Constants.KEY_FWA_LIST,satList);
        editor.commit();
    }
    public static void setFWAIDHistory(Context context, Set<String> satList){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_FWA_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(Constants.KEY_FWA_ID_LIST,satList);
        editor.commit();
    }

    public static void performIndexQuery(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_QUERY_DONE_STATUS, true);
        editor.commit();
    }

    public static void setCrashMessage(Context context, String crashMessage){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_CRASH_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_CRASH_TEXT,crashMessage);
        editor.commit();
    }

    public static void setOnlineLoginDate(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_LAST_ONLINE_LOGIN_DATE, Utilities.getDateStringDBFormat());
        editor.commit();
    }

    public static void setTestModeRequest(Context context, boolean onTestMode){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_ONTEST_TEXT,onTestMode);
        editor.commit();
    }

    public static void setLoginDate(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_LOGIN_DATE,Utilities.getDateStringDBFormat());
        editor.commit();
    }

    public static void setLastSearchResult(Context context, String result, String searchParameter){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_LAST_SEARCH_RESULT,result);
        editor.putString(Constants.KEY_LAST_SEARCH_PARAMETER,searchParameter);
        editor.commit();
    }

    public static void setHelplineNumber(Context context, String number){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_HELPLINE,number);
        editor.commit();
    }

    public static void setNewNotice(Context context, String notice){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_LAST_NOTICE,notice);
        editor.commit();
    }

    //This flag is set when dbdownload task has started to ensure internal db to db synchronization
    // process before going to dashboard
    public static void setInternalDBSynchronizationFlag(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_INTERNAL_DB_SYNC,true);
        editor.commit();
    }

    /*Setting Section End....................................................*/

    /**
     * Getting Section Start.....................................................................................................
     **/

    public static String getBaseUrl(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_URL_PREF, 0);
        String baseUrl = sharedPreferences.getString(Constants.KEY_BASE_URL, null);
        return baseUrl;
    }

    public static int getDistrictCode(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_DISTRICT_MAPPING_PREF, 0);
        return sharedPreferences.getInt(Constants.KEY_DISTRICT_CODE_PREF, -1);
    }

    public static boolean isDistrictChoiceRequired(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_DISTRICT_MAPPING_PREF, 0);
        return sharedPreferences.getBoolean(Constants.KEY_DISTRICT_SELECT_PREF, true);
    }

    public static boolean hasLoggedInToday(Context mContext){
        sharedPreferences = mContext.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        String lastLoginDate = sharedPreferences.getString(Constants.KEY_LOGIN_DATE, null);
        return lastLoginDate==null?true:(lastLoginDate.equals(Utilities.getDateStringDBFormat()));
    }

    public static String lastOnlineLogin(Context mContext){
        sharedPreferences = mContext.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        String lastOnlineLogin = sharedPreferences.getString(Constants.KEY_LAST_ONLINE_LOGIN_DATE, null);
        return lastOnlineLogin;
    }

    public static String getSyncUrl(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_URL_PREF, 0);
        String syncUrl = sharedPreferences.getString(Constants.KEY_SYNC_URL, null);
        return syncUrl;
    }

    public static String getProviderCode(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PROVIDER_PREF, 0);
        String provCode = sharedPreferences.getString(Constants.KEY_PROVIDER, null);
        return provCode;
    }

    public static String getProviderName(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PROVIDER_PREF, 0);
        String provName = sharedPreferences.getString(Constants.KEY_PROVIDER_NAME, null);
        return provName;
    }

    public static boolean getCrashFlag(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_CRASH_PREF, 0);
        boolean crashFlag = sharedPreferences.getBoolean(Constants.KEY_CRASH_FLAG, false);
        return crashFlag;
    }

    public static boolean isOnTestMode(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        boolean isOnTest = sharedPreferences.getBoolean(Constants.KEY_ONTEST_TEXT, false);
        return isOnTest;
    }

    public static String getCrashDetail(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_CRASH_PREF, 0);
        String crashDetail = sharedPreferences.getString(Constants.KEY_CRASH_TEXT, null);
        return crashDetail;
    }

    public static boolean getQueryDoneStatus(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        boolean isDone = sharedPreferences.getBoolean(Constants.KEY_QUERY_DONE_STATUS, false);
        return isDone;
    }

    public static int getSelectedLang(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_LANG_PREF, 0);
        int langSelection = sharedPreferences.getInt(Constants.KEY_LANG_SELECTION,-1);
        return langSelection;
    }


    public static Set<String> getFWAHistory(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_FWA_PREF, 0);
        Set<String> satHistory = sharedPreferences.getStringSet(Constants.KEY_FWA_LIST,null);
        return satHistory;
    }

    public static Set<String> getFWAIDHistory(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_FWA_PREF, 0);
        Set<String> satFWAIDHistory = sharedPreferences.getStringSet(Constants.KEY_FWA_ID_LIST,null);
        return satFWAIDHistory;
    }
    public static String getLastSearchResult(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        String lastSearchResult = sharedPreferences.getString(Constants.KEY_LAST_SEARCH_RESULT,null);
        return lastSearchResult;
    }

    public static String getHelpline(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        String helplineNumber = sharedPreferences.getString(Constants.KEY_HELPLINE,null);
        return helplineNumber;
    }

    public static String getLastNotice(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        String lastNotice = sharedPreferences.getString(Constants.KEY_LAST_NOTICE,null);
        return lastNotice;
    }

    public static boolean isInternalDBSyncRequired(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        boolean isRequired = sharedPreferences.getBoolean(Constants.KEY_INTERNAL_DB_SYNC,false);
        return isRequired;
    }

    /*Clearing Section Start..................................................................................*/
    /*this required to call so that the last crash message will be cleared from crash after sending
        it to server*/
    public static void clearCrashDetail(Context context) {
        setCrashMessage(context,"");
    }

    public static void clearProviderData(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PROVIDER_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

    }

    //crash flag clearing is required so that the crash dialog won't appear in next login if no issue will occur
    public static void clearCrashFlag(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_CRASH_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_CRASH_FLAG, false);//clearing flag by setting false
        editor.commit();

    }

    public static void clearStaticTable(Context context) {
        sharedPreferences = context.getSharedPreferences(String.valueOf(Constants.SHARED_LIST_IDENTIFIER), 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

    }

    public static void clearInternalDBSynchronizationFlag(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_MISC_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_INTERNAL_DB_SYNC,false);
        editor.commit();
    }

}

