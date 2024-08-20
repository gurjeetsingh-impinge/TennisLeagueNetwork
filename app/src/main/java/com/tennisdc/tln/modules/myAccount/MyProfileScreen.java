package com.tennisdc.tln.modules.myAccount;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.PlayerInformationModel;
import com.tennisdc.tln.ui.RecyclerAdapter;

import butterknife.ButterKnife;
import butterknife.BindView;

public class MyProfileScreen extends AppCompatActivity {

    ImageView mImgEditProfile;
    RecyclerView mRVChampionship;
    TextView mTxtTitleToolbar;
    TextView mTxtUsername;
    TextView mTxtUserAddress;
    TextView mTxtFavPlayer;
    TextView mTxtFavShot;
    TextView mTxtFavLink;
    TextView mTxtGameDescription;
    TextView mTxtAboutMe;
    TextView mTxtMemberSince;
    TextView mTxtPlayingReason;
    TextView mTxtHomeCourt;
    TextView mTxtParticipatingCity;
    TextView mTxtRating;
    TextView mTxtRatingStatus;
    TextView mTxtYearPoint;
    TextView mTxtUTRratingMyProfile;
    TextView mTxtUTRratingTextMyProfile;
    TextView mTxtChampionShipsTextMyProfile;
    ImageView mImgBack;
    ImageView mImgUserMyProfile;
    Prefs.AppData prefs = null;
    PlayerInformationModel.UserDataBean mUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_screen);
        App.LogFacebookEvent(this,this.getClass().getName());
        prefs = new Prefs.AppData(this);
        Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();



        mTxtTitleToolbar = (TextView) findViewById(R.id.mTxtTitleToolbar);
        mTxtUsername = (TextView) findViewById(R.id.mTxtUsernameMyProfile);
        mTxtUserAddress = (TextView) findViewById(R.id.mTxtUserAddressMyProfile);
        mTxtFavPlayer = (TextView) findViewById(R.id.mTxtFavPlayerMyProfile);
        mTxtFavShot = (TextView) findViewById(R.id.mTxtFavShotMyProfile);
        mTxtFavLink = (TextView) findViewById(R.id.mTxtFavLinkMyProfile);
        mTxtGameDescription = (TextView) findViewById(R.id.mTxtGameDescriptionMyProfile);
        mTxtAboutMe = (TextView) findViewById(R.id.mTxtAboutMeMyProfile);
        mTxtMemberSince = (TextView) findViewById(R.id.mTxtMemberSinceMyProifle);
        mTxtPlayingReason = (TextView) findViewById(R.id.mTxtPlayingReasonMyProifle);
        mTxtHomeCourt = (TextView) findViewById(R.id.mTxtHomeCourtMyProifle);
        mTxtParticipatingCity = (TextView) findViewById(R.id.mTxtParticipatingCityMyProifle);
        mTxtRating = (TextView) findViewById(R.id.mTxtRatingMyProifle);
        mTxtRatingStatus = (TextView) findViewById(R.id.mTxtRatingStatusMyProifle);
        mTxtYearPoint = (TextView) findViewById(R.id.mTxtYearPointMyProifle);
        mTxtUTRratingMyProfile = (TextView) findViewById(R.id.mTxtUTRratingMyProfile);
        mTxtUTRratingTextMyProfile = (TextView) findViewById(R.id.textViewUTRrating);
        mTxtChampionShipsTextMyProfile = (TextView) findViewById(R.id.textViewChampionshipsMyProfile);
        mTxtTitleToolbar.setText(R.string.profile);
        mImgBack = (ImageView) findViewById(R.id.mImgBack);
        mImgUserMyProfile = (ImageView) findViewById(R.id.mImgUserMyProfile);
        mImgEditProfile = (ImageView) findViewById(R.id.mImgRightCorner);
        mRVChampionship = (RecyclerView) findViewById(R.id.mRVChampionshipProfile);


        if (getIntent().getStringExtra("mPlayerId").trim().isEmpty()) {
            mUserData = gson.fromJson(prefs.getUserData(), new TypeToken<PlayerInformationModel.UserDataBean>() {
            }.getType());
            mImgEditProfile.setVisibility(View.VISIBLE);
        } else {
            mUserData = gson.fromJson(prefs.getOtherUserData(), new TypeToken<PlayerInformationModel.UserDataBean>() {
            }.getType());
            mImgEditProfile.setVisibility(View.GONE);
        }

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mImgEditProfile.setImageResource(R.drawable.ic_edit_new);
        mRVChampionship.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mImgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyProfileScreen.this, EditProfileScreen.class));
            }
        });

        if (mUserData.getChampionship_list().size() <= 0) {
            mTxtChampionShipsTextMyProfile.setVisibility(View.GONE);
            mRVChampionship.setVisibility(View.GONE);
        } else {
            mTxtChampionShipsTextMyProfile.setVisibility(View.VISIBLE);
            mRVChampionship.setVisibility(View.VISIBLE);
            mRVChampionship.setAdapter(new RecyclerAdapter<PlayerInformationModel.UserDataBean.ChampionshipListBean, ChampionShipHolder>(mUserData.getChampionship_list()) {

                @Override
                public ChampionShipHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(MyProfileScreen.this).inflate(R.layout.view_single_item, null);
                    return new ChampionShipHolder(view);
                }

                @Override
                public void onBindViewHolder(ChampionShipHolder holder, int position) {
                    holder.bindItem(getItem(position));
                }

            });
        }
        setUI();
    }

    class ChampionShipHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text1)
        TextView mTxtData;

        public ChampionShipHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(PlayerInformationModel.UserDataBean.ChampionshipListBean mString) {
            mTxtData.setText(mString.getName());
        }
    }

    void setUI() {
        mTxtUsername.setText(mUserData.getUser_name());
        mTxtUserAddress.setText(mUserData.getUser_city() + ", " + mUserData.getUser_state());
        Glide.with(MyProfileScreen.this).load(mUserData.getUser_image())
                .placeholder(R.drawable.ic_profile_placeholder).into(mImgUserMyProfile);
        mTxtFavPlayer.setText(mUserData.getFav_professional_player());
        mTxtFavShot.setText(mUserData.getFav_shot());
        mTxtFavLink.setText(mUserData.getFav_link());
        mTxtGameDescription.setText(mUserData.getGame_description());
        mTxtAboutMe.setText(mUserData.getAbout_me());
        mTxtMemberSince.setText(mUserData.getMore_info_member_since());
        mTxtPlayingReason.setText(mUserData.getMore_info_playing_region());
        mTxtHomeCourt.setText(mUserData.getHome_court());
        mTxtParticipatingCity.setText(mUserData.getMore_info_participating_city());
        mTxtRating.setText(mUserData.getMore_info_tln_player_rating());
        mTxtRatingStatus.setText(mUserData.getMore_info_league_rating_status());
        mTxtYearPoint.setText(mUserData.getMore_info_player_of_the_year_point());
        if (mUserData.getUTRRating() == null || mUserData.getUTRRating().trim().isEmpty()) {
            mTxtUTRratingMyProfile.setVisibility(View.GONE);
            mTxtUTRratingTextMyProfile.setVisibility(View.GONE);
        } else {
            mTxtUTRratingMyProfile.setVisibility(View.VISIBLE);
            mTxtUTRratingTextMyProfile.setVisibility(View.VISIBLE);

            mTxtUTRratingMyProfile.setText(mUserData.getUTRRating());
        }
    }


}
