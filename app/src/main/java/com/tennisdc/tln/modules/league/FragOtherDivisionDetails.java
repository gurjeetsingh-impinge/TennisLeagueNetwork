package com.tennisdc.tln.modules.league;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.model.Competition;
import com.tennisdc.tln.model.DivisionBean;
import com.tennisdc.tln.model.NameIdPair;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.model.SubmittedScore;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.RecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

public class FragOtherDivisionDetails extends Fragment {

    private static final String EXTRA_DIVISION = "DivisionId";
    private static final String EXTRA_COMPETITION = "CurCompetition";

    private NameIdPair mDivision;
    private Competition mCompetition;

    public static Bundle buildArguments(NameIdPair division, DivisionBean competition) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(competition));
        args.putParcelable(EXTRA_DIVISION, Parcels.wrap(division));
        return args;
    }

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_league_details, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("My Leagues");
        Bundle args = getArguments();
        if (args == null) throw new RuntimeException("Missing Arguments");

        mDivision = Parcels.unwrap(args.getParcelable(EXTRA_DIVISION));
        mCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));

        fetchDivisionReport();
    }

    private void fetchDivisionReport() {
        getActivity().setTitle(mCompetition.CompetitionName);

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.alert_msg_please_wait), true, false);

        Request divisionReportRequest = WSHandle.Leagues.getDetailsRequest(App.sOAuth, mDivision.Id, new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    //final List<NameIdPair> courts = new Gson().fromJson(response.getString("courts"), new TypeToken<List<NameIdPair>>() {}.getType());
                    Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

                    final List<PlayerDetail> playerDetails = gson.fromJson(response.getString("div_details"), new TypeToken<List<PlayerDetail>>() {}.getType());
                    final List<SubmittedScore> submittedScores = gson.fromJson(response.getString("score_details"), new TypeToken<List<SubmittedScore>>() {}.getType());

                    mPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
                        @Override
                        public Fragment getItem(int position) {
                            switch (position) {
                                case 0:
                                    return FragDivisionReport.getInstance(mCompetition, playerDetails);

                                case 1:
                                    return FragSubmittedScore.getInstance(submittedScores);
                            }
                            return null;
                        }

                        @Override
                        public int getCount() {
                            return 2;
                        }

                        @Override
                        public CharSequence getPageTitle(int position) {
                            switch (position) {
                                default:
                                case 0:
                                    return mDivision.Name;

                                case 1:
                                    return "Scores";
                            }
                        }

                    });

                    mPagerSlidingTabStrip.setViewPager(mPager);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });

        VolleyHelper.getInstance(getActivity()).addToRequestQueue(divisionReportRequest);
    }

    public static class FragDivisionReport extends Fragment {

        private static final String EXTRA_COMPETITION = "cur_competition";
        private static final String EXTRA_LIST = "object_list";

        public static Fragment getInstance(Competition curCompetition, List<PlayerDetail> list) {
            FragDivisionReport fragDivisionReport = new FragDivisionReport();

            Bundle args = new Bundle();
            args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curCompetition));
            args.putParcelable(EXTRA_LIST, Parcels.wrap(list));

            fragDivisionReport.setArguments(args);

            return fragDivisionReport;
        }

        /* Views */
        @BindView(R.id.recyclerView)
        RecyclerView mRecyclerView;

        /* Data */
        private List<PlayerDetail> mPlayerDetails;

        //private Competition mCurCompetition;
        //private PlayerDetail mCurPlayer;
        //private List<NameIdPair> mCourts;
        //private boolean mCanShowContact;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_league_standings, container, false);
            App.LogFacebookEvent(getActivity(),this.getClass().getName());

            ButterKnife.bind(this, view);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Bundle args = getArguments();
            if (args == null) throw new RuntimeException("Missing Arguments");

            //mCurCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
            mPlayerDetails = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
            //mCourts = Parcels.unwrap(args.getParcelable(EXTRA_COURTS));
            //mCanShowContact = args.getBoolean(EXTRA_SHOW_CANTACT, false);

            //mCurPlayer = new Prefs.AppData(getActivity()).getPlayer();

            mRecyclerView.setAdapter(new RecyclerAdapter<PlayerDetail, PlayerDetailsViewHolder>(mPlayerDetails) {

                @Override
                public PlayerDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_league_player_details_list_item, null);
                    return new PlayerDetailsViewHolder(view);
                }

                @Override
                public void onBindViewHolder(PlayerDetailsViewHolder holder, int position) {
                    holder.bindItem(getItem(position));
                }

            });
        }

        class PlayerDetailsViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.txtPlayerName)
            TextView PlayerNameTextView;

            @BindView(R.id.txtWinLoss)
            TextView WinLossTextView;

            @BindView(R.id.txtGames)
            TextView GamesTextView;

            private PlayerDetail mPlayerDetail;

            public PlayerDetailsViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindItem(PlayerDetail playerDetail) {
                mPlayerDetail = playerDetail;
                PlayerNameTextView.setText(mPlayerDetail.Name);
                WinLossTextView.setText(mPlayerDetail.WinLoss);
                GamesTextView.setText(mPlayerDetail.Games);
            }
        }

    }

    public static class FragSubmittedScore extends Fragment {

        private static final String EXTRA_LIST = "object_list";

        public static FragSubmittedScore getInstance(List<SubmittedScore> list) {
            FragSubmittedScore fragSubmittedScore = new FragSubmittedScore();

            Bundle args = new Bundle();
            args.putParcelable(EXTRA_LIST, Parcels.wrap(list));

            fragSubmittedScore.setArguments(args);

            return fragSubmittedScore;
        }
        /* Views */
        @BindView(R.id.recyclerView)
        RecyclerView mRecyclerView;
        /* Data */
        private List<SubmittedScore> mSubmittedScores;
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_recycler_view, container, false);
            App.LogFacebookEvent(getActivity(),this.getClass().getName());

            ButterKnife.bind(this, view);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

            return view;
        }
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Bundle args = getArguments();
            if (args == null) throw new RuntimeException("Missing Arguments");

            mSubmittedScores = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
            mRecyclerView.setAdapter(new RecyclerAdapter<SubmittedScore, SubmittedScoreViewHolder>(mSubmittedScores) {

                @Override
                public SubmittedScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_submitted_score_list_item, null);
                    return new SubmittedScoreViewHolder(view);
                }

                @Override
                public void onBindViewHolder(SubmittedScoreViewHolder holder, int position) {
                    holder.bindItem(getItem(position));
                }

            });
        }
        class SubmittedScoreViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.txtMatchResult)
            TextView MatchResultTextView;

            @BindView(R.id.txtMatchScore)
            TextView MatchScoreTextView;

            @BindView(R.id.txtMatchDate)
            TextView MatchDateTextView;

            public SubmittedScoreViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindItem(SubmittedScore submittedScore) {
                MatchResultTextView.setText(submittedScore.MatchResult);
                MatchScoreTextView.setText(submittedScore.Score);
                MatchDateTextView.setText(submittedScore.MatchDate);
            }
        }
    }

}
