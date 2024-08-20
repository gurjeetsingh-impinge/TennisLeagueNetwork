package com.tennisdc.tln.modules.swag

import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.tennisdc.tln.R
import com.tennisdc.tln.SingleFragmentActivity
import com.tennisdc.tln.common.DialogsUtil
import com.tennisdc.tln.interfaces.OnDialogButtonNuteralClickListener
import com.tennisdc.tln.model.Program
import com.tennisdc.tln.model.Swag_items
import com.tennisdc.tln.modules.buy.FragCheckout
import com.tennisdc.tln.network.VolleyHelper
import com.tennisdc.tln.network.WSHandle
import com.tennisdc.tln.ui.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_frag_swag.*
import kotlinx.android.synthetic.main.fragment_frag_swag.view.*
import kotlinx.android.synthetic.main.view_swag_item.view.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [FragSwag.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragSwag : Fragment() {
    lateinit var list: List<Swag_items>
    private var mRecyclerItemSelectionHelper: RecyclerAdapter.RecyclerSelectionHelper<Swag_items>? =
        null
    lateinit var mView:View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_frag_swag, container, false)
        mView.recyclerViewSwagItem.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        getSwagItems(mView)
        mView.btnCheckout.setOnClickListener{
            var mFlagProceed = false
            val selectedPrograms: MutableList<Program> = ArrayList()
            val selectedItems: MutableList<Swag_items> = ArrayList()
            val addDesc: String = ""
            val selectedPositionsItems = mRecyclerItemSelectionHelper!!.selectedPositions
            if (selectedPositionsItems.size() > 0) {
                mFlagProceed = true
                for (i in 0 until selectedPositionsItems.size()) selectedItems.add(
                    list.get(
                        selectedPositionsItems.keyAt(i)
                    )
                )
            }
            if (mFlagProceed) startActivity(
                SingleFragmentActivity.getIntent(
                    activity,
                    FragCheckout::class.java,
                    FragCheckout.buildArgs(selectedPrograms, selectedItems,addDesc)
                )
            )

        }
        return mView
    }

    override fun onResume() {
        super.onResume()
        activity?.title = "Swag Program"
    }

    private fun getSwagItems(view: View) {
        view.progress_circular.visibility = View.VISIBLE
        WSHandle.Swag.getSeparateSwagItems(object :
            VolleyHelper.IRequestListener<List<Swag_items>, String> {
            override fun onFailureResponse(response: String?) {
                view.progress_circular.visibility = View.GONE
            }

            override fun onSuccessResponse(response: List<Swag_items>?) {
                view.progress_circular.visibility = View.GONE
                if (response!=null){
                    list = response
                    setAdapter()
                    mView.viewSummary.visibility = View.VISIBLE
                }
            }

            override fun onError(error: VolleyError?) {
                view.progress_circular.visibility = View.GONE
            }

        })
    }

    fun updateFinalCost(mFlagSwagItem: Boolean?) {
        var finalCost = 0.0
        //        if (mFlagSwagItem) {
        val selectedPositionsItems = mRecyclerItemSelectionHelper!!.selectedPositions
        if (selectedPositionsItems.size() > 0) {
            btnCheckout.setEnabled(true)
            for (i in 0 until selectedPositionsItems.size()) {
                val swagItems: Swag_items = list.get(selectedPositionsItems.keyAt(i))
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    fun setAdapter(){
        mRecyclerItemSelectionHelper = RecyclerAdapter.RecyclerSelectionHelper(mView.recyclerViewSwagItem)
        mRecyclerItemSelectionHelper!!.setSelectionMode(RecyclerAdapter.RecyclerSelectionHelper.SELECTION_MODE_MULTI)
        mView.recyclerViewSwagItem.adapter = object :
            RecyclerAdapter<Swag_items?, SwagViewHolder?>(list) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwagViewHolder {
                val view = LayoutInflater.from(activity).inflate(R.layout.view_swag_item, null)
                return SwagViewHolder(view)
            }

            override fun onBindViewHolder(holder: SwagViewHolder, position: Int) {
                val item = list[position]
                holder.apply {
                    Glide.with(activity!!).load(item.image)
                        .placeholder(R.drawable.ic_photos).into(mImgSwagItem)
                    mTxtNameSwagItem.setText(item.title)
                    mTxtPriceSwagItem.text = activity!!.getString(R.string.currency_symbol) + item.cost
                    mTxtShippingSwagItem.text =
                        "+" + activity!!.getString(R.string.currency_symbol) + item.shipping_cost + " " + getString(
                            R.string.shipping_cost
                        )
//			mTxtSizeSwagItem.setText(String.valueOf(item.sizes.size()));
                    //			mTxtSizeSwagItem.setText(String.valueOf(item.sizes.size()));
                    if (item.sizes!=null && item.sizes.isNotEmpty()){
                        mTxtSizeSwagItem.text = item.sizes[0]
                    }else{
                        mTxtSizeSwagItem.text = "Select Size"
                    }
                    mTxtSizeSwagItem.setOnClickListener{
                        val mSizePopup = PopupMenu(activity, mTxtSizeSwagItem)
                        for (i in item.sizes.indices) {
                            mSizePopup.menu.add(item.sizes.get(i))
                        }
                        //registering popup with OnMenuItemClickListener
                        //registering popup with OnMenuItemClickListener
                        mSizePopup.setOnMenuItemClickListener { item ->
                            mTxtSizeSwagItem.text = item.title
                            true
                        }
                        mSizePopup.show()
                    }
                    mImgInfoSwagItem.setOnClickListener{
                        DialogsUtil().openAlertDialogWithCustomTitle(
                            activity,
                            item.title,
                            item.description,
                            activity!!.getString(R.string.done),
                            "",
                            "",
                            object : OnDialogButtonNuteralClickListener {
                                override fun onPositiveButtonClicked() {}
                                override fun onNegativeButtonClicked() {}
                                override fun onNuteralButtonClicked() {}
                            })
                    }
                    if (item.sizes.size <= 0) {
                        mTxtSizeSwagItem.visibility = View.GONE
                    } else {
                        mTxtSizeSwagItem.visibility = View.VISIBLE
                    }
                    chkBxSwagItem.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                            mRecyclerItemSelectionHelper!!.setItemChecked(
                                layoutPosition,
                                isChecked,
                                false
                            )
                            updateFinalCost(true)
                        }
                    })
                }
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }
        }
    }

    class SwagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        RecyclerAdapter.RecyclerSelectionHelper.SelectionHelperCallbacks{
        val chkBxSwagItem = itemView.chkBxSwagItem
        val mImgSwagItem = itemView.mImgSwagItem
        val mTxtNameSwagItem = itemView.mTxtNameSwagItem
        val mTxtPriceSwagItem = itemView.mTxtPriceSwagItem
        val mTxtShippingSwagItem = itemView.mTxtShippingSwagItem
        val mTxtSizeSwagItem = itemView.mTxtSizeSwagItem
        val txtAlreadyEnrolled = itemView.txtAlreadyEnrolled
        val mImgInfoSwagItem = itemView.mImgInfoSwagItem
        val btnCheckout = itemView.btnCheckout

        override fun setActivated(active: Boolean) {
            chkBxSwagItem.isChecked = active
        }
    }
}