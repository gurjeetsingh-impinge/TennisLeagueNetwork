package com.tennisdc.tln.modules.league

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tennisdc.tln.BaseDialog
import com.tennisdc.tln.R
import com.tennisdc.tln.common.App
import com.tennisdc.tln.common.GsonRealmExclusionStrategy
import com.tennisdc.tln.common.Prefs
import com.tennisdc.tln.model.MatchesPlayedModel
import com.tennisdc.tln.model.Player
import com.tennisdc.tln.modules.league.MonthYearPickerDialog.Companion.newInstance
import com.tennisdc.tln.network.VolleyHelper
import com.tennisdc.tln.network.WSHandle
import com.tennisdc.tln.ui.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_matches_played.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.json.JSONObject
import java.util.*
import android.widget.Toast

var prefs: Prefs.AppData? = null
var mMatchesPlayed: MatchesPlayedModel? = null
var mSelectMonth = 0
var mSelectYear = 0

class MatchesPlayedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches_played)
        App.LogFacebookEvent(this, this.javaClass.name)
        prefs = Prefs.AppData(this)

        /* fetch current month and year */
        val calendar = Calendar.getInstance()
        mSelectMonth = calendar.get(Calendar.MONTH) + 1
        mSelectYear = calendar.get(Calendar.YEAR)
        getMatchesPlayed()
        calendar.add(Calendar.YEAR, 9)
        val pd = newInstance(mSelectMonth,
                calendar[Calendar.DAY_OF_MONTH], mSelectYear, Calendar.getInstance().get(Calendar.YEAR), -1)
        mTxtDateMatchesPlayed.text = "${pd.getMonthNameByNumber(mSelectMonth)} / $mSelectYear"
        /* click for date picker */
        mRLDate.setOnClickListener {
            val pd = newInstance(mSelectMonth,
                    calendar[Calendar.DAY_OF_MONTH], mSelectYear, calendar.get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR))
            pd.setListener(object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int) {
                    val formatedDate = ""
                    var currentDateFormat = ""
                    var mValidDateFlag = true
                    if (selectedYear < Calendar.getInstance().get(Calendar.YEAR)) {
                            mValidDateFlag = false
                    }

                    if (selectedYear == Calendar.getInstance().get(Calendar.YEAR)) {
                        if (selectedMonth < (Calendar.getInstance().get(Calendar.MONTH) + 1)) {
                            mValidDateFlag = false
                        }
                    }
                    if (mValidDateFlag) {
                        mSelectMonth = selectedMonth
                        mSelectYear = selectedYear
                        //  "$selectedMonth/$selectedDay/$selectedYear" //"MM/dd/yyyy"
                        currentDateFormat = "${pd.getMonthNameByNumber(mSelectMonth)} / $mSelectYear" //"MM/dd/yyyy"
                        mTxtDateMatchesPlayed.text = "$currentDateFormat"
                        getMatchesPlayed()
                    } else {
                        Toast.makeText(this@MatchesPlayedActivity, getString(R.string.please_select_future_date), Toast.LENGTH_LONG).show()
                    }
                }
            })
            pd.show(fragmentManager, "MonthYearPickerDialog")
        }

        /* back icon click */
        mImgBack.setOnClickListener { finish() }

    }

    /* API call for get matches played */
    fun getMatchesPlayed() {
        mNSVMatchesPlayed.visibility = View.GONE
        val progressDialog = ProgressDialog.show(this, null, "Please wait...")
        var mMatchesPlayedRequest = WSHandle.Leagues.getMatchesPlayedRequest(mSelectMonth, mSelectYear, object : VolleyHelper.IRequestListener<JSONObject, String?> {
            override fun onFailureResponse(response: String?) {
                progressDialog.dismiss()
                val dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response)
                dialog.show(supportFragmentManager, "dlg-frag")
            }

            override fun onSuccessResponse(response: JSONObject?) {
                progressDialog.dismiss()
                val listType = object : TypeToken<MatchesPlayedModel?>() {}.type
                mMatchesPlayed = GsonBuilder().setExclusionStrategies(GsonRealmExclusionStrategy()).create().fromJson(response.toString(), listType);
                setUI()
            }

            override fun onError(error: VolleyError) {
                progressDialog.dismiss()
                Toast.makeText(this@MatchesPlayedActivity, "Network Error : " + error.message, Toast.LENGTH_LONG).show()
            }
        })

        VolleyHelper.getInstance(this).addToRequestQueue<Any>(mMatchesPlayedRequest)
    }

    /* populate data after receiving from database */
    fun setUI() {
        mNSVMatchesPlayed.visibility = View.VISIBLE
        mImgRightCorner.visibility = View.GONE
        mTxtTitleToolbar.text = mMatchesPlayed!!.main_heading
        mDescriptionMatchesPlayed.text = Html.fromHtml(mMatchesPlayed!!.description, Html.FROM_HTML_MODE_COMPACT)

        /* local matches UI population*/
        mLocalMatchesPlayed.text = mMatchesPlayed!!.matches_played
        mWinnerLocalMatchesPlayed.text = mMatchesPlayed!!.winner
        mHeading1MatchesPlayed.text = mMatchesPlayed!!.heading1
        var mMatchedPlayedAdapters: RecyclerAdapter<*, *>? = null

        mMatchedPlayedAdapters = object : RecyclerAdapter<Player, MactchesPlayedItemViewHolder>(mMatchesPlayed!!.players) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MactchesPlayedItemViewHolder {
                val view = LayoutInflater.from(this@MatchesPlayedActivity).inflate(R.layout.item_row_matches_played, null)
                return MactchesPlayedItemViewHolder(view)
            }

            override fun onBindViewHolder(holder: MactchesPlayedItemViewHolder, position: Int) {
                holder.bindItem(this@MatchesPlayedActivity, mMatchesPlayed!!.players.get(position), position)
            }
        }
//        mRVMatchesPlayed.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_big))

        mRVMatchesPlayed.setAdapter(mMatchedPlayedAdapters)

        /* Global matches UI population*/
        mGlobalMatchesPlayed.text = mMatchesPlayed!!.heading2
        mWinnerGlobalMatchesPlayed.text = mMatchesPlayed!!.matches_played_globally
        mHeading2MatchesPlayed.text = mMatchesPlayed!!.heading2
        var mGlobalMatchedPlayedAdapters: RecyclerAdapter<*, *>? = null

        mGlobalMatchedPlayedAdapters = object : RecyclerAdapter<Player, MactchesPlayedItemViewHolder>(mMatchesPlayed!!.nationwide_players) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MactchesPlayedItemViewHolder {
                val view = LayoutInflater.from(this@MatchesPlayedActivity).inflate(R.layout.item_row_matches_played, null)
                return MactchesPlayedItemViewHolder(view)
            }

            override fun onBindViewHolder(holder: MactchesPlayedItemViewHolder, position: Int) {
                holder.bindItem(this@MatchesPlayedActivity, mMatchesPlayed!!.players.get(position), position)
            }
        }
//        mRVMatchesPlayed.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_big))

        mRVGlobalMatchesPlayed.setAdapter(mGlobalMatchedPlayedAdapters)
    }

    class MactchesPlayedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTxtPositionMatchesPlayed: TextView
        var mTxtPlayNameMatchesPlayed: TextView
        var mTxtValueMatchesPlayed: TextView
        var mLayoutMainMatchesPlayed: LinearLayout

        fun bindItem(mContext: Context, mPlayerDetail: Player, mPosition: Int) {
            mTxtPositionMatchesPlayed.text = "#${mPlayerDetail.index}"
            mTxtPlayNameMatchesPlayed.text = mPlayerDetail.name
            mTxtValueMatchesPlayed.text = "${mPlayerDetail.matches}"
            if (mPosition % 2 == 0)
                mLayoutMainMatchesPlayed.setBackgroundResource(R.color.gray_button_dark)
            else
                mLayoutMainMatchesPlayed.setBackgroundResource(R.color.gray_button_border)
            var mLayoutParameter = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            mLayoutMainMatchesPlayed.layoutParams = mLayoutParameter
        }

        init {
            ButterKnife.bind(this, itemView)
            mLayoutMainMatchesPlayed = itemView.findViewById(R.id.mLayoutMainMatchesPlayed)
            mTxtPositionMatchesPlayed = itemView.findViewById(R.id.mTxtPositionMatchesPlayed)
            mTxtPlayNameMatchesPlayed = itemView.findViewById(R.id.mTxtPlayNameMatchesPlayed)
            mTxtValueMatchesPlayed = itemView.findViewById(R.id.mTxtValueMatchesPlayed)
        }
    }
}