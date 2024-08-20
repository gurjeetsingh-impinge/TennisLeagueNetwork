package com.tennisdc.tln.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tennisdc.tln.R;
import com.tennisdc.tln.model.ReferFriendEmailAddress;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 17-Apr-2018.
 */

public class ReferralRecyclerAdapter extends RecyclerView.Adapter<ReferralRecyclerAdapter.RecyclerItemViewHolder> {

    private ArrayList<ReferFriendEmailAddress> myList;
  //  int mLastPosition = 0;

    public ReferralRecyclerAdapter(ArrayList<ReferFriendEmailAddress> myList ) {
        this.myList = myList;
    }

    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_refer_friend_email_item, parent, false);
        RecyclerItemViewHolder holder = new RecyclerItemViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, final int position) {
     //   Log.d("onBindViewHoler ", myList.size() + "");
        holder.mTitleEmail_txt.setText(myList.get(position).title);
        holder.mEmailAddr_edt.setText(myList.get(position).emailAddr);

        holder.mEmailAddr_edt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myList.get(position).emailAddr = s.toString();
            }
        });

       // mLastPosition =position;
    }

    @Override
    public int getItemCount() {
        return(null != myList?myList.size():0);
    }

    public void notifyData(ArrayList<ReferFriendEmailAddress> myList) {
       // Log.d("notifyData ", myList.size() + "");
        this.myList = myList;
        notifyDataSetChanged();
    }

    public ArrayList<ReferFriendEmailAddress> retrieveData()
    {

        return this.myList;
    }

    public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_TitleEmail)
        TextView mTitleEmail_txt;

        @BindView(R.id.edt_FrndsEmailAddr)
        EditText mEmailAddr_edt;

        /* data */
        ReferFriendEmailAddress mEmailAddr;

        public RecyclerItemViewHolder(final View parent) {
            super(parent);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(ReferFriendEmailAddress item) {
            mEmailAddr = item;
            mTitleEmail_txt.setText(item.title);
            mEmailAddr_edt.setText(item.emailAddr);
        }
    }


}


