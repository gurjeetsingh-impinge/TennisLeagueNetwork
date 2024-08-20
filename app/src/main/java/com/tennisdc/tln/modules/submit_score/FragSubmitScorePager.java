package com.tennisdc.tln.modules.submit_score;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.DialogsUtil;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Pickers;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.common.ScoreFormat;
import com.tennisdc.tln.interfaces.OnDialogButtonNuteralClickListener;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tln.model.MatchStats;
import com.tennisdc.tln.model.NameIdPair;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.model.PlayerInformationModel;
import com.tennisdc.tln.model.PlayerScore;
import com.tennisdc.tln.model.ScoreRange;
import com.tennisdc.tln.modules.common.DialogClickListener;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.CustomSpinnerWithErrorAdapter;
import com.tennisdc.tln.ui.DividerItemDecoration;
import com.tennisdc.tln.ui.EmptyRecyclerView;
import com.tennisdc.tln.ui.RecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import io.realm.Realm;

/**
 * Created  on 21-01-2015.
 */
public class FragSubmitScorePager extends Fragment {

    /**
     * on selecting any item in the competition list
     * will go to next page
     * for submitting its details
     */
    public static class FragSelectCompetition extends Fragment {

        private EmptyRecyclerView mCompetitionRecyclerView;

        private List<NameIdPair> mCompetitions;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.frag_select_from_list, container, false);
            App.LogFacebookEvent(getActivity(),this.getClass().getName());

            mCompetitionRecyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.recyclerView);
            mCompetitionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            mCompetitionRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

            Request competitionListRequest = WSHandle.SubmitScore.getCompetitionListRequest(new Prefs.AppData(getActivity()).getOAuthToken(), new VolleyHelper.IRequestListener<List<NameIdPair>, String>() {

                @Override
                public void onFailureResponse(String response) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessResponse(List<NameIdPair> response) {
                    mCompetitions = response;

                    if (mCompetitions == null || mCompetitions.size() <= 0) {
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, getString(R.string.alert_msg_league_no_data_found));
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
                    }

                    if (mCompetitions.size() == 1) {
                        startActivity(SingleFragmentActivity.getIntent(getActivity(), FragSubmitCompetitionDetails.class, FragSubmitCompetitionDetails.getInstanceArguments(mCompetitions.get(0))));
                    }

                    mCompetitionRecyclerView.setAdapter(new RecyclerAdapter<NameIdPair, CompetitionViewHolder>(mCompetitions) {

                        @Override
                        public CompetitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_competition_item, parent, false);
                            return new CompetitionViewHolder(view);
                        }

                        @Override
                        public void onBindViewHolder(CompetitionViewHolder holder, int position) {
                            holder.bindData(getItem(position));
                        }

                    });

                    progressDialog.dismiss();
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
        public void onResume() {
            super.onResume();
            getActivity().setTitle("Submit Score");
        }

        private class CompetitionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView NameTextView;
            private NameIdPair mNameIdPair;

            public CompetitionViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                NameTextView = (TextView) itemView.findViewById(R.id.txtName);
            }

            public void bindData(NameIdPair nameIdPair) {
                mNameIdPair = nameIdPair;
                NameTextView.setText(mNameIdPair.Name);
            }

            @Override
            public void onClick(View v) {
                startActivity(SingleFragmentActivity.getIntent(getActivity(), FragSubmitCompetitionDetails.class, FragSubmitCompetitionDetails.getInstanceArguments(mNameIdPair)));
            }
        }


    }

    /**
     * will ask for the details for the match
     * like, date, court, length, score type
     */
    public static class FragSubmitCompetitionDetails extends Fragment implements DialogClickListener {

        private static final String EXTRA_COMPETITION = "competition";
        private List<NameIdPair> mCompetitors;
        private boolean mShowWalkOver = false;
        private Context mContext;
        private Realm mRealm;
        private MatchStats mMatchStats;
        private TextView DateTextView;
        private Spinner CourtsSpinner;
        private TextView MatchLengthTextView;
        private Spinner ScoreFormatsSpinner;
        private View.OnClickListener NextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSuccess = true;

                Court court = (Court) CourtsSpinner.getSelectedItem();
                if (court.getId() <= 0) {
                    isSuccess = false;
                    ((CustomSpinnerWithErrorAdapter) CourtsSpinner.getAdapter()).setError(CourtsSpinner.getSelectedView(), "Please select a Location");
                }

                ScoreFormat scoreFormat = (ScoreFormat) ScoreFormatsSpinner.getSelectedItem();

                mMatchStats.Court = court;
                mMatchStats.ScoreFormatName = scoreFormat.name();

                if (isSuccess) {
                    //TODO Next Page
                    startActivity(SingleFragmentActivity.getIntent(getActivity(), FragPlayerScoreBoard.class, FragPlayerScoreBoard.getInstanceArguments(mMatchStats, mCompetitors, mShowWalkOver)));
                }
            }
        };

        public static Bundle getInstanceArguments(NameIdPair competition) {
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(competition));

            return args;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mContext = activity;
            mRealm = Realm.getInstance(mContext);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.frag_match_details, container, false);
            App.LogFacebookEvent(getActivity(),this.getClass().getName());

            DateTextView = (TextView) rootView.findViewById(R.id.txtDate);
            CourtsSpinner = (Spinner) rootView.findViewById(R.id.spnrCourts);
            MatchLengthTextView = (TextView) rootView.findViewById(R.id.txtLength);
            ScoreFormatsSpinner = (Spinner) rootView.findViewById(R.id.spnrScoreFormats);

            rootView.findViewById(R.id.btnNext).setOnClickListener(NextClickListener);

            DateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pickers.MatchDatePickerFragment pickerDialog = Pickers.MatchDatePickerFragment.newInstance(mMatchStats.Date);
                    pickerDialog.setTargetFragment(FragSubmitCompetitionDetails.this, 67);
                    pickerDialog.show(getFragmentManager(), "picker-dlg");
                }
            });

            ScoreFormatsSpinner.setAdapter(new CustomSpinnerWithErrorAdapter<>(getActivity(), ScoreFormat.values()));

            MatchLengthTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, (int) mMatchStats.Hours);
                    cal.set(Calendar.MINUTE, (int) mMatchStats.Minutes);

                    DlgMatchLengthPicker dlgMatchLengthPicker = DlgMatchLengthPicker.newInstance(cal.getTimeInMillis());
                    dlgMatchLengthPicker.setTargetFragment(FragSubmitCompetitionDetails.this, 68);
                    dlgMatchLengthPicker.show(getFragmentManager(), "length-dlg");
                }
            });

            return rootView;
        }

        public void onDatePicked(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            mMatchStats.Date = cal.getTimeInMillis();

            DateTextView.setText(DateUtils.formatDateTime(getActivity(), mMatchStats.Date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        }

        public void onTimePicked(int hourOfDay, int minute) {
            mMatchStats.Hours = hourOfDay;
            mMatchStats.Minutes = minute;
            MatchLengthTextView.setText(hourOfDay + " Hour(s), " + minute + " Minute(s)");
        }

        @Override
        public void onResume() {
            super.onResume();

            getActivity().setTitle(mMatchStats.Competition == null ? "Submit Score" : mMatchStats.Competition.Name);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Bundle args = getArguments();
            if (args == null) throw new RuntimeException("Missing Arguments");

            mMatchStats = new MatchStats();

            mMatchStats.Competition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));

            getActivity().setTitle(mMatchStats.Competition.Name);

            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

            Request competitionListRequest = WSHandle.SubmitScore.getCompetitorsRequest(new Prefs.AppData(getActivity()).getOAuthToken(), mMatchStats.Competition.Id, new VolleyHelper.IRequestListener<JSONObject, String>() {

                @Override
                public void onFailureResponse(String response) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessResponse(JSONObject response) {
                    try {
                        Type listType = new TypeToken<List<NameIdPair>>() {
                        }.getType();
                        //List<NameIdPair> mCourts = new Gson().fromJson(response.getString("courts"), listType);
                        mCompetitors = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("competitors"), listType);
                        mShowWalkOver = response.getBoolean("show_walkover_option");
                        List<Court> mCourts = new ArrayList<Court>();

                        Court court = new Court();
                        court.setId(0);
                        court.setName("Select Match Location");
                        mCourts.add(court);

                        court = new Court();
                        court.setId(1);
                        court.setName("New Court (specify in Notes Field)");
                        mCourts.add(court);

                        List<Court> courts = mRealm.allObjects(Court.class);
                        mCourts.addAll(courts);

                        DateTextView.setText(DateUtils.formatDateTime(getActivity(), mMatchStats.Date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));

                        CourtsSpinner.setAdapter(new CustomSpinnerWithErrorAdapter.CourtsAdapter(getActivity(), mCourts));

                        PlayerDetail player = new Prefs.AppData(getActivity()).getPlayer();

                        for (int i = 0; i < mCourts.size(); i++)
                            if (player.HomeCourt.getId() == mCourts.get(i).getId()) {
                                CourtsSpinner.setSelection(i);
                                break;
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

            VolleyHelper.getInstance(getActivity()).addToRequestQueue(competitionListRequest);
        }


        @Override
        public void onDetach() {
            super.onDetach();
            mContext = null;
            mRealm.close();
            mRealm = null;
        }

        @Override
        public void onSendClick() {

        }

        @Override
        public void onNoClick() {

        }
    }

    /**
     * will ask for the player names and score
     * as per the score type selected on previous page
     */
    public static class FragPlayerScoreBoard extends Fragment {

        private static final String EXTRA_MATCH_STATS = "match_stats";
        private static final String EXTRA_COMPETITORS = "competitors";
        private static final String EXTRA_SHOWWALKOVER = "mShowWalkOver";
        @BindView(R.id.spnrWinner)
        Spinner WinnerSpinner;
        @BindView(R.id.spnrOpponent)
        Spinner OpponentSpinner;
        @BindView(R.id.vwWinnerScoreContainer)
        LinearLayout WinnerScoreContainer;
        @BindView(R.id.vwOpponentScoreContainer)
        LinearLayout OpponentScoreContainer;
        @BindView(R.id.btnWinnerScore)
        Button WinnerScoreButton;
        @BindView(R.id.btnOpponentScore)
        Button OpponentScoreButton;
        @BindView(R.id.chkBxEndedInTie)
        CheckBox TieCheckBox;
        @BindView(R.id.chkShowWalkOver)
        CheckBox ShowWalkOverTieCheckBox;
        @BindView(R.id.chkBxRetired)
        CheckBox RetiredCheckBox;
        @BindView(R.id.chkBxCancelled)
        CheckBox CancelledCheckBox;
        @BindView(R.id.chkBxNoShow)
        CheckBox NoShowCheckBox;
        @BindView(R.id.chkBxHitAround)
        CheckBox HitAroundCheckBox;
        @BindView(R.id.edtMatchNotes)
        EditText MatchNotesEditText;
        PlayerScore WinnerPlayerScore = new PlayerScore();
        PlayerScore OpponentPlayerScore = new PlayerScore();
        private PlayerDetail mPlayer;
        private MatchStats mMatchStats;
        private boolean mShowWalkOver;
        List<NameIdPair> competitorsTemp;
        private NumberPicker[] WinnerScoreNumberPicker, OpponentScoreNumberPicker;
        private boolean IsWinnerScorecardOpen = false;
        private boolean IsOpponentScorecardOpen = false;
        BaseDialog.SimpleDialog mResponseDialog;


        CallbackManager callbackManager;
        ShareDialog shareDialog;


        private View.OnClickListener NextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSuccess = true;

                WinnerPlayerScore.Player = (NameIdPair) WinnerSpinner.getSelectedItem();
                OpponentPlayerScore.Player = (NameIdPair) OpponentSpinner.getSelectedItem();

                if (WinnerPlayerScore.Player.equals(OpponentPlayerScore.Player)) {
                    isSuccess = false;
                    //((CustomSpinnerWithErrorAdapter) WinnerSpinner.getAdapter()).setError(WinnerSpinner.getSelectedView(), "Winner & Opponent should be different.");
                    ((CustomSpinnerWithErrorAdapter) OpponentSpinner.getAdapter()).setError(OpponentSpinner.getSelectedView(), "Winner & Opponent should be different.");
                }

                if (IsWinnerScorecardOpen) WinnerScoreButton.performClick();

                if (IsOpponentScorecardOpen) OpponentScoreButton.performClick();

                //if (winnerPlayerScore.totalScore() < opponentPlayerScore.totalScore()) {
                /*if(!PlayerScore.isValidScore(winnerPlayerScore, opponentPlayerScore)){
                    isSuccess = false;
                    Toast.makeText(getActivity(), "Invalid Score Cards", Toast.LENGTH_LONG).show();
                }*/

                //if (isSuccess) {
                mMatchStats.Winner = WinnerPlayerScore;
                mMatchStats.Looser = OpponentPlayerScore;
                //startActivity(SingleFragmentActivity.getIntent(getActivity(), FragSubmitScore.class, FragSubmitScore.getInstanceArguments(mMatchStats)));
                    /*DlgConfirmScore.getDialogInstanceLeagues(mMatchStats.ScoreFormatName, winnerPlayerScore, opponentPlayerScore).setPositiveButton("Confirm", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.getIntent(getActivity(), FragSubmitScore.class, FragSubmitScore.getInstanceArguments(mMatchStats)));
                        }
                    }).show(getChildFragmentManager(), "dlg-confirm");*/
                //}

                mMatchStats.Comment = MatchNotesEditText.getText().toString().trim();

                mMatchStats.IsTie = TieCheckBox.isChecked();
                mMatchStats.IsLateCancel = CancelledCheckBox.isChecked();
                mMatchStats.IsNoShow = NoShowCheckBox.isChecked();
                mMatchStats.IsRetired = RetiredCheckBox.isChecked();
                mMatchStats.IsHitAround = HitAroundCheckBox.isChecked(); //false;
                mMatchStats.IsShowWalkOver = ShowWalkOverTieCheckBox.isChecked(); //false;
//                boolean mShowWalkOverValue = ShowWalkOverTieCheckBox.isChecked();
                if (mMatchStats.IsShowWalkOver) {
                    mMatchStats.Winner.setSet1(0);
                    mMatchStats.Winner.setSet2(0);
                    mMatchStats.Winner.setSet3(0);
                    mMatchStats.Winner.setSet3Tie(0);
                    mMatchStats.Looser.setSet1(0);
                    mMatchStats.Looser.setSet2(0);
                    mMatchStats.Looser.setSet3(0);
                    mMatchStats.Looser.setSet3Tie(0);
                }
//				if (!mMatchStats.IsTie && !mMatchStats.IsLateCancel && !mMatchStats.IsNoShow && !mMatchStats.IsRetired && !mMatchStats.IsHitAround) {
//					if (PlayerScore.isWinnersValidScore(mMatchStats.Winner, mMatchStats.Looser)) {
//						if (!PlayerScore.isValidScore(mMatchStats.Winner, mMatchStats.Looser)) {
//							isSuccess = false;
//							Toast.makeText(getActivity(), R.string.invalid_score_card, Toast.LENGTH_LONG).show();
//						}
//					} else {
//						isSuccess = false;
//						final BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance("Invalid Winner Score",
//								0, "The correct score format is winning 2 sets, we apologize but we don't allow results where the winner wins all 3 sets. Please don't include the 3rd set results.");
//						dialog.setPositiveButton("Ok", new View.OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								dialog.dismiss();
//							}
//						});
//						dialog.show(getChildFragmentManager(), "InValidScore");
//					}
//
//				}

                if (isSuccess) {
                    String mSharingQuote = "";
                    if (!mMatchStats.IsShowWalkOver) {
                        String mWinnerName = WinnerPlayerScore.Player.Name;
                        String[] mNameSplit = WinnerPlayerScore.Player.Name.split(",");
                        if (mNameSplit.length > 1) {
                            mWinnerName = mNameSplit[1] + " " + mNameSplit[0];
                        } else if (mNameSplit.length > 0) {
                            mWinnerName = mNameSplit[0];
                        }
                        String mWinnerText = mWinnerName + /*" (" + String.valueOf(Integer.valueOf(WinnerPlayerScore.Player.SeasonRecordWin)+1)
                                +"-"+ WinnerPlayerScore.Player.SeasonRecordLoss+")*/ " over ";


                        String mOpponentName = OpponentPlayerScore.Player.Name;
                        String[] mNameOSplit = OpponentPlayerScore.Player.Name.split(",");
                        if (mNameOSplit.length > 1) {
                            mOpponentName = mNameOSplit[1] + " " + mNameOSplit[0];
                        } else if (mNameOSplit.length > 0) {
                            mOpponentName = mNameOSplit[0];
                        }
                        String mOpponentText = mOpponentName + /*+ " (" + OpponentPlayerScore.Player.SeasonRecordWin
                                +"-"+ String.valueOf(Integer.valueOf(OpponentPlayerScore.Player.SeasonRecordLoss)+1)+")*/" ";

                        String mSet1Text = "";
//                        if(WinnerPlayerScore.getSet1() > 0 || OpponentPlayerScore.getSet1() > 0){
                        mSet1Text = WinnerPlayerScore.getSet1() + "-" + OpponentPlayerScore.getSet1();
//                        }
                        String mSet2Text = "";
//                        if(WinnerPlayerScore.getSet2() > 0 || OpponentPlayerScore.getSet2() > 0){
                        mSet2Text = ", " + WinnerPlayerScore.getSet2() + "-" + OpponentPlayerScore.getSet2();
//                        }
                        String mSet3Text = "";
                        if (WinnerPlayerScore.getSet3() > 0 || OpponentPlayerScore.getSet3() > 0) {
                            mSet3Text = ", " + WinnerPlayerScore.getSet3() + "-" + OpponentPlayerScore.getSet3();
                        }
                        String mScoreText = "by the score of (" + mSet1Text + mSet2Text + mSet3Text + ")";


                        mSharingQuote = mWinnerText + mOpponentText + mScoreText;

//                        new DialogsUtil().openAlertDialog(getActivity(), "Do you want to post this score on facebook?", "Yes", "No",
//								new OnDialogButtonClickListener() {
//									@Override
//									public void onPositiveButtonClicked() {
//										shareWithFb(mSharingQuote);
//									}
//
//									@Override
//									public void onNegativeButtonClicked() {
//										startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
//										getActivity().finish();
//									}
//								});
                    }

                    submitScore(false, mSharingQuote);
                }
            }
        };

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        private void shareWithFb(String mSharingQuote, JSONObject response) {
            boolean facebookAppFound = false;
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mSharingQuote);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("www.facebook.com"));

            PackageManager pm = getActivity().getPackageManager();
            List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
            for (int i = 0; i < activityList.size(); i++) {
                ResolveInfo app = activityList.get(i);
                if (app.activityInfo.packageName.contains("com.facebook.katana")) {
                    ActivityInfo activityInfo = app.activityInfo;
                    ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    shareIntent.setComponent(name);
                    facebookAppFound = true;
                    break;
                }
            }
            if (facebookAppFound) {
                PlayerInformationModel.UserDataBean mUserData = new Gson().fromJson(new Prefs.AppData(getActivity()).getUserData(), new TypeToken<PlayerInformationModel.UserDataBean>() {
                }.getType());
                callbackManager = CallbackManager.Factory.create();
                shareDialog = new ShareDialog(getActivity());
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setQuote(mSharingQuote)
                            .setContentUrl(Uri.parse(new Prefs.AppData(getActivity()).getDomainName() + "/?From=" + mUserData.getUser_id()))
                            .build();
                    shareDialog.show(linkContent);
                }
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        showResponseDialog(response);
                    }

                    @Override
                    public void onCancel() {
                        showResponseDialog(response);
//                        startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
//                        getActivity().finish();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        showResponseDialog(response);
//                        startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
//                        getActivity().finish();
                    }
                });
//				String sharerUrl =
//						"https://www.facebook.com/sharer/sharer.php?u=" + mSharingData;
//				shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            } else {
                startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                getActivity().finish();
                Toast.makeText(getActivity(), " Your device don't have facebook app installed to share score on facebook.", Toast.LENGTH_LONG).show();

            }
//			startActivity(shareIntent);
        }

        public static Bundle getInstanceArguments(MatchStats matchStats, List<NameIdPair> competitors, boolean mShowWalkOver) {
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_MATCH_STATS, Parcels.wrap(matchStats));
            args.putParcelable(EXTRA_COMPETITORS, Parcels.wrap(competitors));
            args.putBoolean(EXTRA_SHOWWALKOVER, mShowWalkOver);

            return args;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.frag_score_board, container, false);
            App.LogFacebookEvent(getActivity(),this.getClass().getName());

            ButterKnife.bind(this, rootView);

            rootView.findViewById(R.id.btnNext).setOnClickListener(NextClickListener);

            WinnerScoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!IsWinnerScorecardOpen) {
                        WinnerScoreContainer.setVisibility(View.VISIBLE);
                        WinnerScoreButton.setText("Done");
                    } else {
                        ScoreFormat scoreFormat = mMatchStats.getScoreFormat();
                        WinnerPlayerScore.submitScore(WinnerScoreNumberPicker, scoreFormat);

                        WinnerScoreButton.setText(WinnerPlayerScore.getScoreString(scoreFormat));
                        WinnerScoreContainer.setVisibility(View.GONE);
                    }
                    IsWinnerScorecardOpen = !IsWinnerScorecardOpen;
                }
            });

            OpponentScoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!IsOpponentScorecardOpen) {
                        OpponentScoreContainer.setVisibility(View.VISIBLE);
                        OpponentScoreButton.setText("Done");
                    } else {
                        ScoreFormat scoreFormat = mMatchStats.getScoreFormat();
                        OpponentPlayerScore.submitScore(OpponentScoreNumberPicker, scoreFormat);

                        OpponentScoreButton.setText(OpponentPlayerScore.getScoreString(scoreFormat));
                        OpponentScoreContainer.setVisibility(View.GONE);
                    }
                    IsOpponentScorecardOpen = !IsOpponentScorecardOpen;
                }
            });

            NoShowCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        CancelledCheckBox.setChecked(false);
                    }
                }
            });

            CancelledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        NoShowCheckBox.setChecked(false);
                    }
                }
            });

            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getActivity().setTitle("Submit Score");

            Bundle args = getArguments();
            if (args == null) throw new RuntimeException("Missing Arguments");

            mMatchStats = Parcels.unwrap(args.getParcelable(EXTRA_MATCH_STATS));
            mShowWalkOver = args.getBoolean(EXTRA_SHOWWALKOVER);
            if (mShowWalkOver) {
                ShowWalkOverTieCheckBox.setVisibility(View.VISIBLE);
            } else {
                ShowWalkOverTieCheckBox.setVisibility(View.GONE);
            }
            List<NameIdPair> competitors = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITORS));
            competitorsTemp = competitors;
            if (mMatchStats.Competition.Non_League)
                HitAroundCheckBox.setVisibility(View.VISIBLE);

            ScoreFormat scoreFormat = mMatchStats.getScoreFormat();
            int setCount = scoreFormat.getSetCount();
            ScoreRange[] scoreRanges = scoreFormat.getScoreRanges();
            String[] setLabels = scoreFormat.getSetLabels();
            int[] defaultScore = scoreFormat.getWinnerDefaultScore();

            WinnerScoreNumberPicker = new NumberPicker[setCount];
            OpponentScoreNumberPicker = new NumberPicker[setCount];

            for (int i = 0; i < setCount; i++) {
                LinearLayout viewWinner = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.view_score_picker, WinnerScoreContainer, false);
                viewWinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                WinnerScoreNumberPicker[i] = (NumberPicker) viewWinner.findViewById(R.id.numPkrScore);

                ((TextView) viewWinner.findViewById(R.id.txtLabel)).setText(setLabels[i]);
                WinnerScoreNumberPicker[i].setMaxValue(scoreRanges[i].max);
                WinnerScoreNumberPicker[i].setMinValue(scoreRanges[i].min);
                WinnerScoreNumberPicker[i].setValue(defaultScore[i]);

                WinnerScoreContainer.addView(viewWinner);

                LinearLayout viewOpponent = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.view_score_picker, OpponentScoreContainer, false);
                viewOpponent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                OpponentScoreNumberPicker[i] = (NumberPicker) viewOpponent.findViewById(R.id.numPkrScore);

                ((TextView) viewOpponent.findViewById(R.id.txtLabel)).setText(setLabels[i]);
                OpponentScoreNumberPicker[i].setMaxValue(scoreRanges[i].max);
                OpponentScoreNumberPicker[i].setMinValue(scoreRanges[i].min);

                OpponentScoreContainer.addView(viewOpponent);
            }

            WinnerSpinner.setAdapter(new CustomSpinnerWithErrorAdapter<>(getActivity(), competitors));

            OpponentSpinner.setAdapter(new CustomSpinnerWithErrorAdapter<>(getActivity(), competitors));

            mPlayer = new Prefs.AppData(getActivity()).getPlayer();
            if (mPlayer != null && mPlayer.id > 0) {
                int i = 0;
                for (; i < competitors.size(); i++) {
                    if (competitors.get(i).Id == mPlayer.id) {
                        break;
                    }
                }

                WinnerSpinner.setSelection(i);
                OpponentSpinner.setSelection(i);
            }

            WinnerPlayerScore.submitScore(WinnerScoreNumberPicker, scoreFormat);
            OpponentPlayerScore.submitScore(OpponentScoreNumberPicker, scoreFormat);

            WinnerScoreButton.setText(WinnerPlayerScore.getScoreString(mMatchStats.getScoreFormat()));
            OpponentScoreButton.setText(OpponentPlayerScore.getScoreString(mMatchStats.getScoreFormat()));

            WinnerScoreButton.performClick();
            OpponentScoreButton.performClick();
        }

        private void showResponseDialog(JSONObject response){
            try{
//                if(mResponseDialog != null && mResponseDialog.isVisible()){
//                    return;
//                }
                mResponseDialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response.getString("responseMessage"));
                mResponseDialog.setNeutralButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                        getActivity().finish();
                    }
                });
                mResponseDialog.show(getChildFragmentManager(), "dlg-frag");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void submitScore(boolean allow_same_score, String mSharingQuote) {
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Submitting Score...", true, false);

            Request submitScoreRequest = WSHandle.SubmitScore.getSubmitScoreRequest(new Prefs.AppData(getActivity()).getOAuthToken(),
                    mMatchStats, allow_same_score, new VolleyHelper.IRequestListener<JSONObject, JSONObject>() {

                        @Override
                        public void onFailureResponse(JSONObject response) {
                            progressDialog.dismiss();
                            String mMsgString = "";
                            try {
                                mMsgString = response.getString("responseMessage");
                                mMsgString = mMsgString + "\n\n Date : " + response.getJSONObject("score").getString("date");
                                mMsgString = mMsgString + "\n Score : " + response.getJSONObject("score").getString("score");;
                                mMsgString = mMsgString + "\n Opponent : " + response.getJSONObject("score").getString("opponent");;
                                mMsgString = mMsgString + "\n Winner : " + response.getJSONObject("score").getString("winner");;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, mMsgString);
                            dialog.setPositiveButton(getActivity().getString(R.string.yes), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    submitScore(true,mSharingQuote);
                                }
                            });
                            dialog.setNegativeButton(getActivity().getString(R.string.no), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show(getChildFragmentManager(), "dlg-frag");
                        }

                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            progressDialog.dismiss();
                            try {
                                if (response.getString("responseCode").equals("200")) {
                                    if (mMatchStats.IsShowWalkOver || mMatchStats.IsLateCancel || mMatchStats.IsNoShow) {

                                        startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                                        getActivity().finish();

                                    } else {
                                        if (new Prefs.AppData(getActivity()).getFacebookSharing() || !new Prefs.AppData(getActivity()).getFacebookSharingOption()) {
//                                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response.getString("responseMessage"));
//                                            dialog.setNeutralButton("Ok", new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
                                            new DialogsUtil().openAlertDialogWithCustomTitle(getActivity(), getActivity().getString(R.string.share_score), getActivity().getString(R.string.share_score_string),
                                                    "Opt-out of sharing", "Don't Share", "Share",
                                                    new OnDialogButtonNuteralClickListener() {
                                                        @Override
                                                        public void onPositiveButtonClicked() {
                                                            new Prefs.AppData(getActivity()).setFacebookSharing(false);
                                                            new Prefs.AppData(getActivity()).setFacebookSharingOption(true);
                                                            showResponseDialog(response);
                                                        }

                                                        @Override
                                                        public void onNegativeButtonClicked() {
                                                            showResponseDialog(response);
                                                        }

                                                        @Override
                                                        public void onNuteralButtonClicked() {
                                                            shareWithFb(mSharingQuote,response);

                                                        }

                                                    });
//                                                }
//                                            });
//                                            dialog.show(getChildFragmentManager(), "dlg-frag");
                                        } else {
                                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response.getString("responseMessage"));
                                            dialog.setNeutralButton("Ok", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                                                    getActivity().finish();
                                                }
                                            });
                                            dialog.show(getChildFragmentManager(), "dlg-frag");
                                        }
                                    }
                                } else if (response.getString("responseCode").equals("401")) {
                                    BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response.getString("responseMessage"));
                                    dialog.setPositiveButton("Yes", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            submitScore(true, mSharingQuote);
                                        }
                                    });
                                    dialog.setPositiveButton("No", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            resetPage();
                                        }
                                    });
                                    dialog.show(getChildFragmentManager(), "dlg-frag");
                                }
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                        }
                    });

            VolleyHelper.getInstance(getActivity()).addToRequestQueue(submitScoreRequest);
        }

        private void resetPage() {
            startActivity(SingleFragmentActivity.getIntent(getActivity(), FragPlayerScoreBoard.class,
                    FragPlayerScoreBoard.getInstanceArguments(mMatchStats, competitorsTemp, mShowWalkOver)));

        }
    }

    public static class FragSubmitScore extends Fragment {

        private static final String EXTRA_MATCH_STATS = "match_stats";
        @BindView(R.id.chkBxEndedInTie)
        CheckBox TieCheckBox;
        @BindView(R.id.chkBxRetired)
        CheckBox RetiredCheckBox;
        @BindView(R.id.chkBxCancelled)
        CheckBox CancelledCheckBox;
        @BindView(R.id.chkBxNoShow)
        CheckBox NoShowCheckBox;
        @BindView(R.id.chkBxHitAround)
        CheckBox HitAroundCheckBox;
        @BindView(R.id.edtMatchNotes)
        EditText MatchNotesEditText;
        @BindView(R.id.txtWinner)
        TextView WinnerTextView;
        @BindView(R.id.txtWinnerScore)
        TextView WinnerScoreTextView;
        @BindView(R.id.txtOpponent)
        TextView OpponentTextView;
        @BindView(R.id.txtOpponentScore)
        TextView OpponentScoreTextView;
        private MatchStats mMatchStats;
        private View.OnClickListener NextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitScore(false);
            }

        };

        void submitScore(boolean allow_same_score){
            {

                boolean isSuccess = true;

                mMatchStats.Comment = MatchNotesEditText.getText().toString().trim();

                mMatchStats.IsTie = TieCheckBox.isChecked();
                mMatchStats.IsLateCancel = CancelledCheckBox.isChecked();
                mMatchStats.IsNoShow = NoShowCheckBox.isChecked();
                mMatchStats.IsRetired = RetiredCheckBox.isChecked();
                mMatchStats.IsHitAround = false;

                if (!mMatchStats.IsTie && !mMatchStats.IsLateCancel && !mMatchStats.IsNoShow && !mMatchStats.IsRetired) {
                    if (!PlayerScore.isValidScore(mMatchStats.Winner, mMatchStats.Looser)) {
                        isSuccess = false;
                        Toast.makeText(getActivity(), R.string.invalid_score_card, Toast.LENGTH_LONG).show();
                    }
                }

                if (isSuccess) {
                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Submitting Score...", true, false);

                    Request submitScoreRequest = WSHandle.SubmitScore.getSubmitScoreRequest(new Prefs.AppData(getActivity()).getOAuthToken(),
                            mMatchStats, allow_same_score, new VolleyHelper.IRequestListener<JSONObject, JSONObject>() {

                                @Override
                                public void onFailureResponse(JSONObject response) {
                                    progressDialog.dismiss();
                                    String mMsgString = "";
                                    try {
                                        mMsgString = response.getString("responseMessage");
                                        mMsgString = mMsgString + "\n\n Date : " + response.getJSONObject("score").getString("date");
                                        mMsgString = mMsgString + "\n Score : " + response.getJSONObject("score").getString("score");;
                                        mMsgString = mMsgString + "\n Opponent : " + response.getJSONObject("score").getString("opponent");;
                                        mMsgString = mMsgString + "\n Winner : " + response.getJSONObject("score").getString("winner");;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, mMsgString);
                                    dialog.setPositiveButton(getActivity().getString(R.string.yes), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                            submitScore(true);
                                        }
                                    });
                                    dialog.setNegativeButton(getActivity().getString(R.string.no), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show(getChildFragmentManager(), "dlg-frag");
                                }

                                @Override
                                public void onSuccessResponse(JSONObject response) {
                                    try {
                                        if (response.getString("responseCode").equals("200")) {
                                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response.getString("responseMessage"));
                                            dialog.setNeutralButton("Ok", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                                                    getActivity().finish();
                                                }
                                            });
                                            dialog.show(getChildFragmentManager(), "dlg-frag");
                                        }
                                    } catch (JSONException | NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                                }
                            });

                    VolleyHelper.getInstance(getActivity()).addToRequestQueue(submitScoreRequest);
                }
            }
        }
        public static Bundle getInstanceArguments(MatchStats matchStats) {
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_MATCH_STATS, Parcels.wrap(matchStats));

            return args;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.frag_submit_score, container, false);
            App.LogFacebookEvent(getActivity(),this.getClass().getName());

            ButterKnife.bind(this, rootView);

            rootView.findViewById(R.id.btnSubmit).setOnClickListener(NextClickListener);

            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getActivity().setTitle("Submit Score");

            Bundle args = getArguments();
            if (args == null) throw new RuntimeException("Missing Arguments");

            mMatchStats = Parcels.unwrap(args.getParcelable(EXTRA_MATCH_STATS));

            WinnerTextView.setText(mMatchStats.Winner.Player.Name);
            WinnerScoreTextView.setText(mMatchStats.Winner.getScoreString(mMatchStats.getScoreFormat()));

            OpponentTextView.setText(mMatchStats.Looser.Player.Name);
            OpponentScoreTextView.setText(mMatchStats.Looser.getScoreString(mMatchStats.getScoreFormat()));
        }
    }
}
