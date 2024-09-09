package org.sci.rhis.fwc;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.MethodUtils;

/**
 * Created by arafat.hasan on 2/25/2016.
 */
public class AssesmentActivity extends FWCServiceActivity {
    private Context mContext;

    StringBuilder reportURL;

    String providerId, zillaId, upazilaId, unionId, password;

    private ProviderInfo provider;
    String strfacility_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fwc_assesment);

        init();

    }

    private void init() {
        mContext = this;
        provider = getIntent().getParcelableExtra("Provider");
        providerId = provider.getProviderCode();
        zillaId = provider.getZillaID();
        upazilaId = provider.getUpazillaID();
        unionId = provider.getUnionID();
        strfacility_id = provider.getmFacilityId();


        sendAssesmentRequest();

    }

    @Override
    public void callbackAsyncTask(String result) {

    }

    public void sendAssesmentRequest() {

        String baseURL = "http://119.148.43.35/survey?url64=";

        try {
            reportURL = new StringBuilder();
            reportURL.append("{\"device\":\"ANDROID\",\"facility_id\":" + strfacility_id + ",\"user_id\":" + providerId + ",\"token\":123456}");

            String url = baseURL + Base64.encodeToString(reportURL.toString().getBytes(), Base64.NO_WRAP);
            WebView webView = (WebView) findViewById(R.id.webViewMIS);
            MethodUtils.startWebView(url, webView, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
