package com.tennisdc.tln;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kcode.bottomlib.BottomDialog;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.DialogsUtil;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.interfaces.OnDialogButtonClickListener;
import com.tennisdc.tln.model.AlertDetail;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tennisleaguenetwork.model.CourtRegion;
import com.tennisdc.tln.model.HomeItemModel;
import com.tennisdc.tennisleaguenetwork.model.Location;
import com.tennisdc.tln.model.OurPlayerDetailMode;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.model.PlayerInformationModel;
import com.tennisdc.tennisleaguenetwork.model.UncaughtException;
import com.tennisdc.tln.modules.AlertDialog.AlertsPagerAdapter;
import com.tennisdc.tln.modules.DlgCommunicationSettings;
import com.tennisdc.tln.modules.common.FragListSelection;
import com.tennisdc.tln.modules.courts.FragCourts;
import com.tennisdc.tln.modules.ladder.FragLadderDetails;
import com.tennisdc.tln.modules.latest_score.FragLastestScore;
import com.tennisdc.tln.modules.league.LeagueSwagActivity;
import com.tennisdc.tln.modules.league.MatchesPlayedActivity;
import com.tennisdc.tln.modules.myAccount.EditProfileScreen;
import com.tennisdc.tln.modules.myAccount.MyAccountScreen;
import com.tennisdc.tln.modules.partner.FragPartnerProgramDetails;
import com.tennisdc.tln.modules.poty.FragPlayersOfTheYearHome;
import com.tennisdc.tln.modules.profile.CardStackAdapter;
import com.tennisdc.tln.modules.profile.FragProfileNew;
import com.tennisdc.tln.modules.referral.FragReferralRewards;
import com.tennisdc.tln.modules.submit_score.FragSubmitScorePager;
import com.tennisdc.tln.modules.support.FragAppVersion;
import com.tennisdc.tln.modules.support.FragSupport;
import com.tennisdc.tln.modules.utr.UtrConfigurationScreen;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.services.ErrorReportingService;
import com.tennisdc.tln.services.SubmitPaymentService;
import com.tennisdc.tln.ui.EmptyRecyclerView;
import com.tennisdc.tln.ui.RecyclerAdapterHome;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.exceptions.RealmMigrationNeededException;

import static com.tennisdc.tln.modules.common.FragListSelection.EXTRA_TITLE;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class FragHome extends Fragment implements View.OnClickListener, CardStackListener {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String versionCodeString = "version_code";
    List<String> mCarouselUrl;
    View v;
    // get the bottom sheet view
//    LinearLayout llBottomSheet;
//    BottomSheetBehavior bottomSheetBehavior;
    //    Group carouFselContainer;
    FrameLayout carouselContainer;
    SharedPreferences sharedpreferences;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String mDomainName;
    private Realm mRealm;
    private Prefs mPrefs;
    private ViewPager viewPager;
    private CardStackView mCardStackView;
    private CardView mCVStackView;
    private View mViewStackView;
    private CarouselViewAdapter adapter;
    private ImageView[] mDots;
    private String currentLiveVersion = "";
    PlayerInformationModel.UserDataBean mUserData;
    JSONArray mCompititionArray = new JSONArray();
    private CardStackAdapter cardStackAdapter;
    private CardStackLayoutManager layoutManager;
    private int communityMemberPageCount = 1;
    private boolean showPartnerProgram = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            mRealm = Realm.getInstance(context);
        } catch (RealmMigrationNeededException e) {
            // Realm.deleteRealmFile(context);
            mRealm = Realm.getInstance(context);
        } catch (UnsatisfiedLinkError e) {
//            Realm.deleteRealmFile(context);
//            mRealm = Realm.getInstance(context);
        }
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.i("TAG", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("TAG", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("TAG", "printHashKey()", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPrefs = new Prefs(mContext);
        checkForFirstTimeLaunch();
        printHashKey(getActivity());
    }

    private void checkForFirstTimeLaunch() {
        if (mPrefs.getApp_runFirst() == null) {
            // That's mean First Time Launch
            mPrefs.setApp_runFirst("NO");
            DlgCommunicationSettings communicationSettings = DlgCommunicationSettings.getInstance();
            communicationSettings.show(getChildFragmentManager(), "communication-dlg");
        } else {
            final int versionCode = BuildConfig.VERSION_CODE;
            sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            int preferencesVersionCode = sharedpreferences.getInt(versionCodeString, 0);
            if (preferencesVersionCode == 0 || preferencesVersionCode < versionCode) {
                WSHandle.Login.logout(new Prefs.AppData(mContext).getOAuthToken(), new VolleyHelper.IRequestListener<String, String>() {
                    @Override
                    public void onFailureResponse(String response) {
                        Toast.makeText(getActivity(), "Error while logging out.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccessResponse(String response) {
                        SharedPreferences.Editor edit = sharedpreferences.edit();
                        edit.putInt(versionCodeString, versionCode);
                        edit.commit();
                        new Prefs.AppData(mContext).setOAuthToken(null);

                        if (mRealm != null) {
                            mRealm.beginTransaction();
                            mRealm.allObjects(Court.class).clear();
                            mRealm.allObjects(Location.class).clear();
                            mRealm.allObjects(CourtRegion.class).clear();
                            mRealm.commitTransaction();
                        }
                        startActivity(AuthActivity.getIntent(mContext));
                        getActivity().finish();

                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(mContext, "Network error while logging out.", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
        }
    }

    private void showAlert(List<AlertDetail> alertsModel) {

        final Dialog dialog = new Dialog(this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pager_layout);

        AlertsPagerAdapter mAdapter = new AlertsPagerAdapter(this.getContext(), alertsModel);
        ViewPager pager = (ViewPager) dialog.findViewById(R.id.viewPager);
        pager.setAdapter(mAdapter);

        ImageButton closeButton = (ImageButton) dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button dontShowButton = (Button) dialog.findViewById(R.id.dontShowButton);
        dontShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dontShowAlert();
                dialog.dismiss();
            }
        });

        LinearLayout dotsIndicator = (LinearLayout) dialog.findViewById(R.id.pager_dots);

        final ImageView[] ivArrayDotsPager = new ImageView[alertsModel.size()];
        for (int i = 0; i < ivArrayDotsPager.length; i++) {
            ivArrayDotsPager[i] = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            ivArrayDotsPager[i].setLayoutParams(params);
            ivArrayDotsPager[i].setImageResource(R.drawable.page_indicator_unselected);
            ivArrayDotsPager[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setAlpha(1);
                }
            });
            dotsIndicator.addView(ivArrayDotsPager[i]);
            dotsIndicator.bringToFront();
        }

        ivArrayDotsPager[0].setImageResource(R.drawable.page_indicator_selected);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                for (int i = 0; i < ivArrayDotsPager.length; i++) {
                    ivArrayDotsPager[i].setImageResource(R.drawable.page_indicator_unselected);
                }
                ivArrayDotsPager[position].setImageResource(R.drawable.page_indicator_selected);
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < ivArrayDotsPager.length; i++) {
                    ivArrayDotsPager[i].setImageResource(R.drawable.page_indicator_unselected);
                }
                ivArrayDotsPager[position].setImageResource(R.drawable.page_indicator_selected);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        dialog.show();
    }

    RecyclerAdapterHome<HomeItemModel, HomeItemViewHolder> mHomeItemAdapters;
    EmptyRecyclerView mRVHome;
    Prefs.AppData prefs;
    BroadcastReceiver mUpdateNotificationBadge = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (mTxtBagdeHome != null) {
////                if (prefs.getNotificationCount() > 0) {
////                    mTxtBagdeHome.setVisibility(View.VISIBLE);
////                    mTxtBagdeHome.setText(String.valueOf(prefs.getNotificationCount()));
////                } else {
////                    mTxtBagdeHome.setVisibility(View.GONE);
////                }
////            }

            if (mHomeItemAdapters != null) {
                mHomeItemAdapters.updateBadgeCount(String.valueOf(prefs.getNotificationCount()));
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mUpdateNotificationBadge);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        App.LogFacebookEvent(getActivity(), this.getClass().getName());
        prefs = new Prefs.AppData(mContext);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateNotificationBadge, new IntentFilter(Constants.UPDATE_NOTIFICATION_COUNT));

        if (prefs.getPlayer() != null || !prefs.getPlayer().status.equalsIgnoreCase("Banned")) {
            v = inflater.inflate(R.layout.frag_home, container, false);
            TextView welcome_player = (TextView) v.findViewById(R.id.welcome_player_name);
            TextView tvAppVersion = v.findViewById(R.id.tvAppVersion);
//            mTxtBagdeHome = v.findViewById(R.id.mTxtBagdeHome);
            mRVHome = v.findViewById(R.id.mRVHome);
            mCardStackView = v.findViewById(R.id.card_stack_view);
            mCVStackView = v.findViewById(R.id.card_view_stack_view);
            mViewStackView = v.findViewById(R.id.view_card_stack_view);

            mViewStackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCVStackView.setVisibility(View.GONE);
                }
            });
//            mTxtBagdeHome = v.findViewById(R.id.mTxtBagdeHome);

            mRVHome.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//            llBottomSheet = (LinearLayout) v.findViewById(R.id.bottom_sheet);
//
//            // init the bottom sheet behavior
//            bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
//            // change the state of the bottom sheet
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//            // set callback for changes
//            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//                @Override
//                public void onStateChanged(@NonNull View bottomSheet, int newState) {
//
//                }
//
//                @Override
//                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//                }
//            });


            tvAppVersion.setText("v " + BuildConfig.VERSION_NAME);
            tvAppVersion.setVisibility(View.GONE);
            StringBuilder playerNameBuilder = new StringBuilder();
            playerNameBuilder.append("Welcome ");
            playerNameBuilder.append(prefs.getPlayer().firstName);
            playerNameBuilder.append("!");
            welcome_player.setText(playerNameBuilder.toString());

            ImageView img_referral = (ImageView) v.findViewById(R.id.img_referral);
            img_referral.setOnClickListener(this);
            mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...");
            mProgressDialog.dismiss();
            ViewFlipper viewFlipper = (ViewFlipper) v.findViewById(R.id.viewFlipper);
            mDomainName = new Prefs.AppData(mContext).getDomainName();
            if (TextUtils.equals(mDomainName, App.sDefaultDomain)) {
                viewFlipper.setDisplayedChild(1);
            } else {
                viewFlipper.setDisplayedChild(0);
//                mImgMyProfile = v.findViewById(R.id.mImgMyProfile);
//                v.findViewById(R.id.btnSubmitScore).setOnClickListener(this);
//                v.findViewById(R.id.btnLatestScore).setOnClickListener(this);
                v.findViewById(R.id.btnLeagues).setOnClickListener(this);
                v.findViewById(R.id.btnPartnerPrograms).setOnClickListener(this);

                Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

                mUserData = gson.fromJson(prefs.getUserData(), new TypeToken<PlayerInformationModel.UserDataBean>() {
                }.getType());
//                if (mUserData != null && mUserData.getUser_image() != null) {
//                    Glide.with(getActivity()).load(mUserData.getUser_image()).placeholder(R.drawable.ic_dummy_user).into(mImgMyProfile);
//                } else {
//                    mImgMyProfile.setImageResource(R.drawable.ic_dummy_user);
//                }

                if (!prefs.getNoLadder()) {
                    v.findViewById(R.id.btnTennisLadder).animate().alpha(1.0f);
                    v.findViewById(R.id.btnTennisLadder).setOnClickListener(this);
                } else {
                    v.findViewById(R.id.btnTennisLadder).animate().alpha(0.5f);
                }

                if (prefs.getLadderIconStatus()) {
                    v.findViewById(R.id.btnTennisLadder).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.viewTennisLadder).setVisibility(View.VISIBLE);
                } else {
                    v.findViewById(R.id.btnTennisLadder).setVisibility(View.GONE);
                    v.findViewById(R.id.viewTennisLadder).setVisibility(View.GONE);
                }
//                v.findViewById(R.id.btnCourts).setOnClickListener(this);
                v.findViewById(R.id.btnTournaments).setOnClickListener(this);
//                v.findViewById(R.id.btnBuy).setOnClickListener(this);
//                v.findViewById(R.id.btnCustomerSupport).setOnClickListener(this);
//                v.findViewById(R.id.btnUpcomingPrograms).setOnClickListener(this);
//                v.findViewById(R.id.btnNationals).setOnClickListener(this);
//                v.findViewById(R.id.btnPOTY).setOnClickListener(this);
//                v.findViewById(R.id.btnMyProfile).setOnClickListener(this);
            }
//            prefs.setNotificationCount(prefs.getNotificationCount() + 1);
            setupHomeItems();
            getPlayerList();


        } else {
            v = inflater.inflate(R.layout.frag_home_banned, container, false);
            TextView welcome_player = (TextView) v.findViewById(R.id.welcome_player_name);
            StringBuilder playerNameBuilder = new StringBuilder();
            playerNameBuilder.append("Welcome ");
            playerNameBuilder.append(prefs.getPlayer().firstName);
            playerNameBuilder.append("!");
            welcome_player.setText(playerNameBuilder.toString());
            mDomainName = new Prefs.AppData(mContext).getDomainName();
            v.findViewById(R.id.btnCustomerSupport).setOnClickListener(this);
        }

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Prefs.AppData appData = new Prefs.AppData(getActivity());
//        Competition competition = null;
//        Toast.makeText(getActivity(),competition.getCompetitionName(),Toast.LENGTH_SHORT);

        if (!appData.getPlayer().status.equalsIgnoreCase("Banned")) {


            if (new Prefs.AppData(getContext()).getPlayer().mobilePopupVisible) {
                fetchAlertList();
            }

//			if (appData.getUpdatePlayer()) {
//				final ProgressDialog progressDialog = ProgressDialog.show(mContext, null, "Please wait...");
//				WSHandle.Login.getUserInfo(new VolleyHelper.IRequestListener<PlayerDetail, String>() {
//					@Override
//					public void onFailureResponse(String response) {
//						progressDialog.dismiss();
//						Toast.makeText(getActivity(), "Server Error:" + response, Toast.LENGTH_LONG).show();
//					}
//
//					@Override
//					public void onSuccessResponse(PlayerDetail response) {
//						appData.setPlayer(response);
//						appData.setUpdatePlayer(false);
//
//					}
//
//					@Override
//					public void onError(VolleyError error) {
//						progressDialog.dismiss();
//						Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
//					}
//				});
//			}
            if (mRealm != null) {
                if (mRealm.allObjects(Court.class).size() == 0) {
                    final ProgressDialog progressDialog = ProgressDialog.show(mContext, null, "Please wait...");
                    WSHandle.Courts.getCourtsHome(new VolleyHelper.IRequestListener<List<Court>, String>() {
                        @Override
                        public void onFailureResponse(String response) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccessResponse(List<Court> response) {
                            //progressDialog.dismiss();
                            //Realm realm = Realm.getInstance(mContext);
                            if (mRealm != null) {
                                mRealm.beginTransaction();
                                try {
                                    /* delete existing courts */
                                    mRealm.allObjects(Court.class).clear();

                                    /* add new courts */
                                    mRealm.copyToRealm(response);

                                    mRealm.commitTransaction();

                                    if (mRealm.allObjects(Location.class).size() == 0) {
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

                                                try {
                                                    mRealm.beginTransaction();

                                                    /* delete existing Locations and Court regions */
                                                    mRealm.allObjects(CourtRegion.class).clear();
                                                    mRealm.allObjects(Location.class).clear();

                                                    /* add new Locations and Court regions */
                                                    mRealm.copyToRealm(response);

                                                    mRealm.commitTransaction();
                                                } catch (Exception e) {
                                                    mRealm.cancelTransaction();
                                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onError(VolleyError error) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                                            }

                                        });
                                    } else progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    mRealm.cancelTransaction();
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }

        /*sendNotification("test");*/

        if (getArguments() != null && getArguments().containsKey("notification")) {
            viewLatestScroce();
        }
    }

    private void setupHomeItems() {
        try {
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Fetching Championships...");
            WSHandle.Settings.getChampionShip(new VolleyHelper.IRequestListener<JSONObject, String>() {
                @Override
                public void onFailureResponse(String response) {
//                Log.e("response->", response);
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
                }

                @Override
                public void onSuccessResponse(JSONObject response) {
//                Log.e("response->", response.toString());
//                progressDialog.dismiss();
                    try {
                        mCompititionArray = response.getJSONArray("competitions");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {
//                Log.e("response->", error.getMessage());
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
                }
            });
            HomeItemModel mItemMyProfile = null;
            if (mUserData != null && mUserData.getUser_image() != null) {
                try {
                    mItemMyProfile = new HomeItemModel(4, "", getActivity().getString(R.string.text_my_profile), mUserData.getUser_image(), 0);
                } catch (Exception e) {
                    mItemMyProfile = new HomeItemModel(4, "", getActivity().getString(R.string.text_my_profile), "", 0);
                }
            } else {
                mItemMyProfile = new HomeItemModel(4, "", getActivity().getString(R.string.text_my_profile), "", 0);
            }

            ArrayList<HomeItemModel> mHomeItemList = new ArrayList<>();
            String mUtrRating = "";
            if (mUserData == null || mUserData.getUtr_rating() == null) {
            } else {
                mUtrRating = mUserData.getUtr_rating();
                mUtrRating = mUtrRating.split("-")[0];
            }

//        mUtrRating = "2.00";

            HomeItemModel mItemSubmitScore = new HomeItemModel(0, "", getActivity().getString(R.string.submit_score), "", R.drawable.ic_submitscore);
            HomeItemModel mItemCourt = new HomeItemModel(1, "", getActivity().getString(R.string.text_courts), "", R.drawable.ic_courts);
            HomeItemModel mItemJointToday = new HomeItemModel(2, "", getActivity().getString(R.string.text_join_today), "", R.drawable.ic_buy);
            HomeItemModel mItemUpcomingProgram = new HomeItemModel(3, "", getActivity().getString(R.string.text_upcoming_programs), "", R.drawable.ic_upcoming_programs);
            HomeItemModel mItemLatestScore = new HomeItemModel(5, String.valueOf(prefs.getNotificationCount()), getActivity().getString(R.string.text_latest_score), "", R.drawable.ic_latestscore_icon);
            HomeItemModel mItemPlayerofYear = new HomeItemModel(6, "", getActivity().getString(R.string.text_player_of_the_year_poty), "", R.drawable.ic_players_of_the_year);
            HomeItemModel mItemUTR = new HomeItemModel(8, mUtrRating, getActivity().getString(R.string.text_utr), "", R.drawable.ic_utr_icon);
            HomeItemModel mItemNationals = new HomeItemModel(7, "", getActivity().getString(R.string.nationals_championship), "", R.drawable.ic_nationals);
            HomeItemModel mItemTennisLesson = new HomeItemModel(10, "", getActivity().getString(R.string.new_tennis_lesson), "", R.drawable.ic_lesson);
            HomeItemModel mItemCustomerSupport = new HomeItemModel(9, "", getActivity().getString(R.string.text_customer_support), "", R.drawable.ic_customer_support);
            HomeItemModel mItemReferFriend = new HomeItemModel(11, "", getActivity().getString(R.string.refer_friend), "", R.drawable.ic_refer_friend);
            HomeItemModel mItemMatches = new HomeItemModel(12, "", getActivity().getString(R.string.monthly_contest), "", R.drawable.ic_monthly_contest);
            HomeItemModel mItemCommunityMembers = new HomeItemModel(13, "", getActivity().getString(R.string.comminity_members), "", R.drawable.ic_community_member);
            HomeItemModel mItemLeagueSwag = new HomeItemModel(14, "", getActivity().getString(R.string.swag), "", R.drawable.ic_buy);
//            HomeItemModel mItemSwag = new HomeItemModel(15, "", getActivity().getString(R.string.swag_items), "", R.drawable.ic_buy);

            /* 7 days check to show new badge on maches */
            if (prefs.getDateMatchesPlayed().trim().isEmpty()) {
                prefs.setDateMatchesPlayed(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            }
            SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
            Date mDate = new Date();
            mDate.setTime(Long.valueOf(prefs.getDateMatchesPlayed()));
            try {
                Date date1 = myFormat.parse(myFormat.format(mDate));
                Date date2 = myFormat.parse(myFormat.format(Calendar.getInstance().getTime()));
                long diff = date2.getTime() - date1.getTime();
                long mDaysLeft = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                if (mDaysLeft < 7) {
                    mItemMatches.setmBadgeCount("new");
                } else {
                    mItemMatches.setmBadgeCount("");
                }
                System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mHomeItemList.add(mItemSubmitScore);
            mHomeItemList.add(mItemJointToday);
            mHomeItemList.add(mItemCourt);
            mHomeItemList.add(mItemUpcomingProgram);
            mHomeItemList.add(mItemMyProfile);
            mHomeItemList.add(mItemCommunityMembers);
            mHomeItemList.add(mItemLatestScore);
            mHomeItemList.add(mItemPlayerofYear);
            mHomeItemList.add(mItemReferFriend);
            mHomeItemList.add(mItemTennisLesson);
            mHomeItemList.add(mItemNationals);
            mHomeItemList.add(mItemCustomerSupport);
            mHomeItemList.add(mItemLeagueSwag);
            /*mHomeItemList.add(mItemSwag);*/
            if (prefs.getURTActive())
                mHomeItemList.add(mItemUTR);
            if (prefs.getMonthlyContestShow())
                mHomeItemList.add(mItemMatches);

            mHomeItemAdapters = new RecyclerAdapterHome<HomeItemModel, HomeItemViewHolder>(mHomeItemList) {

                @Override
                public void updateBadgeCount(String mBadgeCount) {
                    mHomeItemList.get(5).setmBadgeCount(mBadgeCount);
                    notifyDataSetChanged();
                }

                @Override
                public HomeItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_row_home, null);
                    return new HomeItemViewHolder(view);
                }

                @Override
                public void onBindViewHolder(HomeItemViewHolder holder, int position) {
                    holder.bindItem(getItem(position), mHomeItemList.size());
                }

            };
            mRVHome.setAdapter(mHomeItemAdapters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (layoutManager.getTopPosition() == cardStackAdapter.getItemCount()) {
            mCVStackView.setVisibility(View.GONE);
            mProgressDialog.show();
            getPlayerList();
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    class HomeItemViewHolder extends RecyclerView.ViewHolder {

        //        @BindView(R.id.mLayoutMainHomeItem)
        LinearLayout mLayoutMainHomeItem;

        //        @BindView(R.id.mTxtHomeItem)
        TextView mTxtHomeItem;

        //        @BindView(R.id.mImgHomeItem)
        ImageView mImgHomeItem;

        //        @BindView(R.id.mImgCircleHomeItem)
        CircleImageView mImgCircleHomeItem;

        //        @BindView(R.id.mTxtBagdeHome)
        TextView mTxtBagdeHome;

        private int itemSelectedColor = ContextCompat.getColor(getActivity(), R.color.button_color_fade);
        private int qualifiedItemSelectedColor = ContextCompat.getColor(getActivity(), R.color.button_color_fade_more);

        public HomeItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mLayoutMainHomeItem = itemView.findViewById(R.id.mLayoutMainHomeItem);
            mTxtHomeItem = itemView.findViewById(R.id.mTxtHomeItem);
            mImgHomeItem = itemView.findViewById(R.id.mImgHomeItem);
            mImgCircleHomeItem = itemView.findViewById(R.id.mImgCircleHomeItem);
            mTxtBagdeHome = itemView.findViewById(R.id.mTxtBagdeHome);
//            mLayoutMainHomeItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }

        public void bindItem(HomeItemModel mHomeItemDetail, int maxCount) {
            /*if (mHomeItemDetail.getmID() == 12) {
                mTxtBagdeHome.setVisibility(View.VISIBLE);
                mTxtBagdeHome.setBackgroundResource(R.drawable.bg_white_rounded_red_corners);
                mTxtBagdeHome.setText("new");
            } else {*/
            if (!mHomeItemDetail.getmBadgeCount().trim().isEmpty()) {
                if (StringUtil.isNumeric(mHomeItemDetail.getmBadgeCount())) {
                    if (Integer.valueOf(mHomeItemDetail.getmBadgeCount()) > 0) {
                        mTxtBagdeHome.setVisibility(View.VISIBLE);
                        mTxtBagdeHome.setBackgroundResource(R.drawable.bg_badge_grey);
                        mTxtBagdeHome.setBackgroundResource(R.drawable.bg_badge);
                        mTxtBagdeHome.setTextColor(getActivity().getResources().getColor(R.color.black));
                        mTxtBagdeHome.setText(String.valueOf(mHomeItemDetail.getmBadgeCount().trim()));
                    } else {
                        mTxtBagdeHome.setVisibility(View.GONE);
                    }
                } else {
                    mTxtBagdeHome.setVisibility(View.VISIBLE);
                    mTxtBagdeHome.setBackgroundResource(R.drawable.bg_badge_grey);
                    mTxtBagdeHome.setBackgroundResource(R.drawable.bg_badge);
                    mTxtBagdeHome.setTextColor(getActivity().getResources().getColor(R.color.black));
                    //        mTxtBagdeHome.setText(mHomeItemDetail.getmBadgeCount().trim());
                    if (mHomeItemDetail.getmBadgeCount().equals("new")) {
                        mTxtBagdeHome.setBackgroundResource(R.drawable.bg_white_rounded_red_corners);
                        mTxtBagdeHome.setTextColor(getActivity().getResources().getColor(R.color.red));
                        mTxtBagdeHome.setText(mHomeItemDetail.getmBadgeCount());
                    }
                }
            } else {
                mTxtBagdeHome.setVisibility(View.GONE);
            }
//            }
            mTxtHomeItem.setText(mHomeItemDetail.getmTitle());
            if (mHomeItemDetail.getmImage() != 0) {
                mImgCircleHomeItem.setVisibility(View.GONE);
                mImgHomeItem.setVisibility(View.VISIBLE);
                mImgHomeItem.setImageResource(mHomeItemDetail.getmImage());
            } else {
                if (mHomeItemDetail.getmImageUrl() != null && mHomeItemDetail.getmImageUrl() != null) {
                    mImgCircleHomeItem.setVisibility(View.VISIBLE);
                    mImgHomeItem.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(mHomeItemDetail.getmImageUrl()).placeholder(R.drawable.ic_dummy_user).into(mImgCircleHomeItem);
                } else {
                    mImgCircleHomeItem.setVisibility(View.GONE);
                    mImgHomeItem.setVisibility(View.VISIBLE);
                    mImgHomeItem.setImageResource(R.drawable.ic_dummy_user);
                }
            }

            mLayoutMainHomeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Class fragClass = null;
                    switch (mHomeItemDetail.getmID()) {
                        case 0:
                            fragClass = FragSubmitScorePager.FragSelectCompetition.class;
                            break;
                        case 1:
                            fragClass = FragCourts.class;
                            break;
                        case 2:
                            startActivity(SingleFragmentActivity.BuyActivity.getIntent(mContext));
                            return;
                        case 3:
                            fragClass = FragAlert.class;
//                    startActivity(SingleFragmentActivity.getIntent(mContext, fragClass, null));
                            break;
                        case 4:
                            startActivity(new Intent(mContext, MyAccountScreen.class));
                            return;
                        case 5:
                            fragClass = FragLastestScore.class;
                            break;
                        case 6:
                            startActivity(SingleFragmentActivity.getIntent(mContext, FragPlayersOfTheYearHome.class, null));
                            return;
                        case 7:
                            String[] mOptionList = new String[mCompititionArray.length()];
                            for (int i = 0; i < mCompititionArray.length(); i++) {
                                try {
                                    mOptionList[i] = mCompititionArray.getJSONObject(i).getString("name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            BottomDialog dialog = BottomDialog.newInstance("", getActivity().getString(R.string.btn_cancel), mOptionList);
                            dialog.show(getChildFragmentManager(), "dialog");
                            //add item click listener
                            dialog.setListener(new BottomDialog.OnClickListener() {
                                @Override
                                public void click(int position) {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    Prefs.AppData prefs = new Prefs.AppData(mContext);

                                    try {
                                        i.setData(Uri.parse(mCompititionArray.getJSONObject(position).getString("link")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
//                                            "https://"/*www.tennisnortheast.com*/ + prefs.getDomainName() + "/info/2019_EOY_MIAMI_tourney"));
                                    startActivity(i);
                                }
                            });
                            return;
                        case 8:
                            startActivity(new Intent(mContext, UtrConfigurationScreen.class));
                            return;
                        case 9:
                            fragClass = FragSupport.class;
                            break;
                        case 10:
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            Prefs.AppData prefs = new Prefs.AppData(mContext);

                            try {
//                                i.setData(Uri.parse("https://" + prefs.getDomainName()/*www.tennisnortheast.com*/ + "/Boston-Tennis-Lessons"));
                                i.setData(Uri.parse(prefs.getTennisLessonUrl()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                                            "https://"/*www.tennisnortheast.com*/ + prefs.getDomainName() + "/info/2019_EOY_MIAMI_tourney"));
                            startActivity(i);
                            return;
                        case 11:
                            startActivity(SingleFragmentActivity.getIntent(mContext, FragReferralRewards.class, null));
                            return;
                        case 12:
                            startActivity(new Intent(mContext, MatchesPlayedActivity.class));
                            return;
                        case 13:
                            if (mCommunityPlayersList.size() > 0) {
                                mCVStackView.setVisibility(View.VISIBLE);
                                layoutManager = new CardStackLayoutManager(getActivity(), FragHome.this);
                                mCardStackView.setLayoutManager(layoutManager);
                                cardStackAdapter = new CardStackAdapter(getActivity(), mCommunityPlayersList);
                                mCardStackView.setAdapter(cardStackAdapter);
                                mCardStackView.scrollToPosition(0);
                            } else {
                                mCVStackView.setVisibility(View.GONE);
                                Toast.makeText(mContext, "No members found!", Toast.LENGTH_LONG).show();
                            }
                            return;
                        case 14:
                            startActivity(new Intent(mContext, LeagueSwagActivity.class));
                            return;
                        /*case 15:
                            startActivity(SingleFragmentActivity.SwagItemsActivity.getIntent(mContext));
                            return;*/
                        default:
                            break;
                    }
                    startActivity(SingleFragmentActivity.getIntent(mContext, fragClass, null));

                }
            });
        }

    }

    private void setUpViewPager(ArrayList<String> topBanner) {
        if (topBanner.size() > 0) {
            viewPager = (ViewPager) v.findViewById(R.id.view_pager_carousel);
            carouselContainer = v.findViewById(R.id.carousel_container);
            carouselContainer.setVisibility(View.VISIBLE);
            mCarouselUrl = new ArrayList<>();
            for (int i = 0; i < topBanner.size(); i++) {
                mCarouselUrl.add(topBanner.get(i));
            }


            //view.findViewById(R.id.carousel_progress_indicator).setVisibility(View.VISIBLE);
            final LinearLayout dotLayout = (LinearLayout) v.findViewById(R.id.viewPagerDots);
            if (topBanner.size() > 1) {
                setUiPageViewController(dotLayout, mCarouselUrl);
                dotLayout.setVisibility(View.VISIBLE);
            } else {
                dotLayout.setVisibility(View.INVISIBLE);
            }
            adapter = new CarouselViewAdapter(getActivity().getSupportFragmentManager(), mCarouselUrl);
            viewPager.setAdapter(adapter);
            //viewPager.setOffscreenPageLimit(3);
            viewPager.setCurrentItem(0);
            viewPager.setClipToPadding(false);
            // set padding manually, the more you set the padding the more you see of prev & next page
            /*viewPager.setPadding((int)getResources().getDimension(R.dimen.ad_carousel_padding), 0,
                    (int)getResources().getDimension(R.dimen.ad_carousel_padding), 0);*/
            // sets a margin b/w individual pages to ensure that there is a gap b/w them
            viewPager.setPageMargin((int) getResources().getDimension(R.dimen.ad_carousel_margin));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                int oldPosition = 0;

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (dotLayout.getVisibility() == View.VISIBLE) {
                        mDots[position].setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                R.drawable.black_view_pager_indicator_selected));
                        mDots[oldPosition].setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                R.drawable.white_non_selected_view_pager_indicator));
                        oldPosition = position;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
            carouselContainer = v.findViewById(R.id.carousel_container);
            carouselContainer.setVisibility(View.GONE);
        }
    }

    /**
     * @param viewPagerDots this will set up the page indicator circles
     * @param mCarouselUrl
     */
    private void setUiPageViewController(LinearLayout viewPagerDots, List<String> mCarouselUrl) {

        if (!mCarouselUrl.isEmpty() && mCarouselUrl.size() > 0) {
            mDots = new ImageView[mCarouselUrl.size()];
            for (int i = 0; i < mDots.length; i++) {
                mDots[i] = new ImageView(getActivity());
                mDots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),
                        R.drawable.white_non_selected_view_pager_indicator));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(3, 0, 3, 0);
                viewPagerDots.addView(mDots[i], params);
            }
            mDots[0].setImageDrawable(ContextCompat.getDrawable(getActivity(),
                    R.drawable.black_view_pager_indicator_selected));
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        Prefs.AppData prefs = new Prefs.AppData(mContext);
        if (!prefs.getPlayer().status.equalsIgnoreCase("Banned")) {
            if (prefs.getTopBanner() != null)
                setUpViewPager(prefs.getTopBanner());
        }
        if (!TextUtils.isEmpty(mDomainName))
            /*findTextViewTitle("  " + mDomainName);*/ getActivity().setTitle("  " + mDomainName);
        else
            /* findTextViewTitle("  " + mDomainName);*/getActivity().setTitle("  " + mDomainName);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.ic_action);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // get logged error count
        try {
            Realm realm = Realm.getInstance(getActivity());
            int exceptionCount = realm.allObjects(UncaughtException.class).size();
            realm.close();

            if (exceptionCount > 0) {
                getActivity().startService(new Intent(getActivity(), ErrorReportingService.class));
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        try {
            if (mRealm == null)
                mRealm = Realm.getInstance(getActivity());
//        //test tannu
            // getActivity().startService(new Intent(getActivity(), SubmitPaymentService.class));
        } catch (RealmMigrationNeededException e) {
            //  Realm.deleteRealmFile(getActivity());
            mRealm = Realm.getInstance(getActivity());
//        //test tannu
            // getActivity().startService(new Intent(getActivity(), SubmitPaymentService.class));
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        getActivity().startService(new Intent(getActivity(), SubmitPaymentService.class));
        // Calling Update Api of Player
        updatePlayerInfo();
        getPlayerDetails();
    }

    @Override
    public void onDetach() {
        mContext = null;
        mRealm.close();
        mRealm = null;
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Prefs.AppData prefs = new Prefs.AppData(mContext);
        if (prefs.getPlayer().status.equalsIgnoreCase("Banned")) {
            inflater.inflate(R.menu.banned_home, menu);
        } else {
            inflater.inflate(R.menu.opt_home, menu);
            int positionOfMenuItem = 8; // or whatever...
            MenuItem item = menu.getItem(positionOfMenuItem);
            SpannableString s = new SpannableString("Delete My Data");
            s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
            item.setTitle(s);
        }
        PlayerDetail curPlayerDetail = new Prefs.AppData(mContext).getPlayer();
        menu.findItem(R.id.action_profile).setTitle("Hi " + curPlayerDetail.Name);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        if (prefs.getURTActive()) {
//            menu.findItem(R.id.action_utr).setVisible(true);
//            menu.findItem(R.id.action_utr).setTitle(prefs.getUtrLabel());
//        } else {
//            menu.findItem(R.id.action_utr).setVisible(false);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
          /*  case R.id.action_my_account:
                startActivity(new Intent(mContext, MyAccountScreen.class));
                return true;*/
//            case R.id.action_utr:
//                startActivity(new Intent(mContext, UtrConfigurationScreen.class));
//                return true;
            case R.id.action_communication_setting:
                DlgCommunicationSettings communicationSettings = DlgCommunicationSettings.getInstance();
                communicationSettings.show(getChildFragmentManager(), "communication-dlg");
                return true;

            case R.id.action_my_profile:
                startActivity(SingleFragmentActivity.getIntent(mContext, FragProfileNew.class, null));
                return true;

            case R.id.action_rate_us:
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;

            case R.id.action_refer_a_friend:
                startActivity(SingleFragmentActivity.getIntent(mContext, FragReferralRewards.class, null));
                return true;

            case R.id.action_league_rules:
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://"/*www.tennisnortheast.com*/ + prefs.getDomainName() + "/info/rules")));
                return true;

            case R.id.action_contact_us:
                startActivity(SingleFragmentActivity.getIntent(mContext, FragSupport.class, null));
                return true;

            case R.id.action_app_version:
                Bundle bundle = new Bundle();
                bundle.putString(FragAppVersion.BUNDLE_EXTRAS_CURRENT_LIVE_VERSION, currentLiveVersion);
                startActivity(SingleFragmentActivity.getIntent(mContext, FragAppVersion.class, bundle));
                return true;

            case R.id.action_delete_data:
                openDeleteDataPopup();
                return true;

            case R.id.action_logout:
                //new LogoutAsyncTask().execute();
                WSHandle.Login.logout(new Prefs.AppData(mContext).getOAuthToken(), new VolleyHelper.IRequestListener<String, String>() {
                    @Override
                    public void onFailureResponse(String response) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error while logging out.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccessResponse(String response) {
                        new Prefs.AppData(mContext).setOAuthToken(null);
                        new Prefs.AppData(mContext).setGoogleAnalyticsId(null);

                        if (mRealm != null) {
                            mRealm.beginTransaction();
                            mRealm.allObjects(Court.class).clear();
                            mRealm.allObjects(Location.class).clear();
                            mRealm.allObjects(CourtRegion.class).clear();
                            mRealm.commitTransaction();
                        }
                        startActivity(AuthActivity.getIntent(mContext));
                        getActivity().finish();

                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(mContext, "Network error while logging out.", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDeleteDataPopup() {
        new DialogsUtil().openAlertDialog(getActivity(), "I would like to confirm I would like my Data deleted from the organization.\n" +
                        "An email will be sent to the staff to delete your profile.",
                "Yes", "No", new OnDialogButtonClickListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        deleteMyData();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }
                });
    }

    private void deleteMyData() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
        WSHandle.Profile.deleteMyData(new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccessResponse(String message) {
                progressDialog.dismiss();
                new DialogsUtil().openAlertDialog(getActivity(), message, "Ok", "",
                        new OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveButtonClicked() {}

                            @Override
                            public void onNegativeButtonClicked() {}
                        });
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
            }
        });
    }

    private String getCurrentAppVersion() {
        String currentAppVersion = "";
        try {
            PackageInfo pInfo = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0);
            currentAppVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return currentAppVersion;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Class fragClass = null;
        switch (v.getId()) {
//            case R.id.btnSubmitScore:
//                fragClass = FragSubmitScorePager.FragSelectCompetition.class;
//                break;

//            case R.id.btnLatestScore:
//                fragClass = FragLastestScore.class;
//                break;

            case R.id.btnLeagues:
//                fragClass = FragLeagueDetails.class;
                fragClass = FragListSelection.class;
                Bundle data = new Bundle();
                data.putString(EXTRA_TITLE, "League Report");
                startActivity(SingleFragmentActivity.getIntent(mContext, fragClass, data));
                return;

            case R.id.btnPartnerPrograms:
                if (showPartnerProgram)
                    fragClass = FragPartnerProgramDetails.class;
                break;

            case R.id.btnTennisLadder:
                fragClass = FragLadderDetails.class;
                break;

//            case R.id.btnCourts:
//                fragClass = FragCourts.class;
//                break;

            case R.id.btnTournaments:
//                fragClass = FragTournamentAll.class;
                fragClass = FragListSelection.class;
                Bundle data1 = new Bundle();
                data1.putString(EXTRA_TITLE, "My Tourney's");
                startActivity(SingleFragmentActivity.getIntent(mContext, fragClass, data1));
                return;

//            case R.id.btnCustomerSupport:
//                fragClass = FragSupport.class;
//                break;

//            case R.id.btnBuy:
//                startActivity(SingleFragmentActivity.BuyActivity.getIntent(mContext));
//                return;

//            case R.id.btnUpcomingPrograms:
//                fragClass = FragAlert.class;
//                startActivity(SingleFragmentActivity.getIntent(mContext, fragClass, null));
//                return;

//            case R.id.btnNationals:
//                BottomDialog dialog = BottomDialog.newInstance("", getString(R.string.btn_cancel), new String[]{
//                        "National Championship"});
///**
// *
// * BottomDialog dialog = BottomDialog.newInstance("titleText","cancelText",new String[]{"item1","item2"});
// *
// * use public static BottomDialog newInstance(String titleText,String cancelText, String[] items)
// * set cancel text
// */
//                dialog.show(getChildFragmentManager(), "dialog");
//                //add item click listener
//                dialog.setListener(new BottomDialog.OnClickListener() {
//                    @Override
//                    public void click(int position) {
//                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        Prefs.AppData prefs = new Prefs.AppData(mContext);
//
//                        i.setData(Uri.parse("https://"/*www.tennisnortheast.com*/ + prefs.getDomainName() + "/info/2019_EOY_MIAMI_tourney"));
//                        startActivity(i);
//                    }
//                });
//                return;

//            case R.id.btnPOTY:
//                startActivity(SingleFragmentActivity.getIntent(mContext, FragPlayersOfTheYearHome.class, null));
//                return;

//            case R.id.btnMyProfile:
//                startActivity(new Intent(mContext, MyAccountScreen.class));
//                return;
        }
        if (fragClass != null)
            startActivity(SingleFragmentActivity.getIntent(mContext, fragClass, null));
    }

    private void fetchAlertList() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Please wait...", true, false);

        Request divisionReportRequest = WSHandle.Alerts.getDetailsRequest(App.sOAuth, new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                try {
                    progressDialog.dismiss();
                } catch (IllegalArgumentException e) {

                }
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();

                    Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

                    List<AlertDetail> mAlertDetails = gson.fromJson(response.getString("slider_images"), new TypeToken<List<AlertDetail>>() {
                    }.getType());

                    showAlert(mAlertDetails);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
        VolleyHelper.getInstance(getActivity()).addToRequestQueue(divisionReportRequest);
    }

    private void dontShowAlert() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        Request divisionReportRequest = WSHandle.Alerts.updateToNotShow(App.sOAuth, true, new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    PlayerDetail player = new Prefs.AppData(getContext()).getPlayer();
                    player.mobilePopupVisible = false;
                    new Prefs.AppData(getContext()).setPlayer(player);
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
        VolleyHelper.getInstance(getActivity()).addToRequestQueue(divisionReportRequest);
    }

    private void updatePlayerInfo() {

        WSHandle.Login.updatePlayer(new Prefs.AppData(mContext).getOAuthToken(), new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {

            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {

                    if (response.has("current_live_version")) {
                        currentLiveVersion = response.getString("current_live_version");
                    }

                    String oauth_token = response.getString("oauth_token");
                    String domain_name = response.getString("domain_name");
                    String referral_url = response.getString("referral_url");
                    String customerSupportEmail = response.getString("customer_support_email");
                    String customerSupportPhone = response.getString("customer_support_phone");
                    boolean noLadder = response.getBoolean("no_ladder");
                    showPartnerProgram = response.getBoolean("show_partner_program");

                    PlayerDetail playerDetail = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("player"), new TypeToken<PlayerDetail>() {
                    }.getType());

                    ArrayList<String> topBanners = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("top_banners"), new TypeToken<List<String>>() {
                    }.getType());
                    Prefs.AppData prefs = new Prefs.AppData(mContext);
                    prefs.setPlayer(playerDetail);
                    prefs.setOAuthToken(oauth_token);
                    prefs.setDomainName(domain_name);
                    prefs.setSupportEmail(customerSupportEmail);
                    prefs.setSupportPhone(customerSupportPhone);
                    prefs.setTopBanner(topBanners);
                    prefs.setNoLadder(noLadder);
                    prefs.setReferralUrl(referral_url);
                    final String appPackageName = getActivity().getPackageName();
                    Button btnPartnerProg = v.findViewById(R.id.btnPartnerPrograms);
                    if (showPartnerProgram) {
                        btnPartnerProg.setVisibility(View.VISIBLE);
                    } else {
                        btnPartnerProg.setVisibility(View.GONE);
                    }
                    checkForceUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
              /*  mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Network error occurred. Please try again.", Toast.LENGTH_SHORT).show();
           */
            }
        });
    }

    private void checkForceUpdate() {

        WSHandle.Login.checkForceUpdate(new Prefs.AppData(mContext).getOAuthToken(), new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {

            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
//{"responseCode":"200","responseMessage":"Successful","force_update":false}
                    if (response.has("force_update")) {
                        if (response.getBoolean("force_update")) {
//                            VersionChecker versionChecker = new VersionChecker();
//                            String mLatestVersionName = "";
//                            try {
//                                mLatestVersionName = versionChecker.execute().get();
//                            } catch (InterruptedException | ExecutionException e) {
//                                e.printStackTrace();
//                            }

                            String currentAppVersion = getCurrentAppVersion();
                            if (!currentAppVersion.equalsIgnoreCase(currentLiveVersion)) {
//                            if (!currentAppVersion.equalsIgnoreCase(mLatestVersionName)) {
                                final String appPackageName = getActivity().getPackageName();
                                final BaseDialog.SimpleDialog buyDialog = BaseDialog.SimpleDialog.getDialogInstance(
                                        getActivity().getString(R.string.new_update_is_available), 0,
                                        getActivity().getString(R.string.your_current_version_is) + " " +
                                                currentAppVersion + "." + getActivity().getString(R.string.new_version_available_is) +
                                                " " + currentLiveVersion);
                                buyDialog.setPositiveButton(getActivity().getString(R.string.update_lower), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        buyDialog.dismiss();
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                    }
                                }).setNegativeButton(getActivity().getString(R.string.btn_cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        buyDialog.dismiss();
                                    }
                                });
                                buyDialog.show(getChildFragmentManager(), "buy-dlg");


                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
              /*  mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Network error occurred. Please try again.", Toast.LENGTH_SHORT).show();
           */
            }
        });
    }

    public static class CarouselFragment extends Fragment {

        private String mUrl;

        public CarouselFragment() {
            // Required empty public constructor
        }

        public static Fragment newInstance(String url) {
            CarouselFragment f = new CarouselFragment();
            f.mUrl = url;
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            CarouselAdView carouselAdView = new CarouselAdView(getActivity());
            carouselAdView.loadDataWithZoneId(mUrl);
            return carouselAdView;
        }
    }

    public static class CarouselAdView extends WebView implements View.OnTouchListener {

        private String mUrl;
        private SingleFragmentActivity mSingleFragmentActivity;

        /**
         * @param navActivity
         */
        public CarouselAdView(SingleFragmentActivity navActivity) {
            this(navActivity, null);
        }

        public CarouselAdView(Context ctx) {
            this(ctx, null);
        }

        /**
         * @param attrs
         * @param context
         */
        public CarouselAdView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        /**
         * @param attrs
         * @param context
         * @param defStyleAttr
         */
        public CarouselAdView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            mSingleFragmentActivity = (SingleFragmentActivity) context;
            initializeSettings();
        }

        @SuppressLint("SetJavaScriptEnabled")
        public void initializeSettings() {


            /**
             * Tells the WebView to enable JavaScript execution.
             * <b>The default is false.</b>
             *
             * @param flag true if the WebView should execute JavaScript
             */
            getSettings().setJavaScriptEnabled(true);

            /**
             * Since we dont want the vertical or horizontal scrolling effect on the webview
             * we will disable these effects on the webview
             *
             *
             */
            setHorizontalScrollBarEnabled(false);
            setVerticalScrollBarEnabled(false);
//            setHorizontalScrollBarEnabled(true);
//            setVerticalScrollBarEnabled(false);

            /**
             * Set the touch listener. This is used for Appboy.
             * If you remove this, and integrate Ad clicks in some other way,
             * please move the Appboy logic with it as well!
             */
            setOnTouchListener(this);

            /**
             * Sets the WebViewClient that will receive various notifications and
             * requests. This will replace the current handler.
             *
             * @param client an implementation of WebViewClient
             */
            //setWebViewClient(new CarouselWebClient(mSingleFragmentActivity));
            //getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            if (Build.VERSION.SDK_INT >= 19) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            getSettings().setDomStorageEnabled(true);
            File dir = mSingleFragmentActivity.getCacheDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            getSettings().setAppCachePath(dir.getPath());
            getSettings().setAllowFileAccess(true);
            getSettings().setAppCacheEnabled(true);
            getSettings().setLoadsImagesAutomatically(true);
            if (!NetworkUtils.isOnline(mSingleFragmentActivity))
                getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            else
                getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);/**/

            //getSettings().setAppCacheEnabled(true);
            //getSettings().setAppCachePath(mSingleFragmentActivity.getCacheDir().getPath());
            //getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                /**
                 * Add our zone id for the clicked ad to our params HashMap
                 */
                HashMap<String, String> params = new HashMap<>();
                params.put("url", mUrl);

            }
            return false;
        }

        /**
         * @param url This method will build the web data and load the urlList
         *            <p>
         *            addpted from the NUMI app code base
         */
        public void loadDataWithZoneId(String url) {
            mUrl = url;

            String webData = buildDataWithZoneId();
            loadData(webData, "text/html", null);
            //loadUrl("www.google.com");
        }

        /**
         * Adopted from NUMI app code base
         */
        private String buildDataWithZoneId() {
            // CSS fills width and height of the view (it'll stretch..) and sets a fixed position, and removes margins
            String html = "<html><body><img src=\"" + mUrl + "\" width=\"100%\" height=\"100%\"\"/></body></html>";
            return html;
        }
    }

    public static class CarouselWebClient extends WebViewClient {

        static final String LOG_TAG = CarouselWebClient.class.getSimpleName();
        private SingleFragmentActivity mSingleFragmentActivity;

        public CarouselWebClient(SingleFragmentActivity navActivity) {
            mSingleFragmentActivity = navActivity;
            Log.e("CarouselWebClient", "CarouselWebClient");
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            /**
             *
             * @see #processUri(Uri)
             *
             */
            return processUri(view, Uri.parse(url));
        }

        /*@TargetApi(Build.VERSION_CODES.N)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.e("LOG_TAG", "Url for web request" +  request.getUrl().getHost());
            return processUri(view, request.getUrl());
        }*/

        private boolean processUri(WebView view, Uri uri) {

            Log.e("LOG_TAG", "URI value" + uri);

            String urlHost = uri.getHost();

            String urlScheme = uri.getScheme();


            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(uri);
            mSingleFragmentActivity.startActivity(i);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

        }
    }

    class CarouselViewAdapter extends FragmentPagerAdapter {

        List<String> urlList;

        CarouselViewAdapter(FragmentManager fm, List<String> mCarouselUrl) {
            super(fm);
            this.urlList = mCarouselUrl;
        }

        @Override
        public Fragment getItem(int position) {
            int index = position % urlList.size();
            return CarouselFragment.newInstance(urlList.get(index));
        }

        @Override
        public int getCount() {
            return urlList.size();
        }
    }

    void getPlayerDetails() {
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
        WSHandle.Profile.getPlayerInformation(prefs.getUserID(), "1", new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {

            }

            @Override
            public void onSuccessResponse(String response) {
//                progressDialog.dismiss();
                Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

                mUserData = gson.fromJson(response, new TypeToken<PlayerInformationModel.UserDataBean>() {
                }.getType());
                prefs.setUserData(response);
                PlayerDetail mPlayerDetail = prefs.getPlayer();
                mPlayerDetail.CanPushNotification = mUserData.isScore_notification();
                prefs.setPlayer(mPlayerDetail);
                setupHomeItems();

//				setUI(mPlayerDetails);
            }

            @Override
            public void onError(VolleyError error) {
            }
        });
    }

    ArrayList<OurPlayerDetailMode> mCommunityPlayersList = new ArrayList<>();

    void getPlayerList() {
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
        WSHandle.Profile.getPlayerList(prefs.getUserID(), String.valueOf(communityMemberPageCount), new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
            }

            @Override
            public void onSuccessResponse(String response) {
//                progressDialog.dismiss();
                communityMemberPageCount++;
                int oldSize = mCommunityPlayersList.size();
                Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();
                /*if (mCommunityPlayersList.isEmpty()) {*/
                mCommunityPlayersList.addAll(gson.fromJson(response, new TypeToken<ArrayList<OurPlayerDetailMode>>() {
                }.getType()));
                int newSize = mCommunityPlayersList.size();
                if (cardStackAdapter != null) {
                    cardStackAdapter.notifyItemRangeInserted(oldSize, newSize - oldSize);
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mCVStackView.setVisibility(View.VISIBLE);
                }
                /*} else{
                    try {
                        int previousCount = mCommunityPlayersList.size();
                        mCommunityPlayersList.addAll(gson.fromJson(response, new TypeToken<ArrayList<OurPlayerDetailMode>>() {
                        }.getType()));
                        int newCount = mCommunityPlayersList.size();
                        if (cardStackAdapter!=null) {
                            cardStackAdapter.notifyItemRangeInserted(previousCount, newCount - previousCount);
//                        If there are 30 new entries then need to fetch for more data again
                            if (newCount - previousCount == 30){
                                getPlayerList();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }*/

//				setUI(mPlayerDetails);
            }

            @Override
            public void onError(VolleyError error) {
            }
        });
    }
    /*private class LogoutAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            return WSHandle.Login.logout(new Prefs.AppData(mContext).getOAuthToken());
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...");
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            mProgressDialog.dismiss();
            if (jsonObject != null) {
                try {
                    if (!jsonObject.get("responseCode").equals("200")) {
                        Toast.makeText(mContext, "Error while logging out.", Toast.LENGTH_LONG).show();
                    } else {
                        new Prefs.AppData(mContext).setOAuthToken(null);

                        mRealm.beginTransaction();
                        mRealm.allObjects(Court.class).clear();
                        mRealm.allObjects(Location.class).clear();
                        mRealm.allObjects(CourtRegion.class).clear();
                        mRealm.commitTransaction();

                        startActivity(SingleFragmentActivity.AuthActivity.getIntent(mContext));
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
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


    /*private void sendNotification(String message) {

//        Intent intent = new Intent(Constants.UPDATE_NOTIFICATION_COUNT);

//        intent = Intent(this, AuthActivity::class.java)
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getActivity());
//
        Intent intent  =  new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtra("notification",true);

//        if(mAppUtils.getUserData(mPref).user_type.equals(AppConstants.BUSINESS_USER)) {
//            mainActivityIntent = Intent(this, DashBoardScreen::class.java)
//        }else {
//            mainActivityIntent = Intent(this, CustomerDashBoardScreen::class.java)
//        }
//        stackBuilder.addNextIntent(mainActivityIntent)
//        if (intent != null) {
//            stackBuilder.addNextIntent(intent)
//        }
//        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT, intent, 0);

//        java.lang.IllegalStateException: No intents added to TaskStackBuilder; cannot getPendingIntent
        Uri defaultSoundUri = null;
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        */

    /**
     *
     *//*
        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = getString(R.string.app_name);
        NotificationCompat.Builder notificationBuilder = null;
   *//*     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val mChannel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(mChannel)
            notificationBuilder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentTitle(resources.getString(R.string.app_name))
                    .setContentText(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT))
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setSound(defaultSoundUri)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT)))
                    .setContentIntent(pendingIntent).setOngoing(false).setVibrate(longArrayOf(300, 300))
        } else {*//*
            notificationBuilder = new NotificationCompat.Builder(getActivity(), channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(Html.fromHtml(message))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setChannelId(channelId)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(message)))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pendingIntent).setOngoing(false);
//        }

        notificationManager.notify(0, notificationBuilder.build());
    }*/

    void viewLatestScroce() {
        Class fragClass = FragLastestScore.class;
        startActivity(SingleFragmentActivity.getIntent(mContext, fragClass, null));
    }
}
