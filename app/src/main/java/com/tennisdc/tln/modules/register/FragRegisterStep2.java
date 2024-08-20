package com.tennisdc.tln.modules.register;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.JoiningDoubles;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.model.SkillLevel;
import com.tennisdc.tln.model.UstaRank;
import com.tennisdc.tln.modules.DateYearPicker.YearMonthPickerDialog;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.CustomSpinnerWithErrorAdapter;
import com.tennisdc.tln.ui.PhoneNumberFormattingTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FragRegisterStep2 extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final static int REQUEST_READ_PHONE_STATE = 111;
    private final static String EXTRA_FIRST_NAME = "fname";
    private final static String EXTRA_LAST_NAME = "lname";
    private final static String EXTRA_EMAIL = "email";
    private final static String EXTRA_PASS = "pass";
    private final static String EXTRA_ZIP = "zip";
    private final static String EXTRA_DOMAIN_ID = "location_id";
    private final static String EXTRA_STATE = "state";
    private final static String EXTRA_CITY = "city";
    final Calendar myCalendar = Calendar.getInstance();
    private Context mContext;
    private EditText edtMobile;
    private EditText edtReferralMethod;
    private Spinner spnrPreferredRating;
    private Spinner spnrUstaLevel;
    private Spinner spnrJoinDoubles;
    private ProgressDialog mProgressDialog;
    private LinearLayout llPartnersSkillLevel;
    private LinearLayout llHavePartner;
    private Spinner spnrPartnersPreferredRating;
    private EditText edtPartnerFN;
    private EditText edtPartnerLN;
    private EditText edtPartnersEmail;
    private CheckBox mCheckBox;
    private TextView txtTermsNCond;
    private TextView linkTermsNCond;
    private EditText birthRange;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mPass;
    private String mZip;
    private String mMobile;
    private String mPartnersFName, mPartnerLName, mPartnersEmail, mPartnerSkillId;
    private String mReferralMethod;
    private String mDomainId = null;
    private String mState = "";
    private String mCity = "";

    private String birth_range_string = "";

    private UstaRank mUstaRannks[] = new UstaRank[]{new UstaRank("Not Rated", ""), new UstaRank("2.0", "2.0"), new UstaRank("2.5", "2.5"), new UstaRank("3.0", "3.0"), new UstaRank("3.5", "3.5"), new UstaRank("4.0", "4.0"), new UstaRank("4.5", "4.5"), new UstaRank("5.0", "5.0")};
    private JoiningDoubles mJoinDoubles[] = new JoiningDoubles[]{new JoiningDoubles("Not Interested", "0"), new JoiningDoubles("Joining, have a partner", "1"), new JoiningDoubles("Joining, don't have a partner", "2")};
    private CustomSpinnerWithErrorAdapter<SkillLevel> mSkillLevelAdapter;
    private CustomSpinnerWithErrorAdapter<SkillLevel> mPartnersSkillLevelAdapter;

    private View.OnClickListener SubmitCreateAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isError = false;

            String strMobile = edtMobile.getText().toString().trim();
            String strBirthRange = birthRange.getText().toString().trim();

            if (TextUtils.isEmpty(strMobile)) {
                isError = true;
                edtMobile.setError("Provide Mobile Number");
            } else {
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                try {
                    Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(strMobile, Locale.getDefault().getCountry());
                    strMobile = String.valueOf(phoneNumber.getNationalNumber());
                    String numbers = strMobile.replaceAll("[^0-9]", "");
                    if (numbers.length() < 10) {
                        isError = true;
                        edtMobile.setError("Invalid Mobile Number");
                    }
                    /*if (!(strMobile.length() >= 10 && strMobile.length() <= 12 && TextUtils.isDigitsOnly(strMobile))) {
                        isError = true;
                        edtMobile.setError("Invalid Mobile");
                    }*/
                } catch (NumberParseException e) {
                    isError = true;
                    edtMobile.setError("Invalid Mobile Number");
                }
            }

            SkillLevel skillLevel = (SkillLevel) spnrPreferredRating.getSelectedItem();
            if (skillLevel == null && skillLevel.id == 0) {
                isError = true;
                mSkillLevelAdapter.setError(spnrPreferredRating.getSelectedView(), "Please Select Skill Level");
            }

            JoiningDoubles joiningDoubles = (JoiningDoubles) spnrJoinDoubles.getSelectedItem();
            int doublesInterest = Integer.parseInt(joiningDoubles.value);
            switch (doublesInterest) {
                case 0:
                    mPartnersFName = null;
                    mPartnerLName = null;
                    mPartnersEmail = null;
                    mPartnerSkillId = null;
                    break;
                case 1:
                    if (checkForPartnersEmptyFields()) isError = true;
                    if (checkForPartnersSkillLevel()) isError = true;
                    break;
                case 2:
                    if (checkForPartnersSkillLevel()) isError = true;
                    break;
            }

            if (!mCheckBox.isChecked()) {
                Toast.makeText(mContext, "Please accept 'Terms of Service'.", Toast.LENGTH_SHORT).show();
                isError = true;
            }

            mReferralMethod = edtReferralMethod.getText().toString().trim();
            if (TextUtils.isEmpty(mReferralMethod)) {
                edtReferralMethod.setError("Please tell us how you found out about the league");
                isError = true;
            }

            if (!isError) {
                UstaRank ustaRank = (UstaRank) spnrUstaLevel.getSelectedItem();

                if (App.isNetworkOnline(mContext)) {
                    //new CreateAccountAsyncTask().execute(mFirstName, mLastName, mEmail, mPass, mZip, strMobile, String.valueOf(skillLevel.id), ustaRank.value);
                    mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...", true, false);
                    WSHandle.Login.createAccount(mFirstName, mLastName, mEmail, mPass, mZip, mDomainId, strMobile, String.valueOf(skillLevel.id), ustaRank.value,
                            birth_range_string, joiningDoubles.value, mPartnerSkillId, mPartnersFName, mPartnerLName, mPartnersEmail,
                            mReferralMethod,mState,mCity, new VolleyHelper.IRequestListener<JSONObject, String>() {
                                @Override
                                public void onFailureResponse(String response) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onSuccessResponse(final JSONObject response) {
                                    mProgressDialog.dismiss();
                                    try {
                                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response.getString("responseMessage"));
                                        dialog.setPositiveButton(getString(android.R.string.ok), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    String oauth_token = response.getString("oauth_token");
                                                    String domain_name = response.getString("domain_name");
                                                    //long playerId =  jsonObject.getLong("player_id");
                                                    String google_analytics_id = response.getString("google_analytics_id");
                                                    String customerSupportEmail = response.getString("customer_support_email");
                                                    String customerSupportPhone = response.getString("customer_support_phone");

                                                    PlayerDetail playerDetail = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("player"), new TypeToken<PlayerDetail>() {
                                                    }.getType());

                                                    Prefs.AppData prefs = new Prefs.AppData(mContext);
                                                    prefs.setPlayer(playerDetail);
                                                    prefs.setOAuthToken(oauth_token);
                                                    prefs.setDomainName(domain_name);
                                                    prefs.setGoogleAnalyticsId(google_analytics_id);
                                                    prefs.setSupportPhone(customerSupportPhone);
                                                    prefs.setSupportEmail(customerSupportEmail);

                                                    startActivity(SingleFragmentActivity
                                                            .getIntentWithFlag(mContext, null, null));

                                                    getActivity().finish();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        dialog.show(getChildFragmentManager(), "dlg-frag");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(mContext, "Network error occurred", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Internet not working?").show(getChildFragmentManager(), "dlg-frg");
                }
            }
        }
    };

    public static Bundle getFragArgumentBundle(String fName, String lName, String email, String pass,
                                               String zip, String domainId, String state, String city) {

        Bundle args = new Bundle();
        args.putString(EXTRA_FIRST_NAME, fName);
        args.putString(EXTRA_LAST_NAME, lName);
        args.putString(EXTRA_EMAIL, email);
        args.putString(EXTRA_PASS, pass);
        args.putString(EXTRA_ZIP, zip);
        args.putString(EXTRA_DOMAIN_ID, domainId);
        args.putString(EXTRA_STATE, state);
        args.putString(EXTRA_CITY, city);

        return args;
    }

    private boolean checkForPartnersSkillLevel() {
        SkillLevel partnersSkillLevel = (SkillLevel) spnrPartnersPreferredRating.getSelectedItem();
        if (partnersSkillLevel == null || partnersSkillLevel.id != 0) {
            mPartnerSkillId = String.valueOf(partnersSkillLevel.id);
            return false;
        } else {
            mPartnersSkillLevelAdapter.setError(spnrPartnersPreferredRating.getSelectedView(), "Please Select Skill Level");
            return true;
        }
    }

    private boolean checkForPartnersEmptyFields() {
        boolean isError = false;
        View firstErrorView = null;

        mPartnersFName = edtPartnerFN.getText().toString().trim();
        mPartnerLName = edtPartnerLN.getText().toString().trim();
        mPartnersEmail = edtPartnersEmail.getText().toString().trim();

        if (TextUtils.isEmpty(mPartnersFName)) {
            edtPartnerFN.setError("Provide Partner's First Name");
            firstErrorView = edtPartnerFN;
            isError = true;
        }

        if (TextUtils.isEmpty(mPartnerLName)) {
            edtPartnerLN.setError("Provide Partner's Last Name");
            if (firstErrorView == null) firstErrorView = edtPartnerLN;
            isError = true;
        }

        if (TextUtils.isEmpty(mPartnersEmail)) {
            edtPartnersEmail.setError("Provide Partner's Email");
            if (firstErrorView == null) firstErrorView = edtPartnersEmail;
            isError = true;
        } else if (!App.isValidEmail(mPartnersEmail)) {
            edtPartnersEmail.setError("Invalid Email");
            if (firstErrorView == null) firstErrorView = edtPartnersEmail;
            isError = true;
        }

        return isError;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mFirstName = args.getString(EXTRA_FIRST_NAME);
            mLastName = args.getString(EXTRA_LAST_NAME);
            mEmail = args.getString(EXTRA_EMAIL);
            mPass = args.getString(EXTRA_PASS);
            mZip = args.getString(EXTRA_ZIP);
            mDomainId = args.getString(EXTRA_DOMAIN_ID);
            mState = args.getString(EXTRA_STATE);
            mCity = args.getString(EXTRA_CITY);
        } else {
            throw new RuntimeException("missing arguments");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_register_step2, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());

        edtMobile = (EditText) v.findViewById(R.id.edtMobile);
        birthRange = (EditText) v.findViewById(R.id.birthRange);
        edtReferralMethod = (EditText) v.findViewById(R.id.edtHowFindUs);
        spnrPreferredRating = (Spinner) v.findViewById(R.id.spnrPreferredLevel);
        spnrUstaLevel = (Spinner) v.findViewById(R.id.spnrUstaLevel);
        spnrJoinDoubles = (Spinner) v.findViewById(R.id.spnrJoinDoubles);
        llPartnersSkillLevel = (LinearLayout) v.findViewById(R.id.linearLayoutPartnersPreferredLevel);
        llHavePartner = (LinearLayout) v.findViewById(R.id.linearLayoutHavePartner);
        spnrPartnersPreferredRating = (Spinner) v.findViewById(R.id.spnrPartnersPreferredLevel);
        edtPartnerFN = (EditText) v.findViewById(R.id.edtPartnersFN);
        edtPartnerLN = (EditText) v.findViewById(R.id.edtPartnersLName);
        edtPartnersEmail = (EditText) v.findViewById(R.id.edtPartnersEmail);

        llPartnersSkillLevel.setVisibility(View.GONE);
        llHavePartner.setVisibility(View.GONE);

//        edtMobile.setFilters(new InputFilter[]{new PartialRegexInputFilter()});
        edtMobile.addTextChangedListener(new PhoneNumberFormattingTextWatcher(Locale.US.getCountry()));
        //edtMobile.setText(App.getMobileNumber(mContext));
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            edtMobile.setText(App.getMobileNumber(mContext)+"");
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }

        ArrayAdapter<UstaRank> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mUstaRannks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<JoiningDoubles> adapter2 = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mJoinDoubles);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnrUstaLevel.setAdapter(adapter);
        spnrJoinDoubles.setAdapter(adapter2);
        spnrJoinDoubles.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        //Object item = parent.getItemAtPosition(pos);
                        displayJoiningDoublesView(pos);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        v.findViewById(R.id.btnSubmit).setOnClickListener(SubmitCreateAccountClickListener);

        mCheckBox = (CheckBox) v.findViewById(R.id.chk_termsNConditions);
        txtTermsNCond = (TextView) v.findViewById(R.id.txtTermsNCond);
        linkTermsNCond = (TextView) v.findViewById(R.id.linkTerms);
        txtTermsNCond.setText("I agree to the ");

        linkTermsNCond.setPaintFlags(linkTermsNCond.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        linkTermsNCond.setText("Terms of Service");
        linkTermsNCond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlTerms = "https://www.tennisnortheast.com/info/terms_service";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlTerms));
                startActivity(intent);
            }
        });

        birthRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogWithoutDateField().show();
            }
        });
        return v;
    }

    private YearMonthPickerDialog createDialogWithoutDateField() {

        YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(FragRegisterStep2.this.getContext(), new YearMonthPickerDialog.OnDateSetListener() {
            @Override
            public void onYearMonthSet(int year, int month) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM - yyyy");

                birthRange.setText(dateFormat.format(calendar.getTime()));

                calendar.set(Calendar.DATE, 01);


                SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                birth_range_string = serverDateFormat.format(calendar.getTime());

            }

            @Override
            public void onCancel() {

            }
        });
        return yearMonthPickerDialog;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (App.isNetworkOnline(mContext)) {
            //new GetPreferredLevelAsyncTask().execute();
            mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...", true, false);
            WSHandle.Login.getSkillLevels(new VolleyHelper.IRequestListener<List<SkillLevel>, String>() {
                @Override
                public void onFailureResponse(String response) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), "Error fetching skill levels.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessResponse(List<SkillLevel> response) {
                    SkillLevel defaultSkillLevel = new SkillLevel();
                    defaultSkillLevel.display_name = "Please select Skill Level";
                    defaultSkillLevel.id = 0;

                    response.add(0, defaultSkillLevel);

                    mSkillLevelAdapter = new CustomSpinnerWithErrorAdapter<>(mContext, response);
                    mSkillLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spnrPreferredRating.setAdapter(mSkillLevelAdapter);
                    mProgressDialog.dismiss();
                }

                @Override
                public void onError(VolleyError error) {
                    mProgressDialog.dismiss();
                    Toast.makeText(mContext, "Network error occurred", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Internet not working?").show(getChildFragmentManager(), "dlg-frg");
        }
    }

    @Override
    public void onDetach() {
        mContext = null;
        super.onDetach();
    }

    private void displayJoiningDoublesView(int pos) {
        switch (pos) {
            case 0:
                llPartnersSkillLevel.setVisibility(View.GONE);
                llHavePartner.setVisibility(View.GONE);
                break;
            case 1:
                llPartnersSkillLevel.setVisibility(View.VISIBLE);
                llHavePartner.setVisibility(View.VISIBLE);
                getPartnersSkillLevel();
                break;
            case 2:
                llPartnersSkillLevel.setVisibility(View.VISIBLE);
                llHavePartner.setVisibility(View.GONE);
                getPartnersSkillLevel();
                break;
        }
    }

    private void getPartnersSkillLevel() {

        if (App.isNetworkOnline(mContext)) {
            mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...", true, false);
            WSHandle.Login.getPartnersSkillLevels(new VolleyHelper.IRequestListener<List<SkillLevel>, String>() {
                @Override
                public void onFailureResponse(String response) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), "Error fetching skill levels.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessResponse(List<SkillLevel> response) {
                    SkillLevel defaultSkillLevel = new SkillLevel();
                    defaultSkillLevel.display_name = "Please select Skill Level";
                    defaultSkillLevel.id = 0;

                    response.add(0, defaultSkillLevel);

                    mPartnersSkillLevelAdapter = new CustomSpinnerWithErrorAdapter<>(mContext, response);
                    mPartnersSkillLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spnrPartnersPreferredRating.setAdapter(mPartnersSkillLevelAdapter);
                    mProgressDialog.dismiss();
                }

                @Override
                public void onError(VolleyError error) {
                    mProgressDialog.dismiss();
                    Toast.makeText(mContext, "Network error occurred", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Internet not working?").show(getChildFragmentManager(), "dlg-frg");
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
//                    TelephonyManager tMgr = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
                    edtMobile.setText(App.getMobileNumber(mContext));
                }
                break;

            default:
                break;
        }
    }
    /*private class GetPreferredLevelAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            return WSHandle.Login.getSkillLevels();
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...", true, false);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            mProgressDialog.dismiss();
            if (jsonObject != null) {
                try {
                    CompitionType listType = new TypeToken<List<SkillLevel>>() {}.getType();
                    List<SkillLevel> posts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(jsonObject.getString("preferred_level_data"), listType);

                    SkillLevel defaultSkillLevel = new SkillLevel();
                    defaultSkillLevel.display_name = "Please select Skill Level";
                    defaultSkillLevel.id = 0;

                    posts.add(0, defaultSkillLevel);

                    mSkillLevelAdapter = new CustomSpinnerWithErrorAdapter<>(mContext, posts);
                    mSkillLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spnrPreferredRating.setAdapter(mSkillLevelAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "error preferred level.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }*/

    /*private class CreateAccountAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...", true, false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return WSHandle.Login.createAccount(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]);
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            mProgressDialog.dismiss();
            if (jsonObject != null) {
                try {
                    if (jsonObject.getString("responseCode").equals("200")) {
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, jsonObject.getString("responseMessage"));
                        dialog.setPositiveButton(getString(android.R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    String oauth_token = jsonObject.getString("oauth_token");
                                    String domain_name = jsonObject.getString("domain_name");
                                    //long playerId =  jsonObject.getLong("player_id");
                                    String customerSupportEmail = jsonObject.getString("customer_support_email");
                                    String customerSupportPhone = jsonObject.getString("customer_support_phone");

                                    PlayerDetail playerDetail = new GsonBuilder()
                                            .setExclusionStrategies(new GsonRealmExclusionStrategy())
                                            .create()
                                            .fromJson(jsonObject.getString("player"), new TypeToken<PlayerDetail>() {}.getType());

                                    Prefs.AppData prefs = new Prefs.AppData(mContext);
                                    prefs.setPlayer(playerDetail);
                                    prefs.setOAuthToken(oauth_token);
                                    prefs.setDomainName(domain_name);
                                    prefs.setSupportPhone(customerSupportPhone);
                                    prefs.setSupportEmail(customerSupportEmail);

                                    startActivity(SingleFragmentActivity.getIntent(mContext, null, null));
                                    getActivity().finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        dialog.show(getChildFragmentManager(), "dlg-frag");
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("responseMessage"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

}
