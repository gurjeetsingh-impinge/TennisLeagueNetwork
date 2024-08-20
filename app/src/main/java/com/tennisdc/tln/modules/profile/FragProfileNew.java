package com.tennisdc.tln.modules.profile;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.PartialRegexInputFilter;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.Common;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tln.model.UserDetails;
import com.tennisdc.tln.modules.DateYearPicker.YearMonthPickerDialog;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.CustomSpinnerWithErrorAdapter;
import com.tennisdc.tln.ui.PhoneNumberFormattingTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 03-Jul-17.
 */

public class FragProfileNew extends Fragment implements View.OnClickListener {

    public static String USER_DETAILS = "User Details";
    public static int UPDATE_USER_DETAILS = 111;
    @BindView(R.id.txtView_full_name)
    TextView mNameTextView;
    @BindView(R.id.txtView_Email)
    TextView mEmailTextView;
    @BindView(R.id.txtView_Mobile)
    TextView mMobileTextView;
    @BindView(R.id.txtView_Address)
    TextView mAddressTextView;
    @BindView(R.id.txtView_preferred_home_court)
    TextView mPreferredHomeCourtTextView;
    @BindView(R.id.txtView_age_range)
    TextView mAgeRangeTextView;
    @BindView(R.id.switchBtn_facebookSharing)
    SwitchCompat mFacebookSharing_SwitchBtn;
    @BindView(R.id.switchBtn_playWorkDay)
    SwitchCompat mPlayWorkDayCheckbox;
    @BindView(R.id.switchBtn_playOff_notifier)
    SwitchCompat mPlayoffNotifierSwitchBtn;
    @BindView(R.id.switchBtn_ladder_notifier)
    SwitchCompat mLadderNotifierSwitchBtn;
    @BindView(R.id.switchBtn_division_score_notifier)
    SwitchCompat mDivisionNotifierSwitchBtn;
    @BindView(R.id.txtView_favPlayer)
    TextView mFavPlayerTextView;
    @BindView(R.id.img_name_edit)
    ImageView mImgNameUpdate;
    @BindView(R.id.img_email_edit)
    ImageView mImgEmailUpdate;
    @BindView(R.id.img_password_edit)
    ImageView mImgPasswordUpdate;
    @BindView(R.id.img_mobile_edit)
    ImageView mImgMobileEdit;
    @BindView(R.id.img_mobile_cancel)
    ImageView mImgMobileCancel;
    @BindView(R.id.img_mobile_upload)
    ImageView mImgMobileUpload;
    @BindView(R.id.img_address_edit)
    ImageView mImgAddressUpdate;
    @BindView(R.id.spnrPreferredHomeCourt)
    Spinner mSpnrPreferredHomeCourt;
    @BindView(R.id.img_homeCourt_edit)
    ImageView mImgHomeCourtEdit;
    @BindView(R.id.img_homeCourt_cancel)
    ImageView mImgHomeCourtCancel;
    @BindView(R.id.img_homeCourt_upload)
    ImageView mImgHomeCourtUpload;
    @BindView(R.id.img_favPlayer_edit)
    ImageView mImgFavPlayerEdit;
    @BindView(R.id.img_favPlayer_cancel)
    ImageView mImgFavPlayerCancel;
    @BindView(R.id.img_favPlayer_upload)
    ImageView mImgFavPlayerUpload;
    @BindView(R.id.spnrFavPlayer)
    Spinner mSpnrFavPlayer;
    @BindView(R.id.edittext_Mobile)
    EditText mEditMobile;
    //    @BindView(R.id.spnrAgeRange)
//    Spinner mSpnrAgeRange;
    @BindView(R.id.img_ageRange_edit)
    ImageView mImgAgeRangeEdit;
    @BindView(R.id.img_ageRange_cancel)
    ImageView mImgAgeRangeCancel;
    @BindView(R.id.img_ageRange_upload)
    ImageView mImgAgeRangeUpload;
    private UserDetails mUser;

    //    private String mAgeRangeArr[] = {"Please Select","18-25","26-32","33-40","41-50","51-60","61+"};
    private String mFavPlayerArr[] = {"Please Select", "Angelique Kerber", "Roger Federer", "Andy Roddick", "Andy Murray", "Andre Agassi", "Novak Djokovic", "James Blake", "Boris Becker",
            "Marat Safin", "Pete Sampras", "leyton Hewitt", "Richard Gasquet", "David Nalbandian", "Tommy Haas", "John McEnroe", "Bjorn Borg",
            "Nikolay Davydenko", "Fernando Gonzalez", "David Ferrer", "Arthur Ashe", "Stan Smith", "Carlos Moya", "Tim Henman", "Guillermo Canas",
            "Juan Carlos Ferrero", "Tommy Robredo", "Jimmy Connors", "Rod Laver", "Ivan Ljubicic", "Paul-Henri Mathieu", "Mario Ancic", "Olivier Rochus",
            "Igor Andreev", "Thomas Johansson", "Mardy Fish", "Ivan Lendl", "Justine Henin-Hardenne", "Venus William", "Serena Williams", "Ana Ivanovic",
            "Martina Hingis", "Jelena Jankovic", "Amelie Mauresmo", "Maria Sharapova", "Lindsay Davenport", "Kim Clijsters", "Svetlana Kuznetsova",
            "Daniela Hantuchova", "Steffi Graf", "Martina Navratilova", "Elena Dementieva", "Monica Seles", "Patty Schnyder", "Billie Jean King"};

    private CustomSpinnerWithErrorAdapter<Common> mPreferredHomeCourtAdapter;
    private String birth_range_string = "";
    private boolean mFacebookSharing;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile_new, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());
        ButterKnife.bind(this, view);

        mImgNameUpdate.setOnClickListener(this);
        mImgPasswordUpdate.setOnClickListener(this);
        mImgEmailUpdate.setOnClickListener(this);
        mImgAddressUpdate.setOnClickListener(this);
        mImgMobileEdit.setOnClickListener(this);
        mImgMobileUpload.setOnClickListener(this);
        mImgMobileCancel.setOnClickListener(this);
        mImgHomeCourtEdit.setOnClickListener(this);
        mImgHomeCourtUpload.setOnClickListener(this);
        mImgHomeCourtCancel.setOnClickListener(this);
        mImgAgeRangeEdit.setOnClickListener(this);
        mImgAgeRangeUpload.setOnClickListener(this);
        mImgAgeRangeCancel.setOnClickListener(this);
        mImgFavPlayerEdit.setOnClickListener(this);
        mImgFavPlayerUpload.setOnClickListener(this);
        mImgFavPlayerCancel.setOnClickListener(this);
        mAgeRangeTextView.setOnClickListener(this);
        mFacebookSharing = new Prefs.AppData(getActivity()).getFacebookSharing();
        mFacebookSharing_SwitchBtn.setChecked(mFacebookSharing);
        mFacebookSharing_SwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                new Prefs.AppData(getActivity()).setFacebookSharing(b);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("My Settings");
        fetchData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchData();

//        ArrayAdapter<String> ageRangeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mAgeRangeArr);
//        ageRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpnrAgeRange.setAdapter(ageRangeAdapter);

        ArrayAdapter<String> favPlayerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mFavPlayerArr);
        favPlayerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrFavPlayer.setAdapter(favPlayerAdapter);

        mEditMobile.setFilters(new InputFilter[]{new PartialRegexInputFilter()});
        mEditMobile.addTextChangedListener(new PhoneNumberFormattingTextWatcher(Locale.US.getCountry()));
    }

    private void fetchData() {
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
        WSHandle.Profile.getUserDetails(new VolleyHelper.IRequestListener<UserDetails, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Server Error:" + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(UserDetails response) {
                /*appData.setPlayer(response);
                appData.setUpdatePlayer(false);*/
                mUser = response;
                displayData();
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayData() {
        mNameTextView.setText(mUser.getFullName());
        mEmailTextView.setText(mUser.getEmail());
        mMobileTextView.setText(mUser.getMobile());
        mPlayoffNotifierSwitchBtn.setChecked(mUser.isPlayoffNotifier());
        mLadderNotifierSwitchBtn.setChecked(mUser.isLadderScoreNotifier());
        mDivisionNotifierSwitchBtn.setChecked(mUser.isDivisionScoreNotifier());
        mPlayWorkDayCheckbox.setChecked(mUser.isDayTimePlay());
        mPreferredHomeCourtTextView.setText(mUser.getCourtName());

//        mAgeRangeTextView.setText(mUser.getAgeRange());
        if (mUser.getDOB() != null) {
            mAgeRangeTextView.setText(mUser.getDOB());
        } else {
            mAgeRangeTextView.setText(getActivity().getResources().getString(R.string.text_age_range_default));
        }
        String addr = mUser.getStreet() + " " + mUser.getCity() + " " + mUser.getState();
        mAddressTextView.setText(addr);
        mFavPlayerTextView.setText(mUser.getFavoritePlayer());

        mPlayWorkDayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                uploadPlayWorkDayData();
            }
        });

        mPlayoffNotifierSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                uploadPlayOffNotifierData();
            }
        });

        mLadderNotifierSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                uploadLadderNotifierData();
            }
        });

        mDivisionNotifierSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                uploadDivisionNotifierData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mImgNameUpdate)) {
            // startActivity(SingleFragmentActivity.MyProfileActivity.getIntent(getContext()),FragUpdateProfileName.getFragArgsBundle(mUser.getFullName()));
            //  startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfileName.class, FragUpdateProfileName.getFragArgsBundle(mUser.getFullName())));
            DlgUpdateProfileName updateName = new DlgUpdateProfileName();
            updateName.setUserName(mUser.getFullName());
            updateName.show(getFragmentManager(), "UpdateProfileName");
        } else if (v.equals(mImgEmailUpdate)) {
            //startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfileEmail.class, null));
            DlgUpdateProfileEmail updateEmail = new DlgUpdateProfileEmail();
            updateEmail.show(getFragmentManager(), "Update Email");
        } else if (v.equals(mImgPasswordUpdate)) {
            // startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfilePassword.class, null));
            DlgUpdateProfilePassword updatePassword = new DlgUpdateProfilePassword();
            updatePassword.show(getFragmentManager(), "UpdateProfilePassword");
        } else if (v.equals(mImgAddressUpdate)) {
            DlgUpdateProfileAddress dlgUpdateProfileAddress = new DlgUpdateProfileAddress();
            dlgUpdateProfileAddress.setUser(mUser);
            dlgUpdateProfileAddress.show(getFragmentManager(), "UpdateProfileAddress");
        } else if (v.equals(mImgMobileEdit)) {
            mMobileTextView.setVisibility(View.GONE);
            mImgMobileEdit.setVisibility(View.GONE);
            mEditMobile.setVisibility(View.VISIBLE);
            mImgMobileCancel.setVisibility(View.VISIBLE);
            mImgMobileUpload.setVisibility(View.VISIBLE);
            mEditMobile.setText(mMobileTextView.getText().toString().trim());
        } else if (v.equals(mImgMobileUpload)) {
            uploadMobileData();
        } else if (v.equals(mImgMobileCancel)) {
            mMobileTextView.setVisibility(View.VISIBLE);
            mImgMobileEdit.setVisibility(View.VISIBLE);
            mEditMobile.setVisibility(View.GONE);
            mImgMobileCancel.setVisibility(View.GONE);
            mImgMobileUpload.setVisibility(View.GONE);
        } else if (v.equals(mImgHomeCourtEdit)) {
            fetchHomeCourtList();
        } else if (v.equals(mImgHomeCourtUpload)) {
            uploadHomeCourtData();
        } else if (v.equals(mImgHomeCourtCancel)) {
            mImgHomeCourtEdit.setVisibility(View.VISIBLE);
            mPreferredHomeCourtTextView.setVisibility(View.VISIBLE);
            mImgHomeCourtCancel.setVisibility(View.GONE);
            mImgHomeCourtUpload.setVisibility(View.GONE);
            mSpnrPreferredHomeCourt.setVisibility(View.GONE);
        } else if (v.equals(mImgAgeRangeEdit)) {
            mImgAgeRangeEdit.setVisibility(View.GONE);
            mImgAgeRangeCancel.setVisibility(View.VISIBLE);
            mImgAgeRangeUpload.setVisibility(View.VISIBLE);

            new YearMonthPickerDialog(FragProfileNew.this.getContext(), new YearMonthPickerDialog.OnDateSetListener() {
                @Override
                public void onYearMonthSet(int year, int month) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM - yyyy");

                    mAgeRangeTextView.setText(dateFormat.format(calendar.getTime()));

                    calendar.set(Calendar.DATE, 01);


                    SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    birth_range_string = serverDateFormat.format(calendar.getTime());

                }

                @Override
                public void onCancel() {
                    mImgAgeRangeEdit.setVisibility(View.VISIBLE);
                    mImgAgeRangeCancel.setVisibility(View.GONE);
                    mImgAgeRangeUpload.setVisibility(View.GONE);
                }
            }).show();
//            mSpnrAgeRange.setVisibility(View.VISIBLE);
//
//            boolean isSelected = false;
//            for(int i=0;i<mAgeRangeArr.length;i++) {
//                if (mAgeRangeArr[i].equals(mUser.getAgeRange())) {
//                    mSpnrAgeRange.setSelection(i);
//                    isSelected = true;
//                    break;
//                }
//            }
//            if(!isSelected)
//                mSpnrAgeRange.setSelection(0);
        } else if (v.equals(mImgAgeRangeUpload)) {
            uploadAgeRangeData();
        } else if (v.equals(mImgAgeRangeCancel)) {
            mImgAgeRangeEdit.setVisibility(View.VISIBLE);
            mAgeRangeTextView.setVisibility(View.VISIBLE);
            mImgAgeRangeCancel.setVisibility(View.GONE);
            mImgAgeRangeUpload.setVisibility(View.GONE);
//            mSpnrAgeRange.setVisibility(View.GONE);
        } else if (v.equals(mImgFavPlayerEdit)) {
            mImgFavPlayerEdit.setVisibility(View.GONE);
            mFavPlayerTextView.setVisibility(View.GONE);
            mImgFavPlayerUpload.setVisibility(View.VISIBLE);
            mImgFavPlayerCancel.setVisibility(View.VISIBLE);
            mSpnrFavPlayer.setVisibility(View.VISIBLE);

            boolean isSelected = false;
            for (int i = 0; i < mFavPlayerArr.length; i++) {
                if (mFavPlayerArr[i].equals(mUser.getFavoritePlayer())) {
                    mSpnrFavPlayer.setSelection(i);
                    isSelected = true;
                    break;
                }
            }
            if (!isSelected)
                mSpnrFavPlayer.setSelection(0);

        } else if (v.equals(mImgFavPlayerUpload)) {
            uploadFavouritePlayerData();
        } else if (v.equals(mImgFavPlayerCancel)) {
            mImgFavPlayerEdit.setVisibility(View.VISIBLE);
            mFavPlayerTextView.setVisibility(View.VISIBLE);
            mImgFavPlayerUpload.setVisibility(View.GONE);
            mImgFavPlayerCancel.setVisibility(View.GONE);
            mSpnrFavPlayer.setVisibility(View.GONE);
        }
    }

    private void fetchHomeCourtList() {
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
        WSHandle.Courts.getCourtsHome(new VolleyHelper.IRequestListener<List<Court>, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                mImgHomeCourtEdit.setVisibility(View.VISIBLE);
                mPreferredHomeCourtTextView.setVisibility(View.VISIBLE);
                mImgHomeCourtCancel.setVisibility(View.GONE);
                mImgHomeCourtUpload.setVisibility(View.GONE);
                mSpnrPreferredHomeCourt.setVisibility(View.GONE);
            }

            @Override
            public void onSuccessResponse(List<Court> response) {

                mImgHomeCourtEdit.setVisibility(View.GONE);
                mPreferredHomeCourtTextView.setVisibility(View.GONE);
                mImgHomeCourtCancel.setVisibility(View.VISIBLE);
                mImgHomeCourtUpload.setVisibility(View.VISIBLE);
                mSpnrPreferredHomeCourt.setVisibility(View.VISIBLE);

                List<Common> courtList = new ArrayList<Common>();
                Common defaultCourt = new Common("Please Select", 0);
                courtList.add(defaultCourt);

                for (int i = 0; i < response.size(); i++) {
                    String name = response.get(i).getName();
                    long id = response.get(i).getId();
                    courtList.add(new Common(name, id));
                }

                mPreferredHomeCourtAdapter = new CustomSpinnerWithErrorAdapter<>(getContext(), courtList);
                mPreferredHomeCourtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpnrPreferredHomeCourt.setAdapter(mPreferredHomeCourtAdapter);

                boolean isSelected = false;
                for (int i = 0; i < courtList.size(); i++) {
                    if (courtList.get(i).display_name.equals(mUser.getCourtName())) {
                        mSpnrPreferredHomeCourt.setSelection(i);
                        isSelected = true;
                        break;
                    }
                }
                if (!isSelected)
                    mSpnrPreferredHomeCourt.setSelection(0);

                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                mImgHomeCourtEdit.setVisibility(View.VISIBLE);
                mPreferredHomeCourtTextView.setVisibility(View.VISIBLE);
                mImgHomeCourtCancel.setVisibility(View.GONE);
                mImgHomeCourtUpload.setVisibility(View.GONE);
                mSpnrPreferredHomeCourt.setVisibility(View.GONE);
            }
        });
    }

    private void uploadMobileData() {
        boolean isError = false;
        String mobile = mEditMobile.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            isError = true;
            mEditMobile.setError("Provide Mobile Number");
        } else {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(mobile, Locale.getDefault().getCountry());
                mobile = String.valueOf(phoneNumber.getNationalNumber());
                String numbers = mobile.replaceAll("[^0-9]", "");
                if (numbers.length() < 10) {
                    isError = true;
                    mEditMobile.setError("Invalid Mobile Number");
                }
            } catch (NumberParseException e) {
                isError = true;
                mEditMobile.setError("Invalid Mobile Number");
            }
        }

        if (!isError) {
            mMobileTextView.setVisibility(View.VISIBLE);
            mImgMobileEdit.setVisibility(View.VISIBLE);
            mEditMobile.setVisibility(View.GONE);
            mImgMobileCancel.setVisibility(View.GONE);
            mImgMobileUpload.setVisibility(View.GONE);
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                JSONObject player = new JSONObject();
                player.put("primary_telephone_number", mEditMobile.getText().toString().trim());
                params.put("player", player);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            uploadDataToServer(params);
        }
    }

    private void uploadHomeCourtData() {
        mImgHomeCourtEdit.setVisibility(View.VISIBLE);
        mPreferredHomeCourtTextView.setVisibility(View.VISIBLE);
        mImgHomeCourtCancel.setVisibility(View.GONE);
        mImgHomeCourtUpload.setVisibility(View.GONE);
        mSpnrPreferredHomeCourt.setVisibility(View.GONE);

        JSONObject params = new JSONObject();
        try {
            params.put("oauth_token", App.sOAuth);
            JSONObject player = new JSONObject();
            if (mSpnrPreferredHomeCourt.getSelectedItemPosition() != 0) {
                Common court = (Common) mSpnrPreferredHomeCourt.getSelectedItem();
                player.put("court_id", court.id);
            }
            params.put("player", player);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uploadDataToServer(params);
    }

    private void uploadAgeRangeData() {
        mImgAgeRangeEdit.setVisibility(View.VISIBLE);
        mAgeRangeTextView.setVisibility(View.VISIBLE);
        mImgAgeRangeCancel.setVisibility(View.GONE);
        mImgAgeRangeUpload.setVisibility(View.GONE);

        JSONObject params = new JSONObject();
        try {
            params.put("oauth_token", App.sOAuth);
            JSONObject player = new JSONObject();
            player.put("dob", birth_range_string);
            params.put("player", player);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uploadDataToServer(params);
    }

    private void uploadFavouritePlayerData() {
        mImgFavPlayerEdit.setVisibility(View.VISIBLE);
        mFavPlayerTextView.setVisibility(View.VISIBLE);
        mImgFavPlayerUpload.setVisibility(View.GONE);
        mImgFavPlayerCancel.setVisibility(View.GONE);
        mSpnrFavPlayer.setVisibility(View.GONE);

        JSONObject params = new JSONObject();
        try {
            params.put("oauth_token", App.sOAuth);
            JSONObject player = new JSONObject();
            if (mSpnrFavPlayer.getSelectedItemPosition() != 0)
                player.put("favorite_player", mSpnrFavPlayer.getSelectedItem().toString());
            params.put("player", player);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uploadDataToServer(params);
    }

    private void uploadPlayWorkDayData() {
        JSONObject params = new JSONObject();
        try {
            params.put("oauth_token", App.sOAuth);
            JSONObject player = new JSONObject();
            player.put("daytime_play", mPlayWorkDayCheckbox.isChecked());
            params.put("player", player);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uploadDataToServer(params);
    }

    private void uploadPlayOffNotifierData() {
        JSONObject params = new JSONObject();
        try {
            params.put("oauth_token", App.sOAuth);
            JSONObject player = new JSONObject();
            player.put("playoff_notifier", mPlayoffNotifierSwitchBtn.isChecked());
            params.put("player", player);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uploadDataToServer(params);
    }

    private void uploadLadderNotifierData() {
        JSONObject params = new JSONObject();
        try {
            params.put("oauth_token", App.sOAuth);
            JSONObject player = new JSONObject();
            player.put("ladder_score_notifier", mLadderNotifierSwitchBtn.isChecked());
            params.put("player", player);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uploadDataToServer(params);
    }

    private void uploadDivisionNotifierData() {
        JSONObject params = new JSONObject();
        try {
            params.put("oauth_token", App.sOAuth);
            JSONObject player = new JSONObject();
            player.put("division_score_notifier", mDivisionNotifierSwitchBtn.isChecked());
            params.put("player", player);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uploadDataToServer(params);
    }

    private void uploadDataToServer(JSONObject params) {
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
                fetchData();
                /*final BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                dialog.setNeutralButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                       fetchData();
                    }
                });
                dialog.show(getChildFragmentManager(), "dlg-frag");*/
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_USER_DETAILS) {
            if (resultCode == Activity.RESULT_OK) {
                fetchData();
            } else {
                getActivity().onBackPressed();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }
}
