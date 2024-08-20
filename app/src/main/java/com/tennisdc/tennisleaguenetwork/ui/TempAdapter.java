package com.tennisdc.tennisleaguenetwork.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.tennisdc.tln.R;
import androidx.appcompat.app.AppCompatActivity;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.Competition;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.TournamentPlayer;
import com.tennisdc.tennisleaguenetwork.model.TournamentRound;
import com.tennisdc.tennisleaguenetwork.model.TournamentRoundDetails;
import com.tennisdc.tennisleaguenetwork.modules.common.DlgSendMail;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import androidx.core.content.ContextCompat;

/**
 * Created on 30-May-17.
 */

public class TempAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private Competition mCurCompetition;
    private List<TournamentRound> header = new ArrayList<TournamentRound>();// header titles
    private HashMap<String, List<TournamentRoundDetails>> child = new HashMap<String, List<TournamentRoundDetails>>();

    public TempAdapter(Context context, List<TournamentRound> listDataHeader,
                                 HashMap<String, List<TournamentRoundDetails>> listChildData, Competition curCompetition) {
        this._context = context;
        this.header = listDataHeader;
        this.child = listChildData;

        this.mCurCompetition = curCompetition;
    }

    @Override
    public TournamentRoundDetails getChild(int groupPosition, int childPosititon) {
        // This will return the child
        final TournamentRound tournamentRound = this.header.get(groupPosition);
       /* return tournamentRound.getTournamentRoundDeatilsList().get(childPosititon);*/
        return tournamentRound.getMatchesList().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ButterKnife.bind(this, convertView);

        // Inflating child layout and setting textview
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_tournament_child_items, parent, false);
        }
        if(header.get(groupPosition).getMatchesList() != null)
        {
            final LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout_childItems);
            final LinearLayout mScoreLayout = (LinearLayout) convertView.findViewById(R.id.layout_score);
            final TextView mPlayer1Label = (TextView) convertView.findViewById(R.id.player_1);
            final TextView mPlayer1 = (TextView) convertView.findViewById(R.id.txt_player1);
            final TextView mPlayer2Label = (TextView) convertView.findViewById(R.id.player_2);
            final TextView mPlayer2 = (TextView) convertView.findViewById(R.id.txt_player2);
            final TextView mScoreLabel = (TextView) convertView.findViewById(R.id.score);
            final TextView mScoreValue = (TextView) convertView.findViewById(R.id.txt_score);
            final ImageView mImgContactPlayer1 = (ImageView) convertView.findViewById(R.id.imgContactPlayer1);
            final ImageView mImgContactPlayer2 = (ImageView) convertView.findViewById(R.id.imgContactPlayer2);


            if((childPosition % 2) == 0) //Is even
                //layout.setBackgroundColor(Color.parseColor("#D3D3D3"));// #89CFF0 //#C0C0C0
                layout.setBackgroundColor(ContextCompat.getColor(this._context, R.color.gray_button_border));
             else {//Is odd
               // layout.setBackgroundColor(Color.parseColor("#D1D1D1")); // #A1CAF1 //#D3D3D3
                layout.setBackgroundColor(ContextCompat.getColor(this._context, R.color.gray_button_light));
            }

            final TournamentRoundDetails tournamentRoundDetails = getChild(groupPosition, childPosition);

            final TournamentPlayer player1 = tournamentRoundDetails.getPlayer1();
            final TournamentPlayer player2 = tournamentRoundDetails.getPlayer2();

            final PlayerDetail mCurPlayer = new Prefs.AppData(this._context).getPlayer();
            final PlayerDetail mtoPlayer = new Prefs.AppData(this._context).getPlayer(); // Bad codding... placing dummy data.

            if(player1!=null) {
                mPlayer1.setText(player1.getName());
                if(player1.isWinner()) {
                    mPlayer1Label.setTypeface(null, Typeface.BOLD);
                    mPlayer1.setTypeface(null, Typeface.BOLD);
                } else {
                    mPlayer1Label.setTypeface(null, Typeface.NORMAL);
                    mPlayer1.setTypeface(null, Typeface.NORMAL);
                }

                if(player1.isContact()) {
                    mImgContactPlayer1.setVisibility(View.VISIBLE);
                    mtoPlayer.setDummyData(player1.getName(), player1.getEmail(), player1.getPhone());

                    mImgContactPlayer1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showContactPopup(v , mCurPlayer, mtoPlayer);
                        }
                    });
                } else {
                    mImgContactPlayer1.setVisibility(View.INVISIBLE);
                }
            }else {
                mPlayer1.setText(" ");
                mImgContactPlayer1.setVisibility(View.INVISIBLE);
            }

            if(player2!=null) {
                mPlayer2.setText(player2.getName());
                if(player2.isWinner()) {
                    mPlayer2Label.setTypeface(null, Typeface.BOLD);
                    mPlayer2.setTypeface(null, Typeface.BOLD);
                } else {
                    mPlayer2Label.setTypeface(null, Typeface.NORMAL);
                    mPlayer2.setTypeface(null, Typeface.NORMAL);
                }

                if(player2.isContact()) {
                    mImgContactPlayer2.setVisibility(View.VISIBLE);
                    mtoPlayer.setDummyData(player2.getName(), player2.getEmail(), player2.getPhone());

                    mImgContactPlayer2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showContactPopup(v , mCurPlayer, mtoPlayer);
                        }
                    });
                } else {
                    mImgContactPlayer2.setVisibility(View.INVISIBLE);
                }
            }else {
                mPlayer2.setText(" ");
                mImgContactPlayer2.setVisibility(View.INVISIBLE);
            }

            final String score = tournamentRoundDetails.getScore();
            if(score != null && !score.isEmpty()) {
                mScoreLayout.setVisibility(View.VISIBLE);
                mScoreValue.setText(getChild(groupPosition, childPosition).getScore());
            }
            else {
                mScoreLayout.setVisibility(View.GONE);
            }
        }
        return convertView;
    }


    public void showContactPopup(View contactImg ,final PlayerDetail curPlayer, final PlayerDetail playerDetail){
        PopupMenu popup = new PopupMenu(this._context, contactImg);
        MenuInflater inflater = popup.getMenuInflater();
        Menu popupMenu = popup.getMenu();
        inflater.inflate(R.menu.contact_popup_menu, popupMenu);

        if(playerDetail.Email == null || playerDetail.Email.isEmpty())
            popupMenu.findItem(R.id.action_contact_mail).setVisible(false);

        if(playerDetail.Phone == null || playerDetail.Phone.isEmpty()) {
            popupMenu.findItem(R.id.action_contact_call).setVisible(false);
            popupMenu.findItem(R.id.action_contact_message).setVisible(false);
        }

       /* popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                ContactImageView.setSelected(false);
            }
        });*/

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_contact_call: {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + playerDetail.Phone));
                        _context.startActivity(Intent.createChooser(intent, "Call"));
                    }
                    return true;

                    case R.id.action_contact_message: {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" +  playerDetail.Phone));
                        _context.startActivity(Intent.createChooser(intent, "Send Message"));
                    }
                    return true;

                    case R.id.action_contact_mail: {
                       // DlgSendMail.getDialogInstanceTournament(mCurCompetition, curPlayer, playerDetail).show(getChildFragmentManager(), "dlg-mail");
                        DlgSendMail.getDialogInstanceTournament(mCurCompetition, curPlayer, playerDetail).show((((AppCompatActivity)_context).getSupportFragmentManager()), "dlg-mail");
                    }
                    return true;
                }
                return false;
            }
        });
        popup.show();
        //ContactImageView.setSelected(true);
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        // return children count
        if(this.header.get(groupPosition).getMatchesList()!=null)
            return this.header.get(groupPosition).getMatchesList().size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        // Get header position
        return this.header.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        // Get header size
        if(this.header != null)
             return this.header.size();
        else
            return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        // Inflating header layout and setting text
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_tournament_group_header, parent, false);
        }

        final TextView mType = (TextView) convertView.findViewById(R.id.txt_tournamentType);
        final TextView mDate = (TextView) convertView.findViewById(R.id.txt_tournamentDate);
        final TextView mShow = (TextView) convertView.findViewById(R.id.txt_show);
        final ImageView mImgArrow = (ImageView) convertView.findViewById(R.id.img_arrow);

        mType.setText(header.get(groupPosition).getTournamentTitle());
        mDate.setText(header.get(groupPosition).getTournamentDate());

        // If group is expanded then change the text
        if(header.get(groupPosition).getMatchesList() != null && header.get(groupPosition).getMatchesList().size() > 0) {
            if(isExpanded) {
                mImgArrow.setVisibility(View.VISIBLE);
                mImgArrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_white_24dp);  //mShow.setText("Hide");
                mShow.setVisibility(View.GONE);
            }
            else {
                mImgArrow.setVisibility(View.VISIBLE);
                mImgArrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_white_24dp);  //mShow.setText("Show");
                mShow.setVisibility(View.GONE);
            }
        } else {
            mShow.setVisibility(View.VISIBLE);
            mShow.setText("No data");
            mImgArrow.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
