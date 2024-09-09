package org.sci.rhis.fwc;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.MethodUtils;

import java.util.Arrays;

public class WebViewActivity extends FWCServiceActivity {

    private ProviderInfo provider;
    private Context mContext = this;
    String providerId, password,url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        init();
    }

    private void init(){
        prepareUrl();
        WebView webView = (WebView) findViewById(R.id.webViewETicketing);
        MethodUtils.startWebView(url, webView, mContext);
    }

    private void prepareUrl(){
        provider = getIntent().getParcelableExtra("Provider");

        providerId = provider.getProviderCode();


        int webViewContent = getIntent().getIntExtra("Content",0);
        switch (webViewContent){
            case Flag.E_TICKETING:
                password="123";//TODO:has to take dynamically later
                url = "http://mchdrhis.icddrb.org:8085/eticketing/public/api/check-login?provider_id=";
                url+=providerId+"&password="+password+"&tab=from_tab";
                break;

            case Flag.MEETING_MINUTES:
                String designation = Arrays.asList(new String[]{Flag.FWV,Flag.MIDWIFE,Flag.PARAMEDIC}).
                        contains(provider.getmProviderType().toString())?"FWV":"SACMO";

                String division = provider.getDivID();
                String district = provider.getZillaID();
                String upazila = provider.getUpazillaID();
                String union = provider.getUnionID();
                url="http://mamoni.net:8080/rhis_fwc_monitoring/meetingminutes?providerCode=";
                url+=providerId+"&designation="+designation+"&category=1&division="+division+
                        "&district="+district+"upazila="+upazila+"&union="+union;
                break;

            default:
                break;
        }


    }

    @Override
    public void callbackAsyncTask(String result) {

    }
}
