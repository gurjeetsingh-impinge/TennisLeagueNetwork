package com.tennisdc.tln.modules.tournament;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.Competition;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.model.SubmittedScore;
import com.tennisdc.tln.modules.common.DlgSendMail;
import com.tennisdc.tln.modules.common.FragListSelection;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.RecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

public class FragTournamentDetails extends Fragment {

    private static final int REQ_COMPETITION = 123;

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    private Competition mCurCompetition;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_COMPETITION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                mCurCompetition = Parcels.unwrap(data.getParcelableExtra(FragListSelection.RESULT_SELECTION));
                fetchTournamentReport(mCurCompetition);
            } else {
                getActivity().onBackPressed();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_league_details, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());

        ButterKnife.bind(this, view);

        return view;
    }    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fetchCompetitionList();
    }

    public static class FragDivisionReport extends Fragment {

        private static final String EXTRA_COMPETITION = "cur_competition";
        private static final String EXTRA_LIST = "object_list";
        //private static final String EXTRA_COURTS = "courts";
        private static final String EXTRA_IS_TOURNAMENT = "is_tournament";
        /* Views */
        @BindView(R.id.recyclerView)
        RecyclerView mRecyclerView;
        /* Data */
        private List<PlayerDetail> mPlayerDetails;
        private Competition mCurCompetition;
        private PlayerDetail mCurPlayer;
        //private List<NameIdPair> mCourts;
        private boolean mIsTournament;

        public static Fragment getInstance(Competition curCompetition, List<PlayerDetail> list, boolean isTournament) {
            FragDivisionReport fragDivisionReport = new FragDivisionReport();

            Bundle args = new Bundle();
            //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
            args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curCompetition));
            args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
            args.putBoolean(EXTRA_IS_TOURNAMENT, isTournament);

            fragDivisionReport.setArguments(args);

            return fragDivisionReport;
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

                //ContactImageView.setVisibility(mPlayerDetail.id == mCurPlayer.id ? View.GONE : View.VISIBLE);
                ContactImageView.setEnabled(mPlayerDetail.id != mCurPlayer.id);
                CardViewContainer.setForeground(getResources().getDrawable(mPlayerDetail.id == mCurPlayer.id ? R.color.yellow_item_highlight : android.R.color.transparent));
                //CardViewContainer.setCardElevation(mPlayerDetail.id == mCurPlayer.id ? 3 : 8);
            }
        }        @Override
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

            mCurCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
            mPlayerDetails = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
            //mCourts = Parcels.unwrap(args.getParcelable(EXTRA_COURTS));
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



    }    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle("My Tournaments");
    }

    public static class FragSubmittedScore extends Fragment {

        private static final String EXTRA_LIST = "object_list";
        /* Views */
        @BindView(R.id.recyclerView)
        RecyclerView mRecyclerView;
        /* Data */
        private List<SubmittedScore> mSubmittedScores;

        public static FragSubmittedScore getInstance(List<SubmittedScore> list) {
            FragSubmittedScore fragSubmittedScore = new FragSubmittedScore();

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
        }        @Override
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


    }    private void fetchCompetitionList() {
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
                    fetchTournamentReport(mCurCompetition);
                } else {
                    startActivityForResult(FragListSelection.getInstance(getActivity(), Parcels.wrap(response), "Tournament Report", getString(R.string.alert_msg_select_a_competition)), REQ_COMPETITION);
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchTournamentReport(Competition competition) {
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
                                    return FragDivisionReport.getInstance(mCurCompetition, playerDetails, isTournament);

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
                                    return "Standings";

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
    }






}
