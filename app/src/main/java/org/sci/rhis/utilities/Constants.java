package org.sci.rhis.utilities;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONArray;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by arafat.hasan on 2/7/2016.
 */
public class Constants {


    public static final int ADD_CODE = 1;
    public static final int SUBTRACT_CODE = 2;

    public static final String TEST_BASE_URL="http://mamoni.net:8080/eMIS_SYM_DEV/";

    public static final int ANDROID_CLIENT_CODE = 2;
    public static final int ANDROID_CLIENT_OFFLINE_CODE = 22;

    public static final String FWV_ANDROID = "FWV_TAB";
    public static final String SACMO_ANDROID = "SACMO_TAB";

    public static final String SIDE_EFFECT_TAG = "*SideEffect";
    public static final String TREATMENT_TAG = "*Treatment";
    public static final String ADVICE_TAG = "*Advice";
    public static final String REFER_REASON_TAG = "*ReferReason";
    //public static String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/rhis_fwc/databases/";
    public static String getDB_PATH(){
        if(!SharedPref.isOnTestMode(GlobalActivity.context)){
            return Environment.getExternalStorageDirectory().getAbsolutePath()+"/rhis_fwc/databases/";
        }else {
            return Environment.getExternalStorageDirectory().getAbsolutePath()+"/fwc_test/databases/";
        }
    }
    public static String TEST_DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fwc_test/databases/";
    public static String CSBA_DB = Environment.getExternalStorageDirectory().getAbsolutePath()+"/emis/";

    /**
     * Common Simple Date Formats
     */
    public static final String SHORT_HYPHEN_FORMAT_DATABASE = "yyyy-MM-dd";
    public static final String SHORT_HYPHEN_FORMAT_YEAR_MONTH = "yyyy-MM";
    public static final String LONG_HYPHEN_FORMAT_DATABASE = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String SHORT_HYPHEN_FORMAT_WITH_TIMESTAMP_DATABASE = "yyyy-MM-dd hh:mm:ss";
    public static final String SHORT_HYPHEN_FORMAT_WITH_TIMESTAMP_BRITISH = "dd-MM-yyyy hh:mm:ss";
    public static final String LONG_HYPHEN_FORMAT_BRITISH = "dd-MMM-yyyy";
    public static final String LONG_FORMAT_BRITISH = "dd MMM yyyy";
    public static final String LONG_HYPHEN_FORMAT_AMERICAN = "MMM-dd-yyyy";
    public static final String SHORT_SLASH_FORMAT_BRITISH = "dd/MM/yyyy";
    public static final String SPECIAL_FORMAT_1 = "ddMMM yyyy";
    public static final String SHORT_SLASH_FORMAT_AMERICAN = "MM/dd/yyyy";
    public static final String FULL_SPACE_FORMAT = "E, dd MMM yyyy HH:mm:ss Z";
    public static final String TIME_FORMAT_AMPM = "hh:mm aa";
    public static final String TIME_FORMAT_24 = "HH:mm:ss";

    /**
     * Intent Extra tags.................................................................................................
     */
    public static final String KEY_PROVIDER = "Provider";
    public static final String KEY_PROVIDER_NAME = "ProviderName";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_BOYCOUNT = "bouCount";
    public static final String KEY_GIRLCOUNT = "girlCount";
    public static final String KEY_PMS_COUNT = "pmsCount";
    public static final String KEY_HEALTH_ID = "HealthId";
    public static final String KEY_MOBILE = "Mobile";
    public static final String KEY_CLIENT_NAME = "ClientName";
    public static final String KEY_OPTION = "options";
    public static final String KEY_IUD_COUNT = "IUDCount";
    public static final String KEY_Implant_COUNT = "ImplantCount";
    public static final String KEY_FP_EXAMINATION = "FPExamValues";
    public static final String KEY_IUD_IMPLANT_DATE = "IUDImplantDate";
    public static final String KEY_Implant_IMPLANT_DATE = "ImplantImplantDate";
    public static final String KEY_FP_LMP_DATE = "fpLMPDate";
    public static final String KEY_FP_DELIVERY_DATE = "fpDeliveryDate";

    public static final String KEY_IS_APON_ELIGIBLE = "isApon";
    public static final String KEY_IS_SUKHI_ELIGIBLE = "isSukhi";

    public static final String KEY_METHOD_CODE   = "methodCode";
    public static final String KEY_IS_NEW      = "isNew";


    /**
     * Table Names tags.................................................................................................
     * Shared Pref Tags.................................................................................................
     */
    public static final String TABLE_TREATMENT_LIST = "treatmentlist";
    public static final String TABLE_DISEASE_LIST   = "diseaselist";
    public static final String TABLE_SYMPTOM_LIST   = "symptomlist";
    public static final String TABLE_SATELLITE_SESSION_PLANNING = "satelite_session_planning";
    public static final String TABLE_SATELLITE_SESSION_PLANNING_DETAIL = "satelite_planning_detail";



    public static final String KEY_DISTRICT_MAPPING_PREF = "district_mapping";
    public static final String KEY_DISTRICT_CODE_PREF = "district_code";
    public static final String KEY_DISTRICT_SELECT_PREF = "select_district";
    public static final String KEY_URL_PREF = "urlPref";
    public static final String KEY_BASE_URL = "base_url_new";
    public static final String KEY_SYNC_URL = "sync_url";
    public static final String KEY_CRASH_PREF = "crash_pref";
    public static final String KEY_CRASH_TEXT = "crash_text";
    public static final String KEY_ONTEST_TEXT = "onTestMode";
    public static final String KEY_LOGIN_DATE = "login_date";
    public static final String KEY_LAST_ONLINE_LOGIN_DATE = "last_online_login_date";
    public static final String KEY_CRASH_FLAG = "crash_flag";
    public static final String KEY_LANG_PREF = "lang_pref";
    public static final String KEY_MISC_PREF = "miscellaneous_pref";
    public static final String KEY_PROVIDER_PREF = "provider_pref";
    public static final String KEY_SATELLITE_PREF = "sat_pref";
    public static final String KEY_LANG_SELECTION = "lang_selection";
    public static final String KEY_QUERY_DONE_STATUS = "lang_selection";
    public static final String KEY_FWA_PREF = "fwa_pref";
    public static final String KEY_LAST_SEARCH_RESULT = "last_search_result";
    public static final String KEY_LAST_SEARCH_PARAMETER = "last_search_parameter";
    public static final String KEY_HELPLINE = "helpline_number";
    public static final String KEY_LAST_NOTICE = "last_notice";
    public static final String KEY_INTERNAL_DB_SYNC = "db_to_db_synchronization";

    public static final String KEY_SATELLITE_LIST = "sat_list";
    public static final String KEY_FWA_LIST = "fwa_list";
    public static final String KEY_FWA_ID_LIST = "fwa_id_list";

    public static final int SHARED_LIST_IDENTIFIER          = 0xDEADBEEF; //Key

    public static final int TREATMENT_LIST_IDENTIFIER       = 0xDEAD0001; //0001
    public static final int DISEASE_LIST_IDENTIFIER         = 0xDEAD0002; //0010
    public static final int SYMPTOM_LIST_IDENTIFIER         = 0xDEAD0004; //0100
    public static final int ADVICE_LIST_IDENTIFIER          = 0xDEAD0008; //1000

    public static final int ANC_SERVICE_IDENTIFIER          = 0xDEAD0100; //00010000
    public static final int DELIVERY_SERVICE_IDENTIFIER     = 0xDEAD0200; //00100000
    public static final int PNC_SERVICE_IDENTIFIER          = 0xDEAD0400; //01000000
    public static final int PNCN_SERVICE_IDENTIFIER         = 0xDEAD0800; //00010000

    public static final int PILLCONDOM_SERVICE_IDENTIFIER   = 0xDEAD1000; //00010000
    public static final int IUD_SERVICE_IDENTIFIER          = 0xDEAD2000; //00100000
    public static final int PM_SERVICE_IDENTIFIER           = 0xDEAD3000; //00110000
    public static final int INJECTABLE_SERVICE_IDENTIFIER   = 0xDEAD4000; //01000000
    public static final int GP_SERVICE_IDENTIFIER           = 0xDEAD8000; //10000000


    public static final int LIST_IDENTIFIER_MASK            = 0xDEAD0000; //mask

    public static final String UPLOADDB = "DB", UPLOADFILE = "FILE";

    public static String getBaseUrl(Context eContext){
        if(SharedPref.isOnTestMode(eContext)){
            return TEST_BASE_URL;
        }else{
            return SharedPref.getBaseUrl(eContext);
        }
    }

    public static String getSymmetricdsUrl(Context eContext) {
        return SharedPref.getSyncUrl(eContext);
    }

    public enum dbTables {
        ancservice,clientmap,clientmap_extension,death,delivery,diseaselist,elco,followup_notification,
        fpexamination,fpinfo,fpmapping,fpmethods,gpservice,immunizationhistory,implantfollowupservice,
        implantservice,item_source,iudfollowupservice,iudservice,login_location_tracking,member,
        newborn,pacservice,permanent_method_service,permanent_method_followup_service,pillcondomservice,
        pncservicechild,pncservicemother,pregwomen,providerdb,regserial,satelite_planning_details,
        satelite_planning_master,symptomlist,treatmentlist,womaninjectable,child_care_service
    }

    public static final String IS_SPECIAL = "special";
    public static final String GEO_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/.eMIS_GEO/";
    public static final String ZILLA_FILENAME = "zilla.json";
    public static final String VILLAGE_FILENAME = "vill.json";
    //district mapping task - salt, download url, file name, local path
    public static final String BASE64_SALT = "Jr0Hx5s";
    public static final String DISTRICT_MAP_DOWNLOAD_PATH = "http://emis.dgfp.gov.bd/district-map.txt";
    public static final String DISTRICT_MAP_FILE_NAME = "district-map.txt";
    public static final String DISTRICT_MAP_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/rhis_fwc/mapping_data/";
    public static Map<String, JSONArray> listData;

    //Get GP symptom, disease, treatment JSONArray
    public static JSONArray getGPJSONData(String type){
        return listData.get(type);
    }

    //Set GP symptom, disease, treatment
    public static void setGPJSONData(){
        listData = new HashMap<>();

        listData.put("symptom", CommonQueryExecution.getDropDownListAsJSONArray(Constants.TABLE_SYMPTOM_LIST,"serviceid=10"));
        listData.put("treatment", CommonQueryExecution.getDropDownListAsJSONArray(Constants.TABLE_TREATMENT_LIST,"serviceid=10"));
        listData.put("disease", CommonQueryExecution.getDropDownListAsJSONArray(Constants.TABLE_DISEASE_LIST,"serviceid=10"));
    }
}

