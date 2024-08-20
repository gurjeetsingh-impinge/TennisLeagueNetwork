package com.tennisdc.tennisleaguenetwork.modules.league;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
//import androidx.content.ContextCompat;
//import androidx.fragment.view.ViewPager;
//import android.support.v7.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.GsonRealmExclusionStrategy;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.Competition;
import com.tennisdc.tennisleaguenetwork.model.NameIdPair;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.SubmittedScore;
import com.tennisdc.tennisleaguenetwork.modules.common.DialogClickListener;
import com.tennisdc.tennisleaguenetwork.modules.common.DlgSendMail;
import com.tennisdc.tennisleaguenetwork.modules.common.FragListSelection;
import com.tennisdc.tennisleaguenetwork.modules.partner.FragPartnerProgramDetails;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.ui.DividerItemDecoration;
import com.tennisdc.tennisleaguenetwork.ui.RecyclerAdapter;
import com.tennisdc.tln.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.BindView;
import hirondelle.date4j.DateTime;

public class FragLeagueDetails extends Fragment implements DialogClickListener {

    private static final int REQ_COMPETITION = 123;

    public static FragLeagueDetails sFragLeagueDetails;

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    private Competition mCurCompetition;
    private View view;

    public static int countSelection = 0;
    public static boolean isMultiSelect = false;
    private List<PlayerDetail> mPlayerList = new ArrayList<>();
    public static PlayerDetail mCurPlayer;

    FragDivisionReport fragDivisionReport;
    boolean isBottomviewVisible = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sFragLeagueDetails = this;
        countSelection = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_league_details, container, false);

        ButterKnife.bind(this, view);
        addListnerToSlider();
        addBottomView();

        return view;
    }

    private void addListnerToSlider() {
        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                fragDivisionReport.resetList();

                LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);
                hiddenPanel.setVisibility(View.INVISIBLE);
                fragDivisionReport.updateRecyclerView(true);
            }
        });
    }

    private void addBottomView() {
        LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);

        Button sendMail = (Button) hiddenPanel.findViewById(R.id.sendMail);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DlgSendMail dialog = DlgSendMail.getDialogInstanceLeagues(mCurCompetition,mCurPlayer, mPlayerList, false);
                dialog.setTargetFragment(FragLeagueDetails.this, 0);
                dialog.show(getChildFragmentManager(), "dlg-mail");

                hideBottomView();
            }
        });

        Button sendSMS = (Button) hiddenPanel.findViewById(R.id.sendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DlgSendMail dialog =  DlgSendMail.getDialogInstanceLeagues(mCurCompetition,mCurPlayer, mPlayerList, true);
                dialog.setTargetFragment(FragLeagueDetails.this, 0);
                dialog.show(getChildFragmentManager(), "dlg-sms");

                hideBottomView();
            }
        });

        Button Cancel = (Button) hiddenPanel.findViewById(R.id.cancelBtn);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomView();
                fragDivisionReport.resetList();
            }
        });
    }

    public void showBottomView() {
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_up);
        LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.VISIBLE);
        isMultiSelect = true;
        isBottomviewVisible = true;
        fragDivisionReport.updateRecyclerView(false);
    }

    public void hideBottomView() {
        Animation bottom_down = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_down);
        LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);
        hiddenPanel.startAnimation(bottom_down);
        hiddenPanel.setVisibility(View.INVISIBLE);
        isMultiSelect = false;
        isBottomviewVisible = false;
        fragDivisionReport.updateRecyclerView(true);
    }

    public void updatePlayers(PlayerDetail playerDetail) {
        if (playerDetail.isSelected) {
            if (!mPlayerList.contains(playerDetail)) {
                mPlayerList.add(playerDetail);
            }
        }
        else {
            if (mPlayerList.contains(playerDetail)) {
                mPlayerList.remove(playerDetail);
            }
        }
    }

    @Override
    public void onSendClick() {
        fragDivisionReport.resetList();
        hideBottomView();
    }
    @Override
    public void onNoClick() {
        fragDivisionReport.resetList();
        hideBottomView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("My Leagues");

        fetchCompetitionList();
    }

    private void fetchCompetitionList() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        PlayerDetail player = new Prefs.AppData(getActivity()).getPlayer();

        Request competitionListRequest = WSHandle.Leagues.getCompetitionListRequest(player.id, new VolleyHelper.IRequestListener<List<Competition>, String>() {

            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(List<Competition> response) {
                progressDialog.dismiss();
                if (response == null || response.size() <= 0) {
                    BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, "We don't have you enrolled in current or future seasons. Join today to enroll**!**");
                    dialog.setPositiveButton("Join Today!", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                        }
                    });
                    dialog.setNeutralButton("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().onBackPressed();
                        }
                    });
                    dialog.show(getChildFragmentManager(), "dlg-frag");
                } else if (response.size() == 1) {
                    mCurCompetition = response.get(0);
                    fetchDivisionReport(mCurCompetition);
                } else {
                    startActivityForResult(FragListSelection.getInstance(getActivity(), Parcels.wrap(response), "League Report", getString(R.string.alert_msg_select_a_competition)), REQ_COMPETITION);
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });

        VolleyHelper.getInstance(getActivity()).addToRequestQueue(competitionListRequest);
    }

    @Override
    public void onDestroy() {
        sFragLeagueDetails = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mCurCompetition != null)
        getActivity().setTitle(mCurCompetition.CompetitionName);
    }

    private void fetchDivisionReport(Competition competition) {
        getActivity().setTitle(mCurCompetition.CompetitionName);

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.alert_msg_please_wait), true, false);

        Request divisionReportRequest = WSHandle.Leagues.getDetailsRequest(App.sOAuth, competition.DivisionId, new VolleyHelper.IRequestListener<JSONObject, String>() {
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

                    boolean canShowContact;
                    try {
                        DateTime startDateTime = new DateTime(response.getString("start_date").split("T")[0]);
                        DateTime endInDateTime = new DateTime(response.getString("end_date").split("T")[0]);
                        DateTime nowDateTime = DateTime.now(TimeZone.getTimeZone("UTC"));

                        canShowContact = nowDateTime.gteq(startDateTime) && nowDateTime.lteq(endInDateTime.plusDays(30));
                    } catch (Exception ex) {
                        canShowContact = true;
                    }

                    final boolean finalCanShowContact = canShowContact;

                    mPager.setOffscreenPageLimit(2);

                    mPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
                        @Override
                        public Fragment getItem(int position) {
                            switch (position) {
                                case 0:
                                    fragDivisionReport  = (FragDivisionReport)FragDivisionReport.getInstance(mCurCompetition, playerDetails, finalCanShowContact);
                                    return fragDivisionReport;

                                case 1:
                                    return FragSubmittedScore.getInstance(submittedScores);

                                case 2:
                                    return FragOtherDivisions.getInstance(mCurCompetition);
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
                                    return TextUtils.isEmpty(mCurCompetition.DivisionName) ? "Standings" : mCurCompetition.DivisionName;

                                case 1:
                                    return "Scores";

                                case 2:
                                    return "All Divisions";
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

    public void setCurrentTab(int index) {
        if(mPager!= null) {
            mPager.setCurrentItem(index, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_COMPETITION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                mCurCompetition = Parcels.unwrap(data.getParcelableExtra(FragListSelection.RESULT_SELECTION));
                fetchDivisionReport(mCurCompetition);
            } else {
                getActivity().onBackPressed();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    public static class FragDivisionReport extends Fragment {

        private static final String EXTRA_COMPETITION = "cur_competition";
        private static final String EXTRA_LIST = "object_list";
        //private static final String EXTRA_COURTS = "courts";
        private static final String EXTRA_SHOW_CANTACT = "can_show_contact";

        public static Fragment getInstance(Competition curCompetition, List<PlayerDetail> list, boolean canShowContact) {
            FragDivisionReport fragDivisionReport = new FragDivisionReport();

            Bundle args = new Bundle();
            //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
            args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curCompetition));
            args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
            args.putBoolean(EXTRA_SHOW_CANTACT, canShowContact);

            fragDivisionReport.setArguments(args);

            return fragDivisionReport;
        }

        /* Views */
        @BindView(R.id.recyclerView)
        RecyclerView mRecyclerView;

        /* Data */
//        private List<PlayerDetail> mPlayerDetails;

        private Competition mCurCompetition;
        //private List<NameIdPair> mCourts;
        private boolean mCanShowContact;
        RecyclerAdapter adapter;
        private List<PlayerDetail> mPlayerDetails;

        public void resetList() {
            countSelection = 0;
            if (mPlayerDetails != null) {
                for (PlayerDetail playerDetail : mPlayerDetails) {
                    playerDetail.isSelected = false;
                }
            }
            if (adapter != null) {
                mRecyclerView.swapAdapter(adapter,false);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                adapter.notifyDataSetChanged();
            }
        }

        public void updateRecyclerView(boolean isReset) {

            mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(),
                    mRecyclerView.getPaddingTop(),
                    mRecyclerView.getPaddingRight(),((isReset) ? 0 : 150));
        }


        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_league_standings, container, false);

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
            mCanShowContact = args.getBoolean(EXTRA_SHOW_CANTACT, false);

            mCurPlayer = new Prefs.AppData(getActivity()).getPlayer();

            adapter = new RecyclerAdapter<PlayerDetail, PlayerDetailsViewHolder>(mPlayerDetails) {

                @Override
                public PlayerDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_league_player_details_list_item, null);
                    return new PlayerDetailsViewHolder(view);
                }

                @Override
                public void onBindViewHolder(PlayerDetailsViewHolder holder, int position) {
                    holder.bindItem(getItem(position), mPlayerDetails.size());
                }

            };
            mRecyclerView.setAdapter(adapter);
        }

        class PlayerDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

            @BindView(R.id.card_view)
            CardView CardViewContainer;

            @BindView(R.id.txtPlayerName)
            TextView PlayerNameTextView;

            @BindView(R.id.txtWinLoss)
            TextView WinLossTextView;

            @BindView(R.id.txtGames)
            TextView GamesTextView;

            @BindView(R.id.imgContactMenu)
            ImageView ContactImageView;

            private PlayerDetail mPlayerDetail;
            private int itemSelectedColor = ContextCompat.getColor(getActivity(), R.color.button_color_fade);
            private int  playerCount = 0;

            public PlayerDetailsViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                ContactImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContactImageView.getVisibility() == View.VISIBLE && !isMultiSelect) {
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
                                            DlgSendMail.getDialogInstanceLeagues(mCurCompetition, mCurPlayer, mPlayerDetail).show(getChildFragmentManager(), "dlg-mail");
                                        /*Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("message/rfc822");
                                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {mPlayerDetail.Email});
                                        intent.putExtra(Intent.EXTRA_SUBJECT, "Dummy Subject");
                                        intent.putExtra(Intent.EXTRA_TEXT, "Dummy body.");
                                        startActivity(Intent.createChooser(intent, "Send Email"));*/
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

            public void bindItem(PlayerDetail playerDetail, int maxCount) {
                playerCount = maxCount;
                mPlayerDetail = playerDetail;
                PlayerNameTextView.setText(mPlayerDetail.Name);
                WinLossTextView.setText(mPlayerDetail.WinLoss);
                GamesTextView.setText(mPlayerDetail.Games);

                ContactImageView.setVisibility(mCanShowContact ? (mPlayerDetail.id == mCurPlayer.id || mPlayerDetail.IsRetired ? View.INVISIBLE : View.VISIBLE) : View.GONE);

                if (mCanShowContact) {
                    if (mPlayerDetail.id == mCurPlayer.id || mPlayerDetail.IsRetired) {

                    }
                    else {
                        itemView.setOnClickListener(this);
                    }
                }
                CardViewContainer.setForeground(getResources().getDrawable(mPlayerDetail.id == mCurPlayer.id ? R.color.yellow_item_highlight : android.R.color.transparent));

                View itemBG = itemView.findViewById(R.id.itemBG);

                if (itemBG != null) {
                    if (playerDetail.isSelected) {
                        itemBG.setBackgroundColor(itemSelectedColor);
                    }
                    else {
                        itemBG.setBackgroundColor(Color.WHITE);
                    }
                }
            }

            @Override
            public void onClick(View v) {
                if (mPlayerDetail.id == mCurPlayer.id){
                    return;
                }

                if (countSelection >= 5 &&  !mPlayerDetail.isSelected) {
                    final BaseDialog.SimpleDialog alert = BaseDialog.SimpleDialog.getDialogInstance("Max limit reached", 0, "Maximum 5 players can be selected.");
                    alert.setPositiveButton("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });
                    alert.show(getChildFragmentManager(), "select-alert");
                    return;
                }

                mPlayerDetail.isSelected = !mPlayerDetail.isSelected;

                View itemBG = v.findViewById(R.id.itemBG);
                if (mPlayerDetail.isSelected){
                    countSelection += 1;
                    itemBG.setBackgroundColor(itemSelectedColor);
                }
                else {
                    countSelection -= 1;
                    itemBG.setBackgroundColor(Color.WHITE);
                }


                if (countSelection == 1) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

                    if (frag instanceof FragLeagueDetails){
                        if (!((FragLeagueDetails) frag).isBottomviewVisible) {
                            ((FragLeagueDetails) frag).showBottomView();
                        }
                    }
                }
                if (countSelection == 0) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

                    if (frag instanceof FragLeagueDetails){
                        ((FragLeagueDetails) frag).hideBottomView();
                    }
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

                if (frag instanceof FragLeagueDetails){
                    ((FragLeagueDetails) frag).updatePlayers(mPlayerDetail);
                }
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

    public static class FragOtherDivisions extends Fragment {

        private static final String EXTRA_COMPETITION = "CurCompetition";
        private Competition mCompetition;

        public static FragOtherDivisions getInstance(Competition curCompetition) {
            FragOtherDivisions fragSubmittedScore = new FragOtherDivisions();

            Bundle args = new Bundle();
            args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curCompetition));

            fragSubmittedScore.setArguments(args);

            return fragSubmittedScore;
        }

        /* Views */
        @BindView(R.id.recyclerView)
        RecyclerView mRecyclerView;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.frag_recycler_view, container, false);

            ButterKnife.bind(this, v);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

            return v;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Bundle args = getArguments();
            if (args == null) throw new RuntimeException("Missing Arguments");

            mCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));

            WSHandle.Leagues.getLeagueDivisions(mCompetition.CompetitionId, new VolleyHelper.IRequestListener<List<NameIdPair>, String>() {
                @Override
                public void onFailureResponse(String response) {
                    Toast.makeText(getActivity(), "Some Error Occurred, Please try again", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessResponse(List<NameIdPair> response) {
                    mRecyclerView.setAdapter(new RecyclerAdapter<NameIdPair, DivisionViewHolder>(response) {

                        @Override
                        public DivisionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_selection_list_item, parent, false);
                            return new DivisionViewHolder(view);
                        }

                        @Override
                        public void onBindViewHolder(DivisionViewHolder holder, int position) {
                            holder.bindData(getItem(position));
                        }

                    });
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(getActivity(), "Network Error Occurred, Please try again", Toast.LENGTH_LONG).show();
                }
            });


        }

        class DivisionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @BindView(android.R.id.text1)
            TextView text1;

            NameIdPair mDivision;

            public DivisionViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
            }

            public void bindData(NameIdPair item) {
                mDivision = item;

                text1.setText(mDivision.Name);
            }

            @Override
            public void onClick(View v) {
                if (mCompetition.DivisionId == mDivision.Id) {
                    FragLeagueDetails.sFragLeagueDetails.setCurrentTab(0);
                    //Toast.makeText(getActivity(), "You belong to this division", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(SingleFragmentActivity.getIntent(getActivity(), FragOtherDivisionDetails.class, FragOtherDivisionDetails.buildArguments(mDivision, mCompetition)));
                }
            }
        }

    }

}
