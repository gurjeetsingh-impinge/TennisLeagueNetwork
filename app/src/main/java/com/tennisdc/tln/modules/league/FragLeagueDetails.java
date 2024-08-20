package com.tennisdc.tln.modules.league;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.Competition;
import com.tennisdc.tln.model.DivisionBean;
import com.tennisdc.tln.model.NameIdPair;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.model.SubmittedScore;
import com.tennisdc.tln.modules.common.DialogClickListener;
import com.tennisdc.tln.modules.common.DlgSendMail;
import com.tennisdc.tln.modules.common.FragListSelection;
import com.tennisdc.tln.modules.myAccount.MyAccountScreen;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.DividerItemDecoration;
import com.tennisdc.tln.ui.RecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.BindView;
import hirondelle.date4j.DateTime;

import static com.tennisdc.tln.modules.common.FragListSelection.RESULT_SELECTION;

public class FragLeagueDetails extends Fragment implements DialogClickListener {

    private static final int REQ_COMPETITION = 123;

    public static FragLeagueDetails sFragLeagueDetails;

    @BindView(R.id.pager)
    ViewPager mPager;
    @BindView(R.id.mTxtPayoffMessage)
    TextView mTxtPayoffMessage;
    @BindView(R.id.mTxtQualifierMessage)
    TextView mTxtQualifierMessage;

    @BindView(R.id.btn_show_guide)
    Button mButtonShowGuide;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    private Competition mCurCompetition;
    private DivisionBean mCurDivision;
    private long mCompetionID;
    private String mStartDate = "";
    private View view;

    public static int countSelection = 0;
    public static boolean isMultiSelect = false;
    private List<PlayerDetail> mPlayerList = new ArrayList<>();
    public static PlayerDetail mCurPlayer;

    FragDivisionReport fragDivisionReport;
    boolean isBottomviewVisible = false;
    Prefs.AppData prefs;

//    SimpleDateFormat mSDF = new SimpleDateFormat("EEEE, MMMMM dd");
//    SimpleDateFormat mSDFDefault = new SimpleDateFormat("yyyy-MM-dd");

    public interface CancelHandlerInterface {
        public void cancelHandlerInterface();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sFragLeagueDetails = this;
        countSelection = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_league_details, container, false);
        App.LogFacebookEvent(getActivity(), this.getClass().getName());

        ButterKnife.bind(this, view);
        //cancelHandlerInterface = (CancelHandlerInterface)this;
        prefs = new Prefs.AppData(getActivity());
        addListnerToSlider();
        addBottomView();
        addClickListener();
        return view;
    }

    private void addClickListener() {
        mButtonShowGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShowGuidePopup();
            }
        });
    }

    void openShowGuidePopup(){
        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance("Guide", 0, getString(R.string.guide_details));
        dialog.setNeutralButton("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show(getChildFragmentManager(), "dlg-frag");
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
                isBottomviewVisible = false;
                fragDivisionReport.mChkSelectAllLeague.setChecked(false);
                fragDivisionReport.updateRecyclerView(true);
            }
        });
    }

    CancelHandlerInterface cancelHandlerInterface = (CancelHandlerInterface) getActivity();

    private void addBottomView() {
        LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);

        Button sendMail = (Button) hiddenPanel.findViewById(R.id.sendMail);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DlgSendMail dialog = DlgSendMail.getDialogInstanceLeaguesDivision(mCompetionID, mCurCompetition, mCurPlayer,
                        mPlayerList, false, prefs.getSupportEmail());
//                DlgSendMail dialog = DlgSendMail.getDialogInstanceLeagues(mCurCompetition, mCurDivision, mCurPlayer,
//                        mPlayerList, false, prefs.getSupportEmail());
                dialog.setTargetFragment(FragLeagueDetails.this, 0);
                dialog.show(getFragmentManager(), "dlg-mail");

                hideBottomView();
            }
        });

        Button sendSMS = (Button) hiddenPanel.findViewById(R.id.sendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DlgSendMail dialog = DlgSendMail.getDialogInstanceLeagues(mCurCompetition, mCurPlayer, mPlayerList, true);
                dialog.setTargetFragment(FragLeagueDetails.this, 0);
                dialog.show(getFragmentManager(), "dlg-sms");

                hideBottomView();
            }
        });

        Button Cancel = (Button) hiddenPanel.findViewById(R.id.cancelBtn);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomView();
                cancelHandlerInterface = (CancelHandlerInterface) fragDivisionReport;
                cancelHandlerInterface.cancelHandlerInterface();
                fragDivisionReport.resetList();
            }
        });
    }

    public void showBottomView(boolean mFlagShowSendMsg) {
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_up);
        LinearLayout hiddenPanel = (LinearLayout) view.findViewById(R.id.bottomView);
        Button sendSMS = (Button) hiddenPanel.findViewById(R.id.sendSMS);
        sendSMS.setEnabled(mFlagShowSendMsg);
        if (mFlagShowSendMsg) {
            sendSMS.setBackgroundColor(getActivity().getResources().getColor(R.color.button_color));
        } else {
            sendSMS.setBackgroundColor(getActivity().getResources().getColor(R.color.button_color_fade));
        }
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
        } else {
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
        Bundle mBundle = getArguments();
        if (mBundle.containsKey(FragListSelection.RESULT_SELECTION)) {
            mCurCompetition = Parcels.unwrap(mBundle.getParcelable(FragListSelection.RESULT_SELECTION));
        }
        if (mBundle.containsKey(FragListSelection.RESULT_SELECTION_DIVISION)) {
            mCurDivision = Parcels.unwrap(mBundle.getParcelable(FragListSelection.RESULT_SELECTION_DIVISION));
        }
        if (mBundle.containsKey(FragListSelection.RESULT_COMPETITION_ID)) {
            mCompetionID = mBundle.getInt(FragListSelection.RESULT_COMPETITION_ID);
        }
        if (mBundle.containsKey(FragListSelection.RESULT_START_DATE)) {
            mStartDate = mBundle.getString(FragListSelection.RESULT_START_DATE);
        }
        fetchDivisionReport(mCurDivision, mBundle.getInt(FragListSelection.RESULT_STATUS, 0),
                mBundle.getLong(FragListSelection.RESULT_COMPETITION_ID, 0));
/* mCurCompetition = Parcels.unwrap(data.getParcelableExtra(FragListSelection.RESULT_SELECTION));
                mCurDivision = Parcels.unwrap(data.getParcelableExtra(FragListSelection.RESULT_SELECTION_DIVISION));
                mCompetionID = data.getLongExtra(FragListSelection.RESULT_COMPETITION_ID, 0);
                mStartDate = data.getStringExtra(FragListSelection.RESULT_START_DATE);
                fetchDivisionReport(mCurDivision, data.getIntExtra(FragListSelection.RESULT_STATUS, 0),
                        data.getLongExtra(FragListSelection.RESULT_COMPETITION_ID, 0));*/
//        fetchCompetitionList();
    }

    private void fetchCompetitionList() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        PlayerDetail player = new Prefs.AppData(getActivity()).getPlayer();

        Request competitionListRequest = WSHandle.Leagues.getCompetitionListRequest(player.id, new VolleyHelper.IRequestListener<List<Competition>, String>() {

            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                if (!response.isEmpty())
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
        if (mCurCompetition != null) {
            if (mCurCompetition.getStart_date().toLowerCase().equalsIgnoreCase("Currently On Going".toLowerCase())) {
                getActivity().setTitle(mCurCompetition.CompetitionName);
            } else {
                getActivity().setTitle(mCurCompetition.CompetitionName + " Season starts " + mCurCompetition.getStart_date());
            }
        }
    }

    private void fetchDivisionReport(final Competition competition) {
        if (mCurCompetition.getStart_date().toLowerCase().equalsIgnoreCase("Currently On Going".toLowerCase())) {
            getActivity().setTitle(mCurCompetition.CompetitionName);
        } else {
            getActivity().setTitle(mCurCompetition.CompetitionName + " Season starts " + mCurCompetition.getStart_date());
        }

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.alert_msg_please_wait), true, false);

        Request divisionReportRequest = WSHandle.Leagues.getDetailsRequest(App.sOAuth, competition.DivisionId, new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                if (!response.isEmpty())
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    //final List<NameIdPair> courts = new Gson().fromJson(response.getString("courts"), new TypeToken<List<NameIdPair>>() {}.getType());
                    Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

                    final List<PlayerDetail> playerDetails = gson.fromJson(response.getString("div_details"), new TypeToken<List<PlayerDetail>>() {
                    }.getType());
                    final List<SubmittedScore> submittedScores = gson.fromJson(response.getString("score_details"), new TypeToken<List<SubmittedScore>>() {
                    }.getType());

                    boolean canShowContact;
                    boolean isEnrolled;
                    DateTime startDateTimeTemp = null;
                    String mPayoffMessageTemp = "";
                    String mQualifier_titleTemp = "";
                    String mPlayoff_eligible_txtTemp = "";
                    try {
                        if (response.has("playoff_message")) {
                            mPayoffMessageTemp = response.getString("playoff_message");
                        }
                        if (response.has("qualifier_title")) {
                            mQualifier_titleTemp = Html.fromHtml(response.getString("qualifier_title")).toString();
                        }
                        if (response.has("playoff_eligible_txt")) {
                            mPlayoff_eligible_txtTemp = response.getString("playoff_eligible_txt");
                        }
                        startDateTimeTemp = new DateTime(response.getString("start_date").split("T")[0]);
//                        startDateTimeTemp = new DateTime(response.getString("start_date").split("T")[0]);
                        DateTime endInDateTime = new DateTime(response.getString("end_date").split("T")[0]);
                        DateTime nowDateTime = DateTime.now(TimeZone.getTimeZone("UTC"));

//                        canShowContact = nowDateTime.gteq(startDateTime) && nowDateTime.lteq(endInDateTime.plusDays(30));
                        canShowContact = response.getBoolean("show_contact_info");
                        isEnrolled = response.getBoolean("is_enrolled");
                    } catch (Exception ex) {
                        canShowContact = true;
                        isEnrolled = true;
                    }
                    final String mPayoffMessage = mPayoffMessageTemp;
                    final String mQualifier_title = mQualifier_titleTemp;
                    final String mPlayoff_eligible_txt = mPlayoff_eligible_txtTemp;

                    final boolean finalCanShowContact = canShowContact;
                    final boolean finalIsEnrolled = isEnrolled;
//                    final String startDateTime = startDateTimeTemp.toString();

                    mPager.setOffscreenPageLimit(2);

                    mPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
                        @Override
                        public Fragment getItem(int position) {
                            switch (position) {
                                case 0:
                                    if (mCurCompetition != null) {
                                        fragDivisionReport = (FragDivisionReport) FragDivisionReport.getInstance(mCurCompetition, playerDetails,
                                                finalCanShowContact, finalIsEnrolled, competition.status, competition.getCompetitionId(), mStartDate,
                                                mPayoffMessage, mQualifier_title, mPlayoff_eligible_txt);
                                    } else {
                                        fragDivisionReport = (FragDivisionReport) FragDivisionReport.getInstance(mCurDivision, playerDetails,
                                                finalCanShowContact, finalIsEnrolled, competition.status, competition.getCompetitionId(), mStartDate,
                                                mPayoffMessage, mQualifier_title, mPlayoff_eligible_txt);
                                    }
                                    return fragDivisionReport;

                                case 1:
                                    return FragSubmittedScore.getInstance(submittedScores);

                                case 2:
                                    return FragOtherDivisions.getInstance(mCurDivision);
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

    private void fetchDivisionReport(DivisionBean competition, final int mStatus, final long mCompetitionID) {
        if (mCurCompetition != null) {
            if (mCurCompetition.getStart_date().toLowerCase().equalsIgnoreCase("Currently On Going".toLowerCase())) {
                getActivity().setTitle(mCurCompetition.CompetitionName);
            } else {
                getActivity().setTitle(mCurCompetition.CompetitionName + "Season starts " + mCurCompetition.getStart_date());
            }
        }
        if (mCurDivision != null) {
            if (mStartDate.toLowerCase().equalsIgnoreCase("Currently On Going".toLowerCase())) {
                getActivity().setTitle(mCurDivision.getName());
            } else {
                getActivity().setTitle(mCurDivision.getName() + "Season starts " + mStartDate);
            }
        }

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.alert_msg_please_wait), true, false);

        Request divisionReportRequest = WSHandle.Leagues.getDetailsRequest(App.sOAuth, competition.getDivision_id(), new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                if (!response.isEmpty())
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    //final List<NameIdPair> courts = new Gson().fromJson(response.getString("courts"), new TypeToken<List<NameIdPair>>() {}.getType());
                    Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();
//                    DivisionMainDetails mDivisionList = gson.fromJson(response.toString(), new TypeToken<DivisionMainDetails>() {
//                    }.getType());
                    final List<PlayerDetail> playerDetails = gson.fromJson(response.getString("div_details"), new TypeToken<List<PlayerDetail>>() {
                    }.getType());
                    final List<SubmittedScore> submittedScores = gson.fromJson(response.getString("score_details"), new TypeToken<List<SubmittedScore>>() {
                    }.getType());

                    boolean canShowContact;
                    boolean isEnrolled;
                    DateTime startDateTimeTemp = null;
                    String mPayoffMessageTemp = "";
                    String mQualifier_titleTemp = "";
                    String mPlayoff_eligible_txtTemp = "";
                    try {
                        if (response.has("playoff_message")) {
                            mPayoffMessageTemp = response.getString("playoff_message");
                        }
                        if (response.has("qualifier_title")) {
                            mQualifier_titleTemp = Html.fromHtml(response.getString("qualifier_title")).toString();
                        }
                        if (response.has("playoff_eligible_txt")) {
                            mPlayoff_eligible_txtTemp = response.getString("playoff_eligible_txt");
                        }
                        startDateTimeTemp = new DateTime(response.getString("start_date").split("T")[0]);
                        DateTime endInDateTime = new DateTime(response.getString("end_date").split("T")[0]);
                        DateTime nowDateTime = DateTime.now(TimeZone.getTimeZone("UTC"));

//                        canShowContact = nowDateTime.gteq(startDateTime) && nowDateTime.lteq(endInDateTime.plusDays(30));
                        canShowContact = response.getBoolean("show_contact_info");
                        isEnrolled = response.getBoolean("is_enrolled");
                    } catch (Exception ex) {
                        canShowContact = true;
                        isEnrolled = true;
                    }

                    final String mPayoffMessage = mPayoffMessageTemp;
                    final String mQualifier_title = mQualifier_titleTemp;
                    final String mPlayoff_eligible_txt = mPlayoff_eligible_txtTemp;
                    final boolean finalCanShowContact = canShowContact;
                    final boolean finalIsEnrolled = isEnrolled;

//                    final String startDateTime = startDateTimeTemp.toString();
                    mPager.setOffscreenPageLimit(2);

                    mPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
                        @Override
                        public Fragment getItem(int position) {
                            switch (position) {
                                case 0:
                                    if (mCurCompetition != null) {
                                        fragDivisionReport = (FragDivisionReport) FragDivisionReport.getInstance(mCurCompetition, playerDetails,
                                                finalCanShowContact, finalIsEnrolled, mStatus, mCompetitionID, mStartDate, mPayoffMessage,
                                                mQualifier_title, mPlayoff_eligible_txt);
                                    } else {
                                        fragDivisionReport = (FragDivisionReport) FragDivisionReport.getInstance(mCurDivision, playerDetails,
                                                finalCanShowContact, finalIsEnrolled, mStatus, mCompetitionID, mStartDate, mPayoffMessage,
                                                mQualifier_title, mPlayoff_eligible_txt);
                                    }
                                    return fragDivisionReport;

                                case 1:
                                    return FragSubmittedScore.getInstance(submittedScores);

                                case 2:
                                    return FragOtherDivisions.getInstance(mCurDivision);
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
                                    if (mCurCompetition != null)
                                        return TextUtils.isEmpty(mCurCompetition.DivisionName) ? "Standings" : mCurCompetition.DivisionName;

                                    if (mCurDivision != null)
                                        return TextUtils.isEmpty(mCurDivision.getName()) ? "Standings" : mCurDivision.getName();

                                case 1:
                                    return "Scores";

                                case 2:
                                    return "All Divisions";
                            }//
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
        if (mPager != null) {
            mPager.setCurrentItem(index, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_COMPETITION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                mCurCompetition = Parcels.unwrap(data.getParcelableExtra(RESULT_SELECTION));
                mCurDivision = Parcels.unwrap(data.getParcelableExtra(FragListSelection.RESULT_SELECTION_DIVISION));
                mCompetionID = data.getLongExtra(FragListSelection.RESULT_COMPETITION_ID, 0);
                mStartDate = data.getStringExtra(FragListSelection.RESULT_START_DATE);
                fetchDivisionReport(mCurDivision, data.getIntExtra(FragListSelection.RESULT_STATUS, 0),
                        data.getLongExtra(FragListSelection.RESULT_COMPETITION_ID, 0));
            } else {
                getActivity().onBackPressed();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    public static class FragDivisionReport extends Fragment implements CancelHandlerInterface {

        private static final String EXTRA_COMPETITION = "cur_competition";
        private static final String EXTRA_LIST = "object_list";
        //private static final String EXTRA_COURTS = "courts";
        private static final String EXTRA_SHOW_CANTACT = "can_show_contact";
        private static final String EXTRA_IS_ENROLLED = "is_enrolled";
        private static final String EXTRA_STATUS = "status";
        private static final String RESULT_COMPETITION_ID = "competition_id";
        private static final String RESULT_START_DATE = "mStartDate";
        private static final String RESULT_PLAYOFF_MESSAGE = "mPayoffMessage";
        private static final String RESULT_QUALIFIER_TITLE = "mQualifier_title";
        private static final String RESULT_PLAYOFF_ELEGIBLE_TEXT = "mPlayoff_eligible_txt";

        public static Fragment getInstance(Competition curCompetition, List<PlayerDetail> list, boolean canShowContact,
                                           boolean isEnrolled, int mStatus, long mCompetitionID, String mStartDate, String mPayoffMessage,
                                           String mQualifier_title, String mPlayoff_eligible_txt) {
            FragDivisionReport fragDivisionReport = new FragDivisionReport();

            Bundle args = new Bundle();
            //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
            args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curCompetition));
            args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
            args.putBoolean(EXTRA_SHOW_CANTACT, canShowContact);
            args.putBoolean(EXTRA_IS_ENROLLED, isEnrolled);
            args.putInt(EXTRA_STATUS, mStatus);
            args.putLong(RESULT_COMPETITION_ID, mCompetitionID);
            args.putString(RESULT_START_DATE, mStartDate);
            args.putString(RESULT_PLAYOFF_MESSAGE, mPayoffMessage);
            args.putString(RESULT_QUALIFIER_TITLE, mQualifier_title);
            args.putString(RESULT_PLAYOFF_ELEGIBLE_TEXT, mPlayoff_eligible_txt);
//            args.putString(RESULT_PLAYOFF_MESSAGE, "THis is the rest value fkljf sdfhsjd sehdfis sefhohfs   sejfjkdsfn");

            fragDivisionReport.setArguments(args);

            return fragDivisionReport;
        }

        public static Fragment getInstance(DivisionBean curDivison, List<PlayerDetail> list, boolean canShowContact,
                                           boolean isEnrolled, int mStatus, long mCompetitionID, String mStartDate, String mPayoffMessage,
                                           String mQualifier_title, String mPlayoff_eligible_txt) {
            FragDivisionReport fragDivisionReport = new FragDivisionReport();

            Bundle args = new Bundle();
            //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
            args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curDivison));
            args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
            args.putBoolean(EXTRA_SHOW_CANTACT, canShowContact);
            args.putBoolean(EXTRA_IS_ENROLLED, isEnrolled);
            args.putInt(EXTRA_STATUS, mStatus);
            args.putLong(RESULT_COMPETITION_ID, mCompetitionID);
            args.putString(RESULT_START_DATE, mStartDate);
            args.putString(RESULT_PLAYOFF_MESSAGE, mPayoffMessage);
            args.putString(RESULT_QUALIFIER_TITLE, mQualifier_title);
            args.putString(RESULT_PLAYOFF_ELEGIBLE_TEXT, mPlayoff_eligible_txt);
//            args.putString(RESULT_PLAYOFF_MESSAGE, "THis is the rest value fkljf sdfhsjd sehdfis sefhohfs   sejfjkdsfn");

            fragDivisionReport.setArguments(args);

            return fragDivisionReport;
        }

        /* Views */
        @BindView(R.id.mChkSelectAllLeague)
        CheckBox mChkSelectAllLeague;

        @BindView(R.id.recyclerView)
        RecyclerView mRecyclerView;

        @BindView(R.id.llEnroll)
        LinearLayout mLayoutEnroll;

        @BindView(R.id.txtEnrollMessage)
        TextView mTxtEnrollLeague;

        @BindView(R.id.btnJoinEnroll)
        Button mBtnJoinEnroll;

        boolean mCheckFromSelectAll = false;

        //    To check whether we needs to visible the button "Show Guide"
        public boolean checkForOneTime = false;

        /* Data */
//        private List<PlayerDetail> mPlayerDetails;

        private Competition mCurCompetition;
        private DivisionBean mCurDivision;
        //private List<NameIdPair> mCourts;
        private boolean mCanShowContact;
        private boolean mIsEnrolled;
        private int mStatus;
        private String mStartDate;
        private String mPlayOffMessage = "";
        private String mPlayoffTitle = "";
        private String mQualifier_title = "";
        RecyclerAdapter adapter;
        private List<PlayerDetail> mPlayerDetails;

        public void findTextViewTitle(String title) {
//			String title = "title";

            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            ab.setTitle(title);

            Window window = getActivity().getWindow();
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

        public void resetList() {
            countSelection = 0;
            if (mPlayerDetails != null) {
                for (PlayerDetail playerDetail : mPlayerDetails) {
                    playerDetail.isSelected = false;
                }
            }
            if (adapter != null) {
                mRecyclerView.swapAdapter(adapter, false);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                adapter.notifyDataSetChanged();
            }
        }

        public void updateRecyclerView(boolean isReset) {

            mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(),
                    mRecyclerView.getPaddingTop(),
                    mRecyclerView.getPaddingRight(), ((isReset) ? 0 : 150));
        }


        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_league_standings, container, false);
            App.LogFacebookEvent(getActivity(), this.getClass().getName());

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

            if (Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION)) instanceof Competition)
                mCurCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
            else
                mCurDivision = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));

            mPlayerDetails = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
            //mCourts = Parcels.unwrap(args.getParcelable(EXTRA_COURTS));
            mCanShowContact = args.getBoolean(EXTRA_SHOW_CANTACT, false);
//            mCanShowContact = true;
            mIsEnrolled = args.getBoolean(EXTRA_IS_ENROLLED, false);
            mStatus = args.getInt(EXTRA_STATUS, 0);
            mStartDate = args.getString(RESULT_START_DATE);
            mPlayOffMessage = args.getString(RESULT_PLAYOFF_MESSAGE);
            mQualifier_title = args.getString(RESULT_QUALIFIER_TITLE);
            mPlayoffTitle = args.getString(RESULT_PLAYOFF_ELEGIBLE_TEXT);
//            mCompetionID = args.getLong(RESULT_COMPETITION_ID);
//            getActivity().setTitle(mCurCompetition.get);
            mCurPlayer = new Prefs.AppData(getActivity()).getPlayer();
            if (mCurCompetition != null) {
                if (mCurCompetition.getStart_date().toLowerCase().equalsIgnoreCase("Currently On Going".toLowerCase())) {
                    findTextViewTitle(mCurCompetition.CompetitionName);
                } else {
                    findTextViewTitle(mCurCompetition.CompetitionName + " Starts on " +
                            mCurCompetition.getStart_date());

                }
            } else {
                if (mStartDate.toLowerCase().equalsIgnoreCase("Currently On Going".toLowerCase())) {
                    findTextViewTitle(mCurDivision.getName());
                } else {
                    findTextViewTitle(mCurDivision.getName() + " Starts on " + mStartDate);
                }
//                getActivity().setTitle(mCurDivision.getName() + " Starts on " + mStartDate);
            }
            if (mStatus > 1 && !mIsEnrolled) {
                mLayoutEnroll.setVisibility(View.VISIBLE);
                mTxtEnrollLeague.setText(R.string.enroll_now_string);
                mBtnJoinEnroll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                    }
                });
            } else {
                mLayoutEnroll.setVisibility(View.GONE);
            }
            if (!mPlayOffMessage.isEmpty()) {
                sFragLeagueDetails.mTxtPayoffMessage.setVisibility(View.VISIBLE);
                sFragLeagueDetails.mTxtPayoffMessage.setText(mPlayOffMessage);
            } else {
                sFragLeagueDetails.mTxtPayoffMessage.setVisibility(View.GONE);
            }
            if (!mQualifier_title.isEmpty()) {
                sFragLeagueDetails.mTxtQualifierMessage.setVisibility(View.VISIBLE);
                sFragLeagueDetails.mTxtQualifierMessage.setText(mQualifier_title);
            } else {
                sFragLeagueDetails.mTxtQualifierMessage.setVisibility(View.GONE);
            }
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
            if (mCanShowContact) {
                mChkSelectAllLeague.setVisibility(View.VISIBLE);
            } else {
                mChkSelectAllLeague.setVisibility(View.GONE);
            }
            mChkSelectAllLeague.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mCheckFromSelectAll = false;
                    return false;
                }
            });
            mChkSelectAllLeague.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment frag = fm.findFragmentById(R.id.fragmentContainer);
                    if (!mCheckFromSelectAll) {
                        mCheckFromSelectAll = true;
                        if (isChecked) {
                            for (int i = 0; i < mPlayerDetails.size(); i++) {
                                PlayerDetail mPlayerDetail = mPlayerDetails.get(i);
                                if (mPlayerDetail.id == mCurPlayer.id) {
                                    mPlayerDetail.isSelected = false;
                                } else {
                                    mPlayerDetail.isSelected = true;
                                    countSelection += 1;
                                }

                                if (frag instanceof FragLeagueDetails) {
                                    ((FragLeagueDetails) frag).updatePlayers(mPlayerDetail);
                                }
                            }

                            if (frag instanceof FragLeagueDetails) {
                                if (!((FragLeagueDetails) frag).isBottomviewVisible) {
                                    ((FragLeagueDetails) frag).showBottomView(false);
                                }
                            }
                        } else {
                            for (int i = 0; i < mPlayerDetails.size(); i++) {
                                PlayerDetail mPlayerDetail = mPlayerDetails.get(i);
                                if (mPlayerDetail.id != mCurPlayer.id) {
                                    mPlayerDetail.isSelected = false;
                                    countSelection -= 1;
                                }
                                if (frag instanceof FragLeagueDetails) {
                                    ((FragLeagueDetails) frag).updatePlayers(mPlayerDetail);
                                }
                            }
                            if (frag instanceof FragLeagueDetails) {
                                ((FragLeagueDetails) frag).hideBottomView();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void cancelHandlerInterface() {
            mChkSelectAllLeague.setChecked(false);
        }

        class PlayerDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @BindView(R.id.card_view)
            CardView CardViewContainer;

            @BindView(R.id.txtPlayerName)
            TextView PlayerNameTextView;

            @BindView(R.id.txtPlayerNameDetails)
            TextView PlayerNameDetailsTextView;

            @BindView(R.id.txtWinLoss)
            TextView WinLossTextView;

            @BindView(R.id.txtGames)
            TextView GamesTextView;

            @BindView(R.id.imgContactMenu)
            ImageView ContactImageView;

            private PlayerDetail mPlayerDetail;
            private int itemSelectedColor = ContextCompat.getColor(getActivity(), R.color.button_color_fade);
            private int qualifiedItemSelectedColor = ContextCompat.getColor(getActivity(), R.color.button_color_fade_more);
            private int playerCount = 0;

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

                                        case R.id.action_contact_head_to_head: {
                                            startActivity(new Intent(getActivity(), HeadToHeadActivity.class).putExtra("mPlayerId", mPlayerDetail.id));
                                            return true;
                                        }
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
                String mPlayNameString = "";
                if (mPlayerDetail.profile_active) {
                    mPlayNameString = /*mPlayerDetail.Name + */" " + mPlayerDetail.win_loss_diff + " " + mPlayerDetail.Daytime;
                } else {
                    mPlayNameString = /*mPlayerDetail.Name + */" " + mPlayerDetail.win_loss_diff + " " + mPlayerDetail.Daytime;
                }
                if (mPlayerDetail.IsRetired) {
                    if (mPlayerDetail.profile_active) {
                        mPlayNameString = PlayerNameTextView.getText().toString() + " Ret.";
                    } else {
                        mPlayNameString = PlayerNameTextView.getText().toString() + " Ret.";
                    }
                }


                if (mPlayerDetail.profile_active) {
                    PlayerNameTextView.setOnClickListener(this);
                    setClickableString(String.valueOf(mPlayerDetail.id), mPlayerDetail.Name, mPlayerDetail.Name, PlayerNameTextView);
                } else {
                    PlayerNameTextView.setText(mPlayerDetail.Name);
                }
                PlayerNameDetailsTextView.setText(mPlayNameString);
                WinLossTextView.setText(mPlayerDetail.WinLoss);
                GamesTextView.setText(mPlayerDetail.Games);

//                ContactImageView.setVisibility(mCanShowContact ? (mPlayerDetail.id == mCurPlayer.id || mPlayerDetail.IsRetired ? View.INVISIBLE : View.VISIBLE) : View.GONE);
                Prefs.AppData mPref = new Prefs.AppData(getActivity());
                if (mCanShowContact) {
                    if (String.valueOf(mPlayerDetail.id).equals(mPref.getUserID())) {
                        ContactImageView.setVisibility(View.GONE);
                    } else {
                        if (!checkForOneTime) {
                            checkForOneTime = true;
                            if (mPref.canShowGuidePopup()) {
                                sFragLeagueDetails.openShowGuidePopup();
                                mPref.setGuideButtonVisibility(false);
                            }
                        }
                        sFragLeagueDetails.mButtonShowGuide.setVisibility(View.VISIBLE);
                        ContactImageView.setVisibility(View.VISIBLE);
                    }
                    if (mPlayerDetail.id == mCurPlayer.id || mPlayerDetail.IsRetired) {

                    } else {
                        CardViewContainer.setOnClickListener(this);
                    }
                } else {
                    ContactImageView.setVisibility(View.GONE);
                }
                CardViewContainer.setForeground(getResources().getDrawable(mPlayerDetail.id == mCurPlayer.id ? R.color.yellow_item_highlight : android.R.color.transparent));

                View itemBG = itemView.findViewById(R.id.itemBG);

                if (itemBG != null) {
                    if (playerDetail.isSelected) {
                        itemBG.setBackgroundColor(itemSelectedColor);
                    } else {
                        if (mPlayerDetail.IsPlayoffQualified) {
                            itemBG.setBackgroundColor(qualifiedItemSelectedColor);
                        } else {
                            itemBG.setBackgroundColor(Color.WHITE);
                        }
                    }

                }
            }

            public void setClickableString(String mID, String clickableValue, String wholeValue, TextView yourTextView) {
                String value = wholeValue;
                SpannableString spannableString = new SpannableString(value);
                int startIndex = value.indexOf(clickableValue);
                int endIndex = startIndex + clickableValue.length();
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void updateDrawState(TextPaint ds) {
//                        super.updateDrawState(ds);
                        ds.setUnderlineText(true); // <-- this will remove automatic underline in set span
                    }

                    @Override
                    public void onClick(View widget) {
                        startActivity(new Intent(getActivity(), MyAccountScreen.class).putExtra("mPlayerId", mID));
                    }
                }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                yourTextView.setText(spannableString);
                yourTextView.setMovementMethod(LinkMovementMethod.getInstance()); // <-- important, onClick in ClickableSpan won't work without this
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
//                    case R.id.txtPlayerName:
//                        startActivity(new Intent(getActivity(), MyAccountScreen.class));
//                        break;
                    case R.id.card_view:
                        if (mPlayerDetail.id == mCurPlayer.id) {
                            return;
                        }

                        if (countSelection >= 5 && !mPlayerDetail.isSelected) {
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
                        if (mPlayerDetail.isSelected) {
                            countSelection += 1;
                            itemBG.setBackgroundColor(itemSelectedColor);
                        } else {
                            countSelection -= 1;
                            if (mPlayerDetail.IsPlayoffQualified) {
                                itemBG.setBackgroundColor(qualifiedItemSelectedColor);
                            } else {
                                itemBG.setBackgroundColor(Color.WHITE);
                            }
                        }


                        if (countSelection == 1) {
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

                            if (frag instanceof FragLeagueDetails) {
                                if (!((FragLeagueDetails) frag).isBottomviewVisible) {
                                    ((FragLeagueDetails) frag).showBottomView(true);
                                }
                            }
                        }
                        if (countSelection == 0) {
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            Fragment frag = fm.findFragmentById(R.id.fragmentContainer);
//                            if (((FragLeagueDetails) frag).mPlayerList.size() <= 0) {
                            if (frag instanceof FragLeagueDetails) {
                                ((FragLeagueDetails) frag).hideBottomView();
                            }
//                            }
                        }
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

                        if (frag instanceof FragLeagueDetails) {
                            ((FragLeagueDetails) frag).updatePlayers(mPlayerDetail);
                        }
                        mCheckFromSelectAll = true;
                        if (((FragLeagueDetails) frag).mPlayerList.size() < mPlayerDetails.size()) {
                            mChkSelectAllLeague.setChecked(false);
                        } else {
                            mChkSelectAllLeague.setChecked(true);
                        }
                        break;
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
            App.LogFacebookEvent(getActivity(), this.getClass().getName());

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
        //        private Competition mCompetition;
        private DivisionBean mCompetition;

        public static FragOtherDivisions getInstance(DivisionBean curCompetition) {
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
            App.LogFacebookEvent(getActivity(), this.getClass().getName());

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

            WSHandle.Leagues.getLeagueDivisions(mCompetition.getDivision_id(), new VolleyHelper.IRequestListener<List<NameIdPair>, String>() {
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

            @BindView(R.id.text1bind)
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
                if (mCompetition.getDivision_id() == mDivision.Id) {
                    FragLeagueDetails.sFragLeagueDetails.setCurrentTab(0);
                    //Toast.makeText(getActivity(), "You belong to this division", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(SingleFragmentActivity.getIntent(getActivity(), FragOtherDivisionDetails.class,
                            FragOtherDivisionDetails.buildArguments(mDivision, mCompetition)));
                }
            }
        }

    }

}
