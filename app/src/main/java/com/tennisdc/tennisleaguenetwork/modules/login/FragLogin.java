package com.tennisdc.tennisleaguenetwork.modules.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.tennisdc.tln.R;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.GsonRealmExclusionStrategy;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.modules.register.FragRegisterStep1;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

public class FragLogin extends Fragment {

    private Context mContext;

    private EditText edtUsername;
    private EditText edtPassword;
    private TextView txtViewShowPassword;

    private ProgressDialog mProgressDialog;
    private int passwordNotVisible=1;

    private View.OnClickListener ShowTextClickListenser = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(passwordNotVisible == 1){
                txtViewShowPassword.setText(R.string.txt_hide_pwd);
                edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordNotVisible = 0 ;
            }
            else {
                txtViewShowPassword.setText(R.string.txt_show_pwd);
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordNotVisible = 1 ;
            }
        }
    };

    private View.OnClickListener LoginButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isError = false;

            String strUsername = edtUsername.getText().toString().trim();
            String strPassword = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(strUsername)) {
                edtUsername.setError("Provide Email");
                isError = true;
            } else if (!App.isValidEmail(strUsername)) {
                edtUsername.setError("Invalid Email");
                isError = true;
            }

            if (TextUtils.isEmpty(strPassword)) {
                edtPassword.setError("Provide Password");
                isError = true;
            }

            if (!isError) {
                if (App.isNetworkOnline(mContext)) {
                    //new LoginAsyncTask().execute(strUsername, strPassword);
                    mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...");
                    WSHandle.Login.login(strUsername, strPassword, new VolleyHelper.IRequestListener<JSONObject, String>() {
                        @Override
                        public void onFailureResponse(String response) {
                            mProgressDialog.dismiss();
                            edtUsername.setError("invalid/wrong username");
                            edtPassword.setError("invalid/wrong password");

                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                            dialog.show(getChildFragmentManager(), "dlg-frag");
                        }

                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            try {
                                Log.e(" Login ->", response.toString());
                                String oauth_token = response.getString("oauth_token");
                                String domain_name = response.getString("domain_name");
                                String referral_url = response.getString("referral_url");
                                String customerSupportEmail = response.getString("customer_support_email");
                                String customerSupportPhone = response.getString("customer_support_phone");
                                boolean noLadder = response.getBoolean("no_ladder");

                                PlayerDetail playerDetail = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("player"), new TypeToken<PlayerDetail>() {}.getType());

                                ArrayList<String> topBanners = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("top_banners"), new TypeToken<List<String>>() {}.getType());
                                Prefs.AppData prefs = new Prefs.AppData(mContext);
                                prefs.setPlayer(playerDetail);
                                prefs.setOAuthToken(oauth_token);
                                prefs.setDomainName(domain_name);
                                prefs.setSupportEmail(customerSupportEmail);
                                prefs.setSupportPhone(customerSupportPhone);
                                prefs.setTopBanner(topBanners);
                                prefs.setNoLadder(noLadder);
                                prefs.setReferralUrl(referral_url);

                                startActivity(SingleFragmentActivity.getIntent(mContext, null, null));
                                getActivity().finish();
                            } catch (JSONException e) {
                                   e.printStackTrace();
                            }
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onError(VolleyError error) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(), "Network error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Internet not working?").show(getChildFragmentManager(), "dlg-frg");
                }
            }
        }
    };

    private View.OnClickListener RegisterButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(SingleFragmentActivity.getIntent(mContext, FragRegisterStep1.class, null));
        }
    };

    private View.OnClickListener ForgotPassButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(SingleFragmentActivity.getIntent(mContext, FragForgotPass.class, null));
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_login, container, false);

        edtUsername = (EditText) v.findViewById(R.id.edtUsername);
        edtPassword = (EditText) v.findViewById(R.id.edtPassword);
        txtViewShowPassword = (TextView) v.findViewById(R.id.txtViewShow);
        txtViewShowPassword.setOnClickListener(ShowTextClickListenser);

        v.findViewById(R.id.btnLogin).setOnClickListener(LoginButtonClickListener);
        v.findViewById(R.id.btnRegister).setOnClickListener(RegisterButtonClickListener);
        v.findViewById(R.id.btnForgotPassword).setOnClickListener(ForgotPassButtonClickListener);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.ic_action);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onDetach() {
        mContext = null;
        super.onDetach();
    }

    /*private class LoginAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            return WSHandle.Login.login(params[0], params[1]);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...");
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            mProgressDialog.dismiss();
            if (jsonObject != null) {
                try {
                    if (jsonObject.get("responseCode").equals("200")) {
                        String oauth_token = jsonObject.getString("oauth_token");
                        String domain_name = jsonObject.getString("domain_name");

                        String customerSupportEmail = jsonObject.getString("customer_support_email");
                        String customerSupportPhone = jsonObject.getString("customer_support_phone");

                        PlayerDetail playerDetail = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(jsonObject.getString("player"), new TypeToken<PlayerDetail>() {}.getType());

                        Prefs.AppData prefs = new Prefs.AppData(mContext);
                        prefs.setPlayer(playerDetail);
                        prefs.setOAuthToken(oauth_token);
                        prefs.setDomainName(domain_name);
                        prefs.setSupportEmail(customerSupportEmail);
                        prefs.setSupportPhone(customerSupportPhone);

                        startActivity(SingleFragmentActivity.getIntent(mContext, null, null));
                        getActivity().finish();
                    } else {
                        edtUsername.setError("invalid/wrong username");
                        edtPassword.setError("invalid/wrong password");

                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, jsonObject.getString("responseMessage"));
                        dialog.show(getChildFragmentManager(), "dlg-frag");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

}
