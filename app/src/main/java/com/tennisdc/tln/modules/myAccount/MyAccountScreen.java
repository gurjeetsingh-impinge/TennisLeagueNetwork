package com.tennisdc.tln.modules.myAccount;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.Constants;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.PlayerInformationModel;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountScreen extends AppCompatActivity {

    TextView mBtnCompleteProfile;
    TextView mBtnEditProfile;
    TextView mBtnPhotosProfile;
    TextView mBtnStatsProfile;
    TextView mBtnReferralsProfile;
    TextView mBtnScoreProfile;
    TextView mTxtTitleToolbar;
    ImageView mImgBack;

    TextView mTxtUsernameMyAccount;
    TextView mTxtRatingMyAccount;
    TextView mTxtLeagueReportMyAccount;
    TextView mTxtChampionshipMyAccount;
    CircleImageView mImgUserMyAccount;
    ImageView mImgChampionCupMyAccount;
    ImageView mImgPotyMyAccount;
    ImageView mImgLegacyWinMyAccount;
    Prefs.AppData prefs = null;
    String mPlayerId = "";
    public PlayerInformationModel.UserDataBean mPlayerDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_screen);
        App.LogFacebookEvent(this,this.getClass().getName());

        prefs = new Prefs.AppData(this);

//        prefs.setNotificationCount(prefs.getNotificationCount() + 1);
        Intent intent = new Intent(Constants.UPDATE_NOTIFICATION_COUNT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        mBtnCompleteProfile = (TextView) findViewById(R.id.mBtnCompleteProfile);
        mBtnEditProfile = (TextView) findViewById(R.id.mBtnEditProfile);
        mBtnPhotosProfile = (TextView) findViewById(R.id.mBtnPhotosProfile);
        mBtnStatsProfile = (TextView) findViewById(R.id.mBtnStatsProfile);
        mBtnReferralsProfile = (TextView) findViewById(R.id.mBtnReferralsProfile);
        mBtnScoreProfile = (TextView) findViewById(R.id.mBtnScoreProfile);
        mTxtTitleToolbar = (TextView) findViewById(R.id.mTxtTitleToolbar);
        mImgBack = (ImageView) findViewById(R.id.mImgBack);

        mTxtUsernameMyAccount = (TextView) findViewById(R.id.mTxtUsernameMyAccount);
        mTxtRatingMyAccount = (TextView) findViewById(R.id.mTxtRatingMyAccount);
        mTxtLeagueReportMyAccount = (TextView) findViewById(R.id.mTxtLeagueReportMyAccount);
        mTxtChampionshipMyAccount = (TextView) findViewById(R.id.mTxtChampionshipMyAccount);
        mImgUserMyAccount = (CircleImageView) findViewById(R.id.mImgUserMyAccount);
        mImgChampionCupMyAccount = (ImageView) findViewById(R.id.mImgChampionCupMyAccount);
        mImgPotyMyAccount = (ImageView) findViewById(R.id.mImgPotyMyAccount);
        mImgLegacyWinMyAccount = (ImageView) findViewById(R.id.mImgLegacyWinMyAccount);

        if (getIntent() != null && getIntent().hasExtra("mPlayerId")) {
            mPlayerId = getIntent().getStringExtra("mPlayerId");
            if(mPlayerId.equalsIgnoreCase(prefs.getUserID())) {
                mBtnEditProfile.setVisibility(View.VISIBLE);
                mBtnReferralsProfile.setVisibility(View.VISIBLE);
            }else{
                mBtnEditProfile.setVisibility(View.GONE);
                mBtnReferralsProfile.setVisibility(View.GONE);
            }
        }

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTxtTitleToolbar.setText(R.string.my_account);
        mBtnCompleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString("mPlayerId",mPlayerId);
                startActivity(new Intent(MyAccountScreen.this, MyProfileScreen.class)
                .putExtras(mBundle));
            }
        });
        mBtnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountScreen.this, EditProfileScreen.class));
            }
        });
        mBtnPhotosProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString("mPlayerId",mPlayerId);
                startActivity(new Intent(MyAccountScreen.this, PhotosScreen.class)
                        .putExtras(mBundle));
            }
        });
        mBtnStatsProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString("mPlayerId",mPlayerId);
                startActivity(new Intent(MyAccountScreen.this, MyStatsScreen.class)
                        .putExtras(mBundle));
            }
        });
        mBtnReferralsProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountScreen.this, MyReferralsScreen.class));
            }
        });
        mBtnScoreProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString("mPlayerId",mPlayerId);
                startActivity(new Intent(MyAccountScreen.this, MyScoreScreen.class)
                        .putExtras(mBundle));
            }
        });

//        if(prefs.getUserData().equalsIgnoreCase(""))
//        getPlayer     Details();
//        else {
//            Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();
//
//            PlayerInformationModel.UserDataBean mUserData = gson.fromJson(prefs.getUserData(), new TypeToken<PlayerInformationModel.UserDataBean>() {
//            }.getType());
//            setUI(mUserData);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPlayerDetails();
    }

    void getPlayerDetails() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        String mPlayerIdTemp = "";
        if(mPlayerId.trim().isEmpty()){
            mPlayerIdTemp = prefs.getUserID();
        }else{
            mPlayerIdTemp = mPlayerId;
        }
        WSHandle.Profile.getPlayerInformation(mPlayerIdTemp, "1", new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                dialog.show(getSupportFragmentManager(), "dlg-frag");
            }

            @Override
            public void onSuccessResponse(String response) {
                progressDialog.dismiss();
                Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

                final PlayerInformationModel.UserDataBean mPlayerDetailsTemp = gson.fromJson(response, new TypeToken<PlayerInformationModel.UserDataBean>() {
                }.getType());
                mPlayerDetails = mPlayerDetailsTemp;
                if(mPlayerId.trim().isEmpty()){
                    prefs.setUserData(response);
                }else {
                    prefs.setOtherUserData(response);
                }
                setUI(mPlayerDetails);
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MyAccountScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void setUI(PlayerInformationModel.UserDataBean mPlayerDetails) {
        mTxtUsernameMyAccount.setText(mPlayerDetails.getUser_name());
        Glide.with(MyAccountScreen.this).load(mPlayerDetails.getUser_image()).placeholder(R.drawable.ic_profile_placeholder).into(mImgUserMyAccount);
        if(mPlayerDetails.getChampion_cup_image().trim().isEmpty()) {
            mImgChampionCupMyAccount.setVisibility(View.GONE);
        }else {
            mImgChampionCupMyAccount.setVisibility(View.VISIBLE);
            Glide.with(MyAccountScreen.this).load(mPlayerDetails.getChampion_cup_image())
                    .into(mImgChampionCupMyAccount);
        }
        if(mPlayerDetails.getPoty_image().trim().isEmpty()) {
            mImgPotyMyAccount.setVisibility(View.GONE);
        }else {
            mImgPotyMyAccount.setVisibility(View.VISIBLE);
            Glide.with(MyAccountScreen.this).load(mPlayerDetails.getPoty_image())
                    .into(mImgPotyMyAccount);
        }
        if(mPlayerDetails.getLegacy_win_image().trim().isEmpty()) {
            mImgLegacyWinMyAccount.setVisibility(View.GONE);
        }else {
            mImgLegacyWinMyAccount.setVisibility(View.VISIBLE);
            Glide.with(MyAccountScreen.this).load(mPlayerDetails.getLegacy_win_image())
                    .into(mImgLegacyWinMyAccount);
        }
        if (mPlayerDetails.getCurrent_rating() == null)
            mTxtRatingMyAccount.setText("0");
        else
            mTxtRatingMyAccount.setText(mPlayerDetails.getCurrent_rating());
        mTxtLeagueReportMyAccount.setText(mPlayerDetails.getOverall_league_record());
        if (mPlayerDetails.getTotal_championship() == null)
            mTxtChampionshipMyAccount.setText("Won: " + "0");
        else
            mTxtChampionshipMyAccount.setText("Won: " + mPlayerDetails.getTotal_championship());

    }
}
