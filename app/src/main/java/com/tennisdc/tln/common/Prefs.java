package com.tennisdc.tln.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.model.PlayerDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created  on 16-12-2014.
 */
public class Prefs {

	public static final String KEY_SET_APP_RUN_FIRST_TIME = "KEY_SET_APP_RUN_FIRST_TIME";
	public static final String KEY_SET_ALERT_RUN_FIRST_TIME = "KEY_SET_ALERT_RUN_FIRST_TIME";
	private final SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;


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

	public String getApp_runFirst() {
		String App_runFirst = mPreferences.getString(KEY_SET_APP_RUN_FIRST_TIME, null);
		return App_runFirst;
	}

	public void setApp_runFirst(String App_runFirst) {
		mEditor.remove(KEY_SET_APP_RUN_FIRST_TIME);
		mEditor.putString(KEY_SET_APP_RUN_FIRST_TIME, App_runFirst);
		mEditor.commit();
	}

	public Boolean getAlertClicked() {
		Boolean App_runFirst = mPreferences.getBoolean(KEY_SET_ALERT_RUN_FIRST_TIME, false);
		return App_runFirst;
	}

	public void setAlertClicked() {
		mEditor.remove(KEY_SET_ALERT_RUN_FIRST_TIME);
		mEditor.putBoolean(KEY_SET_ALERT_RUN_FIRST_TIME, false);
		mEditor.commit();
	}

	public static class AppData {

		/* First time install */
		private static final String KEY_OAUTH_TOKEN = "oauth_token";
		/* Url for Tennis Lesson */
		private static final String KEY_TENNIS_LESSON_URL = "tennis_lesson_url";
		/* Device Token for notification */
		private static final String DEVICE_TOKEN = "device_token";
		/* Complete User Data */
		private static final String KEY_USER_DATA = "user_data";
		private static final String KEY_OTHER_PLAYER_DATA = "other_player_data";
		/* USER ID */
		private static final String KEY_USER_ID = "user_ID";
		/* Referral URL */
		private static final String KEY_REFERRAL_URL = "referral_url";
		/* domain name */
		private static final String KEY_DOMAIN_NAME = "domain_name";
		/*google analytics id*/
		private static final String GOOGLE_ANALYTICS_ID = "google_analytics_id";
		/* customer support email */
		private static final String KEY_SUPPORT_EMAIL = "support_email";
		/* domain name */
		private static final String KEY_SUPPORT_PHONE = "support_phone";
		private static final String KEY_TOP_BANNER = "top_banners";
		/* Ladder Presence */
		private static final String KEY_NO_LADDER = "no_ladder";

		private static final String KEY_SHOW_LADDER_ICON = "show_ladder_icon";
		private static final String KEY_SHOW_MONTHLY_CONTEST = "showMonthlyContest";
		/* player id */
		private static final String KEY_PLAYER = "player";
		/* update player */
		private static final String KEY_UPDATE_PLAYER = "update_player";
		/* notificatio count */
		private static final String KEY_NOTIFICATION_COUNT = "notification_count";
		/* home court */
		private static final String KEY_HOME_COURT = "home_court";
		/* UTR  active */
		private static final String KEY_UTR_ACTIVE = "utr_active";
		/* UTR label */
		private static final String KEY_UTR_LABEL = "utr_label";
		/* Facebook Sharing option */
		private static final String KEY_FACEBOOK_SHARING = "facebook_sharing";
		/* Facebook Sharing option */
		private static final String KEY_FACEBOOK_SHARING_OPTION = "facebook_sharing_option";
		/* Customer id */
		private static final String KEY_CUSTOMER_ID = "customer_id";
		/* Key for Matches Played new flag */
		private static final String KEY_MATCHES_PLAYED = "matches_playes";
		/*Key to check whether we should show Guide button in Division Report screen or not*/
		private static final String KEY_SHOW_GUIDE_BUTTON = "guide_button";
		private Prefs mPrefs;

		public AppData(Context context) {
			mPrefs = new Prefs(context);
		}

		public String getOAuthToken() {
			return mPrefs.getString(KEY_OAUTH_TOKEN, null);
		}

		public void setOAuthToken(String oAuthToken) {
			mPrefs.putString(KEY_OAUTH_TOKEN, oAuthToken);
			App.sOAuth = oAuthToken;
		}

		public String getTennisLessonUrl() {
			return mPrefs.getString(KEY_TENNIS_LESSON_URL, null);
		}

		public void setTennisLessonUrl(String mTennisLessonUrl) {
			mPrefs.putString(KEY_TENNIS_LESSON_URL, mTennisLessonUrl);
		}
		public void setUserID(String userID) {
			mPrefs.putString(KEY_USER_ID, userID);
		}

		public String getUserID() {
			return mPrefs.getString(KEY_USER_ID, "");
		}

		public void setUserData(String userData) {
			mPrefs.putString(KEY_USER_DATA, userData);
		}

		public String getUserData() {
			return mPrefs.getString(KEY_USER_DATA, "");
		}

		public void setOtherUserData(String userData) {
			mPrefs.putString(KEY_OTHER_PLAYER_DATA, userData);
		}

		public String getOtherUserData() {
			return mPrefs.getString(KEY_OTHER_PLAYER_DATA, "");
		}

		public String getReferralUrl() {
			return mPrefs.getString(KEY_REFERRAL_URL, null);
		}

		public void setReferralUrl(String referralUrl) {
			mPrefs.putString(KEY_REFERRAL_URL, referralUrl);
		}

		public String getDomainName() {
			return mPrefs.getString(KEY_DOMAIN_NAME, null);
		}

		public void setDomainName(String domainName) {
			mPrefs.putString(KEY_DOMAIN_NAME, domainName);
		}

		public String getDeviceToken() {
			return mPrefs.getString(DEVICE_TOKEN, "");
		}

		public void setDeviceToken(String domainName) {
			mPrefs.putString(DEVICE_TOKEN, domainName);
		}

		public String getGoogleAnalyticsId() {
			return mPrefs.getString(GOOGLE_ANALYTICS_ID, null);
		}

		public void setGoogleAnalyticsId(String googleAnalyticsId) {
			mPrefs.putString(GOOGLE_ANALYTICS_ID, googleAnalyticsId);
		}

		public String getSupportEmail() {
			return mPrefs.getString(KEY_SUPPORT_EMAIL, null);
		}

		public void setSupportEmail(String supportEmail) {
			mPrefs.putString(KEY_SUPPORT_EMAIL, supportEmail);
		}

		public String getSupportPhone() {
			return mPrefs.getString(KEY_SUPPORT_PHONE, null);
		}

		public void setSupportPhone(String supportPhone) {
			mPrefs.putString(KEY_SUPPORT_PHONE, supportPhone);
		}

		public ArrayList<String> getTopBanner() {
			String jsonString = mPrefs.getString(KEY_TOP_BANNER, null);
			if (TextUtils.isEmpty(jsonString))
				return null;
			return new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(jsonString, new TypeToken<List<String>>() {
			}.getType());
		}

		public void setTopBanner(ArrayList<String> banners) {
			mPrefs.putString(KEY_TOP_BANNER, new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().toJson(banners));
		}

		public boolean getLadderIconStatus() {
			return mPrefs.getBoolean(KEY_SHOW_LADDER_ICON, false);
		}

		public void setLadderIconShow(boolean noLadder) {
			mPrefs.putBoolean(KEY_SHOW_LADDER_ICON, noLadder);
		}

		public boolean getMonthlyContestShow() {
			return mPrefs.getBoolean(KEY_SHOW_MONTHLY_CONTEST, false);
		}

		public void setMonthlyContestShow(boolean noLadder) {
			mPrefs.putBoolean(KEY_SHOW_MONTHLY_CONTEST, noLadder);
		}

		public boolean getNoLadder() {
			return mPrefs.getBoolean(KEY_NO_LADDER, false);
		}

		public void setNoLadder(boolean noLadder) {
			mPrefs.putBoolean(KEY_NO_LADDER, noLadder);
		}

		public PlayerDetail getPlayer() {
			String jsonString = mPrefs.getString(KEY_PLAYER, null);
			if (TextUtils.isEmpty(jsonString))
				return null;
			return new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(jsonString, PlayerDetail.class);
		}

		public void setPlayer(PlayerDetail player) {
			mPrefs.putString(KEY_PLAYER, new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().toJson(player));
		}

		public boolean getUpdatePlayer() {
			return mPrefs.getBoolean(KEY_UPDATE_PLAYER, false);
		}

		public void setUpdatePlayer(boolean updaetPlayer) {
			mPrefs.putBoolean(KEY_UPDATE_PLAYER, updaetPlayer);
		}

		public int getNotificationCount() {
			return mPrefs.getInt(KEY_NOTIFICATION_COUNT, 0);
		}

		public void setNotificationCount(int updaetPlayer) {
			mPrefs.putInt(KEY_NOTIFICATION_COUNT, updaetPlayer);
		}

		public boolean getURTActive() {
			return mPrefs.getBoolean(KEY_UTR_ACTIVE, false);
		}

		public void setURTActive(boolean urtActive) {
			mPrefs.putBoolean(KEY_UTR_ACTIVE, urtActive);
		}

		public String getUtrLabel() {
			return mPrefs.getString(KEY_UTR_LABEL, "");
		}

		public void setUtrLabel(String utrLabel) {
			mPrefs.putString(KEY_UTR_LABEL, utrLabel);
		}

		public String getCustomerID() {
			return mPrefs.getString(KEY_CUSTOMER_ID, "");
		}

		public void setCustomerID(String customer_id) {
			mPrefs.putString(KEY_CUSTOMER_ID, customer_id);
		}

		public String getDateMatchesPlayed() {
			return mPrefs.getString(KEY_MATCHES_PLAYED, "");
		}

		public void setDateMatchesPlayed(String mDateMatchesPlayed) {
			mPrefs.putString(KEY_MATCHES_PLAYED, mDateMatchesPlayed);
		}

		public boolean getFacebookSharing() {
			return mPrefs.getBoolean(KEY_FACEBOOK_SHARING, false);
		}

		public void setFacebookSharing(boolean facebookSharing) {
			mPrefs.putBoolean(KEY_FACEBOOK_SHARING, facebookSharing);
		}

		public boolean getFacebookSharingOption() {
			return mPrefs.getBoolean(KEY_FACEBOOK_SHARING_OPTION, false);
		}

		public void setFacebookSharingOption(boolean facebookSharing) {
			mPrefs.putBoolean(KEY_FACEBOOK_SHARING_OPTION, facebookSharing);
		}

		public String getHomeCourt() {
			return mPrefs.getString(KEY_HOME_COURT, "");
		}

		public void setHomeCourt(String homeCourt) {
			mPrefs.putString(KEY_HOME_COURT, homeCourt);
		}

		public boolean canShowGuidePopup(){
			return mPrefs.getBoolean(KEY_SHOW_GUIDE_BUTTON,true);
		}

		public void setGuideButtonVisibility(boolean visibility){
			mPrefs.putBoolean(KEY_SHOW_GUIDE_BUTTON,visibility);
		}
	}

}
