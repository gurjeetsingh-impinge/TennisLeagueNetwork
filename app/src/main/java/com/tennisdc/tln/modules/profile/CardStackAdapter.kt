package com.tennisdc.tln.modules.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tennisdc.tln.R
import com.tennisdc.tln.common.Prefs
import com.tennisdc.tln.model.OurPlayerDetailMode

class CardStackAdapter(var mContext : Context,
        private var mItemList: ArrayList<OurPlayerDetailMode>
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_profile_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mItemData = mItemList[position]
        holder.mTxtPlayerTitleCard.text = Prefs.AppData(mContext).domainName.replace("www.","").replace(".com","") + " " + mContext.getString(R.string.comminity_members)
        holder.mTxtPlayerNameCard.text = mItemData.name
        holder.mTxtPlayerRecordCard.text = mContext.getString(R.string.players_record) + " : " + mItemData.season_record
        holder.mTxtPlayerHomeCourtCard.text = mItemData.home_court
        holder.mTxtPlayerRegionCard.text = mItemData.player_region
        holder.mTxtPlayerSinceCard.text = mContext.getString(R.string.member_since) + " : " + mItemData.member_since
        Glide.with(mContext)
                .load(mItemData.avatar)
            .placeholder(R.drawable.ic_dummy_user)
                .into(holder.mImgProfileImageCard)
//        holder.itemView.setOnClickListener { v ->
//            Toast.makeText(v.context, mItemData.name, Toast.LENGTH_SHORT).show()
//        }
        if(mItemData.gender.equals("m",true)){
          holder.mLLProfileCard.setBackgroundResource(R.color.green)
        }else{
            holder.mLLProfileCard.setBackgroundResource(R.color.purple_bright)
        }
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

//    fun setSpots(spots: ArrayList<OurPlayerDetailMode>) {
//        this.mItemList = spots
//    }
//
//    fun getSpots(): ArrayList<OurPlayerDetailMode> {
//        return mItemList
//    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mLLProfileCard: LinearLayout = view.findViewById(R.id.mLLProfileCard)
        val mImgProfileImageCard: AppCompatImageView = view.findViewById(R.id.mImgProfileImageCard)
        var mTxtPlayerTitleCard: AppCompatTextView = view.findViewById(R.id.mTxtPlayerTitleCard)
        var mTxtPlayerNameCard: AppCompatTextView = view.findViewById(R.id.mTxtPlayerNameCard)
        var mTxtPlayerRecordCard: AppCompatTextView = view.findViewById(R.id.mTxtPlayerRecordCard)
        var mTxtPlayerHomeCourtCard: AppCompatTextView = view.findViewById(R.id.mTxtPlayerHomeCourtCard)
        var mTxtPlayerRegionCard: AppCompatTextView = view.findViewById(R.id.mTxtPlayerRegionCard)
        var mTxtPlayerSinceCard: AppCompatTextView = view.findViewById(R.id.mTxtPlayerSinceCard)
    }

}