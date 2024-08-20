package com.tennisdc.tln.modules.latest_score;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tennisdc.tln.R;
import com.tennisdc.tln.model.LatestScoreModel;

import java.util.List;

public class RecentScoreAAdapter extends RecyclerView.Adapter<RecentScoreAAdapter.HeaderHolder> {
    Context context;
    private List<LatestScoreModel> mScoreList;


    public RecentScoreAAdapter(Context context, List<LatestScoreModel> mScoreList) {
        this.context = context;
        this.mScoreList = mScoreList;
    }

    @Override
    public HeaderHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.header_layout, parent, false);
        return new HeaderHolder(view);

    }

    @Override
    public void onBindViewHolder(HeaderHolder holder, int position) {

        holder.txtHeader.setText(mScoreList.get(position).getTitle());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.mInnerRv.setLayoutManager(layoutManager);
        InnerScoresAdapter innerScoresAdapter=new InnerScoresAdapter(context,mScoreList.get(position).getScores());
        holder.mInnerRv.setAdapter(innerScoresAdapter);
    }

    @Override
    public int getItemCount() {
        return mScoreList.size();
    }


    public class HeaderHolder extends RecyclerView.ViewHolder {
        TextView txtHeader;
        RecyclerView mInnerRv;

        public HeaderHolder(View itemView) {
            super(itemView);
            txtHeader = (TextView) itemView.findViewById(R.id.txtheaderLayout);
            mInnerRv = (RecyclerView) itemView.findViewById(R.id.mInnerRv);

        }
    }


}
