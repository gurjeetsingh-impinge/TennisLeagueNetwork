package com.tennisdc.tln.modules.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.Competition;
import com.tennisdc.tln.model.DivisionBean;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.modules.league.FragLeagueDetails;
import com.tennisdc.tln.modules.tournament.FragTournamentAll;
import com.tennisdc.tln.modules.tournament.UpcomingTournamentActivity;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.DividerItemDecoration;
import com.tennisdc.tln.ui.RecyclerAdapter;

import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created  on 02-02-2015.
 */
public class FragListSelection extends Fragment {

    private static final String EXTRA_LIST = "object_list";
    public static final String EXTRA_TITLE = "title";
    private static final String EXTRA_SUB_TITLE = "sub_title";

    public static final String RESULT_SELECTION = "selection_object";
    public static final String RESULT_SELECTION_DIVISION = "selection_object_devision";
    public static final String RESULT_STATUS = "result_status";
    public static final String RESULT_START_DATE = "mStartDate";
    public static final String RESULT_COMPETITION_ID = "competition_id";

    public static Intent getInstance(Context context, Parcelable list, String title, String subTitle) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_LIST, list);
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_SUB_TITLE, subTitle);

        return SingleFragmentActivity.getIntent(context, FragListSelection.class, args);
    }

    /* Views*/
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.txtSubTitle)
    TextView mSubTitleTextView;

    /* Data */
    private List<Competition> mDataList;
    String strTitle = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_select_from_list, container, false);

        App.LogFacebookEvent(getActivity(),this.getClass().getName());
        ButterKnife.bind(this, rootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

        rootView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    getActivity().setResult(Activity.RESULT_CANCELED);
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       Bundle args = getArguments();
        if (args == null) throw new RuntimeException("Missing Arguments");
        strTitle = args.getString(EXTRA_TITLE);
        if(strTitle.contains("Tourney")){
            fetchTourneyList();
        }else{
            fetchLeagueList();
        }
        if (!TextUtils.isEmpty(strTitle)) getActivity().setTitle(strTitle);
       /* mDataList = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
        strTitle = args.getString(EXTRA_TITLE);
        String strSubTitle = args.getString(EXTRA_SUB_TITLE);

        if (!TextUtils.isEmpty(strSubTitle)) mSubTitleTextView.setText(strSubTitle);


        mRecyclerView.setAdapter(new RecyclerAdapter<Object, ObjectViewHolder>(mDataList) {

            @Override
            public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_selection_list_item, parent, false);
                return new ObjectViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ObjectViewHolder holder, int position) {
                holder.bindData(getItem(position));
            }

        });*/
    }

    class ObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.text1bind)
        TextView text1;
        @BindView(R.id.text2bind)
        TextView text2;
        @BindView(R.id.headerText)
        TextView headerText;


        public ObjectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void bindData(Object item) {
            text1.setText(((Competition) item).CompetitionName);

            text2.setTextColor(Color.parseColor(((Competition) item).getColor_code()));
            text2.setText(((Competition) item).description);
        }

        @Override
        public void onClick(View v) {
            if(!strTitle.toLowerCase().contains("tourney")) {
//            if (((Competition) mDataList.get(getAdapterPosition())).getDivisions() != null) {
                if(((Competition) mDataList.get(getAdapterPosition())).getDivisions().size() == 1){
                    Bundle data = new Bundle();
                    data.putParcelable(RESULT_SELECTION, Parcels.wrap((Competition) mDataList.get(getAdapterPosition())));
                    data.putParcelable(RESULT_SELECTION_DIVISION,
                            Parcels.wrap((DivisionBean) ((Competition) mDataList.get(getAdapterPosition())).divisions.get(0)));
                    data.putInt(RESULT_STATUS, ((Competition) mDataList.get(getAdapterPosition())).status);
                    data.putString(RESULT_START_DATE, ((Competition) mDataList.get(getAdapterPosition())).getStart_date());
                    data.putLong(RESULT_COMPETITION_ID,((Competition) mDataList.get(getAdapterPosition())).getCompetitionId());
                    Class fragClass = FragLeagueDetails.class;
                    startActivity(SingleFragmentActivity.getIntent(getActivity(), fragClass, data));
                }else {
                    BaseDialog.DivisionDialog dialog = BaseDialog.DivisionDialog.getDialogInstance(null, 0,
                            ((Competition) mDataList.get(getAdapterPosition())).getDivisions(),
                            ((Competition) mDataList.get(getAdapterPosition())).status)
                            .setRecyclerView(((Competition) mDataList.get(getAdapterPosition())));
                    dialog.show(getChildFragmentManager(), "dlg-frag");
                }
            } else {
                Competition mCompetition = ((Competition) mDataList.get(getAdapterPosition()));

//                startActivity(FragListSelection.getInstance(getActivity(), Parcels.wrap(response), "", getString(R.string.alert_msg_select_a_competition)), REQ_COMPETITION);

                if (mCompetition.getType().equalsIgnoreCase("upcoming")) {
                    Intent mIntent = new Intent(getActivity(), UpcomingTournamentActivity.class);
                    mIntent.putExtra("id",mCompetition.getCompetitionId());
                    mIntent.putExtra("mStatus",mCompetition.status);
                    startActivity(mIntent);
                } else {
                    Bundle data = new Bundle();
//                    data.putParcelable(RESULT_SELECTION, Parcels.wrap(mDataList.get(getAdapterPosition())));
//                    data.putInt(RESULT_STATUS, mCompetition.status);
//                    data.putString(RESULT_START_DATE, mCompetition.getStart_date());
//                    data.putLong(RESULT_COMPETITION_ID,mCompetition.getCompetitionId());
//                    Class fragClass = FragTournament.class;
//                    startActivity(SingleFragmentActivity.getIntent(getActivity(), fragClass, data));

//                    Intent data = new Intent();
                    data.putParcelable(RESULT_SELECTION, Parcels.wrap(mDataList.get(getAdapterPosition())));
//                    data.putParcelable(RESULT_SELECTION_DIVISION, Parcels.wrap(mDataList.get(getAdapterPosition())));
                    data.putInt(RESULT_STATUS, mCompetition.status);
                    data.putString(RESULT_START_DATE, mCompetition.getStart_date());
                    data.putLong(RESULT_COMPETITION_ID, mCompetition.getCompetitionId());
                    Class fragClass = FragTournamentAll.class;
                    startActivity(SingleFragmentActivity.getIntent(getActivity(), fragClass, data));
//                    getActivity().setResult(Activity.RESULT_OK, data);
//                    getActivity().finish();
                }
            }
        }
    }
    private void fetchLeagueList() {
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
                } else {
                    mDataList = response;
                    strTitle = "League Report";
//                    String strSubTitle = args.getString(EXTRA_SUB_TITLE);

//                    if (!TextUtils.isEmpty(strSubTitle)) mSubTitleTextView.setText(strSubTitle);

                    if (!TextUtils.isEmpty(strTitle)) getActivity().setTitle(strTitle);

                    mRecyclerView.setAdapter(new RecyclerAdapter<Competition, ObjectViewHolder>(mDataList) {

                        @Override
                        public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_selection_list_item, parent, false);
                            return new ObjectViewHolder(view);
                        }

                        @Override
                        public void onBindViewHolder(ObjectViewHolder holder, int position) {
                            holder.bindData(getItem(position));
                        }

                    });
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });

        VolleyHelper.getInstance(getActivity()).addToRequestQueue(competitionListRequest);
    }


    private void fetchTourneyList() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...",
                true, false);

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
                    BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null,
                            0, getActivity().getString(R.string.alert_msg_tournament_no_data_found));
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
                }
//                  else if (response.size() == 1) {
//                    mCurCompetition = response.get(0);
//                    fetchTournamentReport(mCurCompetition);
//                    fetchTournamentListDetails(mCurCompetition);
//                }
                else {
                    mDataList = response;
                    strTitle = "My Tourney's";
//                    String strSubTitle = args.getString(EXTRA_SUB_TITLE);

//                    if (!TextUtils.isEmpty(strSubTitle)) mSubTitleTextView.setText(strSubTitle);

                    if (!TextUtils.isEmpty(strTitle)) getActivity().setTitle(strTitle);

                    mRecyclerView.setAdapter(new RecyclerAdapter<Competition, ObjectViewHolder>(mDataList) {

                        @Override
                        public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_selection_list_item, parent, false);
                            return new ObjectViewHolder(view);
                        }

                        @Override
                        public void onBindViewHolder(ObjectViewHolder holder, int position) {
                            holder.bindData(getItem(position));
                        }

                    });
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

}
