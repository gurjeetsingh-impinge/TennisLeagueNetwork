package com.tennisdc.tln.modules.league

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.tennisdc.tln.R
import com.tennisdc.tln.common.App
import com.tennisdc.tln.common.Prefs
import com.tennisdc.tln.model.HeadToHeadData
import com.tennisdc.tln.model.Scores
import com.tennisdc.tln.network.VolleyHelper
import com.tennisdc.tln.network.WSHandle
import com.tennisdc.tln.ui.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_head_to_head.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.view_scores_details.*
import kotlinx.android.synthetic.main.view_scores_details.view.*
import kotlin.math.log

class HeadToHeadActivity : AppCompatActivity() {

    private lateinit var adapter: RecyclerAdapter<Scores, HeadToHeadViewHolder>
    var prefs: Prefs.AppData? = null
    var userId: String = ""
    var playerId: String = ""
    private var mProgressDialog: ProgressDialog? = null
    private var scoresDetails: ArrayList<Scores> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_head_to_head)
        prefs = Prefs.AppData(this)
        mImgBack.setOnClickListener {
            finish()
        }
//        scoresDetails.add(Scores("","22/10/17","233"))
        recycler_View.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        mTxtTitleToolbar.text = getString(R.string.head_to_head)
        if (App.isNetworkOnline(this)) {
            mProgressDialog = ProgressDialog.show(this, null, "Please wait...", true, false)
            //new CreateAccountAsyncTask().execute(mFirstName, mLastName, mEmail, mPass, mZip, strMobile, String.valueOf(skillLevel.id), ustaRank.value);
            userId = prefs!!.userID
            if (intent != null && intent.hasExtra("mPlayerId")) {
                playerId = intent.extras!!["mPlayerId"].toString()
            }

            WSHandle.Leagues.getHeadToHeadData(userId, playerId, object :
                VolleyHelper.IRequestListener<HeadToHeadData, String> {
                override fun onFailureResponse(response: String?) {
                    mProgressDialog!!.dismiss()
                    Toast.makeText(
                        this@HeadToHeadActivity,
                        "Error fetching Head to Head data",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSuccessResponse(response: HeadToHeadData?) {
                    mProgressDialog!!.dismiss()
                    Log.d("TAG", "onSuccessResponse: called")
                    if (response != null) {
                        val player = response.player
                        player?.apply {
                            user_name.text = player.player_name
                            if (!league_rating.isNullOrEmpty())
                                user_league_rating.text = league_rating
                            if (!player_wins.isNullOrEmpty())
                                user_head_to_head_wins.text = player_wins.toString()
                            if (!playoff_wins.isNullOrEmpty())
                                user_playoff_head_to_head_wins.text = playoff_wins.toString()
                            if (!games_won.isNullOrEmpty())
                                user_games_won.text = games_won.toString()
                            if (!game_win_percentage.isNullOrEmpty())
                                user_game_win_percentage.text = game_win_percentage.toString()
                            Glide.with(this@HeadToHeadActivity).load(avatar)
                                .placeholder(R.drawable.default_pic)
                                .into(mImgCircleUser)
                        }
                        val opponent = response.opponent
                        opponent?.apply {
                            opponent_name.text = oppnent_name
                            if (!league_rating.isNullOrEmpty())
                                opponent_league_rating.text = league_rating
                            if (!player_wins.isNullOrEmpty())
                                opponent_head_to_head_wins.text = player_wins.toString()
                            if (!playoff_wins.isNullOrEmpty())
                                opponent_playoff_head_to_head_wins.text = playoff_wins.toString()
                            if (!games_won.isNullOrEmpty())
                                opponent_games_won.text = games_won.toString()
                            if (!game_win_percentage.isNullOrEmpty())
                                opponent_game_win_percentage.text = game_win_percentage.toString()
                            if (avatar!!.contains("http://graph.facebook.com/10163156020800355/picture?type=large")) {
                                mImgCircleOpponent.setImageResource(R.drawable.default_pic)
                            } else {
                                Glide.with(this@HeadToHeadActivity).load(avatar)
                                    .placeholder(R.drawable.default_pic)
                                    .into(mImgCircleOpponent)
                            }
                        }


                        if (response.scores.isNotEmpty()) {
//                            scoresDetails.clear()
                            scoresDetails.addAll(response.scores)
                            setAdapter()
                        }

                    }
                }

                override fun onError(error: VolleyError?) {
                    mProgressDialog!!.dismiss()
                    Toast.makeText(
                        this@HeadToHeadActivity,
                        "Network error occurred",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
        }

    }

    fun setAdapter() {
        adapter =
            object : RecyclerAdapter<Scores, HeadToHeadViewHolder>(scoresDetails) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): HeadToHeadViewHolder {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_scores_details, null)
                    return HeadToHeadViewHolder(view)
                }

                override fun onBindViewHolder(holder: HeadToHeadViewHolder, position: Int) {
                    val score = scoresDetails[position]
                    holder.matchDate.text = score.match_date
                    holder.result.text = score.result
                    holder.matchScore.text = score.match_score
                    if (type!=null){
                        holder.type.text = score.type
                        holder.type.visibility = View.VISIBLE
                    }else{
                        holder.type.visibility = View.GONE
                    }
                }

            }

        recycler_View.adapter = adapter
    }

    class HeadToHeadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val matchDate: TextView = itemView.match_date
        val result: TextView = itemView.result
        val matchScore: TextView = itemView.match_score
        val type: TextView = itemView.type
    }
}