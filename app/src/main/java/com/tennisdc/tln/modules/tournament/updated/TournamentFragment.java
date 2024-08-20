package com.tennisdc.tln.modules.tournament.updated;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.model.Competition;
import com.tennisdc.tln.model.Tab;
import com.tennisdc.tln.model.TournamentRound;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

public class TournamentFragment extends Fragment {

	public final static String BUNDLE_EXTRA_COMPETITION = "competition";

	@BindView(R.id.tabs)
	TabLayout tabs;

	@BindView(R.id.pager)
	ViewPager pager;
	int matchRound;
	Competition competition;
	private List<Tab> tabList;

	public static TournamentFragment newInstance(Competition competition) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(BUNDLE_EXTRA_COMPETITION, competition);

		TournamentFragment tournamentFragment = new TournamentFragment();
		tournamentFragment.setArguments(bundle);

		return tournamentFragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_tourneys, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		App.LogFacebookEvent(getActivity(),this.getClass().getName());

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (null != getArguments()) {
			competition = getArguments().getParcelable(BUNDLE_EXTRA_COMPETITION);


			tabList = new ArrayList<>();

			// Get Competition Detail
			fetchTournamentListDetails();
		}

	}

	private void fetchTournamentListDetails() {

		getActivity().setTitle(competition.CompetitionName);
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null,
				"Please wait...", true, false);

		WSHandle.Tournament.getTournamentDetailsList(competition.CompetitionId,
				new VolleyHelper.IRequestListener<List<TournamentRound>, String>() {
					@Override
					public void onFailureResponse(String response) {
						progressDialog.dismiss();
						Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccessResponse(List<TournamentRound> response) {
						// Log.e("PotyResponse",response.toString());
						progressDialog.dismiss();
						if (response != null && response.size() > 0) {
							setUpChildUi(response);
						}
					}

					@Override
					public void onError(VolleyError error) {
						progressDialog.dismiss();
						Toast.makeText(getActivity(), getString(R.string.network_error),
								Toast.LENGTH_LONG).show();
					}
				});
	}

	private void setUpChildUi(List<TournamentRound> response) {

		if (response.size() < 1) {
			return;
		}
		matchRound = response.size();

		switch (matchRound) {
			case 5:
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(0)),
						getString(R.string.tab_title_round_32)));
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(1)),
						getString(R.string.tab_title_round_16)));
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(2)),
						getString(R.string.tab_title_quarter_finals)));
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(3)),
						getString(R.string.tab_title_semi_finals)));
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(4)),
						getString(R.string.tab_title_finals)));
				break;
			case 4:
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(0)),
						getString(R.string.tab_title_round_16)));
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(1)),
						getString(R.string.tab_title_quarter_finals)));
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(2)),
						getString(R.string.tab_title_semi_finals)));
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(3)),
						getString(R.string.tab_title_finals)));
				break;
			case 3:
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(0)),
						getString(R.string.tab_title_quarter_finals)));
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(1)),
						getString(R.string.tab_title_semi_finals)));
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(2)),
						getString(R.string.tab_title_finals)));
				break;
			case 2:
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(0)),
						getString(R.string.tab_title_semi_finals)));
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(1)),
						getString(R.string.tab_title_finals)));
				break;
			default:
				tabList.add(new Tab(TournamentChildFragment.newInstance(response.get(0)),
						getString(R.string.tab_title_finals)));
				break;
		}

		pager.setAdapter(new TabAdapter(getChildFragmentManager(), tabList));
		tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
		tabs.setupWithViewPager(pager);

		// Setting Current Tab to the next FourthComing Match
		for (int i = 0; i < response.size(); i++) {
			if (response.get(i).OpenDefault)
				pager.setCurrentItem(i);
		}
	}
}
