package com.tennisdc.tennisleaguenetwork.modules.ladder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
//import androidx.fragment.view.ViewPager;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.tennisdc.tennisleaguenetwork.model.ChallengeIncoming;
import com.tennisdc.tennisleaguenetwork.model.ChallengeOutgoing;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.SubmittedScore;
import com.tennisdc.tennisleaguenetwork.modules.common.DialogClickListener;
import com.tennisdc.tennisleaguenetwork.modules.common.DlgSendMail;
import com.tennisdc.tennisleaguenetwork.modules.partner.FragPartnerProgramDetails;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.ui.RecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

/**
 * Created  on 2015-02-17.
 */
public class FragLadderDetails extends Fragment  implements DialogClickListener {

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    public static int countSelection = 0;
    public static boolean isMultiSelect = false;

    private View view;
    private FragLadderStandings  fragLadderStandings;

    private List<PlayerDetail> mPlayerList = new ArrayList<>();
    private PlayerDetail mCurPlayer;
    boolean isBottomviewVisible = false;
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_partner_details, container, false);

        ButterKnife.bind(this, view);

        countSelection = 0;

        addBottomView();

        addListnerToSlider();

        mCurPlayer = new Prefs.AppData(getActivity()).getPlayer();

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
                fragLadderStandings.resetList();

                LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);
                hiddenPanel.setVisibility(View.INVISIBLE);
                fragLadderStandings.updateRecyclerView(true);
            }
        });
    }

    private void addBottomView() {
        LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);

        Button sendMail = (Button) hiddenPanel.findViewById(R.id.sendMail);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCurPlayer.IsLadderProgramEnrolled) {
                    final BaseDialog.SimpleDialog buyDialog = BaseDialog.SimpleDialog.getDialogInstance("Enroll Tennis Ladder?", 0, "We currently don't have you enrolled in the Tennis Ladder please go to the website to enroll today.");
                    buyDialog.setPositiveButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                            buyDialog.dismiss();
                        }
                    }).setNegativeButton("No", null);
                    buyDialog.show(getChildFragmentManager(), "buy-dlg");
                    return;
                }

                DlgSendMail dialog = DlgSendMail.getDialogInstancePartnerProgram(mCurPlayer, mPlayerList, false);
                dialog.setTargetFragment(FragLadderDetails.this, 0);
                dialog.show(getChildFragmentManager(), "dlg-mail");

                hideBottomView();
            }
        });

        Button sendSMS = (Button) hiddenPanel.findViewById(R.id.sendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCurPlayer.IsLadderProgramEnrolled) {
                    final BaseDialog.SimpleDialog buyDialog = BaseDialog.SimpleDialog.getDialogInstance("Enroll Tennis Ladder?", 0, "We currently don't have you enrolled in the Tennis Ladder please go to the website to enroll today.");
                    buyDialog.setPositiveButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                            buyDialog.dismiss();
                        }
                    }).setNegativeButton("No", null);
                    buyDialog.show(getChildFragmentManager(), "buy-dlg");
                    return;
                }

                DlgSendMail dialog =  DlgSendMail.getDialogInstancePartnerProgram(mCurPlayer, mPlayerList, true);
                dialog.setTargetFragment(FragLadderDetails.this, 0);
                dialog.show(getChildFragmentManager(), "dlg-sms");

                hideBottomView();
            }
        });

        Button Cancel = (Button) hiddenPanel.findViewById(R.id.cancelBtn);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideBottomView();

                fragLadderStandings.resetList();
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
        fragLadderStandings.updateRecyclerView(false);
    }

    public void hideBottomView() {
        Animation bottom_down = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_down);
        LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);
        hiddenPanel.startAnimation(bottom_down);
        hiddenPanel.setVisibility(View.INVISIBLE);
        isMultiSelect = false;
        isBottomviewVisible = false;
        fragLadderStandings.updateRecyclerView(true);
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
        fragLadderStandings.resetList();
        hideBottomView();
    }
    @Override
    public void onNoClick() {
        fragLadderStandings.resetList();
        hideBottomView();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        getActivity().setTitle("Tennis Ladder");
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        //long playerId = new Prefs.AppData(getActivity()).getPlayer().id;

        Request divisionReportRequest = WSHandle.Ladder.getDetailsRequest(App.sOAuth, new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    if(response.getString("responseCode").equals("200")) {
                        Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();
                        final List<PlayerDetail> playerDetails = gson.fromJson(response.getString("ladder_details"), new TypeToken<List<PlayerDetail>>() {}.getType());
                        final List<SubmittedScore> submittedScores = gson.fromJson(response.getString("score_details"), new TypeToken<List<SubmittedScore>>() {}.getType());
                        final ChallengeOutgoing challengeOutgoing = gson.fromJson(response.getString("challenge_outgoing"), ChallengeOutgoing.class);
                        final ChallengeIncoming challengeIncoming = gson.fromJson(response.getString("challenge_incoming"), ChallengeIncoming.class);
                        //final List<NameIdPair> courts = new Gson().fromJson(response.getString("courts"), new TypeToken<List<NameIdPair>>() {}.getType());

                        mPager.setOffscreenPageLimit(2);

                        mPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
                            @Override
                            public Fragment getItem(int position) {
                                switch (position) {
                                    case 0:
                                        fragLadderStandings = (FragLadderStandings)FragLadderStandings.getInstance(playerDetails);
                                        return fragLadderStandings;

                                    case 1:
                                        return FragLadderSubmittedScore.getInstance(submittedScores);

                                    case 2:
                                        return FragChallenges.getInstance(challengeIncoming, challengeOutgoing);
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
                                        return "Scores";

                                    case 2:
                                        return "Challenges";
                                }
                            }

                        });

                        mPagerSlidingTabStrip.setViewPager(mPager);
                    } else if(response.getString("responseCode").equals("404")) {
                        final BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response.getString("responseMessage"));
                        dialog.setNeutralButton("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        })
                        .show(getChildFragmentManager(), "dlg-frg");
                    }
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

    public static class FragLadderStandings extends Fragment {

        //private static final String EXTRA_COMPETITION = "cur_competition";
        private static final String EXTRA_LIST = "object_list";
        private boolean mIsPlayerExist = false;
        //private static final String EXTRA_COURTS = "courts";

        public static Fragment getInstance(List<PlayerDetail> list) {
            FragLadderStandings fragDivisionReport = new FragLadderStandings();

            Bundle args = new Bundle();
            //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
            //args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curCompetition));
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
        private PlayerDetail mCurPlayer;
        //private List<NameIdPair> mCourts;
        RecyclerAdapter adapter;


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

            //mCurCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
            mPlayerDetails = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
            //mCourts = Parcels.unwrap(args.getParcelable(EXTRA_COURTS));

            mCurPlayer = new Prefs.AppData(getActivity()).getPlayer();

            for (PlayerDetail playerDetail : mPlayerDetails) {
                if (playerDetail.id == mCurPlayer.id) {
                    mIsPlayerExist = true;
                }
            }

            adapter = new RecyclerAdapter<PlayerDetail, PlayerDetailsViewHolder>(mPlayerDetails) {

                @Override
                public PlayerDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_ladder_player_details_list_item, null);
                    return new PlayerDetailsViewHolder(view);
                }

                @Override
                public void onBindViewHolder(PlayerDetailsViewHolder holder, int position) {
                    holder.bindItem(getItem(position), mPlayerDetails.size());
                }

            };
            mRecyclerView.setAdapter(adapter);
        }


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

        class PlayerDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @BindView(R.id.card_view)
            CardView CardViewContainer;

            @BindView(R.id.txtPlayerName)
            TextView PlayerNameTextView;

            @BindView(R.id.txtStanding)
            TextView StandingTextView;

            @BindView(R.id.txtLadderRecord)
            TextView LadderRecordTextView;

            @BindView(R.id.txtHomeCourt)
            TextView HomeCourtTextView;

            @BindView(R.id.imgContactMenu)
            ImageView ContactImageView;

            @BindView(R.id.btnChallenge)
            Button ChallengeButton;

            private PlayerDetail mPlayerDetail;

            private int  playerCount = 0;

            private int itemSelectedColor = ContextCompat.getColor(getActivity(), R.color.button_color_fade);

            public PlayerDetailsViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                ChallengeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance("Challenge?", 0, "You are looking to Challenge " + mPlayerDetail.Name + ". They will have 2 days to accept or decline your challenge. If they don't respond you will take their spot in the ladder.");

                        dialog.setNegativeButton("Cancel", null).setPositiveButton("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Processing Request", "Please wait...");
                                Request sendChallengeRequest = WSHandle.LadderChallenge.getCreateChallengeRequest(App.sOAuth, mPlayerDetail.id, new VolleyHelper.IRequestListener<String, String>() {
                                    @Override
                                    public void onFailureResponse(String response) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onSuccessResponse(String response) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();

                                        startActivity(SingleFragmentActivity.getIntent(getActivity(), FragLadderDetails.class, null));
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                                    }
                                });

                                VolleyHelper.getInstance(getActivity()).addToRequestQueue(sendChallengeRequest);
                            }
                        });

                        dialog.show(getChildFragmentManager(), "dlg-confirm");
                    }
                });

                ContactImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPlayerDetail.id != mCurPlayer.id && !isMultiSelect) {
                            PopupMenu popup = new PopupMenu(getActivity(), ContactImageView);
                            MenuInflater inflater = popup.getMenuInflater();
                            Menu popupMenu = popup.getMenu();
                            inflater.inflate(R.menu.contact_popup_menu, popupMenu);

                            /*if (!TextUtils.isEmpty(mPlayerDetail.ChallengeText)) {
                                inflater.inflate(R.menu.challenge_popup_menu, popupMenu);
                                MenuItem challengeMenuItem = popupMenu.findItem(R.id.action_challenge);
                                challengeMenuItem.setVisible(true);
                                challengeMenuItem.setTitle(mPlayerDetail.ChallengeText);
                                challengeMenuItem.setEnabled(mPlayerDetail.IsChallengeButtonEnable);
                            }*/

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
                                    if (mCurPlayer.IsLadderProgramEnrolled) {
                                        switch (item.getItemId()) {
                                            case R.id.action_contact_call: {
                                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                                intent.setData(Uri.parse("tel:" + mPlayerDetail.Phone));
                                                startActivity(Intent.createChooser(intent, "Call"));
                                            }
                                            return true;

                                            case R.id.action_contact_mail: {
                                                DlgSendMail.getDialogInstanceTennisLadder(mCurPlayer, mPlayerDetail).show(getChildFragmentManager(), "dlg-mail");
                                            }
                                            return true;

                                            case R.id.action_contact_message: {
                                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mPlayerDetail.Phone));
                                                startActivity(Intent.createChooser(intent, "Send Message"));
                                            }
                                            return true;

                                            case R.id.action_challenge: {
                                                //TODO create challenge
                                                BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance("Challenge?", 0, "You are looking to Challenge " + mPlayerDetail.Name + ". They will have 2 days to accept or decline your challenge. If they don't respond you will take their spot in the ladder.");

                                                dialog.setNegativeButton("Cancel", null).setPositiveButton("OK", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Processing Request", "Please wait...");
                                                        Request sendChallengeRequest = WSHandle.LadderChallenge.getCreateChallengeRequest(App.sOAuth, mPlayerDetail.id, new VolleyHelper.IRequestListener<String, String>() {
                                                            @Override
                                                            public void onFailureResponse(String response) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                                                            }

                                                            @Override
                                                            public void onSuccessResponse(String response) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();

                                                                startActivity(SingleFragmentActivity.getIntent(getActivity(), FragLadderDetails.class, null));
                                                                getActivity().finish();
                                                            }

                                                            @Override
                                                            public void onError(VolleyError error) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                                                            }
                                                        });

                                                        VolleyHelper.getInstance(getActivity()).addToRequestQueue(sendChallengeRequest);
                                                    }
                                                });

                                                dialog.show(getChildFragmentManager(), "dlg-confirm");
                                            }
                                            return true;
                                        }
                                    } else {
                                        final BaseDialog.SimpleDialog buyDialog = BaseDialog.SimpleDialog.getDialogInstance("Enroll Tennis Ladder?", 0, "We currently don't have you enrolled in the Tennis Ladder please go to the website to enroll today.");
                                        buyDialog.setPositiveButton("Yes", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                                                buyDialog.dismiss();
                                            }
                                        }).setNegativeButton("No", null);
                                        buyDialog.show(getChildFragmentManager(), "buy-dlg");
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

                    if (frag instanceof FragLadderDetails){
                        if (!((FragLadderDetails) frag).isBottomviewVisible)
                            ((FragLadderDetails) frag).showBottomView();
                    }
                }
                if (countSelection == 0) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

                    if (frag instanceof FragLadderDetails){
                        ((FragLadderDetails) frag).hideBottomView();
                    }
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

                if (frag instanceof FragLadderDetails){
                    ((FragLadderDetails) frag).updatePlayers(mPlayerDetail);
                }
            }

            public void bindItem(PlayerDetail playerDetail, int maxCount) {
                playerCount = maxCount;
                mPlayerDetail = playerDetail;
                PlayerNameTextView.setText(mPlayerDetail.Name);
                StandingTextView.setText(String.valueOf(mPlayerDetail.PotyRank));
                LadderRecordTextView.setText(mPlayerDetail.LadderRecord);
                HomeCourtTextView.setText(mPlayerDetail.HomeCourt.getName());

                ContactImageView.setVisibility(mPlayerDetail.id == mCurPlayer.id ? View.GONE : View.VISIBLE);

                CardViewContainer.setForeground(getResources().getDrawable(mPlayerDetail.id == mCurPlayer.id ? R.color.yellow_item_highlight : android.R.color.transparent));

                if (!TextUtils.isEmpty(mPlayerDetail.ChallengeText)) {
                    ChallengeButton.setVisibility(View.VISIBLE);
                    ChallengeButton.setText(mPlayerDetail.ChallengeText);
                    ChallengeButton.setEnabled(mPlayerDetail.IsChallengeButtonEnable);
                } else {
                    ChallengeButton.setVisibility(View.GONE);
                }

                View itemBG = itemView.findViewById(R.id.itemBG);

                if (itemBG != null) {
                    if (playerDetail.isSelected) {
                        itemBG.setBackgroundColor(itemSelectedColor);
                    }
                    else {
                        itemBG.setBackgroundColor(Color.WHITE);
                    }
                }

                if (mPlayerDetail != null) {
                    if (mPlayerDetail.id != mCurPlayer.id) {
                        itemView.setOnClickListener(this);
                    }
                }
            }
        }
    }

    public static class FragLadderSubmittedScore extends Fragment {

        private static final String EXTRA_LIST = "object_list";

        public static Fragment getInstance(List<SubmittedScore> list) {
            FragLadderSubmittedScore fragSubmittedScore = new FragLadderSubmittedScore();

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
            if(mSubmittedScores != null) {
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

    public static class FragChallenges extends Fragment {

        private static final String EXTRA_CHALLENGE_INCOMING = "challenge_incoming";
        private static final String EXTRA_CHALLENGE_OUTGOING = "challenge_outgoing";

        public static Fragment getInstance(ChallengeIncoming challengeIncoming, ChallengeOutgoing challengeOutgoing) {
            FragChallenges fragSubmittedScore = new FragChallenges();

            Bundle args = new Bundle();
            args.putParcelable(EXTRA_CHALLENGE_INCOMING, Parcels.wrap(challengeIncoming));
            args.putParcelable(EXTRA_CHALLENGE_OUTGOING, Parcels.wrap(challengeOutgoing));

            fragSubmittedScore.setArguments(args);

            return fragSubmittedScore;
        }

        //Views
        @BindView(R.id.vwChallengeIncoming)
        View mChallengeIncomingView;

        @BindView(R.id.vwChallengeOutgoing)
        View mChallengeOutgoingView;

        @BindView(R.id.txtNoChallenges)
        TextView mNoChallengesTextView;

        //Data
        private ChallengeIncoming mChallengeIncoming;
        private ChallengeOutgoing mChallengeOutgoing;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_ladder_challenges, container, false);
            ButterKnife.bind(this, view);

            Bundle args = getArguments();
            if (args != null) {
                mChallengeIncoming = Parcels.unwrap(args.getParcelable(EXTRA_CHALLENGE_INCOMING));
                mChallengeOutgoing = Parcels.unwrap(args.getParcelable(EXTRA_CHALLENGE_OUTGOING));
            }

            if (mChallengeIncoming == null || TextUtils.isEmpty(mChallengeIncoming.Header)) {
                mChallengeIncomingView.setVisibility(View.GONE);
            } else {
                ((TextView) mChallengeIncomingView.findViewById(R.id.txtHeader)).setText(mChallengeIncoming.Header);
                ((TextView) mChallengeIncomingView.findViewById(R.id.txtMessage)).setText(mChallengeIncoming.Message);

                //mChallengeIncomingView.findViewById(R.id.btnNegative).setVisibility(View.GONE);
                //((Button)mChallengeOutgoingView.findViewById(R.id.btnNegative)).setText("Decline");
                //mChallengeIncomingView.findViewById(R.id.btnPositive).setVisibility(mChallengeIncoming.IsAcceptButtonVisible ? View.VISIBLE : View.GONE);

                if (mChallengeIncoming.IsAcceptButtonVisible) {
                    View acceptBtn = mChallengeIncomingView.findViewById(R.id.btnPositive);
                    acceptBtn.setVisibility(View.VISIBLE);
                    acceptBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance("Accept Challenge?", 0, "Do you want to accept the challenge?");

                            dialog.setNegativeButton("No", null).setPositiveButton("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
                                    Request acceptRequest = WSHandle.LadderChallenge.getAcceptRequest(App.sOAuth, mChallengeIncoming, new VolleyHelper.IRequestListener<String, String>() {
                                        @Override
                                        public void onFailureResponse(String response) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onSuccessResponse(String response) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();

                                            startActivity(SingleFragmentActivity.getIntent(getActivity(), FragLadderDetails.class, null));
                                            getActivity().finish();
                                        }

                                        @Override
                                        public void onError(VolleyError error) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    VolleyHelper.getInstance(getActivity()).addToRequestQueue(acceptRequest);
                                }
                            });
                            dialog.show(getChildFragmentManager(), "dlg-confirm");
                        }
                    });
                }

                Button declineBtn = (Button) mChallengeIncomingView.findViewById(R.id.btnNegative);
                declineBtn.setText("Decline");
                declineBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance("Decline Challenge?", 0, "Do you want to decline the challenge?");

                        dialog.setNegativeButton("No", null).setPositiveButton("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //TODO decline request
                                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
                                Request declineRequest = WSHandle.LadderChallenge.getDeclineRequest(App.sOAuth, mChallengeIncoming, new VolleyHelper.IRequestListener<String, String>() {
                                    @Override
                                    public void onFailureResponse(String response) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onSuccessResponse(String response) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();

                                        startActivity(SingleFragmentActivity.getIntent(getActivity(), FragLadderDetails.class, null));
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                                    }
                                });

                                VolleyHelper.getInstance(getActivity()).addToRequestQueue(declineRequest);
                            }
                        });

                        dialog.show(getChildFragmentManager(), "dlg-confirm");
                    }
                });
            }

            if (mChallengeOutgoing == null || TextUtils.isEmpty(mChallengeOutgoing.Status)) {
                mChallengeOutgoingView.setVisibility(View.GONE);
            } else {
                ((TextView) mChallengeOutgoingView.findViewById(R.id.txtHeader)).setText(mChallengeOutgoing.Header);
                ((TextView) mChallengeOutgoingView.findViewById(R.id.txtMessage)).setText(mChallengeOutgoing.Message);

                //mChallengeOutgoingView.findViewById(R.id.btnPositive).setVisibility(View.GONE);

                mChallengeOutgoingView.findViewById(R.id.btnNegative).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance("Cancel Challenge?", 0, "Do you want to cancel the challenge?");

                        dialog.setNegativeButton("No", null).setPositiveButton("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //TODO cancel request
                                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
                                Request cancelRequest = WSHandle.LadderChallenge.getCancelRequest(App.sOAuth, mChallengeOutgoing, new VolleyHelper.IRequestListener<String, String>() {
                                    @Override
                                    public void onFailureResponse(String response) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onSuccessResponse(String response) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();

                                        startActivity(SingleFragmentActivity.getIntent(getActivity(), FragLadderDetails.class, null));
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                                    }
                                });

                                VolleyHelper.getInstance(getActivity()).addToRequestQueue(cancelRequest);
                            }
                        });

                        dialog.show(getChildFragmentManager(), "dlg-confirm");
                    }
                });
            }

            if ((mChallengeIncoming == null || TextUtils.isEmpty(mChallengeIncoming.Header)) && (mChallengeOutgoing == null || TextUtils.isEmpty(mChallengeOutgoing.Status))) {
                mNoChallengesTextView.setVisibility(View.VISIBLE);
            }

            return view;
        }

    }
}
