package com.tennisdc.tennisleaguenetwork.services;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.Payment;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created  on 2015-05-05.
 */
public class SubmitPaymentService extends IntentService {

    public static final int STATUS_REPORTING = 1;
    public static final int STATUS_IDLE = 0;

    public static int STATUS = STATUS_IDLE;

    public SubmitPaymentService() {
        super(SubmitPaymentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (STATUS == STATUS_IDLE && App.isNetworkOnline(getApplication())) {
            STATUS = STATUS_REPORTING;
            List<JSONObject> jsonObjects = new ArrayList<>();

            /* get all */
            Realm realm = Realm.getInstance(getApplicationContext());
            RealmResults<Payment> payments = realm.allObjects(Payment.class);
            for (final Payment payment : payments) {
                try {
                    jsonObjects.add(new JSONObject(payment.getJsonString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            realm.close();

            if (jsonObjects.size() > 0) {
                for (final JSONObject paymentJsonObject : jsonObjects) {

                    WSHandle.Buy.submitPaymentDetails(paymentJsonObject, new VolleyHelper.IRequestListener<String, String>() {
                        @Override
                        public void onFailureResponse(String response) {
                            Toast.makeText(getApplicationContext(), "Failed while submitting payment details : " + response, Toast.LENGTH_LONG).show();
                            STATUS = STATUS_IDLE;
                        }

                        @Override
                        public void onSuccessResponse(String response) {
                            Toast.makeText(getApplicationContext(), "Payments details submitted successfully", Toast.LENGTH_LONG).show();
                            Realm realm = Realm.getInstance(getApplicationContext());
                            try {
                                realm.beginTransaction();
                                realm.where(Payment.class).equalTo("id", paymentJsonObject.get("transaction_id").toString()).findFirst().removeFromRealm();
                                realm.commitTransaction();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                realm.close();
                            }

                            new Prefs.AppData(getApplicationContext()).setUpdatePlayer(true);

                            STATUS = STATUS_IDLE;
                        }

                        @Override
                        public void onError(VolleyError error) {
                            //Toast.makeText(getApplicationContext(), "Failed while submitting payment details", Toast.LENGTH_LONG).show();
                            STATUS = STATUS_IDLE;
                        }
                    });
                }
            }
        }
    }

}
