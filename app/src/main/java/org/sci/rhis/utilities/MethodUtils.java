package org.sci.rhis.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.satellite_session_planning.RetrieveSatellitePlanningInfo;
import org.sci.rhis.fwc.R;
import org.sci.rhis.model.LocationHolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


/**
 * Created by arafat.hasan on 9/4/2016.
 */
public class MethodUtils {
    /**
     * @param eHistoryLabel-TextView of History Segment
     * @param eHistoryLayout         - Layout of History Segment
     */
    public static void makeHistoryCollapsible(final TextView eHistoryLabel, final LinearLayout eHistoryLayout) {
        ((ViewGroup) eHistoryLabel.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eHistoryLayout.getVisibility() == View.GONE) {
                    eHistoryLayout.setVisibility(View.VISIBLE);
                    eHistoryLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                } else {
                    eHistoryLayout.setVisibility(View.GONE);
                    eHistoryLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
                }
            }
        });
    }

    /**
     * NOTE: This method is not a generic method, it is only for fp screening
     *
     * @param viewGroup
     * @return "result of checking whether there is any unselected radio group"
     */
    public static boolean hasUnselectedRadioGroup(ViewGroup viewGroup) {
        boolean hasUnselected = false;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof LinearLayout) {
                ViewGroup childGroup = null;
                if (view instanceof ViewGroup) {
                    childGroup = (ViewGroup) view;
                }
                for (int j = 0; j < childGroup.getChildCount(); j++) {
                    View childView = childGroup.getChildAt(j);
                    if (childView instanceof RadioGroup) {
                        if (((ViewGroup) childView.getParent()).getVisibility() != View.GONE) {
                            if (!Validation.isAnyChildChecked((RadioGroup) childView)) {
                                hasUnselected = true;
                            }
                        }

                    }
                }
            }
        }
        return hasUnselected;
    }

    /**
     * NOTE: This method is not a generic method, it is only for fp screening
     *
     * @param viewGroup
     * @return "result of checking whether there is any unselected radio group"
     */
    public static void prepareRadioGroupToRemoveErrorSign(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof LinearLayout) {
                ViewGroup childGroup = null;
                if (view instanceof ViewGroup) {
                    childGroup = (ViewGroup) view;
                }
                for (int j = 0; j < childGroup.getChildCount(); j++) {
                    View childView = childGroup.getChildAt(j);
                    if (childView instanceof RadioGroup) {
                        if (((ViewGroup) childView.getParent()).getVisibility() != View.GONE) {
                            ((RadioGroup) childView).setOnCheckedChangeListener((radioGroup, i1) -> MethodUtils.removeErrorSignFromRadioGroup(radioGroup));
                        }

                    }
                }
            }
        }
    }

    /**
     * NOTE: This method is not a generic method, it is only for fp screening
     *
     * @param viewGroup
     * @return "result of checking whether there is any unselected radio group"
     */
    public static boolean hasUnselectedFirstChildRadioGroup(ViewGroup viewGroup) {
        boolean hasUnselected = false;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof LinearLayout) {
                ViewGroup childGroup = null;
                if (view instanceof ViewGroup) {
                    childGroup = (ViewGroup) view;
                }
                for (int j = 0; j < childGroup.getChildCount(); j++) {
                    View childView = childGroup.getChildAt(j);
                    if (childView instanceof RadioGroup) {
                        if (((ViewGroup) childView.getParent()).getVisibility() != View.GONE) {
                            if (!Validation.isFirstChildChecked((RadioGroup) childView))
                                hasUnselected = true;
                        }

                    }
                }
            }
        }
        return hasUnselected;
    }

    public static void setAllFirstChildCheckedListener(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof LinearLayout) {
                ViewGroup childGroup = null;
                if (view instanceof ViewGroup) {
                    childGroup = (ViewGroup) view;
                }
                for (int j = 0; j < childGroup.getChildCount(); j++) {
                    View childView = childGroup.getChildAt(j);
                    if (childView instanceof RadioGroup) {
                        if (((ViewGroup) childView.getParent()).getVisibility() != View.GONE) {
                            setFirstChildCheckedListener((RadioGroup) childView);
                        }

                    }
                }
            }
        }
    }

    public static void showLongTimedToast(Context eContext, String msg) {
        Toast toast = Toast.makeText(eContext, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showLongTimedColoredToast(Context eContext, String msg, int colorId) {
        Toast toast = Toast.makeText(eContext, msg, Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(20);
        toastLayout.setBackgroundColor(colorId);
        toast.show();
    }

    public static void setFirstChildCheckedListener(RadioGroup radioGroup) {
        RadioButton firstChild = (RadioButton) radioGroup.getChildAt(0);
        firstChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    buttonView.setError(null);
                }
            }
        });
    }

    /**
     * @param context
     * @param spinner
     * @param val
     * @param resourceId Set single spinner by a partial value
     */
    public static void setSpinnerCustomValue(Context context, Spinner spinner, String val, int resourceId, String offset) {
        if (spinner != null) {
            List<String> spinnerArray = Arrays.asList(context.getResources().getStringArray(resourceId));

            if (!val.equals(null) && !val.equals("")) {
                int valPosition = 0;
                for (int i = 0; i < spinnerArray.size(); i++) {
                    String s = spinnerArray.get(i);
                    if (!s.equals("")) {
                        if (Integer.valueOf(s.split(offset)[0]) == Integer.valueOf(val)) {
                            valPosition = i;
                        }
                    }
                }
                spinner.setSelection(valPosition);
            } else {
                spinner.setSelection(0);
            }
        }
    }

    public static void hideSoftKeyboard(Context eContext) {
        View view = ((Activity) eContext).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) eContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * Starting a webview with a specific URL
     *
     * @param url
     * @param webView
     * @param eContext
     */
    public static void startWebView(String url, WebView webView, final Context eContext) {
        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(eContext, R.style.MyTheme);
                    progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);


    }
    //***************************WEB VIEW PART END***************************************************

    /**
     * @param dob
     * @return age by day, month & year from dob
     */
    public static String calculatePreciseAge(String dob) {
        String preciseAge = "";
        if (dob != null && !dob.equals("")) {
            try {
                int[] agePartArray = Converter.ageFromDOB(Converter.stringToDate(Constants.SHORT_HYPHEN_FORMAT_DATABASE, dob));
                if (agePartArray[2] != 0)
                    preciseAge += Utilities.ConvertNumberToBangla(String.valueOf(agePartArray[2])) + " বছর ";
                if (agePartArray[1] != 0)
                    preciseAge += Utilities.ConvertNumberToBangla(String.valueOf(agePartArray[1])) + " মাস ";
                if (agePartArray[0] != 0 && agePartArray[2] == 0)
                    preciseAge += Utilities.ConvertNumberToBangla(String.valueOf(agePartArray[0])) + " দিন ";

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return preciseAge;
    }

    /**
     * @param url
     * @return status of server availability
     */
    public static boolean isConnectedToServer(String url, int timeout) {
        boolean okay;
        try {
            URL myUrl = new URL(url + "apistatus");
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();

            //Read the response
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String bufferedStrChunk;
            while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                stringBuilder.append(bufferedStrChunk);
            }
            if (stringBuilder.toString().equals("API is accessible!!!")) {
                okay = true;
            } else {
                okay = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            okay = false;
        }
        return okay;

    }

    /**
     * getting particular key by value from Map
     */
    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * @param root
     * @param message
     */
    public static void showSnackBar(View root, String message, boolean isAlert) {
        Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_LONG);
        snackbar.show();
        View view = snackbar.getView();
        if (isAlert) view.setBackgroundColor(Color.RED);
        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public static void showSnackBarFromTop(View root, String message, boolean isAlert) {
        Snackbar snack = Snackbar.make(root, message, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snack.show();
    }

    public static void removeErrorSignFromRadioGroup(RadioGroup radioGroup) {
        int childCount = radioGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioButton child = (RadioButton) radioGroup.getChildAt(i);
            child.setError(null);
        }
    }

    /**
     *
     * @param date
     * @return extracting month from any date
     */
    public static int getMonthFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH)+1;
    }

    /**
     *
     * @param date
     * @return extracting year from any date
     */
    public static int getYearFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * @return IMEI number of active SIM slot
     */
    public static String getIMEInumber(Activity activity){
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) activity.getSystemService(activity.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imeiNumber = telephonyManager.getDeviceId();
        return imeiNumber;
    }

    /**
     * @param eView-View of Collapsible action layout
     * @param eCollapsibleLayout- Array of layouts of Collapsible Segments
     */
    public static void makeLayoutCollapsible(final View eView, final View[] eCollapsibleLayout) {
        eView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<eCollapsibleLayout.length;i++){
                    if (eCollapsibleLayout[i].getVisibility() == View.GONE) {
                        eCollapsibleLayout[i].setVisibility(View.VISIBLE);
                    } else {
                        eCollapsibleLayout[i].setVisibility(View.GONE);
                    }
                }

            }
        });
    }

    public static ArrayList<String> sortKeysByMapValues(Map<String,Integer> map) {
        Integer[] valueArray = map.values().toArray(new Integer[map.size()]);
        ArrayList<Integer> values =  new ArrayList<>(Arrays.asList(valueArray));
        Collections.sort(values);
        ArrayList<String> keys = new ArrayList<>();
        for(Integer value: values){
            keys.add(MethodUtils.getKey(map,value));
        }

        return keys;
    }

    /**
     * @param locationList e.g. villageLIst or zillalist or likewise
     * @param locationCode
     * @return single LocationHolder object
     */
    public static LocationHolder getLocationObjectByCode(ArrayList<LocationHolder> locationList, String locationCode){
        LocationHolder locationHolderObject = null;
        for(LocationHolder singleLoc:locationList){
            if(singleLoc.getCode().equals(locationCode)){
                locationHolderObject=singleLoc;
            }
        }
        return locationHolderObject;
    }

    public static void clearJSONObject(JSONObject jsonObject){
        while(jsonObject.length()>0)
            jsonObject.remove(jsonObject.keys().next());

    }

    /**
     * this method will copy a file to a desired location
     * @param fileName
     * @param newLocation
     */
    public static void copyFileFromAsset(String fileName, String newLocation) {
        try {
            InputStream inputStream = GlobalActivity.context.getAssets().open(fileName);

            // Path to the just created empty file
            String outfilename = newLocation + fileName;

            File checkFolder = new File(newLocation);
            if(!checkFolder.exists()) {
                checkFolder.mkdirs();
            }

            //Open the empty db as the output stream
            OutputStream outputStream = new FileOutputStream(outfilename);
            // transfer byte to inputfile to outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer))>0) {
                outputStream.write(buffer,0,length);
            }

            //Close the streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public static boolean isFileExists(String fileWithLocation){
        boolean isExists = false;
        try {
            File dbfile = new File(fileWithLocation);
            isExists = dbfile.exists();
        } catch(SQLiteException e) {
            Log.d("FileChecking", "File doesn't exist");
        }

        return isExists;
    }

    /**
     * This method is used to check whether the provider is registered for symmetricDS service
     * @return
     */
    public static boolean isRegisteredForSymmetricdDS(){
        boolean symTablesExist, hasActiveSymService;
        //firstly we have to check whether the sym tables are created or not
        try{
            String query = "select count(*) from sqlite_master where type = 'table' and name like 'sym\\_%' escape '\\'";
            if(Integer.valueOf(CommonQueryExecution.getSingleColumnFromSingleQueryResult(query))>0){
                symTablesExist = true;
            }else {
                return false;
            }
            //checking whether there is any row in sym_outgoing_batch table for being confirmed that the
            //symmetricDS service has started actively
            String secondQuery = "select count(*) from sym_outgoing_batch where status='OK'";
            //String secondQuery = "select count(*) from sym_node_security";
            if(Integer.valueOf(CommonQueryExecution.getSingleColumnFromSingleQueryResult(secondQuery))>0){
                hasActiveSymService = true;
            }else {
                return false;
            }

            return symTablesExist && hasActiveSymService;

        }catch (NumberFormatException  nfe){
            Log.e("From regChecking:",nfe.getMessage());
            return false;
        }catch (Exception e){
            Log.e("From regChecking:",e.getMessage());
            return false;
        }

    }

    public static boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) GlobalActivity.context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method will open another app directly or open playstore to give provision to
     * install another app by package name
     * @param context
     * @param packageName
     */
    public static void openOrInstallApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * This method will return the free storage space in MB
     * @return internal free space in MB
     */
    public static long getInternalFreeSpace()
    {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        long free  = ((long)statFs.getAvailableBlocks() * (long)statFs.getBlockSize()) / 1048576;
        return free;
    }

    public static ProgressDialog ConfigureProgressDialog(Context context, int style, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(style);
        progressDialog.setMessage(message);
        return progressDialog;
    }
}
