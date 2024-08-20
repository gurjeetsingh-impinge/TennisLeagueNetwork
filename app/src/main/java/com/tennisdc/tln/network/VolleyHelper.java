package com.tennisdc.tln.network;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.tennisdc.tln.Constants;

import java.net.URL;
import com.datatheorem.android.trustkit.TrustKit;
import com.android.volley.toolbox.HurlStack;
import java.net.MalformedURLException;

/**
 * Created  on 15-01-2015.
 */
public class VolleyHelper {

    public static final int REQUEST_TIMEOUT_MS = 1000 * 30;

    private static VolleyHelper mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    private VolleyHelper(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

//        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
//            private final LruCache<String, Bitmap> cache = new LruCache<>(30);
//
//            @Override
//            public Bitmap getBitmap(String url) {
//                Bitmap bitmap = cache.get(url);
//                return bitmap;
//            }
//
//            @Override
//            public void putBitmap(String url, Bitmap bitmap) {
//                cache.put(url, bitmap);
//            }
//        });
    }

    public static synchronized void initialise(Context context) {
        if (mInstance == null)
            mInstance = new VolleyHelper(context);
    }

    public static synchronized VolleyHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyHelper(context);
        }
        return mInstance;
    }

    public static synchronized VolleyHelper getInstance() {
        if (mInstance == null)
            throw new RuntimeException("Volley Helper is not initialised.");
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.

           // mRequestQueue = Volley.newRequestQueue(Application.getContext(), new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));

            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

//            Context context = mCtx.getApplicationContext();
//
//            // TRUSTKIT
//            TrustKit.initializeWithNetworkSecurityConfiguration(context);
//
//            String serverHostname = null;
//
//            try {
//                URL url = new URL(Constants.BASE_URL);
//                serverHostname = url.getHost();
//                Log.i("LOG_TAG", "Server Hostname: " + serverHostname);
//            } catch (MalformedURLException e) {
//                Log.e("LOG_TAG", e.getMessage());
//            }
//
//            // TRUSTKIT
//            mRequestQueue = Volley.newRequestQueue(context, new HurlStack(null, TrustKit.getInstance().getSSLSocketFactory(serverHostname)));
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

//    public ImageLoader getImageLoader() {
//        return mImageLoader;
//    }

    public static interface IRequestListener<T, U> {

        void onFailureResponse(U response);

        void onSuccessResponse(T response);

        void onError(VolleyError error);
    }
}
