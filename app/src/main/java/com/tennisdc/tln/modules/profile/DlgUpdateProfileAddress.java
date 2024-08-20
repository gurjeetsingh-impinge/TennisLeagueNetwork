package com.tennisdc.tln.modules.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.model.Common;
import com.tennisdc.tln.model.UserDetails;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 28-Sep-17.
 */

public class DlgUpdateProfileAddress extends BaseDialog {

    public static int UPDATE_USER_DETAILS = 111;
    @BindView(R.id.edt_street)
    EditText mStreetEdt;
    @BindView(R.id.edt_city)
    EditText mCityEdt;
    @BindView(R.id.spnrState)
    Spinner mState_spnr;
    private String mStreet;
    private String mCity;
    private String mState;

    private Common mStateArr[] = new Common[]{new Common("Please Select", 0), new Common("AL", 0), new Common("AK", 1), new Common("AZ", 2), new Common("AR", 3), new Common("CA", 4), new Common("CO", 5),
            new Common("CT", 6), new Common("DE", 7), new Common("DC", 8), new Common("FL", 9), new Common("GA", 10), new Common("HI", 11), new Common("ID", 12),
            new Common("IL", 13), new Common("IN", 14), new Common("IA", 15), new Common("KS", 16), new Common("KY", 17), new Common("LA", 18), new Common("ME", 19),
            new Common("MD", 20), new Common("MA", 21), new Common("MI", 22), new Common("MN", 23), new Common("MS", 24), new Common("MO", 25), new Common("MT", 26),
            new Common("NE", 27), new Common("NV", 28), new Common("NH", 29), new Common("NJ", 30), new Common("NM", 31), new Common("NY", 32), new Common("NC", 33),
            new Common("ND", 34), new Common("OH", 35), new Common("OK", 36), new Common("OR", 37), new Common("PA", 38), new Common("RI", 39), new Common("SC", 40),
            new Common("SD", 41), new Common("TN", 42), new Common("TX", 43), new Common("UT", 44), new Common("VT", 45), new Common("VA", 46), new Common("WA", 47),
            new Common("WV", 48), new Common("WI", 49), new Common("WY", 50)};

    @Override
    protected int getCustomContentLayout() {
        return R.layout.dlg_update_address;
    }

    @Override
    protected void preCreateViewSetup() {
        super.preCreateViewSetup();

        setPositiveButton("Update", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAddress();
            }
        });

        setNegativeButton("Cancel", null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void postCreateViewSetup(View v) {
        super.postCreateViewSetup(v);
        ButterKnife.bind(this, v);

        setTitle("Update Address");

        mStreetEdt.setText(mStreet);
        mCityEdt.setText(mCity);

        ArrayAdapter<Common> stateAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mStateArr);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mState_spnr.setAdapter(stateAdapter);

        boolean isSelected = false;
        for (int i = 0; i < mStateArr.length; i++) {
            if (mStateArr[i].display_name.equals(mState)) {
                mState_spnr.setSelection(i);
                isSelected = true;
                break;
            }
        }
        if (!isSelected)
            mState_spnr.setSelection(0);
    }

    private void updateAddress() {

        JSONObject params = new JSONObject();
        try {
            params.put("oauth_token", App.sOAuth);
            JSONObject player = new JSONObject();
            if (!TextUtils.isEmpty(mStreetEdt.getText().toString().trim()))
                player.put("mailing_address_1", mStreetEdt.getText().toString().trim());
            if (!TextUtils.isEmpty(mCityEdt.getText().toString().trim()))
                player.put("city", mCityEdt.getText().toString().trim());
            if (mState_spnr.getSelectedItemPosition() != 0)
                player.put("state", mState_spnr.getSelectedItem().toString());
            params.put("player", player);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
        WSHandle.Profile.updateProfileDetails(params, new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                dialog.show(getChildFragmentManager(), "dlg-frag");
            }

            @Override
            public void onSuccessResponse(String response) {
                progressDialog.dismiss();
                final BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                dialog.setNeutralButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // getActivity().onBackPressed();SingleFragmentActivity.HomeActivity.getIntent(getActivity())
                        //    startActivityForResult(SingleFragmentActivity.HomeActivity.getIntent(getActivity()), FragProfile.UPDATE_USER_DETAILS);
                        // startActivity(SingleFragmentActivity.MyProfileActivity.getIntent(getActivity()));
                        dialog.dismiss();
                        dismiss();
                        getActivity().startActivityForResult(getActivity().getIntent(), UPDATE_USER_DETAILS);
                        getActivity().finish();
                    }
                });
                dialog.show(getChildFragmentManager(), "dlg-frag");
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void setUser(UserDetails userDetails) {
        this.mCity = userDetails.City;
        this.mStreet = userDetails.Street;
        this.mState = userDetails.State;
    }

}
