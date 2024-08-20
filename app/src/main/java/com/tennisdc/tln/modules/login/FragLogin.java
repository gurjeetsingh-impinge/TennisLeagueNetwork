package com.tennisdc.tln.modules.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.FragHome;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.OurPlayerDetailMode;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.model.PlayerInformationModel;
import com.tennisdc.tln.modules.profile.CardStackAdapter;
import com.tennisdc.tln.modules.register.FragRegisterStep1;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.StackFrom;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragLogin extends Fragment {

	private Context mContext;

	private EditText edtUsername;
	private EditText edtPassword;
	private TextView txtViewShowPassword;

	private ProgressDialog mProgressDialog;
	private int passwordNotVisible = 1;
	Prefs.AppData prefs;

	private View.OnClickListener ShowTextClickListenser = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (passwordNotVisible == 1) {
				txtViewShowPassword.setText(R.string.txt_hide_pwd);
				edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				passwordNotVisible = 0;
			} else {
				txtViewShowPassword.setText(R.string.txt_show_pwd);
				edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				passwordNotVisible = 1;
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
					WSHandle.Login.login(strUsername, strPassword,prefs.getDeviceToken(),"0", new VolleyHelper.IRequestListener<JSONObject, String>() {
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
								String user_id = response.getJSONObject("player").getString("player_id");
								String oauth_token = response.getString("oauth_token");
								String tennis_lesson_url = response.getString("tennis_lesson_url");
								String domain_name = response.getString("domain_name");
								String referral_url = response.getString("referral_url");
								String google_analytics_id = response.getString("google_analytics_id");
								String customerSupportEmail = response.getString("customer_support_email");
								String customerSupportPhone = response.getString("customer_support_phone");
								boolean utrActive = response.getBoolean("utr_active");
								String utrLabel = response.getString("utr_label");
								String homeCourt = response.getJSONObject("player").getJSONObject("home_court").getString("name");
								String customer_id = "";
								if(response.getString("customer_token") != null){
									customer_id = response.getString("customer_token");
								}
								boolean noLadder = response.getBoolean("no_ladder");
								boolean showLadderIcon = response.getBoolean("show_ladder_icon");
								boolean showMonthlyContest = response.getBoolean("show_monthly_contest");

								PlayerDetail playerDetail = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("player"), new TypeToken<PlayerDetail>() {
								}.getType());

								ArrayList<String> topBanners = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("top_banners"), new TypeToken<List<String>>() {
								}.getType());
								prefs.setPlayer(playerDetail);

								prefs.setUserID(user_id);
								prefs.setOAuthToken(oauth_token);
								prefs.setTennisLessonUrl(tennis_lesson_url);
								prefs.setDomainName(domain_name);
								prefs.setGoogleAnalyticsId(google_analytics_id);
								prefs.setSupportEmail(customerSupportEmail);
								prefs.setSupportPhone(customerSupportPhone);
								prefs.setTopBanner(topBanners);
								prefs.setNoLadder(noLadder);
								prefs.setLadderIconShow(showLadderIcon);
								prefs.setReferralUrl(referral_url);
								prefs.setHomeCourt(homeCourt);
								prefs.setURTActive(utrActive);
								prefs.setUtrLabel(utrLabel);
								prefs.setCustomerID(customer_id);
								prefs.setMonthlyContestShow(showMonthlyContest);
								getPlayerDetails();
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

	void getPlayerDetails() {
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
		WSHandle.Profile.getPlayerInformation(prefs.getUserID(), "1", new VolleyHelper.IRequestListener<String, String>() {
			@Override
			public void onFailureResponse(String response) {
				progressDialog.dismiss();
				startActivity(SingleFragmentActivity.getIntentWithFlag(mContext,
						null, null));
				getActivity().finish();

//				BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
//				dialog.show(getSupportFragmentManager(), "dlg-frag");
			}

			@Override
			public void onSuccessResponse(String response) {
				progressDialog.dismiss();
				Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

				final PlayerInformationModel.UserDataBean mPlayerDetails = gson.fromJson(response, new TypeToken<PlayerInformationModel.UserDataBean>() {
				}.getType());
				prefs.setUserData(response);
				startActivity(SingleFragmentActivity.getIntentWithFlag(mContext,
						FragHome.class, null));
				getActivity().finish();
//				setUI(mPlayerDetails);
			}

			@Override
			public void onError(VolleyError error) {
				progressDialog.dismiss();
				startActivity(SingleFragmentActivity.getIntentWithFlag(mContext,
						null, null));
				getActivity().finish();

//				Toast.makeText(MyAccountScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	}

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
		App.LogFacebookEvent(getActivity(),this.getClass().getName());

		edtUsername = (EditText) v.findViewById(R.id.edtUsername);
		edtPassword = (EditText) v.findViewById(R.id.edtPassword);
		txtViewShowPassword = (TextView) v.findViewById(R.id.txtViewShow);
		txtViewShowPassword.setOnClickListener(ShowTextClickListenser);

		v.findViewById(R.id.btnLogin).setOnClickListener(LoginButtonClickListener);
		v.findViewById(R.id.btnRegister).setOnClickListener(RegisterButtonClickListener);
		v.findViewById(R.id.btnForgotPassword).setOnClickListener(ForgotPassButtonClickListener);
		prefs = new Prefs.AppData(mContext);
		if(prefs.getDeviceToken().equals("")){
			getDeviceToken();
		}


		return v;
	}
	@Override
	public void onResume() {
		super.onResume();
		ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (actionBar != null) {
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
	void getDeviceToken() {
		FirebaseInstanceId.getInstance().getInstanceId()
				.addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
					@Override
					public void onComplete(@NonNull Task<InstanceIdResult> task) {
						if (task.isSuccessful()) {
							prefs.setDeviceToken(task.getResult().getToken());
						}
					}
				});
	}
}
