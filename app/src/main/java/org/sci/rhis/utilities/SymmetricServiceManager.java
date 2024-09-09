package org.sci.rhis.utilities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.jumpmind.symmetric.android.SQLiteOpenHelperRegistry;
import org.jumpmind.symmetric.android.SymmetricService;
import org.jumpmind.symmetric.common.ParameterConstants;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.SimpleCursor;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by jamil.zaman on 03/03/16.
 * Manage properties of Symmetric service
 */
public class SymmetricServiceManager {

    private static SymmetricServiceManager ssm = null;
    private static String LOGTAG = "FWC-SYMMSERVICE-MANAGER";
    final String HELPER_KEY = "RHIS-FWC";
    private DatabaseWrapper dbWrapper;
    private Context context;

    private int providerId;
    private int providerType;
    private String server = null;
    private String nodeId = null;
    private String groupId = null;

    private SymmetricServiceManager(Context context, int providerId, int providerType) {
        this.context = context;
        this.providerId = providerId;
        this.providerType = providerType;

        try {
            dbWrapper = new DatabaseWrapper(context);
        } catch (IOException ioe) {
            Log.e(LOGTAG, "Could not open the database");
        }
    };

    public SymmetricServiceManager(Context context) {
        this.context = context;
        this.providerId = 0;
        this.providerType = 4; //FWV - default

        try {
            dbWrapper = new DatabaseWrapper(context);
        } catch (IOException ioe) {
            Log.e(LOGTAG, "Could not open the database");
        }
    };


    /*public static SymmetricServiceManager getSymmetricServiceManager(Context context, int providerId, int providerType) {
        if(ssm == null) {
            ssm = new SymmetricServiceManager(context);
        }
        return ssm;
    }*/

    private boolean populateNodeDetails() {
        boolean result = false;
        SQLiteDatabase db = DatabaseWrapper.getDatabase();
        String sql =    "SELECT * FROM node_details " +
                        "WHERE providerId = " + providerId + " " +
                        "AND providerType = " + providerType + " " +
                        "AND client = " + Constants.ANDROID_CLIENT_CODE + " "; //

        SimpleCursor rs = new SimpleCursor(db.rawQuery(sql, null));

        if(rs.next()) {
            result  = true;

            //TODO - Handle, In far far future if we need to cater for anyother provider
            groupId = rs.getInt("client") == Constants.ANDROID_CLIENT_CODE ? Constants.FWV_ANDROID : Constants.SACMO_ANDROID;
            nodeId = rs.getString("id");
            server = rs.getString("server");
        }

        return result;
    }

    public void startSynchronizationService(String server, String nodeId) {

        SQLiteOpenHelperRegistry.register(HELPER_KEY, dbWrapper);
        Intent intent = new Intent(context, SymmetricService.class);

        // --
        /*SQLiteOpenHelperRegistry.register(HELPER_KEY, client.getDatabaseWrapper());
        Intent intent = new Intent(this, SymmetricService.class);
*/
        if(isMyServiceRunning(SymmetricService.class)) {
            //stop service
            stopSynchronizationService();
        }

        // Notify the service of the database helper key
        intent.putExtra(SymmetricService.INTENTKEY_SQLITEOPENHELPER_REGISTRY_KEY,
                HELPER_KEY);
        intent.putExtra(SymmetricService.INTENTKEY_REGISTRATION_URL,
                Constants.getSymmetricdsUrl(context)+server);
        intent.putExtra(SymmetricService.INTENTKEY_EXTERNAL_ID, nodeId/*"NOAPARA01_TAB"*/);
        intent.putExtra(SymmetricService.INTENTKEY_NODE_GROUP_ID, "FWV");
        intent.putExtra(SymmetricService.INTENTKEY_START_IN_BACKGROUND, true);

        Properties properties = new Properties();
        properties.setProperty(ParameterConstants.AUTO_RELOAD_ENABLED, "false");
        properties.setProperty(ParameterConstants.AUTO_RELOAD_REVERSE_ENABLED, "true");
        properties.setProperty(ParameterConstants.TRANSPORT_HTTP_COMPRESSION_LEVEL, "9");
        properties.setProperty(ParameterConstants.ROUTING_PEEK_AHEAD_WINDOW, "20000");
        intent.putExtra(SymmetricService.INTENTKEY_PROPERTIES, properties);

        context.startService(intent);
        // --
    }

    public void stopSynchronizationService() {
        Intent intent = new Intent(context, SymmetricService.class);
        context.stopService(intent);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
