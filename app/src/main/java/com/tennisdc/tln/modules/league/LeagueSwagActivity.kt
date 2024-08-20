package com.tennisdc.tln.modules.league

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.tennisdc.tln.R
import com.tennisdc.tln.SingleFragmentActivity
import com.tennisdc.tln.common.DialogsUtil
import com.tennisdc.tln.common.Prefs
import com.tennisdc.tln.interfaces.OnDialogButtonClickListener
import com.tennisdc.tln.interfaces.OnDialogButtonNuteralClickListener
import com.tennisdc.tln.model.LeagueSwagItems
import com.tennisdc.tln.model.PreOrderResponse
import com.tennisdc.tln.model.Program
import com.tennisdc.tln.model.Swag_items
import com.tennisdc.tln.modules.buy.FragCheckout
import com.tennisdc.tln.network.VolleyHelper
import com.tennisdc.tln.network.WSHandle
import com.tennisdc.tln.ui.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_league_swag.*
import kotlinx.android.synthetic.main.activity_league_swag.btnCheckout
import kotlinx.android.synthetic.main.activity_league_swag.txtFinalCost
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.view_league_swag.view.*

class LeagueSwagActivity : AppCompatActivity() {

    private var mAdapter: RecyclerView.Adapter<LeagueSwagViewHolder>? = null
    var itemsList = ArrayList<LeagueSwagItems.Response.Data.SwagItems?>()

    var mDialogUtils = DialogsUtil()
    var mLeagueDataItem: LeagueSwagItems.Response.Data? = null
    private var mRecyclerItemSelectionHelper: RecyclerAdapter.RecyclerSelectionHelper<Swag_items>? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_league_swag)
        title = "Swag"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mTxtTitleToolbar.text = getString(R.string.league_swag)
        mImgBack.setOnClickListener {
            finish()
        }
        recycler_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        setAdapter()
        recycler_view.adapter = mAdapter
        getSwagItems()
        btnCheckout.setOnClickListener{
            var mFlagProceed = false
            val selectedItems: MutableList<LeagueSwagItems.Response.Data.SwagItems> = java.util.ArrayList()
            val selectedPositionsItems = mRecyclerItemSelectionHelper!!.selectedPositions
            if (selectedPositionsItems.size() > 0) {
                mFlagProceed = true
                try {
                    for (i in 0 until selectedPositionsItems.size()) {
                        selectedItems.add(itemsList[selectedPositionsItems.keyAt(i)]!!)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            if (mFlagProceed) startActivity(

                SingleFragmentActivity.getIntent(
                    this,
                    FragCheckout::class.java,
                    FragCheckout.buildArgsForLeagueSwagItems(selectedItems)
                )
            )

        }
    }

    /*
    * To get List of Swag Items
    * */
    private fun getSwagItems() {
        progress_bar.visibility = View.VISIBLE
        WSHandle.Leagues.getSwagItems(
            Prefs.AppData(this).oAuthToken,
            object : VolleyHelper.IRequestListener<LeagueSwagItems, String> {
                override fun onFailureResponse(response: String?) {
                    progress_bar.visibility = View.GONE
                }

                override fun onSuccessResponse(leagueSwagItems: LeagueSwagItems?) {
                    progress_bar.visibility = View.GONE
                    try {
                        if (leagueSwagItems?.response?.data != null) {
                            text_title.text = leagueSwagItems.response.data.page_heading
                            text_launched.text = leagueSwagItems.response.data.page_title
                            text_launched.visibility = View.VISIBLE
                            mLeagueDataItem = leagueSwagItems.response.data
                            itemsList.addAll(leagueSwagItems.response.data.swag_items)
                            mAdapter?.notifyDataSetChanged()
                            viewSummary.visibility  =View.VISIBLE
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError?) {
                    progress_bar.visibility = View.GONE
                }

            })
    }

    // this event will enable the back
    // function to the button on press
    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setAdapter() {
        mRecyclerItemSelectionHelper = RecyclerAdapter.RecyclerSelectionHelper(recycler_view)
        mRecyclerItemSelectionHelper!!.setSelectionMode(RecyclerAdapter.RecyclerSelectionHelper.SELECTION_MODE_MULTI)
        mAdapter = object : RecyclerView.Adapter<LeagueSwagViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): LeagueSwagViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_league_swag, parent, false)
                return LeagueSwagViewHolder(view)
            }

            override fun onBindViewHolder(holder: LeagueSwagViewHolder, position: Int) {
                val swagItem = itemsList[position]
                if (swagItem != null) {
                    holder.apply {
                        Glide.with(baseContext).load(swagItem.avatar)
                            .placeholder(android.R.drawable.ic_menu_gallery).into(itemImage)
                        itemPrice.text = "$" + swagItem.cost + " per " + swagItem.name + ","
                        shippedPrice.text = "+$" + swagItem.shipping_cost + " shipping cost"
                        itemSize.text = swagItem.size.default

                        itemSize.setOnClickListener {
                            val mSizePopup = PopupMenu(this@LeagueSwagActivity, holder.itemSize)
                            for (i in swagItem.size.options.indices) {
                                mSizePopup.menu.add(swagItem.size.options.get(i))
                            }

                            mSizePopup.setOnMenuItemClickListener { item ->
                                itemSize.text = item.title
                                true
                            }
                            mSizePopup.show()
                        }

                        itemDetails.setOnClickListener {
                            mDialogUtils.openAlertDialogWithCustomTitle(this@LeagueSwagActivity,
                                "Item Details",
                                swagItem.description,
                                getString(R.string.ok),
                                "",
                                "",
                                object : OnDialogButtonNuteralClickListener {
                                    override fun onPositiveButtonClicked() {}
                                    override fun onNegativeButtonClicked() {}
                                    override fun onNuteralButtonClicked() {}
                                })
                        }
                        if (mLeagueDataItem != null && mLeagueDataItem!!.show_pre_order_button) {
                            buttonPreorder.visibility = View.VISIBLE
                            textPleaseConfirm.visibility = View.VISIBLE
                            textStreetAddress.visibility = View.VISIBLE
                            textStreetAddress.setText(mLeagueDataItem!!.shipping_address1)
                            textCityState.visibility = View.VISIBLE
                            textCityState.setText(mLeagueDataItem!!.shipping_address2)
                        } else {
                            buttonPreorder.visibility = View.GONE
                            textStreetAddress.visibility = View.GONE
                            textPleaseConfirm.visibility = View.GONE
                            textCityState.visibility = View.GONE
                        }

                        buttonPreorder.setOnClickListener {
                            if (textStreetAddress.text.toString()
                                    .isNotEmpty() && textCityState.text.toString().isNotEmpty()
                            ) {
                                DialogsUtil().openAlertDialog(
                                    this@LeagueSwagActivity,
                                    "Is this address completely correct? \n" + textStreetAddress.text.toString() + "\n" + textCityState.text.toString(),
                                    "Yes",
                                    "No",
                                    object : OnDialogButtonClickListener {
                                        override fun onPositiveButtonClicked() {
                                            callPreorderApi(
                                                swagItem.name,
                                                swagItem.id.toString(),
                                                itemSize.text.toString(),
                                                swagItem.cost,
                                                textStreetAddress.text.toString(),
                                                textCityState.text.toString()
                                            )
                                        }

                                        override fun onNegativeButtonClicked() {}
                                    })

                            } else {
                                Toast.makeText(
                                    this@LeagueSwagActivity,
                                    "Please fill the complete address!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }

                        chkBxSwagItem.setOnCheckedChangeListener { _, isChecked ->
                            mRecyclerItemSelectionHelper!!.setItemChecked(
                                layoutPosition,
                                isChecked,
                                false
                            )
                            updateFinalCost(true)
                        }

                        buttonOrder.setOnClickListener {
                            startActivity(SingleFragmentActivity.SwagItemsActivity.getIntent(this@LeagueSwagActivity))
                        }
                    }
                }
            }
            override fun getItemCount(): Int {
                return itemsList.size
            }
        }
    }

    fun updateFinalCost(mFlagSwagItem: Boolean?) {
        var finalCost = 0.0
        //        if (mFlagSwagItem) {
        val selectedPositionsItems = mRecyclerItemSelectionHelper!!.selectedPositions
        if (selectedPositionsItems.size() > 0) {
            btnCheckout.setEnabled(true)
            for (i in 0 until selectedPositionsItems.size()) {
                val swagItems: LeagueSwagItems.Response.Data.SwagItems = itemsList.get(selectedPositionsItems.keyAt(i))!!
                finalCost += java.lang.Double.valueOf(swagItems.cost) + java.lang.Double.valueOf(
                    swagItems.shipping_cost
                )
            }
        }

        if (finalCost == 0.00) {
            btnCheckout.setEnabled(false)
        } else {
            btnCheckout.setEnabled(true)
        }
        txtFinalCost.text = String.format("$%.2f", finalCost)
    }

    private fun callPreorderApi(
        name: String,
        swagItemId: String,
        size: String,
        cost: String,
        shippingAddress1: String,
        shippingAddress2: String
    ) {
        progress_bar.visibility = View.VISIBLE
        WSHandle.Leagues.callPreorderApi(name,
            swagItemId,
            size,
            cost,
            shippingAddress1,
            shippingAddress2,
            object : VolleyHelper.IRequestListener<PreOrderResponse, String> {
                override fun onFailureResponse(response: String?) {
                    progress_bar.visibility = View.GONE
                    Toast.makeText(this@LeagueSwagActivity, ""+response, Toast.LENGTH_SHORT).show()
                }

           override fun onSuccessResponse(response: PreOrderResponse?) {
                    progress_bar.visibility = View.GONE
                    if (response != null) {
                        DialogsUtil().openAlertDialogWithCustomTitle(
                            this@LeagueSwagActivity,
                            response.response.data.title,
                            response.response.message,
                            "",
                            "",
                            "Ok",
                            object : OnDialogButtonNuteralClickListener {
                                override fun onPositiveButtonClicked() {}

                                override fun onNegativeButtonClicked() {}

                                override fun onNuteralButtonClicked() {
                                    itemsList.clear()
                                    mAdapter?.notifyDataSetChanged()
                                    getSwagItems()
                                }

                            })
                    }
                }

                override fun onError(error: VolleyError?) {
                    progress_bar.visibility = View.GONE
                }

            })
    }

    class LeagueSwagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemSize = itemView.mTxtSizeSwagItem
        val itemDetails = itemView.mImgInfoSwagItem
        val itemPrice = itemView.mTxtPriceSwagItem
        val shippedPrice = itemView.mTxtShippingSwagItem
        val itemImage = itemView.mImgSwagItem
        val buttonPreorder = itemView.btn_pre_order
        val buttonOrder = itemView.btn_order_now
        val textPleaseConfirm = itemView.text_please_confirm
        val textStreetAddress = itemView.text_street_address
        val textCityState = itemView.text_city_state
        val chkBxSwagItem = itemView.chkBxSwagItem
    }

}