package com.tennisdc.tln.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

//import com.crashlytics.android.Crashlytics;
//import com.braintreepayments.api.BraintreeFragment;
//import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.tennisdc.tln.R;
import com.tennisdc.tln.model.CouponDetails;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.paypal.internal.ApiClient;
import com.tennisdc.tln.paypal.internal.ApiClientRequestInterceptor;
import com.tennisdc.tln.paypal.models.Settings;

//import io.fabric.sdk.android.Fabric;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.realm.Realm;
import io.realm.internal.log.RealmLog;
import retrofit.RestAdapter;


public class App extends MultiDexApplication {

    public static final String sDefaultDomain = "tennisleaguenetwork.com";

    public static String sOAuth;
    public static boolean isPopupShow;

    public static CouponDetails sCouponDetails;
    public static GoogleAnalytics sGoogleAnalytics;
    public static Tracker sTracker;
    private Realm realm;
    //	BraintreeFragment mBraintreeFragment;
    private static ApiClient sApiClient;


    public static ApiClient getApiClient(Context context) {
        if (sApiClient == null) {
            sApiClient = new RestAdapter.Builder()
                    .setEndpoint(Settings.getEnvironmentUrl(context))
                    .setRequestInterceptor(new ApiClientRequestInterceptor())
                    .build()
                    .create(ApiClient.class);
        }

        return sApiClient;
    }

    private void updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static void resetApiClient() {
        sApiClient = null;
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
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.getStackTrace();
        }

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

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (null == sTracker) {
            String googleAnalyticsId = new Prefs.AppData(this).getGoogleAnalyticsId();
            sTracker = sGoogleAnalytics.newTracker(googleAnalyticsId);
        }
        return sTracker;
    }

    public static void LogFacebookEvent(Context mContext, String mEventValue) {
        Log.println(RealmLog.WARN, "pageView", mEventValue);
        AppEventsLogger logger = AppEventsLogger.newLogger(mContext);
        Bundle mBundle = new Bundle();
        mBundle.putString("PageView", mEventValue);
        logger.logEvent("track", mBundle);
    }

	/*public static void LogFacebookEvent(Context mContext, String mEventValue){
		Log.println(RealmLog.WARN,"pageView", mEventValue);
		AppEventsLogger logger = AppEventsLogger.newLogger(mContext);
		Bundle mBundle = new Bundle();
		mBundle.putString("PageView", mEventValue);
		logger.logEvent("track", mBundle);
	}*/

    @Override
    public void onCreate() {
        super.onCreate();
//		Fabric.with(this, new Crashlytics());
//		handleSSLHandshake();
        sOAuth = new Prefs.AppData(getApplicationContext()).getOAuthToken();
		/*try {
			mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);
			// mBraintreeFragment is ready to use!
		} catch (InvalidArgumentException e) {
			// There was an issue with your authorization string.
		}*/
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        VolleyHelper.initialise(getApplicationContext());
        updateAndroidSecurityProvider();
		/*RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
		Realm.setDefaultConfiguration(realmConfiguration);*/
	/*	Realm.init(getApplicationContext());

		RealmConfiguration config =new RealmConfiguration.Builder()
				.schemaVersion(1)
						.deleteRealmIfMigrationNeeded()
						.build();

		Realm.setDefaultConfiguration(config);*/

        //Fresco image library initialization
//		Fresco.initialize(this);

        /* For setting the actionbar font */
        //FontUtils.setReplaceNativeTypeface(this, FontUtils.NativeTypefaces.MONOSPACE, FontUtils.AppTypefaces.NOTEWORTHY_BOLD);

        // Initialize GoogleAnalytics Reference

        FirebaseOptions options = new FirebaseOptions.Builder()
//				.setApplicationId("537180101027") // Required for Analytics.
//				.setApiKey("AIzaSyBq1oZPm17ZFMUcee9uP-kvT72oRcTOy4E") // Required for Auth.
//				.setDatabaseUrl("https://androidtlnapp.firebaseio.com") // Required for RTDB.
                .setApplicationId(getString(R.string.google_app_id)) // Required for Analytics.
                .setApiKey(getString(R.string.google_api_key)) // Required for Auth.
                .setDatabaseUrl(getString(R.string.firebase_database_url)) // Required for RTDB.
                // ...
                .build();
        FirebaseApp.initializeApp(this /* Context */, options, "secondary");
        try {
            sGoogleAnalytics = GoogleAnalytics.getInstance(this);
            MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
                    .detectUnsafeIntentLaunch()
                    .build();
            StrictMode.setVmPolicy(policy);
        }
//        handleSSLHandshake();

    }

    /**
     * Enables https connections
     */
    public SSLSocketFactory getSocketFactory(Context context)
            throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Load CAs from an InputStream (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream caInput = new BufferedInputStream(context.getResources().openRawResource(R.raw.certificate));
        // I paste my myFile.crt in raw folder under res.
        Certificate ca;

        //noinspection TryFinallyCanBeTryWithResources
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    @SuppressLint("TrulyRandom")
    public void handleSSLHandshake() {
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(getSocketFactory(getApplicationContext()));
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public enum DistanceUnit {
        km, mi
    }

//	@SuppressLint("TrulyRandom")
//	public static void handleSSLHandshake() {
//		try {
//			TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//				public X509Certificate[] getAcceptedIssuers() {
//					return new X509Certificate[0];
//				}
//
//				@Override
//				public void checkClientTrusted(X509Certificate[] certs, String authType) {
//				}
//
//				@Override
//				public void checkServerTrusted(X509Certificate[] certs, String authType) {
//				}
//			}};
//
//			SSLContext sc = SSLContext.getInstance("SSL");
//			sc.init(null, trustAllCerts, new SecureRandom());
//			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//				@Override
//				public boolean verify(String arg0, SSLSession arg1) {
//					return true;
//				}
//			});
//		} catch (Exception ignored) {
//		}
//	}

}
