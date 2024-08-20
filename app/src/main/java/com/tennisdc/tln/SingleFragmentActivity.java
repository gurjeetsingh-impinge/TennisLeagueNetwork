package com.tennisdc.tln;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.realm.Realm;
import io.realm.exceptions.RealmMigrationNeededException;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tennisleaguenetwork.model.CourtRegion;
import com.tennisdc.tennisleaguenetwork.model.Location;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.modules.buy.FragProgramList;
import com.tennisdc.tln.modules.login.FragLogin;
import com.tennisdc.tln.modules.swag.FragSwag;

import java.util.ArrayList;

public class SingleFragmentActivity extends HelperActivity {

    protected final static String EXTRA_FRAGMENT_CLASS = "fragment_class";
    public final static String EXTRA_FRAGMENT_ARGS = "fragment_params";
    private final static String TAG = SingleFragmentActivity.class.getSimpleName();
    /* required for singleTask activities, so that fragment can be reloaded again */
    private final static String EXTRA_RELOAD_FRAG = "reload_fragment";
    private final static String EXTRA_ERROR = "error";
    Tracker mTracker;
    private boolean mReloadFrag;
    private String mError;
    private Fragment fragment;

    public static Intent getIntent(Context context, Class fragmentClass, Bundle fragmentArgs) {
        Intent intent = new Intent(context, SingleFragmentActivity.class);


        intent.putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass);
        if (fragmentArgs != null) {
            intent.putExtra(EXTRA_FRAGMENT_ARGS, fragmentArgs);
        }

        return intent;
    }


    public static Intent getIntentWithFlag(Context context, Class fragmentClass, Bundle fragmentArgs) {
        Intent intent = new Intent(context, SingleFragmentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass);
        if (fragmentArgs != null) {
            intent.putExtra(EXTRA_FRAGMENT_ARGS, fragmentArgs);
        }
        return intent;
    }

    public static Intent getIntent(Context context, Class fragmentClass, Bundle fragmentArgs, boolean reloadFrag) {
        Intent intent = getIntent(context, fragmentClass, fragmentArgs);
        intent.putExtra(EXTRA_RELOAD_FRAG, reloadFrag);
        return intent;
    }

    public static Intent getIntent(Context context, Class fragmentClass, Bundle fragmentArgs, String error) {
        Intent intent = new Intent(context, SingleFragmentActivity.class);

        intent.putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass);
        if (fragmentArgs != null) {
            intent.putExtra(EXTRA_FRAGMENT_ARGS, fragmentArgs);
        }
        if (!TextUtils.isEmpty(error)) {
            intent.putExtra(EXTRA_ERROR, error);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(getContentViewId());

        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        App application = (App) getApplication();
        //mTracker = application.getDefaultTracker();
        // [END shared_tracker]
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) mReloadFrag = intent.getBooleanExtra(EXTRA_RELOAD_FRAG, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(App.sOAuth)) setTitle(new Prefs.AppData(this).getDomainName());
        else {
            String str = "  Tennis League Network, llc";
            setTitle(str);
        }

        FragmentManager fManager = getSupportFragmentManager();

        Fragment fragment = fManager.findFragmentById(R.id.fragmentContainer);

        /**
         * in case if it is already existing, and wants to reload the
         * fragments again, specially for singleTask Activities
         */
        if (fragment != null) {
            if (mReloadFrag) {
                FragmentTransaction ft = fManager.beginTransaction();
                ft.remove(fragment);
                ft.commit();
                fragment = null;
            }
        }
        mReloadFrag = false;

        if (fragment == null) {
            if (fManager.getBackStackEntryCount() > 0)
                fManager.beginTransaction().add(R.id.fragmentContainer, createFragment()).addToBackStack(null).commit();
            else
                fManager.beginTransaction().add(R.id.fragmentContainer, createFragment()).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
        if (findViewById(R.id.fragmentDetailContainer) != null) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragmentDetailContainer);
            if (fragment != null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                // ft.commit();
                ft.commitAllowingStateLoss();
            }
        }
        //   super.onSaveInstanceState(outState);
    }

    public int getContentViewId() {
        return R.layout.activity_fragment;
    }

    private Realm mRealm;
    public Fragment createFragment() {
        Intent intent = getIntent();
        if (intent != null) {
            try {
                mRealm = Realm.getInstance(this);
            } catch (RealmMigrationNeededException e) {
                Realm.deleteRealmFile(this);
                mRealm = Realm.getInstance(this);
            }
            Class fragmentClass = (Class) intent.getSerializableExtra(EXTRA_FRAGMENT_CLASS);
            Bundle fragmentArgs = intent.getBundleExtra(EXTRA_FRAGMENT_ARGS);
            mError = intent.getStringExtra(EXTRA_ERROR);

            if (fragmentClass != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                try {
                    if(intent.hasExtra("notification")){
                        fragmentArgs.putBoolean("notification",true);
                    }
                    fragment = (Fragment) fragmentClass.newInstance();
                    if (fragmentArgs != null) fragment.setArguments(fragmentArgs);
                    return fragment;
                } catch (InstantiationException | IllegalAccessException e) {
                    Log.e(TAG, "ERROR:" + e.getMessage());
                }
            } else {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setIcon(R.drawable.ic_action);

                new com.tennisdc.tennisleaguenetwork.common.Prefs.AppData(this).setOAuthToken(null);

                if(mRealm != null) {
                    mRealm.beginTransaction();
                    mRealm.allObjects(Court.class).clear();
                    mRealm.allObjects(Location.class).clear();
                    mRealm.allObjects(CourtRegion.class).clear();
                    mRealm.commitTransaction();
                }

                 if (TextUtils.isEmpty(new Prefs.AppData(this).getOAuthToken())) {
                    return new FragLogin();
                } else {
                    Bundle mBundle = new Bundle();
                    if(intent.hasExtra("notification")){
                        mBundle.putBoolean("notification",true);
                    }
                    FragHome mFragHome = new FragHome();
                    mFragHome.setArguments(mBundle);
                    return mFragHome;
                }
            }
        }
        return new FragLogin();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if(fragment != null)
            fragment.onActivityResult(requestCode, resultCode, data);
    }

    /*public static class AuthActivity extends SingleFragmentActivity {

        public static Intent getIntent(Context context) {
            Intent intent = new Intent(context, AuthActivity.class);
            intent.putExtra(EXTRA_FRAGMENT_CLASS, FragLogin.class);
            return intent;
        }

        @Override
        protected void onCreate(Bundle arg0) {
            super.onCreate(arg0);
            if (!TextUtils.isEmpty(new Prefs.AppData(this).getOAuthToken())) {
                startActivity(HomeActivity.getIntent(this));
                finish();
            } else {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setIcon(R.mipmap.ic_launcher);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }*/

    /**
     * Check to make sure global_tracker.xml was configured correctly (this function only needed
     * for sample apps).
     */
    private boolean checkConfiguration() {
        XmlResourceParser parser = getResources().getXml(R.xml.global_tracker);

        boolean foundTag = false;
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String tagName = parser.getName();
                    String nameAttr = parser.getAttributeValue(null, "name");

                    foundTag = "string".equals(tagName) && "ga_trackingId".equals(nameAttr);
                }

                if (parser.getEventType() == XmlResourceParser.TEXT) {
                    if (foundTag && parser.getText().contains("REPLACE_01000ME")) {
                        return false;
                    }
                }

                parser.next();
            }
        } catch (Exception e) {
            Log.w(TAG, "checkConfiguration", e);
            return false;
        }

        return true;
    }

    public static class HomeActivity extends SingleFragmentActivity {

        public static Intent getIntent(Context context) {
            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra(EXTRA_FRAGMENT_CLASS, FragHome.class);
            return intent;
        }

        @Override
        protected void onCreate(Bundle arg0) {
            super.onCreate(arg0);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_action);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

//			setActionBarTitleAsMarquee();
            try {
                mTracker.setScreenName("Home Screen");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            }catch (NullPointerException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


	/*	private void setActionBarTitleAsMarquee(){
			Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
//			TextView textView = (TextView) toolbar.getChildAt(0);
			TextView textView = (TextView) toolbar.getChildAt(2);
			// Get Action Bar's title
			try {
//				View v = getWindow().getDecorView();
//				int resId = getResources().getIdentifier("action_bar_title", "id", "android");
//				TextView title = (TextView) v.findViewById(resId);

				// Set the ellipsize mode to MARQUEE and make it scroll only once
				textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
				textView.setMarqueeRepeatLimit(-1);

				// In order to start strolling, it has to be focusable and focused
				textView.setFocusable(true);
				textView.setFocusableInTouchMode(true);
				textView.requestFocus();
			}catch (NullPointerException e){

			}
		}*/
    }

    public static class BuyActivity extends SingleFragmentActivity {


        public static Intent getIntent(Context context) {
            Intent intent = new Intent(context, BuyActivity.class);
            intent.putExtra(EXTRA_RELOAD_FRAG, true);
            intent.putExtra(EXTRA_FRAGMENT_CLASS, FragProgramList.class);
            return intent;
        }

        @Override
        protected void onCreate(Bundle arg0) {
            super.onCreate(arg0);
            try{
            mTracker.setScreenName("Buy Screen");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            }catch (NullPointerException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onBackPressed() {
            startActivity(HomeActivity.getIntent(this));
        }
    }

    public static class SwagItemsActivity extends SingleFragmentActivity{

        public static Intent getIntent(Context context) {
            Intent intent = new Intent(context,SwagItemsActivity.class);
            intent.putExtra(EXTRA_RELOAD_FRAG, true);
            intent.putExtra(EXTRA_FRAGMENT_CLASS, FragSwag.class);
            return intent;
        }

        @Override
        protected void onCreate(Bundle arg0) {
            super.onCreate(arg0);
            try{
                mTracker.setScreenName("Swag Screen");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onBackPressed() {
            startActivity(HomeActivity.getIntent(this));
        }
    }

    public static class MyProfileActivity extends SingleFragmentActivity {

        public static Intent getIntent(Context context) {
            Intent intent = new Intent(context, MyProfileActivity.class);
            intent.putExtra(EXTRA_RELOAD_FRAG, true);
            return intent;
        }

        @Override
        protected void onCreate(Bundle arg0) {
            super.onCreate(arg0);

            try{
            // Sending Profile Screen
            mTracker.setScreenName("Profile Screen");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            }catch (NullPointerException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onBackPressed() {
            startActivity(MyProfileActivity.getIntent(this));
        }
    }

    public void findTextViewTitle(String title) {
//			String title = "title";

        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);

        Window window = getWindow();
        View decor = window.getDecorView();

        ArrayList<View> views = new ArrayList<View>();
        decor.findViewsWithText(views, title, View.FIND_VIEWS_WITH_TEXT);

        for (View view : views) {
            Log.d("TAG", "view " + view.toString());
            if (view instanceof TextView) {
                TextView tvTitle = (TextView) view;
                tvTitle.setTextSize(15f);
                tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tvTitle.setMarqueeRepeatLimit(-1);

                // In order to start strolling, it has to be focusable and focused
                tvTitle.setFocusable(true);
                tvTitle.setFocusableInTouchMode(true);
                tvTitle.requestFocus();
            }
        }
    }
}
