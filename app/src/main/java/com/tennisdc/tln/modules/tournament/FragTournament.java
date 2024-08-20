package com.tennisdc.tln.modules.tournament;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.model.Competition;
import com.tennisdc.tln.model.TournamentRound;
import com.tennisdc.tln.model.TournamentRoundDetails;
import com.tennisdc.tln.modules.common.FragListSelection;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.TempAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import io.realm.Realm;

/**
 * Created  on 30-May-17.
 */

public class FragTournament extends Fragment {

    private static final int REQ_COMPETITION = 123;
    private static final String EXTRA_COMPETITION = "competition";
    private Realm mRealm;
    @BindView(R.id.expand_tournamentList)
    ExpandableListView mExpandableListView;

    private static TempAdapter mAdapter;
    private ArrayList<TournamentRound> mTournamentList = new ArrayList<TournamentRound>();
    private HashMap<String, List<TournamentRoundDetails>> mTournamentDetailsList = new HashMap<String, List<TournamentRoundDetails>>();
    private Competition mCurCompetition;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_COMPETITION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                mCurCompetition = Parcels.unwrap(data.getParcelableExtra(FragListSelection.RESULT_SELECTION));
                fetchTournamentListDetails(mCurCompetition.CompetitionId);
            } else {
                getActivity().onBackPressed();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_tournament_details, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurCompetition = Parcels.unwrap(getArguments().getParcelable(FragListSelection.RESULT_SELECTION));
        fetchTournamentListDetails(mCurCompetition.CompetitionId);
//        fetchCompetitionList();
        // Setting group indicator null for custom indicator
        mExpandableListView.setGroupIndicator(null);
        setExpandableViewListenser();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("My Tournaments");
    }

  /*  public static Bundle getInstanceArguments(NameIdPair competition) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(competition));

        return args;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRealm = Realm.getInstance(getContext());
    }*/

//    private void fetchCompetitionList() {
//
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);
//        PlayerDetail player = new Prefs.AppData(getActivity()).getPlayer();
//        WSHandle.Tournament.getCompetitionList(player.id, new VolleyHelper.IRequestListener<List<Competition>, String>() {
//
//            @Override
//            public void onFailureResponse(String response) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onSuccessResponse(List<Competition> response) {
//                progressDialog.dismiss();
//                if (response == null || response.size() <= 0) {
//                    BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, getString(R.string.alert_msg_tournament_no_data_found));
//                    dialog.setPositiveButton("Yes", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
//                        }
//                    }).setNegativeButton("No", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            getActivity().onBackPressed();
//                        }
//                    });
//                    dialog.show(getChildFragmentManager(), "dlg-frag");
//                } else if (response.size() == 1) {
//                    mCurCompetition = response.get(0);
//                    fetchTournamentListDetails(mCurCompetition.CompetitionId);
//                } else {
//                    Class fragClass = FragTournamentAll.class;
//                    Bundle data1 = new Bundle();
//                    data1.putString(EXTRA_TITLE, "My Tourney's");
//                    data1.putString(FragListSelection.RESULT_SELECTION, "My Tourney's");
//                    startActivity(SingleFragmentActivity.getIntent(getActivity(), fragClass, data1));
//
//                    startActivityForResult(FragListSelection.getInstance(getActivity(), Parcels.wrap(response), "FragTournament Report", getString(R.string.alert_msg_select_a_competition)), REQ_COMPETITION);
//                }
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void fetchTournamentListDetails(long id){

        getActivity().setTitle(mCurCompetition.CompetitionName);

        //
       final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        WSHandle.Tournament.getTournamentDetailsListJson(id, new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                Log.e("Response",response.toString());
                if (response != null) {
                    Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();
                    final ArrayList<TournamentRound> tournamentRoundList = new ArrayList<TournamentRound>();
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
                    }

                    mTournamentList = tournamentRoundList;
                    createDetailsHashmap(mTournamentList, mTournamentDetailsList);
                    mAdapter = new TempAdapter(getContext(), mTournamentList, mTournamentDetailsList, mCurCompetition); //Sanmati
                    mExpandableListView.setAdapter(mAdapter);

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
       // VolleyHelper.getInstance(getActivity());
    }

    private void createDetailsHashmap(ArrayList<TournamentRound> list, HashMap<String, List<TournamentRoundDetails>> hashMap){
        // Hash map for both header and child
//        HashMap<String, List<TournamentRoundDetails>> hashMap = new HashMap<String, List<TournamentRoundDetails>>();
        hashMap.clear();
        // Adding headers to list
       /* for (int i=0;i<mTournamentList.size();i++) {
            final TournamentRound tournamentRound = mTournamentList.get(i);
            hashMap.put(tournamentRound.getTournamentName(), tournamentRound.getTournamentRoundDeatilsList());
        }*/
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
