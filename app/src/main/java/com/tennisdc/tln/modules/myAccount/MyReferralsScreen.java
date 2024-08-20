package com.tennisdc.tln.modules.myAccount;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.PlayerInformationModel;
import com.tennisdc.tln.model.PlayerReferralModel;
import com.tennisdc.tln.modules.referral.FragReferralRewards;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.RecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.ButterKnife;
import butterknife.BindView;

public class MyReferralsScreen extends AppCompatActivity {
    RecyclerView mRecyclerView;
    TextView mTxtTitleToolbar;
    ImageView mImgBack;
    TextView mTxtReferralOption1;
    TextView mTxtReferralOption2;
    Button mBtnBusinessCard;
    Button mBtnTennisFlyers;
    ArrayList<PlayerReferralModel> mPlayerReferralsList = new ArrayList<>();
    String mBusinessCardList = "";
    String mFlyerCardList = "";
    Prefs.AppData prefs = null;

    PlayerInformationModel.UserDataBean mUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_referrals_screen);
        App.LogFacebookEvent(this,this.getClass().getName());
        prefs = new Prefs.AppData(this);
        mUserData = new Gson().fromJson(prefs.getUserData(), new TypeToken<PlayerInformationModel.UserDataBean>() {
        }.getType());
        mTxtTitleToolbar = (TextView)findViewById(R.id.mTxtTitleToolbar);
        mTxtReferralOption1 = (TextView)findViewById(R.id.mTxtReferrealOption1);
        mTxtReferralOption2 = (TextView)findViewById(R.id.mTxtReferrealOption2);
        mTxtTitleToolbar.setText(R.string.referrals);
        mImgBack = (ImageView) findViewById(R.id.mImgBack);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mBtnBusinessCard = (Button) findViewById(R.id.mBtnBusinessCard);
        mBtnTennisFlyers = (Button) findViewById(R.id.mBtnTennisFlyers);

        mTxtReferralOption2.setText(Html.fromHtml(getString(R.string._2_use_the_refer_a_friend_page_and_we_ll_send_them_an_email_in_your_name)));
        mTxtReferralOption1.setText(getString(R.string._1_use_your_referral_link_in_any_emaill_to_your_teniis_friends_and_if_they_use_it_they_ll_be_markerd_as_your_referral)+
                "\n"+prefs.getDomainName()+"/?From="+mUserData.getUser_id());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        getPlayerReferrals();

        mTxtReferralOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(SingleFragmentActivity.getIntent(MyReferralsScreen.this, FragReferralRewards.class, null));
            }
        });
        mBtnBusinessCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyReferralsScreen.this, BusinessCardActivity.class)
                        .putExtra("list",mBusinessCardList));
            }
        });
        mBtnTennisFlyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyReferralsScreen.this, TennisFlyersScreen.class)
                        .putExtra("list", mFlyerCardList));
            }
        });
    }

    class StatsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.mTxtMyStats)
        TextView mTxtData;
        @BindView(R.id.mTxtMyStatsValue)
        TextView mTxtMyStatsValue;
        @BindView(R.id.mRlMyStats)
        RelativeLayout mLayoutMain;

        public StatsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(PlayerReferralModel mReferralData,PlayerReferralModel mReferralrevious, int position) {
            if (position % 2 == 0) {
                mLayoutMain.setBackgroundColor(getResources().getColor(R.color.colorGreyLight));
            } else {
                mLayoutMain.setBackgroundColor(getResources().getColor(R.color.white));
            }
           if(mReferralrevious != null){
               if(mReferralrevious.getJoined_status() != (mReferralData.getJoined_status())){
                   mTxtData.setVisibility(View.VISIBLE);
                   mTxtData.setText("Hasn't Joined Us");
               }else{
                   mTxtData.setVisibility(View.GONE);
               }
           }else{
               mTxtData.setVisibility(View.VISIBLE);
               mTxtData.setText("Who Joined Us");
           }
            mTxtMyStatsValue.setText(mReferralData.getName());
        }
    }


    void getPlayerReferrals() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        WSHandle.Profile.getPlayerRefferal(prefs.getUserID(), new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                dialog.show(getSupportFragmentManager(), "dlg-frag");
            }

            @Override
            public void onSuccessResponse(String response) {
                try {
                    JSONObject mJsonObject = new JSONObject(response);

                progressDialog.dismiss();
                Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

                mPlayerReferralsList = gson.fromJson(mJsonObject.getString("player_referrals"),
                        new TypeToken<ArrayList<PlayerReferralModel>>() {
                        }.getType());

                mBusinessCardList = mJsonObject.getString("business_cards");

                mFlyerCardList = mJsonObject.getString("flyer_cards");

                Collections.sort(mPlayerReferralsList, new Comparator< PlayerReferralModel >() {
                    @Override public int compare(PlayerReferralModel p1, PlayerReferralModel p2) {
                        return p2.getJoined_status()- p1.getJoined_status(); // Ascending
                    }
                });

                mRecyclerView.setAdapter(new RecyclerAdapter<PlayerReferralModel, StatsHolder>(mPlayerReferralsList) {
                    @Override
                    public StatsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(MyReferralsScreen.this).inflate(R.layout.view_my_referrals_item, null);
                        return new StatsHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(StatsHolder holder, int position) {
                        if(position > 0){
                            holder.bindItem(getItem(position),getItem(position - 1), position);
                        }else {
                            holder.bindItem(getItem(position), null, position);
                        }
                    }

                });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MyReferralsScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
