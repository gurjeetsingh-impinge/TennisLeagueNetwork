package com.tennisdc.tln.services;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.VolleyError;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.model.UncaughtException;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created  on 2015-03-20.
 */
public class ErrorReportingService extends IntentService {

    public static final int STATUS_REPORTING = 1;
    public static final int STATUS_IDLE = 0;

    public static int STATUS = STATUS_IDLE;

    public ErrorReportingService() {
        super(ErrorReportingService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (STATUS == STATUS_IDLE && App.isNetworkOnline(getApplication())) {
            STATUS = STATUS_REPORTING;
            Realm realm = Realm.getInstance(this);
            try {
                RealmResults<UncaughtException> uncaughtExceptions = realm.allObjects(UncaughtException.class);

                if (uncaughtExceptions.size() > 0) {
                    JSONArray uncaughtExceptionsJsonArray = new JSONArray();
                    for (UncaughtException uncaughtException : uncaughtExceptions) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("error", uncaughtException.getDetails());
                            uncaughtExceptionsJsonArray.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    WSHandle.ErrorHandler.mailError(uncaughtExceptionsJsonArray, new VolleyHelper.IRequestListener<String, String>() {
                        @Override
                        public void onFailureResponse(String response) {
                            //Toast.makeText(getApplicationContext(), "Failed while reporting errors : " + response, Toast.LENGTH_LONG).show();
                            STATUS = STATUS_IDLE;
                        }

                        @Override
                        public void onSuccessResponse(String response) {
                            Realm realm = Realm.getInstance(getApplicationContext());
                            realm.beginTransaction();

                            realm.allObjects(UncaughtException.class).clear();

                            realm.commitTransaction();
                            realm.close();
                            STATUS = STATUS_IDLE;
                        }

                        @Override
                        public void onError(VolleyError error) {
                           // Toast.makeText(getApplicationContext(), "Failed while reporting errors", Toast.LENGTH_LONG).show();
                            STATUS = STATUS_IDLE;
                        }
                    });
                }
            } finally {
                realm.close();
            }
        }
    }
}
