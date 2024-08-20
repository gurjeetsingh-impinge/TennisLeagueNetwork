package com.tennisdc.tennisleaguenetwork.modules.referral;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.modules.poty.FragPlayersOfTheYearHome;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 27-Mar-2018.
 */

public class FragReferralRewards extends Fragment {

    @BindView(R.id.txt_share_link_clickhere)
    TextView mShareLink_ClickHere_txt;

    @BindView(R.id.txt_refer_friend_clickhere)
    TextView mReferFriend_ClickHere_txt;

    @BindView(R.id.txt_refer_code_clickhere)
    TextView mReferCode_ClickHere_txt;

    @BindView(R.id.txt_PlayerOfTheYearPoints)
    TextView mPlayerOfTheYear_txt;

    @BindView(R.id.txt_share_referral_link)
    TextView mShare_Referral_Link;

    @BindView(R.id.txt_share_referral_code)
    TextView mShare_Referral_Code;

    /* data */
    private PlayerDetail mPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_referral_rewards, container, false);
        ButterKnife.bind(this, view);

        Prefs.AppData appData = new Prefs.AppData(getActivity());
        mPlayer = appData.getPlayer();

        mShare_Referral_Link.setText(appData.getReferralUrl());
        mShare_Referral_Code.setText(String.valueOf(mPlayer.id));

        mShareLink_ClickHere_txt.setPaintFlags(mShareLink_ClickHere_txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mShareLink_ClickHere_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String urlPolicy = "https://www.tennisdc.com/api/privacy_policy";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlPolicy));
                startActivity(intent);*/
            }
        });

        mReferFriend_ClickHere_txt.setPaintFlags(mReferFriend_ClickHere_txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mReferFriend_ClickHere_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(SingleFragmentActivity.getIntent(getContext(), FragReferFriend.class, null));
            }
        });

        mReferCode_ClickHere_txt.setPaintFlags(mReferCode_ClickHere_txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mReferCode_ClickHere_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPlayerOfTheYear_txt.setText(Html.fromHtml("Players also get <b><u><font color='#50307d'>Players of the Year points</font></u></b> for referrals"));
        mPlayerOfTheYear_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SingleFragmentActivity.getIntent(getContext(), FragPlayersOfTheYearHome.class, null));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Referral Reward");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_FIRST_USER) {
            startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
