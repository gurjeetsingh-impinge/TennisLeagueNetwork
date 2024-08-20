package com.tennisdc.tln.modules.latest_score;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tennisdc.tln.R;
import com.tennisdc.tln.model.LatestScoreModel;

import java.util.ArrayList;

public class InnerScoresAdapter extends RecyclerView.Adapter<InnerScoresAdapter.InnerHolder> {

    Context context;
    ArrayList<LatestScoreModel.ScoresBean> scoresBeans;

    public InnerScoresAdapter(Context context, ArrayList<LatestScoreModel.ScoresBean> scoresBeans) {
        this.context = context;
        this.scoresBeans = scoresBeans;
    }

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_latest_score_item, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, int position) {
        holder.txtProgramNameLatestScore.setText("Played at "+scoresBeans.get(position).getMatch_location());
//        holder.txtDivisionNameLatestScore.setText(scoresBeans.get(position).getProgram()+" - "+
//                scoresBeans.get(position).getDivision_level());
        if(scoresBeans.get(position).getCompetition_level().trim().isEmpty()){
            holder.txtDivisionNameLatestScore.setVisibility(View.GONE);
        }else {
            holder.txtDivisionNameLatestScore.setVisibility(View.VISIBLE);
            holder.txtDivisionNameLatestScore.setText(scoresBeans.get(position).getCompetition_level());
        }
        holder.txtWinnerNameLatestScore.setText(scoresBeans.get(position).getWinner_name());
        holder.txtOpponentNameLatestScore.setText(scoresBeans.get(position).getOpponent_name());
        holder.txtScoreLatestScore.setText(scoresBeans.get(position).getScore());
        holder.txtDateLatestScore.setText(scoresBeans.get(position).getDate());
        if(scoresBeans.get(position).getNewScore()){
            holder.mTxtNewScore.setVisibility(View.VISIBLE);
        }else{
            holder.mTxtNewScore.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return scoresBeans.size();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        private TextView txtProgramNameLatestScore;
        private TextView txtDivisionNameLatestScore;
        private TextView txtWinnerNameLatestScore;
        private TextView txtOpponentNameLatestScore;
        private TextView txtScoreLatestScore;
        private TextView txtDateLatestScore;
        private TextView mTxtNewScore;
        public InnerHolder(View itemView) {
            super(itemView);
            txtProgramNameLatestScore = (TextView) itemView.findViewById(R.id.txtProgramNameLatestScore);
            txtDivisionNameLatestScore = (TextView) itemView.findViewById(R.id.txtDivisionNameLatestScore);
            txtWinnerNameLatestScore = (TextView) itemView.findViewById(R.id.txtWinnerNameLatestScore);
            txtOpponentNameLatestScore = (TextView) itemView.findViewById(R.id.txtOpponentNameLatestScore);
            txtScoreLatestScore = (TextView) itemView.findViewById(R.id.txtScoreLatestScore);
            txtDateLatestScore = (TextView) itemView.findViewById(R.id.txtDateLatestScore);
            mTxtNewScore = (TextView) itemView.findViewById(R.id.mTxtNewScore);
        }
    }
}
