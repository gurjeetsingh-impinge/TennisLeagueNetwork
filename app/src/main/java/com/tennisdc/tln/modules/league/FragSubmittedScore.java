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
//
//import com.tennisdc.tln.R;
//import com.tennisdc.tln.model.SubmittedScore;
//import com.tennisdc.tln.ui.RecyclerAdapter;
//
//import org.parceler.Parcels;
//
//import java.util.List;
//
//import butterknife.ButterKnife;
//import butterknife.BindView;
//
//public class FragSubmittedScore extends Fragment {
//
//    private static final String EXTRA_LIST = "object_list";
//
//    public static FragSubmittedScore getInstance(List<SubmittedScore> list) {
//        FragSubmittedScore fragSubmittedScore = new FragSubmittedScore();
//
//        Bundle args = new Bundle();
//        args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
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
//    /* Data */
//    private List<SubmittedScore> mSubmittedScores;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.frag_recycler_view, container, false);
//
//        ButterKnife.bind(this, view);
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));
//
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        Bundle args = getArguments();
//        if (args == null) throw new RuntimeException("Missing Arguments");
//
//        mSubmittedScores = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
//        mRecyclerView.setAdapter(new RecyclerAdapter<SubmittedScore, SubmittedScoreViewHolder>(mSubmittedScores) {
//
//            @Override
//            public SubmittedScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_submitted_score_list_item, null);
//                return new SubmittedScoreViewHolder(view);
//            }
//
//            @Override
//            public void onBindViewHolder(SubmittedScoreViewHolder holder, int position) {
//                holder.bindItem(getItem(position));
//            }
//
//        });
//    }
//
//    class SubmittedScoreViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.txtMatchResult)
//        TextView MatchResultTextView;
//
//        @BindView(R.id.txtMatchScore)
//        TextView MatchScoreTextView;
//
//        @BindView(R.id.txtMatchDate)
//        TextView MatchDateTextView;
//
//        public SubmittedScoreViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//
//        public void bindItem(SubmittedScore submittedScore) {
//            MatchResultTextView.setText(submittedScore.MatchResult);
//            MatchScoreTextView.setText(submittedScore.Score);
//            MatchDateTextView.setText(submittedScore.MatchDate);
//        }
//    }
//}