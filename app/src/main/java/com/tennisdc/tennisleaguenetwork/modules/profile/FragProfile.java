package com.tennisdc.tennisleaguenetwork.modules.profile;

import android.app.ProgressDialog;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tennisleaguenetwork.model.UserDetails;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 03-Jul-17.
 */

public class FragProfile extends Fragment implements View.OnClickListener{

    @BindView(R.id.txtView_full_name)
    TextView mNameTextView;

    @BindView(R.id.txtView_Email)
    TextView mEmailTextView;

    @BindView(R.id.txtView_Mobile)
    TextView mMobileTextView;

    @BindView(R.id.txtView_Address)
    TextView mAddressTextView;

    @BindView(R.id.txtView_preferred_home_court)
    TextView mPreferredHomeCourtTextView;

    @BindView(R.id.txtView_age_range)
    TextView mAgeRangeTextView;

    @BindView(R.id.switchBtn_playWorkDay)
    SwitchCompat mPlayWorkDayCheckbox;

    @BindView(R.id.switchBtn_playOff_notifier)
    SwitchCompat mPlayoffNotifierSwitchBtn;

    @BindView(R.id.switchBtn_ladder_notifier)
    SwitchCompat mLadderNotifierSwitchBtn;

    @BindView(R.id.switchBtn_division_score_notifier)
    SwitchCompat mDivisionNotifierSwitchBtn;

    @BindView(R.id.txtView_favPlayer)
    TextView mFavPlayerTextView;

    @BindView(R.id.img_name_edit)
    ImageView mImgNameUpdate;

    @BindView(R.id.img_email_edit)
    ImageView mImgEmailUpdate;

    @BindView(R.id.img_password_edit)
    ImageView mImgPasswordUpdate;

    @BindView(R.id.img_details_edit)
    ImageView mImgDetailsUpdate;

    private UserDetails mUser;
    public static String USER_DETAILS = "User Details";
    public static int UPDATE_USER_DETAILS = 111;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile, container, false);
        ButterKnife.bind(this, view);
       // setHasOptionsMenu(true);

        mImgNameUpdate.setOnClickListener(this);
        mImgPasswordUpdate.setOnClickListener(this);
        mImgDetailsUpdate.setOnClickListener(this);
        mImgEmailUpdate.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("My Profile");
        fetchData();
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.opt_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           *//* case R.id.action_edit_profile:
                 startActivity(SingleFragmentActivity.getIntent(getActivity(), FragUpdateProfileDetails.class , null));
//                startActivityForResult(FragUpdateProfileDetails.getInstance(getActivity(), Parcels.wrap(mUser)), UPDATE_USER_DETAILS);
                return true;*//*
            case R.id.action_update_name:
                startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfileName.class, FragUpdateProfileName.getFragArgsBundle(mUser.getFullName())));
                return true;
            case R.id.action_update_email:
                startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfileEmail.class, null));
                return true;
            case R.id.action_update_password:
                startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfilePassword.class, null));
                return true;
            case R.id.action_update_details:
                startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfileDetails.class, FragUpdateProfileDetails.getFragArgsBundle(mUser.getMobile(),
                                    mUser.isPlayoffNotifier(),mUser.isLadderScoreNotifier(),mUser.isDivisionScoreNotifier(),mUser.isDayTimePlay(),
                                    mUser.getStreet(),mUser.getCity(),mUser.getState(),mUser.getCourtName(),mUser.getAgeRange(),mUser.getFavoritePlayer())));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
*/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchData();
    }

    private void fetchData(){
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
        WSHandle.Profile.getUserDetails(new VolleyHelper.IRequestListener<UserDetails, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Server Error:" + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(UserDetails response) {
                /*appData.setPlayer(response);
                appData.setUpdatePlayer(false);*/
                mUser = response;
                displayData();
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayData() {
        mNameTextView.setText(mUser.getFullName());
        mEmailTextView.setText(mUser.getEmail());
        mMobileTextView.setText(mUser.getMobile());
        mPlayoffNotifierSwitchBtn.setChecked(mUser.isPlayoffNotifier());
        mLadderNotifierSwitchBtn.setChecked(mUser.isLadderScoreNotifier());
        mDivisionNotifierSwitchBtn.setChecked(mUser.isDivisionScoreNotifier());
        mPlayWorkDayCheckbox.setChecked(mUser.isDayTimePlay());
        mPreferredHomeCourtTextView.setText(mUser.getCourtName());
        mAgeRangeTextView.setText(mUser.getAgeRange());
        String addr = mUser.getStreet() + " " + mUser.getCity() + " " + mUser.getState();
        mAddressTextView.setText(addr);
        mFavPlayerTextView.setText(mUser.getFavoritePlayer());
    }

    @Override
    public void onClick(View v) {
        if(v.equals(mImgNameUpdate)) {
           // startActivity(SingleFragmentActivity.MyProfileActivity.getIntent(getContext()),FragUpdateProfileName.getFragArgsBundle(mUser.getFullName()));
            startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfileName.class, FragUpdateProfileName.getFragArgsBundle(mUser.getFullName())));
        } else if(v.equals(mImgEmailUpdate)){
            startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfileEmail.class, null));
        } else if(v.equals(mImgPasswordUpdate)){
            startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfilePassword.class, null));
        } else if(v.equals(mImgDetailsUpdate)) {
            startActivity(SingleFragmentActivity.getIntent(getContext(), FragUpdateProfileDetails.class, FragUpdateProfileDetails.getFragArgsBundle(mUser.getMobile(),
                    mUser.isPlayoffNotifier(),mUser.isLadderScoreNotifier(),mUser.isDivisionScoreNotifier(),mUser.isDayTimePlay(),
                    mUser.getStreet(),mUser.getCity(),mUser.getState(),mUser.getCourtName(),mUser.getAgeRange(),mUser.getFavoritePlayer())));
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_USER_DETAILS) {
            if (resultCode == Activity.RESULT_OK) {
                fetchData();
            } else {
                getActivity().onBackPressed();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }*/
}
