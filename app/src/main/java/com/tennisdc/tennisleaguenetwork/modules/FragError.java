package com.tennisdc.tennisleaguenetwork.modules;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tennisleaguenetwork.AuthActivity;
import com.tennisdc.tln.BuildConfig;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tennisleaguenetwork.model.CourtRegion;
import com.tennisdc.tennisleaguenetwork.model.Location;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.services.ErrorReportingService;
import com.tennisdc.tln.common.App;

import io.realm.Realm;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created  on 2015-03-21.
 */
public class FragError extends Fragment {

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String versionCodeString = "version_code";
    private Context mContext;
    private Realm mRealm;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            mRealm = Realm.getInstance(context);
        } catch (RealmMigrationNeededException e) {
            Realm.deleteRealmFile(context);
            mRealm = Realm.getInstance(context);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_error, container, false);
//        App.LogFacebookEvent(getActivity(),FragError.class.getName());
        view.findViewById(R.id.btnPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().finish();
                final int versionCode = BuildConfig.VERSION_CODE;
                sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                int preferencesVersionCode = sharedpreferences.getInt(versionCodeString, 0);
                if (preferencesVersionCode == 0 || preferencesVersionCode < versionCode) {
                    WSHandle.Login.logout(new Prefs.AppData(mContext).getOAuthToken(), new VolleyHelper.IRequestListener<String, String>() {
                        @Override
                        public void onFailureResponse(String response) {
                            Toast.makeText(mContext, "Error while logging out.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccessResponse(String response) {
                            SharedPreferences.Editor edit = sharedpreferences.edit();
                            edit.putInt(versionCodeString,versionCode);
                            edit.commit();
                            new Prefs.AppData(mContext).setOAuthToken(null);
                            mRealm.beginTransaction();
                            mRealm.allObjects(Court.class).clear();
                            mRealm.allObjects(Location.class).clear();
                            mRealm.allObjects(CourtRegion.class).clear();
                            mRealm.commitTransaction();
                            startActivity(AuthActivity.getIntent(mContext));
                            getActivity().finish();
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Toast.makeText(mContext, "Network error while logging out.", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().startService(new Intent(getActivity(), ErrorReportingService.class));
    }

}
