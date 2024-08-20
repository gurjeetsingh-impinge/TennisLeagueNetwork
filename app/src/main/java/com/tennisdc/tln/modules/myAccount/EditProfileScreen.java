package com.tennisdc.tln.modules.myAccount;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.soundcloud.android.crop.Crop;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.Constants;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.DialogsUtil;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.ImageUtility;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.interfaces.OnDialogButtonClickListener;
import com.tennisdc.tln.interfaces.ScalingUtilities;
import com.tennisdc.tln.interfaces.UserPicture;
import com.tennisdc.tln.interfaces.VolleyMultipartRequest;
import com.tennisdc.tln.model.PlayerInformationModel;
import com.tennisdc.tln.model.State;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSCore;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.PhoneNumberFormattingTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileScreen extends AppCompatActivity implements OnDialogButtonClickListener {
    Spinner mSpnStateEditProfile;
    Spinner mSpnPlayingReasonEditProfile;
    Spinner mSpnHomeCourtEditProfile;
    Spinner mSpnLeagueTypeEditProfile;
    Spinner mSpnFavoriteShotEditProfile;
    Spinner mSpnMonthEditProfile;
    Spinner mSpnYearEditProfile;
    Spinner mSpnFavoritePlayerEditProfile;

    SwitchCompat mSwitchPlayWorkDay;
    SwitchCompat mSwitchScoreNotification;
    SwitchCompat mSwitchPlayOffNotifier;
    SwitchCompat mSwitchLadderNotifier;
    SwitchCompat mSwitchDivisionNotifier;

    boolean mSwitchPlayWorkDayValue = false;
    boolean mSwitchScoreNotificationValue = false;
    boolean mSwitchPlayOffNotifierValue = false;
    boolean mSwitchLadderNotifierValue = false;
    boolean mSwitchDivisionNotifierValue = false;;

    TextView mTxtPhoneEditProfile;
    TextView mTxtTitleToolbar;
    ImageView mImgBack;
    ImageView mImgAddProfileImage;
    CircleImageView mImgUserEditProfile;
    Button mBtnUpdateProfile;
    EditText mEdtStreetAddress;
    EditText mEdtCityAddress;
    EditText mEdtPhoneEditProfile;
    EditText mEdtFavLink;
    EditText mEdtPlayingPrefference;
    EditText mEdtGameDescription;
    EditText mEdtAboutMe;
    private ProgressDialog mProgressDialog;

    private ArrayList<State> mStateCodeList;
    Prefs.AppData prefs = null;
    PlayerInformationModel.UserDataBean mUserData;


    ImageUtility mImgUtils;
    DialogsUtil mDialogUtils;
    int mDialogType;
    private Uri cameraUri;
    private Uri targetUri;

    String mRequestCameraPermissions;
    String mRequestSettings;
    String mGrantPermissions;
    String mCancel;
    String mGoToSettings;
    String mFileNew;
    File mFile;
    private TextView mTextDeleteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_screen);
        App.LogFacebookEvent(this,this.getClass().getName());
        prefs = new Prefs.AppData(this);
        mDialogUtils = new DialogsUtil();
        mImgUtils = new ImageUtility(this);
        mRequestCameraPermissions = getString(R.string.explanation_multiple_request);
        mRequestSettings = getString(R.string.explanation_multiple_settings);
        mGrantPermissions = getString(R.string.action_grant_permission);
        mCancel = getString(R.string.action_cancel);
        mGoToSettings = getString(R.string.action_goto_settings);
        Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

        mUserData = gson.fromJson(prefs.getUserData(), new TypeToken<PlayerInformationModel.UserDataBean>() {
        }.getType());
        initUI();
        getstateList();
        clickEvents();
        onItemSelectedEvents();
    }

    private void initUI() {
        mSpnStateEditProfile = (Spinner) findViewById(R.id.mSpnStateEditProfile);
        mSpnPlayingReasonEditProfile = (Spinner) findViewById(R.id.mSpnPlayingReasonEditProfile);
        mSpnHomeCourtEditProfile = (Spinner) findViewById(R.id.mSpnHomeCourtEditProfile);
        mSpnLeagueTypeEditProfile = (Spinner) findViewById(R.id.mSpnLeagueTypeEditProfile);
        mSpnFavoriteShotEditProfile = (Spinner) findViewById(R.id.mSpnFavoriteShotEditProfile);
        mSpnMonthEditProfile = (Spinner) findViewById(R.id.mSpnMonthEditProfile);
        mSpnYearEditProfile = (Spinner) findViewById(R.id.mSpnYearEditProfile);
        mSpnFavoritePlayerEditProfile = (Spinner) findViewById(R.id.mSpnFavoritePlayerEditProfile);

        mSwitchPlayWorkDay = (SwitchCompat) findViewById(R.id.switchBtn_playWorkDay);
        mSwitchScoreNotification = (SwitchCompat) findViewById(R.id.switchBtn_score_notification);
        mSwitchPlayOffNotifier = (SwitchCompat) findViewById(R.id.switchBtn_playOff_notifier);
        mSwitchLadderNotifier = (SwitchCompat) findViewById(R.id.switchBtn_ladder_notifier);
        mSwitchDivisionNotifier = (SwitchCompat) findViewById(R.id.switchBtn_division_score_notifier);

        mEdtStreetAddress = (EditText) findViewById(R.id.mEdtStreetAddress);
        mEdtCityAddress = (EditText) findViewById(R.id.mEdtCityAddress);
        mEdtPhoneEditProfile = (EditText) findViewById(R.id.mEdtPhoneEditProfile);
        mEdtFavLink = (EditText) findViewById(R.id.mEdtFavLink);

        mEdtPlayingPrefference = (EditText) findViewById(R.id.mEdtPlayingPrefference);
        mEdtGameDescription = (EditText) findViewById(R.id.mEdtGameDescription);
        mEdtAboutMe = (EditText) findViewById(R.id.mEdtAboutMe);

        mTxtPhoneEditProfile = (TextView) findViewById(R.id.mEdtPhoneEditProfile);
        mTxtTitleToolbar = (TextView) findViewById(R.id.mTxtTitleToolbar);
        mTxtTitleToolbar.setText(R.string.edit_profile);
        mImgUserEditProfile = (CircleImageView) findViewById(R.id.mImgUserEditProfile);
        mImgAddProfileImage = (ImageView) findViewById(R.id.mImgAddProfileImage);
        mImgBack = (ImageView) findViewById(R.id.mImgBack);
        mBtnUpdateProfile = (Button) findViewById(R.id.mBtnUpdateProfile);
        mTextDeleteData = (TextView) findViewById(R.id.text_delete_data);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mImgAddProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermissions();
            }
        });
        mBtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
        mTextDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogsUtil().openAlertDialog(EditProfileScreen.this, "I would like to confirm I would like my Data deleted from the organization.\n" +
                                "An email will be sent to the staff to delete your profile.",
                        "Yes", "No", new OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                deleteMyData();
                            }

                            @Override
                            public void onNegativeButtonClicked() {

                            }
                        });
            }
        });
        mTxtPhoneEditProfile.addTextChangedListener(new PhoneNumberFormattingTextWatcher(Locale.US.getCountry()));

        mEdtStreetAddress.setText(mUserData.getUser_street_address());
        mEdtStreetAddress.setSelection(mEdtStreetAddress.getText().length());
        mEdtCityAddress.setText(mUserData.getUser_city());
        if (mUserData.getUser_image().isEmpty()) {
            mImgAddProfileImage.setVisibility(View.VISIBLE);
        } else {
            mImgAddProfileImage.setVisibility(View.VISIBLE);
            Glide.with(EditProfileScreen.this).load(mUserData.getUser_image())
                    .placeholder(R.drawable.ic_profile_placeholder).into(mImgUserEditProfile);
        }
        mEdtPhoneEditProfile.setText(mUserData.getPhone_number());
        mEdtPlayingPrefference.setText(mUserData.getPlaying_preference_time());
        mEdtFavLink.setText(mUserData.getFav_link());


        mEdtGameDescription.setText(mUserData.getGame_description());
        mEdtAboutMe.setText(mUserData.getAbout_me());

        if (mUserData.getCourts().size() > 0) {
            if (mUserData.getCourts().get(0).get(1).equalsIgnoreCase(""))
                mUserData.getCourts().remove(0);
        }
        mSpnPlayingReasonEditProfile.setAdapter(new ListItemAdapter(mUserData.getPlaying_regions()));
        mSpnHomeCourtEditProfile.setAdapter(new ListItemAdapter(mUserData.getCourts()));
        mSpnLeagueTypeEditProfile.setAdapter(new ListItemAdapter(mUserData.getFourty_plus_leagues()));
        mSpnFavoriteShotEditProfile.setAdapter(new ListItemAdapter(mUserData.getFavourite_shorts()));
        mSpnFavoritePlayerEditProfile.setAdapter(new ListItemAdapter(mUserData.getFavorite_players()));

        for(int i = 0 ; i < mUserData.getPlaying_regions().size() ; i++){
            List<String> mData = mUserData.getPlaying_regions().get(i);
            if(mData.get(0).equalsIgnoreCase(mUserData.getMore_info_playing_region())) {
                mSpnPlayingReasonEditProfile.setSelection(i);
                break;
            }
        }
        for(int i = 0 ; i < mUserData.getCourts().size() ; i++){
            List<String> mData = mUserData.getCourts().get(i);
            if(mData.get(0).equalsIgnoreCase(mUserData.getHome_court())) {
                mSpnHomeCourtEditProfile.setSelection(i);
                break;
            }
        }
        for(int i = 0 ; i < mUserData.getFourty_plus_leagues().size() ; i++){
            List<String> mData = mUserData.getFourty_plus_leagues().get(i);
            if(mData.get(0).equalsIgnoreCase(mUserData.getFouty_plus_leage())) {
                mSpnLeagueTypeEditProfile.setSelection(i);
                break;
            }
        }
        for(int i = 0 ; i < mUserData.getFavourite_shorts().size() ; i++){
            List<String> mData = mUserData.getFavourite_shorts().get(i);
            if(mData.get(0).equalsIgnoreCase(mUserData.getFav_shot())) {
                mSpnFavoriteShotEditProfile.setSelection(i);
                break;
            }
        }
        for(int i = 0 ; i < mUserData.getFavorite_players().size() ; i++){
            List<String> mData = mUserData.getFavorite_players().get(i);
            if(mData.get(0).equalsIgnoreCase(mUserData.getFav_professional_player())) {
                mSpnFavoritePlayerEditProfile.setSelection(i);
                break;
            }
        }

        ArrayList<Integer> mYearList = new ArrayList<>();
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 1970; i--) {
            mYearList.add(i);
        }

        mSpnYearEditProfile.setAdapter(new ArrayAdapter<Integer>(EditProfileScreen.this, R.layout.view_spinner_item, mYearList));
//        mTxtMemberSince.setText(mUserData.getMore_info_member_since());
//        mTxtPlayingReason.setText(mUserData.getMore_info_playing_region());
//        mTxtHomeCourt.setText(mUserData.getHome_court());
//        mTxtParticipatingCity.setText(mUserData.getMore_info_participating_city());
//        mTxtRating.setText(mUserData.getMore_info_tln_player_rating());
//        mTxtRatingStatus.setText(mUserData.getMore_info_league_rating_status());
//        mTxtYearPoint.setText(mUserData.getMore_info_player_of_the_year_point());

        mSwitchPlayWorkDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mSwitchPlayWorkDayValue){
                    updateNotifier("daytime_play_switch");
                }
            }
        });
        mSwitchScoreNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mSwitchScoreNotificationValue){
                    updateNotifier("push_notification_switch");
                }
            }
        });
        mSwitchPlayOffNotifier.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mSwitchPlayOffNotifierValue){
                    updateNotifier("playoff_notifier_switch");
                }
            }
        });
        mSwitchLadderNotifier.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mSwitchLadderNotifierValue){
                    updateNotifier("email_notifier_switch");
                }
            }
        });
        mSwitchDivisionNotifier.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mSwitchDivisionNotifierValue){
                    updateNotifier("division_score_notifier_switch");
                }
            }
        });



        if (mUserData.isDaytime_play())
            mSwitchPlayWorkDay.setChecked(true);
        else
            mSwitchPlayWorkDay.setChecked(false);

        if (mUserData.isScore_notification())
            mSwitchScoreNotification.setChecked(true);
        else
            mSwitchScoreNotification.setChecked(false);

        if (mUserData.isPlayoff_notifier())
            mSwitchPlayOffNotifier.setChecked(true);
        else
            mSwitchPlayOffNotifier.setChecked(false);

        if (mUserData.isEmail_notifier())
            mSwitchLadderNotifier.setChecked(true);
        else
            mSwitchLadderNotifier.setChecked(false);

        if (mUserData.isDivision_score_notifier())
            mSwitchDivisionNotifier.setChecked(true);
        else
            mSwitchDivisionNotifier.setChecked(false);

        mSwitchPlayWorkDayValue = true;
        mSwitchScoreNotificationValue = true;
        mSwitchPlayOffNotifierValue = true;
        mSwitchLadderNotifierValue = true;
        mSwitchDivisionNotifierValue = true;
    }

    private void deleteMyData() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        WSHandle.Profile.deleteMyData(new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccessResponse(String message) {
                progressDialog.dismiss();
                new DialogsUtil().openAlertDialog(EditProfileScreen.this, message, "Ok", "",
                        new OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveButtonClicked() {}

                            @Override
                            public void onNegativeButtonClicked() {}
                        });
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
            }
        });
    }

    private void clickEvents() {

    }

    private void onItemSelectedEvents() {
    }

    class ListItemAdapter extends BaseAdapter {

        List<List<String>> mList;

        public ListItemAdapter(List<List<String>> mList) {
            this.mList = mList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(EditProfileScreen.this).inflate(R.layout.view_spinner_item, parent, false);
            }
            TextView mTxtData = (TextView) listItem.findViewById(R.id.mTxt);
            mTxtData.setText(mList.get(position).get(0));
            return listItem;
        }
    }

    class StateItemAdapter extends BaseAdapter {

        List<State> mList;

        public StateItemAdapter(List<State> mList) {
            this.mList = mList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(EditProfileScreen.this).inflate(R.layout.view_spinner_item, parent, false);
            }
            TextView mTxtData = (TextView) listItem.findViewById(R.id.mTxt);
            mTxtData.setText(mList.get(position).code);
            return listItem;
        }
    }

    void getstateList() {
        if (App.isNetworkOnline(EditProfileScreen.this)) {
            mProgressDialog = ProgressDialog.show(EditProfileScreen.this, null, "Please wait...", true, false);
            WSHandle.Login.getStateCodes(new VolleyHelper.IRequestListener<JSONObject, String>() {
                @Override
                public void onFailureResponse(String response) {
                    mProgressDialog.dismiss();
                    Toast.makeText(EditProfileScreen.this, response, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessResponse(JSONObject response) {
                    mProgressDialog.dismiss();
                    try {
                        mStateCodeList = new Gson().fromJson(response.getJSONArray("states").toString(),
                                new TypeToken<List<State>>() {
                                }.getType());

                        mSpnStateEditProfile.setAdapter(new StateItemAdapter(mStateCodeList));

                        if(mUserData != null && mStateCodeList.contains(mUserData.getUser_state())){
                            mSpnStateEditProfile.setSelection(mStateCodeList.indexOf(mUserData.getUser_state()));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(VolleyError error) {
                    mProgressDialog.dismiss();
                    Toast.makeText(EditProfileScreen.this, "Network error occurred", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    void updateProfile() {
        boolean mValid = true;
       /* if (mEdtStreetAddress.getText().toString().trim().isEmpty()) {
            mEdtStreetAddress.setError(getString(R.string.this_field_cannot_be_empty));
            mEdtStreetAddress.requestFocus();
            mValid = false;
        } else if (mEdtCityAddress.getText().toString().trim().isEmpty()) {
            mEdtCityAddress.setError(getString(R.string.this_field_cannot_be_empty));
            mEdtCityAddress.requestFocus();
            mValid = false;
        } else */if (mEdtPhoneEditProfile.getText().toString().trim().isEmpty()) {
            mEdtPhoneEditProfile.setError(getString(R.string.this_field_cannot_be_empty));
            mEdtPhoneEditProfile.requestFocus();
            mValid = false;
        }/* else if (mEdtFavLink.getText().toString().trim().isEmpty()) {
            mEdtFavLink.setError(getString(R.string.this_field_cannot_be_empty));
            mEdtFavLink.requestFocus();
            mValid = false;
//        } */else if (!URLUtil.isValidUrl(mEdtFavLink.getText().toString())) {
//           mEdtFavLink.setError(getString(R.string.please_enter_valid_url));
//            mEdtFavLink.requestFocus();
//            mValid = false;
//        }/* else if (mEdtPlayingPrefference.getText().toString().trim().isEmpty()) {
           /* mEdtPlayingPrefference.setError(getString(R.string.this_field_cannot_be_empty));
            mEdtPlayingPrefference.requestFocus();
            mValid = false;*/
        } else if (mEdtGameDescription.getText().toString().trim().isEmpty()) {
            mEdtGameDescription.setError(getString(R.string.this_field_cannot_be_empty));
            mEdtGameDescription.requestFocus();
            mValid = false;
        } else if (mEdtAboutMe.getText().toString().trim().isEmpty()) {
            mEdtAboutMe.setError(getString(R.string.this_field_cannot_be_empty));
            mEdtAboutMe.requestFocus();
            mValid = false;
        }
        if (mValid) {
            if (!mEdtAboutMe.getText().toString().equals(mUserData.getAbout_me())) {
                updateAboutUs();
            }
            String mDate = "1/" + (mSpnMonthEditProfile.getSelectedItemPosition() + 1) + mSpnYearEditProfile.getSelectedItem().toString();
            final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
            /*String id,String mailing_address_1,String city,String state,String primary_telephone_number
                ,String location_region_id,String court_id, String fourty_plus_league_option,String daytime_play,String external_link,
                                               String loc_preference,String favorite_shot,String game_description,
                                               String dob,String favorite_player,*/
            WSHandle.Profile.updatePlayerProfile(String.valueOf(mUserData.getUser_id()), mEdtStreetAddress.getText().toString(), mEdtCityAddress.getText().toString(),
                    ((State) mSpnStateEditProfile.getSelectedItem()).code, mEdtPhoneEditProfile.getText().toString(),
                    ((List<String>) mSpnPlayingReasonEditProfile.getSelectedItem()).get(1),
                    ((List<String>) mSpnHomeCourtEditProfile.getSelectedItem()).get(1), ((List<String>) mSpnLeagueTypeEditProfile.getSelectedItem()).get(1),
                    mEdtPlayingPrefference.getText().toString(), mEdtFavLink.getText().toString(), ((List<String>) mSpnFavoriteShotEditProfile.getSelectedItem()).get(1),
                    mEdtGameDescription.getText().toString(), mDate, ((List<String>) mSpnFavoritePlayerEditProfile.getSelectedItem()).get(1),
                    new VolleyHelper.IRequestListener<String, String>() {
                        @Override
                        public void onFailureResponse(String response) {
                            progressDialog.dismiss();
                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                            dialog.show(getSupportFragmentManager(), "dlg-frag");
                        }

                        @Override
                        public void onSuccessResponse(String response) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileScreen.this, "Successfully Updated " + response, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(EditProfileScreen.this,
                                    MyAccountScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }

                        @Override
                        public void onError(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    void updateAboutUs() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
            /*String id,String mailing_address_1,String city,String state,String primary_telephone_number
                ,String location_region_id,String court_id, String fourty_plus_league_option,String daytime_play,String external_link,
                                               String loc_preference,String favorite_shot,String game_description,
                                               String dob,String favorite_player,*/
        WSHandle.Profile.updatePlayerStatus(mEdtAboutMe.getText().toString(),
                new VolleyHelper.IRequestListener<String, String>() {
                    @Override
                    public void onFailureResponse(String response) {
                        progressDialog.dismiss();
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                        dialog.show(getSupportFragmentManager(), "dlg-frag");
                    }

                    @Override
                    public void onSuccessResponse(String response) {
                        progressDialog.dismiss();
                        mUserData.setAbout_me(mEdtAboutMe.getText().toString());
                        Gson mGson = new Gson();
                        String mUserDataString = mGson.toJson(mUserData, PlayerInformationModel.UserDataBean.class);
                        prefs.setUserData(mUserDataString);
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /*******************************/
    void cameraPermissionsGranted() {
        cameraUri = mImgUtils.CameraGalleryIntent(this, Constants.REQUEST_CAMERA, Constants.REQUEST_GALLERY);
        targetUri = mImgUtils.getUri(this);
    }

    void getDialogType(int dialogType) {
        mDialogType = dialogType;
    }

    /**
     * Check whether user has give camera and storage permissions
     */
    void checkCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkMultiplePermissions(Constants.REQUEST_CODE_ASK_CAMERA_MULTIPLE_PERMISSIONS);
        } else {
            cameraPermissionsGranted();
        }
    }

    /**
     * Check if user has allowed application to use Location Permissions else ask for permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    void checkMultiplePermissions(int permissionCode) {
        String[] PERMISSIONS = {Constants.CAMERA_PERMISSION, Constants.READ_EXTERNAL_STORAGE_PERMISSION,
                Constants.WRITE_EXTERNAL_STORAGE_PERMISSION};
        if (!hasPermissions(EditProfileScreen.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(EditProfileScreen.this, PERMISSIONS, permissionCode);
        } else {
            cameraPermissionsGranted();
        }
    }

    /**
     * Handle result for permission grant or denial
     */

    @TargetApi(Build.VERSION_CODES.M)
    void takeActionOnPermissionChanges(int[] grantResults, OnDialogButtonClickListener onDialogButtonClickListener
            , String mRequestPermissions, String mRequsetSettings, String mGrantPermissions, String mCancel, String mGoToSettings) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            cameraPermissionsGranted();
        } else {
            boolean showRationale1 = shouldShowRequestPermissionRationale(Constants.CAMERA_PERMISSION);
            boolean showRationale2 = shouldShowRequestPermissionRationale(Constants.READ_EXTERNAL_STORAGE_PERMISSION);
            boolean showRationale3 = shouldShowRequestPermissionRationale(Constants.WRITE_EXTERNAL_STORAGE_PERMISSION);

            if (showRationale1 && showRationale2 && showRationale3) {
                //explain to user why we need the permissions
                getDialogType(Constants.DIALOG_DENY);
                mDialogUtils.openAlertDialog(EditProfileScreen.this, mRequestPermissions, mGrantPermissions, mCancel, onDialogButtonClickListener);
            } else {
                //explain to user why we need the permissions and ask him to go to settings to enable it
                getDialogType(Constants.DIALOG_NEVER_ASK);
                mDialogUtils.openAlertDialog(EditProfileScreen.this, mRequsetSettings, mGoToSettings, mCancel, onDialogButtonClickListener);
            }
        }
    }

    /**
     * check if user has permissions for the asked permissions
     */
    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * redirect user to your application settings in device
     */
    public void redirectToAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        takeActionOnPermissionChanges(grantResults, this,
                mRequestCameraPermissions, mRequestSettings, mGrantPermissions, mCancel, mGoToSettings);
    }

    /**
     * User clicked positive button on Alert Dialog
     */
    @Override
    public void onPositiveButtonClicked() {
        switch (mDialogType) {
            case Constants.DIALOG_DENY:
                checkMultiplePermissions(Constants.REQUEST_CODE_ASK_CAMERA_MULTIPLE_PERMISSIONS);
                break;
            case Constants.DIALOG_NEVER_ASK:
                redirectToAppSettings();
                break;
        }
    }

    /**
     * User clicked positive button on Alert Dialog
     * so close the activity and prevent user from opening camera
     */
    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CAMERA) {
            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), targetUri);
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                Drawable color = new ColorDrawable(getResources().getColor(android.R.color.transparent));
                BitmapDrawable imageProfileBitmap = new BitmapDrawable(getResources(), imageBitmap);
                if (imageProfileBitmap != null) {
                    uploadprofilePicture(imageProfileBitmap);
                }
//                new RotateImage().execute(cameraUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mImgAddProfileImage.setVisibility(View.VISIBLE);
            mImgUserEditProfile.setImageBitmap((Bitmap) data.getExtras().get("data"));
            /*Glide.with(this)
                    .load("file://" + cameraUri.getPath())
                    .into(mImgUserEditProfile);*/
//            Crop.of(cameraUri, targetUri).withAspect(80, 80).start(EditProfileScreen.this);
        } else if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_GALLERY) {
            if (data != null) {
                try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), targetUri);
                    new RotateImage().execute(data.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mImgAddProfileImage.setVisibility(View.VISIBLE);

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(data.getData(),filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                mImgUserEditProfile.setImageBitmap(BitmapFactory.decodeFile(picturePath));
               /* Glide.with(this)
                        .load("file://" + data.getData())
                        .into(mImgUserEditProfile);*/
//                Crop.of(data.getData(), targetUri).withAspect(80, 80).start(EditProfileScreen.this);
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {

            String image = mImgUtils.compressImage(targetUri.getPath(), this);


            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), targetUri);
                new RotateImage().execute(targetUri);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Glide.with(this)
                    .load("file://" + targetUri.getPath())
                    .into(mImgUserEditProfile);
//            uploading image to server

        }

    }

    void uploadprofilePicture(final BitmapDrawable imageProfileBitmap) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        VolleyMultipartRequest mMultiPart = new VolleyMultipartRequest(Request.Method.POST, WSCore._URL + "/" + "update_profile_image.json", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse networkResponse) {
                Log.e("result", new String(networkResponse.data));
                String message = "";

                try {
                    JSONObject jsonObject = new JSONObject(new String(networkResponse.data));
                    if (jsonObject.getString("responseCode").equals("200")) {
                        Toast.makeText(EditProfileScreen.this, "Profile Pic uploaded successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Something went wrong");
                        dialog.show(getSupportFragmentManager(), "dlg-frag");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String message = null;
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> params = new HashMap<>();
                params.put("oauth_token", App.sOAuth);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                if (imageProfileBitmap != null) {
                    params.put("picture", new DataPart("file_image1" + Calendar.getInstance().getTimeInMillis() + ".png", getFileDataFromDrawable(EditProfileScreen.this,
                            imageProfileBitmap), "image/png"));
                }
                return params;

            }
        };
        mMultiPart.setRetryPolicy(new DefaultRetryPolicy(120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyHelper.getInstance(this).addToRequestQueue(mMultiPart);
    }

    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    class RotateImage extends AsyncTask<Uri, String, String> {
        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Uri... params) {
//            mFileNew = savebitmap(params[0], "profile");
            mFileNew = new ImageUtility(EditProfileScreen.this).getRealPathFromURI(EditProfileScreen.this,params[0]);
            return mFileNew;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(String file) {
            super.onPostExecute(file);
            if (file != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
                Drawable color = new ColorDrawable(getResources().getColor(android.R.color.transparent));
                BitmapDrawable imageProfileBitmap = new BitmapDrawable(getResources(), bitmap);
                LayerDrawable ldrawable = new LayerDrawable(new Drawable[]{color, imageProfileBitmap});
                if (imageProfileBitmap != null) {
                    uploadprofilePicture(imageProfileBitmap);
                }
               /* img_profilepic.setImageDrawable(ldrawable);
                Log.e("responceDrawablePic", "" + roundedImage);*/

            }
        }
    }

    private String savebitmap(Uri path, String filename) {
//        String dir = Environment.getExternalStorageDirectory().getPath() + File.separator + "TLN";
        String dir = getExternalFilesDir("TLN").getPath();
        File file_dir = new File(dir);
        if (!file_dir.exists()){
            boolean isCreated = file_dir.mkdirs();
        Log.e("TAG", "savebitmap: isCreated" + isCreated);
        }else {
            Log.e("TAG", "Already: isCreated" );
        }
        FileOutputStream outStream = null;
        mFile = new File(dir, filename + ".jpg");
        if (mFile.exists()) {
            mFile.delete();
            mFile = new File(dir, filename + ".jpg");
            Log.e("file exist", "" + mFile + ",Bitmap= " + filename);
        }/*else{
            mFile = new File(dir, filename + ".jpg");
        }*/
        try {
            Bitmap p_image = ScalingUtilities.createScaledBitmap(new UserPicture(path, getContentResolver()).getBitmap(), 500, 500, ScalingUtilities.ScalingLogic.CROP);
            outStream = new FileOutputStream(mFile);
            p_image.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("path", "" + path);
        Log.e("file", "" + mFile);
        return mFile.toString();
    }

    private void updateNotifier(String mValue){
        final ProgressDialog progressDialog = ProgressDialog.show(EditProfileScreen.this, null, "Please wait...");

        WSHandle.Profile.updateNotifierStatus(mValue,  new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                dialog.show(getSupportFragmentManager(), "dlg-frag");
            }

            @Override
            public void onSuccessResponse(String response) {
                progressDialog.dismiss();
                mUserData.setAbout_me(mEdtAboutMe.getText().toString());
                Gson mGson = new Gson();
                String mUserDataString = mGson.toJson(mUserData, PlayerInformationModel.UserDataBean.class);
                prefs.setUserData(mUserDataString);
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(EditProfileScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                hideSoftKeyboard(getWindow().getDecorView().getRootView());

        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) EditProfileScreen.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
