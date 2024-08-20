package com.tennisdc.tln.modules.tournament.updated;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.model.Competition;
import com.tennisdc.tln.model.TournamentRound;

import butterknife.ButterKnife;
import butterknife.BindView;

public class TournamentChildFragment extends Fragment {


	public final static String BUNDLE_EXTRA_TOURNAMENT_ROUND = "mTournamentRound";
	public static final int GAME_PER_ROW = 2;
	private final static String ROUND_16 = "Round 16";
	private final static String ROUND_32 = "Round 32";
	private final static String QUARTER_FINALS = "Quarter-finals";
	private final static String SEMI_FINALS = "Semi-finals";
	private final static String FINALS = "Finals";
	public static int THRESHOLD_ROUND_COUNT = 3;
	@BindView(R.id.tvDate)
	TextView tvDate;
	@BindView(R.id.recyclerView)
	RecyclerView recyclerView;

	public static TournamentChildFragment newInstance(TournamentRound tournamentRound) {
		Bundle args = new Bundle();

		args.putParcelable(BUNDLE_EXTRA_TOURNAMENT_ROUND, tournamentRound);

		TournamentChildFragment fragment = new TournamentChildFragment();
		fragment.setArguments(args);

		return fragment;
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fargment_child_tournament, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.bind(this, view);
		App.LogFacebookEvent(getActivity(),this.getClass().getName());

		// Set adapter
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
				LinearLayoutManager.VERTICAL, false));
		if (null != getArguments()) {

			TournamentRound mTournamentRound = getArguments().getParcelable(BUNDLE_EXTRA_TOURNAMENT_ROUND);
			tvDate.setText(mTournamentRound.TournamentDate);

			switch (mTournamentRound.getTournamentTitle()) {
				case ROUND_32:
					NonFinalTournamentChildAdapter mStartTournamentChildAdapter =
							new NonFinalTournamentChildAdapter(this,
									NonFinalTournamentChildAdapter.TOURNAMENT_TITLE_ROUND16);
					recyclerView.setAdapter(mStartTournamentChildAdapter);
					mStartTournamentChildAdapter.updateList(mTournamentRound.getMatchesList());
					break;
				case ROUND_16:
					NonFinalTournamentChildAdapter mRound16TournamentChildAdapter =
							new NonFinalTournamentChildAdapter(this,
									NonFinalTournamentChildAdapter.TOURNAMENT_TITLE_ROUND16);
					recyclerView.setAdapter(mRound16TournamentChildAdapter);
					mRound16TournamentChildAdapter.updateList(mTournamentRound.getMatchesList());
					break;
				case QUARTER_FINALS:
					NonFinalTournamentChildAdapter mNonFinalTournamentChildAdapter;
					if (((TournamentFragment) getParentFragment()).matchRound > THRESHOLD_ROUND_COUNT) {
						mNonFinalTournamentChildAdapter = new NonFinalTournamentChildAdapter(
								this, NonFinalTournamentChildAdapter.TOURNAMENT_TITLE_OTHERS);
					} else {
						mNonFinalTournamentChildAdapter =
								new NonFinalTournamentChildAdapter(this,
										NonFinalTournamentChildAdapter.TOURNAMENT_TITLE_ROUND16);
					}
					recyclerView.setAdapter(mNonFinalTournamentChildAdapter);
					mNonFinalTournamentChildAdapter.updateList(mTournamentRound.getMatchesList());
					break;
				case SEMI_FINALS:
					NonFinalTournamentChildAdapter mNonFinalTournamentChildAdapter1;
					if (((TournamentFragment) getParentFragment()).matchRound >= THRESHOLD_ROUND_COUNT) {
						mNonFinalTournamentChildAdapter1 =
								new NonFinalTournamentChildAdapter(this,
										NonFinalTournamentChildAdapter.TOURNAMENT_TITLE_OTHERS);
					} else {
						mNonFinalTournamentChildAdapter1 =
								new NonFinalTournamentChildAdapter(this,
										NonFinalTournamentChildAdapter.TOURNAMENT_TITLE_ROUND16);
					}
					recyclerView.setAdapter(mNonFinalTournamentChildAdapter1);
					mNonFinalTournamentChildAdapter1.updateList(mTournamentRound.getMatchesList());
					break;
				case FINALS:
					FinalTournamentChildAdapter mFinalTournamentChildAdapter =
							new FinalTournamentChildAdapter(this);
					recyclerView.setAdapter(mFinalTournamentChildAdapter);
					mFinalTournamentChildAdapter.updateList(mTournamentRound.getMatchesList());
					break;
			}
		}
	}

	Competition getCompetition() {
		return ((TournamentFragment) getParentFragment()).competition;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
