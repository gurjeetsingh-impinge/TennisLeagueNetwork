package com.tennisdc.tln.modules.latest_score;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.Constants;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.LatestScoreModel;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.EmptyRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FragLastestScore extends Fragment {

    private EmptyRecyclerView mLatestScoreRecyclerView;
    private TextView txtTitleLatestScore;
    private List<LatestScoreModel> mScoreList = new ArrayList<>();
    RecentScoreAAdapter recentScoreAAdapter;
    Prefs.AppData prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mRootView = inflater.inflate(R.layout.fragment_frag_lastest_score, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());
        prefs = new Prefs.AppData(getActivity());
        mLatestScoreRecyclerView = (EmptyRecyclerView) mRootView.findViewById(R.id.recyclerView);
        txtTitleLatestScore = (TextView) mRootView.findViewById(R.id.txtTitleLatestScore);
        mLatestScoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mLatestScoreRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        WSHandle.Scores.getLatestScores(new VolleyHelper.IRequestListener<JSONObject, String>() {

            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                resetBadgeCount();
            }

            @Override
            public void onSuccessResponse(final JSONObject response) {
//                mScoreList = response;
                Type listType = new TypeToken<List<LatestScoreModel>>() {
                }.getType();
                try {
                    txtTitleLatestScore.setText(response.getString("title"));
                    mScoreList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create()
                            .fromJson(response.getJSONArray("scores")/*.getJSONObject(0).getJSONArray("scores")*/.toString(), listType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                if(mScoreList.size()>0){
//                    if(mScoreList.get(0).getScores().size()>0){
//                        mScoreList.get(0).getScores().get(0).setNewScore(true);
//                    }
//                }
                recentScoreAAdapter = new RecentScoreAAdapter(getActivity(), mScoreList);
                mLatestScoreRecyclerView.setAdapter(recentScoreAAdapter);
                progressDialog.dismiss();
                resetBadgeCount();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                resetBadgeCount();
            }
        });

//        VolleyHelper.getInstance(getActivity()).addToRequestQueue(competitionListRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Latest Scores");
    }
    void resetBadgeCount() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
        WSHandle.Profile.resetBadgeCount(new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
//                BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
//                dialog.show(getChildFragmentManager(), "dlg-frag");
            }

            @Override
            public void onSuccessResponse(String response) {
                progressDialog.dismiss();
                prefs.setNotificationCount(0);
                Intent intent = new Intent(Constants.UPDATE_NOTIFICATION_COUNT);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
//                Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
