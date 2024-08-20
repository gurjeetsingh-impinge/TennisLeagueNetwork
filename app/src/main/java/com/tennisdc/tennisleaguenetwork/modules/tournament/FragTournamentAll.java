package com.tennisdc.tennisleaguenetwork.modules.tournament;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tennisleaguenetwork.common.GsonRealmExclusionStrategy;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.Competition;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.SubmittedScore;
import com.tennisdc.tennisleaguenetwork.model.TournamentRound;
import com.tennisdc.tennisleaguenetwork.model.TournamentRoundDetails;
import com.tennisdc.tennisleaguenetwork.modules.common.DlgSendMail;
import com.tennisdc.tennisleaguenetwork.modules.common.FragListSelection;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.ui.RecyclerAdapter;
import com.tennisdc.tennisleaguenetwork.ui.TempAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 14-Aug-17.
 */

public class FragTournamentAll extends Fragment {

    private static final int REQ_COMPETITION = 123;

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    private static Competition mCurCompetition;
    private List<TournamentRound> mTournamentRoundList;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_COMPETITION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                mCurCompetition = Parcels.unwrap(data.getParcelableExtra(FragListSelection.RESULT_SELECTION));
                fetchTournamentListDetails(mCurCompetition);
            } else {
                getActivity().onBackPressed();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_league_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchCompetitionList();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("My Tourneys");
    }

    public static class FragDivisionReportClass extends Fragment {

        private static final String EXTRA_COMPETITION = "cur_competition";
        private static final String EXTRA_LIST = "object_list";
        private static final String EXTRA_IS_TOURNAMENT = "is_tournament";
        /* Views */
        @BindView(R.id.recyclerView_tournament)
        RecyclerView mRecyclerView;
        /* Data */
        private List<PlayerDetail> mPlayerDetails;
        private Competition mCurCompetition;
        private PlayerDetail mCurPlayer;
        private boolean mIsTournament;

        public static Fragment getInstance(Competition curCompetition, List<PlayerDetail> list, boolean isTournament) {
            FragDivisionReportClass fragDivisionReport = new FragDivisionReportClass();
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curCompetition));
            args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
            args.putBoolean(EXTRA_IS_TOURNAMENT, isTournament);
            fragDivisionReport.setArguments(args);
            return fragDivisionReport;
        }

        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_tournament_recycler_view, container, false);
            ButterKnife.bind(this, view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Bundle args = getArguments();
            if (args == null) throw new RuntimeException("Missing Arguments");

            mCurCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
            mPlayerDetails = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
            mIsTournament = args.getBoolean(EXTRA_IS_TOURNAMENT);
            mCurPlayer = new Prefs.AppData(getActivity()).getPlayer();
            mRecyclerView.setAdapter(new RecyclerAdapter<PlayerDetail, PlayerDetailsViewHolder>(mPlayerDetails) {

                @Override
                public PlayerDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_tournament_player_details_list_item, null);
                    return new PlayerDetailsViewHolder(view);
                }

                @Override
                public void onBindViewHolder(PlayerDetailsViewHolder holder, int position) {
                    holder.bindItem(getItem(position));
                }

            });
        }

        class PlayerDetailsViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.vwSeasonsRecord)
            View SeasonsRecordView;

            @BindView(R.id.card_view)
            CardView CardViewContainer;

            @BindView(R.id.txtPlayerName)
            TextView PlayerNameTextView;

            @BindView(R.id.txtSeasonRecord)
            TextView SeasonRecordTextView;

            @BindView(R.id.txtOverallRecord)
            TextView OverallRecordTextView;

            @BindView(R.id.imgContactMenu)
            ImageView ContactImageView;

            private PlayerDetail mPlayerDetail;

            public PlayerDetailsViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                if (!mIsTournament) SeasonsRecordView.setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContactImageView.isEnabled()) {
                            PopupMenu popup = new PopupMenu(getActivity(), ContactImageView);
                            MenuInflater inflater = popup.getMenuInflater();
                            Menu popupMenu = popup.getMenu();
                            inflater.inflate(R.menu.contact_popup_menu, popupMenu);

                            popupMenu.findItem(R.id.action_contact_call).setVisible(mPlayerDetail.CanCall);
                            popupMenu.findItem(R.id.action_contact_message).setVisible(mPlayerDetail.CanText);

                            popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
                                @Override
                                public void onDismiss(PopupMenu menu) {
                                    ContactImageView.setSelected(false);
                                }
                            });

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.action_contact_call: {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + mPlayerDetail.Phone));
                                            startActivity(Intent.createChooser(intent, "Call"));
                                        }
                                        return true;

                                        case R.id.action_contact_mail: {
                                            DlgSendMail.getDialogInstanceTournament(mCurCompetition, mCurPlayer, mPlayerDetail).show(getChildFragmentManager(), "dlg-mail");
                                        }
                                        return true;

                                        case R.id.action_contact_message: {
                                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mPlayerDetail.Phone));
                                            //intent.setType("vnd.android-dir/mms-sms");
                                            startActivity(Intent.createChooser(intent, "Send Message"));
                                        }
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            popup.show();
                            ContactImageView.setSelected(true);
                        }
                    }
                });
            }

            public void bindItem(PlayerDetail playerDetail) {
                mPlayerDetail = playerDetail;
                PlayerNameTextView.setText(mPlayerDetail.Name);
                SeasonRecordTextView.setText(mPlayerDetail.SeasonRecord);
                OverallRecordTextView.setText(mPlayerDetail.OverallRecord);
                ContactImageView.setEnabled(mPlayerDetail.id != mCurPlayer.id);
                CardViewContainer.setForeground(getResources().getDrawable(mPlayerDetail.id == mCurPlayer.id ? R.color.yellow_item_highlight : android.R.color.transparent));
            }
       }
    }
    //--------------------

    public static class FragTournamentRoundDetailsClass extends Fragment {

        private static final String EXTRA_COMPETITION = "cur_competition";
        private static final String EXTRA_TOURNAMENT_LIST = "tournamen_header_list";
        private static final String EXTRA_TOURNAMENT_DETAILS_LIST = "tournamen_details_list";
        private static final String EXTRA_IS_TOURNAMENT = "is_tournament";

        @BindView(R.id.expand_tournamentList)
        ExpandableListView mExpandableListView;

        private static TempAdapter mAdapter;
        private ArrayList<TournamentRound> mTournamentRoundList = new ArrayList<TournamentRound>();
        private HashMap<String, List<TournamentRoundDetails>> mTournamentRoundDetailsList = new HashMap<String, List<TournamentRoundDetails>>();

       // private Competition mCurCompetition;
       // private boolean mIsTournament;

        public static Fragment getInstance(List<TournamentRound> tournamentHeaderList) {
            FragTournamentRoundDetailsClass fragTournamentListDetails = new FragTournamentRoundDetailsClass();
            Bundle args = new Bundle();
           // args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curCompetition));
            args.putParcelable(EXTRA_TOURNAMENT_LIST, Parcels.wrap(tournamentHeaderList));
          //  args.putBoolean(EXTRA_IS_TOURNAMENT, isTournament);
            fragTournamentListDetails.setArguments(args);
            return fragTournamentListDetails;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_tournament_details, container, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Bundle args = getArguments();
            if (args == null) throw new RuntimeException("Missing Arguments");
          //  mCurCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
            mTournamentRoundList = Parcels.unwrap(args.getParcelable(EXTRA_TOURNAMENT_LIST));
            //mIsTournament = args.getBoolean(EXTRA_IS_TOURNAMENT);

            createDetailsHashmap(); //mTournamentRoundList,mTournamentRoundDetailsList);
            // Setting group indicator null for custom indicator
            mExpandableListView.setGroupIndicator(null);
            setExpandableViewListenser();

            mAdapter = new TempAdapter(getContext(), mTournamentRoundList, mTournamentRoundDetailsList, mCurCompetition ); //Sanmati
            mExpandableListView.setAdapter(mAdapter);
            openDefaultGroupView();
        }

        private void createDetailsHashmap(){ // (ArrayList<TournamentRound> list, HashMap<String, List<TournamentRoundDetails>> hashMap){
            mTournamentRoundDetailsList.clear();
            // Adding headers to list
            for (int i=0;i<mTournamentRoundList.size();i++) {
                final TournamentRound tournamentRound = mTournamentRoundList.get(i);
                mTournamentRoundDetailsList.put(tournamentRound.getTournamentTitle(), tournamentRound.getMatchesList());
                // To-do : mTournamentRoundDetailsList.put(tournamentRound.getTournamentName(), tournamentRound.getTournamentRoundDeatilsList());
            }
        }

        private void openDefaultGroupView(){
            for(int i=0;i<mTournamentRoundList.size();i++){
                if(mTournamentRoundList.get(i).isOpenDefault()) {
                    mExpandableListView.expandGroup(i);
                    break;
                }
            }
        }
        private void setExpandableViewListenser() {
            // This listener will expand one group at one time
            // You can remove this listener for expanding all groups
            mExpandableListView
                    .setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                        // Default position
                        int previousGroup = -1;
                        @Override
                        public void onGroupExpand(int groupPosition) {
                            if (groupPosition != previousGroup)
                                // Collapse the expanded group
                                mExpandableListView.collapseGroup(previousGroup);
                            previousGroup = groupPosition;
                        }
                    });

        }
    }


    //--------------------

    public static class FragSubmittedScoreClass extends Fragment {

        private static final String EXTRA_LIST = "object_list";
        /* Views */
        @BindView(R.id.recyclerView)
        RecyclerView mRecyclerView;
        /* Data */
        private List<SubmittedScore> mSubmittedScores;

        public static FragSubmittedScoreClass getInstance(List<SubmittedScore> list) {
            FragSubmittedScoreClass fragSubmittedScore = new FragSubmittedScoreClass();
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
            fragSubmittedScore.setArguments(args);
            return fragSubmittedScore;
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

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_recycler_view, container, false);
            ButterKnife.bind(this, view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
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
    }

    //-------------

    private void fetchCompetitionList() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        PlayerDetail player = new Prefs.AppData(getActivity()).getPlayer();
        WSHandle.Tournament.getCompetitionList(player.id, new VolleyHelper.IRequestListener<List<Competition>, String>() {

            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(List<Competition> response) {
                progressDialog.dismiss();
                if (response == null || response.size() <= 0) {
                    BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, getString(R.string.alert_msg_tournament_no_data_found));
                    dialog.setPositiveButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                        }
                    }).setNegativeButton("No", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().onBackPressed();
                        }
                    });
                    dialog.show(getChildFragmentManager(), "dlg-frag");
                } else if (response.size() == 1) {
                    mCurCompetition = response.get(0);
                   // fetchTournamentReport(mCurCompetition);
                    fetchTournamentListDetails(mCurCompetition);
                } else {
                    startActivityForResult(FragListSelection.getInstance(getActivity(), Parcels.wrap(response), "My Tourneys", getString(R.string.alert_msg_select_a_competition)), REQ_COMPETITION);
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchTournamentReport(final Competition competition, List<TournamentRound> tournamentRoundList) {
        getActivity().setTitle(mCurCompetition.CompetitionName);
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);
        WSHandle.Tournament.getTournamentReport(competition.CompetitionId, new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();
                    final List<PlayerDetail> playerDetails = gson.fromJson(response.getString("tournament_details"), new TypeToken<List<PlayerDetail>>() {}.getType());
                    final List<SubmittedScore> submittedScores = gson.fromJson(response.getString("score_details"), new TypeToken<List<SubmittedScore>>() {}.getType());
                    final boolean isTournament = Boolean.parseBoolean(response.getString("is_tournament"));

                    mPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
                        @Override
                        public Fragment getItem(int position) {
                            switch (position) {
                                case 0:
                                    return FragTournamentRoundDetailsClass.getInstance(mTournamentRoundList);

                                case 1:
                                    return FragDivisionReportClass.getInstance(competition, playerDetails, isTournament);

                                case 2:
                                    return FragSubmittedScoreClass.getInstance(submittedScores);
                            }
                            return null;
                        }

                        @Override
                        public int getCount() {
                            return 3;
                        }

                        @Override
                        public CharSequence getPageTitle(int position) {
                            switch (position) {
                                default:
                                case 0:
                                    return "Standings";

                                case 1:
                                    return "List of Players";

                                case 2:
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
    }

    private void fetchTournamentListDetails(final Competition competition){

        getActivity().setTitle(mCurCompetition.CompetitionName);
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        WSHandle.Tournament.getTournamentDetailsList(competition.CompetitionId, new VolleyHelper.IRequestListener<List<TournamentRound>, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(List<TournamentRound> response) {
               // Log.e("PotyResponse",response.toString());
                if (response != null && response.size() > 0) {

                   /* final ArrayList<TournamentRound> tournamentRoundList = new ArrayList<TournamentRound>();
                    try {
                        if(response.opt("round32_date")!=null){
                            TournamentRound round32_Obj = new TournamentRound();
                            round32_Obj.setTournamentName("Round 32");
                            round32_Obj.setTournamentDate(response.getString("round32_date"));
                            if(response.optJSONArray("round32")!= null) {
                                List<TournamentRoundDetails> quater_List = gson.fromJson(response.getString("round16"), new TypeToken<List<TournamentRoundDetails>>() {
                                }.getType());
                                round32_Obj.setTournamentRoundDeatilsList(quater_List);
                            } else
                                round32_Obj.setTournamentRoundDeatilsList(null);
                            tournamentRoundList.add(round32_Obj);
                        }

                        if(response.opt("roundof16_date")!=null){
                            TournamentRound round16_Obj = new TournamentRound();
                            round16_Obj.setTournamentName("Round 16");
                            round16_Obj.setTournamentDate(response.getString("roundof16_date"));
                            if(response.optJSONArray("round16")!= null) {
                                List<TournamentRoundDetails> quater_List = gson.fromJson(response.getString("round16"), new TypeToken<List<TournamentRoundDetails>>() {
                                }.getType());
                                round16_Obj.setTournamentRoundDeatilsList(quater_List);
                            } else
                                round16_Obj.setTournamentRoundDeatilsList(null);
                            tournamentRoundList.add(round16_Obj);
                        }

                        if(response.opt("quarters_date")!=null){
                            TournamentRound quater_Obj = new TournamentRound();
                            quater_Obj.setTournamentName("Quater Final");
                            quater_Obj.setTournamentDate(response.getString("quarters_date"));
                            if(response.optJSONArray("quater")!= null) {
                                List<TournamentRoundDetails> quater_List = gson.fromJson(response.getString("quater"), new TypeToken<List<TournamentRoundDetails>>() {
                                }.getType());
                                quater_Obj.setTournamentRoundDeatilsList(quater_List);
                            } else
                                quater_Obj.setTournamentRoundDeatilsList(null);
                            tournamentRoundList.add(quater_Obj);
                        }

                        if(response.opt("semi_date")!=null){
                            TournamentRound semi_Obj = new TournamentRound();
                            semi_Obj.setTournamentName("Semi Final");
                            semi_Obj.setTournamentDate(response.getString("semi_date"));
                            if(response.optJSONArray("semi")!= null) {
                                List<TournamentRoundDetails> semi_List = gson.fromJson(response.getString("semi"), new TypeToken<List<TournamentRoundDetails>>() {
                                }.getType());
                                semi_Obj.setTournamentRoundDeatilsList(semi_List);
                            } else
                                semi_Obj.setTournamentRoundDeatilsList(null);
                            tournamentRoundList.add(semi_Obj);
                        }

                        if(response.opt("final_date")!=null){
                            TournamentRound final_Obj = new TournamentRound();
                            final_Obj.setTournamentName("Final");
                            final_Obj.setTournamentDate(response.getString("final_date"));
                            if(response.optJSONArray("final")!=null) {
                                List<TournamentRoundDetails> final_List = gson.fromJson(response.getString("final"), new TypeToken<List<TournamentRoundDetails>>() {
                                }.getType());
                                final_Obj.setTournamentRoundDeatilsList(final_List);
                            } else
                                final_Obj.setTournamentRoundDeatilsList(null);
                            tournamentRoundList.add(final_Obj);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    mTournamentRoundList = response;
                    fetchTournamentReport(competition,mTournamentRoundList);

                } else {
                    final BaseDialog.SimpleDialog buyDialog = BaseDialog.SimpleDialog.getDialogInstance("Enroll Tournaments", 0, "We currently don't have you enrolled in Partner Program please go to the website to enroll today.");
                    buyDialog.setPositiveButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                            buyDialog.dismiss();
                        }
                    }).setNegativeButton("No", null);
                    buyDialog.show(getChildFragmentManager(), "buy-dlg");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }
}
