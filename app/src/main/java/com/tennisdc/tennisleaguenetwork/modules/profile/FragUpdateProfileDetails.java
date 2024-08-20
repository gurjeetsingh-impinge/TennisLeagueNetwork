package com.tennisdc.tennisleaguenetwork.modules.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.PartialRegexInputFilter;
import com.tennisdc.tennisleaguenetwork.model.Common;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tennisleaguenetwork.model.CourtRegion;
import com.tennisdc.tennisleaguenetwork.model.UserDetails;
import com.tennisdc.tennisleaguenetwork.model.UstaRank;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.ui.CustomSpinnerWithErrorAdapter;
import com.tennisdc.tennisleaguenetwork.ui.PhoneNumberFormattingTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 11-Jul-17.
 */

public class FragUpdateProfileDetails extends Fragment {

    @BindView(R.id.edt_Mobile)
    EditText mMobileEdt;

    @BindView(R.id.edt_street)
    EditText mStreetEdt;

    @BindView(R.id.edt_city)
    EditText mCityEdt;

    @BindView(R.id.switchBtn_playWorkDay)
    SwitchCompat mPlayWorkDay_SwitchBtn;

    @BindView(R.id.switchBtn_playOff_notifier)
    SwitchCompat mPlayoffNotifier_SwitchBtn;

    @BindView(R.id.switchBtn_ladder_notifier)
    SwitchCompat mLadderNotifier_SwitchBtn;

    @BindView(R.id.switchBtn_division_score_notifier)
    SwitchCompat mDivisionNotifier_SwitchBtn;

    @BindView(R.id.spnrState)
    Spinner mState_spnr;

    @BindView(R.id.spnrPreferredHomeCourt)
    Spinner mPreferredHomeCourt_Spnr;

    @BindView(R.id.spnrAgeRange)
    Spinner mAgeRange_Spnr;

    @BindView(R.id.spnrFavPlayer)
    Spinner mFavPlayer_Spnr;

    @BindView(R.id.btnUpdate)
    Button mUpdateBtn;

    private static String USER_DETAILS = "User Details";
    private UserDetails mUser;

    private String mMobile;
    private boolean mPlayoff;
    private boolean mLadder;
    private boolean mDivisionScore;
    private boolean mPlayWorkDay ;
    private String mStreet ;
    private String mCity;
    private String mState ;
    private String mHomeCourt;
    private String mAgeRange;
    private String mFavPlayer;
    private List<Court> mHomeCourtList;

    private Common mStateArr[] = new Common[]{new Common("Please Select",0),new Common("AL",0), new Common("AK",1), new Common("AZ",2), new Common("AR",3), new Common("CA",4), new Common("CO",5),
                            new Common("CT",6), new Common("DE",7), new Common("DC",8), new Common("FL",9), new Common("GA",10), new Common("HI",11), new Common("ID",12),
                            new Common("IL",13), new Common("IN",14), new Common("IA",15), new Common("KS",16), new Common("KY",17), new Common("LA",18), new Common("ME",19),
                            new Common("MD",20), new Common("MA",21), new Common("MI",22), new Common("MN",23), new Common("MS",24), new Common("MO",25), new Common("MT",26),
                            new Common("NE",27), new Common("NV",28), new Common("NH",29), new Common("NJ",30), new Common("NM",31), new Common("NY",32), new Common("NC",33),
                            new Common("ND",34), new Common("OH",35), new Common("OK",36), new Common("OR",37), new Common("PA", 38), new Common("RI", 39), new Common("SC",40),
                            new Common("SD",41), new Common("TN",42), new Common("TX",43), new Common("UT",44), new Common("VT",45), new Common("VA",46), new Common("WA",47),
                            new Common("WV",48), new Common("WI",49), new Common("WY",50)};

    private String mFavPlayerArr[] = {"Please Select","Angelique Kerber","Roger Federer","Andy Roddick","Andy Murray","Andre Agassi","Novak Djokovic","James Blake","Boris Becker",
                                        "Marat Safin","Pete Sampras","leyton Hewitt","Richard Gasquet","David Nalbandian","Tommy Haas","John McEnroe","Bjorn Borg",
                                        "Nikolay Davydenko","Fernando Gonzalez","David Ferrer","Arthur Ashe","Stan Smith","Carlos Moya","Tim Henman","Guillermo Canas",
                                        "Juan Carlos Ferrero","Tommy Robredo","Jimmy Connors","Rod Laver","Ivan Ljubicic","Paul-Henri Mathieu","Mario Ancic","Olivier Rochus",
                                        "Igor Andreev","Thomas Johansson","Mardy Fish","Ivan Lendl","Justine Henin-Hardenne","Venus William","Serena Williams","Ana Ivanovic",
                                        "Martina Hingis","Jelena Jankovic","Amelie Mauresmo","Maria Sharapova","Lindsay Davenport","Kim Clijsters","Svetlana Kuznetsova",
                                        "Daniela Hantuchova","Steffi Graf","Martina Navratilova","Elena Dementieva","Monica Seles","Patty Schnyder","Billie Jean King" };

    private String mAgeRangeArr[] = {"Please Select","18-25","26-32","33-40","41-50","51-60","61+"};

    private CustomSpinnerWithErrorAdapter<Common> mPreferredHomeCourtAdapter;


    public static Bundle getFragArgsBundle(String mobile, boolean playoff, boolean ladder, boolean divisionScore, boolean playWorkDay,
                                            String street, String city, String state, String homeCourt, String ageRange, String favPlayer) {
        Bundle args = new Bundle();
        args.putString("Mobile", mobile);
        args.putBoolean("Playoff", playoff);
        args.putBoolean("Ladder", ladder);
        args.putBoolean("DivisionScore", divisionScore);
        args.putBoolean("PlayWorkDay", playWorkDay);
        args.putString("Street", street);
        args.putString("City", city);
        args.putString("State", state);
        args.putString("HomeCourt", homeCourt);
        args.putString("AgeRange", ageRange);
        args.putString("FavPlayer", favPlayer);

        return args;
    }

   /* public static Intent getInstance(Context context, Parcelable object) {
        FragUpdateProfileDetails f = new FragUpdateProfileDetails();
        Bundle args = new Bundle();
        args.putParcelable(USER_DETAILS, object);
        f.setArguments(args);
        return SingleFragmentActivity.getIntent(context, FragUpdateProfileDetails.class, args);
    }*/

   /* public static Bundle setBundle(UserDetails userDetails) {
    //    FragUpdateProfileDetails instance = new FragUpdateProfileDetails();
        Bundle args = new Bundle();
        args.putParcelable(USER_DETAILS, Parcels.wrap(userDetails));
      //  instance.setArguments(args);
        return args;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         Bundle args = getArguments();
        if (args != null) {
            mMobile = args.getString("Mobile");
            mPlayoff = args.getBoolean("Playoff");
            mLadder = args.getBoolean("Ladder");
            mDivisionScore = args.getBoolean("DivisionScore");
            mPlayWorkDay = args.getBoolean("PlayWorkDay");
            mStreet = args.getString("Street");
            mCity = args.getString("City");
            mState = args.getString("State");
            mHomeCourt = args.getString("HomeCourt");
            mAgeRange = args.getString("AgeRange");
            mFavPlayer = args.getString("FavPlayer");

        } else {
            throw new RuntimeException("missing arguments");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_update_details, container, false);
        ButterKnife.bind(this, view);

        ArrayAdapter<Common> stateAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mStateArr);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mState_spnr.setAdapter(stateAdapter);

        ArrayAdapter<String> favPlayerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mFavPlayerArr);
        favPlayerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFavPlayer_Spnr.setAdapter(favPlayerAdapter);

        ArrayAdapter<String> ageRangeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mAgeRangeArr);
        ageRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAgeRange_Spnr.setAdapter(ageRangeAdapter);

        mMobileEdt.setFilters(new InputFilter[]{new PartialRegexInputFilter()});
        mMobileEdt.addTextChangedListener(new PhoneNumberFormattingTextWatcher(Locale.US.getCountry()));
        mMobileEdt.setText(mMobile);
        mPlayoffNotifier_SwitchBtn.setChecked(mPlayoff);
        mLadderNotifier_SwitchBtn.setChecked(mLadder);
        mDivisionNotifier_SwitchBtn.setChecked(mDivisionScore);
        mPlayWorkDay_SwitchBtn.setChecked(mPlayWorkDay);
        mStreetEdt.setText(mStreet);
        mCityEdt.setText(mCity);

        boolean isSelected = false;

        for(int i=0;i<mStateArr.length;i++) {
            if (mStateArr[i].display_name.equals(mState)) {
                mState_spnr.setSelection(i);
                isSelected = true;
                break;
            }
        }
        if(!isSelected)
                mState_spnr.setSelection(0);

        isSelected = false;
        for(int i=0;i<mFavPlayerArr.length;i++) {
            if (mFavPlayerArr[i].equals(mFavPlayer)) {
                mFavPlayer_Spnr.setSelection(i);
                isSelected = true;
                break;
            }
        }
        if(!isSelected)
            mFavPlayer_Spnr.setSelection(0);

        isSelected = false;
        for(int i=0;i<mAgeRangeArr.length;i++) {
            if (mAgeRangeArr[i].equals(mAgeRange)) {
                mAgeRange_Spnr.setSelection(i);
                isSelected = true;
                break;
            }
        }
        if(!isSelected)
            mAgeRange_Spnr.setSelection(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Update Details");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fetchHomeCourtList();
        mUpdateBtn.setOnClickListener(UpdateClickListener);

       /* Bundle args = getArguments();
        if (args == null) throw new RuntimeException("Missing Arguments");
        mUser = Parcels.unwrap(args.getParcelable(USER_DETAILS)); */
    }

    private void fetchHomeCourtList() {
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
        WSHandle.Courts.getCourts(new VolleyHelper.IRequestListener<List<Court>, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(List<Court> response) {
                List<Common> courtList = new ArrayList<Common>();
                Common defaultCourt = new Common("Please Select",0);
                courtList.add(defaultCourt);

                for(int i=0;i<response.size();i++) {
                    String name = response.get(i).getName();
                    long id = response.get(i).getId();
                    courtList.add(new Common(name,id));
                }

                mPreferredHomeCourtAdapter = new CustomSpinnerWithErrorAdapter<>(getContext(), courtList);
                mPreferredHomeCourtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mPreferredHomeCourt_Spnr.setAdapter(mPreferredHomeCourtAdapter);

                boolean isSelected = false;
                for(int i=0;i<courtList.size();i++) {
                    if (courtList.get(i).display_name.equals(mHomeCourt)) {
                        mPreferredHomeCourt_Spnr.setSelection(i);
                        isSelected = true;
                        break;
                    }
                }
                if(!isSelected)
                    mState_spnr.setSelection(0);

                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private View.OnClickListener UpdateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isError = false;
            String mobile = mMobileEdt.getText().toString().trim();
            if (TextUtils.isEmpty(mobile)) {
                isError = true;
                mMobileEdt.setError("Provide Mobile Number");
            } else {
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                try {
                    Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(mobile, Locale.getDefault().getCountry());
                    mobile = String.valueOf(phoneNumber.getNationalNumber());
                    String numbers = mobile.replaceAll("[^0-9]", "");
                    if (numbers.length() < 10) {
                        isError = true;
                        mMobileEdt.setError("Invalid Mobile Number");
                    }
                } catch (NumberParseException e) {
                    isError = true;
                    mMobileEdt.setError("Invalid Mobile Number");
                }
            }

            if(!isError) {
                JSONObject params = new JSONObject();
                try {
                    params.put("oauth_token", App.sOAuth);
                    JSONObject player = new JSONObject();
                    player.put("primary_telephone_number", mMobileEdt.getText().toString().trim());
                    if(!TextUtils.isEmpty(mStreetEdt.getText().toString().trim()))
                        player.put("mailing_address_1", mStreetEdt.getText().toString().trim());
                    if(!TextUtils.isEmpty(mCityEdt.getText().toString().trim()))
                        player.put("city", mCityEdt.getText().toString().trim());
                    if(mState_spnr.getSelectedItemPosition()!=0)
                        player.put("state", mState_spnr.getSelectedItem().toString());
                    if(mPreferredHomeCourt_Spnr.getSelectedItemPosition()!=0) {
                        Common court = (Common) mPreferredHomeCourt_Spnr.getSelectedItem();
                        player.put("court_id", court.id);
                    }
                    if(mAgeRange_Spnr.getSelectedItemPosition()!=0)
                        player.put("age_range", mAgeRange_Spnr.getSelectedItem().toString());
                    if(mFavPlayer_Spnr.getSelectedItemPosition()!=0)
                        player.put("favorite_player", mFavPlayer_Spnr.getSelectedItem().toString());
                    player.put("daytime_play", mPlayWorkDay_SwitchBtn.isChecked());
                    player.put("playoff_notifier",mPlayoffNotifier_SwitchBtn.isChecked());
                    player.put("ladder_score_notifier",mLadderNotifier_SwitchBtn.isChecked());
                    player.put("division_score_notifier",mDivisionNotifier_SwitchBtn.isChecked());
                    params.put("player",player);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
                WSHandle.Profile.updateProfileDetails(params,new VolleyHelper.IRequestListener<String, String>() {
                    @Override
                    public void onFailureResponse(String response) {
                        progressDialog.dismiss();
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                        dialog.show(getChildFragmentManager(), "dlg-frag");
                    }

                    @Override
                    public void onSuccessResponse(String response) {
                        progressDialog.dismiss();
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                        dialog.setNeutralButton("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // getActivity().onBackPressed();SingleFragmentActivity.HomeActivity.getIntent(getActivity())
                                //    startActivityForResult(SingleFragmentActivity.HomeActivity.getIntent(getActivity()), FragProfile.UPDATE_USER_DETAILS);
                               // startActivity(SingleFragmentActivity.MyProfileActivity.getIntent(getActivity()));
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
            /*JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                JSONObject player = new JSONObject();
                if(!TextUtils.isEmpty(mMobileEdt.getText().toString().trim())) {
                    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                    try {
                        Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(mMobile, Locale.getDefault().getCountry());
                        mMobile = String.valueOf(phoneNumber.getNationalNumber());
                        String numbers = mMobile.replaceAll("[^0-9]", "");
                        if (numbers.length() < 10) {
                            //isError = true;
                            mMobileEdt.setError("Invalid Mobile Number");
                            return;
                        } else
                            player.put("primary_telephone_number", mMobileEdt.getText().toString().trim());
                    } catch (NumberParseException e) {
                      //  isError = true;
                        mMobileEdt.setError("Invalid Mobile Number");
                        return;
                    }
                }else {
                    mMobileEdt.setError("Mobile number can't be empty");
                    return;
                }*/
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_FIRST_USER) {
            /*Bundle args = getArguments();
            if (args == null) throw new RuntimeException("Missing Arguments");
            mUser = Parcels.unwrap(args.getParcelable(USER_DETAILS));*/
            startActivity(SingleFragmentActivity.MyProfileActivity.getIntent(getActivity()));
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}