//package com.tennisdc.tennisleaguenetwork;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import com.tennisdc.tln.R;
//import com.tennisdc.tln.BuildConfig;
//
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentPagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.Display;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ViewFlipper;
//import com.android.volley.Request;
//import com.android.volley.VolleyError;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//import com.tennisdc.tln.common.App;
//import com.tennisdc.tennisleaguenetwork.common.GsonRealmExclusionStrategy;
//import com.tennisdc.tennisleaguenetwork.common.Prefs;
//import com.tennisdc.tennisleaguenetwork.model.AlertDetail;
//import com.tennisdc.tennisleaguenetwork.model.Court;
//import com.tennisdc.tennisleaguenetwork.model.CourtRegion;
//import com.tennisdc.tennisleaguenetwork.model.Location;
//import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
//import com.tennisdc.tennisleaguenetwork.model.UncaughtException;
//import com.tennisdc.tennisleaguenetwork.modules.AlertDialog.AlertsPagerAdapter;
//import com.tennisdc.tennisleaguenetwork.modules.DlgCommunicationSettings;
//import com.tennisdc.tennisleaguenetwork.modules.courts.FragCourts;
//import com.tennisdc.tennisleaguenetwork.modules.ladder.FragLadderDetails;
//import com.tennisdc.tennisleaguenetwork.modules.league.FragLeagueDetails;
//import com.tennisdc.tennisleaguenetwork.modules.partner.FragPartnerProgramDetails;
//import com.tennisdc.tennisleaguenetwork.modules.poty.FragPlayersOfTheYearHome;
//import com.tennisdc.tennisleaguenetwork.modules.profile.FragProfileNew;
//import com.tennisdc.tennisleaguenetwork.modules.referral.FragReferralRewards;
//import com.tennisdc.tennisleaguenetwork.modules.submit_score.FragSubmitScorePager;
//import com.tennisdc.tennisleaguenetwork.modules.support.FragSupport;
//import com.tennisdc.tennisleaguenetwork.modules.tournament.FragTournamentAll;
//import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
//import com.tennisdc.tennisleaguenetwork.network.WSHandle;
//import com.tennisdc.tennisleaguenetwork.services.ErrorReportingService;
//import com.tennisdc.tennisleaguenetwork.services.SubmitPaymentService;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import io.realm.Realm;
//import io.realm.exceptions.RealmMigrationNeededException;
//import androidx.core.content.ContextCompat;
//
//public class FragHome extends Fragment implements View.OnClickListener {
//
//    private Context mContext;
//
//    private ProgressDialog mProgressDialog;
//
//    private String mDomainName;
//    private Realm mRealm;
//
//    private Prefs mPrefs ;
//    private ViewPager viewPager;
//    private CarouselViewAdapter adapter;
//    List<String> mCarouselUrl;
//    private ImageView[] mDots;
//    View v;
//    FrameLayout carouselContainer;
//    SharedPreferences sharedpreferences;
//    public static final String MyPREFERENCES = "MyPrefs";
//    public static final String versionCodeString = "version_code";
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mContext = context;
//        try {
//            mRealm = Realm.getInstance(context);
//        } catch (RealmMigrationNeededException e) {
//            Realm.deleteRealmFile(context);
//            mRealm = Realm.getInstance(context);
//        }
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//        mPrefs = new Prefs(mContext);
//        checkForFirstTimeLaunch();
//    }
//
//    private void checkForFirstTimeLaunch(){
//        if(mPrefs.getApp_runFirst()==null)
//        {
//            // That's mean First Time Launch
//            mPrefs.setApp_runFirst("NO");
//            DlgCommunicationSettings communicationSettings = DlgCommunicationSettings.getInstance();
//            communicationSettings.show(getChildFragmentManager(), "communication-dlg");
//        } else {
//            final int versionCode = BuildConfig.VERSION_CODE;
//            sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//            int preferencesVersionCode = sharedpreferences.getInt(versionCodeString, 0);
//            if (preferencesVersionCode == 0 || preferencesVersionCode < versionCode) {
//                WSHandle.Login.logout(new Prefs.AppData(mContext).getOAuthToken(), new VolleyHelper.IRequestListener<String, String>() {
//                    @Override
//                    public void onFailureResponse(String response) {
//                        Toast.makeText(mContext, "Error while logging out.", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onSuccessResponse(String response) {
//                        SharedPreferences.Editor edit = sharedpreferences.edit();
//                        edit.putInt(versionCodeString,versionCode);
//                        edit.commit();
//                        new Prefs.AppData(mContext).setOAuthToken(null);
//
//                        mRealm.beginTransaction();
//                        mRealm.allObjects(Court.class).clear();
//                        mRealm.allObjects(Location.class).clear();
//                        mRealm.allObjects(CourtRegion.class).clear();
//                        mRealm.commitTransaction();
//
//                        startActivity(AuthActivity.getIntent(mContext));
//                        getActivity().finish();
//
//                    }
//
//                    @Override
//                    public void onError(VolleyError error) {
//                        Toast.makeText(mContext, "Network error while logging out.", Toast.LENGTH_LONG).show();
//                    }
//                });
//                return;
//            }
//        }
//    }
//
//    private void showAlert(List<AlertDetail> alertsModel) {
//
//        final Dialog dialog = new Dialog(this.getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.pager_layout);
//
//        AlertsPagerAdapter mAdapter = new AlertsPagerAdapter(this.getContext(), alertsModel);
//        ViewPager pager = (ViewPager) dialog.findViewById(R.id.viewPager);
//        pager.setAdapter(mAdapter);
//
//        ImageButton closeButton = (ImageButton) dialog.findViewById(R.id.closeButton);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        Button dontShowButton = (Button) dialog.findViewById(R.id.dontShowButton);
//        dontShowButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dontShowAlert();
//                dialog.dismiss();
//            }
//        });
//
//        LinearLayout dotsIndicator = (LinearLayout) dialog.findViewById(R.id.pager_dots);
//
//        final ImageView[] ivArrayDotsPager = new ImageView[alertsModel.size()];
//        for (int i = 0; i < ivArrayDotsPager.length; i++) {
//            ivArrayDotsPager[i] = new ImageView(getActivity());
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(5, 0, 5, 0);
//            ivArrayDotsPager[i].setLayoutParams(params);
//            ivArrayDotsPager[i].setImageResource(R.drawable.page_indicator_unselected);
//            ivArrayDotsPager[i].setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View view) {
//                    view.setAlpha(1);
//                }
//            });
//            dotsIndicator.addView(ivArrayDotsPager[i]);
//            dotsIndicator.bringToFront();
//        }
//
//        ivArrayDotsPager[0].setImageResource(R.drawable.page_indicator_selected);
//
//        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
//        {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                for (int i = 0; i < ivArrayDotsPager.length; i++) {
//                    ivArrayDotsPager[i].setImageResource(R.drawable.page_indicator_unselected);
//                }
//                ivArrayDotsPager[position].setImageResource(R.drawable.page_indicator_selected);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                for (int i = 0; i < ivArrayDotsPager.length; i++) {
//                    ivArrayDotsPager[i].setImageResource(R.drawable.page_indicator_unselected);
//                }
//                ivArrayDotsPager[position].setImageResource(R.drawable.page_indicator_selected);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) { }
//        });
//
//        dialog.show();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        App.LogFacebookEvent(getActivity(),this.getClass().getName());
//        Prefs.AppData prefs = new Prefs.AppData(mContext);
//
//        if (prefs.getPlayer().status.equalsIgnoreCase("Banned")) {
//            v = inflater.inflate(R.layout.frag_home_banned, container, false);
//            TextView welcome_player = (TextView) v.findViewById(R.id.welcome_player_name);
//            StringBuilder playerNameBuilder = new StringBuilder();
//            playerNameBuilder.append("Welcome ");
//            playerNameBuilder.append(prefs.getPlayer().firstName);
//            playerNameBuilder.append("!");
//            welcome_player.setText(playerNameBuilder.toString());
//            mDomainName = new Prefs.AppData(mContext).getDomainName();
//            v.findViewById(R.id.btnCustomerSupport).setOnClickListener(this);
//        } else {
//            v = inflater.inflate(R.layout.frag_home, container, false);
//            TextView welcome_player = (TextView) v.findViewById(R.id.welcome_player_name);
//            StringBuilder playerNameBuilder = new StringBuilder();
//            playerNameBuilder.append("Welcome ");
//            playerNameBuilder.append(prefs.getPlayer().firstName);
//            playerNameBuilder.append("!");
//            welcome_player.setText(playerNameBuilder.toString());
//
//            ImageView img_referral = (ImageView) v.findViewById(R.id.img_referral);
//            img_referral.setOnClickListener(this);
//
//            ViewFlipper viewFlipper = (ViewFlipper) v.findViewById(R.id.viewFlipper);
//            mDomainName = new Prefs.AppData(mContext).getDomainName();
//            if (TextUtils.equals(mDomainName, App.sDefaultDomain)) {
//                viewFlipper.setDisplayedChild(1);
//            } else {
//                viewFlipper.setDisplayedChild(0);
//                v.findViewById(R.id.btnSubmitScore).setOnClickListener(this);
//                v.findViewById(R.id.btnLeagues).setOnClickListener(this);
//                v.findViewById(R.id.btnPartnerPrograms).setOnClickListener(this);
//
//                if(!prefs.getNoLadder()) {
//                    v.findViewById(R.id.btnTennisLadder).animate().alpha(1.0f);
//                    v.findViewById(R.id.btnTennisLadder).setOnClickListener(this);
//                } else {
//                    v.findViewById(R.id.btnTennisLadder).animate().alpha(0.5f);
//                }
//                v.findViewById(R.id.btnCourts).setOnClickListener(this);
//                v.findViewById(R.id.btnTournaments).setOnClickListener(this);
//                v.findViewById(R.id.btnBuy).setOnClickListener(this);
//                v.findViewById(R.id.btnCustomerSupport).setOnClickListener(this);
////                v.findViewById(R.id.btnUpcomingPrograms).setOnClickListener(this);
////                v.findViewById(R.id.btnPOTY).setOnClickListener(this);
//            }
//        }
//
//
//        return v;
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        final Prefs.AppData appData = new Prefs.AppData(getActivity());
//        if (!appData.getPlayer().status.equalsIgnoreCase("Banned")) {
//
//
//        if (new Prefs.AppData(getContext()).getPlayer().mobilePopupVisible) {
//            fetchAlertList();
//        }
//
//        if (appData.getUpdatePlayer()) {
//            final ProgressDialog progressDialog = ProgressDialog.show(mContext, null, "Please wait...");
//            WSHandle.Login.getUserInfo(new VolleyHelper.IRequestListener<PlayerDetail, String>() {
//                @Override
//                public void onFailureResponse(String response) {
//                    progressDialog.dismiss();
//                    Toast.makeText(getActivity(), "Server Error:" + response, Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onSuccessResponse(PlayerDetail response) {
//                    appData.setPlayer(response);
//                    appData.setUpdatePlayer(false);
//
//                }
//
//                @Override
//                public void onError(VolleyError error) {
//                    progressDialog.dismiss();
//                    Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//
//        if (mRealm.allObjects(Court.class).size() == 0) {
//            final ProgressDialog progressDialog = ProgressDialog.show(mContext, null, "Please wait...");
//            WSHandle.Courts.getCourts(new VolleyHelper.IRequestListener<List<Court>, String>() {
//                @Override
//                public void onFailureResponse(String response) {
//                    progressDialog.dismiss();
//                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onSuccessResponse(List<Court> response) {
//                    //progressDialog.dismiss();
//                    //Realm realm = Realm.getInstance(mContext);
//
//                    mRealm.beginTransaction();
//                    try {
//                        /* delete existing courts */
//                        mRealm.allObjects(Court.class).clear();
//
//                        /* add new courts */
//                        mRealm.copyToRealm(response);
//
//                        mRealm.commitTransaction();
//
//                        if (mRealm.allObjects(Location.class).size() == 0) {
//                        /* Fetch Locations and regions */
//                            WSHandle.Courts.getLocationAndRegions(new VolleyHelper.IRequestListener<List<Location>, String>() {
//                                @Override
//                                public void onFailureResponse(String response) {
//                                    progressDialog.dismiss();
//                                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
//                                }
//
//                                @Override
//                                public void onSuccessResponse(List<Location> response) {
//                                    progressDialog.dismiss();
//
//                                    try {
//                                        mRealm.beginTransaction();
//
//                                    /* delete existing Locations and Court regions */
//                                        mRealm.allObjects(CourtRegion.class).clear();
//                                        mRealm.allObjects(Location.class).clear();
//
//                                    /* add new Locations and Court regions */
//                                        mRealm.copyToRealm(response);
//
//                                        mRealm.commitTransaction();
//                                    } catch (Exception e) {
//                                        mRealm.cancelTransaction();
//                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onError(VolleyError error) {
//                                    progressDialog.dismiss();
//                                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
//                                }
//
//                            });
//                        } else progressDialog.dismiss();
//                    } catch (Exception e) {
//                        progressDialog.dismiss();
//                        mRealm.cancelTransaction();
//                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                }
//
//                @Override
//                public void onError(VolleyError error) {
//                    progressDialog.dismiss();
//                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }
//    }
//
//    private void setUpViewPager(ArrayList<String> topBanner){
//        if (topBanner.size() > 0) {
//            viewPager = (ViewPager) v.findViewById(R.id.view_pager_carousel);
//            carouselContainer = (FrameLayout) v.findViewById(R.id.carousel_container);
//            carouselContainer.setVisibility(View.VISIBLE);
//            mCarouselUrl = new ArrayList<>();
//            for (int i=0; i<topBanner.size(); i++) {
//                mCarouselUrl.add(topBanner.get(i));
//            }
//
//
//            //view.findViewById(R.id.carousel_progress_indicator).setVisibility(View.VISIBLE);
//            final LinearLayout dotLayout = (LinearLayout) v.findViewById(R.id.viewPagerDots);
//            if (topBanner.size() > 1) {
//                setUiPageViewController(dotLayout, mCarouselUrl);
//                dotLayout.setVisibility(View.VISIBLE);
//            } else {
//                dotLayout.setVisibility(View.INVISIBLE);
//            }
//            adapter = new CarouselViewAdapter(getActivity().getSupportFragmentManager(), mCarouselUrl);
//            viewPager.setAdapter(adapter);
//            //viewPager.setOffscreenPageLimit(3);
//            viewPager.setCurrentItem(0);
//            viewPager.setClipToPadding(false);
//            // set padding manually, the more you set the padding the more you see of prev & next page
//            /*viewPager.setPadding((int)getResources().getDimension(R.dimen.ad_carousel_padding), 0,
//                    (int)getResources().getDimension(R.dimen.ad_carousel_padding), 0);*/
//            // sets a margin b/w individual pages to ensure that there is a gap b/w them
//            viewPager.setPageMargin((int) getResources().getDimension(R.dimen.ad_carousel_margin));
//            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                int oldPosition = 0;
//
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                }
//
//                @Override
//                public void onPageSelected(int position) {
//                    if (dotLayout.getVisibility() == View.VISIBLE) {
//                        mDots[position].setImageDrawable(ContextCompat.getDrawable(getActivity(),
//                                R.drawable.black_view_pager_indicator_selected));
//                        mDots[oldPosition].setImageDrawable(ContextCompat.getDrawable(getActivity(),
//                                R.drawable.white_non_selected_view_pager_indicator));
//                        oldPosition = position;
//                    }
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//
//                }
//            });
//        } else {
//            carouselContainer = (FrameLayout) v.findViewById(R.id.carousel_container);
//            carouselContainer.setVisibility(View.GONE);
//        }
//    }
//
//    /**
//     * @param viewPagerDots this will set up the page indicator circles
//     * @param mCarouselUrl
//     */
//    private void setUiPageViewController(LinearLayout viewPagerDots, List<String> mCarouselUrl) {
//
//        if (!mCarouselUrl.isEmpty() && mCarouselUrl.size() > 0) {
//            mDots = new ImageView[mCarouselUrl.size()];
//            for (int i = 0; i < mDots.length; i++) {
//                mDots[i] = new ImageView(getActivity());
//                mDots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),
//                        R.drawable.white_non_selected_view_pager_indicator));
//
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                );
//
//                params.setMargins(3, 0, 3, 0);
//                viewPagerDots.addView(mDots[i], params);
//            }
//            mDots[0].setImageDrawable(ContextCompat.getDrawable(getActivity(),
//                    R.drawable.black_view_pager_indicator_selected));
//        }
//    }
//
//    class CarouselViewAdapter extends FragmentPagerAdapter {
//
//        List<String> urlList;
//
//        public CarouselViewAdapter(FragmentManager fm, List<String> mCarouselUrl) {
//            super(fm);
//            this.urlList = mCarouselUrl;
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            int index = position%urlList.size();
//            return  CarouselFragment.newInstance(urlList.get(index));
//        }
//
//        @Override
//        public int getCount() {
//            return urlList.size();
//        }
//    }
//
//    public static class CarouselFragment extends Fragment {
//
//        private String mUrl;
//
//        public CarouselFragment() {
//            // Required empty public constructor
//        }
//
//        public static Fragment newInstance(String url) {
//            CarouselFragment f = new CarouselFragment();
//            f.mUrl = url;
//            return f;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//
//            CarouselAdView carouselAdView = new CarouselAdView(getActivity());
//            carouselAdView.loadDataWithZoneId(mUrl);
//            return carouselAdView;
//        }
//    }
//
//    public static class CarouselAdView extends WebView implements View.OnTouchListener {
//
//        private String mUrl;
//        private SingleFragmentActivity mSingleFragmentActivity;
//
//        /**
//         *
//         * @param navActivity
//         *
//         */
//        public CarouselAdView(SingleFragmentActivity navActivity) {
//            this(navActivity, null);
//        }
//
//        public CarouselAdView(Context ctx) {
//            this(ctx, null);
//        }
//
//        /**
//         * @param attrs
//         * @param context
//         *
//         */
//        public CarouselAdView(Context context, AttributeSet attrs) {
//            this(context, attrs, 0);
//        }
//
//        /**
//         *
//         * @param attrs
//         * @param context
//         * @param defStyleAttr
//         *
//         */
//        public CarouselAdView(Context context, AttributeSet attrs, int defStyleAttr) {
//            super(context, attrs, defStyleAttr);
//            mSingleFragmentActivity = (SingleFragmentActivity) context;
//            initializeSettings();
//        }
//
//        public void initializeSettings() {
//
//
//            /**
//             * Tells the WebView to enable JavaScript execution.
//             * <b>The default is false.</b>
//             *
//             * @param flag true if the WebView should execute JavaScript
//             */
//            getSettings().setJavaScriptEnabled(true);
//
//            /**
//             * Since we dont want the vertical or horizontal scrolling effect on the webview
//             * we will disable these effects on the webview
//             *
//             *
//             */
//            setHorizontalScrollBarEnabled(false);
//            setVerticalScrollBarEnabled(false);
//
//            /**
//             * Set the touch listener. This is used for Appboy.
//             * If you remove this, and integrate Ad clicks in some other way,
//             * please move the Appboy logic with it as well!
//             */
//            setOnTouchListener(this);
//
//            /**
//             * Sets the WebViewClient that will receive various notifications and
//             * requests. This will replace the current handler.
//             *
//             * @param client an implementation of WebViewClient
//             */
//            //setWebViewClient(new CarouselWebClient(mSingleFragmentActivity));
//            //getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//            if (Build.VERSION.SDK_INT >= 19) {
//                setLayerType(View.LAYER_TYPE_HARDWARE, null);
//            } else {
//                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            }
//
//            getSettings().setDomStorageEnabled(true);
//            File dir = mSingleFragmentActivity.getCacheDir();
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            getSettings().setAppCachePath(dir.getPath());
//            getSettings().setAllowFileAccess(true);
//            getSettings().setAppCacheEnabled(true);
//            getSettings().setLoadsImagesAutomatically(true);
//            if(!NetworkUtils.isOnline(mSingleFragmentActivity))
//                getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//            else
//                getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);/**/
//
//            //getSettings().setAppCacheEnabled(true);
//            //getSettings().setAppCachePath(mSingleFragmentActivity.getCacheDir().getPath());
//            //getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                /**
//                 * Add our zone id for the clicked ad to our params HashMap
//                 */
//                HashMap<String, String> params = new HashMap<>();
//                params.put("url", mUrl);
//
//            }
//            return false;
//        }
//
//        /**
//         *
//         * @param url
//         *
//         * This method will build the web data and load the urlList
//         *
//         * addpted from the NUMI app code base
//         *
//         *
//         */
//        public void loadDataWithZoneId(String url) {
//            mUrl = url;
//
//            String webData = buildDataWithZoneId();
//            loadData(webData, "text/html", null);
//            //loadUrl("www.google.com");
//        }
//
//        /**
//         *
//         * Adopted from NUMI app code base
//         *
//         *
//         */
//        private String buildDataWithZoneId() {
//            // CSS fills width and height of the view (it'll stretch..) and sets a fixed position, and removes margins
//            String html = "<html><body><img src=\"" + mUrl + "\" width=\"100%\" height=\"100%\"\"/></body></html>";
//            return html;
//        }
//    }
//
//    public static class CarouselWebClient extends WebViewClient {
//
//        private SingleFragmentActivity mSingleFragmentActivity;
//        public static final String "LOG_TAG" = CarouselWebClient.class.getSimpleName();
//
//        public CarouselWebClient(SingleFragmentActivity navActivity) {
//            mSingleFragmentActivity = navActivity;
//            Log.e("CarouselWebClient","CarouselWebClient");
//        }
//
//        @SuppressWarnings("deprecation")
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//            /**
//             *
//             * @see #processUri(Uri)
//             *
//             */
//            return processUri(view, Uri.parse(url));
//        }
//
//        /*@TargetApi(Build.VERSION_CODES.N)
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            Log.e("LOG_TAG", "Url for web request" +  request.getUrl().getHost());
//            return processUri(view, request.getUrl());
//        }*/
//
//        private boolean processUri(WebView view, Uri uri) {
//
//            Log.e("LOG_TAG", "URI value" + uri);
//
//            String urlHost = uri.getHost();
//
//            String urlScheme = uri.getScheme();
//
//
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(uri);
//                mSingleFragmentActivity.startActivity(i);
//                return true;
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//
//        }
//    }
//
//
//
//     @Override
//    public void onResume() {
//        super.onResume();
//
//         Prefs.AppData prefs = new Prefs.AppData(mContext);
//         if (!prefs.getPlayer().status.equalsIgnoreCase("Banned")) {
//             setUpViewPager(prefs.getTopBanner());
//         }
//        if (!TextUtils.isEmpty(mDomainName)) getActivity().setTitle("  "+mDomainName);
//        else getActivity().setTitle("  "+mDomainName);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.ic_action);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//
//        // get logged error count
//        Realm realm = Realm.getInstance(getActivity());
//        int exceptionCount = realm.allObjects(UncaughtException.class).size();
//        realm.close();
//
//        if (exceptionCount > 0) {
//            getActivity().startService(new Intent(getActivity(), ErrorReportingService.class));
//        }
//        //test tannu
//     //   getActivity().startService(new Intent(getActivity(), SubmitPaymentService.class));
//
//        // Calling Update Api of Player
//         updatePlayerInfo();
//    }
//
//    @Override
//    public void onDetach() {
//        mContext = null;
//        mRealm.close();
//        mRealm = null;
//        super.onDetach();
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        Prefs.AppData prefs = new Prefs.AppData(mContext);
//        if (prefs.getPlayer().status.equalsIgnoreCase("Banned")) {
//            inflater.inflate(R.menu.banned_home, menu);
//        } else {
//            inflater.inflate(R.menu.opt_home, menu);
//        }
//        PlayerDetail curPlayerDetail = new Prefs.AppData(mContext).getPlayer();
//        menu.findItem(R.id.action_profile).setTitle("Hi " + curPlayerDetail.Name);
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_communication_setting:
//                DlgCommunicationSettings communicationSettings = DlgCommunicationSettings.getInstance();
//                communicationSettings.show(getChildFragmentManager(), "communication-dlg");
//                return true;
//
//            case R.id.action_my_profile :
//                startActivity(SingleFragmentActivity.getIntent(mContext, FragProfileNew.class, null));
//                return true;
//
//            case R.id.action_rate_us:
//                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                }
//                return true;
//
//            case R.id.action_refer_a_friend :
//                startActivity(SingleFragmentActivity.getIntent(mContext, FragReferralRewards.class, null));
//                return true;
//
//            case R.id.action_contact_us:
//                startActivity(SingleFragmentActivity.getIntent(mContext, FragSupport.class, null));
//                return true;
//
//            case R.id.action_logout:
//                //new LogoutAsyncTask().execute();
//                mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...");
//                WSHandle.Login.logout(new Prefs.AppData(mContext).getOAuthToken(), new VolleyHelper.IRequestListener<String, String>() {
//                    @Override
//                    public void onFailureResponse(String response) {
//                        mProgressDialog.dismiss();
//                        Toast.makeText(mContext, "Error while logging out.", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onSuccessResponse(String response) {
//                        new Prefs.AppData(mContext).setOAuthToken(null);
//
//                        mRealm.beginTransaction();
//                        mRealm.allObjects(Court.class).clear();
//                        mRealm.allObjects(Location.class).clear();
//                        mRealm.allObjects(CourtRegion.class).clear();
//                        mRealm.commitTransaction();
//
//                        startActivity(AuthActivity.getIntent(mContext));
//                        getActivity().finish();
//
//                        mProgressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onError(VolleyError error) {
//                        mProgressDialog.dismiss();
//                        Toast.makeText(mContext, "Network error while logging out.", Toast.LENGTH_LONG).show();
//                    }
//                });
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * Called when a view has been clicked.
//     *
//     * @param v
//     *         The view that was clicked.
//     */
//    @Override
//    public void onClick(View v) {
//        Class fragClass = null;
//        switch (v.getId()) {
//            case R.id.btnSubmitScore:
//                fragClass = FragSubmitScorePager.FragSelectCompetition.class;
//                break;
//
//            case R.id.btnLeagues:
//                fragClass = FragLeagueDetails.class;
//                break;
//
//            case R.id.btnPartnerPrograms:
//                fragClass = FragPartnerProgramDetails.class;
//                break;
//
//            case R.id.btnTennisLadder:
//                fragClass = FragLadderDetails.class;
//                break;
//
//            case R.id.btnCourts:
//                fragClass = FragCourts.class;
//                break;
//
//            case R.id.btnTournaments:
//                fragClass = FragTournamentAll.class;
//                break;
//
//            case R.id.btnCustomerSupport:
//                fragClass = FragSupport.class;
//                break;
//
//            case R.id.btnBuy:
//                startActivity(SingleFragmentActivity.BuyActivity.getIntent(mContext));
//                return;
//
//            /*case R.id.btnUpcomingPrograms:
//                fragClass = FragAlert.class;
//                startActivity(SingleFragmentActivity.getIntent(mContext, fragClass, null));
//                return;
//
//            case R.id.btnPOTY:
//                startActivity(SingleFragmentActivity.getIntent(mContext, FragPlayersOfTheYearHome.class, null));
//                return;*/
//
//            case R.id.img_referral:
//                fragClass = FragReferralRewards.class;
//                break;
//        }
//
//        startActivity(SingleFragmentActivity.getIntent(mContext, fragClass, null));
//    }
//
//    private void fetchAlertList() {
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);
//
//        Request divisionReportRequest = WSHandle.Alerts.getDetailsRequest(App.sOAuth, new VolleyHelper.IRequestListener<JSONObject, String>() {
//            @Override
//            public void onFailureResponse(String response) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onSuccessResponse(JSONObject response) {
//                try {
//                    progressDialog.dismiss();
//
//                    Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();
//
//                    List<AlertDetail> mAlertDetails = gson.fromJson(response.getString("slider_images"), new TypeToken<List<AlertDetail>>() {}.getType());
//
//                    showAlert(mAlertDetails);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            }
//        });
//        VolleyHelper.getInstance(getActivity()).addToRequestQueue(divisionReportRequest);
//    }
//
//    private void dontShowAlert() {
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);
//
//        Request divisionReportRequest = WSHandle.Alerts.updateToNotShow(App.sOAuth, true, new VolleyHelper.IRequestListener<JSONObject, String>() {
//            @Override
//            public void onFailureResponse(String response) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onSuccessResponse(JSONObject response) {
//                progressDialog.dismiss();
//
//                PlayerDetail player =  new Prefs.AppData(getContext()).getPlayer();
//                player.mobilePopupVisible = false;
//                new Prefs.AppData(getContext()).setPlayer(player);
//
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            }
//        });
//        VolleyHelper.getInstance(getActivity()).addToRequestQueue(divisionReportRequest);
//    }
//
//    private void updatePlayerInfo(){
//
//        WSHandle.Login.updatePlayer(new Prefs.AppData(mContext).getOAuthToken(),  new VolleyHelper.IRequestListener<JSONObject, String>() {
//            @Override
//            public void onFailureResponse(String response) {
//
//            }
//
//            @Override
//            public void onSuccessResponse(JSONObject response) {
//                try {
//                    String oauth_token = response.getString("oauth_token");
//                    String domain_name = response.getString("domain_name");
//                    String referral_url = response.getString("referral_url");
//                    String customerSupportEmail = response.getString("customer_support_email");
//                    String customerSupportPhone = response.getString("customer_support_phone");
//                    boolean noLadder = response.getBoolean("no_ladder");
//
//                    PlayerDetail playerDetail = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("player"), new TypeToken<PlayerDetail>() {}.getType());
//
//                    ArrayList<String> topBanners = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("top_banners"), new TypeToken<List<String>>() {}.getType());
//                    Prefs.AppData prefs = new Prefs.AppData(mContext);
//                    prefs.setPlayer(playerDetail);
//                    prefs.setOAuthToken(oauth_token);
//                    prefs.setDomainName(domain_name);
//                    prefs.setSupportEmail(customerSupportEmail);
//                    prefs.setSupportPhone(customerSupportPhone);
//                    prefs.setTopBanner(topBanners);
//                    prefs.setNoLadder(noLadder);
//                    prefs.setReferralUrl(referral_url);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//              /*  mProgressDialog.dismiss();
//                Toast.makeText(getActivity(), "Network error occurred. Please try again.", Toast.LENGTH_SHORT).show();
//           */ }
//        });
//    }
//
//
//    /*private class LogoutAsyncTask extends AsyncTask<String, Void, JSONObject> {
//
//        @Override
//        protected JSONObject doInBackground(String... params) {
//            return WSHandle.Login.logout(new Prefs.AppData(mContext).getOAuthToken());
//        }
//
//        @Override
//        protected void onPreExecute() {
//            mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...");
//        }
//
//        @Override
//        protected void onPostExecute(JSONObject jsonObject) {
//            mProgressDialog.dismiss();
//            if (jsonObject != null) {
//                try {
//                    if (!jsonObject.get("responseCode").equals("200")) {
//                        Toast.makeText(mContext, "Error while logging out.", Toast.LENGTH_LONG).show();
//                    } else {
//                        new Prefs.AppData(mContext).setOAuthToken(null);
//
//                        mRealm.beginTransaction();
//                        mRealm.allObjects(Court.class).clear();
//                        mRealm.allObjects(Location.class).clear();
//                        mRealm.allObjects(CourtRegion.class).clear();
//                        mRealm.commitTransaction();
//
//                        startActivity(SingleFragmentActivity.AuthActivity.getIntent(mContext));
//                        getActivity().finish();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }*/
//
//}
