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

public class NonFinalTournamentChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    static int TOURNAMENT_TITLE_ROUND16 = 16;
    static int TOURNAMENT_TITLE_OTHERS = 89;

    private Context mContext;
    private Fragment mFragment;
    private LayoutInflater mLayoutInflator;
    private ArrayList<TournamentRoundDetails> matchList;
    private PlayerDetail playerToContact;
    private TournamentPlayer selectedPlayer;
    private int tournamentTitleType;

    NonFinalTournamentChildAdapter(Fragment fragment, int tournamentTitleType) {
        this.mContext = fragment.getActivity();
        this.mLayoutInflator = LayoutInflater.from(mContext);
        matchList = new ArrayList<>();
        mFragment = fragment;
        this.tournamentTitleType = tournamentTitleType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MidTourneyHolder(mLayoutInflator.inflate(R.layout.row_non_final_tournament, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MidTourneyHolder) holder).bindMatch(matchList.get(position * GAME_PER_ROW), matchList.get(position * GAME_PER_ROW + 1));
    }

    @Override
    public int getItemCount() {
        return matchList.size() / GAME_PER_ROW;
    }

    void updateList(List<TournamentRoundDetails> list) {
        if (list != null && list.size() > 0) {
            matchList.clear();
            matchList.addAll(list);
            notifyDataSetChanged();
        }
    }

    class MidTourneyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        @BindView(R.id.ivPlusSetOnePlayerOne)
        ImageView ivPlusSetOnePlayerOne;
        @BindView(R.id.ivPlusSetOnePlayerTwo)
        ImageView ivPlusSetOnePlayerTwo;
        @BindView(R.id.viewOneLineLeft)
        View viewOneLineLeft;
        @BindView(R.id.groupSetOne)
        Group groupSetOne;

        @BindView(R.id.tvSetTwoPlayerOne)
        TextView tvSetTwoPlayerOne;
        @BindView(R.id.tvSetTwoPlayerTwo)
        TextView tvSetTwoPlayerTwo;
        @BindView(R.id.tvSetTwoScoreOne)
        TextView tvSetTwoScoreOne;
        @BindView(R.id.tvSetTwoScoreTwo)
        TextView tvSetTwoScoreTwo;
        @BindView(R.id.tvSetTwoScoreThree)
        TextView tvSetTwoScoreThree;
        @BindView(R.id.tvSetTwoTB)
        TextView tvSetTwoTB;
        @BindView(R.id.ivPlusSetTwoPlayerOne)
        ImageView ivPlusSetTwoPlayerOne;
        @BindView(R.id.ivPlusSetTwoPlayerTwo)
        ImageView ivPlusSetTwoPlayerTwo;
        @BindView(R.id.viewTwoLineLeft)
        View viewTwoLineLeft;
        @BindView(R.id.groupSetTwo)
        Group groupSetTwo;

        MidTourneyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Visibility Toggling according to Match Round
            if (tournamentTitleType == TOURNAMENT_TITLE_ROUND16) {
                viewOneLineLeft.setVisibility(View.INVISIBLE);
                viewTwoLineLeft.setVisibility(View.INVISIBLE);
            } else {
                viewOneLineLeft.setVisibility(View.VISIBLE);
                viewTwoLineLeft.setVisibility(View.VISIBLE);
            }

            // Set ClickListener
            ivPlusSetOnePlayerOne.setOnClickListener(this);
            ivPlusSetOnePlayerTwo.setOnClickListener(this);
            ivPlusSetTwoPlayerOne.setOnClickListener(this);
            ivPlusSetTwoPlayerOne.setOnClickListener(this);
        }

        void bindMatch(TournamentRoundDetails tournamentRoundDetailsOne,
                       TournamentRoundDetails tournamentRoundDetailsTwo) {
            // Set Player Name SetOne
            if(tournamentRoundDetailsOne.Player1.Profile_active == null)
                tournamentRoundDetailsOne.Player1.Profile_active = false;
            if(tournamentRoundDetailsOne.Player1.Profile_active){
                tvSetOnePlayerOne.setText(Html.fromHtml("<u>" + tournamentRoundDetailsOne.Player1.Name+ "</b>"));
                tvSetOnePlayerOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, MyAccountScreen.class)
                                .putExtra("mPlayerId", String.valueOf(tournamentRoundDetailsOne.Player1.getPlayer_id())));
                    }
                });
            }else{
                tvSetOnePlayerOne.setText(tournamentRoundDetailsOne.Player1.Name);
                tvSetOnePlayerOne.setOnClickListener(null);
            }

            if(tournamentRoundDetailsOne.Player2.Profile_active == null)
                tournamentRoundDetailsOne.Player2.Profile_active = false;
            if(tournamentRoundDetailsOne.Player2.Profile_active){
                tvSetOnePlayerTwo.setText(Html.fromHtml("<u>" + tournamentRoundDetailsOne.Player2.Name+ "</b>"));
                tvSetOnePlayerTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, MyAccountScreen.class)
                                .putExtra("mPlayerId", String.valueOf(tournamentRoundDetailsOne.Player2.getPlayer_id())));
                    }
                });
            }else{
                tvSetOnePlayerTwo.setText(tournamentRoundDetailsOne.Player2.Name);
                tvSetOnePlayerTwo.setOnClickListener(null);
            }
//            if (tournamentRoundDetailsOne.Player1.Active == null) {
//                tvSetOnePlayerOne.setText(Html.fromHtml("<u>" + tournamentRoundDetailsOne.Player1.Name + "</b>"));
//                tvSetOnePlayerOne.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mContext.startActivity(new Intent(mContext, MyAccountScreen.class)
//                                .putExtra("mPlayerId", String.valueOf(tournamentRoundDetailsOne.Player1.)));
//                    }
//                });
//            }else{
//
//            }
            // Highlight the Winner SetOne
            if (tournamentRoundDetailsOne.Player1.isWinner()) {
                tvSetOnePlayerOne.setTypeface(null, Typeface.BOLD);
            } else if (tournamentRoundDetailsOne.Player2.isWinner()) {
                tvSetOnePlayerTwo.setTypeface(null, Typeface.BOLD);
            }

            // Set Contact options SetOne
            if (tournamentRoundDetailsOne.Player1.Contact) {
                ivPlusSetOnePlayerOne.setVisibility(View.VISIBLE);
            } else {
                ivPlusSetOnePlayerOne.setVisibility(View.GONE);
            }
            if (tournamentRoundDetailsOne.Player2.Contact) {
                ivPlusSetOnePlayerTwo.setVisibility(View.VISIBLE);
            } else {
                ivPlusSetOnePlayerTwo.setVisibility(View.GONE);
            }

            // Set Scores SetOne
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


            // Set Player Name SetTwo
            tvSetTwoPlayerOne.setText(tournamentRoundDetailsTwo.Player1.Name);
            tvSetTwoPlayerTwo.setText(tournamentRoundDetailsTwo.Player2.Name);

            // Highlight the Winner SetTwo
            if (tournamentRoundDetailsTwo.Player1.isWinner()) {
                tvSetTwoPlayerOne.setTypeface(null, Typeface.BOLD);
            } else if (tournamentRoundDetailsTwo.Player2.isWinner()) {
                tvSetTwoPlayerTwo.setTypeface(null, Typeface.BOLD);
            }

            // Set Contact options SetTwo
            if (tournamentRoundDetailsTwo.Player1.Contact) {
                ivPlusSetTwoPlayerOne.setVisibility(View.VISIBLE);
            } else {
                ivPlusSetTwoPlayerOne.setVisibility(View.GONE);
            }
            if (tournamentRoundDetailsTwo.Player2.Contact) {
                ivPlusSetTwoPlayerTwo.setVisibility(View.VISIBLE);
            } else {
                ivPlusSetTwoPlayerTwo.setVisibility(View.GONE);
            }

            // Set Scores SetTwo
            String score2 = tournamentRoundDetailsTwo.Score != null ? tournamentRoundDetailsTwo.Score : "";
            if (score2.isEmpty()) {
                groupSetTwo.setVisibility(View.GONE);
            } else {
                groupSetTwo.setVisibility(View.VISIBLE);
                if (score2.contains(";")) {
                    String[] scores2 = score2.split(";");
                    switch (scores2.length) {
                        case 1:
                            tvSetTwoScoreOne.setText(scores2[0].trim());
                            break;
                        case 2:
                            tvSetTwoScoreOne.setText(scores2[0].trim());
                            tvSetTwoScoreTwo.setText(scores2[1].trim());
                            break;
                        case 3:
                            tvSetTwoScoreOne.setText(scores2[0].trim());
                            tvSetTwoScoreTwo.setText(scores2[1].trim());
                            if (scores2[2].contains("TB")) {
                                tvSetTwoTB.setText(getTextFromHtml(scores2[2]));
                            } else {
                                tvSetTwoScoreThree.setText(scores2[2].trim());
                            }
                            break;
                    }
                } else {
                    tvSetTwoScoreOne.setText(score2.trim());
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
                case R.id.ivPlusSetTwoPlayerOne:
                    popupMenu.inflate(R.menu.contact_popup_menu);
                    selectedPlayer = matchList.get(getAdapterPosition() * GAME_PER_ROW + 1).Player1;
                    break;
                case R.id.ivPlusSetTwoPlayerTwo:
                    popupMenu.inflate(R.menu.contact_popup_menu);
                    selectedPlayer = matchList.get(getAdapterPosition() * GAME_PER_ROW + 1).Player2;
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
                            intentCall.setData(Uri.parse("tel:" + playerToContact.Phone));
                            mContext.startActivity(Intent.createChooser(intentCall, "Call"));
                            break;
                        case R.id.action_contact_mail:
                            DlgSendMail.getDialogInstanceTournament(((TournamentChildFragment) mFragment).getCompetition(), new
                                    Prefs.AppData(mContext).getPlayer(), playerToContact)
                                    .show(mFragment.getChildFragmentManager(), "dlg-mail");
                            break;
                        case R.id.action_contact_message:
                            Intent intentMessage = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + playerToContact.Phone));
                            mContext.startActivity(Intent.createChooser(intentMessage, "Send Message"));
                            break;
                    }
                    return true;
                }
            });
        }

        Spanned getTextFromHtml(String htmlText) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Html.fromHtml(htmlText.trim(), Html.FROM_HTML_MODE_COMPACT);
            } else {
                return Html.fromHtml(htmlText.trim());
            }
        }
    }
}
