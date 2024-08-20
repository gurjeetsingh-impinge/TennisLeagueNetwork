package com.tennisdc.tln.modules.utr

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.tennisdc.tln.BaseDialog
import com.tennisdc.tln.R
import com.tennisdc.tln.common.App
import com.tennisdc.tln.common.Prefs
import com.tennisdc.tln.model.UTRConfigurationModel
import com.tennisdc.tln.network.VolleyHelper
import com.tennisdc.tln.network.WSHandle
import kotlinx.android.synthetic.main.activity_utr_configuration_screen.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.net.URL


class UtrConfigurationScreen : AppCompatActivity() {

    lateinit var mUtrConfiguration: UTRConfigurationModel
    var mDomainName  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_utr_configuration_screen)
        App.LogFacebookEvent(this, this.javaClass.name)
        mTxtData1UTR.movementMethod = LinkMovementMethod.getInstance()
        var mTempDomain = Prefs.AppData(this).domainName
        if(!mTempDomain.contains("http")){
            mTempDomain = "https://"+mTempDomain
        }
        val url = URL(mTempDomain)
        mDomainName = url.getHost()
        mDomainName = mDomainName.replace("www.", "").replace(".com", "")
        mTxtFirstUTR.text = mDomainName +" "+ getString(R.string.utr_first_line)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mTxtData2UTR.text = Html.fromHtml(getString(R.string.just_answer_these_questions_to_set_up_utr_1) +
                    " " + mDomainName + getString(R.string.just_answer_these_questions_to_set_up_utr_2))
            mTxtData1UTR.text = Html.fromHtml(getString(R.string.utr_can_now_be_seen_on_the_tennis_channel))
        } else {
            mTxtData2UTR.text = Html.fromHtml(getString(R.string.just_answer_these_questions_to_set_up_utr_1) +
                    " " + mDomainName + getString(R.string.just_answer_these_questions_to_set_up_utr_2), Html.FROM_HTML_MODE_COMPACT)
            mTxtData1UTR.text = Html.fromHtml(getString(R.string.utr_can_now_be_seen_on_the_tennis_channel), Html.FROM_HTML_MODE_COMPACT)
        }

        mImgBack.setOnClickListener(View.OnClickListener { finish() })
        mTxtTitleToolbar.setAllCaps(true)
        mTxtTitleToolbar.text = getString(R.string.universal_tennis_rating_configuration)
        mTxtTitleToolbar.isSelected = true

        mQuestion1YesUTR.setOnClickListener(View.OnClickListener { updateUTRConfiguration("q1", true) })
        mQuestion1NoUTR.setOnClickListener(View.OnClickListener { updateUTRConfiguration("q1", false) })
        mQuestion2YesUTR.setOnClickListener(View.OnClickListener { updateUTRConfiguration("q2", true) })
        mQuestion2NoUTR.setOnClickListener(View.OnClickListener { updateUTRConfiguration("q2", false) })
        getUTRConfiguration()
    }


    internal fun getUTRConfiguration() {
//        val progressDialog = ProgressDialog.show(this, null, "Please wait...")
        WSHandle.UTR.getUTRConfiguration(object : VolleyHelper.IRequestListener<UTRConfigurationModel, String> {
            override fun onFailureResponse(response: String) {
//                progressDialog.dismiss()
                val dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response)
                dialog.show(supportFragmentManager, "dlg-frag")
            }

            override fun onSuccessResponse(mUtrConfigurationTemp: UTRConfigurationModel) {
//                progressDialog.dismiss()
                mUtrConfiguration = mUtrConfigurationTemp
                updateUI()
            }

            override fun onError(error: VolleyError) {
//                progressDialog.dismiss()
                Toast.makeText(this@UtrConfigurationScreen, "Network Error : " + error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    internal fun updateUTRConfiguration(question: String, answer: Boolean) {
        val progressDialog = ProgressDialog.show(this, null, "Please wait...")
        WSHandle.UTR.updateUTRConfiguration(question, answer, object : VolleyHelper.IRequestListener<String, String> {
            override fun onFailureResponse(response: String) {
                progressDialog.dismiss()
                val dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response)
                dialog.show(supportFragmentManager, "dlg-frag")
            }

            override fun onSuccessResponse(mUtrConfigurationTemp: String) {
                progressDialog.dismiss()
                getUTRConfiguration()

            }

            override fun onError(error: VolleyError) {
                progressDialog.dismiss()
                Toast.makeText(this@UtrConfigurationScreen, "Network Error : " + error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun updateUI() {
        mTxtQuestion1UTR.text = mUtrConfiguration.question1_title

        if (mUtrConfiguration.isDisplay_question2_no) {
            mQuestion2NoUTR.visibility = View.VISIBLE
        } else {
            mQuestion2NoUTR.visibility = View.GONE
        }
        if (mUtrConfiguration.isDisplay_question2) {
            mTxtQuestion2UTR.visibility = View.VISIBLE
            mSwitchQuestion2UTR.visibility = View.VISIBLE
            mTxtQuestion2UTR.text = mUtrConfiguration.question2_title
        } else {
            mTxtQuestion2UTR.visibility = View.GONE
            mSwitchQuestion2UTR.visibility = View.GONE
        }
        if (mUtrConfiguration.question1 == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mQuestion1YesUTR.setBackgroundColor(resources.getColor(R.color.link_normal, theme.resources.newTheme()))
                mQuestion1NoUTR.setBackgroundColor(resources.getColor(R.color.white, theme.resources.newTheme()))
                mQuestion1YesUTR.setTextColor(resources.getColor(R.color.white, theme.resources.newTheme()))
                mQuestion1NoUTR.setTextColor(resources.getColor(R.color.text_color_grey, theme.resources.newTheme()))
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mQuestion1YesUTR.setBackgroundColor(resources.getColor(R.color.white, theme.resources.newTheme()))
                mQuestion1NoUTR.setBackgroundColor(resources.getColor(R.color.link_normal, theme.resources.newTheme()))
                mQuestion1YesUTR.setTextColor(resources.getColor(R.color.text_color_grey, theme.resources.newTheme()))
                mQuestion1NoUTR.setTextColor(resources.getColor(R.color.white, theme.resources.newTheme()))
            }
        }
        if (mUtrConfiguration.question2 == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mQuestion2YesUTR.setBackgroundColor(resources.getColor(R.color.link_normal, theme.resources.newTheme()))
                mQuestion2NoUTR.setBackgroundColor(resources.getColor(R.color.white, theme.resources.newTheme()))
                mQuestion2YesUTR.setTextColor(resources.getColor(R.color.white, theme.resources.newTheme()))
                mQuestion2NoUTR.setTextColor(resources.getColor(R.color.text_color_grey, theme.resources.newTheme()))
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mQuestion2YesUTR.setBackgroundColor(resources.getColor(R.color.white, theme.resources.newTheme()))
                mQuestion2NoUTR.setBackgroundColor(resources.getColor(R.color.link_normal, theme.resources.newTheme()))
                mQuestion2YesUTR.setTextColor(resources.getColor(R.color.text_color_grey, theme.resources.newTheme()))
                mQuestion2NoUTR.setTextColor(resources.getColor(R.color.white, theme.resources.newTheme()))
            }
        }

    }
}
