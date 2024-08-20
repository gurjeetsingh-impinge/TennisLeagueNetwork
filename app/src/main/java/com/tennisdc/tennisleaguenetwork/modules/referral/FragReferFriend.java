package com.tennisdc.tennisleaguenetwork.modules.referral;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.Common;
import com.tennisdc.tennisleaguenetwork.model.Location;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.ReferFriendEmailAddress;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.ui.CustomSpinnerWithErrorAdapter;
import com.tennisdc.tennisleaguenetwork.ui.ReferralRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 05-Apr-2018.
 */

public class FragReferFriend  extends Fragment {

    @BindView(R.id.edt_UserEmail)
    EditText mEdt_UserEmail;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.spnrSelectCity)
    Spinner mSpnr_SelectCity;

    @BindView(R.id.btnReferSubmit)
    Button mBtn_Submit;

    @BindView(R.id.txt_moreEmails)
    TextView mTxt_ExtraEmails;

    /* data */
    private PlayerDetail mPlayer;
    private ArrayList<Common> mLocationList = new ArrayList<Common>();
    private CustomSpinnerWithErrorAdapter<Common> mLocationAdapter;
    private ArrayList<ReferFriendEmailAddress> mEmailAddressList;
    private ReferralRecyclerAdapter mReferralRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_refer_a_friend, container, false);
        ButterKnife.bind(this, view);

        setUserInfo();

        createDefaultEmailAddrList();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mReferralRecyclerAdapter = new ReferralRecyclerAdapter(mEmailAddressList);
        mRecyclerView.setAdapter(mReferralRecyclerAdapter);

        mTxt_ExtraEmails.setPaintFlags(mTxt_ExtraEmails.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mTxt_ExtraEmails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    mEmailAddressList = mReferralRecyclerAdapter.retrieveData();
                int original = mEmailAddressList.size();
                int size =original + 1;
                mEmailAddressList.add(new ReferFriendEmailAddress(size, String.valueOf(size)+"th friend's email:", ""));
                int newS =  mEmailAddressList.size();
                mReferralRecyclerAdapter.notifyData(mEmailAddressList);
            }
        });


       /* mTxt_MoreEmails.setPaintFlags(mTxt_MoreEmails.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mTxt_MoreEmails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    mEmailAddressList = mReferralRecyclerAdapter.retrieveData();
                int original = mEmailAddressList.size();
               int size =original + 1;
                mEmailAddressList.add(new ReferFriendEmailAddress(size, String.valueOf(size)+"th friend's email:", "Email"));
              int newS =  mEmailAddressList.size();
                mReferralRecyclerAdapter.notifyData(mEmailAddressList);
            }
        });*/

        mBtn_Submit.setOnClickListener(SubmitClickListener);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private View.OnClickListener SubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isError = false;
            View firstErrorView = null;

            Common city = (Common) mSpnr_SelectCity.getSelectedItem();
            if (city.id == 0) {
                isError = true;
                mLocationAdapter.setError(mSpnr_SelectCity.getSelectedView(), "Please Select City");
                firstErrorView = mSpnr_SelectCity;
            }

            String strEmail = mEdt_UserEmail.getText().toString().trim();
            if (TextUtils.isEmpty(strEmail)) {
                mEdt_UserEmail.setError("Provide Email");
                if (firstErrorView == null) firstErrorView = mEdt_UserEmail;
                isError = true;
            } else if (!App.isValidEmail(strEmail)) {
                mEdt_UserEmail.setError("Invalid Email");
                if (firstErrorView == null) firstErrorView = mEdt_UserEmail;
                isError = true;
            }

            mEmailAddressList = mReferralRecyclerAdapter.retrieveData();
           ArrayList<String> emailList = new ArrayList<String>();
            for(int i=0;i<mEmailAddressList.size();i++) {
                String emailAddr = mEmailAddressList.get(i).emailAddr ;
                if(!TextUtils.isEmpty(emailAddr) && App.isValidEmail(emailAddr))
                    emailList.add(emailAddr);
            }
            if(emailList.size() < 0)
                isError = true;

            /*String firstEmail = mEdt_firstFrndEmail.getText().toString().trim();
            if(TextUtils.isEmpty(firstEmail)) {
                mEdt_firstFrndEmail.setError("Provide Friend's Email");
                if (firstErrorView == null) firstErrorView = mEdt_firstFrndEmail;
                isError = true;
            } else if (!App.isValidEmail(firstEmail)) {
                mEdt_firstFrndEmail.setError("Invalid Email");
                if (firstErrorView == null) firstErrorView = mEdt_firstFrndEmail;
                isError = true;
            }
*/
            if (!isError) {
                createReferrals(emailList);
               // startActivity(SingleFragmentActivity.getIntent(mContext, FragRegisterStep2.class, FragRegisterStep2.getFragArgumentBundle(strFName, strLName, strEmail, strPass, strZip)));
            } else {
                if (firstErrorView != null) {
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    firstErrorView.requestFocus();
                    imm.showSoftInput(firstErrorView, 0);
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Refer A Friend");
    }

    private void setUserInfo(){
        Prefs.AppData appData = new Prefs.AppData(getActivity());
        mPlayer = appData.getPlayer();

        mEdt_UserEmail.setText(mPlayer.Email);
        String city = mPlayer.CityName;

        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
         /* Fetch Locations and regions */
        WSHandle.Courts.getLocationAndRegions(new VolleyHelper.IRequestListener<List<Location>, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(List<Location> response) {
                progressDialog.dismiss();

                Location defaultLoc = new Location();
                defaultLoc.setName("Please select City");
                defaultLoc.setId(0);

                response.add(0, defaultLoc);

                int setSelection = 0;
                for(int i=0;i<response.size();i++){
                    Common obj = new Common(response.get(i).getName(), response.get(i).getId());
                    mLocationList.add(i,obj);

                    if(obj.display_name.equals(mPlayer.CityName))
                        setSelection = i;
                }

                mLocationAdapter = new CustomSpinnerWithErrorAdapter<Common>(getContext(), mLocationList);
                mLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                mSpnr_SelectCity.setAdapter(mLocationAdapter);
                mSpnr_SelectCity.setSelection(setSelection);

                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createDefaultEmailAddrList(){
        mEmailAddressList = new ArrayList<ReferFriendEmailAddress>();
        mEmailAddressList.add(new ReferFriendEmailAddress(1,"1st friend's email:*", ""));
        mEmailAddressList.add(new ReferFriendEmailAddress(2,"2nd friend's email:", ""));
        mEmailAddressList.add(new ReferFriendEmailAddress(3,"3rd friend's email:", ""));
    }

    private void createReferrals(ArrayList<String> referralEmailArr ){

        JSONObject params = new JSONObject();
        try {
            params.put("oauth_token", App.sOAuth);
            params.put("location_id", mSpnr_SelectCity.getSelectedItemId());
            JSONArray arr = new JSONArray(Arrays.asList(referralEmailArr));
            params.put("refer_friend_emails",arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
        WSHandle.Referral.createReferrals(params,new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                dialog.show(getChildFragmentManager(), "dlg-frag");
            }

            @Override
            public void onSuccessResponse(String response) {
                progressDialog.dismiss();
                final BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                dialog.setNeutralButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //getActivity().startActivityForResult(getActivity().getIntent(), UPDATE_USER_DETAILS);
                        getActivity().finish();
                    }
                });
                dialog.show(getChildFragmentManager(), "dlg-frag");
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_FIRST_USER) {
            startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
