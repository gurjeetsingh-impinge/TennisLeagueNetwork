package com.tennisdc.tln.modules.myAccount;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.tennisdc.tln.model.PlayScoreModel;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.RecyclerAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.BindView;

public class MyScoreScreen extends AppCompatActivity {
    RecyclerView mRecyclerView;
    TextView mTxtTitleToolbar;
    TextView mTxtNoRecordsScores;
    LinearLayout mLayoutDataScores;
    ImageView mImgBack;
    Button mBtnShowMoreScores;
    ArrayList<PlayScoreModel> mPlayerScoreList = new ArrayList<>();
    RecyclerAdapter mAdapter = null;
    Prefs.AppData prefs = null;
    int mPageCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_score_screen);
        App.LogFacebookEvent(this,this.getClass().getName());
        prefs = new Prefs.AppData(this);
        mTxtTitleToolbar = (TextView) findViewById(R.id.mTxtTitleToolbar);
        mTxtNoRecordsScores = (TextView) findViewById(R.id.mTxtNoRecordsScores);
        mLayoutDataScores = (LinearLayout) findViewById(R.id.mLayoutDataScores);
        mTxtTitleToolbar.setText(R.string.scores);
        mImgBack = (ImageView) findViewById(R.id.mImgBack);
        mBtnShowMoreScores = (Button) findViewById(R.id.mBtnShowMoreScores);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mBtnShowMoreScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageCount = mPageCount + 1;
                if(getIntent().getStringExtra("mPlayerId").trim().isEmpty()){
                    getPlayerScore(prefs.getUserID());
                }else {
                    getPlayerScore(getIntent().getStringExtra("mPlayerId"));
                }
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

//        ArrayList<HashMap<String, String>> mStatsList = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            HashMap<String, String> mHashMap = new HashMap<>();
//            if (i % 2 == 0) {
//                mHashMap.put("date", "03/11/19");
//                mHashMap.put("opponents", "R. Kerr over S. Chagnon");
//                mHashMap.put("score", "3-4,4-3");
//            } else {
//                mHashMap.put("date", "02/15/19");
//                mHashMap.put("opponents", "S. Chagnon over J. Schumer");
//                mHashMap.put("score", "6-3,6-4");
//            }
//
//            mStatsList.add(mHashMap);
//        }
        if(getIntent().getStringExtra("mPlayerId").trim().isEmpty()){
        getPlayerScore(prefs.getUserID());
        }else {
            getPlayerScore(getIntent().getStringExtra("mPlayerId"));
        }

    }

    class ScoreHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.mTxtDateMyScore)
        TextView mTxtDate;
        @BindView(R.id.mTxtOpponentMyScore)
        TextView mTxtOpponents;
        @BindView(R.id.mTxtScoreMyScore)
        TextView mTxtScore;
        @BindView(R.id.mRlMyStats)
        RelativeLayout mLayoutMain;

        public ScoreHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(PlayScoreModel mScareData, int position) {
            if (position % 2 == 0) {
                mLayoutMain.setBackgroundColor(getResources().getColor(R.color.colorGreyLight));
            } else {
                mLayoutMain.setBackgroundColor(getResources().getColor(R.color.white));
            }
            mTxtDate.setText(mScareData.getDate());
            mTxtOpponents.setText(mScareData.getResult());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mTxtOpponents.setText(Html.fromHtml(mScareData.getResult(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                mTxtOpponents.setText(Html.fromHtml(mScareData.getResult()));
            }
            mTxtScore.setText(mScareData.getScore());
        }
    }

    void getPlayerScore(String mPlayerID) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        WSHandle.Profile.getPlayerScores(mPlayerID, mPageCount, new VolleyHelper.IRequestListener<String, String>() {
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

                ArrayList<PlayScoreModel> mPlayerScoreListTemp = gson.fromJson(response,
                        new TypeToken<ArrayList<PlayScoreModel>>() {
                        }.getType());
                int mIndex = mPlayerScoreList.size();
                mPlayerScoreList.addAll(mPlayerScoreListTemp);
//                if(mAdapter == null) {
                if(mPlayerScoreList.size()>0) {
                    mTxtNoRecordsScores.setVisibility(View.GONE);
                    mLayoutDataScores.setVisibility(View.VISIBLE);
                    mAdapter = new RecyclerAdapter<PlayScoreModel, ScoreHolder>(mPlayerScoreList) {

                        @Override
                        public ScoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(MyScoreScreen.this).inflate(R.layout.view_my_scores_item, null);
                            return new ScoreHolder(view);
                        }

                        @Override
                        public void onBindViewHolder(ScoreHolder holder, int position) {
                            holder.bindItem(getItem(position), position);
                        }

                    };
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.scrollToPosition(mIndex);
                }else{
                    mTxtNoRecordsScores.setVisibility(View.VISIBLE);
                    mLayoutDataScores.setVisibility(View.GONE);
                }
//                }else{
//                    mAdapter.up
//                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MyScoreScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}