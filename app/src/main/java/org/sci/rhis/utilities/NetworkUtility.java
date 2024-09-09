package org.sci.rhis.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by jamil.zaman on 27/12/15.
 */
public class NetworkUtility {

    private Context context;

    public NetworkUtility(Context context) {
        this.context = context;
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
