package com.tennisdc.tennisleaguenetwork.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.ExclusionStrategy;
import com.tennisdc.tennisleaguenetwork.model.CouponDetails;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends MultiDexApplication {

    public static final String sDefaultDomain = "tennisleaguenetwork.com";

    public static String sOAuth;
    public static boolean isPopupShow;

    public static CouponDetails sCouponDetails;

    @Override
    public void onCreate() {
        super.onCreate();

        sOAuth = new Prefs.AppData(getApplicationContext()).getOAuthToken();

        VolleyHelper.initialise(getApplicationContext());

        //Fresco image library initialization
        //Fresco.initialize(this);
        /* For setting the actionbar font */
        //FontUtils.setReplaceNativeTypeface(this, FontUtils.NativeTypefaces.MONOSPACE, FontUtils.AppTypefaces.NOTEWORTHY_BOLD);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static boolean isNetworkOnline(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getMobileNumber(Context context) {
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    public enum DistanceUnit {
        km, mi
    }

    public static double convertMeterTo(double meters, DistanceUnit unit) {
        switch (unit) {
            case km:
                return meters / 1000;
            case mi:
                return meters / 1609.34;
        }
        return 0;
    }

    public static double convertToMeter(double distance, DistanceUnit unit) {
        switch (unit) {
            case km:
                return distance * 1000;
            case mi:
                return distance * 1609.34;
        }
        return 0;
    }
}
