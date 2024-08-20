package com.tennisdc.tln.modules.tournament;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.model.UpcomingTournamentModel;
import com.tennisdc.tln.modules.myAccount.MyAccountScreen;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.DividerItemDecoration;
import com.tennisdc.tln.ui.RecyclerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.BindView;

public class UpcomingTournamentActivity extends AppCompatActivity {

    /* Views*/
    RecyclerView mRecyclerViewPlayers1;
    RecyclerView mRecyclerViewPlayers2;
    TextView mTxtEnrolledMessage;
    Button mBtnJoinEnroll;
    TextView mTxtEnrollMessage;
    TextView mTxtTimeleftforTournament;
    LinearLayout mLayoutEnroll;

    int mStatus = 0;
    boolean mEnrolledStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_tournament);
        App.LogFacebookEvent(this,this.getClass().getName());
        mStatus = getIntent().getIntExtra("mStatus", 0);
        mRecyclerViewPlayers1 = (RecyclerView) findViewById(R.id.recyclerViewPlayers1);
        mRecyclerViewPlayers2 = (RecyclerView) findViewById(R.id.recyclerViewPlayers2);
        mTxtEnrolledMessage = (TextView) findViewById(R.id.txtEnrolledMessageTournament);
        mBtnJoinEnroll = (Button) findViewById(R.id.btnJoinEnroll);
        mTxtEnrollMessage = (TextView) findViewById(R.id.txtEnrollMessage);
        mTxtTimeleftforTournament = (TextView) findViewById(R.id.txtTimeleftforTournament);
        mLayoutEnroll = (LinearLayout) findViewById(R.id.llEnroll);
        mRecyclerViewPlayers1.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewPlayers1.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

        mRecyclerViewPlayers2.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewPlayers2.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));
        mBtnJoinEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SingleFragmentActivity.BuyActivity.getIntent(UpcomingTournamentActivity.this));
            }        });

        fetchTournamentUpcomingList();
    }

    private void fetchTournamentUpcomingList() {

//        getActivity().setTitle(mCurCompetition.CompetitionName);
        final ProgressDialog progressDialog = ProgressDialog.show(UpcomingTournamentActivity.this, null, "Please wait...", true, false);

        WSHandle.Tournament.getTournamentPlayersForUpcoming(getIntent().getLongExtra("id", 0)/*7312*/, new VolleyHelper.IRequestListener<UpcomingTournamentModel, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(UpcomingTournamentActivity.this, response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(UpcomingTournamentModel response) {
                findTextViewTitle(response.getCompetition_name() + ": " +
                        response.getDivision_name() + ": " + response.getSkill_name());

                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setIcon(R.drawable.ic_action);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                setTitle(response.getCompetition_name()+": "+
//                        response.getDivision_name()+": "+response.getSkill_name());

                progressDialog.dismiss();
//                if(response != null){
                mEnrolledStatus = response.isEnrolled();
                if (mStatus == 1) {
                    mLayoutEnroll.setVisibility(View.GONE);
                } else {
                    if (!response.isEnrolled()) {
                        mLayoutEnroll.setVisibility(View.VISIBLE);
                    } else {
                        mLayoutEnroll.setVisibility(View.GONE);
                    }
                }
//                }
                printDifference(response.getStart_date());
                mRecyclerViewPlayers1.setAdapter(new RecyclerAdapter<UpcomingTournamentModel.PlayerStanding1Bean, ObjectViewHolder>(response.getPlayer_standing1()) {

                    @Override
                    public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(UpcomingTournamentActivity.this).inflate(R.layout.viewupcoming_tournament_selection_list_item, parent, false);
                        return new ObjectViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(ObjectViewHolder holder, int position) {
                        holder.bindData((UpcomingTournamentModel.PlayerStanding1Bean) getItem(position));
                    }

                });
                mRecyclerViewPlayers2.setAdapter(new RecyclerAdapter<UpcomingTournamentModel.PlayerStanding1Bean, ObjectViewHolder>(response.getPlayer_standing2()) {

                    @Override
                    public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(UpcomingTournamentActivity.this).inflate(R.layout.viewupcoming_tournament_selection_list_item, parent, false);
                        return new ObjectViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(ObjectViewHolder holder, int position) {
                        holder.bindData((UpcomingTournamentModel.PlayerStanding1Bean) getItem(position));
                    }

                });
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(UpcomingTournamentActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    class ObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txtPlayernameUpcomingtournament)
        TextView mTxtPlayername;
        @BindView(R.id.txtleagueRatingUpcomingtournament)
        TextView mTxtleagueRating;
        @BindView(R.id.txtleagueMatchesUpcomingtournament)
        TextView mTxtleagueMatches;


        public ObjectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void bindData(UpcomingTournamentModel.PlayerStanding1Bean item) {
            if (mEnrolledStatus) {
                mTxtPlayername.setText(Html.fromHtml("<u>" + item.getPlayer_name() + "</b>"));
                mTxtPlayername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(UpcomingTournamentActivity.this, MyAccountScreen.class)
                                .putExtra("mPlayerId", String.valueOf(item.getPlayer_id())));
                    }
                });
            } else {
                if (item.isProfile_active()) {
                    mTxtPlayername.setText(Html.fromHtml("<u>" + item.getPlayer_name() + "</b>"));
                    mTxtPlayername.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(UpcomingTournamentActivity.this, MyAccountScreen.class)
                                    .putExtra("mPlayerId", String.valueOf(item.getPlayer_id())));
                        }
                    });
                } else {
                    mTxtPlayername.setText(item.getPlayer_name());
                }
            }
            mTxtleagueRating.setText(item.getActual_league_rating());
            mTxtleagueMatches.setText(item.getLeague_matches());
        }

        @Override
        public void onClick(View v) {
        }
    }

    public void findTextViewTitle(String title) {
//			String title = "title";

        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);

        Window window = getWindow();
        View decor = window.getDecorView();

        ArrayList<View> views = new ArrayList<View>();
        decor.findViewsWithText(views, title, View.FIND_VIEWS_WITH_TEXT);

        for (View view : views) {
            Log.d("TAG", "view " + view.toString());
            if (view instanceof TextView) {
                TextView tvTitle = (TextView) view;
                tvTitle.setTextSize(15f);
                tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tvTitle.setMarqueeRepeatLimit(-1);

                // In order to start strolling, it has to be focusable and focused
                tvTitle.setFocusable(true);
                tvTitle.setFocusableInTouchMode(true);
                tvTitle.requestFocus();
            }
        }
    }

    public void printDifference(String mStartTimeTemp) {
        SimpleDateFormat mSDFDefault = new SimpleDateFormat("yyyy-MM-dd");
        Date mStartTime = null;
//        Date mCuurentTime = null;
        try {
            mStartTime = mSDFDefault.parse(mStartTimeTemp.split("T")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//
        // Creates two calendars instances
        Calendar calStartTime = Calendar.getInstance();
        if (mStartTime != null)
            calStartTime.setTime(mStartTime);

        Calendar calCurrentTime = Calendar.getInstance();


        // Get the represented date in milliseconds
        long millis2 = calStartTime.getTimeInMillis();
        long millis1 = calCurrentTime.getTimeInMillis();

        //milliseconds
        long different = millis2 - millis1;

        System.out.println("startDate : " + mStartTime.getTime());
        System.out.println("endDate : " + calCurrentTime.getTime());
        System.out.println("different : " + different);


        CountDownTimer mCountDownTimer = new
                CountDownTimer(different, 1000) {
                    @Override
                    public void onTick(long l) {
                        calculateDifference(l);
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
    }

    void calculateDifference(long different) {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;
        mTxtTimeleftforTournament.setText(elapsedDays + " days, " + elapsedHours + " hours, " + elapsedMinutes +
                " minutes, " + elapsedSeconds + " seconds");
//        System.out.printf(
//                "%d days, %d hours, %d minutes, %d seconds%n",
//                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
    }

}
