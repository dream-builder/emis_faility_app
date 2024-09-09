package org.sci.rhis.utilities;

/**
 * Created by arafat.hasan on 1/8/2018.
 */

public class ConstantQueries {

    public static final String INDEX_QUERY_CM = "create index if not exists idx_health_cm on clientMap(healthId);";
    public static final String INDEX_QUERY_MEMBER = "create index if not exists idx_health_member on Member(HealthID);";

    public static final String CREATE_LOGIN_LOCATION_TRACKING_SCRIPT = "CREATE TABLE IF NOT EXISTS [login_location_tracking] (\n" +
            "\t[providerid] integer NOT NULL, \n" +
            "\t[login_time] timestamp without time zone NOT NULL, \n" +
            "\t[latitude] real, \n" +
            "\t[longitude] real, \n" +
            "\t[satellite_name] character varying, \n" +
            "\t[gps_address] character varying, \n" +
            "\t[systementrydate] timestamp without time zone DEFAULT CURRENT_TIMESTAMP, \n" +
            "\t[modifydate] timestamp without time zone DEFAULT CURRENT_TIMESTAMP, \n" +
            "\t[uploaddate] timestamp without time zone DEFAULT CURRENT_TIMESTAMP, \n" +
            "\t[upload] integer DEFAULT 1,\n" +
            "\tCONSTRAINT [sqlite_autoindex_login_location_tracking_1] PRIMARY KEY ([providerid], [login_time])\n" +
            ")";
    public static final String CREATE_CLIENTMAP_EXTENSION = "CREATE TABLE IF NOT EXISTS [clientmap_extension] (\n" +
            "\t[generated_id] bigint NOT NULL PRIMARY KEY, \n" +
            "\t[member_count] int, \n" +
            "\t[nid] varchar(20), \n" +
            "\t[nid_status] varchar(2), \n" +
            "\t[br_id] varchar(20), \n" +
            "\t[br_id_status] varchar(2), \n" +
            "\t[mobile_no_2] bigint, \n" +
            "\t[marital_status] int, \n" +
            "\t[education] varchar(2), \n" +
            "\t[religion] varchar(2), \n" +
            "\t[nationality] int, \n" +
            "\t[occupation] varchar(2), \n" +
            "\t[member_info] varchar(254), \n" +
            "\t[extra_col_1] varchar(254), \n" +
            "\t[extra_col_2] varchar(254), \n" +
            "\tFOREIGN KEY ([generated_id])\n" +
            "\t\tREFERENCES [clientMap] ([generatedId])\n" +
            "\t\tON UPDATE CASCADE ON DELETE CASCADE\n" +
            ");";

    public static final String CREATE_IMPLANT_FOLLOWUP_SERVICE_SCRIPT="CREATE TABLE IF NOT EXISTS [implantFollowupService] (\n" +
            "[healthId] bigint NOT NULL, " +
            "[implantCount] integer NOT NULL, " +
            "[serviceId] integer NOT NULL, " +
            "[providerId] integer, " +
            "[followupDate] date, " +
            "[allowance] real, " +
            "[complication] text, " +
            "[treatment] text, " +
            "[management] text, " +
            "[attendantName] text, " +
            "[providerAsAttendant] integer, " +
            "[implantRemoverName] text, " +
            "[implantRemoverDesignation] integer, " +
            "[implantRemoveDate] date, " +
            "[implantRemoveReason] text, " +
            "[fpMethod] integer, " +
            "[fpAmount] integer, " +
            "[refer] integer, " +
            "[referCenterName] integer, " +
            "[referReason] text, " +
            "[monitoringOfficerName] text, " +
            "[comment] text, " +
            "[isFixedVisit] integer, " +
            "[systemEntryDate] date, " +
            "[modifyDate] date, " +
            "[sateliteCenterName] text, " +
            "[client] integer," +
            "CONSTRAINT [sqlite_autoindex_implantFollowupService_1] PRIMARY KEY ([healthId], [implantCount], [serviceId])" +
            ");";

    public static final String CREATE_IMPLANT_SERVICE_SCRIPT = "CREATE TABLE IF NOT EXISTS [implantService] (\n" +
            "\t[healthId] bigint NOT NULL, \n" +
            "\t[implantCount] integer NOT NULL, \n" +
            "\t[providerId] integer, \n" +
            "\t[implantType] integer, \n" +
            "\t[implantImplantDate] date, \n" +
            "\t[serviceSource] integer, \n" +
            "\t[clientAllowance] real, \n" +
            "\t[attendantName] text, \n" +
            "\t[attendantDesignation] integer, \n" +
            "\t[attendantAllowance] real, \n" +
            "\t[providerAsAttendant] integer, \n" +
            "\t[treatment] text, \n" +
            "\t[implantAfterDelivery] integer, \n" +
            "\t[systemEntryDate] date, \n" +
            "\t[modifyDate] date, \n" +
            "\t[sateliteCenterName] text, \n" +
            "\t[client] integer,\n" +
            "\tCONSTRAINT [sqlite_autoindex_implantService_1] PRIMARY KEY ([healthId], [implantCount])\n" +
            ");";

    public static final String CREATE_FOLLOWUP_NOTIFICATION_SCRIPT = "CREATE TABLE IF NOT EXISTS [followup_notification] (" +
            "[healthid] bigint NOT NULL, " +
            "[service_name] text, " +
            "[systementrydate] timestamp without time zone NOT NULL, " +
            "CONSTRAINT service_notification_pkey PRIMARY KEY (healthid, service_name, systementrydate)" +
            ")";

    public static final String CREATE_PERMANENT_METHOD_SCRIPT = "CREATE TABLE  IF NOT EXISTS [permanent_method_service] (\n" +
            "\t[healthid] bigint NOT NULL, \n" +
            "\t[pmscount] integer NOT NULL, \n" +
            "\t[providerid] integer NOT NULL, \n" +
            "\t[permanent_method_operation_date] date NOT NULL, \n" +
            "\t[permanent_method_operation_time] integer, \n" +
            "\t[operation_name] integer, \n" +
            "\t[doctor_name] character varying, \n" +
            "\t[doctor_designation] integer, \n" +
            "\t[client_came_alone] integer, \n" +
            "\t[companion_name] character varying, \n" +
            "\t[companion_designation] integer, \n" +
            "\t[companion_address] text, \n" +
            "\t[companion_mobile] bigint, \n" +
            "\t[companion_allowance] integer, \n" +
            "\t[client_allowance] integer, \n" +
            "\t[cloth] integer, \n" +
            "\t[complication] text, \n" +
            "\t[treatment] character varying, \n" +
            "\t[management] text, \n" +
            "\t[advice] text, \n" +
            "\t[client] integer, \n" +
            "\t[refer] integer, \n" +
            "\t[refer_center_name] integer, \n" +
            "\t[refer_reason] text, \n" +
            "\t[systementrydate] timestamp without time zone DEFAULT (CURRENT_TIMESTAMP), \n" +
            "\t[modifydate] timestamp without time zone DEFAULT (CURRENT_TIMESTAMP), \n" +
            "\t[uploaddate] timestamp without time zone DEFAULT (CURRENT_TIMESTAMP), \n" +
            "\t[upload] integer,\n" +
            "\tCONSTRAINT [sqlite_autoindex_permanent_method_service_1] PRIMARY KEY ([healthid], [pmscount])\n" +
            ");";

    public static final String CREATE_PERMANENT_METHOD_FOLLOW_UP_SCRIPT = "CREATE TABLE IF NOT EXISTS [permanent_method_followup_service] (\n" +
            "\t[healthid] bigint NOT NULL, \n" +
            "\t[serviceid] bigint NOT NULL, \n" +
            "\t[pmscount] integer NOT NULL, \n" +
            "\t[followup_date] date, \n" +
            "\t[followup_reason] text, \n" +
            "\t[providerid] integer NOT NULL, \n" +
            "\t[treatment] text, \n" +
            "\t[complication] text, \n" +
            "\t[management] text, \n" +
            "\t[examination] text, \n" +
            "\t[doctor_name] character varying, \n" +
            "\t[doctor_designation] integer, \n" +
            "\t[refer] integer, \n" +
            "\t[refer_center_name] integer, \n" +
            "\t[refer_reason] text, \n" +
            "\t[systementrydate] timestamp without time zone DEFAULT (CURRENT_TIMESTAMP), \n" +
            "\t[modifydate] timestamp without time zone DEFAULT (CURRENT_TIMESTAMP), \n" +
            "\t[uploaddate] timestamp without time zone DEFAULT (CURRENT_TIMESTAMP), \n" +
            "\t[upload] integer,\n" +
            "\tCONSTRAINT [sqlite_autoindex_permanent_method_followup_service_1] PRIMARY KEY ([healthid], [serviceid], [pmscount])\n" +
            ");";

    public static final String CREATE_CHILD_CARE_SERVICE_TABLE_SCRIPT = "CREATE TABLE IF NOT EXISTS [child_care_service] (\n" +
            "[healthid] bigint COLLATE BINARY NOT NULL,\n" +
            "[providerid] bigint COLLATE BINARY,\n" +
            "[visitdate] date COLLATE BINARY,\n" +
            "[indicationstartdate] date COLLATE BINARY,\n" +
            "[isfollowup] integer COLLATE BINARY,\n" +
            "[refer] integer COLLATE BINARY,\n" +
            "[satelitecentername] text COLLATE BINARY,\n" +
            "[client] integer COLLATE BINARY,\n" +
            "[systementrydate] timestamp without time zone COLLATE BINARY NOT NULL,\n" +
            "[modifydate] timestamp without time zone COLLATE BINARY,\n" +
            "[uploaddate] timestamp without time zone COLLATE BINARY,\n" +
            "[comment] text COLLATE BINARY,\n" +
            "[other1] text COLLATE BINARY,\n" +
            "[other2] text COLLATE BINARY,\n" +
            "[advice] text COLLATE BINARY,\n" +
            "CONSTRAINT [sqlite_autoindex_child_care_service_1] PRIMARY KEY ([healthid], [systementrydate])\n" +
            ")";

    public static final String CREATE_CHILD_CARE_SERVICE_DETAIL_TABLE_SCRIPT = "CREATE TABLE IF NOT EXISTS [child_care_service_detail] (\n" +
            "[healthid] bigint COLLATE BINARY,\n" +
            "[entrydate] timestamp without time zone COLLATE BINARY,\n" +
            "[inputid] integer COLLATE BINARY,\n" +
            "[inputvalue] text COLLATE BINARY,\n" +
            "[comment] text COLLATE BINARY,\n" +
            "[systementrydate] timestamp without time zone COLLATE BINARY,\n" +
            "[modifydate] timestamp without time zone COLLATE BINARY,\n" +
            "[uploaddate] timestamp without time zone COLLATE BINARY\n" +
            ")";

    public static String GET_ANC_NOTIFICATION_LIST(String zillaId,String upazilaId,String unionId){
        return "select preg.\"LMP\" as LMP, preg.\"EDD\" as EDD,\n" +
                "\tpreg.\"healthId\" as \"healthId\", cm.name  as \"Name\",\n" +
                "\t(case when cm.\"husbandName\" is not null then cm.\"husbandName\" else '' end) as \"Husband Name\",\n" +
                "\t(case when cm.\"mobileNo\" is not null then cm.\"mobileNo\" else '' end) as \"Mobile\",\n" +
                "\t(case when cm.age is not null then cm.age else 0 end) as \"Age\",\n" +
                "\t(case when notific.\"healthid\" is null then 1 else 2 end) as \"is_missed\"\n" +
                "\t \n" +
                "from (select * from \"pregWomen\" where \"LMP\" is not null and\n" +
                "\t(\"LMP\" between date('now', '-294 day') and date('now')) and\n" +
                " \t((date('now') between date(\"LMP\", '+0 day') and date(\"LMP\", '+112 day')) or\n" +
                " \t(date('now') between date(\"LMP\", '+168 day') and date(\"LMP\", '+196 day')) or\n" +
                " \t(date('now') between date(\"LMP\", '+217 day') and date(\"LMP\", '+224 day')) or\n" +
                " \t(date('now') between date(\"LMP\", '+245 day') and date(\"LMP\", '+252 day'))) and\n" +
                "\t\"healthId\" not in (select fol.\"healthId\"\n" +
                "\t\tfrom \"ancService\" fol \n" +
                "\t\tinner join \"pregWomen\" pregw on fol.\"healthId\" = pregw.\"healthId\" \n" +
                "\t\twhere pregw.\"LMP\" is not null and \n" +
                "\t\t(fol.\"visitDate\" between date(pregw.\"LMP\", '+0 day') and date(pregw.\"LMP\", '+112 day')) or\n" +
                "\t\t(fol.\"visitDate\" between date(pregw.\"LMP\", '+168 day') and date(pregw.\"LMP\", '+196 day')) or\n" +
                "\t\t(fol.\"visitDate\" between date(pregw.\"LMP\", '+217 day') and date(pregw.\"LMP\", '+224 day')) or\n" +
                "\t\t(fol.\"visitDate\" between date(pregw.\"LMP\", '+245 day') and date(pregw.\"LMP\", '+252 day')))) as preg\n" +
                "\n" +
                "inner join \"clientMap\" cm on cm.\"generatedId\"=preg.\"healthId\" and cm.\"zillaId\"= "+zillaId+" and cm.\"upazilaId\"= "+upazilaId+" and cm.\"unionId\"= "+unionId+"\n" +
                "left join (select distinct(notif.\"healthid\") \n" +
                "\tfrom \"followup_notification\" notif \n" +
                "\tinner join \"pregWomen\" pregw on notif.\"healthid\" = pregw.\"healthId\" \n" +
                "\twhere notif.service_name = 'ANC' and\n" +
                "\t\tpregw.\"LMP\" is not null and \n" +
                "(date(notif.\"systementrydate\") between date(pregw.\"LMP\", '+0 day') and date(pregw.\"LMP\", '+112 day')) or\n" +
                " \t(date(notif.\"systementrydate\") between date(pregw.\"LMP\", '+168 day') and date(pregw.\"LMP\", '+196 day')) or\n" +
                " \t(date(notif.\"systementrydate\") between date(pregw.\"LMP\", '+217 day') and date(pregw.\"LMP\", '+224 day')) or\n" +
                " \t(date(notif.\"systementrydate\") between date(pregw.\"LMP\", '+245 day') and date(pregw.\"LMP\", '+252 day'))) as notific on preg.\"healthId\"=notific.\"healthid\"";

    }

    public static String GET_IUD_NOTIFICATION_LIST(String zillaId,String upazilaId,String unionId){
        return "select iud.\"iudImplantDate\" as \"iudImplantDate\", iud.\"healthId\" as \"healthId\", cm.name  as \"Name\",\n" +
                " (case when cm.\"husbandName\" is not null then cm.\"husbandName\" else '' end) as \"Husband Name\",\n" +
                " (case when cm.\"mobileNo\" is not null then cm.\"mobileNo\" else '' end) as \"Mobile\",\n" +
                " (case when cm.age is not null then cm.age else 0 end) as \"Age\",\n" +
                " (case when notific.\"healthid\" is null then 1 else 2 end) as \"is_missed\"\n" +
                "from (select * from \"iudService\" where \"iudImplantDate\" is not null and\n" +
                "\t((date('now') between date(\"iudImplantDate\", '+23 day') and date(\"iudImplantDate\", '+37 day')) or\n" +
                " \t(date('now') between date(\"iudImplantDate\", '+5 month') and date(\"iudImplantDate\", '+7 month')) or \n" +
                " \t(date('now') between date(\"iudImplantDate\", '+11 month') and date(\"iudImplantDate\", '+13 month')) ) and\n" +
                " \t\"healthId\" not in (select fol.\"healthId\"\n" +
                "\t\tfrom \"iudFollowupService\" fol \n" +
                "\t\tinner join \"iudService\" iuds on fol.\"healthId\" = iuds.\"healthId\" \n" +
                "\t\twhere iuds.\"iudImplantDate\" is not null and\n" +
                "\t\t\t(fol.\"followupDate\" between date(iuds.\"iudImplantDate\", '+23 day') and date(iuds.\"iudImplantDate\", '+37 day')) or\n" +
                "\t\t\t(fol.\"followupDate\" between date(iuds.\"iudImplantDate\", '+5 month') and date(iuds.\"iudImplantDate\", '+7 month')) or\n" +
                "\t\t\t(fol.\"followupDate\" between date(iuds.\"iudImplantDate\", '+11 month') and date(iuds.\"iudImplantDate\", '+13 month'))  )) as iud\n" +
                "inner join \"clientMap\" cm on cm.\"generatedId\"=iud.\"healthId\" and cm.\"zillaId\"= "+zillaId+" and cm.\"upazilaId\"= "+upazilaId+" and cm.\"unionId\"= "+unionId+"\n" +
                "left join (select notif.\"healthid\" \n" +
                "           from \"followup_notification\" notif \n" +
                "\t\t   inner join \"iudService\" iuds on notif.\"healthid\" = iuds.\"healthId\" \n" +
                "\t\t   where notif.service_name = 'IUD' and\n" +
                "\t\t\t\tiuds.\"iudImplantDate\" is not null and\n" +
                "\t\t\t\t(date(notif.\"systementrydate\") between  date(iuds.\"iudImplantDate\", '+23 day') and date(iuds.\"iudImplantDate\", '+37 day')) or\n" +
                "\t\t\t\t(date(notif.\"systementrydate\") between date(iuds.\"iudImplantDate\", '+5 month') and date(iuds.\"iudImplantDate\", '+7 month')) or\n" +
                "\t\t\t\t(date(notif.\"systementrydate\") between date(iuds.\"iudImplantDate\", '+11 month') and date(iuds.\"iudImplantDate\", '+13 month')) ) as notific on iud.\"healthId\"=notific.\"healthid\"";
    }

    public static String GET_IMPLANT_NOTIFICATION_LIST(String zillaId,String upazilaId,String unionId){
        return "select imp.\"implantImplantDate\" as \"implantImplantDate\", imp.\"healthId\" as \"healthId\", cm.name  as \"Name\",\n" +
                " (case when cm.\"husbandName\" is not null then cm.\"husbandName\" else '' end) as \"Husband Name\",\n" +
                " (case when cm.\"mobileNo\" is not null then cm.\"mobileNo\" else '' end) as \"Mobile\",\n" +
                " (case when cm.age is not null then cm.age else 0 end) as \"Age\",\n" +
                " (case when notific.\"healthid\" is null then 1 else 2 end) as \"is_missed\"\n" +
                "from (select * from \"implantService\" where \"implantImplantDate\" is not null and\n" +
                "\t((date('now') between date(\"implantImplantDate\", '+23 day') and date(\"implantImplantDate\", '+37 day')) or\n" +
                " \t(date('now') between date(\"implantImplantDate\", '+5 month') and date(\"implantImplantDate\", '+7 month')) or \n" +
                " \t(date('now') between date(\"implantImplantDate\", '+11 month') and date(\"implantImplantDate\", '+13 month')) ) and\n" +
                " \t\"healthId\" not in (select fol.\"healthId\"\n" +
                "\t\tfrom \"implantFollowupService\" fol \n" +
                "\t\tinner join \"implantService\" imps on fol.\"healthId\" = imps.\"healthId\" \n" +
                "\t\twhere imps.\"implantImplantDate\" is not null and\n" +
                "\t\t\t(fol.\"followupDate\" between date(imps.\"implantImplantDate\", '+23 day') and date(imps.\"implantImplantDate\", '+37 day')) or\n" +
                "\t\t\t(fol.\"followupDate\" between date(imps.\"implantImplantDate\", '+5 month') and date(imps.\"implantImplantDate\", '+7 month')) or\n" +
                "\t\t\t(fol.\"followupDate\" between date(imps.\"implantImplantDate\", '+11 month') and date(imps.\"implantImplantDate\", '+13 month'))  )) as imp\n" +
                "inner join \"clientMap\" cm on cm.\"generatedId\"=imp.\"healthId\" and cm.\"zillaId\"= "+zillaId+" and cm.\"upazilaId\"= "+upazilaId+" and cm.\"unionId\"= "+unionId+"\n" +
                "left join (select notif.\"healthid\" \n" +
                "           from \"followup_notification\" notif \n" +
                "\t\t   inner join \"implantService\" imps on notif.\"healthid\" = imps.\"healthId\" \n" +
                "\t\t   where notif.service_name = 'IUD' and\n" +
                "\t\t\t\timps.\"implantImplantDate\" is not null and\n" +
                "\t\t\t\t(date(notif.\"systementrydate\") between date(imps.\"implantImplantDate\", '+23 day') and date(imps.\"implantImplantDate\", '+37 day')) or\n" +
                "\t\t\t\t(date(notif.\"systementrydate\") between date(imps.\"implantImplantDate\", '+5 month') and date(imps.\"implantImplantDate\", '+7 month')) or\n" +
                "\t\t\t\t(date(notif.\"systementrydate\") between date(imps.\"implantImplantDate\", '+11 month') and date(imps.\"implantImplantDate\", '+13 month')) ) as notific on imp.\"healthId\"=notific.\"healthid\"";
    }


    public static final String CREATE_SATELLITE_SESSION_PLANNING =" CREATE TABLE IF NOT EXISTS [satelite_session_planning] (\n" +
            "\t[planning_id] integer COLLATE BINARY NOT NULL, \n" +
            "\t[facility_id] bigint COLLATE BINARY NOT NULL, \n" +
            "\t[providerid] integer COLLATE BINARY NOT NULL, \n" +
            "\t[year] integer COLLATE BINARY NOT NULL, \n" +
            "\t[month] integer COLLATE BINARY NOT NULL, \n" +
            "\t[status] integer COLLATE BINARY NOT NULL, \n" +
            "\t[zillaid] integer COLLATE BINARY NOT NULL, \n" +
            "\t[upazilaid] integer COLLATE BINARY NOT NULL, \n" +
            "\t[unionid] integer COLLATE BINARY NOT NULL, \n" +
            "\t[submit_date] timestamp without time zone COLLATE BINARY NOT NULL, \n" +
            "\t[approve_date] timestamp without time zone COLLATE BINARY, \n" +
            "\t[comments] text COLLATE BINARY, \n" +
            "\t[systementrydate] timestamp without time zone COLLATE BINARY, \n" +
            "\t[modifydate] timestamp without time zone COLLATE BINARY, \n" +
            "\t[uploaddate] timestamp without time zone COLLATE BINARY, \n" +
            "\t[upload] integer COLLATE BINARY, \n" +
            "\t[other1] text COLLATE BINARY, \n" +
            "\t[other2] text COLLATE BINARY,\n" +
            "\tCONSTRAINT [sqlite_autoindex_satelite_session_planning_1] PRIMARY KEY ([planning_id], [facility_id], [year], [month])";


    public static final String CREATE_SATELLITE_PLANNING_DETAIL =" CREATE TABLE IF NOT EXISTS [satelite_planning_detail] (\n" +
            "\t[planning_detail_id] integer COLLATE BINARY NOT NULL, \n" +
            "\t[planning_id] bigint COLLATE BINARY NOT NULL, \n" +
            "\t[schedule_date] date COLLATE BINARY NOT NULL, \n" +
            "\t[satelite_name] character varying COLLATE BINARY NOT NULL, \n" +
            "\t[fwa_name] character varying COLLATE BINARY NOT NULL, \n" +
            "\t[fwa_id] integer COLLATE BINARY, \n" +
            "\t[mouzaid] integer COLLATE BINARY NOT NULL, \n" +
            "\t[villageid] integer COLLATE BINARY NOT NULL, \n" +
            "\t[systementrydate] timestamp without time zone COLLATE BINARY, \n" +
            "\t[modifydate] timestamp without time zone COLLATE BINARY, \n" +
            "\t[uploaddate] timestamp without time zone COLLATE BINARY, \n" +
            "\t[upload] integer COLLATE BINARY, \n" +
            "\t[other1] text COLLATE BINARY, \n" +
            "\t[other2] text COLLATE BINARY,\n" +
            "\tCONSTRAINT [sqlite_autoindex_satelite_planning_detail_1] PRIMARY KEY ([planning_detail_id], [planning_id])\n" +
            ")\n";

    /**
     * Getting Satellite Names
     * date - current date
     * */
    public static String GET_SATELLITE_NAME(String date)
    {
        return "select satelite_name from satelite_planning_detail spd " +
                "inner join satelite_session_planning ssp using(planning_id) " +
                " where spd.schedule_date='"+ date+"' and ssp.status=1";
    }

     /*
     * @param tableName
     * @return the insert query considering delta data from remote db
     * Deleting processes are not considered here
     */
    public static String getServiceSyncQuery(String tableName){
        /*SAMPLE QUERY for performing UPDATE/INSERT operation in anc service
        * with new_or_modified as (
              select  a.*
                from
              rdb.ancservice a
                left join
              ancservice b on b.healthid = a.healthid and b.serviceid=a.serviceid and b.modifydate>=a.modifydate
             where b.healthid is null
            )
            insert or replace into ancservice
            select * from new_or_modified ;
        * */
        String singleSyncQuery = "with new_or_modified as (" +
                " select  a.* " +
                " from rdb."+tableName+" a " +
                " left join "+tableName+" b on ";
        for(String joinColumn : ConstantMaps.tableListWithPKey.get(tableName)){
            //adding primary key columns as joining condition
            singleSyncQuery+="b."+joinColumn+" = a."+joinColumn+" and ";
        }
        singleSyncQuery+="b.modifydate >= a.modifydate";//to consider update also
        //do nothing if original DB has latest update
        singleSyncQuery+=" where b.healthid is null)"+
                " insert or replace into "+tableName+
                " select * from new_or_modified;";
        return singleSyncQuery;
    }

    /**
     * @return the query of clientmap synchronization
     */
    public static String getClientmapSyncQuery(){
        String clientMapQuery = "with new_or_modified as (" +
                " select  a.* " +
                " from rdb.clientmap a \n" +
                " left join clientmap b on b.generatedid = a.generatedid and b.modifydate>=a.modifydate" +
                " where b.healthid is null)"+
                " insert or replace into clientmap"+
                " select * from new_or_modified;";
        String clientMapExtensionQuery = "with new_or_modified as (" +
                " select  a.* " +
                " from rdb.clientmap_extension a" +
                " left join clientmap_extension b on b.generatedid = a.generatedid and b.modifydate>=a.modifydate" +
                " where b.healthid is null)"+
                " insert or replace into clientmap_extension"+
                " select * from new_or_modified;";
        return clientMapQuery+clientMapExtensionQuery;
    }

    /**
     * the query of member synchronization
     */
    public static final String MEMBER_SYNC_QUERY = "with new_or_modified as (" +
            " select  a.* " +
            " from rdb.member a " +
            " left join member b on b.healthid = a.healthid and b.zillaid = a. zillaid " +
            " and b.upazilaid = a.upazilaid and b.unionid = a.unionid" +
            " and b.mouzaid=a.mouzaid and b.villageid=a.villageid and b.hhno=a.hhno and b.extype=a.extype " +
            " where b.healthid is null)"+//modify date is not considered here as server data always has latest update
            " insert or replace into member"+
            " select * from new_or_modified;";

    /**
     * the query of syncing providerdb from old database
     */
    public static final String PROVIDERDB_SYNC_QUERY = "insert into providerdb"+
            " select * from rdb.providerdb;";

}

