package com.tennisdc.tln.modules.tournament.updated;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.model.TournamentPlayer;
import com.tennisdc.tln.model.TournamentRoundDetails;
import com.tennisdc.tln.modules.common.DlgSendMail;
import com.tennisdc.tln.modules.myAccount.MyAccountScreen;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

import static com.tennisdc.tln.modules.tournament.updated.TournamentChildFragment.GAME_PER_ROW;

public class FinalTournamentChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


	private Context mContext;
	private Fragment mFragment;
	private LayoutInflater mLayoutInflator;
	private ArrayList<TournamentRoundDetails> matchList;
	private PlayerDetail playerToContact;
	private TournamentPlayer selectedPlayer;

	FinalTournamentChildAdapter(Fragment fragment) {
		this.mContext = fragment.getActivity();
		mFragment = fragment;
		this.mLayoutInflator = LayoutInflater.from(mContext);
		matchList = new ArrayList<>();
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new EndTourneyHolder(mLayoutInflator.inflate(R.layout.row_final_tournament, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (matchList.size() > 0)
			((EndTourneyHolder) holder).bindMatch(matchList.get(position * GAME_PER_ROW));
	}

	@Override
	public int getItemCount() {
		return matchList.size();
	}

	void updateList(List<TournamentRoundDetails> list) {
		if (list != null && list.size() > 0) {
			matchList.clear();
			matchList.addAll(list);
			notifyDataSetChanged();
		}
	}

	class EndTourneyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		@BindView(R.id.tvSetOnePlayerOne)
		TextView tvSetOnePlayerOne;
		@BindView(R.id.tvSetOnePlayerTwo)
		TextView tvSetOnePlayerTwo;
		@BindView(R.id.tvSetOneScoreOne)
		TextView tvSetOneScoreOne;
		@BindView(R.id.tvSetOneScoreTwo)
		TextView tvSetOneScoreTwo;
		@BindView(R.id.tvSetOneScoreThree)
		TextView tvSetOneScoreThree;
		@BindView(R.id.tvSetOneTB)
		TextView tvSetOneTB;
		@BindView(R.id.tvChampionName)
		TextView tvChampionName;
		@BindView(R.id.tvTextChampion)
		TextView tvTextChampion;
		@BindView(R.id.ivPlusSetOnePlayerOne)
		ImageView ivPlusSetOnePlayerOne;
		@BindView(R.id.ivPlusSetOnePlayerTwo)
		ImageView ivPlusSetOnePlayerTwo;
		@BindView(R.id.sdvChampion)
		ImageView sdvChampion;
		@BindView(R.id.groupSetOne)
		Group groupSetOne;


		EndTourneyHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);

			// Set ClickListener
			ivPlusSetOnePlayerOne.setOnClickListener(this);
			ivPlusSetOnePlayerTwo.setOnClickListener(this);
		}

		void bindMatch(TournamentRoundDetails tournamentRoundDetailsOne) {
			// Set Player Name
			if(tournamentRoundDetailsOne.Player1.Profile_active){
				if (null != tournamentRoundDetailsOne.Player1)
					tvSetOnePlayerOne.setText(Html.fromHtml("<u>" + tournamentRoundDetailsOne.Player1.Name+ "</b>"));
				if (null != tournamentRoundDetailsOne.Player2)
					tvSetOnePlayerTwo.setText(Html.fromHtml("<u>" + tournamentRoundDetailsOne.Player2.Name+ "</b>"));
                tvSetOnePlayerOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != tournamentRoundDetailsOne.Player1) {
                            mContext.startActivity(new Intent(mContext, MyAccountScreen.class)
                                    .putExtra("mPlayerId", String.valueOf(tournamentRoundDetailsOne.Player1.getPlayer_id())));
                        }
                        if (null != tournamentRoundDetailsOne.Player2) {
                            mContext.startActivity(new Intent(mContext, MyAccountScreen.class)
                                    .putExtra("mPlayerId", String.valueOf(tournamentRoundDetailsOne.Player2.getPlayer_id())));
                        }

                    }
                });
			}else{
				if (null != tournamentRoundDetailsOne.Player1)
					tvSetOnePlayerOne.setText(tournamentRoundDetailsOne.Player1.Name);
				if (null != tournamentRoundDetailsOne.Player2)
					tvSetOnePlayerTwo.setText(tournamentRoundDetailsOne.Player2.Name);
                tvSetOnePlayerOne.setOnClickListener(null);
			}

			// Highlight the Winner
			if (tournamentRoundDetailsOne.Player1 != null && tournamentRoundDetailsOne.Player1.isWinner()) {
				tvTextChampion.setVisibility(View.VISIBLE);
				sdvChampion.setVisibility(View.VISIBLE);
				sdvChampion.setVisibility(View.VISIBLE);
				tvSetOnePlayerOne.setTypeface(null, Typeface.BOLD);
				Glide.with(mContext).load(tournamentRoundDetailsOne.Player1.Image).placeholder(R.drawable.user).into(sdvChampion);
//				sdvChampion.setImageURI(Uri.parse(tournamentRoundDetailsOne.Player1.Image));
				tvChampionName.setText(tournamentRoundDetailsOne.Player1.Name);
			} else if (tournamentRoundDetailsOne.Player2 != null && tournamentRoundDetailsOne.Player2.isWinner()) {
				tvTextChampion.setVisibility(View.VISIBLE);
				sdvChampion.setVisibility(View.VISIBLE);
				sdvChampion.setVisibility(View.VISIBLE);
				tvSetOnePlayerTwo.setTypeface(null, Typeface.BOLD);
				Glide.with(mContext).load(tournamentRoundDetailsOne.Player2.Image).placeholder(R.drawable.user).into(sdvChampion);
//				sdvChampion.setImageURI(Uri.parse(tournamentRoundDetailsOne.Player2.Image));
				tvChampionName.setText(tournamentRoundDetailsOne.Player2.Name);
			} else {
				tvTextChampion.setVisibility(View.GONE);
				sdvChampion.setVisibility(View.GONE);
				sdvChampion.setVisibility(View.GONE);
			}

			// Set Contact options
			if (null != tournamentRoundDetailsOne.Player1 && tournamentRoundDetailsOne.Player1.Contact) {
				ivPlusSetOnePlayerOne.setVisibility(View.VISIBLE);
			} else {
				ivPlusSetOnePlayerOne.setVisibility(View.GONE);
			}

			if (null != tournamentRoundDetailsOne.Player2 && tournamentRoundDetailsOne.Player2.Contact) {
				ivPlusSetOnePlayerTwo.setVisibility(View.VISIBLE);
			} else {
				ivPlusSetOnePlayerTwo.setVisibility(View.GONE);
			}

			// Set Scores
			String score1 = tournamentRoundDetailsOne.Score != null ? tournamentRoundDetailsOne.Score : "";
			if (score1.isEmpty()) {
				groupSetOne.setVisibility(View.GONE);
			} else {
				groupSetOne.setVisibility(View.VISIBLE);
				if (score1.contains(";")) {
					String[] scores1 = score1.split(";");
					switch (scores1.length) {
						case 1:
							tvSetOneScoreOne.setText(scores1[0].trim());
							break;
						case 2:
							tvSetOneScoreOne.setText(scores1[0].trim());
							tvSetOneScoreTwo.setText(scores1[1].trim());
							break;
						case 3:
							tvSetOneScoreOne.setText(scores1[0].trim());
							tvSetOneScoreTwo.setText(scores1[1].trim());
							if (scores1[2].contains("TB")) {
								tvSetOneTB.setText(getTextFromHtml(scores1[2]));
							} else {
								tvSetOneScoreThree.setText(scores1[2].trim());
							}
							break;
					}
				} else {
					tvSetOneScoreOne.setText(score1.trim());
				}
			}
		}

		@Override
		public void onClick(View view) {
			PopupMenu popupMenu = new PopupMenu(mContext, view);

			switch (view.getId()) {
				case R.id.ivPlusSetOnePlayerOne:
					popupMenu.inflate(R.menu.contact_popup_menu);
					selectedPlayer = matchList.get(getAdapterPosition() * GAME_PER_ROW).Player1;
					break;
				case R.id.ivPlusSetOnePlayerTwo:
					popupMenu.inflate(R.menu.contact_popup_menu);
					selectedPlayer = matchList.get(getAdapterPosition() * GAME_PER_ROW).Player2;
					break;
			}
			popupMenu.show();

			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {

					playerToContact = new PlayerDetail();
					playerToContact.Email = selectedPlayer.Email;
					playerToContact.Name = selectedPlayer.Name;
					playerToContact.Phone = selectedPlayer.Phone;
					switch (menuItem.getItemId()) {
						case R.id.action_contact_call:
							Intent intentCall = new Intent(Intent.ACTION_DIAL);
							intentCall.setData(Uri.parse("tel:" + selectedPlayer.Phone));
							mContext.startActivity(Intent.createChooser(intentCall, "Call"));
							break;
						case R.id.action_contact_mail:
							DlgSendMail.getDialogInstancePartnerProgram(new
									Prefs.AppData(mContext).getPlayer(), playerToContact)
									.show(mFragment.getChildFragmentManager(), "dlg-mail");
							break;
						case R.id.action_contact_message:
							Intent intentMessage = new Intent(Intent.ACTION_SENDTO,
									Uri.parse("smsto:" + selectedPlayer.Phone));
							mContext.startActivity(Intent.createChooser(intentMessage,
									"Send Message"));
							break;
					}
					return true;
				}
			});
		}

		Spanned getTextFromHtml(String htmlText) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				return Html.fromHtml(htmlText.trim(), Html.FROM_HTML_MODE_LEGACY);
			} else {
				return Html.fromHtml(htmlText.trim());
			}
		}
	}
}
