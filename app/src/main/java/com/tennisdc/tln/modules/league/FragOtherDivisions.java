//package com.tennisdc.tln.modules.league;
//
//import android.os.Bundle;
//import androidx.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.VolleyError;
//import com.tennisdc.tln.R;
//import com.tennisdc.tln.SingleFragmentActivity;
//import com.tennisdc.tln.model.DivisionBean;
//import com.tennisdc.tln.model.NameIdPair;
//import com.tennisdc.tln.network.VolleyHelper;
//import com.tennisdc.tln.network.WSHandle;
//import com.tennisdc.tln.ui.DividerItemDecoration;
//import com.tennisdc.tln.ui.RecyclerAdapter;
//
//import org.parceler.Parcels;
//
//import java.util.List;
//
//import butterknife.ButterKnife;
//import butterknife.BindView;
//
//public class FragOtherDivisions extends Fragment {
//
//    private static final String EXTRA_COMPETITION = "CurCompetition";
//    //        private Competition mCompetition;
//    private DivisionBean mCompetition;
//
//    public static FragOtherDivisions getInstance(DivisionBean curCompetition) {
//        FragOtherDivisions fragSubmittedScore = new FragOtherDivisions();
//
//        Bundle args = new Bundle();
//        args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curCompetition));
//
//        fragSubmittedScore.setArguments(args);
//
//        return fragSubmittedScore;
//    }
//
//    /* Views */
//    @BindView(R.id.recyclerView)
//    RecyclerView mRecyclerView;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.frag_recycler_view, container, false);
//
//        ButterKnife.bind(this, v);
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));
//
//        return v;
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        Bundle args = getArguments();
//        if (args == null) throw new RuntimeException("Missing Arguments");
//
//        mCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
//
//        WSHandle.Leagues.getLeagueDivisions(mCompetition.getDivision_id(), new VolleyHelper.IRequestListener<List<NameIdPair>, String>() {
//            @Override
//            public void onFailureResponse(String response) {
//                Toast.makeText(getActivity(), "Some Error Occurred, Please try again", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onSuccessResponse(List<NameIdPair> response) {
//                mRecyclerView.setAdapter(new RecyclerAdapter<NameIdPair, DivisionViewHolder>(response) {
//
//                    @Override
//                    public DivisionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_selection_list_item, parent, false);
//                        return new DivisionViewHolder(view);
//                    }
//
//                    @Override
//                    public void onBindViewHolder(DivisionViewHolder holder, int position) {
//                        holder.bindData(getItem(position));
//                    }
//
//                });
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                Toast.makeText(getActivity(), "Network Error Occurred, Please try again", Toast.LENGTH_LONG).show();
//            }
//        });
//
//
//    }
//
//    class DivisionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        @BindView(android.R.id.text1)
//        TextView text1;
//
//        NameIdPair mDivision;
//
//        public DivisionViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//
//            itemView.setOnClickListener(this);
//        }
//
//        public void bindData(NameIdPair item) {
//            mDivision = item;
//
//            text1.setText(mDivision.Name);
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (mCompetition.getDivision_id() == mDivision.Id) {
//                FragLeagueDetails.sFragLeagueDetails.setCurrentTab(0);
//                //Toast.makeText(getActivity(), "You belong to this division", Toast.LENGTH_LONG).show();
//            } else {
//                startActivity(SingleFragmentActivity.getIntent(getActivity(), FragOtherDivisionDetails.class,
//                        FragOtherDivisionDetails.buildArguments(mDivision, mCompetition)));
//            }
//        }
//    }
//
//}