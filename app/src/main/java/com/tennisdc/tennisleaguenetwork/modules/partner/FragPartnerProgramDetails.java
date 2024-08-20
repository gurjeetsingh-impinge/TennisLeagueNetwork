package com.tennisdc.tennisleaguenetwork.modules.partner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import androidx.appcompat.app.AppCompatActivity;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.GsonRealmExclusionStrategy;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.Competition;
import com.tennisdc.tennisleaguenetwork.model.NameIdPair;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.SubmittedScore;
import com.tennisdc.tennisleaguenetwork.modules.common.DlgSendMail;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.ui.RecyclerAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.BindView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import java.util.ArrayList;
import com.tennisdc.tennisleaguenetwork.modules.common.DialogClickListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


/**
 * Created  on 2015-02-17.
 */
public class FragPartnerProgramDetails extends Fragment implements DialogClickListener {

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    @BindView(R.id.llExpiryDateLayout)
    LinearLayout mExpirationDateLayout;

    @BindView(R.id.txtExpiryDate)
    TextView mTxtExpiryDate;

    public static int countSelection = 0;
    public static boolean isMultiSelect = false;

    private View view;
    private FragPartnerStandings partnerStandings;

    private List<PlayerDetail> mPlayerList = new ArrayList<>();
    private PlayerDetail mCurPlayer;
    boolean isBottomviewVisible = false;
    Handler handler = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_partner_details, container, false);

        ButterKnife.bind(this, view);
        mCurPlayer = new Prefs.AppData(getActivity()).getPlayer();

        countSelection = 0;

        addBottomView();

        addListnerToSlider();

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
                partnerStandings.resetList();

                LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);
                hiddenPanel.setVisibility(View.INVISIBLE);
                partnerStandings.updateRecyclerView(true);
            }
        });
    }

    private void addBottomView() {
        LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);

        Button sendMail = (Button) hiddenPanel.findViewById(R.id.sendMail);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCurPlayer.IsPartnerProgramEnrolled) {
                   showEnrollPopup();
                    return;
                }

                DlgSendMail dialog = DlgSendMail.getDialogInstancePartnerProgram(mCurPlayer, mPlayerList, false);
                dialog.setTargetFragment(FragPartnerProgramDetails.this, 0);
                dialog.show(getChildFragmentManager(), "dlg-mail");

                hideBottomView();
            }
        });

        Button sendSMS = (Button) hiddenPanel.findViewById(R.id.sendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCurPlayer.IsPartnerProgramEnrolled) {
                    showEnrollPopup();
                    return;
                }

                DlgSendMail dialog =  DlgSendMail.getDialogInstancePartnerProgram(mCurPlayer, mPlayerList, true);
                dialog.setTargetFragment(FragPartnerProgramDetails.this, 0);
                dialog.show(getChildFragmentManager(), "dlg-sms");

                hideBottomView();
           }
        });

        Button Cancel = (Button) hiddenPanel.findViewById(R.id.cancelBtn);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideBottomView();

                partnerStandings.resetList();
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
        partnerStandings.updateRecyclerView(false);
    }

    public void hideBottomView() {
        Animation bottom_down = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_down);
        LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);
        hiddenPanel.startAnimation(bottom_down);
        hiddenPanel.setVisibility(View.INVISIBLE);
        isMultiSelect = false;
        isBottomviewVisible = false;
        partnerStandings.updateRecyclerView(true);
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
        partnerStandings.resetList();
        hideBottomView();
    }
    @Override
    public void onNoClick() {
        partnerStandings.resetList();
        hideBottomView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        long playerId = new Prefs.AppData(getActivity()).getPlayer().id;

        Request divisionReportRequest = WSHandle.PartnerProgram.getDetailsRequest(App.sOAuth, playerId, new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    if(response.getString("responseCode").equals("404"))
                        progressDialog.dismiss();

                    Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

                    final List<PlayerDetail> playerDetails = gson.fromJson(response.getString("partner_details"), new TypeToken<List<PlayerDetail>>() {}.getType());
                    final List<SubmittedScore> submittedScores = gson.fromJson(response.getString("score_details"), new TypeToken<List<SubmittedScore>>() {}.getType());
                    String expiryDate = response.getString("expiration_date");

                    if(!expiryDate.equals("null")) {
                        mExpirationDateLayout.setVisibility(View.VISIBLE);
                        mTxtExpiryDate.setText(expiryDate);
                    }

                    mPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
                        @Override
                        public Fragment getItem(int position) {
                            switch (position) {
                                case 0:
                                     partnerStandings = (FragPartnerStandings)FragPartnerStandings.getInstance(playerDetails);
                                     return partnerStandings;

                                case 1:
                                    return FragPartnerSubmittedScore.getInstance(submittedScores);
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
                                    return "List of Tennis Partners";

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

        // Is current user enrolled for Partner Program
        PlayerDetail curr_user = new Prefs.AppData(getActivity()).getPlayer();
        if(!curr_user.IsPartnerProgramEnrolled)
        {
            // Wait for 4 secs to display pop-up if user has not enrolled for partner program
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    showEnrollPopup();
                }
            }, 4000);
        }
        VolleyHelper.getInstance(getActivity()).addToRequestQueue(divisionReportRequest);
    }

    private void showEnrollPopup()
    {
        if (getChildFragmentManager() != null) {
            final BaseDialog.SimpleDialog buyDialog = BaseDialog.SimpleDialog.getDialogInstance("Enroll Partner Program?", 0, "We don\'t currently have you enrolled in this Program. Come join these players today?");
            buyDialog.setPositiveButton("Yes", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                    buyDialog.dismiss();
                }
            }).setNegativeButton("No", null);
            buyDialog.show(getChildFragmentManager(), "buy-dlg");
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(getString(R.string.page_partner_program));
        (((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public static class FragPartnerStandings extends Fragment {

        private static final String EXTRA_COMPETITION = "cur_competition";
        private static final String EXTRA_LIST = "object_list";
        private static final String EXTRA_COURTS = "courts";
        private boolean mIsPlayerExist = false;

        public static Fragment getInstance(List<PlayerDetail> list) {
            FragPartnerStandings fragDivisionReport = new FragPartnerStandings();

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
        private Competition mCurCompetition;
        private PlayerDetail mCurPlayer;
        private List<NameIdPair> mCourts;

        RecyclerAdapter adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_recycler_view, container, false);

            ButterKnife.bind(this, view);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

            return view;
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

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Bundle args = getArguments();
            if (args == null) throw new RuntimeException("Missing Arguments");

            mCurCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
            mPlayerDetails = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
            mCourts = Parcels.unwrap(args.getParcelable(EXTRA_COURTS));

            mCurPlayer = new Prefs.AppData(getActivity()).getPlayer();

            for (PlayerDetail playerDetail : mPlayerDetails) {
                if (playerDetail.id == mCurPlayer.id) {
                    mIsPlayerExist = true;
                    break;
                }
            }

            adapter = new RecyclerAdapter<PlayerDetail, PlayerDetailsViewHolder>(mPlayerDetails) {
                @Override
                public PlayerDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_partner_player_details_list_item, null);
                    return new PlayerDetailsViewHolder(view);
                }

                @Override
                public void onBindViewHolder(PlayerDetailsViewHolder holder, int position) {
                    holder.bindItem(getItem(position), mPlayerDetails.size());
                }
            };

            mRecyclerView.setAdapter(adapter);

            /*if(mIsPlayerExist) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                layoutManager.scrollToPosition(playerPosition);
            }*/

        }

        class PlayerDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @BindView(R.id.card_view)
            CardView CardViewContainer;

            @BindView(R.id.txtPlayerName)
            TextView PlayerNameTextView;

            @BindView(R.id.txtSkillRating)
            TextView SkillRatingTextView;

            @BindView(R.id.txtHomeCourt)
            TextView HomeCourtTextView;

            @BindView(R.id.imgContactMenu)
            ImageButton ContactImageView;

            @BindView(R.id.netImgPlayerImage)
            NetworkImageView PlayerImageView;

            private PlayerDetail mPlayerDetail;
            private int  playerCount = 0;

            private int itemSelectedColor = ContextCompat.getColor(getActivity(), R.color.button_color_fade);


            public PlayerDetailsViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                ContactImageView.setOnClickListener(new View.OnClickListener() {
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
                                    if (mCurPlayer.IsPartnerProgramEnrolled) {
                                        switch (item.getItemId()) {
                                            case R.id.action_contact_call: {
                                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                                intent.setData(Uri.parse("tel:" + mPlayerDetail.Phone));
                                                startActivity(Intent.createChooser(intent, "Call"));
                                            }
                                            return true;

                                            case R.id.action_contact_mail: {
                                                DlgSendMail.getDialogInstancePartnerProgram(mCurPlayer, mPlayerDetail).show(getChildFragmentManager(), "dlg-mail");
                                            }
                                            return true;

                                            case R.id.action_contact_message: {
                                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mPlayerDetail.Phone));
                                                startActivity(Intent.createChooser(intent, "Send Message"));
                                            }
                                            return true;
                                        }
                                    } else {
                                        final BaseDialog.SimpleDialog buyDialog = BaseDialog.SimpleDialog.getDialogInstance("Enroll Partner Program?", 0, "We currently don't have you enrolled in Partner Program please go to the website to enroll today.");
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

                    if (frag instanceof FragPartnerProgramDetails){
                        if (!((FragPartnerProgramDetails) frag).isBottomviewVisible)
                        ((FragPartnerProgramDetails) frag).showBottomView();
                    }
                }
                if (countSelection == 0) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

                    if (frag instanceof FragPartnerProgramDetails){
                        ((FragPartnerProgramDetails) frag).hideBottomView();
                    }
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

                if (frag instanceof FragPartnerProgramDetails){
                    ((FragPartnerProgramDetails) frag).updatePlayers(mPlayerDetail);
                }
            }

            public void bindItem(PlayerDetail playerDetail, int maxCount) {
                playerCount = maxCount;
                mPlayerDetail = playerDetail;
                PlayerNameTextView.setText(mPlayerDetail.Name);
                SkillRatingTextView.setText(mPlayerDetail.SkillRating);
                HomeCourtTextView.setText(mPlayerDetail.HomeCourt.getName());
                PlayerImageView.setDefaultImageResId(android.R.drawable.ic_menu_gallery);
                if (!TextUtils.isEmpty(mPlayerDetail.Image)) {
                    Glide.with(getActivity()).load(mPlayerDetail.Image).placeholder(android.R.drawable.ic_menu_gallery).into(PlayerImageView);
//                    PlayerImageView.setImageUrl(mPlayerDetail.Image, VolleyHelper.getInstance(getActivity()).getImageLoader());
                    PlayerImageView.setVisibility(View.VISIBLE);
                } else {
                    PlayerImageView.setVisibility(View.GONE);
                }

                ContactImageView.setVisibility(mPlayerDetail.id == mCurPlayer.id ? View.GONE : View.VISIBLE);

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

                if (mPlayerDetail.id != mCurPlayer.id) {
                    itemView.setOnClickListener(this);
                }
            }
        }
    }

    public static class FragPartnerSubmittedScore extends Fragment {

        private static final String EXTRA_LIST = "object_list";

        public static Fragment getInstance(List<SubmittedScore> list) {
            FragPartnerSubmittedScore fragSubmittedScore = new FragPartnerSubmittedScore();

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
}
