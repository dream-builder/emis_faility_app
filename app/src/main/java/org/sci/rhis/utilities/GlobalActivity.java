package org.sci.rhis.utilities;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.fwc.LoginActivity;

import java.io.IOException;

/**
 * Created by arafat.hasan on 11/23/2016.
 */

public class GlobalActivity extends MultiDexApplication {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        try {
            new DatabaseWrapper(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, ex) -> {
                    Log.e("GlobalActivity", ex.getMessage());
                    ex.printStackTrace();
                    SharedPref.setCrashFlag(context,true);
                    SharedPref.setCrashMessage(context,"("+Utilities.getDateTimeStringDBFormat()+
                            ") = "+ ExceptionUtils.getFullStackTrace(ex));
                    Intent i = new Intent(context, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    System.exit(0);

                });

        /*Set Symptom, treatment, disease list for GP*/
        Constants.setGPJSONData();
    }
}
