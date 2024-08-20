package com.tennisdc.tennisleaguenetwork.modules.submit_score;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tennisleaguenetwork.common.GsonRealmExclusionStrategy;
import com.tennisdc.tennisleaguenetwork.common.Pickers;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.common.ScoreFormat;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tennisleaguenetwork.model.MatchStats;
import com.tennisdc.tennisleaguenetwork.model.NameIdPair;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.PlayerScore;
import com.tennisdc.tennisleaguenetwork.model.ScoreRange;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.ui.CustomSpinnerWithErrorAdapter;
import com.tennisdc.tennisleaguenetwork.ui.DividerItemDecoration;
import com.tennisdc.tennisleaguenetwork.ui.EmptyRecyclerView;
import com.tennisdc.tennisleaguenetwork.ui.RecyclerAdapter;

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
        }        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.frag_select_from_list, container, false);

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



    }

    /**
     * will ask for the details for the match
     * like, date, court, length, score type
     */
    public static class FragSubmitCompetitionDetails extends Fragment {

        private static final String EXTRA_COMPETITION = "competition";
        private List<NameIdPair> mCompetitors;
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
                    startActivity(SingleFragmentActivity.getIntent(getActivity(), FragPlayerScoreBoard.class, FragPlayerScoreBoard.getInstanceArguments(mMatchStats, mCompetitors)));
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
        }        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.frag_match_details, container, false);

            DateTextView = (TextView) rootView.findViewById(R.id.txtDate);
            CourtsSpinner = (Spinner) rootView.findViewById(R.id.spnrCourts);
            MatchLengthTextView = (TextView) rootView.findViewById(R.id.txtLength);
            ScoreFormatsSpinner = (Spinner) rootView.findViewById(R.id.spnrScoreFormats);

            rootView.findViewById(R.id.btnNext).setOnClickListener(NextClickListener);

            DateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pickers.MatchDatePickerFragment pickerDialog = new Pickers.MatchDatePickerFragment() {

                        @Override
                        public long getTime() {
                            return mMatchStats.Date;
                        }

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.YEAR, year);
                            cal.set(Calendar.MONTH, month);
                            cal.set(Calendar.DAY_OF_MONTH, day);
                            mMatchStats.Date = cal.getTimeInMillis();

                            DateTextView.setText(DateUtils.formatDateTime(getActivity(), mMatchStats.Date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
                        }
                    };

                    pickerDialog.show(getChildFragmentManager(), "picker-dlg");
                }
            });

            ScoreFormatsSpinner.setAdapter(new CustomSpinnerWithErrorAdapter<>(getActivity(), ScoreFormat.values()));

            MatchLengthTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DlgMatchLengthPicker dlgMatchLengthPicker = new DlgMatchLengthPicker() {
                        @Override
                        public long getTime() {
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.HOUR_OF_DAY, (int) mMatchStats.Hours);
                            cal.set(Calendar.MINUTE, (int) mMatchStats.Minutes);
                            return cal.getTimeInMillis();
                        }

                        @Override
                        public void onTimeSet(int hourOfDay, int minute) {
                            mMatchStats.Hours = hourOfDay;
                            mMatchStats.Minutes = minute;
                            MatchLengthTextView.setText(hourOfDay + " Hour(s), " + minute + " Minute(s)");
                        }
                    };

                    dlgMatchLengthPicker.show(getFragmentManager(), "length-dlg");

                    /*
                    Pickers.MatchLengthTimePickerFragment pickerDialog = new Pickers.MatchLengthTimePickerFragment() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mMatchStats.Hours = hourOfDay;
                            mMatchStats.Minutes = minute;
                            MatchLengthTextView.setText(hourOfDay + " Hour(s), " + minute + " Minute(s)");
                        }

                        @Override
                        public long getTime() {
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.HOUR_OF_DAY, (int) mMatchStats.Hours);
                            cal.set(Calendar.MINUTE, (int) mMatchStats.Minutes);
                            return cal.getTimeInMillis();
                        }
                    };

                    pickerDialog.show(getChildFragmentManager(), "picker-dlg");*/
                }
            });

            return rootView;
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
                        Type listType = new TypeToken<List<NameIdPair>>() {}.getType();
                        //List<NameIdPair> mCourts = new Gson().fromJson(response.getString("courts"), listType);
                        mCompetitors = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("competitors"), listType);

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
    }

    /**
     * will ask for the player names and score
     * as per the score type selected on previous page
     */
    public static class FragPlayerScoreBoard extends Fragment {

        private static final String EXTRA_MATCH_STATS = "match_stats";
        private static final String EXTRA_COMPETITORS = "competitors";
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
        private NumberPicker[] WinnerScoreNumberPicker, OpponentScoreNumberPicker;
        private boolean IsWinnerScorecardOpen = false;
        private boolean IsOpponentScorecardOpen = false;
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

                if (!mMatchStats.IsLateCancel && !mMatchStats.IsNoShow && !mMatchStats.IsRetired && !mMatchStats.IsHitAround) {
                    if(PlayerScore.isWinnersValidScore(mMatchStats.Winner, mMatchStats.Looser)) {
                        if (!PlayerScore.isValidScore(mMatchStats.Winner, mMatchStats.Looser)) {
                            isSuccess = false;
                            Toast.makeText(getActivity(),R.string.invalid_score_card, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        isSuccess = false;
                        final BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance("Invalid Winner Score", 0, "The correct score format is winning 2 sets, we apologize but we don't allow results where the winner wins all 3 sets. Please don't include the 3rd set results.");
                        dialog.setPositiveButton("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show(getChildFragmentManager(), "InValidScore");
                    }

                }

                if (isSuccess) {
                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Submitting Score...", true, false);

                    Request submitScoreRequest = WSHandle.SubmitScore.getSubmitScoreRequest(new Prefs.AppData(getActivity()).getOAuthToken(), mMatchStats, new VolleyHelper.IRequestListener<String, String>() {

                        @Override
                        public void onFailureResponse(String response) {
                            progressDialog.dismiss();
                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                            dialog.show(getChildFragmentManager(), "dlg-frag");
                        }

                        @Override
                        public void onSuccessResponse(String response) {
                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                            dialog.setNeutralButton("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                                    getActivity().finish();
                                }
                            });
                            dialog.show(getChildFragmentManager(), "dlg-frag");
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
        };

        public static Bundle getInstanceArguments(MatchStats matchStats, List<NameIdPair> competitors) {
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_MATCH_STATS, Parcels.wrap(matchStats));
            args.putParcelable(EXTRA_COMPETITORS, Parcels.wrap(competitors));

            return args;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.frag_score_board, container, false);

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
            List<NameIdPair> competitors = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITORS));

            if(mMatchStats.Competition.Non_League)
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

                boolean isSuccess = true;

                mMatchStats.Comment = MatchNotesEditText.getText().toString().trim();

                mMatchStats.IsTie = TieCheckBox.isChecked();
                mMatchStats.IsLateCancel = CancelledCheckBox.isChecked();
                mMatchStats.IsNoShow = NoShowCheckBox.isChecked();
                mMatchStats.IsRetired = RetiredCheckBox.isChecked();
                mMatchStats.IsHitAround = false;

                if (!mMatchStats.IsLateCancel && !mMatchStats.IsNoShow && !mMatchStats.IsRetired) {
                    if (!PlayerScore.isValidScore(mMatchStats.Winner, mMatchStats.Looser)) {
                        isSuccess = false;
                        Toast.makeText(getActivity(), R.string.invalid_score_card, Toast.LENGTH_LONG).show();
                    }
                }

                if (isSuccess) {
                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Submitting Score...", true, false);

                    Request submitScoreRequest = WSHandle.SubmitScore.getSubmitScoreRequest(new Prefs.AppData(getActivity()).getOAuthToken(), mMatchStats, new VolleyHelper.IRequestListener<String, String>() {

                        @Override
                        public void onFailureResponse(String response) {
                            progressDialog.dismiss();
                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                            dialog.show(getChildFragmentManager(), "dlg-frag");
                        }

                        @Override
                        public void onSuccessResponse(String response) {
                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                            dialog.setNeutralButton("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                                    getActivity().finish();
                                }
                            });
                            dialog.show(getChildFragmentManager(), "dlg-frag");
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

        };

        public static Bundle getInstanceArguments(MatchStats matchStats) {
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_MATCH_STATS, Parcels.wrap(matchStats));

            return args;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.frag_submit_score, container, false);

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
