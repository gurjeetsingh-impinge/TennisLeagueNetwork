package com.tennisdc.tln.modules.myAccount;

import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.PlayerStatsModel;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.RecyclerAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.BindView;

public class MyStatsScreen extends AppCompatActivity {
        RecyclerView mRecyclerView;
    TextView mTxtTitleToolbar;
    ImageView mImgBack;
    ArrayList<PlayerStatsModel> mPlayerStatsList = new ArrayList<>();
    Prefs.AppData prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stats_screen);
        App.LogFacebookEvent(this,this.getClass().getName());
        prefs = new Prefs.AppData(this);
        mTxtTitleToolbar = (TextView)findViewById(R.id.mTxtTitleToolbar);
        mTxtTitleToolbar.setText(R.string.stats);
        mImgBack = (ImageView) findViewById(R.id.mImgBack);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

//        ArrayList<HashMap<String, String>> mStatsList = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            HashMap<String, String> mHashMap = new HashMap<>();
//            if (i % 2 == 0) {
//                mHashMap.put("title", "Title_" + i + "_Title");
//                mHashMap.put("value", "Value_" + i + "_Value");
//            } else {
//                mHashMap.put("title", "Title_" + i);
//                mHashMap.put("value", "Value_" + i);
//            }
//            mStatsList.add(mHashMap);
//        }
        if(getIntent().getStringExtra("mPlayerId").trim().isEmpty()){
            getPlayerStats(prefs.getUserID());
        }else {
            getPlayerStats(getIntent().getStringExtra("mPlayerId"));
        }
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

        public void bindItem(PlayerStatsModel mPlayerStat, int position) {
            if (position % 2 == 0) {
                mLayoutMain.setBackgroundColor(getResources().getColor(R.color.colorGreyLight));
            } else {
                mLayoutMain.setBackgroundColor(getResources().getColor(R.color.white));
            }
            mTxtData.setText(mPlayerStat.getTitle());
            mTxtMyStatsValue.setText(mPlayerStat.getValue());
        }
    }


    void getPlayerStats(String mPlayerID) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        WSHandle.Profile.getPlayerStats(mPlayerID, new VolleyHelper.IRequestListener<String, String>() {
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

                mPlayerStatsList = gson.fromJson(response,
                        new TypeToken<ArrayList<PlayerStatsModel>>() {
                        }.getType());

                mRecyclerView.setAdapter(new RecyclerAdapter<PlayerStatsModel, StatsHolder>(mPlayerStatsList) {

                    @Override
                    public StatsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(MyStatsScreen.this).inflate(R.layout.view_my_stats_item, null);
                        return new StatsHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(StatsHolder holder, int position) {
                        holder.bindItem(getItem(position), position);
                    }

                });
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MyStatsScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
