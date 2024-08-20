package com.tennisdc.tennisleaguenetwork;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by rajath.akki on 2/18/2018.
 */

public class NetworkUtils {

    /*
     * This method is to check whether device has network connection
     * @param context
     * @returns boolean value
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
