package com.tennisdc.tennisleaguenetwork;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.GsonRealmExclusionStrategy;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.AlertDetail;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.SubmittedScore;
import com.tennisdc.tennisleaguenetwork.modules.partner.FragPartnerProgramDetails;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.ui.RecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;
//import org.parceler.guava.collect.Lists;

import java.util.List;
import butterknife.ButterKnife;
import butterknife.BindView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tennisdc.tln.R;

/**
 * Created by Inderjeet Kumar on 11/01/18.
 */

public class FragAlert  extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    RecyclerAdapter adapter;

    List<AlertDetail> mAlertDetails;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_alert_list, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle("Upcoming Programs");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_FIRST_USER) {
            startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        Request divisionReportRequest = WSHandle.Alerts.getDetailsRequest(App.sOAuth, new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
//                    if(response.getString("responseCode").equals("404"))
                        progressDialog.dismiss();

                    Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

                    mAlertDetails = gson.fromJson(response.getString("slider_images"), new TypeToken<List<AlertDetail>>() {}.getType());

                    adapter = new RecyclerAdapter<AlertDetail, FragAlert.AlertDetailsHeader>(mAlertDetails) {
                        @Override
                        public FragAlert.AlertDetailsHeader onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(getActivity()).inflate(R.layout.frag_alert_item, null);
                            return new FragAlert.AlertDetailsHeader(view);
                        }

                        @Override
                        public void onBindViewHolder(FragAlert.AlertDetailsHeader holder, int position) {
                            holder.bindItem(getItem(position));
                        }
                    };

                    mRecyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
        VolleyHelper.getInstance(getActivity()).addToRequestQueue(divisionReportRequest);
    }

    class AlertDetailsHeader extends RecyclerView.ViewHolder {

        @BindView(R.id.alert_image)
        NetworkImageView PlayerImageView;

        @BindView(R.id.alert_desc)
        TextView alertDesc;

        @BindView(R.id.alert_title)
        TextView alertTitle;

        private AlertDetailsHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindItem(AlertDetail alertDetails) {
            alertTitle.setText(alertDetails.getTitle());
            alertDesc.setText(Html.fromHtml(alertDetails.tagDesc));
            PlayerImageView.setDefaultImageResId(android.R.drawable.ic_menu_gallery);

            if (!TextUtils.isEmpty(alertDetails.getImageUrl())) {
                Glide.with(getActivity()).load(alertDetails.getImageUrl()).placeholder(android.R.drawable.ic_menu_gallery).into(PlayerImageView);
//                PlayerImageView.setImageUrl(alertDetails.getImageUrl(), VolleyHelper.getInstance(getActivity()).getImageLoader());
                PlayerImageView.setVisibility(View.VISIBLE);
            } else {
                PlayerImageView.setVisibility(View.GONE);
            }
        }
    }
}
