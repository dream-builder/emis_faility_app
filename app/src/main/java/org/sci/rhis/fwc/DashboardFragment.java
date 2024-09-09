package org.sci.rhis.fwc;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DashboardDBHelper;
import org.sci.rhis.model.DashboardDataModel;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.ConstantQueries;
import org.sci.rhis.utilities.Constants;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.SharedPref;
import org.sci.rhis.utilities.Utilities;

import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by arafat.hasan on 1/8/2018.
 */

public class DashboardFragment extends Fragment implements View.OnTouchListener{
    View fragmentView;
    ArrayList<DashboardDataModel> generalPerson_iud_missed = null, generalPerson_iud_due = null;
    ArrayList<DashboardDataModel> generalPerson_implant_missed = null, generalPerson_implant_due = null;
    ArrayList<DashboardDataModel> generalPerson_anc_missed = null, generalPerson_anc_due = null;

    private String[] ancCount, pncCount;
    private String deliveryCount,gpCount, childServiceCount,pillCondomCount,injectableCount,iudCount,iudFollowupCount,
        implantCount,implantFollowupCount,permanentMethodCount,permanentMethodFollowupCount;

    private int ancFollowupDue = 0, iudFollowupDue = 0, implantFollowupDue = 0, ancFollowupMissed = 0, iudFollowupMissed = 0, implantFollowupMissed = 0;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_dashboard,
                container, false);
        preInit(fragmentView);
        return fragmentView;

    }

    private void preInit(View view){
        view.findViewById(R.id.badge_notification_1_1).setOnTouchListener(this);
        view.findViewById(R.id.badge_notification_1_2).setOnTouchListener(this);
        //2 is missing for the nonce as PNC badge
        view.findViewById(R.id.badge_notification_3_1).setOnTouchListener(this);
        view.findViewById(R.id.badge_notification_3_2).setOnTouchListener(this);
        view.findViewById(R.id.badge_notification_4_1).setOnTouchListener(this);
        view.findViewById(R.id.badge_notification_4_2).setOnTouchListener(this);
    }

    public void preLoading(View view){
        ((TextView) view.findViewById(R.id.tvServiceStatisticsLabel)).setText(R.string.string_data_loading);
        Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutFollowupServiceNotifications),View.GONE);

    }
    public void postSpecialLoading(View view){
        ((TextView) view.findViewById(R.id.tvServiceStatisticsLabel)).setText(R.string.string_data_sync);
        Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutFollowupServiceNotifications),View.GONE);

    }

    public void initDashboardFragment(View view) {
        String lastLoginDate = SharedPref.lastOnlineLogin(getActivity());
        ((TextView) view.findViewById(R.id.tvLoginLabel))
                .setText(lastLoginDate!=null?("সর্বশেষ অনলাইন লগিনঃ "+Utilities.convertEnglishDateToBengali(lastLoginDate)):"সর্বশেষ অনলাইন লগিনের তথ্য অনুপস্থিত!");
        //Setting latest notice in dashboard if any
        if(SharedPref.getLastNotice(getActivity())!=null && !SharedPref.getLastNotice(getActivity()).equals("")){
            ((TextView) view.findViewById(R.id.noticeTextView))
                    .setText(SharedPref.getLastNotice(getActivity()));
        }

        View[] collapsedViews = {getActivity().findViewById(R.id.layoutFollowupServiceNotifications),
                getActivity().findViewById(R.id.layoutDashboardLabels)};
        MethodUtils.makeLayoutCollapsible(getActivity().findViewById(R.id.layoutDashboardFollowupHeader),collapsedViews);

        //Load Dashboard statics data

        Calendar c = Calendar.getInstance();
        String year = String.format(Locale.ENGLISH,"%02d", c.get(Calendar.YEAR));
        String month = String.format(Locale.ENGLISH,"%02d", (c.get(Calendar.MONTH) + 1));
        String startDate = year + "-" + month + "-01";
        String endDate = year + "-" + month + "-31";
        ancCount = DashboardDBHelper.ANCVisitCount(((SecondActivity) getActivity()).provider.getProviderCode(), startDate, endDate);
        deliveryCount = DashboardDBHelper.deliveryCount(((SecondActivity) getActivity()).provider.getProviderCode(), startDate, endDate);
        gpCount = DashboardDBHelper.commonServiceCount(Constants.dbTables.gpservice,"visitdate");
        childServiceCount = DashboardDBHelper.commonServiceCount(Constants.dbTables.child_care_service,"systementrydate");
        pncCount = DashboardDBHelper.PNCVisitCount(((SecondActivity) getActivity()).provider.getProviderCode(), startDate, endDate);
        //FP services....
        pillCondomCount = DashboardDBHelper.commonServiceCount(Constants.dbTables.pillcondomservice,"visitdate");
        injectableCount = DashboardDBHelper.commonServiceCount(Constants.dbTables.womaninjectable,"dosedate");
        iudCount = DashboardDBHelper.commonServiceCount(Constants.dbTables.iudservice,"iudimplantdate");
        iudFollowupCount = DashboardDBHelper.commonServiceCount(Constants.dbTables.iudfollowupservice,"followupdate");
        implantCount = DashboardDBHelper.commonServiceCount(Constants.dbTables.implantservice,"implantimplantdate");
        implantFollowupCount = DashboardDBHelper.commonServiceCount(Constants.dbTables.implantfollowupservice,"followupdate");
        permanentMethodCount = DashboardDBHelper.commonServiceCount(Constants.dbTables.permanent_method_service,"permanent_method_operation_date");
        permanentMethodFollowupCount = DashboardDBHelper.commonServiceCount(Constants.dbTables.permanent_method_followup_service,"followup_date");
        //counting ends.................................................
        IUDFollowupData();
        implantFollowupData();
        ANCFollowupData();
    }

    private void ANCFollowupData() {

        JSONArray jsonArrayANC = CommonQueryExecution.getQueryDataAsJSONArray(ConstantQueries
                .GET_ANC_NOTIFICATION_LIST(((SecondActivity) getActivity()).provider.getZillaID(),
                        ((SecondActivity) getActivity()).provider.getUpazillaID(),
                        ((SecondActivity) getActivity()).provider.getUnionID()));

        generalPerson_anc_due = new ArrayList<>();
        generalPerson_anc_missed = new ArrayList<>();
        for (int i = 0; i < jsonArrayANC.length(); i++) {
            DashboardDataModel gp = null, gp_missed=null;
            try {

                JSONObject jObject = (JSONObject) jsonArrayANC.get(i);
                gp = new DashboardDataModel(Long.valueOf(jObject.getString("healthid")),
                        jObject.getString("name"), jObject.getString("husband name"),
                        Integer.parseInt(jObject.getString("age")), "Female", jObject.getString("mobile"),
                        (jObject.getString("lmp")+","+jObject.getString("edd")));
                if(jObject.getString("is_missed").equals("1")) {
                    gp_missed = gp;
                }

            } catch (JSONException e) {
                Log.e("JSON", "There was an error parsing the JSON", e);
            }

            generalPerson_anc_due.add(gp);
            if(gp_missed!=null) generalPerson_anc_missed.add(gp_missed);

        }
        ancFollowupDue = generalPerson_anc_due.size();
        ancFollowupMissed = generalPerson_anc_missed.size();
    }

    private void IUDFollowupData() {
        JSONArray jsonArrayIUD = CommonQueryExecution.getQueryDataAsJSONArray(ConstantQueries
                .GET_IUD_NOTIFICATION_LIST(((SecondActivity) getActivity()).provider.getZillaID(),
                        ((SecondActivity) getActivity()).provider.getUpazillaID(),
                        ((SecondActivity) getActivity()).provider.getUnionID()));

        generalPerson_iud_due = new ArrayList<>();
        generalPerson_iud_missed = new ArrayList<>();
        for (int i = 0; i < jsonArrayIUD.length(); i++) {
            DashboardDataModel gp = null,gp_missed=null;
            try {
                JSONObject jObject = (JSONObject) jsonArrayIUD.get(i);
                gp = new DashboardDataModel(Long.valueOf(jObject.getString("healthid")), jObject.getString("name"),
                        jObject.getString("husband name"), Integer.parseInt(jObject.getString("age")),
                        "Female", jObject.getString("mobile"), jObject.getString("iudimplantdate"));
                if(jObject.getString("is_missed").equals("1")) {
                    gp_missed = gp;
                }
            } catch (JSONException e) {
                Log.e("JSON", "There was an error parsing the JSON", e);
            }
            generalPerson_iud_due.add(gp);
            if(gp_missed!=null) generalPerson_iud_missed.add(gp_missed);

        }
        iudFollowupDue = generalPerson_iud_due.size();
        iudFollowupMissed = generalPerson_iud_missed.size();
    }

    private void implantFollowupData() {
        JSONArray jsonArrayIUD = CommonQueryExecution.getQueryDataAsJSONArray(ConstantQueries
                .GET_IMPLANT_NOTIFICATION_LIST(((SecondActivity) getActivity()).provider.getZillaID(),
                        ((SecondActivity) getActivity()).provider.getUpazillaID(),
                        ((SecondActivity) getActivity()).provider.getUnionID()));

        generalPerson_implant_due = new ArrayList<>();
        generalPerson_implant_missed = new ArrayList<>();
        for (int i = 0; i < jsonArrayIUD.length(); i++) {
            DashboardDataModel gp = null,gp_missed=null;
            try {
                JSONObject jObject = (JSONObject) jsonArrayIUD.get(i);
                gp = new DashboardDataModel(Long.valueOf(jObject.getString("healthid")), jObject.getString("name"),
                        jObject.getString("husband name"), Integer.parseInt(jObject.getString("age")), "Female",
                        jObject.getString("mobile"), jObject.getString("implantimplantdate"));
                if(jObject.getString("is_missed").equals("1")) {
                    gp_missed = gp;
                }
            } catch (JSONException e) {
                Log.e("JSON", "There was an error parsing the JSON", e);
            }
            generalPerson_implant_due.add(gp);
            if(gp_missed!=null) generalPerson_implant_missed.add(gp_missed);

        }
        implantFollowupDue = generalPerson_implant_due.size();
        implantFollowupMissed = generalPerson_implant_missed.size();

    }

    public void showDashboardFollowupData(View view){
        //hiding followup notification for SACMO_HS (ProvType = 6) and CSBA
        if(!ProviderInfo.getProvider().getmProviderType().equals(Flag.SACMO_HS) && !ProviderInfo.getProvider().getmCsba().equals("1")){
            ((TextView) view.findViewById(R.id.textViewFollowupServicesLabel)).setText(R.string.string_followup_service);
            Utilities.MakeVisible(getActivity(),view.findViewById(R.id.layoutFollowupServiceNotifications));
            ((TextView) view.findViewById(R.id.badge_notification_3_2)).setText("" + String.format(Locale.ENGLISH,"%02d", iudFollowupDue));
            ((TextView) view.findViewById(R.id.badge_notification_3_1)).setText("" + String.format(Locale.ENGLISH,"%02d", iudFollowupMissed));
            ((TextView) view.findViewById(R.id.badge_notification_4_2)).setText("" + String.format(Locale.ENGLISH,"%02d", implantFollowupDue));
            ((TextView) view.findViewById(R.id.badge_notification_4_1)).setText("" + String.format(Locale.ENGLISH,"%02d", implantFollowupMissed));
            ((TextView) view.findViewById(R.id.badge_notification_1_2)).setText("" + String.format(Locale.ENGLISH,"%02d", ancFollowupDue));
            ((TextView) view.findViewById(R.id.badge_notification_1_1)).setText("" + String.format(Locale.ENGLISH,"%02d", ancFollowupMissed));
        }else{
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutDashboardFollowups),View.GONE);
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutDashboardLabels),View.GONE);
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutDashboardFollowupHeader),View.GONE);
        }


        showDashboardStasticsData(view);
    }

    private void showNotificationList(int elementCount, String serviceType,
                                      boolean isMissedList, ArrayList<DashboardDataModel> list){
        if (elementCount != 0) {
            Intent intent = new Intent(getActivity(), FollowupNotificationListActivity.class);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            intent.putExtra("ListObject", list);
            intent.putExtra("Color", isMissedList?1:2);
            intent.putExtra("service_name", serviceType);
            getActivity().startActivityForResult(intent, ActivityResultCodes.DASHBOARDDATA_ACTIVITY);
        } else {
            MethodUtils.showSnackBar(getActivity().findViewById(R.id.layoutHome),"আজ কোন ফলোআপ ডিউ নেই",true);
        }
    }

    private void showDashboardStasticsData(View view){
        Calendar c = Calendar.getInstance();
        String year = String.format(Locale.ENGLISH,"%02d", c.get(Calendar.YEAR));
        CustomSimpleDateFormat df = new CustomSimpleDateFormat("MMMM");
        String month_name = ConstantMaps.MonthNameEnglishToBengali.get(df.format(c.getTime()));
        ((TextView) view.findViewById(R.id.tvServiceStatisticsLabel)).setText("মাসিক সেবার পরিসংখ্যান (" +
                month_name + ", " + Utilities.ConvertNumberToBangla(year)+")");

        ((TextView) view.findViewById(R.id.tvGPData)).setText(Utilities.ConvertNumberToBangla(gpCount));
        ((TextView) view.findViewById(R.id.tvChildData)).setText(Utilities.ConvertNumberToBangla(childServiceCount));
        //FP Parts


        if(!ProviderInfo.getProvider().getmProviderType().equals(Flag.SACMO_HS)){
            ((TextView) view.findViewById(R.id.tvDeliveryData)).setText(Utilities.ConvertNumberToBangla(deliveryCount));

            for (int i = 0; i < 4; i++) {
                if (ancCount[i] == null) {
                    ancCount[i] = "0";
                }
            }
            ((TextView) view.findViewById(R.id.tvANCVisit1)).setText(Utilities.ConvertNumberToBangla(ancCount[0]));
            ((TextView) view.findViewById(R.id.tvANCVisit2)).setText(Utilities.ConvertNumberToBangla(ancCount[1]));
            ((TextView) view.findViewById(R.id.tvANCVisit3)).setText(Utilities.ConvertNumberToBangla(ancCount[2]));
            ((TextView) view.findViewById(R.id.tvANCVisit4)).setText(Utilities.ConvertNumberToBangla(ancCount[3]));

            for (int i = 0; i < 8; i++) {
                if (pncCount[i] == null) {
                    pncCount[i] = "0";
                }
            }
            ((TextView) view.findViewById(R.id.tvPNCMVisit1)).setText(Utilities.ConvertNumberToBangla(pncCount[0]));
            ((TextView) view.findViewById(R.id.tvPNCMVisit2)).setText(Utilities.ConvertNumberToBangla(pncCount[1]));
            ((TextView) view.findViewById(R.id.tvPNCMVisit3)).setText(Utilities.ConvertNumberToBangla(pncCount[2]));
            ((TextView) view.findViewById(R.id.tvPNCMVisit4)).setText(Utilities.ConvertNumberToBangla(pncCount[3]));
            ((TextView) view.findViewById(R.id.tvPNCNVisit1)).setText(Utilities.ConvertNumberToBangla(pncCount[4]));
            ((TextView) view.findViewById(R.id.tvPNCNVisit2)).setText(Utilities.ConvertNumberToBangla(pncCount[5]));
            ((TextView) view.findViewById(R.id.tvPNCNVisit3)).setText(Utilities.ConvertNumberToBangla(pncCount[6]));
            ((TextView) view.findViewById(R.id.tvPNCNVisit4)).setText(Utilities.ConvertNumberToBangla(pncCount[7]));

            //FP parts....
            ((TextView) view.findViewById(R.id.tvPCData)).setText(Utilities.ConvertNumberToBangla(pillCondomCount));
            ((TextView) view.findViewById(R.id.tvInjectableData)).setText(Utilities.ConvertNumberToBangla(injectableCount));
            ((TextView) view.findViewById(R.id.tvIUDData)).setText(Utilities.ConvertNumberToBangla(iudCount));
            ((TextView) view.findViewById(R.id.tvIUDFollowupData)).setText(Utilities.ConvertNumberToBangla(iudFollowupCount));
            ((TextView) view.findViewById(R.id.tvImplantData)).setText(Utilities.ConvertNumberToBangla(implantCount));
            ((TextView) view.findViewById(R.id.tvImplantFollowupData)).setText(Utilities.ConvertNumberToBangla(implantFollowupCount));
            ((TextView) view.findViewById(R.id.tvPMData)).setText(Utilities.ConvertNumberToBangla(permanentMethodCount));
            ((TextView) view.findViewById(R.id.tvPMFollowupData)).setText(Utilities.ConvertNumberToBangla(permanentMethodFollowupCount));

        }else {
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.linearLayoutTableViewMNC),View.GONE);
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutDeliveryStatistics),View.GONE);
        }

        if(ProviderInfo.getProvider().getmCsba().equals("1")) {
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutGPStatistics),View.GONE);
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutChildStatistics),View.GONE);
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutPcInjectionStatistics),View.GONE);
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutIudAllStatistics),View.GONE);
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutImplantAllStatistics),View.GONE);
            Utilities.MakeInvisible(getActivity(),view.findViewById(R.id.layoutPMAllStatistics),View.GONE);

        }

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.badge_notification_1_1:
                showNotificationList(ancFollowupMissed,"ANC",true,generalPerson_anc_missed);
                break;
            case R.id.badge_notification_1_2:
                showNotificationList(ancFollowupDue,"ANC",false,generalPerson_anc_due);
                break;
            case R.id.badge_notification_3_1:
                showNotificationList(iudFollowupMissed,"IUD",true,generalPerson_iud_missed);
                break;
            case R.id.badge_notification_3_2:
                showNotificationList(iudFollowupDue,"IUD",false,generalPerson_iud_due);
                break;
            case R.id.badge_notification_4_1:
                showNotificationList(implantFollowupMissed,"Implant",true,generalPerson_implant_missed);
                break;
            case R.id.badge_notification_4_2:
                showNotificationList(implantFollowupDue,"Implant",false,generalPerson_implant_due);
                break;
        }
        return false;
    }
}
