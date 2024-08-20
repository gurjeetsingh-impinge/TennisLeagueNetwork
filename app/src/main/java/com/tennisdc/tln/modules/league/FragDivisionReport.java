//package com.tennisdc.tln.modules.league;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Bundle;
//import androidx.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupMenu;
//import android.widget.TextView;
//
//import com.tennisdc.tln.BaseDialog;
//import com.tennisdc.tln.R;
//import com.tennisdc.tln.SingleFragmentActivity;
//import com.tennisdc.tln.common.Prefs;
//import com.tennisdc.tln.model.Competition;
//import com.tennisdc.tln.model.DivisionBean;
//import com.tennisdc.tln.model.PlayerDetail;
//import com.tennisdc.tln.modules.common.DlgSendMail;
//import com.tennisdc.tln.ui.RecyclerAdapter;
//
//import org.parceler.Parcels;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.ButterKnife;
//import butterknife.BindView;
//
//import static com.tennisdc.tln.modules.league.FragLeagueDetails.isMultiSelect;
//
//
//public class FragDivisionReport extends Fragment {
//
//    private static final String EXTRA_COMPETITION = "cur_competition";
//    private static final String EXTRA_LIST = "object_list";
//    //private static final String EXTRA_COURTS = "courts";
//    private static final String EXTRA_SHOW_CANTACT = "can_show_contact";
//    private static final String EXTRA_IS_ENROLLED = "is_enrolled";
//    private static final String EXTRA_STATUS = "status";
//    private static final String RESULT_COMPETITION_ID = "competition_id";
//    private static final String RESULT_START_DATE = "mStartDate";
//
//    public static Fragment getInstance(Competition curCompetition, List<PlayerDetail> list, boolean canShowContact,
//                                       boolean isEnrolled, int mStatus, long mCompetitionID, String mStartDate) {
//        FragDivisionReport fragDivisionReport = new FragDivisionReport();
//
//        Bundle args = new Bundle();
//        //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
//        args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curCompetition));
//        args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
//        args.putBoolean(EXTRA_SHOW_CANTACT, canShowContact);
//        args.putBoolean(EXTRA_IS_ENROLLED, isEnrolled);
//        args.putInt(EXTRA_STATUS, mStatus);
//        args.putLong(RESULT_COMPETITION_ID, mCompetitionID);
//        args.putString(RESULT_START_DATE, mStartDate);
//
//        fragDivisionReport.setArguments(args);
//
//        return fragDivisionReport;
//    }
//
//    public static Fragment getInstance(DivisionBean curDivison, List<PlayerDetail> list, boolean canShowContact,
//                                       boolean isEnrolled, int mStatus, long mCompetitionID, String mStartDate) {
//        FragDivisionReport fragDivisionReport = new FragDivisionReport();
//
//        Bundle args = new Bundle();
//        //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
//        args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(curDivison));
//        args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
//        args.putBoolean(EXTRA_SHOW_CANTACT, canShowContact);
//        args.putBoolean(EXTRA_IS_ENROLLED, isEnrolled);
//        args.putInt(EXTRA_STATUS, mStatus);
//        args.putLong(RESULT_COMPETITION_ID, mCompetitionID);
//        args.putString(RESULT_START_DATE, mStartDate);
//
//        fragDivisionReport.setArguments(args);
//
//        return fragDivisionReport;
//    }
//
//    /* Views */
//    @BindView(R.id.recyclerView)
//    RecyclerView mRecyclerView;
//    @BindView(R.id.llEnroll)
//    LinearLayout mLayoutEnroll;
//    @BindView(R.id.txtEnrollMessage)
//    TextView mTxtEnrollLeague;
//    @BindView(R.id.btnJoinEnroll)
//    Button mBtnJoinEnroll;
//
//    /* Data */
////        private List<PlayerDetail> mPlayerDetails;
//
//    private Competition mCurCompetition;
//    private DivisionBean mCurDivision;
//    //private List<NameIdPair> mCourts;
//    private boolean mCanShowContact;
//    private boolean mIsEnrolled;
//    private int mStatus;
//    private String mStartDate;
//    RecyclerAdapter adapter;
//    private List<PlayerDetail> mPlayerDetails;
//
//    public void findTextViewTitle(String title) {
////			String title = "title";
//
//        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        ab.setTitle(title);
//
//        Window window = getActivity().getWindow();
//        View decor = window.getDecorView();
//
//        ArrayList<View> views = new ArrayList<View>();
//        decor.findViewsWithText(views, title, View.FIND_VIEWS_WITH_TEXT);
//
//        for (View view : views) {
//            Log.d("TAG", "view " + view.toString());
//            if(view instanceof TextView) {
//                TextView tvTitle = (TextView) view;
//                tvTitle.setTextSize(15f);
//                tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                tvTitle.setMarqueeRepeatLimit(-1);
//
//                // In order to start strolling, it has to be focusable and focused
//                tvTitle.setFocusable(true);
//                tvTitle.setFocusableInTouchMode(true);
//                tvTitle.requestFocus();
//            }
//        }
//    }
//    public void resetList() {
//        FragLeagueDetails.countSelection = 0;
//        if (mPlayerDetails != null) {
//            for (PlayerDetail playerDetail : mPlayerDetails) {
//                playerDetail.isSelected = false;
//            }
//        }
//        if (adapter != null) {
//            mRecyclerView.swapAdapter(adapter, false);
//            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//            adapter.notifyDataSetChanged();
//        }
//    }
//
//    public void updateRecyclerView(boolean isReset) {
//
//        mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(),
//                mRecyclerView.getPaddingTop(),
//                mRecyclerView.getPaddingRight(), ((isReset) ? 0 : 150));
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.frag_league_standings, container, false);
//
//        ButterKnife.bind(this, view);
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));
//
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        Bundle args = getArguments();
//        if (args == null) throw new RuntimeException("Missing Arguments");
//
//        if (Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION)) instanceof Competition)
//            mCurCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
//        else
//            mCurDivision = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
//
//        mPlayerDetails = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
//        //mCourts = Parcels.unwrap(args.getParcelable(EXTRA_COURTS));
//        mCanShowContact = args.getBoolean(EXTRA_SHOW_CANTACT, false);
//        mIsEnrolled = args.getBoolean(EXTRA_IS_ENROLLED, false);
//        mStatus = args.getInt(EXTRA_STATUS, 0);
//        mStartDate = args.getString(RESULT_START_DATE);
////            mCompetionID = args.getLong(RESULT_COMPETITION_ID);
////            getActivity().setTitle(mCurCompetition.get);
//        FragLeagueDetails.mCurPlayer = new Prefs.AppData(getActivity()).getPlayer();
//        if (mCurCompetition != null) {
//            getActivity().setTitle(mCurCompetition.CompetitionName + " Start Date: " + mCurCompetition.getStart_date());
//        } else {
//
//            findTextViewTitle(mCurDivision.getName() + " Start Date: " + mStartDate);
////                getActivity().setTitle(mCurDivision.getName() + " Start Date: " + mStartDate);
//        }
//        if (mStatus > 1 && !mIsEnrolled) {
//            mLayoutEnroll.setVisibility(View.VISIBLE);
//            mTxtEnrollLeague.setText(R.string.enroll_now_string);
//            mBtnJoinEnroll.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
//                }
//            });
//        } else {
//            mLayoutEnroll.setVisibility(View.GONE);
//        }
//        adapter = new RecyclerAdapter<PlayerDetail, PlayerDetailsViewHolder>(mPlayerDetails) {
//
//            @Override
//            public PlayerDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_league_player_details_list_item, null);
//                return new PlayerDetailsViewHolder(view);
//            }
//
//            @Override
//            public void onBindViewHolder(PlayerDetailsViewHolder holder, int position) {
//                holder.bindItem(getItem(position), mPlayerDetails.size());
//            }
//
//        };
//        mRecyclerView.setAdapter(adapter);
//    }
//
//    class PlayerDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        @BindView(R.id.card_view)
//        CardView CardViewContainer;
//
//        @BindView(R.id.txtPlayerName)
//        TextView PlayerNameTextView;
//
//        @BindView(R.id.txtWinLoss)
//        TextView WinLossTextView;
//
//        @BindView(R.id.txtGames)
//        TextView GamesTextView;
//
//        @BindView(R.id.imgContactMenu)
//        ImageView ContactImageView;
//
//        private PlayerDetail mPlayerDetail;
//        private int itemSelectedColor = ContextCompat.getColor(getActivity(), R.color.button_color_fade);
//        private int playerCount = 0;
//
//        public PlayerDetailsViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//
//            ContactImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (ContactImageView.getVisibility() == View.VISIBLE && !isMultiSelect) {
//                        PopupMenu popup = new PopupMenu(getActivity(), ContactImageView);
//                        MenuInflater inflater = popup.getMenuInflater();
//                        Menu popupMenu = popup.getMenu();
//                        inflater.inflate(R.menu.contact_popup_menu, popupMenu);
//
//                        popupMenu.findItem(R.id.action_contact_call).setVisible(mPlayerDetail.CanCall);
//                        popupMenu.findItem(R.id.action_contact_message).setVisible(mPlayerDetail.CanText);
//
//                        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
//                            @Override
//                            public void onDismiss(PopupMenu menu) {
//                                ContactImageView.setSelected(false);
//                            }
//                        });
//
//                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                switch (item.getItemId()) {
//                                    case R.id.action_contact_call: {
//                                        Intent intent = new Intent(Intent.ACTION_DIAL);
//                                        intent.setData(Uri.parse("tel:" + mPlayerDetail.Phone));
//                                        startActivity(Intent.createChooser(intent, "Call"));
//                                    }
//                                    return true;
//
//                                    case R.id.action_contact_mail: {
//                                        DlgSendMail.getDialogInstanceLeagues(mCurCompetition, FragLeagueDetails.mCurPlayer, mPlayerDetail).show(getChildFragmentManager(), "dlg-mail");
//                                        /*Intent intent = new Intent(Intent.ACTION_SEND);
//                                        intent.setType("message/rfc822");
//                                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {mPlayerDetail.Email});
//                                        intent.putExtra(Intent.EXTRA_SUBJECT, "Dummy Subject");
//                                        intent.putExtra(Intent.EXTRA_TEXT, "Dummy body.");
//                                        startActivity(Intent.createChooser(intent, "Send Email"));*/
//                                    }
//                                    return true;
//
//                                    case R.id.action_contact_message: {
//                                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mPlayerDetail.Phone));
//                                        //intent.setType("vnd.android-dir/mms-sms");
//                                        startActivity(Intent.createChooser(intent, "Send Message"));
//                                    }
//                                    return true;
//                                }
//
//                                return false;
//                            }
//                        });
//
//                        popup.show();
//                        ContactImageView.setSelected(true);
//                    }
//                }
//            });
//        }
//
//        public void bindItem(PlayerDetail playerDetail, int maxCount) {
//            playerCount = maxCount;
//            mPlayerDetail = playerDetail;
//            PlayerNameTextView.setText(mPlayerDetail.Name);
//            WinLossTextView.setText(mPlayerDetail.WinLoss);
//            GamesTextView.setText(mPlayerDetail.Games);
//
////                ContactImageView.setVisibility(mCanShowContact ? (mPlayerDetail.id == FragLeagueDetails.mCurPlayer.id || mPlayerDetail.IsRetired ? View.INVISIBLE : View.VISIBLE) : View.GONE);
//
//            if (mCanShowContact) {
//                ContactImageView.setVisibility(View.VISIBLE);
//                if (mPlayerDetail.id == FragLeagueDetails.mCurPlayer.id || mPlayerDetail.IsRetired) {
//
//                } else {
//                    itemView.setOnClickListener(this);
//                }
//            } else {
//                ContactImageView.setVisibility(View.GONE);
//            }
//            CardViewContainer.setForeground(getResources().getDrawable(mPlayerDetail.id == FragLeagueDetails.mCurPlayer.id ? R.color.yellow_item_highlight : android.R.color.transparent));
//
//            View itemBG = itemView.findViewById(R.id.itemBG);
//
//            if (itemBG != null) {
//                if (playerDetail.isSelected) {
//                    itemBG.setBackgroundColor(itemSelectedColor);
//                } else {
//                    itemBG.setBackgroundColor(Color.WHITE);
//                }
//            }
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (mPlayerDetail.id == FragLeagueDetails.mCurPlayer.id) {
//                return;
//            }
//
//            if (FragLeagueDetails.countSelection >= 5 && !mPlayerDetail.isSelected) {
//                final BaseDialog.SimpleDialog alert = BaseDialog.SimpleDialog.getDialogInstance("Max limit reached", 0, "Maximum 5 players can be selected.");
//                alert.setPositiveButton("Ok", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alert.dismiss();
//                    }
//                });
//                alert.show(getChildFragmentManager(), "select-alert");
//                return;
//            }
//
//            mPlayerDetail.isSelected = !mPlayerDetail.isSelected;
//
//            View itemBG = v.findViewById(R.id.itemBG);
//            if (mPlayerDetail.isSelected) {
//                FragLeagueDetails.countSelection += 1;
//                itemBG.setBackgroundColor(itemSelectedColor);
//            } else {
//                FragLeagueDetails.countSelection -= 1;
//                itemBG.setBackgroundColor(Color.WHITE);
//            }
//
//
//            if (FragLeagueDetails.countSelection == 1) {
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                Fragment frag = fm.findFragmentById(R.id.fragmentContainer);
//
//                if (frag instanceof FragLeagueDetails) {
//                    if (!((FragLeagueDetails) frag).isBottomviewVisible) {
//                        ((FragLeagueDetails) frag).showBottomView();
//                    }
//                }
//            }
//            if (FragLeagueDetails.countSelection == 0) {
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                Fragment frag = fm.findFragmentById(R.id.fragmentContainer);
//
//                if (frag instanceof FragLeagueDetails) {
//                    ((FragLeagueDetails) frag).hideBottomView();
//                }
//            }
//            FragmentManager fm = getActivity().getSupportFragmentManager();
//            Fragment frag = fm.findFragmentById(R.id.fragmentContainer);
//
//            if (frag instanceof FragLeagueDetails) {
//                ((FragLeagueDetails) frag).updatePlayers(mPlayerDetail);
//            }
//        }
//    }
//
//}