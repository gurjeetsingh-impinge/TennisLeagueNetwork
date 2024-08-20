package com.tennisdc.tennisleaguenetwork.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created  on 16-12-2014.
 */
public class Prefs {

    private final SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    public static final String KEY_SET_APP_RUN_FIRST_TIME = "KEY_SET_APP_RUN_FIRST_TIME";
    public static final String KEY_SET_ALERT_RUN_FIRST_TIME = "KEY_SET_ALERT_RUN_FIRST_TIME";


    public Prefs(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPreferences.edit();
    }

    private String getString(String key, String defaultValue) {
        if (TextUtils.isEmpty(key)) return null;
        return mPreferences.getString(key, defaultValue);
    }

    private void putString(String key, String value) {
        if (!TextUtils.isEmpty(key)) mPreferences.edit().putString(key, value).apply();
    }

    private int getInt(String key, int defaultValue) {
        if (TextUtils.isEmpty(key)) return 0;
        return mPreferences.getInt(key, defaultValue);
    }

    private void putInt(String key, int value) {
        if (!TextUtils.isEmpty(key)) mPreferences.edit().putInt(key, value).apply();
    }

    private float getFloat(String key, int defaultValue) {
        if (TextUtils.isEmpty(key)) return 0;
        return mPreferences.getFloat(key, defaultValue);
    }

    private void putFloat(String key, float value) {
        if (!TextUtils.isEmpty(key)) mPreferences.edit().putFloat(key, value).apply();
    }

    private long getLong(String key, long defaultValue) {
        if (TextUtils.isEmpty(key)) return 0;
        return mPreferences.getLong(key, defaultValue);
    }

    private void putLong(String key, long value) {
        if (TextUtils.isEmpty(key)) return;
        mPreferences.edit().putLong(key, value).apply();
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        return !TextUtils.isEmpty(key) && mPreferences.getBoolean(key, defaultValue);
    }

    private void putBoolean(String key, boolean value) {
        if (TextUtils.isEmpty(key)) return;
        mPreferences.edit().putBoolean(key, value).apply();
    }

    public void setApp_runFirst(String App_runFirst)
    {
        mEditor.remove(KEY_SET_APP_RUN_FIRST_TIME);
        mEditor.putString(KEY_SET_APP_RUN_FIRST_TIME, App_runFirst);
        mEditor.commit();
    }

    public String getApp_runFirst()
    {
        String  App_runFirst= mPreferences.getString(KEY_SET_APP_RUN_FIRST_TIME, null);
        return  App_runFirst;
    }

    public Boolean getAlertClicked()
    {
        Boolean  App_runFirst= mPreferences.getBoolean(KEY_SET_ALERT_RUN_FIRST_TIME, false);
        return  App_runFirst;
    }

    public void setAlertClicked()
    {
        mEditor.remove(KEY_SET_ALERT_RUN_FIRST_TIME);
        mEditor.putBoolean(KEY_SET_ALERT_RUN_FIRST_TIME, false);
        mEditor.commit();
    }

    public static class AppData {

        private Prefs mPrefs;

        public AppData(Context context) {
            mPrefs = new Prefs(context);
        }

        /* First time install */
        private static final String KEY_OAUTH_TOKEN = "oauth_token";

        public void setOAuthToken(String oAuthToken) {
            mPrefs.putString(KEY_OAUTH_TOKEN, oAuthToken);
            App.sOAuth = oAuthToken;
        }

        public String getOAuthToken() {
            return mPrefs.getString(KEY_OAUTH_TOKEN, null);
        }

        /* Referral URL */
        private static final String KEY_REFERRAL_URL = "referral_url";

        public void setReferralUrl(String referralUrl) {
            mPrefs.putString(KEY_REFERRAL_URL, referralUrl);
        }

        public String getReferralUrl() {
            return mPrefs.getString(KEY_REFERRAL_URL, null);
        }

        /* domain name */
        private static final String KEY_DOMAIN_NAME = "domain_name";

        public void setDomainName(String domainName) {
            mPrefs.putString(KEY_DOMAIN_NAME, domainName);
        }

        public String getDomainName() {
            return mPrefs.getString(KEY_DOMAIN_NAME, null);
        }

        /* customer support email */
        private static final String KEY_SUPPORT_EMAIL = "support_email";

        public void setSupportEmail(String supportEmail) {
            mPrefs.putString(KEY_SUPPORT_EMAIL, supportEmail);
        }

        public String getSupportEmail() {
            return mPrefs.getString(KEY_SUPPORT_EMAIL, null);
        }

        /* domain name */
        private static final String KEY_SUPPORT_PHONE = "support_phone";

        public void setSupportPhone(String supportPhone) {
            mPrefs.putString(KEY_SUPPORT_PHONE, supportPhone);
        }

        public String getSupportPhone() {
            return mPrefs.getString(KEY_SUPPORT_PHONE, null);
        }

        private static final String KEY_TOP_BANNER = "top_banners";

        public ArrayList<String> getTopBanner() {
            String jsonString = mPrefs.getString(KEY_TOP_BANNER, null);
            if(TextUtils.isEmpty(jsonString))
                return null;
            return new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(jsonString, new TypeToken<List<String>>() {}.getType());
        }

        public void setTopBanner(ArrayList<String> banners) {
            mPrefs.putString(KEY_TOP_BANNER, new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().toJson(banners));
        }

        /* Ladder Presence */
        private static final String KEY_NO_LADDER = "no_ladder";

        public void setNoLadder(boolean noLadder) {
            mPrefs.putBoolean(KEY_NO_LADDER, noLadder);
        }

        public boolean getNoLadder() {
            return mPrefs.getBoolean(KEY_NO_LADDER, false);
        }

        /* player id */
        private static final String KEY_PLAYER = "player";

        public void setPlayer(PlayerDetail player) {
            mPrefs.putString(KEY_PLAYER, new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().toJson(player));
        }

        public PlayerDetail getPlayer() {
            String jsonString = mPrefs.getString(KEY_PLAYER, null);
            if(TextUtils.isEmpty(jsonString))
                return null;
            return new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(jsonString, PlayerDetail.class);
        }


        /* update player */
        private static final String KEY_UPDATE_PLAYER = "update_player";

        public void setUpdatePlayer(boolean updaetPlayer) {
            mPrefs.putBoolean(KEY_UPDATE_PLAYER, updaetPlayer);
        }

        public boolean getUpdatePlayer() {
            return mPrefs.getBoolean(KEY_UPDATE_PLAYER, false);
        }
    }

}
