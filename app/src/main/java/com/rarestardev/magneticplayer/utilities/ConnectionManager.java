package com.rarestardev.magneticplayer.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionManager {

    // check internet connection
    public static Boolean isInternetConnected(Context context) {
        if (isInternetEnable(context)) {
            try {
                Process process = Runtime.getRuntime().exec("ping -c 1 google.com");
                int returnVal = process.waitFor();
                return (returnVal == 0);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(Constants.appLog, e.getMessage());
            }
        }
        return false;
    }

    // Check enable wifi or mobile data
    public static Boolean isInternetEnable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}
