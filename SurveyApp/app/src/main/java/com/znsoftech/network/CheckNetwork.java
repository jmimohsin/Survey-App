package com.znsoftech.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Mohsin on 11-08-2015.
 */
public class CheckNetwork {

    public static boolean isAvailable(Context context) {
        boolean isNetworkAvailable = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) {
                    isNetworkAvailable = true;
                    break;
                }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) {
                    isNetworkAvailable = true;
                    break;
                }

        }
        return isNetworkAvailable;
    }
}
